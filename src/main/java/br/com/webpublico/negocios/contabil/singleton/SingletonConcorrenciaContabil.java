package br.com.webpublico.negocios.contabil.singleton;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.BloqueioMovimentoContabilException;
import br.com.webpublico.interfaces.BloqueioMovimentoContabil;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import java.io.Serializable;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 12/01/15
 * Time: 09:18
 * To change this template use File | Settings | File Templates.
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonConcorrenciaContabil implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(SingletonConcorrenciaContabil.class);
    private Set<Empenho> empenhos;
    private Set<Liquidacao> liquidacoes;
    private Set<Pagamento> pagamentos;
    private Set<PagamentoExtra> pagamentoExtras;
    private Set<ReceitaExtra> receitaExtras;
    private Set<TransferenciaContaFinanceira> transferenciasFinanceira;
    private Set<LiberacaoCotaFinanceira> liberacoesFinanceira;
    private Set<SolicitacaoCotaFinanceira> solicitacoesFinanceira;
    private Set<Bordero> borderos;
    private Set<BloqueioMovimentoContabil> bloqueiosMovimentosContabeis;

    @PostConstruct
    private void init() {
        reiniciarEmpenhos();
        reiniciarLiquidacoes();
        reiniciarPagamentos();
        reiniciarReceitasExtras();
        reiniciarPagamentosExtras();
        reiniciarTransferenciasFinanceiras();
        reiniciarLiberacoesFinanceiras();
        reiniciarSolicitacoesFinanceiras();
        reiniciarBorderos();
        reiniciarBloqueioMovimentosContabeis();
    }

    public void reiniciarBloqueioMovimentosContabeis() {
        bloqueiosMovimentosContabeis = Sets.newHashSet();
    }

    public void reiniciarBorderos() {
        borderos = Sets.newHashSet();
    }

    public void reiniciarSolicitacoesFinanceiras() {
        solicitacoesFinanceira = Sets.newHashSet();
    }

    public void reiniciarLiberacoesFinanceiras() {
        liberacoesFinanceira = Sets.newHashSet();
    }

    public void reiniciarTransferenciasFinanceiras() {
        transferenciasFinanceira = Sets.newHashSet();
    }

    public void reiniciarPagamentosExtras() {
        pagamentoExtras = Sets.newHashSet();
    }

    public void reiniciarReceitasExtras() {
        receitaExtras = Sets.newHashSet();
    }

    public void reiniciarPagamentos() {
        pagamentos = Sets.newHashSet();
    }

    public void reiniciarLiquidacoes() {
        liquidacoes = Sets.newHashSet();
    }

    public void reiniciarEmpenhos() {
        empenhos = Sets.newHashSet();
    }

    public void reiniciar() {
        init();
    }

    //EMPENHO

    public void bloquear(Empenho empenho) {
        empenhos.add(empenho);
    }


    public void desbloquear(Empenho empenho) {
        empenhos.remove(empenho);
    }


    public boolean isDisponivel(Empenho empenho) {
        return !empenhos.contains(empenho);
    }

    //LIQUIDACAO

    public void bloquear(Liquidacao liquidacao) {
        liquidacoes.add(liquidacao);
    }


    public void desbloquear(Liquidacao liquidacao) {
        liquidacoes.remove(liquidacao);
    }


    public boolean isDisponivel(Liquidacao liquidacao) {
        return !liquidacoes.contains(liquidacao);
    }

    //PAGAMENTO

    public void bloquear(Pagamento pagamento) {
        pagamentos.add(pagamento);
    }


    public void desbloquear(Pagamento pagamento) {
        pagamentos.remove(pagamento);
    }


    public boolean isDisponivel(Pagamento pagamento) {
        return !pagamentos.contains(pagamento);
    }

    //DESPESA EXTRA

    public void bloquear(PagamentoExtra pagamentoExtra) {
        pagamentoExtras.add(pagamentoExtra);
    }


    public void desbloquear(PagamentoExtra pagamentoExtra) {
        pagamentoExtras.remove(pagamentoExtra);
    }


    public boolean isDisponivel(PagamentoExtra pagamentoExtra) {
        return !pagamentoExtras.contains(pagamentoExtra);
    }

    //RECEITA EXTRA

    public void bloquear(ReceitaExtra receitaExtra) {
        receitaExtras.add(receitaExtra);
    }


    public void desbloquear(ReceitaExtra receitaExtra) {
        receitaExtras.remove(receitaExtra);
    }


    public boolean isDisponivel(ReceitaExtra receitaExtra) {
        return !receitaExtras.contains(receitaExtra);
    }

    //TRANSFERÊNCIA

    public void bloquear(TransferenciaContaFinanceira transferencia) {
        transferenciasFinanceira.add(transferencia);
    }


    public void desbloquear(TransferenciaContaFinanceira transferencia) {
        transferenciasFinanceira.remove(transferencia);
    }


    public boolean isDisponivel(TransferenciaContaFinanceira transferencia) {
        return !transferenciasFinanceira.contains(transferencia);
    }

    //LIBERAÇÃO FINANCEIRA

    public void bloquear(LiberacaoCotaFinanceira liberacao) {
        liberacoesFinanceira.add(liberacao);
    }


    public void desbloquear(LiberacaoCotaFinanceira liberacao) {
        liberacoesFinanceira.remove(liberacao);
    }


    public boolean isDisponivel(LiberacaoCotaFinanceira liberacao) {
        return !liberacoesFinanceira.contains(liberacao);
    }

    //SOLICITAÇÃO FINANCEIRA

    public void bloquear(SolicitacaoCotaFinanceira solicitacao) {
        solicitacoesFinanceira.add(solicitacao);
    }


    public void desbloquear(SolicitacaoCotaFinanceira solicitacao) {
        solicitacoesFinanceira.remove(solicitacao);
    }


    public boolean isDisponivel(SolicitacaoCotaFinanceira solicitacao) {
        return !solicitacoesFinanceira.contains(solicitacao);
    }


    public void bloquear(Bordero bordero) {
        borderos.add(bordero);
    }


    public void desbloquear(Bordero bordero) {
        borderos.remove(bordero);
    }


    public boolean isDisponivel(Bordero bordero) {
        return !borderos.contains(bordero);
    }


    public void bloquearMovimentoContabil(BloqueioMovimentoContabil bloqueioMovimentoContabil) throws BloqueioMovimentoContabilException {
        validarBloqueioMovimentoContabil(bloqueioMovimentoContabil);
        bloqueiosMovimentosContabeis.add(bloqueioMovimentoContabil);
    }


    public void desbloquearMovimentoContabil(BloqueioMovimentoContabil bloqueioMovimentoContabil) {
        bloqueiosMovimentosContabeis.remove(bloqueioMovimentoContabil);
    }


    public void validarBloqueioMovimentoContabil(BloqueioMovimentoContabil bloqueioMovimentoContabil) throws BloqueioMovimentoContabilException {
        if (bloqueiosMovimentosContabeis.contains(bloqueioMovimentoContabil)) {
            throw new BloqueioMovimentoContabilException(bloqueioMovimentoContabil.getMensagemBloqueioMovimentoContabil());
        }
    }

    public void tratarExceptionConcorrenciaSaldos(ExcecaoNegocioGenerica e) {
        if (e != null) {
            if (e.getMessage().contains("concurrent access timeout")) {
                FacesUtil.addOperacaoNaoRealizada("Não foi possível processar a operação, já existe outro usuário gerando saldo financeiro. Por favor tente novamente em alguns segundos.");
            } else if (e.getMessage().contains("org.hibernate.exception.ConstraintViolationException")) {
                FacesUtil.addOperacaoNaoPermitida("Essa operação já foi executada por outro usuário.");
            } else if (e.getMessage().contains("Transaction is required to perform this operation")) {
                FacesUtil.addOperacaoNaoPermitida("Não foi possível realizar esta operação, tente novamente. Se o problema persistir entre em contato com o suporte.");
            } else {
                FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            }
        } else {
            FacesUtil.addOperacaoNaoPermitida("Não foi possível realizar esta operação, tente novamente. Se o problema persistir entre em contato com o suporte.");
        }
    }

    public Set<Empenho> getEmpenhos() {
        return empenhos;
    }

    public Set<Liquidacao> getLiquidacoes() {
        return liquidacoes;
    }

    public Set<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public Set<PagamentoExtra> getPagamentoExtras() {
        return pagamentoExtras;
    }

    public Set<ReceitaExtra> getReceitaExtras() {
        return receitaExtras;
    }

    public Set<TransferenciaContaFinanceira> getTransferenciasFinanceira() {
        return transferenciasFinanceira;
    }

    public Set<LiberacaoCotaFinanceira> getLiberacoesFinanceira() {
        return liberacoesFinanceira;
    }

    public Set<SolicitacaoCotaFinanceira> getSolicitacoesFinanceira() {
        return solicitacoesFinanceira;
    }

    public Set<Bordero> getBorderos() {
        return borderos;
    }
}
