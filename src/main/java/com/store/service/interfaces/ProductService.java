package com.store.service.interfaces;

import com.store.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {

    void save(Product product);

    Optional<Product> findProduct(Integer productId);

    Page<Product> findProductByExample(Product product, Pageable pageable);

    Optional<Product> updateById(Integer productId, Product product);

    void deleteById(Integer productId);

}
