package com.champlain.enrollmentsservice;

import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockServerConfig {
    @Bean
    public ClientAndServer mockServer(){
        return ClientAndServer. startClientAndServer(1080);
    }
    @Bean
    public MockServerClient mockServerClient(ClientAndServer clientAndServer){
        return new MockServerClient("localhost", clientAndServer.getPort());
    }
}
