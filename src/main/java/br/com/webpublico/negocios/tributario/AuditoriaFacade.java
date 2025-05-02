package br.com.webpublico.negocios.tributario;

import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.singletons.SingletonEntidade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by wellington on 18/04/2017.
 */
@Stateless
public class AuditoriaFacade extends AbstractFacade<RevisaoAuditoria> {
    private static final Logger logger = LoggerFactory.getLogger(AuditoriaFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AuditoriaFacade() {
        super(RevisaoAuditoria.class);
    }

    @EJB
    private AuditoriaBeanFacade auditoriaBeanFacade;

    @EJB
    private SingletonEntidade singletonEntidade;

    public String montarSQLInsertAuditoria(Long idRevisao, String tableName, String columns, boolean tabelaComID) {
        String insert = " insert into " + tableName + "_AUD ( " + columns + ", rev, revtype) " +
            " select " + columns + ", " + idRevisao + ", 0 from " + tableName + " tabela ";
        if (tabelaComID) {
            insert += " where not exists (select 1 from " + tableName + "_AUD tabela_aud where tabela_aud.id = tabela.id and tabela_aud.revtype = 0 and tabela_aud.rev = " + idRevisao + ") ";
        }
        return insert;
    }

    public List<String> buscarColunasDaTabela(String table_name) {
        List<String> colunas = Lists.newArrayList();
        String sql = " select column_name from user_tab_columns " +
            "        where table_name = :table_name ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("table_name", table_name);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            for (Object coluna : q.getResultList()) {
                colunas.add(coluna.toString());
            }
        }
        return colunas;
    }

    public List<String> buscarTabelasAuditadas() {
        List<String> tabelasAuditoria = Lists.newArrayList();
        String sql = " select ut.table_name " +
            "   from user_tables ut " +
            " where exists(select 1 from user_tables s_ut where s_ut.table_name = ut.table_name||'_AUD') ";
        Query q = em.createNativeQuery(sql);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            for (Object tabela : q.getResultList()) {
                tabelasAuditoria.add(tabela.toString());
                logger.debug("Tabela [{}]", tabela);
            }
        }
        return tabelasAuditoria;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future inserirAuditoriaParaAsTabelas(AssistenteBarraProgresso assistenteBarraProgresso, List<String> tabelas) {
        for (String tabela : tabelas) {
            inserirAuditoriaParaTabela(tabela);
            assistenteBarraProgresso.conta();
        }
        return new AsyncResult(null);
    }

    public void inserirAuditoriaParaTabela(String tabela) {
        List<String> colunas = buscarColunasDaTabela(tabela.toUpperCase().trim());
        List<String> colunasAud = buscarColunasDaTabela(tabela.toUpperCase().trim() + "_AUD");
        if (colunas.size() != (colunasAud.size() - 2)) {
            logger.debug("Falha Tabela [{}] Número de colunas incoerente", tabela);
        } else {
            String colunasSeparadasPorVirgula = StringUtils.join(colunas, ",");
            String sqlInsert = montarSQLInsertAuditoria(1l, tabela, colunasSeparadasPorVirgula, colunas.contains("ID"));
            logger.debug("Insert [{}]", sqlInsert);
            em.createNativeQuery(sqlInsert).executeUpdate();
            logger.debug("Sucesso Tabela [{}]", tabela);
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future updateRevisaoDasTabelasDeAuditoria(AssistenteBarraProgresso assistenteBarraProgresso, List<String> tabelas, Long idRevisaoOld, Long idRevisaoNew) {
        for (String tabela : tabelas) {
            auditoriaBeanFacade.executeInsertOrUpdate("update " + tabela + "_aud set rev = " + idRevisaoNew + " where rev =  " + idRevisaoOld);
            logger.debug("Sucesso Tabela [{}]", tabela);
            assistenteBarraProgresso.conta();
        }
        return new AsyncResult(null);
    }

    private void criarAuditoriaParaClasse(Class<?> classe) {
        String tabela = classe.getSimpleName();
        if (classe.isAnnotationPresent(Table.class)) {
            tabela = classe.getAnnotation(Table.class).name();
        }
        for (Class<?> subClasse : singletonEntidade.classesQueExtendem(classe)) {
            criarAuditoriaParaClasse(subClasse);
        }
        inserirAuditoriaParaTabela(tabela);
    }

    public void criarAuditoriaFromEntityNotFound(String exceptionMessage) {
        logger.debug("criarAuditoriaFromEntityNotFound");
        String entidade = exceptionMessage.substring(exceptionMessage.indexOf("find") + 5, exceptionMessage.indexOf(" with"));
        try {
            criarAuditoriaParaClasse(Class.forName(entidade));
        } catch (Exception ce) {
            logger.debug("Problema ao criar auditoria: " + ce);
        }
    }

    public RevisaoAuditoria buscarRevisaoAuditoriaAnterior(Date dataRevisao, Long id, String tabela) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select max(rev.id) from ")
            .append(tabela + "_aud aud ")
            .append("  inner join revisaoauditoria rev on rev.id = aud.rev ")
            .append("  where rev.datahora < to_date(:dataRevisao, 'dd/MM/yyyy HH24:mi:ss') ")
            .append("  and aud.id = :id ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("dataRevisao", Util.formatterDataHora.format(dataRevisao));
        q.setParameter("id", id);
        if (q.getResultList() != null && q.getResultList().get(0) != null) {
            RevisaoAuditoria revisaoAuditoria = recuperar(((BigDecimal) q.getResultList().get(0)).longValue());
            if (revisaoAuditoria != null) {
                return revisaoAuditoria;
            }
        }
        return null;
    }
}
