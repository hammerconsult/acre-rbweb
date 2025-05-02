/*
 * Codigo gerado automaticamente em Mon Mar 05 14:54:30 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.StatusPeriodoAquisitivo;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "aprovacaoFeriasControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoAprovacaoFerias", pattern = "/aprovacao-ferias/novo/", viewId = "/faces/rh/administracaodepagamento/aprovacaoferias/edita.xhtml"),
        @URLMapping(id = "editarAprovacaoFerias", pattern = "/aprovacao-ferias/editar/#{aprovacaoFeriasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/aprovacaoferias/edita.xhtml"),
        @URLMapping(id = "listarAprovacaoFerias", pattern = "/aprovacao-ferias/listar/", viewId = "/faces/rh/administracaodepagamento/aprovacaoferias/lista.xhtml"),
        @URLMapping(id = "verAprovacaoFerias", pattern = "/aprovacao-ferias/ver/#{aprovacaoFeriasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/aprovacaoferias/visualizar.xhtml")
})
public class AprovacaoFeriasControlador extends PrettyControlador<AprovacaoFerias> implements Serializable, CRUD {

    @EJB
    private AprovacaoFeriasFacade aprovacaoFeriasFacade;
    @EJB
    private SugestaoFeriasFacade sugestaoFeriasFacade;
    private ConverterAutoComplete converterContratoFp;
    private ConverterAutoComplete converterHierarquia;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ContratoFP contratoFP;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    private List<SugestaoFerias> sugestoesFerias;
    private Boolean aprovadas;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private SugestaoFerias sugestaoFeriasSelecionada;

    public AprovacaoFeriasControlador() {
        super(AprovacaoFerias.class);
    }

    public SugestaoFerias getSugestaoFeriasSelecionada() {
        return sugestaoFeriasSelecionada;
    }

    public void setSugestaoFeriasSelecionada(SugestaoFerias sugestaoFeriasSelecionada) {
        this.sugestaoFeriasSelecionada = sugestaoFeriasSelecionada;
    }

    public List<SugestaoFerias> getSugestoesFerias() {
        return sugestoesFerias;
    }

    public void setSugestoesFerias(List<SugestaoFerias> sugestoesFerias) {
        this.sugestoesFerias = sugestoesFerias;
    }

    public Boolean getAprovadas() {
        return aprovadas;
    }

    public void setAprovadas(Boolean aprovadas) {
        this.aprovadas = aprovadas;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public AprovacaoFeriasFacade getFacade() {
        return aprovacaoFeriasFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return aprovacaoFeriasFacade;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaOrganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public Converter getConverterContrato() {
        if (converterContratoFp == null) {
            converterContratoFp = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFp;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public List<ContratoFP> completaContrato(String parte) {
        if (hierarquiaOrganizacional == null) {
            return contratoFPFacade.recuperaContratoMatriculaSql(parte.trim());
        } else {
            return contratoFPFacade.recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursiva(parte.trim(), hierarquiaOrganizacional, UtilRH.getDataOperacao());
        }
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public void setaUnidadeOrganizacional(SelectEvent item) {
        hierarquiaOrganizacional = (HierarquiaOrganizacional) item.getObject();

    }

    private void validarConsultaAprovacao() throws ExcecaoNegocioGenerica {
        Boolean validou = Boolean.TRUE;
        if (hierarquiaOrganizacional == null && contratoFP == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "É obrigatório informar o 'Local de Trabalho' ou 'Servidor', por favor informe um desses campos.");
            validou = Boolean.FALSE;
        }

        if (!validou) {
            throw new ExcecaoNegocioGenerica("");
        }
    }

    public void filtrar() {
        try {
            validarConsultaAprovacao();
            sugestoesFerias = sugestaoFeriasFacade.recuperarSugestaoFeriasPorContrato(contratoFP, hierarquiaOrganizacional, aprovadas);
//            addAprovacaoFerias(sugestoesFerias, aprovacaoFerias);
        } catch (Exception e) {

        }
    }

    private void addAprovacaoFerias(List<SugestaoFerias> sugestoesFerias, List<AprovacaoFerias> aprovacaoFerias) {
        if (!sugestoesFerias.isEmpty()) {
            for (SugestaoFerias su : sugestoesFerias) {
                if (!su.getListAprovacaoFerias().isEmpty()) {
                    aprovacaoFerias.addAll(su.getListAprovacaoFerias());
                } else {
                    AprovacaoFerias ap = new AprovacaoFerias();
                    ap.setSugestaoFerias(su);
                    aprovacaoFerias.add(ap);
                }

            }
        }
    }

    @URLAction(mappingId = "novoAprovacaoFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        contratoFP = null;
        hierarquiaOrganizacional = null;
        aprovadas = null;
    }

    public void limpar() {
        FacesUtil.redirecionamentoInterno("/consulta/programacao-ferias/novo/");
    }

    @URLAction(mappingId = "editarAprovacaoFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verAprovacaoFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public AprovacaoFerias ajudarDataAprovacaoFerias(AprovacaoFerias af) {
        FolhaDePagamento fp = folhaDePagamentoFacade.recuperarUltimaFolhaDePagamentoNormalEfetivada();
        Date dataEfetivacao = fp.getEfetivadaEm();
        Date hoje = new Date();
        dataEfetivacao = Util.getDataHoraMinutoSegundoZerado(dataEfetivacao);
        hoje = Util.getDataHoraMinutoSegundoZerado(hoje);

        if (hoje.compareTo(dataEfetivacao) > 0) {
            af.setDataAprovacao(hoje);
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(dataEfetivacao);
            c.add(Calendar.DAY_OF_MONTH, 1);
            af.setDataAprovacao(c.getTime());
        }

        return af;
    }

    public void fixaData(AprovacaoFerias af) {
        if (af.getAprovado()) {
            ajudarDataAprovacaoFerias(af);
        } else {
            af.setAprovado(Boolean.FALSE);
            af.setDataAprovacao(null);
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/aprovacao-ferias/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        if (hierarquiaOrganizacional == null) {
            return contratoFPFacade.recuperaContratoMatriculaSql(parte.trim());
        } else {
            return contratoFPFacade.recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursiva(parte.trim(), hierarquiaOrganizacional, UtilRH.getDataOperacao());
        }
    }

    public boolean isDataAprovacaoAnteriorOuIgualADataDeEfetivacaoUltimaFolha(AprovacaoFerias af) {
        Date dataInformada = af.getDataAprovacao();
        if (dataInformada == null) {
            return false;
        }
        FolhaDePagamento fp = folhaDePagamentoFacade.recuperarUltimaFolhaDePagamentoNormalEfetivada();
        Date dataEfetivacao = fp.getEfetivadaEm();
        dataEfetivacao = Util.getDataHoraMinutoSegundoZerado(dataEfetivacao);

        if (dataInformada.compareTo(dataEfetivacao) <= 0) {
            return true;
        }
        return false;
    }

    public void selecionarSugestaoFerias(ActionEvent ev) {
        sugestaoFeriasSelecionada = (SugestaoFerias) ev.getComponent().getAttributes().get("sugestao");
        if (sugestaoFeriasSelecionada.getAprovacaoFerias() == null){
            AprovacaoFerias af = new AprovacaoFerias();
            af.setSugestaoFerias(sugestaoFeriasSelecionada);
            sugestaoFeriasSelecionada.setListAprovacaoFerias(Util.adicionarObjetoEmLista(sugestaoFeriasSelecionada.getListAprovacaoFerias(), af));
        }
    }

    public void removerAprovacaoDeFerias(ActionEvent ev) {
        sugestaoFeriasSelecionada = (SugestaoFerias) ev.getComponent().getAttributes().get("sugestao");
        if (sugestaoFeriasSelecionada.getAprovacaoFerias() == null || sugestaoFeriasSelecionada.getAprovacaoFerias().getDataAprovacao() == null){
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Não é possível 'Desaprovar' férias que ainda não foram aprovadas.");
            return;
        }
        if (sugestaoFeriasSelecionada.getPeriodoAquisitivoFL().getStatus().equals(StatusPeriodoAquisitivo.CONCEDIDO) || sugestaoFeriasSelecionada.getPeriodoAquisitivoFL().getStatus().equals(StatusPeriodoAquisitivo.PARCIAL)) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Não é possível remover a aprovação de férias já concedidas/concedida parcialmente.");
            return;
        }
        AprovacaoFerias af =sugestaoFeriasSelecionada.getAprovacaoFerias();
        af.setDataAprovacao(null);
        af.setAprovado(Boolean.FALSE);
        sugestaoFeriasSelecionada.setListAprovacaoFerias(Util.adicionarObjetoEmLista(sugestaoFeriasSelecionada.getListAprovacaoFerias(), af));
        salvar();
        FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "A sugestão de férias selecionada não está mais aprovada.");
    }

    public boolean isDataAprovacaoPosteriorAHoje(AprovacaoFerias af) {
        if (af.getDataAprovacao() == null) {
            return false;
        }

        Date hoje = Util.getDataHoraMinutoSegundoZerado(new Date());
        Date dataAprovacao = Util.getDataHoraMinutoSegundoZerado(af.getDataAprovacao());

        if (dataAprovacao.compareTo(hoje) > 0) {
            return true;
        }
        return false;
    }

    public void confirmarDataAprovacao() {
        AprovacaoFerias aprovacaoSelecionada = sugestaoFeriasSelecionada.getAprovacaoFerias();
        if (aprovacaoSelecionada.getDataAprovacao() ==null){
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Por favor, informe a o campo 'Data aprovação'.");
            return;
        }

        if (isDataAprovacaoPosteriorAHoje(aprovacaoSelecionada)) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A data de aprovação não deve ser posterior a hoje <b>'" + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "'</b>.");
            aprovacaoSelecionada.setDataAprovacao(null);
            return;
        }

        if (isDataAprovacaoAnteriorOuIgualADataDeEfetivacaoUltimaFolha(aprovacaoSelecionada)) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A data de aprovação deve ser posterior a data de efetivação da última folha <b>'" + new SimpleDateFormat("dd/MM/yyyy").format(folhaDePagamentoFacade.recuperarUltimaFolhaDePagamentoNormalEfetivada().getEfetivadaEm()) + "'</b>.");
            aprovacaoSelecionada.setDataAprovacao(null);
            return;
        }

        if (aprovacaoSelecionada.getDataAprovacao() != null) {
            aprovacaoSelecionada.setAprovado(Boolean.TRUE);
        } else {
            aprovacaoSelecionada.setAprovado(Boolean.FALSE);
        }

        salvar();
        RequestContext.getCurrentInstance().execute("aprovacaoFerias.hide();");
        RequestContext.getCurrentInstance().update("Formulario:tabelaFerias");
        RequestContext.getCurrentInstance().update("painelInfoMotivoDesabilitado");
        FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "As programação de férias foi aprovada com sucesso.");
    }

    @Override
    public void salvar() {
        sugestaoFeriasFacade.salvar(sugestaoFeriasSelecionada);
        this.sugestoesFerias = Util.adicionarObjetoEmLista(this.sugestoesFerias, sugestaoFeriasSelecionada);
    }

    public void aprovarTudo(){
        if (this.sugestoesFerias == null || this.sugestoesFerias.isEmpty()){
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Não existe(m) programação(ões) de férias a ser(em) aprovada(s).");
            return;
        }

        for (SugestaoFerias sf : this.sugestoesFerias){
            if (!sf.estaAprovada()){
                sf = aprovacaoFeriasFacade.aprovar(sf);
            }
        }
    }
}
