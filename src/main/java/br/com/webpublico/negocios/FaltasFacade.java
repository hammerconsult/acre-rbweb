/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.rh.VwFalta;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFalta;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.com.webpublico.util.DataUtil.localDateToDate;

/**
 * @author peixe
 */
@Stateless
public class FaltasFacade extends AbstractFacade<Faltas> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public FaltasFacade() {
        super(Faltas.class);
    }

    public AfastamentoFacade getAfastamentoFacade() {
        return afastamentoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Faltas> recuperaFaltasPorContratoPeriodo(ContratoFP contrato, Date inicio, Date fim) {
        StringBuilder strQuery = new StringBuilder();
        strQuery.append(" from Faltas faltas ");
        strQuery.append("where faltas.contratoFP = :contrato");
        strQuery.append("  and :fim >= faltas.inicio");
        strQuery.append("  and :inicio <= faltas.fim ");
        strQuery.append("order by faltas.inicio");

        Query q = em.createQuery(strQuery.toString());
        q.setParameter("contrato", contrato);
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);

        return q.getResultList();
    }

    public List<VwFalta> buscarFaltasPorContratoAndPeriodo(ContratoFP contrato, LocalDate inicio, LocalDate fim) {
        StringBuilder strQuery = new StringBuilder();
        strQuery.append(" select faltas.* from VwFalta faltas ");
        strQuery.append("where faltas.contratoFP_id = :contrato");
        strQuery.append("  and :fim >= faltas.inicio");
        strQuery.append("  and :inicio <= faltas.fim ");
        strQuery.append("order by faltas.inicio");

        Query q = em.createNativeQuery(strQuery.toString(), VwFalta.class);
        q.setParameter("contrato", contrato.getId());
        q.setParameter("inicio", localDateToDate(inicio));
        q.setParameter("fim", localDateToDate(fim));
        return q.getResultList();
    }

    public List<VwFalta> buscarFaltasPorVinculoFPAndTipoFaltaAndData(VinculoFP contrato, TipoFalta tipo, Date maximaData) {
        StringBuilder strQuery = new StringBuilder();
        strQuery.append(" SELECT vw.* FROM VWFALTA VW ");
        strQuery.append(" where vw.contratoFP_id = :contrato");
        strQuery.append("  and vw.inicio <= :dataMaxima and ");
        strQuery.append("  vw.tipoFalta = :tipo ");
        strQuery.append("order by vw.inicio ");

        Query q = em.createNativeQuery(strQuery.toString(), VwFalta.class);
        q.setParameter("contrato", contrato.getId());
        q.setParameter("dataMaxima", maximaData);
        q.setParameter("tipo", tipo.name());
        return q.getResultList();
    }


    public List<Faltas> recuperaFaltasConferencia(HierarquiaOrganizacional ho, Date inicio, Date fim) {
        StringBuilder strQuery = new StringBuilder();
        strQuery.append("select new Faltas(contrato, sum(faltas.fim - faltas.inicio + 1))");
        strQuery.append("  from Faltas faltas");
        strQuery.append(" inner join faltas.contratoFP contrato");
        strQuery.append(" inner join contrato.lotacaoFuncionals lotacao");
        strQuery.append(" where :fim >= faltas.inicio");
        strQuery.append("   and :inicio <= faltas.fim");
        strQuery.append("   and :fim >= lotacao.inicioVigencia");
        strQuery.append("   and :fim <= coalesce(lotacao.finalVigencia, :fim)");
        strQuery.append("   and lotacao.unidadeOrganizacional in (select distinct subordinadaId from VwHierarquiaAdministrativa where codigo like :codigo )");
        strQuery.append(" group by contrato order by contrato");

        Query q = em.createQuery(strQuery.toString());
        q.setParameter("codigo", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);

        return q.getResultList();
    }

    public Integer recuperaDiasDeFaltas(ContratoFP contrato, PeriodoAquisitivoFL periodo) {
        Integer totalDiasDeFaltas = 0;
        Query q = em.createQuery("from Faltas a where a.contratoFP = :vinculo and "
            + " a.tipoFalta = :tipo and "
            + " (a.inicio between :dataInicio and :dataFim or a.fim between :dataInicio and :dataFim) "
            + " and a.dataCadastro >= :dataAtualizacao ");
        q.setParameter("vinculo", contrato);
        q.setParameter("tipo", TipoFalta.FALTA_INJUSTIFICADA);
        q.setParameter("dataInicio", periodo.getInicioVigencia());
        q.setParameter("dataFim", periodo.getFinalVigencia());
        if (periodo.getDataAtualizacao() == null) {
            q.setParameter("dataAtualizacao", Util.getDataHoraMinutoSegundoZerado(new Date()));
        } else {
            q.setParameter("dataAtualizacao", periodo.getDataAtualizacao());
        }
        for (Faltas a : (List<Faltas>) q.getResultList()) {
            Date dataMaiorInicio = DataUtil.retornaMaiorData(periodo.getInicioVigencia(), a.getInicio());
            Date dataMenorFinal = DataUtil.retornaMenorData(periodo.getFinalVigencia(), a.getFim());

            Calendar cInicio = Calendar.getInstance();
            Calendar cFim = Calendar.getInstance();
            cInicio.setTime(dataMaiorInicio);
            cFim.setTime(dataMenorFinal);

            totalDiasDeFaltas += (cFim.get(Calendar.DAY_OF_MONTH) - cInicio.get(Calendar.DAY_OF_MONTH)) + 1;
        }
        //logger.log(Level.INFO, " Total de Dias de Faltas:{0}", totalDiasDeFaltas);
        return totalDiasDeFaltas;
    }


    public Integer recuperaDiasDeFaltasPorPeriodo(Long contratoID, Date ini, Date fi) {
        Integer totalDiasDeFaltas = 0;
        Query q = em.createQuery("from VwFalta vw " +
            "    where vw.contratoFP = :vinculo and " +
            "    (vw.inicio between to_date(:dataInicio,'dd/MM/yyyy') and to_date(:dataFim,'dd/MM/yyyy') " +
            "    or vw.fim between to_date(:dataInicio,'dd/MM/yyyy') and to_date(:dataFim,'dd/MM/yyyy')) ");
        q.setParameter("vinculo", new BigDecimal(contratoID));
        q.setParameter("dataInicio", DataUtil.getDataFormatada(ini));
        q.setParameter("dataFim", DataUtil.getDataFormatada(fi));
        for (VwFalta falta : (List<VwFalta>) q.getResultList()) {
            Integer totalDiasJustificados = 0;
            Date inicio = falta.getInicio();
            Date fim = falta.getFim();
            if (falta.getInicio().before(ini) || falta.getFim().after(fi)) {
                if (falta.getInicio().before(ini)) {
                    inicio = ini;
                }
                if (falta.getFim().after(fi)) {
                    fim = fi;
                }
                if (falta.getJustificativa() != null) {
                    if (falta.getTotalFaltasEfetivas() > 0) {
                        Date inicioJustificativa = falta.getInicioJustificativa();
                        Date fimJustificativa = falta.getFimJustificativa();
                        if (falta.getInicioJustificativa().before(ini)) {
                            inicioJustificativa = ini;
                        }
                        if (falta.getFimJustificativa().after(fi)) {
                            fimJustificativa = fi;
                        }
                        totalDiasJustificados = DataUtil.diasEntreDate(inicioJustificativa, fimJustificativa);
                        totalDiasDeFaltas += DataUtil.diasEntreDate(inicio, fim) - totalDiasJustificados;
                    }
                } else {
                    totalDiasDeFaltas += DataUtil.diasEntreDate(inicio, fim);
                }
            } else {
                totalDiasDeFaltas += falta.getTotalFaltasEfetivas();
            }
        }
        return totalDiasDeFaltas;
    }

    public List<Faltas> listaFiltrandoX(String s, int inicio, int max) {
        String hql = " select falta from Faltas falta "
            + " inner join falta.contratoFP contrato "
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " where (lower(matricula.matricula) like :filtro) "
            + " or (lower(pf.nome) like :filtro) "
            + " or (lower(contrato.numero) like :filtro) "
            + " or (lower(falta.tipoFalta) like :filtro) "
            + " OR (('0'|| cast(extract (day from falta.inicio) as string) "
            + " || replace('/'||'0'|| cast(extract (month from falta.inicio) as string),'00','0') "
            + " ||'/'|| cast(extract (year from falta.inicio) as string)) "
            + " like :filtro) ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");

        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<Faltas> listaFaltasMes(VinculoFP vinculoFP, Integer ano, Mes mes) {
        Calendar iniMes = Calendar.getInstance();
        Calendar fimMes = Calendar.getInstance();
        iniMes.setTime(Util.criaDataPrimeiroDiaDoMesFP(mes.ordinal(), ano));
        fimMes.setTime(Util.recuperaDataUltimoDiaDoMes(iniMes.getTime()));

        StringBuilder hql = new StringBuilder();
        hql.append("  from Faltas f");
        hql.append(" where f.contratoFP.id = :vinculo");
        hql.append("   and ((extract(month from f.fim) = :mes and extract(year from f.fim) = :ano) ");
        hql.append("  or (extract(month from f.inicio) = :mes and extract(year from f.inicio) = :ano))  ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("vinculo", vinculoFP.getId());
        q.setParameter("mes", UtilRH.getMesZeroDezembroCorreto(mes.getNumeroMesIniciandoEmZero()));
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    public List<Faltas> recuperaFaltasPorContrato(ContratoFP contratoFP) {
        if (contratoFP == null) return new ArrayList<>();

        StringBuilder hql = new StringBuilder();
        hql.append("  from Faltas f");
        hql.append(" where f.contratoFP = :vinculo");
        hql.append("   and f.tipoFalta = :tipo and f not in(select fal.faltas from JustificativaFaltas fal where fal.faltas.contratoFP = :vinculo) order by f.inicio desc");

        Query q = em.createQuery(hql.toString());
        q.setParameter("vinculo", contratoFP);
        q.setParameter("tipo", TipoFalta.FALTA_INJUSTIFICADA);
        return q.getResultList();
    }


    public List<Faltas> buscarFaltasComDiaAJustificar(ContratoFP contratoFP) {
        String sql = "select distinct faltas.* from faltas " +
            " left join justificativafaltas justificativa on faltas.id = justificativa.FALTAS_ID " +
            " where CONTRATOFP_ID = :contratoFP and TIPOFALTA = :tipoFalta  " +
            " and fim - inicio  + 1 > (select coalesce(sum(justificativa.DIAS), 0) from justificativafaltas justificativa " +
            " inner join faltas faltas2 on justificativa.FALTAS_ID = faltas2.id " +
            " where faltas2.id = faltas.id)";
        Query q = em.createNativeQuery(sql, Faltas.class);
        q.setParameter("contratoFP", contratoFP.getId());
        q.setParameter("tipoFalta", TipoFalta.FALTA_INJUSTIFICADA.name());
        return q.getResultList();
    }

    public BigDecimal buscarQuantidadeDiasJustificados(Faltas faltas, JustificativaFaltas justificativaFaltas) {
        String sql = "select coalesce(sum(justificativa.DIAS), 0) from justificativafaltas justificativa " +
            " inner join faltas on justificativa.FALTAS_ID = faltas.id " +
            " where faltas.id  = :idFalta ";
        if (justificativaFaltas != null && justificativaFaltas.getId() != null) {
            sql += " and justificativa.id <> :justificativa";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idFalta", faltas.getId());
        if (justificativaFaltas != null && justificativaFaltas.getId() != null) {
            q.setParameter("justificativa", justificativaFaltas.getId());
        }
        return !q.getResultList().isEmpty() ? (BigDecimal) q.getResultList().get(0) : BigDecimal.ZERO;
    }

    public Long recuperaQuantidadeFaltasPorPeriodo(ContratoFP contratoFP, Date inicio, Date fim, TipoFalta
        tipoFalta) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select sum(f.dias) from Faltas f ");
        hql.append(" where f.contratoFP = :contratoFP ");
        hql.append(" and f.tipoFalta = :tipoFalta ");
        hql.append(" and f not in(select fal.faltas from JustificativaFaltas fal where fal.faltas.contratoFP = :contratoFP) ");
        hql.append(" and (f.inicio between :inicio and :fim or f.fim between :inicio and :fim) ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("tipoFalta", tipoFalta);
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);

        return q.getSingleResult() == null ? 0 : (Long) q.getSingleResult();
    }

    public Faltas servidorPossuiFaltasNoPeriodo(Faltas faltas) {
        String hql = " select f from Faltas f" +
            "       where f.contratoFP = :contrato" +
            "         and ((:inicio between f.inicio and f.fim or :fim between f.inicio and f.fim) or " +
            "         (f.inicio between :inicio and :fim or f.fim between :inicio and :fim))";
        if (faltas.getId() != null) {
            hql += " and f <> :falta";
        }
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        q.setParameter("contrato", faltas.getContratoFP());
        q.setParameter("inicio", faltas.getInicio());
        q.setParameter("fim", faltas.getFim());
        if (faltas.getId() != null) {
            q.setParameter("falta", faltas);
        }
        try {
            return (Faltas) q.getSingleResult();
        } catch (NoResultException npe) {
            return null;
        }
    }

    public VwFalta buscarFaltasInjustificadasNoPeriodo(ContratoFP contrato, Date inicio, Date fim) {
        String sql = " select f.* from VwFalta f" +
            "       where f.contratoFP_id = :contrato" +
            "         and ((:inicio between f.inicio and f.fim or :fim between f.inicio and f.fim) or " +
            "         (f.inicio between :inicio and :fim or f.fim between :inicio and :fim))" +
            "         and f.tipoFalta = :tipoFalta";
        Query q = em.createNativeQuery(sql, VwFalta.class);
        q.setMaxResults(1);
        q.setParameter("contrato", contrato.getId());
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        q.setParameter("tipoFalta", TipoFalta.FALTA_INJUSTIFICADA.name());
        try {
            return (VwFalta) q.getSingleResult();
        } catch (NoResultException npe) {
            return null;
        }
    }

    public double recuperaNumeroFaltas(VinculoFP ep, Date inicioVigencia, Date finalVigencia) {
        Integer totalFaltas = 0;

        String hql = " from Faltas faltas "
            + " where faltas.contratoFP.id = :ep "
            + " and faltas.tipoFalta = :tipo " +
            " and (to_date(to_char(faltas.inicio,'mm/yyyy'),'mm/yyyy') between  " +
            " to_date(to_char(:inicioVigencia,'mm/yyyy'),'mm/yyyy')  and to_date(to_char(:finalVigencia,'mm/yyyy'),'mm/yyyy') " +

            " or  to_date(to_char(faltas.fim,'mm/yyyy'),'mm/yyyy') between  to_date(to_char(:inicioVigencia,'mm/yyyy'),'mm/yyyy')" +
            "  and to_date(to_char(:finalVigencia,'mm/yyyy'),'mm/yyyy') ) ";


        Query q = em.createQuery(hql);
        q.setParameter("ep", ep.getIdCalculo());
        q.setParameter("tipo", TipoFalta.FALTA_INJUSTIFICADA);
        q.setParameter("inicioVigencia", inicioVigencia);
        q.setParameter("finalVigencia", finalVigencia);

        List<Faltas> faltas = (List<Faltas>) q.getResultList();
        for (Faltas falta : faltas) {
            Calendar varInicio = Calendar.getInstance();
            Calendar varFim = Calendar.getInstance();

            varInicio.setTime(falta.getInicio());
            varFim.setTime(falta.getFim());
            Calendar varIniMes = Calendar.getInstance();
            Calendar varFimMes = Calendar.getInstance();
            varIniMes.setTime(inicioVigencia);
            varFimMes.setTime(finalVigencia);

            DateTime varIniMesJoda = new DateTime(varIniMes);
//
//
            DateTime dataTimeFimDoMes = new DateTime(varFimMes);
//
            if (varInicio.getTime().before(varIniMesJoda.toDate())) {
                varInicio.setTime(varIniMesJoda.toDate());
            }
            if (varFim.getTime().after(dataTimeFimDoMes.toDate())) {
                varFim.setTime(dataTimeFimDoMes.toDate());
            }
            DateTime vi = new DateTime(varInicio);
            vi = DataUtil.zerarHorasMinutosSegundos(vi);
            DateTime vf = new DateTime(varFim);
            vf = DataUtil.zerarHorasMinutosSegundos(vf);
            if (!vi.isEqual(vf)) {
                if (varInicio.after(varFim)) {
                    continue;
                }
            }
            totalFaltas += (varFim.get(Calendar.DAY_OF_MONTH) - varInicio.get(Calendar.DAY_OF_MONTH)) + 1;
        }
        return totalFaltas.doubleValue();
    }

    public Integer buscarQuantidadeDeFaltasPorData(Date inicio) {
        Integer total = 0;
        String hql = "select count(falta) from Faltas falta where falta.dataCadastro = :data ";
        Query q = em.createQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        Long bg = (Long) q.getSingleResult();
        total = bg.intValue();
        return total;
    }

    public boolean hasFolhaPeriodo(VinculoFP vinculoFP, Faltas faltas) {
        String sql = " select ficha.VINCULOFP_ID, folha.mes, folha.ano from folhadepagamento folha " +
            " inner join fichafinanceirafp ficha on ficha.FOLHADEPAGAMENTO_ID = folha.id " +
            " inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.id " +
            " inner join eventofp evento on item.EVENTOFP_ID = evento.id " +
            " where ficha.VINCULOFP_ID = :vinculo" +
            " and evento.IDENTIFICACAOEVENTOFP = :identificacao " +
            " and (folha.ano between extract(year from :inicioFalta) and extract(year from :finalFaltas) " +
            " and folha.MES  between extract(month from :inicioFalta) and extract(month from :finalFaltas)) " +
            " AND folha.EFETIVADAEM IS NOT NULL ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("vinculo", vinculoFP.getId());
        q.setParameter("inicioFalta", faltas.getInicio());
        q.setParameter("finalFaltas", faltas.getFim());
        q.setParameter("identificacao", IdentificacaoEventoFP.FALTA.name());
        return !q.getResultList().isEmpty();
    }

    public List<Faltas> buscarFaltasPorPeriodo(Long contratoID, Date inicio, Date fim) {
        String hql = " from Faltas faltas "
            + " where faltas.contratoFP.id = :contratoID "
            + " and faltas.tipoFalta = :tipo ";
        hql += " and ( to_date(:inicio,'dd/MM/yyyy') between faltas.inicio and faltas.fim "; //so pega as faltas do mes
        hql += " or  to_date(:fim,'dd/MM/yyyy') between faltas.inicio  and faltas.fim ) "; //so pega as faltas do mes

        Query q = em.createQuery(hql);
        q.setParameter("contratoID", contratoID);
        q.setParameter("tipo", TipoFalta.FALTA_INJUSTIFICADA);
        q.setParameter("inicio", DataUtil.getDataFormatada(inicio));
        q.setParameter("fim", DataUtil.getDataFormatada(fim));
        return q.getResultList();
    }

    public JustificativaFaltas buscarJustificativaPorFalta(Long faltaID) {
        String hql = " from JustificativaFaltas just "
            + " where just.faltas.id = :faltaID order by just.inicioVigencia";
        Query q = em.createQuery(hql);
        q.setParameter("faltaID", faltaID);
        List<JustificativaFaltas> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return null;
        }
        return resultado.get(0);
    }

    public List<Faltas> buscarFaltasPorContratoIndependenteDeJustificativa(ContratoFP contrato) {
        String sql = " select f.* from faltas f " +
            " where f.CONTRATOFP_ID = :contrato ";
        Query q = em.createNativeQuery(sql, Faltas.class);
        q.setParameter("contrato", contrato.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

}
