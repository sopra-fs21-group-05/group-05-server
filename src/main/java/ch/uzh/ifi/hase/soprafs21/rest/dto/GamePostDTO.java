package ch.uzh.ifi.hase.soprafs21.rest.dto;


public class GamePostDTO {
    private int roundNr;
    private Long gameId;
    private String submittedPicture;

    public int getRoundNr() { return roundNr; }
    public void setRoundNr(int roundNr) { this.roundNr = roundNr; }


    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }


    public String getSubmittedPicture() { return submittedPicture; }
    public void setSubmittedPicture(String submittedPicture) { this.submittedPicture = submittedPicture; }
}
