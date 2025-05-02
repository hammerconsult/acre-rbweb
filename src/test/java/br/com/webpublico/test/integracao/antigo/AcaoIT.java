package br.com.webpublico.test.integracao.antigo;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class AcaoIT {

    private WebDriver driver;
    private String baseUrl = "";
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void testAcao() throws Exception {
        driver.get("http://localhost:8080/webpublico/");
        driver.findElement(By.cssSelector("input[type=submit]")).click();
        driver.findElement(By.xpath("//ul[@id='menuEsquerdo_menu']/li[7]/a/span/span")).click();
        driver.findElement(By.linkText("Ação")).click();
        driver.switchTo().frame("corpo");
        driver.findElement(By.id("listaAcao:formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.id("Formulario:codigo")).clear();
        driver.findElement(By.id("Formulario:codigo")).sendKeys("01");
        driver.findElement(By.id("Formulario:tipoAcao")).click();
        driver.findElement(By.id("Formulario:tipoAcao")).click();
        driver.findElement(By.id("Formulario:descricao")).click();
        driver.findElement(By.id("Formulario:descricao")).clear();
        driver.findElement(By.id("Formulario:descricao")).sendKeys("Melhorar a Saude");
        driver.findElement(By.id("Formulario:descricaoProduto")).click();
        driver.findElement(By.id("Formulario:descricaoProduto")).clear();
        driver.findElement(By.id("Formulario:descricaoProduto")).sendKeys("Vacinas para a população");
        driver.findElement(By.id("Formulario:totalFinanceiro")).click();
        driver.findElement(By.id("Formulario:totalFinanceiro")).clear();
        driver.findElement(By.id("Formulario:totalFinanceiro")).sendKeys("10000");
        driver.findElement(By.id("Formulario:totalFisico")).click();
        driver.findElement(By.id("Formulario:totalFisico")).clear();
        driver.findElement(By.id("Formulario:totalFisico")).sendKeys("1");

        ((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:funcao_hinput'].value = '7747'");

        //*[@id="Formulario:funcao_input"]

//        driver.findElement(By.id("Formulario:funcao_input")).click();
//        driver.findElement(By.id("Formulario:funcao_input")).clear();
//        driver.findElement(By.id("Formulario:funcao_input")).sendKeys("");
//        driver.findElement(By.id("ui-active-menuitem")).click();
//        driver.findElement(By.id("Formulario:subFuncao_input")).click();
//        driver.findElement(By.id("Formulario:subFuncao_input")).clear();
//        driver.findElement(By.id("Formulario:subFuncao_input")).sendKeys("");
//        driver.findElement(By.id("ui-active-menuitem")).click();
        ((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:subFuncao_hinput'].value = '7755'");
        new Select(driver.findElement(By.xpath("//*[@id='Formulario:programaPPA']"))).selectByVisibleText("Melhorar");

        // ERROR: Caught exception [ERROR: Unsupported command [select]]
        driver.findElement(By.id("Formulario:periodicidadeMedicao")).click();
        new Select(driver.findElement(By.xpath("//*[@id='Formulario:periodicidadeMedicao']"))).selectByVisibleText("Bimestral");
//        driver.findElement(By.cssSelector("option[value=7804]")).click();
        driver.findElement(By.id("Formulario:unidadeMedidaProduto")).click();
        new Select(driver.findElement(By.xpath("//*[@id='Formulario:unidadeMedidaProduto']"))).selectByVisibleText("Metro");
        //      driver.findElement(By.cssSelector("option[value=7778]")).click();
        driver.findElement(By.id("Formulario:andamento")).click();
        //  new Select(driver.findElement(By.xpath("//*[@id='Formulario:unidadeMedidaProduto']"))).selectByVisibleText("Nova");
        driver.findElement(By.cssSelector("option[value=NOVA]")).click();
        driver.findElement(By.cssSelector("span.ui-tree-node-label")).click();
        driver.findElement(By.xpath("//li[@id='Formulario:arvoreP_node_0']/div/span/span[2]")).click();
        driver.findElement(By.id("Formulario:j_idt54")).click();
        driver.findElement(By.cssSelector("span.ui-icon.ui-icon-pencil")).click();
        driver.findElement(By.id("Formulario:tabelaProvisao:0:j_idt69")).clear();
        driver.findElement(By.id("Formulario:tabelaProvisao:0:j_idt69")).sendKeys("2000");
        driver.findElement(By.id("Formulario:tabelaProvisao:0:j_idt73")).clear();
        driver.findElement(By.id("Formulario:tabelaProvisao:0:j_idt73")).sendKeys("2");
        driver.findElement(By.cssSelector("span.ui-icon.ui-icon-check")).click();
        driver.findElement(By.xpath("//span[@id='Formulario:tabelaProvisao:1:j_idt75']/span")).click();
        driver.findElement(By.id("Formulario:tabelaProvisao:1:j_idt69")).clear();
        driver.findElement(By.id("Formulario:tabelaProvisao:1:j_idt69")).sendKeys("2000");
        driver.findElement(By.id("Formulario:tabelaProvisao:1:j_idt73")).clear();
        driver.findElement(By.id("Formulario:tabelaProvisao:1:j_idt73")).sendKeys("3");
        driver.findElement(By.xpath("//span[@id='Formulario:tabelaProvisao:1:j_idt75']/span[2]")).click();
        driver.findElement(By.xpath("//span[@id='Formulario:tabelaProvisao:2:j_idt75']/span")).click();
        driver.findElement(By.id("Formulario:tabelaProvisao:2:j_idt69")).clear();
        driver.findElement(By.id("Formulario:tabelaProvisao:2:j_idt69")).sendKeys("2000");
        driver.findElement(By.id("Formulario:tabelaProvisao:2:j_idt73")).clear();
        driver.findElement(By.id("Formulario:tabelaProvisao:2:j_idt73")).sendKeys("4");
        driver.findElement(By.xpath("//span[@id='Formulario:tabelaProvisao:2:j_idt75']/span[2]")).click();
        driver.findElement(By.xpath("//span[@id='Formulario:tabelaProvisao:3:j_idt75']/span")).click();
        driver.findElement(By.id("Formulario:tabelaProvisao:3:j_idt69")).clear();
        driver.findElement(By.id("Formulario:tabelaProvisao:3:j_idt69")).sendKeys("2000");
        driver.findElement(By.id("Formulario:tabelaProvisao:3:j_idt73")).clear();
        driver.findElement(By.id("Formulario:tabelaProvisao:3:j_idt73")).sendKeys("5");
        driver.findElement(By.xpath("//span[@id='Formulario:tabelaProvisao:3:j_idt75']/span[2]")).click();
        driver.findElement(By.id("Formulario:valorFinanceiro")).click();
        driver.findElement(By.id("Formulario:valorFinanceiro")).clear();
        driver.findElement(By.id("Formulario:valorFinanceiro")).sendKeys("2500");
        driver.findElement(By.id("Formulario:valorFisico")).clear();
        driver.findElement(By.id("Formulario:valorFisico")).sendKeys("100");
        driver.findElement(By.id("Formulario:dataMedicao_input")).click();
        driver.findElement(By.linkText("12")).click();
        driver.findElement(By.id("Formulario:j_idt85")).click();
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();
        driver.findElement(By.xpath("//span[@id='Formulario:tabelaProvisao:3:j_idt75']/span")).click();
        driver.findElement(By.id("Formulario:tabelaProvisao:3:j_idt69")).click();
        driver.findElement(By.id("Formulario:tabelaProvisao:3:j_idt69")).clear();
        driver.findElement(By.id("Formulario:tabelaProvisao:3:j_idt69")).sendKeys("4000");
        driver.findElement(By.xpath("//span[@id='Formulario:tabelaProvisao:3:j_idt75']/span[2]")).click();
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

        driver.findElement(By.id("listaAcao:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("visualizarAcao:fomularioVisualizar:botaoExcluir")).click();
        driver.switchTo().alert().accept();
//        ((JavascriptExecutor) driver).executeScript("document.forms['visualizarAcao:fomularioVisualizar'].elements['visualizarAcao:fomularioVisualizar:botaoExcluir'].value = 'if(!confirm('Deseja excluir este registro?')) == true'");
        //*[@id="visualizarAcao:fomularioVisualizar:botaoExcluir"]
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
