package com.orchestrate.flashsale.controller;

import com.orchestrate.flashsale.models.Product;
import com.orchestrate.flashsale.service.ProductService;
import jakarta.persistence.GeneratedValue;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody Product product){
        Product savedProduct = productService.createProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    @GetMapping
    public ResponseEntity<?> getProducts(){
        return ResponseEntity.ok(productService.getProducts());
    }
}
