/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.ClassificacaoContaAuxiliar;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author venon
 */
@Entity
@Audited
@Etiqueta("Tipo Conta Auxiliar")
public class TipoContaAuxiliar implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Código")
    private String codigo;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Tabelavel
    @Obrigatorio
    private String chave;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Máscara")
    private String mascara;
    @Tabelavel
    @Obrigatorio
    private String itens;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo de Conta Auxiliar")
    @Enumerated(EnumType.STRING)
    private ClassificacaoContaAuxiliar classificacaoContaAuxiliar;

    @Transient
    private Long criadoEm;

    public TipoContaAuxiliar() {
        criadoEm = System.nanoTime();
    }

    public TipoContaAuxiliar(String codigo, String descricao, String chave, String mascara, String itens) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.chave = chave;
        this.mascara = mascara;
        this.itens = itens;
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getItens() {
        return itens;
    }

    public void setItens(String itens) {
        this.itens = itens;
    }

    public String getMascara() {
        return mascara;
    }

    public void setMascara(String mascara) {
        this.mascara = mascara;
    }

    public ClassificacaoContaAuxiliar getClassificacaoContaAuxiliar() {
        return classificacaoContaAuxiliar;
    }

    public void setClassificacaoContaAuxiliar(ClassificacaoContaAuxiliar classificacaoContaAuxiliar) {
        this.classificacaoContaAuxiliar = classificacaoContaAuxiliar;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
