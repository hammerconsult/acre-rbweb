package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.arquivo.dto.ArquivoDTO;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wellington on 04/09/17.
 */
public class LegislacaoDTO implements Serializable, NfseDTO {

    private Long id;
    private TipoLegislacaoDTO tipoLegislacaoDTO;
    private String nome;
    private String sumula;
    private Date dataPublicacao;
    private ArquivoDTO arquivoDTO;
    private Integer ordemExibicao;
    private Boolean habilitarExibicao;

    public LegislacaoDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoLegislacaoDTO getTipoLegislacaoDTO() {
        return tipoLegislacaoDTO;
    }

    public void setTipoLegislacaoDTO(TipoLegislacaoDTO tipoLegislacaoDTO) {
        this.tipoLegislacaoDTO = tipoLegislacaoDTO;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSumula() {
        return sumula;
    }

    public void setSumula(String sumula) {
        this.sumula = sumula;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public ArquivoDTO getArquivoDTO() {
        return arquivoDTO;
    }

    public void setArquivoDTO(ArquivoDTO arquivoDTO) {
        this.arquivoDTO = arquivoDTO;
    }

    public Integer getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(Integer ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }

    public Boolean getHabilitarExibicao() {
        return habilitarExibicao;
    }

    public void setHabilitarExibicao(Boolean habilitarExibicao) {
        this.habilitarExibicao = habilitarExibicao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LegislacaoDTO that = (LegislacaoDTO) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
