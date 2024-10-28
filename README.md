
Esta é uma demostração da implementação de um serviço que recupera todas as issues de um determinado repositório
no github e retorne um JSON. Foram implementadas duas operações:
#### Enviar issues : retorna assincronamente com 1 dia de diferença via webhook com as issues e contribuidores que existiam no projeto no momento da chamada.
#### Receber agora : retorna imediatamente o resultado para o usuário via API 

### Executar a aplicação backend 

Pré-requisitos

 -  Java 17
 -  Maven
 -  PostgreSQL
 
### Primeira opção : Executar os comandos maven :

 mvn clean install
   
 mvn spring-boot:run

### Segunda opção : Executar a aplicação como container docker

Docker instalados na sua máquina.

Executar o docker-compose file :

docker-compose up --build


### Para acessar a API da aplicação:
http://127.0.0.1:8080/jsn-issues-analysis/swagger-ui/index.html
