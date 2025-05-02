/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.negocios.ArquivoFacade;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author terminal4
 */
@ManagedBean
@SessionScoped
public class RecebeArquivoSimples implements Serializable {

    @EJB
    private ArquivoFacade arquivoFacade;
    private Arquivo arquivo;
    private static final Logger logger = LoggerFactory.getLogger(RecebeArquivoSimples.class);
    public void uploadArquivo(FileUploadEvent evento) {
        try {
            UploadedFile arquivoRecebido = evento.getFile();
            Arquivo arq = new Arquivo();
            arq.setNome(arquivoRecebido.getFileName());
            //arq.setMimeType(arquivoRecebido.getContentType()); //No prime 2 não funciona
            arq.setMimeType(arquivoFacade.getMimeType(arquivoRecebido.getFileName()));
            arq.setDescricao("Arquivo upload");
            arq.setTamanho(arquivoRecebido.getSize());
            arquivo = arq;
            arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
            arquivoRecebido.getInputstream().close();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public List<Arquivo> listaArquivos() {
        return arquivoFacade.listaArquivos();
    }

}
