package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.ArquivoDesif;
import br.com.webpublico.nfse.util.GeradorExcelArquivoDesif;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ArquivoDesifFacade extends AbstractFacade<ArquivoDesif> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoDesifRegistro0100Facade arquivoDesifRegistro0100Facade;
    @EJB
    private ArquivoDesifRegistro0200Facade arquivoDesifRegistro0200Facade;
    @EJB
    private ArquivoDesifRegistro0300Facade arquivoDesifRegistro0300Facade;
    @EJB
    private ArquivoDesifRegistro0400Facade arquivoDesifRegistro0400Facade;
    @EJB
    private ArquivoDesifRegistro0410Facade arquivoDesifRegistro0410Facade;
    @EJB
    private ArquivoDesifRegistro0430Facade arquivoDesifRegistro0430Facade;
    @EJB
    private ArquivoDesifRegistro0440Facade arquivoDesifRegistro0440Facade;
    @EJB
    private ArquivoDesifRegistro1000Facade arquivoDesifRegistro1000Facade;

    public ArquivoDesifFacade() {
        super(ArquivoDesif.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ArquivoDesifRegistro0100Facade getArquivoDesifRegistro0100Facade() {
        return arquivoDesifRegistro0100Facade;
    }

    public ArquivoDesifRegistro0200Facade getArquivoDesifRegistro0200Facade() {
        return arquivoDesifRegistro0200Facade;
    }

    public ArquivoDesifRegistro0300Facade getArquivoDesifRegistro0300Facade() {
        return arquivoDesifRegistro0300Facade;
    }

    public ArquivoDesifRegistro0400Facade getArquivoDesifRegistro0400Facade() {
        return arquivoDesifRegistro0400Facade;
    }

    public ArquivoDesifRegistro0410Facade getArquivoDesifRegistro0410Facade() {
        return arquivoDesifRegistro0410Facade;
    }

    public ArquivoDesifRegistro0430Facade getArquivoDesifRegistro0430Facade() {
        return arquivoDesifRegistro0430Facade;
    }

    public ArquivoDesifRegistro0440Facade getArquivoDesifRegistro0440Facade() {
        return arquivoDesifRegistro0440Facade;
    }

    public ArquivoDesifRegistro1000Facade getArquivoDesifRegistro1000Facade() {
        return arquivoDesifRegistro1000Facade;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @Asynchronous
    public Future<File> exportarExcel(ArquivoDesif arquivoDesif) {
        return new AsyncResult<>(new GeradorExcelArquivoDesif(arquivoDesif, this).gerar());
    }
}
