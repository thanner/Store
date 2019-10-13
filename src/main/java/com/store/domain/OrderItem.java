package com.store.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@JsonDeserialize(builder = OrderItem.OrderItemBuilder.class)
@Builder(builderClassName = "OrderItemBuilder", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "item_pedido")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @JsonBackReference(value = "order")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Order order;

    //@JsonBackReference(value = "product")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_produto", nullable = false)
    private Product product;

    @Column(name = "quantidade", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "preco", precision = 10, scale = 2)
    private BigDecimal price;

    @JsonPOJOBuilder(withPrefix = "")
    public static class OrderItemBuilder {
    }

}
