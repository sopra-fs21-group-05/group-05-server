package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;

import java.util.ArrayList;
import java.util.List;

public class GameroomGetDTO {

    private Long id;
    private String roomname;
    private List<UserGetDTO> users;
    private Long startedGame;
    private Long creator;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoomname() { return roomname; }
    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public List<UserGetDTO> getUsers() { return users; }
    public void setUsers(List<User> users) {
        List<UserGetDTO> userList = new ArrayList<>();
        for (User u:users) {
            UserGetDTO uDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(u);
            userList.add(uDTO);
        }
        this.users = userList; }

    public Long getStartedGame() { return startedGame; }
    public void setStartedGame(Long startedGame) { this.startedGame = startedGame; }

    public Long getCreator() { return creator; }
    public void setCreator(Long creator) { this.creator = creator; }
}
