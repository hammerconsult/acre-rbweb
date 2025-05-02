/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity

@Etiqueta("Política de Estoque")
/**
 *
 * Representa a cota mensal de determinado material que um centro de custo (Conta) pode utilizar.
 *
 * @author arthur
 */
public class PoliticaDeEstoque extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Unidade Administrativa")
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private UnidadeOrganizacional unidadeOrganizacional;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Material")
    private Material material;

    @Tabelavel
    @Positivo
    @Etiqueta("Ponto de Reposição")
    private BigDecimal pontoDeReposicao;

    @Tabelavel
    @Positivo
    @Etiqueta("Estoque Mínimo")
    private BigDecimal estoqueMinimo;

    @Tabelavel
    @Positivo
    @Etiqueta("Estoque Máximo")
    private BigDecimal estoqueMaximo;

    @Tabelavel
    @Positivo
    @Etiqueta("Lote Econômico")
    private BigDecimal loteEconomico;

    @Etiqueta("Nível de Notificação")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    private TipoNivelNotificacao nivelDeNotificacao;

    @Positivo
    @Etiqueta("Saída Máxima")
    @Tabelavel
    private BigDecimal saidaMaxima;

    public PoliticaDeEstoque() {
        super();
    }

    public BigDecimal getEstoqueMaximo() {
        return estoqueMaximo;
    }

    public void setEstoqueMaximo(BigDecimal estoqueMaximo) {
        this.estoqueMaximo = estoqueMaximo;
    }

    public BigDecimal getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(BigDecimal estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPontoDeReposicao() {
        return pontoDeReposicao;
    }

    public void setPontoDeReposicao(BigDecimal pontoDeReposicao) {
        this.pontoDeReposicao = pontoDeReposicao;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getLoteEconomico() {
        return loteEconomico;
    }

    public void setLoteEconomico(BigDecimal loteEconomico) {
        this.loteEconomico = loteEconomico;
    }

    public TipoNivelNotificacao getNivelDeNotificacao() {
        return nivelDeNotificacao;
    }

    public void setNivelDeNotificacao(TipoNivelNotificacao nivelDeNotificacao) {
        this.nivelDeNotificacao = nivelDeNotificacao;
    }

    public BigDecimal getSaidaMaxima() {
        return saidaMaxima;
    }

    public void setSaidaMaxima(BigDecimal saidaMaxima) {
        this.saidaMaxima = saidaMaxima;
    }

    @Override
    public String toString() {
        return estoqueMinimo.toString();
    }

    public enum TipoNivelNotificacao {
        ESTOQUE_MINIMO("Estoque Mínimo"),
        PONTO_REPOSICAO("Ponto de Reposição");

        private String descricao;

        private TipoNivelNotificacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
