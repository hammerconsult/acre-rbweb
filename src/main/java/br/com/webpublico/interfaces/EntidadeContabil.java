package br.com.webpublico.interfaces;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.TipoEventoContabil;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 16/04/14
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
public interface EntidadeContabil {
    public String getReferenciaArquivoPrestacaoDeContas();

    public String getComplementoHistoricoPrestacaoDeContas();
}
