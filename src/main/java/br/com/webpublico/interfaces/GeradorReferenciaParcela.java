package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;

public interface GeradorReferenciaParcela {

    public void geraReferencia(SituacaoParcelaValorDivida spvd);

    public String geraReferencia(Long idParcela);

}
