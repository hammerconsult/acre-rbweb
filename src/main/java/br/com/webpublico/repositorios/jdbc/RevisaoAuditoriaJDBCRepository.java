package br.com.webpublico.repositorios.jdbc;

import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.repositorios.jdbc.statement.StatementsRevisaoAuditoria;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static br.com.webpublico.repositorios.jdbc.util.JDBCUtil.validarCampo;

/**
 * Created by Wellington Abdo on 14/02/2017.
 */
@RepositorioJDBC
public class RevisaoAuditoriaJDBCRepository extends WebPublicoJDBCRepository<RevisaoAuditoria> {

    @Autowired
    public RevisaoAuditoriaJDBCRepository(Connection conn) {
        super(conn);
    }

    @Override
    protected void validarRegistro(RevisaoAuditoria registro) {
        validarCampo(registro.getDataHora(), "Data/Hora da Revisão");
        validarCampo(registro.getIp(), "Ip da Revisão");
        validarCampo(registro.getUsuario(), "Usuário da Revisão");
    }

    @Override
    public PreparedStatement recuperarPreparedStatementInsert() {
        return getPreparedStatement(StatementsRevisaoAuditoria.INSERT);
    }

    @Override
    protected PreparedStatement recuperarPreparedStatementUpdate() {
        return null;
    }

    @Override
    protected PreparedStatement recuperarPreparedStatementInsertAud() {
        return null;
    }

    @Override
    protected PreparedStatement recuperarPreparedStatementPreCarga() {
        return getPreparedStatement(StatementsRevisaoAuditoria.SELECT_ALL);
    }

    @Override
    protected RevisaoAuditoria preencherRegistro(ResultSet rs) throws SQLException {
        RevisaoAuditoria revisaoAuditoria = new RevisaoAuditoria();
        revisaoAuditoria.setId(getLong(rs, "id"));
        revisaoAuditoria.setDataHora(getDate(rs, "datahora"));
        revisaoAuditoria.setIp(getString(rs, "ip"));
        revisaoAuditoria.setUsuario(getString(rs, "usuario"));
        return revisaoAuditoria;
    }

    @Override
    protected RevisaoAuditoria preencherRegistroSimples(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    protected void preencherCamposInsert(RevisaoAuditoria registro, PreparedStatement ps) throws SQLException {
        registro.setId(nextIDFactory.nextID());
        setLongObrigatorio(ps, 1, registro.getId());
        setDateObrigatorio(ps, 2, registro.getDataHora());
        setStringObrigatorio(ps, 3, registro.getIp());
        setStringObrigatorio(ps, 4, registro.getUsuario());
    }

    @Override
    protected void preencherCamposUpdate(RevisaoAuditoria registro, PreparedStatement ps) throws SQLException {

    }

    @Override
    protected void preencherCamposInsertAud(RevisaoAuditoria revisaoAuditoria, RevisaoAuditoria registro, PreparedStatement ps, Long tipo) throws SQLException {

    }
}
