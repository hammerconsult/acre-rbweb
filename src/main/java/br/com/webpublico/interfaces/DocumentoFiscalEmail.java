package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.DoctoAcaoFiscal;
import br.com.webpublico.enums.TipoDoctoAcaoFiscal;

/**
 * @author Alex
 * @since 29/03/2017 17:35
 */
public interface DocumentoFiscalEmail {

    String getNome();
    DoctoAcaoFiscal getDocumento();
    String getIdentificacao();
    TipoDoctoAcaoFiscal getTipoDocumento();
}
