# 🛒 PetCare - Product Management & Shopping Cart System

## 📋 Tổng quan

Hệ thống quản lý sản phẩm và giỏ hàng cho PetCare cho phép:
- **Admin**: Tạo, quản lý sản phẩm cho vật nuôi
- **Pet Owner**: Duyệt và thêm sản phẩm vào giỏ hàng

## 🏗️ Kiến trúc hệ thống

### Models
- **Product**: Sản phẩm với thông tin chi tiết (giá, stock, danh mục, loại pet)
- **CartItem**: Item trong giỏ hàng của user
- **ProductCategory**: Enum các danh mục sản phẩm 
- **PetType**: Enum các loại thú cưng

### Repositories
- **ProductRepository**: Truy vấn sản phẩm với filtering, search
- **CartItemRepository**: Quản lý giỏ hàng

### Services  
- **ProductService**: Business logic cho sản phẩm
- **CartService**: Business logic cho giỏ hàng

### Controllers
- **AdminProductController**: API cho admin quản lý sản phẩm
- **PublicProductController**: API public để browse sản phẩm
- **CartController**: API cho Pet Owner quản lý giỏ hàng

---

## 🔑 API Endpoints

### 🛡️ Admin Product Management
**Base URL**: `/api/admin/products`
**Yêu cầu**: Role ADMIN + JWT Token

#### Tạo sản phẩm mới
```http
POST /api/admin/products
Headers:
  Authorization: Bearer {jwt_token}
  user-id: {admin_user_id}
  Content-Type: application/json

{
  "name": "Premium Dog Food",
  "description": "High-quality nutrition for adult dogs",
  "brand": "PetNutrition Pro",
  "price": 25.99,
  "stockQuantity": 100,
  "category": "FOOD",
  "targetPetType": "DOG", 
  "imageUrl": "https://example.com/dog-food.jpg",
  "specifications": "{\"protein\": \"26%\", \"fat\": \"15%\"}",
  "ingredients": "[\"Chicken\", \"Rice\", \"Vegetables\"]",
  "usageInstructions": "{\"feeding_guide\": \"1-2 cups daily\"}",
  "weight": 5.0,
  "dimensions": "30 x 20 x 15",
  "ageGroup": "Adult",
  "isActive": true
}
```

#### Cập nhật sản phẩm
```http  
PUT /api/admin/products/{productId}
Headers:
  Authorization: Bearer {jwt_token}
  user-id: {admin_user_id}
```

#### Cập nhật trạng thái sản phẩm
```http
PATCH /api/admin/products/{productId}/status
Headers:
  Authorization: Bearer {jwt_token}
  user-id: {admin_user_id}

{
  "productId": 1,
  "isActive": false,
  "isAvailable": true,
  "reason": "Product recall"
}
```

#### Cập nhật stock
```http
PATCH /api/admin/products/{productId}/stock?stock=50
Headers:
  Authorization: Bearer {jwt_token}  
  user-id: {admin_user_id}
```

#### Xem tất cả sản phẩm (Admin)
```http
GET /api/admin/products?page=0&size=10
Headers:
  Authorization: Bearer {jwt_token}
```

#### Analytics - Sản phẩm sắp hết hàng
```http
GET /api/admin/products/analytics/low-stock?threshold=5
Headers:
  Authorization: Bearer {jwt_token}
```

#### Analytics - Sản phẩm hết hàng
```http
GET /api/admin/products/analytics/out-of-stock
Headers:
  Authorization: Bearer {jwt_token}
```

---

### 🌍 Public Product APIs
**Base URL**: `/api/products`
**Yêu cầu**: Không cần authentication

#### Xem sản phẩm có sẵn
```http
GET /api/products?page=0&size=12
```

#### Xem chi tiết sản phẩm
```http
GET /api/products/{productId}
```

#### Lọc sản phẩm theo danh mục
```http
GET /api/products/category/FOOD?page=0&size=12
```

#### Lọc sản phẩm theo loại pet
```http
GET /api/products/pet-type/DOG?page=0&size=12
```

#### Tìm kiếm sản phẩm
```http
GET /api/products/search?keyword=food&page=0&size=12
```

#### Lọc kết hợp
```http
GET /api/products/filter?category=FOOD&petType=DOG&page=0&size=12
```

#### Danh sách categories
```http
GET /api/products/categories
```

#### Danh sách pet types  
```http
GET /api/products/pet-types
```

---

### 🛒 Shopping Cart APIs
**Base URL**: `/api/cart`
**Yêu cầu**: Role PET_OWNER + JWT Token

