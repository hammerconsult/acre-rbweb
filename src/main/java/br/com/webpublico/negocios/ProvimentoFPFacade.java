/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DetalhamentoProvimento;
import br.com.webpublico.entidadesauxiliares.RelatorioProvimentos;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author everton
 */
@Stateless
public class ProvimentoFPFacade extends AbstractFacade<ProvimentoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private CargoFacade cargoFacade;
    @EJB
    private CBOFacade cboFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public CargoFacade getCargoFacade() {
        return cargoFacade;
    }

    public CBOFacade getCboFacade() {
        return cboFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public TipoProvimentoFacade getTipoProvimentoFacade() {
        return tipoProvimentoFacade;
    }

    public LotacaoFuncionalFacade getLotacaoFuncionalFacade() {
        return lotacaoFuncionalFacade;
    }

    public ProvimentoFPFacade() {
        super(ProvimentoFP.class);
    }

    public void remover(ProvimentoFP provimento) {
        super.remover(provimento);
    }

    public List<ProvimentoFP> recuperaProvimentosPorVinculoFP(ContratoFP contratoFP) {
//        Query q = em.createQuery("select new " + new ProvimentoFP().getClass().getCanonicalName()
//                + " (provimento.vinculoFP,provimento.tipoProvimento, "
//                + " provimento.dataProvimento,atoLegal,"
//                + " provimento.observacao,provimento.sequencia) "
        Query q = em.createQuery("select provimento"
            + " from ProvimentoFP provimento "
            + " left join provimento.atoLegal atoLegal"
            //Busca Contratos, Progressões e Acesso a Cargo de confiança
            + " where provimento.vinculoFP = :contratoFP  "
            //Busca Aposentadorias
            + " or provimento.vinculoFP in "
            + " (select aposentadoria from Aposentadoria aposentadoria  "
            + "  where aposentadoria.contratoFP = :contratoFP) "
            //Busca Instituidores de Pensão
            + " or provimento.vinculoFP in "
            + " (select instituidor from Pensao pensao "
            + " inner join pensao.contratoFP instituidor "
            + "  where instituidor = :contratoFP) "
            + " order by provimento.dataProvimento, provimento.sequencia ");
        q.setParameter("contratoFP", contratoFP);
        List<ProvimentoFP> listaProvimentoFP = new ArrayList<ProvimentoFP>();
        for (ProvimentoFP p : (List<ProvimentoFP>) q.getResultList()) {
            p.setContratoFP(contratoFP);
            listaProvimentoFP.add(p);
        }
        return listaProvimentoFP;

        //        Query q = em.createQuery("select new " + new ProvimentoFP().getClass().getCanonicalName()
//                + " (provimento.vinculoFP,provimento.tipoProvimento, "
//                + " provimento.dataProvimento,provimento.atoLegal,"
//                + " provimento.observacao,provimento.sequencia,"
//                + " contratoFP,situacaoFuncional,previdenciaVinculoFP,"
//                + " enquadramentoFuncional) "
//                + " from ProvimentoFP provimento,ContratoFP contratoFP, "
//                + " PrevidenciaVinculoFP previdenciaVinculoFP, "
//                + " EnquadramentoFuncional enquadramentoFuncional "
//                + " left join contratoFP.situacaoFuncionals situacaoFuncional "
//                + " where ((provimento.vinculoFP = contratoFP) "
//                + " or (previdenciaVinculoFP.contratoFP = contratoFP)"
//                + " or(enquadramentoFuncional.contratoServidor = contratoFP)) "
//                + " and provimento.vinculoFP = :vinculoFP ");

//        Query q = em.createQuery("select new " + new ProvimentoFP().getClass().getCanonicalName()
//                + " (provimento.vinculoFP,provimento.tipoProvimento, "
//                + " provimento.dataProvimento,provimento.atoLegal,"
//                + " provimento.observacao,provimento.sequencia,"
//                + " (select contrato from ContratoFP contrato where contrato = provimento.vinculoFP),"
//                + " (select situacaoFuncional from SituacaoContratoFP situacaoContrato"
//                + " where situacaoContrato.contratoFP = provimento.vinculoFP  "
//                + " and provimento.dataProvimento >= situacaoContrato.inicioVigencia and "
//                + " provimento.dataProvimento <= coalesce(situacaoContrato.finalVigencia,provimento.dataProvimento)),"
//                + " (select previdencia from PrevidenciaVinculoFP previdencia "
//                + " where previdencia.contratoFP = provimento.vinculoFP "
//                + " and provimento.dataProvimento >= previdencia.inicioVigencia and "
//                + " provimento.dataProvimento <= coalesce(previdencia.finalVigencia,provimento.dataProvimento)),"
//                + " (select enquadramento from EnquadramentoFuncional enquadramento"
//                + "  where enquadramento.contratoServidor = provimento.vinculoFP"
//                + "  and provimento.dataProvimento >= enquadramento.inicioVigencia and "
//                + " provimento.dataProvimento <= coalesce(enquadramento.finalVigencia,provimento.dataProvimento))) "
//                + " from ProvimentoFP provimento "
//                + " where provimento.vinculoFP = :vinculoFP ");
        //Query q = em.createQuery("");
    }

    public Integer geraSequenciaPorVinculoFP(VinculoFP vinculoFP) {
        Query q = em.createQuery(" select coalesce(max(provimento.sequencia),0) from ProvimentoFP provimento "
            + " where provimento.vinculoFP = :vinculoFP");
        q.setMaxResults(1);
        q.setParameter("vinculoFP", vinculoFP);
        return ((Integer) q.getSingleResult()) + 1;
    }

    public ProvimentoFP recuperaProvimento(VinculoFP vinculoFP, TipoProvimento tipo, Date data) {
        try {
            Query q = em.createQuery(" from ProvimentoFP provimento "
                + " where provimento.tipoProvimento = :tipo "
                + " and provimento.vinculoFP = :vinculo "
                + "and to_char(provimento.dataProvimento,'dd/MM/yyyy') = :dataInicioVigencia order by provimento.dataProvimento desc");
            q.setParameter("tipo", tipo);
            q.setParameter("vinculo", vinculoFP);
            q.setParameter("dataInicioVigencia", DataUtil.getDataFormatada(data));
            q.setMaxResults(1);
            return (ProvimentoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            logger.debug("nenhum provimento encontrado para recuperaProvimento " + vinculoFP + " - " + tipo + " erro: " + nre.getMessage());
            return null;
        }
    }


    public ProvimentoFP recuperaProvimentoByTipoAndDataProvimentoDesc(VinculoFP vinculoFP, TipoProvimento tipo) {
        try {
            Query q = em.createQuery(" from ProvimentoFP provimento "
                + " where provimento.tipoProvimento = :tipo "
                + " and provimento.vinculoFP = :vinculo order by provimento.dataProvimento desc");
            q.setParameter("tipo", tipo);
            q.setParameter("vinculo", vinculoFP);
            q.setMaxResults(1);
            return (ProvimentoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            //logger.debug("nenhum provimento encontrado para recuperaProvimentoByTipoAndDataProvimentoDesc " + vinculoFP + " - " + tipo + " erro: " + nre.getMessage());
            return null;
        }
    }

    public List<ProvimentoFP> recuperaProvimentos(String matricula, String numeroVinculo) {

        String hql = "select prov from ProvimentoFP prov " +
            " inner join prov.vinculoFP vinc " +
            " inner join vinc.matriculaFP mat " +
            " where vinc.numero = :numeroVinculo " +
            " and mat.matricula = :matricula " +
            " order by prov.dataProvimento, prov.sequencia ";

        Query q = em.createQuery(hql);
        q.setParameter("matricula", matricula);
        q.setParameter("numeroVinculo", numeroVinculo);

        List<ProvimentoFP> listaProvimentoFP = q.getResultList();
        if (listaProvimentoFP != null) {
            return listaProvimentoFP;
        }
        return new ArrayList<ProvimentoFP>();
    }

    public ProvimentoFP recuperarProvimentoAnterior(ProvimentoFP p) {
        String hql = "select p from ProvimentoFP p" +
            " inner join p.vinculoFP v" +
            " where v = :vinculo and p.sequencia = :seq";
        Query q = em.createQuery(hql);
        q.setParameter("vinculo", p.getContratoFP());
        q.setParameter("seq", p.getSequencia() - 1);
        q.setMaxResults(1);
        try {
            return (ProvimentoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public boolean isUltimoProvimentoVinculoDoTipo(Long idVinculoFP, int codigoTipoProvimento) {
        String sql = "     select tp.codigo" +
            "       from provimentofp p" +
            " inner join tipoprovimento tp on tp.id = p.tipoprovimento_id" +
            "      where p.vinculofp_id = :id" +
            "   order by p.dataprovimento desc, p.sequencia desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", idVinculoFP);
        q.setMaxResults(1);
        try {
            return Integer.parseInt("" + q.getSingleResult()) == codigoTipoProvimento;
        } catch (NoResultException nre) {
            return false;
        }
    }

    public ProvimentoFP buscarUltimoProvimentoFPDoTipoProgressao(Long idVinculoFP, int codigoTipoProvimento) {
        String sql = " select P.* from PROVIMENTOFP P " +
            " INNER JOIN ENQUADRAMENTOFUNCIONAL E ON P.ID = E.PROVIMENTOFP_ID " +
            " INNER JOIN TIPOPROVIMENTO T ON T.ID = P.TIPOPROVIMENTO_ID" +
            " WHERE E.CONTRATOSERVIDOR_ID = :idVinculoFP " +
            " AND T.CODIGO = :codigoTipoProvimento " +
            " AND P.DATAPROVIMENTO = (SELECT MAX(PF.DATAPROVIMENTO) FROM PROVIMENTOFP PF " +
            " WHERE PF.VINCULOFP_ID = P.VINCULOFP_ID " +
            " AND PF.TIPOPROVIMENTO_ID = P.TIPOPROVIMENTO_ID)";
        Query q = em.createNativeQuery(sql, ProvimentoFP.class);
        q.setParameter("idVinculoFP", idVinculoFP);
        q.setParameter("codigoTipoProvimento", codigoTipoProvimento);
        if (q.getResultList().isEmpty()) {
            logger.debug("Nenhum provimento para o código " + codigoTipoProvimento + " buscando por provimento de nomeação.");
            if (codigoTipoProvimento != TipoProvimento.NOMEACAO) {
                return buscarUltimoProvimentoFPDoTipoProgressao(idVinculoFP, TipoProvimento.NOMEACAO);
            } else {
                return null;
            }
        }
        return (ProvimentoFP) q.getResultList().get(0);
    }

    public Integer buscarQuantidadeDeProvimentoPorTipoProvimento(Date inicio, int tipoProvimento) {
        TipoProvimento tipo = tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(tipoProvimento);
        if (tipo == null) {
            throw new ExcecaoNegocioGenerica("Nenhum provimento encontratdo com o código: " + tipo);
        }
        Integer total = 0;
        String hql = "select count(distinct r.id) from ProvimentoFP_aud r inner join RevisaoAuditoria rev on rev.id= r.rev inner join TipoProvimento tipo on tipo.id = r.tipoProvimento_id where r.revtype = 0 and to_date(to_char(rev.datahora,'dd/MM/yyyy'),'dd/MM/yyyy') =  :data and tipo.codigo = :codigo";
        Query q = em.createNativeQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        q.setParameter("codigo", tipoProvimento);
        if (q.getResultList().isEmpty()) {
            return total;
        }
        BigDecimal bg = (BigDecimal) q.getSingleResult();
        total = bg.intValue();
        return total;
    }

}
