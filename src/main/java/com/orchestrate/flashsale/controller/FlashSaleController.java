package com.orchestrate.flashsale.controller;

import com.orchestrate.flashsale.models.Product;
import com.orchestrate.flashsale.service.FlashSaleService;
import com.orchestrate.flashsale.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FlashSaleController {

    private final FlashSaleService flashSaleService;
    private final ProductService productService;

    // POST: Create an Order (The Flash Sale Trigger)
    @PostMapping("/order")
    public ResponseEntity<?> createOrder(@RequestParam(name = "productId") Long productId,
                                         @RequestParam(name = "quantity") Integer quantity){
        return ResponseEntity.ok(flashSaleService.placeOrder(productId,quantity));
    }

    // GET: Fetch All products (to see stock decreasing)
    @GetMapping("/products")
    public ResponseEntity<?> getProducts(){
        return ResponseEntity.ok(productService.getProducts());
    }

    // PUT: Update stock (or restocking)
    @PutMapping("/products/{productId}")
    public ResponseEntity<?> updateProducts(@PathVariable Long productId, @RequestParam(name = "quantity") Integer quantity){
        return ResponseEntity.ok(productService.updateProductStock(productId,quantity));
    }

    // POST: Add new Product
    @PostMapping("/products")
    public ResponseEntity<?> addNewProduct(@RequestBody Product product){
        return ResponseEntity.ok(productService.createProduct(product));
    }

    // Delete: Remove a product
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId){
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product Removed Successfully");
    }
}
