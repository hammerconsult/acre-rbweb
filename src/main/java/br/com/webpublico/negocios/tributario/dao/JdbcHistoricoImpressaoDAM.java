package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.HistoricoImpressaoDAM;
import br.com.webpublico.entidades.ItemDAM;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.seguranca.service.SistemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository(value = "historicoImpressaoDAM")
public class JdbcHistoricoImpressaoDAM extends JdbcDaoSupport implements Serializable {
    @Autowired
    SingletonGeradorId geradorDeIds;
    @Autowired
    SistemaService sistemaService;
    @Autowired
    JdbcDamDAO jdbcDamDAO;

    @Autowired
    public JdbcHistoricoImpressaoDAM(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public SistemaService getSistemaService() {
        return sistemaService;
    }

    public void inserirNovoHistorico(DAM dam, HistoricoImpressaoDAM.TipoImpressao origemImpressao) {
        inserirNovoHistorico(dam, getSistemaService().getUsuarioCorrente(), origemImpressao);
    }

    public void inserirNovoHistorico(DAM dam, UsuarioSistema usuario, HistoricoImpressaoDAM.TipoImpressao origemImpressao) {
        if (dam != null) {
            String sqlInsereHistorico = "INSERT INTO historicoimpressaodam " +
                "(ID, DAM_ID, USUARIOSISTEMA_ID, DATAOPERACAO, TIPOIMPRESSAO, PARCELA_ID) " +
                "VALUES (?,?,?,?,?,?)";

            String sqlIdsNovaSituacao = " select id from dam where tipo = ? and " +
                    " (id in(select dam_id from itemdam where parcela_id = ? and dam_id <> ?) or" +
                    "  id in ( select dam_id from itemdam " +
                    " inner join parcelavalordivida pvd on pvd.id = itemdam.parcela_id " +
                    " inner join opcaopagamento op on op.id = pvd.opcaopagamento_id " +
                    " where op.promocional = 1 and pvd.valordivida_id = ? and trunc(pvd.vencimento) < trunc(sysdate))) " +
                    " and situacao = ? ";

            Long idUsuario = usuario != null ? usuario.getId() : null;
            for (ItemDAM item : dam.getItens()) {
                getJdbcTemplate().update(sqlInsereHistorico, new Object[]{
                    geradorDeIds.getProximoId(),
                    item.getDAM().getId(),
                    idUsuario,
                    new Date(),
                    origemImpressao.name(),
                    item.getParcela().getId()
                });

                List<BigDecimal> ids = getJdbcTemplate().queryForList(sqlIdsNovaSituacao, new Object[]{
                        dam.getTipo().name(),
                        item.getParcela().getId(), dam.getId(), item.getParcela().getValorDivida().getId(),
                        DAM.Situacao.ABERTO.name(),
                }, BigDecimal.class);

                for (BigDecimal id : ids) {
                    jdbcDamDAO.atualizar(id.longValue(), DAM.Situacao.CANCELADO, usuario);
                }
            }

        }
    }

}
