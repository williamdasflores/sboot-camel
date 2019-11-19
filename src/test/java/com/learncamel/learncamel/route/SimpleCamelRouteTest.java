package com.learncamel.learncamel.route;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@ActiveProfiles("dev")
@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class SimpleCamelRouteTest {

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    Environment env;

    @BeforeClass
    public static void startCleanUp() throws IOException {
        FileUtils.cleanDirectory(new File("data/input"));
        FileUtils.deleteDirectory(new File("data/output"));
    }

    @Test
    public void testMoveFile() throws InterruptedException {
        String fileName = "fileTest.txt";
        String message = "type,sku#,itemdescription,price\n" +
                "ADD,100,Samsung TV,500\n" +
                "ADD,100,LG TV,200";

        producerTemplate.sendBodyAndHeader(env.getProperty("fromRoute"),
                message, Exchange.FILE_NAME, fileName);

        Thread.sleep(3000);

        File outFile = new File("data/output/"+fileName);
        Assert.assertTrue(outFile.exists());
    }

    @Test
    public void testMoveFile_ADD() throws InterruptedException, IOException {
        String fileName = "fileTest.txt";
        String message = "type,sku#,itemdescription,price\n" +
           //     "ADD,102,Sony TV,900\n" +
                "ADD,103,Apple Iphone,999";

        producerTemplate.sendBodyAndHeader(env.getProperty("fromRoute"),
                message, Exchange.FILE_NAME, fileName);

        Thread.sleep(3000);

        File outFile = new File("data/output/"+fileName);
        Assert.assertTrue(outFile.exists());

        String output = new String(Files.readAllBytes(Paths.get("data/output/Success.txt")));
        String outputMessage = "Data Updated Successfully";
        Assert.assertEquals(outputMessage, output);
    }

    @Test
    public void testMoveFile_UPDATE() throws InterruptedException, IOException {
        String fileName = "fileTestUpdate.txt";
        String message = "type,sku#,itemdescription,price\n" +
                "UPDATE,100,Samsung TV,300";

        producerTemplate.sendBodyAndHeader(env.getProperty("fromRoute"),
                message, Exchange.FILE_NAME, fileName);

        Thread.sleep(3000);

        File outFile = new File("data/output/"+fileName);
        Assert.assertTrue(outFile.exists());

        String output = new String(Files.readAllBytes(Paths.get("data/output/Success.txt")));
        String outputMessage = "Data Updated Successfully";
        Assert.assertEquals(outputMessage, output);
    }
}
