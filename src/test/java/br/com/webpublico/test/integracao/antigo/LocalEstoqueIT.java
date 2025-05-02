package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class LocalEstoqueIT {
	private WebDriver driver;
	private String baseUrl="";
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testELocalEstoque() throws Exception {
		driver.get("http://localhost:8080/webpublico/");
		driver.findElement(By.cssSelector("input[type=submit]")).click();
		driver.findElement(By.linkText("Materiais")).click();
		driver.findElement(By.id("localEstoque")).click();
		driver.switchTo().frame("corpo");
		driver.findElement(By.id("listaLocalEstoque:formTabelaGenerica:botaoNovo")).click();
		driver.findElement(By.id("Formulario:descricao")).clear();
		driver.findElement(By.id("Formulario:descricao")).sendKeys("Local Estoque Teste");
		driver.findElement(By.id("Formulario:codigo")).clear();
		driver.findElement(By.id("Formulario:codigo")).sendKeys("45687");
		driver.findElement(By.id("Formulario:fechadoEm_input")).click();
		driver.findElement(By.linkText("27")).click();
		driver.findElement(By.id("Formulario:superior_input")).click();
		driver.findElement(By.id("Formulario:superior_input")).clear();
		((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:superior_hinput'].value = '7868'");
		//driver.findElement(By.id("ui-active-menuitem")).click();
		driver.findElement(By.xpath("//li[@id='Formulario:arvoreSup_node_0']/div/span/span")).click();
		//driver.findElement(By.xpath("//li[@id='Formulario:arvoreSup_node_0_0']/div/span/span[2]")).click();
		driver.findElement(By.id("Formulario:unidadeOrg_input")).click();
		((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:unidadeOrg_hinput'].value = '7714'");
		driver.findElement(By.xpath("//li[@id='Formulario:arvoreUnidOrg_node_0']/div/span")).click();
		driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

                assertEquals("Salvo com Sucesso", driver.findElement(By.id("listaLocalEstoque:msgs")).getText());
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
