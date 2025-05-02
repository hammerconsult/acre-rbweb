package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.EstadoBem;
import br.com.webpublico.entidades.EventoBem;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;

import java.math.BigDecimal;
import java.util.Date;

public class VOEventoBem {

    private Long idEvento;
    private Long idDetentor;
    private Long idBem;
    private Date dataLancamento;
    private Date dataOperacao;
    private String operacao;
    private BigDecimal valorLancamento;
    private String identificacaoBem;
    private String descricaoBem;
    private String situacao;
    private String unidadeAdministrativa;
    private String unidadeOrcamentaria;
    private String grupoPatrimonial;
    private String localizacao;
    private String tipoAquisicao;
    private String estadoConservacao;
    private String situacaoConservacao;
    private EstadoBem estadoResultante;
    private EstadoBem estadoInicial;
    private VOEventoBem ultimoEvento;
    private TipoEventoBem tipoEventoBem;
    private SituacaoEventoBem situacaoEventoBem;

    public Long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Long idEvento) {
        this.idEvento = idEvento;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public BigDecimal getValorLancamento() {
        return valorLancamento;
    }

    public void setValorLancamento(BigDecimal valorLancamento) {
        this.valorLancamento = valorLancamento;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
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

    public String getGrupoPatrimonial() {
        return grupoPatrimonial;
    }

    public void setGrupoPatrimonial(String grupoPatrimonial) {
        this.grupoPatrimonial = grupoPatrimonial;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getEstadoConservacao() {
        return estadoConservacao;
    }

    public void setEstadoConservacao(String estadoConservacao) {
        this.estadoConservacao = estadoConservacao;
    }

    public String getSituacaoConservacao() {
        return situacaoConservacao;
    }

    public void setSituacaoConservacao(String situacaoConservacao) {
        this.situacaoConservacao = situacaoConservacao;
    }

    public Long getIdDetentor() {
        return idDetentor;
    }

    public void setIdDetentor(Long idDetentor) {
        this.idDetentor = idDetentor;
    }

    public EstadoBem getEstadoResultante() {
        return estadoResultante;
    }

    public void setEstadoResultante(EstadoBem estadoResultante) {
        this.estadoResultante = estadoResultante;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getTipoAquisicao() {
        return tipoAquisicao;
    }

    public void setTipoAquisicao(String tipoAquisicao) {
        this.tipoAquisicao = tipoAquisicao;
    }

    public EstadoBem getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(EstadoBem estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public TipoEventoBem getTipoEventoBem() {
        return tipoEventoBem;
    }

    public void setTipoEventoBem(TipoEventoBem tipoEventoBem) {
        this.tipoEventoBem = tipoEventoBem;
    }

    public SituacaoEventoBem getSituacaoEventoBem() {
        return situacaoEventoBem;
    }

    public void setSituacaoEventoBem(SituacaoEventoBem situacaoEventoBem) {
        this.situacaoEventoBem = situacaoEventoBem;
    }

    public Long getIdBem() {
        return idBem;
    }

    public void setIdBem(Long idBem) {
        this.idBem = idBem;
    }

    public String getIdentificacaoBem() {
        return identificacaoBem;
    }

    public void setIdentificacaoBem(String identificacaoBem) {
        this.identificacaoBem = identificacaoBem;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public VOEventoBem getUltimoEvento() {
        return ultimoEvento;
    }

    public void setUltimoEvento(VOEventoBem ultimoEvento) {
        this.ultimoEvento = ultimoEvento;
    }
}
