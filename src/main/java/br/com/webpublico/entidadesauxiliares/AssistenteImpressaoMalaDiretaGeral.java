package br.com.webpublico.entidadesauxiliares;


import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DetailProcessAsync;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class AssistenteImpressaoMalaDiretaGeral extends AssistenteBarraProgresso {

    private ImprimeDAM imprimeDAM;
    private Long idMala;
    private List<ImpressaoMalaDiretaGeral> itens;
    private String pastaMalaDireta;
    private int numFuture;

    public AssistenteImpressaoMalaDiretaGeral(ImprimeDAM imprimeDAM,
                                              Long idMala,
                                              List<ImpressaoMalaDiretaGeral> itens,
                                              UsuarioSistema usuario,
                                              String pastaMalaDireta,
                                              int numFuture) {
        this.imprimeDAM = imprimeDAM;
        this.idMala = idMala;
        this.itens = itens;
        this.pastaMalaDireta = pastaMalaDireta;
        this.numFuture = numFuture;
        this.setTotal(itens.size());
        this.setUsuarioSistema(usuario);
    }

    public ImprimeDAM getImprimeDAM() {
        return imprimeDAM;
    }

    public void setImprimeDAM(ImprimeDAM imprimeDAM) {
        this.imprimeDAM = imprimeDAM;
    }

    public Long getIdMala() {
        return idMala;
    }

    public void setIdMala(Long idMala) {
        this.idMala = idMala;
    }

    public List<ImpressaoMalaDiretaGeral> getItens() {
        return itens;
    }

    public void setItens(List<ImpressaoMalaDiretaGeral> itens) {
        this.itens = itens;
    }

    @Override
    public String getDescricao() {
        return "Impressão de Mala Direta Geral ID [" + idMala + "] Numero Future [" + numFuture + "]";
    }

    public String getPastaMalaDireta() {
        return pastaMalaDireta;
    }

    public void setPastaMalaDireta(String pastaMalaDireta) {
        this.pastaMalaDireta = pastaMalaDireta;
    }

    public int getNumFuture() {
        return numFuture;
    }

    public void setNumFuture(int numFuture) {
        this.numFuture = numFuture;
    }
}
