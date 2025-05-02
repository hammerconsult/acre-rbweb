package br.com.webpublico.repositorios.jdbc.statement;

/**
 * Created by Skywalker on 15/10/2015.
 */
public enum StatementsUsuarioWeb implements JDBCStatement {
    INSERT(" INSERT INTO USUARIOWEB (ID, LOGIN, PASSWORD_HASH, EMAIL, ACTIVATED, " +
        " LANG_KEY, ACTIVATION_KEY, RESET_KEY, CREATED_BY, CREATED_DATE, RESET_DATE, LAST_MODIFIED_BY, LAST_MODIFIED_DATE, " +
        " DICASENHA, IMAGE_URL, TENTATIVALOGIN, USERNFSECADASTROECONOMICO_ID, PESSOA_ID, ULTIMOACESSO) " +
        " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "),
    SELECT_ALL(" SELECT ID, LOGIN, PASSWORD_HASH, EMAIL, ACTIVATED, LANG_KEY, ACTIVATION_KEY, RESET_KEY, " +
        " CREATED_BY, CREATED_DATE, RESET_DATE, LAST_MODIFIED_BY, LAST_MODIFIED_DATE, DICASENHA, IMAGE_URL, " +
        " TENTATIVALOGIN, USERNFSECADASTROECONOMICO_ID, PESSOA_ID, ULTIMOACESSO " +
        " FROM USUARIOWEB "),
    INSERT_AUD(" INSERT INTO USUARIOWEB (ID, REV, REVTYPE, LOGIN, PASSWORD_HASH, EMAIL, ACTIVATED, " +
        " LANG_KEY, ACTIVATION_KEY, RESET_KEY, CREATED_BY, CREATED_DATE, RESET_DATE, LAST_MODIFIED_BY, LAST_MODIFIED_DATE, " +
        " DICASENHA, IMAGE_URL, TENTATIVALOGIN, USERNFSECADASTROECONOMICO_ID, PESSOA_ID, ULTIMOACESSO) " +
        " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

    String statement;

    StatementsUsuarioWeb(String statement) {
        this.statement = statement;
    }

    @Override
    public String getStatement() {
        return statement;
    }
}
