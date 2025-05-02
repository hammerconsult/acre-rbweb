package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class PlanoDeContasIT {

    private WebDriver driver;
    private String baseUrl = "http://localhost:8080/webpublico/";
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void testPlanoDeContas() throws Exception {
        driver.get("http://localhost:8080/webpublico/");
        driver.findElement(By.cssSelector("input[type=submit]")).click();
        driver.findElement(By.xpath("//ul[@id='menuEsquerdo_menu']/li[4]/a/span/span")).click();
        driver.findElement(By.cssSelector("#planoDeContas > span.wijmo-wijmenu-text")).click();
        driver.switchTo().frame("corpo");
        driver.findElement(By.id("listaPlano:formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.id("Formulario:descricao")).click();
        driver.findElement(By.id("Formulario:descricao")).clear();
        driver.findElement(By.id("Formulario:descricao")).sendKeys("Plano de Contas Teste");
        driver.findElement(By.id("Formulario:tipoConta")).click();
        Select select = new Select(driver.findElement(By.xpath("//select")));
        select.selectByVisibleText("Conta Despesa");

        /// ERROR: Caught exception [ERROR: Unsupported command [select]]
        ///driver.findElement(By.cssSelector("option[value=7729]")).click();
        driver.findElement(By.id("Formulario:noSuperior")).click();
        driver.findElement(By.id("Formulario2:codigo")).clear();
        driver.findElement(By.id("Formulario2:codigo")).sendKeys("01.000.000");
        driver.findElement(By.id("Formulario2:descricao")).clear();
        driver.findElement(By.id("Formulario2:descricao")).sendKeys("Conta Descricao Teste");
        driver.findElement(By.id("Formulario2:ativa")).click();
        driver.findElement(By.id("Formulario2:permitirDesdobramento")).click();
        driver.findElement(By.id("Formulario2:novaConta")).submit();
        //acessar um elemento de uma lista. obs nao funciona.
        //driver.findElement(By.id("Formulario:tabela:0:addConta")).click();

        driver.findElement(By.id("Formulario:noSuperior")).click();
    //solução para dialog. "nao muiot elegante"
        driver.switchTo().activeElement().click();
        driver.findElement(By.id("Formulario2:codigo")).clear();
        driver.findElement(By.id("Formulario2:codigo")).sendKeys("01.001.000");
        driver.findElement(By.id("Formulario2:descricao")).clear();
        driver.findElement(By.id("Formulario2:descricao")).sendKeys("Conta filha 1 geracao");
        driver.findElement(By.id("Formulario2:ativa")).click();
        driver.findElement(By.id("Formulario2:permitirDesdobramento")).click();
        driver.findElement(By.id("Formulario2:novaConta")).submit();
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();


        assertEquals("Salvo com Sucesso", driver.findElement(By.id("listaPlano:msgs")).getText());
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
