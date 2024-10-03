package com.restapi;

import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;


public class ApiTest {

    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_NOT_FOUND = 404;

    @BeforeClass
    public static void setup() {
        // Configurar a URI base e o caminho base para a API Rest
        baseURI = "http://jsonplaceholder.typicode.com";
        basePath = "/posts";
    }

    // sobrecarda de método
    public Response getApiResponse() {
        return getApiResponse(STATUS_CODE_OK);
    }

    public Response getApiResponse(int status) {
       return given()
                .when()
                    .get()
                .then()
                    .assertThat()
                        .statusCode(status)
                .extract()
                    .response();
    }

    @Test
    public void givenPostsEndpoint_whenGetRequest_thenStatusCode200() {
        // verifica status code
        getApiResponse();
    }

    @Test
    public void givenPostsEndpoint_whenGetRequest_thenStatusCode404() {
        // verifica status code quando existe algum erro na declaração do endpoint
        String originalBasePath = basePath;

        try {
            basePath = "/test";
            getApiResponse(STATUS_CODE_NOT_FOUND);
        } finally {
            basePath = originalBasePath;
        }
    }

    @Test
    public void givenPostsEndpoint_whenGetRequest_thenArraySizeIs100() {
        // verifica se retorna 100 objetos na lista
        getApiResponse()
                .then()
                    .body("size()", is(100));
    }

    @Test
    public void givenPostsEndpoint_whenGetRequest_thenResponseIsArray() {
        // verifica se a resposta da rota é um array
        getApiResponse()
                .then()
                    .body("$", instanceOf(List.class));
    }

    @Test
    public void givenPostsEndpoint_whenGetRequest_thenResponseListIsNotEmpty() {
        // verifica se a lista retornada está vazia
        getApiResponse()
                .then()
                    .body("size()", greaterThan(0));
    }

    @Test
    public void givenPostsEndpoint_whenGetRequest_thenValidateObjectShape() {
        // valida shape do objeto dentro do array
        getApiResponse()
                .then()
                    .body("size()", greaterThan(0)) // Valida que a lista não está vazia
                    .body("[0]", hasKey("userId")) // Valida que o campo userId está presente
                    .body("[0]", hasKey("id")) // Valida que o campo id está presente
                    .body("[0]", hasKey("title")) // Valida que o campo title está presente
                    .body("[0]", hasKey("body")) // Valida que o campo body está presente
                    .body("[0].userId", instanceOf(Integer.class)) // Valida que o campo userId é do tipo Integer
                    .body("[0].id", instanceOf(Integer.class)) // Valida que o campo id é do tipo Integer
                    .body("[0].title", instanceOf(String.class)) // Valida que o campo title é do tipo String
                    .body("[0].body", instanceOf(String.class)); // Valida que o campo body é do tipo String
    }

    @Test
    public void givenPostsEndpoint_whenGetRequest_thenValidateResponseUsingJsonSchema() {
        get().then().assertThat().body(matchesJsonSchemaInClasspath("posts-schema.json"));
    }
}
