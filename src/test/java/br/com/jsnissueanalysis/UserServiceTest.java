package br.com.jsnissueanalysis;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import br.com.jsnissueanalysis.dto.UserDto;
import br.com.jsnissueanalysis.service.GitHubService;
import br.com.jsnissueanalysis.service.UserService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;



public class UserServiceTest {
    
    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private UserService userHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUser() {
    
        String repositoryToken = "test-token";
        StringBuilder uriBase = new StringBuilder("https://api.github.com/repos/test/repo"); 

       
        Map<String, Object> mockUserData = Map.of(
                "owner", Map.of("login", "testUser"),
                "name", "repoName"
        );

        // iniciar mocks
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(requestHeadersSpec).when(requestHeadersSpec).headers(any());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Mono.just(mockUserData)).when(responseSpec).bodyToMono(Object.class);

 
        Mono<UserDto> result = userHandler.getUser(webClient, uriBase, repositoryToken);

    
        StepVerifier.create(result)
                .expectNextMatches(userDto ->
                        userDto.getName().equals("testUser") &&
                        userDto.getRepositoryName().equals("repoName")
                )
                .expectComplete()
                .verify();
    }


}