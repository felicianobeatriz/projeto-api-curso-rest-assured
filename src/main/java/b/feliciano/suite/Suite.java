package b.feliciano.suite;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import b.feliciano.core.BaseTest;
import io.restassured.RestAssured;

@RunWith(org.junit.runners.Suite.class)
@SuiteClasses({
	ContasTest.Class,
	MovimentacaoTest.Class
	
})
public class Suite extends BaseTest{
	
	@BeforeClass
	public void _efeturarLogin() {
		Map<String, String> login= new HashMap<String, String>();
		login.put("email", "fulaninho@123");
		login.put("senha", "123456");
		
		String TOKEN = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token")
		;
		
		//Header compartilhado
		RestAssured.requestSpecification.header("Authorization","JWT "+ TOKEN)

	}
}
