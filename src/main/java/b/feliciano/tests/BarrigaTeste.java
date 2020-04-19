package b.feliciano.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import b.feliciano.core.BaseTest;

public class BarrigaTeste extends BaseTest{
	
	private String TOKEN;

	@Before
	public void efeturarLogin() {
		Map<String, String> login= new HashMap<String, String>();
		login.put("email", "fulaninho@123");
		login.put("senha", "123456");
		
		TOKEN = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token")
		;

	}
	
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
	
		Map<String, String> conta = new HashMap<String, String>();
		conta.put("nome", "novaconta1");
		
		given()
			.header("Authorization","JWT "+ TOKEN)
			.body(conta)
		.when()
			.post("/contas")
		.then()
			.statusCode(201);
		;
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		
	
		
		Map<String, String> conta = new HashMap<String, String>();
		conta.put("nome", "Conta Alterada!");
		
		given()
			.header("Authorization","JWT "+ TOKEN)
			.body(conta)
		.when()
			.put("/contas/105806")
		.then()
			.statusCode(200)
			.body("name", is("Conta Alterada!"))
		;
	}
	
	@Test
	public void rejeitaContaComNomeIgual() {
	
		Map<String, String> conta = new HashMap<String, String>();
		conta.put("nome", "Conta Alterada!");
		
		given()
			.header("Authorization","JWT "+ TOKEN)
			.body(conta)
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
}

