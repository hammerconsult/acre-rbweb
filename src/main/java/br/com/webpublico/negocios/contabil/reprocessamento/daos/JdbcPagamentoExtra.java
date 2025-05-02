package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.GuiaPagamentoExtra;
import br.com.webpublico.entidades.PagamentoExtra;
import br.com.webpublico.entidades.PagamentoReceitaExtra;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.PagamentoExtraSetter;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

@Repository
public class JdbcPagamentoExtra extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcPagamentoExtra(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Autowired
    private JdbcPagamentoReceitaExtra jdbcPagamentoReceitaExtra;
    @Autowired
    private JdbcGuiaPagamentoExtra jdbcGuiaPagamentoExtra;

    public PagamentoExtra atualizar(PagamentoExtra pagamentoExtra, Long idRev) {
        if (Util.isNotNull(pagamentoExtra)) {
            getJdbcTemplate().batchUpdate(PagamentoExtraSetter.SQL_UPDATE_ALL, new PagamentoExtraSetter(pagamentoExtra, pagamentoExtra.getId()));
            getJdbcTemplate().batchUpdate(PagamentoExtraSetter.SQL_INSERT_AUD, new PagamentoExtraSetter(pagamentoExtra, pagamentoExtra.getId(), idRev, 1));

            if (Util.isNotNull(pagamentoExtra.getPagamentoReceitaExtras()) && !pagamentoExtra.getPagamentoReceitaExtras().isEmpty()) {
                for (PagamentoReceitaExtra pagamentoReceitaExtra :
                    pagamentoExtra.getPagamentoReceitaExtras()) {
                    if (Util.isNotNull(pagamentoReceitaExtra.getId())) {
                        jdbcPagamentoReceitaExtra.atualizar(pagamentoReceitaExtra, idRev);
                    } else {
                        jdbcPagamentoReceitaExtra.salvar(pagamentoReceitaExtra, idRev);
                    }
                }
                List<PagamentoReceitaExtra> pagamentoReceitaExtrasSalvos = jdbcPagamentoReceitaExtra.buscarPagamentosReceitaExtraPorPagamentoExtra(pagamentoExtra);
                if (pagamentoExtra.getPagamentoReceitaExtras().size() != pagamentoReceitaExtrasSalvos.size()) {
                    for (PagamentoReceitaExtra pagamentoReceitaExtra : pagamentoReceitaExtrasSalvos) {
                        if (!pagamentoExtra.getPagamentoReceitaExtras().contains(pagamentoReceitaExtra)) {
                            jdbcPagamentoReceitaExtra.delete(pagamentoReceitaExtra, idRev);
                        }
                    }
                }
            }

            if (Util.isNotNull(pagamentoExtra.getGuiaPagamentoExtras()) && !pagamentoExtra.getGuiaPagamentoExtras().isEmpty()) {
                for (GuiaPagamentoExtra guiaPagamentoExtra : pagamentoExtra.getGuiaPagamentoExtras()) {
                    if (Util.isNotNull(guiaPagamentoExtra.getId())) {
                        jdbcGuiaPagamentoExtra.atualizar(guiaPagamentoExtra, idRev);
                    } else {
                        jdbcGuiaPagamentoExtra.salvar(guiaPagamentoExtra, idRev);
                    }
                }
                List<GuiaPagamentoExtra> guiasPagamentoExtrasSalvos = jdbcGuiaPagamentoExtra.buscarGuiaPagamentoExtraPorPagamentoExtra(pagamentoExtra);
                if (pagamentoExtra.getGuiaPagamentoExtras().size() != guiasPagamentoExtrasSalvos.size()) {
                    for (GuiaPagamentoExtra guiaPagamentoExtra : guiasPagamentoExtrasSalvos) {
                        if (!pagamentoExtra.getGuiaPagamentoExtras().contains(guiaPagamentoExtra)) {
                            jdbcGuiaPagamentoExtra.delete(guiaPagamentoExtra, idRev);
                        }
                    }
                }
            }
        }
        return pagamentoExtra;
    }

    public void atualizarPagamentoExtra(StatusPagamento statusPagamento, PagamentoExtra pagamentoExtra, Long idRev) {
        Object[] objectos = new Object[4];
        if (Util.isNotNull(statusPagamento) && Util.isNotNull(pagamentoExtra)) {
            objectos[0] = statusPagamento.name();
            objectos[1] = pagamentoExtra.getNumeroPagamento();
            objectos[2] = pagamentoExtra.getNumeroRE();
            objectos[3] = pagamentoExtra.getId();
            getJdbcTemplate().update(PagamentoExtraSetter.SQL_UPDATE, objectos);
            if (Util.isNotNull(idRev)) {
                getJdbcTemplate().batchUpdate(PagamentoExtraSetter.SQL_INSERT_AUD, new PagamentoExtraSetter(pagamentoExtra, pagamentoExtra.getId(), idRev, 0));
            }
        }
    }

    public void atualizarStatus(PagamentoExtra pagamentoExtra, StatusPagamento statusPagamento, Long idRev) {
        if (Util.isNotNull(pagamentoExtra)) {
            Object[] objetos = new Object[2];
            objetos[0] = statusPagamento.name();
            objetos[1] = pagamentoExtra.getId();
            getJdbcTemplate().update(PagamentoExtraSetter.SQL_UPDATE_STATUS, objetos);
            getJdbcTemplate().batchUpdate(PagamentoExtraSetter.SQL_INSERT_AUD, new PagamentoExtraSetter(pagamentoExtra, pagamentoExtra.getId(), idRev, 1));
        }
    }

    public void indefeir(PagamentoExtra pagamentoExtra, Long idRev) {
        if (Util.isNotNull(pagamentoExtra)) {
            Object[] objetos = new Object[3];
            objetos[0] = pagamentoExtra.getStatus().name();
            objetos[1] = pagamentoExtra.getDataConciliacao();
            objetos[2] = pagamentoExtra.getId();
            getJdbcTemplate().update(PagamentoExtraSetter.SQL_UPDATE_INDEFERIR, objetos);
            getJdbcTemplate().batchUpdate(PagamentoExtraSetter.SQL_INSERT_AUD, new PagamentoExtraSetter(pagamentoExtra, pagamentoExtra.getId(), idRev, 1));
        }
    }

    public PagamentoExtra salvar(PagamentoExtra pagamentoExtra, Long idRev) {
        if (Util.isNotNull(pagamentoExtra)) {
            getJdbcTemplate().batchUpdate(PagamentoExtraSetter.SQL_INSERT, new PagamentoExtraSetter(pagamentoExtra, pagamentoExtra.getId()));
            getJdbcTemplate().batchUpdate(PagamentoExtraSetter.SQL_INSERT_AUD, new PagamentoExtraSetter(pagamentoExtra, pagamentoExtra.getId(), idRev, 0));
        }
        return pagamentoExtra;
    }

}
