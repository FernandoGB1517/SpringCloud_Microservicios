package com.fernando.springcloud.msvc.products.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.fernando.libs.msvc.commons.entities.Product;
import com.fernando.springcloud.msvc.products.servicies.ProductService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
// @RequestMapping("/api/products")
public class ProductController {

    private final Logger logger= LoggerFactory.getLogger(ProductController.class);

    final private ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestHeader(name = "message-request", required = false) String message) {
        logger.info("Ingresando al metodo del controlador ProductController::list");
        logger.info("message: {}", message);
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detailProduct(@PathVariable Long id) throws InterruptedException {
        logger.info("Ingresando al metodo del controlador ProductController::detailProduct");

        if(id.equals(10L)){
            // throw new IllegalStateException("Producto no encontrado!");
        }
        
        if(id.equals(7L)){
            TimeUnit.SECONDS.sleep(3L);
        }

        Optional<Product> productOptional = service.findById(id);

        if(productOptional.isPresent()){
            return ResponseEntity.ok(productOptional.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }
    
    @PostMapping()
    public ResponseEntity<Product> create(@RequestBody Product product) {
        logger.info("Ingresando al metodo del controlador ProductController::create, creando: {}", product);
        // Product productNew = service.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(/*productNew*/service.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product product) {
        logger.info("Ingresando al metodo del controlador ProductController::update, editando: {}", product);

        Optional<Product> productOptional = service.findById(id);

        if(productOptional.isPresent()){
            Product productDb = productOptional.orElseThrow();
            productDb.setName(product.getName());
            productDb.setPrice(product.getPrice());
            productDb.setCreateAt(product.getCreateAt());

            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(productDb));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Optional<Product> productOptional = service.findById(id);

        if(productOptional.isPresent()){
            this.service.deleteById(id);

            logger.info("Ingresando al metodo del controlador ProductController::delete, eliminado: {}", productOptional.get());
            
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
    
}
