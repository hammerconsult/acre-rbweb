package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoLevantamento;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 26/01/2017.
 */
@Entity
@Audited
@Etiqueta("Levantamento de Estoque")
public class LevantamentoEstoque extends SuperEntidade implements Comparable<LevantamentoEstoque> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do Levantamento")
    private Date dataLevantamento;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @ManyToOne
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeAdministrativa;

    @ManyToOne
    @Etiqueta("Unidade Orçamentária")
    private UnidadeOrganizacional unidadeOrcamentaria;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Transient
    @Etiqueta("Unidade Administrativa")
    private HierarquiaOrganizacional hierarquiaAdministrativa;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Transient
    @Etiqueta("Unidade Orçamentária")
    private HierarquiaOrganizacional hierarquiaOrcamentaria;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Local de Estoque")
    private LocalEstoque localEstoque;

    @Etiqueta("Local de Estoque")
    private String observacoes;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoLevantamento situacao;

    @Invisivel
    @OneToMany(mappedBy = "levantamentoEstoque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemLevantamentoEstoque> itensLevantamentoEstoque;

    public LevantamentoEstoque() {
        super();
        this.situacao = SituacaoLevantamento.EM_ELABORACAO;
        this.itensLevantamentoEstoque = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataLevantamento() {
        return dataLevantamento;
    }

    public void setDataLevantamento(Date dataLevantamento) {
        this.dataLevantamento = dataLevantamento;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        if (hierarquiaAdministrativa != null) {
            unidadeAdministrativa = hierarquiaAdministrativa.getSubordinada();
        }
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        if (hierarquiaOrcamentaria != null) {
            unidadeOrcamentaria = hierarquiaOrcamentaria.getSubordinada();
        }
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public SituacaoLevantamento getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoLevantamento situacao) {
        this.situacao = situacao;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public List<ItemLevantamentoEstoque> getItensLevantamentoEstoque() {
        Collections.sort(itensLevantamentoEstoque);
        return itensLevantamentoEstoque;
    }

    public void setItensLevantamentoEstoque(List<ItemLevantamentoEstoque> itensLevantamentoEstoque) {
        this.itensLevantamentoEstoque = itensLevantamentoEstoque;
    }

    public void removerItemLevantamentoEstoque(ItemLevantamentoEstoque item) {
        this.getItensLevantamentoEstoque().remove(item);
        FacesUtil.atualizarComponente("Formulario:gridGeral");
    }

    public Boolean hasMaterialNosItensDoLevantamento(Material material) {
        for (ItemLevantamentoEstoque itemLev : getItensLevantamentoEstoque()) {
            if (material.equals(itemLev.getMaterial())) {
                return true;
            }
        }
        return false;
    }

    public String getQuantidadeTotalFormatada() {
        return Util.formataQuandoDecimal(getValotTotalQuantidade(), TipoMascaraUnidadeMedida.QUATRO_CASAS_DECIMAL);
    }

    public BigDecimal getValotTotalQuantidade() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ItemLevantamentoEstoque item : this.getItensLevantamentoEstoque()) {
            retorno = retorno.add(item.getQuantidade());
        }
        return retorno;
    }

    public BigDecimal getValotTotal() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ItemLevantamentoEstoque item : this.getItensLevantamentoEstoque()) {
            retorno = retorno.add(item.getValorTotal().setScale(2, RoundingMode.FLOOR));
        }
        return retorno;
    }

    public Boolean isFinalizado() {
        return SituacaoLevantamento.FINALIZADO.equals(this.getSituacao());
    }

    public Boolean isEfetivado() {
        return SituacaoLevantamento.EFETIVADO.equals(this.getSituacao());
    }

    public Boolean isEmElaboracao() {
        return SituacaoLevantamento.EM_ELABORACAO.equals(this.getSituacao());
    }

    @Override
    public int compareTo(LevantamentoEstoque o) {
        try {
            return this.codigo.compareTo(o.getCodigo());
        } catch (NullPointerException n) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return codigo + " - " + observacoes;
    }
}
