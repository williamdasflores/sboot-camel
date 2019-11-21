package com.learncamel.learncamel.process;

import com.learncamel.learncamel.domain.Item;
import com.learncamel.learncamel.exception.DataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BuildSQLProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Item item = (Item) exchange.getIn().getBody();
        log.info("Item Processor is: " +item);

        if (item.getSku() == null || "".equals(item.getSku()))
            throw new DataException("Sku does not exist for " +item.getItemDescription());

        StringBuilder query = new StringBuilder();
        if ("ADD".equals(item.getTransactionType())) {
            query.append("INSERT INTO ITEMS (SKU, ITEM_DESCRIPTION, PRICE) VALUES('");
            query.append(item.getSku() +"','"+ item.getItemDescription() +"'," +item.getPrice() +")");
        } else if ("UPDATE".equals(item.getTransactionType())) {
            query.append("UPDATE ITEMS SET PRICE = ");
            query.append(item.getPrice() + " WHERE SKU = '" +item.getSku()+ "'");
;        } else if ("DELETE".equals(item.getTransactionType())) {
            query.append("DELETE FROM ITEMS WHERE SKU = '" +item.getSku()+ "'");
        }

        log.info("Final Query: " +query);
        exchange.getIn().setBody(query.toString());

    }
}
