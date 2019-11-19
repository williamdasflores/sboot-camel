package com.learncamel.learncamel.route;

import com.learncamel.learncamel.domain.Item;
import com.learncamel.learncamel.process.BuildSQLProcessor;
import com.learncamel.learncamel.process.SuccessProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
public class SimpleCamelRoute extends RouteBuilder {

    @Autowired
    Environment env;

    @Qualifier("dataSource")
    @Autowired
    DataSource dataSource;

    @Autowired
    BuildSQLProcessor buildSQLProcessor;

    @Autowired
    SuccessProcessor successProcessor;

    @Override
    public void configure() throws Exception {
        log.info(">>>>>>>>>> Starting Route with CAMEL <<<<<<<<<<");

        DataFormat bindy = new BindyCsvDataFormat(Item.class);
        /*from("timer:hello?period=10s")
                .log("Timer Invoked and the body is ${body}")
                .pollEnrich("file:data/input?delete=true&readLock=none")
                .to("file:data/output");*/


        // Example how to retrieve from application.yml
        from("{{startRoute}}")
                .log("Timer Invoked and the body " + env.getProperty("message"))
                .choice()
                    .when((header("env").isNotEqualTo("mock")))
                        .pollEnrich("{{fromRoute}}")
                    .otherwise()
                        .log("mock env flow and the body is ${body}")
                .end()
                .to("{{toRoute1}}")
                .unmarshal(bindy)
                .log("Unmarshaled object is ${body}")
                .split(body())
                    .log("Records is ${body}")
                    .process(buildSQLProcessor)
                    .to("{{toRoute2}}")
                .end()
        .process(successProcessor)
        .to("{{toRoute3}}");

        log.info("Ending Route");

    }
}
