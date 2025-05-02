package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
@Etiqueta("Pagamento Por Movimentação Tributaria")
@Table(name = "PAGAMENTOPORMOVTRIBUTARIA")
public class PagamentoPorMovimentacaoTributaria extends SuperEntidade {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal desconto;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcao;
    private BigDecimal honorarios;
    @ManyToOne
    private ParcelaValorDivida parcela;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getImposto() {
        if (imposto == null) {
            imposto = BigDecimal.ZERO;
        }
        return imposto;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        if (taxa == null) {
            taxa = BigDecimal.ZERO;
        }
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getDesconto() {
        if (desconto == null) {
            desconto = BigDecimal.ZERO;
        }
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getJuros() {
        if (juros == null) {
            juros = BigDecimal.ZERO;
        }
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        if (multa == null) {
            multa = BigDecimal.ZERO;
        }
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecao() {
        if (correcao == null) {
            correcao = BigDecimal.ZERO;
        }
        return correcao;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public BigDecimal getHonorarios() {
        if (honorarios == null) {
            honorarios = BigDecimal.ZERO;
        }
        return honorarios;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public BigDecimal getTotal() {
        return getImposto().add(getTaxa()).add(getJuros().add(getMulta().add(getCorrecao().add(getHonorarios()))));
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }
}
