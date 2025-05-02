package br.com.webpublico.entidades;

import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.interfaces.EntidadeContabil;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "EXECORCDOCUMENTOOFICIAL")
public class ExecucaoOrcDocumentoOficial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DocumentoOficial documentoOficial;
    @ManyToOne
    private Empenho empenho;
    @ManyToOne
    private EmpenhoEstorno empenhoEstorno;
    @ManyToOne
    private Liquidacao liquidacao;
    @ManyToOne
    private LiquidacaoEstorno liquidacaoEstorno;
    @ManyToOne
    private Pagamento pagamento;
    @ManyToOne
    private PagamentoEstorno pagamentoEstorno;
    @ManyToOne
    private ReceitaExtra receitaExtra;
    @ManyToOne
    private PagamentoExtra pagamentoExtra;
    @ManyToOne
    private ReceitaExtraEstorno receitaExtraEstorno;
    @ManyToOne
    private PagamentoExtraEstorno pagamentoExtraEstorno;
    @ManyToOne
    private ObrigacaoAPagarEstorno obrigacaoAPagarEstorno;
    @ManyToOne
    private ObrigacaoAPagar obrigacaoAPagar;

    public ExecucaoOrcDocumentoOficial() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public EmpenhoEstorno getEmpenhoEstorno() {
        return empenhoEstorno;
    }

    public void setEmpenhoEstorno(EmpenhoEstorno empenhoEstorno) {
        this.empenhoEstorno = empenhoEstorno;
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
    }

    public LiquidacaoEstorno getLiquidacaoEstorno() {
        return liquidacaoEstorno;
    }

    public void setLiquidacaoEstorno(LiquidacaoEstorno liquidacaoEstorno) {
        this.liquidacaoEstorno = liquidacaoEstorno;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public PagamentoEstorno getPagamentoEstorno() {
        return pagamentoEstorno;
    }

    public void setPagamentoEstorno(PagamentoEstorno pagamentoEstorno) {
        this.pagamentoEstorno = pagamentoEstorno;
    }

    public ReceitaExtra getReceitaExtra() {
        return receitaExtra;
    }

    public void setReceitaExtra(ReceitaExtra receitaExtra) {
        this.receitaExtra = receitaExtra;
    }

    public PagamentoExtra getPagamentoExtra() {
        return pagamentoExtra;
    }

    public void setPagamentoExtra(PagamentoExtra pagamentoExtra) {
        this.pagamentoExtra = pagamentoExtra;
    }

    public ReceitaExtraEstorno getReceitaExtraEstorno() {
        return receitaExtraEstorno;
    }

    public void setReceitaExtraEstorno(ReceitaExtraEstorno receitaExtraEstorno) {
        this.receitaExtraEstorno = receitaExtraEstorno;
    }

    public PagamentoExtraEstorno getPagamentoExtraEstorno() {
        return pagamentoExtraEstorno;
    }

    public void setPagamentoExtraEstorno(PagamentoExtraEstorno pagamentoExtraEstorno) {
        this.pagamentoExtraEstorno = pagamentoExtraEstorno;
    }

    public ObrigacaoAPagarEstorno getObrigacaoAPagarEstorno() {
        return obrigacaoAPagarEstorno;
    }

    public void setObrigacaoAPagarEstorno(ObrigacaoAPagarEstorno obrigacaoAPagarEstorno) {
        this.obrigacaoAPagarEstorno = obrigacaoAPagarEstorno;
    }

    public ObrigacaoAPagar getObrigacaoAPagar() {
        return obrigacaoAPagar;
    }

    public void setObrigacaoAPagar(ObrigacaoAPagar obrigacaoAPagar) {
        this.obrigacaoAPagar = obrigacaoAPagar;
    }

    public ExecucaoOrcDocumentoOficial build(EntidadeContabil entidadeContabil, ModuloTipoDoctoOficial modulo, DocumentoOficial documentoOficial) {
        this.setDocumentoOficial(documentoOficial);
        switch (modulo) {
            case NOTA_EMPENHO:
                this.setEmpenho((Empenho) entidadeContabil);
                break;

            case NOTA_RESTO_EMPENHO:
                this.setEmpenho((Empenho) entidadeContabil);
                break;

            case NOTA_ESTORNO_EMPENHO:
                this.setEmpenhoEstorno((EmpenhoEstorno) entidadeContabil);
                break;

            case NOTA_RESTO_ESTORNO_EMPENHO:
                this.setEmpenhoEstorno((EmpenhoEstorno) entidadeContabil);
                break;

            case NOTA_LIQUIDACAO:
                this.setLiquidacao((Liquidacao) entidadeContabil);
                break;

            case NOTA_RESTO_LIQUIDACAO:
                this.setLiquidacao((Liquidacao) entidadeContabil);
                break;

            case NOTA_ESTORNO_LIQUIDACAO:
                this.setLiquidacaoEstorno((LiquidacaoEstorno) entidadeContabil);
                break;

            case NOTA_RESTO_ESTORNO_LIQUIDACAO:
                this.setLiquidacaoEstorno((LiquidacaoEstorno) entidadeContabil);
                break;

            case NOTA_PAGAMENTO:
                this.setPagamento((Pagamento) entidadeContabil);
                break;

            case NOTA_RESTO_PAGAMENTO:
                this.setPagamento((Pagamento) entidadeContabil);
                break;

            case NOTA_ESTORNO_PAGAMENTO:
                this.setPagamentoEstorno((PagamentoEstorno) entidadeContabil);
                break;

            case NOTA_RESTO_ESTORNO_PAGAMENTO:
                this.setPagamentoEstorno((PagamentoEstorno) entidadeContabil);
                break;

            case NOTA_RECEITA_EXTRA:
                this.setReceitaExtra((ReceitaExtra) entidadeContabil);
                break;

            case NOTA_RECEITA_EXTRA_PAGAMENTO:
                this.setReceitaExtra((ReceitaExtra) entidadeContabil);
                break;

            case NOTA_PAGAMENTO_EXTRA:
                this.setPagamentoExtra((PagamentoExtra) entidadeContabil);
                break;

            case NOTA_RECEITA_EXTRA_ESTORNO:
                this.setReceitaExtraEstorno((ReceitaExtraEstorno) entidadeContabil);
                break;

            case NOTA_PAGAMENTO_EXTRA_ESTORNO:
                this.setPagamentoExtraEstorno((PagamentoExtraEstorno) entidadeContabil);
                break;

            case NOTA_OBRIGACAO_A_PAGAR_ESTORNO:
                this.setObrigacaoAPagarEstorno((ObrigacaoAPagarEstorno) entidadeContabil);
                break;

            case NOTA_OBRIGACAO_A_PAGAR:
                this.setObrigacaoAPagar((ObrigacaoAPagar) entidadeContabil);
                break;
        }
        return this;
    }

    public static String getSql(ModuloTipoDoctoOficial modulo) {
        switch (modulo) {
            case NOTA_EMPENHO:
                return " where exdoc.empenho_id = :obj ";

            case NOTA_RESTO_EMPENHO:
                return " where exdoc.empenho_id = :obj ";

            case NOTA_ESTORNO_EMPENHO:
                return " where exdoc.empenhoestorno_id = :obj ";

            case NOTA_RESTO_ESTORNO_EMPENHO:
                return " where exdoc.empenhoestorno_id = :obj ";

            case NOTA_LIQUIDACAO:
                return " where exdoc.liquidacao_id = :obj ";

            case NOTA_RESTO_LIQUIDACAO:
                return " where exdoc.liquidacao_id = :obj ";

            case NOTA_ESTORNO_LIQUIDACAO:
                return " where exdoc.liquidacaoEstorno_id = :obj ";

            case NOTA_RESTO_ESTORNO_LIQUIDACAO:
                return " where exdoc.liquidacaoEstorno_id = :obj ";

            case NOTA_PAGAMENTO:
                return " where exdoc.pagamento_id = :obj ";

            case NOTA_RESTO_PAGAMENTO:
                return " where exdoc.pagamento_id = :obj ";

            case NOTA_ESTORNO_PAGAMENTO:
                return " where exdoc.pagamentoestorno_id = :obj ";

            case NOTA_RESTO_ESTORNO_PAGAMENTO:
                return " where exdoc.pagamentoestorno_id = :obj ";

            case NOTA_RECEITA_EXTRA:
                return " where exdoc.receitaextra_id = :obj ";

            case NOTA_RECEITA_EXTRA_PAGAMENTO:
                return " where exdoc.receitaextra_id = :obj ";

            case NOTA_PAGAMENTO_EXTRA:
                return " where exdoc.pagamentoextra_id = :obj ";

            case NOTA_RECEITA_EXTRA_ESTORNO:
                return " where exdoc.receitaextra_id = :obj ";

            case NOTA_PAGAMENTO_EXTRA_ESTORNO:
                return " where exdoc.pagamentoextraestorno_id = :obj ";

            case NOTA_OBRIGACAO_A_PAGAR_ESTORNO:
                return " where exdoc.obrigacaoapagarestorno_id = :obj ";

            case NOTA_OBRIGACAO_A_PAGAR:
                return " where exdoc.obrigacaoapagar_id = :obj ";
        }
        return "";
    }

}
