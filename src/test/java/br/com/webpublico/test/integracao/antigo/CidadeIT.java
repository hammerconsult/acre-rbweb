package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class CidadeIT {
	private WebDriver driver;
	private String baseUrl="";
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testECidade() throws Exception {
		driver.get("http://localhost:8080/webpublico/");
		driver.findElement(By.cssSelector("input[type=submit]")).click();
		driver.findElement(By.xpath("//ul[@id='menuEsquerdo_menu']/li[2]/a/span/span")).click();
		driver.findElement(By.linkText("Cidade")).click();
		driver.switchTo().frame("corpo");
		driver.findElement(By.id("listaCidade:formTabelaGenerica:botaoNovo")).click();
		driver.findElement(By.id("Formulario:nome")).clear();
		driver.findElement(By.id("Formulario:nome")).sendKeys("Cidade Teste Andre");
                driver.findElement(By.id("Formulario:CEP")).clear();
		driver.findElement(By.id("Formulario:CEP")).sendKeys("86990-000");
                //driver.findElement(By.cssSelector("option[value=179]")).click();
                ((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:cidade_hinput'].value = '179'");
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
