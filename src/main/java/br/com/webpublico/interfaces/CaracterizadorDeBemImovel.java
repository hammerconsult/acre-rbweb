package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.CondicaoDeOcupacao;
import br.com.webpublico.entidades.GrupoObjetoCompra;
import br.com.webpublico.enums.EstadoConservacaoBem;

/**
 * Created by Desenvolvimento on 11/11/2015.
 */
public interface CaracterizadorDeBemImovel extends CaracterizadorDeBem {

    CondicaoDeOcupacao getCondicaoDeOcupacao();
    String getBci();
    String getNumeroRegistro();
}
