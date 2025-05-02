/*
 * Codigo gerado automaticamente em Mon Jan 09 17:42:41 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Condominio;
import br.com.webpublico.entidades.Face;
import br.com.webpublico.entidades.FaceServico;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class CondominioFacade extends AbstractFacade<Condominio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private EnderecoCorreioFacade enderecoCorreioFacade;
    @EJB
    private ConsultaCepFacade consultaCepFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private SetorFacade setorFacade;
    @EJB
    private QuadraFacade quadraFacade;
    @EJB
    private AtributoFacade atributoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CondominioFacade() {
        super(Condominio.class);
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public EnderecoCorreioFacade getEnderecoCorreioFacade() {
        return enderecoCorreioFacade;
    }

    public ConsultaCepFacade getConsultaCepFacade() {
        return consultaCepFacade;
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public CidadeFacade getCidadeFacade() {
        return cidadeFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public SetorFacade getSetorFacade() {
        return setorFacade;
    }

    public QuadraFacade getQuadraFacade() {
        return quadraFacade;
    }

    @Override
    public Condominio recuperar(Object id) {
        Condominio c = em.find(Condominio.class, id);
        c.getLotes().size();
        return c;

    }

    public AtributoFacade getAtributoFacade() {
        return atributoFacade;
    }

    public List<FaceServico> getServicosFace(Face face) {
        String hql = "select servico from FaceServico servico where servico.face = :face";
        Query q = em.createQuery(hql);
        q.setParameter("face", face);
        return q.getResultList();
    }

    public Long buscarProximoCodigo() {
        try {
            String sql = " select ((coalesce(max(c.codigo), 0)) + 1) from condominio c ";
            Query q = em.createNativeQuery(sql);
            List<BigDecimal> resultado = q.getResultList();
            Long codigo = (resultado != null && !resultado.isEmpty()) ? resultado.get(0).longValue() : 1L;

            while (isCodigoAdicionado(codigo, null)) {
                codigo += 1;
            }

            return codigo;
        } catch (Exception e) {
            logger.error("Erro ao buscar proximo codigo. ", e);
            return 1L;
        }
    }

    public boolean isCodigoAdicionado(Long codigo, Long id) {
        String sql = " select * from condominio c " +
            " where (coalesce(c.codigo, 0) = :codigo or coalesce(c.codigoManual, 0) = :codigo) " +
            (id != null ? " and c.id <> :idCondominio " : "");

        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        if(id != null) {
            q.setParameter("idCondominio", id);
        }

        return !q.getResultList().isEmpty();
    }
}
