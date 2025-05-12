package com.yorkDev.buynowdotcom.controller;

import com.yorkDev.buynowdotcom.dtos.ProductDto;
import com.yorkDev.buynowdotcom.model.Product;
import com.yorkDev.buynowdotcom.request.AddProductRequest;
import com.yorkDev.buynowdotcom.request.ProductUpdateRequest;
import com.yorkDev.buynowdotcom.response.ApiResponse;
import com.yorkDev.buynowdotcom.response.PaginatedResponse;
import com.yorkDev.buynowdotcom.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
public class ProductController {
    private final IProductService productService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDto> productDtos = productService.getConvertedProducts(products);
        return ResponseEntity.ok().body(new ApiResponse("Found", productDtos));
    }

    @GetMapping("/product/{productId}/product")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        ProductDto productDto = productService.convertToDto(product);
        return ResponseEntity.ok().body(new ApiResponse("Found", productDto));
    }


    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest product) {
        Product theProduct = productService.addProduct(product);
        ProductDto productDto = productService.convertToDto(theProduct);
        return ResponseEntity.ok(new ApiResponse("Add product success!", productDto));

    }

    @PutMapping("/product/{productId}/update")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody ProductUpdateRequest request, @PathVariable Long productId) {
        Product theProduct = productService.updateProduct(request, productId);
        ProductDto productDto = productService.convertToDto(theProduct);
        return ResponseEntity.ok(new ApiResponse("Update product success!", productDto));
    }

    @DeleteMapping("/product/{productId}/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.ok(new ApiResponse("Delete product success!", productId));
    }

    // Spring will extract two params this way
    // GET /products/by/brand-and-name?brandName=Samsung&productName=Galaxy
    @GetMapping("/by/brand-and-name")
    public ResponseEntity<ApiResponse> getProductByBrandAndName(@RequestParam String brandName, @RequestParam String productName) {
        List<Product> products = productService.getProductsByBrandAndName(brandName, productName);
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    }

    @GetMapping("/by/category-and-brand")
    public ResponseEntity<ApiResponse> getProductByCategoryAndBrand(@RequestParam String category, @RequestParam String brand) {
        List<Product> products = productService.getProductsByCategoryAndBrand(category, brand);
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    }

    @GetMapping("/by/name")
    public ResponseEntity<ApiResponse> getProductByName(@RequestParam String name) {
        List<Product> products = productService.getProductsByName(name);
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    }

    @GetMapping("/by-brand")
    public ResponseEntity<ApiResponse> findProductByBrand(@RequestParam String brand) {
        List<Product> products = productService.getProductsByBrand(brand);
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    }

    @GetMapping("/{category}/all")
    public ResponseEntity<ApiResponse> findProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    }

    @GetMapping("/{category}")
    public ResponseEntity<ApiResponse> getProductsByCategoryPaginated(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int limit
    ) {
        Page<Product> pagedProducts = productService.getProductsByCategoryPaginated(category, page - 1, limit);
        List<ProductDto> productDtos = productService.getConvertedProducts(pagedProducts.getContent());

        PaginatedResponse<ProductDto> responseBody = new PaginatedResponse<>(
                productDtos,
                pagedProducts.getNumber() + 1,
                pagedProducts.getTotalPages(),
                pagedProducts.getTotalElements()
        );

        return ResponseEntity.ok(new ApiResponse("success", responseBody));
    }

}
