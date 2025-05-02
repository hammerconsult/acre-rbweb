/*
 * Codigo gerado automaticamente em Tue Feb 22 10:54:59 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoUsuarioSistema;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ArquivoUsuarioSistemaFacade extends AbstractFacade<ArquivoUsuarioSistema> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ArquivoUsuarioSistemaFacade() {
        super(ArquivoUsuarioSistema.class);
    }

    public void salvar(ArquivoUsuarioSistema entity, Arquivo arquivo, FileUploadEvent fileUploadEvent) {
        if (verificaSeExisteArquivo(arquivo)) {
            if (entity.getArquivo() == null) {
                arquivoFacade.removerArquivo(arquivo);
            }
        }
        if (fileUploadEvent != null) {
            entity.setArquivo(arquivo);
            salvarArquivo(fileUploadEvent, arquivo);
        }
        em.merge(entity);
    }

    public void salvarNovo(ArquivoUsuarioSistema entity, Arquivo arquivo, FileUploadEvent fileUploadEvent) {
        try {
            if (fileUploadEvent != null) {
                entity.setArquivo(arquivo);
                salvarArquivo(fileUploadEvent, arquivo);
            }
            em.persist(entity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void salvarArquivo(FileUploadEvent fileUploadEvent, Arquivo arq) {
        try {
            UploadedFile arquivoRecebido = fileUploadEvent.getFile();
            arq.setNome(arquivoRecebido.getFileName());
            arq.setMimeType(arquivoRecebido.getContentType()); //No prime 2 não funciona
            arq.setTamanho(arquivoRecebido.getSize());
            arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
            arquivoRecebido.getInputstream().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public boolean verificaSeExisteArquivo(Arquivo arq) {
        Arquivo ar = null;
        if (arq == null) {
            return false;
        }
        try {
            ar = arquivoFacade.recupera(arq.getId());
        } catch (Exception e) {
            return false;
        }
        if (arq.equals(ar)) {
            return true;
        }
        return false;
    }
}
