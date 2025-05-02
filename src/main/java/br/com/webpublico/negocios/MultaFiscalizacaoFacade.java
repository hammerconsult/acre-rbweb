package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MultaFiscalizacao;
import br.com.webpublico.enums.FormaCalculoMultaFiscalizacao;
import br.com.webpublico.enums.IncidenciaMultaFiscalizacao;
import br.com.webpublico.enums.TipoCalculoMultaFiscalizacao;
import br.com.webpublico.enums.TipoMultaFiscalizacao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author fabio
 */
@Stateless
public class MultaFiscalizacaoFacade extends AbstractFacade<MultaFiscalizacao> {

    @EJB
    private IndiceEconomicoFacade indiceEconomicoFacade;
    @EJB
    private TributoFacade tributoFacade;

    public MultaFiscalizacaoFacade() {
        super(MultaFiscalizacao.class);
    }

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Long ultimoCodigoMaisUm() {
        Query q = em.createNativeQuery("select coalesce(max(codigo), 0) + 1 as codigo from MultaFiscalizacao");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public Long ultimoCodigo() {
        String sql = "select max(codigo) from MultaFiscalizacao";
        Query q = em.createNativeQuery(sql);
        String resultado = (String) q.getResultList().get(0);
        return resultado != null ? new BigDecimal(resultado).longValue() : 1;
    }

    public boolean existeCodigo(Long codigo) {
        String sql = "select * from MultaFiscalizacao where codigo = :codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    public boolean existeCodigoMultaFiscalizacao(MultaFiscalizacao multa) {
        String sql = "select * from MultaFiscalizacao where codigo = :codigo and id = :id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", multa.getCodigo());
        q.setParameter("id", multa.getId());
        return !q.getResultList().isEmpty();
    }

    public TributoFacade getTributoFacade() {
        return tributoFacade;
    }

    public IndiceEconomicoFacade getIndiceEconomicoFacade() {
        return indiceEconomicoFacade;
    }

    public List<MultaFiscalizacao> listaFiltrando(String s) {
        return listaFiltrando(s, false);
    }

    public List<MultaFiscalizacao> listaFiltrando(String s, boolean somenteMensal) {
        String hql = "from MultaFiscalizacao obj where " +
            " (lower(obj.artigo) like :filtro OR " +
            " lower(to_char(obj.codigo)) like :filtro) ";
        if (somenteMensal) {
            hql += " and obj.tipoCalculoMultaFiscalizacao = :tipoCalculoMulta ";
        }
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        if (somenteMensal) {
            q.setParameter("tipoCalculoMulta", TipoCalculoMultaFiscalizacao.MENSAL);
        }
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<MultaFiscalizacao> buscarMultasPorIncidenciaAndTipoMultaAndTipoCalculoAndFormaCalculo(String s) {
        String sql = "select obj.* " +
            "   from MultaFiscalizacao obj " +
            "   where (lower(obj.artigo) like :filtro OR " +
            "           lower(to_char(obj.codigo)) like :filtro) " +
            "   and obj.INCIDENCIAMULTAFISCALIZACAO = :incidencia " +
            "   and obj.TIPOMULTAFISCALIZACAO = :tipoMulta " +
            "   and obj.tipoCalculoMultaFiscalizacao = :tipoCalculo " +
            "   and obj.formaCalculoMultaFiscalizacao = :formaCalculo ";

        Query q = getEntityManager().createNativeQuery(sql, MultaFiscalizacao.class);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("incidencia", IncidenciaMultaFiscalizacao.MULTA_ACESSORIA.name());
        q.setParameter("tipoMulta", TipoMultaFiscalizacao.FIXO.name());
        q.setParameter("tipoCalculo", TipoCalculoMultaFiscalizacao.ANUAL.name());
        q.setParameter("formaCalculo", FormaCalculoMultaFiscalizacao.VALOR.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }
}
