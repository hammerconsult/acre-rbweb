package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Renato
 */
@Stateless
public class CorrespondenciaFacade extends AbstractFacade<Correspondencia> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SolicitacaoDoctoOficialFacade solicitacaoDoctoOficialFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;

    public SolicitacaoDoctoOficialFacade getSolicitacaoDoctoOficialFacade() {
        return solicitacaoDoctoOficialFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CorrespondenciaFacade() {
        super(Correspondencia.class);
    }

    public List<DocumentoOficial> recuperaDocumentosOficialPendentes() {
        Query consulta = em.createQuery("select docto from DocumentoOficial docto"
                + " where docto.modeloDoctoOficial.tipoDoctoOficial.controleEnvioRecebimento = true");
        return (List<DocumentoOficial>) consulta.getResultList();
    }

    public List<Correspondencia> recuperaTodasCorrespondencia() {
        Query consulta = em.createQuery("select c from Correspondencia c");
        return (List<Correspondencia>) consulta.getResultList();
    }

    public SolicitacaoDoctoOficial recuperaSolicitacaoDoDocumentoOficial(DocumentoOficial documentoOficial) {
        Query consulta = em.createQuery("select sol from SolicitacaoDoctoOficial sol where sol.documentoOficial = :docto");
        consulta.setParameter("docto", documentoOficial);
        try {
            return (SolicitacaoDoctoOficial) consulta.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public CadastroImobiliario recuperarCadastroImobiliario(SolicitacaoDoctoOficial solicitacao) {
        String hql = "select ci from SolicitacaoDoctoOficial sol "
                + "inner join sol.cadastroImobiliario ci "
                + "where sol = :solicitacao";
        Query q = em.createQuery(hql);
        q.setParameter("solicitacao", solicitacao);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            CadastroImobiliario ci = (CadastroImobiliario) q.getResultList().get(0);
            ci.getPropriedade().size();
            return ci;
        }
        return null;
    }

    public CadastroRural recuperarCadastroRural(SolicitacaoDoctoOficial solicitacao) {
        String hql = "select cr from SolicitacaoDoctoOficial sol "
                + "inner join sol.cadastroRural cr "
                + "where sol = :solicitacao";
        Query q = em.createQuery(hql);
        q.setParameter("solicitacao", solicitacao);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            CadastroRural cr = (CadastroRural) q.getResultList().get(0);
            cr.getPropriedade().size();
            return cr;
        }
        return null;
    }

    public String recuperarRg(PessoaFisica p) {
        p = em.find(PessoaFisica.class, p.getId());
        p.getDocumentosPessoais().size();
        for (DocumentoPessoal documentoPessoal : p.getDocumentosPessoais()) {

            if (documentoPessoal instanceof RG) {
                return ((RG) documentoPessoal).getNumero();
            }
        }
        return " - ";
    }

    public void salvarListaDeCorrespondenciaPendentes(List<Correspondencia> listaDeCorrespondenciaPendentes) {
        for (Correspondencia correspondencia : listaDeCorrespondenciaPendentes) {
            if (correspondencia.getEmissao() != null) {
                em.persist(correspondencia);
            }
        }
    }

    public Correspondencia salvarCorrespondencia(Correspondencia correspondencia) {
        return em.merge(correspondencia);
    }

    public boolean documentoEstaEmOutraCorrespondencia(DocumentoOficial documentoOficial) {
        Query consulta = em.createQuery("select c from Correspondencia c where c.documentoOficial = :docto");
        consulta.setParameter("docto", documentoOficial);
        return !consulta.getResultList().isEmpty();
    }
}
