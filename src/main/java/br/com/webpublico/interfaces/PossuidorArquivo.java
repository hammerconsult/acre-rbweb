package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.DetentorArquivoComposicao;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 02/07/14
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
public interface PossuidorArquivo {

    public DetentorArquivoComposicao getDetentorArquivoComposicao();

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao);
}
