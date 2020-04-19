package b.feliciano.tests;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

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
	
	@Test
	public void deveIncluirContaComSucesso() {
		Map<String, String> login= new HashMap<String, String>();
		login.put("email", "fulaninho@123");
		login.put("senha", "123456");
		
		String token = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token")
		;
	
		
		Map<String, String> conta = new HashMap<String, String>();
		conta.put("nome", "novaconta1");
		
		given()
			.header("Authorization","JWT "+ token)
			.body(conta)
		.when()
			.post("/contas")
		.then()
			.statusCode(201);
		;
	}
}

