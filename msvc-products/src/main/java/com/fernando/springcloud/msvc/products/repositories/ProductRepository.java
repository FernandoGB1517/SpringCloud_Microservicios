package com.fernando.springcloud.msvc.products.repositories;

import org.springframework.data.repository.CrudRepository;

import com.fernando.libs.msvc.commons.entities.Product;


public interface ProductRepository extends CrudRepository<Product, Long>{
    
}
