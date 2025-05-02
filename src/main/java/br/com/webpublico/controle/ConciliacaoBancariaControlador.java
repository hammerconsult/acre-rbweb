package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.RelatorioPesquisaGenerico;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConciliacaoBancariaFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 24/09/13
 * Time: 15:08
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "lista", pattern = "/conciliacao-bancaria-manual/lista/", viewId = "/faces/financeiro/conciliacao/novaconciliacaobancaria/lista.xhtml"),
    @URLMapping(id = "novo-conciliar", pattern = "/conciliacao-bancaria-manual/", viewId = "/faces/financeiro/conciliacao/novaconciliacaobancaria/lista.xhtml")
})
public class ConciliacaoBancariaControlador implements Serializable {

    private ConciliacaoBancaria selecionado;
    @EJB
    private ConciliacaoBancariaFacade conciliacaoBancariaFacade;
    private ConverterAutoComplete converterContaBancaria;
    private List listaDeObjetos;
    private RelatorioPesquisaGenerico relatorioPesquisaGenerico;
    private List<String> mensagemDosObjetosRemovidos;
    private List<Object> objetosRemovidos;

    public ConciliacaoBancariaControlador() {

    }

    @URLAction(mappingId = "lista", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        parametrosIniciais();
    }

    private void parametrosIniciais() {
        selecionado = new ConciliacaoBancaria();
        selecionado.setDataConciliacao(UtilRH.getDataOperacao());
        listaDeObjetos = Lists.newArrayList();
        objetosRemovidos = Lists.newArrayList();
        mensagemDosObjetosRemovidos = Lists.newArrayList();
        relatorioPesquisaGenerico = null;
        FacesUtil.executaJavaScript("setaFoco('Formulario:contaBancaria_input')");
    }


    @URLAction(mappingId = "novo-conciliar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoConciliar() {
        parametrosIniciais();
        setarContaBancaria();
    }

    private void setarContaBancaria() {
        Conciliacao conciliacao = (Conciliacao) Web.pegaDaSessao(Conciliacao.class);
        selecionado.setContaBancaria(conciliacao.getContaBancaria());
    }


    public void cancelar() {
        FacesUtil.redirecionamentoInterno("/");
    }

