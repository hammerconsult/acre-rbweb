/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.seguranca.service.QueryReprocessamentoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author venon
 */
@Stateless
public class SubContaFacade extends AbstractFacade<SubConta> {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private ConvenioReceitaFacade convenioReceitaFacade;
    @EJB
    private PlanoDeContasFacade planoDeContasFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private ContaFacade contaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public SubContaFacade() {
        super(SubConta.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConvenioReceitaFacade getConvenioReceitaFacade() {
        return convenioReceitaFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    public List<ContaBancariaEntidade> listaContaBancariaEntidade(String parte) {
        return contaBancariaEntidadeFacade.listaPorSituacao(parte, SituacaoConta.ATIVO);
    }

    public List<SubContaUniOrg> listaUnidadesPorSubConta(SubConta sub) {
        String hql = "from SubContaUniOrg su where su.subConta = :subconta";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("subconta", sub);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<SubContaUniOrg>();
        } else {
            return q.getResultList();
        }
    }

    public List<SubContaFonteRec> listaFontesPorSubConta(SubConta sub) {
        String hql = "select su from SubContaFonteRec su inner join su.fonteDeRecursos fr where su.subConta = :subconta order by fr.exercicio.ano asc";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("subconta", sub);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<SubContaFonteRec>();
        } else {
            return q.getResultList();
        }
    }

    public List<SubConta> listaFiltrandoSubContaPorTipoUnidades(String parte, Exercicio ex, List<Long> listaUni, TipoRecursoSubConta tipoRecursoSubConta) {
        String sql = "SELECT sc FROM SubConta sc "
            + " INNER JOIN sc.unidadesOrganizacionais uni "
            + " WHERE uni.unidadeOrganizacional.id IN (:unidades)"
            + " AND sc.tipoRecursoSubConta = :tipoRecurso "
            + " AND(sc.codigo LIKE :parte OR LOWER(sc.descricao) LIKE :parte) "
            + " AND uni.exercicio.id = :exercicio "
            + " AND sc.situacao = 'ATIVO' ";
        Query q = em.createQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("exercicio", ex.getId());
        q.setParameter("unidades", listaUni);
        q.setParameter("tipoRecurso", tipoRecursoSubConta);
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return new ArrayList<SubConta>();
        }
    }

