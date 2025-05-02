package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@GrupoDiagrama(nome = "Arrecadação")
@Entity

@Audited
@Etiqueta("Parcelas do Lançamento de Desconto")
public class LancamentoDescontoParcela extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private LancamentoDesconto lancamentoDesconto;
    @ManyToOne
    private ParcelaValorDivida parcela;
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcao;
    private BigDecimal honorarios;
    private BigDecimal desconto;
    private BigDecimal honorariosAtualizados;

    @OneToMany(mappedBy = "lancamentoDescontoParcela", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LancamentoDescontoParcelaTributo> descontos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LancamentoDesconto getLancamentoDesconto() {
        return lancamentoDesconto;
    }

    public void setLancamentoDesconto(LancamentoDesconto lancamentoDesconto) {
        this.lancamentoDesconto = lancamentoDesconto;
    }

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    public BigDecimal getImposto() {
        return imposto != null ? imposto : BigDecimal.ZERO;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa != null ? taxa : BigDecimal.ZERO;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getJuros() {
        return juros != null ? juros : BigDecimal.ZERO;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa != null ? multa : BigDecimal.ZERO;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecao() {
        return correcao != null ? correcao : BigDecimal.ZERO;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public BigDecimal getHonorarios() {
        return honorarios != null ? honorarios : BigDecimal.ZERO;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public BigDecimal getDesconto() {
        return desconto != null ? desconto : BigDecimal.ZERO;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public List<LancamentoDescontoParcelaTributo> getDescontos() {
        return descontos;
    }

    public void setDescontos(List<LancamentoDescontoParcelaTributo> descontos) {
        this.descontos = descontos;
    }

    public BigDecimal getHonorariosAtualizados() {
        return honorariosAtualizados != null ? honorariosAtualizados : BigDecimal.ZERO;
    }

    public void setHonorariosAtualizados(BigDecimal honorariosAtualizados) {
        this.honorariosAtualizados = honorariosAtualizados;
    }

    public BigDecimal getTotal() {
        return getImposto().add(getTaxa()).add(getJuros()).add(getMulta()).add(getCorrecao()).add(getHonorarios()).subtract(getDesconto());
    }

    public BigDecimal getTotalComDescontos() {
        return getImposto().add(getTaxa()).add(getJuros()).add(getMulta()).add(getCorrecao()).add(getHonorariosAtualizados()).subtract(getDesconto()).subtract(getTotalDesconto());
    }

    public BigDecimal getDescontoPorTipo(Tributo.TipoTributo tipoTributo) {
        BigDecimal valorDesconto = BigDecimal.ZERO;
        for (LancamentoDescontoParcelaTributo lancamentoDescontoParcelaTributo : getDescontos()) {
            if (tipoTributo.equals(lancamentoDescontoParcelaTributo.getTipoTributo())) {
                valorDesconto = valorDesconto.add(lancamentoDescontoParcelaTributo.getDesconto());
            }
        }
        return valorDesconto;
    }

    public BigDecimal getTotalDesconto() {
        return getDescontoPorTipo(Tributo.TipoTributo.IMPOSTO)
            .add(getDescontoPorTipo(Tributo.TipoTributo.TAXA)
                .add(getDescontoPorTipo(Tributo.TipoTributo.JUROS)
                    .add(getDescontoPorTipo(Tributo.TipoTributo.MULTA)
                        .add(getDescontoPorTipo(Tributo.TipoTributo.CORRECAO)
                            .add(getDescontoPorTipo(Tributo.TipoTributo.HONORARIOS))))));
    }

    public BigDecimal getTotalDescontoComOriginal() {
        return getDesconto().add(getDescontoPorTipo(Tributo.TipoTributo.IMPOSTO)
            .add(getDescontoPorTipo(Tributo.TipoTributo.TAXA)
                .add(getDescontoPorTipo(Tributo.TipoTributo.JUROS)
                    .add(getDescontoPorTipo(Tributo.TipoTributo.MULTA)
                        .add(getDescontoPorTipo(Tributo.TipoTributo.CORRECAO)
                            .add(getDescontoPorTipo(Tributo.TipoTributo.HONORARIOS)))))));
    }

}
