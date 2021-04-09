package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class GameroomPostDTO {

    private String roomname;
    private String password;
    private Long userId;

    public String getRoomname() { return roomname; }
    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
