/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.test.integracao;

import java.util.Calendar;
import org.dbunit.database.QueryDataSet;
import java.util.NoSuchElementException;
import org.dbunit.operation.DatabaseOperation;
import org.openqa.selenium.By;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gecen
 */
public class BairroIT extends BaseIT {

    private void chegarATela() {

        getWithUrlBase("/faces/tributario/cadastromunicipal/bairro/lista.xhtml");
    }

    @Test
    public void testNovo() throws Exception {

        this.refreshListDataSets("dataSetPais.xml", "dataSetUf.xml", "dataSetCidade.xml");
        chegarATela();
        driver.findElement(By.id("listaBairro:formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.id("Formulario:descricao")).click();
        driver.findElement(By.id("Formulario:descricao")).clear();
        driver.findElement(By.id("Formulario:descricao")).sendKeys("BAIRRO_INCLUIDO");
        executeJavaScript("document.forms['Formulario'].elements['Formulario:cidade_hinput'].value = '310840'");
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

        assertEquals("Salvo com Sucesso", driver.findElement(By.id("listaBairro:msgs")).getText());


        QueryDataSet dataSet = new QueryDataSet(connection);
        dataSet.addTable("BAIRRO", "SELECT * FROM BAIRRO WHERE DESCRICAO='BAIRRO_INCLUIDO'");
        DatabaseOperation.DELETE_ALL.execute(connection, dataSet);

    }


    @Test
    public void testAlterar() throws Exception {

        this.refreshDataSet("dataSetPais.xml");
        this.refreshDataSet("dataSetUf.xml");
        this.refreshDataSet("dataSetCidade.xml");
        this.refreshDataSet("dataSetBairro.xml");
        chegarATela();
        driver.findElement(By.id("listaBairro:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("listaBairro:formularioTabela:tabela:globalFilter")).sendKeys("Bairro TESTE1");




//        for (int second = 0;; second++) {
//            if (second >= 60) {
//                fail("timeout");
//            }
//            try {
//                if (isElementPresent(By.id("listaBairro:formularioTabela:tabela:0:visualizar"))) {
//                    break;
//                }
//            } catch (Exception e) {
//            }
//            Thread.sleep(1000);
//        }
//


        driver.findElement(By.id("listaBairro:formularioTabela:tabela:0:visualizar")).click();

        driver.findElement(By.id("fcView:fomularioVisualizar:botaoEditar")).click();
        driver.findElement(By.id("Formulario:descricao")).clear();
        driver.findElement(By.id("Formulario:descricao")).sendKeys("Bairro TESTE alterado");
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

        assertEquals("Salvo com Sucesso", driver.findElement(By.id("fcView:msgs")).getText());
    }

    @Test
    public void testExcluir() throws Exception {

        this.refreshDataSet("dataSetPais.xml");
        this.refreshDataSet("dataSetUf.xml");
        this.refreshDataSet("dataSetCidade.xml");
        this.refreshDataSet("dataSetBairro.xml");
        chegarATela();
        driver.findElement(By.id("listaBairro:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("listaBairro:formularioTabela:tabela:globalFilter")).sendKeys("Bairro TESTE2");
        driver.findElement(By.id("listaBairro:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("fcView:fomularioVisualizar:botaoExcluir")).click();
        executeJavaScript("window.confirm = function(msg) { return true; }");
        //driver.switchTo().alert().accept();

    }
}
