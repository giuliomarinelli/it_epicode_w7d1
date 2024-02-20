package it.epicode.w6d5.devices_management.security;

import it.epicode.w6d5.devices_management.Models.entities.User;
import it.epicode.w6d5.devices_management.exceptions.BadRequestException;
import it.epicode.w6d5.devices_management.exceptions.NotFoundException;
import it.epicode.w6d5.devices_management.exceptions.UnauthorizedException;
import it.epicode.w6d5.devices_management.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTools jwtTools;

    @Autowired
    private UserService userSvc;


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new UnauthorizedException("No provided access token");
        }

        String token = auth.split(" ")[1];

        jwtTools.validateToken(token);

        UUID userId = jwtTools.extractUserIdFromToken(token);

        User u;

        try {
            u = userSvc.findById(userId);
        } catch (NotFoundException | BadRequestException e) {
            throw new UnauthorizedException(e.getMessage());
        }


        if (req.getServletPath().startsWith("/my-profile")) {
            String[] path = req.getServletPath().split("/");
            UUID pathUserId = UUID.fromString(path[path.length - 1]);
            if (!pathUserId.equals(userId))
                throw new UnauthorizedException("Access denied");
        }


        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(req, res);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return new AntPathMatcher().match("/auth/**", request.getServletPath());
    }
}
