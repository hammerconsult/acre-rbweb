package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteExclusaoContrato;
import br.com.webpublico.entidadesauxiliares.RelacionamentoContrato;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ExclusaoContratoFacade;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaExclusaoContrato", pattern = "/exclusao-contrato/novo/", viewId = "/faces/administrativo/contrato/exclusao-contrato/edita.xhtml"),
    @URLMapping(id = "listarExclusaoContrato", pattern = "/exclusao-contrato/listar/", viewId = "/faces/administrativo/contrato/exclusao-contrato/lista.xhtml"),
    @URLMapping(id = "verExclusaoContrato", pattern = "/exclusao-contrato/ver/#{exclusaoContratoControlador.id}/", viewId = "/faces/administrativo/contrato/exclusao-contrato/visualizar.xhtml")
})
public class ExclusaoContratoControlador extends PrettyControlador<ExclusaoContrato> implements Serializable, CRUD {

    @EJB
    private ExclusaoContratoFacade facade;
    private AssistenteExclusaoContrato assistente;
    private Future<AssistenteExclusaoContrato> future;

    public ExclusaoContratoControlador() {
        super(ExclusaoContrato.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/exclusao-contrato/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novaExclusaoContrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataExclusao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setSituacao(SituacaoMovimentoAdministrativo.EM_ELABORACAO);
        selecionado.setTipoExclusao(TipoExclusaoContrato.CONTRATO);
        novoAssistente();
    }

    @URLAction(mappingId = "verExclusaoContrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        if (selecionado.isTipoExclusaoContrato()) {
            Contrato contrato = facade.getContratoFacade().recuperarSemDependencias(selecionado.getIdMovimento());
            if (contrato == null) {
                FacesUtil.addOperacaoRealizada("Contrato (ID: " + selecionado.getIdMovimento() + ") já foi excluído com sucesso!");
                return;
            }
        }
    }

