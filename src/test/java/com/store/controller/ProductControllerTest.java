package com.store.controller;

import com.store.AbstractTest;
import com.store.domain.Product;
import com.store.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ProductController.class, secure = false)
public class ProductControllerTest extends AbstractTest {

    @MockBean
    private ProductService productService;

    private Product product;

    @Before
    public void setup() {
        super.setup();
        product = setupProduct();
    }

    @Test
    public void getProductByIdShouldReturnProduct() throws Exception {
        given(productService.findProduct(any(Integer.class))).willReturn(product);
        final ResultActions result = mockMvc.perform(get(productPath + "/" + productId));
        result.andExpect(status().isOk());
        verifyJsonProductById(result);
    }

    @Test
    public void getProductsShouldReturnProductPaged() throws Exception {
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        Page<Product> pageProduct = new PageImpl<>(productList);
        given(productService.findProductByExample(any(Product.class), any(Pageable.class))).willReturn(pageProduct);
        final ResultActions result = mockMvc.perform(get(productPath + "?id=" + product.getId() + "&name=" + product.getName()));
        result.andExpect(status().isOk());
        verifyJsonProductPaged(result);
    }

    @Test
    public void postReturnsCorrectResponse() throws Exception {
        given(productService.save(any(Product.class))).willReturn(product);
        final ResultActions result =
                mockMvc.perform(post(productPath)
                        .content(mapper.writeValueAsBytes(product))
                        .contentType(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isCreated());
        verifyJsonProductById(result);
    }

    @Test
    public void putReturnsCorrectResponse() throws Exception {
        given(productService.updateById(eq(productId), any(Product.class))).willReturn(product);
        final ResultActions result = mockMvc.perform(put(productPath + "/" + productId)
                .content(mapper.writeValueAsBytes(product))
                .contentType(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isOk());
        verifyJsonProductById(result);
    }

    @Test
    public void deleteReturnsCorrectResponse() throws Exception {
        mockMvc.perform(delete(productPath + "/" + productId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(StringUtils.EMPTY));
    }

    private void verifyJsonProductById(final ResultActions result) throws Exception {
        verifyJsonProduct(result, "product");
        result
                .andExpect(jsonPath("_links.products.href", is(productPath)))
                .andExpect(jsonPath("_links.self.href", is(productPath + "/" + productId)));
    }

    private void verifyJsonProductPaged(final ResultActions result) throws Exception {
        verifyJsonProduct(result, "_embedded.productList[0]");
        result.andExpect(jsonPath("_links.self.href", is(productPath)));
    }

    private void verifyJsonProduct(final ResultActions result, String productPath) throws Exception {
        result
                .andExpect(jsonPath(productPath + ".id", is(product.getId())))
                .andExpect(jsonPath(productPath + ".name", is(product.getName())))
                .andExpect(jsonPath(productPath + ".suggestedPrice", is(product.getSuggestedPrice().doubleValue())))
                .andExpect(jsonPath(productPath + ".productStatus", is(product.getProductStatus().toString())));
    }

}
