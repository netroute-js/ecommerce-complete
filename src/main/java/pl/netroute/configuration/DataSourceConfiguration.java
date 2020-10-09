package pl.netroute.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    @Bean(destroyMethod = "close")
    @ConfigurationProperties("ecommerce.database")
    HikariDataSource ecommerceDatasource() {
        return DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    JdbcTemplate ecommerceJdbcTemplate(DataSource ecommerceDatasource) {
        return new JdbcTemplate(ecommerceDatasource);
    }

}
