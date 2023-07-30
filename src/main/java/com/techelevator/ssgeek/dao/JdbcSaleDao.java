package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Sale;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcSaleDao implements SaleDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSaleDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Sale getSale(int saleId) {
        Sale sale = null;
        String sql = "SELECT * FROM sale WHERE sale_id = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, saleId);
        if (row.next()) {
            sale = mapRowToSale(row);
        }
        return sale;
    }

    @Override
    public List<Sale> getSalesUnshipped() {
        List<Sale> results = new ArrayList<>();
        String sql = "SELECT sale.sale_id, sale.customer_id, customer.name, sale.sale_date, sale.ship_date FROM sale " +
                    "JOIN customer ON customer.customer_id = sale.customer_id " +
                    "WHERE sale.ship_date IS NULL " +
                    "ORDER BY sale_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
        while(row.next()) {
            Sale sale = mapRowToSale(row);
            sale.setCustomerName("name");
            results.add(sale);
        }
        return results;
    }

    @Override
    public List<Sale> getSalesByCustomerId(int customerId) {
        List<Sale> results = new ArrayList<>();
        String sql = "SELECT * FROM sale " +
                    "JOIN customer ON customer.customer_id = sale.customer_id " +
                    "WHERE sale.customer_id = ? " +
                    "ORDER BY sale_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, customerId);
        while(row.next()) {
            Sale sale = mapRowToSale(row);
            results.add(sale);
        }
        return results;
    }

    @Override
    public List<Sale> getSalesByProductId(int productId) {
        List<Sale> results = new ArrayList<>();
        String sql = "SELECT * FROM sale " +
                    "JOIN customer ON customer.customer_id = sale.customer_id " +
                    "JOIN line_item ON line_item.sale_id = sale.sale_id " +
                    "JOIN product ON product.product_id = line_item.product_id " +
                    "WHERE product.product_id = ? " +
                    "ORDER BY sale.sale_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, productId);
        while(row.next()) {
            Sale sale = mapRowToSale(row);
            sale.setCustomerName(row.getString("name"));
            results.add(sale);
        }
        return results;
    }

    @Override
    public Sale createSale(Sale newSale) {
        String sql = "INSERT INTO sale (customer_id, sale_date, ship_date) VALUES (?, ?, ?) RETURNING sale_id";
        int newId = jdbcTemplate.queryForObject(sql, int.class, newSale.getCustomerId(), newSale.getSaleDate(), newSale.getShipDate());
        return getSale(newId);
    }

    @Override
    public void updateSale(Sale updatedSale) {
        String sql = "UPDATE sale SET customer_id = ?, sale_date = ?, ship_date = ? WHERE sale_id = ?";
        jdbcTemplate.update(sql, updatedSale.getCustomerId(), updatedSale.getSaleDate(), updatedSale.getShipDate(), updatedSale.getSaleId());
    }

    @Override
    public void deleteSale(int saleId) {
        String lineItemQuery = "DELETE FROM line_item WHERE sale_id = ?";
        jdbcTemplate.update(lineItemQuery, saleId);

        String saleQuery = "DELETE FROM sale WHERE sale_id = ?";
        jdbcTemplate.update(saleQuery, saleId);
    }

    private Sale mapRowToSale(SqlRowSet results) {
        Sale sale = new Sale();
        sale.setSaleId(results.getInt("sale_id"));
        sale.setCustomerId(results.getInt("customer_id"));
        if (results.getDate("sale_date").toLocalDate() != null) {
            sale.setSaleDate(results.getDate("sale_Date").toLocalDate());
        }
        if (results.getDate("ship_date").toLocalDate() != null) {
            sale.setShipDate(results.getDate("ship_date").toLocalDate());
        }
        return sale;
    }

}
