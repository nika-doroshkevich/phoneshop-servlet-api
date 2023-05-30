package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class Order extends Cart {

    private Long id;
    private BigDecimal subtotal;
    private BigDecimal deliveryCost;

    private String firstName;
    private String lastName;
    private String phone;

    private LocalDate deliveryDate;
    private String deliveryAddress;

    private PaymentMethod paymentMethod;
}
