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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.json.JSONArray;

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

    public Game createGame(Gameroom gameroom) {

        checkIfGameExists(gameroom);

        Game newGame = new Game();
        List<User> users = new ArrayList<>(gameroom.getUsers());
        List<User> players = new ArrayList<>();

        //assign each user a material set
        MaterialSet[] sets = MaterialSet.values();
        int SetNr = 0;
        for (User user : users) {
            //User userById = userRepository.getOne(user.getId());
            user.setMaterialSet(sets[SetNr]);
            SetNr++;

            user = userRepository.save(user);
            userRepository.flush();

            players.add(user);
        }

        newGame.setUserList(players);
        newGame.setRoundNr(0);
        newGame.setGameroom(gameroom);

        // saves the given entity but data is only persisted in the database once flush() is called
        newGame = gameRepository.save(newGame);
        gameRepository.flush();


        log.debug("Created Information for Game: {}", newGame);
        return newGame;
    }

    public Game assignGridPictures(Game game, List<Picture> pictureList){
        game.setGridPictures(pictureList);
        game = gameRepository.save(game);
        gameRepository.flush();
        return game;
    }


    //assigns the "next" materialset to specific player
    public User assignMaterialset(Long gameId, Long userId) {
        Game game = getExistingGame(gameId);
        User user = getPlayerInGame(userId, gameId);

        String baseErrorMessage = "The provided %s is not the current %s. ";
        if(game.getRoundNr() != gameRepository.getOne(game.getGameId()).getRoundNr()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, "roundNr", "roundNr"));
        }

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

        String baseErrorMessage = "The provided %s is not the current %s. ";
        if(game.getRoundNr() != gameRepository.getOne(game.getGameId()).getRoundNr()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, "roundNr", "roundNr"));
        }

        Random rand = new Random();
        //assign each user random coordinates (each coordinate removed once assigned)
        //picks random index from list
        int randomIndex = rand.nextInt(coordinatesList.size());
        //gets the coordinates at the randomIndex
        GridCoordinates randomElement = coordinatesList.get(randomIndex);
        //remove the index to prevent it to be picked again
        coordinatesList.remove(randomIndex);
        System.out.println(coordinatesList.size());
        //assign the coordinates to player
        user.setCoordinatesAssignedPicture(randomElement);

        System.out.println(randomElement);


        int pictureIndex = randomElement.getPictureNr();
        System.out.println(pictureIndex);

        Map<String, String> assignedPicture = new HashMap<>();
        assignedPicture.put(randomElement.toString(),game.getGridPictures().get(pictureIndex));


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
                UserService.restrictPlayer(user);
            }
        }
        return winners;
    }

    public List<String> getPicturesFromPixabay(){
        List<String> pictures = new ArrayList<>();
        List<String> keywords = new ArrayList<String>();
        Collections.addAll(keywords, "yellow+flower", "red+car", "tree", "fruits", "butterfly", "mushroom",
                "school", "beach", "bike", "farm", "safari", "balloon", "rainbow", "books", "street", "sunrise");

        //get one base64 encoded picture for each keyword
        for (String k:keywords) {
            try {
                String responseBody = sendGetRequest(k);
                String urlString = parseJson(responseBody);
                URL url = new URL(urlString);
                byte[] byteArray = getPictureFromUrl(url);
                String encodedPicture = encodePicture(byteArray);
                pictures.add(encodedPicture);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        return pictures;
    }


    private String sendGetRequest(String keyword) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("https://pixabay.com/api/?key=20947416-386f2cea1a25d1b2b70bdcc1f&q="+ keyword
                            +"&image_type=photo&per_page=5&orientation=horizontal"))
                    .setHeader("User-Agent", "Java 11 HttpClient Bot")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // print status code
            System.out.println(response.statusCode());

            // print response body
            System.out.println(response.body());
            return response.body();

        } catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    private String encodePicture(byte[] pictureBytes){
        String encoded = Base64.getEncoder().encodeToString(pictureBytes);

        System.out.println(encoded);
        return encoded;
    }


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
            in.close();
            byte[] response = out.toByteArray();
            return response;
        } catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    private String parseJson(String jsonString){
        JSONObject json = new JSONObject(jsonString);

        JSONArray hitsJSONArray = json.getJSONArray("hits");
        //get webformaturl of  one of the 5 results, as we only need 1 picture of each categroy
        Random random = new Random();
        int i = random.nextInt(5); //random int from 0 to 4
        JSONObject hitJSONObject = hitsJSONArray.getJSONObject(i);
        String url = hitJSONObject.getString("webformatURL");
        System.out.println(url);
        return url;
    }

    private void checkIfGameExists(Gameroom gameroom) {
        Long gameId = gameroom.getStartedGame();

        String baseErrorMessage = "The game already exist. Therefore, the game could not be created!";

        if (gameId != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
        }
    }

    public Gameroom getGameroomById(Long roomId){
        return gameroomService.getGameroomById(roomId);
    }


    //TODO: if picture is submitted per User, the game.userRecreations needs to be "cleared" after every round
    //submits the recreated picture of one user --> adds to game.userRecreations
    public Map<Long,String> submitPicture(Game gameInput,String submittedPicture,Long userId) {
        Game currentGame = getExistingGame(gameInput.getGameId());
        User user = getPlayerInGame(userId,currentGame.getGameId());
        Map<Long,String> submissions = new HashMap<>();
        submissions.putAll(currentGame.getUserRecreations());

        String baseErrorMessage = "The provided %s was not found. ";

        if(submittedPicture==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, "picture"));
        }

        //append userRecreations Map in currentGame
        submissions.put(user.getId(),submittedPicture);
        System.out.println(submissions);
        currentGame.setUserRecreations(submissions);

        //return Map
        return currentGame.getUserRecreations();
    }

    public List<Picture> makePictureList(){
        List<String> pictureStringList = getPicturesFromPixabay();
        List<Picture> pictureList = new ArrayList<>();

        for (String string: pictureStringList){
            Picture picture= createPicture(string);
            pictureList.add(picture);
        }

        return pictureList;
    }

    public Picture createPicture(String picture){
            Picture newPicture = new Picture();
            newPicture.setEncodedPicture(picture);
            pictureRepository.save(newPicture);
            pictureRepository.flush();
            return newPicture;
    }


    //returns a list of all submitted pictures in the current round
    public List<String> getSubmittedPictures(Long gameId) {
        Game game = getExistingGame(gameId);

        String baseErrorMessage = "Could not find the submitted pictures.";

        if(game.getUserRecreations().isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage));
        }

        Map<Long,String> userRecreations = game.getUserRecreations();
        List<String> submittedPictures = new ArrayList<String>(userRecreations.values());

        return submittedPictures;
    }

    
    //submit guesses of all users via dictionary (k:userId, v: guess as String) & add their scores
    public void submitAndCheckGuesses(Long gameId, Long userId, Map<Long,String> guesses){
        User playerThatRecreatedPicture = getPlayerInGame(userId,gameId);

        //get the originally assigned picture coordinates as a string
        String rightGuess = playerThatRecreatedPicture.getCoordinatesAssignedPicture().toString();

        //List<String> gridCoordinates = Arrays.asList(GridCoordinates.values().toString());
        List<String> gridCoordinates = Stream.of(GridCoordinates.values())
                .map(GridCoordinates::name)
                .collect(Collectors.toList());


        String baseErrorMessage = "Contains invalid guesses.";
        for(String guess : guesses.values()){
            System.out.println(guess);
            if(!gridCoordinates.stream().anyMatch(coordinate -> guess.equals(coordinate))){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage));
            }
        }

        //iterate through dictionary and check if guess is correct
        for(Map.Entry<Long,String> entry : guesses.entrySet()){
            if(entry.getValue().equals(rightGuess)){
                //update points for both players in user entity
                User guessingUser = getPlayerInGame(entry.getKey(), gameId);
                int updatedScore1 = playerThatRecreatedPicture.getPoints() + 1;
                playerThatRecreatedPicture.setPoints(updatedScore1);
                int updatedScore2 = guessingUser.getPoints() + 1;
                guessingUser.setPoints(updatedScore2);
                System.out.println("userId"+guessingUser.getId() +":"+ guessingUser.getPoints());

            }
        }
    }

    public Game updateGame(Long gameId){
        Game game = gameRepository.getOne(gameId);
        game.setRoundNr(game.getRoundNr() + 1);
        Map<Long,String> userRecreations = new HashMap<>();
        game.setUserRecreations(userRecreations);
        gameRepository.save(game);
        gameRepository.flush();

        return game;
    }

    public Map<String, String> getPictureGrid(Long gameId) {
        Game game = getExistingGame(gameId);
        Map<String,String> pictureGrid = new HashMap<>();

        List<GridCoordinates> coordinatesList = game.getGridCoordinates();
        System.out.println(coordinatesList);
        List<String> pictureList = game.getGridPictures();

        String baseErrorMessage = "The provided %s is not the current %s. ";
        if(game.getRoundNr() != gameRepository.getOne(game.getGameId()).getRoundNr()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, "roundNr", "roundNr"));
        }

        for(GridCoordinates g : coordinatesList){
            pictureGrid.put(g.toString(),pictureList.get(g.getPictureNr()));
        }

        return pictureGrid;
    }
}




