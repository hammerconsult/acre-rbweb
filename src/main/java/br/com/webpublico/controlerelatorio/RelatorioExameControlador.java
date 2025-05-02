package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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
 * Created by carlos on 25/11/15.
 */
@ManagedBean(name = "relatorioExameControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoConsultaExame",
        pattern = "/rh/consulta-de-exames/",
        viewId = "/faces/rh/relatorios/relatorioconsultaexame.xhtml")
})
public class RelatorioExameControlador extends AbstractReport implements Serializable {
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ConverterAutoComplete converterAutoCompleteContratoFP;
    private ConverterAutoComplete converterAutoCompleteUnidadeOrganizacional;
    private ContratoFP contratoFP;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Date dataInicio;
    private Date dataFim;
    private Boolean isMostrarPanelServidor;
    private Boolean isMostrarPanelUnidade;
    private String filtroPesquisa;
    private String filtros;

    public static final String EXAME = "relatorioExame.jasper";

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.recuperaContratoMatricula(parte);
    }

    public List<HierarquiaOrganizacional> completarUnidadeOrganizacional(String filter) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(filter.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "novoConsultaExame", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        isMostrarPanelServidor = false;
        isMostrarPanelUnidade = false;
        dataInicio = null;
        dataFim = null;
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
        return filtroPesquisa.equals(RelatorioConsultaAcidenteControlador.TipoPesquisa.PESQUISAR_SERVIDOR.name());
    }

    private boolean isPesquisaUnidade() {
        return filtroPesquisa.equals(RelatorioConsultaAcidenteControlador.TipoPesquisa.PESQUISAR_UNIDADE.name());
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
        return Util.getListSelectItem(RelatorioConsultaAcidenteControlador.TipoPesquisa.values());
    }

    public void emitir() {
        setGeraNoDialog(Boolean.TRUE);
        HashMap parameters = new HashMap();
        if (isValidaCampo()) {
            try {

                parameters.put("IMAGEM", super.getCaminhoImagem());
                parameters.put("WHERE", isCriterio());
                parameters.put("DATA_INICIO", dataInicio);
                parameters.put("DATA_FINAL", dataFim);

                gerarRelatorio(EXAME, parameters);
            } catch (Exception e) {
                FacesUtil.addErroAoGerarRelatorio("");
            }
        }
    }

    public void limparCampos() {
        filtroPesquisa = null;
        isMostrarPanelServidor = false;
        isMostrarPanelUnidade = false;
        dataInicio = null;
        dataFim = null;
    }

    public String isCriterio() {
        StringBuilder sb = new StringBuilder(" WHERE 1 = 1 ");
        String juncao = " and ";
        filtros = "";

        if (contratoFP.getId() != null) {
            sb.append(juncao).append("PCMSO.CONTRATOFP_ID = " + contratoFP.getId()).append("").append(" ");
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
}
