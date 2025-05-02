/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Licitação Membro Comissão")
@Table(name = "LICITMEMBCOM")
public class LicitacaoMembroComissao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Licitação")
    private Licitacao licitacao;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Membro Comissão")
    private MembroComissao membroComissao;
    @Invisivel
    @Transient
    private Long criadoEm;

    public LicitacaoMembroComissao() {
        this.criadoEm = System.nanoTime();
    }

    public LicitacaoMembroComissao(Licitacao licitacao, MembroComissao membroComissao) {
        this.criadoEm = System.nanoTime();
        this.licitacao = licitacao;
        this.membroComissao = membroComissao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public MembroComissao getMembroComissao() {
        return membroComissao;
    }

    public void setMembroComissao(MembroComissao membroComissao) {
        this.membroComissao = membroComissao;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return this.membroComissao.getId() + " - " + this.membroComissao.getPessoaFisica().getNome();
    }
}
