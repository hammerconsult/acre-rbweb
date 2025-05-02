package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoParcela;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by andregustavo on 18/05/2015.
 */
@Entity
@Audited
public class RestituicaoParcela extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoRestituicao processoRestituicao;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    @ManyToOne
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    private SituacaoParcela situacaoParcela;
    @Temporal(TemporalType.DATE)
    private Date pagamentoParcela;
    private String referencia;
    private String tipoDebito;
    private String tipoCadastro;
    private String divida;
    private BigDecimal valorPago;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoRestituicao getProcessoRestituicao() {
        return processoRestituicao;
    }

    public void setProcessoRestituicao(ProcessoRestituicao processoRestituicao) {
        this.processoRestituicao = processoRestituicao;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getTipoDebito() {
        return tipoDebito;
    }

    public void setTipoDebito(String tipoDebito) {
        this.tipoDebito = tipoDebito;
    }

    public String getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(String tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public Date getPagamentoParcela() {
        return pagamentoParcela;
    }

    public void setPagamentoParcela(Date pagamentoParcela) {
        this.pagamentoParcela = pagamentoParcela;
    }

    public String getDivida() {
        return divida;
    }

    public void setDivida(String divida) {
        this.divida = divida;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }
}
