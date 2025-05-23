package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.CorrecaoDadosAdministrativoVO;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.CorrecaoDadosAdministrativoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.AssistenteBarraProgresso;
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
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by mga on 02/04/2018.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-correcao-dados-adm", pattern = "/correcao-dados-administrativo/", viewId = "/faces/administrativo/correcao-dados/correcao-dados-adm.xhtml"),
})
public class CorrecaoDadosAdministrativoControlador implements Serializable {

    @EJB
    private CorrecaoDadosAdministrativoFacade facade;
    private CorrecaoDadosAdministrativoVO selecionado;
    private Future<CorrecaoDadosAdministrativoVO> future;

    private LoteEfetivacaoTransferenciaBem efetivacaoTransferencia;
    private Bem bemEfetivacaoTransferencia;
    private List<EventoBem> eventosEfetivacaoTransferenciaDeletados;
    private List<Class> movimentosBloqueados;
    private List<UnidadeOrganizacional> unidadesContrato;
    private List<Bem> bensBloqueados;
    private List<RequisicaoMaterial> requisicoesMateriais;

    @URLAction(mappingId = "novo-correcao-dados-adm", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = new CorrecaoDadosAdministrativoVO();
        bemEfetivacaoTransferencia = null;
        selecionado.setDataOperacao(facade.getSistemaFacade().getDataOperacao());

        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());

