package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class LogradouroIT {
	private WebDriver driver;
	private String baseUrl="";
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testELogradouro() throws Exception {
		driver.get("http://localhost:8080/webpublico/");
		driver.findElement(By.cssSelector("input[type=submit]")).click();
		driver.findElement(By.linkText("Cadastros Gerais")).click();
		driver.findElement(By.linkText("Logradouro")).click();
		driver.switchTo().frame("corpo");
		driver.findElement(By.id("listaLogradouro:formTabelaGenerica:botaoNovo")).click();
		driver.findElement(By.id("Formulario:nome")).click();
		driver.findElement(By.id("Formulario:nome")).clear();
		driver.findElement(By.id("Formulario:nome")).sendKeys("Logradouro Teste");
		driver.findElement(By.id("Formulario:tipoLogradouro")).click();
		// ERROR: Caught exception [ERROR: Unsupported command [select]]
		((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:tipoLogradouro'].value = '5568'");
		driver.findElement(By.id("Formulario:bairro_input")).click();
		driver.findElement(By.id("Formulario:bairro_input")).clear();
		((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:bairro_hinput'].value = '8117'");
		driver.findElement(By.id("Formulario:botao")).click();
		driver.findElement(By.id("Formulario:cep")).clear();
		driver.findElement(By.id("Formulario:cep")).sendKeys("86990-001");
                driver.findElement(By.id("Formulario:inicio")).clear();
		driver.findElement(By.id("Formulario:inicio")).sendKeys("01");
		driver.findElement(By.id("Formulario:fim")).clear();
		driver.findElement(By.id("Formulario:fim")).sendKeys("02");
		driver.findElement(By.id("Formulario:botao")).click();

                //teste mais que um cep
                /*driver.findElement(By.id("Formulario:botao")).click();
		driver.findElement(By.id("Formulario:cep")).clear();
		driver.findElement(By.id("Formulario:cep")).sendKeys("86990-002");
                driver.findElement(By.id("Formulario:inicio")).clear();
		driver.findElement(By.id("Formulario:inicio")).sendKeys("03");
		driver.findElement(By.id("Formulario:fim")).clear();
		driver.findElement(By.id("Formulario:fim")).sendKeys("05");
		driver.findElement(By.id("Formulario:botao")).click();*/


		driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

	private boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
}
