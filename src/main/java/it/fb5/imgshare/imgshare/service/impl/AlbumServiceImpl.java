package it.fb5.imgshare.imgshare.service.impl;

import it.fb5.imgshare.imgshare.configuration.SecurityPrincipal;
import it.fb5.imgshare.imgshare.dto.AlbumCreationDto;
import it.fb5.imgshare.imgshare.dto.AlbumDto;
import it.fb5.imgshare.imgshare.dto.AlbumImageDto;
import it.fb5.imgshare.imgshare.dto.UserFavoriteDto;
import it.fb5.imgshare.imgshare.entity.Album;
import it.fb5.imgshare.imgshare.entity.Album.Visibility;
import it.fb5.imgshare.imgshare.entity.AlbumImage;
import it.fb5.imgshare.imgshare.entity.AlbumVote;
import it.fb5.imgshare.imgshare.entity.Image;
import it.fb5.imgshare.imgshare.entity.User;
import it.fb5.imgshare.imgshare.entity.UserFavorite;
import it.fb5.imgshare.imgshare.exception.ValidationException;
import it.fb5.imgshare.imgshare.repository.AlbumRepository;
import it.fb5.imgshare.imgshare.repository.UserRepository;
import it.fb5.imgshare.imgshare.service.AlbumService;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository, UserRepository userRepository) {
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<String> getAllTags() {
        return StreamSupport
                .stream(this.albumRepository.findAllTags().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public AlbumDto getAlbum(long albumId, SecurityPrincipal principal) {
        var currentUser = this.getCurrentUser(principal);

        var album = this.albumRepository.findById(albumId).orElse(null);

        if (album == null) {
            return null;
        }

        return AlbumDto.fromAlbum(album, currentUser);
    }

    @Override
    public List<AlbumDto> getAlbums(int limit, AlbumOrder order, SecurityPrincipal principal) {
        var currentUser = this.getCurrentUser(principal);

        var stream = StreamSupport
                .stream(this.albumRepository.findAll().spliterator(), false)
                .filter(a -> a.getVisibility().equals(Album.Visibility.PUBLIC))
                .map(a -> AlbumDto.fromAlbum(a, currentUser));

        stream = this.getSortedStream(order, stream);

        if (limit > 0) {
            stream = stream.limit(limit);
        }

        return stream.collect(Collectors.toList());
    }

    @Override
    public List<AlbumDto> getOwnAlbums(int limit, AlbumOrder order, SecurityPrincipal principal) {
        var currentUser = this.getCurrentUser(principal);

        var stream = currentUser.getAlbums().stream()
                .map(a -> AlbumDto.fromAlbum(a, currentUser));

        stream = this.getSortedStream(order, stream);

        if (limit > 0) {
            stream = stream.limit(limit);
        }

        return stream.collect(Collectors.toList());
    }

    @Override
    public List<AlbumDto> getAlbumsByUser(long userId, int limit, AlbumOrder order,
            SecurityPrincipal principal) {
        var currentUser = this.getCurrentUser(principal);

        User user = this.userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Collections.emptyList();
        }

        var stream = user.getAlbums().stream()
                .filter(a -> a.getVisibility().equals(Album.Visibility.PUBLIC))
                .map(a -> AlbumDto.fromAlbum(a, currentUser));

        stream = this.getSortedStream(order, stream);

        if (limit > 0) {
            stream = stream.limit(limit);
        }

        return stream.collect(Collectors.toList());
    }

    @Override
    public List<AlbumDto> getAlbumsByTag(String tagName, int limit, AlbumOrder order,
            SecurityPrincipal principal) {
        var currentUser = this.getCurrentUser(principal);

        var stream = StreamSupport
                .stream(this.albumRepository.findAllByTags(tagName).spliterator(), false)
                .filter(a -> a.getVisibility().equals(Album.Visibility.PUBLIC))
                .map(a -> AlbumDto.fromAlbum(a, currentUser));

        stream = this.getSortedStream(order, stream);

        if (limit > 0) {
            stream = stream.limit(limit);
        }

        return stream.collect(Collectors.toList());
    }

    @Override
    public List<AlbumImageDto> getAlbumImages(long albumId, SecurityPrincipal principal) {
        var album = this.albumRepository.findById(albumId).orElse(null);

        if (album == null) {
            return Collections.emptyList();
        }

        return album.getImages().stream()
                .map(AlbumImageDto::fromAlbumImage)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserFavoriteDto> getOwnFavoriteAlbums(int limit, SecurityPrincipal principal) {
        var currentUser = this.getCurrentUser(principal);

        var stream = currentUser.getFavorites().stream()
                .map(a -> UserFavoriteDto.fromUserFavorite(a, currentUser));

        stream = stream
                .sorted(Comparator.comparing(UserFavoriteDto::getFavoriteTimestamp).reversed());

        if (limit > 0) {
            stream = stream.limit(limit);
        }

        return stream.collect(Collectors.toList());
    }

    @Override
    public List<UserFavoriteDto> getFavoriteAlbums(long userId, int limit,
            SecurityPrincipal principal) {
        var currentUser = this.getCurrentUser(principal);
        var user = this.userRepository.findById(userId).orElse(null);

        if (user == null) {
            return Collections.emptyList();
        }

        var stream = user.getFavorites().stream()
                .filter(a -> a.getAlbum().getVisibility().equals(Album.Visibility.PUBLIC))
                .map(a -> UserFavoriteDto.fromUserFavorite(a, currentUser));

        stream = stream
                .sorted(Comparator.comparing(UserFavoriteDto::getFavoriteTimestamp).reversed());

        if (limit > 0) {
            stream = stream.limit(limit);
        }

        return stream.collect(Collectors.toList());
    }

    @Override
    public AlbumDto createAlbum(AlbumCreationDto creationDto, SecurityPrincipal principal)
            throws ValidationException {

        var currentUser = getCurrentUser(principal);

        Image coverImage = null;
        var images = new ArrayList<AlbumImage>();

        for (Image image : currentUser.getImages()) {
            for (AlbumImageDto imageDto : creationDto.getImages()) {
                if (image.getId() == imageDto.getId()) {
                    AlbumImage albumImage = new AlbumImage();
                    albumImage.setImage(image);
                    albumImage.setDescription(imageDto.getDescription());
                    images.add(albumImage);

                    if (image.getId() == creationDto.getCoverImage()) {
                        coverImage = image;
                    }
                }
            }
        }

        if (images.size() <= 0) {
            throw new ValidationException("images", "Album has no images!");
        }

        if (coverImage == null) {
            throw new ValidationException("coverImage", "Album cover is not set!");
        }

        var album = new Album();
        album.setImages(images);
        album.setCoverImage(coverImage);
        album.setUser(currentUser);
        album.setTitle(creationDto.getTitle());
        album.setDescription(creationDto.getDescription());
        album.setVotes(Collections.emptyList());
        album.setComments(Collections.emptyList());

        var visibility = creationDto.getVisibility();

        if (Visibility.PUBLIC.ordinal() == visibility) {
            album.setVisibility(Visibility.PUBLIC);
        }

        if (Visibility.PRIVATE.ordinal() == visibility) {
            album.setVisibility(Visibility.PRIVATE);
        }

        var tags = Arrays.stream(creationDto.getTags().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());

        album.setCreationTimestamp(ZonedDateTime.now());
        album.setTags(tags);

        album = this.albumRepository.save(album);

        return AlbumDto.fromAlbum(album, currentUser);
    }

    @Override
    public AlbumDto editAlbum(long id, AlbumCreationDto creationDto, SecurityPrincipal principal)
            throws ValidationException {

        var currentUser = getCurrentUser(principal);

        var album = this.albumRepository.findById(id).orElse(null);
        if (album == null) {
            return null;
        }

        Image coverImage = null;
        var images = new ArrayList<AlbumImage>();

        for (Image image : currentUser.getImages()) {
            for (AlbumImageDto imageDto : creationDto.getImages()) {
                if (image.getId() == imageDto.getId()) {
                    AlbumImage albumImage = new AlbumImage();
                    albumImage.setImage(image);
                    albumImage.setDescription(imageDto.getDescription());
                    images.add(albumImage);

                    if (image.getId() == creationDto.getCoverImage()) {
                        coverImage = image;
                    }
                }
            }
        }

        if (images.size() <= 0) {
            throw new ValidationException("images", "Album has no images!");
        }

        if (coverImage == null) {
            throw new ValidationException("coverImage", "Album cover is not set!");
        }

        album.setImages(images);
        album.setCoverImage(coverImage);

        var visibility = creationDto.getVisibility();

        if (Visibility.PUBLIC.ordinal() == visibility) {
            album.setVisibility(Visibility.PUBLIC);
        }

        if (Visibility.PRIVATE.ordinal() == visibility) {
            album.setVisibility(Visibility.PRIVATE);
        }

        var tags = Arrays.stream(creationDto.getTags().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());

        album.setTitle(creationDto.getTitle());
        album.setDescription(creationDto.getDescription());
        album.setEditTimestamp(ZonedDateTime.now());
        album.setTags(tags);

        album = this.albumRepository.save(album);

        return AlbumDto.fromAlbum(album, currentUser);
    }

    @Override
    public AlbumDto voteAlbum(long albumId, int vote, SecurityPrincipal principal) {
        var currentUser = getCurrentUser(principal);

        Album album = this.albumRepository.findById(albumId).orElse(null);
        if (album == null) {
            return null;
        }

        var votes = album.getVotes();
        var currentVote = votes.stream()
                .filter(v -> v.getUser().equals(currentUser))
                .findAny();

        if (currentVote.isPresent()) {
            if (currentVote.get().getVote() == vote) {
                votes.remove(currentVote.get());
            } else {
                currentVote.get().setVote(vote);
            }
        } else {
            votes.add(new AlbumVote(currentUser, vote));
        }

        album.setVotes(votes);

        album = this.albumRepository.save(album);

        return AlbumDto.fromAlbum(album, currentUser);
    }

    @Override
    public AlbumDto favoriteAlbum(long albumId, SecurityPrincipal principal) {
        var currentUser = getCurrentUser(principal);

        Album album = this.albumRepository.findById(albumId).orElse(null);
        if (album == null) {
            return null;
        }

        var favorites = currentUser.getFavorites();
        var favorite = favorites.stream()
                .filter(f -> f.getAlbum().equals(album))
                .findAny();

        if (favorite.isPresent()) {
            favorites.remove(favorite.get());
        } else {
            favorites.add(new UserFavorite(album, ZonedDateTime.now()));
        }

        currentUser.setFavorites(favorites);

        this.userRepository.save(currentUser);

        return AlbumDto.fromAlbum(album, currentUser);
    }

    private User getCurrentUser(SecurityPrincipal principal) {
        if (principal == null) {
            return null;
        }

        return this.userRepository.findById(principal.getUserId()).orElseThrow();
    }

    private Stream<AlbumDto> getSortedStream(AlbumOrder order, Stream<AlbumDto> stream) {
        switch (order) {
            default:
            case TIMESTAMP:
                stream = stream
                        .sorted(Comparator.comparing(AlbumDto::getCreationTimestamp).reversed());
                break;
            case VOTE_SCORE:
                stream = stream.sorted(Comparator.comparing(AlbumDto::getVoteScore).reversed());
                break;
        }

        return stream;
    }
}
