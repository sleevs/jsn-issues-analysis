package br.com.jsnissueanalysis;

import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.jsnissueanalysis.dto.IssueDto;
import br.com.jsnissueanalysis.service.IssuesService;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IssueServiceTest {
   
    
    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private IssuesService issuesHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetIssues() {
        // Arrange
        String repositoryToken = "test-token";
        StringBuilder uriBase = new StringBuilder("/repos/test/repo");

        // Mock issue data
        Map<String, Object> mockIssue = Map.of(
                "title", "Issue 1",
                "user", Map.of("login", "author1"),
                "labels", List.of(Map.of("name", "bug"))
        );

        // Setup mocks
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(requestHeadersSpec).when(requestHeadersSpec).headers(any()); // This should return the requestHeadersSpec
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Flux.just(mockIssue)).when(responseSpec).bodyToFlux(Object.class);

        // Act
        Flux<IssueDto> result = issuesHandler.getIssues(webClient, uriBase, repositoryToken);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(issue ->
                    issue.getTitle().equals("Issue 1") &&
                    issue.getAuthor().equals("author1") &&
                    issue.getLabels().equals(List.of("bug"))
                )
                .expectComplete()
                .verify();
    }
}
