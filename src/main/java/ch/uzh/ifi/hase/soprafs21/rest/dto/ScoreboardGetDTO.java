package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.Map;

public class ScoreboardGetDTO {

    private Long scoreboardId;
    private Map<Long,Integer> userPoints;
    private Map<Long, String> userNames;

    public Long getScoreboardId() { return scoreboardId; }
    public void setScoreboardId(Long scoreboardId) { this.scoreboardId = scoreboardId; }

    public Map<Long, Integer> getUserPoints() { return userPoints; }
    public void setUserPoints(Map<Long, Integer> userPoints) { this.userPoints = userPoints; }

    public Map<Long, String> getUserNames() { return userNames; }
    public void setUserNames(Map<Long, String> userNames) { this.userNames = userNames; }
}
