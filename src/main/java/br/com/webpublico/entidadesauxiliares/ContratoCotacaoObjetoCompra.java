package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.ObjetoCompra;

import java.math.BigDecimal;

public class ContratoCotacaoObjetoCompra {
    ObjetoCompra objetoCompra;
    BigDecimal preco;

    public BigDecimal getPreco() {
        return preco;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }
}
