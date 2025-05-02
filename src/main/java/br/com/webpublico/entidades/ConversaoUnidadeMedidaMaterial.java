/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoMovimentoMaterial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Conversão de Unidade de Medida")
@Table(name = "CONVERSAOUNIDADEMEDIDAMAT")
public class ConversaoUnidadeMedidaMaterial extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long numero;

    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do Movimento")
    private Date dataMovimento;

    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoMovimentoMaterial situacao;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuario;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeAdministrativa;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Orçamentária")
    private UnidadeOrganizacional unidadeOrcamentaria;

    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Local de Estoque")
    private LocalEstoque localEstoque;

    @Invisivel
    @Etiqueta("Itens")
    @OneToMany(mappedBy = "conversaoUnidadeMedida", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemConversaoUnidadeMedidaMaterial> itens;

    public ConversaoUnidadeMedidaMaterial() {
        this.itens = new ArrayList<>();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
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

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public SituacaoMovimentoMaterial getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMovimentoMaterial situacao) {
        this.situacao = situacao;
    }

    public List<ItemConversaoUnidadeMedidaMaterial> getItens() {
        return itens;
    }

    public void setItens(List<ItemConversaoUnidadeMedidaMaterial> itens) {
        this.itens = itens;
    }

    public void ordernarItem() {
        Iterator<ItemConversaoUnidadeMedidaMaterial> itens = getItens().iterator();
        while (itens.hasNext()) {
            ItemConversaoUnidadeMedidaMaterial proximo = itens.next();
            int i = getItens().indexOf(proximo);
            proximo.setNumeroItem(i + 1);
            Util.adicionarObjetoEmLista(getItens(), proximo);
        }
    }

    @Override
    public String toString() {
        try {
            return numero + " - " + DataUtil.getDataFormatada(dataMovimento) + " - " + localEstoque;
        } catch (NullPointerException e) {
            return "";
        }
    }
}
