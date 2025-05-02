/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.test.integracao;

import org.dbunit.operation.DatabaseOperation;
import org.dbunit.database.QueryDataSet;
import org.openqa.selenium.By;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author brainiac
 */
public class AcaoIT extends BaseIT {

    private void chegarATela() {

        getWithUrlBase("/faces/financeiro/ppa/acao/lista.xhtml");
    }

    @Test
    public void TesteNovo() throws Exception {

        this.refreshDataSet("dataSetExercicio.xml");
        this.refreshDataSet("dataSetEsferaDeGoverno.xml");
        this.refreshDataSet("dataSetAtoLegal.xml");
        this.refreshDataSet("dataSetUnidadeMedida.xml");
        this.refreshDataSet("dataSetUnidadeOrganizacional.xml");
        this.refreshDataSet("dataSetPlanejamentoEstrategico.xml");
        this.refreshDataSet("dataSetItemPlanoEstrategico.xml");
        this.refreshDataSet("dataSetMacroObjetivoEstrategico.xml");
        this.refreshDataSet("dataSetPPA.xml");
        this.refreshDataSet("dataSetProgramaPPA.xml");
        this.refreshDataSet("dataSetFuncao.xml");
        this.refreshDataSet("dataSetSubFuncao.xml");
        this.refreshDataSet("dataSetPeriodicidade.xml");

        chegarATela();
        driver.findElement(By.id("formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.cssSelector("option[value=\"ATIVIDADE\"]")).click();
        driver.findElement(By.id("Formulario:descricao")).click();
        driver.findElement(By.id("Formulario:descricao")).clear();
        driver.findElement(By.id("Formulario:descricao")).sendKeys("Teste");
        driver.findElement(By.id("Formulario:descricaoProduto")).click();
        driver.findElement(By.id("Formulario:descricaoProduto")).clear();
        driver.findElement(By.id("Formulario:descricaoProduto")).sendKeys("AAAAAAAAAAA");
        driver.findElement(By.id("Formulario:totalFinanceiro")).clear();
        driver.findElement(By.id("Formulario:totalFinanceiro")).sendKeys("R$ 100,00");
        driver.findElement(By.id("Formulario:totalFisico")).click();
        driver.findElement(By.id("Formulario:totalFisico")).clear();
        driver.findElement(By.id("Formulario:totalFisico")).sendKeys("100");
        driver.findElement(By.id("Formulario:funcao_input")).click();
        driver.findElement(By.xpath("//input[@id='Formulario:funcao_input']")).clear();
        driver.findElement(By.xpath("//input[@id='Formulario:funcao_input']")).sendKeys("legis");
        driver.findElement(By.xpath("//a[contains(text(),'1 - LEGISLATIVA')]")).click();
        driver.findElement(By.xpath("//input[@id='Formulario:subFuncao_input']")).click();
        driver.findElement(By.xpath("//input[@id='Formulario:subFuncao_input']")).clear();
        driver.findElement(By.xpath("//input[@id='Formulario:subFuncao_input']")).sendKeys("31 - AÇÃO LEGISLATIVA");
        driver.findElement(By.cssSelector("option[value=\"451250\"]")).click();
        driver.findElement(By.id("Formulario:periodicidadeMedicao")).click();
        driver.findElement(By.cssSelector("option[value=\"299668\"]")).click();
        driver.findElement(By.id("Formulario:unidadeMedidaProduto")).click();
        driver.findElement(By.cssSelector("option[value=\"299402\"]")).click();
        driver.findElement(By.cssSelector("option[value=\"NOVA\"]")).click();
        driver.findElement(By.xpath("//li[@id='Formulario:arvoreUni_node_0']/div/span/span")).click();
        driver.findElement(By.xpath("//li[@id='Formulario:arvoreUni_node_0_0']/div/span/span[2]")).click();
        driver.findElement(By.xpath("//span[@id='Formulario:tabelaProvisao:0:Editor']/span")).click();
        driver.findElement(By.id("Formulario:tabelaProvisao:0:metaFinanceira")).clear();
        driver.findElement(By.id("Formulario:tabelaProvisao:0:metaFinanceira")).sendKeys("100");
        driver.findElement(By.id("Formulario:tabelaProvisao:0:metaFisica")).clear();
        driver.findElement(By.id("Formulario:tabelaProvisao:0:metaFisica")).sendKeys("100");
        driver.findElement(By.xpath("//span[@id='Formulario:tabelaProvisao:0:Editor']/span[2]")).click();
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();


        assertEquals("Salvo com Sucesso", driver.findElement(By.id("formTabelaGenerica:msgs")).getText());

//        QueryDataSet dataSet = new QueryDataSet(connection);
//        dataSet.addTable("ACAOPPA", "SELECT * FROM ACAOPPA WHERE DESCRICAO='Teste'");
//        DatabaseOperation.DELETE.execute(connection, dataSet);
    }

    @Test
    public void TesteAlterar() throws Exception {

        this.refreshDataSet("dataSetExercicio.xml");
        this.refreshDataSet("dataSetEsferaDeGoverno.xml");
        this.refreshDataSet("dataSetAtoLegal.xml");
        this.refreshDataSet("dataSetUnidadeMedida.xml");
        this.refreshDataSet("dataSetUnidadeOrganizacional.xml");
        this.refreshDataSet("dataSetPlanejamentoEstrategico.xml");
        this.refreshDataSet("dataSetItemPlanoEstrategico.xml");
        this.refreshDataSet("dataSetMacroObjetivoEstrategico.xml");
        this.refreshDataSet("dataSetPPA.xml");
        this.refreshDataSet("dataSetProgramaPPA.xml");
        this.refreshDataSet("dataSetFuncao.xml");
        this.refreshDataSet("dataSetSubFuncao.xml");
        this.refreshDataSet("dataSetPeriodicidade.xml");
        this.refreshDataSet("dataSetAcaoPPA.xml");

        chegarATela();
        driver.findElement(By.id("listaAcao:tabela:Busca_filter")).clear();
        driver.findElement(By.id("listaAcao:tabela:Busca_filter")).sendKeys("teste");
        driver.findElement(By.id("listaAcao:tabela:0:imgVisualizar")).click();
        driver.findElement(By.id("visualizarAcao:fomularioVisualizar:botaoEditar")).click();
        driver.findElement(By.id("Formulario:descricao")).click();
        driver.findElement(By.id("Formulario:descricao")).clear();
        driver.findElement(By.id("Formulario:descricao")).sendKeys("Teste Alterado");
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();

        assertEquals("Salvo com Sucesso", driver.findElement(By.id("visualizarAcao:msgs")).getText());
    }

    @Test
    public void TesteExcluir() throws Exception {

        this.refreshDataSet("dataSetExercicio.xml");
        this.refreshDataSet("dataSetEsferaDeGoverno.xml");
        this.refreshDataSet("dataSetAtoLegal.xml");
        this.refreshDataSet("dataSetUnidadeMedida.xml");
        this.refreshDataSet("dataSetUnidadeOrganizacional.xml");
        this.refreshDataSet("dataSetPlanejamentoEstrategico.xml");
        this.refreshDataSet("dataSetItemPlanoEstrategico.xml");
        this.refreshDataSet("dataSetMacroObjetivoEstrategico.xml");
        this.refreshDataSet("dataSetPPA.xml");
        this.refreshDataSet("dataSetProgramaPPA.xml");
        this.refreshDataSet("dataSetFuncao.xml");
        this.refreshDataSet("dataSetSubFuncao.xml");
        this.refreshDataSet("dataSetPeriodicidade.xml");
        this.refreshDataSet("dataSetAcaoPPA.xml");

        chegarATela();
        driver.findElement(By.id("listaAcao:tabela:Busca_filter")).clear();
        driver.findElement(By.id("listaAcao:tabela:Busca_filter")).sendKeys("Teste Alterado");
        driver.findElement(By.id("listaAcao:tabela:0:imgVisualizar")).click();
        driver.findElement(By.id("visualizarAcao:fomularioVisualizar:botaoExcluir")).click();
        driver.switchTo().alert().accept();
    }
}
