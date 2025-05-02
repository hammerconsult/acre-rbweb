/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.test.integracao;

import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author andre
 */
public class DataBaseIT {

    private static final Logger logger = LoggerFactory.getLogger(DataBaseIT.class);

    protected static EntityManager em;
    protected static IDatabaseTester databaseTester;
    protected static IDatabaseConnection connection;
    protected static FlatXmlDataSetBuilder xmlDataSetBuilder;
    private static final String DEFAULT_PATH = "dataset" + System.getProperty("file.separator");
    private static EJBContainer ejbC;
    private static Context ctx;

    /**
     * Conecta com o banco de dados
     *
     * @throws Exception
     */
    protected static void initDBUnit() throws Exception {
        databaseTester = new JdbcDatabaseTester("oracle.jdbc.OracleDriver",
            "jdbc:oracle:thin:@localhost:1521:ORCL",
            "webpublico",
            "senha10",
            "WEBPUBLICOTESTE");//Banco para teste
        connection = databaseTester.getConnection();
        xmlDataSetBuilder = new FlatXmlDataSetBuilder();
    }

    public DataBaseIT() {
    }

    /**
     * EntityManager para recuperar objeto do banco
     *
     * @return
     */
    public static EntityManagerFactory createEntityManagerFactory() {
        EntityManagerFactory result = Persistence.createEntityManagerFactory("webPublicoTesteUnitario");
        return result;
    }

    /**
     * Fecha a conexão com o banco de dados
     *
     * @throws Exception
     */
    public static void tearDownDBUnit() throws Exception {
        databaseTester.onTearDown();
        connection.close();
    }

    /**
     * Executa uma operação do DBUnit, verifique as constantes em {@link DatabaseOperation}.
     * <p>
     * <p>Exeplo: </p>
     * <code>
     * executeDataSet("dataSet.xml", DatabaseOperation.REFRESH);
     * </code>
     * <br/>
     * O arquivo dataSet.xml deve estar em br.com.webpublico.test.integracao.dataset
     * <br/>
     *
     * @param fileName  Nome do arquivo
     * @param operation Uma operação que extenda {@link DatabaseOperation}.
     */
    protected void executeDataSet(String fileName, DatabaseOperation operation) {
        try {
            final FlatXmlDataSet xmlDataSet = createXmlDataSet(fileName);
            operation.execute(connection, xmlDataSet);
        } catch (DatabaseUnitException e) {
            throw new RuntimeException("Erro no DBUnit ao tentar executar a operação "
                + "utilizando o comando do DBUnit: " + operation.getClass().getSimpleName(), e);
        } catch (SQLException e) {
            throw new RuntimeException("Erro de SQL ao tentar executar a operação "
                + "utilizando o comando: " + operation.getClass().getSimpleName(), e);
        }
    }

    /**
     * Cria um objeto {@link FlatXmlDataSet} que contém informações do arquivo XML que é utilizado para "carregar" o banco de dados.
     *
     * @param fileName Nome do arquivo deve estar em br.com.webpublico.test.integracao.dataset
     * @return Um conjunto de dados em XML, {@link FlatXmlDataSet}.
     */
    protected FlatXmlDataSet createXmlDataSet(String fileName) {

        try {
            return xmlDataSetBuilder.build(this.getClass().getResourceAsStream(DEFAULT_PATH + fileName));
        } catch (DataSetException e) {
            throw new RuntimeException("Erro ao tentar criar dataset", e);
        }
    }

    /**
     * Insere no banco de dados utilizando um conjunto de dados no formato XML, esse arquivo deve ser padrão para o DBUnit.
     *
     * @param name Nome do arquivo que deve estar em br.com.webpublico.test.integracao.dataset
     */
    protected void insertDataSet(String name) {
        executeDataSet(name, DatabaseOperation.INSERT);
    }

    /**
     * Atualiza o banco de dados utilizando um conjunto de dados no formato XML, esse arquivo deve ser padrão para o DBUnit.
     * E estar em br.com.webpublico.test.integracao.dataset
     *
     * @param name Nome do arquivo que deve estar em br.com.webpublico.test.integracao.dataset
     */
    protected void refreshDataSet(String name) {
        executeDataSet(name, DatabaseOperation.REFRESH);
    }

    protected void refreshListDataSets(String... names) {
        for (String name : names) {
            executeDataSet(name, DatabaseOperation.REFRESH);
        }
    }

    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Limpa o banco de dados e insere as informações utilizando um conjunto de dados no formato XML, esse arquivo deve ser padrão para o DBUnit.
     *
     * @param name Nome do arquivo que deve estar em br.com.webpublico.test.integracao.dataset
     */
    protected void cleanInsertDataSet(String name) {
        executeDataSet(name, DatabaseOperation.CLEAN_INSERT);
    }

    /**
     * Altera o banco de dados utilizando um conjunto de dados no formato XML, esse arquivo deve ser padrão para o DBUnit.
     *
     * @param path Nome do arquivo que deve estar em br.com.webpublico.test.integracao.dataset
     */
    protected void updateDataSet(String path) {
        executeDataSet(path, DatabaseOperation.UPDATE);
    }

    /**
     * Exclui os registros do banco de dados utilizando um conjunto de dados no formato XML, esse arquivo deve ser padrão para o DBUnit.
     *
     * @param path Nome do arquivo que deve estar em br.com.webpublico.test.integracao.dataset
     */
    protected void deleteDataSet(String path) {
        executeDataSet(path, DatabaseOperation.DELETE);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        try {
            Map properties = new HashMap();
            //properties.put(EJBContainer.MODULES, new File("/home/gecen/NetBeansProjects/webpublico/build/web/WEB-INF/classes"));
            ejbC = EJBContainer.createEJBContainer();
            ctx = ejbC.getContext();
            em = createEntityManagerFactory().createEntityManager();
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }

    }

    //public <T> Resultado<T> invocar(String nomeFuncao, Class<T> clazz, Object... args) {
    public static <E> E findReference(Class<E> clazz) throws NamingException {
        return clazz.cast(ctx.lookup("java:global/classes/" + clazz.getSimpleName()));
    }
}
