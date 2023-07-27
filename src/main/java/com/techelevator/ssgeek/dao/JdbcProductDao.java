package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductDao implements ProductDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcProductDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public Product getProduct(int productId) {
        Product product = null;
        String sql = "SELECT * FROM product WHERE product_id = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, productId);
        if (row.next()) {
            product = mapRowToProduct(row);
        }
        return product;
    }

    @Override
    public List<Product> getProducts() {
        List<Product> results = new ArrayList<>();
        String sql = "SELECT * FROM product";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
        while(row.next()) {
            Product product = mapRowToProduct(row);
            results.add(product);
        }
        return results;
    }

    @Override
    public List<Product> getProductsWithNoSales() {
        List<Product> results = new ArrayList<>();
        String sql = "SELECT * FROM product " +
                    "JOIN line_item ON line_item.product_id = product.product_id " +
                    "JOIN sale ON sale.sale_id = line_item.sale_id " +
                    "WHERE sale.sale_date IS NULL " +
                    "ORDER BY product.product_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
        if (row.next()) {
            Product product = mapRowToProduct(row);
            results.add(product);
        }
        return results;
    }

    @Override
    public Product createProduct(Product newProduct) {
        String sql = "INSERT INTO product (name, description, price, image_name) VALUES (?, ?, ?, ?) RETURNING product_id";
        int newId = jdbcTemplate.queryForObject(sql, int.class, newProduct.getName(), newProduct.getDescription(), newProduct.getPrice(), newProduct.getImageName());
        newProduct.setProductId(newId);
        return newProduct;
    }

    @Override
    public void updateProduct(Product updatedProduct) {
        String sql = "UPDATE product SET name = ?, description = ? price = ? image_name = ? WHERE product_id = ?";
        jdbcTemplate.update(sql, updatedProduct.getName(), updatedProduct.getDescription(), updatedProduct.getPrice(), updatedProduct.getImageName(), updatedProduct.getProductId());


    }

    @Override
    public void deleteProduct(int productId) {
        String sql = "DELETE FROM line_item WHERE product_id = ?";
        jdbcTemplate.update(sql, productId);

        String newSql = "DELETE FROM product WHERE product_id = ?";
        jdbcTemplate.update(newSql, productId);

    }


    private Product mapRowToProduct(SqlRowSet results) {
        Product product = new Product();
        product.setProductId(results.getInt("product_id"));
        product.setName(results.getString("name"));
        product.setDescription(results.getString("description"));
        product.setPrice(results.getBigDecimal("price"));
        product.setImageName(results.getString("image_name"));
        return product;
    }
}
