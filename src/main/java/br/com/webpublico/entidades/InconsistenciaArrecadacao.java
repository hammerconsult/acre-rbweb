package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
public class InconsistenciaArrecadacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private LoteBaixa loteBaixa;
    private String descricao;
    @ManyToOne
    private ItemLoteBaixa itemLoteBaixa;
    @Enumerated(EnumType.STRING)
    private SituacaoParcela situacaoParcela;
    @Enumerated(EnumType.STRING)
    private Inconsistencia inconsistencia;
    @Transient
    private Long criadoEm;

    public InconsistenciaArrecadacao() {
        criadoEm = System.nanoTime();
    }

    public InconsistenciaArrecadacao(LoteBaixa loteBaixa, ItemLoteBaixa itemLoteBaixa, SituacaoParcela situacaoParcela, Inconsistencia inconsistencia) {
        this.loteBaixa = loteBaixa;
        this.itemLoteBaixa = itemLoteBaixa;
        this.situacaoParcela = situacaoParcela;
        this.inconsistencia = inconsistencia;
        criadoEm = System.nanoTime();
    }

    public InconsistenciaArrecadacao(LoteBaixa loteBaixa, String descricao, Inconsistencia inconsistencia) {
        this.loteBaixa = loteBaixa;
        this.descricao = descricao;
        this.inconsistencia = inconsistencia;
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public void setLoteBaixa(LoteBaixa loteBaixa) {
        this.loteBaixa = loteBaixa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ItemLoteBaixa getItemLoteBaixa() {
        return itemLoteBaixa;
    }

    public void setItemLoteBaixa(ItemLoteBaixa itemLoteBaixa) {
        this.itemLoteBaixa = itemLoteBaixa;
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public Inconsistencia getInconsistencia() {
        return inconsistencia;
    }

    public void setInconsistencia(Inconsistencia inconsistencia) {
        this.inconsistencia = inconsistencia;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.InconsistenciaArrecadacao[ id=" + id + " ]";
    }

    public enum Inconsistencia {

        ARQUIVO_INCONSISTENTE("Arquivo Inconsistente, não pode ser lido"),
        DAM_NAO_ENCONTRATO("DAM não encontrado"),
        DAM_NAO_ENCONTRATO_DE_NFSE("DAM de Nota Fiscal Eletrônica não encontrado"),
        DEBITO_PAGO_EM_DUPLICIDADE_NO_MESMO_ARQUIVO("Débito pago em duplicidade no mesmo aquivo"),
        NAO_EXISTE_DATA_DE_VENCIMENTO_PARA_O_DAM("Não existe data de Vencimento para o DAM"),
        DIFERENCA_NO_TOTAL_DO_LOTE("Diferença no total do lote"),
        DIFERENCA_ENTRE_VALOR_DEVIDO_E_VALOR_PAGO("Diferença entre valor devido e valor pago"),
        DEBITO_COM_SITUAÇAO_INCONSISTENTE("Débito com situação inconsistente"),
        DIFERENCA_QUANTIDADE_DAMS("Diferença entre a quatidade de DAMs informada e a quantidade presente no lote");
        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        private Inconsistencia(String descricao) {
            this.descricao = descricao;
        }
    }

    public String getDescricaoCompletaParaTable() {
        StringBuilder string = new StringBuilder("");
        final String ICOL = "<td>";
        final String ICOLVALOR = "<td style=\"text-align: right\">";
        final String FCOL = "</td>";
        if (itemLoteBaixa == null) {
            string.append(ICOL).append(inconsistencia.getDescricao()).append(FCOL);
        } else if (itemLoteBaixa.getDam() == null) {
            string.append(ICOL).append(itemLoteBaixa.getCodigoBarrasInformado()).append(FCOL);
            string.append(ICOL).append(itemLoteBaixa.getDamInformado()).append(FCOL);
            string.append(ICOL).append(Util.dateToString(loteBaixa.getDataPagamento())).append(FCOL);
            string.append(ICOLVALOR).append(Util.formataNumero(itemLoteBaixa.getDam() == null ? BigDecimal.ZERO : itemLoteBaixa.getDam().getValorOriginal())).append(FCOL);
            string.append(ICOLVALOR).append(Util.formataNumero(itemLoteBaixa.getDam() == null ? BigDecimal.ZERO : itemLoteBaixa.getDam().getJuros())).append(FCOL);
            string.append(ICOLVALOR).append(Util.formataNumero(itemLoteBaixa.getDam() == null ? BigDecimal.ZERO : itemLoteBaixa.getDam().getMulta())).append(FCOL);
            string.append(ICOLVALOR).append(Util.formataNumero(itemLoteBaixa.getDam() == null ? BigDecimal.ZERO : itemLoteBaixa.getDam().getCorrecaoMonetaria())).append(FCOL);
            string.append(ICOLVALOR).append(Util.formataNumero(itemLoteBaixa.getTotal())).append(FCOL);
            string.append(ICOLVALOR).append(Util.formataNumero(itemLoteBaixa.getValorPago())).append(FCOL);
            string.append(ICOLVALOR).append(Util.formataNumero(itemLoteBaixa.getValorPago().subtract(itemLoteBaixa.getTotal()))).append(FCOL);
            string.append(ICOL).append(inconsistencia.getDescricao());
        } else {
            string.append(ICOL).append(itemLoteBaixa.getCodigoBarrasInformado()).append(FCOL);
            string.append(ICOL).append(itemLoteBaixa.getDamInformado()).append(FCOL);
            string.append(ICOL).append(Util.dateToString(loteBaixa.getDataPagamento())).append(FCOL);
            string.append(ICOLVALOR).append(Util.formataNumero(itemLoteBaixa.getDam() == null ? BigDecimal.ZERO : itemLoteBaixa.getDam().getValorOriginal())).append(FCOL);
            string.append(ICOLVALOR).append(Util.formataNumero(itemLoteBaixa.getDam() == null ? BigDecimal.ZERO : itemLoteBaixa.getDam().getJuros())).append(FCOL);
            string.append(ICOLVALOR).append(Util.formataNumero(itemLoteBaixa.getDam() == null ? BigDecimal.ZERO : itemLoteBaixa.getDam().getMulta())).append(FCOL);
            string.append(ICOLVALOR).append(Util.formataNumero(itemLoteBaixa.getDam() == null ? BigDecimal.ZERO : itemLoteBaixa.getDam().getCorrecaoMonetaria())).append(FCOL);
            string.append(ICOLVALOR).append(Util.formataNumero(itemLoteBaixa.getTotal())).append(FCOL);
            string.append(ICOLVALOR).append(Util.formataNumero(itemLoteBaixa.getValorPago())).append(FCOL);
            string.append(ICOLVALOR).append(Util.formataNumero(itemLoteBaixa.getValorPago().subtract(itemLoteBaixa.getTotal()))).append(FCOL);
            string.append(ICOL).append(inconsistencia.getDescricao());
            if (inconsistencia.equals(Inconsistencia.DEBITO_COM_SITUAÇAO_INCONSISTENTE)) {
                string.append(": ").append(situacaoParcela.getDescricao());
            }
            string.append(FCOL);
        }
        return string.toString();
    }

    public static String getHeaderTable() {
        StringBuilder string = new StringBuilder("");
        final String ICOL = "<td style=\"font-weight:bold;\">";
        final String ICOLVALOR = "<td style=\"font-weight:bold; text-align: right\">";
        final String FCOL = "</td>";
        string.append(ICOL).append("Código de Barras").append(FCOL);
        string.append(ICOL).append("Número do DAM").append(FCOL);
        string.append(ICOL).append("Pagamento").append(FCOL);
        string.append(ICOLVALOR).append("Original").append(FCOL);
        string.append(ICOLVALOR).append("Juros").append(FCOL);
        string.append(ICOLVALOR).append("Multa").append(FCOL);
        string.append(ICOLVALOR).append("Correção").append(FCOL);
        string.append(ICOLVALOR).append("Devido").append(FCOL);
        string.append(ICOLVALOR).append("Pago").append(FCOL);
        string.append(ICOLVALOR).append("Diferença");
        string.append(ICOL).append("Inconsistência").append(FCOL);
        return string.toString();

    }
}
