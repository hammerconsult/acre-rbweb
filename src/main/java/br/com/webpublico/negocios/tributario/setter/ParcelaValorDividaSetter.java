package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ParcelaValorDivida;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class ParcelaValorDividaSetter implements BatchPreparedStatementSetter {

    private final List<ParcelaValorDivida> parcelas;
    private final List<Long> ids;
    private Long idRevisao;

    public ParcelaValorDividaSetter(List<ParcelaValorDivida> parcelas, List<Long> ids) {
        this.parcelas = parcelas;
        this.ids = ids;
    }

    public ParcelaValorDividaSetter(List<ParcelaValorDivida> parcelas, List<Long> ids, Long idRevisao) {
        this.parcelas = parcelas;
        this.ids = ids;
        this.idRevisao = idRevisao;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ParcelaValorDivida parcela = parcelas.get(i);
        parcela.setId(ids.get(i));
        if (parcela.getResultadoParcela() != null && idRevisao == null) {
            parcela.getResultadoParcela().setIdParcela(parcela.getId());
        }
        ps.setLong(1, parcela.getId());
        ps.setLong(2, parcela.getOpcaoPagamento().getId());
        ps.setLong(3, parcela.getValorDivida().getId());
        ps.setDate(4, new Date(parcela.getVencimento().getTime()));
        ps.setBigDecimal(5, parcela.getPercentualValorTotal());
        ps.setDate(6, new Date(parcela.getDataRegistro().getTime()));
        ps.setString(7, parcela.getSequenciaParcela());
        ps.setBoolean(8, parcela.getDividaAtiva());
        ps.setBoolean(9, parcela.getDividaAtivaAjuizada());
        ps.setBigDecimal(10, parcela.getValor());
        if (parcela.getDataPrescricao() != null) {
            ps.setDate(11, new Date(parcela.getDataPrescricao().getTime()));
        } else {
            ps.setNull(11, Types.NULL);
        }
        if (idRevisao != null) {
            ps.setLong(12, idRevisao);
            ps.setLong(13, 0L);
        }
    }

    @Override
    public int getBatchSize() {
        return parcelas.size();
    }
}
