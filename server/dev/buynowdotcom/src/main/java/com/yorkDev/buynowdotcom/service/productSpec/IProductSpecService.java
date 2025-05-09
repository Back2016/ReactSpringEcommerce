package com.yorkDev.buynowdotcom.service.productSpec;

import com.yorkDev.buynowdotcom.dtos.ProductSpecDto;
import com.yorkDev.buynowdotcom.model.ProductSpec;
import com.yorkDev.buynowdotcom.request.AddProductSpecRequest;

import java.util.List;


public interface IProductSpecService {
    ProductSpec getProductSpecById(Long productSpecId);

    void deleteProductSpecById(Long productSpecId);

    List<ProductSpecDto> addProductSpecs(List<AddProductSpecRequest> requests, Long productId);

    ProductSpecDto convertToDto(ProductSpec productSpec);
}
