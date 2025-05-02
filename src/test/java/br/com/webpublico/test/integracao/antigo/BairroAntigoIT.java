package br.com.webpublico.test.integracao.antigo;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public class BairroAntigoIT {

    private WebDriver driver;
    private String baseUrl = "";
    private StringBuffer verificationErrors = new StringBuffer();

    private IDatabaseTester databaseTester;
    public IDataSet dataSet;
    public IDatabaseConnection connection;


    @Before
    public void setUp() throws Exception {

        databaseTester = new JdbcDatabaseTester("oracle.jdbc.OracleDriver",
            "jdbc:oracle:thin:@localhost:1521:ORCL",
            "webpublico",
            "senha10",
            "WEBPUBLICO");

        connection = databaseTester.getConnection();


        //connection.getConfig().setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
        //connection.getConfig().setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);


        dataSet = null;
        dataSet = getDataSet();


        databaseTester.setSetUpOperation(DatabaseOperation.REFRESH);
        databaseTester.setTearDownOperation(DatabaseOperation.NONE);

        //databaseTester.setDataSet(dataSet);

        try {
            DatabaseOperation.INSERT.execute(connection, dataSet);
        } catch (Exception ex) {
            //System.out.println("Erro Insert "+ex);
            //assertEquals("1", "2");
            connection.close();
        }

        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

    }

    @Test
    public void testEBairro() throws Exception {
        driver.get("http://localhost:8080/webpublico/");
        driver.findElement(By.cssSelector("input[type=submit]")).click();
        driver.findElement(By.linkText("Cadastros Gerais")).click();
        driver.findElement(By.linkText("Bairro")).click();
        driver.switchTo().frame("corpo");
        driver.findElement(By.id("listaBairro:formTabelaGenerica:botaoNovo")).click();
        driver.findElement(By.id("Formulario:descricao")).click();
        driver.findElement(By.id("Formulario:descricao")).clear();
        driver.findElement(By.id("Formulario:descricao")).sendKeys("Bairro do André");
        ((JavascriptExecutor) driver).executeScript("document.forms['Formulario'].elements['Formulario:cidade_hinput'].value = '-10000000'");
        driver.findElement(By.id("Formulario:rodapeEditar:salvar")).click();
    }

    @After
    public void tearDown() throws Exception {
        databaseTester.onTearDown();
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected IDataSet getDataSet() throws Exception {
        //System.out.println("PATH" + System.getProperty("file.separator"));
        return new FlatXmlDataSetBuilder().build(this.getClass().getResourceAsStream("dataset" + System.getProperty("file.separator") + "dataSetCidade.xml"));
    }

    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.REFRESH;
    }

    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.NONE;
    }

    protected IDatabaseConnection getConnection()
        throws Exception {

        Class driverClass =
            Class.forName("oracle.jdbc.OracleDriver");

        Connection jdbcConnection =
            DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:ORCL", "webpublico", "senha10");

        return new DatabaseConnection(jdbcConnection);
    }
}
