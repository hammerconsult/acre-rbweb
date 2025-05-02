package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.RGFAnexo05Item;
import br.com.webpublico.enums.*;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo05Facade;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mateus on 26/04/18.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo5", pattern = "/relatorio/rgf/anexo5/", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo05.xhtml")
})
public class RelatorioRGFAnexo05Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    @EJB
    private RelatorioRGFAnexo05Facade relatorioRGFAnexo05Facade;

    public RelatorioRGFAnexo05Controlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-rgf-anexo5", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO5_RGF;
        super.limparCampos();
    }

    public List<ParametrosRelatorios> montarParametros(String dataFinal) {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
        parametros.add(new ParametrosRelatorios(" trunc(SLD.DATASALDO) ", ":DATAFINAL", null, OperacaoRelatorio.MENOR_IGUAL, dataFinal, null, 1, true));
        parametros.add(new ParametrosRelatorios(" trunc(SLD.DATASALDO) ", ":DATAINICIAL", null, OperacaoRelatorio.MENOR_IGUAL, "01/01/" + exercicio.getAno(), null, 10, true));
        parametros.add(new ParametrosRelatorios(" trunc(SALDO.DATASALDO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), dataFinal, 3, true));
        parametros.add(new ParametrosRelatorios(" trunc(emp.dataempenho) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), dataFinal, 4, true));
        parametros.add(new ParametrosRelatorios(" trunc(est.dataestorno) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), dataFinal, 5, true));
        parametros.add(new ParametrosRelatorios(" trunc(liq.dataliquidacao) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), dataFinal, 6, true));
        parametros.add(new ParametrosRelatorios(" trunc(pag.datapagamento) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), dataFinal, 7, true));
        parametros.add(new ParametrosRelatorios(" trunc(re.datareceita) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), dataFinal, 8, true));
        parametros.add(new ParametrosRelatorios(" trunc(pg.datapagto) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), dataFinal, 9, true));
        parametros.add(new ParametrosRelatorios(" trunc(lanc.datalancamento) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), dataFinal, 12, true));
        parametros.add(new ParametrosRelatorios(" trunc(transf.DATATRANSFERENCIA) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), dataFinal, 11, true));
        parametros.add(new ParametrosRelatorios(" trunc(ajuste.DATAAJUSTE) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), dataFinal, 13, true));
        parametros.add(new ParametrosRelatorios(" trunc(LIB.DATALIBERACAO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), dataFinal, 14, true));
        if (esferaDoPoder != null) {
            parametros.add(new ParametrosRelatorios("vw.esferadopoder", ":esfera", null, OperacaoRelatorio.LIKE, esferaDoPoder.name(), null, 2, false));
        }
        return parametros;
    }

    private List<ParametrosRelatorios> montarParametrosRelatorio() {
        relatoriosItemDemonst = relatorioRGFAnexo05Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 5", exercicio, TipoRelatorioItemDemonstrativo.RGF);
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setParametros(montarParametros(getData()));
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setExercicio(exercicio);
        itemDemonstrativoFiltros.setApresentacaoRelatorio(ApresentacaoRelatorio.ORGAO);
        return itemDemonstrativoFiltros.getParametros();
    }

    public String getData() {
        return Util.getDiasMes(mes.getNumeroMes(), exercicio.getAno()) + "/" + mes.getNumeroMesString() + "/" + exercicio.getAno();
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        RelatoriosItemDemonst relatoriosItemDemonst = relatorioRGFAnexo05Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 5", exercicio, TipoRelatorioItemDemonstrativo.RGF);
        dto.setNomeParametroBrasao("IMAGEM");
        if (EsferaDoPoder.EXECUTIVO.equals(esferaDoPoder)) {
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER EXECUTIVO");
            dto.adicionarParametro("NOMERELATORIO", "DEMONSTRATIVO DA DISPONIBILIDADE DE CAIXA E DOS RESTOS A PAGAR");
        } else if (EsferaDoPoder.LEGISLATIVO.equals(esferaDoPoder)) {
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER LEGISLATIVO");
            dto.adicionarParametro("NOMERELATORIO", "DEMONSTRATIVO DA DISPONIBILIDADE DE CAIXA E DOS RESTOS A PAGAR");
        } else {
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("NOMERELATORIO", "DEMONSTRATIVO CONSOLIDADO DA DISPONIBILIDADE DE CAIXA E DOS RESTOS A PAGAR");
        }
        dto.adicionarParametro("ID_EXERCICIO", exercicio.getId());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno());
        dto.adicionarParametro("DATAINICIAL", getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", " " + mes.getDescricao().toUpperCase() + " DE " + exercicio.getAno());
        dto.adicionarParametro("relatoriosItemDemonstDTO", relatoriosItemDemonst.toDto());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros(getData())));
        dto.setNomeRelatorio("LRF-RGF-ANEXO-5-" + exercicio.getAno() + "-" + mes.getNumeroMesString() + "-" + (esferaDoPoder != null ? esferaDoPoder.getDescricao().substring(0, 1) : "C"));
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRGFAnexo05";
    }

    @Override
    public String getApi() {
        return "contabil/rgf-anexo5/";
    }

    private void limparHashs() {
        relatorioRGFAnexo05Facade.getRelatorioDDRFacade().limparHash();
        relatorioRGFAnexo05Facade.getRelatorioDDRFacade().limparHashItensRecuperados();
        relatorioRGFAnexo05Facade.limparHash();
        relatorioRGFAnexo05Facade.limparHashItensRecuperados();
    }

    private Mes getMesInicial() {
        if (Mes.ABRIL.equals(mes)) {
            return Mes.JANEIRO;
        } else if (Mes.AGOSTO.equals(mes)) {
            return Mes.MAIO;
        } else {
            return Mes.SETEMBRO;
        }
    }

    public List<RGFAnexo05Item> prepararDadosAnexo5(List<ParametrosRelatorios> parametros) {
        relatoriosItemDemonst = relatorioRGFAnexo05Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 5", exercicio, TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRGFAnexo05Facade.getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(exercicio, "", "Anexo 5", TipoRelatorioItemDemonstrativo.RGF);
        itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        limparHashs();
        List<ParametrosRelatorios> parametrosRelatorio = montarParametrosRelatorio();
        parametrosRelatorio.addAll(parametros);
        return prepararGrupo1(parametrosRelatorio);
    }

    private List<RGFAnexo05Item> prepararGrupo1(List<ParametrosRelatorios> parametros) {
        List<RGFAnexo05Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente it : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo05Facade.getItemDemonstrativoFacade().recuperaPorGrupo(it.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo05Item item = new RGFAnexo05Item();
                item.setDescricao(it.getDescricaoComEspacos());
                item.setValorColuna1(relatorioRGFAnexo05Facade.getRelatorioDDRFacade().calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_CAIXA_BRUTA));
                item.setValorColuna2(relatorioRGFAnexo05Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_A_PAGAR_PROCESSADOS_LIQUIDADOS_NAO_PAGOS_EXERCICIOS_ANTERIORES));
                item.setValorColuna3(relatorioRGFAnexo05Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_A_PAGAR_PROCESSADOS_LIQUIDADOS_NAO_PAGOS));
                item.setValorColuna4(relatorioRGFAnexo05Facade.calcularRestoEmpenhoNaoProcessadosExerciciosAnteriores(itemDemonstrativo, relatoriosItemDemonst, parametros));
                BigDecimal valorColuna5 = relatorioRGFAnexo05Facade.getRelatorioDDRFacade().calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_COMPROMETIDA_CONSIGNACAO);
                valorColuna5 = valorColuna5.add(relatorioRGFAnexo05Facade.getRelatorioDDRFacade().calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_COMPROMETIDA_COMPENSATORIAS));
                item.setValorColuna5(valorColuna5);
                item.setValorColuna6(BigDecimal.ZERO);
                item.setValorColuna7(item.getValorColuna1().subtract(item.getValorColuna2().add(item.getValorColuna3()).add(item.getValorColuna4()).add(item.getValorColuna5())));
                item.setValorColuna8(relatorioRGFAnexo05Facade.calcularRestoEmpenhoNaoProcessados(itemDemonstrativo, relatoriosItemDemonst, parametros));
                item.setValorColuna9(BigDecimal.ZERO);
                item.setValorColuna10(item.getValorColuna7().subtract(item.getValorColuna8()));
                toReturn.add(item);
            }
        }
        return toReturn;
    }
}
