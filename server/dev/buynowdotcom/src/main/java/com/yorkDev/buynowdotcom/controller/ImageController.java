package com.yorkDev.buynowdotcom.controller;

import com.yorkDev.buynowdotcom.dtos.ImageDto;
import com.yorkDev.buynowdotcom.model.Image;
import com.yorkDev.buynowdotcom.response.ApiResponse;
import com.yorkDev.buynowdotcom.service.image.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/images")
public class ImageController {
    private final IImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("productId") Long productId) {
        List<ImageDto> imageDtos = imageService.saveImages(productId, files);
        return ResponseEntity.ok(new ApiResponse("Images uploaded successfully!", imageDtos));
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/image/download/{imageId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) throws SQLException {
        Image image = imageService.getImageById(imageId);
        ByteArrayResource resource = new ByteArrayResource(image.getImage().getBytes(1, (int) image.getImage().length()));
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(resource);
    }

    @PutMapping("/image/{imageId}/update")
    public ResponseEntity<ApiResponse> updateImage(@PathVariable Long imageId, @RequestParam("file") MultipartFile file) {
        imageService.updateImage(file, imageId);
        return ResponseEntity.ok(new ApiResponse("Image updated successfully!", null));
    }

    @DeleteMapping("/image/{imageId}/delete")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImageById(imageId);
        return ResponseEntity.ok(new ApiResponse("Delete Image success!", null));
    }
}
