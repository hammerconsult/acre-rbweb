package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.AgrupadorGrupoLevantamentoEstoque;
import br.com.webpublico.enums.SituacaoLevantamento;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.enums.TipoOperacaoBensEstoque;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Desenvolvimento on 31/01/2017.
 */
@Entity
@Audited
@Table(name = "EFETIVACAOLEVANTAMENTOESTO")
@Etiqueta("Efetivação de Levantamento de Estoque")
public class EfetivacaoLevantamentoEstoque extends EntradaMaterial {

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Unidade Orçamentária")
    @Transient
    private HierarquiaOrganizacional hierarquiaOrcamentaria;

    @Invisivel
    @OneToMany(mappedBy = "efetivacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEfetivacaoLevantamentoEstoque> itensEfetivacao;

    @Invisivel
    @OneToMany(mappedBy = "efetivacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEfetivacaoLevantamentoGrupoMaterial> itensEfetivacaoGrupo;

    public EfetivacaoLevantamentoEstoque() {
        super();
        this.setItensEfetivacao(new ArrayList<ItemEfetivacaoLevantamentoEstoque>());
        this.setItensEfetivacaoGrupo(new ArrayList<ItemEfetivacaoLevantamentoGrupoMaterial>());
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        if (hierarquiaOrcamentaria != null) {
            setUnidadeOrganizacional(hierarquiaOrcamentaria.getSubordinada());
        }
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public List<ItemEfetivacaoLevantamentoEstoque> getItensEfetivacao() {
        return itensEfetivacao;
    }

    public void setItensEfetivacao(List<ItemEfetivacaoLevantamentoEstoque> itensEfetivacao) {
        this.itensEfetivacao = itensEfetivacao;
    }

    public List<ItemEfetivacaoLevantamentoGrupoMaterial> getItensEfetivacaoGrupo() {
        return itensEfetivacaoGrupo;
    }

    public void setItensEfetivacaoGrupo(List<ItemEfetivacaoLevantamentoGrupoMaterial> itensEfetivacaoGrupo) {
        this.itensEfetivacaoGrupo = itensEfetivacaoGrupo;
    }

    public void criarItemEfetivacaoLevantamento(List<LevantamentoEstoque> levantamentos, List<AgrupadorGrupoLevantamentoEstoque> agrupamentos) {
        this.getItensEfetivacao().clear();
        this.getItensEfetivacaoGrupo().clear();
        for (LevantamentoEstoque levantamentoEstoque : levantamentos) {
            ItemEfetivacaoLevantamentoEstoque item = new ItemEfetivacaoLevantamentoEstoque();
            item.setEfetivacao(this);
            levantamentoEstoque.setSituacao(SituacaoLevantamento.EFETIVADO);
            item.setLevantamentoEstoque(levantamentoEstoque);
            this.getItensEfetivacao().add(item);
        }

        for (AgrupadorGrupoLevantamentoEstoque agrupamento : agrupamentos) {
            ItemEfetivacaoLevantamentoGrupoMaterial itemGrupo = new ItemEfetivacaoLevantamentoGrupoMaterial();
            itemGrupo.setEfetivacao(this);
            itemGrupo.setGrupoMaterial(agrupamento.getGrupoMaterial());
            itemGrupo.setQuantidade(agrupamento.getQuantidade());
            itemGrupo.setValorLevantamento(agrupamento.getValorTotal());
            itemGrupo.setValorSaldoGrupo(agrupamento.getSaldoGrupoMaterial());
            itemGrupo.setValorMaterial(agrupamento.getEstoqueAtual());
            itemGrupo.setTipoEstoque(agrupamento.getTipoEstoque());
            this.getItensEfetivacaoGrupo().add(itemGrupo);
        }
    }

    public BigDecimal getValorTotalLevantamento() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemEfetivacaoLevantamentoGrupoMaterial item : itensEfetivacaoGrupo) {
            total = total.add(item.getValorLevantamento());
        }
        return total;
    }

    public BigDecimal getValorTotalQuantidade() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemEfetivacaoLevantamentoGrupoMaterial item : itensEfetivacaoGrupo) {
            total = total.add(item.getQuantidade());
        }
        return total;
    }

    public BigDecimal getValorTotalSaldo() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemEfetivacaoLevantamentoGrupoMaterial item : itensEfetivacaoGrupo) {
            total = total.add(item.getValorSaldoGrupo());
        }
        return total;
    }

    public BigDecimal getValorTotalEstoque() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemEfetivacaoLevantamentoGrupoMaterial item : itensEfetivacaoGrupo) {
            total = total.add(item.getValorMaterial());
        }
        return total;
    }

    public BigDecimal getValorTotalDiferenca() {
        return getValorTotalSaldo().subtract(getValorTotalEstoque()).subtract(getValorTotalLevantamento());
    }

    public String getValorTotalQuantidadeFormatado() {
        return Util.formataQuandoDecimal(getValorTotalQuantidade(), TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
    }

    public String getHistoricoRazao(TipoOperacaoBensEstoque operacao) {
        try {
            String numero = getNumero() != null ? getNumero().toString() : "Gerado ao salvar";
            Integer exercucio = DataUtil.getAno(getDataEntrada());
            return operacao + ",  Entrada por Levantamento n° " + numero + "/" + exercucio;
        } catch (Exception ex) {
            return "Entrada por Levantamento";
        }
    }
}
