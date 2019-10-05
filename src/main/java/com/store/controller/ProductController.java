package com.store.controller;

import com.store.api.ProductApi;
import com.store.domain.Product;
import com.store.resource.ProductResource;
import com.store.service.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
public class ProductController implements ProductApi {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ResponseEntity<ProductResource> addProduct(Product product) {
        Product persistedProduct = productService.save(product);
        return new ResponseEntity<>(new ProductResource(persistedProduct), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ProductResource> getProductById(@PathVariable Integer productId) {
        Optional<Product> productOptional = productService.findProduct(productId);
        return getResponseEntity(productOptional, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PagedResources<Product>> getProduct(Integer productId, String nome, BigDecimal suggestedPrice, Pageable pageable, PagedResourcesAssembler assembler) {
        Product productExample = Product.builder().productId(productId).name(nome).suggestedPrice(suggestedPrice).build();
        Page<Product> productPage = productService.findProductByExample(productExample, pageable);
        return new ResponseEntity<PagedResources<Product>>(assembler.toResource(productPage), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ProductResource> updateProductById(Integer productId, Product product) {
        Optional<Product> productOptional = productService.updateById(productId, product);
        return getResponseEntity(productOptional, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteProductById(Integer productId) {
        productService.deleteById(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private ResponseEntity<ProductResource> getResponseEntity(Optional<Product> productOptional, HttpStatus httpStatus) {
        return productOptional.map(product -> new ResponseEntity<>(new ProductResource(product), httpStatus)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
