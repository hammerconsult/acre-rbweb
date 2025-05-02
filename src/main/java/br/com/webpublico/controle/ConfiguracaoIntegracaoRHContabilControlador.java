package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.rh.integracao.TipoContabilizacao;
import br.com.webpublico.enums.rh.integracao.TipoContribuicao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.rh.arquivos.integracao.ConfiguracaoIntegracaoRHContabilFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.viewobjects.IntegracaoEquiplanoRHVO;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "configuracao-integracao-rh-contabil", pattern = "/configuracao/integracao-rh-contabil/", viewId = "/faces/rh/configuracao/integracao-rh-contabil.xhtml"),
})
public class ConfiguracaoIntegracaoRHContabilControlador implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ConfiguracaoIntegracaoRHContabilControlador.class);

    @EJB
    private ConfiguracaoIntegracaoRHContabilFacade facade;
    private List<IntegracaoEquiplanoRHVO> configuracoes;
    private List<IntegracaoEquiplanoRHVO> configuracoesFilter;
    private IntegracaoEquiplanoRHVO selecionado;
    private Mes mes;
    private Exercicio exercicio;
    private String filtro;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private Future futureTransportarConfiguracao;
    private AssistenteBarraProgresso assistente;
    private Mes mesOrigem;
    private Exercicio exercicioOrigem;
    private Mes mesDestino;
    private Exercicio exercicioDestino;
    private TipoCopia tipoCopia;
    private RecursoFP recursoFP;
    private TipoContabilizacao tipoContabilizacao;
    private List<EventoFP> eventosFP;
    private EventoFP[] eventosFPSelecionados;
    private Date inicioVigencia;
    private Date finalVigencia;
    private List<RecursoFP> filtroRecursos;
    private List<EventoFP> filtroEventos;
    private Boolean apenasEventosCadastrados;

    public ConfiguracaoIntegracaoRHContabilControlador() {
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;

    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    private void preencherInicioEFimDeVigencia(List<IntegracaoEquiplanoRHVO> configuracoes) {
        inicioVigencia = null;
        finalVigencia = null;
        if (configuracoes != null && !configuracoes.isEmpty()) {
            FontesRecursoFP fontesRecursoFP = configuracoes.get(0).getFontesRecursoFP();
            if (fontesRecursoFP != null) {
                inicioVigencia = fontesRecursoFP.getInicioVigencia();
                finalVigencia = fontesRecursoFP.getFinalVigencia();
            }
        }
    }

    @URLAction(mappingId = "configuracao-integracao-rh-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        mes = Mes.getMesToInt(DataUtil.getMes(facade.getDataOperacao()));
        exercicio = facade.getExercicioCorrente();
        configuracoes = Lists.newArrayList();
        filtroRecursos = Lists.newArrayList();
        filtroEventos = Lists.newArrayList();
        selecionado = null;
        filtro = null;
        tipoFolhaDePagamento = null;
        apenasEventosCadastrados = Boolean.FALSE;
        buscarConfiguracoes();
    }

    public void buscarConfiguracoes() {
        try {
            lancarExceptionAssistente();
            validarExercicio();
            configuracoes = facade.buscarConfiguracoes(mes, exercicio, filtroRecursos, filtroEventos, apenasEventosCadastrados);

            HierarquiaOrganizacional orcamentariaLogada = facade.getOrcamentariaLogada();
            HierarquiaOrganizacional administrativaLogada = facade.getAdministrativaLogada();
            configuracoes.forEach(configuracao -> configuracao.setOrcamentaria(orcamentariaLogada));
            configuracoes.forEach(configuracao -> configuracao.setAdministrativa(administrativaLogada));

            preencherInicioEFimDeVigencia(configuracoes);
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        }
    }

    private void validarExercicio() {
        ValidacaoException onpe = new ValidacaoException();
        if (exercicio == null) {
            onpe.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        onpe.lancarException();
    }

    private void lancarExceptionAssistente() {
        if (assistente != null && assistente.getValidacaoException() != null && !assistente.getValidacaoException().getMensagens().isEmpty()) {
            assistente.getValidacaoException().lancarException();
        }
    }

    public void preencherDadosParaCopia() {
        mesDestino = mes;
        exercicioDestino = exercicio;
        mesOrigem = Mes.getMesToInt(mesDestino.getNumeroMes() - 1);
        exercicioOrigem = exercicio;
    }

    public void copiarConfiguracao() {
        try {
            assistente = AssistenteBarraProgresso.getAssistenteBarraProgresso(facade.getDataOperacao(), null, facade.getUsuarioCorrente());
            futureTransportarConfiguracao = facade.copiarConfiguracao(assistente, mesOrigem, exercicioOrigem, mesDestino, exercicioDestino, tipoCopia);
            FacesUtil.executaJavaScript("iniciarAssistente()");
            FacesUtil.executaJavaScript("dlgcopia.hide()");

        } catch (Exception e) {
            logger.error("ERRO ", e);
        }
    }

    public void verificarProgresso() {
        if (futureTransportarConfiguracao != null && futureTransportarConfiguracao.isDone()) {
            FacesUtil.executaJavaScript("finalizarAssistente()");
        }
    }

    public void limparFonteEvento() {
        FacesUtil.executaJavaScript("fontedeevento.clearFilters()");
        configuracoes = new ArrayList<>();
        configuracoesFilter = new ArrayList<>();
    }

    public void preencherPorFonteDespesa() {
        selecionado.setFonteDeRecursos(((ContaDeDestinacao) selecionado.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos());
    }

    public void preencherPorContaDestinacao() {
        selecionado.setFonteDeRecursos(selecionado.getContaDeDestinacao().getFonteDeRecursos());
    }

    public void validarEvento() {
        ValidacaoException onpe = new ValidacaoException();
        if (selecionado.getTipoContabilizacao() == null)
            onpe.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Contabilização deve ser informado.");
        if (selecionado.getDespesaORC() == null && renderCampo("codigoreduzido"))
            onpe.adicionarMensagemDeCampoObrigatorio("O campo Código Reduzido deve ser informado.");
        if (selecionado.getFonteDespesaORC() == null && renderCampo("fontedespesaorc"))
            onpe.adicionarMensagemDeCampoObrigatorio("O campo Fonte de Despesa Orçamentaria deve ser informado.");
        if (selecionado.getContaDeDestinacao() == null && renderCampo("contadestinacao"))
            onpe.adicionarMensagemDeCampoObrigatorio("O campo Conta de Destinação deve ser informado.");
        if (selecionado.getDesdobramento() == null && renderCampo("desdobramento"))
            onpe.adicionarMensagemDeCampoObrigatorio("O campo Desdobramento deve ser informado.");
        if (selecionado.getSubConta() == null && renderCampo("subconta"))
            onpe.adicionarMensagemDeCampoObrigatorio("O campo Conta Financeira deve ser informado.");
        if (selecionado.getFornecedor() == null && renderCampo("fornecedor"))
            onpe.adicionarMensagemDeCampoObrigatorio("O campo Fornecedor deve ser informado.");
        if (selecionado.getContaExtraorcamentaria() == null && renderCampo("contaextra"))
            onpe.adicionarMensagemDeCampoObrigatorio("O campo Conta Extra Orçamentaria deve ser informado.");
        if (selecionado.getCredor() == null && renderCampo("credor"))
            onpe.adicionarMensagemDeCampoObrigatorio("O campo Credor deve ser informado.");
        if (selecionado.getTipoContribuicao() == null && renderCampo("tipocontribuicao"))
            onpe.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Contribuição deve ser informado.");
        if (selecionado.getOperacaoFormula() == null && renderCampo("operacao"))
            onpe.adicionarMensagemDeCampoObrigatorio("O campo Operação deve ser informado.");
        onpe.lancarException();
    }

    public void salvarEvento() {
        try {
            validarEvento();
            assistente = AssistenteBarraProgresso.getAssistenteBarraProgresso(facade.getDataOperacao(), null, facade.getUsuarioCorrente());
            Util.adicionarObjetoEmLista(configuracoes, selecionado);
            assistente.setMensagensValidacaoFacesUtil(new ArrayList<FacesMessage>());
            facade.salvarConfiguracao(assistente, selecionado, mes, exercicio, exercicio);
            lancarExceptionAssistente();
            selecionado = null;
            FacesUtil.executaJavaScript("dlgevento.hide()");
            FacesUtil.executaJavaScript("fontedeevento.filter()");
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        }
    }

    public void cancelarEvento() {
        this.selecionado = null;
    }

    public void editarEvento(IntegracaoEquiplanoRHVO integracaoEquiplanoRHVO) {
        this.selecionado = (IntegracaoEquiplanoRHVO) Util.clonarObjeto(integracaoEquiplanoRHVO);
        if (selecionado.getFonteDeRecursos() != null)
            this.selecionado.setContaDeDestinacao(facade.recuperarContaDeDestinacao(selecionado.getFonteDeRecursos()));
    }

    public void removerEvento(IntegracaoEquiplanoRHVO integracaoEquiplanoRHVO) {
        try {
            if (integracaoEquiplanoRHVO.getIdFonteEvento() == null)
                throw new ValidacaoException("O Evento não possui configuração para excluir!");
            configuracoes.remove(integracaoEquiplanoRHVO);
            facade.removerConfiguracao(integracaoEquiplanoRHVO);
            FacesUtil.addOperacaoRealizada("Registro excluído com sucesso.");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        } catch (Exception e) {
            logger.error("ERRO ", e);
            FacesUtil.addOperacaoRealizada("Ocorreu um erro ao excluir. Detalhes do erro: " + e.getMessage());
        }
    }

    public void clonarCampos() {
        IntegracaoEquiplanoRHVO clone = (IntegracaoEquiplanoRHVO) Util.clonarObjeto(selecionado);
        selecionado = new IntegracaoEquiplanoRHVO();
        selecionado.setTipoContabilizacao(clone.getTipoContabilizacao());
        selecionado.setOrcamentaria(clone.getOrcamentaria());
        selecionado.setAdministrativa(clone.getAdministrativa());
        selecionado.setRecursoFP(clone.getRecursoFP());
        selecionado.setEventoFP(clone.getEventoFP());
        selecionado.setIdFonteEvento(clone.getIdFonteEvento());
        selecionado.setEditTipoContabilizacao(clone.getEditTipoContabilizacao());
        selecionado.setEditDespesaOrc(clone.getEditDespesaOrc());
        selecionado.setEditContaDeDestinacao(clone.getEditContaDeDestinacao());
        selecionado.setEditSubConta(clone.getEditSubConta());
        selecionado.setEditFornecedor(clone.getEditFornecedor());
        selecionado.setEditContaExtraorcamentaria(clone.getEditContaExtraorcamentaria());
        selecionado.setEditCredor(clone.getEditCredor());
        selecionado.setEditTipoContribuicao(clone.getEditTipoContribuicao());
        selecionado.setEditOperacaoFormula(clone.getEditOperacaoFormula());
    }

    public boolean renderCampo(String campo) {
        if (selecionado == null || selecionado.getTipoContabilizacao() == null)
            return false;
        if (TipoContabilizacao.EMPENHO.equals(selecionado.getTipoContabilizacao()))
            return Lists.newArrayList("codigoreduzido", "fontedespesaorc", "desdobramento", "subconta", "fornecedor", "ordenadordespesa", "tipocontribuicao", "operacao").contains(campo);
        if (TipoContabilizacao.OBRIGACAO_A_APAGAR.equals(selecionado.getTipoContabilizacao()))
            return Lists.newArrayList("codigoreduzido", "fontedespesaorc", "desdobramento", "fornecedor", "operacao").contains(campo);
        if (TipoContabilizacao.RETENCAO_EXTRAORCAMENTARIA_CONSIGNACOES.equals(selecionado.getTipoContabilizacao()))
            return Lists.newArrayList("contaextra", "credor", "operacao").contains(campo);
        return false;
    }

    public void preencherDadosParaConfiguracaoLote() {
        recursoFP = null;
        tipoContabilizacao = null;
        eventosFP = Lists.newArrayList();
        eventosFPSelecionados = null;
    }

    public void buscarEventosFPporRecursoFP() {
        if (recursoFP != null) {
            List<EventoFP> list = new ArrayList<>();
            for (IntegracaoEquiplanoRHVO obj : configuracoes) {
                if (obj.getRecursoFP().equals(recursoFP) && ((tipoContabilizacao == null && obj.getTipoContabilizacao() == null) ||
                    (tipoContabilizacao != null && tipoContabilizacao.equals(obj.getTipoContabilizacao())))) {
                    EventoFP eventoFP = obj.getEventoFP();
                    list.add(eventoFP);
                }
            }
            eventosFP = list;
            Collections.sort(eventosFP, new Comparator<EventoFP>() {
                @Override
                public int compare(EventoFP o1, EventoFP o2) {
                    try {
                        return Integer.valueOf(o1.getCodigo()).compareTo(Integer.valueOf(o2.getCodigo()));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }
            });
        }
    }

    private void validarCamposRecurso() {
        ValidacaoException ve = new ValidacaoException();
        if (recursoFP == null)
            ve.adicionarMensagemDeCampoObrigatorio("O campo RecursoFP deve ser informado.");
        if (eventosFPSelecionados == null || eventosFPSelecionados.length == 0)
            ve.adicionarMensagemDeCampoObrigatorio("É Obrigatório adicionar pelo menos um eventoFP.");
        ve.lancarException();
    }

    public void atualizarCampos() {
        try {
            validarCamposRecurso();
            selecionado = new IntegracaoEquiplanoRHVO();
            selecionado.setTipoContabilizacao(tipoContabilizacao);
            if (tipoContabilizacao == null) {
                selecionado.setEditTipoContabilizacao(true);
                selecionado.setEditDespesaOrc(true);
                selecionado.setEditContaDeDestinacao(true);
                selecionado.setEditSubConta(true);
                selecionado.setEditFornecedor(true);
                selecionado.setEditContaExtraorcamentaria(true);
                selecionado.setEditCredor(true);
                selecionado.setEditTipoContribuicao(true);
                selecionado.setEditOperacaoFormula(true);
            }

            FacesUtil.executaJavaScript("dlgeventos.show();");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void salvarEventos() {
        try {
            validarEventoLote();
            assistente = AssistenteBarraProgresso.getAssistenteBarraProgresso(facade.getDataOperacao(), null, facade.getUsuarioCorrente());
            assistente.setMensagensValidacaoFacesUtil(new ArrayList<FacesMessage>());
            for (EventoFP eventoFP : eventosFPSelecionados) {

                Optional<IntegracaoEquiplanoRHVO> optional = null;
                for (IntegracaoEquiplanoRHVO obj : configuracoes) {
                    if (obj.getRecursoFP().equals(recursoFP) && obj.getEventoFP().equals(eventoFP)) {
                        optional = Optional.of(obj);
                        break;
                    }
                }
                if (optional.isPresent()) {
                    IntegracaoEquiplanoRHVO integracaoEquiplanoRHVO = optional.get();

                    if (tipoContabilizacao != selecionado.getTipoContabilizacao()) {
                        IntegracaoEquiplanoRHVO conf = (IntegracaoEquiplanoRHVO) Util.clonarObjeto(selecionado);
                        conf.setRecursoFP(integracaoEquiplanoRHVO.getRecursoFP());
                        conf.setEventoFP(integracaoEquiplanoRHVO.getEventoFP());
                        conf.setIdFonteEvento(integracaoEquiplanoRHVO.getIdFonteEvento());
                        facade.salvarConfiguracao(assistente, conf, mes, exercicio, exercicio);
                        continue;
                    }

                    if (selecionado.getEditTipoContabilizacao())
                        integracaoEquiplanoRHVO.setTipoContabilizacao(selecionado.getTipoContabilizacao());
                    if (renderCampo("codigoreduzido") && (selecionado.getEditDespesaOrc() || selecionado.getEditTipoContabilizacao()))
                        integracaoEquiplanoRHVO.setDespesaORC(selecionado.getDespesaORC());
                    if (renderCampo("fontedespesaorc") && (selecionado.getEditDespesaOrc() || selecionado.getEditTipoContabilizacao())) {
                        integracaoEquiplanoRHVO.setFonteDespesaORC(selecionado.getFonteDespesaORC());
                        integracaoEquiplanoRHVO.setFonteDeRecursos(((ContaDeDestinacao) selecionado.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos());
                    }
                    if (renderCampo("contadestinacao") && (selecionado.getEditContaDeDestinacao() || selecionado.getEditTipoContabilizacao())) {
                        integracaoEquiplanoRHVO.setContaDeDestinacao(selecionado.getContaDeDestinacao());
                        integracaoEquiplanoRHVO.setFonteDeRecursos(selecionado.getContaDeDestinacao().getFonteDeRecursos());
                    }
                    if (renderCampo("desdobramento") && (selecionado.getEditDespesaOrc() || selecionado.getEditTipoContabilizacao()))
                        integracaoEquiplanoRHVO.setDesdobramento(selecionado.getDesdobramento());
                    if (renderCampo("subconta") && (selecionado.getEditSubConta() || selecionado.getEditTipoContabilizacao()))
                        integracaoEquiplanoRHVO.setSubConta(selecionado.getSubConta());
                    if (renderCampo("fornecedor") && (selecionado.getEditFornecedor() || selecionado.getEditTipoContabilizacao()))
                        integracaoEquiplanoRHVO.setFornecedor(selecionado.getFornecedor());
                    if (renderCampo("contaextra") && (selecionado.getEditContaExtraorcamentaria() || selecionado.getEditTipoContabilizacao()))
                        integracaoEquiplanoRHVO.setContaExtraorcamentaria(selecionado.getContaExtraorcamentaria());
                    if (renderCampo("credor") && (selecionado.getEditCredor() || selecionado.getEditTipoContabilizacao()))
                        integracaoEquiplanoRHVO.setCredor(selecionado.getCredor());
                    if (renderCampo("tipocontribuicao") && (selecionado.getEditTipoContribuicao() || selecionado.getEditTipoContabilizacao()))
                        integracaoEquiplanoRHVO.setTipoContribuicao(selecionado.getTipoContribuicao());
                    if (renderCampo("operacao") && (selecionado.getEditOperacaoFormula() || selecionado.getEditTipoContabilizacao()))
                        integracaoEquiplanoRHVO.setOperacaoFormula(selecionado.getOperacaoFormula());
                    facade.salvarConfiguracao(assistente, integracaoEquiplanoRHVO, mes, exercicio, exercicio);
                }
            }
            lancarExceptionAssistente();
            buscarConfiguracoes();
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.executaJavaScript("dlgeventos.hide()");
            FacesUtil.executaJavaScript("dlgedicaolote.hide()");
            FacesUtil.executaJavaScript("fontedeevento.clearFilters()");
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        }
    }

    public void limparTipoContabilizacao() {
        selecionado.setTipoContabilizacao(tipoContabilizacao);
    }

    public void limparDespesaOrc() {
        selecionado.setDespesaORC(null);
        selecionado.setFonteDespesaORC(null);
        selecionado.setDesdobramento(null);
        selecionado.setFonteDeRecursos(null);
    }

    public void limparContaDeDestinacao() {
        selecionado.setContaDeDestinacao(null);
    }

    public void limparSubConta() {
        selecionado.setSubConta(null);
    }

    public void limparFornecedor() {
        selecionado.setFornecedor(null);
    }

    public void limparContaExtraorcamentaria() {
        selecionado.setContaExtraorcamentaria(null);
    }

    public void limparCredor() {
        selecionado.setCredor(null);
    }

    public void limparTipoContribuicao() {
        selecionado.setTipoContribuicao(null);
    }

    public void limparOperacaoFormula() {
        selecionado.setOperacaoFormula(null);
    }

    private void validarEventoLote() {
        ValidacaoException coe = new ValidacaoException();
        if (selecionado.getTipoContabilizacao() == null && selecionado.getEditTipoContabilizacao())
            coe.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Contabilização deve ser informado.");
        if (selecionado.getDespesaORC() == null && renderCampo("codigoreduzido") && (selecionado.getEditDespesaOrc() || selecionado.getEditTipoContabilizacao()))
            coe.adicionarMensagemDeCampoObrigatorio("O campo Código Reduzido deve ser informado.");
        if (selecionado.getFonteDespesaORC() == null && renderCampo("fontedespesaorc") && (selecionado.getEditDespesaOrc() || selecionado.getEditTipoContabilizacao()))
            coe.adicionarMensagemDeCampoObrigatorio("O campo Fonte de Despesa Orçamentaria deve ser informado.");
        if (selecionado.getContaDeDestinacao() == null && renderCampo("contadestinacao") && (selecionado.getEditContaDeDestinacao() || selecionado.getEditTipoContabilizacao()))
            coe.adicionarMensagemDeCampoObrigatorio("O campo Conta de Destinação deve ser informado.");
        if (selecionado.getDesdobramento() == null && renderCampo("desdobramento") && (selecionado.getEditDespesaOrc() || selecionado.getEditTipoContabilizacao()))
            coe.adicionarMensagemDeCampoObrigatorio("O campo Desdobramento deve ser informado.");
        if (selecionado.getSubConta() == null && renderCampo("subconta") && (selecionado.getEditSubConta() || selecionado.getEditTipoContabilizacao()))
            coe.adicionarMensagemDeCampoObrigatorio("O campo Conta Financeira deve ser informado.");
        if (selecionado.getFornecedor() == null && renderCampo("fornecedor") && (selecionado.getEditFornecedor() || selecionado.getEditTipoContabilizacao()))
            coe.adicionarMensagemDeCampoObrigatorio("O campo Fornecedor deve ser informado.");
        if (selecionado.getContaExtraorcamentaria() == null && renderCampo("contaextra") && (selecionado.getEditContaExtraorcamentaria() || selecionado.getEditTipoContabilizacao()))
            coe.adicionarMensagemDeCampoObrigatorio("O campo Conta Extra Orçamentaria deve ser informado.");
        if (selecionado.getCredor() == null && renderCampo("credor") && (selecionado.getEditCredor() || selecionado.getEditTipoContabilizacao()))
            coe.adicionarMensagemDeCampoObrigatorio("O campo Credor deve ser informado.");
        if (selecionado.getTipoContribuicao() == null && renderCampo("tipocontribuicao") && (selecionado.getEditTipoContribuicao() || selecionado.getEditTipoContabilizacao()))
            coe.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Contribuição deve ser informado.");
        if (selecionado.getOperacaoFormula() == null && renderCampo("operacao") && (selecionado.getEditOperacaoFormula() || selecionado.getEditTipoContabilizacao()))
            coe.adicionarMensagemDeCampoObrigatorio("O campo Operação deve ser informado.");
        coe.lancarException();
        ValidacaoException onpe = new ValidacaoException();
        if (!(selecionado.getEditTipoContabilizacao() || selecionado.getEditDespesaOrc() || selecionado.getEditSubConta() || selecionado.getEditFornecedor()
            || selecionado.getEditTipoContribuicao()
            || selecionado.getEditTipoContribuicao() || selecionado.getEditOperacaoFormula()))
            onpe.adicionarMensagemDeCampoObrigatorio("Nenhum campo selecionado para alteração. Para sair clique em Cancelar.");

        onpe.lancarException();
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItemSemCampoVazio(Mes.values(), false);
    }

    public List<SelectItem> getTiposCopia() {
        return Util.getListSelectItemSemCampoVazio(TipoCopia.values());
    }

    public List<SelectItem> getOperacoesFormula() {
        return Util.getListSelectItem(OperacaoFormula.values());
    }

    public List<SelectItem> getTiposContribuicao() {
        return Util.getListSelectItem(TipoContribuicao.values());
    }

    public List<SelectItem> getTiposContabilizacao() {
        return Util.getListSelectItem(TipoContabilizacao.values());
    }

    public List<FonteDespesaORC> completarFontesDespesaORC(String filtro) {
        return facade.completarFontesDespesaORC(filtro, selecionado.getDespesaORC());
    }

    public List<Conta> completarDesdobramentos(String filtro) {
        return facade.completarDesdobramentos(filtro, selecionado.getDespesaORC());
    }

    public List<VinculoFP> completarVinculosFP(String filtro) {
        return facade.completarVinculosFP(filtro);
    }

    public List<Conta> completarContaExtraorcamentaria(String filtro) {
        return facade.completarContaExtraorcamentaria(filtro);
    }

    public List<ContaDeDestinacao> completarContasDeDestinacao(String filtro) {
        return facade.completarContasDeDestinacao(exercicio, filtro);
    }

    public List<SelectItem> getTiposFolhaDePagamento() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

    public List<IntegracaoEquiplanoRHVO> getConfiguracoes() {
        return configuracoes;
    }

    public void setConfiguracoes(List<IntegracaoEquiplanoRHVO> configuracoes) {
        this.configuracoes = configuracoes;
    }

    public List<IntegracaoEquiplanoRHVO> getConfiguracoesFilter() {
        return configuracoesFilter;
    }

    public void setConfiguracoesFilter(List<IntegracaoEquiplanoRHVO> configuracoesFilter) {
        this.configuracoesFilter = configuracoesFilter;
    }

    public List<RecursoFP> completarRecursosFP(String filtro) {

        if (configuracoes == null || configuracoes.isEmpty()) {
            return new ArrayList<>();
        }

        List<RecursoFP> list = new ArrayList<>();
        Set<RecursoFP> uniqueValues = new HashSet<>();
        for (IntegracaoEquiplanoRHVO obj : configuracoes) {
            if (obj.getRecursoFP().toString().toLowerCase().contains(filtro.toLowerCase())) {
                RecursoFP fp = obj.getRecursoFP();
                if (uniqueValues.add(fp)) {
                    list.add(fp);
                }
            }
        }
        return list;
    }

    public IntegracaoEquiplanoRHVO getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(IntegracaoEquiplanoRHVO selecionado) {
        this.selecionado = selecionado;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Mes getMesOrigem() {
        return mesOrigem;
    }

    public void setMesOrigem(Mes mesOrigem) {
        this.mesOrigem = mesOrigem;
    }

    public Exercicio getExercicioOrigem() {
        return exercicioOrigem;
    }

    public void setExercicioOrigem(Exercicio exercicioOrigem) {
        this.exercicioOrigem = exercicioOrigem;
    }

    public Mes getMesDestino() {
        return mesDestino;
    }

    public void setMesDestino(Mes mesDestino) {
        this.mesDestino = mesDestino;
    }

    public Exercicio getExercicioDestino() {
        return exercicioDestino;
    }

    public void setExercicioDestino(Exercicio exercicioDestino) {
        this.exercicioDestino = exercicioDestino;
    }

    public TipoCopia getTipoCopia() {
        return tipoCopia;
    }

    public void setTipoCopia(TipoCopia tipoCopia) {
        this.tipoCopia = tipoCopia;
    }

    public RecursoFP getRecursoFP() {
        return recursoFP;
    }

    public void setRecursoFP(RecursoFP recursoFP) {
        this.recursoFP = recursoFP;
    }

    public TipoContabilizacao getTipoContabilizacao() {
        return tipoContabilizacao;
    }

    public void setTipoContabilizacao(TipoContabilizacao tipoContabilizacao) {
        this.tipoContabilizacao = tipoContabilizacao;
    }

    public List<EventoFP> getEventosFP() {
        return eventosFP;
    }

    public void setEventosFP(List<EventoFP> eventosFP) {
        this.eventosFP = eventosFP;
    }

    public EventoFP[] getEventosFPSelecionados() {
        return eventosFPSelecionados;
    }

    public void setEventosFPSelecionados(EventoFP[] eventosFPSelecionados) {
        this.eventosFPSelecionados = eventosFPSelecionados;
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public List<SituacaoCadastralPessoa> getSituacoesDisponiveis() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    public enum TipoCopia {
        COPIA_SUBSTITUICAO("Substituição"),
        COPIA_NAO_CONFIGURADO("Não Configurados");

        private String descricao;

        TipoCopia(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public void gerarRelatorioRelacaoEmpenho(String tipoRelatorioExtensao) {
        try {
            validarCamposGerarRelatorio();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", facade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "RECURSOS HUMANOS");
            dto.adicionarParametro("NOMERELATORIO", "RELAÇÃO DE EMPENHOS");
            dto.adicionarParametro("MES", mes.name());
            dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno());
            dto.adicionarParametro("TIPO_FOLHA", tipoFolhaDePagamento.name());
            dto.adicionarParametro("FILTROS", mes.getNumeroMesString() + "/" + exercicio.getAno() + ", Tipo de Folha: " + tipoFolhaDePagamento.getDescricao());
            dto.setNomeRelatorio("RELACAO_DE_EMPENHOS_" + mes.getNumeroMesString() + "/" + exercicio.getAno() + "_" + tipoFolhaDePagamento.getDescricao().toUpperCase());
            dto.setApi("contabil/conferencia-integracao-rh-contabil/");
            ReportService.getInstance().gerarRelatorio(facade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
            cancelarDialog("dlgrelatorioempenho");
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorioInconcistencia(String tipoRelatorioExtensao) {
        try {
            validarCamposGerarRelatorio();
            FolhaDePagamento folha = facade.buscarFolhaDePagamento(mes, exercicio, tipoFolhaDePagamento);
            validarFolhaDePagamento(folha);
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", facade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "RECURSOS HUMANOS");
            dto.adicionarParametro("NOMERELATORIO", "Relatório de Inconsistências da Integração Folha " + tipoFolhaDePagamento.getDescricao() + ". Competência " + mes.getDescricao() + "/" + exercicio.getAno());
            dto.adicionarParametro("MES", mes.name());
            dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno());
            dto.adicionarParametro("TIPO_FOLHA", tipoFolhaDePagamento.name());
            dto.adicionarParametro("ID_FOLHA", folha.getId());
            dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(facade.getDataOperacao()));
            dto.adicionarParametro("FILTROS", mes.getNumeroMesString() + "/" + exercicio.getAno() + ", Tipo de Folha: " + tipoFolhaDePagamento.getDescricao());
            dto.setNomeRelatorio("RELATORIO_INCONSISTENCIAS_INTEGRACAO_FOLHA_" + mes.getNumeroMesString() + "/" + exercicio.getAno() + "_" + tipoFolhaDePagamento.getDescricao().toUpperCase());
            dto.setApi("rh/inconsistencia-integracao-rh-contabil/");
            ReportService.getInstance().gerarRelatorio(facade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
            cancelarDialog("dlgrelatorio");
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCamposGerarRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informao.");
        }
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (tipoFolhaDePagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha de Pagamento deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarFolhaDePagamento(FolhaDePagamento folha) {
        ValidacaoException ve = new ValidacaoException();
        if (folha == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Folha de Pagamento para a competência de " + mes.getNumeroMesString() + "/" + exercicio.getAno());
        }
        ve.lancarException();
    }

    public List<RecursoFP> completarRecursosFPs(String parte) {
        return facade.buscarRecursosFP(parte, mes, exercicio);
    }

    public List<EventoFP> completarEventos(String parte) {
        if (filtroRecursos != null && !filtroRecursos.isEmpty()) {
            List<Long> idsRecursos = filtroRecursos.stream().map(RecursoFP::getId).collect(Collectors.toList());
            return facade.buscarEventos(parte, mes, exercicio.getAno(), idsRecursos);
        }
        return facade.buscarEventos(parte, mes, exercicio.getAno(), null);
    }

    public void limparFiltroEventos() {
        filtroEventos = Lists.newArrayList();
    }

    public void cancelarDialog(String dialog) {
        tipoFolhaDePagamento = null;
        FacesUtil.executaJavaScript(dialog + ".hide()");
    }

    public List<RecursoFP> getFiltroRecursos() {
        return filtroRecursos;
    }

    public void setFiltroRecursos(List<RecursoFP> filtroRecursos) {
        this.filtroRecursos = filtroRecursos;
    }

    public List<EventoFP> getFiltroEventos() {
        return filtroEventos;
    }

    public void setFiltroEventos(List<EventoFP> filtroEventos) {
        this.filtroEventos = filtroEventos;
    }

    public Boolean getApenasEventosCadastrados() {
        return apenasEventosCadastrados;
    }

    public void setApenasEventosCadastrados(Boolean apenasEventosCadastrados) {
        this.apenasEventosCadastrados = apenasEventosCadastrados;
    }
}
