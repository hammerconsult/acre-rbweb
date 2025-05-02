package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.SolicitacaoMaterialDocumentoOficial;
import br.com.webpublico.entidades.TipoDoctoOficial;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class VOSolicitacaoMaterialDocumentoOfical implements Serializable {
    private TipoDoctoOficial tipoDoctoOficial;
    private List<SolicitacaoMaterialDocumentoOficial> documentosOficiais;

    public VOSolicitacaoMaterialDocumentoOfical() {
        documentosOficiais = Lists.newArrayList();
    }

    public VOSolicitacaoMaterialDocumentoOfical(TipoDoctoOficial tipoDoctoOficial, List<SolicitacaoMaterialDocumentoOficial> documentosOficiais) {
        this.tipoDoctoOficial = tipoDoctoOficial;
        this.documentosOficiais = documentosOficiais;
    }

    public TipoDoctoOficial getTipoDoctoOficial() {
        return tipoDoctoOficial;
    }

    public void setTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.tipoDoctoOficial = tipoDoctoOficial;
    }

    public List<SolicitacaoMaterialDocumentoOficial> getDocumentosOficiais() {
        return documentosOficiais;
    }

    public void setDocumentosOficiais(List<SolicitacaoMaterialDocumentoOficial> documentosOficiais) {
        this.documentosOficiais = documentosOficiais;
    }
}
