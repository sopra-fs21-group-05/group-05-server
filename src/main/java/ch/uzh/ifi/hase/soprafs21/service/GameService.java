package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.MaterialSet;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
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

import org.json.JSONObject;
import org.json.JSONArray;

@Service
@Transactional
public class GameService {
    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();


    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game createGame(Gameroom gameroom) {

        checkIfGameExists(gameroom);

        Game newGame = new Game();
        newGame.setUserList(new ArrayList<>(gameroom.getUsers()));
        newGame.setRoundNr(0);
        newGame.setGameroom(gameroom);
        //TODO: set material sets and scoreboard

        // saves the given entity but data is only persisted in the database once flush() is called
        newGame = gameRepository.save(newGame);
        gameRepository.flush();

        log.debug("Created Information for Game: {}", newGame);
        return newGame;
    }


    //assigns the "next" materialset to specific player
    //TODO: modify once MaterialSet (entity or enum) is created
    public User assignMaterialset(Game game, Long userId) {
        Long gameId = game.getGameId();
        User user = getPlayerInGame(userId, gameId);

        MaterialSet newSet;
        int newSetNr;
        int prevSetId = user.getMaterialSet().getSetNr();

        if(prevSetId != 4){
            newSetNr = prevSetId+1;
        }else{
            newSetNr = 0;
        }

        newSet = MaterialSet.nameOfSetNr(newSetNr);
        user.setMaterialSet(newSet);

        return user;
    }

    //assigns picture to recreate to specific player
    public User assignPicture(Game game, Long userId) {
        Long gameId = game.getGameId();
        User user = getPlayerInGame(userId, gameId);

        String coordinatesAssignedPicture = null;
        user.setCoordinatesAssignedPicture(coordinatesAssignedPicture);

        //TODO: implementation once game initial setup is done
        return user;
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
            String responseBody = sendGetRequest(k);
            String url = parseJson(responseBody);
            byte[] byteArray = getPictureFromUrl(url);
            String encodedPicture = encodePicture(byteArray);
            pictures.add(encodedPicture);
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


    private byte[] getPictureFromUrl(String urlString){
        try {
            URL url = new URL(urlString);
            InputStream in = new BufferedInputStream(url.openStream());
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
        Game game = gameRepository.findByGameroom(gameroom);

        String baseErrorMessage = "The game already exist. Therefore, the gamecould not be created!";

        if (game != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
        }
    }


    /*//submits the recreated picture of one user --> adds to game.userRecreations
    public User submitPicture(Game gameInput, Long userId) {
        Game currentGame = getExistingGame(gameInput.getGameId());
        User user = getPlayerInGame(userId, currentGame.getGameId());

        //submit the picture

        return user;
    }*/


}
