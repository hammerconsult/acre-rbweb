package br.com.webpublico.ws.model;

import java.util.Date;

public class HistoricoLotacaoDTO {

    private String codigo;
    private String descricao;
    private Date inicioVigencia;
    private Date finalVigencia;
    private Long hierarquiaId;
    private Long subordinadaId;
    private Long superiorId;
    private Date inicioVigenciaHierarquia;
    private Date finalVigenciaHierarquia;
    private Integer nivelEntidade;
    private OrgaoDTO orgao;

    public HistoricoLotacaoDTO() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Long getSubordinadaId() {
        return subordinadaId;
    }

    public void setSubordinadaId(Long subordinadaId) {
        this.subordinadaId = subordinadaId;
    }

    public Long getSuperiorId() {
        return superiorId;
    }

    public void setSuperiorId(Long superiorId) {
        this.superiorId = superiorId;
    }

    public Date getInicioVigenciaHierarquia() {
        return inicioVigenciaHierarquia;
    }

    public void setInicioVigenciaHierarquia(Date inicioVigenciaHierarquia) {
        this.inicioVigenciaHierarquia = inicioVigenciaHierarquia;
    }

    public Date getFinalVigenciaHierarquia() {
        return finalVigenciaHierarquia;
    }

    public void setFinalVigenciaHierarquia(Date finalVigenciaHierarquia) {
        this.finalVigenciaHierarquia = finalVigenciaHierarquia;
    }

    public Long getHierarquiaId() {
        return hierarquiaId;
    }

    public void setHierarquiaId(Long hierarquiaId) {
        this.hierarquiaId = hierarquiaId;
    }

    public Integer getNivelEntidade() {
        return nivelEntidade;
    }

    public void setNivelEntidade(Integer nivelEntidade) {
        this.nivelEntidade = nivelEntidade;
    }

    public OrgaoDTO getOrgao() {
        return orgao;
    }

    public void setOrgao(OrgaoDTO orgao) {
        this.orgao = orgao;
    }
}
