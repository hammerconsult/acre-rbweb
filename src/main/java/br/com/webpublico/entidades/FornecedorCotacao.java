package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.ContratoCotacao;
import br.com.webpublico.entidadesauxiliares.ContratoCotacaoObjetoCompra;
import br.com.webpublico.enums.OrigemCotacao;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Novo
 * Date: 25/06/15
 * Time: 08:56
 * To change this template use File | Settings | File Templates.
 */
public class FornecedorCotacao implements Serializable {

    private Pessoa fornecedor;

    private Contrato contrato;

    private UnidadeExterna unidadeExterna;

    private OrigemCotacao origemCotacao;

    private ContratoCotacao contratoCotacao;

    private String observacao;

    public ContratoCotacao getContratoCotacao() {
        return contratoCotacao;
    }

    public void setContratoCotacao(ContratoCotacao contratoCotacao) {
        this.contratoCotacao = contratoCotacao;
    }

    public FornecedorCotacao() {
        this.origemCotacao = OrigemCotacao.FORNECEDOR;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public UnidadeExterna getUnidadeExterna() {
        return unidadeExterna;
    }

    public void setUnidadeExterna(UnidadeExterna unidadeExterna) {
        this.unidadeExterna = unidadeExterna;
    }

    public OrigemCotacao getOrigemCotacao() {
        return origemCotacao;
    }

    public void setOrigemCotacao(OrigemCotacao origemCotacao) {
        this.origemCotacao = origemCotacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public String toString() {
        String retorno = "";
        if (fornecedor != null) {
            retorno = fornecedor.getNomeCpfCnpj();
        }
        return retorno;
    }
}
