package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ArquivoDesifRegistro0300 extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private Long linha;
    @ManyToOne
    private ArquivoDesif arquivoDesif;
    @ManyToOne
    private ProdutoServicoBancario produtoServicoBancario;
    private String descricaoComplementar;
    private String conta;
    private String desdobramento;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLinha() {
        return linha;
    }

    public void setLinha(Long linha) {
        this.linha = linha;
    }

    public ArquivoDesif getArquivoDesif() {
        return arquivoDesif;
    }

    public void setArquivoDesif(ArquivoDesif arquivoDesif) {
        this.arquivoDesif = arquivoDesif;
    }

    public ProdutoServicoBancario getProdutoServicoBancario() {
        return produtoServicoBancario;
    }

    public void setProdutoServicoBancario(ProdutoServicoBancario produtoServicoBancario) {
        this.produtoServicoBancario = produtoServicoBancario;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(String desdobramento) {
        this.desdobramento = desdobramento;
    }
}
