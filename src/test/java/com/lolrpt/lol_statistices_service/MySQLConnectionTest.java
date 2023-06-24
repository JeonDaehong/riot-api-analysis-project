package com.lolrpt.lol_statistices_service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application.properties")
public class MySQLConnectionTest {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MySQLConnectionTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void databaseConnectionTest() {
        try {
            String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
            assertEquals("1", result);
        } catch ( Exception e ) {
            fail("Database Connect Fail: " + e.getMessage());
        }
    }

}
