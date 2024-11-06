package com.fernando.springcloud.msvc.items.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fernando.libs.msvc.commons.entities.Product;


@FeignClient(name = "msvc-products")
public interface ProductFeignClient {

    @GetMapping
    List<Product> findAll();

    @GetMapping("/{id}")
    Product details(@PathVariable Long id);

    @PostMapping
    public Product create(@RequestBody Product Product);

    @PutMapping("/{id}")
    public Product update(@RequestBody Product product, @PathVariable Long id);

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id);

}
