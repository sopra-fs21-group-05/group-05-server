package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Gameroom;
import ch.uzh.ifi.hase.soprafs21.entity.Scoreboard;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g., UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "points", target = "points")
    @Mapping(source = "restrictedMode", target = "restrictedMode")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "materialSet", target = "materialSet")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "token", target = "token")
    UserAuthDTO convertEntityToUserAuthDTO(User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "token", target = "token")
    User convertUserAuthDTOToEntity(UserAuthDTO userAuthDTO);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "roomname", target = "roomname")
    @Mapping(source = "roomId", target = "id")
    Gameroom convertGameroomPostDTOtoEntity(GameroomPostDTO gameroomPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "roomname", target = "roomname")
    @Mapping(source = "users", target = "users")
    @Mapping(source = "startedGame", target = "startedGame")
    GameroomGetDTO convertEntityToGameroomGetDTO(Gameroom gameroom);

    @Mapping(source = "materialSet", target = "materialSet")
    @Mapping(source = "coordinatesAssignedPicture", target = "coordinatesAssignedPicture")
    GameGetDTO convertEntityToGameGetDTO(User user);

    @Mapping(source = "gameId", target = "gameId")
    Game convertGamePostDTOToEntity(GamePostDTO gamePostDTO);

    @Mapping(source = "scoreboardId", target = "scoreboardId")
    @Mapping(source = "userPoints", target = "userPoints")
    ScoreboardGetDTO convertEntityToScoreboardGetDTO(Scoreboard scoreboard);




}
