package br.com.webpublico.negocios;

import br.com.webpublico.controle.CancelamentoIsencaoCadastroImobiliarioControlador;
import br.com.webpublico.entidades.CancelamentoIsencaoCadastroImobiliario;
import br.com.webpublico.entidades.IsencaoCadastroImobiliario;
import br.com.webpublico.negocios.tributario.dao.JdbcIsencaoCadastroImobiliarioDAO;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 10/04/14
 * Time: 18:17
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class CancelamentoIsencaoCadastroImobiliarioFacade extends AbstractFacade<CancelamentoIsencaoCadastroImobiliario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public CancelamentoIsencaoCadastroImobiliarioFacade() {
        super(CancelamentoIsencaoCadastroImobiliario.class);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(CancelamentoIsencaoCadastroImobiliario entity) {
        salvar(entity);
    }

    @Override
    public void salvar(CancelamentoIsencaoCadastroImobiliario entity) {
        entity = em.merge(entity);
        IsencaoCadastroImobiliario isencao = entity.getIsencao();
        isencao.setSituacao(IsencaoCadastroImobiliario.Situacao.CANCELADO);
        isencao.setFinalVigencia(entity.getDataOperacao());
        em.merge(isencao);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public int contarIsencoesPorFiltro(CancelamentoIsencaoCadastroImobiliarioControlador.FiltroCancelamentoIsencao filtro) {
        String sql = "select count(*) from IsencaoCadastroImobiliario isen " +
            " inner join ProcessoIsencaoIPTU processo on processo.id = isen.processoIsencaoIPTU_id " +
            " where ";
        sql = montarCondicaoQueryIsencaoConformeFiltro(filtro, sql);
        Query q = em.createNativeQuery(sql);
        definirParametrosQueryIsencaoConformeFiltro(filtro, q);
        return ((BigDecimal) q.getResultList().get(0)).intValue();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<IsencaoCadastroImobiliario> buscarIsencoesPorFiltro(CancelamentoIsencaoCadastroImobiliarioControlador.FiltroCancelamentoIsencao filtro, int maxRegistros, int pagina) {
        String sql = "select * from IsencaoCadastroImobiliario isen " +
            " inner join ProcessoIsencaoIPTU processo on processo.id = isen.processoIsencaoIPTU_id " +
            " where ";

        sql = montarCondicaoQueryIsencaoConformeFiltro(filtro, sql);
        sql = StringUtils.removeEnd(sql.trim(), "where");
        Query q = em.createNativeQuery(sql, IsencaoCadastroImobiliario.class);
        definirParametrosQueryIsencaoConformeFiltro(filtro, q);
        q.setFirstResult((maxRegistros * (pagina - 1)));
        q.setMaxResults(maxRegistros);
        return q.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<IsencaoCadastroImobiliario> buscarIsencoesComIdVigenciaAndSituacaoPorFiltro(CancelamentoIsencaoCadastroImobiliarioControlador.FiltroCancelamentoIsencao filtro, int maxRegistros, int pagina) {
        String sql = "select isen.id, isen.finalVigencia, isen.situacao isen " +
            " from IsencaoCadastroImobiliario isen " +
            " inner join ProcessoIsencaoIPTU processo on processo.id = isen.processoIsencaoIPTU_id " +
            " where ";
        sql = montarCondicaoQueryIsencaoConformeFiltro(filtro, sql);
        Query q = em.createNativeQuery(sql);
        definirParametrosQueryIsencaoConformeFiltro(filtro, q);
        q.setFirstResult((maxRegistros * (pagina - 1)));
        q.setMaxResults(maxRegistros);
        List<IsencaoCadastroImobiliario> retorno = Lists.newArrayList();
        List<Object[]> objetos = q.getResultList();

        for (Object[] objeto : objetos) {
            IsencaoCadastroImobiliario isencao = new IsencaoCadastroImobiliario();
            isencao.setId((BigDecimal) objeto[0]);
            isencao.setFinalVigencia((Date) objeto[1]);
            isencao.setSituacao(IsencaoCadastroImobiliario.Situacao.valueOf((String) objeto[2]));
            retorno.add(isencao);
        }

        return retorno;
    }

    private void definirParametrosQueryIsencaoConformeFiltro(CancelamentoIsencaoCadastroImobiliarioControlador.FiltroCancelamentoIsencao filtro, Query q) {
        if (filtro.getCadastroImobiliario() != null) {
            q.setParameter("cadastroEconomico_id", filtro.getCadastroImobiliario().getId());
        }
        if (filtro.getNumeroProtocolo() != null && !filtro.getNumeroProtocolo().trim().isEmpty()) {
            q.setParameter("numeroProtocolo", filtro.getNumeroProtocolo());
        }
        if (filtro.getNumeroProcesso() != null && filtro.getNumeroProcesso() > 0) {
            q.setParameter("numeroProcesso", filtro.getNumeroProcesso());
        }
        if (filtro.getExercicioProcesso() != null) {
            q.setParameter("exercicioProcesso_id", filtro.getExercicioProcesso().getId());
        }
    }

    private String montarCondicaoQueryIsencaoConformeFiltro(CancelamentoIsencaoCadastroImobiliarioControlador.FiltroCancelamentoIsencao filtro, String sql) {
        String juncao = "";
        if (filtro.getCadastroImobiliario() != null) {
            sql += " isen.cadastroImobiliario_id = :cadastroEconomico_id ";
            juncao = " and ";
        }
        if (filtro.getExercicioProcesso() != null) {
            sql += juncao + " processo.exercicioProcesso_id = :exercicioProcesso_id ";
            juncao = " and ";
        }
        if (filtro.getNumeroProcesso() != null && filtro.getNumeroProcesso() > 0) {
            sql += juncao + " processo.numero = :numeroProcesso ";
            juncao = " and ";
        }
        if (filtro.getNumeroProtocolo() != null && !filtro.getNumeroProtocolo().trim().isEmpty()) {
            sql += juncao + " processo.numeroProtocolo = :numeroProtocolo ";
        }
        return sql;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @Asynchronous
    public Future<AssistenteBarraProgresso> cancelarTodasIsencoes(AssistenteBarraProgresso assistente, CancelamentoIsencaoCadastroImobiliarioControlador.FiltroCancelamentoIsencao filtro, String motivo, int maxRegistros, int totalPaginas) {
        int pagina = 1;
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        JdbcIsencaoCadastroImobiliarioDAO dao = (JdbcIsencaoCadastroImobiliarioDAO) ap.getBean("jdbcIsencaoCadastroImobiliarioDAO");

        while (pagina <= totalPaginas) {
            for (IsencaoCadastroImobiliario isencao : buscarIsencoesComIdVigenciaAndSituacaoPorFiltro(filtro, maxRegistros, pagina)) {
                if (!isencao.getSituacao().equals(IsencaoCadastroImobiliario.Situacao.CANCELADO)) {
                    CancelamentoIsencaoCadastroImobiliario cancelamento = new CancelamentoIsencaoCadastroImobiliario();
                    cancelamento.setDataOperacao(new Date());
                    cancelamento.setMotivo(motivo);
                    cancelamento.setUsuarioSistema(assistente.getUsuarioSistema());
                    cancelamento.setIsencao(isencao);
                    dao.inserirCancelamentoIsencaoIPTU(cancelamento);
                }
                assistente.conta();
            }

            pagina += 1;
        }
        assistente.setExecutando(false);
        return new AsyncResult<>(assistente);
    }
}
