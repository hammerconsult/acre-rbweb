package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.Desdobramento;
import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.contabil.execucao.DesdobramentoPagamento;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DesdobramentoPagamentoRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        DesdobramentoPagamento desdobramentoPagamento = new DesdobramentoPagamento();
        desdobramentoPagamento.setId(rs.getLong("ID"));
        Pagamento pagamento = new Pagamento();
        pagamento.setId(rs.getLong("PAGAMENTO_ID"));
        desdobramentoPagamento.setPagamento(pagamento.getId() != 0 ? pagamento : null);
        Desdobramento desdobramento = new Desdobramento();
        desdobramento.setId(rs.getLong("DESDOBRAMENTO_ID"));
        desdobramentoPagamento.setDesdobramento(desdobramento.getId() != 0 ? desdobramento : null);
        desdobramentoPagamento.setValor(rs.getBigDecimal("VALOR"));
        desdobramentoPagamento.setSaldo(rs.getBigDecimal("SALDO"));
        return desdobramentoPagamento;
    }
}
