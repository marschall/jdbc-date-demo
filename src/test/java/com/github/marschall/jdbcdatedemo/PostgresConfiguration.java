package com.github.marschall.jdbcdatedemo;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

@Configuration
public class PostgresConfiguration {

  @Bean
  public DataSource dataSource() {
    SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
    dataSource.setSuppressClose(true);
    String userName = System.getProperty("user.name");
    // defaults from Postgres.app
    dataSource.setUrl("jdbc:postgresql:" + userName);
    dataSource.setUsername(userName);
    dataSource.setPassword("");
    return dataSource;
  }

}
