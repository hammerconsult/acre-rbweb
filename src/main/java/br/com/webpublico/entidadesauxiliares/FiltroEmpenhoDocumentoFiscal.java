package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoRequisicaoCompra;

public class FiltroEmpenhoDocumentoFiscal {

    private Long id;
    private TipoFiltroEmpenhoDocto tipo;
    private TipoRequisicaoCompra tipoRequisicaoCompra;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoFiltroEmpenhoDocto getTipo() {
        return tipo;
    }

    public void setTipo(TipoFiltroEmpenhoDocto tipo) {
        this.tipo = tipo;
    }

    public TipoRequisicaoCompra getTipoRequisicaoCompra() {
        return tipoRequisicaoCompra;
    }

    public void setTipoRequisicaoCompra(TipoRequisicaoCompra tipoRequisicaoCompra) {
        this.tipoRequisicaoCompra = tipoRequisicaoCompra;
    }

    public String getCondicaoSql() {
        if (TipoFiltroEmpenhoDocto.ENTRADA_COMPRA.equals(tipo)) {
            return " and dfec.doctofiscalliquidacao_id = " + id;
        }
        return " and req.id = " + id;
    }

    public enum TipoFiltroEmpenhoDocto {
        ENTRADA_COMPRA,
        REQUISICAO_COMPRA;
    }

}
