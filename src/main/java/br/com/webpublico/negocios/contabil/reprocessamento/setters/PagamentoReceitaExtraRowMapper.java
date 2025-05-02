package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.PagamentoEstornoRecExtra;
import br.com.webpublico.entidades.PagamentoExtra;
import br.com.webpublico.entidades.PagamentoReceitaExtra;
import br.com.webpublico.entidades.ReceitaExtra;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PagamentoReceitaExtraRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        PagamentoReceitaExtra pagamentoReceitaExtra = new PagamentoReceitaExtra();
        pagamentoReceitaExtra.setId(rs.getLong("ID"));

        PagamentoExtra pagamentoExtra = new PagamentoExtra();
        pagamentoExtra.setId(rs.getLong("PAGAMENTOEXTRA_ID"));
        pagamentoReceitaExtra.setPagamentoExtra(pagamentoExtra.getId() != 0 ? pagamentoExtra : null);

        ReceitaExtra receitaExtra = new ReceitaExtra();
        receitaExtra.setId(rs.getLong("RECEITAEXTRA_ID"));
        pagamentoReceitaExtra.setReceitaExtra(receitaExtra.getId() != 0 ? receitaExtra : null);

        PagamentoEstornoRecExtra pagamentoEstornoRecExtra = new PagamentoEstornoRecExtra();
        pagamentoEstornoRecExtra.setId(rs.getLong("PAGAMENTOESTORNORECEXTRA_ID"));
        pagamentoReceitaExtra.setPagamentoEstornoRecExtra(pagamentoEstornoRecExtra.getId() != 0 ? pagamentoEstornoRecExtra : null);

        return pagamentoReceitaExtra;
    }
}
