package com.orchestrate.flashsale.service;

import com.orchestrate.flashsale.dao.ProductRepositoryDao;
import com.orchestrate.flashsale.entities.ProductEntity;
import com.orchestrate.flashsale.mapper.ProductMapper;
import com.orchestrate.flashsale.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepositoryDao productRepositoryDao;
    private final ProductMapper productMapper;

    public Product createProduct(Product product) {
        ProductEntity productEntity = productMapper.mapProduct(product);
        return productMapper.mapProductEntity(productRepositoryDao.save(productEntity));
    }

    public List<Product> getProducts(){
        List<ProductEntity> productEntities = productRepositoryDao.findAll();
        return productEntities.stream()
                .map(productMapper::mapProductEntity)
                .toList();
    }

    public Product updateProductStock(Long productId,Integer quantity){
        ProductEntity productEntity = productRepositoryDao.findByProductId(productId).orElse(null);
        if(productEntity!=null) productEntity.setStockQuantity(quantity);
        return productMapper.mapProductEntity(productRepositoryDao.save(productEntity));
    }

    public void deleteProduct(Long productId){
        productRepositoryDao.deleteByProductId(productId);
    }
}
