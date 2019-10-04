package com.store.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "produto")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonProperty("id")
    private Integer productId;

    @Column(name = "nome", length = 100)
    //@JsonProperty("nome")
    private String name;

    @Column(name = "preco_sugerido", precision = 10, scale = 2)
    //@JsonProperty("precoSugerido")
    private BigDecimal suggestedPrice;

    @JsonBackReference
    @OneToMany(targetEntity = OrderItem.class, mappedBy = "product", fetch = FetchType.LAZY)
    //@JsonProperty("listaItensPedidos")
    private List<OrderItem> orderItemList;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_produto")
    //@JsonProperty("statusProduto")
    private ProductStatus productStatus;

}
