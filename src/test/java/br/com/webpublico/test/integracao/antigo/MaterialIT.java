package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class MaterialIT {
	private WebDriver driver;
	private String baseUrl="";
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testEMaterial() throws Exception {
		driver.get("http://localhost:8080/webpublico/");
		driver.findElement(By.cssSelector("input[type=submit]")).click();
		driver.findElement(By.xpath("//ul[@id='menuEsquerdo_menu']/li[3]/a/span/span")).click();
		driver.findElement(By.id("materiais")).click();
		driver.switchTo().frame("corpo");
		driver.findElement(By.id("listaMaterial:formTabelaGenerica:botaoNovo")).click();
		driver.findElement(By.id("Formulario:descricao")).clear();
		driver.findElement(By.id("Formulario:descricao")).sendKeys("Novo Material Teste");
		driver.findElement(By.id("Formulario:codigo")).clear();
		driver.findElement(By.id("Formulario:codigo")).sendKeys("01234567");
		driver.findElement(By.id("Formulario:controleDeLote")).click();
		driver.findElement(By.id("Formulario:grupoMat_input")).clear();
		((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:grupoMat_hinput'].value = '7858'");
		driver.findElement(By.id("ui-active-menuitem")).click();
		driver.findElement(By.xpath("//li[@id='Formulario:arvoreGrupo_node_0']/div/span/span")).click();
		driver.findElement(By.xpath("//li[@id='Formulario:arvoreGrupo_node_0_4']/div/span/span[2]")).click();
		driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

                assertEquals("Salvo com Sucesso", driver.findElement(By.id("listaMaterial:msgs")).getText());
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
