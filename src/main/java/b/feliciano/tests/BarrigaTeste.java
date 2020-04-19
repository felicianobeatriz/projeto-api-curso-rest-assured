package b.feliciano.tests;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import b.feliciano.core.BaseTest;

public class BarrigaTeste extends BaseTest{

	@Test
	public void naoDeveAcessarAPISemToken() {
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
}
