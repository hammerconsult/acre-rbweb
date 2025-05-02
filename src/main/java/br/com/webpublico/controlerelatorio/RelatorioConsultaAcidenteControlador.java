package br.com.webpublico.controlerelatorio;


import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoAcidente;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by israeleriston on 17/11/15.
 * Modified by carlos on 18/11/15.
 */
@ManagedBean(name = "relatorioConsultaAcidenteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoConsultaAcidente",
        pattern = "/rh/consulta-de-acidentes/",
        viewId = "/faces/rh/relatorios/relatorioconsultaacidente.xhtml")
})
public class RelatorioConsultaAcidenteControlador extends AbstractReport implements Serializable {

    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    private ConverterAutoComplete converterAutoCompleteContratoFP;
    private ConverterAutoComplete converterAutoCompleteUnidadeOrganizacional;

    private ContratoFP contratoFP;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    private Boolean isMostrarPanelServidor;
    private Boolean isMostrarPanelUnidade;

    private String filtroPesquisa;
    private Date dataInicio;
    private Date dataFim;
    private String filtros;

    private TipoAcidente tipoAcidente;
    public static final String ACIDENTE_PERCUSO_TRABALHO = "relatorioAcidentePercursoAndTrabalho.jasper";
    public static final String ACIDENTE_PERCUSO = "relatorioAcidenteTrajeto.jasper";
    public static final String ACIDENTE_TRABALHO = "RelatorioAcidenteTrabalho-Consulta.jasper";

    @URLAction(mappingId = "novoConsultaAcidente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        isMostrarPanelServidor = false;
        isMostrarPanelUnidade = false;
        dataInicio = null;
        dataFim = null;
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.recuperaContratoMatricula(parte);
    }

