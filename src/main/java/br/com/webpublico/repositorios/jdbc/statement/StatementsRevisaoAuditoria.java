package br.com.webpublico.repositorios.jdbc.statement;

/**
 * Created by Wellington Abdo on 14/02/2017.
 */
public enum StatementsRevisaoAuditoria implements JDBCStatement {
    INSERT(" INSERT INTO REVISAOAUDITORIA (ID, DATAHORA, IP, USUARIO) VALUES (?, ?, ?, ?) "),
    SELECT_ALL(" SELECT ID, DATAHORA, IP, USUARIO FROM REVISAOAUDITORIA ");

    private String statements;

    StatementsRevisaoAuditoria(String statements) {
        this.statements = statements;
    }

    @Override
    public String getStatement() {
        return statements;
    }
}
