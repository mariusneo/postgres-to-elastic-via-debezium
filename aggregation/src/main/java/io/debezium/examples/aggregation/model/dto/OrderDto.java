package io.debezium.examples.aggregation.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class OrderDto {
	private final Integer id;
	private final Date order_date;
	private final CustomerDto customer;
	private final Integer quantity;
	private final ProductDto product;

	@JsonCreator
	public OrderDto(
			@JsonProperty("id") Integer id,
			@JsonProperty("order_date")  Date order_date,
			@JsonProperty("customer") CustomerDto customer,
			@JsonProperty("quantity") Integer quantity,
			@JsonProperty("product") ProductDto product) {
		this.id = id;
		this.order_date = order_date;
		this.customer = customer;
		this.quantity = quantity;
		this.product = product;
	}

	public Integer getId() {
		return id;
	}

	public Date getOrder_date() {
		return order_date;
	}

	public CustomerDto getCustomer() {
		return customer;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public ProductDto getProduct() {
		return product;
	}

	@Override
	public String toString() {
		return "Order{" +
				"id=" + id +
				", order_date=" + order_date +
				", customer=" + customer +
				", quantity=" + quantity +
				", product=" + product +
				'}';
	}
}
