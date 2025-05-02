package br.com.webpublico.repositorios.jdbc;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.repositorios.jdbc.statement.StatementsBanco;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static br.com.webpublico.repositorios.jdbc.util.JDBCUtil.validarCampo;

/**
 * @author Paschualetto
 * @since 15/10/2015
 */
@RepositorioJDBC
public class BancoJDBCRepository extends WebPublicoChaveNegocioJDBCRepository<Banco> {

    private static final Logger logger = LoggerFactory.getLogger(BancoJDBCRepository.class);
    private Map<String, Banco> bancosPorDescricao;

    @Autowired
    public BancoJDBCRepository(Connection conn, RevisaoAuditoriaJDBCRepository revisaoAuditoriaJDBCRepository) {
        super(conn, revisaoAuditoriaJDBCRepository);
    }

    @Override
    protected void validarRegistro(Banco registro) {
        validarCampo(registro.getNumeroBanco(), "Numero");
        validarCampo(registro.getDescricao(), "Descriçao");
    }

    @Override
    protected PreparedStatement recuperarPreparedStatementInsert() {
        return getPreparedStatement(StatementsBanco.INSERT);
    }

    @Override
    protected void preencherCamposInsert(Banco registro, PreparedStatement ps) throws SQLException {
        registro.setId(nextIDFactory.nextID());
        setLongObrigatorio(ps, 1, registro.getId());
        setStringObrigatorio(ps, 2, registro.getDescricao());
        setStringObrigatorio(ps, 3, registro.getNumeroBanco());
        setStringOpcional(ps, 4, registro.getDigitoVerificador());
        setStringOpcional(ps, 5, registro.getHomePage());
        setStringOpcional(ps, 6, registro.getNumeroContrato());
    }

    @Override
    protected PreparedStatement recuperarPreparedStatementUpdate() {
        return null;
    }

    @Override
    protected void preencherCamposUpdate(Banco registro, PreparedStatement ps) throws SQLException {

    }

    @Override
    protected PreparedStatement recuperarPreparedStatementInsertAud() {
        return getPreparedStatement(StatementsBanco.INSERT_AUD);
    }

    @Override
    protected void preencherCamposInsertAud(RevisaoAuditoria revisaoAuditoria, Banco registro, PreparedStatement ps, Long tipo) throws SQLException {
        setLongObrigatorio(ps, 1, registro.getId());
        setLongObrigatorio(ps, 2, revisaoAuditoria.getId());
        setLongObrigatorio(ps, 3, tipo);
        setStringObrigatorio(ps, 4, registro.getDescricao());
        setStringObrigatorio(ps, 5, registro.getNumeroBanco());
        setStringOpcional(ps, 6, registro.getDigitoVerificador());
        setStringOpcional(ps, 7, registro.getHomePage());
        setStringOpcional(ps, 8, registro.getNumeroContrato());
    }

    @Override
    protected PreparedStatement recuperarPreparedStatementPreCarga() {
        return getPreparedStatement(StatementsBanco.SELECT_ALL);
    }

    @Override
    protected Banco preencherRegistro(ResultSet rs) throws SQLException {
        final Banco banco = new Banco();
        banco.setId(rs.getLong("id"));
        banco.setNumeroBanco(rs.getString("numeroBanco"));
        banco.setDescricao(rs.getString("descricao"));
        banco.setDigitoVerificador(rs.getString("digitoVerificador"));
        banco.setHomePage(rs.getString("homePage"));
        banco.setNumeroContrato(rs.getString("numeroContrato"));
        return banco;
    }

    @Override
    protected Banco preencherRegistroSimples(ResultSet rs) throws SQLException {
        return null;
    }

    public Banco buscarPorDescricao(String descricao) {
        if (descricao == null) {
            logger.trace("Impossível Buscar por Descrição com Descrição Null");
            return null;
        }
        if (bancosPorDescricao == null) {
            carregarBancosPorDescricao();
        }
        return bancosPorDescricao.get(descricao.toLowerCase().trim());
    }

    private void carregarBancosPorDescricao() {
        bancosPorDescricao = Maps.newHashMap();
        try (ResultSet rs = recuperarPreparedStatementPreCarga().executeQuery()) {
            while (rs.next()) {
                Banco banco = preencherRegistroSimples(rs);
                if (banco == null) {
                    banco = preencherRegistro(rs);
                }
                bancosPorDescricao.put(banco.getDescricao().toLowerCase().trim(), banco);
            }
        } catch (SQLException ex) {
            getLogger().debug("Erro no método carregarBancosPorDescricao() " + ex.getMessage());
            throw new WebPublicoJDBCException("Erro carregando bancos por descrição", ex);
        }
    }
}
