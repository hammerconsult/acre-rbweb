/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Item Solicitação Serviço")
public class ItemSolicitacaoServico extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @Etiqueta("Item Solicitação")
    private ItemSolicitacao itemSolicitacao;

    @ManyToOne
    @Etiqueta("Serviço Comprável")
    private ServicoCompravel servicoCompravel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemSolicitacao getItemSolicitacao() {
        return itemSolicitacao;
    }

    public void setItemSolicitacao(ItemSolicitacao itemSolicitacao) {
        this.itemSolicitacao = itemSolicitacao;
    }

    public ServicoCompravel getServicoCompravel() {
        return servicoCompravel;
    }

    public void setServicoCompravel(ServicoCompravel servico) {
        this.servicoCompravel = servico;
    }


    @Override
    public String toString() {
        return itemSolicitacao.getLoteSolicitacaoMaterial().toString() + " Item Nº " + itemSolicitacao.getNumero() + " - " + itemSolicitacao.getDescricao();
    }

    public String getDescricaoServicoCompravel() {
        return servicoCompravel.getDescricao();
    }
}
