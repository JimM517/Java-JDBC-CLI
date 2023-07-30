package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.LineItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcLineItemDao implements LineItemDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcLineItemDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public List<LineItem> getLineItemsBySale(int saleId) {
        List<LineItem> results = new ArrayList<>();
        String sql = "SELECT line_item_id, line_item.sale_id, product.name, product.price, line_item.product_id, line_item.quantity FROM line_item " +
                    "JOIN product ON product.product_id = line_item.product_id " +
                    "JOIN sale ON sale.sale_id = line_item.sale_id " +
                    "JOIN customer ON customer.customer_id = sale.customer_id " +
                    "WHERE line_item.sale_id = ? " +
                    "ORDER BY line_item_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, saleId);
        while(row.next()) {
            LineItem lineItem = mapRowToLineItem(row);
            lineItem.setProductName(row.getString("name"));
            lineItem.setPrice(row.getBigDecimal("price"));
            results.add(lineItem);
        }
        return results;
    }



    private LineItem mapRowToLineItem(SqlRowSet results) {
        LineItem lineItem = new LineItem();
        lineItem.setLineItemId(results.getInt("line_item_id"));
        lineItem.setSaleId(results.getInt("sale_id"));
        lineItem.setProductId(results.getInt("product_id"));
        lineItem.setQuantity(results.getInt("quantity"));
    }

}
