package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import com.google.common.base.Objects;

import java.io.Serializable;

public class VOUsuarioUnidade implements Serializable {
    private Long id;
    private Long idUnidade;
    private Long idUsuario;
    private Long idGrupo;
    private Boolean gestorProtocolo;
    private Boolean gestorMateriais;
    private Boolean gestorLicitacao;
    private Boolean gestorPatrimonio;
    private Boolean gestorOrcamento;
    private Boolean gestorFinanceiro;
    private Long idHo;
    private Long subordinadaId;
    private String codigoHo;
    private String descricaoHo;

    public VOUsuarioUnidade() {
        this.gestorProtocolo = Boolean.FALSE;
        this.gestorMateriais = Boolean.FALSE;
        this.gestorLicitacao = Boolean.FALSE;
        this.gestorPatrimonio = Boolean.FALSE;
        this.gestorOrcamento = Boolean.FALSE;
        this.gestorFinanceiro = Boolean.FALSE;
    }

    public VOUsuarioUnidade(HierarquiaOrganizacional hierarquia) {
        this.idHo = hierarquia.getId();
        this.subordinadaId = hierarquia.getSubordinada().getId();
        this.codigoHo = hierarquia.getCodigo();
        this.descricaoHo = hierarquia.getDescricao();

        this.gestorProtocolo = Boolean.FALSE;
        this.gestorMateriais = Boolean.FALSE;
        this.gestorLicitacao = Boolean.FALSE;
        this.gestorPatrimonio = Boolean.FALSE;
        this.gestorOrcamento = Boolean.FALSE;
        this.gestorFinanceiro = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(Long idUnidade) {
        this.idUnidade = idUnidade;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Long idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Boolean getGestorProtocolo() {
        return gestorProtocolo;
    }

    public void setGestorProtocolo(Boolean gestorProtocolo) {
        this.gestorProtocolo = gestorProtocolo;
    }

    public Boolean getGestorMateriais() {
        return gestorMateriais;
    }

    public void setGestorMateriais(Boolean gestorMateriais) {
        this.gestorMateriais = gestorMateriais;
    }

    public Boolean getGestorLicitacao() {
        return gestorLicitacao;
    }

    public void setGestorLicitacao(Boolean gestorLicitacao) {
        this.gestorLicitacao = gestorLicitacao;
    }

    public Boolean getGestorPatrimonio() {
        return gestorPatrimonio;
    }

    public void setGestorPatrimonio(Boolean gestorPatrimonio) {
        this.gestorPatrimonio = gestorPatrimonio;
    }

    public Boolean getGestorOrcamento() {
        return gestorOrcamento;
    }

    public void setGestorOrcamento(Boolean gestorOrcamento) {
        this.gestorOrcamento = gestorOrcamento;
    }

    public Boolean getGestorFinanceiro() {
        return gestorFinanceiro;
    }

    public void setGestorFinanceiro(Boolean gestorFinanceiro) {
        this.gestorFinanceiro = gestorFinanceiro;
    }

    public Long getIdHo() {
        return idHo;
    }

    public void setIdHo(Long idHo) {
        this.idHo = idHo;
    }

    public Long getSubordinadaId() {
        return subordinadaId;
    }

    public void setSubordinadaId(Long subordinadaId) {
        this.subordinadaId = subordinadaId;
    }

    public String getCodigoHo() {
        return codigoHo;
    }

    public void setCodigoHo(String codigoHo) {
        this.codigoHo = codigoHo;
    }

    public String getDescricaoHo() {
        return descricaoHo;
    }

    public void setDescricaoHo(String descricaoHo) {
        this.descricaoHo = descricaoHo;
    }

    public String getDescricaoHierarquia() {
        if (this.codigoHo != null && this.descricaoHo != null) {
            return this.codigoHo + " - " + this.descricaoHo;
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VOUsuarioUnidade that = (VOUsuarioUnidade) o;
        if (idGrupo != null) {
            return Objects.equal(subordinadaId, that.subordinadaId) && Objects.equal(idGrupo, that.idGrupo) && Objects.equal(idUsuario, that.idUsuario);
        }
        return Objects.equal(subordinadaId, that.subordinadaId) && Objects.equal(idUsuario, that.idUsuario);
    }

    @Override
    public int hashCode() {
        if (idGrupo != null) {
            return Objects.hashCode(subordinadaId, idGrupo, idUsuario);
        }
        return Objects.hashCode(subordinadaId, idUsuario);
    }
}
