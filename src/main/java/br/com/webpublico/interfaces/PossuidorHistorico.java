package br.com.webpublico.interfaces;

import java.util.Date;

/**
 * Created by Buzatto on 08/01/2016.
 */
public interface PossuidorHistorico {

    Date getInicioVigencia();

    Date getFinalVigencia();

    boolean temFinalVigencia();

    boolean temHistorico();

    void criarOrAtualizarAndAssociarHistorico(PossuidorHistorico original);

    void voltarHistorico();
}
