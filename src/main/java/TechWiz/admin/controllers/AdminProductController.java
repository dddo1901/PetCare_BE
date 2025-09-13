package TechWiz.admin.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import TechWiz.admin.models.Product;
import TechWiz.admin.models.ProductCategory;
import TechWiz.admin.models.dto.CreateProductRequest;
import TechWiz.admin.models.dto.UpdateProductStatusRequest;
import TechWiz.admin.services.ProductService;
import TechWiz.auths.models.dto.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductRequest request,
                                         @RequestHeader("user-id") Long adminUserId) {
        try {
            Product product = productService.createProduct(request, adminUserId);
            return ResponseEntity.ok(new ApiResponse(true, "Product created successfully", product));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error creating product: " + e.getMessage()));
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                         @Valid @RequestBody CreateProductRequest request,
                                         @RequestHeader("user-id") Long adminUserId) {
        try {
            Optional<Product> updatedProduct = productService.updateProduct(productId, request, adminUserId);
            if (updatedProduct.isPresent()) {
                return ResponseEntity.ok(new ApiResponse(true, "Product updated successfully", updatedProduct.get()));
            } else {
                return ResponseEntity.notFound()
                        .build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error updating product: " + e.getMessage()));
        }
    }

    @PatchMapping("/{productId}/status")
    public ResponseEntity<?> updateProductStatus(@PathVariable Long productId,
                                               @Valid @RequestBody UpdateProductStatusRequest request,
                                               @RequestHeader("user-id") Long adminUserId) {
        try {
            boolean updated = productService.updateProductStatus(productId, request, adminUserId);
            if (updated) {
                return ResponseEntity.ok(new ApiResponse(true, "Product status updated successfully"));
            } else {
                return ResponseEntity.notFound()
                        .build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error updating product status: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        try {
            boolean deleted = productService.deleteProduct(productId);
            if (deleted) {
                return ResponseEntity.ok(new ApiResponse(true, "Product deleted successfully"));
            } else {
                return ResponseEntity.notFound()
                        .build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error deleting product: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Product> products = productService.getAllProductsForAdmin(page, size);
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
            if (product.isPresent()) {
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

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String keyword,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Product> products = productService.searchAllProductsForAdmin(keyword, page, size);
            return ResponseEntity.ok(new ApiResponse(true, "Search results retrieved", products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error searching products: " + e.getMessage()));
        }
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<?> getProductsByCreator(@PathVariable Long creatorId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Product> products = productService.getProductsByCreator(creatorId, page, size);
            return ResponseEntity.ok(new ApiResponse(true, "Products by creator retrieved", products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving products: " + e.getMessage()));
        }
    }

    @GetMapping("/analytics/category-count")
    public ResponseEntity<?> getProductCountByCategory(@RequestParam ProductCategory category) {
        try {
            Long count = productService.getProductCountByCategory(category);
            return ResponseEntity.ok(new ApiResponse(true, "Product count retrieved", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving product count: " + e.getMessage()));
        }
    }
}
