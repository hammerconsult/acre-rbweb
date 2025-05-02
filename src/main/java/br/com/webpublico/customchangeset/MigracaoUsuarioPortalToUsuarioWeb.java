package br.com.webpublico.customchangeset;

import br.com.webpublico.util.Seguranca;
import br.com.webpublico.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Skywalker on 19/10/2015.
 */
public class MigracaoUsuarioPortalToUsuarioWeb extends AbstractCustomChangeSet {

    private Connection conn;

    @Override
    protected Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    @Override
    protected void inicializarRepositorios(Connection conn) {
        this.conn = conn;
    }

    @Override
    protected void processar() throws SQLException {
        PreparedStatement psInsertUsuarioWeb = this.conn.prepareStatement(" INSERT INTO USUARIOWEB (ID, ACTIVATED, " +
            " CREATED_BY, CREATED_DATE, PESSOA_ID, PASSWORD_HASH, LOGIN, EMAIL, ULTIMOACESSO) VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 1, 'MIGRACAO', CURRENT_DATE, ?, ?, ?, ?, ?) ");

        PreparedStatement psUsuariosPortalWeb = this.conn.prepareStatement("  SELECT U.ID, U.PESSOA_ID, U.SENHA, U.EMAIL, U.ULTIMOACESSO, " +
            " REGEXP_REPLACE(COALESCE(PF.CPF, PJ.CNPJ), '[^0-9]', '') AS LOGIN " +
            "   FROM USUARIOPORTALWEB U " +
            " LEFT JOIN PESSOAFISICA PF ON PF.ID = U.PESSOA_ID " +
            " LEFT JOIN PESSOAJURIDICA PJ ON PJ.ID = U.PESSOA_ID " +
            "WHERE NOT EXISTS (SELECT 1 FROM USUARIOWEB UW WHERE UW.LOGIN = REGEXP_REPLACE(COALESCE(PF.CPF, PJ.CNPJ), '[^0-9]', '')) ");


        AtomicInteger count = new AtomicInteger(0);
        ResultSet resultSet = psUsuariosPortalWeb.executeQuery();
        while (resultSet.next()) {
            try {
                psInsertUsuarioWeb.clearParameters();
                psInsertUsuarioWeb.setLong(1, resultSet.getLong("PESSOA_ID"));
                psInsertUsuarioWeb.setString(2, Seguranca.bCryptPasswordEncoder.encode(resultSet.getString("SENHA")));
                psInsertUsuarioWeb.setString(3, StringUtil.retornaApenasNumeros(resultSet.getString("LOGIN")));
                psInsertUsuarioWeb.setString(4, resultSet.getString("EMAIL"));
                psInsertUsuarioWeb.setDate(5, resultSet.getDate("ULTIMOACESSO"));
                psInsertUsuarioWeb.execute();
                count.addAndGet(1);
            } catch (Exception e) {
                getLogger().debug("NAO INSERIU USUARIOPORTALWEB ID: " + resultSet.getLong("ID") + " ERRO: " + e.getMessage());
            } finally {
                if ((count.get() % 500 == 0)) {
                    getLogger().debug("Registrou " + count.get() + ". ");
                }
            }
        }
    }
}
