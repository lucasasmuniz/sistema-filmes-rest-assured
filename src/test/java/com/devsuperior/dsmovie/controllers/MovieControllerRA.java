package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;

public class MovieControllerRA {
	
	private String adminToken, clientToken;
	private String movieTitle;
	private Long existingId, nonExistingId;

	@BeforeEach
	public void setUp() {
		baseURI = "http://localhost:8080"; 
		movieTitle = "harry";
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
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {	
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {		
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
	}
}
