/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ItemMovimentoEstoque;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity

/**
 * Representa a quantidade e o valor dos materiais armazenados em determinado LocalEstoque.
 *
 * @author arthur
 */
public class Estoque extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @ManyToOne
    private LocalEstoqueOrcamentario localEstoqueOrcamentario;

    @Tabelavel
    @ManyToOne
    private Material material;

    /**
     * Quantidade da Mercadoria em estoque.
     */
    @Obrigatorio
    @Tabelavel
    private BigDecimal fisico;

    /**
     * Valor da Mercadoria em estoque.
     */
    @Obrigatorio
    @Tabelavel
    @Monetario
    private BigDecimal financeiro;

    @Temporal(TemporalType.DATE)
    private Date dataEstoque;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date liberadoEm;

    private Boolean bloqueado;

    @Enumerated(EnumType.STRING)
    private TipoEstoque tipoEstoque;

    @OneToMany(mappedBy = "estoque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstoqueLoteMaterial> lotesMaterial;

    public Estoque() {
        super();
        inicializarAtributos();
    }

    public Estoque(LocalEstoqueOrcamentario leo, Material m, Date data, TipoEstoque tipoEstoque) {
        super();
        inicializarAtributos();
        this.localEstoqueOrcamentario = leo;
        this.material = m;
        this.dataEstoque = data;
        this.tipoEstoque = tipoEstoque;
    }

    public Estoque(Estoque estoque, Date dataMovimento) {
        super();
        inicializarAtributos();
        criarNovosEstoquesLoteComBaseNosAnteriores(estoque.getLotesMaterial());
        this.localEstoqueOrcamentario = estoque.getLocalEstoqueOrcamentario();
        this.material = estoque.getMaterial();
        this.fisico = estoque.getFisico();
        this.financeiro = estoque.getFinanceiro();
        this.dataEstoque = dataMovimento;
        this.tipoEstoque = estoque.getTipoEstoque();
    }

    private void inicializarAtributos() {
        bloqueado = false;
        dataEstoque = new Date();
        financeiro = BigDecimal.ZERO;
        fisico = BigDecimal.ZERO;
        lotesMaterial = new ArrayList<>();
    }

    private void criarNovosEstoquesLoteComBaseNosAnteriores(List<EstoqueLoteMaterial> lotes) {
        for (EstoqueLoteMaterial elm : lotes) {
            this.lotesMaterial.add(new EstoqueLoteMaterial(elm, this));
        }
    }

    public Date getLiberadoEm() {
        return liberadoEm;
    }

    public void setLiberadoEm(Date bloqueadoEm) {
        this.liberadoEm = bloqueadoEm;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public Date getDataEstoque() {
        return dataEstoque;
    }

    public void setDataEstoque(Date dataEstoque) {
        this.dataEstoque = dataEstoque;
    }

    public BigDecimal getFinanceiro() {
        return financeiro;
    }

    public void setFinanceiro(BigDecimal financeiro) {
        this.financeiro = financeiro;
    }

    public BigDecimal getFisico() {
        return fisico;
    }

    public void setFisico(BigDecimal fisico) {
        this.fisico = fisico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalEstoqueOrcamentario getLocalEstoqueOrcamentario() {
        return localEstoqueOrcamentario;
    }

    public void setLocalEstoqueOrcamentario(LocalEstoqueOrcamentario localEstoqueOrcamentario) {
        this.localEstoqueOrcamentario = localEstoqueOrcamentario;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public List<EstoqueLoteMaterial> getLotesMaterial() {
        return lotesMaterial;
    }

    public void setLotesMaterial(List<EstoqueLoteMaterial> lotesMaterial) {
        this.lotesMaterial = lotesMaterial;
    }

    public void atualizarValores(ItemMovimentoEstoque item) {
        if (item.getTipoOperacao().isCredito()) {
            financeiro = financeiro.add(item.getValorTotal());
            fisico = fisico.add(item.getQuantidade());
        } else {
            financeiro = financeiro.subtract(item.getValorTotal());
            fisico = fisico.subtract(item.getQuantidade());
        }
    }

    public boolean foiCriadoNaData(Date dataMovimento) {
        return DataUtil.dataSemHorario(dataEstoque).equals(DataUtil.dataSemHorario(dataMovimento));
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoqueOrcamentario.getLocalEstoque();
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return localEstoqueOrcamentario.getUnidadeOrcamentaria();
    }

    public EstoqueLoteMaterial getEstoqueLoteMaterialDoLote(LoteMaterial lm) {
        for (EstoqueLoteMaterial elm : lotesMaterial) {
            if (elm.getLoteMaterial().equals(lm)) {
                return elm;
            }
        }
        return null;
    }

    public BigDecimal getCustoMedio() {
        if (getFisico().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal custo = getFinanceiro().divide(getFisico(), 8, RoundingMode.HALF_EVEN);
        return custo.setScale(4, RoundingMode.HALF_EVEN);
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }
}
