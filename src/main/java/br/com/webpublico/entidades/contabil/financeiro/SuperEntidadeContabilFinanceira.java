package br.com.webpublico.entidades.contabil.financeiro;

import br.com.webpublico.entidades.ParametroEvento;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.interfaces.EntidadeContabil;

import java.io.Serializable;

/**
 * Created by renatoromanini on 26/09/2017.
 */
public abstract class SuperEntidadeContabilFinanceira extends SuperEntidadeContabilGerarContaAuxiliar implements Serializable, EntidadeContabil {
    private ParametroEvento.ComplementoId complementoId;

    public ParametroEvento.ComplementoId getComplementoId() {
        if(complementoId == null){
            complementoId = ParametroEvento.ComplementoId.NORMAL;
        }
        return complementoId;
    }

    public void setComplementoId(ParametroEvento.ComplementoId complementoId) {
        this.complementoId = complementoId;
    }
}

