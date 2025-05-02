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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Entrada de Materiais por Incorporação")
@Table(name = "ENTRADAINCORPORACAO")
public class EntradaIncorporacaoMaterial extends EntradaMaterial {

    @Obrigatorio
    @Etiqueta("Pessoa")
    @ManyToOne
    private Pessoa pessoa;

    @Tabelavel
    @Etiqueta("Pessoa")
    @Pesquisavel
    @Transient
    private String descricaoPessoa;

    @Tabelavel
    @Etiqueta("Unidade Admnistrativa")
    @Pesquisavel
    @Transient
    private String descricaoUnidade;

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getDescricaoPessoa() {
        return descricaoPessoa;
    }

    public void setDescricaoPessoa(String descricaoPessoa) {
        this.descricaoPessoa = descricaoPessoa;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }
}
