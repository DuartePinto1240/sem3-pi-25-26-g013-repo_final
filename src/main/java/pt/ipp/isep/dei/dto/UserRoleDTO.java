package main.java.pt.ipp.isep.dei.dto;

/**
 * Data Transfer Object (DTO) for a User Role.
 * <p>
 * This class is used to transfer role information (ID and description)
 * between different layers of the application (e.g., domain to UI)
 * without exposing the internal domain structure.
 * </p>
 */
public class UserRoleDTO {

    /** The unique identifier of the role. */
    private  String id;

    /** The description of the role. */
    private  String description;

    /**
     * Constructs a {@code UserRoleDTO}.
     *
     * @param id the unique identifier of the role
     * @param description the description of the role
     */
    public UserRoleDTO(String id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Gets the unique identifier of the role.
     *
     * @return the role ID
     */
    public String getId() {return this.id;}

    /**
     * Gets the description of the role.
     *
     * @return the role description
     */
    public String getDescription() {return this.description;}
}