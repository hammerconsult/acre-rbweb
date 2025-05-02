package br.com.webpublico.test.integracao;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.JavascriptExecutor;
import org.junit.Test;
import org.openqa.selenium.By;
import static org.junit.Assert.*;

/**
 *
 * @author brainiac
 */
public class CidadeIT extends BaseIT {

    private void chegarATela() {

        getWithUrlBase("/faces/tributario/cadastromunicipal/cidade/lista.xhtml");
    }

    @Test
    public void testeNovo() throws Exception {

        this.refreshListDataSets("dataSetPais.xml", "dataSetUf.xml");

        chegarATela();
        driver.findElement(By.id("listaCidade:formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.id("Formulario:nome")).click();
        driver.findElement(By.id("Formulario:nome")).clear();
        driver.findElement(By.id("Formulario:nome")).sendKeys("Cidade Teste Inclusão 60");
        driver.findElement(By.id("Formulario:CEP")).clear();
        driver.findElement(By.id("Formulario:CEP")).sendKeys("99999-999");
        new Select(driver.findElement(By.xpath("//select[@id='Formulario:uf']"))).selectByVisibleText("SF");
        // executeJavaScript("document.forms['Formulario'].elements['Formulario:uf_hinput'].value = '608'");
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

        assertEquals("Salvo com Sucesso", driver.findElement(By.id("listaCidade:msgs")).getText());
    }

    protected boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @Test
    public void testAlterar() throws Exception {

        this.refreshListDataSets("dataSetPais.xml", "dataSetUf.xml", "dataSetCidade.xml");

        chegarATela();
        driver.findElement(By.id("listaCidade:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("listaCidade:formularioTabela:tabela:globalFilter")).sendKeys("Cidade Test Alteração");

        for (int second = 0;; second++) {
            if (second >= 60) {
                fail("timeout");
            }
            try {
                if (isElementPresent(By.id("listaCidade:formularioTabela:tabela:0:visualizar"))) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
        driver.findElement(By.id("listaCidade:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("visualizaCidade:fomularioVisualizar:botaoEditar")).click();
        driver.findElement(By.id("Formulario:nome")).clear();
        driver.findElement(By.id("Formulario:nome")).sendKeys("Cidade teste Alterado");
        driver.findElement(By.id("Formulario:CEP")).click();
        driver.findElement(By.id("Formulario:CEP")).sendKeys("99999-100");
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

        assertEquals("Salvo com Sucesso", driver.findElement(By.id("visualizaCidade:msgs")).getText());
    }

    @Test
    public void testExcluir() throws Exception {

        this.refreshListDataSets("dataSetPais.xml", "dataSetUf.xml", "dataSetCidade.xml");

        chegarATela();
        driver.findElement(By.id("listaCidade:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("listaCidade:formularioTabela:tabela:globalFilter")).sendKeys("Cidade Test Exclusão");
        for (int second = 0;; second++) {
            if (second >= 60) {
                fail("timeout");
            }
            try {
                if (isElementPresent(By.id("listaCidade:formularioTabela:tabela:0:visualizar"))) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        driver.findElement(By.id("listaCidade:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("visualizaCidade:fomularioVisualizar:botaoExcluir")).click();
        driver.switchTo().alert().accept();

    }
}
