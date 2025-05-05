package com.yorkDev.buynowdotcom.service.image;

import com.yorkDev.buynowdotcom.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {
    Image getImageById(Long imageId);
    void deleteImageById(Long imageId);
    void updateImage(MultipartFile file, Long imageId);
    List<Image> saveImages(Long productId, List<MultipartFile> files);
}
