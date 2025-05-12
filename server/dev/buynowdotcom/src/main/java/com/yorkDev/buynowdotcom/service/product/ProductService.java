package com.yorkDev.buynowdotcom.service.product;

import com.yorkDev.buynowdotcom.dtos.ImageDto;
import com.yorkDev.buynowdotcom.dtos.ProductDto;
import com.yorkDev.buynowdotcom.dtos.ProductSpecDto;
import com.yorkDev.buynowdotcom.model.*;
import com.yorkDev.buynowdotcom.repository.*;
import com.yorkDev.buynowdotcom.request.AddProductRequest;
import com.yorkDev.buynowdotcom.request.ProductUpdateRequest;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    // Create repos
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ImageRepository imageRepository;
    private final ProductSpecRepository productSpecRepository;
    private final ModelMapper modelMapper;

    @Override
    public Product addProduct(AddProductRequest request) {
        if (productExists(request.getName(), request.getBrand())) {
            throw new EntityExistsException(request.getName() + " already exists!");
        }
        Category category = Optional
                .ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        return productRepository.save(createProduct(request, category));
    }

    // Helper methods for add Product
    private boolean productExists(String name, String brand) {
        return productRepository.existsByNameAndBrand(name, brand);
    }

    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                request.getLongDescription(),
                category
        );
    }
    // Helper methods for add Product Ends //

    @Override
    public Product updateProduct(ProductUpdateRequest request, Long productId) {
        return productRepository
                .findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, request))
                .map(productRepository::save)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    // Helper methods for update product
    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setLongDescription(request.getLongDescription());
        Category category = categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);
        return existingProduct;
    }
    // Helper methods for update product Ends//

    @Override
    public void deleteProductById(Long productId) {
        productRepository
                .findById(productId)
                .ifPresentOrElse(

                        product -> {
                            List<CartItem> cartItems = cartItemRepository.findByProductId(productId);
                            // Remove the cart items from the cart and delete them from cartItem repo
                            // Cart is dynamic, and the transaction is not done yet, so delete the cartItem is fine.
                            cartItems.forEach(cartItem -> {
                                Cart cart = cartItem.getCart();
                                cart.removeItem(cartItem);
                                cartItemRepository.delete(cartItem);
                            });

                            List<OrderItem> orderItems = orderItemRepository.findByProductId(productId);
                            // For orders, the transactions are done, and we want to keep other histories
                            // So just break relationship between orderItem and product (deleted, so set to null)
                            // But the orderItem's other infos are kept.
                            orderItems.forEach(orderItem -> {
                                orderItem.setProduct(null);
                                orderItemRepository.save(orderItem);
                            });

                            // Cascade in product for category is set to all,
                            // so need to manually break relationships before moving on
                            // Break relationship between category and the product
                            // Delete product from category
                            Optional.ofNullable(product.getCategory())
                                    .ifPresent(category -> category.getProducts().remove(product));
                            // Set category of the product to null
                            product.setCategory(null);
                            // Finally delete product
                            productRepository.deleteById(product.getId());
                        }
                        ,
                        () -> {
                            throw new EntityNotFoundException("Product not found!");
                        }
                );

    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found!"));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByNameAndBrand(name, brand);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public Page<Product> getProductsByCategoryPaginated(String category, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return productRepository.findByCategoryName(category, pageable);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream().map(this :: convertToDto).toList();
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        Long productId = product.getId();
        // Set images to productDto
        List<Image> images =  imageRepository.findByProductId(productId);
        List<ImageDto> imageDtos = images.stream().map(image -> modelMapper.map(image, ImageDto.class)).toList();
        productDto.setImages(imageDtos);
        // Set Specs to productDto
        List<ProductSpec> specs = productSpecRepository.findByProductId(productId);
        List<ProductSpecDto> specDtos = specs.stream().map(spec -> modelMapper.map(spec, ProductSpecDto.class)).toList();
        productDto.setProductSpecs(specDtos);
        return productDto;
    }
}
