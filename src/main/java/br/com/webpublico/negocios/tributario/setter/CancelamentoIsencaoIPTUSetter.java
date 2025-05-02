package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.entidades.CancelamentoIsencaoCadastroImobiliario;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class CancelamentoIsencaoIPTUSetter implements BatchPreparedStatementSetter {
    private CancelamentoIsencaoCadastroImobiliario cancelamento;
    private Long revisaoAuditoria;
    private Long tipoRevisaoAuditoria;


    public CancelamentoIsencaoIPTUSetter(CancelamentoIsencaoCadastroImobiliario cancelamento) {
        this.cancelamento = cancelamento;
    }

    public CancelamentoIsencaoIPTUSetter(CancelamentoIsencaoCadastroImobiliario cancelamento, Long revisaoAuditoria, Long tipoRevisaoAuditoria) {
        this.cancelamento = cancelamento;
        this.revisaoAuditoria = revisaoAuditoria;
        this.tipoRevisaoAuditoria = tipoRevisaoAuditoria;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, cancelamento.getId());
        if (cancelamento.getUsuarioSistema() != null) {
            ps.setLong(2, cancelamento.getUsuarioSistema().getId());
        } else {
            ps.setNull(2, Types.NUMERIC);
        }
        if (cancelamento.getDataOperacao() != null) {
            ps.setDate(3, new java.sql.Date(cancelamento.getDataOperacao().getTime()));
        } else {
            ps.setNull(3, Types.DATE);
        }
        if (cancelamento.getMotivo() != null) {
            ps.setString(4, cancelamento.getMotivo());
        } else {
            ps.setNull(4, Types.VARCHAR);
        }
        if (cancelamento.getIsencao() != null) {
            ps.setLong(5, cancelamento.getIsencao().getId());
        } else {
            ps.setNull(5, Types.NUMERIC);
        }
        if (revisaoAuditoria != null && tipoRevisaoAuditoria != null) {
            ps.setLong(6, revisaoAuditoria);
            ps.setLong(7, tipoRevisaoAuditoria);

        }
    }


    @Override
    public int getBatchSize() {
        return 1;
    }
}
