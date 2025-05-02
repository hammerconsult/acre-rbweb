package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class PlanejamentoEstrategicoIT {

    private WebDriver driver;
    private String baseUrl = "";
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void testEPlanejamentoEstrategico() throws Exception {
        driver.get("http://localhost:8080/webpublico/");
        driver.findElement(By.cssSelector("input[type=submit]")).click();
        driver.findElement(By.xpath("//ul[@id='menuEsquerdo_menu']/li[7]/a/span/span")).click();
        driver.findElement(By.xpath("//ul[@id='menuEsquerdo_menu']/li[7]/ul/li[8]/a/span")).click();
        driver.switchTo().frame("corpo");
        driver.findElement(By.id("planejamentoEstrategico:formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.id("Formulario:descricao")).click();
        driver.findElement(By.id("Formulario:descricao")).clear();
        driver.findElement(By.id("Formulario:descricao")).sendKeys("Planjejamento Municipal");
        driver.findElement(By.id("Formulario:visao")).click();
        driver.findElement(By.id("Formulario:visao")).clear();
        driver.findElement(By.id("Formulario:visao")).sendKeys("Elevar os padrões de bem estar e");
        driver.findElement(By.id("Formulario:visao")).clear();
        driver.findElement(By.id("Formulario:visao")).sendKeys("Elevar os padrões de bem estar e melhoria continua...");
        driver.findElement(By.id("Formulario:missao")).click();
        driver.findElement(By.id("Formulario:visao")).click();
        driver.findElement(By.id("Formulario:visao")).clear();
        driver.findElement(By.id("Formulario:visao")).sendKeys("");
        driver.findElement(By.id("Formulario:missao")).click();
        driver.findElement(By.id("Formulario:missao")).clear();
        driver.findElement(By.id("Formulario:missao")).sendKeys("Elevar os padrões de bem estar e melhoria continua...");
        driver.findElement(By.id("Formulario:visao")).click();
        driver.findElement(By.id("Formulario:visao")).clear();
        driver.findElement(By.id("Formulario:visao")).sendKeys("Que o Município de Rio Branco tenha uma infancia sadia e feliz...");
        driver.findElement(By.id("Formulario:j_idt13_content")).click();
        driver.findElement(By.id("Formulario:valores")).click();
        driver.findElement(By.id("Formulario:valores")).clear();
        driver.findElement(By.id("Formulario:valores")).sendKeys("Promover a Justiça");
        driver.findElement(By.id("Formulario:valores")).clear();
        driver.findElement(By.id("Formulario:valores")).sendKeys("Promover a Justiça social e uma ética de convivência...");
        driver.findElement(By.id("Formulario:itemExer")).click();
        Select select = new Select(driver.findElement(By.xpath("//select")));
        select.selectByVisibleText("2007");
        driver.findElement(By.id("Formulario:botaoAdd")).click();
        new Select(driver.findElement(By.xpath("//select"))).selectByVisibleText("2008");
        driver.findElement(By.id("Formulario:botaoAdd")).click();
        new Select(driver.findElement(By.xpath("//select"))).selectByVisibleText("2009");
        driver.findElement(By.id("Formulario:botaoAdd")).click();
        new Select(driver.findElement(By.xpath("//select"))).selectByVisibleText("2010");
        driver.findElement(By.id("Formulario:botaoAdd")).click();
        driver.findElement(By.id("Formulario:inputNomeMacro")).clear();
        driver.findElement(By.id("Formulario:inputNomeMacro")).sendKeys("Desenvolvimento Sustentavel");
        driver.findElement(By.id("Formulario:botaoAddMacro")).click();
        driver.findElement(By.id("Formulario:inputItemNome")).clear();
        driver.findElement(By.id("Formulario:inputItemNome")).sendKeys("Item 1");
        driver.findElement(By.id("Formulario:inputDescricao")).clear();
        driver.findElement(By.id("Formulario:inputDescricao")).sendKeys("Item 1 Descricao");
        driver.findElement(By.id("Formulario:botaoAddItemPlanejamento")).click();
        driver.findElement(By.id("Formulario:inputItemNome")).clear();
        driver.findElement(By.id("Formulario:inputItemNome")).sendKeys("Item 2");
        driver.findElement(By.id("Formulario:inputDescricao")).clear();
        driver.findElement(By.id("Formulario:inputDescricao")).sendKeys("Item 2 descrição");
        new Select(driver.findElement(By.xpath("//select[@id='Formulario:itemSup']"))).selectByVisibleText("Item 1");
        driver.findElement(By.id("Formulario:botaoAddItemPlanejamento")).click();
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

        assertEquals("Salvo com Sucesso", driver.findElement(By.id("planejamentoEstrategico:msgs")).getText());
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