    public List<SubConta> listaFiltrandoSubContaPorUnidades(String parte, Exercicio ex, List<Long> listaUni) {
        String sql = "SELECT sc FROM SubConta sc "
            + " INNER JOIN sc.unidadesOrganizacionais uni "
            + " WHERE uni.unidadeOrganizacional.id IN (:unidades)"
            + " AND(sc.codigo LIKE :parte OR LOWER(sc.descricao) LIKE :parte) "
            + " AND uni.exercicio.id = :exercicio "
            + " AND sc.situacao = 'ATIVO' ";
        Query q = em.createQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("exercicio", ex.getId());
        q.setParameter("unidades", listaUni);
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return new ArrayList<SubConta>();
        }
    }

    public List<SubConta> listaContaFinanceiraOperacaoDeCredito(String parte, Exercicio ex, List<Long> listaUni) {
        String sql = "SELECT sc FROM SubConta sc "
            + " INNER JOIN sc.unidadesOrganizacionais uni "
            + " WHERE uni.unidadeOrganizacional.id IN (:unidades)"
            + " AND (sc.tipoRecursoSubConta = '" + TipoRecursoSubConta.OPERACAO_CREDITO.name() + "'"
            + "   OR sc.tipoRecursoSubConta = '" + TipoRecursoSubConta.RECURSO_TESOURO.name() + "')"
            + " AND(sc.codigo LIKE :parte OR LOWER(sc.descricao) LIKE :parte) "
            + " AND uni.exercicio.id = :exercicio "
            + " AND sc.situacao = 'ATIVO' ";
        Query q = em.createQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("exercicio", ex.getId());
        q.setParameter("unidades", listaUni);
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return new ArrayList<SubConta>();
        }
    }

    public List<SubConta> listaContaFinanceiraDividaPublica(String parte, Exercicio ex, List<Long> listaUni) {
        String sql = "SELECT sc FROM SubConta sc "
            + " INNER JOIN sc.unidadesOrganizacionais uni "
            + " WHERE uni.unidadeOrganizacional.id IN (:unidades)"
            + " AND (sc.tipoRecursoSubConta = '" + TipoRecursoSubConta.RECURSO_UNIDADE.name() + "'"
            + "   OR sc.tipoRecursoSubConta = '" + TipoRecursoSubConta.RECURSO_TESOURO.name() + "')"
            + " AND(sc.codigo LIKE :parte OR LOWER(sc.descricao) LIKE :parte) "
            + " AND uni.exercicio.id = :exercicio "
            + " AND sc.situacao = 'ATIVO' ";
        Query q = em.createQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("exercicio", ex.getId());
        q.setParameter("unidades", listaUni);
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return new ArrayList<SubConta>();
        }
    }

    public List<SubConta> buscarContasFinanceirasPrecatorios(String parte, Exercicio ex, List<Long> unidades) {
        String sql = "SELECT sc FROM SubConta sc "
            + " INNER JOIN sc.unidadesOrganizacionais uni "
            + " WHERE uni.unidadeOrganizacional.id IN (:unidades)"
            + " AND sc.tipoRecursoSubConta in (:tiposSubConta) "
            + " AND(sc.codigo LIKE :parte OR LOWER(sc.descricao) LIKE :parte) "
            + " AND uni.exercicio.id = :exercicio "
            + " AND sc.situacao = 'ATIVO' ";
        Query q = em.createQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("exercicio", ex.getId());
        q.setParameter("unidades", unidades);
        List<TipoRecursoSubConta> tiposSubConta = Lists.newArrayList();
        tiposSubConta.add(TipoRecursoSubConta.RECURSO_UNIDADE);
        tiposSubConta.add(TipoRecursoSubConta.RECURSO_TESOURO);
        tiposSubConta.add(TipoRecursoSubConta.OPERACAO_CREDITO);
        q.setParameter("tiposSubConta", tiposSubConta);
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        return q.getResultList();
    }

    public List<SubConta> listaPorTipoRecursoUnidade(String parte, TipoRecursoSubConta tipoRecurso, UnidadeOrganizacional uo) {
        String sql = "SELECT SC.* "
            + " FROM SUBCONTA SC "
            + " INNER JOIN SUBCONTAUNIORG UNI ON SC.ID = UNI.SUBCONTA_ID AND UNI.UNIDADEORGANIZACIONAL_ID = :param "
            + " WHERE SC.TIPORECURSOSUBCONTA = :tipoRecurso "
            + " AND ((LOWER (SC.CODIGO) LIKE :parte) OR (LOWER (SC.DESCRICAO) LIKE :parte)) AND ROWNUM <= 10 AND SC.SITUACAO = 'ATIVO'";
        Query q = em.createNativeQuery(sql, SubConta.class);
        q.setParameter("param", uo.getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("tipoRecurso", tipoRecurso.name());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<SubConta>();
        } else {
            return q.getResultList();
        }
    }

    public List<SubConta> listaPorContaEUnidadeOrganizacional(String parte, UnidadeOrganizacional uni) {
        String sql = "SELECT SUB.* FROM SUBCONTAUNIORG SCU "
            + " INNER JOIN SUBCONTA SUB ON SCU.SUBCONTA_ID = SUB.ID"
            //+ " INNER JOIN TIPOCONTAFINANCEIRA TCF ON SUB.TIPOCONTAFINANCEIRA_ID = TCF.ID AND TCF.PAGAMENTO = 1"
            + " INNER JOIN UNIDADEORGANIZACIONAL UNI ON SCU.UNIDADEORGANIZACIONAL_ID = UNI.ID"
            + " WHERE UNI.ID = :uni"
            + " AND (SUB.CODIGO LIKE :parte OR lower(SUB.DESCRICAO) LIKE :parte) ";
        Query q = em.createNativeQuery(sql, SubConta.class);
        q.setParameter("uni", uni.getId());
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<SubConta>();
        } else {
            return q.getResultList();
        }
    }

    public List<SubConta> listaPorUnidadeOrganizacionalFonteRec(String parte, UnidadeOrganizacional uni, Exercicio exe, FonteDeRecursos font, Date data) {
        String sql = "SELECT distinct SUB.* FROM vwhierarquiaadministrativa VW "
            + " inner join HIERARQUIAORGANIZACIONAL hadm on hadm.id=VW.ID "
            + " inner join vwhierarquiaorcamentaria vworc on hadm.hierarquiaorc_id = vworc.id "
            + " inner join unidadeorganizacional u on u.id = vworc.subordinada_id AND U.ID = :uni "
            + " inner join SUBCONTAUNIORG SCU on scu.unidadeorganizacional_id = u.id "
            + " INNER JOIN SUBCONTA SUB ON SCU.SUBCONTA_ID = SUB.ID "
            + " inner join subcontafonterec subf on sub.id = subf.subconta_id "
            + " inner join fontederecursos font on subf.fontederecursos_id = font.id "
            + " where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia  and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy')) "
            + " and to_date(:data, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:data, 'dd/MM/yyyy')) "
            + " and (SUB.CODIGO LIKE :parte OR LOWER(SUB.DESCRICAO) LIKE :parte)"
            + " and (SCU.EXERCICIO_ID = :exe) and font.id = :font "
            + " and SUB.SITUACAO = 'ATIVO' ";
        Query q = em.createNativeQuery(sql, SubConta.class);
        q.setParameter("uni", uni.getId());
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(data));
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("exe", exe.getId());
        q.setParameter("font", font.getId());
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<SubConta>();
        } else {
            return q.getResultList();
        }
    }

    public List<SubConta> listaPorExercicio(String parte, Exercicio exer) {
        String sql = "SELECT SUB.* FROM SUBCONTA SUB "
            + " INNER JOIN subcontauniorg SUC on sub.id = SUC.subconta_id "
            + " WHERE (SUB.CODIGO LIKE :parte OR LOWER(SUB.DESCRICAO) LIKE :parte) "
            + " and suc.exercicio_id = :exer "
            + " and SUB.SITUACAO = 'ATIVO' ";
        Query q = em.createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("exer", exer.getId());
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<SubConta>();
        } else {
            return q.getResultList();
        }
    }

    public List<SubConta> listaPorUnidadeOrganizacional(String parte, UnidadeOrganizacional uni, Exercicio exe, Date data) {
        String sql = "SELECT distinct SUB.* FROM unidadeorganizacional u" +
            " inner join hierarquiaorganizacional ho on u.id = ho.subordinada_id" +
            "        and ho.tipohierarquiaorganizacional = 'ORCAMENTARIA'" +
            "        and to_date(:data, 'dd/MM/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date(:data, 'dd/MM/yyyy'))" +
            " inner join SUBCONTAUNIORG SCU on scu.unidadeorganizacional_id = u.id" +
            " INNER JOIN SUBCONTA SUB ON SCU.SUBCONTA_ID = SUB.ID" +
            " where (SUB.CODIGO LIKE :parte OR LOWER(SUB.DESCRICAO) LIKE :parte)"
            + " and (SCU.EXERCICIO_ID = :exe) "
            + " and SUB.SITUACAO = 'ATIVO' "
            + "   and U.ID = :uni ";
        Query q = em.createNativeQuery(sql, SubConta.class);
        q.setParameter("uni", uni.getId());
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(data));
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("exe", exe.getId());
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<SubConta>();
        } else {
            return q.getResultList();
        }
    }

    public List<SubConta> listaPorUnidadeOrganizacionalEBanco(String parte, UnidadeOrganizacional uni, Exercicio exe, Date data, Banco banco) {
        String sql = "SELECT distinct SUB.* FROM unidadeorganizacional u"
            + "  INNER JOIN hierarquiaorganizacional ho on u.id = ho.subordinada_id"
            + "        AND ho.tipohierarquiaorganizacional = 'ORCAMENTARIA'"
            + "        AND to_date(:data, 'dd/MM/yyyy') BETWEEN ho.iniciovigencia  AND COALESCE(ho.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
            + "  INNER JOIN SUBCONTAUNIORG SCU ON scu.unidadeorganizacional_id = u.id "
            + "  INNER JOIN SUBCONTA SUB ON SCU.SUBCONTA_ID = SUB.ID "
            + "  INNER JOIN CONTABANCARIAENTIDADE CO ON CO.ID = SUB.CONTABANCARIAENTIDADE_ID "
            + "  INNER JOIN AGENCIA AG ON AG.ID = CO.AGENCIA_ID "
            + "WHERE (SUB.CODIGO LIKE :parte OR LOWER(SUB.DESCRICAO) LIKE :parte)"
            + "  AND SCU.EXERCICIO_ID = :exe "
            + "  AND SUB.SITUACAO = 'ATIVO' "
            + "  AND U.ID = :uni "
            + "  AND AG.BANCO_ID = :banco ";
        Query q = em.createNativeQuery(sql, SubConta.class);
        q.setParameter("uni", uni.getId());
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(data));
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("exe", exe.getId());
        q.setParameter("banco", banco.getId());
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<SubConta>();
        } else {
            return q.getResultList();
        }
    }
