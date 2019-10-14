package com.store.integration;

import com.store.AbstractTest;
import com.store.domain.Product;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.annotation.DirtiesContext.ClassMode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductIntegrationTest extends AbstractTest {

    private Product product;

    @Before
    public void setup() {
        super.setup();
        product = setupProduct();
    }

    @Test
    public void stage1_whenPostProduct_thenAssertionSucceeds() throws Exception {
        final ResultActions result = postResource(productPath, product);
        result.andExpect(status().isCreated());
        verifyJsonProductById(result);
    }

    @Test
    public void stage2_whenGetProduct_thenAssertionSucceeds() throws Exception {
        postResource(productPath, product);

        final ResultActions result = mockMvc.perform(get(productPath + "/" + productId));
        result.andExpect(status().isOk());
        verifyJsonProductById(result);
    }

    @Test
    public void stage3_whenGetProductPaged_thenAssertionSucceeds() throws Exception {
        postResource(productPath, product);

        final ResultActions result = mockMvc.perform(get(productPath + "?id=" + product.getId() + "&name=" + product.getName()));
        result.andExpect(status().isOk());
        verifyJsonProductPaged(result);
    }

    @Test
    public void stage4_whenPutProduct_thenAssertionSucceeds() throws Exception {
        postResource(productPath, product);

        final ResultActions result = mockMvc.perform(put(productPath + "/" + productId)
                .content(mapper.writeValueAsBytes(product))
                .contentType(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isOk());
        verifyJsonProductById(result);
    }

    @Test
    public void stage5_whenDeleteProduct_thenAssertionSucceeds() throws Exception {
        postResource(productPath, product);

        deleteResource(productPath, productId).andExpect(status().isNoContent()).andExpect(content().string(StringUtils.EMPTY));
    }

    private void verifyJsonProductById(final ResultActions result) throws Exception {
        verifyJsonProduct(result, "product");
        result
                .andExpect(jsonPath("_links.products.href", is(productPath)))
                .andExpect(jsonPath("_links.self.href", is(productPath + "/" + productId)));
    }

    private void verifyJsonProductPaged(final ResultActions result) throws Exception {
        verifyJsonProduct(result, "_embedded.productList[0]");
        result.andExpect(jsonPath("_links.self.href", is(productPath + "?page=0&size=10")));
    }

    private void verifyJsonProduct(final ResultActions result, String productPath) throws Exception {
        result
                .andExpect(jsonPath(productPath + ".id", is(product.getId())))
                .andExpect(jsonPath(productPath + ".name", is(product.getName())))
                .andExpect(jsonPath(productPath + ".suggestedPrice", is(product.getSuggestedPrice().doubleValue())))
                .andExpect(jsonPath(productPath + ".productStatus", is(product.getProductStatus().toString())));
    }
}
