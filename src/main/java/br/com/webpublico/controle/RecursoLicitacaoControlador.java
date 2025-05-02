/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RecursoLicitacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author renato
 */
@ManagedBean(name = "recursoLicitacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-recurso-licitacao", pattern = "/recurso-licitacao/novo/", viewId = "/faces/administrativo/licitacao/recurso/edita.xhtml"),
    @URLMapping(id = "editar-recurso-licitacao", pattern = "/recurso-licitacao/editar/#{recursoLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/recurso/edita.xhtml"),
    @URLMapping(id = "ver-recurso-licitacao", pattern = "/recurso-licitacao/ver/#{recursoLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/recurso/visualizar.xhtml"),
    @URLMapping(id = "listar-recurso-licitacao", pattern = "/recurso-licitacao/listar/", viewId = "/faces/administrativo/licitacao/recurso/lista.xhtml")
})
public class RecursoLicitacaoControlador extends PrettyControlador<RecursoLicitacao> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(RecursoLicitacaoControlador.class);

    @EJB
    private RecursoLicitacaoFacade recursoLicitacaoFacade;

    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;

    public RecursoLicitacaoControlador() {
        super(RecursoLicitacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return recursoLicitacaoFacade;
    }

    public List<SelectItem> getTipoJulgamentoRecurso() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoJulgamentoRecurso object : TipoJulgamentoRecurso.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoRecurso() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoRecursoLicitacao object : TipoRecursoLicitacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoSituacaoLicitacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoSituacaoLicitacao.ANULADA, TipoSituacaoLicitacao.ANULADA.getDescricao()));
        toReturn.add(new SelectItem(TipoSituacaoLicitacao.CANCELADA, TipoSituacaoLicitacao.CANCELADA.getDescricao()));
        toReturn.add(new SelectItem(TipoSituacaoLicitacao.SUSPENSA, TipoSituacaoLicitacao.SUSPENSA.getDescricao()));
        return toReturn;
    }

    public List<Licitacao> buscarLicitacoes(String parte) {
        return recursoLicitacaoFacade.getLicitacaoFacade().buscarLicitacoesPorModalidadeAndNumeroOrDescricaoOrExercico(parte.trim());
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            recursoLicitacaoFacade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
            if (selecionado.isTipoRecursoRecurso()) {
                carregarListasDaLicitacaoSelecionada();
                if (!selecionado.temTipoJulgamentoInformado()) {
                    criarNovoStatusParaLicitacaoSelecionadaReferenteAoRecursoDoTipoRecursoSemJulgamento(selecionado.getLicitacao());
                }
                if (selecionado.isTipoJulgamentoIndeferido()) {
                    criarNovoStatusParaLicitacaoSelecionadaReferenteAoRecursoDoTipoRecursoJulgadoEIndeferido(selecionado.getLicitacao());
                }
                if (selecionado.isTipoJulgamentoDeferido()) {
                    if (podeAbrirDialogDeCancelamento()) {
//                        FacesUtil.executaJavaScript("dialogRecursoDeferido.show()");
//                        return;
                        naoCancelarLicitacao();
                    }
                }

                super.salvar();
            }
        } catch (StatusLicitacaoException se) {
            redireciona();
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private boolean podeAbrirDialogDeCancelamento() {
        return isNovo() || (isEditando() && isSituacaoAtualEmRecursoCriadaParaEsteRecurso());
    }

    private void carregarListasDaLicitacaoSelecionada() {
        selecionado.setLicitacao(recursoLicitacaoFacade.getLicitacaoFacade().recuperar(selecionado.getLicitacao().getId()));
    }

    private void criarNovoStatusParaLicitacaoSelecionadaReferenteAoRecursoDoTipoRecursoSemJulgamento(Licitacao licitacao) {
        if (!isSituacaoAtualEmRecursoCriadaParaEsteRecurso()) {
            criarNovoStatus(licitacao, TipoSituacaoLicitacao.EM_RECURSO, getMotivoAguardandoJulgamento());
        }
    }

    private boolean isSituacaoAtualEmRecursoCriadaParaEsteRecurso() {
        if (selecionado.getStatusAtualLicitacao() != null) {
            if (TipoSituacaoLicitacao.EM_RECURSO.equals(selecionado.getStatusAtualLicitacao().getStatusLicitacao().getTipoSituacaoLicitacao())) {
                return true;
            }
        }
        return false;
    }

    private void criarNovoStatusParaLicitacaoSelecionadaReferenteAoRecursoDoTipoRecursoJulgadoEIndeferido(Licitacao licitacao) {
        String motivoJulgadoIndeferido = "Status criado automaticamente pelo sistema para o recurso número: " + selecionado.getNumero() + ". Julgado e indeferido.";
        StatusLicitacao statusAtualLicitacao = licitacao.getStatusAtualLicitacao();
        if (selecionado.isRecursoSemStatus()) {
            criarNovoStatus(licitacao, TipoSituacaoLicitacao.EM_RECURSO, getMotivoAguardandoJulgamento());
            criarNovoStatusAndamento(licitacao, motivoJulgadoIndeferido, statusAtualLicitacao);
            criarNovoStatusAdjudicada(licitacao, motivoJulgadoIndeferido, statusAtualLicitacao);
        } else {
            StatusLicitacao statusAnteriorAoAtual = licitacao.getStatusAnteriorAoAtual();
            criarNovoStatusAndamento(licitacao, motivoJulgadoIndeferido, statusAnteriorAoAtual);
            criarNovoStatusAdjudicada(licitacao, motivoJulgadoIndeferido, statusAnteriorAoAtual);
        }
    }

    private void criarNovoStatusAndamento(Licitacao licitacao, String motivoJulgadoIndeferido, StatusLicitacao status) {
        if (status.isTipoAndamento()) {
            criarNovoStatus(licitacao, TipoSituacaoLicitacao.ANDAMENTO, motivoJulgadoIndeferido);
        }
    }

    private void criarNovoStatusAdjudicada(Licitacao licitacao, String motivoJulgadoIndeferido, StatusLicitacao status) {
        if (status.isTipoAdjudicada()) {
            criarNovoStatus(licitacao, TipoSituacaoLicitacao.ADJUDICADA, motivoJulgadoIndeferido);
        }
    }

    private void incrementarMaisUmNoNumeroDoUltimoStatusCriado() {
        RecursoStatus ultimoStatusCriado = selecionado.getStatus().get(selecionado.getStatus().size() - 1);
        ultimoStatusCriado.getStatusLicitacao().setNumero(ultimoStatusCriado.getStatusLicitacao().getNumero() + 1);
    }

    private void criarNovoStatus(Licitacao licitacao, TipoSituacaoLicitacao tipo, String motivo) {
        StatusLicitacao statusLicitacao = new StatusLicitacao();
        statusLicitacao.setLicitacao(licitacao);
        statusLicitacao.setResponsavel(recursoLicitacaoFacade.getSistemaFacade().getUsuarioCorrente());
        statusLicitacao.setDataStatus(new Date());
        statusLicitacao.setNumero(Long.valueOf(licitacao.getListaDeStatusLicitacao().size() + selecionado.getStatus().size() + Long.valueOf(getIncrementa())));
        statusLicitacao.setTipoSituacaoLicitacao(tipo);
        statusLicitacao.setMotivoStatusLicitacao(motivo);

        RecursoStatus recursoStatus = new RecursoStatus(selecionado, statusLicitacao);
        statusLicitacao.setRecursoStatus(recursoStatus);
        selecionado.setStatus(Util.adicionarObjetoEmLista(selecionado.getStatus(), recursoStatus));
    }

    private int getIncrementa() {
        return selecionado.isRecursoSemStatus() ? 1 : 0;
    }

    public void cancelarLicitacao() {
        String motivoJulgadoDeferido = "Status criado automaticamente pelo sistema para o recurso número: " + selecionado.getNumero() + ". Julgado e deferido.";
        if (isSituacaoAtualEmRecursoCriadaParaEsteRecurso()) {
            criarNovoStatus(selecionado.getLicitacao(), TipoSituacaoLicitacao.CANCELADA, motivoJulgadoDeferido);
        } else {
            criarNovoStatus(selecionado.getLicitacao(), TipoSituacaoLicitacao.EM_RECURSO, getMotivoAguardandoJulgamento());
            criarNovoStatus(selecionado.getLicitacao(), TipoSituacaoLicitacao.CANCELADA, motivoJulgadoDeferido);

            incrementarMaisUmNoNumeroDoUltimoStatusCriado();
        }
        super.salvar();
    }

    public void naoCancelarLicitacao() {
        String motivoContinuaEmAndamento = "Status criado automaticamente pelo sistema para o recurso número: " + selecionado.getNumero() + ". Licitação continua em andamento";
        if (isSituacaoAtualEmRecursoCriadaParaEsteRecurso()) {
            criarNovoStatus(selecionado.getLicitacao(), TipoSituacaoLicitacao.ANDAMENTO, motivoContinuaEmAndamento);
        } else {
            criarNovoStatus(selecionado.getLicitacao(), TipoSituacaoLicitacao.EM_RECURSO, getMotivoAguardandoJulgamento());
            criarNovoStatus(selecionado.getLicitacao(), TipoSituacaoLicitacao.ANDAMENTO, motivoContinuaEmAndamento);

            incrementarMaisUmNoNumeroDoUltimoStatusCriado();
        }
        super.salvar();
    }

    public String getMotivoAguardandoJulgamento() {
        return "Status criado automaticamente pelo sistema para o recurso número: " + selecionado.getNumero() + ". Aguardando julgamento.";
    }


    public ConverterAutoComplete getConverterPessoa() {
        return new ConverterAutoComplete(Pessoa.class, recursoLicitacaoFacade.getLicitacaoFacade().getPessoaFacade());
    }

    public ConverterAutoComplete getConverterPessoaFisica() {
        return new ConverterAutoComplete(PessoaFisica.class, recursoLicitacaoFacade.getPessoaFisicaFacade());
    }

    public List<Pessoa> completaPessoa(String parte) {
        return recursoLicitacaoFacade.getLicitacaoFacade().getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        List<PessoaFisica> retorno = new ArrayList<>();
        for (Pessoa pessoa : completaPessoa(parte)) {
            if (pessoa.isPessoaFisica()) {
                retorno.add((PessoaFisica) pessoa);
            }
        }
        return retorno;
    }

    public List<VeiculoDePublicacao> completaVeiculoDePublicacao(String parte) {
        return recursoLicitacaoFacade.getVeiculoDePublicacaoFacade().lista();
    }

    @URLAction(mappingId = "novo-recurso-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-recurso-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
    }

    @URLAction(mappingId = "editar-recurso-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        try {
            recursoLicitacaoFacade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
        } catch (StatusLicitacaoException se) {
            FacesUtil.printAllFacesMessages(se.getMensagens());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/recurso-licitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public boolean isNovo() {
        return Operacoes.NOVO.equals(operacao);
    }

    public boolean isEditando() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public boolean isVisualizar() {
        return Operacoes.VER.equals(operacao);
    }

    public boolean desabilitarSelectOneMenuResultado() {
        return isEditando() && selecionado.temTipoJulgamentoInformado();
    }

    public void validarLicitacaoSelecionada() {
        if (selecionado.getLicitacao() != null) {
            carregarListasDaLicitacaoSelecionada();
            StatusLicitacao statusAtualLicitacao = selecionado.getLicitacao().getStatusAtualLicitacao();
            if (statusAtualLicitacao.getRecursoStatus() != null) {
                validarLicitacaoComUltimoStatusEmRecurso(statusAtualLicitacao);
                validarLicitacaoComUltimoStatusCancelada(statusAtualLicitacao);
            }
        }
    }

    private void validarLicitacaoComUltimoStatusCancelada(StatusLicitacao statusAtualLicitacao) {
        if (!selecionado.equals(statusAtualLicitacao.getRecursoStatus().getRecursoLicitacao())) {
            if (TipoSituacaoLicitacao.CANCELADA.equals(statusAtualLicitacao.getTipoSituacaoLicitacao())) {
                FacesUtil.addOperacaoNaoPermitida("A licitação selecionada está cancelada.");
                selecionado.setLicitacao(null);
            }
        }
    }

    private void validarLicitacaoComUltimoStatusEmRecurso(StatusLicitacao statusAtualLicitacao) {
        if (!selecionado.equals(statusAtualLicitacao.getRecursoStatus().getRecursoLicitacao())) {
            if (TipoSituacaoLicitacao.EM_RECURSO.equals(statusAtualLicitacao.getTipoSituacaoLicitacao())) {
                FacesUtil.addOperacaoNaoPermitida("A licitação selecionada está aguardando julgamento do recurso: " + statusAtualLicitacao.getRecursoStatus().getRecursoLicitacao().getNumero());
                selecionado.setLicitacao(null);
            }
        }
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.RECURSO);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }
}
