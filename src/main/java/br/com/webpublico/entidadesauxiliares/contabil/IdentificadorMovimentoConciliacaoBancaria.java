package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.Identificador;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mateus on 02/08/17.
 */
public class IdentificadorMovimentoConciliacaoBancaria {

    private Identificador identificador;
    private List<MovimentoConciliacaoBancaria> movimentos;

    public IdentificadorMovimentoConciliacaoBancaria() {
    }

    public Identificador getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = identificador;
    }

    public List<MovimentoConciliacaoBancaria> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<MovimentoConciliacaoBancaria> movimentos) {
        this.movimentos = movimentos;
    }

    public BigDecimal getValorTotalIdentificador() {
        BigDecimal total = BigDecimal.ZERO;
        for (MovimentoConciliacaoBancaria movimento : movimentos) {
            total = total.add(movimento.getCredito()).subtract(movimento.getDebito());
        }
        return total;
    }

}
