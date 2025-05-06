package com.yorkDev.buynowdotcom.service.image;

import com.yorkDev.buynowdotcom.dtos.ImageDto;
import com.yorkDev.buynowdotcom.model.Image;
import com.yorkDev.buynowdotcom.model.Product;
import com.yorkDev.buynowdotcom.repository.ImageRepository;
import com.yorkDev.buynowdotcom.repository.ProductRepository;
import com.yorkDev.buynowdotcom.service.product.IProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {
    private final ImageRepository imageRepository;
    private final IProductService productService;

    @Override
    public Image getImageById(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found!"));
    }

//    @Override
//    public void deleteImageById(Long imageId) {
//        imageRepository.findById(imageId)
//                .ifPresentOrElse(
//                        imageRepository::delete,
//                        () -> {
//                            throw new EntityNotFoundException("Image not found!");
//                        }
//                );
//    }

    @Override
    @Transactional
    public void deleteImageById(Long imageId) {
        imageRepository.findById(imageId)
                .ifPresentOrElse(image -> {
                    // Break relationship from the product side
                    Product product = image.getProduct();
                    if (product != null) {
                        product.getImages().remove(image);
                        image.setProduct(null);
                    }

                    // Now delete the image
                    imageRepository.delete(image);
                }, () -> {
                    throw new EntityNotFoundException("Image not found!");
                });
    }


    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    // Uploads images, associates them with a product, generates download URLs, and returns DTOs
    @Override
    public List<ImageDto> saveImages(Long productId, List<MultipartFile> files) {
        Product product = productService.getProductById(productId);

        List<ImageDto> savedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                // Optimize with @PostPersist in Image model to avoid double save
                Image savedImage = imageRepository.save(image);

                ImageDto imageDto = new ImageDto();
                imageDto.setId(savedImage.getId());
                imageDto.setFileName(savedImage.getFileName());
                imageDto.setDownloadUrl(savedImage.getDownloadUrl());
                savedImages.add(imageDto);
            } catch (IOException | SQLException e) {
                throw new RuntimeException("Failed to save image: " + e.getMessage(), e);
            }
        }
        return savedImages;
    }
}
