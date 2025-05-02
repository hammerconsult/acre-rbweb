/*
 * Codigo gerado automaticamente em Fri Apr 20 16:12:42 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class RetornoFuncaoGratificadaFacade extends AbstractFacade<RetornoFuncaoGratificada> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private FuncaoGratificadaFacade funcaoGratificadaFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RetornoFuncaoGratificadaFacade() {
        super(RetornoFuncaoGratificada.class);
    }

    public FichaFinanceiraFPFacade getFichaFinanceiraFPFacade() {
        return fichaFinanceiraFPFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public void salvar(RetornoFuncaoGratificada entity) {
        FuncaoGratificada fg = funcaoGratificadaFacade.recuperaFuncaoGratificadaVigente(entity.getContratoFP(), entity.getDataRetorno());
        if (fg != null) {
            fg.setFinalVigencia(entity.getDataRetorno());
        }
        em.persist(fg);
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(RetornoFuncaoGratificada entity) {
        FuncaoGratificada fg = funcaoGratificadaFacade.recuperaFuncaoGratificadaVigente(entity.getContratoFP(), sistemaFacade.getDataOperacao());
        if (fg != null) {
            fg.setFinalVigencia(entity.getDataRetorno());
        }

        em.persist(fg);


        ProvimentoFP provimentoFP = new ProvimentoFP();
        provimentoFP.setVinculoFP(entity.getContratoFP());
        provimentoFP.setDataProvimento(entity.getDataRetorno());
        provimentoFP.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.EXONERACAO_DE_FUNCAO_GRATIFICADA));
        provimentoFP.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(entity.getContratoFP()));
        em.persist(provimentoFP);

        provimentoFP = (ProvimentoFP) recuperar(ProvimentoFP.class, provimentoFP.getId());

        entity.setProvimentoFP(provimentoFP);

        super.salvarNovo(entity);
    }

    public Integer buscarQuantidadeDeServidoresRetornoAcessoFG(Date inicio) {
        Integer total = 0;
        String hql = "select count(distinct r.id) from RetornoFuncaoGratificada_aud r inner join revisaoAuditoria rev on rev.id= r.rev where r.revtype = 0 and to_date(to_char(rev.datahora,'dd/MM/yyyy'),'dd/MM/yyyy') =  :data ";
        Query q = em.createNativeQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        BigDecimal bg = (BigDecimal) q.getSingleResult();
        total = bg.intValue();
        return total;
    }

    public List<RetornoFuncaoGratificada> buscarRetornosFuncaoGratificadaPorContrato(ContratoFP contrato) {
        String sql = " select r.* from RETORNOFUNCAOGRATIFICADA r " +
            " where r.CONTRATOFP_ID = :contrato ";
        Query q = em.createNativeQuery(sql, RetornoFuncaoGratificada.class);
        q.setParameter("contrato", contrato.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }
}