//    public List<SubConta> listaPorUnidadeOrganizacional(String parte, UnidadeOrganizacional uni) {
//        String sql = "SELECT distinct SUB.* FROM SUBCONTAUNIORG SCU "
//                + " INNER JOIN SUBCONTA SUB ON SCU.SUBCONTA_ID = SUB.ID"
//                + " INNER JOIN UNIDADEORGANIZACIONAL UNI ON SCU.UNIDADEORGANIZACIONAL_ID = UNI.ID AND UNI.ID= :uni"
//                + " WHERE (SUB.CODIGO LIKE :parte OR LOWER(SUB.DESCRICAO) LIKE :parte) AND SUB.SITUACAO = 'ATIVO'";
//        Query q = em.createNativeQuery(sql, SubConta.class);
//        q.setParameter("uni", uni.getId());
//        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
//        q.setMaxResults(10);
//        if (q.getResultList().isEmpty()) {
//            return new ArrayList<SubConta>();
//        } else {
//            return q.getResultList();
//        }
//    }

    public List<SubConta> listaPorUnidadeOrganizacionalTipoRecurso(String parte, UnidadeOrganizacional uni, TipoRecursoSubConta tipo) {
        String sql = "SELECT distinct SUB.* FROM SUBCONTAUNIORG SCU "
            + " INNER JOIN SUBCONTA SUB ON SCU.SUBCONTA_ID = SUB.ID"
            + " INNER JOIN UNIDADEORGANIZACIONAL UNI ON SCU.UNIDADEORGANIZACIONAL_ID = UNI.ID AND UNI.ID= :uni"
            + " WHERE (SUB.CODIGO LIKE :parte OR lower(SUB.DESCRICAO) LIKE :parte)"
            + " AND SUB.tipoRecursoSubConta = :tipo"
            + " AND SUB.SITUACAO = 'ATIVO'";
        Query q = em.createNativeQuery(sql, SubConta.class);
        q.setParameter("uni", uni.getId());
        q.setParameter("tipo", tipo.name());
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<SubConta>();
        } else {
            return q.getResultList();
        }
    }

    public List<SubConta> buscarContasFinanceiraPorUnidadeTipoContaDeDestinacao(String parte, UnidadeOrganizacional uni, TipoRecursoSubConta tipo, ContaDeDestinacao contaDeDestinacao) {
        String sql = "SELECT DISTINCT SUB.* FROM SUBCONTA SUB "
            + " INNER JOIN CONTABANCARIAENTIDADE CBE ON SUB.CONTABANCARIAENTIDADE_ID = CBE.ID";
        if (uni != null) {
            sql = sql + " INNER JOIN SUBCONTAUNIORG SCU ON SCU.SUBCONTA_ID = SUB.ID AND SCU.UNIDADEORGANIZACIONAL_ID = :uni ";
        }
        if (contaDeDestinacao != null) {
            sql = sql + " INNER JOIN SUBCONTAFONTEREC FON ON FON.SUBCONTA_ID = SUB.ID AND FON.contaDeDestinacao_ID = :contaDeDestinacao ";
        }
        sql = sql + " WHERE SUB.SITUACAO = 'ATIVO' and (SUB.CODIGO LIKE :parte OR lower(SUB.DESCRICAO) LIKE :parte)";
        if (tipo != null) {
            sql = sql + " AND SUB.TIPORECURSOSUBCONTA = :tipo ";
        }
        Query q = em.createNativeQuery(sql, SubConta.class);
        if (uni != null) {
            q.setParameter("uni", uni.getId());
        }
        if (tipo != null) {
            q.setParameter("tipo", tipo.name());
        }
        if (contaDeDestinacao != null) {
            q.setParameter("contaDeDestinacao", contaDeDestinacao.getId());
        }
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        List<SubConta> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return resultado;
        }
    }

    public UnidadeOrganizacional recuperarUnidadeDaSubConta(SubConta subConta, Exercicio exercicio) {
        try {
            String sql = " SELECT UNID.* FROM SUBCONTA SUB " +
                "      INNER JOIN SUBCONTAUNIORG SUO ON SUO.SUBCONTA_ID = SUB.ID " +
                "      INNER JOIN UNIDADEORGANIZACIONAL UNID ON UNID.ID = SUO.UNIDADEORGANIZACIONAL_ID " +
                "        WHERE SUB.ID = :subConta " +
                "        AND SUO.EXERCICIO_ID = :exercicio ";
            Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
            q.setParameter("subConta", subConta.getId());
            q.setParameter("exercicio", exercicio.getId());
            q.setMaxResults(1);
            return (UnidadeOrganizacional) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public SubConta recuperaMaster(ContaBancariaEntidade cbe) {
        String hql = "from SubConta sub where sub.contaBancariaEntidade = :cbe";
        Query q = em.createQuery(hql);
        q.setParameter("cbe", cbe);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return new SubConta();
        } else {
            return (SubConta) q.getSingleResult();
        }
    }

    public List<SubConta> listaFiltrandoSubContasPorBanco(Banco banco, String trim) {
        StringBuilder hql = new StringBuilder();
        hql.append(" from SubConta c where c.contaBancariaEntidade.agencia.banco = :banco and c.codigo like :numeroConta and c.situacao = 'ATIVO'");
        Query q = em.createQuery(hql.toString());
        q.setParameter("banco", banco);
        q.setParameter("numeroConta", "%" + trim + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<SubConta> listaFiltrandoSubContasPorTipoRecurso(String parte, TipoRecursoSubConta tipo) {
        StringBuilder hql = new StringBuilder();
        hql.append(" from SubConta c where c.tipoRecursoSubConta = :tipo "
            + " and (c.codigo like :parte or lower(c.descricao) like :parte) and c.situacao = 'ATIVO'");
        Query q = em.createQuery(hql.toString());
        q.setParameter("tipo", tipo);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<SubConta> listaFiltrandoSubContasPorTipoRecursoExercicio(String parte, TipoRecursoSubConta tipo, Exercicio ex) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select c from SubConta c  "
                + " inner join c.subContaFonteRecs sub "
                + " inner join sub.fonteDeRecursos fonte "
                + " where (c.codigo like :parte or lower(c.descricao) like :parte) "
                + " and c.situacao = 'ATIVO' "
                + " and fonte.tipoFonteRecurso = '")
            .append(tipo.name())
            .append("' and fonte.exercicio = :exercicio");
        Query q = em.createQuery(hql.toString(), SubConta.class);
        q.setParameter("exercicio", ex);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<SubConta> listaPorContaBancEntidade(ContaBancariaEntidade conta) {
        String sql = "SELECT distinct SUB.* FROM SUBCONTA SUB "
            + " INNER JOIN TIPOCONTAFINANCEIRA TCF ON SUB.TIPOCONTAFINANCEIRA_ID = TCF.ID"
            + " WHERE SUB.CONTABANCARIAENTIDADE_ID = :parte AND TCF.ARRECADACAO = 1 ";
        Query q = em.createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", conta.getId());
        List<SubConta> l = q.getResultList();
        if (l.isEmpty()) {
            return new ArrayList<SubConta>();
        } else {
            return l;
        }
    }

    public List<SubConta> listaFiltrandoSubConta(String parte) {
        String sql = " SELECT SC.* "
            + " FROM SUBCONTA SC "
            + " WHERE ((LOWER (SC.DESCRICAO) LIKE :parte) OR (LOWER (SC.CODIGO) LIKE :parte)) "
            + " AND SC.SITUACAO = 'ATIVO'";
        Query q = em.createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<SubConta> listaPorContaBancariaEntidade(String parte, ContaBancariaEntidade cbe) {
        String sql = "SELECT sc.* FROM subconta sc "
            + "WHERE sc.contabancariaentidade_id = :cbe "
            + "AND (sc.codigo LIKE :parte OR lower(sc.descricao) LIKE :parte) AND SC.SITUACAO = 'ATIVO'";
        Query q = getEntityManager().createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("cbe", cbe.getId());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<SubConta> listaPorContaBancariaEntidade(ContaBancariaEntidade cbe) {
        String sql = "SELECT sc.* FROM subconta sc "
            + "WHERE sc.contabancariaentidade_id = :cbe ";
        Query q = getEntityManager().createNativeQuery(sql, SubConta.class);
        q.setParameter("cbe", cbe.getId());
        return q.getResultList();
    }

    public List<SubConta> listaPorContaBancariaEntidadeOuTodos(String parte, ContaBancariaEntidade cbe) {
        String sql = "SELECT sc.* FROM subconta sc "
            + "WHERE (sc.codigo LIKE :parte OR lower(sc.descricao) LIKE :parte) AND SC.SITUACAO = 'ATIVO' ";
        if (cbe != null) {
            sql += " and sc.contabancariaentidade_id = :cbe ";
        }
        sql += " order by sc.codigo";
        Query q = getEntityManager().createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        if (cbe != null) {
            q.setParameter("cbe", cbe.getId());
        }
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<SubConta> listaPorContaBancariaEntidadeDoUsuarioLogadoOuTodas(String parte, ContaBancariaEntidade cbe, UnidadeOrganizacional unidade, Exercicio exercicio, UsuarioSistema usuarioSistema, Date data) {
        String sql = " SELECT DISTINCT SUB.* FROM SUBCONTA SUB "
            + " INNER JOIN SUBCONTAUNIORG SC ON SC.SUBCONTA_ID = SUB.ID "
            + " INNER JOIN UNIDADEORGANIZACIONAL UNID ON UNID.ID = SC.UNIDADEORGANIZACIONAL_ID "
            + " INNER JOIN HIERARQUIAORGANIZACIONAL HOORC ON UNID.ID = HOORC.SUBORDINADA_ID "
            + " AND HOORC.TIPOHIERARQUIAORGANIZACIONAL = 'ORCAMENTARIA' AND TO_DATE(:data, 'dd/MM/yyyy') BETWEEN HOORC.INICIOVIGENCIA AND COALESCE(HOORC.FIMVIGENCIA, TO_DATE(:data, 'dd/MM/yyyy')) "
            + " INNER JOIN usuariounidadeorganizacorc UUO ON UUO.UNIDADEORGANIZACIONAL_ID = hoorc.SUBORDINADA_ID "
            + " INNER JOIN USUARIOSISTEMA USU ON UUO.USUARIOSISTEMA_ID = USU.ID "
            + " WHERE (SUB.codigo LIKE :parte OR lower(SUB.descricao) LIKE :parte)  "
            + " AND SC.EXERCICIO_ID = :exercicio "
            + " AND UUO.USUARIOSISTEMA_ID = :usuario ";
        if (cbe != null) {
            sql += " AND SUB.contabancariaentidade_id = :cbe ";
        }
        sql += " order by sub.codigo ";

        Query q = getEntityManager().createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("usuario", usuarioSistema.getId());
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(data));
        if (cbe != null) {
            q.setParameter("cbe", cbe.getId());
        }
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<SubConta>();
        }
        return q.getResultList();
    }

    public List<SubConta> listaPorContaBancariaEntidadeOuTodosPorUnidadesLogadas(String parte, ContaBancariaEntidade cbe, String unidades) {
        String sql = "SELECT sc.* FROM subconta sc "
            + "WHERE (sc.codigo LIKE :parte OR lower(sc.descricao) LIKE :parte) AND SC.SITUACAO = 'ATIVO' ";
        if (cbe != null) {
            sql += " and sc.contabancariaentidade_id = :cbe ";
        }
        Query q = getEntityManager().createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        if (cbe != null) {
            q.setParameter("cbe", cbe.getId());
        }
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<SubConta> listaPorContaBancariaEntidadeTransferencia(String parte, ContaBancariaEntidade cbe) {
        String sql = "SELECT distinct sc.* FROM subconta sc "
            + "INNER JOIN tipocontafinanceira tcf ON sc.tipocontafinanceira_id = tcf.id"
            + "where sc.contabancariaentidade_id = :cbe "
            + "AND tcf.transferencia = 1 "
            + "AND (sc.codigo LIKE :parte OR lower(sc.descricao) LIKE :parte)"
            + "AND SC.SITUACAO = 'ATIVO'";
        Query q = getEntityManager().createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("cbe", cbe.getId());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<SubConta> listaTodas(String parte) {
        String sql = "SELECT sc.* FROM subconta sc "
            + " WHERE (sc.codigo LIKE :parte OR lower(sc.descricao) LIKE :parte) "
            + " AND SC.SITUACAO = 'ATIVO' ";
        Query q = getEntityManager().createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public SubConta recuperaSubContaArrecadacao(String banco, String agencia, String numeroConta) {
        String sql = "SELECT sc.* FROM SubConta sc "
            + "INNER JOIN ContaBancariaEntidade cbe ON cbe.id = sc.contaBancariaEntidade_id "
            + "INNER JOIN Agencia ag ON ag.id = cbe.agencia_id "
            + "INNER JOIN Banco b ON b.id = ag.banco_id "
            + "INNER JOIN TipoContaFinanceira tpc ON tpc.id = sc.tipoContaFinanceira_id "
            + "WHERE tpc.arrecadacao = 1 AND "
            + "concat(trim(b.numeroBanco), trim(b.digitoVerificador)) = :banco AND "
            + "concat(trim(ag.numeroAgencia), trim(ag.digitoVerificador)) = :agencia AND "
            + "concat(trim(cbe.numeroConta), trim(cbe.digitoVerificador)) = :conta";
        Query q = em.createNativeQuery(sql, SubConta.class);
        q.setParameter("banco", banco.trim());
        q.setParameter("agencia", agencia.trim());
        q.setParameter("conta", numeroConta.trim());
        List<SubConta> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return null;
        } else if (resultado.size() > 0) {
            //System.out.println("recuperaSubContaMasterDeConta - mais de uma conta master encontrada para String " + banco + " - " + agencia + " - " + numeroConta + ": " + resultado + ". retornando 1o registro");
        }
        return resultado.get(0);
    }

    @Override
    public SubConta recuperar(Object id) {
        SubConta rl = em.find(SubConta.class, id);
        rl.getSubContaFonteRecs().size();
        rl.getUnidadesOrganizacionais().size();
        rl.getConvenioReceitas().size();
        return rl;
    }

    public SubConta recuperaPorConta(String codigo) {
        String hql = "from SubConta s where s.codigo = :codigo";
        Query q = em.createQuery(hql).setParameter("codigo", codigo);
        if (!q.getResultList().isEmpty()) {
            return (SubConta) q.getResultList().get(0);
        }
        return null;
    }

    public boolean validaSubContaMesmoCodigo(SubConta subConta) {
        String sql = " from SubConta sub where sub.codigo = :codigo ";
        if (subConta.getId() != null) {
            sql += " and sub.id <> :id ";
        }

        Query consulta = em.createQuery(sql);
        consulta.setParameter("codigo", subConta.getCodigo().trim());
        if (subConta.getId() != null) {
            consulta.setParameter("id", subConta.getId());
        }

        if (consulta.getResultList().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public List<SubConta> lista() {
        String hql = "from SubConta obj " +
            "order by obj.contaBancariaEntidade.agencia.banco.numeroBanco," +
            " obj.contaBancariaEntidade.agencia.banco.descricao," +
            " obj.contaBancariaEntidade.agencia.numeroAgencia, " +
            " obj.contaBancariaEntidade.agencia.digitoVerificador, " +
            " obj.contaBancariaEntidade.agencia.nomeAgencia, " +
            " obj.contaBancariaEntidade.numeroConta, " +
            " obj.contaBancariaEntidade.digitoVerificador, " +
            " obj.contaBancariaEntidade.nomeConta, " +
            " obj.codigo ";
        Query q = getEntityManager().createQuery(hql);

        return q.getResultList();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public SubConta salvarRetornando(SubConta entity) {
        return em.merge(entity);
    }

    public void salvarNovaContaFinanceira(SubConta entity, Notificacao notificacao) {
        em.persist(entity);
        salvarUnidadeFonteConvenioReceita(entity);
        salvarNotificaoCriacaoContaFinanceiraParaConvenio(entity);
        if (notificacao != null) {
            NotificacaoService.getService().marcarComoLida(notificacao);
        }
    }

    public void salvarUnidadeFonteConvenioReceita(SubConta entity) {
        if (TipoRecursoSubConta.CONVENIO_CONGENERE.equals(entity.getTipoRecursoSubConta())) {
            for (ConvenioReceitaSubConta convenioReceitaSubConta : entity.getConvenioReceitas()) {
                ConvenioReceita convenioReceita = convenioReceitaFacade.recuperar(convenioReceitaSubConta.getConvenioReceita().getId());

                //UNIDADES ORCAMENTARIS DO CONVENIO DE RECEITA
                List<UnidadeOrganizacional> unidadesOrganizacionaisConvenioReceita = getUnidadesOrganizacionaisConvenioReceita(convenioReceita);

                for (SubContaUniOrg subContaUniOrg : entity.getUnidadesOrganizacionais()) {
                    if (!unidadesOrganizacionaisConvenioReceita.contains(subContaUniOrg.getUnidadeOrganizacional())) {
                        ConvenioReceitaUnidade convenioReceitaUnidade = new ConvenioReceitaUnidade();
                        convenioReceitaUnidade.setConvenioReceita(convenioReceita);
                        convenioReceitaUnidade.setUnidadeOrganizacional(subContaUniOrg.getUnidadeOrganizacional());
                        convenioReceitaUnidade.setExercicio(subContaUniOrg.getExercicio());
                        em.persist(convenioReceitaUnidade);
                    }
                }

                //FONTES DE RECURSOS DO CONVENIO
                List<FonteDeRecursos> fontesConvenioReceita = getFonteRecursoCnvenioReceita(convenioReceita);
                for (SubContaFonteRec subContaFonteRec : entity.getSubContaFonteRecs()) {
                    if (!fontesConvenioReceita.contains(subContaFonteRec.getFonteDeRecursos())) {
                        Conta contaDestinacaoDaFonte = getContaDestinacaoDaFonte(subContaFonteRec.getFonteDeRecursos());
                        if (contaDestinacaoDaFonte != null) {
                            ConvenioRecConta conta = new ConvenioRecConta();
                            conta.setConvenioReceita(convenioReceita);
                            conta.setConta(contaDestinacaoDaFonte);
                            em.persist(conta);
                        }
                    }
                }
            }
        }
    }

    private Conta getContaDestinacaoDaFonte(FonteDeRecursos fonteDeRecursos) {
        try {
            Exercicio exercicioCorrente = sistemaFacade.getExercicioCorrente();
            return planoDeContasFacade.recuperarContaDestinacaoPorFonte(fonteDeRecursos, exercicioCorrente);
        } catch (Exception e) {
            return null;
        }
    }

    private List<UnidadeOrganizacional> getUnidadesOrganizacionaisConvenioReceita(ConvenioReceita entity) {
        List<UnidadeOrganizacional> unidadesSubConta = new ArrayList<>();
        List<ConvenioReceitaUnidade> convenioReceitaUnidades = entity.getConvenioReceitaUnidades();
        for (ConvenioReceitaUnidade unidadesOrganizacionai : convenioReceitaUnidades) {
            unidadesSubConta.add(unidadesOrganizacionai.getUnidadeOrganizacional());
        }
        return unidadesSubConta;
    }

    private List<FonteDeRecursos> getFonteRecursoCnvenioReceita(ConvenioReceita entity) {
        List<FonteDeRecursos> fontes = new ArrayList<>();
        List<ConvenioRecConta> convenioRecConta = entity.getConvenioRecConta();
        for (ConvenioRecConta recConta : convenioRecConta) {
            Conta conta = recConta.getConta();
            if (conta instanceof ContaDeDestinacao) {
                fontes.add(((ContaDeDestinacao) conta).getFonteDeRecursos());
            }
        }
        return fontes;
    }


    public SubConta recuperarContaFinanceiraPorCodigoExericicio(String codigo, Exercicio exercicio) {

        String sql = " select c.* from subconta c " +
            " inner join subcontauniorg sub on sub.subconta_id = c.id " +
            " where c.codigo like :codigo" +
            " and c.situacao = '" + SituacaoConta.ATIVO.name() + "'" +
            " and sub.exercicio_id = :idExercicio ";
        Query consulta = em.createNativeQuery(sql, SubConta.class);
        consulta.setParameter("idExercicio", exercicio.getId());
        consulta.setParameter("codigo", "%" + codigo + "%");
        return (SubConta) consulta.getResultList().get(0);
    }

    @Deprecated
    //Utilizar com Conta de Destinação ao invés de Fonte de Recursos
    public List<SubConta> buscarContasFinanceirasPorDividaPublica(String parte, ContaBancariaEntidade cbe, UnidadeOrganizacional unidadeOrganizacional, Exercicio exercicio, FonteDeRecursos fonteDeRecursos, DividaPublica dividaPublica, Boolean buscarSomenteAtivas) {

        String sql = " SELECT DISTINCT SUB.* FROM SUBCONTA SUB " +
            " INNER JOIN SUBCONTADIVIDAPUBLICA SCD ON SCD.SUBCONTA_ID = SUB.ID " +
            " INNER JOIN SUBCONTAUNIORG SC ON SC.SUBCONTA_ID = SUB.ID " +
            " LEFT JOIN SUBCONTAFONTEREC SCF ON SCF.SUBCONTA_ID = SUB.ID " +
            " INNER JOIN UNIDADEORGANIZACIONAL UNID ON UNID.ID = SC.UNIDADEORGANIZACIONAL_ID " +
            " INNER JOIN DIVIDAPUBLICA DP ON SCD.DIVIDAPUBLICA_ID = DP.ID " +
            " WHERE DP.ID = :idDivida " +
            "  AND (SUB.codigo LIKE :parte OR lower(SUB.descricao) LIKE :parte)  " +
            "  AND SC.EXERCICIO_ID = :idExercicio" +
            (buscarSomenteAtivas ? "  AND sub.situacao = :ativo " : "") +
            "  AND UNID.ID = :idUnidade ";
        if (cbe != null) {
            if (cbe.getId() != null) {
                sql += "                  AND SUB.contabancariaentidade_id = :idContaBancaria";
            }
        }
        if (fonteDeRecursos != null) {
            if (fonteDeRecursos.getId() != null) {
                sql += "                  AND SCF.FONTEDERECURSOS_ID = :idFonteRecurso";
            }
        }
        sql += "                  order by sub.codigo ";
        Query q = em.createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("idDivida", dividaPublica.getId());
        if (buscarSomenteAtivas) {
            q.setParameter("ativo", SituacaoConta.ATIVO.name());
        }
        if (cbe != null) {
            if (cbe.getId() != null) {
                q.setParameter("idContaBancaria", cbe.getId());
            }
        }
        if (fonteDeRecursos != null) {
            if (fonteDeRecursos.getId() != null) {
                q.setParameter("idFonteRecurso", fonteDeRecursos.getId());
            }
        }
        if (q.getResultList().isEmpty() || q.getResultList() == null) {
            String message = " Este Lançamento é referente a dívida pública: <b>" + dividaPublica + "</b>. A mesma não possui conta financeira em seu cadastro,";
            if (cbe != null) {
                if (cbe.getId() != null) {
                    message += " com a Conta Bancaria : " + cbe.toString() + ";";
                }
            }
            if (fonteDeRecursos != null) {
                if (fonteDeRecursos.getId() != null) {
                    message += " com a Fonte de Recursos: " + fonteDeRecursos.toString() + ".";
                }
            }
            throw new ExcecaoNegocioGenerica(message);
        }
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<SubConta> buscarContasFinanceirasPorDividaPublica(String parte, ContaBancariaEntidade cbe, UnidadeOrganizacional unidadeOrganizacional, Exercicio exercicio, ContaDeDestinacao contaDeDestinacao, DividaPublica dividaPublica, Boolean buscarSomenteAtivas) {

        String sql = " SELECT DISTINCT SUB.* FROM SUBCONTA SUB " +
            " INNER JOIN SUBCONTADIVIDAPUBLICA SCD ON SCD.SUBCONTA_ID = SUB.ID " +
            " INNER JOIN SUBCONTAUNIORG SC ON SC.SUBCONTA_ID = SUB.ID " +
            " LEFT JOIN SUBCONTAFONTEREC SCF ON SCF.SUBCONTA_ID = SUB.ID " +
            " INNER JOIN UNIDADEORGANIZACIONAL UNID ON UNID.ID = SC.UNIDADEORGANIZACIONAL_ID " +
            " INNER JOIN DIVIDAPUBLICA DP ON SCD.DIVIDAPUBLICA_ID = DP.ID " +
            " WHERE DP.ID = :idDivida " +
            "  AND (SUB.codigo LIKE :parte OR lower(SUB.descricao) LIKE :parte)  " +
            "  AND SC.EXERCICIO_ID = :idExercicio" +
            (buscarSomenteAtivas ? "  AND sub.situacao = :ativo " : "") +
            "  AND UNID.ID = :idUnidade ";
        if (cbe != null && cbe.getId() != null) {
            sql += "                  AND SUB.contabancariaentidade_id = :idContaBancaria";
        }
        if (contaDeDestinacao != null && contaDeDestinacao.getId() != null) {
            sql += "                  AND SCF.contaDeDestinacao_ID = :idcontaDeDestinacao";
        }
        sql += "                  order by sub.codigo ";
        Query q = em.createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("idDivida", dividaPublica.getId());
        if (buscarSomenteAtivas) {
            q.setParameter("ativo", SituacaoConta.ATIVO.name());
        }
        if (cbe != null && cbe.getId() != null) {
            q.setParameter("idContaBancaria", cbe.getId());
        }
        if (contaDeDestinacao != null && contaDeDestinacao.getId() != null) {
            q.setParameter("idcontaDeDestinacao", contaDeDestinacao.getId());
        }
        q.setMaxResults(10);
        List<SubConta> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            String message = " Este Lançamento é referente a dívida pública: <b>" + dividaPublica + "</b>. A mesma não possui conta financeira em seu cadastro,";
            if (cbe != null) {
                if (cbe.getId() != null) {
                    message += " com a Conta Bancaria : " + cbe.toString() + ";";
                }
            }
            if (contaDeDestinacao != null) {
                if (contaDeDestinacao.getId() != null) {
                    message += " com a Conta de Destinação de Recursos: " + contaDeDestinacao.toString() + ".";
                }
            }
            throw new ExcecaoNegocioGenerica(message);
        }
        return resultado;
    }

    public List<SubConta> buscarContasFinanceirasPorBancariaEntidadeOuTodosPorUnidadeAndContaDeDestinacao(String parte, ContaBancariaEntidade cbe, UnidadeOrganizacional unidadeOrganizacional, Exercicio exercicio, ContaDeDestinacao contaDeDestinacao, List<TipoContaFinanceira> tiposContaFinanceiras, List<TipoRecursoSubConta> tipoRecursos, Boolean buscarSomenteAtivas) {
        String sql = " SELECT DISTINCT SUB.* FROM SUBCONTA SUB " +
            "                  INNER JOIN SUBCONTAUNIORG SC ON SC.SUBCONTA_ID = SUB.ID " +
            "                  LEFT JOIN SUBCONTAFONTEREC SCF ON SCF.SUBCONTA_ID = SUB.ID " +
            "                  INNER JOIN UNIDADEORGANIZACIONAL UNID ON UNID.ID = SC.UNIDADEORGANIZACIONAL_ID " +
            "                  WHERE (SUB.codigo LIKE :parte OR lower(SUB.descricao) LIKE :parte)  " +
            "                  AND SC.EXERCICIO_ID = :idExercicio" +
            (buscarSomenteAtivas ? " AND sub.situacao = :ativo " : "");
        if (unidadeOrganizacional != null && unidadeOrganizacional.getId() != null) {
            sql += "                  AND UNID.ID = :idUnidade ";
        }
        if (cbe != null && cbe.getId() != null) {
            sql += "                  AND SUB.contabancariaentidade_id = :idContaBancaria";
        }
        if (contaDeDestinacao != null && contaDeDestinacao.getId() != null) {
            sql += "                  AND SCF.contadedestinacao_ID = :idContaDeDestinacao";
        }
        if (tiposContaFinanceiras != null) {
            sql += "                  AND SUB.TIPOCONTAFINANCEIRA IN (" + getTiposContaFinanceiraAsString(tiposContaFinanceiras) + ")";
        }

        if (tipoRecursos != null) {
            sql += "                  AND SUB.TIPORECURSOSUBCONTA IN (" + getTiposRecursosAsString(tipoRecursos) + ")";
        }

        sql += "                  order by sub.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idExercicio", exercicio.getId());
        if (buscarSomenteAtivas) {
            q.setParameter("ativo", SituacaoConta.ATIVO.name());
        }
        if (unidadeOrganizacional != null && unidadeOrganizacional.getId() != null) {
            q.setParameter("idUnidade", unidadeOrganizacional.getId());
        }
        if (cbe != null && cbe.getId() != null) {
            q.setParameter("idContaBancaria", cbe.getId());
        }
        if (contaDeDestinacao != null && contaDeDestinacao.getId() != null) {
            q.setParameter("idContaDeDestinacao", contaDeDestinacao.getId());
        }
        q.setMaxResults(10);
        return q.getResultList();
    }

    @Deprecated
    //Utilizar com Conta de Destinação ao invés de Fonte de Recursos
    public List<SubConta> buscarContasFinanceirasPorBancariaEntidadeOuTodosPorUnidade(String parte, ContaBancariaEntidade cbe, UnidadeOrganizacional unidadeOrganizacional, Exercicio exercicio, FonteDeRecursos fonteDeRecursos, List<TipoContaFinanceira> tiposContaFinanceiras, List<TipoRecursoSubConta> tipoRecursos, Boolean buscarSomenteAtivas) {
        String sql = " SELECT DISTINCT SUB.* FROM SUBCONTA SUB " +
            "                  INNER JOIN SUBCONTAUNIORG SC ON SC.SUBCONTA_ID = SUB.ID " +
            "                  LEFT JOIN SUBCONTAFONTEREC SCF ON SCF.SUBCONTA_ID = SUB.ID " +
            "                  INNER JOIN UNIDADEORGANIZACIONAL UNID ON UNID.ID = SC.UNIDADEORGANIZACIONAL_ID " +
            "                  WHERE (SUB.codigo LIKE :parte OR lower(SUB.descricao) LIKE :parte)  " +
            "                  AND SC.EXERCICIO_ID = :idExercicio" +
            (buscarSomenteAtivas ? " AND sub.situacao = :ativo " : "");
        if (unidadeOrganizacional != null) {
            if (unidadeOrganizacional.getId() != null) {
                sql += "                  AND UNID.ID = :idUnidade ";
            }
        }
        if (cbe != null) {
            if (cbe.getId() != null) {
                sql += "                  AND SUB.contabancariaentidade_id = :idContaBancaria";
            }
        }
        if (fonteDeRecursos != null) {
            if (fonteDeRecursos.getId() != null) {
                sql += "                  AND SCF.FONTEDERECURSOS_ID = :idFonteRecurso";
            }
        }
        if (tiposContaFinanceiras != null) {
            sql += "                  AND SUB.TIPOCONTAFINANCEIRA IN (" + getTiposContaFinanceiraAsString(tiposContaFinanceiras) + ")";
        }

        if (tipoRecursos != null) {
            sql += "                  AND SUB.TIPORECURSOSUBCONTA IN (" + getTiposRecursosAsString(tipoRecursos) + ")";
        }

        sql += "                  order by sub.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idExercicio", exercicio.getId());
        if (buscarSomenteAtivas) {
            q.setParameter("ativo", SituacaoConta.ATIVO.name());
        }
        if (unidadeOrganizacional != null) {
            if (unidadeOrganizacional.getId() != null) {
                q.setParameter("idUnidade", unidadeOrganizacional.getId());
            }
        }
        if (cbe != null) {
            if (cbe.getId() != null) {
                q.setParameter("idContaBancaria", cbe.getId());
            }
        }
        if (fonteDeRecursos != null) {
            if (fonteDeRecursos.getId() != null) {
                q.setParameter("idFonteRecurso", fonteDeRecursos.getId());
            }
        }
        q.setMaxResults(10);
        return q.getResultList();
    }


    public List<SubConta> listaPorContaBancariaUnidadeFonteRecurso(String parte, ContaBancariaEntidade cbe, UnidadeOrganizacional unidadeOrganizacional, Exercicio exercicio, String fonteDeRecursos) {

        String sql = " SELECT DISTINCT SUB.* FROM SUBCONTA SUB " +
            "                  INNER JOIN SUBCONTAUNIORG SC ON SC.SUBCONTA_ID = SUB.ID " +
            "                  INNER JOIN SUBCONTAFONTEREC SCF ON SCF.SUBCONTA_ID = SUB.ID " +
            "                  INNER JOIN UNIDADEORGANIZACIONAL UNID ON UNID.ID = SC.UNIDADEORGANIZACIONAL_ID " +
            "                  WHERE (SUB.codigo LIKE :parte OR lower(SUB.descricao) LIKE :parte)  " +
            "                  AND SC.EXERCICIO_ID = :idExercicio" +
            "                  AND sub.situacao = '" + SituacaoConta.ATIVO.name() + "'" +
            "                  AND UNID.ID = :idUnidade " + fonteDeRecursos;
        if (cbe != null) {
            if (cbe.getId() != null) {
                sql += "                  AND SUB.contabancariaentidade_id = :idContaBancaria";
            }
        }
        sql += "                  order by sub.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        if (cbe != null) {
            if (cbe.getId() != null) {
                q.setParameter("idContaBancaria", cbe.getId());
            }
        }
        List<SubConta> retorno = (List<SubConta>) q.getResultList();
        q.setMaxResults(10);
        return retorno;
    }

    public String getTiposContaFinanceiraAsString(List<TipoContaFinanceira> tiposContaFinanceiras) {
        String retorno = "";
        for (TipoContaFinanceira tipo : tiposContaFinanceiras) {
            retorno += "'" + tipo.name() + "',";
        }
        retorno = retorno.substring(0, retorno.length() - 1) + "";
        return retorno;
    }

    public String getTiposRecursosAsString(List<TipoRecursoSubConta> tipos) {
        String retorno = "";
        for (TipoRecursoSubConta tipo : tipos) {
            retorno += "'" + tipo.name() + "',";
        }
        retorno = retorno.substring(0, retorno.length() - 1) + "";
        return retorno;
    }

    public List<SubConta> buscarContasFinanceirasPorFonteRecurso(String filtro, FonteDeRecursos fonteDeRecursos) {
        String sql = " SELECT s.* " +
            " FROM subconta s " +
            "   INNER JOIN SubContaFonteRec scu ON scu.subconta_id = s.id " +
            "   INNER JOIN fontederecursos f ON scu.fontederecursos_id = f.id " +
            " WHERE (s.codigo like :filtro OR LOWER(s.descricao) LIKE :filtro) " +
            "       AND f.id = :fonterecurso and s.situacao = :situacao ";

        Query q = em.createNativeQuery(sql.toString(), SubConta.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("fonterecurso", fonteDeRecursos.getId());
        q.setParameter("situacao", SituacaoConta.ATIVO.name());
        q.setMaxResults(10);
        List<SubConta> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return resultado;
        }
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    public void salvarNotificaoCriacaoContaFinanceiraParaConvenio(SubConta subConta) {
        for (ConvenioReceitaSubConta crs : subConta.getConvenioReceitas()) {
            if (crs.getConvenioReceita().getId() != null) {
                if (subConta.getTipoRecursoSubConta().equals(TipoRecursoSubConta.CONVENIO_CONGENERE)) {
                    Notificacao notificacao = new Notificacao();
                    notificacao.setDescricao("A Conta Financeira: " + subConta.toString() + " foi criada para o Convênio de Receita: " + crs.getConvenioReceita().toString() + " na data " + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + ".");
                    notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
                    notificacao.setTitulo("Conta Financeira Vinculada ao Convênio de Receita!");
                    notificacao.setTipoNotificacao(TipoNotificacao.CRIACAO_CONTA_FINANCEIRA_CONVENIO_RECEITA);
                    notificacao.setLink("/convenio-receita/ver/" + crs.getConvenioReceita().getId() + "/");
                    NotificacaoService.getService().notificar(notificacao);
                }
            }
        }
    }

    public List<SubConta> buscarContasFinanceirasAtivas(String parte) {
        String sql = " SELECT SC.* "
            + " FROM SUBCONTA SC "
            + " WHERE (LOWER(SC.DESCRICAO) LIKE :parte OR LOWER(SC.CODIGO) LIKE :parte) "
            + " AND SC.SITUACAO = :ativo";
        Query q = em.createNativeQuery(sql, SubConta.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("ativo", SituacaoConta.ATIVO.name());
        return q.getResultList();
    }

    public SubConta buscarContaFinanceiraPorCodigoExericicio(String codigo, Exercicio exercicio) {
        if (codigo == null || exercicio == null) return null;
        String sql = " select c.* from subconta c " +
            " inner join subcontauniorg sub on sub.subconta_id = c.id " +
            " where trim(c.codigo) like :codigo" +
            " and c.situacao = '" + SituacaoConta.ATIVO.name() + "'" +
            " and sub.exercicio_id = :idExercicio ";
        Query consulta = em.createNativeQuery(sql, SubConta.class);
        consulta.setParameter("idExercicio", exercicio.getId());
        consulta.setParameter("codigo", "%" + codigo.trim() + "%");
        List<SubConta> resultado = consulta.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public List<SubConta> buscarTodasContaFinanceiraPorExericicioUnidadeComMovimentacao(Exercicio exercicio, UnidadeOrganizacional unidadeOrganizacional, Date dataInicial, Date dataFinal, Boolean buscarMovimentacao) {
        QueryReprocessamentoService queryReprocessamentoService = (QueryReprocessamentoService) Util.getSpringBeanPeloNome("queryReprocessamentoService");
        String sqlMovimentoFinanceiro = queryReprocessamentoService.getReprocessamentoFinanceiro();
        String filtros = " where DATA_MOVIMENTO between to_date(:DATAINICIAL,'dd/MM/yyyy') and to_date(:DATAFINAL,'dd/MM/yyyy')";

        StringBuilder subSqlMovimento = new StringBuilder();
        subSqlMovimento.append(sqlMovimentoFinanceiro
            .replace("$OperacoesReceitaDeducao", OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()))
            .replace("$Filtros", filtros));


        String sql = " select distinct c.* from subconta c " +
            " inner join subcontauniorg sub on sub.subconta_id = c.id " +
            " where sub.exercicio_id = :EXERCICIO_ID" +
            " and c.situacao = :situacao";
        if (unidadeOrganizacional != null) {
            sql += " and  sub.unidadeorganizacional_id = :unidadeOrganizacional ";
        }
        if (buscarMovimentacao) {
            sql += " and c.id in (select distinct dados.subconta from (" + subSqlMovimento.toString() + ") dados ) ";
        }

        Query consulta = em.createNativeQuery(sql, SubConta.class);
        consulta.setParameter("situacao", SituacaoConta.ATIVO.name());
        consulta.setParameter("EXERCICIO_ID", exercicio.getId());
        if (buscarMovimentacao) {
            consulta.setParameter("DATAINICIAL", DataUtil.getDataFormatada(dataInicial));
            consulta.setParameter("DATAFINAL", DataUtil.getDataFormatada(dataFinal));
            consulta.setParameter("tipoAjusteAumentativo", TipoAjusteDisponivel.recuperarListaName(TipoAjusteDisponivel.retornarAumentativo()));
            consulta.setParameter("tipoAjusteDiminutivo", TipoAjusteDisponivel.recuperarListaName(TipoAjusteDisponivel.retornarDiminutivo()));
        }
        if (unidadeOrganizacional != null) {
            consulta.setParameter("unidadeOrganizacional", unidadeOrganizacional.getId());
        }
        List<SubConta> resultado = consulta.getResultList();
        if (!resultado.isEmpty()) {
            return resultado;
        }
        return Lists.newArrayList();
    }
}
