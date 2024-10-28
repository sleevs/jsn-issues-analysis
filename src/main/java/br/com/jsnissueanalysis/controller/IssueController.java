package br.com.jsnissueanalysis.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import br.com.jsnissueanalysis.dto.ResponseDto;
import br.com.jsnissueanalysis.dto.UserDto;
import br.com.jsnissueanalysis.service.GitHubService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import reactor.core.publisher.Mono;


@RestController
public class IssueController {

    private GitHubService gitHubService;
  
    public IssueController(GitHubService gitHubService){
        this.gitHubService = gitHubService;
    }


 @PostMapping
@CircuitBreaker(name = "requestIssues", fallbackMethod = "fallbackMethod")
public Mono<ResponseDto> requestIssues(@RequestBody UserDto userDto) {
    
    if (userDto.getName() == null || userDto.getName().isEmpty()) {
        return Mono.error(new IllegalArgumentException("O nome do usuário não pode ser nulo ou vazio."));
    }


    return gitHubService.processamentoRequest(userDto)
        .onErrorResume(throwable -> {
            if (throwable instanceof WebClientResponseException) {
                WebClientResponseException webClientException = (WebClientResponseException) throwable;
  
                if (webClientException.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    return Mono.just(createErrorResponse("Erro de solicitação: " + webClientException.getResponseBodyAsString()));
                } else if (webClientException.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return Mono.just(createErrorResponse("Recurso não encontrado: " + webClientException.getResponseBodyAsString()));
                }
            }
            return Mono.error(throwable); // Retorna erro para outros casos
        });
    }

    // Método para criar uma resposta de erro
    private ResponseDto createErrorResponse(String message) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setErro(message);
        return responseDto;
    }

    // Método de fallback para o Circuit Breaker
    public Mono<ResponseDto> fallbackMethod(UserDto userDto, Throwable throwable) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setErro("Erro ao processar a requisição: " + throwable.getMessage());
        return Mono.just(responseDto);
    }

    
}
