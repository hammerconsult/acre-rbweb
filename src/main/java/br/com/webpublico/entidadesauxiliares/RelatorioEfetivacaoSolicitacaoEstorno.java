package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by alex on 07/07/17.
 */
public class RelatorioEfetivacaoSolicitacaoEstorno {

    private BigDecimal codigoEfetivacao;
    private Date dataHoraCriacao;
    private String responsavelOrigem;
    private String responsavelDestino;
    private String unidadeOrigem;
    private String unidadeDestino;
    private String identificacao;
    private String descricao;
    private String estadoBem;
    private BigDecimal valorOriginal;
    private BigDecimal ajustes;

    public RelatorioEfetivacaoSolicitacaoEstorno() {
    }

    public BigDecimal getCodigoEfetivacao() {
        return codigoEfetivacao;
    }

    public void setCodigoEfetivacao(BigDecimal codigoEfetivacao) {
        this.codigoEfetivacao = codigoEfetivacao;
    }

    public Date getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public void setDataHoraCriacao(Date dataHoraCriacao) {
        this.dataHoraCriacao = dataHoraCriacao;
    }

    public String getResponsavelOrigem() {
        return responsavelOrigem;
    }

    public void setResponsavelOrigem(String responsavelOrigem) {
        this.responsavelOrigem = responsavelOrigem;
    }

    public String getResponsavelDestino() {
        return responsavelDestino;
    }

    public void setResponsavelDestino(String responsavelDestino) {
        this.responsavelDestino = responsavelDestino;
    }

    public String getUnidadeOrigem() {
        return unidadeOrigem;
    }

    public void setUnidadeOrigem(String unidadeOrigem) {
        this.unidadeOrigem = unidadeOrigem;
    }

    public String getUnidadeDestino() {
        return unidadeDestino;
    }

    public void setUnidadeDestino(String unidadeDestino) {
        this.unidadeDestino = unidadeDestino;
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

    public String getEstadoBem() {
        return estadoBem;
    }

    public void setEstadoBem(String estadoBem) {
        this.estadoBem = estadoBem;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getAjustes() {
        return ajustes;
    }

    public void setAjustes(BigDecimal ajustes) {
        this.ajustes = ajustes;
    }
}
