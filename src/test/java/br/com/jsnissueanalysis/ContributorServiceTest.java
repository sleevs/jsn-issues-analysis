package br.com.jsnissueanalysis;



import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.any;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

import br.com.jsnissueanalysis.dto.ContributorDto;
import br.com.jsnissueanalysis.service.ContributorService;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;



public class ContributorServiceTest {



    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ContributorService contributorHandler;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetContributors() {
        // Arrange
        String repositoryToken = "test-token";
        StringBuilder uriBase = new StringBuilder("/repos/test/repo");

        // Mock the response from the WebClient
        Map<String, Object> mockContributor = Map.of(
                "login", "contributor1",
                "html_url", "https://github.com/contributor1",
                "contributions", 42
        );

            // Setup mocks
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(requestHeadersSpec).when(requestHeadersSpec).headers(any());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Flux.just(mockContributor)).when(responseSpec).bodyToFlux(Object.class);

        // Act
        Flux<ContributorDto> result = contributorHandler.getContributors(webClient, uriBase, repositoryToken);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(contributor -> {
                    // Check that the returned contributor matches the mock data
                    return contributor.getName().equals("contributor1")
                            && contributor.getUser().equals("https://github.com/contributor1")
                            && contributor.getContributions() == 42;
                })
                .expectComplete()
                .verify();
    }
}