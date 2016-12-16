package com.jindata.apiserver.service.dao;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Profile("mysql")
@Configuration
public class MysqlDatasource {
    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource ds = new DriverManagerDataSource(url, username, password);
        ds.setDriverClassName("com.");
    }

}
