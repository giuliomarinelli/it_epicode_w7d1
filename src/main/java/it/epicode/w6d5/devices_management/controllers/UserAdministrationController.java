package it.epicode.w6d5.devices_management.controllers;

import it.epicode.w6d5.devices_management.Models.entities.User;
import it.epicode.w6d5.devices_management.Models.reqDTO.RoleDTO;
import it.epicode.w6d5.devices_management.Models.reqDTO.UserDTO;
import it.epicode.w6d5.devices_management.Models.resDTO.DeleteRes;
import it.epicode.w6d5.devices_management.exceptions.BadRequestException;
import it.epicode.w6d5.devices_management.exceptions.NotFoundException;
import it.epicode.w6d5.devices_management.exceptions.ValidationMessages;
import it.epicode.w6d5.devices_management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserAdministrationController {

    @Autowired
    private UserService userSvc;

    @GetMapping("/users-administration")
    public Page<User> getAll(Pageable pageable) {
        return userSvc.getAll(pageable);
    }

    @GetMapping("/users-administration/{id}")
    public User get(@PathVariable UUID id) throws NotFoundException, BadRequestException {
        return userSvc.findById(id);
    }

    @PostMapping("/users-administration")
    public User post(@Validated @RequestBody UserDTO userDTO, BindingResult validation) throws BadRequestException {
        if (validation.hasErrors())
            throw new BadRequestException(ValidationMessages.generateValidationErrorMessage(validation));
        return userSvc.create(userDTO);
    }

    @PutMapping("/users-administration/{id}")
    public User put(@Validated @RequestBody UserDTO userDTO, BindingResult validation, @PathVariable UUID id) throws BadRequestException {
        if (validation.hasErrors())
            throw new BadRequestException(ValidationMessages.generateValidationErrorMessage(validation));
        return userSvc.update(userDTO, id);
    }

    @PatchMapping("/users-administration/{id}")
    public User updateRole(@Validated @RequestBody RoleDTO roleDTO, BindingResult validation, @PathVariable UUID id) throws BadRequestException {
        return userSvc.updateRole(roleDTO, id);
    }

    @DeleteMapping("/users-administration/{id}")
    public DeleteRes delete(@Validated @RequestBody UserDTO userDTO, BindingResult validation, @PathVariable UUID id) throws BadRequestException {
        return userSvc.delete(id);
    }

}
