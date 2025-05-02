/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ArquivoRetornoBBFacade extends AbstractFacade<ArquivoRetornoBB> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ArquivoRetornoBBFacade() {
        super(ArquivoRetornoBB.class);
    }

    public void salvarNovo(ArquivoRetornoBB entity, Arquivo arquivo, FileUploadEvent fileUploadEvent) {
        if (fileUploadEvent != null) {
            entity.setArquivo(arquivo);
            arquivoFacade.salvar(arquivo, fileUploadEvent);
            super.salvarNovo(entity);
        }
    }

}
