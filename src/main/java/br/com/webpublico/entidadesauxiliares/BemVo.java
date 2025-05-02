package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.EstadoBem;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.entidades.TipoReducao;
import br.com.webpublico.enums.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class BemVo implements Serializable, Comparable<BemVo> {

    private Bem bem;
    private Long id;
    private String identificacao;
    private String descricao;
    private SituacaoEventoBem situacaoEventoBem;
    private TipoEventoBem tipoEventoBem;
    private Long idEventoBem;
    private String unidadeAdministrativa;
    private String unidadeOrcamentaria;
    private EstadoBem estadoInicial;
    private EstadoBem estadoResultante;
    private EstadoBem ultimoEstado;
    private BigDecimal valorAjusteInicial;
    private BigDecimal valorAjuste;
    private BigDecimal valorAjusteFinal;
    private BigDecimal valorLancamento;
    private TipoReducao tipoReducao;
    private TipoLancamento tipoLancamento;
    private ObjetoCompra objetoCompra;
    private boolean residual;

    public BemVo() {
        residual = false;
        tipoLancamento = TipoLancamento.NORMAL;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EstadoBem getUltimoEstado() {
        return ultimoEstado;
    }

    public void setUltimoEstado(EstadoBem ultimoEstado) {
        this.ultimoEstado = ultimoEstado;
    }

    public boolean getResidual() {
        return residual;
    }

    public void setResidual(boolean residual) {
        this.residual = residual;
    }

    public TipoEventoBem getTipoEventoBem() {
        return tipoEventoBem;
    }

    public void setTipoEventoBem(TipoEventoBem tipoEventoBem) {
        this.tipoEventoBem = tipoEventoBem;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public BigDecimal getValorLancamento() {
        return valorLancamento;
    }

    public void setValorLancamento(BigDecimal valorLancamento) {
        this.valorLancamento = valorLancamento;
    }

    public BigDecimal getValorAjuste() {
        return valorAjuste;
    }

    public void setValorAjuste(BigDecimal valorAjuste) {
        this.valorAjuste = valorAjuste;
    }

    public BigDecimal getValorAjusteFinal() {
        return valorAjusteFinal;
    }

    public void setValorAjusteFinal(BigDecimal valorAjusteFinal) {
        this.valorAjusteFinal = valorAjusteFinal;
    }

    public TipoReducao getTipoReducao() {
        return tipoReducao;
    }

    public void setTipoReducao(TipoReducao tipoReducao) {
        this.tipoReducao = tipoReducao;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Long getIdEventoBem() {
        return idEventoBem;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public SituacaoEventoBem getSituacaoEventoBem() {
        return situacaoEventoBem;
    }

    public void setSituacaoEventoBem(SituacaoEventoBem situacaoEventoBem) {
        this.situacaoEventoBem = situacaoEventoBem;
    }

    public void setIdEventoBem(Long idEventoBem) {
        this.idEventoBem = idEventoBem;
    }

    public String getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(String unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public String getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(String unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public EstadoBem getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(EstadoBem estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public EstadoBem getEstadoResultante() {
        return estadoResultante;
    }

    public void setEstadoResultante(EstadoBem estadoResultante) {
        this.estadoResultante = estadoResultante;
    }

    public BigDecimal getValorAjusteInicial() {
        return valorAjusteInicial;
    }

    public void setValorAjusteInicial(BigDecimal valorAjusteInicial) {
        this.valorAjusteInicial = valorAjusteInicial;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public BigDecimal getValorAjusteInicialPorTipoAjuste(TipoAjusteBemMovel tipoAjuste, Boolean estadoInicialBem) {
        if (tipoAjuste.isOriginal()) {
            return estadoInicialBem ? getEstadoInicial().getValorOriginal() : getBem().getValorOriginal();

        } else if (tipoAjuste.isDepreciacao()) {
            return estadoInicialBem ? getEstadoInicial().getValorAcumuladoDaDepreciacao() : getBem().getValorAcumuladoDaDepreciacao();

        } else {
            return estadoInicialBem ? getEstadoInicial().getValorAcumuladoDaAmortizacao() : getBem().getValorAcumuladoDaAmortizacao();
        }
    }

    public BigDecimal calcularValorAjusteFinal(OperacaoAjusteBemMovel operacao) {
        if (operacao != null) {
            switch (operacao) {
                case AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO:
                case AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA:
                case AJUSTE_BEM_MOVEL_AMORTIZACAO_DIMINUTIVO:
                case AJUSTE_BEM_MOVEL_DEPRECIACAO_DIMINUTIVO:
                    return getValorAjusteInicial().add(getValorAjuste());

                case AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO:
                case AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO_EMPRESA_PUBLICA:
                case AJUSTE_BEM_MOVEL_AMORTIZACAO_AUMENTATIVO:
                case AJUSTE_BEM_MOVEL_DEPRECIACAO_AUMENTATIVO:
                    return getValorAjusteInicial().subtract(getValorAjuste());
                default:
                    return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BemVo bemVo = (BemVo) o;
        return Objects.equals(bem, bemVo.bem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bem);
    }

    @Override
    public int compareTo(BemVo o) {
        if (o.getBem() != null && getBem() != null) {
            return Long.valueOf(getBem().getIdentificacao()).compareTo(Long.valueOf(o.getBem().getIdentificacao()));
        }
        return 0;
    }
}
