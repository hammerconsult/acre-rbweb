package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.DescontoItemParcela;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DescontoItemParcelaSetter implements BatchPreparedStatementSetter {

    private final List<DescontoItemParcela> descontos;
    private final List<Long> ids;
    private Long idRevisao;

    public DescontoItemParcelaSetter(List<DescontoItemParcela> descontos, List<Long> ids) {
        this.descontos = descontos;
        this.ids = ids;
    }

    public DescontoItemParcelaSetter(List<DescontoItemParcela> descontos, List<Long> ids, Long idRevisao) {
        this.descontos = descontos;
        this.ids = ids;
        this.idRevisao = idRevisao;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        DescontoItemParcela desconto = descontos.get(i);
        desconto.setId(ids.get(i));
        ps.setLong(1, desconto.getId());
        ps.setLong(2, desconto.getItemParcelaValorDivida().getId());
        ps.setDate(3, new Date(desconto.getInicio().getTime()));
        ps.setDate(4, new Date(desconto.getFim().getTime()));
        ps.setString(5, desconto.getTipo().name());
        ps.setBigDecimal(6, desconto.getDesconto());
        ps.setString(7, desconto.getOrigem().name());
        if (idRevisao != null) {
            ps.setLong(8, idRevisao);
            ps.setLong(9, 0L);
        }
    }

    @Override
    public int getBatchSize() {
        return descontos.size();
    }
}
