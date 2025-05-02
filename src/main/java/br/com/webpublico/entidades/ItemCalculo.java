package br.com.webpublico.entidades;

import java.math.BigDecimal;

public abstract class ItemCalculo extends SuperEntidade {

    public abstract Tributo getTributo();
    public abstract BigDecimal getValor();
}
