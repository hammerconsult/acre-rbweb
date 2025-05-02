package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.entidades.Material;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 16/04/14
 * Time: 09:04
 * To change this template use File | Settings | File Templates.
 */
public interface ItemValidacaoEstoque {
    public BigDecimal getQuantidadeFísica();

    public Material getMaterial();

    public LocalEstoque getLocalEstoque();
}
