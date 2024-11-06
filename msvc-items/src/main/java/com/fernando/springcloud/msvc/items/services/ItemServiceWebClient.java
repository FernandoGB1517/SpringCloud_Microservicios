package com.fernando.springcloud.msvc.items.services;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

// import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
// import org.springframework.web.reactive.function.client.WebClient.Builder;
// import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fernando.libs.msvc.commons.entities.Product;
import com.fernando.springcloud.msvc.items.models.Item;


// @Primary
@Service
public class ItemServiceWebClient implements ItemService {

    // private final WebClient.Builder client;
    private final WebClient client;

    public ItemServiceWebClient(WebClient client) {
        this.client = client;
    }

    @Override
    public List<Item> findAll() {
        return this.client
            .get()
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(Product.class)
                .map(product -> new Item(product, new Random().nextInt(10) + 1))
            .collectList()
                .block();
    }

    @Override
    public Optional<Item> findById(Long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);
        // try {
            return Optional.of(client
                .get()
                .uri("/{id}", params)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Product.class)
                    .map(product -> new Item(product, new Random().nextInt(10) + 1))
                    .block());
        // } catch (WebClientResponseException e) {
        //     return Optional.empty();
        // }

    }

    @Override
    public Product save(Product product) {
        return client
            .post()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(product)
            .retrieve()
            .bodyToMono(Product.class)
                .block();
    }

    @Override
    public Product update(Product product, Long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);

        return client
        .put()
        .uri("/{id}", params)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(product)
        .retrieve()
        .bodyToMono(Product.class)
            .block();

    }

    @Override
    public void delete(Long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);

        client
            .delete()
            .uri("/{id}", params)
            .retrieve()
            .bodyToMono(Void.class)
                .block();
    }

}
