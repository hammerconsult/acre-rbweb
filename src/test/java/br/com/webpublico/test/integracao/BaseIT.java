package br.com.webpublico.test.integracao;

import java.util.NoSuchElementException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import static org.junit.Assert.*;

/**
 * Classe base para testes.<br/>
 *
 * Fornece métodos utilitários que facilitam a realização de testes com o banco de dados,
 * facilitando a utilização da biblioteca <code>DBUnit</code>.<br/>
 *
 * <p>Um exemplo de utilização é a classe: {@link TesteBairro}.</p>
 *
 * @author Gécen Dacome De Marchi.
 */
public class BaseIT extends DataBaseIT {

    public static final String URL_BASE = "http://localhost:8080/webpublico";
    protected static WebDriver driver;

    public static void login() {
        driver.get(URL_BASE);
        driver.findElement(By.name("j_username")).clear();
        driver.findElement(By.name("j_username")).sendKeys("admin");
        driver.findElement(By.name("j_password")).clear();
        driver.findElement(By.name("j_password")).sendKeys("senha10");
        driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
    }

    public void getWithUrlBase(String relativeUrl) {
        driver.get(URL_BASE + relativeUrl);
    }

    @BeforeClass
    public static void beforeTests() throws Exception {
        initDBUnit();
        initSelenium();
        login();
    }

    /**
     * Inicia e configura o seleniun
     *
     * @throws Exception
     */
    protected static void initSelenium() throws Exception {
        driver = new HtmlUnitDriver(true);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @AfterClass
    public void tearDown() throws Exception {
        tearDownSeleniun();
        tearDownDBUnit();
    }

    /**
     * Encerra o driver do seleniun
     *
     * @throws Exception
     */
    public static void tearDownSeleniun() throws Exception {
        driver.quit();
    }

    /**
     * Executa um java script, normalmente utilizado para configurar campos
     * de auto complemte.<br>
     *
     * Exemplo:<br>
     *
     * executeJavaScript("document.forms['Formulario'].elements['Formulario:cidade_hinput'].value = '999997'");
     *
     * <br>No exeplo acima está sendo configura a cidade para o cadastro de bairros.
     * @param script
     */
    protected void executeJavaScript(String script) {
        ((JavascriptExecutor) driver).executeScript(script);
    }

    /**
     * Verifica se o elemento está presente
     * @param by Utilizado para localizar elementos
     *
     * @return true se encontra o elemento
     */
    protected boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Espera a presença do elemento 60 segundos
     *
     * @param by
     * @throws InterruptedException
     */
    protected void esperarPresencaDoElemento(By by) throws InterruptedException {
        for (int second = 0;; second++) {
            if (second >= 60) {
                fail("timeout");
            }
            try {
                if (isElementPresent(by)) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
    }
}
