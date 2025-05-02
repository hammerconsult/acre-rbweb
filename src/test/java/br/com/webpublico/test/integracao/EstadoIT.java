/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.test.integracao;

import org.junit.Test;
import org.openqa.selenium.By;
import static org.junit.Assert.*;

/**
 *
 * @author brainiac
 */
public class EstadoIT extends BaseIT {

    private void chegarATela() {

        getWithUrlBase("/faces/tributario/cadastromunicipal/uf/lista.xhtml");
    }

    @Test
    public void testNovo() throws Exception {

        this.refreshDataSet("dataSetPais.xml");

        chegarATela();
        driver.findElement(By.id("listaEstado:formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.id("Formulario:nome")).clear();
        driver.findElement(By.id("Formulario:nome")).sendKeys("Estado Teste Incluir 2");
        driver.findElement(By.id("Formulario:uf")).clear();
        driver.findElement(By.id("Formulario:uf")).sendKeys("Ii");
        executeJavaScript("document.forms['Formulario'].elements['Formulario:pais_hinput'].value = '38'");
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();


        assertEquals("Salvo com Sucesso", driver.findElement(By.id("listaEstado:msgs")).getText());
    }

    @Test
    public void testAlterar() throws Exception {

        this.refreshListDataSets("dataSetPais.xml", "dataSetUf.xml");

        chegarATela();
        driver.findElement(By.id("listaEstado:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("listaEstado:formularioTabela:tabela:globalFilter")).sendKeys("TE");
        driver.findElement(By.id("listaEstado:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("listaEstado:fomularioVisualizar:botaoEditar")).click();
        driver.findElement(By.id("Formulario:nome")).clear();
        driver.findElement(By.id("Formulario:nome")).sendKeys("Estado Teste Alterado");
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();


        assertEquals("Salvo com Sucesso", driver.findElement(By.id("listaEstado:msgs")).getText());
    }

    @Test
    public void testExcluir() throws Exception {

        this.refreshListDataSets("dataSetPais.xml", "dataSetUf.xml");

        chegarATela();
        driver.findElement(By.id("listaEstado:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("listaEstado:formularioTabela:tabela:globalFilter")).sendKeys("ET");
        driver.findElement(By.id("listaEstado:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("listaEstado:fomularioVisualizar:botaoExcluir")).click();
        driver.switchTo().alert().accept();

    }
}
