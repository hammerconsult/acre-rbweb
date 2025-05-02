package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.SolicitacaoBaixaPatrimonial;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class ImportacaoDadosSolicitacaoBaixa implements Serializable {

    private List<Bem> bens;
    private SolicitacaoBaixaPatrimonial solicitacaoBaixaPatrimonial;

    public ImportacaoDadosSolicitacaoBaixa() {
        bens = Lists.newArrayList();
    }

    public List<Bem> getBens() {
        return bens;
    }

    public void setBens(List<Bem> bens) {
        this.bens = bens;
    }

    public SolicitacaoBaixaPatrimonial getSolicitacaoBaixaPatrimonial() {
        return solicitacaoBaixaPatrimonial;
    }

    public void setSolicitacaoBaixaPatrimonial(SolicitacaoBaixaPatrimonial solicitacaoBaixaPatrimonial) {
        this.solicitacaoBaixaPatrimonial = solicitacaoBaixaPatrimonial;
    }
}
