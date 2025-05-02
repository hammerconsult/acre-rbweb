package br.com.webpublico.test.integracao.antigo;

import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TipoLogradouroIT {
	private WebDriver driver;
	private String baseUrl="";
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testETipoLogradouro() throws Exception {
		driver.get("http://localhost:8080/webpublico/");
		driver.findElement(By.cssSelector("input[type=submit]")).click();
		driver.findElement(By.linkText("Cadastros Gerais")).click();
		driver.findElement(By.id("tipoLogradouro")).click();
		driver.switchTo().frame("corpo");
		driver.findElement(By.id("listaTipoLogradouro:formTabelaGenerica:botaoNovo")).click();
		driver.findElement(By.id("Formulario:descricao")).click();
		driver.findElement(By.id("Formulario:descricao")).clear();
		driver.findElement(By.id("Formulario:descricao")).sendKeys("Tipo Logradouro Andre");
		driver.findElement(By.id("Formulario:sigla")).clear();
		driver.findElement(By.id("Formulario:sigla")).sendKeys("TLA");
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
