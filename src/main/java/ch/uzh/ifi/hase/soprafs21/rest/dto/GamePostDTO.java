package ch.uzh.ifi.hase.soprafs21.rest.dto;


public class GamePostDTO {
    private int roundNr;
    private String PictureBase64;

    public int getRoundNr() { return roundNr; }
    public void setRoundNr(int roundNr) { this.roundNr = roundNr; }

    public String getPictureBase64() { return PictureBase64; }
    public void setPictureBase64(String pictureBase64) { PictureBase64 = pictureBase64; }
}
