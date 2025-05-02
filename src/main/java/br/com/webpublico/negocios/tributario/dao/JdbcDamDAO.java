package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.tributario.setter.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

@Repository("damDAO")
public class JdbcDamDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcDamDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persisteDam(DAM dam) {
        persisteHistoricoSituacaoDam(dam, null);
    }

    public void persisteDam(DAM dam, Long idUsuario) {
        persisteDamEmLote(Lists.newArrayList(dam));
        persisteHistoricoSituacaoDam(dam, idUsuario);
    }

    public <T extends AssistenteBarraProgresso> void persisteDans(List<DAM> dans, List<ItemDAM> itensDAM, List<HistoricoImpressaoDAM> impressoesDAM,
                             List<TributoDAM> tributosDAM, UsuarioSistema usuario) {
        persisteDans(dans, itensDAM, impressoesDAM, tributosDAM, usuario, null);
    }

    public <T extends AssistenteBarraProgresso> void persisteDans(List<DAM> dans, List<ItemDAM> itensDAM, List<HistoricoImpressaoDAM> impressoesDAM,
                             List<TributoDAM> tributosDAM, UsuarioSistema usuario, T assistente) {
        persisteDamEmLote(dans, assistente);
        persisteItemDamEmLote(itensDAM, assistente);
        persisteHistoricoSituacaoDamEmLote(dans, (usuario != null ? usuario.getId() : null), assistente);
        persisteHistoricoImpressaoEmLote(impressoesDAM, assistente);
        persisteTributoDAMEmLote(tributosDAM, assistente);
    }

    public void persisteDamEmLote(List<DAM> dans) {
        persisteDamEmLote(dans, null);
    }

    public <T extends AssistenteBarraProgresso> void persisteDamEmLote(List<DAM> dans, T assistente) {
        if (dans != null && !dans.isEmpty()) {
            String insert = " insert into dam (id, numerodam, codigobarras, vencimento, emissao, valororiginal, juros, " +
                " multa, correcaomonetaria, desconto, situacao, tipo, numero, exercicio_id, honorarios, sequencia) " +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
            getJdbcTemplate().batchUpdate(insert, new DAMSetter<>(dans, geradorDeIds.getIds(dans.size()), assistente));
        }
    }

    public void persisteHistoricoSituacaoDam(DAM dam, Long idUsuario) {
        persisteHistoricoSituacaoDamEmLote(Lists.newArrayList(dam), idUsuario);
    }

    public void persisteHistoricoSituacaoDamEmLote(List<DAM> dans, Long idUsuario) {
        persisteHistoricoSituacaoDamEmLote(dans, idUsuario, null);
    }

    public <T extends AssistenteBarraProgresso> void persisteHistoricoSituacaoDamEmLote(List<DAM> dans, Long idUsuario, T assistente) {
        if (dans != null && !dans.isEmpty()) {
            String insert = " insert into historicosituacaodam (id, dam_id, usuariosituacao_id, situacaodam, datasituacao) " +
                " values (?, ?, ?, ?, ?) ";
            getJdbcTemplate().batchUpdate(insert, new HistoricoSituacaoDAMSetter<>(dans, geradorDeIds.getIds(dans.size()), idUsuario, assistente));
        }
    }

    public void persisteItemDAM(ItemDAM itemDAM) {
        persisteItemDamEmLote(Lists.newArrayList(itemDAM));
    }

    public void persisteItemDamEmLote(List<ItemDAM> itensDAM) {
        persisteItemDamEmLote(itensDAM, null);
    }

    public <T extends AssistenteBarraProgresso> void persisteItemDamEmLote(List<ItemDAM> itensDAM, T assistente) {
        if (itensDAM != null && !itensDAM.isEmpty()) {
            String insert = " insert into itemdam (id, dam_id, desconto, correcaomonetaria, juros, multa, outrosacrescimos, " +
                " valororiginaldevido, parcela_id, honorarios) " +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
            getJdbcTemplate().batchUpdate(insert, new ItemDAMSetter<>(itensDAM, geradorDeIds.getIds(itensDAM.size()), assistente));
        }
    }

    public void persisteTributoDAM(TributoDAM tributoDAM) {
        persisteTributoDAMEmLote(Lists.newArrayList(tributoDAM));
    }

    public void persisteTributoDAMEmLote(List<TributoDAM> tributosDAM) {
        persisteTributoDAMEmLote(tributosDAM, null);
    }

    public <T extends AssistenteBarraProgresso> void persisteTributoDAMEmLote(List<TributoDAM> tributosDAM, T assistente) {
        if (tributosDAM != null && !tributosDAM.isEmpty()) {
            String insert = " insert into tributodam (id, tributo_id, itemdam_id, valororiginal, desconto)" +
                " values (?, ?, ?, ?, ?) ";
            getJdbcTemplate().batchUpdate(insert, new TributoDAMSetter<>(tributosDAM, geradorDeIds.getIds(tributosDAM.size()), assistente));
        }
    }

    public void atualizar(Long idDam, DAM.Situacao situacao) {
        atualizar(idDam, situacao, null);
    }

    public void atualizar(Long idDam, DAM.Situacao situacao, UsuarioSistema usuario) {
        String sql = " UPDATE DAM SET SITUACAO = ? WHERE ID = ? ";
        persisteHistoricoSituacaoDam(new DAM(idDam, situacao), usuario != null ? usuario.getId() : null);
        getJdbcTemplate().update(sql, situacao.name(), idDam);
    }

    public void persisteHistoricoImpressao(HistoricoImpressaoDAM historicoImpressaoDAM) {
        persisteHistoricoImpressaoEmLote(Lists.newArrayList(historicoImpressaoDAM));
    }

    public void persisteHistoricoImpressaoEmLote(List<HistoricoImpressaoDAM> historicos) {
        persisteHistoricoImpressaoEmLote(historicos, null);
    }

    public <T extends AssistenteBarraProgresso> void persisteHistoricoImpressaoEmLote(List<HistoricoImpressaoDAM> historicos, T assistente) {
        if (historicos != null && !historicos.isEmpty()) {
            String insert = " insert into historicoimpressaodam " +
                " (id, dam_id, usuariosistema_id, dataoperacao, tipoimpressao, parcela_id) " +
                " values (?, ?, ?, ?, ?, ?) ";
            getJdbcTemplate().batchUpdate(insert, new HistoricoImpressaoDAMSetter<>(historicos, geradorDeIds.getIds(historicos.size()), assistente));
        }
    }

}
