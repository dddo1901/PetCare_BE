package TechWiz.admin.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import TechWiz.admin.models.Product;
import TechWiz.admin.models.ProductCategory;
import TechWiz.admin.services.ProductService;
import TechWiz.auths.models.dto.ApiResponse;

@RestController
@RequestMapping("/api/products")
public class PublicProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<?> getActiveProducts(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "12") int size) {
        try {
            Page<Product> products = productService.getActiveProducts(page, size);
            return ResponseEntity.ok(new ApiResponse(true, "Products retrieved successfully", products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving products: " + e.getMessage()));
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable Long productId) {
        try {
            Optional<Product> product = productService.getProductById(productId);
            if (product.isPresent() && product.get().getIsActive() && product.get().getIsAvailable()) {
                return ResponseEntity.ok(new ApiResponse(true, "Product found", product.get()));
            } else {
                return ResponseEntity.notFound()
                        .build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving product: " + e.getMessage()));
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable ProductCategory category,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "12") int size) {
        try {
            Page<Product> products = productService.getProductsByCategory(category, page, size);
            return ResponseEntity.ok(new ApiResponse(true, "Products by category retrieved", products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving products: " + e.getMessage()));
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getFilteredProducts(@RequestParam(required = false) ProductCategory category,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "12") int size) {
        try {
            Page<Product> products;
            
            if (category != null) {
                products = productService.getProductsByCategory(category, page, size);
            } else {
                products = productService.getActiveProducts(page, size);
            }
            
            return ResponseEntity.ok(new ApiResponse(true, "Filtered products retrieved", products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving products: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String keyword,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "12") int size) {
        try {
            Page<Product> products = productService.searchProducts(keyword, page, size);
            return ResponseEntity.ok(new ApiResponse(true, "Search results retrieved", products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error searching products: " + e.getMessage()));
        }
    }

    // Endpoint for getting category and pet type counts (for UI filters)
    @GetMapping("/categories")
    public ResponseEntity<?> getProductCategories() {
        try {
            ProductCategory[] categories = ProductCategory.values();
            return ResponseEntity.ok(new ApiResponse(true, "Categories retrieved", categories));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving categories: " + e.getMessage()));
        }
    }
}
