package com.fernando.springcloud.msvc.items.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.fernando.libs.msvc.commons.entities.Product;
import com.fernando.springcloud.msvc.items.models.Item;
import com.fernando.springcloud.msvc.items.services.ItemService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RefreshScope
@RestController
public class ItemController {

    private final ItemService service;

    @SuppressWarnings("rawtypes")
    private final CircuitBreakerFactory cBreakerFactory;
    
    private final Logger logger= LoggerFactory.getLogger(ItemController.class);

    @Value("${configuracion.texto}")
    private String text;

    @Autowired
    private Environment env;

    @SuppressWarnings("rawtypes")
    public ItemController(@Qualifier("itemServiceWebClient") ItemService service, CircuitBreakerFactory cBreakerFactory) {
        this.service = service;
        this.cBreakerFactory = cBreakerFactory;
    }

    @GetMapping("/fetch-configs")
    public ResponseEntity<?> fetchConfigs(@Value ("${server.port}") String port) {
        Map<String, String> json = new HashMap<>();
        json.put("text", text);
        json.put("port", port);
        logger.info(port);
        logger.info(text);

        if(env.getActiveProfiles().length>0 && env.getActiveProfiles()[0].equals("dev")){
            json.put("autor.nombre", env.getProperty("configuracion.autor.nombre"));
            json.put("autor.email", env.getProperty("configuracion.autor.email"));
        }

        return ResponseEntity.ok(json);
    }
    
    @GetMapping
    public List<Item> list(@RequestParam(name = "name", required = false) String name, 
        @RequestHeader(name = "token-request", required = false) String token) {
            logger.info("Llamada a metodo controller de ItemController::list()");
            logger.info("request Parameter: {}", name);
            logger.info("Token: {}", token);
        return service.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable Long id) {
        Optional<Item> itemOptional = cBreakerFactory.create("items").run(()-> service.findById(id), e ->{
            System.out.println(e.getMessage());
            logger.error(e.getMessage());

            Product product = new Product();
            product.setCreateAt(LocalDate.now());
            product.setId(1L);
            product.setName("Camara Sony");
            product.setPrice(500.00);

            return Optional.of(new Item(product, 5));
        });

        if(itemOptional.isPresent()){
            return ResponseEntity.ok(itemOptional.get());
        }
        
        return ResponseEntity.status(404)
            .body(Collections.singletonMap("message", "No existe el producto en el microservicio msvc-produtcs"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product) {
        logger.info("Product creando: {}", product);
        return service.save(product);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Product update(@RequestBody Product product, @PathVariable Long id) {
        logger.info("Product actualizando: {}", product);
        return service.update(product, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete (@PathVariable Long id) {
        logger.info("Product eliminado con id: {}", id);
        service.delete(id);
    }
    

    @CircuitBreaker(name = "items", fallbackMethod = "getFallBackMethodProduct")
    @GetMapping("/details/{id}")
    public ResponseEntity<?> details2(@PathVariable Long id) {
        Optional<Item> itemOptional = service.findById(id);

        if(itemOptional.isPresent()){
            return ResponseEntity.ok(itemOptional.get());
        }
        
        return ResponseEntity.status(404)
            .body(Collections.singletonMap("message", "No existe el producto en el microservicio msvc-produtcs"));
    }

    public ResponseEntity<?> getFallBackMethodProduct(Throwable e){
        System.out.println(e.getMessage());
        logger.error(e.getMessage());

        Product product = new Product();
        product.setCreateAt(LocalDate.now());
        product.setId(1L);
        product.setName("Camara Sony");
        product.setPrice(500.00);

        return ResponseEntity.ok(new Item(product, 5));
    }

    @CircuitBreaker(name = "items", fallbackMethod = "getFallBackMethodProduct2")
    @TimeLimiter(name = "items")
    @GetMapping("/details2/{id}")
    public CompletableFuture<?> details3(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Item> itemOptional = service.findById(id);

            if(itemOptional.isPresent()){
                return ResponseEntity.ok(itemOptional.get());
            }
            
            return ResponseEntity.status(404)
                .body(Collections.singletonMap("message", "No existe el producto en el microservicio msvc-produtcs"));
        });
    }

    public CompletableFuture<?> getFallBackMethodProduct2(Throwable e){
        return CompletableFuture.supplyAsync(() -> {
            System.out.println(e.getMessage());
            logger.error(e.getMessage());

            Product product = new Product();
            product.setCreateAt(LocalDate.now());
            product.setId(1L);
            product.setName("Camara Sony");
            product.setPrice(500.00);

            return ResponseEntity.ok(new Item(product, 5));
        });
    }
}
