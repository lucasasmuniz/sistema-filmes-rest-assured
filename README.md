# DSMovie RestAssured - Testes de Integração

Este repositório contém a implementação dos testes de integração (IT) utilizando **RestAssured** para o projeto **DSMovie**, proposto no curso da **DevSuperior** como parte do módulo de Testes de integração.

---

## Sobre o Projeto DSMovie

O sistema consiste em uma aplicação de filmes e avaliações. A visualização dos filmes é pública (sem login), mas as operações de inserção, atualização e deleção de filmes estão restritas a usuários com perfil **ADMIN**. Avaliações podem ser registradas por qualquer usuário logado (**CLIENT** ou **ADMIN**).

A entidade `Score` representa a nota (0 a 5) atribuída por um usuário a um filme. Sempre que uma nova avaliação é feita, a média das notas é recalculada e armazenada no filme junto com a contagem total de votos.

---

## Objetivo do Desafio

Implementar os testes de API utilizando **RestAssured** conforme os seguintes requisitos.  
Para ser aprovado no desafio, é necessário implementar **no mínimo 8 dos 10 testes**.

### MovieControllerRA

- `findAllShouldReturnOkWhenMovieNoArgumentsGiven`
- `findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty`
- `findByIdShouldReturnMovieWhenIdExists`
- `findByIdShouldReturnNotFoundWhenIdDoesNotExist`
- `insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle`
- `insertShouldReturnForbiddenWhenClientLogged`
- `insertShouldReturnUnauthorizedWhenInvalidToken`

### ScoreControllerRA

- `saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist`
- `saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId`
- `saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero`

---

## Tecnologias Utilizadas

- Java com Spring Boot  
- RestAssured  
- JUnit 5  
- Banco de dados H2 para testes  
- JSON Simple (para montar objetos JSON a partir de mapas)  

---

## Configuração do RestAssured

Defina a `baseURI` no método `setUp()` com o endereço da API (ex: `http://localhost:8080`).  
Lembre de alterar a porta de onde os testes serão rodados se a aplicação principal estiver usando a 8080.

Utilize os imports estáticos:

```java
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
```

---

## Autenticação - Obtendo Token de Acesso

```java
public static Response getAccessToken(String username, String password) {
    return given()
        .auth()
        .preemptive()
        .basic("myclientid", "myclientsecret")
        .contentType("application/x-www-form-urlencoded")
        .formParam("grant_type", "password")
        .formParam("username", username)
        .formParam("password", password)
    .when()
        .post("/oauth2/token");
}
```

O `Response` é convertido em JSON usando `.jsonPath().getString("access_token")` para extrair o token.

---

## Exemplos de Métodos e Validações

```java
given()
    .header("Authorization", "Bearer " + token)
    .get("/movies")
.then()
    .statusCode(200)
    .body("id", hasItem(1));
```

```java
given()
    .get("/movies")
.then()
    .statusCode(200)
    .body("content.findAll { it.score > 4.0 }.title", hasItems("Inception", "Interstellar"));
```

> **Obs:** se o campo for `float`, use `it.score.toFloat()` para evitar erros de tipo.

---

Este repositório é parte dos meus estudos em testes de integração com **RestAssured**, simulando cenários reais com autenticação, filtros e regras de acesso.

Mais detalhes do projeto principal e uma cobertura de testes unitários utilizando JaCoCo:  
[lucasasmuniz/sistema-filmes-notas-testes](https://github.com/lucasasmuniz/sistema-filmes-notas-testes)
