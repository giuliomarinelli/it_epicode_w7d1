package it.epicode.w6d5.devices_management.controllers;

import it.epicode.w6d5.devices_management.Models.entities.User;
import it.epicode.w6d5.devices_management.Models.reqDTO.AccessTokenRes;
import it.epicode.w6d5.devices_management.Models.reqDTO.LoginDTO;
import it.epicode.w6d5.devices_management.Models.reqDTO.UserDTO;
import it.epicode.w6d5.devices_management.exceptions.BadRequestException;
import it.epicode.w6d5.devices_management.exceptions.NotFoundException;
import it.epicode.w6d5.devices_management.exceptions.UnauthorizedException;
import it.epicode.w6d5.devices_management.exceptions.ValidationMessages;
import it.epicode.w6d5.devices_management.security.JwtTools;
import it.epicode.w6d5.devices_management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    private UserService userSvc;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    JwtTools jwtTools;

    @PostMapping("/auth/register")
    public User register(@RequestBody @Validated UserDTO userDTO, BindingResult validation) throws BadRequestException {
        if (validation.hasErrors())
            throw new BadRequestException(ValidationMessages.generateValidationErrorMessage(validation));
        return userSvc.create(userDTO);
    }

    @PostMapping("/auth/login")
    public AccessTokenRes login(@RequestBody @Validated LoginDTO loginDTO, BindingResult validation) throws BadRequestException, NotFoundException {
        if (validation.hasErrors())
            throw new BadRequestException(ValidationMessages.generateValidationErrorMessage(validation));
        User user = userSvc.findByEmail(loginDTO.email());
        if (!encoder.matches(loginDTO.password(), user.getHashPassword()))
            throw new UnauthorizedException("Email and/or password are not correct");

        return new AccessTokenRes(jwtTools.createToken(user));
    }
}
