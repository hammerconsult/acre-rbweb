package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
public class MovimentoValorParcelamento extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoParcelamento parcelamento;
    @ManyToOne
    private ParcelaValorDivida parcela;
    private BigDecimal valor;
    private Date dia;
    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    public MovimentoValorParcelamento() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoParcelamento getParcelamento() {
        return parcelamento;
    }

    public void setParcelamento(ProcessoParcelamento parcelamento) {
        this.parcelamento = parcelamento;
    }

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getDia() {
        return dia;
    }

    public void setDia(Date dia) {
        this.dia = dia;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public static enum Tipo {
        PAGAMENTO, DESCONTO;
    }
}
