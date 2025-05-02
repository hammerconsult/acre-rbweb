package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.Alvara;
import br.com.webpublico.entidades.CadastroEconomico;

public interface CalculoAlvara {

    Alvara getAlvara();

    void setEmitir(Boolean emitir);

    Boolean getEmitir();

    CadastroEconomico getCadastroEconomico();

}
