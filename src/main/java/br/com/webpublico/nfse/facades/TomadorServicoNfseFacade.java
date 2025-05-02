package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.*;
import br.com.webpublico.nfse.domain.TomadorServicoNfse;
import br.com.webpublico.nfse.domain.dtos.TomadorServicoDTO;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Pageable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class TomadorServicoNfseFacade extends AbstractFacade<TomadorServicoNfse> {

    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private PaisFacade paisFacade;

    public TomadorServicoNfseFacade() {
        super(TomadorServicoNfse.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public TomadorServicoNfse buscarCpfCnpjPrestadorId(String cpfCnpj, Long prestadorId) {
        String hql = " SELECT t.* " +
            "FROM TomadorServicoNfse t " +
            "  inner join dadosPessoaisNfse dados on dados.id = t.dadosPessoaisNfse_id " +
            "WHERE replace(replace(replace(dados.cpfCnpj, '.', ''), '-', ''), '/', '') " +
            "      LIKE replace(replace(replace(:cpfCnpj, '.', ''), '-', ''), '/', '') AND " +
            "      t.prestador_id = :prestadorId";
        Query q = em.createNativeQuery(hql, TomadorServicoNfse.class);
        q.setParameter("cpfCnpj", "%" + cpfCnpj + "%");
        q.setParameter("prestadorId", prestadorId);
        if (!q.getResultList().isEmpty()) {
            TomadorServicoNfse tomadorServicoNfse = (TomadorServicoNfse) q.getResultList().get(0);
            return tomadorServicoNfse;
        }
        return null;
    }

    public List<TomadorServicoDTO> listaTomadorServicoNfsePorCMCNomeRazaoSocialCPFCNPJ(String parte, Long prestadorId, Pageable pageable) {
        String hql = " SELECT t.id as id," +
            " coalesce(dados.nomeRazaoSocial, ' ') as nomeRazaoSocial, " +
            " coalesce(dados.nomeFantasia,' ') as nomeFantasia, " +
            " coalesce(dados.cpfCnpj,' ') as cpfCnpj, " +
            " coalesce(dados.email, ' ') as email, " +
            " coalesce(dados.apelido, ' ') as apelido " +
            "FROM TomadorServicoNfse t " +
            "  inner join dadosPessoaisNfse dados on dados.id = t.dadosPessoaisNfse_id " +
            "WHERE ((replace(replace(replace(dados.cpfCnpj, '.', ''), '-', ''), '/', '') " +
            "      LIKE replace(replace(replace(:parte, '.', ''), '-', ''), '/', '') " +
            "      or lower(dados.nomeRazaoSocial) LIKE :parte or lower(dados.nomeFantasia) like :parte or lower(dados.email) like :parte)  or lower(dados.apelido) like :parte) " +
            "      AND t.prestador_id = :prestadorId";
        Query q = em.createNativeQuery(hql);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("prestadorId", prestadorId);

        q.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        q.setMaxResults(pageable.getPageSize());
        List<Object[]> objs = q.getResultList();
        List<TomadorServicoDTO> retorno = Lists.newLinkedList();
        for (Object[] obj : objs) {
            retorno.add(new TomadorServicoDTO(
                ((BigDecimal) obj[0]).longValue(),
                obj[1].toString(),
                obj[2].toString(),
                obj[3].toString(),
                obj[4].toString(),
                obj[5].toString()
            ));
        }
        return retorno;
    }

    public Integer contarTomadorServicoNfsePorCMCNomeRazaoSocialCPFCNPJ(String parte, Long prestadorId) {

        String hql = " SELECT count(t.id) " +
            "FROM TomadorServicoNfse t " +
            "  inner join dadosPessoaisNfse dados on dados.id = t.dadosPessoaisNfse_id " +
            "WHERE (replace(replace(replace(dados.cpfCnpj, '.', ''), '-', ''), '/', '') " +
            "      LIKE replace(replace(replace(:parte, '.', ''), '-', ''), '/', '') " +
            "      or dados.nomeRazaoSocial LIKE :parte or dados.nomeFantasia like :parte or dados.email like :parte)" +
            "      AND t.prestador_id = :prestadorId";
        Query q = em.createNativeQuery(hql);
        q.setParameter("parte", "%" + parte + "%");
        q.setParameter("prestadorId", prestadorId);

        return ((Number) q.getSingleResult()).intValue();
    }


}
