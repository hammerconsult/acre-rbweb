package br.com.webpublico.interfaces;

/**
 * Created by HardRock on 26/04/2017.
 */
public interface AssistentePaginacao {

    Integer getTotalRegistro();

    void setTotalRegistro(Integer totalRegistro);

    Integer getIndexPagina();

    void setIndexPagina(Integer indexPagina);

    Double getTotalRegistroPorPagina();

    Integer getTamanhoListaPorPagina();

    void setTamanhoListaPorPagina(Integer tamanhoListaPorPagina);

}
