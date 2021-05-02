package com.github.marschall.jdbcdatedemo;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Sql("classpath:schema.sql")
@SpringJUnitConfig
@ContextConfiguration(classes = TestConfiguration.class)
abstract class AbstractJdbcTestCase {

  @Autowired
  private DataSource dataSource;

  @Test
  void testConversion() throws SQLException {
    // 2016-03-27 02:00 -> 2016-03-27 03:00
    // UTC offset +1 -> +2
    try (Connection connection = this.dataSource.getConnection()) {

      BigDecimal id = BigDecimal.ONE;

      try (PreparedStatement insert = connection.prepareStatement(
              "INSERT INTO date_demo_table (id, timestamp_column) VALUES (?, TIMESTAMP '2016-03-27 02:15:00')")) {
        insert.setBigDecimal(1, id);
        insert.executeUpdate();
      }

      try (PreparedStatement select = connection.prepareStatement(
              "SELECT timestamp_column FROM date_demo_table WHERE id = ?")) {
        select.setBigDecimal(1, id);

        int count = 0;
        Timestamp selectedTimestamp = null;
        try (ResultSet result = select.executeQuery()) {
          while (result.next()) {
            selectedTimestamp = result.getTimestamp(1);
            count += 1;
          }
        }
        assertEquals(1, count);
        assertNotNull(selectedTimestamp);
        assertEquals(LocalDateTime.of(2016, 3, 27, 2, 15), selectedTimestamp.toLocalDateTime());
      }
    }
  }

  @Test
  void testDirectDataTypes() throws SQLException {
    // 2016-03-27 02:00 -> 2016-03-27 03:00
    // UTC offset +1 -> +2
    try (Connection connection = this.dataSource.getConnection()) {

      BigDecimal id = BigDecimal.ONE;

      try (PreparedStatement insert = connection.prepareStatement(
              "INSERT INTO date_demo_table (id, timestamp_column) VALUES (?, TIMESTAMP '2016-03-27 02:15:00')")) {
        insert.setBigDecimal(1, id);
        insert.executeUpdate();
      }

      try (PreparedStatement select = connection.prepareStatement(
              "SELECT timestamp_column FROM date_demo_table WHERE id = ?")) {
        select.setBigDecimal(1, id);

        int count = 0;
        LocalDateTime selectedTimestamp = null;
        try (ResultSet result = select.executeQuery()) {
          while (result.next()) {
            selectedTimestamp = result.getObject(1, LocalDateTime.class);
            count += 1;
          }
        }
        assertEquals(1, count);
        assertNotNull(selectedTimestamp);
        assertEquals(LocalDateTime.of(2016, 3, 27, 2, 15), selectedTimestamp);
      }
    }
  }

  @Test
  void testOldType() throws SQLException {
    // 2016-10-30 03:00 -> 2016-10-30 02:00
    // UTC offset +2 -> +1
    // make sure we have the second 2:55, the one in winter time
    ZonedDateTime zonedDateTime = ZonedDateTime.parse("2016-10-30T02:55+01:00[Europe/Paris]");
    Timestamp insertedTimestamp = Timestamp.from(zonedDateTime.toInstant());

    try (Connection connection = this.dataSource.getConnection()) {

      BigDecimal id = BigDecimal.ONE;
      Timestamp selectedTimestamp = null;

      try (PreparedStatement insert = connection.prepareStatement(
              "INSERT INTO date_demo_table (id, timestamp_column) VALUES (?, ?)")) {
        insert.setBigDecimal(1, id);
        insert.setTimestamp(2, insertedTimestamp);
        insert.executeUpdate();
      }

      try (PreparedStatement select = connection.prepareStatement(
              "SELECT timestamp_column FROM date_demo_table WHERE id = ?")) {
        select.setBigDecimal(1, id);

        int count = 0;
        try (ResultSet result = select.executeQuery()) {
          while (result.next()) {
            selectedTimestamp = result.getTimestamp(1);
            count += 1;
          }
        }
        assertEquals(1, count);
        assertNotNull(selectedTimestamp);
        assertEquals(insertedTimestamp.getTime(), selectedTimestamp.getTime());
      }
    }

  }

  @Test
  void testUtc() {
    ZonedDateTime cest = ZonedDateTime.parse("2016-03-27T04:15:00+02:00[Europe/Paris]");
    ZonedDateTime utc = cest.withZoneSameInstant(ZoneOffset.UTC);
    assertEquals(LocalDateTime.of(2016, 3, 27, 2, 15), utc.toLocalDateTime());
  }

}
