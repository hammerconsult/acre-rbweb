package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class BancoIT {
	private WebDriver driver;
	private String baseUrl="";
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testEBanco() throws Exception {
		driver.get("http://localhost:8080/webpublico/");
		driver.findElement(By.cssSelector("input[type=submit]")).click();
		driver.findElement(By.xpath("//ul[@id='menuEsquerdo_menu']/li[2]/a/span/span")).click();
		driver.findElement(By.linkText("Banco")).click();
		driver.switchTo().frame("corpo");
		driver.findElement(By.id("listabanco:formTabelaGenerica:botaoNovo")).click();
		driver.findElement(By.id("Formulario:descricao")).click();
		driver.findElement(By.id("Formulario:descricao")).clear();
		driver.findElement(By.id("Formulario:descricao")).sendKeys("Banco André");
		driver.findElement(By.id("Formulario:numeroBanco")).clear();
		driver.findElement(By.id("Formulario:numeroBanco")).sendKeys("456465");
		driver.findElement(By.id("Formulario:sigla")).clear();
		driver.findElement(By.id("Formulario:sigla")).sendKeys("123");
		driver.findElement(By.id("Formulario:homePage")).clear();
		driver.findElement(By.id("Formulario:homePage")).sendKeys("www.bancoandre.com.br");
		driver.findElement(By.id("Formulario:rodapeEdita:salvar")).click();
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
