package com.yorkDev.buynowdotcom.service.category;

import com.yorkDev.buynowdotcom.model.Category;
import com.yorkDev.buynowdotcom.repository.CategoryRepository;
import com.yorkDev.buynowdotcom.repository.ProductRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public Category addCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new EntityExistsException(category.getName() + " already exists!");
        }
        return categoryRepository.save(category);
//        return Optional.of(category)
//                .filter(c -> !categoryRepository.existsByName(c.getName()))
//                .map(categoryRepository::save)
//                .orElseThrow(() -> new EntityExistsException(category.getName() + " already exists!"));
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        return Optional.ofNullable(findCategoryById(categoryId)).map(oldCategory -> {
            oldCategory.setName(category.getName());
            return categoryRepository.save(oldCategory);
        }).orElseThrow(() -> new EntityNotFoundException("Category not found!"));
    }

    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.findById(categoryId).ifPresentOrElse(
                // Set the category of product under this category to be null and then delete category
                category -> {
                    category.getProducts().forEach(product -> product.setCategory(null));
                    productRepository.saveAll(category.getProducts()); // optional, to persist the update
                    categoryRepository.delete(category);
                }, () -> {
                    throw new EntityNotFoundException("Category not found!");
                });

    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }
}
