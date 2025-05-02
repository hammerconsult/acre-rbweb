package br.com.webpublico.negocios.jdbc;

import java.io.Serializable;

/**
 * Created by fernando on 29/01/15.
 */
public abstract class SkeletonJDBC extends ClassPatternJDBC implements Conectavel, Serializable {

    private BancarioJDBC bancarioJDBC;
    private OrcamentoJDBC orcamentoJDBC;
    private CommonsJDBC commonsJDBC;
    private OrigemContaContabilJDBC origemContaContabilJDBC;
    private ParametroEventoJDBC parametroEventoJDBC;
    private MovimentoSaldoDespesaOrcJDBC movimentoSaldoDespesaOrcJDBC;
    private UpdatePagamentoLiquidacaoJDBC updatePagamentoLiquidacaoJDBC;
    private SaldoDividaPublicaJDBC saldoDividaPublicaJDBC;
    private SaldoContaFinanceiraJDBC saldoContaFinanceiraJDBC;
    private ConsignacaoJDBC consignacaoJDBC;
    private InscricaoDiariaJDBC inscricaoDiariaJDBC;
    private ContabilizarPagamentoJDBC contabilizarPagamentoJDBC;
    private IdGenerator idGenerator;

    protected SkeletonJDBC() {
        createInstances();
    }

    private void createInstances() {
        if (this.conexao != null) {
            System.out.println("Criando instâncias!");
            bancarioJDBC = BancarioJDBC.createInstance(getConnection());
            orcamentoJDBC = OrcamentoJDBC.createInstance(getConnection());
            commonsJDBC = CommonsJDBC.createInstance(getConnection());
            origemContaContabilJDBC = OrigemContaContabilJDBC.createInstance(getConnection());
            parametroEventoJDBC = ParametroEventoJDBC.createInstance(getConnection());
            movimentoSaldoDespesaOrcJDBC = MovimentoSaldoDespesaOrcJDBC.createInstance(getConnection());
            updatePagamentoLiquidacaoJDBC = UpdatePagamentoLiquidacaoJDBC.createInstance(getConnection());
            saldoDividaPublicaJDBC = SaldoDividaPublicaJDBC.createInstance(getConnection());
            saldoContaFinanceiraJDBC = SaldoContaFinanceiraJDBC.createInstance(getConnection());
            consignacaoJDBC = ConsignacaoJDBC.createInstance(getConnection());
            inscricaoDiariaJDBC = InscricaoDiariaJDBC.createInstance(getConnection());
            contabilizarPagamentoJDBC = ContabilizarPagamentoJDBC.createInstance(getConnection());
            idGenerator = IdGenerator.createInstance(getConnection());
        } else {
            System.out.println("Conexão ainda nula em SkeletonJDBC!");
        }
    }

    public BancarioJDBC getBancarioJDBC() {
        return bancarioJDBC;
    }

    public OrcamentoJDBC getOrcamentoJDBC() {
        return orcamentoJDBC;
    }

    public CommonsJDBC getCommonsJDBC() {
        return commonsJDBC;
    }

    public OrigemContaContabilJDBC getOrigemContaContabilJDBC() {
        return origemContaContabilJDBC;
    }

    public ParametroEventoJDBC getParametroEventoJDBC() {
        return parametroEventoJDBC;
    }

    public MovimentoSaldoDespesaOrcJDBC getMovimentoSaldoDespesaOrcJDBC() {
        if (movimentoSaldoDespesaOrcJDBC == null) {
            movimentoSaldoDespesaOrcJDBC = MovimentoSaldoDespesaOrcJDBC.createInstance(getConnection());
        }
        return movimentoSaldoDespesaOrcJDBC;
    }

    public UpdatePagamentoLiquidacaoJDBC getUpdatePagamentoLiquidacaoJDBC() {
        if (updatePagamentoLiquidacaoJDBC == null) {
            updatePagamentoLiquidacaoJDBC = UpdatePagamentoLiquidacaoJDBC.createInstance(getConnection());
        }
        return updatePagamentoLiquidacaoJDBC;
    }

    public SaldoDividaPublicaJDBC getSaldoDividaPublicaJDBC() {
        if (saldoDividaPublicaJDBC == null) {
            saldoDividaPublicaJDBC = SaldoDividaPublicaJDBC.createInstance(getConnection());
        }
        return saldoDividaPublicaJDBC;
    }

    public SaldoContaFinanceiraJDBC getSaldoContaFinanceiraJDBC() {
        if (saldoContaFinanceiraJDBC == null) {
            saldoContaFinanceiraJDBC = SaldoContaFinanceiraJDBC.createInstance(getConnection());
        }
        return saldoContaFinanceiraJDBC;
    }

    public ConsignacaoJDBC getConsignacaoJDBC() {
        if (consignacaoJDBC == null) {
            consignacaoJDBC = ConsignacaoJDBC.createInstance(getConnection());
        }
        return consignacaoJDBC;
    }

    public InscricaoDiariaJDBC getInscricaoDiariaJDBC() {
        if (inscricaoDiariaJDBC == null) {
            inscricaoDiariaJDBC = InscricaoDiariaJDBC.createInstance(getConnection());
        }
        return inscricaoDiariaJDBC;
    }

    public ContabilizarPagamentoJDBC getContabilizarPagamentoJDBC() {
        if (contabilizarPagamentoJDBC == null) {
            contabilizarPagamentoJDBC = ContabilizarPagamentoJDBC.createInstance(getConnection());
        }
        return contabilizarPagamentoJDBC;
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }
}
