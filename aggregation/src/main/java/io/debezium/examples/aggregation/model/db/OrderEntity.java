package io.debezium.examples.aggregation.model.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.Date;

public class OrderEntity {

	private final EventType _eventType;

	private final Integer id;
	private final Integer order_date;
	private final Integer purchaser;
	private final Integer quantity;
	private final Integer product_id;

	@JsonCreator
	public OrderEntity(
			@JsonProperty("_eventType") EventType _eventType,
			@JsonProperty("id") Integer id,
			@JsonProperty("order_date") Integer order_date,
			@JsonProperty("purchaser") Integer purchaser,
			@JsonProperty("quantity") Integer quantity,
			@JsonProperty("product_id") Integer product_id) {
		this._eventType = _eventType == null ? EventType.UPSERT : _eventType;

		this.id = id;
		this.order_date = order_date;
		this.purchaser = purchaser;
		this.quantity = quantity;
		this.product_id = product_id;
	}

	public EventType get_eventType() {
		return _eventType;
	}

	public Integer getId() {
		return id;
	}

	public Integer getOrder_date() {
		return order_date;
	}

	public Integer getPurchaser() {
		return purchaser;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	@Override
	public String toString() {
		return "OrderEntity{" +
				"_eventType=" + _eventType +
				", id=" + id +
				", order_date=" + order_date +
				", purchaser=" + purchaser +
				", quantity=" + quantity +
				", product_id=" + product_id +
				'}';
	}
}
