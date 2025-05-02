package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.ExportPDFMonitor;
import br.com.webpublico.entidadesauxiliares.VORelatorioGenerico;
import br.com.webpublico.negocios.tributario.dao.JdbcVORelatorioGenericoDAO;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.javax0.intern.InternPool;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.context.ContextLoader;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class VORelatorioGenericoFacade {

    protected static final Logger logger = LoggerFactory.getLogger(VORelatorioGenericoFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private InternPool internPool;

    public VORelatorioGenericoFacade() {
        internPool = new InternPool();
    }

    public List<VORelatorioGenerico> gerarVORelatorioGenerico(String sql, Map<String, Object> parametros) {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        JdbcVORelatorioGenericoDAO dao = (JdbcVORelatorioGenericoDAO) ap.getBean("voRelatorioGenericoDAO");
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dao.getJdbcTemplate());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            if (sql.contains(":" + entry.getKey())) {
                parameters.addValue(entry.getKey(), entry.getValue());
            }
        }
        List<VORelatorioGenerico> voRelatorioGenerico = namedParameterJdbcTemplate.query(sql, parameters, new ResultSetExtractor<List<VORelatorioGenerico>>() {
            @Override
            public List<VORelatorioGenerico> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                List<VORelatorioGenerico> retorno = Lists.newArrayList();
                while (resultSet.next()) {
                    retorno.add(new VORelatorioGenerico(resultSet, internPool));
                }
                return retorno;
            }
        });
        return voRelatorioGenerico;
    }


    public ByteArrayOutputStream gerarBytes(String caminho, String jasper, UnidadeOrganizacional unidadeOrganizacional, HashMap parametros, List<VORelatorioGenerico> resultado, ExportPDFMonitor monitor, boolean dividirGeracaoDePDF) throws IOException, JRException {
        AbstractReport abstractReport = AbstractReport.getAbstractReport();
        List<Future<JasperPrint>> futureJaspers = Lists.newArrayList();
        int subListSize = resultado.size() < 100 || !dividirGeracaoDePDF ? resultado.size() : resultado.size() / 4;
        List<List<VORelatorioGenerico>> particoes = Util.partitionList(resultado, subListSize);
        if (monitor != null) {
            monitor.setEstado(ExportPDFMonitor.Estado.GERANDO_REGISTROS);
            monitor.setTotalRegistros(resultado.size());
        }
        for (List<VORelatorioGenerico> parte : particoes) {
            futureJaspers.add(getNewRelatorioFacade().gerarJasper(caminho, jasper, unidadeOrganizacional, parametros, parte));
        }
        List<JasperPrint> jasperPrints = pegarResultadosFutures(futureJaspers);
        return abstractReport.exportarJaspersParaPDF(monitor, jasperPrints.toArray(new JasperPrint[0]));
    }


    @Asynchronous
    @TransactionTimeout(value = 10, unit = TimeUnit.HOURS)
    public Future<JasperPrint> gerarJasper(String caminho, String jasper, UnidadeOrganizacional unidadeOrganizacional, HashMap parametros, List<VORelatorioGenerico> parte) {
        return new AsyncResult<>(gerarBytesRelatorio(caminho, jasper, unidadeOrganizacional, parametros, parte));
    }

    public JasperPrint gerarBytesRelatorio(String caminho, String jasper, UnidadeOrganizacional unidadeOrganizacional, HashMap parametros, List<VORelatorioGenerico> parte) {
        AbstractReport report = AbstractReport.getAbstractReport();
        try {
            return report.gerarBytesDeRelatorioComDadosEmCollectionView(caminho, jasper, parametros, new JRBeanCollectionDataSource(parte), unidadeOrganizacional);
        } catch (JRException e) {
            logger.error("gerarBytesRelatorio", e);
        } catch (IOException e) {
            logger.error("gerarBytesRelatorio", e);
        }
        return null;
    }

    public <T> List<T> pegarResultadosFutures(Future<T>... futures) {
        return pegarResultadosFutures(Arrays.asList(futures));
    }

    public <T> List<T> pegarResultadosFutures(List<Future<T>> futures) {
        aguardarFuturesSeremConcluidas(futures);
        List<T> resultados = Lists.newArrayList();
        for (Future<T> future : futures) {
            try {
                resultados.add(future.get());
            } catch (InterruptedException e) {
                logger.error("PegarResultadosFutures", e);
            } catch (ExecutionException e) {
                logger.error("PegarResultadosFutures", e);
            }
        }
        return resultados;
    }

    public <T> void aguardarFuturesSeremConcluidas(List<Future<T>> futures) {
        while (true) {
            boolean todasCompletas = true;
            for (Future future : futures) {
                if (!future.isDone()) {
                    todasCompletas = false;
                    break;
                }
            }
            if (todasCompletas) {
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.error("aguardarFuturesSeremConcluidas", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public <T extends VORelatorioGenericoFacade> T getNewRelatorioFacade() {
        try {
            return (T) new InitialContext().lookup("java:module/" + getClass().getSimpleName());
        } catch (NamingException e) {
            logger.error("Falha ao recuperar facade", e);
        }
        return null;
    }

    public EntityManager getEm() {
        return em;
    }
}
