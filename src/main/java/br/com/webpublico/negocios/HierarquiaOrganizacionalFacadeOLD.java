/*
 * Codigo gerado automaticamente em Fri May 13 15:37:08 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoEntidade;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
@Deprecated
public class HierarquiaOrganizacionalFacadeOLD extends AbstractFacade<HierarquiaOrganizacional> {

    private static final Logger logger = LoggerFactory.getLogger(HierarquiaOrganizacionalFacadeOLD.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    ConfiguracaoHierarquiaOrganizacionalFacade configuracaoHierarquiaOrganizacionalFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HierarquiaOrganizacionalFacadeOLD() {
        super(HierarquiaOrganizacional.class);
    }

    public List<UnidadeOrganizacional> unidadesOrganizacionaisDisponiveis(Exercicio exerc, TipoHierarquiaOrganizacional tipo) {

        String hql = "from UnidadeOrganizacional uo where uo.id not in(select ho.subordinada from HierarquiaOrganizacional  ho ";
        hql += " where  ho.exercicio= :exerc  and ho.tipoHierarquiaOrganizacional =:tipoHO )  ";
        hql += " and  uo.id not in(select ho.subordinada from HierarquiaOrganizacional  ho where ho.superior is null and ho.exercicio= :exerc )";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("tipoHO", tipo);
        q.setParameter("exerc", exerc);
        return q.getResultList();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalPorUnidade(UnidadeOrganizacional unidade, HierarquiaOrganizacional h, Exercicio exerc) {
        if (unidade == null) {
            return null;
        }
        String hql = "from  HierarquiaOrganizacional h where h.exercicio=:exerc "
            + "and h.subordinada=:unidadeParametro "
            + "and (h.tipoHierarquiaOrganizacional=:tipoHO "
            + "or h.tipoHierarquiaOrganizacional is null)";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("unidadeParametro", unidade);
        q.setParameter("tipoHO", h.getTipoHierarquiaOrganizacional());
        q.setParameter("exerc", exerc);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (HierarquiaOrganizacional) q.getSingleResult();
        } else {
            Exercicio e = exercicioFacade.getExercicioPorAno(exerc.getAno() - 1);
            if (e == null) {
                return null;
            }
            return getHierarquiaOrganizacionalPorUnidade(unidade, h, e);
        }
    }

    public UnidadeOrganizacional getRaiz(Exercicio exerc) {
        Query q = em.createQuery("from HierarquiaOrganizacional ho where ho.exercicio=:exerc and ho.superior is null");
        q.setParameter("exerc", exerc);
        q.setMaxResults(1);
        HierarquiaOrganizacional h;

        if (q.getResultList().size() > 0) {
            HierarquiaOrganizacional hierarquiaOrganizacional = (HierarquiaOrganizacional) q.getSingleResult();
            return hierarquiaOrganizacional.getSubordinada();
        } else {
            return null;
        }
    }

    @Deprecated
    public HierarquiaOrganizacional getRaizHierarquia(Exercicio exerc) {
        Query q = em.createQuery("from HierarquiaOrganizacional ho where ho.exercicio=:exerc and ho.superior is null");
        q.setParameter("exerc", exerc);
        q.setMaxResults(1);
        try {
            if (!q.getResultList().isEmpty()) {
                HierarquiaOrganizacional hierarquiaOrganizacional = (HierarquiaOrganizacional) q.getSingleResult();
                return hierarquiaOrganizacional;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public List<UnidadeOrganizacional> getFilhosDe(UnidadeOrganizacional uo, Exercicio exercicio) {
        Query q = em.createQuery("select ho.subordinada from HierarquiaOrganizacional ho  where  ho.superior = :superior and ho.exercicio=:exercicio order by ho.codigo");
        q.setParameter("superior", uo);
        q.setParameter("exercicio", exercicio);

        return q.getResultList();
    }

    public List<UnidadeOrganizacional> getFilhosDe(UnidadeOrganizacional uo, TipoHierarquiaOrganizacional tipo, Exercicio exerc) {
        String hql = " select ho.subordinada from HierarquiaOrganizacional ho  "
            + " where ho.exercicio=:exerc and ho.superior = :superior and ho.tipoHierarquiaOrganizacional=:tipoHO order by ho.codigo ";
        Query q = em.createQuery(hql);
        q.setParameter("superior", uo);
        q.setParameter("tipoHO", tipo);
        q.setParameter("exerc", exerc);
        //q.setParameter("parametroDataReferencia", dataAtualFormataDDMMYYYY(new Date()));
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> getHIerarquiaFilhosDe(UnidadeOrganizacional uo, TipoHierarquiaOrganizacional tipo, Exercicio exerc) {
        String hql = " select ho from HierarquiaOrganizacional ho  "
            + " where ho.exercicio=:exerc"
            + " and ho.superior = :superior and ho.tipoHierarquiaOrganizacional=:tipoHO order by ho.codigo ";
        Query q = em.createQuery(hql);
        q.setParameter("superior", uo);
        q.setParameter("tipoHO", tipo);
        q.setParameter("exerc", exerc);
        return q.getResultList();
    }

    public HierarquiaOrganizacional getPaiDe(HierarquiaOrganizacional ho, Exercicio exerc) {

        Query q = em.createQuery(" from HierarquiaOrganizacional ho "
            + " where  ho.exercicio=:exerc and ho.subordinada = :subordinada and (ho.tipoHierarquiaOrganizacional=:tipoHO or ho.superior is null)");

        q.setParameter("subordinada", ho.getSuperior());
        q.setParameter("tipoHO", ho.getTipoHierarquiaOrganizacional());
        q.setParameter("exerc", exerc);
        //q.setParameter("parametroDataReferencia", dataAtualFormataDDMMYYYY(new Date()));
        HierarquiaOrganizacional hi = (HierarquiaOrganizacional) q.getSingleResult();
        return hi;
    }

    public List<HierarquiaOrganizacional> filtrandoHierarquiaHorganizacional(String parte, Exercicio exerc) {

        StringBuilder sql = new StringBuilder();
        String banco = sistemaFacade.getBancoDeDadosAtual();
        if (banco.toLowerCase().contains("postgresql")) {
            sql.append(" with dados(ID,CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA) as( ");
        } else {
            sql.append(" with dados(ID,CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA) as(  ");
        }
        sql.append(" select   ");
        sql.append(" ho.*  ");
        sql.append(" from   ");
        sql.append(" unidadeorganizacional und  ");
        sql.append(" inner join hierarquiaorganizacional ho   ");
        sql.append(" on ho.subordinada_id = und.id  ");
        sql.append(" where ho.exercicio_id=:exerc  ");
        sql.append(" and upper(und.descricao) like  upper(:str)");

        sql.append(" union all  ");

        sql.append(" select   ");
        sql.append(" horec.ID,  ");
        sql.append(" horec.CODIGO,  ");
        sql.append(" horec.NIVELNAENTIDADE,  ");
        sql.append(" horec.TIPOHIERARQUIAORGANIZACIONAL,  ");
        sql.append(" horec.SUBORDINADA_ID,  ");
        sql.append(" horec.SUPERIOR_ID,  ");
        sql.append(" horec.VALORESTIMADO,   ");
        sql.append(" horec.INDICEDONO,   ");
        sql.append(" horec.CODIGONO,   ");
        sql.append(" horec.EXERCICIO_ID   ");
        sql.append(" horec.TIPOHIERARQUIA   ");

        sql.append(" from   ");
        sql.append(" hierarquiaorganizacional horec   ");
        sql.append(" inner join dados d   ");
        sql.append(" on horec.subordinada_id = d.superior_id  ");
        sql.append(" where horec.exercicio_id = :exerc  ");
        sql.append(" )select ");
        sql.append("   distinct(ID),CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA   ");
        sql.append(" from  ");
        sql.append(" dados order by codigo, nivelnaentidade ");
        logger.debug(sql.toString());
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("exerc", exerc.getId());
        q.setMaxResults(20);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtrandoHierarquiaHorganizacionalEntidade(String parte, Exercicio exerc) {

        StringBuilder sql = new StringBuilder();
        String banco = sistemaFacade.getBancoDeDadosAtual();
        if (banco.toLowerCase().contains("postgresql")) {
            sql.append(" with RECURSIVE dados(ID,CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA) as(  ");
        } else {
            sql.append(" with dados(ID,CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA) as(  ");
        }
        sql.append(" select   ");
        sql.append(" ho.*  ");
        sql.append(" from   ");
        sql.append(" unidadeorganizacional und  ");
        sql.append(" inner join hierarquiaorganizacional ho   ");
        sql.append(" on ho.subordinada_id = und.id  ");
        sql.append(" where ho.exercicio_id =:exerc");
        sql.append(" and upper(und.descricao) like  upper(:str)");
        sql.append(" and und.entidade_id is not null");

        sql.append(" union all  ");

        sql.append(" select   ");
        sql.append(" horec.ID,  ");
        sql.append(" horec.CODIGO,  ");
        sql.append(" horec.NIVELNAENTIDADE,  ");
        sql.append(" horec.TIPOHIERARQUIAORGANIZACIONAL,  ");
        sql.append(" horec.SUBORDINADA_ID,  ");
        sql.append(" horec.SUPERIOR_ID,  ");
        sql.append("horec.VALORESTIMADO,   ");
        sql.append("horec.INDICEDONO,   ");
        sql.append("horec.CODIGONO,   ");
        sql.append("horec.EXERCICIO_ID  ");
        sql.append("horec.TIPOHIERARQUIA  ");
        sql.append(" from   ");
        sql.append(" hierarquiaorganizacional horec   ");
        sql.append(" inner join dados d   ");
        sql.append(" on horec.subordinada_id = d.superior_id  ");
        sql.append(" where horec.exercicio_id=:exerc");
        sql.append(" )select ");
        sql.append(" DISTINCT(ID),CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA ");
        sql.append(" from ");
        sql.append(" dados where subordinada_id in(select id from unidadeorganizacional where entidade_id is not null)order by codigo, nivelnaentidade ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("exerc", exerc.getId());
        q.setMaxResults(20);
//        imprimeSQL(sql.toString(), q);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtrandoHierarquiaHorganizacionalEntidadeTipo(String parte, String tipo, Exercicio exerc) {

        StringBuilder sql = new StringBuilder();
        String banco = sistemaFacade.getBancoDeDadosAtual();
        if (banco.toLowerCase().contains("postgresql")) {
            sql.append(" with RECURSIVE dados(ID,CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA) as(  ");
        } else {
            sql.append(" with dados(ID,CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA) as(  ");
        }
        sql.append(" select   ");
        sql.append(" ho.ID,ho.CODIGO,ho.NIVELNAENTIDADE,ho.TIPOHIERARQUIAORGANIZACIONAL,ho.SUBORDINADA_ID, ");
        sql.append(" ho.SUPERIOR_ID,ho.VALORESTIMADO,ho.INDICEDONO,ho.CODIGONO,ho.EXERCICIO_ID,ho.TIPOHIERARQUIA ");
        sql.append(" from   ");
        sql.append(" unidadeorganizacional und  ");
        sql.append(" inner join hierarquiaorganizacional ho   ");
        sql.append(" on ho.subordinada_id = und.id ");
        sql.append(" where ho.exercicio_id=:exerc  ");
        sql.append(" and upper(und.descricao) like  upper(:str)");
        sql.append(" and und.entidade_id is not null");

        sql.append(" union all  ");

        sql.append(" select   ");
        sql.append(" horec.ID,horec.CODIGO,horec.NIVELNAENTIDADE,horec.TIPOHIERARQUIAORGANIZACIONAL,horec.SUBORDINADA_ID,horec.SUPERIOR_ID,horec.VALORESTIMADO,horec.INDICEDONO,horec.CODIGONO,horec.EXERCICIO_ID,horec.TIPOHIERARQUIA ");
        sql.append(" from   ");
        sql.append(" hierarquiaorganizacional horec   ");
        sql.append(" inner join dados d   ");
        sql.append(" on horec.subordinada_id = d.superior_id  ");
        sql.append(" where horec.exercicio_id =:exerc  ");
        sql.append(" and  horec.TIPOHIERARQUIAORGANIZACIONAL =:tipo  ");
        sql.append(" )select ");
        sql.append("   DISTINCT(id),CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA  ");
        sql.append(" from  ");
        sql.append(" dados where tipohierarquiaorganizacional=:tipo order by codigo, nivelnaentidade ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("tipo", tipo);
        q.setParameter("exerc", exerc.getId());
        //imprimeSQL(sql.toString(), q);
        q.setMaxResults(20);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtrandoHierarquiaHorganizacionalTipo(String parte, String tipo, Exercicio exerc) {
        StringBuilder sql = new StringBuilder();

        if (tipo.equals("ORCAMENTARIA")) {
            sql.append(" SELECT h.* FROM VWHIERARQUIAORCAMENTARIA VW ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" WHERE (upper(vw.descricao) like :str or VW.CODIGO like :str ) ");
            sql.append(" and  :data between h.iniciovigencia and coalesce(h.fimvigencia, :data)");
            sql.append(" order by h.codigo asc ");
        } else {
            sql.append(" SELECT h.* FROM VWHIERARQUIAADMINISTRATIVA VW  ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" WHERE (upper(vw.descricao) like :str or VW.CODIGO like :str) ");
            sql.append(" and  :data between h.iniciovigencia and coalesce(h.fimvigencia, :data) ");
            sql.append(" order by h.codigo ");
        }
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("data", new Date(), TemporalType.DATE);
        q.setMaxResults(20);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtrandoHierarquiaOrganizacionalEntidadeTipo(String parte, String tipo) {
        StringBuilder sql = new StringBuilder();

        if (tipo.equals("ORCAMENTARIA")) {
            sql.append(" SELECT h.* FROM VWHIERARQUIAORCAMENTARIA VW ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" inner join entidade e on e.id = h.subordinada_id ");
            sql.append(" WHERE (upper(vw.descricao) like :str or VW.CODIGO like :str ) ");
            sql.append(" and  :data between h.iniciovigencia and coalesce(h.fimvigencia, :data)");
            sql.append(" and  :data between vw.iniciovigencia and coalesce(vw.fimvigencia, :data) ");
            sql.append(" order by h.codigo asc ");
        } else {
            sql.append(" SELECT h.* FROM VWHIERARQUIAADMINISTRATIVA VW  ");
            sql.append(" inner join HIERARQUIAORGANIZACIONAL h on h.id=VW.ID ");
            sql.append(" inner join unidadeorganizacional uo on uo.id = h.subordinada_id ");
            sql.append(" inner join entidade e on e.id = uo.entidade_id ");
            sql.append(" WHERE (upper(vw.descricao) like :str or VW.CODIGO like :str) ");
            sql.append(" and  :data between h.iniciovigencia and coalesce(h.fimvigencia, :data) ");
            sql.append(" and  :data between vw.iniciovigencia and coalesce(vw.fimvigencia, :data) ");
            sql.append(" order by h.codigo ");
        }
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("str", parametro);
        q.setParameter("data", new Date(), TemporalType.DATE);
        q.setMaxResults(20);
        return q.getResultList();
    }

    @Deprecated
    public List<HierarquiaOrganizacional> filtrandoHierarquiaHorganizacionalTipo(HierarquiaOrganizacional hierarquia) {
        StringBuilder sql = new StringBuilder();
        String banco = SistemaFacade.getBancoDeDadosAtual();
        if (banco.toLowerCase().contains("postgresql")) {
            sql.append(" with RECURSIVE dados(ID,CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA) as(  ");
        } else {
            sql.append(" with dados(ID,CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA) as(  ");
        }
        sql.append(" select   ");
//        sql.append(" ho.*  ");
        sql.append("ho.ID,ho.CODIGO,ho.NIVELNAENTIDADE,ho.TIPOHIERARQUIAORGANIZACIONAL,ho.SUBORDINADA_ID");
        sql.append(",ho.SUPERIOR_ID,ho.VALORESTIMADO,ho.INDICEDONO,ho.CODIGONO,ho.EXERCICIO_ID,ho.TIPOHIERARQUIA");
        sql.append(" from   ");
        sql.append(" unidadeorganizacional und  ");
        sql.append(" inner join hierarquiaorganizacional ho   ");
        sql.append(" on ho.subordinada_id = und.id  ");
        sql.append(" where ho.exercicio_id=:exerc  ");

        sql.append(" union all  ");

        sql.append(" select   ");
        sql.append(" horec.ID,horec.CODIGO,horec.NIVELNAENTIDADE,horec.TIPOHIERARQUIAORGANIZACIONAL,horec.SUBORDINADA_ID,horec.SUPERIOR_ID,horec.VALORESTIMADO,horec.INDICEDONO,horec.CODIGONO,horec.EXERCICIO_ID,horec.TIPOHIERARQUIA    ");
        sql.append(" from   ");
        sql.append(" hierarquiaorganizacional horec   ");
        sql.append(" inner join dados d   ");
        sql.append(" on horec.subordinada_id = d.superior_id  ");
        sql.append(" where horec.exercicio_id=:exerc and horec.id = :str  ");
        sql.append(" )select ");
        sql.append("   DISTINCT(id),CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA    ");
        sql.append(" from  ");
        sql.append(" dados where tipohierarquiaorganizacional= :tipo order by codigo, nivelnaentidade ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);

        q.setParameter("str", hierarquia.getId());
        q.setParameter("tipo", TipoHierarquiaOrganizacional.ORCAMENTARIA.toString());
        q.setParameter("exerc", hierarquia.getExercicio().getId());
        q.setMaxResults(20);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtrandoHierarquiaHorganizacionalTipoAdm(HierarquiaOrganizacional hierarquia) {
        StringBuilder sql = new StringBuilder();
        String banco = SistemaFacade.getBancoDeDadosAtual();
        if (banco.toLowerCase().contains("postgresql")) {
            sql.append(" with RECURSIVE dados(ID,CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA) as(  ");
        } else {
            sql.append(" with dados(ID,CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA) as(  ");
        }
        sql.append(" select   ");
//        sql.append(" ho.*  ");
        sql.append("ho.ID,ho.CODIGO,ho.NIVELNAENTIDADE,ho.TIPOHIERARQUIAORGANIZACIONAL,ho.SUBORDINADA_ID");
        sql.append(",ho.SUPERIOR_ID,ho.VALORESTIMADO,ho.INDICEDONO,ho.CODIGONO,ho.EXERCICIO_ID,ho.TIPOHIERARQUIA");
        sql.append(" from   ");
        sql.append(" unidadeorganizacional und  ");
        sql.append(" inner join hierarquiaorganizacional ho   ");
        sql.append(" on ho.subordinada_id = und.id  ");
        sql.append(" where ho.exercicio_id=:exerc  ");

        sql.append(" union all  ");

        sql.append(" select   ");
        sql.append(" horec.ID,horec.CODIGO,horec.NIVELNAENTIDADE,horec.TIPOHIERARQUIAORGANIZACIONAL,horec.SUBORDINADA_ID,horec.SUPERIOR_ID,horec.VALORESTIMADO,horec.INDICEDONO,horec.CODIGONO,horec.EXERCICIO_ID,horec.TIPOHIERARQUIA    ");
        sql.append(" from   ");
        sql.append(" hierarquiaorganizacional horec   ");
        sql.append(" inner join dados d   ");
        sql.append(" on horec.subordinada_id = d.superior_id  ");
        sql.append(" where horec.exercicio_id=:exerc and horec.id = :str  ");
        sql.append(" )select ");
        sql.append("   DISTINCT(id),CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA    ");
        sql.append(" from  ");
        sql.append(" dados where tipohierarquiaorganizacional= :tipo order by codigo, nivelnaentidade ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);

        q.setParameter("str", hierarquia.getId());
        q.setParameter("tipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.toString());
        q.setParameter("exerc", hierarquia.getExercicio().getId());
        q.setMaxResults(20);
        return q.getResultList();
    }


    public List<HierarquiaOrganizacional> listaFiltrandoPorOrgao(HierarquiaOrganizacional hierarquia, String parte) {
        String sql = "SELECT ho.* FROM hierarquiaorganizacional ho "
            + "INNER JOIN unidadeorganizacional uni ON ho.subordinada_id = uni.id  "
            + "WHERE ho.codigo LIKE :param "
            + "AND ho.tipohierarquiaorganizacional = 'ORCAMENTARIA' "
            + "AND ho.exercicio_id = :exer "
            + "AND ho.indicedono > 2 AND uni.descricao LIKE :parte";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        String param = hierarquia.getCodigo().substring(0, 6);
        q.setParameter("exer", hierarquia.getExercicio().getId());
        q.setParameter("param", param + "%");
        q.setParameter("parte", "%" + parte.trim().toUpperCase().toLowerCase() + "%");
        q.setMaxResults(20);
        List<HierarquiaOrganizacional> hierarquias = q.getResultList();
        if (hierarquias.isEmpty()) {
            return new ArrayList<HierarquiaOrganizacional>();
        } else {
            return hierarquias;
        }
    }

    public List<HierarquiaOrganizacional> filtraPorNivel(String parte, String nivel, TipoHierarquiaOrganizacional tipo, Exercicio exerc) {

        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ho.* FROM HIERARQUIAORGANIZACIONAL ho ");
        sql.append(" INNER JOIN unidadeorganizacional uni ON uni.id = ho.subordinada_id AND ((upper(uni.descricao) LIKE upper(:param)) or (ho.codigo like(:param)))");
        sql.append(" where ho.INDICEDONO = :nivel and ho.EXERCICIO_ID = :exerc and ho.TIPOHIERARQUIAORGANIZACIONAL = :tipo ORDER BY ho.codigo asc ");

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("param", parametro);
        q.setParameter("nivel", nivel);
        q.setParameter("exerc", exerc.getId());
        q.setParameter("tipo", tipo.toString());

        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> filtraPorNivel(String parte, String nivel, TipoHierarquiaOrganizacional tipo, Date data) {

        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ho.* FROM HIERARQUIAORGANIZACIONAL ho ");
        sql.append(" INNER JOIN unidadeorganizacional uni ON uni.id = ho.subordinada_id ");
        sql.append(" AND ((upper(uni.descricao) LIKE upper(:param)) or (ho.codigo like(:param)))");
        sql.append(" where ho.INDICEDONO = :nivel and ho.TIPOHIERARQUIAORGANIZACIONAL = :tipo ");
        sql.append(" and to_date(:data,'dd/mm/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia,to_date(:data,'dd/mm/yyyy')) ");
        sql.append(" ORDER BY ho.codigo asc ");

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        String parametro = "%" + parte.trim().toUpperCase() + "%";
        q.setParameter("param", parametro);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("nivel", nivel);
        q.setParameter("tipo", tipo.toString());

        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> listaIntervalo(String codigoInicio, String codigoFim, String nivel, Exercicio exerc) {

        StringBuilder sql = new StringBuilder();

        sql.append(" select * from hierarquiaorganizacional ho  ");
        sql.append(" where ho.indicedono = :nivel ");
        sql.append(" and ho.codigo between :inicio and :fim ");
        sql.append(" and ho.exercicio_id = :exerc ORDER BY ho.codigo ");

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);

        q.setParameter("inicio", codigoInicio);
        q.setParameter("fim", codigoFim);
        q.setParameter("exerc", exerc.getId());
        q.setParameter("nivel", nivel);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> listaHOPorNivelTipo(int nivel, TipoHierarquiaOrganizacional tipo, Exercicio exerc) {
        String hql = "from HierarquiaOrganizacional ho where ho.exercicio = :exerc and ho.tipoHierarquiaOrganizacional = :tipo and ho.indiceDoNo = :indice";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("indice", nivel);
        q.setParameter("exerc", exerc);
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> listaOrdenadaPorIndiceDoNo(TipoHierarquiaOrganizacional tipo, Exercicio exerc) throws ExcecaoNegocioGenerica {
        StringBuilder sql = new StringBuilder();
        if (tipo == null) {
            throw new ExcecaoNegocioGenerica("Tipo da Hierarquia esta null! Selecione o tipo de Hierarquia Organizacional");
        } else if (exerc == null) {
            throw new ExcecaoNegocioGenerica("Exercicio esta null");
        }
        sql.append(" select ho.* from HIERARQUIAORGANIZACIONAL ho where ho.TIPOHIERARQUIAORGANIZACIONAL =:tipo and ho.exercicio_id=:exerc order by ho.indicedono desc ");

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("exerc", exerc.getId());
        q.setParameter("tipo", tipo.toString());

        if (q.getResultList().isEmpty()) {
            throw new ExcecaoNegocioGenerica("A Hierarquia do " + tipo.getDescricao() + " não contem registros para o exercicio de" + exerc.getAno() + "!");
        }

        return q.getResultList();

    }

    private List<String> validaSalvar(HierarquiaOrganizacional pai, HierarquiaOrganizacional filho, boolean raiz) {
        List<String> erros = new ArrayList<String>();
        if ((pai == null) && (!raiz)) {
            erros.add("Nenhum hierarquia pai selecionada!");
        }
        if ((filho.getTipoHierarquiaOrganizacional() == null) && (!raiz)) {
            erros.add("O tipo de Hierarquia organizacional esta null. Informe uma unidade!");
        }
        if (filho.getExercicio() == null) {
            erros.add("O exercicio esta null. Informe um exercicio!");
        }
        if (filho.getSubordinada() == null) {
            erros.add("Não foi selecionada nenhuma unidade organizacional para esta hierarquia!");
        }
        if (!raiz) {
            String[] cds = pai.getCodigo().split("\\.");
            int tamFilho = filho.getCodigoNo().length();
            if (pai.getIndiceDoNo() >= cds.length) {
                erros.add("Não e possivel cadastrar mais filhos na arvore. Nivel maximo atingido!");
            }
            if (cds.length < pai.getIndiceDoNo()) {
                erros.add("O tamanho do codigo informado e diferente do tomanho definido na configuração!");
            } else {
                try {
                    if (tamFilho != cds[pai.getIndiceDoNo()].length()) {
                        erros.add("O tamanho do codigo informado e difere do tomanho definido na configuração!");
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    erros.add("O numero de nos ja foi atingido!");
                }
            }
            erros.addAll(validaUnicidadeDoCodigo(filho, pai.getIndiceDoNo() + 1));

        }
        return erros;
    }

    private List<String> validaUnicidadeDoCodigo(HierarquiaOrganizacional ho, int indice) {
        List<String> toReturn = new ArrayList<String>();
        String hql = "from HierarquiaOrganizacional where exercicio=:exerc and codigoNo=:codigo and superior=:sup and TipoHierarquiaOrganizacional=:tipo";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("sup", ho.getSuperior());
        q.setParameter("codigo", ho.getCodigoNo());
        q.setParameter("exerc", ho.getExercicio());
        q.setParameter("tipo", ho.getTipoHierarquiaOrganizacional().toString());
        if (!q.getResultList().isEmpty()) {
            toReturn.add("O codigo " + ho.getCodigoNo() + " ja existe no nivel " + (indice));
        }
        return toReturn;
    }

    private void montaCodigoComMascara(HierarquiaOrganizacional pai, HierarquiaOrganizacional filho) {
        String codigoPai = pai.getCodigo();
        String codigoNo = filho.getCodigoNo();
        int nivel = filho.getIndiceDoNo();
        String[] codigoQuebrado = codigoPai.split("\\.");
        String toReturn = "";

        for (int x = 0; x < codigoQuebrado.length; x++) {
            String parte = codigoQuebrado[x];
            if (x == nivel - 1) {
                parte = codigoNo;
            }
            toReturn += parte + ".";
        }
        toReturn = toReturn.substring(0, toReturn.length() - 1);
        filho.setCodigo(toReturn);
    }

    private List<String> codigosdaRaiz(HierarquiaOrganizacional filho) {
        ConfiguracaoContabil conf = configuracaoContabilFacade.configuracaoContabilVigente();
        List<String> stb = new ArrayList<String>();

        if (conf != null) {
            if (getRaiz(filho.getExercicio()) == null) {
                String mascara = conf.getMascaraUnidadeOrganizacional();
                filho.setCodigo(mascara.replace("#", "0"));
                filho.setCodigoNo(mascara.split("\\.")[0].replace("#", "0"));
                filho.setIndiceDoNo(0);
                filho.setNivelNaEntidade(0);
                filho.setValorEstimado(BigDecimal.ZERO);
            } else {
                stb.add("Ja existe uma raiz definida para este exercicio.");
            }
        } else {
            stb.add("Não existe uma configuração definida. Cadastre uma configuração contabil antes de cadastrar a Hierarquia Organizacional");
        }

        return stb;
    }

    private void calculaDados(HierarquiaOrganizacional pai, HierarquiaOrganizacional filho) {
        filho.setIndiceDoNo(pai.getIndiceDoNo() + 1);
        filho.setSuperior(pai.getSubordinada());
        montaCodigoComMascara(pai, filho);
    }

    public HierarquiaOrganizacional hierarquiaDaUnidadeOrg(UnidadeOrganizacional o) {
        String hql = "from HierarquiaOrganizacional where subordinada = :o order by exercicio";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("o", o);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return (HierarquiaOrganizacional) q.getSingleResult();
        }

    }

    public List<HierarquiaOrganizacional> recuperaHierarquiasFilhas(List<HierarquiaOrganizacional> lista, HierarquiaOrganizacional hierarquia) {
        if ((hierarquia != null) && (hierarquia.getSubordinada() != null)) {
            Query q = em.createQuery("select ho from HierarquiaOrganizacional ho, VinculoFP vinculo where ho.subordinada = vinculo.unidadeOrganizacional"
                + " and ho.superior = :unidade and ho.exercicio = :exercicio and ho.tipoHierarquiaOrganizacional = :tipo ");
            q.setParameter("unidade", hierarquia.getSubordinada());
            q.setParameter("exercicio", sistemaFacade.getExercicioCorrente());
            q.setParameter("tipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA);

            if (q.getResultList().isEmpty()) {
                return lista;
            } else {
                for (HierarquiaOrganizacional ho : (List<HierarquiaOrganizacional>) q.getResultList()) {
                    if (!lista.contains(ho)) {
                        lista.add(ho);
                        recuperaHierarquiasFilhas(lista, ho);
                    }
                }
            }

            return lista;

        } else {
            return lista;
        }
    }

    public List<UnidadeOrganizacional> recuperaUnidadesOrganizacionaisFilhas(List<HierarquiaOrganizacional> lista, HierarquiaOrganizacional ho) {
        List<HierarquiaOrganizacional> listaHierarquia = new ArrayList<HierarquiaOrganizacional>();
        List<UnidadeOrganizacional> listaUnidade = new ArrayList<UnidadeOrganizacional>();

        listaHierarquia.add(ho);

        if (ho.getSubordinada() != null) {
            for (HierarquiaOrganizacional item : (List<HierarquiaOrganizacional>) recuperaHierarquiasFilhas(listaHierarquia, ho)) {
                listaUnidade.add(item.getSubordinada());
            }
        } else {
            listaUnidade.add(ho.getSubordinada());
        }
        return listaUnidade;
    }


    /**
     * Metodo que recuperar o HierarquiaOrganizacional do codigo Orgão.
     *
     * @param codigo - Codigo do Orgão
     * @return HO
     */
    public HierarquiaOrganizacional recuperaHierarquiaOrgao(String codigo) {
        HierarquiaOrganizacional ho = null;
        Query createQuery = em.createNativeQuery("SELECT id FROM HIERARQUIAORGANIZACIONAL WHERE INDICEDONO = 2 AND TIPOHIERARQUIAORGANIZACIONAL = 'ADMINISTRATIVA' AND SUBSTR(CODIGONO,1,3) = :codigo");
        createQuery.setMaxResults(1);
        createQuery.setParameter("codigo", codigo);
        if (createQuery.getResultList().isEmpty()) {
            return null;
        }
        BigDecimal id = (BigDecimal) createQuery.getSingleResult();
        ho = recuperar(Long.parseLong(id + ""));
        return ho;
    }

    public List<HierarquiaOrganizacional> recuperaHierarquiaOrgao() {
        HierarquiaOrganizacional ho = null;
        Query createQuery = em.createQuery("select ho from HierarquiaOrganizacional ho where ho.codigo like '%.00.00000.000.00' and ho.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA'");
        if (createQuery.getResultList().isEmpty()) {
            return null;
        }
        return createQuery.getResultList();
    }


    public BigDecimal totalizaValorPrevisHierarquiaPorNivel(HierarquiaOrganizacional ho, int nivel) {
        StringBuilder stb = new StringBuilder();

        stb.append("  SELECT ");
        stb.append("     sum(coalesce(ho.valorestimado,0))");
        stb.append(" FROM  ");
        stb.append("    hierarquiaorganizacional ho ");
        stb.append(" where ");
        stb.append("     ho.tipohierarquiaorganizacional = 'ORCAMENTARIA' ");
        stb.append(" and ho.indicedono=:nivel ");
        stb.append(" and REGEXP_LIKE(codigo,(select ");
        stb.append("                       '^'||substr(ho.codigo,0,instr(ho.codigo,'.',1,(:nivel-1))) ");
        stb.append("                  from ");
        stb.append("                      hierarquiaorganizacional ho ");
        stb.append("                  where ");
        stb.append("                      ho.tipohierarquiaorganizacional = 'ORCAMENTARIA' ");
        stb.append("                  and ho.id=:ho)) ");
        stb.append("                  and ho.exercicio_id=:exerc ");
        stb.append("                  and ho.id <> :ho ");


        Query q = getEntityManager().createNativeQuery(stb.toString());
        q.setParameter("ho", ho.getId());
        q.setParameter("exerc", ho.getExercicio().getId());
        q.setParameter("nivel", nivel);

        return (BigDecimal) q.getSingleResult();
    }

    private void recalculaValoresPlanejados(HierarquiaOrganizacional ho) {
        HierarquiaOrganizacional pai = this.getPaiDe(ho, ho.getExercicio());
        int nivel = ho.getIndiceDoNo();
        BigDecimal totalValorPrevistoNivel = this.totalizaValorPrevisHierarquiaPorNivel(ho, nivel);
        BigDecimal totalAdd = ho.getValorEstimado();
        BigDecimal temp = BigDecimal.ZERO;
        if (pai.isGerenciaValorEstimando()) {
            temp.add(temp.add(totalAdd.add(totalValorPrevistoNivel)));
            if (temp.compareTo(pai.getValorEstimado()) <= 0) {
                getEntityManager().merge(ho);
            } else {
                ////System.out.println("entrou no erro");
                //erro;
            }
        } else {
            getEntityManager().merge(ho);
            BigDecimal valorEstTemp = ho.getValorEstimado();
            for (int x = nivel; x > 1; x--) {
                BigDecimal totalValor = this.totalizaValorPrevisHierarquiaPorNivel(ho, x).add(valorEstTemp);
                pai = this.getPaiDe(ho, ho.getExercicio());
                pai.setValorEstimado(totalValor);
                getEntityManager().merge(pai);
                valorEstTemp = totalValor;
                ho = pai;
            }
        }
    }

    public List<UnidadeOrganizacional> listaFiltrandoHOPorTipoEntidadesNaturezaPrevidenciaria(String parte) {
        String sql = "SELECT uo.* FROM unidadeorganizacional uo "
            + " JOIN hierarquiaorganizacional ho ON uo.id = ho.subordinada_id"
            + " JOIN entidade e ON e.id = uo.entidade_id "
            + " WHERE (lower(uo.descricao) LIKE :parte OR ho.codigo LIKE :parte)"
            + " AND ho.tipohierarquiaorganizacional = :tipo"
            + " AND :data BETWEEN ho.inicioVigencia AND ho.fimVigencia "
            + " AND uo.tipoUnidade = :natureza ";
        Query q = this.em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("tipo", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        q.setParameter("natureza", TipoEntidade.FUNDO_PUBLICO.name());
        q.setParameter("data", UtilRH.getDataOperacao());
        q.setMaxResults(20);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionaisPorTipoVigente(Date fim, TipoHierarquiaOrganizacional tipoHO) {
        String SQL = " SELECT  * FROM hierarquiaorganizacional WHERE TO_date(coalesce(fimvigencia,sysdate),'DD/MM/YYYY') <= :fim AND tipohierarquiaorganizacional =:tipo ORDER BY codigo";

        Query q = getEntityManager().createNativeQuery(SQL, HierarquiaOrganizacional.class);
        q.setParameter("fim", fim);
        q.setParameter("tipo", tipoHO.name());
        return q.getResultList();
    }

    public int niveisHierarquia(TipoHierarquiaOrganizacional tipo) {
        ConfiguracaoHierarquiaOrganizacional conf = configuracaoHierarquiaOrganizacionalFacade.configuracaoVigente(tipo);
        if (conf != null) {
            return conf.getMascara().split("\\.").length;
        } else {
            throw new ExcecaoNegocioGenerica("Não existe nenhuma Mascara cadastrada para o Tipo " + tipo.getDescricao());
        }
    }

    public List<UnidadeOrganizacional> listaUnidadeOrcamentariaVigentePorTipoEntidade(String parte, TipoEntidade tipoEntidade) {
        String sql = "SELECT uo.* FROM vwhierarquiaorcamentaria vw "
            + " inner join unidadeorganizacional uo on vw.subordinada_id = uo.id " +
            "  inner join unidadeorganizacional uo on vw.subordinada_id = uo.id "
            + " WHERE (lower(vw.descricao) LIKE :parte OR vw.codigo LIKE :parte)"
            + " AND :data BETWEEN vw.inicioVigencia AND vw.fimVigencia "
            + " AND vw.tipoUnidade = :natureza ";

        Query q = this.em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("natureza", tipoEntidade.name());
        q.setParameter("data", UtilRH.getDataOperacao());
        q.setMaxResults(20);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> listaHierarquiaOrcamentariaVigentePorTipoEntidade(String parte, TipoEntidade tipoEntidade) {
        String sql = "SELECT vw.* FROM vwhierarquiaorcamentaria vw "
            + " inner join unidadeorganizacional uo on vw.subordinada_id = uo.id "
            + "   inner join entidade entidade on entidade.id = uo.entidade_id "
            + " WHERE (lower(vw.descricao) LIKE :parte OR vw.codigo LIKE :parte)"
            + " AND :data BETWEEN vw.inicioVigencia AND vw.fimVigencia "
            + " AND entidade.tipoUnidade = :natureza ";

        Query q = this.em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("natureza", tipoEntidade.name());
        q.setParameter("data", UtilRH.getDataOperacao());
        q.setMaxResults(20);
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> listaHierarquiaVigentePorTipoEntidade(String parte, TipoEntidade tipoEntidade, TipoHierarquiaOrganizacional tipo) {
        String sql = "SELECT vw.* FROM HierarquiaOrganizacional vw "
            + " inner join unidadeorganizacional uo on vw.subordinada_id = uo.id" +
            "   inner join entidade entidade on entidade.id = uo.entidade_id "
            + " WHERE (lower(uo.descricao) LIKE :parte OR vw.codigo LIKE :parte)"
            + " AND :data BETWEEN vw.inicioVigencia AND coalesce(vw.fimVigencia,:data) "
            + " AND entidade.tipoUnidade = :natureza and vw.tipohierarquiaorganizacional = :tipo ";

        Query q = this.em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("natureza", tipoEntidade.name());
        q.setParameter("tipo", tipo.name());
        q.setParameter("data", UtilRH.getDataOperacao());
        q.setMaxResults(20);
        return q.getResultList();
    }

    public List<UnidadeOrganizacional> listaUnidadeVigentePorTipoEntidade(String parte, TipoEntidade tipoEntidade, TipoHierarquiaOrganizacional tipo) {
        String sql = "SELECT uo.* FROM HierarquiaOrganizacional vw "
            + " inner join unidadeorganizacional uo on vw.subordinada_id = uo.id" +
            "   inner join entidade entidade on entidade.id = uo.entidade_id "
            + " WHERE (lower(uo.descricao) LIKE :parte OR vw.codigo LIKE :parte)"
            + " AND :data BETWEEN vw.inicioVigencia AND coalesce(vw.fimVigencia,:data) "
            + " AND entidade.tipoUnidade = :natureza and vw.tipohierarquiaorganizacional = :tipo ";

        Query q = this.em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("natureza", tipoEntidade.name());
        q.setParameter("tipo", tipo.name());
        q.setParameter("data", UtilRH.getDataOperacao());
        q.setMaxResults(20);
        return q.getResultList();
    }
}
