package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.GridCoordinates;
import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;
import ch.uzh.ifi.hase.soprafs21.entity.*;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.PictureRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Game Service
 * This class is the "worker" and responsible for all functionality related to the game
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;
    private final GameroomService gameroomService;
    private final PictureRepository pictureRepository;
    private final UserRepository userRepository;
    private final ScoreboardService scoreboardService;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("gameroomService") GameroomService gameroomService,
                       @Qualifier("pictureRepository") PictureRepository pictureRepository,
                       @Qualifier("userRepository") UserRepository userRepository,
                       @Qualifier("scoreboardService") ScoreboardService scoreboardService) {
        this.gameRepository = gameRepository;
        this.gameroomService = gameroomService;
        this.pictureRepository = pictureRepository;
        this.userRepository = userRepository;
        this.scoreboardService = scoreboardService;
    }

    //create a new game
    public Game createGame(Gameroom gameroom) {

        checkIfGameExists(gameroom);

        Game newGame = new Game();
        List<User> users = new ArrayList<>(gameroom.getUsers());
        List<User> players = new ArrayList<>();

        //assign each user a material set
        MaterialSet[] sets = MaterialSet.values();
        int setNr = 0;
        for (User user : users) {
            user.setMaterialSet(sets[setNr]);
            setNr++;

            user = userRepository.save(user);
            userRepository.flush();

            players.add(user);
        }

        newGame.setUserList(players);
        newGame.setRoundNr(1);
        newGame.setGameroom(gameroom);
        newGame.setSubmittedGuesses(0);

        // saves the given entity but data is only persisted in the database once flush() is called
        newGame = gameRepository.save(newGame);
        gameRepository.flush();


        log.debug("Created Information for Game: {}", newGame);
        return newGame;
    }

    //assign grid pictures to a game
    public Game assignGridPictures(Game game, List<Picture> pictureList){
        game.setGridPictures(pictureList);
        game = gameRepository.save(game);
        gameRepository.flush();
        return game;
    }

    //assigns the "next" materialset to specific player
    public User assignMaterialset(Long gameId, Long userId) {
        User user = getPlayerInGame(userId, gameId);

        MaterialSet newSet;
        int newSetNr;
        int prevSetNr = user.getMaterialSet().getSetNr();

        if(prevSetNr != 5){
            newSetNr = prevSetNr+1;
        }else{
            newSetNr = 1;
        }

        newSet = MaterialSet.nameOfSetNr(newSetNr);
        user.setMaterialSet(newSet);

        return user;
    }

    //assigns unique pictures to recreate to all players
    public Map<String, String> assignPicture(Long gameId, Long userId) {
        Game game = getExistingGame(gameId);
        User user = getPlayerInGame(userId,gameId);
        List<GridCoordinates> coordinatesList = game.getGridCoordinates();

        Random rand = new Random();
        //assign each user random coordinates (each coordinate removed once assigned)
        //picks random index from list
        int randomIndex = rand.nextInt(coordinatesList.size());
        //gets the coordinates at the randomIndex
        GridCoordinates randomElement = coordinatesList.get(randomIndex);
        //remove the index to prevent it to be picked again
        coordinatesList.remove(randomIndex);
        //assign the coordinates to player
        user.setCoordinatesAssignedPicture(randomElement);

        int pictureIndex = randomElement.getPictureNr();

        Map<String, String> assignedPicture = new HashMap<>();
        List<String> gridPictures = game.getGridPicturesAsString();

        assignedPicture.put(randomElement.toString(),gridPictures.get(pictureIndex));

        game.setGridCoordinates(coordinatesList);

        return assignedPicture;
    }


    //returns an existing game
    public Game getExistingGame(Long gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        String baseErrorMessage = "The %s provided %s not found.";
        if(optionalGame.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, "gameId", "was"));
        }
        return optionalGame.get();
    }


    //returns a specific player/user in an existing game
    public User getPlayerInGame(Long userId, Long gameId){
        Game game = getExistingGame(gameId);
        User targetUser = null;
        for (User user: game.getUserList()){
            if(user.getId().equals(userId)){
                targetUser = user;
            }
        }

        String baseErrorMessage = "The %s provided %s not found.";
        if(targetUser == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, "userId", "was"));
        }
        return targetUser;
    }

    //Get a list of all players in the game
    public List<User> getPlayers(Long gameId){
        Game game = getExistingGame(gameId);
        return game.getUserList();
    }

    //returns player(s) with the highest score
    public List<User> getWinner(Long gameId){
        List<User> winners = new ArrayList<>();
        int max = 0;
        for(User user: getPlayers(gameId)){
            if(user.getPoints() >= max){
                max = user.getPoints();
            }
        }
        for (User user: getPlayers(gameId)){
            if(user.getPoints() == max){
                winners.add(user);
            }
        }
        return winners;
    }

    //gets 16 pictures from the Pixabay API and returns them as a list of strings (encoded pictures)
    public List<String> getPicturesFromPixabay(){
        List<String> pictures = new ArrayList<>();
        List<String> keywords = new ArrayList<>();
        Collections.addAll(keywords, "yellow+flower", "red+car", "jungle", "fruits", "butterfly", "mushroom",
                "palm+tree", "bike", "farm", "safari", "balloon", "rainbow", "book+shelf", "traffic", "sunrise", "swan",
                "computer", "calculator", "coffee", "chocolate", "swiss+alps", "sandcastle", "frog", "waterfall",
                "hot+dog", "cat", "snowman", "waffle", "sunglasses", "surfboard", "jellyfish", "horse", "pyramids",
                "flame", "cow", "guitar", "piano", "clock", "storm", "umbrella", "baseball", "mailbox", "toast",
                "reef", "hill", "police", "subway", "tent", "skyscraper", "tower+bridge", "parachute", "space+shuttle");

        //choose 16 random keywords of the list
        List<String> selected = new ArrayList<>();
        int keywordNo = keywords.size();
        Random rand = new Random();
        while(selected.size() < 16){
            int randomIndex = rand.nextInt(keywordNo);
            if(!selected.contains(keywords.get(randomIndex))){
                selected.add(keywords.get(randomIndex));
            }
        }

        //get one base64 encoded picture for each keyword
        for (String k:selected) {
            String responseBody = sendGetRequest(k);
            String encodedPicture = getEncodedPictureFromResponse(responseBody);

            //check if this picture duplicated, get another one till not a duplicate
            while(pictures.contains(encodedPicture)){
                encodedPicture = getEncodedPictureFromResponse(responseBody);
            }
            pictures.add(encodedPicture);
        }
        return pictures;
    }

    //gets a base64 encoded picture from the provided url
    private String getEncodedPictureFromResponse(String responseBody){
        String encodedPicture = "";
        try {
            String urlString = parseJson(responseBody);
            URL url = new URL(urlString);
            byte[] byteArray = getPictureFromUrl(url);
            encodedPicture = encodePicture(byteArray);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return encodedPicture;
    }

    //sends a get request to pixabay for the rpovided keyword
    private String sendGetRequest(String keyword) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("https://pixabay.com/api/?key=20947416-386f2cea1a25d1b2b70bdcc1f&q="+ keyword
                            +"&image_type=photo&per_page=5&orientation=horizontal"))
                    .setHeader("User-Agent", "Java 11 HttpClient Bot")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.debug("Response: {} {}", response.statusCode(), response.body());
            return response.body();

        } catch (Exception e){
            log.debug(e.toString());
            return null;
        }
    }

    //encode a byte array picture in base64
    private String encodePicture(byte[] pictureBytes){
        String encoded = Base64.getEncoder().encodeToString(pictureBytes);

        log.debug("Encoded picture: {}", encoded);
        return encoded;
    }

    //get a byte array picture from a url
    private byte[] getPictureFromUrl(URL url){
        try(InputStream in = new BufferedInputStream(url.openStream())) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1!=(n=in.read(buf)))
            {
                out.write(buf, 0, n);
            }
            out.close();
            return out.toByteArray();
        } catch (Exception e){
            log.debug(e.toString());
            return new byte[0];
        }
    }

    //parse json response
    private String parseJson(String jsonString){
        JSONObject json = new JSONObject(jsonString);

        JSONArray hitsJSONArray = json.getJSONArray("hits");
        //get webformaturl of  one of the 5 results, as we only need 1 picture of each category
        Random random = new Random();
        int i = random.nextInt(5); //random int from 0 to 4
        JSONObject hitJSONObject = hitsJSONArray.getJSONObject(i);
        String url = hitJSONObject.getString("webformatURL");
        log.debug("URL: {}", url);
        return url;
    }

    //throws exception if a game already exists for a given gameroom
    private void checkIfGameExists(Gameroom gameroom) {
        Long gameId = gameroom.getStartedGame();

        String baseErrorMessage = "The game already exist. Therefore, the game could not be created!";

        if (gameId != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
        }
    }

    //get gameroom by its id
    public Gameroom getGameroomById(Long roomId){
        return gameroomService.getGameroomById(roomId);
    }

    //submits the recreated picture of one user --> adds to game.userRecreations
    public Map<Long,String> submitPicture(Game gameInput,String submittedPicture,Long userId) {
        Game currentGame = getExistingGame(gameInput.getGameId());
        User user = getPlayerInGame(userId,currentGame.getGameId());
        String baseErrorMessage = "The provided %s was not found. ";

        if(submittedPicture==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, "picture"));
        }
        // add recreation to user
        user.setRecreatedPicture(submittedPicture);
        userRepository.save(user);
        userRepository.flush();

        Map<Long,String> submissions = new HashMap<>();
        submissions.put(user.getId(),user.getRecreatedPicture());

        return submissions;
    }

    //creates a list of pictures from the API call and saves them to the picture repository
    public List<Picture> makePictureList(){
        List<String> pictureStringList = getPicturesFromPixabay();
        List<Picture> pictureList = new ArrayList<>();

        for (String string: pictureStringList){
            Picture picture= createPicture(string);
            pictureList.add(picture);
        }

        return pictureList;
    }

    //saves encoded picture to picture repository
    public Picture createPicture(String picture){
            Picture newPicture = new Picture();
            newPicture.setEncodedPicture(picture);
            pictureRepository.save(newPicture);
            pictureRepository.flush();
            return newPicture;
    }


    //returns a list of all submitted pictures in the current round
    public Map<Long,String> getSubmittedPictures(Long gameId) {
        Game game = getExistingGame(gameId);
        Map<Long,String> submissions = new HashMap<>();

        for (User u: game.getUserList()) {
            if(u.getRecreatedPicture() == null){
                submissions.put(u.getId(),"");
            } else{
                submissions.put(u.getId(),u.getRecreatedPicture());
            }
        }

        return submissions;
    }

    
    //submit guesses of one user via dictionary (k:userId, v: guess as String) & add their scores
    public void submitAndCheckGuesses(Long gameId, Long userId, Map<Long,String> guesses){
        Game game = getExistingGame(gameId);
        User playerThatSubmitsGuesses = getPlayerInGame(userId,game.getGameId());

        int newGuesses = game.getSubmittedGuesses()+1;
        game.setSubmittedGuesses(newGuesses);
        game = gameRepository.save(game);
        gameRepository.flush();

        List<String> gridCoordinates = Stream.of(GridCoordinates.values())
                .map(GridCoordinates::name)
                .collect(Collectors.toList());

        //check if guesses are all valid
        String baseErrorMessage = "Contains invalid guesses. Please enter valid coordinates!";
        for(String guess : guesses.values()){
            String finalGuess = guess.toUpperCase();
            if(gridCoordinates.stream().noneMatch(finalGuess::equals)){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, baseErrorMessage);
            }
        }

        //iterate through dictionary and check if guess is correct
        for(Map.Entry<Long,String> entry : guesses.entrySet()){
            User playerThatRecreatedPicture = getPlayerInGame(entry.getKey(),game.getGameId());
            String rightGuess = playerThatRecreatedPicture.getCoordinatesAssignedPicture().toString();
            //update player scores
            if(entry.getValue().toUpperCase().equals(rightGuess)){
                int updatedScore1 = playerThatSubmitsGuesses.getPoints()+1;
                playerThatSubmitsGuesses.setPoints(updatedScore1);
                int updatedScore2 = playerThatRecreatedPicture.getPoints()+1;
                playerThatRecreatedPicture.setPoints(updatedScore2);

                log.debug("Score of user with userId {}: {}", playerThatSubmitsGuesses.getId(), playerThatSubmitsGuesses.getPoints());
            }
        }

    }

    //updates roundNr and userRecreations (when entering a new round)
    public Game updateGame(Long gameId){
        //go to next round and set guesses to 0
        Game game = gameRepository.getOne(gameId);
        game.setRoundNr(game.getRoundNr() + 1);
        game.setSubmittedGuesses(0);
        gameRepository.save(game);
        gameRepository.flush();

        //delete user recreations
        for (User u: game.getUserList()) {
            u.setRecreatedPicture("");
            userRepository.save(u);
        }
        userRepository.flush();

        return game;
    }

    //returns picture grid
    // keys: grid coordinates, values: indices (0-15) mapped to 4x4 grid, row by row from top to bottom
    public Map<String, String> getPictureGrid(Long gameId) {
        Game game = getExistingGame(gameId);
        Map<String,String> pictureGrid = new HashMap<>();

        List<GridCoordinates> coordinatesList = Arrays.asList(GridCoordinates.values());
        List<String> pictureList = game.getGridPicturesAsString();


        for(GridCoordinates g : coordinatesList){
            pictureGrid.put(g.toString(),pictureList.get(g.getPictureNr()));
        }

        return pictureGrid;
    }

    //removes the game from the game repository
    public void endGame(Long gameId){
        Game endingGame = getExistingGame(gameId);
        scoreboardService.endGame(gameRepository.getOne(gameId));
        //delete pictures from the database
        List <Picture> toDeletePictures = endingGame.getGridPictures();
        endingGame.setGridPictures(null);
        for(Picture picture: toDeletePictures){
            pictureRepository.delete(picture);
            pictureRepository.flush();
        }

        //delete game
        gameRepository.deleteById(gameId);
        gameRepository.flush();
    }
}




