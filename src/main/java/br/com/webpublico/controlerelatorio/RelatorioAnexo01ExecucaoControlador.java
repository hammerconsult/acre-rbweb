/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.EsferaOrcamentaria;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author reidocrime
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-lei4320-anexo1", pattern = "/relatorio/lei4320/anexo1/", viewId = "/faces/financeiro/relatorio/relatorioanexo1loaexecucao.xhtml"),
})

@ManagedBean
public class RelatorioAnexo01ExecucaoControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    private EsferaOrcamentaria esferaOrcamentaria;
    private IntraOrcamentario intra;

    @URLAction(mappingId = "relatorio-lei4320-anexo1", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        intra = IntraOrcamentario.CONSOLIDADO;
        esferaOrcamentaria = EsferaOrcamentaria.ORCAMENTOGERAL;
        portalTipoAnexo = PortalTipoAnexo.ANEXO1_LEI_4320;
        super.limparCampos();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioLEI4320Anexo01";
    }

    @Override
    public String getApi() {
        return "contabil/lei4320-anexo1/";
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("EXERCICIO", exercicio.getId());
        dto.adicionarParametro("FILTRO_DATA", getDescricaoMes());
        dto.adicionarParametro("FILTRO_UG", unidadeGestora != null ? unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao() : "");
        dto.adicionarParametro("ESFERA_ORCAMENTARIA", esferaOrcamentaria.getDescricao().substring(4, esferaOrcamentaria.getDescricao().length()).trim());
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
        dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
        dto.setNomeRelatorio("LEI-4320-ANEXO-01");
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = "";
        filtrosParametroDatas(parametros);
        filtrosParametrosUnidade(parametros);
        filtrosParametrosGerais(parametros);
        if (!esferaOrcamentaria.equals(EsferaOrcamentaria.ORCAMENTOGERAL)) {
            parametros.add(new ParametrosRelatorios(" PPF.esferaOrcamentaria ", ":esfera", null, OperacaoRelatorio.LIKE, esferaOrcamentaria.name(), null, 2, false));
            parametros.add(new ParametrosRelatorios(" PPF.esferaOrcamentaria ", ":esfera", null, OperacaoRelatorio.LIKE, esferaOrcamentaria.name(), null, 3, false));
            parametros.add(new ParametrosRelatorios(" RECFONTE.esferaOrcamentaria ", ":esfera", null, OperacaoRelatorio.LIKE, esferaOrcamentaria.name(), null, 5, false));
            parametros.add(new ParametrosRelatorios(" RECFONTE.esferaOrcamentaria ", ":esfera", null, OperacaoRelatorio.LIKE, esferaOrcamentaria.name(), null, 6, false));
        }
        filtros = " Esfera Orçamentária: " + esferaOrcamentaria.getDescricao().substring(4, esferaOrcamentaria.getDescricao().length()).trim() + " -" + filtros;
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    private void filtrosParametroDatas(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(" trunc(EMP.DATAEMPENHO)", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 2, true));
        parametros.add(new ParametrosRelatorios(" trunc(EST.DATAESTORNO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 3, true));
        parametros.add(new ParametrosRelatorios(" trunc(LANC.DATALANCAMENTO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 5, true));
        parametros.add(new ParametrosRelatorios(" trunc(EST.DATAESTORNO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicial(), getDataFinal(), 6, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, OperacaoRelatorio.BETWEEN, getDataFinal(), null, 0, true));
    }

    public List<SelectItem> getEsferasOrcamentarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (EsferaOrcamentaria eo : EsferaOrcamentaria.values()) {
            toReturn.add(new SelectItem(eo, eo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getOperacoesOrcamentarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (IntraOrcamentario io : IntraOrcamentario.values()) {
            toReturn.add(new SelectItem(io, io.getDescricao()));
        }
        return toReturn;
    }

    public EsferaOrcamentaria getEsferaOrcamentaria() {
        return esferaOrcamentaria;
    }

    public void setEsferaOrcamentaria(EsferaOrcamentaria esferaOrcamentaria) {
        this.esferaOrcamentaria = esferaOrcamentaria;
    }

    public IntraOrcamentario getIntra() {
        return intra;
    }

    public void setIntra(IntraOrcamentario intra) {
        this.intra = intra;
    }

    private enum IntraOrcamentario {
        INTRA("Intra-Orçamentário"),
        EXCETOINTRA("Exceto Intra-Orçamentário"),
        CONSOLIDADO("Consolidado");
        private String descricao;

        IntraOrcamentario(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
