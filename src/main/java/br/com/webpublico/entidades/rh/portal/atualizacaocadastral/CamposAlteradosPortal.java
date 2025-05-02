package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;

import br.com.webpublico.entidades.Dependente;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.pessoa.enumeration.CamposPessoaDTO;
import br.com.webpublico.pessoa.enumeration.TipoPessoaPortal;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by peixe on 29/08/17.
 */
@Entity
public class CamposAlteradosPortal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PessoaFisica pessoaFisica;
    @Enumerated(EnumType.STRING)
    private CamposPessoaDTO campo;
    private String conteudo;
    private Boolean atualizado;
    @ManyToOne
    private Dependente dependente;
    @Enumerated(EnumType.STRING)
    private TipoPessoaPortal tipoPessoaPortal;

    public CamposAlteradosPortal() {
    }

    public CamposAlteradosPortal(PessoaFisica pessoaFisica, CamposPessoaDTO campo, String conteudo, Boolean atualizado, Dependente dependente, TipoPessoaPortal tipoPessoa) {
        this.pessoaFisica = pessoaFisica;
        this.campo = campo;
        this.conteudo = conteudo;
        this.atualizado = atualizado;
        this.dependente = dependente;
        this.tipoPessoaPortal = tipoPessoa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public CamposPessoaDTO getCampo() {
        return campo;
    }

    public void setCampo(CamposPessoaDTO campo) {
        this.campo = campo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Boolean getAtualizado() {
        return atualizado != null ? atualizado : Boolean.FALSE;
    }

    public void setAtualizado(Boolean atualizado) {
        this.atualizado = atualizado;
    }

    public Dependente getDependente() {
        return dependente;
    }

    public void setDependente(Dependente dependente) {
        this.dependente = dependente;
    }

    public TipoPessoaPortal getTipoPessoaPortal() {
        return tipoPessoaPortal;
    }

    public void setTipoPessoaPortal(TipoPessoaPortal tipoPessoaPortal) {
        this.tipoPessoaPortal = tipoPessoaPortal;
    }
}
