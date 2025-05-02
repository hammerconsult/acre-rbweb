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
public class ReceitaLoaIT extends BaseIT {

    private void chegarATela() {

        getWithUrlBase("/faces/financeiro/ppa/receitaloa/lista.xhtml");

    }

    @Test
    public void testNovo() throws Exception {

        this.refreshDataSet("dataSetExercicio.xml");
        this.refreshDataSet("dataSetPlanejamentoEstrategico.xml");
        this.refreshDataSet("dataSetExercicioPlanoEstrategico.xml");
        this.refreshDataSet("dataSetItemPlanoEstrategico.xml");
        this.refreshDataSet("dataSetMacroObjetivoEstrategico.xml");
        this.refreshDataSet("dataSetAtoLegal.xml");
        this.refreshDataSet("dataSetPPA.xml");
        this.refreshDataSet("dataSetLDO.xml");
        this.refreshDataSet("dataSetLOA.xml");
        this.refreshDataSet("dataSetTipoConta.xml");
        this.refreshDataSet("dataSetPlanoDeContas.xml");
        this.refreshDataSet("dataSetConta.xml");
        this.refreshDataSet("dataSetPlanoDeContasExec.xml");

        chegarATela();
        driver.findElement(By.id("Fomulario:tabela:globalFilter")).clear();
        driver.findElement(By.id("Fomulario:tabela:globalFilter")).sendKeys("2012");
        driver.findElement(By.id("Fomulario:tabela:0:visualizar")).click();
        driver.findElement(By.xpath("//input[@id='Formulario:contaDeReceita_input']")).clear();
        driver.findElement(By.xpath("//input[@id='Formulario:contaDeReceita_input']")).sendKeys("receita tri");
        driver.findElement(By.xpath("//a[contains(text(),'1.1.00.00.00.00.00  RECEITA TRIBUTÁRIA')]")).click();
        driver.findElement(By.xpath("//input[@id='Formulario:entidade_input']")).clear();
        driver.findElement(By.xpath("//input[@id='Formulario:entidade_input']")).sendKeys("prefeitura");
        driver.findElement(By.xpath("//a[contains(text(),'Prefeitura do Município de Rio Branco')]")).click();
        driver.findElement(By.id("Formulario:Botao")).click();
        driver.findElement(By.id("Formulario:tabelaReceitas:0:Receita")).click();
        driver.findElement(By.id("formDialogo:fonteDeRecursos_input")).click();
        driver.findElement(By.xpath("//input[@id='formDialogo:fonteDeRecursos_input']")).sendKeys("sem");
        driver.findElement(By.xpath("//a[contains(text(),'0.1.00.000000 - Sem Detalhamento da Destinação de Recursos')]")).click();
        driver.findElement(By.id("formDialogo:valor")).click();
        driver.findElement(By.id("formDialogo:valor")).clear();
        driver.findElement(By.id("formDialogo:valor")).sendKeys("100");
        driver.findElement(By.id("formDialogo:Botao")).click();
        driver.findElement(By.cssSelector("span.ui-icon.ui-icon-closethick")).click();
        driver.findElement(By.id("Formulario:rodape:salvar")).click();


        assertEquals("Salvo com Sucesso", driver.findElement(By.id("msgs")).getText());

    }

    @Test
    public void testExcluir() throws Exception {

        this.refreshDataSet("dataSetExercicio.xml");
        this.refreshDataSet("dataSetPlanejamentoEstrategico.xml");
        this.refreshDataSet("dataSetExercicioPlanoEstrategico.xml");
        this.refreshDataSet("dataSetItemPlanoEstrategico.xml");
        this.refreshDataSet("dataSetMacroObjetivoEstrategico.xml");
        this.refreshDataSet("dataSetAtoLegal.xml");
        this.refreshDataSet("dataSetPPA.xml");
        this.refreshDataSet("dataSetLDO.xml");
        this.refreshDataSet("dataSetLOA.xml");
        this.refreshDataSet("dataSetTipoConta.xml");
        this.refreshDataSet("dataSetPlanoDeContas.xml");
        this.refreshDataSet("dataSetConta.xml");
        this.refreshDataSet("dataSetPlanoDeContasExec.xml");

        chegarATela();
        driver.findElement(By.id("Fomulario:tabela:globalFilter")).clear();
        driver.findElement(By.id("Fomulario:tabela:globalFilter")).sendKeys("2012");
        driver.findElement(By.id("Fomulario:tabela:0:visualizar")).click();
        driver.findElement(By.id("Formulario:tabelaReceitas:0:Remover")).click();
        driver.findElement(By.id("Formulario:rodape:salvar")).click();

        assertEquals("Salvo com Sucesso", driver.findElement(By.id("msgs")).getText());

    }
}
