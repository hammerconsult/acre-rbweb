package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class MoedaIT {
	private WebDriver driver;
	private String baseUrl="";
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testEMoeda() throws Exception {
		driver.get("http://localhost:8080/webpublico/");
		driver.findElement(By.cssSelector("input[type=submit]")).click();
		driver.findElement(By.linkText("Cadastros Gerais")).click();
		driver.findElement(By.id("moeda")).click();
		driver.switchTo().frame("corpo");
		driver.findElement(By.id("listaMoeda:formTabelaGenerica:botaoNovo")).click();
		driver.findElement(By.id("Formulario:descricao")).clear();
		driver.findElement(By.id("Formulario:descricao")).sendKeys("Moeda Teste");
		driver.findElement(By.id("Formulario:ano")).clear();
		driver.findElement(By.id("Formulario:ano")).sendKeys("2050");
		((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:mes'].value = '05'");
		driver.findElement(By.id("Formulario:valor")).clear();
		driver.findElement(By.id("Formulario:valor")).sendKeys("150");
		driver.findElement(By.id("Formulario:indiceEconomico")).click();
		((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:indiceEconomico'].value = '7495'");
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
