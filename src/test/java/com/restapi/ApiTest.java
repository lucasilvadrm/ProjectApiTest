package com.restapi;

import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.*;


public class ApiTest {

    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_NOT_FOUND = 404;

    @BeforeClass
    public static void setup() {
        baseURI = "http://jsonplaceholder.typicode.com";
        basePath = "/posts";
    }

    // sobrecarda de método
    public Response getApiResponse() {
        return getApiResponse(STATUS_CODE_OK);
    }

    @Description("Método que retorna valida se o status code foi o esperado.")
    public Response getApiResponse(int status) {
        return given()
                .filter(new AllureRestAssured())
                .when()
                .get()
                .then()
                .assertThat()
                .statusCode(status)
                .extract()
                .response();
    }

    @Test
    @Description("Verifica se o status code foi 200.")
    public void givenPostsEndpoint_whenGetRequest_thenStatusCode200() {
        getApiResponse();
    }

    @Test
    @Description("verifica status code quando existe algum erro na declaração do endpoint.")
    public void givenPostsEndpoint_whenGetRequest_thenStatusCode404() {
        String originalBasePath = basePath;

        try {
            basePath = "/test";
            getApiResponse(STATUS_CODE_NOT_FOUND);
        } finally {
            basePath = originalBasePath;
        }
    }

    @Test
    @Description("verifica se retorna 100 objetos na lista.")
    public void givenPostsEndpoint_whenGetRequest_thenArraySizeIs100() {
        getApiResponse()
                .then()
                .body("size()", is(100));
    }

    @Test
    @Description("verifica se a resposta da rota é um array.")
    public void givenPostsEndpoint_whenGetRequest_thenResponseIsArray() {
        getApiResponse()
                .then()
                .body("$", instanceOf(List.class));
    }

    @Test
    @Description("verifica se a lista retornada está vazia.")
    public void givenPostsEndpoint_whenGetRequest_thenResponseListIsNotEmpty() {
        getApiResponse()
                .then()
                .body("size()", greaterThan(0));
    }

    @Test
    @Description("valida shape do objeto dentro do array.")
    public void givenPostsEndpoint_whenGetRequest_thenValidateObjectShape() {
        getApiResponse()
                .then()
                .body("size()", greaterThan(0))
                .body("[0]", hasKey("userId"))
                .body("[0]", hasKey("id"))
                .body("[0]", hasKey("title"))
                .body("[0]", hasKey("body"))
                .body("[0].userId", instanceOf(Integer.class))
                .body("[0].id", instanceOf(Integer.class))
                .body("[0].title", instanceOf(String.class))
                .body("[0].body", instanceOf(String.class));
    }

    @Test
    @Description("valida shape da resposta utilizando json-schema.")
    public void givenPostsEndpoint_whenGetRequest_thenValidateResponseUsingJsonSchema() {
        get().then().assertThat().body(matchesJsonSchemaInClasspath("posts-schema.json"));
    }
}
