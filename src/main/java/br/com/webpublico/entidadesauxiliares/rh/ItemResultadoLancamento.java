package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.LancamentoFP;
import br.com.webpublico.entidades.SuperEntidade;

/**
 * @Author peixe on 30/03/2016  09:22.
 */
public class ItemResultadoLancamento extends SuperEntidade {
    private LancamentoFP lancamentoFPWeb;
    private LancamentoFP lancamentoFPTurmalina;

    public LancamentoFP getLancamentoFPWeb() {
        return lancamentoFPWeb;
    }

    public void setLancamentoFPWeb(LancamentoFP lancamentoFPWeb) {
        this.lancamentoFPWeb = lancamentoFPWeb;
    }

    public LancamentoFP getLancamentoFPTurmalina() {
        return lancamentoFPTurmalina;
    }

    public void setLancamentoFPTurmalina(LancamentoFP lancamentoFPTurmalina) {
        this.lancamentoFPTurmalina = lancamentoFPTurmalina;
    }

    @Override
    public Long getId() {
        return null;
    }
}
