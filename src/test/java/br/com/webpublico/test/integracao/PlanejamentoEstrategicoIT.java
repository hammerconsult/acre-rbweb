/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.test.integracao;

import org.dbunit.database.QueryDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import static org.junit.Assert.*;

/**
 *
 * @author brainiac
 */
public class PlanejamentoEstrategicoIT extends BaseIT {

    private void chegarATela() {

        getWithUrlBase("/faces/financeiro/ppa/planejamentoestrategico/lista.xhtml");
    }

    @Test
    public void TesteNovo() throws Exception {

        this.refreshDataSet("dataSetExercicio.xml");

        chegarATela();
        driver.findElement(By.id("planejamentoEstrategico:formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.id("Formulario:descricao")).click();
        driver.findElement(By.id("Formulario:descricao")).clear();
        driver.findElement(By.id("Formulario:descricao")).sendKeys("Planejamento Estratégico 1990-1993");
        driver.findElement(By.id("Formulario:visao")).click();
        driver.findElement(By.id("Formulario:visao")).click();
        driver.findElement(By.id("Formulario:visao")).clear();
        driver.findElement(By.id("Formulario:visao")).sendKeys("Planejamento Estratégico 1990-1993 Visão");
        driver.findElement(By.id("Formulario:missao")).clear();
        driver.findElement(By.id("Formulario:missao")).sendKeys("Planejamento Estratégico 1990-1993 Missão");
        driver.findElement(By.id("Formulario:valores")).clear();
        driver.findElement(By.id("Formulario:valores")).sendKeys("Planejamento Estratégico 1990-1993 Valores");
        driver.findElement(By.id("Formulario:itemExer")).click();
        new Select(driver.findElement(By.xpath("//select[@id='Formulario:itemExer']"))).selectByVisibleText("1990");
        driver.findElement(By.id("Formulario:botaoAdd")).click();
        new Select(driver.findElement(By.xpath("//select[@id='Formulario:itemExer']"))).selectByVisibleText("1991");
        driver.findElement(By.id("Formulario:botaoAdd")).click();
        new Select(driver.findElement(By.xpath("//select[@id='Formulario:itemExer']"))).selectByVisibleText("1992");
        driver.findElement(By.id("Formulario:botaoAdd")).click();
        new Select(driver.findElement(By.xpath("//select[@id='Formulario:itemExer']"))).selectByVisibleText("1993");
        driver.findElement(By.id("Formulario:botaoAdd")).click();
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

        assertEquals("Salvo com Sucesso", driver.findElement(By.id("planejamentoEstrategico:msgs")).getText());

        QueryDataSet dataSet = new QueryDataSet(connection);
        dataSet.addTable("EXERCPLANOESTRATEGICO", "SELECT * FROM EXERCPLANOESTRATEGICO WHERE PLANEJAMENTOESTRATEGICO_ID = 483436");
        DatabaseOperation.DELETE.execute(connection, dataSet);

        dataSet.addTable("PLANEJAMENTOESTRATEGICO", "SELECT * FROM PLANEJAMENTOESTRATEGICO WHERE DESCRICAO='Planejamento Estratégico 1990-1993'");
        DatabaseOperation.DELETE.execute(connection, dataSet);
    }

    @Test
    public void TesteAlterar() throws Exception {

        this.refreshDataSet("dataSetPlanejamentoEstrategico.xml");

        chegarATela();
        driver.findElement(By.id("planejamentoEstrategico:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("planejamentoEstrategico:formularioTabela:tabela:globalFilter")).sendKeys("teste");
        driver.findElement(By.id("planejamentoEstrategico:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("visualizarPlanejamento:fomularioVisualizar:botaoEditar")).click();
        driver.findElement(By.id("Formulario:visao")).click();
        driver.findElement(By.id("Formulario:visao")).clear();
        driver.findElement(By.id("Formulario:visao")).sendKeys("Teste");
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

        assertEquals("Salvo com Sucesso", driver.findElement(By.id("visualizarPlanejamento:msgs")).getText());

        this.deleteDataSet("dataSetPlanejamentoEstrategico.xml");
    }

    @Test
    public void TesteExcluir() throws Exception {

        this.refreshDataSet("dataSetPlanejamentoEstrategico.xml");

        chegarATela();
        driver.findElement(By.id("planejamentoEstrategico:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("planejamentoEstrategico:formularioTabela:tabela:globalFilter")).sendKeys("teste");
        driver.findElement(By.id("planejamentoEstrategico:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("visualizarPlanejamento:fomularioVisualizar:botaoExcluir")).click();
        driver.switchTo().alert().accept();


        this.deleteDataSet("dataSetPlanejamentoEstrategico.xml");
    }
}
