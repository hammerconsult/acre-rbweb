package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoMetrica;
import br.com.webpublico.entidades.MetricaMemoria;
import br.com.webpublico.entidades.MetricaSistema;
import br.com.webpublico.entidades.UsuarioNodeServer;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by André on 27/01/2015.
 */
@Stateless
public class MonitoramentoNodeServerFacade extends AbstractFacade<UsuarioNodeServer> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public MonitoramentoNodeServerFacade() {
        super(UsuarioNodeServer.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<UsuarioNodeServer> buscarUltimosLoginsDeHoje(Date hoje) {
        Query q = em.createQuery("from UsuarioNodeServer where trunc(dataHoraLogin) = :hoje");
        q.setParameter("hoje", DataUtil.dataSemHorario(hoje));
        List<UsuarioNodeServer> toReturn = q.getResultList();
        Collections.sort(toReturn);
        for (UsuarioNodeServer obj : toReturn) {
            obj.getUsuario().getPessoaFisica();
        }
        return toReturn;
    }


    public List<MetricaSistema> buscarUltimosAcessos(Date inicio, Date fim, MetricaSistema.Tipo tipo) {
        Query q = em.createQuery("from MetricaSistema where (data between :inicio and :fim) and tipo = :tipo order by data desc");
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        q.setParameter("tipo", tipo);
        List<MetricaSistema> toReturn = q.getResultList();
        Collections.sort(toReturn);
        return toReturn;
    }

    public List<MetricaMemoria> buscarUltimasMetricasMemoria(Date inicio, Date fim) {
        Query q = em.createQuery("from MetricaMemoria where (data between :inicio and :fim) order by data asc");
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        return q.getResultList();
    }


    public List<LinkedHashMap> buscarSqlsEmExecucao() {
        Query q = em.createNativeQuery("SELECT nvl(ses.username, 'ORACLE PROC') || ' (' || ses.sid || ')' USERNAME, " +
            "       SID, " +
            "       SES.serial#, " +
            "       MACHINE, " +
            "       REPLACE(SQL.SQL_TEXT, CHR(10), '')                         STMT, " +
            "       ltrim(to_char(floor(SES.LAST_CALL_ET / 3600), '09')) || ':' " +
            "           || ltrim(to_char(floor(mod(SES.LAST_CALL_ET, 3600) / 60), '09')) || ':' " +
            "           || ltrim(to_char(mod(SES.LAST_CALL_ET, 60), '09'))     RUNT " +
            "FROM V$SESSION SES, " +
            "     V$SQLtext_with_newlines SQL " +
            "where SES.STATUS = 'ACTIVE' " +
            "  and SES.USERNAME is not null " +
            "  and SES.SQL_ADDRESS = SQL.ADDRESS " +
            "  and SES.SQL_HASH_VALUE = SQL.HASH_VALUE " +
            "  and Ses.AUDSID <> userenv('SESSIONID') " +
            "  and USERNAME not like 'SAERB%' " +
            " order by runt desc, 1, sql.piece");

        List<LinkedHashMap> lista = Lists.newArrayList();
        List<Object[]> retorno = q.getResultList();
        for (Object[] objs : retorno) {
            LinkedHashMap map = new LinkedHashMap();
            map.put("USERNAME", objs[0]);
            map.put("SID", objs[1]);
            map.put("SERIAL", objs[2]);
            map.put("MACHINE", objs[3]);
            map.put("STMT", objs[4]);
            map.put("RUNT", objs[5]);
        }
        return lista;
    }

    public void salvarConfiguracao(ConfiguracaoMetrica configuracaoMetrica) {
        em.merge(configuracaoMetrica);
    }
}
