/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.test.integracao;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.*;

/**
 *
 * @author brainiac
 */
public class LdoIT extends BaseIT {

    private void chegarATela() {

        getWithUrlBase("/faces/financeiro/ppa/ldo/lista.xhtml");
    }

    @Test
    public void testNovo() throws Exception {

        refreshDataSet("dataSetExercicio.xml");
        refreshDataSet("dataSetEsferaDeGoverno.xml");
        refreshDataSet("dataSetAtoLegal.xml");
        refreshDataSet("dataSetPlanejamentoEstrategico.xml");
        refreshDataSet("dataSetPPA.xml");

        chegarATela();
        driver.findElement(By.id("ListaLDO:formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.id("Formulario:ppa_input")).click();
        driver.findElement(By.id("Formulario:ppa_input")).clear();
        driver.findElement(By.xpath("//input[@id='Formulario:ppa_input']")).sendKeys("PPA 2010");
        driver.findElement(By.xpath("//a[contains(text(),'PPA 2010-2013 1')]")).click();
        driver.findElement(By.cssSelector("option[value=\"13756\"]")).click();
        driver.findElement(By.id("Formulario:atoLegal_input")).click();
        driver.findElement(By.id("Formulario:atoLegal_input")).clear();
        driver.findElement(By.xpath("//input[@id='Formulario:atoLegal_input']")).sendKeys("ld");
        driver.findElement(By.xpath("//a[contains(text(),'LDO')]")).click();
        driver.findElement(By.id("Formulario:aprovacao_input")).click();
        driver.findElement(By.linkText("1")).click();
        driver.findElement(By.id("Formulario:versao")).click();
        driver.findElement(By.id("Formulario:versao")).clear();
        driver.findElement(By.id("Formulario:versao")).sendKeys("1.0");
        driver.findElement(By.xpath("//table[@id='Formulario:provisoes']/tbody/tr/td/div/div[2]")).click();
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

        assertEquals("Salvo com Sucesso", driver.findElement(By.id("ListaLDO:msgs")).getText());

    }

    @Test
    public void testAltera() throws Exception {

        refreshDataSet("dataSetExercicio.xml");
        refreshDataSet("dataSetEsferaDeGoverno.xml");
        refreshDataSet("dataSetAtoLegal.xml");
        refreshDataSet("dataSetPlanejamentoEstrategico.xml");
        refreshDataSet("dataSetPPA.xml");
        refreshDataSet("dataSetLDO.xml");

        chegarATela();
        driver.findElement(By.id("ListaLDO:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("ListaLDO:formularioTabela:tabela:globalFilter")).sendKeys("ppa 2010-2013");
        driver.findElement(By.id("ListaLDO:formularioTabela:tabela:exercicio_filter")).clear();
        driver.findElement(By.id("ListaLDO:formularioTabela:tabela:exercicio_filter")).sendKeys("2013");
        driver.findElement(By.id("ListaLDO:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("Visualizar:fomularioVisualizar:botaoEditar")).click();
        driver.findElement(By.xpath("//div[@id='Formulario:painel_content']/table/tbody/tr[2]/td[2]")).click();
        driver.findElement(By.id("Formulario:exercicio")).click();
        driver.findElement(By.cssSelector("option[value=\"13756\"]")).click();
        driver.findElement(By.id("Formulario:versao")).click();
        driver.findElement(By.id("Formulario:versao")).clear();
        driver.findElement(By.id("Formulario:versao")).sendKeys("1.1");
        driver.findElement(By.xpath("//table[@id='Formulario:provisoes']/tbody/tr[2]/td/div/div[2]")).click();
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

        assertEquals("Salvo com Sucesso", driver.findElement(By.id("Visualizar:msgs")).getText());

    }

    @Test
    public void testExcluir() throws Exception {

        refreshDataSet("dataSetExercicio.xml");
        refreshDataSet("dataSetEsferaDeGoverno.xml");
        refreshDataSet("dataSetAtoLegal.xml");
        refreshDataSet("dataSetPlanejamentoEstrategico.xml");
        refreshDataSet("dataSetPPA.xml");
        refreshDataSet("dataSetLDO.xml");

        chegarATela();
        driver.findElement(By.id("ListaLDO:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("ListaLDO:formularioTabela:tabela:globalFilter")).sendKeys("ppa 2010-2013");
        driver.findElement(By.id("ListaLDO:formularioTabela:tabela:exercicio_filter")).clear();
        driver.findElement(By.id("ListaLDO:formularioTabela:tabela:exercicio_filter")).sendKeys("2010");
        driver.findElement(By.id("ListaLDO:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("Visualizar:fomularioVisualizar:botaoExcluir")).click();
        driver.switchTo().alert().accept();
    }
}
