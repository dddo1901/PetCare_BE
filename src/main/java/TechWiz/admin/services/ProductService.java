package TechWiz.admin.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.admin.models.Product;
import TechWiz.admin.models.ProductCategory;
import TechWiz.admin.models.dto.CreateProductRequest;
import TechWiz.admin.models.dto.UpdateProductStatusRequest;
import TechWiz.admin.repositories.ProductRepository;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Admin methods
    public Product createProduct(CreateProductRequest request, Long adminUserId) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setCreatedBy(adminUserId);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        product.setIsAvailable(true); // Default to available

        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(Long productId, CreateProductRequest request, Long adminUserId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setBrand(request.getBrand());
            product.setPrice(request.getPrice());
            product.setCategory(request.getCategory());
            product.setImageUrl(request.getImageUrl());
            product.setUpdatedBy(adminUserId);
            product.setUpdatedAt(LocalDateTime.now());
            
            // Update isActive if provided
            if (request.getIsActive() != null) {
                product.setIsActive(request.getIsActive());
            }

            return Optional.of(productRepository.save(product));
        }
        return Optional.empty();
    }

    public boolean updateProductStatus(Long productId, UpdateProductStatusRequest request, Long adminUserId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (request.getIsActive() != null) {
                product.setIsActive(request.getIsActive());
            }
            if (request.getIsAvailable() != null) {
                product.setIsAvailable(request.getIsAvailable());
            }
            product.setUpdatedBy(adminUserId);
            product.setUpdatedAt(LocalDateTime.now());
            productRepository.save(product);
            return true;
        }
        return false;
    }

    public boolean deleteProduct(Long productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
            return true;
        }
        return false;
    }

    // Public methods (for all users)
    public Page<Product> getActiveProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByIsActiveTrueAndIsAvailableTrueOrderByCreatedAtDesc(pageable);
    }

    public Page<Product> getProductsByCategory(ProductCategory category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategoryAndIsActiveTrueAndIsAvailableTrueOrderByCreatedAtDesc(category, pageable);
    }

    public Page<Product> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.searchActiveProducts(keyword, pageable);
    }

    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    // Admin-only methods
    public Page<Product> getAllProductsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<Product> getProductsByCreator(Long creatorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCreatedByOrderByCreatedAtDesc(creatorId, pageable);
    }

    public Page<Product> searchAllProductsForAdmin(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.searchAllProducts(keyword, pageable);
    }

    // Statistics methods
    public Long getProductCountByCategory(ProductCategory category) {
        return productRepository.countByCategoryAndIsActiveTrueAndIsAvailableTrue(category);
    }

    // Utility methods
    public boolean isProductAvailable(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        return product.isPresent() && 
               product.get().getIsActive() && 
               product.get().getIsAvailable();
    }
}
