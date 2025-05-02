/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Uma produção representa um processo fabril, no qual um ou vários
 * produtos resultantes são gerados.
 *
 * @author cheles
 */
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Produção Própria")
@Audited
@Entity
public class Producao extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Data da Produção")
    @Temporal(TemporalType.DATE)
    private Date dataProducao;

    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Unidade Organizacional")
    @Tabelavel
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta("Itens Produzidos")
    @OneToMany(mappedBy = "producao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemProduzido> itemsProduzidos;

    public Producao() {
        this.itemsProduzidos = new ArrayList<>();
    }

    public Date getDataProducao() {
        return dataProducao;
    }

    public void setDataProducao(Date dataProducao) {
        this.dataProducao = dataProducao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public List<ItemProduzido> getItemsProduzidos() {
        return itemsProduzidos;
    }

    public void setItemsProduzidos(List<ItemProduzido> itemsProduzidos) {
        this.itemsProduzidos = itemsProduzidos;
    }

    //este método só retorna uma entrada depois que a produção foi gerada
    public EntradaProducao getEntradaGerada() {
        return (EntradaProducao) this.getItemsProduzidos().get(0).getItemEntradaMaterial().getEntradaMaterial();
    }

    //este método só retorna uma saida depois que a produção foi gerada
    public SaidaProducao getSaidaGerada() {
        return (SaidaProducao) this.getItemsProduzidos().get(0).getInsumosAplicados().get(0).getInsumoProducao().getItemSaidaMaterial().getSaidaMaterial();
    }

    public String valorTotalItensProduzidos() {
        BigDecimal total = BigDecimal.ZERO;
        if (!itemsProduzidos.isEmpty()) {
            for (ItemProduzido itemsProduzido : itemsProduzidos) {
                total = total.add(itemsProduzido.getValor().multiply(itemsProduzido.getQuantidade()));
            }
        }
        return Util.formataValor(total);
    }

    @Override
    public String toString() {
        return "Produção";
    }
}