        buscarBloqueiosSingleton();
    }

    private void buscarBloqueiosSingleton() {
        if (movimentosBloqueados == null) {
            movimentosBloqueados = Lists.newArrayList();
            if (facade.getSingletonBloqueioPatrimonio().getMapaMovimentoUnidades() != null) {
                movimentosBloqueados.addAll(facade.getSingletonBloqueioPatrimonio().getMapaMovimentoUnidades().keySet());
            }
            if (facade.getSingletonGeradorCodigoPatrimonio().getMovimentosBloqueados() != null) {
                movimentosBloqueados.addAll(facade.getSingletonGeradorCodigoPatrimonio().getMovimentosBloqueados());
            }
        }
        if (bensBloqueados == null) {
            bensBloqueados = Lists.newArrayList();
            if (facade.getSingletonBloqueioPatrimonio().getBensBloqueados() != null) {
                bensBloqueados.addAll(facade.getSingletonBloqueioPatrimonio().getBensBloqueados());
            }
        }
        if (unidadesContrato == null) {
            unidadesContrato = Lists.newArrayList();
            if (facade.getSingletonContrato().getUnidades() != null) {
                unidadesContrato.addAll(facade.getSingletonContrato().getUnidades());
            }
        }
        if (requisicoesMateriais == null) {
            requisicoesMateriais = Lists.newArrayList();
            if (facade.getSingletonConcorrenciaMaterial().getRequisicoesMateriais() != null) {
                requisicoesMateriais.addAll(facade.getSingletonConcorrenciaMaterial().getRequisicoesMateriais());
            }
        }
    }

    public void reinicarSingletonBloqueioClass() {
        facade.getSingletonBloqueioPatrimonio().resetarBloqueioPorUnidade();
        facade.getSingletonGeradorCodigoPatrimonio().resetarBloqueio();
        movimentosBloqueados = Lists.newArrayList();
    }

    public void resetarBloqueioPorUnidade(Class classe) {
        facade.getSingletonGeradorCodigoPatrimonio().desbloquear(classe);
        movimentosBloqueados.remove(classe);
    }

    public void resetarBloqueioBens(Bem bem) {
        facade.getSingletonBloqueioPatrimonio().desbloquearBem(bem);
        bensBloqueados.remove(bem);
    }

    public void resetarBloqueioBens() {
        facade.getSingletonBloqueioPatrimonio().resetarBloqueioBem();
        bensBloqueados = Lists.newArrayList();
    }

    public void reiniciarSingletonUnidadeContrato() {
        facade.getSingletonContrato().reiniciarUnidades();
        unidadesContrato = Lists.newArrayList();
    }

    public void removerUnidadeContratoSingleton(UnidadeOrganizacional unid) {
        facade.getSingletonContrato().desbloquearUnidade(unid);
        unidadesContrato.remove(unid);
    }

    public void reiniciarSingletonSaidaRequisicaoMaterial() {
        facade.getSingletonConcorrenciaMaterial().reiniciarRequisicoes();
        requisicoesMateriais = Lists.newArrayList();
    }

    public void removerRequisicaoMaterialSingleton(RequisicaoMaterial req) {
        facade.getSingletonConcorrenciaMaterial().desbloquearRequisicao(req);
        requisicoesMateriais.remove(req);
    }

    public void consultarBensEfetivacaoTransferencia() {
        try {
            if (efetivacaoTransferencia == null) {
                FacesUtil.addOperacaoNaoPermitida("O campo efetivação de transferência deve ser informado.");
                return;
            }
            if (selecionado.getBens() != null) {
                selecionado.getBens().clear();
            }
            selecionado.setBens(facade.getLoteEfetivacaoTransferenciaBemFacade().buscarBensEfetivacaoTransferencia(efetivacaoTransferencia));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void recuperarEventoBem(Bem bem) {
        bem = facade.getBemFacade().recuperarComDependenciasEventoBem(bem.getId());
        if (bem == null) {
            FacesUtil.addOperacaoNaoPermitida("Não foi possível recuperar bem.");
            return;
        }
        bemEfetivacaoTransferencia = bem;
        eventosEfetivacaoTransferenciaDeletados = Lists.newArrayList();
    }

    public boolean isEventoTransferencia(EventoBem eventoBem) {
        return eventoBem != null
            && (eventoBem.getTipoEventoBem().equals(TipoEventoBem.TRANSFERENCIABEMCONCEDIDA)
            || eventoBem.getTipoEventoBem().equals(TipoEventoBem.TRANSFERENCIADEPRECIACAOCONCEDIDA)
            || eventoBem.getTipoEventoBem().equals(TipoEventoBem.EFETIVACAOTRANSFERENCIABEM)
            || eventoBem.getTipoEventoBem().equals(TipoEventoBem.TRANSFERENCIADEPRECIACAORECEBIDA)
            || eventoBem.getTipoEventoBem().equals(TipoEventoBem.TRANSFERENCIABEM)
        );
    }

    public boolean isMesmoEventoConcedido(EventoBem eventoSelecionado) {
        Boolean controle = Boolean.TRUE;
        List<EventoBem> eventosIguais = Lists.newArrayList();
        for (EventoBem eventoLista : bemEfetivacaoTransferencia.getEventosBem()) {
            if (eventoSelecionado.getTipoEventoBem().equals(eventoLista.getTipoEventoBem())) {
                eventosIguais.add(eventoLista);
            }
        }
        if (eventosIguais.size() > 1) {
            controle = false;
        }
        return controle;
    }

    public void removerEventoBem(EventoBem eventoBem) {
        bemEfetivacaoTransferencia.getEventosBem().remove(eventoBem);
        eventosEfetivacaoTransferenciaDeletados.add(eventoBem);
    }

    public void confirmarEventosRemovidos() {
        if (!eventosEfetivacaoTransferenciaDeletados.isEmpty()) {
            for (EventoBem evento : eventosEfetivacaoTransferenciaDeletados) {
                String nomeTabela = "";
                switch (evento.getTipoEventoBem()) {
                    case TRANSFERENCIABEM:
                        nomeTabela = Util.getNomeTabela(TransferenciaBem.class);
                        break;
                    case TRANSFERENCIABEMCONCEDIDA:
                        nomeTabela = Util.getNomeTabela(TransferenciaBemConcedida.class);
                        break;
                    case TRANSFERENCIADEPRECIACAOCONCEDIDA:
                        nomeTabela = Util.getNomeTabela(TransferenciaDepreciacaoConcedida.class);
                        break;
                    case EFETIVACAOTRANSFERENCIABEM:
                        nomeTabela = Util.getNomeTabela(EfetivacaoTransferenciaBem.class);
                        break;
                    case TRANSFERENCIADEPRECIACAORECEBIDA:
                        nomeTabela = Util.getNomeTabela(TransferenciaDepreciacaoRecebida.class);
                        break;
                }
                facade.getBemFacade().deletarItemEventoBem(evento.getId(), nomeTabela);
                facade.getBemFacade().deletarEventoBem(evento.getId());
            }
        }
        FacesUtil.executaJavaScript("dlgTransferencia.hide()");
        eventosEfetivacaoTransferenciaDeletados = Lists.newArrayList();
    }

    public void finalizarFuture() {
        FacesUtil.atualizarComponente("Formulario");
    }

    public void acompanharFuture() throws ExecutionException, InterruptedException {
        if (future != null && future.isDone()) {
            selecionado = future.get();
            if (CorrecaoDadosAdministrativoVO.TipoFuture.CONSULTA_BENS.equals(selecionado.getTipoFuture())) {
                List<Bem> bensPesquisados = Lists.newArrayList();
                Set<Bem> bensSelecionados = new HashSet<>();
                bensPesquisados.addAll(selecionado.getBens());
                removerEventoDifrenteTransferenciaAndAlienacao(bensPesquisados);
                compararEventoAtualComEventoAnterior(bensPesquisados, bensSelecionados);
                selecionado.getBens().addAll(bensSelecionados);
            }
            future = null;
            FacesUtil.executaJavaScript("terminarFuture()");
        }
    }

    private void compararEventoAtualComEventoAnterior(List<Bem> bensPesquisados, Set<Bem> bensSelecionados) {
        for (Bem bem : bensPesquisados) {
            for (int i = 0; i < bem.getEventosBem().size(); i++) {
                EventoBem eventoAtual = bem.getEventosBem().get(i);
                EventoBem eventoAterior = null;
                if (i != 0) {
                    eventoAterior = bem.getEventosBem().get(i - 1);
                }
                if (eventoAterior != null) {
                    if ((isSolicitacaoTranferencia(eventoAtual) || isEventoTransferencia(eventoAtual)) && isAlienacao(eventoAterior)) {
                        bensSelecionados.add(bem);
                    }

                    if ((TipoEventoBem.ITEMAPROVACAOALIENACAO.equals(eventoAtual.getTipoEventoBem())
                        || TipoEventoBem.AVALIACAO_ALIENACAO.equals(eventoAtual.getTipoEventoBem()))
                        && isEventoTransferencia(eventoAterior)) {
                        bensSelecionados.add(bem);
                    }
                }
            }
        }
    }

    private void removerEventoDifrenteTransferenciaAndAlienacao(List<Bem> bensPesquisados) {
        for (Bem bem : bensPesquisados) {
            Collections.sort(bem.getEventosBem());
            List<EventoBem> eventosUtilizados = Lists.newArrayList();
            for (EventoBem eventoBem : bem.getEventosBem()) {
                if (isEventoTransferencia(eventoBem) || isSolicitacaoTranferencia(eventoBem) || isAlienacao(eventoBem)) {
                    eventosUtilizados.add(eventoBem);
                }
            }
            bem.setEventosBem(new ArrayList<EventoBem>(eventosUtilizados));
        }
    }

    public void unificarMovimentoAlteracaoContratual(AlteracaoContratual alt) {
        facade.unificarMovimentosAlteracaoContratual(alt);
    }

    public List<AlteracaoContratual> completarAlteracaoContratual(String parte) {
        return facade.buscarAlteracoesNaoReprocessadas(parte.trim());
    }

    public void listenerAlteracaoContratual() {
        try {
            if (selecionado.getAlteracaoContratual() != null) {
                selecionado.setAlteracoesContratuais(Lists.newArrayList(facade.getAlteracaoContratualFacade().recuperarComDependenciasMovimentosAndItens(selecionado.getAlteracaoContratual().getId())));
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void reprocessarContratos() {
        try {
            future = facade.reprocessarContrato(selecionado);
            FacesUtil.executaJavaScript("iniciarFuture()");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void reprocessarAlteracaoContratual() {
        try {
            novoAssitenteBarraProgresso(CorrecaoDadosAdministrativoVO.TipoFuture.REPROCESSAMENTO_ALTERACAO_CONTRATUAL);
            future = facade.reprocessarAlteracaoContratual(selecionado);
            FacesUtil.executaJavaScript("iniciarFuture()");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void atualizarValorLiquidadoItemDocumentoFiscal() {
        try {
            if (selecionado.getItensDocumentoEntrada() != null) {
                selecionado.getItensDocumentoEntrada().clear();
            }
            novoAssitenteBarraProgresso(CorrecaoDadosAdministrativoVO.TipoFuture.ITEM_DOCUMENTO_FISCAL);
            future = facade.buscarDocumentosFiscaisAndItens(selecionado);
            FacesUtil.executaJavaScript("iniciarFuture()");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void inserirEfetivacaoMaterial() {
        try {
            novoAssitenteBarraProgresso(CorrecaoDadosAdministrativoVO.TipoFuture.ITEM_DOCUMENTO_FISCAL);
            future = facade.inserirEfetivacaoMaterial(selecionado);
            FacesUtil.executaJavaScript("iniciarFuture()");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void novoAssitenteBarraProgresso(CorrecaoDadosAdministrativoVO.TipoFuture tipoFuture) {
        selecionado.setTipoFuture(tipoFuture);
        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
        assistente.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistente.setDataAtual(facade.getSistemaFacade().getDataOperacao());
        assistente.setExecutando(true);
        selecionado.setAssistenteBarraProgresso(assistente);
    }

    private boolean isSolicitacaoTranferencia(EventoBem eventoAtual) {
        return eventoAtual.getTipoEventoBem() != null && TipoEventoBem.TRANSFERENCIABEM.equals(eventoAtual.getTipoEventoBem());
    }

    private boolean isAlienacao(EventoBem eventoAtual) {
        return eventoAtual.getTipoEventoBem() != null
            && (TipoEventoBem.ITEMLOTESOLICITACAOALIENACAO.equals(eventoAtual.getTipoEventoBem())
            || TipoEventoBem.ITEMAPROVACAOALIENACAO.equals(eventoAtual.getTipoEventoBem())
            || TipoEventoBem.AVALIACAO_ALIENACAO.equals(eventoAtual.getTipoEventoBem()));
    }

    public List<LoteEfetivacaoTransferenciaBem> completarEfetivacaoTransferencia(String parte) {
        return facade.getLoteEfetivacaoTransferenciaBemFacade().listaFiltrando(parte.trim(), "codigo");
    }

    public LoteEfetivacaoTransferenciaBem getEfetivacaoTransferencia() {
        return efetivacaoTransferencia;
    }

    public void setEfetivacaoTransferencia(LoteEfetivacaoTransferenciaBem efetivacaoTransferencia) {
        this.efetivacaoTransferencia = efetivacaoTransferencia;
    }

    public Bem getBemEfetivacaoTransferencia() {
        return bemEfetivacaoTransferencia;
    }

    public void setBemEfetivacaoTransferencia(Bem bemEfetivacaoTransferencia) {
        this.bemEfetivacaoTransferencia = bemEfetivacaoTransferencia;
    }

    public List<UnidadeOrganizacional> getUnidadesBloqueadas(Class classe) {
        if (classe != null && facade.getSingletonBloqueioPatrimonio().getMapaMovimentoUnidades() != null) {
            return facade.getSingletonBloqueioPatrimonio().getMapaMovimentoUnidades().get(classe);
        }
        return Lists.newArrayList();
    }

    public Date getDataOperacao(){
        return facade.getSistemaFacade().getDataOperacao();
    }

    public List<Class> getMovimentosBloqueados() {
        return movimentosBloqueados;
    }

    public void setMovimentosBloqueados(List<Class> movimentosBloqueados) {
        this.movimentosBloqueados = movimentosBloqueados;
    }

    public String buscarNomeDaClasse(Class movimento) {
        return Util.buscarNomeDaClasse(movimento);
    }

    public List<Bem> getBensBloqueados() {
        return bensBloqueados;
    }

    public void setBensBloqueados(List<Bem> bensBloqueados) {
        this.bensBloqueados = bensBloqueados;
    }

    public List<UnidadeOrganizacional> getUnidadesContrato() {
        return unidadesContrato;
    }

    public void setUnidadesContrato(List<UnidadeOrganizacional> unidadesContrato) {
        this.unidadesContrato = unidadesContrato;
    }

    public CorrecaoDadosAdministrativoVO getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(CorrecaoDadosAdministrativoVO selecionado) {
        this.selecionado = selecionado;
    }

    public List<RequisicaoMaterial> getRequisicoesMateriais() {
        return requisicoesMateriais;
    }

    public void setRequisicoesMateriais(List<RequisicaoMaterial> requisicoesMateriais) {
        this.requisicoesMateriais = requisicoesMateriais;
    }
}
