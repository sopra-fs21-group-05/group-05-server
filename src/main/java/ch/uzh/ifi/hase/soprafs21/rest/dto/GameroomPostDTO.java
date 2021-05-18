package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class GameroomPostDTO {

    private String roomname;
    private Long roomId;
    private String password;
    private Long userId;

    public String getRoomname() { return roomname; }
    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
