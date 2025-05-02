/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.ResultadoCalculoRetroativoFP;
import br.com.webpublico.enums.TipoFalta;
import br.com.webpublico.interfaces.CalculoRetroativoFacade;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author WebPublico07
 */
@Stateless
public class CalculoRetroativoFacadeImpl implements CalculoRetroativoFacade {

    protected static final Logger logger = LoggerFactory.getLogger(CalculoRetroativoFacadeImpl.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ResultadoCalculoRetroativoFP verifica(final VinculoFP vinculoFP) {
        Date hoje = new Date();
        Date menorData = hoje;
        Date dataUltimaFolha = recuperaDataUltimaFolha();
        if (dataUltimaFolha != null) {
            Date dataPenultimaFolha = recuperaDataPenultimaFolha(dataUltimaFolha);
            if (dataPenultimaFolha != null && dataUltimaFolha != null) {
                Date ultimoDiaDoMes = Util.recuperaDataUltimoDiaDoMes(dataUltimaFolha);

                final Date dataRetroativoAfastamento = caracterizaRetroativoAfastamento(vinculoFP, dataUltimaFolha, dataPenultimaFolha, ultimoDiaDoMes);
                if (dataRetroativoAfastamento != null && dataRetroativoAfastamento.before(menorData)) {
                    menorData = dataRetroativoAfastamento;
                }

                final Date dataRetroativoFaltas = caracterizaRetroativoFaltas(vinculoFP, dataUltimaFolha, dataPenultimaFolha, ultimoDiaDoMes);
                if (dataRetroativoFaltas != null && dataRetroativoFaltas.before(menorData)) {
                    menorData = dataRetroativoFaltas;
                }

                final Date dataRetroativoEnquadramentoFuncional = caracterizaRetroativoEnquadramentoFuncional(vinculoFP, dataUltimaFolha, dataPenultimaFolha, ultimoDiaDoMes);
                if (dataRetroativoEnquadramentoFuncional != null && dataRetroativoEnquadramentoFuncional.before(menorData)) {
                    menorData = dataRetroativoEnquadramentoFuncional;
                }

                final Date dataRetroativoCedenciaContrato = caracterizaRetroativoCedenciaContrato(vinculoFP, dataUltimaFolha, dataPenultimaFolha, ultimoDiaDoMes);
                if (dataRetroativoCedenciaContrato != null && dataRetroativoCedenciaContrato.before(menorData)) {
                    menorData = dataRetroativoCedenciaContrato;
                }

                final Date dataRetroativoEnquadramentoCC = caracterizaRetroativoEnquadramentoCC(vinculoFP, dataUltimaFolha, dataPenultimaFolha, ultimoDiaDoMes);
                if (dataRetroativoEnquadramentoCC != null && dataRetroativoEnquadramentoCC.before(menorData)) {
                    menorData = dataRetroativoEnquadramentoCC;
                }

                final Date dataRetroativoEnquadramentoFG = caracterizaRetroativoEnquadramentoFG(vinculoFP, dataUltimaFolha, dataPenultimaFolha, ultimoDiaDoMes);
                if (dataRetroativoEnquadramentoFG != null && dataRetroativoEnquadramentoFG.before(menorData)) {
                    menorData = dataRetroativoEnquadramentoFG;
                }

                final Date dataRetroativoConcessaoFerias = caracterizaRetroativoConcessaoFerias(vinculoFP, dataUltimaFolha, dataPenultimaFolha, ultimoDiaDoMes);
                if (dataRetroativoConcessaoFerias != null && dataRetroativoConcessaoFerias.before(menorData)) {
                    menorData = dataRetroativoConcessaoFerias;
                }

                final Date dataRetroativoLancamento = caracterizaRetroativoLancamento(vinculoFP, dataUltimaFolha, dataPenultimaFolha, ultimoDiaDoMes);
                if (dataRetroativoLancamento != null && dataRetroativoLancamento.before(menorData)) {
                    logger.debug("Retroativo de EnquadramentoPCS caracterizado.");
                    menorData = dataRetroativoLancamento;
                }
                final Date dataRetroativoEnquadramentoPCS = caracterizaRetroativoEnquadramentoPCS(vinculoFP, dataUltimaFolha, dataPenultimaFolha, ultimoDiaDoMes);
                if (dataRetroativoEnquadramentoPCS != null && dataRetroativoEnquadramentoPCS.before(menorData)) {
                    logger.debug("Retroativo de EnquadramentoPCS caracterizado.");
                    menorData = dataRetroativoEnquadramentoPCS;
                }

                ResultadoCalculoRetroativoFP rc = new ResultadoCalculoRetroativoFP(hoje);
                rc.setDataInicialCalculoRetroativo(menorData);
                return rc;
            }
        }
        return null;
    }

    public Date recuperaDataUltimaFolha() {
        String sql = "select max(fp.calculadaEm) from FolhaDePagamento fp";
        Query q = getEntityManager().createQuery(sql);
        try {
            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date recuperaDataPenultimaFolha(Date ultimaFolha) {
        String sql = "select max(fp.calculadaEm) "
            + "from FolhaDePagamento fp "
            + "where fp.calculadaEm < :ultimaFolha";
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("ultimaFolha", ultimaFolha);
        try {

            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Date caracterizaRetroativoAfastamento(VinculoFP vinculoFP, Date ultimaFolha, Date penultimaFolha, Date ultimoDiaDoMesDaUltimaFolha) {
//        String hql = "select min(af.inicio) from Afastamento af "
//                + "where (af.inicio < af.dataCadastro "
//                + "and af.contratoFP.id = :vinculoFP "
//                + "and af.dataCadastro > :penultimaFolha "
//                + "and af.dataCadastro <= :ultimaFolha) "
//                + " or "
//                + "(af.dataCadastro > :ultimaFolha "
//                + "and af.dataCadastro < af.inicio "
//                + "and af.inicio < :ultimoDiaDoMes"
//                + ") and af.dataCadastro is not null";
        StringBuilder hql = new StringBuilder();
        hql.append("select min(af.inicio)");
        hql.append("  from Afastamento af");
        hql.append(" where af.contratoFP.id = :vinculoFP");
        hql.append("   and af.dataCadastro > :penultimaFolha");
        hql.append("   and af.dataCadastro <= :ultimaFolha");
        hql.append("   and af.dataCadastro is not null");

        Query q = getEntityManager().createQuery(hql.toString());
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("ultimaFolha", ultimaFolha);
        q.setParameter("penultimaFolha", penultimaFolha);
        //q.setParameter("ultimoDiaDoMes", ultimoDiaDoMesDaUltimaFolha);
        try {
            if (q.getResultList().isEmpty()) {
                return null;
            }
            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Date caracterizaRetroativoEnquadramentoPCS(VinculoFP vinculoFP, Date ultimaFolha, Date penultimaFolha, Date ultimoDiaDoMesDaUltimaFolha) {
//        String hql = "select min(epcs.inicioVigencia) from EnquadramentoFuncional ef, EnquadramentoPCS epcs "
//                + "inner join ef.progressaoPCS progressao "
//                + "inner join ef.categoriaPCS cat "
//                + "inner join epcs.categoriaPCS catEnquadramento "
//                + "inner join epcs.progressaoPCS progressaoEnquadramento "
//                + "where cat = catEnquadramento and progressaoEnquadramento = progressao "
//                + "and (epcs.inicioVigencia < epcs.dataCadastro "
//                + "and ef.contratoServidor.id = :vinculoFP "
//                + "and epcs.dataCadastro > :penultimaFolha "
//                + "and epcs.dataCadastro <= :ultimaFolha) "
//                + " or "
//                + "(epcs.dataCadastro > :ultimaFolha "
//                + "and epcs.dataCadastro < epcs.inicioVigencia "
//                + "and epcs.inicioVigencia < :ultimoDiaDoMes"
//                + ") and epcs.dataCadastro is not null";

        StringBuilder hql = new StringBuilder();
        hql.append("select min(epcs.inicioVigencia)");
        hql.append("  from EnquadramentoFuncional ef, EnquadramentoPCS epcs");
        //hql.append(" inner join ef.progressaoPCS progressao");
        //hql.append(" inner join ef.categoriaPCS cat");
        //hql.append(" inner join epcs.categoriaPCS catEnquadramento");
        //hql.append(" inner join epcs.progressaoPCS progressaoEnquadramento");
        hql.append(" where ef.categoriaPCS = epcs.categoriaPCS");
        hql.append("   and epcs.progressaoPCS = ef.progressaoPCS");
        hql.append("   and ef.contratoServidor.id = :vinculoFP");
        hql.append("   and epcs.dataCadastro > :penultimaFolha");
        hql.append("   and epcs.dataCadastro <= :ultimaFolha");
        hql.append("   and epcs.dataCadastro is not null");

        Query q = getEntityManager().createQuery(hql.toString());
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("ultimaFolha", ultimaFolha);
        q.setParameter("penultimaFolha", penultimaFolha);
        //q.setParameter("ultimoDiaDoMes", ultimoDiaDoMesDaUltimaFolha);

        q.setMaxResults(1);//TODO verificar

        //System.out.println(Util.dateToString(ultimaFolha));
        //System.out.println(Util.dateToString(penultimaFolha));
        //System.out.println(Util.dateToString(ultimoDiaDoMesDaUltimaFolha));
        //System.out.println("--------------");
        try {
            if (q.getResultList().isEmpty()) {
                return null;
            }
            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public Date caracterizaRetroativoEnquadramentoFuncional(VinculoFP vinculoFP, Date ultimaFolha, Date penultimaFolha, Date ultimoDiaDoMesDaUltimaFolha) {
//        String hql = "select min(ef.inicioVigencia) "
//                + "from EnquadramentoFuncional ef "
//                + "where (ef.inicioVigencia < ef.dataCadastro and ef.contratoServidor.id = :vinculoFP "
//                + "and ef.dataCadastro > :penultimaFolha "
//                + "and ef.dataCadastro <= :ultimaFolha) "
//                + " or "
//                + "(ef.dataCadastro > :ultimaFolha "
//                + "and ef.dataCadastro < ef.inicioVigencia "
//                + "and ef.inicioVigencia < :ultimoDiaDoMes"
//                + ") and ef.dataCadastro is not null";
        StringBuilder hql = new StringBuilder();
        hql.append("select min(ef.inicioVigencia)");
        hql.append("  from EnquadramentoFuncional ef");
        hql.append(" where ef.contratoServidor.id = :vinculoFP");
        hql.append("   and ef.dataCadastro > :penultimaFolha");
        hql.append("   and ef.dataCadastro <= :ultimaFolha");
        hql.append("   and ef.dataCadastro is not null");

        Query q = em.createQuery(hql.toString());
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("ultimaFolha", ultimaFolha);
        q.setParameter("penultimaFolha", penultimaFolha);
        //q.setParameter("ultimoDiaDoMes", ultimoDiaDoMesDaUltimaFolha);
        try {
            if (q.getResultList().isEmpty()) {
                return null;
            }
            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date caracterizaRetroativoCedenciaContrato(VinculoFP vinculoFP, Date ultimaFolha, Date penultimaFolha, Date ultimoDiaDoMesDaUltimaFolha) {
//        String hql = "select min(cc.dataCessao) "
//                + "from CedenciaContratoFP cc "
//                + "where (cc.dataCessao < cc.dataCadastro and cc.contratoFP.id = :vinculoFP "
//                + "and cc.dataCadastro > :penultimaFolha "
//                + "and cc.dataCadastro <= :ultimaFolha) "
//                + " or "
//                + "(cc.dataCadastro > :ultimaFolha "
//                + "and cc.dataCadastro < cc.dataCessao "
//                + "and cc.dataCessao < :ultimoDiaDoMes"
//                + ") and  cc.dataCadastro is not null";
        StringBuilder hql = new StringBuilder();
        hql.append("select min(cc.dataCessao)");
        hql.append("  from CedenciaContratoFP cc");
        hql.append(" where cc.contratoFP.id = :vinculoFP");
        hql.append("   and cc.dataCadastro > :penultimaFolha");
        hql.append("   and cc.dataCadastro <= :ultimaFolha");
        hql.append("   and cc.dataCadastro is not null");

        Query q = getEntityManager().createQuery(hql.toString());
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("ultimaFolha", ultimaFolha);
        q.setParameter("penultimaFolha", penultimaFolha);
        //q.setParameter("ultimoDiaDoMes", ultimoDiaDoMesDaUltimaFolha);
        try {
            if (q.getResultList().isEmpty()) {
                return null;
            }
            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date caracterizaRetroativoEnquadramentoCC(VinculoFP vinculoFP, Date ultimaFolha, Date penultimaFolha, Date ultimoDiaDoMesDaUltimaFolha) {
//        String hql = "select min(ec.inicioVigencia) "
//                + "from EnquadramentoCC ec "
//                + "where (ec.inicioVigencia < ec.dataCadastro "
//                + "and ec.dataCadastro > :penultimaFolha "
//                + "and ec.dataCadastro <= :ultimaFolha) "
//                + " or "
//                + "(ec.dataCadastro > :ultimaFolha "
//                + "and ec.dataCadastro < ec.inicioVigencia "
//                + "and ec.inicioVigencia < :ultimoDiaDoMes"
//                + ") and ec.dataCadastro is not null";
        StringBuilder hql = new StringBuilder();
        hql.append("select min(ec.inicioVigencia)");
        hql.append("  from EnquadramentoCC ec");
        hql.append(" inner join ec.cargoConfianca cc");
        hql.append(" where cc.contratoFP.id = :vinculoFP");
        hql.append("   and ec.dataCadastro > :penultimaFolha");
        hql.append("   and ec.dataCadastro <= :ultimaFolha");
        hql.append("   and ec.dataCadastro is not null");

        Query q = getEntityManager().createQuery(hql.toString());
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("ultimaFolha", ultimaFolha);
        q.setParameter("penultimaFolha", penultimaFolha);
        //q.setParameter("ultimoDiaDoMes", ultimoDiaDoMesDaUltimaFolha);
        try {
            if (q.getResultList().isEmpty()) {
                return null;
            }
            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date caracterizaRetroativoEnquadramentoFG(VinculoFP vinculoFP, Date ultimaFolha, Date penultimaFolha, Date ultimoDiaDoMesDaUltimaFolha) {
//        String hql = "select min(efg.inicioVigencia) "
//                + "from EnquadramentoFG efg "
//                + "where (efg.inicioVigencia < efg.dataCadastro "
//                + "and efg.dataCadastro > :penultimaFolha "
//                + "and efg.dataCadastro <= :ultimaFolha) "
//                + " or "
//                + "(efg.dataCadastro > :ultimaFolha "
//                + "and efg.dataCadastro < efg.inicioVigencia "
//                + "and efg.inicioVigencia < :ultimoDiaDoMes"
//                + ") and efg.dataCadastro is not null";
        StringBuilder hql = new StringBuilder();
        hql.append("select min(efg.inicioVigencia)");
        hql.append("  from EnquadramentoFG efg");
        hql.append(" inner join efg.funcaoGratificada fg");
        hql.append(" where fg.contratoFP.id = :vinculoFP");
        hql.append("   and efg.dataCadastro > :penultimaFolha");
        hql.append("   and efg.dataCadastro <= :ultimaFolha");
        hql.append("   and efg.dataCadastro is not null");

        Query q = getEntityManager().createQuery(hql.toString());
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("ultimaFolha", ultimaFolha);
        q.setParameter("penultimaFolha", penultimaFolha);
        //q.setParameter("ultimoDiaDoMes", ultimoDiaDoMesDaUltimaFolha);
        try {
            if (q.getResultList().isEmpty()) {
                return null;
            }
            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date caracterizaRetroativoConcessaoFerias(VinculoFP vinculoFP, Date ultimaFolha, Date penultimaFolha, Date ultimoDiaDoMesDaUltimaFolha) {
//        String hql = "select min(cfl.dataInicial) "
//                + "from ConcessaoFeriasLicenca cfl "
//                + "where (cfl.dataInicial < cfl.dataCadastro and cfl.periodoAquisitivoFL.contratoFP.id = :vinculoFP "
//                + "and cfl.dataCadastro > :penultimaFolha "
//                + "and cfl.dataCadastro <= :ultimaFolha) "
//                + " or "
//                + "(cfl.dataCadastro > :ultimaFolha "
//                + "and cfl.dataCadastro < cfl.dataInicial "
//                + "and cfl.dataInicial < :ultimoDiaDoMes"
//                + ") and cfl.dataCadastro is not null";
        StringBuilder hql = new StringBuilder();
        hql.append("select min(cfl.dataInicial)");
        hql.append("  from ConcessaoFeriasLicenca cfl");
        hql.append(" where cfl.periodoAquisitivoFL.contratoFP.id = :vinculoFP");
        hql.append("   and cfl.dataCadastro > :penultimaFolha");
        hql.append("   and cfl.dataCadastro <= :ultimaFolha");
        hql.append("   and cfl.dataCadastro is not null");

        Query q = getEntityManager().createQuery(hql.toString());
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("ultimaFolha", ultimaFolha);
        q.setParameter("penultimaFolha", penultimaFolha);
        //q.setParameter("ultimoDiaDoMes", ultimoDiaDoMesDaUltimaFolha);
        try {
            if (q.getResultList().isEmpty()) {
                return null;
            }
            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date caracterizaRetroativoLancamento(VinculoFP vinculoFP, Date ultimaFolha, Date penultimaFolha, Date ultimoDiaDoMesDaUltimaFolha) {
//        String hql = "select min(cfl.mesAnoInicial) "
//                + "from LancamentoFP cfl "
//                + "where (cfl.mesAnoInicial < cfl.dataCadastro and cfl.vinculoFP.id = :vinculoFP "
//                + "and cfl.dataCadastro > :penultimaFolha "
//                + "and cfl.dataCadastro <= :ultimaFolha) "
//                + " or "
//                + "(cfl.dataCadastro > :ultimaFolha "
//                + "and cfl.dataCadastro < cfl.mesAnoInicial "
//                + "and cfl.mesAnoInicial < :ultimoDiaDoMes"
//                + ") and cfl.dataCadastro is not null";
        StringBuilder hql = new StringBuilder();
        hql.append("select min(lanc.mesAnoInicial)");
        hql.append("  from LancamentoFP lanc");
        hql.append(" where lanc.vinculoFP.id = :vinculoFP");
        hql.append("   and lanc.dataCadastro > :penultimaFolha");
        hql.append("   and lanc.dataCadastro <= :ultimaFolha");
        hql.append("   and lanc.dataCadastro is not null");

        Query q = getEntityManager().createQuery(hql.toString());
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("ultimaFolha", ultimaFolha);
        q.setParameter("penultimaFolha", penultimaFolha);
        //q.setParameter("ultimoDiaDoMes", ultimoDiaDoMesDaUltimaFolha);
        try {
            if (q.getResultList().isEmpty()) {
                return null;
            }
            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Date caracterizaRetroativoFaltas(VinculoFP vinculoFP, Date ultimaFolha, Date penultimaFolha, Date ultimoDiaDoMesDaUltimaFolha) {
//        String hql = " select min(faltas.inicio) from Faltas faltas "
//                + " where faltas.tipoFalta = :parametroTipoFalta "
//                + " and ((faltas.inicio < faltas.dataCadastro "
//                + " and faltas.contratoFP.id = :vinculoFP "
//                + " and faltas.dataCadastro > :penultimaFolha "
//                + " and faltas.dataCadastro <= :ultimaFolha) "
//                + " or "
//                + " (faltas.dataCadastro > :ultimaFolha "
//                + " and faltas.dataCadastro < faltas.inicio "
//                + " and faltas.inicio < :ultimoDiaDoMes )) and faltas.dataCadastro is not null ";
        StringBuilder hql = new StringBuilder();
        hql.append("select min(faltas.inicio)");
        hql.append("  from Faltas faltas");
        hql.append(" where faltas.contratoFP.id = :vinculoFP");
        hql.append("   and faltas.tipoFalta = :parametroTipoFalta");
        hql.append("   and faltas.dataCadastro > :penultimaFolha");
        hql.append("   and faltas.dataCadastro <= :ultimaFolha");
        hql.append("   and faltas.dataCadastro is not null");

        Query q = getEntityManager().createQuery(hql.toString());
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("ultimaFolha", ultimaFolha);
        q.setParameter("penultimaFolha", penultimaFolha);
        //q.setParameter("ultimoDiaDoMes", ultimoDiaDoMesDaUltimaFolha);
        q.setParameter("parametroTipoFalta", TipoFalta.FALTA_INJUSTIFICADA);
        try {
            if (q.getResultList().isEmpty()) {
                return null;
            }
            return (Date) q.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<EventoFP> recuperaEventosLancadosRetroativos(VinculoFP vinculoFP) {

        try {
            Date dataUltimaFolha = recuperaDataUltimaFolha();
            if (dataUltimaFolha != null) {
                Date dataPenultimaFolha = recuperaDataPenultimaFolha(dataUltimaFolha);
                if (dataPenultimaFolha != null && dataUltimaFolha != null) {
                    Date ultimoDiaDoMes = Util.recuperaDataUltimoDiaDoMes(dataUltimaFolha);
                    return caracterizaEventosRetroativosLancamento(vinculoFP, dataUltimaFolha, dataPenultimaFolha, ultimoDiaDoMes);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<EventoFP>();
    }

    public List<EventoFP> caracterizaEventosRetroativosLancamento(VinculoFP vinculoFP, Date ultimaFolha, Date penultimaFolha, Date ultimoDiaDoMesDaUltimaFolha) {
        String hql = "select clf.eventoFP "
            + "from LancamentoFP cfl "
            + "where clf.eventoFP.ativo is true and ((cfl.mesAnoInicial < cfl.dataCadastro and cfl.vinculoFP.id = :vinculoFP "
            + "and cfl.dataCadastro > :penultimaFolha "
            + "and cfl.dataCadastro <= :ultimaFolha) "
            + " or "
            + "(cfl.dataCadastro > :ultimaFolha "
            + "and cfl.dataCadastro < cfl.mesAnoInicial "
            + "and cfl.mesAnoInicial < :ultimoDiaDoMes)) and cfl.dataCadastro is not null";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("ultimaFolha", ultimaFolha);
        q.setParameter("penultimaFolha", penultimaFolha);
        q.setParameter("ultimoDiaDoMes", ultimoDiaDoMesDaUltimaFolha);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return q.getResultList();
    }
}
