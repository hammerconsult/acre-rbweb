/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.documentoportalservidor;

import br.com.webpublico.entidades.ArquivoComposicao;
import br.com.webpublico.entidades.DocumentoObrigatorioPortal;
import br.com.webpublico.entidades.rh.documentoportalservidor.DocumentoPortalContribuinte;
import br.com.webpublico.enums.TipoPessoa;
import br.com.webpublico.enums.TipoSolicitacaoCadastroPessoa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.util.Util;
import br.com.webpublico.ws.model.WSDocumentoPortalServidor;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class DocumentoPortalContribuinteFacade extends AbstractFacade<DocumentoPortalContribuinte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocumentoPortalContribuinteFacade() {
        super(DocumentoPortalContribuinte.class);
    }

    public List<DocumentoPortalContribuinte> getItensParaContruirDocumentoPortalContribuinte() {
        String hql = "select d from DocumentoPortalContribuinte d where d.superior is null order by d.ordem asc";
        Query q = getEntityManager().createQuery(hql);
        return q.getResultList();
    }

    public DocumentoPortalContribuinte buscarDocumentosPortalServidorComArquivo(Object id) {
        DocumentoPortalContribuinte dps = em.find(DocumentoPortalContribuinte.class, id);
        dps.getDetentorArquivoComposicao().getArquivosComposicao().size();
        for (ArquivoComposicao arquivoComposicao : dps.getDetentorArquivoComposicao().getArquivosComposicao()) {
            arquivoComposicao.getArquivo().getPartes().size();
        }

        return dps;
    }

    public List<WSDocumentoPortalServidor> buscarDocumentosParaOservidorNoPortal() {
        String hql = " select dps from DocumentoPortalContribuinte dps" +
            " where dps.ativo = :ativo  order by dps.publicadoEm desc, dps.nomeNoPortal asc";
        Query q = em.createQuery(hql);
        q.setParameter("ativo", Boolean.TRUE);
        List<DocumentoPortalContribuinte> resultado = q.getResultList();
        return transformarResultadoParaPortal(resultado);
    }

    private List<WSDocumentoPortalServidor> transformarResultadoParaPortal(List<DocumentoPortalContribuinte> resultado) {
        List<WSDocumentoPortalServidor> documentosNoFormatoDoPortal = Lists.newArrayList();
        for (DocumentoPortalContribuinte documentoDaVez : resultado) {
            documentoDaVez = buscarDocumentosPortalServidorComArquivo(documentoDaVez.getId());

            WSDocumentoPortalServidor documentoDoPortal = new WSDocumentoPortalServidor();
            documentoDoPortal.setNomeNoPortal(documentoDaVez.getNomeNoPortal());
            documentoDoPortal.setPublicadoEm(documentoDaVez.getPublicadoEm());
            documentoDoPortal.setAtualizadoEm(documentoDaVez.getAtualizadoEm());
            documentoDoPortal.setOrdem(documentoDaVez.getOrdem());
            documentoDoPortal.setId(documentoDaVez.getId());
            documentoDoPortal.setSuperior(documentoDaVez.getSuperior() != null ? documentoDaVez.getSuperior().getId() : null);

            try {
                documentoDoPortal.setMimeType(documentoDaVez.getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo().getMimeType());
                documentoDoPortal.setDados(documentoDaVez.getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo().getByteArrayDosDados());
                documentoDoPortal.setNomeDoArquivo(documentoDaVez.getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo().getNome());
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }

            documentosNoFormatoDoPortal = Util.adicionarObjetoEmLista(documentosNoFormatoDoPortal, documentoDoPortal);
        }

        for (WSDocumentoPortalServidor wsDoc : documentosNoFormatoDoPortal) {
            for (WSDocumentoPortalServidor doc : documentosNoFormatoDoPortal) {
                if (wsDoc.getId().equals(doc.getSuperior())) {
                    wsDoc.getFilhos().add(doc);
                }
            }
        }

        return documentosNoFormatoDoPortal;
    }

    @Override
    public DocumentoPortalContribuinte recuperar(Object id) {
        DocumentoPortalContribuinte doc = super.recuperar(id);
        if (doc != null && doc.getDetentorArquivoComposicao() != null) {
            doc.getDetentorArquivoComposicao().getArquivosComposicao().size();
            for (ArquivoComposicao arquivoComposicao : doc.getDetentorArquivoComposicao().getArquivosComposicao()) {
                arquivoComposicao.getArquivo().getPartes().size();
            }
        }
        return doc;
    }

    public List<DocumentoObrigatorioPortal> buscarDocumentosObrigatorioCadastro(TipoPessoa tipoPessoa, TipoSolicitacaoCadastroPessoa tipoSolicitacaoCadastroPessoa) {
        StringBuilder hql = new StringBuilder("select doc from DocumentoObrigatorioPortal doc")
            .append(" where doc.tipoSolicitacaoCadastroPessoa = :tipoSolicitacaoCadastroPessoa ");
        if (tipoPessoa != null) {
            hql.append(" and doc.tipoPessoa = :tipoPessoa ");
        }
        Query q = em.createQuery(hql.toString());
        if (tipoPessoa != null) {
            q.setParameter("tipoPessoa", tipoPessoa);
        }
        q.setParameter("tipoSolicitacaoCadastroPessoa", tipoSolicitacaoCadastroPessoa);
        return q.getResultList();
    }

}
