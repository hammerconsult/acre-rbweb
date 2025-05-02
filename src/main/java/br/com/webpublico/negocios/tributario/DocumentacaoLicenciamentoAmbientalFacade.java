package br.com.webpublico.negocios.tributario;


import br.com.webpublico.entidades.tributario.DocumentacaoLicenciamentoAmbiental;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class DocumentacaoLicenciamentoAmbientalFacade extends AbstractFacade<DocumentacaoLicenciamentoAmbiental> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AssuntoLicenciamentoAmbientalFacade assuntoLicenciamentoAmbientalFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    public DocumentacaoLicenciamentoAmbientalFacade() {
        super(DocumentacaoLicenciamentoAmbiental.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public AssuntoLicenciamentoAmbientalFacade getAssuntoLicenciamentoAmbientalFacade() {
        return assuntoLicenciamentoAmbientalFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    @Override
    public DocumentacaoLicenciamentoAmbiental recuperar(Object id) {
        DocumentacaoLicenciamentoAmbiental parametroLicenciamento = super.recuperar(id);
        if (parametroLicenciamento == null) return null;
        Hibernate.initialize(parametroLicenciamento.getDocumentos());
        return parametroLicenciamento;
    }

    public synchronized DocumentacaoLicenciamentoAmbiental recuperarUltimo() {
        Query q = em.createQuery("from DocumentacaoLicenciamentoAmbiental order by dataRegistro desc ");
        List<DocumentacaoLicenciamentoAmbiental> parametros = q.getResultList();
        if (!parametros.isEmpty()) {
            DocumentacaoLicenciamentoAmbiental parametroLicenciamento = parametros.stream().findFirst().get();
            Hibernate.initialize(parametroLicenciamento.getDocumentos());
            return parametroLicenciamento;
        }
        return null;
    }
}
