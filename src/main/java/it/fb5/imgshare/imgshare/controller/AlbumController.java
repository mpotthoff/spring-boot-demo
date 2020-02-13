package it.fb5.imgshare.imgshare.controller;

import it.fb5.imgshare.imgshare.configuration.SecurityPrincipal;
import it.fb5.imgshare.imgshare.dto.AlbumCreationDto;
import it.fb5.imgshare.imgshare.dto.AlbumDto;
import it.fb5.imgshare.imgshare.dto.UserDto;
import it.fb5.imgshare.imgshare.exception.NotFoundException;
import it.fb5.imgshare.imgshare.exception.ValidationException;
import it.fb5.imgshare.imgshare.service.AlbumService;
import it.fb5.imgshare.imgshare.service.AlbumService.AlbumOrder;
import it.fb5.imgshare.imgshare.service.CommentService;
import it.fb5.imgshare.imgshare.service.CommentService.CommentOrder;
import it.fb5.imgshare.imgshare.service.UserService;
import java.util.List;
import javax.validation.Valid;
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
public class AlbumController {

    private final AlbumService albumService;
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public AlbumController(AlbumService albumService, CommentService commentService,
            UserService userService) {
        this.albumService = albumService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String getIndex(@RequestParam(name = "orderBy", defaultValue = "new") String orderBy,
            Model model,
            Authentication authentication) {

        final var principal = SecurityPrincipal.getPrincipal(authentication);

        AlbumOrder order;

        if ("top".equals(orderBy)) {
            order = AlbumOrder.VOTE_SCORE;
        } else {
            order = AlbumOrder.TIMESTAMP;
        }

        model.addAttribute("orderBy", orderBy);
        model.addAttribute("albums", this.albumService.getAlbums(20, order, principal));

        return "index";
    }

    @GetMapping("/t/{tagName}")
    public String getTag(@PathVariable String tagName,
            @RequestParam(name = "orderBy", defaultValue = "new") String orderBy, Model model,
            Authentication authentication) {

        final var principal = SecurityPrincipal.getPrincipal(authentication);

        AlbumOrder order;

        if ("top".equals(orderBy)) {
            order = AlbumOrder.VOTE_SCORE;
        } else {
            order = AlbumOrder.TIMESTAMP;
        }

        model.addAttribute("tagName", tagName);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("albums",
                this.albumService.getAlbumsByTag(tagName, 0, order, principal));

        return "tag";
    }

    @GetMapping("/t.json")
    public ResponseEntity<List<String>> getAllTags() {
        var tags = this.albumService.getAllTags();

        return ResponseEntity.ok(tags);
    }

    @GetMapping("/a/{id}")
    public String getAlbum(@PathVariable long id, Model model, Authentication authentication,
            @RequestParam(name = "orderBy", defaultValue = "new") String orderBy) {
        final var principal = SecurityPrincipal.getPrincipal(authentication);

        UserDto user = null;

        if (principal != null) {
            user = this.userService.getOwnUser(principal);
        }

        AlbumDto album = this.albumService.getAlbum(id, principal);

        if (album == null) {
            throw new NotFoundException();
        }

        CommentOrder order;

        if ("top".equals(orderBy)) {
            order = CommentOrder.VOTE_SCORE;
        } else {
            order = CommentOrder.TIMESTAMP;
        }

        var images = this.albumService.getAlbumImages(id, principal);
        var comments = this.commentService.getComments(id, 0, order, principal);

        model.addAttribute("orderBy", orderBy);
        model.addAttribute("album", album);
        model.addAttribute("images", images);
        model.addAttribute("comments", comments);
        model.addAttribute("user", user);

        return "album";
    }

    @PostMapping("/a/{id}/favorite")
    public String favoriteAlbum(@PathVariable long id, Authentication authentication) {
        final var principal = SecurityPrincipal.getPrincipal(authentication);

        if (principal != null) {
            this.albumService.favoriteAlbum(id, principal);
        }

        return "redirect:/a/" + id;
    }

    @PostMapping("/a/{id}/vote")
    public String voteAlbum(@PathVariable long id, @RequestParam("action") String action,
            Authentication authentication) {
        final var principal = SecurityPrincipal.getPrincipal(authentication);

        if (principal != null) {
            this.albumService.voteAlbum(id, action.equals("upvote") ? 1 : -1, principal);
        }

        return "redirect:/a/" + id;
    }

    @PostMapping("/a/{id}/comment")
    public String commentAlbum(@PathVariable long id, @RequestParam("text") String text,
            Authentication authentication) {
        final var principal = SecurityPrincipal.getPrincipal(authentication);

        if (principal != null) {
            this.commentService.createComment(id, text, principal);
        }

        return "redirect:/a/" + id + "#comments";
    }

    @PostMapping("/a/{id}/comments/{commentId}/vote")
    public String voteComment(@PathVariable long id, @PathVariable long commentId,
            @RequestParam("action") String action,
            Authentication authentication) {
        final var principal = SecurityPrincipal.getPrincipal(authentication);

        if (principal != null) {
            this.commentService.voteComment(commentId, action.equals("upvote") ? 1 : -1, principal);
        }

        return "redirect:/a/" + id + "#" + commentId;
    }

    @GetMapping("/a/create")
    public String showAlbumCreatePage(Model model) {
        model.addAttribute("album", new AlbumCreationDto());

        return "album-create";
    }

    @PostMapping("/a/create")
    public String createAlbum(Authentication authentication,
            @Valid @ModelAttribute(name = "album") AlbumCreationDto creationDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "album-create";
        }

        final var principal = SecurityPrincipal.getPrincipal(authentication);

        AlbumDto album;

        try {
            album = this.albumService.createAlbum(creationDto, principal);
        } catch (ValidationException e) {
            bindingResult.addError(
                    new FieldError(
                            "album",
                            e.getField(),
                            e.getMessage()
                    )
            );

            return "album-create";
        }

        return "redirect:/a/" + album.getId();
    }

    @GetMapping("/a/{id}/edit")
    public String showEditAlbumPage(@PathVariable long id, Authentication authentication,
            Model model) {
        final var principal = SecurityPrincipal.getPrincipal(authentication);

        var album = this.albumService.getAlbum(id, principal);

        if (album == null || principal == null || principal.getUserId() != album.getUser()
                .getId()) {
            throw new NotFoundException();
        }

        var albumImages = this.albumService.getAlbumImages(id, principal);

        model.addAttribute("album", AlbumCreationDto.fromAlbumDto(album, albumImages));

        return "album-edit";
    }

    @PostMapping("/a/{id}/edit")
    public String editAlbum(@PathVariable long id, Authentication authentication,
            @Valid @ModelAttribute(name = "album") AlbumCreationDto creationDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "album-edit";
        }

        final var principal = SecurityPrincipal.getPrincipal(authentication);

        AlbumDto album;

        try {
            album = this.albumService.editAlbum(id, creationDto, principal);
        } catch (ValidationException e) {
            bindingResult.addError(
                    new FieldError(
                            "album",
                            e.getField(),
                            e.getMessage()
                    )
            );

            return "album-edit";
        }

        return "redirect:/a/" + album.getId();
    }
}