    public ConciliacaoBancaria getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ConciliacaoBancaria selecionado) {
        this.selecionado = selecionado;
    }

    public List<ContaBancariaEntidade> completaContaBancaria(String parte) {
        List<ContaBancariaEntidade> retorno = conciliacaoBancariaFacade.getContaBancariaEntidadeFacade().listaFiltrando(parte.trim(), "numeroConta", "nomeConta");
        return retorno;
    }

    public ConverterAutoComplete getConverterContaBancaria() {
        if (converterContaBancaria == null) {
            converterContaBancaria = new ConverterAutoComplete(ContaBancariaEntidade.class, conciliacaoBancariaFacade);
        }
        return converterContaBancaria;
    }

    public List<TipoLancamento> getTipoLancamento() {
        List<TipoLancamento> retorno = new ArrayList<>();
        for (TipoLancamento tipo : TipoLancamento.values()) {
            retorno.add(tipo);
        }
        return retorno;
    }

    public List<ConcedidaRecebida> getTipoConcedidaRecebida() {
        List<ConcedidaRecebida> retorno = new ArrayList<>();
        for (ConcedidaRecebida tipo : ConcedidaRecebida.values()) {
            retorno.add(tipo);
        }
        return retorno;
    }

    public List<SelectItem> getMovimentacaoFinanceira() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (MovimentacaoFinanceira movimentacaoFinanceira : MovimentacaoFinanceira.values()) {
            retorno.add(new SelectItem(movimentacaoFinanceira, movimentacaoFinanceira.getDescricao()));
        }
        return retorno;
    }

    public List getListaDeObjetos() {
        return listaDeObjetos;
    }

    public void setListaDeObjetos(List listaDeObjetos) {
        this.listaDeObjetos = listaDeObjetos;
    }

    public RelatorioPesquisaGenerico getRelatorioPesquisaGenerico() {
        return relatorioPesquisaGenerico;
    }

    public void setRelatorioPesquisaGenerico(RelatorioPesquisaGenerico relatorioPesquisaGenerico) {
        this.relatorioPesquisaGenerico = relatorioPesquisaGenerico;
    }

    public void recuperarRelatorioPesquisaGenerico() {
        String nomeDaClasse = "br.com.webpublico.entidades." + selecionado.getMovimentacaoFinanceira().getClasse();
        if (listaDeObjetos.size() > 0) {
            relatorioPesquisaGenerico = conciliacaoBancariaFacade.getRelatorioPesquisaGenericoFacade().recuperaRelatorioPorClasseTipoPadrao(nomeDaClasse, TipoRelatorio.TABELA);
            if (relatorioPesquisaGenerico == null) {
                try {
                    Class classe = Class.forName(nomeDaClasse);
                    Object o = classe.newInstance();
                    EntidadeMetaData entidadeMetaData = new EntidadeMetaData(classe);
                    relatorioPesquisaGenerico = new RelatorioPesquisaGenerico(o, new EntidadeMetaData(classe));
                } catch (Exception e) {

                }
            }
        } else {
            FacesUtil.addOperacaoNaoPermitida("Nenhum(a) " + selecionado.getMovimentacaoFinanceira().getDescricao() + " foi selecionado(a).");
        }
    }

    public void salvar() {
        try {
            mensagemDosObjetosRemovidos = Lists.newArrayList();
            objetosRemovidos = Lists.newArrayList();
            validarCampos(selecionado);
            removerMovimentos();
            if (!mensagemDosObjetosRemovidos.isEmpty()) {
                FacesUtil.executaJavaScript("dialogMensagens.show()");
            } else {
                conciliacaoBancariaFacade.validarObjetos(selecionado, listaDeObjetos);
                conciliacaoBancariaFacade.salvarObjeto(selecionado, listaDeObjetos);
                novo();
                FacesUtil.addOperacaoRealizada(" Registro salvo com Sucesso. ");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void salvarSemMovimentosInvalidos() {
        try {
            if (!objetosRemovidos.isEmpty()) {
                listaDeObjetos.removeAll(objetosRemovidos);
            }
            conciliacaoBancariaFacade.validarObjetos(selecionado, listaDeObjetos);
            conciliacaoBancariaFacade.salvarObjeto(selecionado, listaDeObjetos);
            novo();
            FacesUtil.addOperacaoRealizada(" Registro salvo com Sucesso. ");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void salvarComMovimentosInvalidos() {
        try {
            conciliacaoBancariaFacade.validarObjetos(selecionado, listaDeObjetos);
            conciliacaoBancariaFacade.salvarObjeto(selecionado, listaDeObjetos);
            novo();
            FacesUtil.addOperacaoRealizada(" Registro salvo com Sucesso. ");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void reiniciarConsulta() {
        relatorioPesquisaGenerico = null;
        selecionado.setMovimentacaoFinanceira(null);
    }

    public void validarCampos(ConciliacaoBancaria cb) {
        ValidacaoException ve = new ValidacaoException();
        if (cb.getDataConciliacao() == null && cb.getTipoLancamento().isNormal()) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Data de Conciliação deve ser informado.");
        }
        if (cb.getContaBancaria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Conta Bancária deve ser informado.");
        }
        if (cb.getMovimentacaoFinanceira() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Movimentação Financeira deve ser informado.");
        }
        if (listaDeObjetos.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Nenhuma Movimentação Financeira foi selecionada.");
        }
        ve.lancarException();
    }

    public Boolean mostrarCampoConcedidaRecebida() {
        return selecionado.possuiDuasDatasConciliacao();
    }

    public boolean isTipoDiferenteDeEstorno() {
        return !MovimentacaoFinanceira.AJUSTE_ATIVO_DISPONIVEL_ESTORNO.equals(selecionado.getMovimentacaoFinanceira()) &&
            !MovimentacaoFinanceira.AJUSTE_ATIVO_DISPONIVEL_NORMAL.equals(selecionado.getMovimentacaoFinanceira()) &&
            !MovimentacaoFinanceira.ESTORNO_DA_DESPESA_EXTRA.equals(selecionado.getMovimentacaoFinanceira()) &&
            !MovimentacaoFinanceira.ESTORNO_DA_TRANFERENCIA_FINANCEIRA_DE_MESMA_UNIDADE.equals(selecionado.getMovimentacaoFinanceira()) &&
            !MovimentacaoFinanceira.ESTORNO_TRANSFERENCIA_SALDO_CONTA_FINANCEIRA.equals(selecionado.getMovimentacaoFinanceira()) &&
            !MovimentacaoFinanceira.OPERACAO_DA_CONCILIACAO.equals(selecionado.getMovimentacaoFinanceira()) &&
            !MovimentacaoFinanceira.ESTORNO_DE_PAGAMENTO_DE_RESTOS_A_PAGAR.equals(selecionado.getMovimentacaoFinanceira()) &&
            !MovimentacaoFinanceira.ESTORNO_DE_RECEITA_EXTRA.equals(selecionado.getMovimentacaoFinanceira()) &&
            !MovimentacaoFinanceira.ESTORNO_DE_RECEITA_REALIZADA.equals(selecionado.getMovimentacaoFinanceira()) &&
            !MovimentacaoFinanceira.ESTORNO_DE_PAGAMENTO.equals(selecionado.getMovimentacaoFinanceira()) &&
            !MovimentacaoFinanceira.ESTORNO_LIBERACAO_SALDO_CONTA_FINANCEIRA.equals(selecionado.getMovimentacaoFinanceira());
    }

    public Boolean habilitarBaixa(Object objeto) {
        switch (selecionado.getMovimentacaoFinanceira()) {
            case PAGAMENTO:
            case PAGAMENTO_DE_RESTOS_A_PAGAR:
                Pagamento pagamento = (Pagamento) objeto;
                return !StatusPagamento.PAGO.equals(pagamento.getStatus());

            case DESPESA_EXTRA:
                PagamentoExtra pagamentoExtra = (PagamentoExtra) objeto;
                return !StatusPagamento.PAGO.equals(pagamentoExtra.getStatus());

            case RECEITA_EXTRAORCAMENTARIA:
                ReceitaExtra receitaExtra = (ReceitaExtra) objeto;
                return SituacaoReceitaExtra.ABERTO.equals(receitaExtra.getSituacaoReceitaExtra());

            case RECEITA_REALIZADA:
                LancamentoReceitaOrc lancamentoReceitaOrc = (LancamentoReceitaOrc) objeto;
                return lancamentoReceitaOrc.getSaldo().compareTo(BigDecimal.ZERO) > 0;

            case LIBERACAO_DE_COTA_FINANCEIRA:
                LiberacaoCotaFinanceira liberacaoCotaFinanceira = (LiberacaoCotaFinanceira) objeto;
                return StatusPagamento.DEFERIDO.equals(liberacaoCotaFinanceira.getStatusPagamento());

            case TRANSFERENCIA_FINANCEIRA:
                TransferenciaContaFinanceira transferenciaContaFinanceira = (TransferenciaContaFinanceira) objeto;
                return StatusPagamento.DEFERIDO.equals(transferenciaContaFinanceira.getStatusPagamento());

            case TRANSPARENCIA_DE_SALDO_NA_MESMA_UNIDADE:
                TransferenciaMesmaUnidade transferenciaMesmaUnidade = (TransferenciaMesmaUnidade) objeto;
                return StatusPagamento.DEFERIDO.equals(transferenciaMesmaUnidade.getStatusPagamento());
        }
        return false;
    }

    public boolean isMovimentoEstornado(Object objeto) {
        switch (selecionado.getMovimentacaoFinanceira()) {
            case PAGAMENTO:
            case PAGAMENTO_DE_RESTOS_A_PAGAR:
                Pagamento pagamento = (Pagamento) objeto;
                return StatusPagamento.ESTORNADO.equals(pagamento.getStatus());

            case DESPESA_EXTRA:
                PagamentoExtra pagamentoExtra = (PagamentoExtra) objeto;
                return StatusPagamento.ESTORNADO.equals(pagamentoExtra.getStatus());

            case RECEITA_EXTRAORCAMENTARIA:
                ReceitaExtra receitaExtra = (ReceitaExtra) objeto;
                return SituacaoReceitaExtra.ESTORNADA.equals(receitaExtra.getSituacaoReceitaExtra());

            case LIBERACAO_DE_COTA_FINANCEIRA:
                LiberacaoCotaFinanceira liberacaoCotaFinanceira = (LiberacaoCotaFinanceira) objeto;
                return StatusPagamento.ESTORNADO.equals(liberacaoCotaFinanceira.getStatusPagamento());

            case TRANSFERENCIA_FINANCEIRA:
                TransferenciaContaFinanceira transferenciaContaFinanceira = (TransferenciaContaFinanceira) objeto;
                return StatusPagamento.ESTORNADO.equals(transferenciaContaFinanceira.getStatusPagamento());

            case TRANSPARENCIA_DE_SALDO_NA_MESMA_UNIDADE:
                TransferenciaMesmaUnidade transferenciaMesmaUnidade = (TransferenciaMesmaUnidade) objeto;
                return StatusPagamento.ESTORNADO.equals(transferenciaMesmaUnidade.getStatusPagamento());
        }
        return false;
    }

    public void baixarMovimento(Object objeto) {
        try {
            switch (selecionado.getMovimentacaoFinanceira()) {
                case PAGAMENTO:
                case PAGAMENTO_DE_RESTOS_A_PAGAR:
                    Pagamento pagamento = (Pagamento) objeto;
                    if (!StatusPagamento.PAGO.equals(pagamento.getStatus())) {
                        conciliacaoBancariaFacade.getPagamentoFacade().baixar(pagamento, StatusPagamento.PAGO);
                    }
                    Util.adicionarObjetoEmLista(listaDeObjetos, conciliacaoBancariaFacade.getPagamentoFacade().recuperar(((Pagamento) objeto).getId()));
                    break;

                case DESPESA_EXTRA:
                    PagamentoExtra pagamentoExtra = (PagamentoExtra) objeto;
                    if (!StatusPagamento.PAGO.equals(pagamentoExtra.getStatus())) {
                        conciliacaoBancariaFacade.getPagamentoExtraFacade().baixar(pagamentoExtra, StatusPagamento.PAGO);
                    }
                    Util.adicionarObjetoEmLista(listaDeObjetos, conciliacaoBancariaFacade.getPagamentoExtraFacade().recuperar(((PagamentoExtra) objeto).getId()));
                    break;

                case RECEITA_EXTRAORCAMENTARIA:
                    ReceitaExtra receitaExtra = (ReceitaExtra) objeto;
                    if (SituacaoReceitaExtra.ABERTO.equals(receitaExtra.getSituacaoReceitaExtra())) {
                        conciliacaoBancariaFacade.getReceitaExtraFacade().baixar(receitaExtra);
                    }
                    Util.adicionarObjetoEmLista(listaDeObjetos, conciliacaoBancariaFacade.getReceitaExtraFacade().recuperar(((ReceitaExtra) objeto).getId()));
                    break;

                case RECEITA_REALIZADA:
                    LancamentoReceitaOrc lancamentoReceitaOrc = (LancamentoReceitaOrc) objeto;
                    if (lancamentoReceitaOrc.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                        conciliacaoBancariaFacade.getLancamentoReceitaOrcFacade().baixar(lancamentoReceitaOrc);
                    }
                    Util.adicionarObjetoEmLista(listaDeObjetos, conciliacaoBancariaFacade.getLancamentoReceitaOrcFacade().recuperar(((LancamentoReceitaOrc) objeto).getId()));
                    break;

                case LIBERACAO_DE_COTA_FINANCEIRA:
                    LiberacaoCotaFinanceira liberacaoCotaFinanceira = (LiberacaoCotaFinanceira) objeto;
                    if (StatusPagamento.DEFERIDO.equals(liberacaoCotaFinanceira.getStatusPagamento())) {
                        conciliacaoBancariaFacade.getLiberacaoCotaFinanceiraFacade().baixar(liberacaoCotaFinanceira);
                    }
                    Util.adicionarObjetoEmLista(listaDeObjetos, conciliacaoBancariaFacade.getLiberacaoCotaFinanceiraFacade().recuperar(((LiberacaoCotaFinanceira) objeto).getId()));
                    break;

                case TRANSFERENCIA_FINANCEIRA:
                    TransferenciaContaFinanceira transferenciaContaFinanceira = (TransferenciaContaFinanceira) objeto;
                    if (StatusPagamento.DEFERIDO.equals(transferenciaContaFinanceira.getStatusPagamento())) {
                        conciliacaoBancariaFacade.getTransferenciaContaFinanceiraFacade().baixar(transferenciaContaFinanceira);
                    }
                    Util.adicionarObjetoEmLista(listaDeObjetos, conciliacaoBancariaFacade.getTransferenciaContaFinanceiraFacade().recuperar(((TransferenciaContaFinanceira) objeto).getId()));
                    break;

                case TRANSPARENCIA_DE_SALDO_NA_MESMA_UNIDADE:
                    TransferenciaMesmaUnidade transferenciaMesmaUnidade = (TransferenciaMesmaUnidade) objeto;
                    if (StatusPagamento.DEFERIDO.equals(transferenciaMesmaUnidade.getStatusPagamento())) {
                        conciliacaoBancariaFacade.getTransferenciaMesmaUnidadeFacade().baixar(transferenciaMesmaUnidade);
                    }
                    Util.adicionarObjetoEmLista(listaDeObjetos, conciliacaoBancariaFacade.getTransferenciaMesmaUnidadeFacade().recuperar(((TransferenciaMesmaUnidade) objeto).getId()));
            }
            FacesUtil.addOperacaoRealizada("Movimento baixado com sucesso.");
            recuperarRelatorioPesquisaGenerico();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void removerMovimentos() {
        for (Object movimento : listaDeObjetos) {
            if (validarAndRemoverMovimento(movimento)) {
                objetosRemovidos.add(movimento);
            }
        }
    }

    private Boolean validarAndRemoverMovimento(Object objeto) {
        switch (selecionado.getMovimentacaoFinanceira()) {
            case PAGAMENTO:
            case PAGAMENTO_DE_RESTOS_A_PAGAR:
                Pagamento pagamento = (Pagamento) objeto;
                if (!StatusPagamento.PAGO.equals(pagamento.getStatus())) {
                    mensagemDosObjetosRemovidos.add("O pagamento " + pagamento.getNumeroPagamento() + " está com a situação " + pagamento.getStatus().getDescricao() + ".");
                    return true;
                }
                break;
            case DESPESA_EXTRA:
                PagamentoExtra pagamentoExtra = (PagamentoExtra) objeto;
                if (!StatusPagamento.PAGO.equals(pagamentoExtra.getStatus())) {
                    mensagemDosObjetosRemovidos.add("A despesa extraorçamentária " + pagamentoExtra.getNumero() + " está com a situação " + pagamentoExtra.getStatus().getDescricao() + ".");
                    return true;
                }
                break;

            case RECEITA_EXTRAORCAMENTARIA:
                ReceitaExtra receitaExtra = (ReceitaExtra) objeto;
                if (SituacaoReceitaExtra.ABERTO.equals(receitaExtra.getSituacaoReceitaExtra())) {
                    mensagemDosObjetosRemovidos.add("A receita extraorçamentária " + receitaExtra.getNumero() + " está com a situação " + receitaExtra.getSituacaoReceitaExtra().getDescricao() + ".");
                    return true;
                }
                break;

            case RECEITA_REALIZADA:
                LancamentoReceitaOrc lancamentoReceitaOrc = (LancamentoReceitaOrc) objeto;
                if (lancamentoReceitaOrc.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                    mensagemDosObjetosRemovidos.add("A receita realizada " + lancamentoReceitaOrc.getNumero() + " está com saldo maior que zero(0).");
                    return true;
                }
                break;

            case LIBERACAO_DE_COTA_FINANCEIRA:
                LiberacaoCotaFinanceira liberacaoCotaFinanceira = (LiberacaoCotaFinanceira) objeto;
                if (StatusPagamento.DEFERIDO.equals(liberacaoCotaFinanceira.getStatusPagamento())) {
                    mensagemDosObjetosRemovidos.add("A liberação financeira " + liberacaoCotaFinanceira.getNumero() + " está com a situação " + liberacaoCotaFinanceira.getStatusPagamento().getDescricao() + ".");
                    return true;
                }
                break;

            case TRANSFERENCIA_FINANCEIRA:
                TransferenciaContaFinanceira transferenciaContaFinanceira = (TransferenciaContaFinanceira) objeto;
                if (StatusPagamento.DEFERIDO.equals(transferenciaContaFinanceira.getStatusPagamento())) {
                    mensagemDosObjetosRemovidos.add("A transferência financeira " + transferenciaContaFinanceira.getNumero() + " está com a situação " + transferenciaContaFinanceira.getStatusPagamento().getDescricao() + ".");
                    return true;
                }
                break;

            case TRANSPARENCIA_DE_SALDO_NA_MESMA_UNIDADE:
                TransferenciaMesmaUnidade transferenciaMesmaUnidade = (TransferenciaMesmaUnidade) objeto;
                if (StatusPagamento.DEFERIDO.equals(transferenciaMesmaUnidade.getStatusPagamento())) {
                    mensagemDosObjetosRemovidos.add("A transferência financeira na mesma unidade " + transferenciaMesmaUnidade.getNumero() + " está com a situação " + transferenciaMesmaUnidade.getStatusPagamento().getDescricao() + ".");
                    return true;
                }
                break;
        }
        return false;
    }

    public List<String> getMensagemDosObjetosRemovidos() {
        return mensagemDosObjetosRemovidos;
    }

    public void setMensagemDosObjetosRemovidos(List<String> mensagemDosObjetosRemovidos) {
        this.mensagemDosObjetosRemovidos = mensagemDosObjetosRemovidos;
    }

    public List<Object> getObjetosRemovidos() {
        return objetosRemovidos;
    }

    public void setObjetosRemovidos(List<Object> objetosRemovidos) {
        this.objetosRemovidos = objetosRemovidos;
    }
}
