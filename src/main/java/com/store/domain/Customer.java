package com.store.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@JsonDeserialize(builder = Customer.CustomerBuilder.class)
@Builder(builderClassName = "CustomerBuilder", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "cliente")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome", length = 100)
    private String name;

    @Column(unique = true, columnDefinition = "CHAR(11)")
    private String cpf;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    // Olhar favoritos, duas dependencias pra add
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @Column(name = "data_nascimento")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;

    @ToString.Exclude
    @JsonManagedReference(value = "customer")
    @OneToMany(targetEntity = Order.class, mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Order> orderList;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_conta")
    private AccountStatus accountStatus;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CustomerBuilder {
    }
}
