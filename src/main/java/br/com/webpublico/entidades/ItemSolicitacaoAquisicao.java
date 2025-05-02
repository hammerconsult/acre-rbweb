package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CaracterizadorDeBemMovel;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Item Solicitação de Aquisição")
public class ItemSolicitacaoAquisicao extends EventoBem implements CaracterizadorDeBemMovel {

    @ManyToOne
    @Etiqueta("Solicitação de Aquisição")
    private SolicitacaoAquisicao solicitacaoAquisicao;

    @ManyToOne
    private ItemDoctoItemAquisicao itemDoctoItemAquisicao;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeAdministrativa;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Unidade Orçamentária")
    private UnidadeOrganizacional unidadeOrcamentaria;

    @Transient
    @Invisivel
    private GrupoBem grupoBem;

    public ItemSolicitacaoAquisicao() {
        super(TipoEventoBem.ITEM_SOLICITACAO_AQUISICAO, TipoOperacao.NENHUMA_OPERACAO);
    }

    public SolicitacaoAquisicao getSolicitacaoAquisicao() {
        return solicitacaoAquisicao;
    }

    public void setSolicitacaoAquisicao(SolicitacaoAquisicao solicitacaoAquisicao) {
        this.solicitacaoAquisicao = solicitacaoAquisicao;
    }

    public ItemRequisicaoDeCompra getItemRequisicaoCompra() {
        return getItemDoctoItemAquisicao().getItemRequisicaoDeCompra();
    }

    public ItemDoctoItemAquisicao getItemDoctoItemAquisicao() {
        return itemDoctoItemAquisicao;
    }

    public void setItemDoctoItemAquisicao(ItemDoctoItemAquisicao itemDoctoItemAquisicao) {
        this.itemDoctoItemAquisicao = itemDoctoItemAquisicao;
    }

    @Override
    public ObjetoCompra getObjetoCompra() {
        return itemDoctoItemAquisicao.getItemRequisicaoDeCompra().getObjetoCompra();
    }

    @Override
    public String getRegistroAnterior() {
        return null;
    }

    @Override
    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return itemDoctoItemAquisicao.getItemRequisicaoDeCompra().getObjetoCompra().getGrupoObjetoCompra();
    }

    @Override
    public String getObservacao() {
        return null;
    }

    @Override
    public DetentorOrigemRecurso getDetentorOrigemRecurso() {
        return null;
    }

    @Override
    public Pessoa getFornecedor() {
        return null;
    }

    @Override
    public EstadoConservacaoBem getEstadoConservacaoBem() {
        return EstadoConservacaoBem.OPERACIONAL;
    }

    @Override
    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return TipoAquisicaoBem.COMPRA;
    }

    @Override
    public String getModelo() {
        try {
            return itemDoctoItemAquisicao.getItemRequisicaoDeCompra().getItemContrato().getItemAdequado().getModelo();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String getMarca() {
        try {
            return itemDoctoItemAquisicao.getItemRequisicaoDeCompra().getItemContrato().getItemAdequado().getMarca();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String getLocalizacao() {
        return itemDoctoItemAquisicao.getItemRequisicaoDeCompra().getRequisicaoDeCompra().getLocalEntrega();
    }

    @Override
    public SituacaoConservacaoBem getSituacaoConservacaoBem() {
        return SituacaoConservacaoBem.USO_NORMAL;
    }

    @Override
    public Date getDataDaOperacao() {
        return getDataOperacao();
    }

    @Override
    public BigDecimal getValorDoBem() {
        return BigDecimal.ZERO;
    }

    @Override
    public String getCodigoPatrimonio() {
        return null;
    }

    @Override
    public String getDescricaoDoBem() {
        if (!Strings.isNullOrEmpty(itemDoctoItemAquisicao.getProdutoEntregue())) {
            return itemDoctoItemAquisicao.getProdutoEntregue();
        }
        return itemDoctoItemAquisicao.getItemRequisicaoDeCompra().getDescricao();
    }

    @Override
    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    @Override
    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    @Override
    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    @Override
    public void setGrupoBem(GrupoBem gb) {
        this.grupoBem = gb;
    }

    @Override
    public List<BemNotaFiscal> getNotasFiscais() {
        return Lists.newArrayList();
    }

    @Override
    public String toString() {
        try {
            return super.toString();
        } catch (NullPointerException ex) {
            return "";
        }
    }
}
