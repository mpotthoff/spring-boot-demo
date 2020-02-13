package it.fb5.imgshare.imgshare;

import com.github.javafaker.Faker;
import it.fb5.imgshare.imgshare.entity.Album;
import it.fb5.imgshare.imgshare.entity.Album.Visibility;
import it.fb5.imgshare.imgshare.entity.AlbumImage;
import it.fb5.imgshare.imgshare.entity.AlbumVote;
import it.fb5.imgshare.imgshare.entity.Comment;
import it.fb5.imgshare.imgshare.entity.CommentVote;
import it.fb5.imgshare.imgshare.entity.Image;
import it.fb5.imgshare.imgshare.entity.User;
import it.fb5.imgshare.imgshare.entity.UserFavorite;
import it.fb5.imgshare.imgshare.repository.AlbumRepository;
import it.fb5.imgshare.imgshare.repository.CommentRepository;
import it.fb5.imgshare.imgshare.repository.ImageRepository;
import it.fb5.imgshare.imgshare.repository.UserRepository;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class FakeInitializer {

    @Autowired
    private Environment environment;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserRepository userRepository;

    private static byte[] encodePng(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        }
    }

    @PostConstruct
    public void populate() throws Exception {
        if (!Arrays.asList(this.environment.getActiveProfiles()).contains("fake")) {
            return;
        }

        if (this.userRepository.count() > 0) {
            return;
        }

        Faker faker = new Faker(Locale.GERMANY);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String[] dimensions = new String[]{"320/200", "640/200", "640/350", "640/480", "720/348",
                "1024/768", "1280/1024", "1366/768", "1600/1200", "1680/1050", "1920/1200"};
        String[] tags = new String[]{"nice", "beautiful", "ugly", "deep", "help", "oof", "yikes",
                "31"};

        User[] users = new User[10];

        for (int i = 0; i < users.length; i++) {
            User user = new User();
            user.setUsername(faker.name().username());
            user.setEmail(faker.internet().emailAddress(user.getUsername()));
            user.setPassword(encoder.encode(user.getUsername()));
            users[i] = this.userRepository.save(user);
        }

        Image[] images = new Image[100];

        for (int i = 0; i < images.length; i++) {
            Image image = new Image();
            image.setUser(users[i % users.length]);
            image.setUploadTimestamp(
                    ZonedDateTime.ofInstant(faker.date().past(30, TimeUnit.DAYS).toInstant(),
                            ZoneId.systemDefault()));
            image.setData(encodePng(ImageIO.read(new URL(
                    "https://picsum.photos/" + dimensions[faker.random().nextInt(dimensions.length)]
                            + ".jpg"))));
            images[i] = this.imageRepository.save(image);
        }

        Album[] albums = new Album[50];

        for (int i = 0; i < albums.length; i++) {
            Album album = new Album();
            album.setUser(users[i % users.length]);
            album.setCreationTimestamp(
                    ZonedDateTime.ofInstant(faker.date().past(30, TimeUnit.DAYS).toInstant(),
                            ZoneId.systemDefault()));
            if (faker.bool().bool()) {
                album.setEditTimestamp(
                        ZonedDateTime.ofInstant(faker.date().past(7, TimeUnit.DAYS).toInstant(),
                                ZoneId.systemDefault()));
            }
            String albumDescription = faker.shakespeare().hamletQuote();
            if (albumDescription.length() > 255) {
                albumDescription = albumDescription.substring(0, 255);
            }
            album.setDescription(albumDescription);
            if (faker.bool().bool()) {
                album.setVisibility(Visibility.PRIVATE);
            } else {
                album.setVisibility(Visibility.PUBLIC);
            }
            album.setTitle(faker.funnyName().name());

            List<String> newTags = new ArrayList<>();
            for (int j = 0; j < faker.random().nextInt(3) + 3; j++) {
                String tag = tags[faker.random().nextInt(tags.length)];
                if (!newTags.contains(tag)) {
                    newTags.add(tag);
                } else {
                    j--;
                }
            }
            album.setTags(newTags);

            List<Image> userImages = Stream.of(images)
                    .filter((u) -> u.getUser().equals(album.getUser()))
                    .collect(Collectors.toList());

            List<AlbumImage> albumImages = new ArrayList<>();
            for (int j = 0; j < faker.random().nextInt(5) + 1; j++) {
                AlbumImage image = new AlbumImage();
                image.setImage(userImages.get(faker.random().nextInt(userImages.size())));
                if (faker.bool().bool()) {
                    String description = faker.harryPotter().quote();
                    if (description.length() > 255) {
                        description = description.substring(0, 255);
                    }
                    image.setDescription(description);
                }
                albumImages.add(image);
            }
            album.setImages(albumImages);

            List<AlbumVote> votes = new ArrayList<>();
            for (int j = 0; j < users.length; j++) {
                int vote = faker.random().nextInt(-1, 1);
                if (vote != 0) {
                    votes.add(new AlbumVote(users[j], vote));
                }
            }
            album.setVotes(votes);

            album.setCoverImage(albumImages.get(faker.random().nextInt(albumImages.size()))
                    .getImage());

            albums[i] = this.albumRepository.save(album);

            Comment[] comments = new Comment[faker.random().nextInt(10)];

            for (int j = 0; j < comments.length; j++) {
                Comment comment = new Comment();
                comment.setAlbum(albums[i]);
                comment.setUser(users[faker.random().nextInt(users.length)]);
                comment.setCreationTimestamp(
                        ZonedDateTime.ofInstant(faker.date().past(7, TimeUnit.DAYS).toInstant(),
                                ZoneId.systemDefault()));

                String commentText = faker.hitchhikersGuideToTheGalaxy().quote();
                if (commentText.length() > 255) {
                    commentText = commentText.substring(0, 255);
                }
                comment.setText(commentText);

                List<CommentVote> commentVotes = new ArrayList<>();
                for (int k = 0; k < users.length; k++) {
                    int vote = faker.random().nextInt(-1, 1);
                    if (vote != 0) {
                        commentVotes.add(new CommentVote(users[k], vote));
                    }
                }
                comment.setVotes(commentVotes);

                comments[j] = this.commentRepository.save(comment);
            }
        }

        for (int i = 0; i < users.length; i++) {
            User user = users[i];

            List<UserFavorite> favorites = new ArrayList<>();
            for (int j = 0; j < faker.random().nextInt(10); j++) {
                Album album = albums[faker.random().nextInt(albums.length)];

                if (favorites.stream().noneMatch((f) -> f.getAlbum().equals(album))) {
                    UserFavorite favorite = new UserFavorite(album,
                            ZonedDateTime.ofInstant(faker.date().past(7, TimeUnit.DAYS).toInstant(),
                                    ZoneId.systemDefault()));
                    favorites.add(favorite);
                }
            }
            user.setFavorites(favorites);

            this.userRepository.save(user);
        }
    }
}
