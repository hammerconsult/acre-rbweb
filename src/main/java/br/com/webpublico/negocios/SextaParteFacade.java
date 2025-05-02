package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClasseAfastamento;
import br.com.webpublico.enums.TipoFalta;
import br.com.webpublico.singletons.SingletonGeradorCodigoRH;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 15/10/13
 * Time: 17:30
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SextaParteFacade extends AbstractFacade<SextaParte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TipoSextaParteFacade tipoSextaParteFacade;
    @EJB
    private BaseFPFacade baseFPFacade;
    @EJB
    private SingletonGeradorCodigoRH singletonGeradorCodigoRH;

    /**
     * Deve ser executado no construtor da subclasse para indicar qual é a
     * classe da entidade
     * <p/>
     * param classe Classe da entidade
     */
    public SextaParteFacade() {
        super(SextaParte.class);
    }

    public SingletonGeradorCodigoRH getSingletonGeradorCodigoRH() {
        return singletonGeradorCodigoRH;
    }

    public void setSingletonGeradorCodigoRH(SingletonGeradorCodigoRH singletonGeradorCodigoRH) {
        this.singletonGeradorCodigoRH = singletonGeradorCodigoRH;
    }

    public BaseFPFacade getBaseFPFacade() {
        return baseFPFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public SextaParte recuperar(Object id) {
        SextaParte sextaParte = super.recuperar(id);
        Hibernate.initialize(sextaParte.getAverbacaoTempoServicoList());
        Hibernate.initialize(sextaParte.getFaltasList());
        Hibernate.initialize(sextaParte.getAfastamentoList());
        Hibernate.initialize(sextaParte.getPeriodoExcludenteList());
        return sextaParte;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SextaParte> getServidoresDireitoSextaParte(DateTime data, String intervaloMeses, VinculoFP vinculoFP) {
        String sql = " select vinculo.id from contratofp contrato inner join vinculofp vinculo on vinculo.id = contrato.id inner join tiporegime tipo on tipo.id =contrato.tiporegime_id \n" +
            "                 where tipo.codigo = 2 and :data between vinculo.inicioVigencia and coalesce(vinculo.finalvigencia,:data) and extract (month from coalesce(vinculo.finalVigencia, :data)) in (" + intervaloMeses + ") and contrato.id not in (select vinculofp_id from SextaParte)";
//                "                 and \n" +
//                "  ((coalesce(vinculo.finalvigencia, :data) - coalesce(vinculo.iniciovigencia,:data))\n" +
//                "  + (select sum(averba.finalvigencia - averba.iniciovigencia) from averbacaotemposervico averba where averba.contratofp_id = vinculo.id \n" +
//                "  and extract (month from coalesce(averba.finalvigencia, :data)) in ("+intervaloMeses+") group by averba.contratofp_id)) /365 >= 25 ";
        if (vinculoFP != null) {
            sql += " and vinculo.id = :vinculo";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("data", data.toDate(), TemporalType.DATE);
        if (vinculoFP != null) {
            q.setParameter("vinculo", vinculoFP.getId());
        }
        List<BigDecimal> obj = q.getResultList();
        List<SextaParte> sextas = new ArrayList<>();
        for (BigDecimal ob : obj) {
            BigDecimal idContrato = (BigDecimal) ob;
//            BigDecimal tempo = (BigDecimal) ob[1];
            ContratoFP contrato = em.find(ContratoFP.class, idContrato.longValue());
            DateTime inicioContrato = new DateTime(contrato.getInicioVigencia());
            DateTime inicio = new DateTime(contrato.getInicioVigencia());
            Period period = new Period(inicio, new DateTime(contrato.getFinalVigencia() == null ? new Date() : contrato.getFinalVigencia()));
            int d = Days.daysBetween(new DateTime(inicio), new DateTime(contrato.getFinalVigencia() == null ? new Date() : contrato.getFinalVigencia())).getDays();
            inicio = inicio.plus(d);
            int totalDiasDescontar = 0;
            int diasTotais = 0;
            diasTotais += d;
            Query qAberbacao = em.createQuery("select a from AverbacaoTempoServico a where a.contratoFP.id = :contrato");
            qAberbacao.setParameter("contrato", idContrato.longValue());
            List<AverbacaoTempoServico> averbaList = qAberbacao.getResultList();
            for (AverbacaoTempoServico a : averbaList) {

                if (a.getInicioVigencia() != null && a.getFinalVigencia() != null) {
                    int dias = Days.daysBetween(new DateTime(a.getInicioVigencia()), new DateTime(a.getFinalVigencia())).getDays() + 1;
                    inicio = inicio.plus(dias);
                    diasTotais += dias;
                }
            }

            DateTime dateTime = inicioContrato.plusYears(25);


            Query qFaltas = em.createQuery("from Faltas a where a.contratoFP = :contrato and a.tipoFalta = :tipo");
            qFaltas.setParameter("contrato", contrato);
//            qFaltas.setParameter("data", new Date());
            qFaltas.setParameter("tipo", TipoFalta.FALTA_INJUSTIFICADA);
            List<Faltas> faltasList = qFaltas.getResultList();
            for (Faltas a : faltasList) {
                if (a.getInicio() != null && a.getFim() != null) {
                    int dias = Days.daysBetween(new DateTime(a.getInicio()), new DateTime(a.getFim())).getDays() + 1;
                    inicio = inicio.minusDays(dias);
                    totalDiasDescontar += dias;
                }
            }
            Query qAfastamento = em.createQuery("from Afastamento a where a.contratoFP = :contrato and a.tipoAfastamento.classeAfastamento <> :tipo and a.tipoAfastamento.descontarTempoServicoEfetivo is true");
            qAfastamento.setParameter("contrato", contrato);
//            qAfastamento.setParameter("data", new Date());
            qAfastamento.setParameter("tipo", ClasseAfastamento.FALTA_JUSTIFICADA);
            List<Afastamento> afastamentosList = qAfastamento.getResultList();
            for (Afastamento a : afastamentosList) {
                if (a.getInicio() != null && a.getTermino() != null) {
                    int dias = Days.daysBetween(new DateTime(a.getInicio()), new DateTime(a.getTermino())).getDays() + 1;
                    inicio = inicio.minusDays(dias);
                    totalDiasDescontar += dias;
                    if (a.getCarencia() != null) {
                        inicio = inicio.plusDays(a.getCarencia());
                        totalDiasDescontar -= a.getCarencia();
                    }
                }
            }
            diasTotais -= totalDiasDescontar;
//            //System.out.println("dias totais: " + diasTotais);
            if ((diasTotais / 365) >= 25) {
//                DateTime dateTime = inicioContrato;
                dateTime = inicioContrato.plusYears(25);
//                logger.debug("Valor data com 25 anos: " + dateTime);
                dateTime = dateTime.minusDays(totalDiasDescontar);
//                logger.debug("Valor data com 25 anos menos faltas e afastamentos: " + dateTime);
                SextaParte sextaParte = new SextaParte(contrato, (diasTotais / 365));
                sextaParte.setInicioVigencia(dateTime.toDate());
                sextas.add(sextaParte);
//                logger.debug("ids recuperados: " + contrato + " tempo em anos: " + tempo + " inicio da vigencia " + dateTime + " total de anos: " + Years.yearsBetween(inicioContrato, inicio).getYears());
            }
        }
        sextas.addAll(buscaAposentadosComSextaParte(data));
        sextas.addAll(buscaPensionistasComSextaParte(data));
        logger.debug("Foram gerados " + sextas.size() + " registros para a sexta parte(cálculo do evento 122).");
        return sextas;
    }

    private List<SextaParte> buscaAposentadosComSextaParte(DateTime data) {
        logger.debug("iniciando aposentados da sexta parte");
        List<SextaParte> sextaPartes = new ArrayList<>();
        String sql = "select distinct apo.id from vinculofp vinculo inner join  aposentadoria apo on apo.id = vinculo.id\n" +
            "inner join fichafinanceirafp ficha on ficha.vinculofp_id = vinculo.id\n" +
            "inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id\n" +
            "inner join eventofp evento on evento.id = item.eventofp_id where :data between vinculo.inicioVigencia and coalesce(vinculo.finalvigencia,:data) and evento.codigo = 122  and vinculo.id not in (select vinculofp_id from SextaParte) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", data.toDate());
        if (!q.getResultList().isEmpty()) {
            for (BigDecimal bd : (List<BigDecimal>) q.getResultList()) {
                Aposentadoria ap = em.find(Aposentadoria.class, bd.longValue());
                SextaParte sextaParte = new SextaParte();
                sextaParte.setInicioVigencia(ap.getInicioVigencia());
                sextaParte.setTemDireito(true);
                sextaParte.setVinculoFP(ap);
                sextaPartes.add(sextaParte);
            }
        }

        return sextaPartes;
    }

    private List<SextaParte> buscaPensionistasComSextaParte(DateTime data) {
        logger.debug("iniciando pensionista da sexta parte");
        List<SextaParte> sextaPartes = new ArrayList<>();
        String sql = "select distinct pen.id from vinculofp vinculo inner join  pensionista pen on pen.id = vinculo.id\n" +
            "inner join fichafinanceirafp ficha on ficha.vinculofp_id = vinculo.id\n" +
            "inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id\n" +
            "inner join eventofp evento on evento.id = item.eventofp_id where :data between vinculo.inicioVigencia and coalesce(vinculo.finalvigencia,:data) and evento.codigo = 122  and vinculo.id not in (select vinculofp_id from SextaParte) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", data.toDate());
        if (!q.getResultList().isEmpty()) {
            for (BigDecimal bd : (List<BigDecimal>) q.getResultList()) {
                Pensionista ap = em.find(Pensionista.class, bd.longValue());
                SextaParte sextaParte = new SextaParte();
                sextaParte.setInicioVigencia(ap.getInicioVigencia());
                sextaParte.setTemDireito(true);
                sextaParte.setVinculoFP(ap);
                sextaPartes.add(sextaParte);
            }
        }

        return sextaPartes;
    }

    public SextaParte getSextaParteByVinculoFP(VinculoFP v) {
        Query q = em.createQuery(" from SextaParte  where vinculoFP = :vinculo ");
        q.setParameter("vinculo", v);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (SextaParte) q.getResultList().get(0);

    }

    public List<SextaParte> findSextaPartePorVinculoFP(VinculoFP v) {
        Query q = em.createQuery("from SextaParte  where vinculoFP = :vinculo ");
        q.setParameter("vinculo", v);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }


    public Integer buscarQuantidadeDeServidoresRegistradosSextaParte(Date inicio) {
        Integer total = 0;
        String hql = "select count(distinct r.id) from SextaParte_aud r inner join revisaoAuditoria rev on rev.id= r.rev where r.revtype = 0 and to_date(to_char(rev.datahora,'dd/MM/yyyy'),'dd/MM/yyyy') =  :data ";
        Query q = em.createNativeQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        BigDecimal bg = (BigDecimal) q.getSingleResult();
        total = bg.intValue();
        return total;
    }

    public TipoSextaParteFacade getTipoSextaParteFacade() {
        return tipoSextaParteFacade;
    }
}
