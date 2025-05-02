package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.ParametroEvento;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;


public abstract class SuperEntidadeContabilGerarContaAuxiliar extends SuperEntidade {

    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        return null;
    }
}
