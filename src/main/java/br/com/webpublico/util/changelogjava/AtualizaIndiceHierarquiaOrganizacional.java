/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.changelogjava;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

import java.sql.*;

/**
 * @author reidocrime
 */
public class AtualizaIndiceHierarquiaOrganizacional implements CustomTaskChange {

    @Override
    public String getConfirmationMessage() {
        return "Indices e codigos da Hierarquia recalcuculados com sucesso";
    }

    @Override
    public void setUp() throws SetupException {
    }

    @Override
    public void setFileOpener(ResourceAccessor ra) {
    }

    @Override
    public ValidationErrors validate(Database dtbs) {
        return null;
    }

    @Override
    public void execute(Database dtbs) throws CustomChangeException {
        try {
            //System.out.println("Esta operação pode demorar alguns minutos");
            Connection conn = ((JdbcConnection) dtbs.getConnection()).getUnderlyingConnection();
            Statement stm = conn.createStatement();

            executaAlteracoes(stm);

        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao Atualizar Indices da Hierarquia Organizacional " + ex.getMessage(), ex);
        }

    }

    private boolean executaAlteracoes(Statement stm) {
        boolean toReturn = false;
        try {
            ResultSet rs = stm.executeQuery("SELECT * FROM hierarquiaorganizacional ho WHERE ho.superior_id IS null");
            toReturn = rs.next();
            if (toReturn) {
                stm.executeUpdate("UPDATE hierarquiaorganizacional SET indicedono = 0 WHERE SUPERIOR_ID IS null");
                alterarDados(stm);
            }
            return toReturn;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao Atualizar Indices da Hierarquia Organizacional " + ex.getMessage(), ex);
        }
    }

    private void alterarDados(Statement stm) throws SQLException {
        ResultSet rs = stm.executeQuery("SELECT ho.id, ho.codigo,ho.indicedono FROM HIERARQUIAORGANIZACIONAL ho ORDER BY ho.CODIGO");
        PreparedStatement p = stm.getConnection().prepareStatement("UPDATE HIERARQUIAORGANIZACIONAL SET codigono =? WHERE id =? ");
        while (rs.next()) {
            String codigo = rs.getString(2);
            int id = rs.getInt(1);
            int indiceno = rs.getInt(3);
            String cd = geraCodigoNo(codigo, indiceno);
            p.setString(1, cd);
            p.setLong(2, id);
            p.addBatch();
        }
        p.executeBatch();
    }

    private String geraCodigoNo(String cod, int nivel) {
        String codigo[] = cod.split("\\.");

        if (nivel != 0) {
            return codigo[nivel - 1];
        } else {
            return codigo[nivel];
        }

    }
}
