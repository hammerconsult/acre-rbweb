package br.com.webpublico.negocios.tributario.consultaparcela.DTO;


import br.com.webpublico.entidades.Tributo;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ValorTipoTributo {
    private Tributo.TipoTributo tipoTributo;
    private BigDecimal valor;

    public ValorTipoTributo(Tributo.TipoTributo tipoTributo, BigDecimal valor) {
        this.tipoTributo = tipoTributo;
        this.valor = valor;
    }

    public ValorTipoTributo(ResultSet resultSet) throws SQLException {
        this.tipoTributo = Tributo.TipoTributo.valueOf(resultSet.getString(1));
        this.valor = resultSet.getBigDecimal(2);
    }

    public ValorTipoTributo(Object[] ob) {
        this.tipoTributo = Tributo.TipoTributo.valueOf((String) ob[0]);
        this.valor = ((BigDecimal) ob[1]);
    }

    public Tributo.TipoTributo getTipoTributo() {
        return tipoTributo;
    }

    public void setTipoTributo(Tributo.TipoTributo tipoTributo) {
        this.tipoTributo = tipoTributo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
