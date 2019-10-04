package com.store.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Entity(name = "pedido")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonProperty("id")
    private Integer orderId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_customer", nullable = false)
    //@JsonProperty("cliente")
    private Customer customer;

    @Column(name = "valor", precision = 10, scale = 2)
    //@JsonProperty("valor")
    private BigDecimal value;

    @JsonManagedReference
    @OneToMany(targetEntity = OrderItem.class, mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    //@JsonProperty("listaItensPedidos")
    private List<OrderItem> orderItemList;

}
