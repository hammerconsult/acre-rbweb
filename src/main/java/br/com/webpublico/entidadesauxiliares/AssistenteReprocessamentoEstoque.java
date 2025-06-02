package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.entidades.Material;
import br.com.webpublico.exception.ValidacaoException;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class AssistenteReprocessamentoEstoque {

    private Long idObjeto;
    private Date dataInicial;
    private Date dataFinal;
    private GrupoMaterial grupoMaterial;
    private LocalEstoque localEstoque;
    private HierarquiaOrganizacional hierarquiaAdm;
    private HierarquiaOrganizacional hierarquiaOrc;
    private Material material;
    private Long idMaterial;
    private Boolean reprocessamento;
    private Boolean reprocessamentoSessao;
    private List<ItemReprocessamentoEstoque> itens;

    public AssistenteReprocessamentoEstoque() {
        itens = Lists.newArrayList();
        reprocessamento = false;
        reprocessamentoSessao = false;
    }

    public Long getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(Long idObjeto) {
        this.idObjeto = idObjeto;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public List<ItemReprocessamentoEstoque> getItens() {
        return itens;
    }

    public void setItens(List<ItemReprocessamentoEstoque> itens) {
        this.itens = itens;
    }

    public Boolean getReprocessamento() {
        return reprocessamento;
    }

    public void setReprocessamento(Boolean reprocessamento) {
        this.reprocessamento = reprocessamento;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public HierarquiaOrganizacional getHierarquiaAdm() {
        return hierarquiaAdm;
    }

    public void setHierarquiaAdm(HierarquiaOrganizacional hierarquiaAdm) {
        this.hierarquiaAdm = hierarquiaAdm;
    }

    public HierarquiaOrganizacional getHierarquiaOrc() {
        return hierarquiaOrc;
    }

    public void setHierarquiaOrc(HierarquiaOrganizacional hierarquiaOrc) {
        this.hierarquiaOrc = hierarquiaOrc;
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public Boolean getReprocessamentoSessao() {
        return reprocessamentoSessao;
    }

    public void setReprocessamentoSessao(Boolean reprocessamentoSessao) {
        this.reprocessamentoSessao = reprocessamentoSessao;
    }

    public String getCondicaoSql() {
        String condicao = "";
        if (hierarquiaAdm != null) {
            condicao += " and vwadm.subordinada_id = " + hierarquiaAdm.getSubordinada().getId();
        }
        if (hierarquiaOrc != null) {
            condicao += " and vworc.subordinada_id = " + hierarquiaOrc.getSubordinada().getId();
        }
        if (localEstoque != null) {
            condicao += " and le.id = " + localEstoque.getId();
        }
        if (grupoMaterial != null) {
            condicao += " and mat.grupo_id = " + grupoMaterial.getId();
        }
        if (material != null) {
            condicao += " and mat.id = " + material.getId();
        }
        if (getReprocessamentoSessao()) {
            condicao += " and mat.id = " + getIdMaterial();
        }
        return condicao;
    }

    public void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data final deve ser informado.");
        }
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data final deve ser superior ou igual a data inicial.");
        }
        ve.lancarException();
    }
}
