/*
 * Codigo gerado automaticamente em Tue Mar 06 11:24:54 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.consultaentidade.ActionConsultaEntidade;
import br.com.webpublico.consultaentidade.TipoAlinhamento;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ResultanteIndependente;
import br.com.webpublico.enums.StatusSolicitacaoCotaFinanceira;
import br.com.webpublico.enums.TipoContaFinanceira;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SolicitacaoCotaFinanceiraFacade;
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
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "solicitacaoCotaFinanceiraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-solicitacao-financeira", pattern = "/solicitacao-financeira/novo/", viewId = "/faces/financeiro/orcamentario/solicitacaocotafinanceira/edita.xhtml"),
    @URLMapping(id = "editar-solicitacao-financeira", pattern = "/solicitacao-financeira/editar/#{solicitacaoCotaFinanceiraControlador.id}/", viewId = "/faces/financeiro/orcamentario/solicitacaocotafinanceira/edita.xhtml"),
    @URLMapping(id = "ver-solicitacao-financeira", pattern = "/solicitacao-financeira/ver/#{solicitacaoCotaFinanceiraControlador.id}/", viewId = "/faces/financeiro/orcamentario/solicitacaocotafinanceira/visualizar.xhtml"),
    @URLMapping(id = "listar-solicitacao-financeira", pattern = "/solicitacao-financeira/listar/", viewId = "/faces/financeiro/orcamentario/solicitacaocotafinanceira/lista.xhtml")
})
public class SolicitacaoCotaFinanceiraControlador extends PrettyControlador<SolicitacaoCotaFinanceira> implements Serializable, CRUD {

    @EJB
    private SolicitacaoCotaFinanceiraFacade facade;
    private ContaBancariaEntidade contaBancariaEntidade;
    private List<SolicitacaoFinanceiraContaDespesa> elementos;
    private HashMap<Long, List<SelectItem>> mapContasDeDestinacaoPorSubConta;
    private BigDecimal valorSubConta;

    public SolicitacaoCotaFinanceiraControlador() {
        super(SolicitacaoCotaFinanceira.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    @Override
    public void salvar() {
        try {
            validarSelecionado();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
                FacesUtil.addOperacaoRealizada("A Solicitação " + this.selecionado.toString() + " foi salvo com sucesso.");
                FacesUtil.executaJavaScript("dialogAprovacao.show()");
            } else {
                facade.validarConcorrencia(selecionado);
                facade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada("A Solicitação " + this.selecionado + " foi alterada com sucesso!");
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarValores() {
        ValidacaoException ve = new ValidacaoException();
        if (isResultanteExecucaoOrcamentaria()) {
            if (selecionado.getElementosDespesa() == null || selecionado.getElementosDespesa().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione ao menos um elemento de despesa para resultandte da execução orçamentária.");
            }
            for (SolicitacaoFinanceiraContaDespesa solicitacaoFinanceiraContaDespesa : selecionado.getElementosDespesa()) {
                if (solicitacaoFinanceiraContaDespesa.getValor() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O Valor do Elemento de Despesa " + solicitacaoFinanceiraContaDespesa.getDespesaORC().getDescricaoContaDespesaPPA() + " do Projeto/Atividade " + solicitacaoFinanceiraContaDespesa.getDespesaORC().getProjetoAtividade() + " é obrigatório.");
                } else if (solicitacaoFinanceiraContaDespesa.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoRealizada("O Valor do Elemento de Despesa " + solicitacaoFinanceiraContaDespesa.getDespesaORC().getDescricaoContaDespesaPPA() + " do Projeto/Atividade " + solicitacaoFinanceiraContaDespesa.getDespesaORC().getProjetoAtividade() + " deve ser maior que 0 (ZERO).");
                }
            }
        }
        if (selecionado.getValorSolicitado().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O Valor da Solicitação deve ser maior que 0 (ZERO).");
        }
        ve.lancarException();
    }

    public void cancelarEnvioSolicitacaoAnalise() {
        super.cancelar();
    }

    private void validarCamposCancelamento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataCancelamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Cancelamento deve ser informado.");
        } else if (selecionado.getDataCancelamento().before(selecionado.getDtSolicitacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data do cancelamento deve ser superior ou igual a data da solicitação.");
        }
        if (selecionado.getMotivoCancelamento() == null || selecionado.getMotivoCancelamento().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Movito do Cancelamento deve ser informado.");
        }
        ve.lancarException();
    }

    public void prepararSolicitacaoParaAprovacao(SolicitacaoCotaFinanceira solicitacao) {
        selecionado = facade.recuperar(solicitacao.getId());
        enviarParaAnalise();
    }

    public void enviarParaAnalise() {
        try {
            validarSelecionado();
            facade.salvarParaAprovarLiberacaoNotificando(selecionado);
            FacesUtil.addOperacaoRealizada(" A Solicitação enviada para aprovação com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(ex);
        }
    }

    public List<SelectItem> getValoresResultanteIndependente() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (ResultanteIndependente ri : ResultanteIndependente.values()) {
            lista.add(new SelectItem(ri, ri.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getContasDeDestinacaoPorSubConta() {
        List<SelectItem> dados = Lists.newArrayList();
        if (selecionado.getContaFinanceira() != null && selecionado.getContaFinanceira().getId() != null) {
            if (mapContasDeDestinacaoPorSubConta.containsKey(selecionado.getContaFinanceira().getId())) {
                return mapContasDeDestinacaoPorSubConta.get(selecionado.getContaFinanceira().getId());
            }

            List<ContaDeDestinacao> contasDeDestinacao = facade.getContaFacade().buscarContasDeDestinacaoPorContaFinanceirAndExercicio("", selecionado.getContaFinanceira(), facade.getSistemaFacade().getExercicioCorrente());
            if (contasDeDestinacao != null && !contasDeDestinacao.isEmpty()) {
                dados.add(new SelectItem(null, ""));
                for (ContaDeDestinacao cd : contasDeDestinacao) {
                    dados.add(new SelectItem(cd, cd.toString()));
                }
            }
            mapContasDeDestinacaoPorSubConta.put(selecionado.getContaFinanceira().getId(), dados);
        }
        return dados;
    }

    public void atualizarFonteComContaDeDestinacao() {
        if (selecionado.getContaDeDestinacao() != null) {
            selecionado.setFonteDeRecursos(selecionado.getContaDeDestinacao().getFonteDeRecursos());
        }
    }

    @URLAction(mappingId = "novo-solicitacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setUsuarioSolicitante(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setDtSolicitacao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setStatus(StatusSolicitacaoCotaFinanceira.ABERTA);
        mapContasDeDestinacaoPorSubConta = new HashMap<>();
        valorSubConta = BigDecimal.ZERO;
    }

    @URLAction(mappingId = "ver-solicitacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-solicitacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditaVer();
    }

    private void recuperarEditaVer() {
        mapContasDeDestinacaoPorSubConta = new HashMap<>();
        recuperarElementos();
        definirContaBancaria();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-financeira/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean isRegistroEditavel() {
        return !selecionado.isSolicitacaoAberta();
    }

    public Boolean isSolicitacaoSalvaAberta(SolicitacaoCotaFinanceira solicitacao) {
        return solicitacao.getId() != null && solicitacao.isSolicitacaoAberta();
    }

    public Boolean isSolicitacaoSalvaAberta() {
        return this.selecionado.getId() != null && this.selecionado.isSolicitacaoAberta();
    }

    public void prepararSolicitacaoParaCancelar() {
        try {
            validarSelecionado();
            selecionado.setDataCancelamento(facade.getSistemaFacade().getDataOperacao());
            FacesUtil.executaJavaScript("dialogCancelar.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarSelecionado() {
        Util.validarCampos(selecionado);
        validarValores();
    }

    public void cancelarSolicitacao() {
        try {
            validarCamposCancelamento();
            selecionado.setStatus(StatusSolicitacaoCotaFinanceira.CANCELADO);
            facade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada(" Solicitação cancelada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void cancelarCancelamento() {
        selecionado.setDataCancelamento(null);
        selecionado.setMotivoCancelamento(null);
    }

    public void definirContaBancaria() {
        try {
            contaBancariaEntidade = selecionado.getContaFinanceira().getContaBancariaEntidade();
            recuperarSaldoSubConta();
            if (isOperacaoNovo()) {
                definirContaDeDestinacao();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void definirContaDeDestinacao() {
        List<SelectItem> contasDeDestinacao = getContasDeDestinacaoPorSubConta();
        if (!contasDeDestinacao.isEmpty()) {
            selecionado.setContaDeDestinacao((ContaDeDestinacao) contasDeDestinacao.get(0).getValue());
            atualizarFonteComContaDeDestinacao();
        }
    }

    public void adicionarElemento(SolicitacaoFinanceiraContaDespesa elemento) {
        elemento.setSolicitacao(selecionado);
        selecionado.getElementosDespesa().add(elemento);
    }

    public Boolean elementoAdicionado(SolicitacaoFinanceiraContaDespesa elemento) {
        return selecionado.getElementosDespesa().contains(elemento);
    }

    public void removerElemento(SolicitacaoFinanceiraContaDespesa elemento) {
        elemento.setValor(BigDecimal.ZERO);
        selecionado.getElementosDespesa().remove(elemento);
        calculcarValorSolicitacao();
    }

    public void recuperarElementos() {
        try {
            elementos = Lists.newArrayList();
            if (isResultanteExecucaoOrcamentaria()) {
                recupearDespesaResultanteDaExecucao();
            } else {
                elementos.clear();
                selecionado.setElementosDespesa(new ArrayList<SolicitacaoFinanceiraContaDespesa>());
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void recupearDespesaResultanteDaExecucao() {
        if (selecionado.getElementosDespesa().isEmpty() || isOperacaoNovo()) {
            selecionado.setValorSolicitado(BigDecimal.ZERO);
            elementos = facade.buscarDespesaOrcPorExercicioAndUnidade(selecionado.getExercicio(), selecionado.getUnidadeOrganizacional(), selecionado.getDtSolicitacao());
        } else {
            elementos.addAll(selecionado.getElementosDespesa());
        }
    }

    public boolean isResultanteExecucaoOrcamentaria() {
        return ResultanteIndependente.RESULTANTE_EXECUCAO_ORCAMENTARIA.equals(selecionado.getResultanteIndependente());
    }

    public void calculcarValorSolicitacao() {
        selecionado.setValorSolicitado(BigDecimal.ZERO);
        for (SolicitacaoFinanceiraContaDespesa solicitacaoFinanceiraContaDespesa : this.selecionado.getElementosDespesa()) {
            selecionado.setValorSolicitado(selecionado.getValorSolicitado().add(solicitacaoFinanceiraContaDespesa.getValor()));
        }
    }

    public void definirContasComoNull() {
        contaBancariaEntidade = null;
        selecionado.setContaFinanceira(null);
    }

    public void definirContaDestComoNull() {
        selecionado.setContaFinanceira(null);
        selecionado.setContaDeDestinacao(null);
        selecionado.setFonteDeRecursos(null);
    }

    public List<SolicitacaoFinanceiraContaDespesa> getElementos() {
        return elementos;
    }

    public void setElementos(List<SolicitacaoFinanceiraContaDespesa> elementos) {
        this.elementos = elementos;
    }

    public BigDecimal getValorSubConta() {
        return valorSubConta;
    }

    public void setValorSubConta(BigDecimal valorSubConta) {
        this.valorSubConta = valorSubConta;
    }

    public List<ContaBancariaEntidade> completarContasBancarias(String parte) {
        return facade.getContaBancariaEntidadeFacade().listaFiltrandoAtivaPorUnidade(parte.trim(), selecionado.getUnidadeOrganizacional(), facade.getSistemaFacade().getExercicioCorrente(), null, Lists.newArrayList(TipoContaFinanceira.MOVIMENTO, TipoContaFinanceira.PAGAMENTO), null);
    }

    public List<SubConta> completarSubContas(String parte) {
        return facade.getSubContaFacade().buscarContasFinanceirasPorBancariaEntidadeOuTodosPorUnidadeAndContaDeDestinacao(parte.trim(), contaBancariaEntidade, selecionado.getUnidadeOrganizacional(), facade.getSistemaFacade().getExercicioCorrente(), selecionado.getContaDeDestinacao(), Lists.newArrayList(TipoContaFinanceira.MOVIMENTO, TipoContaFinanceira.PAGAMENTO), null, true);
    }

    private void recuperarSaldoSubConta() {
        if (selecionado.getContaFinanceira() != null && selecionado.getUnidadeOrganizacional() != null) {
            if (selecionado.getContaDeDestinacao() != null) {
                valorSubConta = facade.getSubContaFacade().getSaldoSubContaFacade().buscarValorUltimoSaldoSubContaPorData(selecionado.getUnidadeOrganizacional(), selecionado.getContaFinanceira(), selecionado.getContaDeDestinacao(), facade.getSistemaFacade().getDataOperacao());
            } else {
                valorSubConta = facade.getSubContaFacade().getSaldoSubContaFacade().buscarValorUltimoSaldoSubContaPorData(selecionado.getUnidadeOrganizacional(), selecionado.getContaFinanceira(), null, facade.getSistemaFacade().getDataOperacao());
            }
        } else {
            valorSubConta = BigDecimal.ZERO;
        }
    }

    public List<ActionConsultaEntidade> getActionsPesquisa() {
        ActionConsultaEntidade action = new ActionSolicitacaoCotaFinanceira();
        action.setDescricao("Enviar");
        action.setIcone("ui-icon-deferir");
        action.setTitle("Clique para enviar esta solicitação para aprovação.");
        action.setStyle("height: 25px");
        action.setAlinhamento(TipoAlinhamento.DIREITA);
        action.setDownload(false);
        return Lists.newArrayList(action);
    }

    public class ActionSolicitacaoCotaFinanceira extends ActionConsultaEntidade {
        @Override
        public boolean canRenderizar(Map<String, Object> registro) {
            Long idSolicitacaoCotaFinanceira = ((Number) registro.get("identificador")).longValue();
            return facade.isSolicitacaoAberta(idSolicitacaoCotaFinanceira);
        }

        @Override
        public void action(Map<String, Object> registro) {
            Long idSolicitacaoCotaFinanceira = ((Number) registro.get("identificador")).longValue();
            selecionado = facade.recuperar(idSolicitacaoCotaFinanceira);
            enviarParaAnalise();
        }
    }
}

