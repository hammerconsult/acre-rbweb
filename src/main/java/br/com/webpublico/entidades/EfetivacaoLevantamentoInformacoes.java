package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Desenvolvimento on 26/10/2016.
 */

@Entity
@Audited
@Table(name = "EFETLEVANTINFO")
public class EfetivacaoLevantamentoInformacoes extends SuperEntidade implements Comparable<EfetivacaoLevantamentoInformacoes> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private LoteEfetivacaoLevantamentoBem loteEfetivacao;

    @ManyToOne
    private GrupoBem grupoBem;

    private BigDecimal valorLevantamentos;
    private BigDecimal valorBens;
    private BigDecimal valorContabil;
    private BigDecimal valorDepreciacaoContabil;
    private BigDecimal valorDepreciacaoBens;
    private BigDecimal valorDepreciacaoLevant;
    private Boolean consulta = Boolean.FALSE;

    public EfetivacaoLevantamentoInformacoes() {
        super();
        this.valorBens = BigDecimal.ZERO;
        this.valorContabil = BigDecimal.ZERO;
        this.valorLevantamentos = BigDecimal.ZERO;
        this.valorDepreciacaoContabil = BigDecimal.ZERO;
        this.valorDepreciacaoBens = BigDecimal.ZERO;
        this.valorDepreciacaoLevant = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteEfetivacaoLevantamentoBem getLoteEfetivacao() {
        return loteEfetivacao;
    }

    public void setLoteEfetivacao(LoteEfetivacaoLevantamentoBem loteEfetivacao) {
        this.loteEfetivacao = loteEfetivacao;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public BigDecimal getValorLevantamentos() {
        return valorLevantamentos;
    }

    public void setValorLevantamentos(BigDecimal valorLevantamentos) {
        this.valorLevantamentos = valorLevantamentos;
    }

    public BigDecimal getValorBens() {
        return valorBens;
    }

    public void setValorBens(BigDecimal valorBens) {
        this.valorBens = valorBens;
    }

    public BigDecimal getValorContabil() {
        return valorContabil;
    }

    public void setValorContabil(BigDecimal valorContabil) {
        this.valorContabil = valorContabil;
    }

    public BigDecimal getValorDepreciacaoContabil() {
        return valorDepreciacaoContabil;
    }

    public void setValorDepreciacaoContabil(BigDecimal valorDepreciacaoContabil) {
        this.valorDepreciacaoContabil = valorDepreciacaoContabil;
    }

    public BigDecimal getValorDepreciacaoBens() {
        return valorDepreciacaoBens;
    }

    public void setValorDepreciacaoBens(BigDecimal valorDepreciacaoBens) {
        this.valorDepreciacaoBens = valorDepreciacaoBens;
    }

    public BigDecimal getValorDepreciacaoLevant() {
        return valorDepreciacaoLevant;
    }

    public void setValorDepreciacaoLevant(BigDecimal valorDepreciacaoLevant) {
        this.valorDepreciacaoLevant = valorDepreciacaoLevant;
    }

    public BigDecimal getBensMaisLevantamentos() {
        return valorLevantamentos.add(valorBens);
    }

    public BigDecimal getDepreciacaoBensMaisDepreciacaoLevantamentos() {
        return valorDepreciacaoBens.add(valorDepreciacaoLevant);
    }

    public String styleClassInconsistente() {
        if (isGrupoInconsistente()) {
            return "vermelhonegrito";
        }
        if (consulta) {
            return "azulnegrito";
        }
        return "verdenegrito";
    }

    public String styleClassInconsistenteBemPrincipal() {
        if (isGrupoInconsistentePrincipal()) {
            return "vermelhonegrito";
        }
        return "verdenegrito";
    }

    public String styleClassDepreciacaoInconsistente() {
        if (isGrupoInconsistenteDepreciacao() || isGrupoComDepreciacaoContabilSuperiorAoValorContabil() ||
            isGrupoComDepreciacaoLevantamentoSuperiorAoValorLevantamento()) {
            return "vermelhonegrito";
        }
        return "verdenegrito";
    }

    public boolean isGrupoComDepreciacaoContabilSuperiorAoValorContabil() {
        return valorDepreciacaoContabil.compareTo(valorContabil) > 0;
    }

    public boolean isGrupoComDepreciacaoLevantamentoSuperiorAoValorLevantamento() {
        return valorDepreciacaoLevant.compareTo(valorLevantamentos) > 0;
    }

    public Boolean isGrupoInconsistenteDepreciacao() {
        return getDepreciacaoBensMaisDepreciacaoLevantamentos().compareTo(valorDepreciacaoContabil) != 0;
    }

    public Boolean isGrupoInconsistentePrincipal() {
        return getBensMaisLevantamentos().compareTo(valorContabil) != 0;
    }

    public Boolean isGrupoInconsistente() {
        return isGrupoInconsistentePrincipal() || isGrupoInconsistenteDepreciacao() ||
            isGrupoComDepreciacaoContabilSuperiorAoValorContabil() ||
            isGrupoComDepreciacaoLevantamentoSuperiorAoValorLevantamento();
    }

    @Override
    public int compareTo(EfetivacaoLevantamentoInformacoes o) {
        if (this.getGrupoBem() == null || o.getGrupoBem() == null) {
            return 0;
        }
        return this.getGrupoBem().getCodigo().compareTo(o.grupoBem.getCodigo());
    }

    public Boolean getConsulta() {
        return consulta;
    }

    public void setConsulta(Boolean consulta) {
        this.consulta = consulta;
    }
}
