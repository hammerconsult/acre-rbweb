package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.VinculoFP;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author peixe on 30/03/2016  09:21.
 */
public class ResultadoLancamento extends SuperEntidade implements Serializable {

    private VinculoFP vinculoFP;
    private List<ItemResultadoLancamento> itensResultados;


    public ResultadoLancamento() {
        itensResultados = new LinkedList<>();
    }

    @Override
    public Long getId() {
        return null;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public List<ItemResultadoLancamento> getItensResultados() {
        return itensResultados;
    }

    public void setItensResultados(List<ItemResultadoLancamento> itensResultados) {
        this.itensResultados = itensResultados;
    }
}
