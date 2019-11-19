package com.learncamel.learncamel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("mock")
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SimpleCamelRouteMockTest extends CamelTestSupport {

    @Autowired
    CamelContext camelContext;

    @Autowired
    Environment env;

    @Autowired
    protected CamelContext createCamelContext() {
        return camelContext;
    }

    @Autowired
    ProducerTemplate producerTemplate;

    @Test
    public void testMoveFileMock() throws InterruptedException {
        String message = "type,sku#,itemdescription,price\n" +
                "ADD,100,Samsung TV,500\n" +
                "ADD,100,LG TV,200";

        MockEndpoint mockEndpoint = getMockEndpoint(env.getProperty("toRoute1"));
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived(message);

        producerTemplate.sendBodyAndHeader(env.getProperty("startRoute")
            , message, "env", env.getProperty("spring.profiles.active"));

        assertMockEndpointsSatisfied();
    }
}
