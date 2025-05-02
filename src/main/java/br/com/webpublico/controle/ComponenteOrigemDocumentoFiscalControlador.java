package br.com.webpublico.controle;

import br.com.webpublico.entidades.DoctoFiscalLiquidacao;
import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.entidadesauxiliares.DocumentoFiscalIntegracao;
import br.com.webpublico.entidadesauxiliares.DocumentoFiscalIntegracaoAssistente;
import br.com.webpublico.negocios.IntegradorDocumentoFiscalLiquidacaoFacade;
import br.com.webpublico.util.FacesUtil;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class ComponenteOrigemDocumentoFiscalControlador implements Serializable {

    @EJB
    private IntegradorDocumentoFiscalLiquidacaoFacade facade;
    private DocumentoFiscalIntegracao documentoFiscalIntegracao;

    public void recuperarDadosDocumentoComprobatorio(DoctoFiscalLiquidacao doctoFiscal, Empenho empenho) {
        if (doctoFiscal != null && empenho != null) {
            DocumentoFiscalIntegracaoAssistente assistente = new DocumentoFiscalIntegracaoAssistente();
            assistente.setDoctoFiscalLiquidacao(doctoFiscal);
            assistente.setEmpenho(empenho);
            assistente.setDadosComponente(true);
            documentoFiscalIntegracao = facade.criarOrigemDocumentoFsical(assistente);
            FacesUtil.executaJavaScript("$('#modalDetalhesDocumento').modal('show');");
        }
    }

    public void redirecionarParaEfetivacaoAquisicaoBemMovel(Long idOrigem) {
        FacesUtil.redirecionamentoInterno("/efetivacao-aquisicao-bem-movel/ver/" + idOrigem + "/");
    }

    public void redirecionarParaEntradaPorCompra(Long idOrigem) {
        FacesUtil.redirecionamentoInterno("/entrada-por-compra/ver/" + idOrigem + "/");
    }

    public void redirecionarParaBemMovel(Long idBem) {
        FacesUtil.redirecionamentoInterno("/bem-movel/ver/" + idBem + "/");
    }

    public DocumentoFiscalIntegracao getDocumentoFiscalIntegracao() {
        return documentoFiscalIntegracao;
    }

    public void setDocumentoFiscalIntegracao(DocumentoFiscalIntegracao documentoFiscalIntegracao) {
        this.documentoFiscalIntegracao = documentoFiscalIntegracao;
    }
}
