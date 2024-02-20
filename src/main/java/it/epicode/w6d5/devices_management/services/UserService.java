package it.epicode.w6d5.devices_management.services;

import it.epicode.w6d5.devices_management.Models.entities.Device;
import it.epicode.w6d5.devices_management.Models.entities.User;
import it.epicode.w6d5.devices_management.Models.reqDTO.RoleDTO;
import it.epicode.w6d5.devices_management.Models.reqDTO.UserDTO;
import it.epicode.w6d5.devices_management.Models.resDTO.DeleteRes;
import it.epicode.w6d5.devices_management.exceptions.BadRequestException;
import it.epicode.w6d5.devices_management.exceptions.NotFoundException;
import it.epicode.w6d5.devices_management.exceptions.UnauthorizedException;
import it.epicode.w6d5.devices_management.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRp;

    public Page<User> getAll(Pageable pageable) {
        return userRp.findAll(pageable);
    }

    public User findById(UUID id) throws NotFoundException, BadRequestException {

            return userRp.findById(id).orElseThrow(
                    () -> new NotFoundException("User not found")
            );


    }

    public User findByEmail(String email) throws NotFoundException, BadRequestException {

            return userRp.findByEmail(email).orElseThrow(
                    () -> new UnauthorizedException("Email and/or password are not correct"));



    }

    public User create(UserDTO userDTO) {
        return userRp.save(new User(
                userDTO.firstName(),
                userDTO.lastName(),
                userDTO.email(),
                encoder.encode(userDTO.password())
        ));
    }

    public User update(UserDTO userDTO, UUID id) throws BadRequestException {
        User u = userRp.findById(id).orElseThrow(
                () -> new BadRequestException("User doesn't exist")
        );
        u.setFirstName(userDTO.firstName());
        u.setLastName(userDTO.lastName());
        u.setEmail(userDTO.email());
        u.setHashPassword(encoder.encode(userDTO.password()));
        try {
        return userRp.save(u);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("'email' already exist");
        }

    }

    public User updateRole(RoleDTO roleDTO, UUID id) throws BadRequestException {
        User u = userRp.findById(id).orElseThrow(
                () -> new BadRequestException("User doesn't exist")
        );
        u.setRole(roleDTO.role());
        return userRp.save(u);
    }

    public DeleteRes delete(UUID id) throws BadRequestException {
        User user = userRp.findById(id).orElseThrow(
                () -> new BadRequestException("User doesn't exist")
        );
        userRp.delete(user);
        return new DeleteRes("User has been deleted");
    }


}
