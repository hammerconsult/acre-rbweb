package br.com.webpublico.customchangeset;

import com.google.common.collect.Maps;
import liquibase.resource.ResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Daniel
 * @since 15/09/2015 11:16
 */
public class AjustaCodigoIBGECidade extends AbstractCustomChangeSet {

    private ResourceAccessor resourceAccessor;
    private String arquivo;
    private Connection conn;
    private Map<String, Long> estados = Maps.newHashMap();

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
        try {
            getLogger().debug("Inserindo Cidades");
            PreparedStatement selectUfs = this.conn.prepareStatement("SELECT DISTINCT ID, UF FROM UF");
            PreparedStatement psInsertCidade = this.conn.prepareStatement(" INSERT INTO CIDADE (ID, NOME, " +
                " UF_ID, CODIGO, CODIGOIBGE) VALUES (HIBERNATE_SEQUENCE.NEXTVAL, ?, ?, ?, ?) ");
            PreparedStatement psUpdateCidade = this.conn.prepareStatement(" UPDATE CIDADE SET CODIGOIBGE = ? " +
                " WHERE ID = ?");

            getLogger().debug("recuperando os estados");

            ResultSet resultSetuFS = selectUfs.executeQuery();
            while (resultSetuFS.next()) {
                try {
                    estados.put(resultSetuFS.getString("UF"), resultSetuFS.getLong("ID"));
                } catch (Exception e) {
                    getLogger().debug("NÃO RECUPEROU OS ESTADOS" + e.getMessage());
                }
            }

            getLogger().debug("estados " + estados.size());
            getLogger().debug("arquivo " + arquivo.length());


            InputStream is = resourceAccessor.getResourceAsStream(arquivo);
            BufferedReader in = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String linha;

            getLogger().debug("Inserindo Cidades");
            PreparedStatement selectCidade = this.conn.prepareStatement("SELECT DISTINCT ID FROM CIDADE" +
                " WHERE REGEXP_REPLACE(nome, '[^0-9A-Za-z]', '') = REGEXP_REPLACE(?, '[^0-9A-Za-z]', '') AND UF_ID = ?");

            while ((linha = in.readLine()) != null && !linha.isEmpty()) {
                try {
                    getLogger().debug("Cidade: {}", linha);
                    String[] dados = linha.split(",");
                    final String nomeCidade = dados[1];
                    if (nomeCidade.isEmpty()) {
                        continue;
                    }
                    final String siglaUF = dados[2];
                    if (siglaUF.isEmpty() || !estados.containsKey(siglaUF)) {
                        continue;
                    }
                    final String codigoIBGE = dados[0];
                    if (codigoIBGE.isEmpty()) {
                        continue;
                    }
                    selectCidade.clearParameters();
                    selectCidade.setString(1, nomeCidade);
                    selectCidade.setLong(2, estados.get(siglaUF));
                    ResultSet resultSetCidade = selectCidade.executeQuery();
                    if (resultSetCidade.next()) {
                        psUpdateCidade.clearParameters();
                        psUpdateCidade.setInt(1, Integer.valueOf(codigoIBGE));
                        psUpdateCidade.setLong(2, resultSetCidade.getLong(1));
                        psUpdateCidade.execute();
                    } else {
                        psInsertCidade.clearParameters();
                        psInsertCidade.setString(1, nomeCidade);
                        psInsertCidade.setLong(2, estados.get(siglaUF));
                        psInsertCidade.setInt(3, Integer.valueOf(codigoIBGE));
                        psInsertCidade.setInt(4, Integer.valueOf(codigoIBGE));
                        psInsertCidade.execute();
                    }
                } catch (Exception ex) {
                    getLogger().debug("NAO INSERIU A CIDADE ID: " + ex.getMessage());
                }
            }
            getLogger().debug("Inseriu todas as Cidades");
        } catch (Exception ex) {
            getLogger().debug("NAO INSERIU A CIDADE ID: " + ex.getMessage());
        }
    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {
        this.resourceAccessor = resourceAccessor;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }
}
