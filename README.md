
Esta é uma demostração da implementação de um serviço que recupera todas as issues de um determinado repositório
no github e retorne um JSON assincronamente com 1 dia de diferença via webhook com as issues
e contribuidores que existiam no projeto no momento da chamada.
### Executar a aplicação backend 

Pré-requisitos

 -  Java 17
 -  Maven
 -  PostgreSQL
 
### Executar os comandos maven :

 mvn clean install
   
 mvn spring-boot:run




### Para acessar a API da aplicação:
http://127.0.0.1:8082/jsn-issues-analysis/swagger-ui/index.html
