package it.fb5.imgshare.imgshare.repository;

import it.fb5.imgshare.imgshare.entity.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {

}
