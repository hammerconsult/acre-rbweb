package br.com.webpublico.negocios.jdbc.rowmapper;

import br.com.webpublico.entidades.RevisaoAuditoria;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RevisaoAuditoriaRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        RevisaoAuditoria revisaoAuditoria = new RevisaoAuditoria();
        revisaoAuditoria.setId(rs.getLong("ID"));
        revisaoAuditoria.setDataHora(rs.getTimestamp("DATAHORA"));
        revisaoAuditoria.setUsuario(rs.getString("USUARIO"));
        revisaoAuditoria.setIp(rs.getString("IP"));
        return revisaoAuditoria;
    }
}
