package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.GuiaPagamento;
import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.PagamentoEstorno;
import br.com.webpublico.entidades.RetencaoPgto;
import br.com.webpublico.entidades.contabil.execucao.DesdobramentoPagamento;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.PagamentoSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/10/14
 * Time: 09:34
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class JdbcPagamento extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcPagamento(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Autowired
    private SingletonGeradorId geradorDeIds;
    @Autowired
    private JdbcRetencaoPagamento jdbcRetencaoPagamento;
    @Autowired
    private JdbcGuiaPagamento jdbcGuiaPagamento;
    @Autowired
    private JdbcPagamentoEstorno jdbcPagamentoEstorno;
    @Autowired
    private JdbcDesdobramentoPagamento jdbcDesdobramentoPagamento;

    public Pagamento salvar(Pagamento pagamento, Long idRev) {
        if (Util.isNotNull(pagamento)) {
            pagamento.setId(geradorDeIds.getProximoId());
            getJdbcTemplate().batchUpdate(PagamentoSetter.SQL_INSERT, new PagamentoSetter(pagamento, pagamento.getId()));
            getJdbcTemplate().batchUpdate(PagamentoSetter.SQL_INSERT_AUD, new PagamentoSetter(pagamento, pagamento.getId(), idRev, 0));
            salvarOrAtualizarRelacionamentos(pagamento, idRev);
        }
        return pagamento;
    }

    public Pagamento atualizar(Pagamento pagamento, Long idRev) {
        if (Util.isNotNull(pagamento)) {
            if (Util.isNull(pagamento.getId())) {
                pagamento.setId(geradorDeIds.getProximoId());
            }
            getJdbcTemplate().batchUpdate(PagamentoSetter.SQL_UPDATE_ALL, new PagamentoSetter(pagamento, pagamento.getId()));
            getJdbcTemplate().batchUpdate(PagamentoSetter.SQL_INSERT_AUD, new PagamentoSetter(pagamento, pagamento.getId(), idRev, 1));
            salvarOrAtualizarRelacionamentos(pagamento, idRev);
        }
        return pagamento;
    }

    public void atualizarPagamento(StatusPagamento status, Pagamento pagamento, Long idRev) {
        Object[] objetos = new Object[4];
        objetos[0] = status.name();
        objetos[1] = pagamento.getNumDocumento() != null ? pagamento.getNumDocumento() : null;
        objetos[2] = pagamento.getNumeroRE() != null ? pagamento.getNumeroRE() : null;
        objetos[3] = pagamento.getId();
        getJdbcTemplate().update(PagamentoSetter.SQL_UPDATE, objetos);
        if (Util.isNotNull(idRev)) {
            getJdbcTemplate().batchUpdate(PagamentoSetter.SQL_INSERT_AUD, new PagamentoSetter(pagamento, pagamento.getId(), idRev, 1));
        }
    }

    public void atualizarStatus(Pagamento pagamento, Long idRev) {
        if (Util.isNotNull(pagamento)) {
            Object[] objetos = new Object[2];
            objetos[0] = pagamento.getStatus().name();
            objetos[1] = pagamento.getId();
            getJdbcTemplate().update(PagamentoSetter.SQL_UPDATE_STATUS, objetos);
            getJdbcTemplate().batchUpdate(PagamentoSetter.SQL_INSERT_AUD, new PagamentoSetter(pagamento, pagamento.getId(), idRev, 1));
        }
    }

    public void salvarOrAtualizarRelacionamentos(Pagamento pagamento, Long idRev) {
        if (Util.isNotNull(pagamento.getRetencaoPgtos()) && !pagamento.getRetencaoPgtos().isEmpty()) {
            for (RetencaoPgto retencoes : pagamento.getRetencaoPgtos()) {
                if (Util.isNotNull(retencoes.getId())) {
                    jdbcRetencaoPagamento.atualizar(retencoes, idRev);
                } else {
                    jdbcRetencaoPagamento.salvar(retencoes, idRev);
                }
            }
            List<RetencaoPgto> retencaoPgtosSalvos = jdbcRetencaoPagamento.buscarRetencaoPagamentosPorPagamento(pagamento);
            if (retencaoPgtosSalvos.size() != pagamento.getRetencaoPgtos().size()) {
                for (RetencaoPgto retencao : retencaoPgtosSalvos
                ) {
                    if (!pagamento.getRetencaoPgtos().contains(retencao)) {
                        jdbcRetencaoPagamento.delete(retencao, idRev);
                    }
                }
            }
        }

        if (Util.isNotNull(pagamento.getGuiaPagamento()) && !pagamento.getGuiaPagamento().isEmpty()) {
            for (GuiaPagamento guiaPagamento : pagamento.getGuiaPagamento()) {
                if (Util.isNotNull(guiaPagamento.getId())) {
                    jdbcGuiaPagamento.atualizar(guiaPagamento, idRev);
                } else {
                    jdbcGuiaPagamento.salvar(guiaPagamento, idRev);
                }
            }
            List<GuiaPagamento> guiasPagamentosSalvos = jdbcGuiaPagamento.buscarGuiaPagamentoPorPagamento(pagamento);
            if (guiasPagamentosSalvos.size() != pagamento.getGuiaPagamento().size()) {
                for (GuiaPagamento guiaPagamento :
                    guiasPagamentosSalvos) {
                    if (!pagamento.getGuiaPagamento().contains(guiaPagamento)) {
                        jdbcGuiaPagamento.delete(guiaPagamento, idRev);
                    }
                }
            }
        }

        if (Util.isNotNull(pagamento.getPagamentoEstornos()) && !pagamento.getPagamentoEstornos().isEmpty()) {
            for (PagamentoEstorno pagamentoEstorno : pagamento.getPagamentoEstornos()) {
                if (Util.isNotNull(pagamentoEstorno.getId())) {
                    jdbcPagamentoEstorno.atualizar(pagamentoEstorno, idRev);
                } else {
                    jdbcPagamentoEstorno.salvar(pagamentoEstorno, idRev);
                }
            }
        }

        if (Util.isNotNull(pagamento.getDesdobramentos()) && !pagamento.getDesdobramentos().isEmpty()) {
            for (DesdobramentoPagamento desdobramentoPagamento : pagamento.getDesdobramentos()) {
                if (Util.isNotNull(desdobramentoPagamento.getId())) {
                    jdbcDesdobramentoPagamento.atualizar(desdobramentoPagamento, idRev);
                } else {
                    jdbcDesdobramentoPagamento.salvar(desdobramentoPagamento, idRev);
                }
            }
            List<DesdobramentoPagamento> desdobramentosSalvos = jdbcDesdobramentoPagamento.buscarDesdobramentoPorPagamento(pagamento);
            if (desdobramentosSalvos.size() != pagamento.getDesdobramentos().size()) {
                for (DesdobramentoPagamento desdobramento : desdobramentosSalvos
                ) {
                    if (!pagamento.getDesdobramentos().contains(desdobramento)) {
                        jdbcDesdobramentoPagamento.delete(desdobramento, idRev);
                    }
                }
            }
        }
    }
}
