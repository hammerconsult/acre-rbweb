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
import java.util.ArrayList;
import java.util.List;

@GrupoDiagrama(nome = "Administrativo")
@Entity

@Audited
@Etiqueta("Veículo de Publicação")
public class VeiculoDePublicacao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Nome")
    @Pesquisavel
    private String nome;
    @OneToMany(mappedBy = "veiculoDePublicacao", cascade = CascadeType.ALL)
    private List<ContratoVeiculoDePublicacao> listaContrato;

    public VeiculoDePublicacao() {
        listaContrato = new ArrayList<ContratoVeiculoDePublicacao>();
    }

    public VeiculoDePublicacao(String nome, List<ContratoVeiculoDePublicacao> listaContrato) {
        this.nome = nome;
        this.listaContrato = listaContrato;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<ContratoVeiculoDePublicacao> getListaContrato() {
        return listaContrato;
    }

    public void setListaContrato(List<ContratoVeiculoDePublicacao> listaContrato) {
        this.listaContrato = listaContrato;
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
