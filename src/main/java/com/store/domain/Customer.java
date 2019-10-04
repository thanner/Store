package com.store.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "cliente")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonProperty("id")
    private Integer customerId;

    @Column(name = "nome", length = 100)
    //@JsonProperty("nome")
    private String name;

    @Column(unique = true, columnDefinition = "CHAR(11)")
    private String cpf;

    @Column(name = "data_nascimento")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    //@JsonProperty("dataNascimento")
    private LocalDate birthDate;

    @JsonManagedReference
    @OneToMany(targetEntity = Order.class, mappedBy = "customer", fetch = FetchType.LAZY)
    //@JsonProperty("listaPedidos")
    private List<Order> orderList;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_conta")
    //@JsonProperty("statusConta")
    private AccountStatus accountStatus;

}
