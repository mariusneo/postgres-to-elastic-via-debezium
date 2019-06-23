package io.debezium.examples.aggregation.db;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp.BasicDataSource;

public class DBCPDataSource {

	private static BasicDataSource ds = new BasicDataSource();

	static {
		ds.setUrl("jdbc:postgresql://localhost/postgres?currentSchema=inventory");
		ds.setUsername("postgres");
		ds.setPassword("postgres");
		ds.setMinIdle(5);
		ds.setMaxIdle(10);
		ds.setMaxOpenPreparedStatements(100);
	}

	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	private DBCPDataSource(){ }
}