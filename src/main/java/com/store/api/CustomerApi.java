package com.store.api;

import com.store.domain.Customer;
import com.store.resource.CustomerResource;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@Api(value = "customer")
@RequestMapping(value = "/customers")
@ExposesResourceFor(Customer.class)
public interface CustomerApi {

    @ApiOperation(value = "Add a new customer", nickname = "addCustomer", authorizations = {@Authorization(value = "bearerAuth")}, tags = {"customer",})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Access token is missing or invalid"),
            @ApiResponse(code = 405, message = "Invalid input")
    })
    @PostMapping
    ResponseEntity<CustomerResource> addCustomer(@ApiParam(value = "Customer that will be added", required = true) @Valid @RequestBody Customer customer);

    @ApiOperation(value = "Get customer by id", nickname = "getCustomerById", response = Customer.class,
            notes = "Retrieves a customer by id",
            authorizations = {@Authorization(value = "bearerAuth")}, tags = {"customer",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Customer.class),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @GetMapping(value = "/{customerId}")
    ResponseEntity<CustomerResource> getCustomerById(@ApiParam(value = "Customer id") @Valid @PathVariable Integer customerId);

    @ApiOperation(value = "Get customers", nickname = "getCustomer", response = Customer.class, responseContainer = "List",
            notes = "Retrieves a collection of users by taking into account customer features. Can be done pagination (quantity of items returned) and ordering",
            authorizations = {@Authorization(value = "bearerAuth")}, tags = {"customer",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Customer.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid status value")
    })
    @GetMapping
    ResponseEntity<PagedResources<CustomerResource>> getCustomer(@ApiParam(value = "Customer id") @Valid @RequestParam(value = "id", required = false) Integer customerId,
                                                                 @ApiParam(value = "Customer name") @Valid @RequestParam(value = "name", required = false) String name,
                                                                 @ApiParam(value = "Customer cpf") @Valid @RequestParam(value = "cpf", required = false) String cpf,
                                                                 @ApiParam(value = "Customer birth date") @Valid @RequestParam(value = "birth-date", required = false) LocalDate birthDate,
                                                                 @PageableDefault Pageable pageable,
                                                                 PagedResourcesAssembler assembler);

    @ApiOperation(value = "Update an existing customer", nickname = "updateCustomerById", authorizations = {@Authorization(value = "bearerAuth")}, tags = {"customer",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid customer id supplied"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid"),
            @ApiResponse(code = 404, message = "Customer not found"),
            @ApiResponse(code = 405, message = "Validation exception")
    })
    @PutMapping(value = "/{customerId}")
    ResponseEntity<CustomerResource> updateCustomerById(@ApiParam(value = "Customer id that will be updated", required = true) @Valid @PathVariable Integer customerId,
                                                        @ApiParam(value = "Data that will be updated in customer", required = true) @Valid @RequestBody Customer customer);


    @ApiOperation(value = "Delete customer", nickname = "deleteCustomerById", authorizations = {@Authorization(value = "bearerAuth")}, tags = {"customer",})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid customer id supplied"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid"),
            @ApiResponse(code = 404, message = "Customer not found")
    })
    @DeleteMapping(value = "/{customerId}")
    ResponseEntity<Void> deleteCustomerById(@ApiParam(value = "Customer id that will be deleted", required = true) @Valid @PathVariable Integer customerId);

}
