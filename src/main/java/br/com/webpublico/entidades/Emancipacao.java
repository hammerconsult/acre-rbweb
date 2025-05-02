/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import java.util.Date;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroUnico")
@Entity
@Audited

public class Emancipacao extends DocumentoPessoal {

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Emissão")
    private Date dataemissao;
    @Column
    @Etiqueta("Número")
    @Obrigatorio
    @Pesquisavel
    private String numero;
    @Column
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;

    public Emancipacao() {
    }

    public Emancipacao(PessoaFisica pessoaFisica, String numero, String descricao) {
        this.setPessoaFisica(pessoaFisica);
        this.numero = numero;
        this.descricao = descricao;
    }

    public Date getDataemissao() {
        return dataemissao;
    }

    public void setDataemissao(Date dataemissao) {
        this.dataemissao = dataemissao;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
