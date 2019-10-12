package com.store.service;

import com.store.AbstractTest;
import com.store.domain.Product;
import com.store.domain.ProductStatus;
import com.store.exception.ResourceNotFoundException;
import com.store.repository.ProductRepository;
import com.store.util.NonNullPropertiesCopier;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(value = ProductService.class, secure = false)
public class ProductServiceTest extends AbstractTest {

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private NonNullPropertiesCopier copier;

    private Product product;

    private Integer wrongProductId;

    @Before
    public void setup() {
        super.setup();
        product = setupProduct();
        wrongProductId = productId + 1;
        given(productRepository.findById(productId)).willReturn(Optional.ofNullable(product));
    }

    @Test
    public void whenSaveProductReturnActiveProduct_thenAssertionSucceeds() {
        Product inactiveProduct = setupProduct();
        inactiveProduct.setProductStatus(ProductStatus.INACTIVE);
        given(productRepository.save(inactiveProduct)).willReturn(inactiveProduct);

        Product persisted = productService.save(inactiveProduct);
        verifyProduct(persisted);
    }

    @Test
    public void whenFindProduct_thenAssertionSucceeds() {
        Product persisted = productService.findProduct(productId);
        verifyProduct(persisted);
    }

    @Test
    public void whenFindProductByExample_thenAssertionSucceeds() {
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        Page<Product> pageProduct = new PageImpl<>(productList);
        given(productRepository.findAll(Example.of(product), Pageable.unpaged())).willReturn(pageProduct);

        Page<Product> persisted = productService.findProductByExample(product, Pageable.unpaged());
        verifyProduct(persisted.getContent().get(0));
    }

    @Test
    public void whenUpdateProduct_thenAssertionSucceeds() {
        Product persisted = productService.updateById(productId, product);
        verifyProduct(persisted);
    }

    @Test
    public void whenDeleteProduct_thenAssertionSucceeds() {
        productService.deleteById(productId);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenFindProductNotFound_thenResourceNotFoundException() {
        productService.findProduct(wrongProductId);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenUpdateProductNotFound_thenResourceNotFoundException() {
        productService.updateById(wrongProductId, product);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenDeleteProductNotFound_thenResourceNotFoundException() {
        productService.deleteById(wrongProductId);
    }

    private void verifyProduct(Product productToVerify) {
        assertThat(productToVerify)
                .hasFieldOrPropertyWithValue("productId", product.getId())
                .hasFieldOrPropertyWithValue("name", product.getName())
                .hasFieldOrPropertyWithValue("suggestedPrice", product.getSuggestedPrice())
                .hasFieldOrPropertyWithValue("productStatus", product.getProductStatus())
        ;
    }

}
