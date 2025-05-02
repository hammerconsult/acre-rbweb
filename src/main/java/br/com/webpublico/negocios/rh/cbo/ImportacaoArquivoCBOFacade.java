package br.com.webpublico.negocios.rh.cbo;

import br.com.webpublico.entidades.rh.ImportacaoArquivoCBO;

import br.com.webpublico.negocios.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Stateless
public class ImportacaoArquivoCBOFacade extends AbstractFacade<ImportacaoArquivoCBO> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ImportacaoArquivoCBOFacade() {
        super(ImportacaoArquivoCBO.class);
    }

    @Override
    public void salvar(ImportacaoArquivoCBO entity) {
        entity.setResponsavel(sistemaFacade.getUsuarioCorrente());
        entity.setDataImportacao(new Date());
        super.salvar(entity);
    }

}
