package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.Util;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 19/07/13
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ProvimentoReversaoFacade extends AbstractFacade<ProvimentoReversao> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private TipoAposentadoriaFacade tipoAposentadoriaFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProvimentoReversaoFacade() {
        super(ProvimentoReversao.class);
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public void salvarNovo(ProvimentoReversao entity, Arquivo arquivo, FileUploadEvent fileUploadEvent) {
        if (fileUploadEvent != null) {
            entity.getLaudoProvimentoReversao().setArquivo(arquivo);
            arquivoFacade.salvar(arquivo, fileUploadEvent);
        }
        salvarNovo(entity);
    }

    public void salvar(ProvimentoReversao entity, Arquivo arquivo, FileUploadEvent fileUploadEvent, Arquivo aRemover) {
        if (arquivoFacade.verificaSeExisteArquivo(aRemover)) {
            arquivoFacade.removerArquivo(aRemover);
        }
        if (fileUploadEvent != null) {
            entity.getLaudoProvimentoReversao().setArquivo(arquivo);
            arquivoFacade.salvar(arquivo, fileUploadEvent);
        }
        em.merge(entity);
    }

    public ProvimentoReversao recuperarProvimentoVigente(Aposentadoria aposentadoria) {
        try {
            String hql = "select pr from ProvimentoReversao pr " +
                    " where pr.aposentadoria = :aposentadoria " +
                    " and :dataVigencia >= pr.inicioVigencia and "
                    + " :dataVigencia <= coalesce(pr.finalVigencia, :dataVigencia)";
            Query q = em.createQuery(hql);
            q.setParameter("aposentadoria", aposentadoria);
            q.setParameter("dataVigencia", new Date());
            return (ProvimentoReversao) q.getSingleResult();
        }catch (NoResultException nre) {
            throw new ExcecaoNegocioGenerica("Não foi possível recuperar o Provimento de Reversão do Servidor");
        }
    }

}
