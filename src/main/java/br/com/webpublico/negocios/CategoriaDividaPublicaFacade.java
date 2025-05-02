/*
 * Codigo gerado automaticamente em Fri Oct 19 13:43:10 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CategoriaDividaPublica;
import br.com.webpublico.entidades.ConfiguracaoAdministrativa;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.NaturezaDividaPublica;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CategoriaDividaPublicaFacade extends AbstractFacade<CategoriaDividaPublica> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoriaDividaPublicaFacade() {
        super(CategoriaDividaPublica.class);
    }

    @Override
    public CategoriaDividaPublica recuperar(Object id) {
        CategoriaDividaPublica cd = em.find(CategoriaDividaPublica.class, id);
        cd.getFilhos().size();
        cd.getSuperior();
        return cd;
    }

    @Override
    public List<CategoriaDividaPublica> lista() {
        String sql = "SELECT * FROM CATEGORIADIVIDAPUBLICA ORDER BY CODIGO";
        Query q = em.createNativeQuery(sql, CategoriaDividaPublica.class);
        return q.getResultList();
    }

    public boolean validaSeExisteCategoriasFilhas(CategoriaDividaPublica categoriaDividaPublica) {
        Query consulta = em.createNativeQuery("SELECT * FROM CATEGORIADIVIDAPUBLICA WHERE SUPERIOR_ID = :ID");
        consulta.setParameter("ID", categoriaDividaPublica.getId());
        return !consulta.getResultList().isEmpty();
    }

    public boolean existeCodigoIgualAtivo(CategoriaDividaPublica categoriaDividaPublica) {
        String sql = "SELECT * FROM CATEGORIADIVIDAPUBLICA WHERE trim(CODIGO) = trim(:CODIGO) AND ATIVOINATIVO = (:SITUACOES) ";
        Query consulta = em.createNativeQuery(sql);
        String codigo = categoriaDividaPublica.getCodigo();
        if (categoriaDividaPublica.getSuperior() != null) {
            codigo = categoriaDividaPublica.getSuperior().getCodigo() + "." + codigo;
        }
        consulta.setParameter("CODIGO", codigo.trim());
        consulta.setParameter("SITUACOES", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()));
        return consulta.getResultList().isEmpty();
    }

    public List<CategoriaDividaPublica> recuperaFilhos(CategoriaDividaPublica categoriaDividaPublica) {
        try {
            if (categoriaDividaPublica.getId() == null) {
                return new ArrayList<CategoriaDividaPublica>();
            }
            Query consulta = em.createNativeQuery("select ct.* from categoriadividapublica ct start with ct.superior_id = :id connect by prior ct.id = ct.superior_id and ct.ativoInativo='ATIVO'", CategoriaDividaPublica.class);
            consulta.setParameter("id", categoriaDividaPublica.getId());
            return consulta.getResultList();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }

    }

    public boolean validaVinculoComDividaPublica(CategoriaDividaPublica categoriaDividaPublica) {
        Query consulta = em.createNativeQuery("SELECT * FROM DIVIDAPUBLICA WHERE CATEGORIADIVIDAPUBLICA_ID = :ID");
        consulta.setParameter("ID", categoriaDividaPublica.getId());
        return !consulta.getResultList().isEmpty();
    }

    public CategoriaConta retornaTipoCategoria(CategoriaDividaPublica categoria) {
        if (validaSeExisteCategoriasFilhas(categoria)) {
            return CategoriaConta.SINTETICA;
        }
        return CategoriaConta.ANALITICA;
    }

    public List<CategoriaDividaPublica> listaFiltrandoNaturezaDivida(String s, String... atributos) {

        String hql = "from " + CategoriaDividaPublica.class.getSimpleName() + " obj where ";
        for (String atributo : atributos) {
            hql += "lower(obj." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<CategoriaDividaPublica> listaFiltrandoNaturezaDividaPorTipoNatureza(String parte, NaturezaDividaPublica naturezaDividaPublica) {
        String sql = " SELECT * FROM CATEGORIADIVIDAPUBLICA CD "
            + " WHERE (TRIM (REPLACE (CD.CODIGO, '.','')) LIKE :parteCod "
            + " OR LOWER(CD.DESCRICAO) LIKE LOWER (:parteDesc)) ";
        if (naturezaDividaPublica != null) {
            sql += " AND CD.naturezaDividaPublica = :tipo ";
        }
        sql += " ORDER BY CD.CODIGO ";
        Query consulta = em.createNativeQuery(sql, CategoriaDividaPublica.class);
        consulta.setParameter("parteCod", "%" + parte.replace(".", "").toLowerCase() + "%");
        consulta.setParameter("parteDesc", "%" + parte.toLowerCase() + "%");
        if (naturezaDividaPublica != null) {
            consulta.setParameter("tipo", naturezaDividaPublica.name());
        }
        return consulta.getResultList();
    }

    public String recuperaMascaraDoCodigo() {
        String hqlDaMascara = "select conf from ConfiguracaoAdministrativa conf where parametro = 'MASCARA_CODIGO_LOCAL_ESTOQUE' and validoate is null";
        Query consultaDaMascara = em.createQuery(hqlDaMascara);
        try {
            ConfiguracaoAdministrativa conf = (ConfiguracaoAdministrativa) consultaDaMascara.getSingleResult();
            return conf.getValor();
        } catch (Exception e) {
            return null;
        }
    }

    public List<CategoriaDividaPublica> buscarFiltrandoCategoriaDividaPublica(String parte) {
        String sql = "SELECT categoria.* "
            + " FROM categoriadividapublica categoria"
            + " WHERE (categoria.codigo LIKE :paramCodigo "
            + " OR (lower (categoria.descricao) LIKE :paramDescricao)) ";
        Query q = getEntityManager().createNativeQuery(sql, CategoriaDividaPublica.class);
        q.setParameter("paramDescricao", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("paramCodigo", parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }
}
