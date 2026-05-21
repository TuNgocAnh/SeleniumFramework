package com.selenium.framework.api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Base class for API service objects. Subclass for each resource (e.g. UserApi, OrderApi), keep
 * endpoint paths private and expose semantic methods to tests.
 */
public abstract class BaseApi {

  protected RequestSpecification given() {
    return ApiClient.spec();
  }

  protected Response get(String path) {
    return given().when().get(path).then().extract().response();
  }

  protected Response post(String path, Object body) {
    return given().body(body).when().post(path).then().extract().response();
  }

  protected Response put(String path, Object body) {
    return given().body(body).when().put(path).then().extract().response();
  }

  protected Response delete(String path) {
    return given().when().delete(path).then().extract().response();
  }
}
