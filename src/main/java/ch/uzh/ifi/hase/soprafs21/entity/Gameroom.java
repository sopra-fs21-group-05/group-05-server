package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal Gameroom Representation
 * This class composes the internal representation of the gameroom and defines how the gameroom is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */

@Entity
@Table(name = "GAMEROOM")
public class Gameroom implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String roomname;

    //TODO: user list uni or bidirectional?
    @OneToMany
    private List<User> users = new ArrayList<User>();

    //TODO: game column foreign key @OnetoOne
}
