package br.com.webpublico.controle.contabil.reprocessamento;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.controle.Web;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ReprocessamentoContabilHistorico;
import br.com.webpublico.entidadesauxiliares.contabil.BarraProgressoAssistente;
import br.com.webpublico.entidadesauxiliares.contabil.ReprocessamentoContabil;
import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.contabil.ApiServiceContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoContabilFacade;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by renato on 20/09/17.
 */
@ManagedBean(name = "reprocessamentoContabilControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-reprocessamento", pattern = "/reprocessamento-contabil-novo/novo/", viewId = "/faces/financeiro/orcamentario/reprocessamento-contabil/edita.xhtml"),
    @URLMapping(id = "editar-reprocessamento", pattern = "/reprocessamento-contabil-novo/editar/#{reprocessamentoContabilControlador.id}/", viewId = "/faces/financeiro/orcamentario/reprocessamento-contabil/edita.xhtml"),
    @URLMapping(id = "acompanhamento-reprocessamento", pattern = ReprocessamentoContabilControlador.URL_ACOMPANHAMENTO, viewId = "/faces/financeiro/orcamentario/reprocessamento-contabil/log.xhtml"),
    @URLMapping(id = "ver-reprocessamento", pattern = "/reprocessamento-contabil-novo/ver/#{reprocessamentoContabilControlador.id}/", viewId = "/faces/financeiro/orcamentario/reprocessamento-contabil/visualizar.xhtml"),
    @URLMapping(id = "listar-reprocessamento", pattern = "/reprocessamento-contabil-novo/listar/", viewId = "/faces/financeiro/orcamentario/reprocessamento-contabil/lista.xhtml")
})
public class ReprocessamentoContabilControlador extends PrettyControlador<ReprocessamentoContabilHistorico> implements Serializable, CRUD {


    public static final String URL_ACOMPANHAMENTO = "/reprocessamento-contabil-novo/acompanhamento/";
    private static final String KEY_PARAMETRO = "AUX-REPROCESSAMENTO_CONTABIL";
    private ReprocessamentoContabil auxiliar;
    @EJB
    private ReprocessamentoContabilFacade reprocessamentoContabilFacade;
    private List<Future<ReprocessamentoContabil>> futures;
    private TipoBalancete[] tiposBalancetes;

