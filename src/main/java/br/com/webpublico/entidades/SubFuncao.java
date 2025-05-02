/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("Subfunção")
public class SubFuncao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @NotNull
    @Etiqueta("Código")
    @Pesquisavel
    @Obrigatorio
    private String codigo;
    @Tabelavel
    @Etiqueta("Descrição")
    @Pesquisavel
    @Obrigatorio
    private String descricao;
    @Tabelavel
    @Etiqueta("Função")
    @ManyToOne
    @Pesquisavel
    private Funcao funcao;
    @Etiqueta("Exibir no RREO - Anexo 2?")
    private Boolean exibirNoAnexo2;
    @Transient
    private Long criadoEm;

    public SubFuncao() {
        criadoEm = System.nanoTime();
        exibirNoAnexo2 = true;
    }

    public SubFuncao(String codigo, String descricao, Funcao funcao) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.funcao = funcao;
        criadoEm = System.nanoTime();
        exibirNoAnexo2 = true;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getExibirNoAnexo2() {
        return exibirNoAnexo2 == null ? Boolean.FALSE : exibirNoAnexo2;
    }

    public void setExibirNoAnexo2(Boolean exibirNoAnexo2) {
        this.exibirNoAnexo2 = exibirNoAnexo2;
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
        if (codigo != null) {
            return codigo + " - " + descricao;
        } else {
            return descricao;
        }
    }

    public String toStringAutoComplete() {
        String retorno = toString();
        return retorno.length() > 68 ? retorno.substring(0, 65) + "..." : retorno;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }
}
