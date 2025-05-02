package br.com.webpublico.relatoriofacade;

import br.com.webpublico.negocios.ItemDemonstrativoFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;

/**
 * Created by Mateus on 06/10/2014.
 */
@Stateless
public class RelatorioRGFAnexo06Calculator2017 implements Serializable {

    @EJB
    private RelatorioRGFAnexo01Calculator2017 relatorioRGFAnexo01Calculator2017;
    @EJB
    private RelatorioRGFAnexo03Calculator2017 relatorioRGFAnexo03Calculator2017;
    @EJB
    private RelatorioRGFAnexo04Calculator2017 relatorioRGFAnexo04Calculator2017;
    @EJB
    private RelatorioRGFAnexo05Calculator2017 relatorioRGFAnexo05Calculator2017;
    @EJB
    private RelatorioRREOAnexo05Calculator relatorioRREOAnexo05Calculator;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private RelatorioRREOAnexo03Calculator relatorioRREOAnexo03Calculator;
    @EJB
    private ItemDemonstrativoCalculator itemDemonstrativoCalculator;
    @EJB
    private RelatorioDemonstrativoDisponibilidadeRecursoFacade relatorioDemonstrativoDisponibilidadeRecursoFacade;

    public RelatorioRGFAnexo06Calculator2017() {
    }

    public RelatorioRGFAnexo01Calculator2017 getRelatorioRGFAnexo01Calculator2017() {
        return relatorioRGFAnexo01Calculator2017;
    }

    public RelatorioRGFAnexo03Calculator2017 getRelatorioRGFAnexo03Calculator2017() {
        return relatorioRGFAnexo03Calculator2017;
    }

    public RelatorioRGFAnexo04Calculator2017 getRelatorioRGFAnexo04Calculator2017() {
        return relatorioRGFAnexo04Calculator2017;
    }

    public RelatorioRGFAnexo05Calculator2017 getRelatorioRGFAnexo05Calculator2017() {
        return relatorioRGFAnexo05Calculator2017;
    }

    public RelatorioRREOAnexo05Calculator getRelatorioRREOAnexo05Calculator() {
        return relatorioRREOAnexo05Calculator;
    }

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }

    public RelatorioRREOAnexo03Calculator getRelatorioRREOAnexo03Calculator() {
        return relatorioRREOAnexo03Calculator;
    }

    public ItemDemonstrativoCalculator getItemDemonstrativoCalculator() {
        return itemDemonstrativoCalculator;
    }

    public RelatorioDemonstrativoDisponibilidadeRecursoFacade getRelatorioDemonstrativoDisponibilidadeRecursoFacade() {
        return relatorioDemonstrativoDisponibilidadeRecursoFacade;
    }
}
