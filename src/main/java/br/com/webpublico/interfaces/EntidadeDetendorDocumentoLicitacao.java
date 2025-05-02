package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.DetentorDocumentoLicitacao;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;

public interface EntidadeDetendorDocumentoLicitacao {

    DetentorDocumentoLicitacao getDetentorDocumentoLicitacao();

    TipoMovimentoProcessoLicitatorio getTipoAnexo();

    void setDetentorDocumentoLicitacao(DetentorDocumentoLicitacao detentorDocumentoLicitacao);

}
