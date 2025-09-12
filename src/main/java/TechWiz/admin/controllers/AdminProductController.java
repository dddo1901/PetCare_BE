package TechWiz.admin.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import TechWiz.admin.models.Product;
import TechWiz.admin.models.ProductCategory;
import TechWiz.admin.models.PetType;
import TechWiz.admin.services.ProductService;
import TechWiz.admin.models.dto.CreateProductRequest;
import TechWiz.admin.models.dto.UpdateProductStatusRequest;
import TechWiz.auths.models.dto.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
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

    @PatchMapping("/{productId}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long productId,
                                       @RequestParam Integer stock,
                                       @RequestHeader("user-id") Long adminUserId) {
        try {
            boolean updated = productService.updateStock(productId, stock, adminUserId);
            if (updated) {
                return ResponseEntity.ok(new ApiResponse(true, "Stock updated successfully"));
            } else {
                return ResponseEntity.notFound()
                        .build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error updating stock: " + e.getMessage()));
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

    @GetMapping("/analytics/low-stock")
    public ResponseEntity<?> getLowStockProducts(@RequestParam(defaultValue = "5") Integer threshold) {
        try {
            List<Product> products = productService.getLowStockProducts(threshold);
            return ResponseEntity.ok(new ApiResponse(true, "Low stock products retrieved", products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving low stock products: " + e.getMessage()));
        }
    }

    @GetMapping("/analytics/out-of-stock")
    public ResponseEntity<?> getOutOfStockProducts() {
        try {
            List<Product> products = productService.getOutOfStockProducts();
            return ResponseEntity.ok(new ApiResponse(true, "Out of stock products retrieved", products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving out of stock products: " + e.getMessage()));
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

    @GetMapping("/analytics/pet-type-count")
    public ResponseEntity<?> getProductCountByPetType(@RequestParam PetType petType) {
        try {
            Long count = productService.getProductCountByPetType(petType);
            return ResponseEntity.ok(new ApiResponse(true, "Product count retrieved", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving product count: " + e.getMessage()));
        }
    }
}
