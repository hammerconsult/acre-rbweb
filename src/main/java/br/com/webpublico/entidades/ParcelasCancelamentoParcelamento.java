package br.com.webpublico.entidades;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributario")
@Table(name = "PARCELASCANCELPARCELAMENTO")
public class ParcelasCancelamentoParcelamento extends SuperEntidade implements Serializable, Comparable<ParcelasCancelamentoParcelamento> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CancelamentoParcelamento cancelamentoParcelamento;
    @ManyToOne
    private ParcelaValorDivida parcela;
    private String referencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date pagamento;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimento;
    private String sequencia;
    private int exercicio;
    private String divida;
    private BigDecimal valorImposto;
    private BigDecimal valorTaxa;
    private BigDecimal valorJuros;
    private BigDecimal valorMulta;
    private BigDecimal valorCorrecao;
    private BigDecimal valorHonorarios;
    private BigDecimal valorDesconto;
    private BigDecimal indiceAtualizacao;
    private BigDecimal valorPago;
    @Enumerated(EnumType.STRING)
    private SituacaoParcela situacaoParcela;
    @Enumerated(EnumType.STRING)
    private TipoParcelaCancelamento tipoParcelaCancelamento;

    public ParcelasCancelamentoParcelamento() {
    }

    public ParcelasCancelamentoParcelamento(CancelamentoParcelamento cancelamentoParcelamento, ResultadoParcela resultadoParcela, TipoParcelaCancelamento tipoParcela, BigDecimal indiceAtualizacao) {
        this.setCancelamentoParcelamento(cancelamentoParcelamento);

        ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
        parcelaValorDivida.setId(resultadoParcela.getIdParcela());

        this.setParcela(parcelaValorDivida);
        this.setReferencia(resultadoParcela.getReferencia());
        this.setDivida(resultadoParcela.getDivida());
        this.setSequencia(resultadoParcela.getParcela());
        this.setExercicio(resultadoParcela.getExercicio());
        this.setValorImposto(resultadoParcela.getValorImposto());
        this.setValorTaxa(resultadoParcela.getValorTaxa());
        this.setValorJuros(resultadoParcela.getValorJuros());
        this.setValorMulta(resultadoParcela.getValorMulta());
        this.setValorCorrecao(resultadoParcela.getValorCorrecao());
        this.setValorHonorarios(resultadoParcela.getValorHonorarios());
        this.setValorPago(resultadoParcela.getValorPago());
        this.setValorDesconto(resultadoParcela.getValorDesconto());
        this.setTipoParcelaCancelamento(tipoParcela);
        this.setPagamento(resultadoParcela.getPagamento());
        this.setVencimento(resultadoParcela.getVencimento());
        this.setIndiceAtualizacao(indiceAtualizacao);
        this.setSituacaoParcela(SituacaoParcela.fromDto(resultadoParcela.getSituacaoEnumValue()));
    }

    public ParcelasCancelamentoParcelamento(ParcelasCancelamentoParcelamento parcelasCancelamentoParcelamento) {
        this.setCancelamentoParcelamento(parcelasCancelamentoParcelamento.getCancelamentoParcelamento());
        this.setParcela(parcelasCancelamentoParcelamento.getParcela());
        this.setReferencia(parcelasCancelamentoParcelamento.getReferencia());
        this.setDivida(parcelasCancelamentoParcelamento.getDivida());
        this.setSequencia(parcelasCancelamentoParcelamento.getSequencia());
        this.setExercicio(parcelasCancelamentoParcelamento.getExercicio());
        this.setValorImposto(parcelasCancelamentoParcelamento.getValorImposto());
        this.setValorTaxa(parcelasCancelamentoParcelamento.getValorTaxa());
        this.setValorJuros(parcelasCancelamentoParcelamento.getValorJuros());
        this.setValorMulta(parcelasCancelamentoParcelamento.getValorMulta());
        this.setValorCorrecao(parcelasCancelamentoParcelamento.getValorCorrecao());
        this.setValorHonorarios(parcelasCancelamentoParcelamento.getValorHonorarios());
        this.setValorDesconto(parcelasCancelamentoParcelamento.getValorDesconto());
        this.setValorPago(parcelasCancelamentoParcelamento.getValorPago());
        this.setTipoParcelaCancelamento(parcelasCancelamentoParcelamento.getTipoParcelaCancelamento());
        this.setPagamento(parcelasCancelamentoParcelamento.getPagamento());
        this.setVencimento(parcelasCancelamentoParcelamento.getVencimento());
        this.setIndiceAtualizacao(parcelasCancelamentoParcelamento.getIndiceAtualizacao());
        this.setSituacaoParcela(parcelasCancelamentoParcelamento.getSituacaoParcela());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CancelamentoParcelamento getCancelamentoParcelamento() {
        return cancelamentoParcelamento;
    }

    public void setCancelamentoParcelamento(CancelamentoParcelamento cancelamentoParcelamento) {
        this.cancelamentoParcelamento = cancelamentoParcelamento;
    }

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public BigDecimal getValorImposto() {
        return valorImposto != null ? valorImposto.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public BigDecimal getValorImpostoAtualizado() {
        return indiceAtualizacao != null ? getValorImposto().multiply(indiceAtualizacao) : getValorImposto();
    }

    public void setValorImposto(BigDecimal valorImposto) {
        this.valorImposto = valorImposto;
    }

    public BigDecimal getValorTaxa() {
        return valorTaxa != null ? valorTaxa.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public BigDecimal getValorTaxaAtualizado() {
        return indiceAtualizacao != null ? getValorTaxa().multiply(indiceAtualizacao) : getValorTaxa();
    }

    public void setValorTaxa(BigDecimal valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    public BigDecimal getValorJuros() {
        return valorJuros != null ? valorJuros.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public BigDecimal getValorJurosAtualizado() {
        return indiceAtualizacao != null ? getValorJuros().multiply(indiceAtualizacao) : getValorJuros();
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public BigDecimal getValorMulta() {
        return valorMulta != null ? valorMulta.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public BigDecimal getValorMultaAtualizado() {
        return indiceAtualizacao != null ? getValorMulta().multiply(indiceAtualizacao) : getValorMulta();
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getValorCorrecao() {
        return valorCorrecao != null ? valorCorrecao.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public BigDecimal getValorCorrecaoAtualizado() {
        return indiceAtualizacao != null ? getValorCorrecao().multiply(indiceAtualizacao) : getValorCorrecao();
    }

    public void setValorCorrecao(BigDecimal valorCorrecao) {
        this.valorCorrecao = valorCorrecao;
    }

    public BigDecimal getValorHonorarios() {
        return valorHonorarios != null ? valorHonorarios.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public BigDecimal getValorHonorariosAtualizado() {
        return indiceAtualizacao != null ? getValorHonorarios().multiply(indiceAtualizacao) : getValorHonorarios();
    }

    public BigDecimal getValorDescontoAtualizado() {
        return indiceAtualizacao != null ? getValorDesconto().multiply(indiceAtualizacao) : getValorDesconto();
    }

    public void setValorHonorarios(BigDecimal valorHonorarios) {
        this.valorHonorarios = valorHonorarios;
    }

    public BigDecimal getIndiceAtualizacao() {
        return indiceAtualizacao;
    }

    public void setIndiceAtualizacao(BigDecimal indiceAtualizacao) {
        this.indiceAtualizacao = indiceAtualizacao;
    }

    public BigDecimal getValorPago() {
        return valorPago != null ? valorPago.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public TipoParcelaCancelamento getTipoParcelaCancelamento() {
        return tipoParcelaCancelamento;
    }

    public Date getPagamento() {
        return pagamento;
    }

    public void setPagamento(Date pagamento) {
        this.pagamento = pagamento;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public String getSequencia() {
        return sequencia;
    }

    public void setSequencia(String sequencia) {
        this.sequencia = sequencia;
    }

    public int getExercicio() {
        return exercicio;
    }

    public void setExercicio(int exercicio) {
        this.exercicio = exercicio;
    }

    public String getDivida() {
        return divida;
    }

    public void setDivida(String divida) {
        this.divida = divida;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto != null ? valorDesconto : BigDecimal.ZERO;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public void setTipoParcelaCancelamento(TipoParcelaCancelamento tipoParcelaCancelamento) {
        this.tipoParcelaCancelamento = tipoParcelaCancelamento;
    }

    public BigDecimal getValorTotal() {
        return (getValorImposto().add(getValorTaxa()).add(getValorCorrecao()).add(getValorJuros())
            .add(getValorMulta()).add(getValorHonorarios())).subtract(getValorDesconto()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getValorTotalSemDesconto() {
        return (getValorImposto().add(getValorTaxa()).add(getValorCorrecao()).add(getValorJuros()
            .add(getValorMulta().add(getValorHonorarios())))).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getValorTotalAtualizado() {
        return (getValorImpostoAtualizado().add(getValorTaxaAtualizado()).add(getValorCorrecaoAtualizado()).add(getValorJurosAtualizado()
            .add(getValorMultaAtualizado().add(getValorHonorariosAtualizado())))).subtract(getValorDescontoAtualizado()).setScale(2, RoundingMode.HALF_UP);
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ParcelasCancelamentoParcelamento that = (ParcelasCancelamentoParcelamento) o;

        if (cancelamentoParcelamento != null ? !cancelamentoParcelamento.equals(that.cancelamentoParcelamento) : that.cancelamentoParcelamento != null)
            return false;
        return !(parcela != null ? !parcela.getId().equals(that.parcela.getId()) : that.parcela != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (cancelamentoParcelamento != null ? cancelamentoParcelamento.hashCode() : 0);
        result = 31 * result + (parcela != null ? parcela.getId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ParcelasCancelamentoParcelamento{" +
            "idParcela=" + parcela.getId() +
            ", tipoParcelaCancelamento='" + tipoParcelaCancelamento + "'" +
            ", divida='" + divida + "'" +
            ", exercicio=" + exercicio +
            ", sequencia='" + sequencia + "'" +
            ", vencimento=" + vencimento +
            ", valorImposto=" + valorImposto +
            ", valorTaxa=" + valorTaxa +
            ", valorJuros=" + valorJuros +
            ", valorMulta=" + valorMulta +
            ", valorCorrecao=" + valorCorrecao +
            ", valorHonorarios=" + valorHonorarios +
            ", indiceAtualizacao=" + indiceAtualizacao +
            ", valorPago=" + valorPago +
            '}';
    }

    @Override
    public int compareTo(ParcelasCancelamentoParcelamento o) {
        int i = this.getTipoParcelaCancelamento().compareTo(o.getTipoParcelaCancelamento());
        if (i == 0) {
            i = Integer.valueOf(this.getExercicio()).compareTo(Integer.valueOf(o.getExercicio()));
        }
        return i;
    }

    public enum TipoParcelaCancelamento {
        PARCELAS_ORIGINAIS(false, 1),
        PARCELAS_PAGAS(true, 2),
        PARCELAS_PAGAS_ATUALIZADAS(true, 3),
        PARCELAS_ORIGINAIS_ATUALIZADAS(false, 4),
        PARCELAS_ABATIDAS(false, 5),
        PARCELAS_EM_ABERTO(false, 6);

        private boolean calcularAtualizacao;
        private Integer ordem;

        TipoParcelaCancelamento(boolean calcularAtualizacao, Integer ordem) {
            this.calcularAtualizacao = calcularAtualizacao;
            this.ordem = ordem;
        }

        public boolean isCalcularAtualizacao() {
            return calcularAtualizacao;
        }

        public Integer getOrdem() {
            return ordem;
        }
    }

}
