package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class MovieControllerRA {
	
	private String adminToken, clientToken, invalidToken;
	private String movieTitle;
	private Long existingId, nonExistingId;
	private Map<String, Object> movie;

	@BeforeEach
	public void setUp() throws JSONException {
		baseURI = "http://localhost:8080"; 
		
		movieTitle = "harry";
		existingId = 1L;
		nonExistingId = 300L;
		adminToken = TokenUtil.obtainAccessToken("maria@gmail.com", "123456");
		clientToken = TokenUtil.obtainAccessToken("alex@gmail.com", "123456");
		invalidToken = clientToken + "123333";
		
		
		movie = new HashMap<>();
		movie.put("title", "Test Movie");
		movie.put("score", 0F);
		movie.put("count", 0);
		movie.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get("/movies")
		.then()
			.statusCode(200)
			.body("content.findAll { it.score.toFloat() > 3 }.title", hasItems("The Witcher", "Venom: Tempo de Carnificina"))
			.body("totalElements", is(29))
			.body("content.id[5]", is(6))
			.body("content.title[5]", equalTo("Django Livre"))
			.body("content.score[5]", is(0.0F))
			.body("content.count[5]", is(0))
			.body("content.image[5]", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/2oZklIzUbvZXXzIFzv7Hi68d6xf.jpg"));
			
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {	
		given()
			.accept(ContentType.JSON)
		.when()
			.get("/movies?title={title}", movieTitle)
		.then()
			.statusCode(200)
			.body("totalElements", is(2))
			.body("content.title", hasItems("Harry Potter e as Relíquias da Morte - Parte 1", "Harry Potter e a Pedra Filosofal"))
			.body("content.id", hasItems(19,20))
			.body("content.score[1]", is(0.0F))
			.body("content.count[1]", is(0))
			.body("content.image[1]", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/lvOLivVeX3DVVcwfVkxKf0R22D8.jpg"));
			
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {	
		given()
			.accept(ContentType.JSON)
		.when()
			.get("/movies/{id}", existingId)
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("title", equalTo("The Witcher"))
			.body("score", is(4.5F))
			.body("count", is(2))
			.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		given()
			.get("/movies/{id}", nonExistingId)
		.then()
			.statusCode(404);
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {	
		movie.put("title", "");
		JSONObject jsonObject = new JSONObject(movie);
		
		given()
			.header("Authorization", "bearer " + adminToken)
			.contentType(ContentType.JSON)
			.body(jsonObject)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(422)
			.body("errors.fieldName", hasItem("title"))
			.body("errors.message", hasItem("Campo requerido"));
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		JSONObject jsonObject = new JSONObject(movie);
		
		given()
			.header("Authorization", "bearer " + clientToken)
			.contentType(ContentType.JSON)
			.body(jsonObject)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		JSONObject jsonObject = new JSONObject(movie);
		
		given()
			.header("Authorization", "bearer " + invalidToken)
			.contentType(ContentType.JSON)
			.body(jsonObject)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(401);
	}
}
