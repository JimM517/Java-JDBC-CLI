package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Sale;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class JdbcSaleDao implements SaleDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSaleDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Sale getSale(int saleId) {
        return null;
    }

    @Override
    public List<Sale> getSalesUnshipped() {
        return null;
    }

    @Override
    public List<Sale> getSalesByCustomerId(int customerId) {
        return null;
    }

    @Override
    public List<Sale> getSalesByProductId(int productId) {
        return null;
    }

    @Override
    public Sale createSale(Sale newSale) {
        return null;
    }

    @Override
    public void updateSale(Sale updatedSale) {

    }

    @Override
    public void deleteSale(int saleId) {

    }


}
