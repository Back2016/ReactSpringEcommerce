package com.yorkDev.buynowdotcom.service.image;

import com.yorkDev.buynowdotcom.model.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {
    private final ImageRepository imageRepository;

    @Override
    public Image getImageById(Long imageId) {
        return null;
    }

    @Override
    public void deleteImageById(Long imageId) {

    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {

    }

    @Override
    public List<Image> saveImages(Long productId, List<MultipartFile> files) {
        return List.of();
    }
}
