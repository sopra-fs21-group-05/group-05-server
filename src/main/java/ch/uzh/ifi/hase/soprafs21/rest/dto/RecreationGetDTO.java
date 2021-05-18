package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.Map;

public class RecreationGetDTO {

    private Map<Long,String> recreations;
    private Map<Long, String> userNames;

    public Map<Long, String> getRecreations() { return recreations; }
    public void setRecreations(Map<Long, String> recreations) { this.recreations = recreations; }

    public Map<Long, String> getUserNames() { return userNames; }
    public void setUserNames(Map<Long, String> userNames) { this.userNames = userNames; }

}
