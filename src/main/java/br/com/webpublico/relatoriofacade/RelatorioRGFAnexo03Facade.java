package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.negocios.contabil.RelatorioItemDemonstrativoCalculador;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by mateus on 25/04/18.
 */
@Stateless
public class RelatorioRGFAnexo03Facade extends RelatorioItemDemonstrativoCalculador implements Serializable {

    @EJB
    private RelatorioRGFAnexo01Facade relatorioRGFAnexo01Facade;
    @EJB
    private RelatorioRREOAnexo03Calculator relatorioRREOAnexo03Calculator;

    public BigDecimal recuperarValorTransferencias(Mes mes, Exercicio exercicio) {
        return relatorioRGFAnexo01Facade.recuperarValorTransferencias(mes, exercicio);
    }

    public Exercicio buscarExercicioAnterior() {
        return getExercicioFacade().getExercicioPorAno(getSistemaFacade().getExercicioCorrente().getAno() - 1);
    }

    public RelatorioRREOAnexo03Calculator getRelatorioRREOAnexo03Calculator() {
        return relatorioRREOAnexo03Calculator;
    }

    public RelatorioRGFAnexo01Facade getRelatorioRGFAnexo01Facade() {
        return relatorioRGFAnexo01Facade;
    }
}
