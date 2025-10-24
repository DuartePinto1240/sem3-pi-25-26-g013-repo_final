package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.ArrayList;
import java.util.List;
import main.java.pt.ipp.isep.dei.dto.UserRoleDTO;

/**
 * Mapper class responsible for converting {@link UserRole} domain objects
 * to {@link UserRoleDTO} Data Transfer Objects.
 * <p>
 * This is typically used to expose domain data without exposing the internal
 * domain structure, ensuring data transfer between layers is safe and controlled.
 * </p>
 */
public class UserRoleMapper {

    /**
     * Converts a single {@link UserRole} domain object to its corresponding {@link UserRoleDTO}.
     *
     * @param role the {@link UserRole} to convert
     * @return the new {@link UserRoleDTO} instance
     */
    public UserRoleDTO toDTO(UserRole role) {
        return new UserRoleDTO(role.getId(), role.getDescription());
    }

    /**
     * Converts a list of {@link UserRole} domain objects to a list of {@link UserRoleDTO}s.
     *
     * @param roles the list of {@link UserRole}s to convert
     * @return a new {@link List} of {@link UserRoleDTO}s
     */
    public List<UserRoleDTO> toDTO(List<UserRole> roles) {
        List<UserRoleDTO> rolesDTO = new ArrayList();

        for(UserRole role : roles) {
            rolesDTO.add(this.toDTO(role));
        }

        return rolesDTO;
    }
}