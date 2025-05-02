package br.com.webpublico.test.integracao.antigo;

import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class UsuarioIT {
	private WebDriver driver;
	private String baseUrl="";
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testEUsuario() throws Exception {
		driver.get("http://localhost:8080/webpublico/");
		driver.findElement(By.cssSelector("input[type=submit]")).click();
		driver.findElement(By.xpath("//ul[@id='menuEsquerdo_menu']/li[2]/a/span/span")).click();
		driver.findElement(By.linkText("Pessoa")).click();
		driver.findElement(By.cssSelector("#usuario > span.wijmo-wijmenu-text")).click();
		driver.switchTo().frame("corpo");
		driver.findElement(By.id("listaUsuario:botaoNovo")).click();
		driver.findElement(By.id("Formulario:Login")).click();
		driver.findElement(By.id("Formulario:Login")).clear();
		driver.findElement(By.id("Formulario:Login")).sendKeys("andre");
		driver.findElement(By.id("Formulario:Senha_input")).clear();
                driver.findElement(By.id("Formulario:Senha_input")).sendKeys("senha10");
		driver.findElement(By.id("Formulario:RepeteSenha_input")).clear();
		driver.findElement(By.id("Formulario:RepeteSenha_input")).sendKeys("senha10");
		driver.findElement(By.id("Formulario:pessoaFisica_input")).clear();
                ((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:pessoaFisica_hinput'].value = '5464'");
		driver.findElement(By.id("Formulario:expira")).click();
		driver.findElement(By.id("Formulario:dataExpiracao_input")).clear();
                driver.findElement(By.id("Formulario:dataExpiracao_input")).click();
		driver.findElement(By.linkText("29")).click();
		driver.findElement(By.xpath("//tr[@id='Formulario:gruposUsuario_row_10']/td/input")).click();
		driver.findElement(By.xpath("//tr[@id='Formulario:gruposUsuario_row_6']/td/input")).click();
		driver.findElement(By.xpath("//tr[@id='Formulario:periodos_row_1']/td/input")).click();
		driver.findElement(By.xpath("//tr[@id='Formulario:periodos_row_2']/td/input")).click();
		driver.findElement(By.xpath("//tr[@id='Formulario:periodos_row_3']/td/input")).click();
		driver.findElement(By.xpath("//tr[@id='Formulario:periodos_row_4']/td/input")).click();
		driver.findElement(By.xpath("//tr[@id='Formulario:periodos_row_5']/td/input")).click();
		driver.findElement(By.xpath("//tr[@id='Formulario:periodos_row_9']/td/input")).click();
		driver.findElement(By.xpath("//tr[@id='Formulario:periodos_row_10']/td/input")).click();
                driver.findElement(By.cssSelector("span.ui-tree-node-label")).click();
		driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

                assertEquals("Salvo com Sucesso", driver.findElement(By.id("listaUsuario:msgs")).getText());
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
