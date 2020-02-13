package it.fb5.imgshare.imgshare.service;

import it.fb5.imgshare.imgshare.configuration.SecurityPrincipal;
import it.fb5.imgshare.imgshare.dto.AlbumCreationDto;
import it.fb5.imgshare.imgshare.dto.AlbumDto;
import it.fb5.imgshare.imgshare.dto.AlbumImageDto;
import it.fb5.imgshare.imgshare.dto.UserFavoriteDto;
import it.fb5.imgshare.imgshare.exception.ValidationException;
import java.util.List;

public interface AlbumService {

    List<String> getAllTags();

    AlbumDto getAlbum(long albumId, SecurityPrincipal principal);

    List<AlbumDto> getAlbums(int limit, AlbumOrder order, SecurityPrincipal principal);

    List<AlbumDto> getOwnAlbums(int limit, AlbumOrder order, SecurityPrincipal principal);

    List<AlbumDto> getAlbumsByUser(long userId, int limit, AlbumOrder order,
            SecurityPrincipal principal);

    List<AlbumDto> getAlbumsByTag(String tagName, int limit, AlbumOrder order,
            SecurityPrincipal principal);

    List<AlbumImageDto> getAlbumImages(long albumId, SecurityPrincipal principal);

    List<UserFavoriteDto> getOwnFavoriteAlbums(int limit, SecurityPrincipal principal);

    List<UserFavoriteDto> getFavoriteAlbums(long userId, int limit, SecurityPrincipal principal);

    AlbumDto createAlbum(AlbumCreationDto creationDto, SecurityPrincipal principal)
            throws ValidationException;

    AlbumDto editAlbum(long id, AlbumCreationDto creationDto, SecurityPrincipal principal)
            throws ValidationException;

    AlbumDto voteAlbum(long albumId, int vote, SecurityPrincipal principal);

    AlbumDto favoriteAlbum(long albumId, SecurityPrincipal principal);

    enum AlbumOrder {
        TIMESTAMP,
        VOTE_SCORE
    }
}
