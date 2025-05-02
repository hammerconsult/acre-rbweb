package br.com.webpublico.entidadesauxiliares.rh.portal;

import br.com.webpublico.controle.portaltransparencia.entidades.ServidorPortal;
import br.com.webpublico.entidades.VinculoFP;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by renatoromanini on 03/06/17.
 */
public class VinculoServidorPortal {
    private VinculoFP vinculoFP;
    private ServidorPortal servidorPortal;
    private List<FuncaoGratificadaPortal> funcoes;

    public VinculoServidorPortal(VinculoFP vinculoFP, ServidorPortal servidorPortal) {
        this.vinculoFP = vinculoFP;
        this.servidorPortal = servidorPortal;
        funcoes = Lists.newArrayList();
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public void setServidorPortal(ServidorPortal servidorPortal) {
        this.servidorPortal = servidorPortal;
    }

    public void setFuncoes(List<FuncaoGratificadaPortal> funcoes) {
        this.funcoes = funcoes;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public ServidorPortal getServidorPortal() {
        return servidorPortal;
    }

    public List<FuncaoGratificadaPortal> getFuncoes() {
        return funcoes;
    }
}

