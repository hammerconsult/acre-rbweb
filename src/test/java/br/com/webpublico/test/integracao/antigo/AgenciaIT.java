package br.com.webpublico.test.integracao.antigo;

import com.thoughtworks.selenium.DefaultSelenium;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class AgenciaIT extends TestCase {

    private WebDriver driver;
    private String baseUrl = "http://localhost:8080/webpublico/";
    private StringBuffer verificationErrors = new StringBuffer();
    private DefaultSelenium selenium;

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        // driver = new HtmlUnitDriver(true);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

    }

    @Test
    public void testAgencia() throws Exception {
        driver.get("http://localhost:8080/webpublico/");
        driver.findElement(By.cssSelector("input[type=submit]")).click();
        driver.findElement(By.xpath("//ul[@id='menuEsquerdo_menu']/li[2]/a/span/span")).click();
        driver.findElement(By.id("agencia")).click();

        driver.switchTo().frame("corpo");
        driver.findElement(By.id("listaGenerica:formTabelaGenerica:botaoNovo")).click();
        //soluçõo para preencher autocomplete.
        ((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:banco_hinput'].value = '6940'");
        //  driver.findElement(By.id("Formulario:banco_input")).sendKeys("BBS - Banco Bonsucesso S.A");

        driver.findElement(By.id("Formulario:numeroAgencia")).click();
        driver.findElement(By.id("Formulario:numeroAgencia")).clear();
        driver.findElement(By.id("Formulario:numeroAgencia")).sendKeys("123");
        driver.findElement(By.id("Formulario:digitoVerificador")).clear();
        driver.findElement(By.id("Formulario:digitoVerificador")).sendKeys("1");
        driver.findElement(By.id("Formulario:nomeAgencia")).clear();
        driver.findElement(By.id("Formulario:nomeAgencia")).sendKeys("Agencia do Gecen");
        ((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:editaEndereco:logradouro_hinput'].value = '5604'");

        driver.findElement(By.id("Formulario:editaEndereco:numero")).click();
        driver.findElement(By.id("Formulario:editaEndereco:numero")).clear();
        driver.findElement(By.id("Formulario:editaEndereco:numero")).sendKeys("555");
        driver.findElement(By.id("Formulario:editaEndereco:complemento")).click();
        driver.findElement(By.id("Formulario:editaEndereco:complemento")).clear();
        driver.findElement(By.id("Formulario:editaEndereco:complemento")).sendKeys("31 B");
        // ERROR: Caught exception [ERROR: Unsupported command [select]]
        driver.findElement(By.cssSelector("option[value=RESIDENCIAL]")).click();
        driver.findElement(By.id("Formulario:rodape:salvar")).click();



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
