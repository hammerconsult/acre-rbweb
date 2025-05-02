/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.test.integracao;

import org.openqa.selenium.NoSuchElementException;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.*;

/**
 *
 * @author brainiac
 */
public class LoaIT extends BaseIT {

    private void chegarATela() {

        getWithUrlBase("/faces/financeiro/ppa/loa/lista.xhtml");
    }

    @Test
    public void testNovo() throws Exception {

        this.refreshDataSet("dataSetExercicio.xml");
        this.refreshDataSet("dataSetAtoLegal.xml");
        this.refreshDataSet("dataSetPlanejamentoEstrategico.xml");
        this.refreshDataSet("dataSetPPA.xml");
        this.refreshDataSet("dataSetLDO.xml");

        chegarATela();
        driver.findElement(By.id("ListaLOA:formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.id("Formulario:ldo")).click();
        new Select(driver.findElement(By.xpath("//select[@id='Formulario:ldo']"))).selectByVisibleText("1.0 - 2012");
        driver.findElement(By.id("Formulario:atoLegal_input")).click();
        driver.findElement(By.id("Formulario:atoLegal_input")).clear();
        executeJavaScript("document.forms['Formulario'].elements['Formulario:atoLegal_hinput'].value = '443498'");
        driver.findElement(By.id("Formulario:aprovacao_input")).click();
        driver.findElement(By.linkText("1")).click();
        driver.findElement(By.id("Formulario:versao")).click();
        driver.findElement(By.id("Formulario:versao")).clear();
        driver.findElement(By.id("Formulario:versao")).sendKeys("2.92");
        driver.findElement(By.id("Formulario:valorDespesa")).clear();
        driver.findElement(By.id("Formulario:valorDespesa")).sendKeys("100");
        driver.findElement(By.id("Formulario:valorReceita")).click();
        driver.findElement(By.id("Formulario:valorReceita")).clear();
        driver.findElement(By.id("Formulario:valorReceita")).sendKeys("100");
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

        assertEquals("Salvo com Sucesso", driver.findElement(By.id("ListaLOA:msgs")).getText());
    }

    @Test
    public void testAlterar() throws Exception {

        this.refreshDataSet("dataSetExercicio.xml");
        this.refreshDataSet("dataSetAtoLegal.xml");
        this.refreshDataSet("dataSetPlanejamentoEstrategico.xml");
        this.refreshDataSet("dataSetPPA.xml");
        this.refreshDataSet("dataSetLDO.xml");
        this.refreshDataSet("dataSetLOA.xml");

        chegarATela();
        driver.findElement(By.id("ListaLOA:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("ListaLOA:formularioTabela:tabela:globalFilter")).sendKeys("4.0");
        driver.findElement(By.id("ListaLOA:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("rodapeVizualizar:fomularioVisualizar:botaoEditar")).click();
        driver.findElement(By.id("Formulario:valorReceita")).clear();
        driver.findElement(By.id("Formulario:valorReceita")).sendKeys("2000");
        driver.findElement(By.id("Formulario:valorDespesa")).click();
        driver.findElement(By.id("Formulario:valorDespesa")).clear();
        driver.findElement(By.id("Formulario:valorDespesa")).sendKeys("2000");
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

//        this.deleteDataSet("dataSetLOA.xml");
//        this.deleteDataSet("dataSetLDO.xml");
//        this.deleteDataSet("dataSetPPA.xml");
//        this.deleteDataSet("dataSetPlanejamentoEstrategico.xml");
//        this.deleteDataSet("dataSetAtoLegal.xml");
//        this.deleteDataSet("dataSetExercicio.xml");
    }

    @Test
    public void testExcluir() throws Exception {

        this.refreshDataSet("dataSetExercicio.xml");
        this.refreshDataSet("dataSetAtoLegal.xml");
        this.refreshDataSet("dataSetPlanejamentoEstrategico.xml");
        this.refreshDataSet("dataSetPPA.xml");
        this.refreshDataSet("dataSetLDO.xml");
        this.refreshDataSet("dataSetLOA.xml");

        chegarATela();
        driver.findElement(By.id("ListaLOA:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("ListaLOA:formularioTabela:tabela:globalFilter")).sendKeys("3.0");
        driver.findElement(By.id("ListaLOA:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("rodapeVizualizar:fomularioVisualizar:botaoExcluir")).click();
        driver.switchTo().alert().accept();

//        this.deleteDataSet("dataSetLOA.xml");
//        this.deleteDataSet("dataSetLDO.xml");
//        this.deleteDataSet("dataSetPPA.xml");
//        this.deleteDataSet("dataSetPlanejamentoEstrategico.xml");
//        this.deleteDataSet("dataSetAtoLegal.xml");
//        this.deleteDataSet("dataSetExercicio.xml");
//
    }
}
