package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItem;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class ScoreControllerRA {
	
	private Long nonExistingMovieId;
	private String token;
	private Map<String,Object> score;
	
	@BeforeEach
	public void setUp() throws JSONException {
		baseURI = "http://localhost:8080";
		
		token = TokenUtil.obtainAccessToken("alex@gmail.com", "123456");
		nonExistingMovieId = 300L;
		
		score = new HashMap<>();
		score.put("movieId", 1);
		score.put("score", 4);
		
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {		
		score.put("movieId", nonExistingMovieId);
		JSONObject jsonObject = new JSONObject(score);
		
		given()
			.header("Authorization", "bearer " + token)
			.contentType(ContentType.JSON)
			.body(jsonObject)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(404);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		score.put("movieId", null);
		JSONObject jsonObject = new JSONObject(score);
		
		given()
			.header("Authorization", "bearer " + token)
			.contentType(ContentType.JSON)
			.body(jsonObject)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(422)
			.body("errors.fieldName", hasItem("movieId"))
			.body("errors.message", hasItem("Campo requerido"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {	
		score.put("score", -3);
		JSONObject jsonObject = new JSONObject(score);
		
		given()
			.header("Authorization", "bearer " + token)
			.contentType(ContentType.JSON)
			.body(jsonObject)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(422)
			.body("errors.fieldName", hasItem("score"))
			.body("errors.message", hasItem("Valor mínimo 0"));
	}
}
