package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operador;
import br.com.webpublico.enums.Perecibilidade;
import br.com.webpublico.enums.StatusMaterial;
import br.com.webpublico.enums.TipoConsulta;
import br.com.webpublico.util.Util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ConsultaLocalEstoqueFiltro {

    private LocalEstoque localEstoque;
    private UnidadeOrganizacional orcamentaria;
    private GrupoMaterial grupoMaterial;
    private Material material;
    private UsuarioSistema usuarioSistema;
    private Date dataOperacao;
    private Date dataReferencia;
    private TipoConsulta tipoConsulta;
    private Perecibilidade perecibilidade;
    private List<UnidadeOrganizacional> unidadesOrcamentarias;
    private List<GrupoMaterial> gruposMateriais;
    private StatusMaterial statusMaterial;
    private Long idMaterial;
    private Operador tipoComparacao;
    private BigDecimal quantidade;

    public void limparCampos() {
        material = null;
        grupoMaterial = null;
        orcamentaria = null;
        unidadesOrcamentarias = new ArrayList<>();
        tipoComparacao = Operador.MAIOR;
        quantidade = BigDecimal.ZERO;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public UnidadeOrganizacional getOrcamentaria() {
        return orcamentaria;
    }

    public void setOrcamentaria(UnidadeOrganizacional orcamentaria) {
        this.orcamentaria = orcamentaria;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public TipoConsulta getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(TipoConsulta tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public Perecibilidade getPerecibilidade() {
        return perecibilidade;
    }

    public void setPerecibilidade(Perecibilidade perecibilidade) {
        this.perecibilidade = perecibilidade;
    }

    public List<UnidadeOrganizacional> getUnidadesOrcamentarias() {
        return unidadesOrcamentarias;
    }

    public void setUnidadesOrcamentarias(List<UnidadeOrganizacional> unidadesOrcamentarias) {
        this.unidadesOrcamentarias = unidadesOrcamentarias;
    }

    public List<GrupoMaterial> getGruposMateriais() {
        return gruposMateriais;
    }

    public void setGruposMateriais(List<GrupoMaterial> gruposMateriais) {
        this.gruposMateriais = gruposMateriais;
    }

    public StatusMaterial getStatusMaterial() {
        return statusMaterial;
    }

    public void setStatusMaterial(StatusMaterial statusMaterial) {
        this.statusMaterial = statusMaterial;
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public Operador getTipoComparacao() {
        return tipoComparacao;
    }

    public void setTipoComparacao(Operador tipoComparacao) {
        this.tipoComparacao = tipoComparacao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public List<Long> getIdsGruposMateriais() {
        if (!Util.isListNullOrEmpty(gruposMateriais)) {
            List<Long> idsGrupos = new ArrayList<>();
            for (GrupoMaterial grupoMaterial : gruposMateriais) {
                idsGrupos.add(grupoMaterial.getId());
            }
            return idsGrupos;
        }
        return Collections.emptyList();
    }

    public List<Long> getIdsUnidadesOrcamentarias() {
        if (!Util.isListNullOrEmpty(unidadesOrcamentarias)) {
            List<Long> idsOrcamentarias = new ArrayList<>();
            for (UnidadeOrganizacional unidade : unidadesOrcamentarias) {
                idsOrcamentarias.add(unidade.getId());
            }
            return idsOrcamentarias;
        }
        return Collections.emptyList();
    }
}
