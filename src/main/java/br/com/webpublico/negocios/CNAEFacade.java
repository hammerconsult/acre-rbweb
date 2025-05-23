/*
 * Codigo gerado automaticamente em Fri Feb 11 14:17:28 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CNAE;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidadesauxiliares.VOCnae;
import br.com.webpublico.nfse.domain.dtos.CnaeNfseDTO;
import br.com.webpublico.nfse.util.PesquisaGenericaNfseUtil;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import com.beust.jcommander.internal.Lists;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class CNAEFacade extends AbstractFacade<CNAE> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CNAEFacade() {
        super(CNAE.class);
    }

    @Override
    public CNAE recuperar(Object id) {
        CNAE c = em.find(CNAE.class, id);
        Hibernate.initialize(c.getServicos());
        return c;
    }

    public List<CNAE> getCnaePorCadastroEconomico(CadastroEconomico cadastroEconomico) {
        StringBuilder sql = new StringBuilder("SELECT cnae.* FROM Cnae cnae ")
            .append("INNER JOIN economicocnae ON economicocnae.cnae_id = cnae.id ")
            .append("AND economicocnae.cadastroeconomico_id = :id");
        Query q = em.createNativeQuery(sql.toString(), CNAE.class);
        q.setParameter("id", cadastroEconomico.getId());
        return q.getResultList();
    }

    public List<CNAE> getCnaeAtivosPorCadastroEconomicoFiltrandoCodigoAndDescricaoCnae(String parte, Long idCmc) {
        String sql = "select c.* from cnae c " +
            " inner join economicocnae ec on ec.cnae_id = c.id " +
            " where (lower(c.descricaoDetalhada) like :parte or lower(c.codigoCnae) like :parte) " +
            " and c.situacao = :situacao " +
            " and ec.cadastroeconomico_id = :id ";
        Query q = em.createNativeQuery(sql, CNAE.class);
        q.setParameter("id", idCmc);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("situacao", CNAE.Situacao.ATIVO.name());
        List<CNAE> result = q.getResultList();
        return result;
    }

    public List<CNAE> listaCnaesAtivos(String parte) {
        String hql = "select cnae from CNAE cnae where (lower(cnae.descricaoDetalhada) like :parte or lower(cnae.codigoCnae) like :parte) and cnae.situacao = :situacao";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("situacao", CNAE.Situacao.ATIVO);
        return q.getResultList();
    }

    public List<CNAE> listaCnaesAtivos() {
        String hql = "select cnae from CNAE cnae where cnae.situacao = :situacao";
        Query q = em.createQuery(hql);
        q.setParameter("situacao", CNAE.Situacao.ATIVO);
        return q.getResultList();
    }

    public List<CNAE> buscarCnaesAtivosPorGrauDeRisco(String parte, GrauDeRiscoDTO grauDeRisco) {
        String hql = "select cnae from CNAE cnae " +
            " where (lower(cnae.descricaoDetalhada) like :parte " +
            " or lower(cnae.codigoCnae) like :parte) " +
            " and cnae.situacao = :situacao ";
        if (grauDeRisco != null) {
            hql += " and cnae.grauDeRisco = :nivel";
        }
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("situacao", CNAE.Situacao.ATIVO);
        if (grauDeRisco != null) {
            q.setParameter("grauDeRisco", grauDeRisco);
        }
        return q.getResultList();
    }


    public Page<CNAE> buscarTodosCnaesPorDescricaoOrdenadoPorDescricao(String param, Pageable pageable) {
        String orderBy = PesquisaGenericaNfseUtil.montarOrdem(pageable);

        String hql = "select cnae from CNAE cnae " +
            (StringUtils.isBlank(param) ? "" : " where lower(cnae.descricaoDetalhada) like :param or lower(cnae.codigoCnae) like :param") +
            orderBy;
        String hqlCount = hql.replace("select cnae", "select count(cnae.id)");

        Query q = em.createQuery(hql);
        Query qCount = em.createNativeQuery(hqlCount);

        if (StringUtils.isNotBlank(param)) {
            q.setParameter("param", "%" + param.trim().toLowerCase() + "%");
            qCount.setParameter("param", "%" + param.trim().toLowerCase() + "%");
        }

        q.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        q.setMaxResults(pageable.getPageSize());

        return new PageImpl(q.getResultList(), pageable, Integer.parseInt("" + qCount.getSingleResult()));
    }

    public CNAE createAndSave(CnaeNfseDTO dto) {
        CNAE cnae = em.find(CNAE.class, dto.getId());
        if (cnae == null) {
            cnae = new CNAE(dto.getCodigo(), dto.getDescricao());
            cnae = em.merge(cnae);
        }
        return cnae;
    }

    public CNAE buscarCnaePorCodigo(String codigo) {
        String hql = "select cn from CNAE cn where cn.codigoCnae = :codigoCnae and cn.situacao = :situacao";
        Query q = em.createQuery(hql);
        q.setParameter("codigoCnae", codigo);
        q.setParameter("situacao", CNAE.Situacao.ATIVO);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (CNAE) q.getResultList().get(0);
        }
        return null;
    }

    public CNAE buscarCnaeAtivoPorCodigoAndGrauDeRisco(String codigo, String grauDeRisco) {
        String sql = "select c.* from cnae c" +
            " where c.codigocnae = :codigo " +
            " and c.situacao = :situacao";
        if (grauDeRisco != null) {
            sql += " and c.grauderisco = :grau ";
        }
        Query q = em.createNativeQuery(sql, CNAE.class);
        q.setParameter("codigo", codigo);
        q.setParameter("situacao", CNAE.Situacao.ATIVO.name());
        if (grauDeRisco != null) {
            q.setParameter("grau", grauDeRisco);
        }
        List<CNAE> cnaes = q.getResultList();
        return (cnaes != null && !cnaes.isEmpty()) ? cnaes.get(0) : null;
    }

    public List<CNAE> buscarCnaesAtivosPorCodigo(String codigo) {
        String sql = " select cn.* from cnae cn where cn.codigocnae = :codigoCnae and cn.situacao = :situacao ";
        Query q = em.createNativeQuery(sql, CNAE.class);
        q.setParameter("codigoCnae", codigo);
        q.setParameter("situacao", CNAE.Situacao.ATIVO.name());

        List<CNAE> cnaes = q.getResultList();
        return cnaes != null ? cnaes : Lists.<CNAE>newArrayList();
    }

    public List<VOCnae> buscarCnaesAtivosToVO() {
        String sql = "select c.id, c.codigocnae, c.descricaodetalhada from cnae c " +
            " where c.situacao = :situacao order by c.codigocnae ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("situacao", CNAE.Situacao.ATIVO.name());

        List<Object[]> objects = q.getResultList();
        List<VOCnae> cnaes = Lists.newArrayList();

        if (objects != null && !objects.isEmpty()) {
            for (Object[] object : objects) {
                VOCnae vo = new VOCnae();
                vo.setId(((BigDecimal) object[0]).longValue());
                vo.setCodigoCnae((String) object[1]);
                vo.setDescricaoDetalhada((String) object[2]);

                cnaes.add(vo);
            }
        }
        return cnaes;
    }
}
