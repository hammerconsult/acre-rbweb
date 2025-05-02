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
public class BancoIT extends BaseIT {

    private void chegarATela() {

        getWithUrlBase("/faces/tributario/cadastromunicipal/banco/lista.xhtml");
    }

    @Test
    public void testeNovo() throws Exception {

        chegarATela();
        driver.findElement(By.id("listabanco:formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.id("Formulario:descricao")).click();
        driver.findElement(By.id("Formulario:descricao")).clear();
        driver.findElement(By.id("Formulario:descricao")).sendKeys("BRT - Banco de Teste");
        driver.findElement(By.id("Formulario:numeroBanco")).clear();
        driver.findElement(By.id("Formulario:numeroBanco")).sendKeys("995");
        driver.findElement(By.id("Formulario:sigla")).clear();
        driver.findElement(By.id("Formulario:sigla")).sendKeys("1");
        driver.findElement(By.id("Formulario:rodapeEdita:salvar")).click();

        assertEquals("Salvo com Sucesso", driver.findElement(By.id("listabanco:msgs")).getText());

    }

    @Test
    public void testeAltera() throws Exception {

        this.refreshDataSet("dataSetBanco.xml");

        chegarATela();
        driver.findElement(By.id("listabanco:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("listabanco:formularioTabela:tabela:globalFilter")).sendKeys("Banco Teste Alterar");
        driver.findElement(By.id("listabanco:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("visualizarGenerico:fomularioVisualizar:botaoEditar")).click();
        driver.findElement(By.id("Formulario:sigla")).click();
        driver.findElement(By.id("Formulario:sigla")).clear();
        driver.findElement(By.id("Formulario:sigla")).sendKeys("1");
        driver.findElement(By.id("Formulario:rodapeEdita:salvar")).click();

    }

    @Test
    public void testeExcluir() throws Exception {

        this.refreshDataSet("dataSetBanco.xml");

        chegarATela();
        driver.findElement(By.id("listabanco:formularioTabela:tabela:globalFilter")).clear();
        driver.findElement(By.id("listabanco:formularioTabela:tabela:globalFilter")).sendKeys("Banco Teste Brasil");
        driver.findElement(By.id("listabanco:formularioTabela:tabela:0:visualizar")).click();
        driver.findElement(By.id("visualizarGenerico:fomularioVisualizar:botaoExcluir")).click();
        driver.switchTo().alert().accept();

    }
}