    public ReprocessamentoContabilControlador() {
        super(ReprocessamentoContabilHistorico.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return reprocessamentoContabilFacade.getReprocessamentoContabilHistoricoFacade();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/reprocessamento-contabil-novo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-reprocessamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        auxiliar = new ReprocessamentoContabil();
        auxiliar.setConfiguracaoContabil(reprocessamentoContabilFacade.buscarConfiguracaoContabilVigente());

        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        Calendar c = Calendar.getInstance();
        c.setTime(sistemaControlador.getDataOperacao());
        c.set(Calendar.DAY_OF_MONTH, 1);

        auxiliar.setDataInicio(c.getTime());
        auxiliar.setDataFim(sistemaControlador.getDataOperacao());
        auxiliar.setExercicio(sistemaControlador.getExercicioCorrente());
        auxiliar.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        auxiliar.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
        ReprocessamentoContabil reprocessamentoContabil = getSingleton().getReprocessamentoContabil();
        if (reprocessamentoContabil != null) {
            auxiliar = reprocessamentoContabil;
        }
    }

    @URLAction(mappingId = "ver-reprocessamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-reprocessamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "acompanhamento-reprocessamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void log() {
        auxiliar = (ReprocessamentoContabil) Web.pegaDaSessao(KEY_PARAMETRO);
        reprocessar();
    }


    public Boolean hasProcessando() {
        return reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando();
    }

    public Boolean hasProcessandoPossuiErros() {
        return reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().getReprocessamentoContabilHistorico().getProcessadosComErro() > 0;
    }

    public List<TipoBalancete> getListaTiposBalancete() {
        return Arrays.asList(TipoBalancete.values());
    }

    private void prepararTipoBalancete() {
        auxiliar.getTipoBalancetes().clear();
        for (TipoBalancete tipoBalancete : tiposBalancetes) {
            auxiliar.getTipoBalancetes().add(tipoBalancete);
        }
    }

    public void redirecionarAcompanhamentoReprocessamento() {
        FacesUtil.redirecionamentoInterno(URL_ACOMPANHAMENTO);
    }

    public void limparObjetosContabilService() {
        ApiServiceContabil.getService().limparObjetos();
    }

    public void carregarContaAuxiliar() {
        ApiServiceContabil.getService().carregarContaAuxiliar(auxiliar.getExercicio());
    }

    public void redirecionarReprocessamento() {
        try {
            reprocessamentoContabilFacade.getFechamentoMensalFacade().validarMesContabilPorPeriodo(auxiliar.getDataInicio(), auxiliar.getDataFim());
            prepararTipoBalancete();
            validarAuxiliar();
            Web.poeNaSessao(KEY_PARAMETRO, auxiliar);
            FacesUtil.redirecionamentoInterno(URL_ACOMPANHAMENTO);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível iniciar o reprocessamento!", " Detalhe do erro: " + e.getMessage());
        }
    }

    public void reprocessar() {
        try {
            if (!hasProcessando() && auxiliar != null) {
                reprocessamentoContabilFacade.apagar(auxiliar);
                reprocessamentoContabilFacade.inicializarSingleton(auxiliar);
                //reprocessamentoContabilFacade.buscarContasAuxiliaresDetalhadas(auxiliar.getExercicio());
                limparObjetosContabilService();
                carregarContaAuxiliar();
                List<ReprocessamentoContabil> retorno = dividirUnidades();
                futures = Lists.newArrayList();
                for (ReprocessamentoContabil reprocessamentoContabil : retorno) {
                    futures.add(reprocessamentoContabilFacade.reprocessar(reprocessamentoContabil));
                }
                BarraProgressoAssistente barra = new BarraProgressoAssistente();
                barra.inicializa();
                ReprocessamentoContabil reprocessamentoContabil = criarReprocessamento(barra, getHierarquiaOrganizacionals(), null);
                futures.add(reprocessamentoContabilFacade.reprocessarTransferencias(reprocessamentoContabil));
            }

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível iniciar o reprocessamento!", " Detalhe do erro: " + e.getMessage());
        }
    }

    private List<ReprocessamentoContabil> dividirUnidades() {
        List<ReprocessamentoContabil> retorno = Lists.newArrayList();
        criarReprocessamentoLOA(retorno);
        criarReprocessamentoTipoEventoContabilRetirandoLOA(retorno);
        return retorno;
    }

    private void criarReprocessamentoTipoEventoContabilRetirandoLOA(List<ReprocessamentoContabil> retorno) {
        List<List<HierarquiaOrganizacional>> partition = particionarHierarquias();
        for (List<HierarquiaOrganizacional> hierarquiaOrganizacionals : partition) {
            BarraProgressoAssistente barra = new BarraProgressoAssistente();
            barra.inicializa();
            ReprocessamentoContabil repro = criarReprocessamento(barra, hierarquiaOrganizacionals, TipoEventoContabil.getTiposEventoContabilRetirandoInicialLOA());
            reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().atribuirReprocessamentoUsuario(repro);
            retorno.add(repro);
        }
    }

    private void criarReprocessamentoLOA(List<ReprocessamentoContabil> retorno) {
        BarraProgressoAssistente barra = new BarraProgressoAssistente();
        barra.inicializa();
        ReprocessamentoContabil repro = criarReprocessamento(barra, getHierarquiaOrganizacionals(), TipoEventoContabil.getTiposEventoContabilInicialLOA());
        reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().atribuirReprocessamentoUsuario(repro);
        retorno.add(repro);
    }

    private ReprocessamentoContabil criarReprocessamento(BarraProgressoAssistente barraProgressoAssistente, List<HierarquiaOrganizacional> partition, List<TipoEventoContabil> tipos) {
        ReprocessamentoContabil re3 = new ReprocessamentoContabil();
        re3.setDataInicio(auxiliar.getDataInicio());
        re3.setDataFim(auxiliar.getDataFim());
        re3.setExercicio(auxiliar.getExercicio());
        re3.setBarraProgressoAssistente(barraProgressoAssistente);
        re3.setUnidadeOrganizacional(auxiliar.getUnidadeOrganizacional());
        re3.setUsuarioSistema(auxiliar.getUsuarioSistema());
        re3.setConfiguracaoContabil(auxiliar.getConfiguracaoContabil());
        re3.getTipoBalancetes().addAll(auxiliar.getTipoBalancetes());
        if (tipos != null) {
            re3.getTipoEventoContabils().addAll(tipos);
        }
        re3.getListaHierarquias().addAll(partition);
        return re3;
    }

    private List<List<HierarquiaOrganizacional>> particionarHierarquias() {
        List<HierarquiaOrganizacional> hierarquiaOrganizacionals = getHierarquiaOrganizacionals();
        if (hierarquiaOrganizacionals.size() > 1) {
            return Lists.partition(hierarquiaOrganizacionals, hierarquiaOrganizacionals.size() / 2);
        }
        return Lists.partition(hierarquiaOrganizacionals, hierarquiaOrganizacionals.size() / 1);
    }

    private List<HierarquiaOrganizacional> getHierarquiaOrganizacionals() {
        List<HierarquiaOrganizacional> hierarquiaOrganizacionals = auxiliar.getListaHierarquias();
        if (hierarquiaOrganizacionals.isEmpty()) {
            hierarquiaOrganizacionals = reprocessamentoContabilFacade.getHierarquiaOrganizacionalFacade().filtraPorNivelTrazendoTodas("", "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), auxiliar.getDataInicio());
        }
        return hierarquiaOrganizacionals;
    }

    private void validarAuxiliar() {
        ValidacaoException ve = new ValidacaoException();
        if (auxiliar.getDataInicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data inicial deve ser informado.");
        }
        ve.lancarException();
        if (auxiliar.getDataFim() != null && auxiliar.getDataFim().before(auxiliar.getDataInicio())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data inicial deve ser anterior a data final.");
        }
        if (auxiliar.getTipoBalancetes().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar pelo menos 1 (UM) tipo de balancete.");
        }
        ve.lancarException();
    }

    public void verificarSituacao() {
        if (!hasProcessando()) {
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    public void abortar() {
        reprocessamentoContabilFacade.finalizarSingleton();
        if (futures != null) {
            for (Future<ReprocessamentoContabil> future : futures) {
                if (future != null) {
                    future.cancel(true);
                }
            }
        }
        redirecionarNovo();
    }

    public void redirecionarNovo() {
        FacesUtil.redirecionamentoInterno("/reprocessamento-contabil-novo/novo/");
    }

    public ReprocessamentoContabil getauxiliar() {
        return auxiliar;
    }

    public void setauxiliar(ReprocessamentoContabil auxiliar) {
        this.auxiliar = auxiliar;
    }

    public TipoBalancete[] getTiposBalancetes() {
        return tiposBalancetes;
    }

    public void setTiposBalancetes(TipoBalancete[] tiposBalancetes) {
        this.tiposBalancetes = tiposBalancetes;
    }

    public void gerarLogPDF() {
        try {
            reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().preparaDialogLog(reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().getReprocessamentoContabilHistorico());
            reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().gerarPDFLog(reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().getReprocessamentoContabilHistorico());
        } catch (Exception e) {
            FacesUtil.addError("Erro", e.getMessage());
        }
    }

    public void gerarLogPDFResumido() {
        try {
            reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().preparaDialogLogErros(reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().getReprocessamentoContabilHistorico());
            reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().gerarPDFLog(reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().getReprocessamentoContabilHistorico());
        } catch (Exception e) {
            FacesUtil.addError("Erro", e.getMessage());
        }
    }

    public void gerarLogPDFResumidoVisualizar(boolean isResumido) {
        try {
            reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().preparaDialogLogErros(selecionado);
            reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().setTipoLog(isResumido ? TipoLog.SOMENTE_ERROS_DISTINTOS : TipoLog.SOMENTE_ERROS);
            reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton().gerarPDFLog(selecionado);
        } catch (Exception e) {
            FacesUtil.addError("Erro", e.getMessage());
        }
    }

    public ReprocessamentoLancamentoContabilSingleton getSingleton() {
        return reprocessamentoContabilFacade.getReprocessamentoLancamentoContabilSingleton();
    }

    public enum TipoLog {
        COMPLETO("Completo"),
        SOMENTE_ERROS("Somente erros (Detalhado)"),
        SOMENTE_ERROS_DISTINTOS("Somentes erros(distintos)");

        private String descricao;

        TipoLog(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum ExtensaoArquioLog {
        PDF("PDF"),
        TXT("Texto");

        private String descricao;

        ExtensaoArquioLog(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

}
