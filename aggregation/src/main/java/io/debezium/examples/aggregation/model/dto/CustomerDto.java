package io.debezium.examples.aggregation.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerDto {
	private final Integer id;
	private final String first_name;
	private final String last_name;
	private final String email;

	@JsonCreator
	public CustomerDto(
			@JsonProperty("id") Integer id,
			@JsonProperty("first_name") String first_name,
			@JsonProperty("last_name") String last_name,
			@JsonProperty("email") String email) {
		this.id = id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		return "Customer{" +
				"id=" + id +
				", first_name='" + first_name + '\'' +
				", last_name='" + last_name + '\'' +
				", email='" + email + '\'' +
				'}';
	}
}
