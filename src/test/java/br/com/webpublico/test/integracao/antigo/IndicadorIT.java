package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class IndicadorIT {
    private WebDriver driver;
    private String baseUrl = "";
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void testIndicador() throws Exception {
        driver.get("http://localhost:8080/webpublico/");
        driver.findElement(By.cssSelector("input[type=submit]")).click();
        driver.findElement(By.xpath("//ul[@id='menuEsquerdo_menu']/li[7]/a/span/span")).click();
        driver.findElement(By.linkText("Indicador")).click();
        driver.switchTo().frame("corpo");
        driver.findElement(By.id("j_idt7:formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.id("Formulario:descricao")).click();
        driver.findElement(By.id("Formulario:descricao")).clear();
        driver.findElement(By.id("Formulario:descricao")).sendKeys("Descricaoq");
        driver.findElement(By.id("Formulario:formula")).clear();
        driver.findElement(By.id("Formulario:formula")).sendKeys("Formula");
        driver.findElement(By.id("Formulario:fonte")).clear();
        driver.findElement(By.id("Formulario:fonte")).sendKeys("Fonte de dados");
        Select select = new Select(driver.findElement(By.xpath("//select")));
        select.selectByVisibleText("Centímetro");

        //driver.findElement(By.cssSelector("option[value=Centímetro]")).click();
        driver.findElement(By.id("Formulario:valor")).click();
        driver.findElement(By.id("Formulario:valor")).clear();
        driver.findElement(By.id("Formulario:valor")).sendKeys("10");
        driver.findElement(By.id("Formulario:apurado_input")).click();
        driver.findElement(By.linkText("17")).click();
        driver.findElement(By.id("Formulario:j_idt30")).click();
        driver.findElement(By.id("Formulario:valor")).clear();
        driver.findElement(By.id("Formulario:valor")).sendKeys("60");
        driver.findElement(By.id("Formulario:apurado_input")).click();
        driver.findElement(By.linkText("23")).click();
        driver.findElement(By.id("Formulario:j_idt30")).click();
        driver.findElement(By.cssSelector("span.ui-tree-node-label")).click();
        driver.findElement(By.id("Formulario:j_idt47:salvar")).click();
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
