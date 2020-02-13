package it.fb5.imgshare.imgshare.controller;

import it.fb5.imgshare.imgshare.configuration.SecurityPrincipal;
import it.fb5.imgshare.imgshare.dto.ImageDto;
import it.fb5.imgshare.imgshare.service.ImageService;
import java.io.IOException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/i/{id}.png")
    public ResponseEntity<byte[]> getImage(@PathVariable long id,
            @RequestParam(name = "maxWidth", defaultValue = "0") int maxWidth)
            throws IOException {

        byte[] data = this.imageService.getImageData(id, maxWidth);

        if (data == null) {
            return ResponseEntity.notFound()
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        var headers = new HttpHeaders();

        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setCacheControl(CacheControl.maxAge(Duration.ofDays(1)));

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<ImageDto> uploadImage(@RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        final var principal = SecurityPrincipal.getPrincipal(authentication);
        byte[] data = file.getBytes();

        return ResponseEntity.ok(this.imageService.createImage(data, principal));
    }
}
