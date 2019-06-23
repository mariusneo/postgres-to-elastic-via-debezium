package io.debezium.examples.aggregation.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductDto {
	private final Integer id;
	private final String name;
	private final String description;
	private final Float weight;

	public ProductDto(@JsonProperty("id") Integer id,
			@JsonProperty("name") String name,
			@JsonProperty("description") String description,
			@JsonProperty("weight") Float weight) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.weight = weight;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Float getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return "Product{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", weight=" + weight +
				'}';
	}
}
