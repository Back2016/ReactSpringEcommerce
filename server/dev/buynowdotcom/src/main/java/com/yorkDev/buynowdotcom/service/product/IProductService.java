package com.yorkDev.buynowdotcom.service.product;

import com.yorkDev.buynowdotcom.dtos.ProductDto;
import com.yorkDev.buynowdotcom.model.Product;
import com.yorkDev.buynowdotcom.request.AddProductRequest;
import com.yorkDev.buynowdotcom.request.ProductUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest request);
    Product updateProduct(ProductUpdateRequest request, Long productId);
    Product getProductById(Long productId);
    void deleteProductById(Long productId);

    List<Product> getAllProducts();
    List<Product> getProductsByCategoryAndBrand(String category, String brand);
    List<Product> getProductsByBrandAndName(String brand, String name);
    List<Product> getProductsByCategory(String category);
    Page<Product> getProductsByCategoryPaginated(String category, int page, int limit);

    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByName(String name);

    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);

    List<String> getSuggestions(String query);

    public Page<Product> getPaginatedProductsByName(String name, int page, int limit);
}
