/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItensBordero;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BorderoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author major
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novobordero", pattern = "/ordem-bancaria/novo/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/bordero/edita.xhtml"),
    @URLMapping(id = "editarbordero", pattern = "/ordem-bancaria/editar/#{borderoControlador.id}/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/bordero/edita.xhtml"),
    @URLMapping(id = "verbordero", pattern = "/ordem-bancaria/ver/#{borderoControlador.id}/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/bordero/visualizar.xhtml"),
    @URLMapping(id = "listarbordero", pattern = "/ordem-bancaria/listar/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/bordero/lista.xhtml"),
    @URLMapping(id = "navega-remover-item", pattern = "/ordem-bancaria/remover-item-bordero/#{borderoControlador.id}/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/bordero/removeItem.xhtml"),
})
public class BorderoControlador extends PrettyControlador<Bordero> implements Serializable, CRUD {

    @EJB
    private BorderoFacade borderoFacade;
    private PagamentoExtra[] arrayPgtoExtra;
    private Pagamento[] arrayPgto;
    private TransferenciaContaFinanceira[] arrayTransferenciaFinanceira;
    private TransferenciaMesmaUnidade[] arrayTransferenciaMesmaUnidade;
    private LiberacaoCotaFinanceira[] arrayLiberacaoCotaFinanceira;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private List<Pagamento> listaPagamentos;
    private List<PagamentoExtra> listaPagamentoExtra;
    private List<TransferenciaContaFinanceira> listaTransferenciaFinanceira;
    private List<TransferenciaMesmaUnidade> listaTransferenciaMesmaUnidade;
    private List<LiberacaoCotaFinanceira> listaLiberacaoCotaFinanceira;
    private ItensBordero itensBordero;
    private String numeroArquivoBancario;
    private TipoOperacaoBordero tipoOperacaoBordero;

    public BorderoControlador() {
        super(Bordero.class);
    }

    public BorderoFacade getFacade() {
        return borderoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return borderoFacade;
    }

    public void limparLista() {
        selecionado.getListaPagamentos().clear();
        selecionado.getListaPagamentosExtra().clear();
        selecionado.getListaTransferenciaFinanceira().clear();
        selecionado.getListaTransferenciaMesmaUnidade().clear();
        selecionado.setSubConta(null);
        tipoOperacaoBordero = null;
    }

    @URLAction(mappingId = "novobordero", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setDataGeracao(sistemaControlador.getDataOperacao());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setSituacao(SituacaoBordero.ABERTO);
        listaPagamentoExtra = new ArrayList<>();
        listaPagamentos = new ArrayList<>();
        listaTransferenciaFinanceira = new ArrayList<>();
        listaTransferenciaMesmaUnidade = new ArrayList<>();
        listaLiberacaoCotaFinanceira = new ArrayList<>();
        tipoOperacaoBordero = null;
    }

    @URLAction(mappingId = "verbordero", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        tipoOperacaoBordero = null;
        numeroArquivoBancario = borderoFacade.recuperaNumeroArquivoBancario(selecionado);
        isGestorFinanceiro();
    }

    @URLAction(mappingId = "editarbordero", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionado = borderoFacade.recuperar(selecionado.getId());
        itensBordero = new ItensBordero();
        numeroArquivoBancario = borderoFacade.recuperaNumeroArquivoBancario(selecionado);
        tipoOperacaoBordero = null;
        isGestorFinanceiro();
    }

