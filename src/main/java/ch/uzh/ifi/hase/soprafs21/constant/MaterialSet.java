package ch.uzh.ifi.hase.soprafs21.constant;


public enum MaterialSet {
    COLOR_CUBES(1),
    BUILDING_BLOCKS(2),
    STICKS_STONES(3),
    ICON_CARDS(4),
    SHOE_LACES(5);

    private final Integer setNr;

    private MaterialSet(final Integer setNr){
        this.setNr = setNr;
    }

    //returns the materialset that belongs to a setNr
    public static MaterialSet nameOfSetNr(int setNr) {
        for (MaterialSet set : values()) {
            if (set.setNr==setNr) {
                return set;
            }
        }
        return null;
    }

    public Integer getSetNr(){
        return setNr;
    }

}
