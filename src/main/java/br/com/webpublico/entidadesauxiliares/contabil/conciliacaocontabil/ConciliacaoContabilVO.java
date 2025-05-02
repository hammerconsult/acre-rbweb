package br.com.webpublico.entidadesauxiliares.contabil.conciliacaocontabil;

import br.com.webpublico.enums.conciliacaocontabil.TipoConfigConciliacaoContabil;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class ConciliacaoContabilVO {
    private Integer quadro;
    private Integer ordem;
    private TipoConfigConciliacaoContabil totalizador;
    private String descricao;
    private BigDecimal valorContabil;
    private BigDecimal valorIntercorrente;
    private List<ConciliacaoContabilVO> linhas;

    public ConciliacaoContabilVO() {
        valorContabil = BigDecimal.ZERO;
        valorIntercorrente = BigDecimal.ZERO;
        linhas = Lists.newArrayList();
    }

    public Integer getQuadro() {
        return quadro;
    }

    public void setQuadro(Integer quadro) {
        this.quadro = quadro;
    }

    public TipoConfigConciliacaoContabil getTotalizador() {
        return totalizador;
    }

    public void setTotalizador(TipoConfigConciliacaoContabil totalizador) {
        this.totalizador = totalizador;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorContabil() {
        return valorContabil != null ? valorContabil : BigDecimal.ZERO;
    }

    public void setValorContabil(BigDecimal valorContabil) {
        this.valorContabil = valorContabil;
    }

    public BigDecimal getValorIntercorrente() {
        return valorIntercorrente != null ? valorIntercorrente : BigDecimal.ZERO;
    }

    public void setValorIntercorrente(BigDecimal valorIntercorrente) {
        this.valorIntercorrente = valorIntercorrente;
    }

    public BigDecimal getValorConciliacao() {
        return getValorContabil().subtract(getValorIntercorrente());
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public List<ConciliacaoContabilVO> getLinhas() {
        return linhas;
    }

    public void setLinhas(List<ConciliacaoContabilVO> linhas) {
        this.linhas = linhas;
    }
}
