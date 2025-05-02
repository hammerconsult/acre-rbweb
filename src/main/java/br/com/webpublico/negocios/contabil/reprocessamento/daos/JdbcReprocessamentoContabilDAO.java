package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.contabil.ReprocessamentoContabil;
import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.DeleteLancamentoContabilSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.DeleteSaldoContaContabilSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 22/03/17
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
@Repository(value = "jdbcReprocessamentoContabilDAO")
public class JdbcReprocessamentoContabilDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcReprocessamentoContabilDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void removerLancamentoSaldo(ReprocessamentoContabil reprocessamentoContabil) {
        for (TipoBalancete tipoBalancete : reprocessamentoContabil.getTipoBalancetes()) {
            if (reprocessamentoContabil.getListaHierarquias() == null || reprocessamentoContabil.getListaHierarquias().isEmpty()) {
                removerLancamento(reprocessamentoContabil, tipoBalancete, null);
                removerSaldo(reprocessamentoContabil, tipoBalancete, null);
            } else {
                for (HierarquiaOrganizacional hierarquiaOrganizacional : reprocessamentoContabil.getListaHierarquias()) {
                    removerLancamento(reprocessamentoContabil, tipoBalancete, hierarquiaOrganizacional);
                    removerSaldo(reprocessamentoContabil, tipoBalancete, hierarquiaOrganizacional);
                }
            }
        }
    }

    private void removerLancamento(ReprocessamentoContabil reprocessamentoContabil, TipoBalancete tipoBalancete, HierarquiaOrganizacional hierarquiaOrganizacional) {
        DeleteLancamentoContabilSetter pss = new DeleteLancamentoContabilSetter(reprocessamentoContabil.getDataInicio(), reprocessamentoContabil.getDataFim(), tipoBalancete, hierarquiaOrganizacional);
        getJdbcTemplate().update(pss.getSql(), pss);
    }

    private void removerSaldo(ReprocessamentoContabil reprocessamentoContabil, TipoBalancete tipoBalancete, HierarquiaOrganizacional hierarquiaOrganizacional) {
        DeleteSaldoContaContabilSetter pss = new DeleteSaldoContaContabilSetter(reprocessamentoContabil.getDataInicio(), reprocessamentoContabil.getDataFim(), tipoBalancete, hierarquiaOrganizacional);
        getJdbcTemplate().update(pss.getSql(), pss);
    }
}
