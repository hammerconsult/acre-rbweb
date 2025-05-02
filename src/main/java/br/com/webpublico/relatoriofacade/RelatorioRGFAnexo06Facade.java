package br.com.webpublico.relatoriofacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;

/**
 * Created by mateus on 27/04/18.
 */
@Stateless
public class RelatorioRGFAnexo06Facade extends ItemDemonstrativoCalculator implements Serializable {

    @EJB
    private RelatorioRGFAnexo01Facade relatorioRGFAnexo01Facade;
    @EJB
    private RelatorioRGFAnexo03Facade relatorioRGFAnexo03Facade;
    @EJB
    private RelatorioRGFAnexo04Facade relatorioRGFAnexo04Facade;
    @EJB
    private RelatorioRGFAnexo05Facade relatorioRGFAnexo05Facade;
    @EJB
    private RelatorioRREOAnexo03Calculator relatorioRREOAnexo03Calculator;
    @EJB
    private RelatorioDemonstrativoDisponibilidadeRecursoFacade relatorioDDRFacade;

    public RelatorioRGFAnexo01Facade getRelatorioRGFAnexo01Facade() {
        return relatorioRGFAnexo01Facade;
    }

    public void setRelatorioRGFAnexo01Facade(RelatorioRGFAnexo01Facade relatorioRGFAnexo01Facade) {
        this.relatorioRGFAnexo01Facade = relatorioRGFAnexo01Facade;
    }

    public RelatorioRGFAnexo03Facade getRelatorioRGFAnexo03Facade() {
        return relatorioRGFAnexo03Facade;
    }

    public void setRelatorioRGFAnexo03Facade(RelatorioRGFAnexo03Facade relatorioRGFAnexo03Facade) {
        this.relatorioRGFAnexo03Facade = relatorioRGFAnexo03Facade;
    }

    public RelatorioRGFAnexo04Facade getRelatorioRGFAnexo04Facade() {
        return relatorioRGFAnexo04Facade;
    }

    public void setRelatorioRGFAnexo04Facade(RelatorioRGFAnexo04Facade relatorioRGFAnexo04Facade) {
        this.relatorioRGFAnexo04Facade = relatorioRGFAnexo04Facade;
    }

    public RelatorioRGFAnexo05Facade getRelatorioRGFAnexo05Facade() {
        return relatorioRGFAnexo05Facade;
    }

    public void setRelatorioRGFAnexo05Facade(RelatorioRGFAnexo05Facade relatorioRGFAnexo05Facade) {
        this.relatorioRGFAnexo05Facade = relatorioRGFAnexo05Facade;
    }

    public RelatorioRREOAnexo03Calculator getRelatorioRREOAnexo03Calculator() {
        return relatorioRREOAnexo03Calculator;
    }

    public void setRelatorioRREOAnexo03Calculator(RelatorioRREOAnexo03Calculator relatorioRREOAnexo03Calculator) {
        this.relatorioRREOAnexo03Calculator = relatorioRREOAnexo03Calculator;
    }

    public RelatorioDemonstrativoDisponibilidadeRecursoFacade getRelatorioDDRFacade() {
        return relatorioDDRFacade;
    }

    public void setRelatorioDDRFacade(RelatorioDemonstrativoDisponibilidadeRecursoFacade relatorioDDRFacade) {
        this.relatorioDDRFacade = relatorioDDRFacade;
    }
}
