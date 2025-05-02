package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemProcessoDeCompra;
import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.RepresentanteLicitacao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 07/03/15
 * Time: 17:01
 * To change this template use File | Settings | File Templates.
 */
public class ArquivoPropostaFornecedor implements Serializable {

    private Date data;
    private Licitacao licitacao;
    private List<ItemProcessoDeCompra> itens;
    private Pessoa fornecedor;
    private RepresentanteLicitacao representante;

    public ArquivoPropostaFornecedor() {
    }

    public List<ItemProcessoDeCompra> getItens() {
        return itens;
    }

    public void setItens(List<ItemProcessoDeCompra> itens) {
        this.itens = itens;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public RepresentanteLicitacao getRepresentante() {
        return representante;
    }

    public void setRepresentante(RepresentanteLicitacao representante) {
        this.representante = representante;
    }
}
