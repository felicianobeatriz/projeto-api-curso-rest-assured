package b.feliciano.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import b.feliciano.core.BaseTest;
import io.restassured.RestAssured;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTeste extends BaseTest{
	
	private String TOKEN;
	private static String NOME_CONTA = "Conta" + System.nanoTime();
	private static Integer CONTA_ID;

	@Before
	public void _efeturarLogin() {
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
		
		//Header compartilhado
		RestAssured.requestSpecification.header("Authorization","JWT "+ TOKEN)

	}
	
	@Test
	public void t01_naoDeveAcessarAPISemToken() {
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
	
	@Test
	public void t02_deveIncluirContaComSucesso() {
	
		Map<String, String> conta = new HashMap<String, String>();
		conta.put("nome",NOME_CONTA);
		
		CONTA_ID = given()
			.header("Authorization","JWT "+ TOKEN)
			.body(conta)
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t03_deveAlterarContaComSucesso() {

		Map<String, String> conta = new HashMap<String, String>();
		conta.put("nome", NOME_CONTA + "Alterada!");
		
		given()
			.header("Authorization","JWT "+ TOKEN)
			.body(conta)
		.when()
			.put("/contas/" + CONTA_ID)
		.then()
			.statusCode(200)
			.body("name", is("Conta Alterada!"))
		;
	}
	
	@Test
	public void t04_rejeitaContaComNomeIgual() {
	
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
	
	@Test
	public void t05_deveInserirMovimentacaoComSucesso() {	
		Movimentacao movimentacao = getMovimentacao();
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body(movimentacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void t06_deveValidarCamposObrigatorios() {	
		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", 
					hasItems(
							"Data da Movimentação é obrigatório", 
							"Data do pagamento é obrigatório", 
							"Descrição é obrigatório", 
							"Interessado é obrigatório", 
							"Valor é obrigatório", 
							"Valor deve ser um número", 
							"Conta é obrigatório", 
							"Situação é obrigatório"))
		;
	}
	
	@Test
	public void t07_naoDeveAceitarDataDeMovimentacaoFutura() {
		Movimentacao movimentacao = getMovimentacao();	
		movimentacao.setData_pagamento(DateUtils.getDataDiferencaDias(2));
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body(movimentacao)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItem("Data de Movimentação deve ser menor ou igual a data atual"))
		;
	}
	
	@Test
	public void t08_naoDeveRemoverContaComPagamentoPendente() {
		given()
			.header("Authorization", "JWT " + TOKEN)
		.when()
			.delete("/contas/"+ CONTA_ID)
		.then()
			.statusCode(500)
			.body("constraints", is("transacoes_conta_id_foreing"))
		;
	}
	
	@Test
	public void t09_deveSomarOTotalDeTodasAsContas() {
		given()
			.header("Authorization", "JWT " + TOKEN)
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_Id == 105806}.saldo",is("100.00"));
		;
	}
	
	@Test
	public void t10_deveRemoverMovimetacao() {
		given()
			.header("Authorization", "JWT " + TOKEN)
		.when()
			.delete("/transacoes/105806")
		.then()
			.statusCode(204)
		;
	}
	
	private Movimentacao getMovimentacao() {
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setConta_id(105806);
		//movimentacao.setUsuario_id();
		movimentacao.setDescricao("descricao da mov");
		movimentacao.setEnvolvido("envolvido da mov");
		movimentacao.setTipo("REC");
		movimentacao.setData_transacao(DateUtils.getDataDiferencaDias(-1));
		movimentacao.setData_pagamento(DateUtils.getDataDiferencaDias(5));
		movimentacao.setValor(200F);
		movimentacao.setStatus(true);		
		
		return movimentacao;
	}
}

