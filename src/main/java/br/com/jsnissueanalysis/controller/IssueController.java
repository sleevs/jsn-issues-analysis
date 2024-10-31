package br.com.jsnissueanalysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import br.com.jsnissueanalysis.dto.RequestDto;
import br.com.jsnissueanalysis.dto.RequestNowDto;
import br.com.jsnissueanalysis.dto.ResponseDto;
import br.com.jsnissueanalysis.dto.UserDto;
import br.com.jsnissueanalysis.exception.IssuesAnalysisException;
import br.com.jsnissueanalysis.service.GitHubService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import reactor.core.publisher.Mono;


@RestController
public class IssueController {

        private GitHubService gitHubService;
   
        public IssueController(GitHubService gitHubService){
            this.gitHubService = gitHubService;
        }

        @Operation(
        summary = "Enviar issues Depois",
        description = "Realiza um request para GITHUB API e retorna um JSON assincronamente com 1 dia de diferença e via webhook com as issues e contribuidores que existiam no projeto no momento da chamada.",
        tags = {"Enviar issues"}
        )
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Issues enviada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
        })
        @PostMapping(value = "/v1/send-after")
        @CircuitBreaker(name = "sendAfter", fallbackMethod = "fallbackMethod")
        public Mono<ResponseDto> sendAfter(@RequestBody RequestDto request) {
            
            if (request.getUsername() == null || request.getUsername().isEmpty()) {
                return Mono.error(new IllegalArgumentException("O nome do usuário não pode ser nulo ou vazio."));
            }

            return gitHubService.sendAfter(request)
                .onErrorResume(throwable -> {
                    if (throwable instanceof WebClientResponseException) {
                        WebClientResponseException webClientException = (WebClientResponseException) throwable;

                        if (webClientException.getStatusCode() == HttpStatus.BAD_REQUEST) {
                            return Mono.just(createErrorResponse("Erro de solicitação: " + webClientException.getResponseBodyAsString()));
                        } else if (webClientException.getStatusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.just(createErrorResponse("Recurso não encontrado: " + webClientException.getResponseBodyAsString()));
                        }
                    } else if (throwable instanceof Exception) {
                        // Handle generic exceptions
                        return Mono.error(new IssuesAnalysisException("Ocorreu um erro interno. Tente novamente mais tarde."));
                    }
                    return Mono.error(throwable);
                });
        }


        @Operation(
        summary = "Receber issues agora",
        description = "Realiza um request para GITHUB API e retorna um JSON com as issues e contribuidores que existiam no projeto no momento da chamada.",
        tags = {"Receber agora"}
        )
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Issues recuperada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
        })   
        @PostMapping(value = "/v1/send-now")
        @CircuitBreaker(name = "sendNow", fallbackMethod = "fallbackMethod")
        public Mono<ResponseDto> sendNow(@RequestBody RequestNowDto request) {
            
            if (request.getUsername() == null || request.getUsername().isEmpty()) {
                return Mono.error(new IllegalArgumentException("O nome do usuário não pode ser nulo ou vazio."));
            }
        
            return gitHubService.sendNow(request)
                .onErrorResume(throwable -> {
                    if (throwable instanceof WebClientResponseException) {
                        WebClientResponseException webClientException = (WebClientResponseException) throwable;
        
                        if (webClientException.getStatusCode() == HttpStatus.BAD_REQUEST) {
                            return Mono.just(createErrorResponse("Erro de solicitação: " + webClientException.getResponseBodyAsString()));
                        } else if (webClientException.getStatusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.just(createErrorResponse("Recurso não encontrado: " + webClientException.getResponseBodyAsString()));
                        }
                    } else if (throwable instanceof Exception) {
                        // Handle generic exceptions
                        return Mono.error(new IssuesAnalysisException("Ocorreu um erro interno. Tente novamente mais tarde."));
                    }
                    return Mono.error(throwable);
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
