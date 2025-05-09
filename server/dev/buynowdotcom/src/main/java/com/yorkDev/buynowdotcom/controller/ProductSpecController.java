package com.yorkDev.buynowdotcom.controller;

import com.yorkDev.buynowdotcom.dtos.ProductSpecDto;
import com.yorkDev.buynowdotcom.model.ProductSpec;
import com.yorkDev.buynowdotcom.request.AddProductSpecRequest;
import com.yorkDev.buynowdotcom.response.ApiResponse;
import com.yorkDev.buynowdotcom.service.productSpec.ProductSpecService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/productSpecs")
public class ProductSpecController {
    private final ProductSpecService productSpecService;

    // GET spec by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getSpecById(@PathVariable Long id) {
            ProductSpec spec = productSpecService.getProductSpecById(id);
            ProductSpecDto specDto = productSpecService.convertToDto(spec);
            return ResponseEntity.ok().body(new ApiResponse("Found: ", specDto));
    }

    // POST: Add specs to a product
    @PostMapping("/product/{productId}")
    public ResponseEntity<ApiResponse> addSpecsToProduct(
            @PathVariable Long productId,
            @RequestBody List<AddProductSpecRequest> requests
    ) {
        List<ProductSpecDto> savedSpecs = productSpecService.addProductSpecs(requests, productId);
        return ResponseEntity.ok().body(new ApiResponse("Specs added: ", savedSpecs));
    }

    // DELETE spec by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteSpecById(@PathVariable Long id) {
        productSpecService.deleteProductSpecById(id);
        return ResponseEntity.ok().body(new ApiResponse("Deleted!", null));
    }
}
