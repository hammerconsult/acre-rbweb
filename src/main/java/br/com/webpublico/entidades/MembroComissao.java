/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.AtribuicaoComissao;
import br.com.webpublico.enums.NaturezaDoCargo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author renato
 */
@Entity

@Audited
@Etiqueta("Membro da Comissão")
@GrupoDiagrama(nome = "Licitacao")
public class MembroComissao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Membro")
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    @Pesquisavel
    private PessoaFisica pessoaFisica;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Atribuição da Comissão")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private AtribuicaoComissao atribuicaoComissao;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Natureza do Cargo")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private NaturezaDoCargo naturezaDoCargo;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Comissão")
    @ManyToOne
    @Pesquisavel
    private Comissao comissao;
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Exonerado Em")
    @Pesquisavel
    private Date exoneradoEm;

    public MembroComissao() {
    }

    public MembroComissao(Comissao comissao) {
        setComissao(comissao);
    }

    public Comissao getComissao() {
        return comissao;
    }

    public void setComissao(Comissao comissao) {
        this.comissao = comissao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtribuicaoComissao getAtribuicaoComissao() {
        return atribuicaoComissao;
    }

    public void setAtribuicaoComissao(AtribuicaoComissao atribuicaoComissao) {
        this.atribuicaoComissao = atribuicaoComissao;
    }

    public NaturezaDoCargo getNaturezaDoCargo() {
        return naturezaDoCargo;
    }

    public void setNaturezaDoCargo(NaturezaDoCargo naturezaDoCargo) {
        this.naturezaDoCargo = naturezaDoCargo;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Date getExoneradoEm() {
        return exoneradoEm;
    }

    public void setExoneradoEm(Date exoneradoEm) {
        this.exoneradoEm = exoneradoEm;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public String toString() {
        return this.pessoaFisica.getNome();
    }

    public boolean temExoneradoEm() {
        return exoneradoEm != null;
    }
}
