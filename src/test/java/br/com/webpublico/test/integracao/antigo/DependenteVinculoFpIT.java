package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class DependenteVinculoFpIT {
	private WebDriver driver;
	private String baseUrl="";
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testEDependenteVinculoFP() throws Exception {
		driver.get("http://localhost:8080/webpublico/");
		driver.findElement(By.cssSelector("input[type=submit]")).click();
		driver.findElement(By.linkText("Recursos Humanos")).click();
		driver.findElement(By.linkText("Dependente Vínculo FP")).click();
		driver.switchTo().frame("corpo");
		driver.findElement(By.id("listaDependenteVinculoFP:formTabelaGenerica:botaoNovo")).click();
		driver.findElement(By.id("Formulario:inicioVigencia_input")).clear();
                driver.findElement(By.id("Formulario:inicioVigencia_input")).click();
		driver.findElement(By.linkText("1")).click();
		driver.findElement(By.id("Formulario:finalVigencia_input")).clear();
                driver.findElement(By.id("Formulario:finalVigencia_input")).click();
		driver.findElement(By.linkText("15")).click();
		driver.findElement(By.id("Formulario:dependente")).click();
		((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:dependente'].value = '8029'");
		driver.findElement(By.id("Formulario:vinculoFP")).click();
                ((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:vinculoFP'].value = '8041'");
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