    @URLAction(mappingId = "navega-remover-item", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void removerItemBordero() {
        super.novo();
        selecionado = borderoFacade.recuperar(selecionado.getId());
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
    }

    @Override
    public void salvar() {
        try {
            borderoFacade.validarCampos(selecionado);
            if (isOperacaoNovo()) {
                selecionado = borderoFacade.salvarNovaOB(selecionado);
                FacesUtil.addOperacaoRealizada(" A " + metadata.getNomeEntidade() + " " + selecionado.getSequenciaArquivo() + " foi salvo com sucesso.");
                FacesUtil.executaJavaScript("dialogDeferirBordero.show()");
            } else {
                borderoFacade.salvar(selecionado, itensBordero);
                if (selecionado.isOrdemBancariaAberta()) {
                    FacesUtil.executaJavaScript("dialogDeferirBordero.show()");
                } else {
                    FacesUtil.addOperacaoRealizada("O " + metadata.getNomeEntidade() + " " + selecionado.getSequenciaArquivo() + " foi alterado com sucesso.");
                    redireciona();
                }
            }
        } catch (ValidacaoException ve) {
            borderoFacade.desbloquear(selecionado);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            borderoFacade.desbloquear(selecionado);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @Override
    public void excluir() {
        try {
            borderoFacade.remover(selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada(" Registro excluído com sucesso.");
        } catch (Exception e) {
            FacesUtil.addError("Operação não Realizada! ", e.getMessage());
        }
    }

    public boolean podeEditarOuExcluir() {
        return SituacaoBordero.ABERTO.equals(selecionado.getSituacao());
    }

    public boolean podeRevomerMovimento() {
        return !SituacaoBordero.ABERTO.equals(selecionado.getSituacao());
    }

    public Boolean renderizarMsgItensRemovidos() {
        for (BorderoPagamento item : selecionado.getListaPagamentos()) {
            if (SituacaoItemBordero.INDEFIRIDO.equals(item.getSituacaoItemBordero())) {
                return true;
            }
        }
        for (BorderoPagamentoExtra item : selecionado.getListaPagamentosExtra()) {
            if (SituacaoItemBordero.INDEFIRIDO.equals(item.getSituacaoItemBordero())) {
                return true;
            }
        }
        for (BorderoTransferenciaFinanceira item : selecionado.getListaTransferenciaFinanceira()) {
            if (SituacaoItemBordero.INDEFIRIDO.equals(item.getSituacaoItemBordero())) {
                return true;
            }
        }
        for (BorderoTransferenciaMesmaUnidade item : selecionado.getListaTransferenciaMesmaUnidade()) {
            if (SituacaoItemBordero.INDEFIRIDO.equals(item.getSituacaoItemBordero())) {
                return true;
            }
        }
        for (BorderoLiberacaoFinanceira item : selecionado.getListaLiberacaoCotaFinanceira()) {
            if (SituacaoItemBordero.INDEFIRIDO.equals(item.getSituacaoItemBordero())) {
                return true;
            }
        }
        return false;
    }

    public boolean canIndeferirOB() {
        return selecionado.getId() != null
            && !SituacaoBordero.PAGO.equals(selecionado.getSituacao())
            && !SituacaoBordero.ABERTO.equals(selecionado.getSituacao())
            && !SituacaoBordero.PAGO_COM_RESTRICOES.equals(selecionado.getSituacao())
            && !SituacaoBordero.INDEFERIDO.equals(selecionado.getSituacao())
            && isGestorFinanceiro();
    }

    public boolean podeIndeferirItemPagamentoOB(BorderoPagamento borderoPagamento) {
        return selecionado.getId() != null
            && !SituacaoItemBordero.PAGO.equals(borderoPagamento.getSituacaoItemBordero())
            && !SituacaoItemBordero.INDEFIRIDO.equals(borderoPagamento.getSituacaoItemBordero())
            && isGestorFinanceiro();
    }

    public boolean podeIndeferirItemDespesaExtraOB(BorderoPagamentoExtra borderoPagamentoExtra) {
        return selecionado.getId() != null
            && !SituacaoItemBordero.PAGO.equals(borderoPagamentoExtra.getSituacaoItemBordero())
            && !SituacaoItemBordero.INDEFIRIDO.equals(borderoPagamentoExtra.getSituacaoItemBordero())
            && isGestorFinanceiro();
    }

    public boolean podeBaixarOB() {
        return selecionado.getId() != null
            && isGestorFinanceiro()
            && selecionado.isOrdemBancariaDeferida();
    }

    private Boolean isGestorFinanceiro() {
        return borderoFacade.getPagamentoFacade().isGestorFinanceiro(
            sistemaControlador.getUsuarioCorrente(),
            sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente(),
            sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(),
            sistemaControlador.getDataOperacao());
    }

    public boolean podeEstornarBaixa() {
        return SituacaoBordero.PAGO.equals(selecionado.getSituacao()) && isGestorFinanceiro();
    }

    public boolean desabilitaCampoBancoEContaFinanceira() {
        return selecionado.getId() != null
            || !selecionado.getListaPagamentos().isEmpty()
            || !selecionado.getListaPagamentosExtra().isEmpty()
            || !selecionado.getListaTransferenciaFinanceira().isEmpty()
            || !selecionado.getListaTransferenciaMesmaUnidade().isEmpty()
            || !selecionado.getListaLiberacaoCotaFinanceira().isEmpty();
    }

    public boolean renderizarBotaoImprimirOB() {
        return !selecionado.getListaPagamentos().isEmpty()
            || !selecionado.getListaPagamentosExtra().isEmpty()
            || !selecionado.getListaTransferenciaFinanceira().isEmpty()
            || !selecionado.getListaTransferenciaMesmaUnidade().isEmpty()
            || !selecionado.getListaLiberacaoCotaFinanceira().isEmpty();
    }

    public Boolean renderizarBotaoDeferir() {
        return SituacaoBordero.ABERTO.equals(selecionado.getSituacao()) && selecionado.getId() != null;
    }

    public void gerarRelatorio() throws IOException, JRException {
        try {
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            Long vias = 3L;
            String arquivoJasper = "RelatorioBorderoPrincipal.jasper";
            HashMap parameters = new HashMap();
            parameters.put("MUNICIPIO", "Município de Rio Branco");
            parameters.put("IMAGEM", abstractReport.getCaminhoImagem());
            parameters.put("SUBREPORT_DIR", abstractReport.getCaminho());
            parameters.put("BORDERO", selecionado.getId());
            parameters.put("VIAS", vias);
            parameters.put("OBSERVACAO", "");
            abstractReport.gerarRelatorio(arquivoJasper, parameters);
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }
    }

    public void baixarBordero() {
        try {
            borderoFacade.baixarBordero(selecionado);
            FacesUtil.addOperacaoRealizada(" A baixa da ordem bancária: " + selecionado + " foi realizada com sucesso.");
            redireciona();
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void estornarBaixarBordero() {
        try {
            borderoFacade.estornarBaixarBordero(selecionado);
            FacesUtil.addOperacaoRealizada(" O estorno da baixa da ordem bancária: " + selecionado + " foi realizada com sucesso.");
            redireciona();
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void deferirOrdemBancaria() {
        try {
            selecionado = borderoFacade.recuperar(selecionado.getId());
            borderoFacade.deferirOrdemBancaria(selecionado, itensBordero);
            FacesUtil.addOperacaoRealizada(" A ordem bancária foi deferida com sucesso.");
            redirecionarParaEdita();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            borderoFacade.desbloquear(selecionado);
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void redirecionarParaEdita() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
    }

    public void indeferirOrdemBancaria() {
        try {
            borderoFacade.indeferirOrdemBancaria(selecionado);
            FacesUtil.addOperacaoRealizada(" A Ordem Bancária foi indeferida com sucesso.");
            redireciona();
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void baixarItemPagamentoOB(BorderoPagamento borderoPagamento) {
        try {
            borderoFacade.baixarItemPagamentoOrdemBancaria(StatusPagamento.PAGO, SituacaoItemBordero.PAGO, borderoPagamento);
            FacesUtil.addOperacaoRealizada(" Item pagamento foi baixado(pago) com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void baixarItemDespesaExtraOB(BorderoPagamentoExtra borderoPagamentoExtra) {
        try {
            borderoFacade.baixarItemDespesaExtraOrdemBancaria(StatusPagamento.PAGO, SituacaoItemBordero.PAGO, borderoPagamentoExtra);
            FacesUtil.addOperacaoRealizada(" Item despesa extra foi baixado(pago) com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void indeferirItemPagamentoOB(BorderoPagamento borderoPagamento) {
        try {
            borderoFacade.indeferirItemPagamentoDaOB(borderoPagamento, null);
            FacesUtil.addOperacaoRealizada(" Item da ordem bancária foi indeferido com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void indeferirItemPagamentoExtraOB(BorderoPagamentoExtra borderoPagamentoExtra) {
        try {
            borderoFacade.indeferirItemPagamentoExtraDaOB(borderoPagamentoExtra, null);
            FacesUtil.addOperacaoRealizada(" Item da ordem bancária foi indeferido com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public List<SubConta> completarSubContas(String parte) {
        if (selecionado.getBanco() == null) {
            return borderoFacade.getSubContaFacade().listaPorUnidadeOrganizacional(parte.trim(), ((Bordero) selecionado).getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao());
        }
        return borderoFacade.getSubContaFacade().listaPorUnidadeOrganizacionalEBanco(parte.trim(), ((Bordero) selecionado).getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), ((Bordero) selecionado).getBanco());
    }

    public void adicionarTodosPagamentos() {
        if (tipoOperacaoBordero != null) {
            switch (tipoOperacaoBordero) {
                case PAGAMENTO:
                    adicionarPagamentos();
                    break;
                case PAGAMENTO_EXTRA:
                    adicionarPagamentosExtras();
                    break;
                case TRANSFERENCIA_FINANCEIRA:
                    adicionarTransferenciaFinanceira();
                    break;
                case TRANSFERENCIA_FINANCEIRA_MESMA_UNIDADE:
                    adicionarTransferenciaMesmaUnidade();
                    break;
                case LIBERACAO_FINANCEIRA:
                    adicionarLiberacaoContaFinanceira();
            }
        }
    }

    public Boolean validaAdiconarLiberacaoFinaneira(LiberacaoCotaFinanceira liberacao) {
        Boolean controle = true;
        for (BorderoLiberacaoFinanceira lib : selecionado.getListaLiberacaoCotaFinanceira()) {
            if (lib.getLiberacaoCotaFinanceira().getId().equals(liberacao.getId())) {
                FacesUtil.addAtencao(" A Liberação Financeira: " + liberacao.toString() + " já foi adiconada na ordem bancária.");
                controle = false;
            }
        }
        return controle;
    }

    public void adicionarLiberacaoContaFinanceira() {
        if (arrayLiberacaoCotaFinanceira.length == 0) {
            FacesUtil.addOperacaoNaoPermitida(" Para contiuar selecione uma Liberação Financeira.");
        } else {
            for (int i = 0; i < arrayLiberacaoCotaFinanceira.length; i++) {
                if (selecionado.getListaLiberacaoCotaFinanceira().contains(arrayLiberacaoCotaFinanceira[i])) {
                    selecionado.getListaLiberacaoCotaFinanceira().remove(arrayLiberacaoCotaFinanceira[i]);
                }
                if (validaAdiconarLiberacaoFinaneira(arrayLiberacaoCotaFinanceira[i])) {
                    selecionado.setValor(selecionado.getValor().add(((LiberacaoCotaFinanceira) arrayLiberacaoCotaFinanceira[i]).getValor()));
                    selecionado.setQntdPagamentos(selecionado.getQntdPagamentos() + 1l);
                    selecionado.getListaLiberacaoCotaFinanceira().add(new BorderoLiberacaoFinanceira(selecionado, arrayLiberacaoCotaFinanceira[i]));
                    for (BorderoLiberacaoFinanceira item : selecionado.getListaLiberacaoCotaFinanceira()) {
                        item.setSituacaoItemBordero(SituacaoItemBordero.BORDERO);
                    }
                }
            }
        }
    }

    public Boolean validaAdiconarTransfFinaneiraMesmaUnidade(TransferenciaMesmaUnidade transferencia) {
        Boolean controle = true;
        for (BorderoTransferenciaMesmaUnidade transf : selecionado.getListaTransferenciaMesmaUnidade()) {
            if (transf.getTransferenciaMesmaUnidade().getId().equals(transferencia.getId())) {
                FacesUtil.addAtencao(" A Transferência Financeira de Mesma Unidade: " + transferencia.toString() + " já foi adiconada na ordem bancária.");
                controle = false;
            }
        }
        return controle;
    }

    public void adicionarTransferenciaMesmaUnidade() {
        if (arrayTransferenciaMesmaUnidade.length == 0) {
            FacesUtil.addOperacaoNaoPermitida(" Para continuar selecione uma Transferência Financeira de Mesma Unidade.");
        } else {
            for (int i = 0; i < arrayTransferenciaMesmaUnidade.length; i++) {
                if (selecionado.getListaTransferenciaMesmaUnidade().contains(arrayTransferenciaMesmaUnidade[i])) {
                    selecionado.getListaTransferenciaMesmaUnidade().remove(arrayTransferenciaMesmaUnidade[i]);
                }
                if (validaAdiconarTransfFinaneiraMesmaUnidade(arrayTransferenciaMesmaUnidade[i])) {
                    selecionado.setValor(selecionado.getValor().add(((TransferenciaMesmaUnidade) arrayTransferenciaMesmaUnidade[i]).getValor()));
                    selecionado.setQntdPagamentos(selecionado.getQntdPagamentos() + 1l);
                    selecionado.getListaTransferenciaMesmaUnidade().add(new BorderoTransferenciaMesmaUnidade(selecionado, arrayTransferenciaMesmaUnidade[i]));
                    for (BorderoTransferenciaMesmaUnidade item : selecionado.getListaTransferenciaMesmaUnidade()) {
                        item.setSituacaoItemBordero(SituacaoItemBordero.BORDERO);
                    }
                }
            }
        }
    }

    public Boolean validaAdiconarTransferenciaFinaneira(TransferenciaContaFinanceira transferencia) {
        Boolean controle = true;
        for (BorderoTransferenciaFinanceira transf : selecionado.getListaTransferenciaFinanceira()) {
            if (transf.getTransferenciaContaFinanceira().getId().equals(transferencia.getId())) {
                FacesUtil.addAtencao(" A Transferência Financeira: " + transferencia.toString() + " já foi adiconada na ordem bancária.");
                controle = false;
            }
        }
        return controle;
    }

    public void adicionarTransferenciaFinanceira() {
        if (arrayTransferenciaFinanceira.length == 0) {
            FacesUtil.addOperacaoNaoPermitida(" Para continuar selecione uma Transferência Financeira.");
        } else {
            for (int i = 0; i < arrayTransferenciaFinanceira.length; i++) {
                if (selecionado.getListaTransferenciaFinanceira().contains(arrayTransferenciaFinanceira[i])) {
                    selecionado.getListaTransferenciaFinanceira().remove(arrayTransferenciaFinanceira[i]);
                }
                if (validaAdiconarTransferenciaFinaneira(arrayTransferenciaFinanceira[i])) {
                    selecionado.setValor(selecionado.getValor().add(((TransferenciaContaFinanceira) arrayTransferenciaFinanceira[i]).getValor()));
                    selecionado.setQntdPagamentos(selecionado.getQntdPagamentos() + 1l);
                    selecionado.getListaTransferenciaFinanceira().add(new BorderoTransferenciaFinanceira(arrayTransferenciaFinanceira[i], selecionado));
                    for (BorderoTransferenciaFinanceira item : selecionado.getListaTransferenciaFinanceira()) {
                        item.setSituacaoItemBordero(SituacaoItemBordero.BORDERO);
                    }
                }
            }
        }
    }

    public void removeTransferenciaFinanceira(BorderoTransferenciaFinanceira transf) {
        selecionado.setValor(selecionado.getValor().subtract(transf.getTransferenciaContaFinanceira().getValor()));
        selecionado.setQntdPagamentos(selecionado.getQntdPagamentos() - 1l);
        selecionado.getListaTransferenciaFinanceira().remove(transf);
        if (transf.getId() != null) {
            itensBordero.setListaTransferenciaFinanceiraExclusao(Util.adicionarObjetoEmLista(itensBordero.getListaTransferenciaFinanceiraExclusao(), transf));
        }
    }

    public void removePagamento(BorderoPagamento pag) {
        Bordero b = ((Bordero) selecionado);
        b.setValor(b.getValor().subtract(pag.getPagamento().getValorFinal()));
        b.setQntdPagamentos(b.getQntdPagamentos() - 1l);
        b.getListaPagamentos().remove(pag);
        if (pag.getId() != null) {
            itensBordero.setListaPagamentosExclusao(Util.adicionarObjetoEmLista(itensBordero.getListaPagamentosExclusao(), pag));
        }
    }

    public void removePagamentoExtra(BorderoPagamentoExtra pagExtra) {
        Bordero b = ((Bordero) selecionado);
        b.setValor(b.getValor().subtract(pagExtra.getPagamentoExtra().getValor()));
        b.setQntdPagamentos(b.getQntdPagamentos() - 1l);
        b.getListaPagamentosExtra().remove(pagExtra);
        if (pagExtra.getId() != null) {
            itensBordero.setListaPagamentoExtraExclusao(Util.adicionarObjetoEmLista(itensBordero.getListaPagamentoExtraExclusao(), pagExtra));
        }
    }

    public void removeTransferenciaMesmaUnidade(BorderoTransferenciaMesmaUnidade transf) {
        selecionado.setValor(selecionado.getValor().subtract(transf.getTransferenciaMesmaUnidade().getValor()));
        selecionado.setQntdPagamentos(selecionado.getQntdPagamentos() - 1l);
        selecionado.getListaTransferenciaMesmaUnidade().remove(transf);
        if (transf.getId() != null) {
            itensBordero.setListaTransferenciaMesmaUnidadeExclusao(Util.adicionarObjetoEmLista(itensBordero.getListaTransferenciaMesmaUnidadeExclusao(), transf));
        }
    }

    public void removeLiberacaoCotaFinanceira(BorderoLiberacaoFinanceira liberacaoCotaFinanceira) {
        selecionado.setValor(selecionado.getValor().subtract(liberacaoCotaFinanceira.getLiberacaoCotaFinanceira().getValor()));
        selecionado.setQntdPagamentos(selecionado.getQntdPagamentos() - 1l);
        selecionado.getListaLiberacaoCotaFinanceira().remove(liberacaoCotaFinanceira);
        if (liberacaoCotaFinanceira.getId() != null) {
            itensBordero.setListaLiberacaoFinanceiraExclusao(Util.adicionarObjetoEmLista(itensBordero.getListaLiberacaoFinanceiraExclusao(), liberacaoCotaFinanceira));
        }
    }


    public void adicionarPagamentosExtras() {
        if (arrayPgtoExtra.length == 0) {
            FacesUtil.addOperacaoNaoPermitida(" Para continuar selecione uma Despesa Extraorçamentária.");
        } else {
            for (int i = 0; i < arrayPgtoExtra.length; i++) {
                if (selecionado.getListaPagamentosExtra().contains(arrayPgtoExtra[i])) {
                    selecionado.getListaPagamentosExtra().remove(arrayPgtoExtra[i]);
                }
                if (podeAdicionarDespesaExtra(arrayPgtoExtra[i])) {
                    selecionado.setValor(selecionado.getValor().add(((PagamentoExtra) arrayPgtoExtra[i]).getValor()));
                    selecionado.setQntdPagamentos(selecionado.getQntdPagamentos() + 1l);
                    selecionado.getListaPagamentosExtra().add(new BorderoPagamentoExtra(selecionado, arrayPgtoExtra[i]));
                    for (BorderoPagamentoExtra item : selecionado.getListaPagamentosExtra()) {
                        item.setSituacaoItemBordero(SituacaoItemBordero.BORDERO);
                    }
                }
            }
        }
    }

    public Boolean podeAdicionarDespesaExtra(PagamentoExtra pagamentoExtra) {
        Boolean controle = true;
        for (BorderoPagamentoExtra pagExtra : selecionado.getListaPagamentosExtra()) {
            if (pagExtra.getPagamentoExtra().getId().equals(pagamentoExtra.getId())) {
                FacesUtil.addAtencao(" A Despesa Extraorçamentária: " + pagamentoExtra.toString() + " já foi adicionada na ordem bancária.");
                controle = false;
            }
        }
        PagamentoExtra despesaExtraSelecionada = borderoFacade.getPagamentoExtraFacade().recuperar(pagamentoExtra.getId());
        if (!despesaExtraSelecionada.getGuiaPagamentoExtras().isEmpty()) {
            for (GuiaPagamentoExtra guiaPagamento : despesaExtraSelecionada.getGuiaPagamentoExtras()) {
                if (TipoDocPagto.FATURA.equals(despesaExtraSelecionada.getTipoDocumento())) {
                    if (guiaPagamento.getGuiaFatura().getCodigoBarra() == null) {
                        FacesUtil.addOperacaoNaoPermitida("A Despesa Extraorçamentária: Nº: " + despesaExtraSelecionada.getNumero() + ", possui guia(s) com código de barras não preenchido.");
                        controle = false;
                    }
                    if (guiaPagamento.getGuiaFatura().getCodigoBarra().startsWith("8")) {
                        FacesUtil.addOperacaoNaoPermitida("A Despesa Extraorçamentária: Nº: " + despesaExtraSelecionada.getNumero() + ", possui guia(s) de fatura iniciada com '8'. Por favor, corrigir o código de barras para continuar.");
                        controle = false;
                    }
                }
                if (guiaPagamento.isGuiaTipoCinco(despesaExtraSelecionada)) {
                    if (guiaPagamento.getDataPagamento() == null) {
                        FacesUtil.addOperacaoNaoPermitida("A Despesa Extraorçamentária: Nº: " + despesaExtraSelecionada.getNumero() + ", possui guia(s) com data de pagamento não preenchida.");
                        controle = false;
                    }
                }
            }
        }
        return controle;
    }

    public void adicionarPagamentos() {
        if (arrayPgto.length == 0) {
            FacesUtil.addOperacaoNaoPermitida(" Para continuar selecione um Pagamento.");
        } else {

            for (int i = 0; i < arrayPgto.length; i++) {
                if (selecionado.getListaPagamentos().contains(arrayPgto[i])) {
                    selecionado.getListaPagamentos().remove(arrayPgto[i]);
                }
                if (podeAdicionarPagamento(arrayPgto[i])) {
                    selecionado.setValor(selecionado.getValor().add(((Pagamento) arrayPgto[i]).getValorFinal()));
                    selecionado.setQntdPagamentos(selecionado.getQntdPagamentos() + 1l);
                    selecionado.getListaPagamentos().add(new BorderoPagamento(selecionado, arrayPgto[i]));
                    for (BorderoPagamento item : selecionado.getListaPagamentos()) {
                        item.setSituacaoItemBordero(SituacaoItemBordero.BORDERO);
                    }
                }
            }
        }
    }

    public Boolean podeAdicionarPagamento(Pagamento pagamento) {
        Boolean controle = true;
        for (BorderoPagamento pag : selecionado.getListaPagamentos()) {
            if (pag.getPagamento().getId().equals(pagamento.getId())) {
                FacesUtil.addAtencao(" O Pagamento: " + pagamento.toString() + " já foi adicionado na ordem bancária.");
                controle = false;
            }
        }
        Pagamento pagamentoSelecionado = borderoFacade.getPagamentoFacade().recuperar(pagamento.getId());
        if (!pagamentoSelecionado.getGuiaPagamento().isEmpty()) {
            for (GuiaPagamento guiaPagamento : pagamentoSelecionado.getGuiaPagamento()) {
                if (TipoDocPagto.FATURA.equals(pagamentoSelecionado.getTipoDocPagto())) {
                    if (guiaPagamento.getGuiaFatura().getCodigoBarra() == null) {
                        FacesUtil.addOperacaoNaoPermitida("O Pagamento: Nº: " + pagamentoSelecionado.getNumeroPagamento() + ", possui guia(s) com código de barras não preenchido.");
                        controle = false;
                    }
                    if (guiaPagamento.getGuiaFatura().getCodigoBarra().startsWith("8")) {
                        FacesUtil.addOperacaoNaoPermitida("O Pagamento: Nº: " + pagamentoSelecionado.getNumeroPagamento() + ", possui guia(s) de fatura iniciada com '8'. Por favor, corrigir o código de barras para continuar.");
                        controle = false;
                    }
                }
                if (guiaPagamento.isGuiaTipoCinco(pagamentoSelecionado)) {
                    if (guiaPagamento.getDataPagamento() == null) {
                        FacesUtil.addOperacaoNaoPermitida("O Pagamento: Nº: " + pagamentoSelecionado.getNumeroPagamento() + ", possui guia(s) com data de pagamento não preenchida.");
                        controle = false;
                    }
                }
            }
        }
        return controle;
    }


    public BigDecimal retornaValorTotalPagamentoExtra() {
        BigDecimal soma = BigDecimal.ZERO;
        Bordero b = ((Bordero) selecionado);
        if (!b.getListaPagamentosExtra().isEmpty() || b.getListaPagamentosExtra() != null) {
            for (BorderoPagamentoExtra pagExtra : b.getListaPagamentosExtra()) {
                soma = soma.add(pagExtra.getPagamentoExtra().getValor());
            }
        }
        return soma;
    }

    public BigDecimal retornaValorTotalPagamento() {
        BigDecimal soma = BigDecimal.ZERO;
        Bordero b = ((Bordero) selecionado);
        if (!b.getListaPagamentos().isEmpty() || b.getListaPagamentos() != null) {
            for (BorderoPagamento pag : b.getListaPagamentos()) {
                soma = soma.add(pag.getPagamento().getValorFinal());
            }
        }
        return soma;
    }

    public BigDecimal retornaValorTotalTransferenciaFinanceira() {
        BigDecimal soma = BigDecimal.ZERO;
        if (!selecionado.getListaTransferenciaFinanceira().isEmpty() || selecionado.getListaTransferenciaFinanceira() != null) {
            for (BorderoTransferenciaFinanceira tra : selecionado.getListaTransferenciaFinanceira()) {
                soma = soma.add(tra.getTransferenciaContaFinanceira().getValor());
            }
        }
        return soma;
    }

    public BigDecimal retornaValorTotalTransferenciaMesmaUnidade() {
        BigDecimal soma = BigDecimal.ZERO;
        if (!selecionado.getListaTransferenciaMesmaUnidade().isEmpty() || selecionado.getListaTransferenciaMesmaUnidade() != null) {
            for (BorderoTransferenciaMesmaUnidade tra : selecionado.getListaTransferenciaMesmaUnidade()) {
                soma = soma.add(tra.getTransferenciaMesmaUnidade().getValor());
            }
        }
        return soma;
    }

    public BigDecimal retornaValorTotalLiberacaoCotaFinanceira() {
        BigDecimal soma = BigDecimal.ZERO;
        if (!selecionado.getListaLiberacaoCotaFinanceira().isEmpty() || selecionado.getListaLiberacaoCotaFinanceira() != null) {
            for (BorderoLiberacaoFinanceira lcf : selecionado.getListaLiberacaoCotaFinanceira()) {
                soma = soma.add(lcf.getLiberacaoCotaFinanceira().getValor());
            }
        }
        return soma;
    }

    public List<Banco> completaBanco(String parte) {
        return borderoFacade.getBancoFacade().listaFiltrando(parte.trim(), "descricao", "numeroBanco");
    }

    public void pesquisarMovimentosContabeis() {
        if (tipoOperacaoBordero != null) {
            switch (tipoOperacaoBordero) {
                case PAGAMENTO:
                    buscarPagamentosOrcamentarios();
                    break;

                case PAGAMENTO_EXTRA:
                    buscarPagamentosExtra();
                    break;

                case TRANSFERENCIA_FINANCEIRA:
                    buscarTransferenciasFinanceiras();
                    break;

                case TRANSFERENCIA_FINANCEIRA_MESMA_UNIDADE:
                    buscarTransferenciasMesmaUnidade();
                    break;

                case LIBERACAO_FINANCEIRA:
                    buscarLiberacoesCotaFinanceiras();
                    break;
            }
        }
    }

    public void buscarPagamentosOrcamentarios() {
        if (selecionado.getBanco() != null && selecionado.getUnidadeOrganizacional() != null) {
            listaPagamentos = borderoFacade.getPagamentoFacade().listaPorBancoUnidade(selecionado.getBanco(), selecionado.getUnidadeOrganizacional(), selecionado.getSubConta(), sistemaControlador.getExercicioCorrente());
        }
    }

    public void buscarPagamentosExtra() {
        if (selecionado.getBanco() != null && selecionado.getUnidadeOrganizacional() != null) {
            listaPagamentoExtra = borderoFacade.getPagamentoExtraFacade().listaPorBancoUnidade(selecionado.getBanco(), selecionado.getUnidadeOrganizacional(), selecionado.getSubConta(), sistemaControlador.getExercicioCorrente());
        }
    }

    public void buscarTransferenciasMesmaUnidade() {
        if (selecionado.getBanco() != null && selecionado.getUnidadeOrganizacional() != null) {
            listaTransferenciaMesmaUnidade = borderoFacade.getTransferenciaMesmaUnidadeFacade().listaFiltrandoPorBancoUnidade(selecionado.getBanco(), selecionado.getUnidadeOrganizacional(), selecionado.getSubConta(), sistemaControlador.getExercicioCorrente());
        }
    }

    public void buscarTransferenciasFinanceiras() {
        if (selecionado.getBanco() != null && selecionado.getUnidadeOrganizacional() != null) {
            listaTransferenciaFinanceira = borderoFacade.getTransferenciaContaFinanceiraFacade().listaFiltrandoPorBancoUnidade(selecionado.getBanco(), selecionado.getUnidadeOrganizacional(), selecionado.getSubConta(), sistemaControlador.getExercicioCorrente());
        }
    }

    public void buscarLiberacoesCotaFinanceiras() {
        if (selecionado.getBanco() != null && selecionado.getUnidadeOrganizacional() != null) {
            listaLiberacaoCotaFinanceira = borderoFacade.getLiberacaoCotaFinanceiraFacade().listaFiltrandoPorBancoUnidade(selecionado.getBanco(), selecionado.getUnidadeOrganizacional(), selecionado.getSubConta(), sistemaControlador.getExercicioCorrente());
        }
    }

    public void recuperaBancoApartirDaContaFinanceira() {
        if (selecionado.getSubConta() != null) {
            selecionado.setBanco(retornoBanco(selecionado.getSubConta()));
        }
        tipoOperacaoBordero = null;
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewPrincipal:operacaoOB')");
    }

    private Banco retornoBanco(SubConta subConta) {
        try {
            return ((SubConta) subConta).getContaBancariaEntidade().getAgencia().getBanco();
        } catch (Exception ex) {
            return new Banco();
        }
    }

    public PagamentoExtra[] getArrayPgtoExtra() {
        return arrayPgtoExtra;
    }

    public void setArrayPgtoExtra(PagamentoExtra[] arrayPgtoExtra) {
        this.arrayPgtoExtra = arrayPgtoExtra;
    }

    public TransferenciaContaFinanceira[] getArrayTransferenciaFinanceira() {
        return arrayTransferenciaFinanceira;
    }

    public void setArrayTransferenciaFinanceira(TransferenciaContaFinanceira[] arrayTransferenciaFinanceira) {
        this.arrayTransferenciaFinanceira = arrayTransferenciaFinanceira;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public TransferenciaMesmaUnidade[] getArrayTransferenciaMesmaUnidade() {
        return arrayTransferenciaMesmaUnidade;
    }

    public void setArrayTransferenciaMesmaUnidade(TransferenciaMesmaUnidade[] arrayTransferenciaMesmaUnidade) {
        this.arrayTransferenciaMesmaUnidade = arrayTransferenciaMesmaUnidade;
    }

    public Pagamento[] getArrayPgto() {
        return arrayPgto;
    }

    public void setArrayPgto(Pagamento[] arrayPgto) {
        this.arrayPgto = arrayPgto;
    }

    public List<PagamentoExtra> getListaPagamentoExtra() {
        return listaPagamentoExtra;
    }

    public void setListaPagamentoExtra(List<PagamentoExtra> listaPagamentoExtra) {
        this.listaPagamentoExtra = listaPagamentoExtra;
    }

    public List<Pagamento> getListaPagamentos() {
        return listaPagamentos;
    }

    public void setListaPagamentos(List<Pagamento> listaPagamentos) {
        this.listaPagamentos = listaPagamentos;
    }

    public List<LiberacaoCotaFinanceira> getListaLiberacaoCotaFinanceira() {
        return listaLiberacaoCotaFinanceira;
    }

    public void setListaLiberacaoCotaFinanceira(List<LiberacaoCotaFinanceira> listaLiberacaoCotaFinanceira) {
        this.listaLiberacaoCotaFinanceira = listaLiberacaoCotaFinanceira;
    }

    public LiberacaoCotaFinanceira[] getArrayLiberacaoCotaFinanceira() {
        return arrayLiberacaoCotaFinanceira;
    }

    public void setArrayLiberacaoCotaFinanceira(LiberacaoCotaFinanceira[] arrayLiberacaoCotaFinanceira) {
        this.arrayLiberacaoCotaFinanceira = arrayLiberacaoCotaFinanceira;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/ordem-bancaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean desabilitaBotaoRemoverItem(Bordero bordero) {
        return !SituacaoBordero.DEFERIDO.equals(bordero.getSituacao());
    }

    public void navegaParaRemoverItemBordero(Bordero bordero) {
        Web.navegacao(getCaminhoOrigem(), "/ordem-bancaria/remover-item-bordero/" + bordero.getId() + "/");
    }

    public void visualizarPagamento(Pagamento pagamento) {
        if (CategoriaOrcamentaria.NORMAL.equals(pagamento.getCategoriaOrcamentaria())) {
            navegarPara("/pagamento/ver/", pagamento.getId());
        } else {
            navegarPara("/pagamento/resto-a-pagar/ver/", pagamento.getId());
        }
    }

    public void visualizarPagamentoExtra(PagamentoExtra pagamentoExtra) {
        PagamentoExtraControlador controlador = (PagamentoExtraControlador) Util.getControladorPeloNome("pagamentoExtraControlador");
        navegarPara(controlador.getCaminhoPadrao() + "ver/", pagamentoExtra.getId());
    }

    public void visualizarTransferencia(TransferenciaContaFinanceira transferenciaContaFinanceira) {
        TransferenciaContaFinanceiraControlador controlador = (TransferenciaContaFinanceiraControlador) Util.getControladorPeloNome("transferenciaContaFinanceiraControlador");
        navegarPara(controlador.getCaminhoPadrao() + "ver/", transferenciaContaFinanceira.getId());
    }

    public void visualizarTransferenciaMesmaUnidade(TransferenciaMesmaUnidade transferenciaMesmaUnidade) {
        TransferenciaMesmaUnidadeControlador controlador = (TransferenciaMesmaUnidadeControlador) Util.getControladorPeloNome("transferenciaMesmaUnidadeControlador");
        navegarPara(controlador.getCaminhoPadrao() + "ver/", transferenciaMesmaUnidade.getId());
    }

    public void visualizarLiberacao(LiberacaoCotaFinanceira liberacaoCotaFinanceira) {
        LiberacaoCotaFinanceiraControlador controlador = (LiberacaoCotaFinanceiraControlador) Util.getControladorPeloNome("liberacaoCotaFinanceiraControlador");
        navegarPara(controlador.getCaminhoPadrao() + "ver/", liberacaoCotaFinanceira.getId());
    }

    public void navegarPara(String caminho, Long id) {
        Web.navegacao(getCaminhoOrigem(), caminho + id + "/");
    }

    public String getNumeroArquivoBancario() {
        return numeroArquivoBancario;
    }

    public void setNumeroArquivoBancario(String numeroArquivoBancario) {
        this.numeroArquivoBancario = numeroArquivoBancario;
    }

    public List<SelectItem> getOperacoes() {
        return Util.getListSelectItem(TipoOperacaoBordero.values(), false);
    }

    public List<TransferenciaContaFinanceira> getListaTransferenciaFinanceira() {
        return listaTransferenciaFinanceira;
    }

    public void setListaTransferenciaFinanceira(List<TransferenciaContaFinanceira> listaTransferenciaFinanceira) {
        this.listaTransferenciaFinanceira = listaTransferenciaFinanceira;
    }

    public List<TransferenciaMesmaUnidade> getListaTransferenciaMesmaUnidade() {
        return listaTransferenciaMesmaUnidade;
    }

    public void setListaTransferenciaMesmaUnidade(List<TransferenciaMesmaUnidade> listaTransferenciaMesmaUnidade) {
        this.listaTransferenciaMesmaUnidade = listaTransferenciaMesmaUnidade;
    }

    public ItensBordero getItensBordero() {
        return itensBordero;
    }

    public void setItensBordero(ItensBordero itensBordero) {
        this.itensBordero = itensBordero;
    }

    public TipoOperacaoBordero getTipoOperacaoBordero() {
        return tipoOperacaoBordero;
    }

    public void setTipoOperacaoBordero(TipoOperacaoBordero tipoOperacaoBordero) {
        this.tipoOperacaoBordero = tipoOperacaoBordero;
    }
}