    public void salvarVerificandoCondicoes() {
        try {
            validarSalvar();
            if (hasValorReservadoPorLicitacaoNegativo()) {
                FacesUtil.executaJavaScript("dlgConfirmaExclusao.show()");
            } else if (hasExclusaoExecucaoComRelacionamentosEmpenho()) {
                FacesUtil.executaJavaScript("dlgConfirmaExclusaoExecucao.show()");
            } else if (hasExclusaoAditivoOuApostilamentoComRelacionamentosExecucao()) {
                FacesUtil.executaJavaScript("dlgConfirmaExclusaoAditivoOuApostilamento.show()");
            } else {
                salvar();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void salvar() {
        try {
            FacesUtil.executaJavaScript("dlgConfirmaExclusao.hide()");
            validarSalvar();
            assistente.setEntity(selecionado);
            selecionado = facade.salvarRetornando(assistente);
            facade.getReprocessamentoSaldoContratoFacade().reprocessarUnicoContrato(assistente.getContrato());
            redirecionarParaVer(selecionado.getId());
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao salvar exclusão de contrato ", e);
            descobrirETratarException(e);
        }
    }

    public void redirecionarParaVer(Long id) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + id + "/");
    }

    private void novoAssistente() {
        assistente = new AssistenteExclusaoContrato();
    }

    public void removerDoPortal() {
        try {
            novoAssistente();
            Contrato contrato = facade.getContratoFacade().recuperar(selecionado.getIdMovimento());
            if (contrato == null) {
                throw new ExcecaoNegocioGenerica("Contrato não localizado. Verifique se ja foi removido.");
            }
            HierarquiaOrganizacional hierarquiaOrganizacional = facade.getContratoFacade().buscarHierarquiaVigenteContrato(contrato, facade.getSistemaFacade().getDataOperacao());

            assistente.setContrato(contrato);
            assistente.setEntity(selecionado);
            assistente.setHierarquiaOrganizacional(hierarquiaOrganizacional);
            assistente.setRelacionamentos(facade.buscarRelacionamentosContrato(selecionado));
            future = facade.removerContrato(assistente);
            FacesUtil.executaJavaScript("iniciarImportacao()");
        } catch (Exception e) {
            logger.error("Erro ao remover do portal ", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void finalizarImportacao() {
        if (future != null && future.isDone()) {
            try {
                facade.removerContratoDoPortalPortal(assistente);
                if (selecionado.getSomenteRemoverPortal()) {
                    FacesUtil.addOperacaoRealizada("O contrato excluído do portal da transparência com sucesso!");
                }
                FacesUtil.executaJavaScript("terminarImportacao()");
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            } catch (Exception e) {
                FacesUtil.executaJavaScript("terminarImportacao()");
                logger.error("Error no processo de exclusão de contrato ", e);
                FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            }
        }
    }

    public void abortar() {
        if (future != null) {
            future.cancel(true);
            assistente.getBarraProgressoItens().finaliza();
        }
    }

    public boolean hasValorReservadoPorLicitacaoNegativo() {
        for (RelacionamentoContrato rel : assistente.getRelacionamentos()) {
            if (rel.isExecucao()) {
                return rel.hasValorReservadoPorLicitacaoNegativo();
            }
        }
        return false;
    }

    public boolean hasExclusaoExecucaoComRelacionamentosEmpenho() {
        return selecionado.isTipoExclusaoExecucaoContrato() && assistente.getRelacionamentos().stream().anyMatch(RelacionamentoContrato::isEmpenho);
    }

    public boolean hasExclusaoAditivoOuApostilamentoComRelacionamentosExecucao() {
        return (selecionado.isTipoExclusaoAditivo() || selecionado.isTipoExclusaoApostilamento()) && assistente.getRelacionamentos().stream().anyMatch(RelacionamentoContrato::isExecucao);
    }

    public void recuperarDadosContrato() {
        if (assistente.getContrato() != null) {
            assistente.setContrato(facade.getContratoFacade().recuperar(assistente.getContrato().getId()));
            selecionado.setIdMovimento(assistente.getContrato().getId());
            assistente.setRelacionamentos(facade.buscarRelacionamentosContrato(selecionado));
            gerarHistorico();
        }
    }

    public void recuperarDadosAlteracaoContratual() {
        try {
            validarAlteracaoContratualTransferencia();
            if (assistente.getAlteracaoContratual() != null) {
                selecionado.setIdMovimento(assistente.getAlteracaoContratual().getId());
                assistente.setContrato(assistente.getAlteracaoContratual().getContrato());
                assistente.setRelacionamentos(facade.buscarRelacionamentosAlteracaoContratual(selecionado));
                gerarHistorico();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAlteracaoContratualTransferencia() {
        ValidacaoException ve = new ValidacaoException();
        List<MovimentoAlteracaoContratual> movimentos = facade.getAlteracaoContratualFacade().buscarMovimentosAndItens(assistente.getAlteracaoContratual().getId());
        if (movimentos.stream()
            .anyMatch(mov -> OperacaoMovimentoAlteracaoContratual.getOperacoesTransferencia().contains(mov.getOperacao()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O " + assistente.getAlteracaoContratual().getTipoAlteracaoContratual() + " possui movimentações do tipo Transferência.");
            assistente.setAlteracaoContratual(null);
        }
        ve.lancarException();
    }

    public void recuperarDadosExecucaoContrato() {
        if (assistente.getExecucaoContrato() != null) {
            selecionado.setIdMovimento(assistente.getExecucaoContrato().getId());
            assistente.setContrato(assistente.getExecucaoContrato().getContrato());
            assistente.setRelacionamentos(facade.buscarRelacionamentosExecucaoContrato(selecionado));
            gerarHistorico();
        }
    }

    public void gerarHistorico() {
        StringBuilder historico = new StringBuilder();
        if (selecionado.isTipoExclusaoContrato()) {
            historico.append("<b>CONTRATO: </b> ").append(assistente.getContrato().toString()).append(", <b>ID:</b> ").append(assistente.getContrato().getId());
            for (RelacionamentoContrato rel : assistente.getRelacionamentos()) {
                historico.append("<br/>").append("<b>").append(rel.getTipo().getDescricao().toUpperCase()).append(":</b> ").append(rel.getMovimento()).append(", <b>ID: </b> ").append(rel.getId());
            }
        } else if (selecionado.isTipoExclusaoAditivo() || selecionado.isTipoExclusaoApostilamento()) {
            historico.append("<b>").append(selecionado.getTipoExclusao().getDescricao().toUpperCase()).append(":</b> ").append(assistente.getAlteracaoContratual().toString()).append(", <b>ID:</b> ").append(assistente.getAlteracaoContratual().getId());
            historico.append("<br/>").append("<b>CONTRATO ID: </b> ").append(assistente.getAlteracaoContratual().getContrato().getId());
            for (RelacionamentoContrato rel : assistente.getRelacionamentos()) {
                historico.append("<br/>").append("<b>").append(rel.getTipo().getDescricao().toUpperCase()).append(":</b> ").append(rel.getMovimento()).append(", <b>ID: </b> ").append(rel.getId());
            }
        } else if (selecionado.isTipoExclusaoExecucaoContrato()) {
            historico.append("<b>").append("EXECUÇÃO CONTRATO: </b> ").append(assistente.getExecucaoContrato().toString()).append(", <b>ID:</b> ").append(assistente.getExecucaoContrato().getId());
            historico.append("<br/>").append("<b>CONTRATO ID: </b> ").append(assistente.getExecucaoContrato().getContrato().getId());
            for (RelacionamentoContrato rel : assistente.getRelacionamentos()) {
                historico.append("<br/>").append("<b>").append(rel.getTipo().getDescricao().toUpperCase()).append(":</b> ").append(rel.getMovimento()).append(", <b>ID: </b> ").append(rel.getId());
            }
        }
        selecionado.setHistorico(historico.toString());
    }

    public boolean hasRelacionamentos() {
        return assistente.getRelacionamentos() != null && !assistente.getRelacionamentos().isEmpty();
    }

    public void limparDadosContrato() {
        assistente.setRelacionamentos(Lists.newArrayList());
        assistente.setContrato(null);
        assistente.setAlteracaoContratual(null);
        selecionado.setHistorico(null);
    }

    private void validarSalvar() {
        validarCamposObrigatorios();
        validarRegrasEspecificas();
    }

    private void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        boolean isTodosEmpenhosTotalmenteEstornados = true;
        for (RelacionamentoContrato rel : assistente.getRelacionamentos()) {
            if (rel.isRequisicaoCompra() && !selecionado.getSomenteRemoverPortal()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Exclusão não Permitida!", selecionado.getTipoExclusao().getDescricao() + " possui requisição de compra posterior ao seu lançamento. Para mais detalhes verifique a aba 'Relaciomentos'.");
                break;
            }
            if (rel.isPagamento()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Exclusão não Permitida!", "A execução orçamentário desse contrato já possui pagamento realizado." +
                    "Para mais detalhes verifique a aba 'Relaciomentos'.");
                break;
            }
        }
        List<ExecucaoContrato> execucoes = selecionado.isTipoExclusaoExecucaoContrato()
            ? Lists.newArrayList(assistente.getExecucaoContrato())
            : Lists.newArrayList(facade.getExecucaoContratoFacade().buscarExecucoesPorOrigem(selecionado.getIdMovimento()));

        for (ExecucaoContrato exec : execucoes) {
            List<Empenho> empenhos = facade.getExecucaoContratoFacade().buscarEmpenhosExecucao(exec);
            for (Empenho empenho : empenhos) {
                if (!facade.getEmpenhoFacade().isEmpenhoTotalmenteEstornado(empenho)) {
                    isTodosEmpenhosTotalmenteEstornados = false;
                    break;
                }
            }
        }
        if (!isTodosEmpenhosTotalmenteEstornados) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Exclusão não Permitida!", "O registro selecionado possui empenho(s) que não foram totalmente estornados.");
        }
        ve.lancarException();
    }

    private void validarCamposObrigatorios() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.isTipoExclusaoContrato()) {
            if (selecionado.getTipoContrato() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de contrato deve ser informado.");
            }
            if (assistente.getContrato() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo contrato deve ser informado.");
            }
        }
        if ((selecionado.isTipoExclusaoAditivo() || selecionado.isTipoExclusaoApostilamento()) && assistente.getAlteracaoContratual() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo " + selecionado.getTipoExclusao().getDescricao() + " deve ser informado.");
        }
        if (selecionado.isTipoExclusaoExecucaoContrato() && assistente.getExecucaoContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo " + selecionado.getTipoExclusao().getDescricao() + " deve ser informado.");
        }
        ve.lancarException();
    }

    public List<Contrato> completarContrato(String parte) {
        if (selecionado.getTipoContrato() != null) {
            return facade.getContratoFacade().buscarContratosPorTipoContrato(parte.trim(), selecionado.getTipoContrato());
        }
        return Lists.newArrayList();
    }

    public List<ExecucaoContrato> completarExecucaoContrato(String parte) {
        return facade.getExecucaoContratoFacade().buscarExecucaoContratoComSaldoAEstornar(parte.trim());
    }

    public List<AlteracaoContratual> completarAlteracaoContratual(String parte) {
        if (selecionado.isTipoExclusaoAditivo()) {
            return facade.getAlteracaoContratualFacade().buscarPorSituacaoAndTermoDiferenteCadastral(parte.trim(), TipoAlteracaoContratual.ADITIVO, SituacaoContrato.values());
        }
        return facade.getAlteracaoContratualFacade().buscarPorSituacaoAndTermoDiferenteCadastral(parte.trim(), TipoAlteracaoContratual.APOSTILAMENTO, SituacaoContrato.values());
    }

    public List<RelacionamentoContrato.Legenda> getLegendas() {
        return Arrays.asList(RelacionamentoContrato.Legenda.values());
    }

    public List<SelectItem> getTiposExclusaoContrato() {
        return Util.getListSelectItemSemCampoVazio(TipoExclusaoContrato.values(), false);
    }

    public List<SelectItem> getTiposAquisicaoContrato() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoAquisicaoContrato.LICITACAO, TipoAquisicaoContrato.LICITACAO.getDescricao()));
        toReturn.add(new SelectItem(TipoAquisicaoContrato.DISPENSA_DE_LICITACAO, TipoAquisicaoContrato.DISPENSA_DE_LICITACAO.getDescricao()));
        toReturn.add(new SelectItem(TipoAquisicaoContrato.REGISTRO_DE_PRECO_EXTERNO, TipoAquisicaoContrato.REGISTRO_DE_PRECO_EXTERNO.getDescricao()));
        toReturn.add(new SelectItem(TipoAquisicaoContrato.CONTRATOS_VIGENTE, TipoAquisicaoContrato.CONTRATOS_VIGENTE.getDescricao()));
        toReturn.add(new SelectItem(TipoAquisicaoContrato.ADESAO_ATA_REGISTRO_PRECO_INTERNA, TipoAquisicaoContrato.ADESAO_ATA_REGISTRO_PRECO_INTERNA.getDescricao()));
        toReturn.add(new SelectItem(TipoAquisicaoContrato.INTENCAO_REGISTRO_PRECO, TipoAquisicaoContrato.INTENCAO_REGISTRO_PRECO.getDescricao()));
        toReturn.add(new SelectItem(TipoAquisicaoContrato.PROCEDIMENTO_AUXILIARES, TipoAquisicaoContrato.PROCEDIMENTO_AUXILIARES.getDescricao()));
        return toReturn;
    }

    public void exibirRevisao(Long idMovimento) {

        Long idRevtype = selecionado.isTipoExclusaoContrato()
            ? facade.getContratoFacade().recuperarIdRevisaoAuditoriaContrato(idMovimento)
            : facade.getAlteracaoContratualFacade().recuperarIdRevisaoAuditoria(idMovimento);

        if (idRevtype == null) {
            FacesUtil.addOperacaoNaoRealizada("Revisão auditoria não encontrada para o movimento ID " + idMovimento + ".");
            return;
        }
        Web.getSessionMap().put("pagina-anterior-auditoria-listar", getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        FacesUtil.redirecionamentoInterno("/auditoria/ver/" + idRevtype + "/" + idMovimento + "/" + Contrato.class.getSimpleName());
    }

    public AssistenteExclusaoContrato getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteExclusaoContrato assistente) {
        this.assistente = assistente;
    }
}