#### Thêm sản phẩm vào giỏ
```http
POST /api/cart/add
Headers:
  Authorization: Bearer {jwt_token}
  user-id: {pet_owner_user_id}
  Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

#### Xem giỏ hàng
```http
GET /api/cart  
Headers:
  Authorization: Bearer {jwt_token}
  user-id: {pet_owner_user_id}
```

**Response:**
```json
{
  "success": true,
  "message": "Cart items retrieved",
  "data": {
    "items": [
      {
        "id": 1,
        "productId": 1,
        "productName": "Premium Dog Food",
        "productImage": "https://example.com/dog-food.jpg", 
        "price": 25.99,
        "quantity": 2,
        "subtotal": 51.98,
        "availableStock": 98,
        "isAvailable": true,
        "addedAt": "2024-01-15T10:30:00"
      }
    ],
    "total": 51.98,
    "totalQuantity": 2,
    "itemCount": 1
  }
}
```

#### Cập nhật số lượng
```http
PUT /api/cart/{productId}?quantity=3
Headers:
  Authorization: Bearer {jwt_token}
  user-id: {pet_owner_user_id}
```

#### Xóa sản phẩm khỏi giỏ
```http
DELETE /api/cart/{productId}
Headers:
  Authorization: Bearer {jwt_token}
  user-id: {pet_owner_user_id}
```

#### Xóa toàn bộ giỏ hàng
```http
DELETE /api/cart/clear
Headers:
  Authorization: Bearer {jwt_token}
  user-id: {pet_owner_user_id}
```

#### Tóm tắt giỏ hàng
```http
GET /api/cart/summary
Headers:
  Authorization: Bearer {jwt_token}
  user-id: {pet_owner_user_id}
```

#### Validate giỏ hàng (trước checkout)
```http
POST /api/cart/validate
Headers:
  Authorization: Bearer {jwt_token}
  user-id: {pet_owner_user_id}
```

---

## 📊 Product Categories

```java
public enum ProductCategory {
    FOOD("Thức ăn"),
    TOYS("Đồ chơi"), 
    HEALTH_CARE("Chăm sóc sức khỏe"),
    GROOMING("Vệ sinh làm đẹp"),
    ACCESSORIES("Phụ kiện"),
    HOUSING("Chuồng và nơi ở"),
    TRAINING("Huấn luyện"),
    CLOTHING("Quần áo"),
    TECHNOLOGY("Công nghệ"),
    SUPPLEMENTS("Thực phẩm bổ sung")
}
```

## 🐾 Pet Types

```java  
public enum PetType {
    DOG("Chó"),
    CAT("Mèo"),
    BIRD("Chim"),
    FISH("Cá"),
    RABBIT("Thỏ"),
    HAMSTER("Chuột hamster"),
    REPTILE("Bò sát"),
    OTHER("Khác")
}
```

---

## 🔧 Setup & Testing

### 1. Database Schema
Tables được tự động tạo bởi JPA:
- `products`
- `cart_items`

### 2. Seed Data
Admin account được tạo trong `DataSeeder`:
```java
// Username: admin@petcare.com
// Password: admin123
// Role: ADMIN
```

### 3. Test với Postman

#### A. Login Admin
```http
POST /api/auth/login
{
  "email": "admin@petcare.com", 
  "password": "admin123"
}
```

#### B. Tạo sản phẩm (Admin)
```http
POST /api/admin/products
Headers: Authorization: Bearer {admin_jwt}
```

#### C. Register Pet Owner
```http
POST /api/auth/register
{
  "email": "owner@test.com",
  "password": "password123",
  "role": "PET_OWNER",
  "roleBasedData": {
    "fullName": "Pet Owner",
    "phoneNumber": "0123456789"
  }
}
```

#### D. Add to Cart (Pet Owner)
```http
POST /api/cart/add  
Headers: Authorization: Bearer {pet_owner_jwt}
```

---

## ⚠️ Lưu ý quan trọng

### Security
- Admin endpoints yêu cầu role ADMIN
- Cart endpoints yêu cầu role PET_OWNER  
- Public product endpoints không cần auth

### Business Logic
- Stock tự động cập nhật `isAvailable` khi = 0
- Cart validation tự động loại bỏ sản phẩm không có sẵn
- Timestamps tự động được cập nhật

### Performance  
- Sử dụng pagination cho tất cả list endpoints
- Lazy loading cho Product relationships
- Indexing cho search queries

---

## 🚀 Tính năng mở rộng

### Sắp tới:
- [ ] Order management system
- [ ] Payment integration  
- [ ] Product reviews & ratings
- [ ] Inventory alerts
- [ ] Product recommendations
- [ ] Image upload service
- [ ] Discount & coupon system

Hệ thống đã sẵn sàng để tích hợp với frontend và testing! 🎉
