package br.com.webpublico.customchangeset;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * @author Daniel
 * @since 26/08/2015 15:15
 */
public abstract class AbstractCustomChangeSet implements CustomTaskChange {

    /**
     * @return o Logger que será utilizado para a exibição de mensagens
     */
    protected Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Método executado antes do método executarPreProcessamento, para inicialização dos repositórios necessários
     *
     * @param conn - A Connection JDBC utilizada pelos repositórios
     */
    protected abstract void inicializarRepositorios(Connection conn);

    /**
     * O método principal, onde a lógica do Load Inicial propriamente dita será implementada
     */
    protected abstract void processar() throws SQLException, ParseException, IOException;

    /**
     * Método opcional executado antes do processamento, para preparos, como por exemplo, a pré-carga de entidades
     * associadas
     */
    protected void executarPreProcessamento() {
    }

    /**
     * Método opcional executado ao fim do processamento, como por exemplo para conferência ou encerramento de recursos
     * utilizados
     */
    protected void executarPosProcessamento() {

    }

    /**
     * Método executado pelo Liquibase, onde os repositórios são inicializados e
     *
     * @param database O Database vindo do Liquibase contendo a conexão com o banco
     * @throws CustomChangeException
     */
    @Override
    public final void execute(Database database) throws CustomChangeException {
        try {
            JdbcConnection databaseConnection = (JdbcConnection) database.getConnection();
            Connection conn = databaseConnection.getUnderlyingConnection();
            inicializarRepositorios(conn);
            executarPreProcessamento();
            processar();
            executarPosProcessamento();
            conn.commit();
        } catch (Exception ex) {
            throw new CustomChangeException(ex);
        }
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {
    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }

    protected String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                return null;
            case Cell.CELL_TYPE_NUMERIC:
                return String.format("%d", (long) cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue().trim();
            case Cell.CELL_TYPE_FORMULA:
                return cell.getStringCellValue().trim();
        }
        return null;
    }
}
