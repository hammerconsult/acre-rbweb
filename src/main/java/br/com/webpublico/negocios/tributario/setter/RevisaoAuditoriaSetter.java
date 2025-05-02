package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.RevisaoAuditoria;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RevisaoAuditoriaSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = "insert into revisaoauditoria (id, datahora, ip, usuario) values (?, ?, ?, ?)";
    private RevisaoAuditoria revisaoAuditoria;

    public RevisaoAuditoriaSetter(RevisaoAuditoria revisaoAuditoria) {
        this.revisaoAuditoria = revisaoAuditoria;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {

        ps.setLong(1, revisaoAuditoria.getId());
        ps.setTimestamp(2, new java.sql.Timestamp(revisaoAuditoria.getDataHora().getTime()));
        ps.setString(3, revisaoAuditoria.getIp());
        ps.setString(4, revisaoAuditoria.getUsuario());

    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
