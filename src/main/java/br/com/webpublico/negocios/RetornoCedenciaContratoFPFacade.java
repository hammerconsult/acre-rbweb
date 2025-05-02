/*
 * Codigo gerado automaticamente em Tue Feb 21 10:56:59 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Persistencia;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static br.com.webpublico.util.DataUtil.localDateToDate;

@Stateless
public class RetornoCedenciaContratoFPFacade extends AbstractFacade<RetornoCedenciaContratoFP> {

    private final long SITUACAO_EM_EXERCICIO = 1L;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;

    public RetornoCedenciaContratoFPFacade() {
        super(RetornoCedenciaContratoFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private SituacaoContratoFP getSituacaoContratoFP(ContratoFP contratoFP, Date inicioVigencia) {
        SituacaoFuncional situacaoFuncional = situacaoFuncionalFacade.recuperaCodigo(SITUACAO_EM_EXERCICIO);
        SituacaoContratoFP situacaoContratoFP = new SituacaoContratoFP();
        situacaoContratoFP.setContratoFP(contratoFP);
        situacaoContratoFP.setSituacaoFuncional(situacaoFuncional);
        situacaoContratoFP.setInicioVigencia(inicioVigencia);
        return situacaoContratoFP;
    }

    public void salvarNovoRetornoServidorCedidoUE(RetornoCedenciaContratoFP retornoCedenciaContratoFP) {
        LotacaoFuncional lotacaoFuncional = retornoCedenciaContratoFP.getLotacaoFuncional();
        CedenciaContratoFP cedenciaContratoFP = retornoCedenciaContratoFP.getCedenciaContratoFP();
        ContratoFP contratoFP = contratoFPFacade.recuperar(retornoCedenciaContratoFP.getContratoFP().getId());
        encerraVigenciaSituacaoContratoFP(contratoFP, DataUtil.getDataDiaAnterior(retornoCedenciaContratoFP.getLotacaoFuncional().getInicioVigencia()));

        SituacaoContratoFP situacaoContratoFP = getSituacaoContratoFP(contratoFP, retornoCedenciaContratoFP.getLotacaoFuncional().getInicioVigencia());

        //situacaoContratoFP = getEntityManager().merge(situacaoContratoFP);
        contratoFP.getSituacaoFuncionals().add(situacaoContratoFP);


        LotacaoFuncional lotacaoFuncionalAntiga = getUltimaLotacaoFuncional(contratoFP);
        if (lotacaoFuncionalAntiga != null && lotacaoFuncionalAntiga.getId() != null) {
            if (!retornoCedenciaContratoFP.getLotacaoFuncional().getUnidadeOrganizacional().equals(lotacaoFuncionalAntiga.getUnidadeOrganizacional())) {
                Date finalVigenciaLotacaoFuncionalAntiga = DataUtil.getDataDiaAnterior(retornoCedenciaContratoFP.getLotacaoFuncional().getInicioVigencia());
                lotacaoFuncionalAntiga.setFinalVigencia(finalVigenciaLotacaoFuncionalAntiga);
                lotacaoFuncionalAntiga.getHorarioContratoFP().setFinalVigencia(DataUtil.getDataDiaAnterior(retornoCedenciaContratoFP.getLotacaoFuncional().getHorarioContratoFP().getInicioVigencia()));
                getEntityManager().merge(lotacaoFuncionalAntiga);

                salvaHorarioContratoFP(retornoCedenciaContratoFP);
                getEntityManager().persist(lotacaoFuncional);

            } else {
                salvaHorarioContratoFP(retornoCedenciaContratoFP);
                retornoCedenciaContratoFP.setLotacaoFuncional(lotacaoFuncionalAntiga);
            }
        } else {
            salvaHorarioContratoFP(retornoCedenciaContratoFP);
            getEntityManager().persist(lotacaoFuncional);
        }

        getEntityManager().merge(contratoFP);

        getEntityManager().merge(cedenciaContratoFP);

        getEntityManager().persist(retornoCedenciaContratoFP);
    }

    private void salvaHorarioContratoFP(RetornoCedenciaContratoFP retornoCedenciaContratoFP) {
        HorarioContratoFP horarioContratoFP = retornoCedenciaContratoFP.getLotacaoFuncional().getHorarioContratoFP();
        getEntityManager().persist(horarioContratoFP);
    }

    private void encerraVigenciaSituacaoContratoFP(ContratoFP contratoFP, Date finalVigencia) {
        SituacaoContratoFP situacaoContratoFP = situacaoFuncionalFacade.recuperaSituacaoFuncionalVigente(contratoFP);
        situacaoContratoFP.setFinalVigencia(finalVigencia);
        getEntityManager().merge(situacaoContratoFP);
    }

    public void salvarRetornoServidorCedidoUE(RetornoCedenciaContratoFP retornoCedenciaContratoFP) {
        LotacaoFuncional lotacaoFuncional = retornoCedenciaContratoFP.getLotacaoFuncional();
        CedenciaContratoFP cedenciaContratoFP = retornoCedenciaContratoFP.getCedenciaContratoFP();
        if (lotacaoFuncional != null) {
            HorarioContratoFP horarioContratoFP = retornoCedenciaContratoFP.getLotacaoFuncional().getHorarioContratoFP();
            getEntityManager().merge(horarioContratoFP);
            getEntityManager().merge(lotacaoFuncional);
        }
        getEntityManager().merge(cedenciaContratoFP);
        getEntityManager().merge(retornoCedenciaContratoFP);
    }

    public void salvarNovoRetornoServidorCedidoPrefeitura(RetornoCedenciaContratoFP retornoCedenciaContratoFP) {
        ContratoFP contratoFP = retornoCedenciaContratoFP.getContratoFP();
        CedenciaContratoFP cedenciaContratoFP = retornoCedenciaContratoFP.getCedenciaContratoFP();

        getEntityManager().merge(cedenciaContratoFP);
        getEntityManager().merge(contratoFP);
        getEntityManager().persist(retornoCedenciaContratoFP);
    }

    public void salvarRetornoServidorCedidoPrefeitura(RetornoCedenciaContratoFP retornoCedenciaContratoFP) {
        ContratoFP contratoFP = retornoCedenciaContratoFP.getContratoFP();
        CedenciaContratoFP cedenciaContratoFP = retornoCedenciaContratoFP.getCedenciaContratoFP();

        getEntityManager().merge(cedenciaContratoFP);
        getEntityManager().merge(contratoFP);
        getEntityManager().merge(retornoCedenciaContratoFP);
    }

    public void removerRetornoServidorCedidoPrefeitura(RetornoCedenciaContratoFP retornoCedenciaContratoFP) {
        Object chave = Persistencia.getId(retornoCedenciaContratoFP);
        Object obj = recuperar(chave);
        if (obj != null) {
            ContratoFP contrato = retornoCedenciaContratoFP.getContratoFP();
            getEntityManager().merge(contrato);
            CedenciaContratoFP cedenciaContratoFP = retornoCedenciaContratoFP.getCedenciaContratoFP();
            getEntityManager().merge(cedenciaContratoFP);

            getEntityManager().remove(obj);
        }
    }

    public void removerRetornoServidorCedidoUE(RetornoCedenciaContratoFP retornoCedenciaContratoFP) {
        LotacaoFuncional lotacaoFuncional = retornoCedenciaContratoFP.getLotacaoFuncional();
        lotacaoFuncional = lotacaoFuncionalFacade.recuperar(lotacaoFuncional.getId());
        Object chave = Persistencia.getId(retornoCedenciaContratoFP);
        Object obj = recuperar(chave);

        if (obj != null) {
            CedenciaContratoFP cedenciaContratoFP = retornoCedenciaContratoFP.getCedenciaContratoFP();
            getEntityManager().merge(cedenciaContratoFP);
            getEntityManager().remove(obj);
        }

        lotacaoFuncionalFacade.remover(lotacaoFuncional);
    }

    public LotacaoFuncional getUltimaLotacaoFuncional(ContratoFP contratoFP) {
        LotacaoFuncional lotacaoFuncional = new LotacaoFuncional();
        if (contratoFP != null) {
            lotacaoFuncional = lotacaoFuncionalFacade.recuperarLotacaoFuncionalVigentePorContratoFP(contratoFP, lotacaoFuncionalFacade.getSistemaFacade().getDataOperacao());
        }

        return lotacaoFuncional;
    }

    public Date recuperaDataRetorno(VinculoFP vinculoFP, LocalDate dataReferencia) {
        String hql = "select max(retorno.dataRetorno) from RetornoCedenciaContratoFP retorno where retorno.contratoFP.id = :vinculoFP " +
                " and (extract(month from retorno.dataRetorno))= (extract(month from :dataParam)) " +
                "and (extract(year from retorno.dataRetorno))= (extract(year from :dataParam))";
        Query q = em.createQuery(hql);
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("dataParam", localDateToDate(dataReferencia));
        q.setMaxResults(1);
        try {
            return (Date) q.getSingleResult();
        } catch (NoResultException nr) {
            nr.printStackTrace();
            return null;

        } catch (Exception nr) {
            nr.printStackTrace();
            return null;
        }
    }

    public List<RetornoCedenciaContratoFP> buscarRetornosCedenciasPorContrato(ContratoFP contrato) {
        String sql = " select retorno.* from RETORNOCEDENCIACONTRATOFP retorno " +
            " where retorno.CONTRATOFP_ID = :contrato ";
        Query q = em.createNativeQuery(sql, RetornoCedenciaContratoFP.class);
        q.setParameter("contrato", contrato.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }
}
