package com.champlain.enrollmentsservice.presentationlayer;

import com.champlain.enrollmentsservice.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.client.MockServerClient;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@MockServerTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        "spring.data.mongodb.port: 0",
        "app.students-service.port=${mockServerPort}",
        "app.courses-service.port=${mockServerPort}"
})
public class AbstractIntegrationClass {

    @Autowired
    protected MockServerClient mockServerClient;

    @Autowired
    protected WebTestClient webTestClient;

    private static final Path TEST_RESOURCES_PATH = Path.of("src/test/resources");

    protected final TestData testData = new TestData();

    @BeforeAll
    public static void setup() {
        ConfigurationProperties.disableLogging(true);
    }

    protected String resourceToString(String relativePath) {
        try {
            return Files.readString(TEST_RESOURCES_PATH.resolve(relativePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}