package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class LoteMaterialIT {
	private WebDriver driver;
	private String baseUrl="";
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testELoteMaterial() throws Exception {
		driver.get("http://localhost:8080/webpublico/");
		driver.findElement(By.cssSelector("input[type=submit]")).click();
		driver.findElement(By.linkText("Materiais")).click();
		driver.findElement(By.id("loteMaterial")).click();
		driver.switchTo().frame("corpo");
		driver.findElement(By.id("listaLoteMaterial:formTabelaGenerica:botaoNovo")).click();
		driver.findElement(By.id("Formulario:identificacao")).click();
		driver.findElement(By.id("Formulario:identificacao")).clear();
		driver.findElement(By.id("Formulario:identificacao")).sendKeys("Lite de Teste");
		driver.findElement(By.id("Formulario:validade")).clear();
                driver.findElement(By.id("Formulario:validade")).click();
                driver.findElement(By.linkText("10")).click();
		driver.findElement(By.id("Formulario:quantidade")).click();
		driver.findElement(By.id("Formulario:quantidade")).clear();
		driver.findElement(By.id("Formulario:quantidade")).sendKeys("50");
		driver.findElement(By.id("Formulario:material_input")).clear();
		((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:material_hinput'].value = '7888'");
		//driver.findElement(By.id("ui-active-menuitem")).click();
		driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

                //assertEquals("Salvo com Sucesso", driver.findElement(By.id("listaLoteMaterial:msgs")).getText());
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
