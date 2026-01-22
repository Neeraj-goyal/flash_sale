package com.orchestrate.flashsale.mapper;

import com.orchestrate.flashsale.entities.ProductEntity;
import com.orchestrate.flashsale.models.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductEntity mapProduct(Product product){
        return ProductEntity.builder()
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .productId(product.getProductId())
                .build();
    }

    public Product mapProductEntity(ProductEntity productEntity){
        return Product.builder()
                .name(productEntity.getName())
                .price(productEntity.getPrice())
                .stockQuantity(productEntity.getStockQuantity())
                .productId(productEntity.getProductId())
                .build();
    }
}
