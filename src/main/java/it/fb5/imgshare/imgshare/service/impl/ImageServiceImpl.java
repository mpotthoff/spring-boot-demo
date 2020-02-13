package it.fb5.imgshare.imgshare.service.impl;

import it.fb5.imgshare.imgshare.configuration.SecurityPrincipal;
import it.fb5.imgshare.imgshare.dto.ImageDto;
import it.fb5.imgshare.imgshare.entity.Image;
import it.fb5.imgshare.imgshare.repository.ImageRepository;
import it.fb5.imgshare.imgshare.repository.UserRepository;
import it.fb5.imgshare.imgshare.service.ImageService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository, UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ImageDto getImage(long imageId) {
        var image = this.imageRepository.findById(imageId).orElse(null);

        if (image == null) {
            return null;
        }

        return ImageDto.fromImage(image);
    }

    @Override
    public List<ImageDto> getOwnImages(int limit, SecurityPrincipal principal) {
        return getImagesByUser(limit, principal.getUserId());
    }

    @Override
    public List<ImageDto> getImagesByUser(int limit, long userId) {
        var currentUser = this.userRepository.findById(userId).orElseThrow();

        var stream = currentUser.getImages().stream()
                .map(ImageDto::fromImage)
                .sorted(Comparator.comparing(ImageDto::getUploadTimestamp).reversed());

        if (limit > 0) {
            stream = stream.limit(limit);
        }

        return stream.collect(Collectors.toList());
    }

    @Override
    public byte[] getImageData(long imageId, int maxWidth) throws IOException {
        var image = this.imageRepository.findById(imageId).orElse(null);

        if (image == null) {
            return null;
        }

        var data = image.getData();

        if (maxWidth < 1) {
            return data;
        }

        var decodedImage = ImageIO.read(new ByteArrayInputStream(data));

        var width = decodedImage.getWidth();
        var height = decodedImage.getHeight();

        if (width <= maxWidth) {
            return data;
        }

        height = (int) (((float) height) * maxWidth / width);
        width = maxWidth;

        var scaledImage = new BufferedImage(width, height, decodedImage.getType());

        var graphics = scaledImage.createGraphics();

        graphics.drawImage(
                decodedImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0,
                null);
        graphics.dispose();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(scaledImage, "png", outputStream);
            return outputStream.toByteArray();
        }
    }

    @Override
    public ImageDto createImage(byte[] data, SecurityPrincipal principal) throws IOException {
        var currentUser = this.userRepository.findById(principal.getUserId()).orElseThrow();

        byte[] encoded;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(ImageIO.read(new ByteArrayInputStream(data)), "png", outputStream);
            encoded = outputStream.toByteArray();
        }

        var image = new Image();

        image.setUser(currentUser);
        image.setUploadTimestamp(ZonedDateTime.now());
        image.setData(encoded);

        image = this.imageRepository.save(image);

        return ImageDto.fromImage(image);
    }
}
