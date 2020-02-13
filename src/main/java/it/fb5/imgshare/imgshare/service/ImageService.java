package it.fb5.imgshare.imgshare.service;

import it.fb5.imgshare.imgshare.configuration.SecurityPrincipal;
import it.fb5.imgshare.imgshare.dto.ImageDto;
import java.io.IOException;
import java.util.List;


public interface ImageService {

    ImageDto getImage(long imageId);

    List<ImageDto> getOwnImages(int limit, SecurityPrincipal principal);

    List<ImageDto> getImagesByUser(int limit, long userId);

    byte[] getImageData(long imageId, int maxWidth) throws IOException;

    ImageDto createImage(byte[] data, SecurityPrincipal principal) throws IOException;
}
