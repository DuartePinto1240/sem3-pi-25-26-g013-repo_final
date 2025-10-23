package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.ArrayList;
import java.util.List;
import main.java.pt.ipp.isep.dei.dto.UserRoleDTO;

public class UserRoleMapper {
    public UserRoleDTO toDTO(UserRole role) {
        return new UserRoleDTO(role.getId(), role.getDescription());
    }

    public List<UserRoleDTO> toDTO(List<UserRole> roles) {
        List<UserRoleDTO> rolesDTO = new ArrayList();

        for(UserRole role : roles) {
            rolesDTO.add(this.toDTO(role));
        }

        return rolesDTO;
    }
}

