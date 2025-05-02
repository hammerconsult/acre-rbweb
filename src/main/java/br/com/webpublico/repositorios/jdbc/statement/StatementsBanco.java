package br.com.webpublico.repositorios.jdbc.statement;

/**
 * Created by Skywalker on 15/10/2015.
 */
public enum StatementsBanco implements JDBCStatement {
    INSERT("INSERT INTO Banco (ID, descricao, numeroBanco, digitoVerificador, homePage, numeroContrato) values (?,?,?,?,?,?)"),
    SELECT_ALL("SELECT ID, descricao, numeroBanco, digitoVerificador, homePage, numeroContrato from Banco"),
    SELECT_LOWER_DESCRICAO("SELECT ID, descricao, numeroBanco, digitoVerificador, homePage, numeroContrato FROM Banco WHERE lower(descricao) = ?"),
    INSERT_AUD("INSERT INTO BANCO_AUD (ID, rev, revtype, descricao, numeroBanco, digitoVerificador, homePage, numeroContrato) values (?,?,?,?,?,?,?,?)");

    String statement;

    StatementsBanco(String statement) {
        this.statement = statement;
    }

    @Override
    public String getStatement() {
        return statement;
    }
}
