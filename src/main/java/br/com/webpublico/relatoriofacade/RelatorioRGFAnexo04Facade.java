package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.negocios.contabil.RelatorioItemDemonstrativoCalculador;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by mateus on 26/04/18.
 */
@Stateless
public class RelatorioRGFAnexo04Facade extends RelatorioItemDemonstrativoCalculador implements Serializable {
    @EJB
    private RelatorioRREOAnexo03Calculator relatorioRREOAnexo03Calculator;
    @EJB
    private RelatorioRGFAnexo01Facade relatorioRGFAnexo01Facade;

    public BigDecimal recuperarValorTransferencias(Mes mes, Exercicio exercicio) {
        return relatorioRGFAnexo01Facade.recuperarValorTransferencias(mes, exercicio);
    }

    public RelatorioRREOAnexo03Calculator getRelatorioRREOAnexo03Calculator() {
        return relatorioRREOAnexo03Calculator;
    }
}
