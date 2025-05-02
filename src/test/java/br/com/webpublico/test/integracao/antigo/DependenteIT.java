package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class DependenteIT {
	private WebDriver driver;
	private String baseUrl="";
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testEDependente() throws Exception {
		driver.get("http://localhost:8080/webpublico/");
		driver.findElement(By.cssSelector("input[type=submit]")).click();
		driver.findElement(By.linkText("Recursos Humanos")).click();
		driver.findElement(By.linkText("Dependente")).click();
		driver.switchTo().frame("corpo");
		driver.findElement(By.id("listaDependente:formTabelaGenerica:botaoNovo")).click();
		driver.findElement(By.id("Formulario:responsavel")).click();
                ((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:responsavel'].value = '5468'");
		driver.findElement(By.id("Formulario:dependente")).click();
		((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:dependente'].value = '5466'");
		driver.findElement(By.id("Formulario:grauDeParentesco")).click();
		((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:grauDeParentesco'].value = '7963'");
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
