package br.com.webpublico.repositorios.jdbc;

import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.repositorios.jdbc.statement.StatementsUsuarioWeb;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RepositorioJDBC
public class UsuarioWebJDBCRepository extends WebPublicoChaveNegocioJDBCRepository<UsuarioWeb> {


    @Autowired
    public UsuarioWebJDBCRepository(Connection conn,
                                    RevisaoAuditoriaJDBCRepository revisaoAuditoriaJDBCRepository) {
        super(conn, revisaoAuditoriaJDBCRepository);
    }

    @Override
    protected PreparedStatement recuperarPreparedStatementInsert() {
        return getPreparedStatement(StatementsUsuarioWeb.INSERT);
    }

    @Override
    protected PreparedStatement recuperarPreparedStatementPreCarga() {
        return getPreparedStatement(StatementsUsuarioWeb.SELECT_ALL);
    }

    @Override
    protected UsuarioWeb preencherRegistro(ResultSet rs) throws SQLException {
//        INSERT(" INSERT INTO USUARIOWEB (ID, LOGIN, PASSWORD_HASH, EMAIL, ACTIVATED, " +
//            " LANG_KEY, ACTIVATION_KEY, RESET_KEY, CREATED_BY, CREATED_DATE, RESET_DATE, LAST_MODIFIED_BY, LAST_MODIFIED_DATE, " +
//            " DICASENHA, IMAGE_URL, TENTATIVALOGIN, USERNFSECADASTROECONOMICO_ID, PESSOA_ID, ULTIMOACESSO) " +
//            " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "),
        UsuarioWeb usuarioWeb = new UsuarioWeb();
        usuarioWeb.setId(getLong(rs, "ID"));
        usuarioWeb.setLogin(getString(rs, "LOGIN"));
        usuarioWeb.setPassword(getString(rs, "PASSWORD_HASH"));
        usuarioWeb.setEmail(getString(rs, "EMAIL"));
        usuarioWeb.setActivated(getBoolean(rs, "ACTIVATED"));
        usuarioWeb.setActivationKey(getString(rs, "ACTIVATION_KEY"));
        usuarioWeb.setResetKey(getString(rs, "RESET_KEY"));
        usuarioWeb.setCreatedBy(getString(rs, "CREATED_BY"));
        usuarioWeb.setCreatedDate(getDate(rs, "CREATED_DATE"));
        usuarioWeb.setResetDate(getDate(rs, "RESET_DATE"));
        usuarioWeb.setLastModifiedBy(getString(rs, "LAST_MODIFIED_BY"));
        usuarioWeb.setLastModifiedDate(getDate(rs, "LAST_MODIFIED_DATE"));
        usuarioWeb.setDicaSenha(getString(rs, "DICASENHA"));
        usuarioWeb.setImageUrl(getString(rs, "IMAGE_URL"));
        usuarioWeb.setTentativaLogin(getInt(rs, "TENTATIVALOGIN"));
        return usuarioWeb;
    }

    @Override
    protected void preencherCamposInsert(UsuarioWeb registro, PreparedStatement ps) throws SQLException {

    }
}
