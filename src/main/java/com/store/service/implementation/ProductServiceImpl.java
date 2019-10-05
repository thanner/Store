package com.store.service.implementation;

import com.store.domain.Product;
import com.store.domain.ProductStatus;
import com.store.repository.ProductRepository;
import com.store.service.interfaces.ProductService;
import com.store.util.NonNullPropertiesCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("productService")
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final NonNullPropertiesCopier copier;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, NonNullPropertiesCopier copier) {
        this.productRepository = productRepository;
        this.copier = copier;
    }

    @Override
    public Product save(Product product) {
        product.setProductStatus(ProductStatus.ACTIVE);
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findProduct(Integer productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Page<Product> findProductByExample(Product product, Pageable pageable) {
        return productRepository.findAll(Example.of(product), pageable);
    }

    @Override
    public Optional<Product> updateById(Integer productId, Product product) {
        Optional<Product> persisted = productRepository.findById(productId);
        persisted.ifPresent(value -> copier.copyNonNullProperties(product, value));
        return persisted;
    }

    @Override
    public void deleteById(Integer productId) {
        Product product = productRepository.getOne(productId);
        product.setProductStatus(ProductStatus.INACTIVE);
    }

}