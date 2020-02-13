package it.fb5.imgshare.imgshare.repository;

import it.fb5.imgshare.imgshare.entity.Album;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends CrudRepository<Album, Long> {

    Iterable<Album> findAllByTags(String tag);

    @Query(nativeQuery = true, value = "SELECT DISTINCT tag FROM album_tag ORDER BY tag ASC;")
    Iterable<String> findAllTags();
}
