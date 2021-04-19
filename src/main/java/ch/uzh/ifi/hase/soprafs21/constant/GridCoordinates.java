package ch.uzh.ifi.hase.soprafs21.constant;


public enum GridCoordinates {
    A1(0),A2(1),A3(2),A4(3),B1(4),B2(5),B3(6),B4(7),C1(8),C2(9),C3(10),C4(11),D1(12),D2(13),D3(14),D4(15);

    private final Integer pictureNr;

    private GridCoordinates(final Integer pictureNr){this.pictureNr=pictureNr;}

    public Integer getPictureNr(){
        return pictureNr;
    }

}
