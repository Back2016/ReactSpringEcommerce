package com.yorkDev.buynowdotcom.service.productSpec;

import com.yorkDev.buynowdotcom.dtos.ProductSpecDto;
import com.yorkDev.buynowdotcom.model.Product;
import com.yorkDev.buynowdotcom.model.ProductSpec;
import com.yorkDev.buynowdotcom.repository.ProductSpecRepository;
import com.yorkDev.buynowdotcom.request.AddProductSpecRequest;
import com.yorkDev.buynowdotcom.service.product.IProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSpecService implements IProductSpecService {
    private final ProductSpecRepository productSpecRepository;
    private final IProductService productService;
    private final ModelMapper modelMapper;

    @Override
    public ProductSpec getProductSpecById(Long productSpecId) {
        return productSpecRepository.findById(productSpecId)
                .orElseThrow(() -> new EntityNotFoundException("Product Spec not found!"));
    }

    @Override
    @Transactional
    public void deleteProductSpecById(Long productSpecId) {
        productSpecRepository.findById(productSpecId)
                .ifPresentOrElse(spec -> {
                    // Break relationship from the product side
                    Product product = spec.getProduct();
                    if (product != null) {
                        product.getProductSpecs().remove(spec);
                        spec.setProduct(null);
                    }

                    // Now delete the image
                    productSpecRepository.delete(spec);
                }, () -> {
                    throw new EntityNotFoundException("Spec not found!");
                });
    }

    @Override
    @Transactional
    public List<ProductSpecDto> addProductSpecs(List<AddProductSpecRequest> requests, Long productId) {
        Product product = productService.getProductById(productId);

        List<ProductSpecDto> savedProductSpecs = new ArrayList<>();

        for (AddProductSpecRequest request : requests) {
            ProductSpec spec = new ProductSpec();
            spec.setSpecName(request.getSpecName());
            spec.setValue(request.getSpecValue());
            spec.setProduct(product);
            ProductSpec savedSpec = productSpecRepository.save(spec);
            product.getProductSpecs().add(savedSpec);

            // Formulate specDto
            ProductSpecDto specDto = convertToDto(savedSpec);
            savedProductSpecs.add(specDto);
        }
        return savedProductSpecs;
    }

    @Override
    public ProductSpecDto convertToDto(ProductSpec productSpec) {
        return modelMapper.map(productSpec, ProductSpecDto.class);
    }
}