    public List<HierarquiaOrganizacional> completarUnidadeOrganizacional(String filter) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(filter.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getSistemaFacade().getDataOperacao());
    }

    public void renderizarOpcaoSelecionada() {
        inicializarAtributos();
        if (isSemFiltro()) {
            naoExibir();
            filtroPesquisa = new String();
        } else if (isPesquisaUnidade()) {
            exibirUnidade();
        } else if (isPesquisaServidor()) {
            exibirServidor();
        }
    }

    private void inicializarAtributos() {
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        contratoFP = new ContratoFP();
    }

    private boolean isPesquisaServidor() {
        return filtroPesquisa.equals(TipoPesquisa.PESQUISAR_SERVIDOR.name());
    }

    private boolean isPesquisaUnidade() {
        return filtroPesquisa.equals(TipoPesquisa.PESQUISAR_UNIDADE.name());
    }

    private boolean isSemFiltro() {
        return filtroPesquisa == null;
    }

    private void exibirServidor() {
        isMostrarPanelServidor = true;
        isMostrarPanelUnidade = false;
    }

    private void exibirUnidade() {
        isMostrarPanelUnidade = true;
        isMostrarPanelServidor = false;
    }

    private void naoExibir() {
        isMostrarPanelUnidade = false;
        isMostrarPanelServidor = false;
    }

    public List<SelectItem> filtroPesquisa() {
        return Util.getListSelectItem(TipoPesquisa.values());
    }

    public List<SelectItem> tipoAcidentes() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (TipoAcidente acidente : TipoAcidente.values()) {
            retorno.add(new SelectItem(acidente, acidente.getDescricao()));
        }

        return retorno;
    }

    public void emitir() {
        setGeraNoDialog(Boolean.TRUE);
        HashMap parameters = new HashMap();
        if (isValidaCampo()) {
            try {
                if (tipoAcidente.equals(TipoAcidente.TODOS)) {
                    parameters.put("IMAGEM", super.getCaminhoImagem());
                    parameters.put("WHERE_TRABALHO", isCriterioTrabalho());
                    parameters.put("WHERE_TRAJETO", isCriterioTrajeto());
                    parameters.put("DATA_INICIO", dataInicio);
                    parameters.put("DATA_FINAL", dataFim);

                    gerarRelatorio(ACIDENTE_PERCUSO_TRABALHO, parameters);
                }

                if (tipoAcidente.equals(TipoAcidente.TRABALHO)) {
                    parameters.put("IMAGEM", super.getCaminhoImagem());
                    parameters.put("WHERE_TRABALHO", isCriterioTrabalho());
                    parameters.put("DATA_INICIO", dataInicio);
                    parameters.put("DATA_FINAL", dataFim);

                    gerarRelatorio(ACIDENTE_TRABALHO, parameters);
                }

                if (tipoAcidente.equals(TipoAcidente.PERCURSO)) {
                    parameters.put("IMAGEM", super.getCaminhoImagem());
                    parameters.put("WHERE_TRAJETO", isCriterioTrajeto());
                    parameters.put("DATA_INICIO", dataInicio);
                    parameters.put("DATA_FINAL", dataFim);

                    gerarRelatorio(ACIDENTE_PERCUSO, parameters);
                }
            } catch (Exception e) {
                FacesUtil.addErroAoGerarRelatorio("");
            }
        }
    }

    public String isCriterioTrabalho() {
        StringBuilder sb = new StringBuilder(" WHERE 1 = 1 ");
        String juncao = " and ";
        filtros = "";
        sb.append(juncao).append("ATRABALHO.OCORRIDOEM BETWEEN TO_DATE('" + DataUtil.getDataFormatada(dataInicio) +
            "', 'dd/mm/yyyy') AND TO_DATE('" + DataUtil.getDataFormatada(dataFim) + "', 'dd/mm/yyyy')").append("").append(" ");

        if (contratoFP.getId() != null) {
            sb.append(juncao).append("ATRABALHO.CONTRATOFP_ID = " + contratoFP.getId()).append("").append(" ");
        }

        if (contratoFP.getId() == null && hierarquiaOrganizacional.getId() != null) {
            sb.append(juncao).append("HO.CODIGO LIKE  '" + hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%'").append("").append(" ");
        }

        return sb.toString();
    }

    public String isCriterioTrajeto() {
        StringBuilder sb = new StringBuilder(" WHERE 1 = 1 ");
        String juncao = " and ";
        filtros = "";
        sb.append(juncao).append("ATRAJETO.OCORRIDOEM BETWEEN TO_DATE('" + DataUtil.getDataFormatada(dataInicio) +
            "', 'dd/mm/yyyy') AND TO_DATE('" + DataUtil.getDataFormatada(dataFim) + "', 'dd/mm/yyyy')").append("").append(" ");

        if (contratoFP.getId() != null) {
            sb.append(juncao).append("ATRAJETO.CONTRATOFP_ID = " + contratoFP.getId()).append("").append(" ");
        }

        if (contratoFP.getId() == null && hierarquiaOrganizacional.getId() != null) {
            sb.append(juncao).append("HO.CODIGO LIKE  '" + hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%'").append("").append(" ");
        }

        return sb.toString();
    }

    public boolean isValidaCampo() {
        boolean valida = true;
        if (filtroPesquisa == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Selecione um Tipo de Pesquisa.");
        }
        if (filtroPesquisa != null && isMostrarPanelServidor && contratoFP == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("O Campo Servidor é obrigatório.");
        }
        if (filtroPesquisa != null && isMostrarPanelUnidade && hierarquiaOrganizacional == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("O Campo Unidade é obrigatório.");
        }
        if (tipoAcidente == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("É necessário selecionar um Tipo de Acidente.");
        }
        if (dataInicio == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("O Campo Data Inicial é obrigatório.");
        }
        if (dataFim == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("O Campo Data Final é obrigatório.");
        }

        return valida;
    }

    public void limparCampos() {
        tipoAcidente = null;
        filtroPesquisa = null;
        isMostrarPanelServidor = false;
        isMostrarPanelUnidade = false;
        dataInicio = null;
        dataFim = null;
    }

    public enum TipoPesquisa {
        PESQUISAR_SERVIDOR("Servidor"),
        PESQUISAR_UNIDADE("Unidade Organizacional");

        private String descricao;

        TipoPesquisa(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public ConverterAutoComplete getConverterAutoCompleteContratoFP() {
        if (converterAutoCompleteContratoFP == null) {
            converterAutoCompleteContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterAutoCompleteContratoFP;
    }

    public void setConverterAutoCompleteContratoFP(ConverterAutoComplete converterAutoCompleteContratoFP) {
        this.converterAutoCompleteContratoFP = converterAutoCompleteContratoFP;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public ConverterAutoComplete getConverterAutoCompleteUnidadeOrganizacional() {
        if (converterAutoCompleteUnidadeOrganizacional == null) {
            converterAutoCompleteUnidadeOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterAutoCompleteUnidadeOrganizacional;
    }

    public void setConverterAutoCompleteUnidadeOrganizacional(ConverterAutoComplete converterAutoCompleteUnidadeOrganizacional) {
        this.converterAutoCompleteUnidadeOrganizacional = converterAutoCompleteUnidadeOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public Boolean getIsMostrarPanelServidor() {
        return isMostrarPanelServidor;
    }

    public void setIsMostrarPanelServidor(Boolean isMostrarPanelServidor) {
        this.isMostrarPanelServidor = isMostrarPanelServidor;
    }

    public Boolean getIsMostrarPanelUnidade() {
        return isMostrarPanelUnidade;
    }

    public void setIsMostrarPanelUnidade(Boolean isMostrarPanelUnidade) {
        this.isMostrarPanelUnidade = isMostrarPanelUnidade;
    }

    public String getFiltroPesquisa() {
        return filtroPesquisa;
    }

    public void setFiltroPesquisa(String filtroPesquisa) {
        this.filtroPesquisa = filtroPesquisa;
    }

    public TipoAcidente getTipoAcidente() {
        return tipoAcidente;
    }

    public void setTipoAcidente(TipoAcidente tipoAcidente) {
        this.tipoAcidente = tipoAcidente;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }
}
