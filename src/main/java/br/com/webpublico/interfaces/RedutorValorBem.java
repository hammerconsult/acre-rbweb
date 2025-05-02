package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.LogReducaoOuEstorno;
import br.com.webpublico.enums.TipoReducaoValorBem;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 16/10/14
 * Time: 17:58
 * To change this template use File | Settings | File Templates.
 */
public interface RedutorValorBem extends EventoBemIncorporavelComContabil {
    public TipoReducaoValorBem getTipoReducaoValorBem();

    public LogReducaoOuEstorno getLogReducaoOuEstorno();

    public String getMensagemSucesso();

    public String getMensagemErro();

    @Override
    public Bem getBem();
}
