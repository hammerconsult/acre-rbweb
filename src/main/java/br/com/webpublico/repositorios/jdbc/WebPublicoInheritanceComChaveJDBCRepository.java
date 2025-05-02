package br.com.webpublico.repositorios.jdbc;

import br.com.webpublico.entidades.EntidadeWebPublicoComChave;
import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.repositorios.jdbc.util.NextIDFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Daniel Franco
 * @since 18/09/2015 11:12
 */
public abstract class WebPublicoInheritanceComChaveJDBCRepository<T extends EntidadeWebPublicoComChave, C extends T> extends WebPublicoChaveNegocioJDBCRepository<T> {

    public WebPublicoInheritanceComChaveJDBCRepository(Connection conn) {
        super(conn);
    }

    public WebPublicoInheritanceComChaveJDBCRepository(Connection conn, RevisaoAuditoriaJDBCRepository revisaoAuditoriaJDBCRepository) {
        super(conn, revisaoAuditoriaJDBCRepository);
    }

    public WebPublicoInheritanceComChaveJDBCRepository(Connection conn, NextIDFactory nextIDFactory) {
        super(conn, nextIDFactory);
    }

    @Override
    protected PreparedStatement recuperarPreparedStatementUpdate() {
        return null;
    }

    @Override
    protected T preencherRegistroSimples(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    protected void preencherCamposUpdate(T registro, PreparedStatement ps) throws SQLException {

    }

    protected abstract C instanciar(Long id);

    protected abstract PreparedStatement recuperarPreparedStatementInsertEspecifico() throws SQLException;

    protected abstract void preencherCamposInsertEspecificos(C registro, PreparedStatement ps) throws SQLException;

    protected abstract PreparedStatement recuperarPreparedStatementUpdateEspecifico() throws SQLException;

    protected abstract void preencherCamposUpdateEspecificos(C registro, PreparedStatement ps) throws SQLException;

    protected abstract void preencherRegistroEspecifico(C registro, ResultSet rs) throws SQLException;

    protected abstract PreparedStatement recuperarPreparedStatementInsertAudEspecifico() throws SQLException;

    protected abstract void preencherCamposInsertAudEspecifico(RevisaoAuditoria revisaoAuditoria, C registro, PreparedStatement ps, Long tipo) throws SQLException;

    protected abstract void validarRegistroFilho(C registro);

    @Override
    protected void inserirFilhos(RevisaoAuditoria revisaoAuditoria, T registro, boolean isDirect, Long tipo) throws SQLException {
        if (isDirect) {
            recuperarPreparedStatementInsertEspecifico().execute();
        } else {
            recuperarPreparedStatementInsertEspecifico().addBatch();
        }
        inserirAuditoriaEspecifico(revisaoAuditoria, (C) registro, isDirect, tipo);
    }

    private void inserirAuditoriaEspecifico(RevisaoAuditoria revisaoAuditoria, C registro, boolean isDirect, Long tipo) throws SQLException {
        PreparedStatement ps = recuperarPreparedStatementInsertAudEspecifico();
        if (ps != null) {
            ps.clearParameters();
            preencherCamposInsertAudEspecifico(revisaoAuditoria, registro, ps, tipo);
            if (isDirect) {
                ps.execute();
            } else {
                ps.addBatch();
            }
        }
    }


    @Override
    public void executarBatchInsertEspecifico() throws SQLException {
        PreparedStatement ps = recuperarPreparedStatementInsertEspecifico();

        if (ps == null)
            return;

        ps.executeBatch();

        PreparedStatement psAud = recuperarPreparedStatementInsertAudEspecifico();
        if (psAud != null) {
            psAud.executeBatch();
        }
    }

    @Override
    protected void updateFilhos(RevisaoAuditoria revisaoAuditoria, T registro, boolean isDirect, Long tipo) throws SQLException {
        if (isDirect) {
            recuperarPreparedStatementUpdateEspecifico().execute();
        } else {
            recuperarPreparedStatementUpdateEspecifico().addBatch();
        }
        inserirAuditoriaEspecifico(revisaoAuditoria, (C) registro, isDirect, tipo);
    }

    @Override
    public void executarBatchUpdateEspecifico() throws SQLException {
        PreparedStatement psEspecifico = recuperarPreparedStatementUpdateEspecifico();
        if (psEspecifico != null) {
            psEspecifico.executeBatch();
        }

        PreparedStatement psAudEspecifico = recuperarPreparedStatementInsertAudEspecifico();
        if (psAudEspecifico != null) {
            psAudEspecifico.executeBatch();
        }
    }
}
