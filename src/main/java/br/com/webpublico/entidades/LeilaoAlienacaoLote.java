package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.VOItemSolicitacaoAlienacao;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 12/11/14
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class LeilaoAlienacaoLote extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private LeilaoAlienacao leilaoAlienacao;

    @ManyToOne
    private LoteAvaliacaoAlienacao loteAvaliacaoAlienacao;

    private Boolean arrematado;

    private BigDecimal valorArrematado;

    @OneToMany(mappedBy = "leilaoAlienacaoLote")
    private List<LeilaoAlienacaoLoteBem> bensDoLoteDaAlienacao;

    @Transient
    private List<VOItemSolicitacaoAlienacao> itensAvaliados;

    @OneToOne(cascade = CascadeType.MERGE)
    @Obrigatorio
    @Etiqueta("Documento(s) Fiscal(is)")
    private DetentorDoctoFiscalLiquidacao detentDoctoFiscalLiquidacao;

    public LeilaoAlienacaoLote() {
        this.valorArrematado = BigDecimal.ZERO;
        this.bensDoLoteDaAlienacao = new ArrayList();
        this.itensAvaliados = new ArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LeilaoAlienacao getLeilaoAlienacao() {
        return leilaoAlienacao;
    }

    public void setLeilaoAlienacao(LeilaoAlienacao leilaoAlienacao) {
        this.leilaoAlienacao = leilaoAlienacao;
    }

    public LoteAvaliacaoAlienacao getLoteAvaliacaoAlienacao() {
        return loteAvaliacaoAlienacao;
    }

    public void setLoteAvaliacaoAlienacao(LoteAvaliacaoAlienacao loteAvaliacaoAlienacao) {
        this.loteAvaliacaoAlienacao = loteAvaliacaoAlienacao;
    }

    public Boolean getArrematado() {
        return arrematado;
    }

    public void setArrematado(Boolean arrematado) {
        this.arrematado = arrematado;
    }

    public BigDecimal getValorArrematado() {
        return valorArrematado;
    }

    public void setValorArrematado(BigDecimal valorArrematado) {
        this.valorArrematado = valorArrematado;
    }

    public List<LeilaoAlienacaoLoteBem> getBensDoLoteDaAlienacao() {
        return bensDoLoteDaAlienacao;
    }

    public void setBensDoLoteDaAlienacao(List<LeilaoAlienacaoLoteBem> bensDoLoteDaAlienacao) {
        this.bensDoLoteDaAlienacao = bensDoLoteDaAlienacao;
    }

    public List<VOItemSolicitacaoAlienacao> getItensAvaliados() {
        return itensAvaliados;
    }

    public void setItensAvaliados(List<VOItemSolicitacaoAlienacao> itensAvaliados) {
        this.itensAvaliados = itensAvaliados;
    }

    public DetentorDoctoFiscalLiquidacao getDetentDoctoFiscalLiquidacao() {
        return detentDoctoFiscalLiquidacao;
    }

    public void setDetentDoctoFiscalLiquidacao(DetentorDoctoFiscalLiquidacao detentDoctoFiscalLiquidacao) {
        this.detentDoctoFiscalLiquidacao = detentDoctoFiscalLiquidacao;
    }

    public BigDecimal getValorTotalProporciaonalArrematado() {
        BigDecimal total = BigDecimal.ZERO;
        for (LeilaoAlienacaoLoteBem leilaoAlienacaoLoteBem : bensDoLoteDaAlienacao) {
            total = total.add(leilaoAlienacaoLoteBem.getValorProporcionalArrematado());
        }
        return total;
    }

    public String getValorTotalProporciaonalArrematadoFormatado() {
        return Util.formataValor(getValorTotalProporciaonalArrematado());
    }

    @Override
    public String toString() {
        try {
            return loteAvaliacaoAlienacao.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public String getHistoricoRazaoAlienacao(String numeroEfetivacaoBaixa, String numeroLoteArrematado) {
        return "Alienação dos bens arrematados na efetivação de alienação n° " + numeroLoteArrematado + "."
            + " Alienação dos bens arrematados na Efetivação da Baixa de Bens Móveis nº " + numeroEfetivacaoBaixa + ".";
    }
}
