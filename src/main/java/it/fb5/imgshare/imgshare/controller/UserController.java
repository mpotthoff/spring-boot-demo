package it.fb5.imgshare.imgshare.controller;

import it.fb5.imgshare.imgshare.configuration.SecurityPrincipal;
import it.fb5.imgshare.imgshare.dto.ImageDto;
import it.fb5.imgshare.imgshare.dto.UserEditDto;
import it.fb5.imgshare.imgshare.dto.UserFavoriteDto;
import it.fb5.imgshare.imgshare.dto.UserRegistrationDto;
import it.fb5.imgshare.imgshare.exception.NotFoundException;
import it.fb5.imgshare.imgshare.exception.ValidationException;
import it.fb5.imgshare.imgshare.service.AlbumService;
import it.fb5.imgshare.imgshare.service.AlbumService.AlbumOrder;
import it.fb5.imgshare.imgshare.service.ImageService;
import it.fb5.imgshare.imgshare.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserService userService;
    private final ImageService imageService;
    private final AlbumService albumService;

    @Autowired
    public UserController(UserService userService, ImageService imageService,
            AlbumService albumService) {
        this.userService = userService;
        this.imageService = imageService;
        this.albumService = albumService;
    }

    @GetMapping("/register")
    public String getRegistration(Model model) {
        model.addAttribute("registration", new UserRegistrationDto());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("registration") UserRegistrationDto registrationDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            this.userService.registerUser(registrationDto);
        } catch (ValidationException e) {
            bindingResult.addError(
                    new FieldError(
                            "registration",
                            e.getField(),
                            e.getMessage()
                    )
            );

            return "register";
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String getLogin(Model model, Authentication authentication,
            @RequestParam(name = "redirect", required = false, defaultValue = "") String redirect) {
        final var principal = SecurityPrincipal.getPrincipal(authentication);

        if (principal != null) {
            if (StringUtils.isAllEmpty(redirect) || !redirect.startsWith("/")) {
                return "redirect:/";
            } else {
                return "redirect:" + redirect;
            }
        }

        if (!StringUtils.isAllEmpty(redirect)) {
            model.addAttribute("redirect", redirect);
        }

        return "login";
    }

    @GetMapping("/me/images.json")
    public ResponseEntity<List<ImageDto>> getOwnImagesJson(Authentication authentication) {
        final var principal = SecurityPrincipal.getPrincipal(authentication);

        var images = this.imageService.getOwnImages(0, principal);

        return ResponseEntity.ok(images);
    }

    @GetMapping(value = {"/me", "/me/images"})
    public String getOwnImages(Model model, Authentication authentication) {
        final var principal = SecurityPrincipal.getPrincipal(authentication);

        model.addAttribute("user", userService.getOwnUser(principal));
        model.addAttribute("images", this.imageService.getOwnImages(0, principal));

        return "profile-images";
    }

    @GetMapping("/me/albums")
    public String getOwnAlbums(Model model, Authentication authentication) {
        final var principal = SecurityPrincipal.getPrincipal(authentication);

        model.addAttribute("user", userService.getOwnUser(principal));
        model.addAttribute("albums",
                this.albumService.getOwnAlbums(0, AlbumOrder.TIMESTAMP, principal));

        return "profile-albums";
    }

    @GetMapping("/me/favorites")
    public String getOwnFavorites(Model model, Authentication authentication) {
        final var principal = SecurityPrincipal.getPrincipal(authentication);

        model.addAttribute("user", userService.getOwnUser(principal));
        model.addAttribute("albums",
                this.albumService.getOwnFavoriteAlbums(0, principal).stream()
                        .map(UserFavoriteDto::getAlbum)
                        .collect(Collectors.toList()));

        return "profile-favorites";
    }

    @GetMapping("/me/edit")
    public String getOwnProfile(Model model, Authentication authentication) {
        final var principal = SecurityPrincipal.getPrincipal(authentication);

        var user = this.userService.getOwnUser(principal);

        model.addAttribute("user", user);
        model.addAttribute("edit", UserEditDto.fromFullUserDto(user));

        return "profile-edit";
    }

    @PostMapping("/me/edit")
    public String editUser(
            @ModelAttribute("edit") UserEditDto userEditDto,
            BindingResult bindingResult, Model model, Authentication authentication) {

        final var principal = SecurityPrincipal.getPrincipal(authentication);

        var user = this.userService.getOwnUser(principal);

        model.addAttribute("user", user);

        if (bindingResult.hasErrors()) {
            return "profile-edit";
        }

        try {
            user = this.userService.updateUser(userEditDto, principal);
        } catch (ValidationException e) {
            bindingResult.addError(
                    new FieldError(
                            "registration",
                            e.getField(),
                            e.getMessage()
                    )
            );

            return "profile-edit";
        }

        model.addAttribute("edit", UserEditDto.fromFullUserDto(user));
        model.addAttribute("user", user);

        return "profile-edit";
    }

    @GetMapping(value = {"/u/{username}", "/u/{username}/albums"})
    public String getUserAlbum(@PathVariable String username, Model model,
            Authentication authentication,
            @RequestParam(name = "orderBy", defaultValue = "new") String orderBy) {
        final var principal = SecurityPrincipal.getPrincipal(authentication);

        AlbumOrder order;

        if ("top".equals(orderBy)) {
            order = AlbumOrder.VOTE_SCORE;
        } else {
            order = AlbumOrder.TIMESTAMP;
        }

        var user = this.userService.getUserByUsername(username);

        if (user == null) {
            throw new NotFoundException();
        }

        model.addAttribute("orderBy", orderBy);
        model.addAttribute("user", user);
        model.addAttribute("albums",
                this.albumService.getAlbumsByUser(user.getId(), 0, order, principal)
        );

        return "user-albums";
    }

    @GetMapping("/u/{username}/favorites")
    public String getUserFavorite(@PathVariable String username, Model model,
            Authentication authentication) {
        var principal = SecurityPrincipal.getPrincipal(authentication);

        var user = this.userService.getUserByUsername(username);

        if (user == null) {
            throw new NotFoundException();
        }

        model.addAttribute("user", user);
        model.addAttribute("albums",
                this.albumService.getFavoriteAlbums(user.getId(), 0, principal).stream()
                        .map(UserFavoriteDto::getAlbum)
                        .collect(Collectors.toList())
        );

        return "user-favorites";
    }
}
