/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author boy
 */
@Entity

@Audited
@Etiqueta("Associação")
@GrupoDiagrama(nome = "RecursosHumanos")
public class Associacao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Pessoa")
    private PessoaJuridica pessoa;
    @Etiqueta("Itens")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "associacao")
    private List<ItemValorAssociacao> itensValoresAssociacoes;

    public Associacao() {
        setItensValoresAssociacoes(new ArrayList<ItemValorAssociacao>());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemValorAssociacao> getItensValoresAssociacoes() {
        return itensValoresAssociacoes;
    }

    public void setItensValoresAssociacoes(List<ItemValorAssociacao> itensValoresAssociacoes) {
        this.itensValoresAssociacoes = itensValoresAssociacoes;
    }

    public PessoaJuridica getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaJuridica pessoa) {
        this.pessoa = pessoa;
    }

    @Override
    public String toString() {
        return getPessoa().getNomeFantasia();
    }

    public boolean temPeloMenosItemValorAdicionado() {
        return !this.getItensValoresAssociacoes().isEmpty();
    }
}
