package br.com.webpublico.test.integracao.antigo;

import com.thoughtworks.selenium.Selenium;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class LeiIT {
	private WebDriver driver;
	private String baseUrl="";
	private StringBuffer verificationErrors = new StringBuffer();
	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testELei() throws Exception {
		driver.get("http://localhost:8080/webpublico/");
		driver.findElement(By.cssSelector("input[type=submit]")).click();
		driver.findElement(By.linkText("Cadastros Gerais")).click();
		driver.findElement(By.linkText("Atos Legais (Lei)")).click();
		driver.switchTo().frame("corpo");
		driver.findElement(By.id("listaLei:formTabelaGenerica:botaoNovo")).click();
		driver.findElement(By.id("Formulario:esferaGoverno")).click();
		((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:esferaGoverno'].value = '7553'");
		driver.findElement(By.id("Formulario:numero")).click();
		driver.findElement(By.id("Formulario:numero")).clear();
		driver.findElement(By.id("Formulario:numero")).sendKeys("007");
		driver.findElement(By.id("Formulario:dataPromulgacao_input")).click();
		driver.findElement(By.linkText("16")).click();
		driver.findElement(By.id("Formulario:dataPublicacao_input")).clear();
                driver.findElement(By.id("Formulario:dataPublicacao_input")).click();
		driver.findElement(By.linkText("18")).click();
		driver.findElement(By.id("Formulario:nome")).clear();
		driver.findElement(By.id("Formulario:nome")).sendKeys("Lei Teste");
		((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:tipoLei'].value = 'DIRETRIZES_ORCAMENTARIAS'");

                //Abaixo corresponde ao botao anexo
                driver.findElement(By.id("Formulario:anexarArquivo")).click();
		driver.findElement(By.id("formularioUpload:descricaoAnexo")).clear();
		driver.findElement(By.id("formularioUpload:descricaoAnexo")).sendKeys("Anexo Teste");

                Selenium selenium = new WebDriverBackedSelenium(driver, baseUrl);
                driver.findElement(By.xpath("//div[@id='formularioUpload:botaoSelecionar_input']/div[2]/button")).click();
                selenium.attachFile("formularioUpload", "http://3.bp.blogspot.com/_-iWxrow4jHo/TCaUgqsYaQI/AAAAAAAACww/fN3HNrw1wo0/s320/nego_bom.jpg");
		driver.findElement(By.cssSelector("td.ui-fileupload-start > button.ui-state-default.ui-corner-all")).click();

		driver.findElement(By.id("formularioUpload:fecharAnexo")).click();
		driver.findElement(By.id("Formulario:salvar")).click();
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
