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
    dataSource.setUrl("jdbc:postgresql:" + userName);
    dataSource.setUsername(userName);

    String password = "Cent-Quick-Space-Bath-8";
    dataSource.setPassword(password);
    return dataSource;
  }

}
