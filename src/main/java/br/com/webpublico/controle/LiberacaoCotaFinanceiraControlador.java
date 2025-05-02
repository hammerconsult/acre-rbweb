/*
 * Codigo gerado automaticamente em Fri Mar 09 08:07:27 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.IConciliar;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LiberacaoCotaFinanceiraFacade;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@Etiqueta("Liberação de Cota Financeira")
@URLMappings(mappings = {
    @URLMapping(id = "novo-liberacao-cota-financeira", pattern = "/liberacao-financeira/novo/", viewId = "/faces/financeiro/orcamentario/liberacaocotafinanceira/edita.xhtml"),
    @URLMapping(id = "editar-liberacao-cota-financeira", pattern = "/liberacao-financeira/editar/#{liberacaoCotaFinanceiraControlador.id}/", viewId = "/faces/financeiro/orcamentario/liberacaocotafinanceira/edita.xhtml"),
    @URLMapping(id = "ver-liberacao-cota-financeira", pattern = "/liberacao-financeira/ver/#{liberacaoCotaFinanceiraControlador.id}/", viewId = "/faces/financeiro/orcamentario/liberacaocotafinanceira/visualizar.xhtml"),
    @URLMapping(id = "listar-liberacao-cota-financeira", pattern = "/liberacao-financeira/listar/", viewId = "/faces/financeiro/orcamentario/liberacaocotafinanceira/lista.xhtml"),
    @URLMapping(id = "nova-liberacao-da-solicitacao", pattern = "/liberacao-financeira/nova-solicitacao/#{liberacaoCotaFinanceiraControlador.id}/notificacao/#{liberacaoCotaFinanceiraControlador.notificacao}/", viewId = "/faces/financeiro/orcamentario/liberacaocotafinanceira/edita.xhtml")
})
public class LiberacaoCotaFinanceiraControlador extends PrettyControlador<LiberacaoCotaFinanceira> implements Serializable, CRUD, IConciliar {

    @EJB
    private LiberacaoCotaFinanceiraFacade liberacaoCotaFinanceiraFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private MoneyConverter moneyConverter;
    private SolicitacaoCotaFinanceira solicitacaoCotaFinanceira;
    private List<SolicitacaoCotaFinanceira> listaSolicitacoes;
    private BigDecimal saldoSolicitacao;
    private ContaBancariaEntidade contaBancariaEntidadeConcedida;
    private ContaBancariaEntidade contaBancariaEntidadeRecebida;
    private Long notificacao;
    private Notificacao notificacaoRecuperada;
    private List<LiberacaoCotaFinanceira> liberacoes;

    public LiberacaoCotaFinanceiraControlador() {
        super(LiberacaoCotaFinanceira.class);
    }

    public void setarContaBancariaConcedida() {
        try {
            contaBancariaEntidadeConcedida = selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira().getContaVinculada().getContaBancariaEntidade();
        } catch (Exception e) {
        }
    }

    public void setarContaBancariaRecebida() {
        try {
            contaBancariaEntidadeRecebida = selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira().getContaBancariaEntidade();
        } catch (Exception e) {
        }
    }

    public Boolean getHabilitaEditar() {
        LiberacaoCotaFinanceira lib = ((LiberacaoCotaFinanceira) selecionado);
        if (lib.getStatusPagamento() == null) {
            return true;
        }
        if (lib.getStatusPagamento().equals(StatusPagamento.INDEFERIDO)
            || lib.getStatusPagamento().equals(StatusPagamento.DEFERIDO)
            || lib.getStatusPagamento().equals(StatusPagamento.ABERTO)) {
            return false;
        }
        return true;
    }

    public Boolean getHabilitaBotaoEditar() {
        LiberacaoCotaFinanceira lib = ((LiberacaoCotaFinanceira) selecionado);
        if (lib.getStatusPagamento() == null) {
            return false;
        }
        return false;
    }

    public Boolean getHabilitaExcluir() {
        LiberacaoCotaFinanceira lib = ((LiberacaoCotaFinanceira) selecionado);
        Boolean controle = false;
        if (lib.getStatusPagamento() == null) {
            return controle;
        }
        if (lib.getStatusPagamento().equals(StatusPagamento.ABERTO)) {
            controle = true;
        }
        return controle;
    }

    public Boolean habilitaBotaoEditarNoLista(LiberacaoCotaFinanceira lib) {
        if (lib.getStatusPagamento() == null) {
            return false;
        }
        if (lib.getStatusPagamento().equals(StatusPagamento.INDEFERIDO)
            || lib.getStatusPagamento().equals(StatusPagamento.DEFERIDO)
            || lib.getStatusPagamento().equals(StatusPagamento.ABERTO)) {
            return true;
        }
        return false;
    }

    public LiberacaoCotaFinanceiraFacade getFacade() {
        return liberacaoCotaFinanceiraFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/liberacao-financeira/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "ver-liberacao-cota-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "editar-liberacao-cota-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
        recuperarLiberacoes();
    }

    @URLAction(mappingId = "nova-liberacao-da-solicitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoLiberacao() {
        novo();
        selecionado.setSolicitacaoCotaFinanceira(liberacaoCotaFinanceiraFacade.getSolicitacaoCotaFinanceiraFacade().recuperar(super.getId()));
        setaEventoConcedido();
        setaEventoRecebido();
        selecionado.setTipoDocumento(TipoDocPagto.ORDEMPAGAMENTO);
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
        selecionado.setValor(selecionado.getSolicitacaoCotaFinanceira().getSaldo());
//        notificacaoRecuperada = NotificacaoService.getService().recuperar(notificacao);
        setarContaBancariaConcedida();
        setarContaBancariaRecebida();
        atualizarContaDeDestinacao();
        recuperarLiberacoes();
    }

    @Override
    public void cancelar() {
        super.cancelar();
    }

    @Override
    public AbstractFacade getFacede() {
        return liberacaoCotaFinanceiraFacade;
    }

    @URLAction(mappingId = "novo-liberacao-cota-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUsuarioSistema(getUsuarioSistema());
        selecionado.setDataLiberacao(sistemaControlador.getDataOperacao());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        solicitacaoCotaFinanceira = new SolicitacaoCotaFinanceira();

//        if (liberacaoCotaFinanceiraFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
//            FacesUtil.addWarn("Aviso! ", liberacaoCotaFinanceiraFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
//        }
    }

    public void carregaListaSolicitacao() {
        listaSolicitacoes = liberacaoCotaFinanceiraFacade.getSolicitacaoCotaFinanceiraFacade().buscarSolicitacaosAbertaPorUnidade(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), sistemaControlador.getExercicioCorrente());
    }

    private Boolean validaFontes() {
        if (!selecionado.getFonteDeRecursoRetirada().equals(selecionado.getFonteDeRecursoRecebida())) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Não é possivel efetuar uma liberação para Fontes de Recurso diferentes.");
            return false;
        }
        return true;
    }

    public Boolean verificaContasBancariasDaContaFinanceira() {
        if (selecionado.getSolicitacaoCotaFinanceira() != null) {
            if (selecionado.getContaFinanceiraRetirada() == null) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Não Existe uma conta vinculada à Conta Financeira: " + selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira() + ".");
                return false;
            }
            if (liberacaoCotaFinanceiraFacade.isMesmaContaBancaria(selecionado.getContaFinanceiraRecebida().getContaBancariaEntidade(), selecionado.getContaFinanceiraRetirada().getContaBancariaEntidade())) {
                return false;
            }
        }
        if (selecionado.getSolicitacaoCotaFinanceira() == null) {
            return false;
        }
        return true;
    }

    @Override
    public void salvar() {
        try {
            atribuirValoresNaLiberacao();
            validarLiberacaoFinanceira();
            validarSolicitacao();
            validarDataConciliacao();
            if (isOperacaoNovo()) {
                validarIntegracaoBancaria();
                liberacaoCotaFinanceiraFacade.salvarNovo(selecionado);
            } else {
                liberacaoCotaFinanceiraFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(" A Liberação Financeira " + this.selecionado.toString() + " foi salva com sucesso.");
            limparNotificacoes();
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            liberacaoCotaFinanceiraFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(ex);
            liberacaoCotaFinanceiraFacade.getSingletonConcorrenciaContabil().desbloquear(selecionado.getSolicitacaoCotaFinanceira());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            liberacaoCotaFinanceiraFacade.getSingletonConcorrenciaContabil().desbloquear(selecionado.getSolicitacaoCotaFinanceira());
        }
    }

    private void limparNotificacoes() {
        try {
            liberacaoCotaFinanceiraFacade.movimentarNotificacoes(selecionado, notificacaoRecuperada);
        } catch (Exception e) {
            FacesUtil.addError("Operação Não Realizada ao criar as notificações!", e.getMessage());
        }
    }

    private void validarSolicitacao() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.getExercicio().equals(selecionado.getSolicitacaoCotaFinanceira().getExercicio())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O exercício da solicitação financeira (" + selecionado.getSolicitacaoCotaFinanceira().getExercicio().getAno() + ") é diferente do exercício logado (" + sistemaControlador.getExercicioCorrente().getAno() + ").");
        }
        if (selecionado.getValor().compareTo(selecionado.getSolicitacaoCotaFinanceira().getValorSolicitado()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor da Liberação de " + "<b>"
                + Util.formataValor(selecionado.getValor()) + "</b>" + " não pode ser maior que o valor da solicitação de " + "<b>"
                + Util.formataValor(selecionado.getSolicitacaoCotaFinanceira().getValorSolicitado()) + "</b>");
        }
        if (selecionado.getValor().compareTo(selecionado.getSolicitacaoCotaFinanceira().getSaldo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor da Liberação de " + "<b>"
                + Util.formataValor(selecionado.getValor()) + "</b>" + " excede o saldo de " + "<b>"
                + Util.formataValor(selecionado.getSolicitacaoCotaFinanceira().getSaldo()) + "</b> " + "  disponível para essa solicitação financeira.");
        }
        ve.lancarException();
    }

    private void validarLiberacaoFinanceira() {
        selecionado.realizarValidacoes();
        validarCampos();
    }


    private void validarIntegracaoBancaria() {
        if (!liberacaoCotaFinanceiraFacade.isMesmaContaBancaria(selecionado.getContaFinanceiraRecebida().getContaBancariaEntidade(), selecionado.getContaFinanceiraRetirada().getContaBancariaEntidade())) {
            if (selecionado.getTipoDocumento() == null && !selecionado.getTipoOperacaoPagto().equals(TipoOperacaoPagto.NAO_APLICAVEL)) {
                throw new ExcecaoNegocioGenerica(" O campo Tipo de Documento deve ser informado!");
            }
            if (selecionado.getTipoOperacaoPagto() == null) {
                throw new ExcecaoNegocioGenerica(" O campo Tipo de Operação deve ser informado!");
            }
            if (selecionado.getFinalidadePagamento() == null && !selecionado.getTipoOperacaoPagto().equals(TipoOperacaoPagto.NAO_APLICAVEL)) {
                throw new ExcecaoNegocioGenerica(" O campo Finalidade do Pagamento deve ser informado!");
            }
        }
    }

    private void atribuirValoresNaLiberacao() {
        if (selecionado.getSolicitacaoCotaFinanceira() != null) {
            selecionado.setSubConta(selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira().getContaVinculada());
            selecionado.setUnidadeOrganizacional(selecionarUnidade());
            selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
            selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
            selecionado.getSolicitacaoCotaFinanceira().setHistoricoLiberacao(selecionado.getObservacoes());
            selecionado.setSaldo(selecionado.getValor());
        }
    }

    public String setaEventoRecebido() {
        ConfigLiberacaoFinanceira configuracao;
        selecionado.setEventoContabilDeposito(null);
        try {
            if (selecionado.getTipoLiberacaoFinanceira() != null && selecionado.getSolicitacaoCotaFinanceira() != null) {
                configuracao = liberacaoCotaFinanceiraFacade.getConfigLiberacaoFinanceiraFacade().recuperaEvento(TipoLancamento.NORMAL, selecionado.getTipoLiberacaoFinanceira(), OrigemTipoTransferencia.RECEBIDO, selecionado.getDataLiberacao(), selecionado.getSolicitacaoCotaFinanceira().getResultanteIndependente());
                selecionado.setEventoContabilDeposito(configuracao.getEventoContabil());
                return selecionado.getEventoContabilDeposito().toString();
            } else {
                return "Nenhum Evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }

    public String setaEventoConcedido() {
        ConfigLiberacaoFinanceira configuracao;
        selecionado.setEventoContabilRetirada(null);
        try {
            if (selecionado.getTipoLiberacaoFinanceira() != null && selecionado.getSolicitacaoCotaFinanceira() != null) {
                configuracao = liberacaoCotaFinanceiraFacade.getConfigLiberacaoFinanceiraFacade().recuperaEvento(TipoLancamento.NORMAL, selecionado.getTipoLiberacaoFinanceira(), OrigemTipoTransferencia.CONCEDIDO, selecionado.getDataLiberacao(), selecionado.getSolicitacaoCotaFinanceira().getResultanteIndependente());
                selecionado.setEventoContabilRetirada(configuracao.getEventoContabil());
                return selecionado.getEventoContabilRetirada().toString();
            } else {
                return "Nenhum Evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Valor liberado deve ser maior que zero(0)");
        }
        if (selecionado.getDataConciliacao() != null) {
            if (Util.getDataHoraMinutoSegundoZerado(selecionado.getDataConciliacao()).before(Util.getDataHoraMinutoSegundoZerado(selecionado.getDataLiberacao()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Data de conciliação concedida não pode ser em dia anterior à data da transferência.");
            }
        }
        if (selecionado.getRecebida() != null) {
            if (Util.getDataHoraMinutoSegundoZerado(selecionado.getRecebida()).before(Util.getDataHoraMinutoSegundoZerado(selecionado.getDataLiberacao()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Data de conciliação recebida não pode ser em dia anterior à data da transferência.");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void recuperaEditaVer() {
        operacao = Operacoes.EDITAR;
        selecionado = liberacaoCotaFinanceiraFacade.recuperar(selecionado.getId());
        setarContaBancariaConcedida();
        setarContaBancariaRecebida();
        solicitacaoCotaFinanceira = selecionado.getSolicitacaoCotaFinanceira();

    }

    public List<SolicitacaoCotaFinanceira> completarSolicitacaoFinanceira(String parte) {
        return liberacaoCotaFinanceiraFacade.getSolicitacaoCotaFinanceiraFacade().buscarSolicitacoesAprovadasPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<FinalidadePagamento> completaFinalidadePagamento(String parte) {
        return liberacaoCotaFinanceiraFacade.getFinalidadePagamentoFacade().listaFiltrando(parte.trim(), "codigo", "codigoOB", "descricao");
    }

    public List<SolicitacaoCotaFinanceira> listaSolicitacoesAbertas() {
        return listaSolicitacoes = liberacaoCotaFinanceiraFacade.getSolicitacaoCotaFinanceiraFacade().buscarSolicitacaosAbertaPorUnidade(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), sistemaControlador.getExercicioCorrente());
    }

    public List<SelectItem> getContasDeDestinacaoPorSubConta() {
        List<SelectItem> dados = Lists.newArrayList();
        if (selecionado.getSolicitacaoCotaFinanceira() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O Campo Solicitação Financeira deve ser informado."));
            return dados;
        } else if (selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira() != null &&
            selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira().getContaVinculada() != null &&
            selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira().getContaVinculada().getId() != null) {
            SubConta subConta = liberacaoCotaFinanceiraFacade.getSubContaFacade().recuperar(selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira().getContaVinculada().getId());
            dados.add(new SelectItem(null, ""));
            for (SubContaFonteRec subContaFonteRec : subConta.getSubContaFonteRecs()) {
                if (subContaFonteRec.getContaDeDestinacao().getExercicio().equals(sistemaControlador.getExercicioCorrente())) {
                    dados.add(new SelectItem(subContaFonteRec.getContaDeDestinacao(), subContaFonteRec.getContaDeDestinacao().toString()));
                }
            }
        }
        return dados;
    }

    public void atualizarFonteComContaDeDestinacao() {
        if (selecionado.getContaDeDestinacao() != null) {
            selecionado.setFonteDeRecursos(selecionado.getContaDeDestinacao().getFonteDeRecursos());
        }
    }

    public List<SelectItem> getListaTipoLiberacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoLiberacaoFinanceira tfl : TipoLiberacaoFinanceira.values()) {
            toReturn.add(new SelectItem(tfl, tfl.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoOperacaoPagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoOperacaoPagto tipoOperacaoPagto : TipoOperacaoPagto.values()) {
            toReturn.add(new SelectItem(tipoOperacaoPagto, tipoOperacaoPagto.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoDocumento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(TipoDocPagto.ORDEMPAGAMENTO, TipoDocPagto.ORDEMPAGAMENTO.getDescricao()));
        return toReturn;
    }

    public Boolean verificaConta() {
        if (selecionado.getSolicitacaoCotaFinanceira() == null) {
            return false;
        }
        if (selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira() == null) {
            return false;
        }
        if (selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira().getContaVinculada() == null) {
            return false;
        }
        return true;
    }

    public UsuarioSistema getUsuarioSistema() {
        return sistemaControlador.getUsuarioCorrente();
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public SolicitacaoCotaFinanceira getSolicitacaoCotaFinanceira() {
        return solicitacaoCotaFinanceira;
    }

    public void setSolicitacaoCotaFinanceira(SolicitacaoCotaFinanceira solicitacaoCotaFinanceira) {
        this.solicitacaoCotaFinanceira = solicitacaoCotaFinanceira;
    }

    public List<SolicitacaoCotaFinanceira> getListaSolicitacoes() {
        return listaSolicitacoes;
    }

    public void setListaSolicitacoes(List<SolicitacaoCotaFinanceira> listaSolicitacoes) {
        this.listaSolicitacoes = listaSolicitacoes;
    }

    public BigDecimal getSaldoSolicitacao() {
        return saldoSolicitacao;
    }

    public void setSaldoSolicitacao(BigDecimal saldoSolicitacao) {
        this.saldoSolicitacao = saldoSolicitacao;
    }

    public void setaSolicitacaoCotaFinanceira(SelectEvent evt) {
        selecionado.setSolicitacaoCotaFinanceira((SolicitacaoCotaFinanceira) evt.getObject());
        setarContaBancariaConcedida();
        setarContaBancariaRecebida();
        selecionado.setValor(((SolicitacaoCotaFinanceira) evt.getObject()).getSaldo());
        selecionado.setObservacoes(((SolicitacaoCotaFinanceira) evt.getObject()).getHistoricoSolicitacao());
        atualizarContaDeDestinacao();
        if (!verificaConta()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Não Existe uma conta vinculada à Conta Financeira: " + selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira() + ".");
        }
        recuperarLiberacoes();
    }

    private void atualizarContaDeDestinacao() {
        try {
            List<SelectItem> contasDeDestinacao = getContasDeDestinacaoPorSubConta();
            if (!contasDeDestinacao.isEmpty()) {
                selecionado.setContaDeDestinacao((ContaDeDestinacao) contasDeDestinacao.get(0).getValue());
                atualizarFonteComContaDeDestinacao();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void selecionarSolicitacao(ActionEvent actionEvent) {
        selecionado.setSolicitacaoCotaFinanceira((SolicitacaoCotaFinanceira) actionEvent.getComponent().getAttributes().get("objeto"));
        setarContaBancariaConcedida();
        setarContaBancariaRecebida();
        selecionado.setValor(selecionado.getSolicitacaoCotaFinanceira().getSaldo());
        selecionado.setObservacoes(selecionado.getSolicitacaoCotaFinanceira().getHistoricoSolicitacao());
        if (!verificaConta()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Não Existe uma conta vinculada à  Conta Financeira: " + selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira() + ".");
        }
        recuperarLiberacoes();
    }

    public void redirecionaParaLista() {
        FacesUtil.redirecionamentoInterno("/liberacao-financeira/listar/");
    }

    public UnidadeOrganizacional selecionarUnidade() {
        return liberacaoCotaFinanceiraFacade.recuperaUnidadeDaSubContaRetirada(selecionado, sistemaControlador.getExercicioCorrente());
    }

    public ContaBancariaEntidade getContaBancariaEntidadeConcedida() {
        return contaBancariaEntidadeConcedida;
    }

    public void setContaBancariaEntidadeConcedida(ContaBancariaEntidade contaBancariaEntidadeConcedida) {
        this.contaBancariaEntidadeConcedida = contaBancariaEntidadeConcedida;
    }

    public ContaBancariaEntidade getContaBancariaEntidadeRecebida() {
        return contaBancariaEntidadeRecebida;
    }

    public void setContaBancariaEntidadeRecebida(ContaBancariaEntidade contaBancariaEntidadeRecebida) {
        this.contaBancariaEntidadeRecebida = contaBancariaEntidadeRecebida;
    }

    public Long getNotificacao() {
        return notificacao;
    }

    public void setNotificacao(Long notificacao) {
        this.notificacao = notificacao;
    }

    public Notificacao getNotificacaoRecuperada() {
        return notificacaoRecuperada;
    }

    public void setNotificacaoRecuperada(Notificacao notificacaoRecuperada) {
        this.notificacaoRecuperada = notificacaoRecuperada;
    }

    public void setarFinalidade(ActionEvent evento) {
        selecionado.setFinalidadePagamento((FinalidadePagamento) evento.getComponent().getAttributes().get("objeto"));
    }

    public Boolean mostrarBotaoBaixar() {
        if (selecionado.getId() != null
            && selecionado.getStatusPagamento().equals(StatusPagamento.DEFERIDO)) {
            return true;
        }
        return false;
    }

    public Boolean mostrarBotaoEstornarBaixar() {
        if (selecionado.getId() != null
            && selecionado.getStatusPagamento().equals(StatusPagamento.PAGO)) {
            return true;
        }
        return false;
    }

    @Override
    public void baixar() {
        try {
            validarLiberacaoFinanceira();
            validarDataConciliacao();
            liberacaoCotaFinanceiraFacade.baixar(selecionado);
            FacesUtil.addOperacaoRealizada(" A Liberação Financeira N° <b>" + selecionado.getNumero() + "</b> foi baixada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarDataConciliacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataConciliacao() != null && DataUtil.getAno(selecionado.getDataLiberacao()).compareTo(DataUtil.getAno(selecionado.getDataConciliacao())) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conciliação está com o exercício diferente da data do registro.");
        }
        if (selecionado.getRecebida() != null && DataUtil.getAno(selecionado.getDataLiberacao()).compareTo(DataUtil.getAno(selecionado.getRecebida())) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conciliação está com o exercício diferente da data do registro.");
        }
        ve.lancarException();
    }

    @Override
    public void estornarBaixa() {
        try {
            validarLiberacaoFinanceira();
            liberacaoCotaFinanceiraFacade.estornarBaixa(selecionado);
            FacesUtil.addOperacaoRealizada(" A Liberação Financeira N° <b>" + selecionado.getNumero() + "</b> foi estornada a baixa com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public Boolean mostrarLiberacoes() {
        return liberacoes != null && !liberacoes.isEmpty();
    }

    private void recuperarLiberacoes() {
        if (selecionado.getSolicitacaoCotaFinanceira() != null) {
            liberacoes = liberacaoCotaFinanceiraFacade.buscarLiberacaoPorSolicitacao(selecionado.getSolicitacaoCotaFinanceira());
            if (selecionado.getId() != null) {
                liberacoes.remove(selecionado);
            }
        }
    }

    public List<LiberacaoCotaFinanceira> getLiberacoes() {
        return liberacoes;
    }

    public void setLiberacoes(List<LiberacaoCotaFinanceira> liberacoes) {
        this.liberacoes = liberacoes;
    }
}
