package com.store;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.format.DateTimeFormatter;

@RunWith(SpringRunner.class)
public class AbstractTest {

    protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    protected static final Integer customerId = 1;
    protected static final Integer orderId = 1;
    protected static final Integer orderItemId = 1;
    protected static final Integer productId = 1;

    protected static final String basePath = "http://localhost";
    protected static final String customerPath = basePath + "/customers";
    protected static final String orderPath = customerPath + "/" + customerId + "/orders";
    protected static final String orderItemPath = orderPath + "/" + orderId + "/order-items";
    protected static final String productPath = basePath + "/products";

}
