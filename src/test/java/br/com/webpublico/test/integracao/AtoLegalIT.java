/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.test.integracao;

import org.dbunit.database.QueryDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 *
 * @author brainiac
 */
public class AtoLegalIT extends BaseIT {

    private void chegarATela() {

        getWithUrlBase("/faces/tributario/cadastromunicipal/atolegal/lista.xhtml");
    }

    @Test
    public void testNovo() throws Exception {

        this.refreshDataSet("dataSetExercicio.xml");
        this.refreshDataSet("dataSetEsferaDeGoverno.xml");

        chegarATela();
        driver.findElement(By.id("listaAtoLegal:formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.id("Formulario:esferaGoverno")).click();
        driver.findElement(By.cssSelector("option[value=\"299022\"]")).click();
        driver.findElement(By.id("Formulario:numero")).click();
        driver.findElement(By.id("Formulario:numero")).clear();
        driver.findElement(By.id("Formulario:numero")).sendKeys("123");
        driver.findElement(By.id("Formulario:dataPromulgacao_input")).click();
        driver.findElement(By.linkText("1")).click();
        driver.findElement(By.id("Formulario:dataPublicacao_input")).click();
        driver.findElement(By.linkText("10")).click();
        driver.findElement(By.id("Formulario:nome")).click();
        driver.findElement(By.id("Formulario:nome")).clear();
        driver.findElement(By.id("Formulario:nome")).sendKeys("Teste Ato Legal");
        driver.findElement(By.id("Formulario:propositoAtoLegal")).click();
        driver.findElement(By.cssSelector("option[value=\"ORCAMENTO\"]")).click();
        driver.findElement(By.id("Formulario:tipoAtoLegal")).click();
        driver.findElement(By.cssSelector("option[value=\"LEI\"]")).click();
        driver.findElement(By.id("Formulario:salvar")).click();

        QueryDataSet dataSet = new QueryDataSet(connection);
        dataSet.addTable("ATOLEGAL", "SELECT * FROM ATOLEGAL WHERE NOME='Teste Ato Legal'");
        DatabaseOperation.DELETE.execute(connection, dataSet);
    }

    @Test
    public void testAlterar() throws Exception {

        this.refreshDataSet("dataSetExercicio.xml");
        this.refreshDataSet("dataSetEsferaDeGoverno.xml");
        this.refreshDataSet("dataSetAtoLegal.xml");

        chegarATela();
        driver.findElement(By.id("listaAtoLegal:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("listaAtoLegal:formularioTabela:tabela:globalFilter")).sendKeys("Teste Ato Legal Alterar");
        driver.findElement(By.id("listaAtoLegal:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("visualizaAtoLegal:fomularioVisualizar:botaoEditar")).click();
        driver.findElement(By.id("Formulario:nome")).click();
        driver.findElement(By.id("Formulario:nome")).clear();
        driver.findElement(By.id("Formulario:nome")).sendKeys("Teste Ato Legal Alterar");
        driver.findElement(By.id("Formulario:dataPromulgacao_input")).click();
        driver.findElement(By.linkText("7")).click();
        driver.findElement(By.id("Formulario:dataPromulgacao_input")).click();
        driver.findElement(By.cssSelector("span.ui-icon.ui-icon-circle-triangle-w")).click();
        driver.findElement(By.id("Formulario:dataPromulgacao_input")).click();
        driver.findElement(By.linkText("30")).click();
        driver.findElement(By.id("Formulario:salvar")).click();

       // this.deleteDataSet("dataSetAtoLegal.xml");
    }

    @Test
    public void testExcluir() throws Exception {

        this.refreshDataSet("dataSetExercicio.xml");
        this.refreshDataSet("dataSetEsferaDeGoverno.xml");
        this.refreshDataSet("dataSetAtoLegal.xml");

        chegarATela();
        driver.findElement(By.id("listaAtoLegal:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("listaAtoLegal:formularioTabela:tabela:globalFilter")).sendKeys("Teste Ato Legal Excluir");
        driver.findElement(By.id("listaAtoLegal:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("visualizaAtoLegal:fomularioVisualizar:botaoExcluir")).click();
        driver.switchTo().alert().accept();

       // this.deleteDataSet("dataSetAtoLegal.xml");
    }
}
