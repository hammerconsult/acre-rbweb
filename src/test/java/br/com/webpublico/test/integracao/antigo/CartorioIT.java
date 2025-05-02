package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class CartorioIT {

    private WebDriver driver;
    private String baseUrl = "";
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void testECartorio() throws Exception {
        driver.get("http://localhost:8080/webpublico/");
        driver.findElement(By.cssSelector("input[type=submit]")).click();
        driver.findElement(By.linkText("Cadastros Gerais")).click();
        driver.findElement(By.linkText("Cartório")).click();
        driver.switchTo().frame("corpo");
        driver.findElement(By.id("listaCartorio:formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.id("Formulario:nome")).click();
        driver.findElement(By.id("Formulario:nome")).clear();
        driver.findElement(By.id("Formulario:nome")).sendKeys("Cartório do Testes");
        ((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:editaendereco:logradouro_hinput'].value = '5608'");
        driver.findElement(By.id("Formulario:editaendereco:numero")).clear();
        driver.findElement(By.id("Formulario:editaendereco:numero")).sendKeys("555");
        driver.findElement(By.id("Formulario:editaendereco:complemento")).clear();
        driver.findElement(By.id("Formulario:editaendereco:complemento")).sendKeys("complemento teste");
        driver.findElement(By.cssSelector("option[value=RESIDENCIAL]")).click();
        driver.findElement(By.id("Formulario:rodapeeditar:salvar")).click();
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
