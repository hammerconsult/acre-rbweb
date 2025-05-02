package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DocumentoOficial;
import br.com.webpublico.entidades.ExecucaoOrcDocumentoOficial;
import br.com.webpublico.entidades.ModeloDoctoOficial;
import br.com.webpublico.entidades.TipoDoctoOficial;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.interfaces.EntidadeContabil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class NotaOrcamentariaFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;

    public void imprimirDocumentoOficial(NotaExecucaoOrcamentaria notaExecucaoOrcamentaria, ModuloTipoDoctoOficial moduloTipoDoctoOficial, EntidadeContabil entidadeContabil, boolean isDownload) throws AtributosNulosException, UFMException {
        DocumentoOficial documentoOficial = salvarDocumentoOficial(notaExecucaoOrcamentaria, moduloTipoDoctoOficial, entidadeContabil);
        if (documentoOficialFacade.isPermitirImpressao(documentoOficial)) {
            if (isDownload) {
                documentoOficialFacade.downloadDocumentoOficialPdf(documentoOficial, notaExecucaoOrcamentaria.getNomeDaNota(), false,
                    " table { width: 100% !important; font-size: 13px !important;}\n",
                    " size: A4 !important; margin-top: 2% !important; margin-left: 3% !important; margin-right: 2% !important; ");
            } else {
                documentoOficialFacade.emiteDocumentoOficial(documentoOficial);
            }
        }
    }

    public void gerarRelatorioDasNotasOrcamentarias(List<NotaExecucaoOrcamentaria> notas, ModuloTipoDoctoOficial moduloTipoDoctoOficial) {
        documentoOficialFacade.gerarRelatorioDasNotasOrcamentarias(notas, moduloTipoDoctoOficial);
    }

    private void salvarDocumentoOficialExecucaoOrc(DocumentoOficial documentoOficial, EntidadeContabil entidadeContabil, ModuloTipoDoctoOficial modulo) {
        ExecucaoOrcDocumentoOficial execucaoOrcDocumentoOficial = new ExecucaoOrcDocumentoOficial().build(entidadeContabil, modulo, documentoOficial);
        salvarDocumentoOficialExecucaoOrc(execucaoOrcDocumentoOficial);
    }

    public DocumentoOficial salvarDocumentoOficial(NotaExecucaoOrcamentaria notaExecucaoOrcamentaria, ModuloTipoDoctoOficial moduloTipoDoctoOficial, EntidadeContabil entidadeContabil) throws AtributosNulosException, UFMException {
        List<TipoDoctoOficial> tipoDoctoOficials = tipoDoctoOficialFacade.recuperaTiposDoctoOficialPorModulo(moduloTipoDoctoOficial, "");
        if (tipoDoctoOficials == null || tipoDoctoOficials.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar a configuração de documento oficial para o(a) " + moduloTipoDoctoOficial.getDescricao() + ".");
        }
        TipoDoctoOficial tipoDoctoOficial = tipoDoctoOficials.get(0);
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipoDoctoOficial);

        DocumentoOficial documentoOficial = documentoOficialFacade.buscarDocumentoOficialExecucaoOrcamentariaPorModulo(notaExecucaoOrcamentaria.getId(), moduloTipoDoctoOficial);
        if (documentoOficial == null) {
            documentoOficial = documentoOficialFacade.geraNovoDocumento(notaExecucaoOrcamentaria, mod, null, null, notaExecucaoOrcamentaria.getIdUnidadeOrcamentaria().longValue(), null);
            salvarDocumentoOficialExecucaoOrc(documentoOficial, entidadeContabil, moduloTipoDoctoOficial);
        } else {
            String conteudoAtual = documentoOficial.getConteudo();
            String conteudoNovo = documentoOficialFacade.mesclaTagsDocto(mod.getConteudo(), documentoOficial, mod.getTipoDoctoOficial().getModuloTipoDoctoOficial(), notaExecucaoOrcamentaria, null);
            if (!conteudoAtual.equals(conteudoNovo)) {
                documentoOficialFacade.removerExcluindoExecucaoOrc(documentoOficial);
                documentoOficial = documentoOficialFacade.geraNovoDocumento(notaExecucaoOrcamentaria, mod, null, null, notaExecucaoOrcamentaria.getIdUnidadeOrcamentaria().longValue(), conteudoNovo);
                salvarDocumentoOficialExecucaoOrc(documentoOficial, entidadeContabil, moduloTipoDoctoOficial);
            }
        }
        return documentoOficial;
    }

    public void salvarDocumentoOficialExecucaoOrc(ExecucaoOrcDocumentoOficial execucaoOrcDocumentoOficial) {
        em.persist(execucaoOrcDocumentoOficial);
    }

    public List<ExecucaoOrcDocumentoOficial> buscarExecucoesOrcsDocumentosOficiais(Long idReferencia, ModuloTipoDoctoOficial modulo) {
        return documentoOficialFacade.buscarExecucoesOrcsDocumentosOficiais(idReferencia, modulo);
    }

}
