package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DocumentosNecessariosAnexo;
import br.com.webpublico.entidades.ItemDocumentoAnexo;
import br.com.webpublico.enums.TipoFuncionalidadeParaAnexo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ITipoDocumentoAnexo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by William on 19/02/2016.
 */
@Stateless
public class DocumentosNecessariosAnexoFacade extends AbstractFacade<DocumentosNecessariosAnexo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public DocumentosNecessariosAnexoFacade() {
        super(DocumentosNecessariosAnexo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean isPossuiMaisDeUmRegistroCadastrado() {
        Query q = em.createNativeQuery(" select * from DocumentosNecessariosAnexo");
        return q.getResultList().isEmpty();
    }

    @Override
    public DocumentosNecessariosAnexo recuperar(Object id) {
        DocumentosNecessariosAnexo doc = em.find(DocumentosNecessariosAnexo.class, id);
        doc.getItens().size();
        return doc;
    }

    public DocumentosNecessariosAnexo recuperarDocumentosNecessariosAnexoVigente() {
        String sql = "SELECT * FROM DOCUMENTOSNECESSARIOSANEXO " +
            "where current_date between datainicio and datafim ";
        Query q = em.createNativeQuery(sql, DocumentosNecessariosAnexo.class);
        if (!q.getResultList().isEmpty()) {
            return (DocumentosNecessariosAnexo) q.getResultList().get(0);
        }
        return null;
    }

    public void validarSeTodosOsTipoDeDocumentoForamAnexados(TipoFuncionalidadeParaAnexo tipo, List<? extends ITipoDocumentoAnexo> tipoDocumentoAnexos) {
        ValidacaoException ve = new ValidacaoException();
        DocumentosNecessariosAnexo doc = recuperarDocumentosNecessariosAnexoVigente();
        if (doc == null) {
            return;
        }
        for (ItemDocumentoAnexo item : doc.getItens()) {
            boolean igual = false;
            if (item.getTipoFuncionalidadeParaAnexo().equals(tipo)) {
                for (ITipoDocumentoAnexo tipoDocumentoAnexo : tipoDocumentoAnexos) {
                    if (item.getTipoDocumentoAnexo().equals(tipoDocumentoAnexo.getTipoDocumentoAnexo())) {
                        igual = true;
                        break;
                    }
                }
                if (!igual) {
                    logger.warn("Não tem todos documentos necessários anexados.");
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Tipo Documento Anexo do tipo '" + item.getTipoDocumentoAnexo().getDescricao() + "'.");
                }
            }
        }
        ve.lancarException();
    }

}
