package br.com.webpublico.negocios.rh.pccr;

import br.com.webpublico.entidades.rh.pccr.ProgressaoPCCR;
import br.com.webpublico.entidades.rh.pccr.ReferenciaProgressaoPCCR;
import br.com.webpublico.entidades.rh.pccr.ValorProgressaoPCCR;
import br.com.webpublico.negocios.AbstractFacade;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 26/06/14
 * Time: 19:23
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ProgressaoPCCRFacade extends AbstractFacade<ProgressaoPCCR> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PlanoCargosSalariosPCCRFacade planoCargosSalariosPCCRFacade;
    @EJB
    private EnquadramentoPCCRFacade enquadramentoPCCRFacade;


    public ProgressaoPCCRFacade() {
        super(ProgressaoPCCR.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ProgressaoPCCR recuperar(Object id) {
        ProgressaoPCCR p = getEntityManager().find(ProgressaoPCCR.class, id);
        p.getValorProgressaoPCCRs().size();
//        p.getFilhos().size();
        return p;    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void preencherProgressoes() {
        ProgressaoPCCR adm001 = new ProgressaoPCCR();
        adm001.setDescricao("ADM001");
        adm001.setPlanoCargosSalarios(enquadramentoPCCRFacade.findPlanosCargosPCCRVigentes(new Date()).get(0));
        em.persist(adm001);
        gerarValorProgressao(adm001);
    }

    private void gerarValorProgressao(ProgressaoPCCR adm001) {
        List<Interval> periods = getPeriodos();
        List<ReferenciaProgressaoPCCR> letras = getLetras();
        Double valor = 251.70;
        for (ReferenciaProgressaoPCCR letra : letras) {
            valor = valor + 100;
            gerarValorProgressaoItem(periods, adm001, letra, valor);
        }
    }

    private List<ReferenciaProgressaoPCCR> getLetras() {
        List<ReferenciaProgressaoPCCR> letras = new LinkedList<>();
        ReferenciaProgressaoPCCR letraA = new ReferenciaProgressaoPCCR();
        letraA.setLetra("A");
        letraA.setNumero(1);
        em.persist(letraA);
        letras.add(letraA);
//        gerarValorProgressaoItem(periods, adm001, letraA, 300.51);
        ReferenciaProgressaoPCCR letraB = new ReferenciaProgressaoPCCR();
        letraB.setLetra("B");
        letraB.setNumero(2);
        em.persist(letraB);
        letras.add(letraB);
        ReferenciaProgressaoPCCR letraC = new ReferenciaProgressaoPCCR();
        letraC.setLetra("C");
        letraC.setNumero(3);
        em.persist(letraC);
        letras.add(letraC);
        ReferenciaProgressaoPCCR letraD = new ReferenciaProgressaoPCCR();
        letraD.setLetra("D");
        letraD.setNumero(4);
        em.persist(letraD);
        letras.add(letraD);
        ReferenciaProgressaoPCCR letraE = new ReferenciaProgressaoPCCR();
        letraE.setLetra("E");
        letraE.setNumero(5);
        em.persist(letraE);
        letras.add(letraE);
        return letras;  //To change body of created methods use File | Settings | File Templates.
    }

    private List<Interval> getPeriodos() {
        List<Interval> periods = new LinkedList<>();
        Interval p = new Interval(new DateTime(1980, 1, 1, 1, 1), new DateTime(1988, 5, 10, 1, 1));
        periods.add(p);
        Interval p1 = new Interval(new DateTime(1988, 5, 11, 1, 1), new DateTime(1995, 8, 15, 1, 1));
        periods.add(p1);
        Interval p2 = new Interval(new DateTime(1995, 8, 16, 1, 1), new DateTime(2005, 3, 31, 1, 1));
        periods.add(p2);
        Interval p3 = new Interval(new DateTime(2005, 4, 1, 1, 1), new DateTime(2015, 12, 31, 1, 1));
        periods.add(p3);
        return periods;
    }

    private void gerarValorProgressaoItem(List<Interval> intervalos, ProgressaoPCCR adm001, ReferenciaProgressaoPCCR letra, double valor) {
        for (Interval p : intervalos) {
            ValorProgressaoPCCR valor1 = new ValorProgressaoPCCR();
            valor1.setInicioVigencia(p.getStart().toDate());
            valor1.setFinalVigencia(p.getEnd().toDate());
            valor1.setProgressaoPCCR(adm001);
            valor1.setReferenciaProgressaoPCCR(letra);
            valor = valor + ((valor + (letra.getNumero() * 5)) * 0.07);
            valor1.setValor(BigDecimal.valueOf(valor));
            adm001.getValorProgressaoPCCRs().add(valor1);
            em.merge(adm001);
        }
    }

}
