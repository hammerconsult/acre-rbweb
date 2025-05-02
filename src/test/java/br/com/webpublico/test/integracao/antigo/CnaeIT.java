package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class CnaeIT {
	private WebDriver driver;
	private String baseUrl="";
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testECnae() throws Exception {
		driver.get("http://localhost:8080/webpublico/");
		driver.findElement(By.cssSelector("input[type=submit]")).click();
		driver.findElement(By.linkText("Cadastros Gerais")).click();
		driver.findElement(By.linkText("CNAE")).click();
		driver.switchTo().frame("corpo");
		driver.findElement(By.id("listaCnae:formTabelaGenerica:botaoNovo")).click();
                driver.findElement(By.id("Formulario:codigoCnae")).clear();
		driver.findElement(By.id("Formulario:codigoCnae")).sendKeys("01.23-4/56");
		driver.findElement(By.id("Formulario:DescricaoDetalhada")).clear();
		driver.findElement(By.id("Formulario:DescricaoDetalhada")).sendKeys("teste CNAE ANDRE");
		driver.findElement(By.id("Formulario:AliquotaIssHomologado")).clear();
		driver.findElement(By.id("Formulario:AliquotaIssHomologado")).sendKeys("24");
		driver.findElement(By.id("Formulario:AliquotaIssFixo")).clear();
		driver.findElement(By.id("Formulario:AliquotaIssFixo")).sendKeys("25");
		driver.findElement(By.id("Formulario:AliquotaAlvara")).clear();
		driver.findElement(By.id("Formulario:AliquotaAlvara")).sendKeys("30");
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
