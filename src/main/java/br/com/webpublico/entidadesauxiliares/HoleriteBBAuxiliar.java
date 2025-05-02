package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;

import java.util.List;

/**
 * Created by carlos on 02/10/15.
 */
public class HoleriteBBAuxiliar {

    private VinculoFP vinculoFP;
    private ProgressaoPCS progressaoPCS;
    private EnquadramentoFuncional enquadramentoFuncional;
    private List<FichaFinanceiraFP> fichaFinanceiraFPs;
    private LotacaoFuncional lotacaoFuncional;

    public HoleriteBBAuxiliar() {
    }


    public HoleriteBBAuxiliar(VinculoFP vinculoFP, EnquadramentoFuncional enquadramentoFuncional , LotacaoFuncional lotacaoFuncional) {
        this.enquadramentoFuncional = enquadramentoFuncional;
        this.fichaFinanceiraFPs = vinculoFP.getFichasFinanceiraFP();
        this.progressaoPCS = enquadramentoFuncional.getProgressaoPCS();
        this.vinculoFP = vinculoFP;
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public EnquadramentoFuncional getEnquadramentoFuncional() {
        return enquadramentoFuncional;
    }

    public void setEnquadramentoFuncional(EnquadramentoFuncional enquadramentoFuncional) {
        this.enquadramentoFuncional = enquadramentoFuncional;
    }

    public List<FichaFinanceiraFP> getFichaFinanceiraFPs() {
        return fichaFinanceiraFPs;
    }

    public void setFichaFinanceiraFPs(List<FichaFinanceiraFP> fichaFinanceiraFPs) {
        this.fichaFinanceiraFPs = fichaFinanceiraFPs;
    }

    public ProgressaoPCS getProgressaoPCS() {
        return progressaoPCS;
    }

    public void setProgressaoPCS(ProgressaoPCS progressaoPCS) {
        this.progressaoPCS = progressaoPCS;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }
}
