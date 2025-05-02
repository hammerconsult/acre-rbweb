/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.nfse.facades;

import br.com.webpublico.nfse.domain.dtos.NfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.util.PesquisaGenericaNfseUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Stateless
public class PesquisaGenericaNfseFacade <T> {

    private final Logger logger = LoggerFactory.getLogger(PesquisaGenericaNfseFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public NfseDTO recuperarRegistro(String table, Long registro) {
        String hql = " ";
        hql += PesquisaGenericaNfseUtil.montarFrom(table);
        hql += PesquisaGenericaNfseUtil.montarWherePorRegistro();
        Query q = em.createQuery(hql);

        q = PesquisaGenericaNfseUtil.atribuirParametroDeBuscaPorId(q, registro);

        Object entity = (T) q.getSingleResult();
        try {
            return ((NfseEntity) entity).toNfseDto();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Page<NfseDTO> pesquisar(Pageable pageable, String table, String searchFields, String query) throws UnsupportedEncodingException {
        List<NfseDTO> dtos = Lists.newArrayList();
        List<NfseEntity> listaResultado = getResultadoDaPesquisa(pageable, table, searchFields, query);
        Integer resultado = getCountDaPesquisa(pageable, table, searchFields, query);
        for (NfseEntity t : listaResultado) {
            dtos.add(((NfseEntity) t).toNfseDto());
        }
        return new PageImpl(dtos, pageable, resultado);
    }

    private List<NfseEntity> getResultadoDaPesquisa(Pageable pageable, String table, String searchFields, String query) {
        String hql = " ";
        hql += PesquisaGenericaNfseUtil.montarFrom(table);
        hql += PesquisaGenericaNfseUtil.montarWhere(searchFields, query);
        hql += PesquisaGenericaNfseUtil.montarOrdem(pageable);

        Query q = em.createQuery(hql);
        q = PesquisaGenericaNfseUtil.atribuirPaginacao(q, pageable);
        q = PesquisaGenericaNfseUtil.atribuirParametroDeBusca(q, query);
        return q.getResultList();
    }

    private Integer getCountDaPesquisa(Pageable pageable, String table, String searchFields, String query) {
        String hql = "";
        hql += "select count(id) ";
        hql += PesquisaGenericaNfseUtil.montarFrom(table);
        hql += PesquisaGenericaNfseUtil.montarWhere(searchFields, query);
        hql += PesquisaGenericaNfseUtil.montarOrdem(pageable);

        Query q = em.createQuery(hql);
        q = PesquisaGenericaNfseUtil.atribuirParametroDeBusca(q, query);

        try {
            return Integer.parseInt("" + q.getSingleResult());
        } catch (NoResultException nre) {
            return 0;
        }
    }


}
