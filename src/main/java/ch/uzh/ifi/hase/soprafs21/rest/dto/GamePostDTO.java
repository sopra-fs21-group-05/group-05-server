package ch.uzh.ifi.hase.soprafs21.rest.dto;


import java.util.Map;

public class GamePostDTO {
    private int roundNr;
    private Long gameId;
    private String submittedPicture;
    private Map<Long,String> guesses;

    public int getRoundNr() { return roundNr; }
    public void setRoundNr(int roundNr) { this.roundNr = roundNr; }


    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }


    public String getSubmittedPicture() { return submittedPicture; }
    public void setSubmittedPicture(String submittedPicture) { this.submittedPicture = submittedPicture; }

    public Map<Long, String> getGuesses() {
        return guesses;
    }

    public void setGuesses(Map<Long, String> guesses) {
        this.guesses = guesses;
    }
}
