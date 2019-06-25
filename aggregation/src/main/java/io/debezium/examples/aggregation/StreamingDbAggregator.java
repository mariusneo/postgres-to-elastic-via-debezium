package io.debezium.examples.aggregation;

import aggregation.orders.Customer;
import aggregation.orders.Order;
import aggregation.orders.Product;
import dbserver1.inventory.orders.Key;
import dbserver1.inventory.orders.Value;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.debezium.examples.aggregation.db.DBCPDataSource;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class StreamingDbAggregator {

	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("usage: java -jar <package> "
					+ StreamingDbAggregator.class.getName()
					+ " <parent_topic> <aggregation_topic> <bootstrap_servers>");
			System.exit(-1);
		}

		final String parentTopic = args[0];
		final String aggregationTopic = args[1];
		final String bootstrapServers = args[2];

		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streaming-db-aggregator");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10 * 1024);
		props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
		props.put(CommonClientConfigs.METADATA_MAX_AGE_CONFIG, 500);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


		// Where to find the Confluent schema registry instance(s)
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
		// Specify default (de)serializers for record keys and for record values.
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);

		StreamsBuilder builder = new StreamsBuilder();

		//1) read parent topic i.e. orders as ktable
		KTable<Key, Value> ordersTable = builder.table(parentTopic);

		ordersTable.toStream().print(Printed.toSysOut());

		//2) map the read organisation entity to the aggregated order dto
		KTable<Key, Order> aggregatedOrdersTable = ordersTable
				.mapValues(StreamingDbAggregator::createOrderDto);


		final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
				"http://localhost:8081");

		final Serde<Key> keyAvroSerde = new SpecificAvroSerde<>();
		keyAvroSerde.configure(serdeConfig, true); // `true` for record keys
		final Serde<Order> aggregatedOrderAvroSerde = new SpecificAvroSerde<>();
		aggregatedOrderAvroSerde.configure(serdeConfig, false); // `false` for record values


		aggregatedOrdersTable.toStream().to(aggregationTopic,
				Produced.with(keyAvroSerde, aggregatedOrderAvroSerde));
		aggregatedOrdersTable.toStream().print(Printed.toSysOut());

		final KafkaStreams streams = new KafkaStreams(builder.build(), props);

		// Delete the application's local state.
		// Note: In real application you'd call `cleanUp()` only under certain conditions.
		// See Confluent Docs for more details:
		// https://docs.confluent.io/current/streams/developer-guide/app-reset-tool.html#step-2-reset-the-local-environments-of-your-application-instances
		//streams.cleanUp();

		streams.start();

		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
	}


	private static Order createOrderDto(Value orderTuple) {
		Product productDto = getProduct(orderTuple.getProductId());
		Customer customerDto = getCustomer(orderTuple.getPurchaser());
		return new Order(orderTuple.getId(),
				orderTuple.getOrderDate(),
				customerDto,
				orderTuple.getQuantity(),
				productDto);
	}


	private static Product getProduct(Integer productId) {
		if (productId == null) {
			return null;
		}
		String sql = "SELECT id, name, description, weight from products where id = ?";
		try (Connection connection = DBCPDataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql);) {
			pstmt.setInt(1, productId);
			try (ResultSet resultSet = pstmt.executeQuery();) {
				if (resultSet.next()) {
					return new Product(
							resultSet.getInt(1),
							resultSet.getString(2),
							resultSet.getString(3),
							resultSet.getFloat(4)
					);
				}
			}
		}
		catch (SQLException e) {
			throw new RuntimeException("SQL Exception occurred", e);
		}

		return null;
	}


	private static Customer getCustomer(Integer customerId) {
		if (customerId == null) {
			return null;
		}
		String sql = "SELECT id, first_name, last_name, email from customers where id = ?";
		try (Connection connection = DBCPDataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql);) {
			pstmt.setInt(1, customerId);
			try (ResultSet resultSet = pstmt.executeQuery();) {
				if (resultSet.next()) {
					return new Customer(
							resultSet.getInt(1),
							resultSet.getString(2),
							resultSet.getString(3),
							resultSet.getString(4)
					);
				}
			}
		}
		catch (SQLException e) {
			throw new RuntimeException("SQL Exception occurred", e);
		}

		return null;
	}
}