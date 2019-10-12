package com.store.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@JsonDeserialize(builder = Product.ProductBuilder.class)
@Builder(builderClassName = "ProductBuilder", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "produto")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome", length = 100)
    private String name;

    @Column(name = "preco_sugerido", precision = 10, scale = 2)
    private BigDecimal suggestedPrice;

    @ToString.Exclude
    @JsonManagedReference(value = "product")
    @OneToMany(targetEntity = OrderItem.class, mappedBy = "product", fetch = FetchType.LAZY)
    private List<OrderItem> orderItemList;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_produto")
    private ProductStatus productStatus;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ProductBuilder {
    }

}