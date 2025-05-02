package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoReducaoValorBem;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Fabio on 30/04/2018.
 */
public class VOReducaoValorBem {

    private Long id;
    private Long idBem;
    private Long idEstadoInicial;
    private Long idEstadoResultante;
    private Date dataLancamento;
    private BigDecimal valorDoLancamento;
    private TipoReducaoValorBem tipoReducaoValorBem;
    private SituacaoEventoBem situacaoEventoBem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdBem() {
        return idBem;
    }

    public void setIdBem(Long idBem) {
        this.idBem = idBem;
    }

    public BigDecimal getValorDoLancamento() {
        return valorDoLancamento;
    }

    public void setValorDoLancamento(BigDecimal valorDoLancamento) {
        this.valorDoLancamento = valorDoLancamento;
    }

    public TipoReducaoValorBem getTipoReducaoValorBem() {
        return tipoReducaoValorBem;
    }

    public void setTipoReducaoValorBem(TipoReducaoValorBem tipoReducaoValorBem) {
        this.tipoReducaoValorBem = tipoReducaoValorBem;
    }

    public Long getIdEstadoInicial() {
        return idEstadoInicial;
    }

    public void setIdEstadoInicial(Long idEstadoInicial) {
        this.idEstadoInicial = idEstadoInicial;
    }

    public Long getIdEstadoResultante() {
        return idEstadoResultante;
    }

    public void setIdEstadoResultante(Long idEstadoResultante) {
        this.idEstadoResultante = idEstadoResultante;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public SituacaoEventoBem getSituacaoEventoBem() {
        return situacaoEventoBem;
    }

    public void setSituacaoEventoBem(SituacaoEventoBem situacaoEventoBem) {
        this.situacaoEventoBem = situacaoEventoBem;
    }
}
