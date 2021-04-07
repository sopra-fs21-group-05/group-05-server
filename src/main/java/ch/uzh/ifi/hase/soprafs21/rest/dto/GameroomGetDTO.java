package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.entity.User;

import java.util.List;

public class GameroomGetDTO {

    private Long id;
    private String roomname;
    private List<User> users;

    public String getRoomname() { return roomname; }
    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

}
