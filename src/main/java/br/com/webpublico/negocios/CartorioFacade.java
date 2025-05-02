/*
 * Codigo gerado automaticamente em Tue Feb 22 09:12:16 BRT 2011
 * Gerador de Facace
*/

package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Cartorio;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class CartorioFacade extends AbstractFacade<Cartorio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private UFFacade uFFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private ConsultaCepFacade consultaCepFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CartorioFacade() {
        super(Cartorio.class);
    }

    public boolean existeCartorio(Cartorio cartorio) {
        String sql = "select c.* from Cartorio c" +
                " where c.pessoaJuridica_id = :pessoaJuridica";
        if(cartorio.getId()!=null){
            sql += " and c.id <> :id";
        }
        Query q = getEntityManager().createNativeQuery(sql,Cartorio.class);
        q.setParameter("pessoaJuridica", cartorio.getPessoaJuridica().getId());
        if(cartorio.getId()!=null){
            q.setParameter("id", cartorio.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public CidadeFacade getCidadeFacade() {
        return cidadeFacade;
    }

    public UFFacade getuFFacade() {
        return uFFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public ConsultaCepFacade getConsultaCepFacade() {
        return consultaCepFacade;
    }
}
