package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoDenuncia;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;

@Stateless
public class TipoDenunciaFacade extends AbstractFacade<TipoDenuncia> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SecretariaFiscalizacaoFacade secretariaFiscalizacaoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoDenunciaFacade() {
        super(TipoDenuncia.class);
    }

    public SecretariaFiscalizacaoFacade getSecretariaFiscalizacaoFacade() {
        return secretariaFiscalizacaoFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public void setSingletonGeradorCodigo(SingletonGeradorCodigo singletonGeradorCodigo) {
        this.singletonGeradorCodigo = singletonGeradorCodigo;
    }

    @Override
    public TipoDenuncia recuperar(Object id) {
        TipoDenuncia td = em.find(TipoDenuncia.class, id);
        return td;
    }

    public Long ultimoCodigoMaisUm() {
        Query q = em.createNativeQuery("select coalesce(max(codigo), 0) + 1 as numero from TipoDenuncia");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public Long ultimoCodigo() {
        String sql = "select max(codigo) from TipoDenuncia";
        Query q = em.createNativeQuery(sql);
        String resultado = (String) q.getResultList().get(0);
        return resultado != null ? new BigDecimal(resultado).longValue() : 1;
    }

    public boolean existeCodigo(Long codigo) {
        String sql = "select * from TipoDenuncia where codigo = :codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    public boolean existeCodigoTipoDenuncia(TipoDenuncia tipoDenuncia) {
        String sql = "select * from TipoDenuncia where codigo = :codigo and id = :id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", tipoDenuncia.getCodigo());
        q.setParameter("id", tipoDenuncia.getId());
        return !q.getResultList().isEmpty();
    }
}
