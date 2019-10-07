package com.store;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(value = OrderItemControllerTest.class, secure = false)
public class OrderItemControllerTest {
}
