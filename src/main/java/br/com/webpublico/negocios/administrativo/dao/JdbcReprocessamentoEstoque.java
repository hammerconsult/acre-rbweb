package br.com.webpublico.negocios.administrativo.dao;

import br.com.webpublico.entidades.Estoque;
import br.com.webpublico.entidades.EstoqueLoteMaterial;
import br.com.webpublico.entidades.MovimentoEstoque;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamentoEstoque;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository("jdbcReprocessamentoEstoque")
public class JdbcReprocessamentoEstoque extends JdbcDaoSupport implements Serializable {

    public static final String UPDATE_ESTOQUE = " update estoque set dataestoque = ?, financeiro = ?, fisico = ?, material_id = ?, localestoqueorcamentario_id = ? where id = ? ";

    public static final String INSERT_ESTOQUE = " insert into estoque (bloqueado, dataestoque, financeiro, fisico, material_id, liberadoem, localestoqueorcamentario_id, tipoestoque, id) " +
        " values(?, ?, ?, ?, ?, ?, ?, ?, ?) ";

    public static final String INSERT_ESTOQUE_LOTE = " insert into estoquelotematerial (id, quantidade, estoque_id, lotematerial_id) " +
        " values (hibernate_sequence.nextval, ?, ?, ?) ";

    public static final String UPDATE_ESTOQUE_LOTE = " update estoquelotematerial set quantidade = ?, estoque_id = ?, lotematerial_id = ? " +
        " where id = ? ";

    public static final String INSERT_MOVIMENTO_ESTOQUE = " insert into movimentoestoque (id, datamovimento, financeiro, fisico, lotematerial_id, material_id, tipooperacao, " +
        "  estoque_id, financeiroreajustado, localestoqueorcamentario_id, descricaodaoperacao, valorunitario) " +
        "  values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcReprocessamentoEstoque(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public MovimentoEstoque inserir(MovimentoEstoque mov, Estoque estoque, EstoqueLoteMaterial estoqueLote) {
        if (estoque.getId() == null) {
            estoque.setId(geradorDeIds.getProximoId());
            getJdbcTemplate().update(INSERT_ESTOQUE, getAtributosEstoqueInsert(estoque));
            insertEstoqueLoteMaterial(estoque, estoqueLote);
            insertMovimentoEstoque(mov, estoque);
            return mov;
        } else {
            getJdbcTemplate().update(UPDATE_ESTOQUE, getAtributosEstoqueUpdate(estoque));
            insertEstoqueLoteMaterial(estoque, estoqueLote);
            insertMovimentoEstoque(mov, estoque);
            return mov;
        }
    }

    private void insertMovimentoEstoque(MovimentoEstoque mov, Estoque estoque) {
        mov.setId(geradorDeIds.getProximoId());
        getJdbcTemplate().update(INSERT_MOVIMENTO_ESTOQUE, getAtributosMovimentoEstoque(mov, estoque));
    }

    private void insertEstoqueLoteMaterial(Estoque estoque, EstoqueLoteMaterial estoqueLote) {
        if (estoqueLote != null) {
            if (estoqueLote.getId() != null) {
                getJdbcTemplate().update(UPDATE_ESTOQUE_LOTE, estoqueLote.getQuantidade(), estoque.getId(), estoqueLote.getLoteMaterial().getId(), estoqueLote.getId());
            } else {
                getJdbcTemplate().update(INSERT_ESTOQUE_LOTE, estoqueLote.getQuantidade(), estoque.getId(), estoqueLote.getLoteMaterial().getId());
            }
        }
    }

    public void updateItemEntrada(Long idItem, Long idMovimento) {
        String sql = " update itementradamaterial set movimentoestoque_id = ? where id = ? ";
        getJdbcTemplate().update(sql, idMovimento, idItem);
    }

    public void updateItemEntradaEstorno(Long idItem, Long idMovimento) {
        String sql = " update itementradamaterialestorno set movimentoestoque_id = ? where id = ? ";
        getJdbcTemplate().update(sql, idMovimento, idItem);
    }

    public void updateItemSaida(Long idItem, Long idMovimento) {
        String sql = " update itemsaidamaterial set movimentoestoque_id = ? where id = ? ";
        getJdbcTemplate().update(sql, idMovimento, idItem);
    }

    public void updateItemConversao(Long idItem, Long idMovimentoEntrada, Long idMovimentoSaida) {
        String sql = " update itemconversaounidademedmat set movimentoestoqueentrada_id = ?, movimentoestoquesaida_id = ? where id = ? ";
        getJdbcTemplate().update(sql, idMovimentoEntrada, idMovimentoSaida, idItem);
    }

    public void deletarEstoque(AssistenteReprocessamentoEstoque assistente) {
        String sql = " delete from estoque " +
            "          where id in (select est.id from estoque est " +
            "                       inner join material mat on mat.id = est.material_id " +
            "                       inner join localestoqueorcamentario leo on leo.id = est.localestoqueorcamentario_id " +
            "                       inner join localestoque le on le.id = leo.localestoque_id " +
            "                       inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "                       inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "                        where to_date(?, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(?, 'dd/MM/yyyy')) " +
            "                        and to_date(?, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(?, 'dd/MM/yyyy')) " +
            "                        and trunc(est.dataestoque) between to_date(?, 'dd/MM/yyyy') and to_date(?, 'dd/MM/yyyy') " + assistente.getCondicaoSql() + ")";
        getJdbcTemplate().update(sql, getAtributosDatasCondicaoSql(assistente));
    }

    public void deleteEstoqueLoteMaterial(AssistenteReprocessamentoEstoque assistente) {
        String sql = " delete from estoquelotematerial lote " +
            "          where lote.estoque_id in (select est.id " +
            "                                   from estoque est " +
            "                                   inner join material mat on mat.id = est.material_id " +
            "                                   inner join localestoqueorcamentario leo on leo.id = est.localestoqueorcamentario_id " +
            "                                   inner join localestoque le on le.id = leo.localestoque_id " +
            "                                   inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "                                   inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "                                     where to_date(?, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(?, 'dd/MM/yyyy')) " +
            "                                     and to_date(?, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(?, 'dd/MM/yyyy')) " +
            "                                     and trunc(est.dataestoque) between to_date(?, 'dd/MM/yyyy') and to_date(?, 'dd/MM/yyyy') " + assistente.getCondicaoSql() + ")";
        getJdbcTemplate().update(sql, getAtributosDatasCondicaoSql(assistente));
    }

    public void deletarMovimentoEstoque(AssistenteReprocessamentoEstoque assistente) {
        String sql = " delete from movimentoestoque " +
            "          where id in (select mov.id from movimentoestoque mov " +
            "                       inner join material mat on mat.id = mov.material_id " +
            "                       inner join localestoqueorcamentario leo on leo.id = mov.localestoqueorcamentario_id " +
            "                       inner join localestoque le on le.id = leo.localestoque_id " +
            "                       inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "                       inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "                         where to_date(?, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(?, 'dd/MM/yyyy')) " +
            "                         and to_date(?, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(?, 'dd/MM/yyyy')) " +
            "                         and trunc(datamovimento) between to_date(?, 'dd/MM/yyyy') and to_date(?, 'dd/MM/yyyy') " + assistente.getCondicaoSql() + ")";
        getJdbcTemplate().update(sql, getAtributosDatasCondicaoSql(assistente));
    }

    public void removerMovimentoEstoqueItemConversao(AssistenteReprocessamentoEstoque assistente) {
        String sql = " update itemconversaounidademedmat set movimentoestoquesaida_id = null, movimentoestoqueentrada_id = null " +
            "           where id in (select distinct item.id from itemconversaounidademedmat item " +
            "                         inner join conversaounidademedidamat conv on conv.id = item.conversaounidademedida_id " +
            "                         inner join material mat on mat.id = coalesce(item.materialentrada_id, item.materialsaida_id) " +
            "                         inner join localestoquematerial lem on lem.material_id = mat.id " +
            "                         inner join localestoque le on le.id = lem.localestoque_id " +
            "                         inner join localestoqueorcamentario leo on leo.localestoque_id = le.id " +
            "                         inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "                         inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "                           where to_date(?, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(?, 'dd/MM/yyyy')) " +
            "                           and to_date(?, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(?, 'dd/MM/yyyy')) " +
            "                           and trunc(conv.datamovimento) between to_date(?, 'dd/MM/yyyy') and to_date(?, 'dd/MM/yyyy') " + assistente.getCondicaoSql() + ")";
        getJdbcTemplate().update(sql, getAtributosDatasCondicaoSql(assistente));
    }

    public void removerMovimentoEstoqueItemSaida(AssistenteReprocessamentoEstoque assistente) {
        String sql = " update itemsaidamaterial set movimentoestoque_id = null  " +
            "           where id in (select item.id from itemsaidamaterial item " +
            "                        inner join saidamaterial saida on saida.id = item.saidamaterial_id " +
            "                        inner join material mat on mat.id = item.material_id " +
            "                        inner join localestoqueorcamentario leo on leo.id = item.localestoqueorcamentario_id " +
            "                        inner join localestoque le on le.id = leo.localestoque_id " +
            "                        inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "                        inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "                           where to_date(?, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(?, 'dd/MM/yyyy')) " +
            "                           and to_date(?, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(?, 'dd/MM/yyyy')) " +
            "                           and trunc(saida.datasaida) between to_date(?, 'dd/MM/yyyy') and to_date(?, 'dd/MM/yyyy') " + assistente.getCondicaoSql() + ")";
        getJdbcTemplate().update(sql, getAtributosDatasCondicaoSql(assistente));
    }

    public void removerMovimentoEstoqueItemEntrada(AssistenteReprocessamentoEstoque assistente) {
        String sql = " update itementradamaterial set movimentoestoque_id = null " +
            "           where id in (select item.id from itementradamaterial item " +
            "                        inner join entradamaterial ent on ent.id = item.entradamaterial_id " +
            "                        inner join material mat on mat.id = item.material_id " +
            "                        inner join localestoqueorcamentario leo on leo.id = item.localestoqueorcamentario_id " +
            "                        inner join localestoque le on le.id = leo.localestoque_id " +
            "                        inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "                        inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "                          where to_date(?, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(?, 'dd/MM/yyyy')) " +
            "                          and to_date(?, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(?, 'dd/MM/yyyy')) " +
            "                          and trunc(ent.dataentrada) between to_date(?, 'dd/MM/yyyy') and to_date(?, 'dd/MM/yyyy') " + assistente.getCondicaoSql() + ")";
        getJdbcTemplate().update(sql, getAtributosDatasCondicaoSql(assistente));
    }

    private Object[] getAtributosDatasCondicaoSql(AssistenteReprocessamentoEstoque assistente) {
        Object[] objetos = new Object[6];

        objetos[0] = DataUtil.getDataFormatada(assistente.getDataInicial());
        objetos[1] = DataUtil.getDataFormatada(assistente.getDataFinal());
        objetos[2] = DataUtil.getDataFormatada(assistente.getDataInicial());
        objetos[3] = DataUtil.getDataFormatada(assistente.getDataFinal());
        objetos[4] = DataUtil.getDataFormatada(assistente.getDataInicial());
        objetos[5] = DataUtil.getDataFormatada(assistente.getDataFinal());
        return objetos;
    }

    private Object[] getAtributosMovimentoEstoque(MovimentoEstoque mov, Estoque estoque) {
        Object[] objetos = new Object[12];
        objetos[0] = mov.getId();
        objetos[1] = mov.getDataMovimento();
        objetos[2] = mov.getFinanceiro();
        objetos[3] = mov.getFisico();
        objetos[4] = mov.getLoteMaterial() != null ? mov.getLoteMaterial().getId() : null;
        objetos[5] = mov.getMaterial().getId();
        objetos[6] = mov.getTipoOperacao().name();
        objetos[7] = estoque.getId();
        objetos[8] = mov.getFinanceiroReajustado() != null ? mov.getFinanceiroReajustado() : null;
        objetos[9] = mov.getLocalEstoqueOrcamentario().getId();
        objetos[10] = mov.getDescricaoDaOperacao();
        objetos[11] = mov.getValorUnitario();
        return objetos;
    }

    private Object[] getAtributosEstoqueInsert(Estoque estoque) {
        Object[] objetos = new Object[9];
        objetos[0] = estoque.getBloqueado();
        objetos[1] = estoque.getDataEstoque();
        objetos[2] = estoque.getFinanceiro();
        objetos[3] = estoque.getFisico();
        objetos[4] = estoque.getMaterial().getId();
        objetos[5] = estoque.getLiberadoEm();
        objetos[6] = estoque.getLocalEstoqueOrcamentario().getId();
        objetos[7] = estoque.getTipoEstoque().name();
        objetos[8] = estoque.getId();
        return objetos;
    }

    private Object[] getAtributosEstoqueUpdate(Estoque estoque) {
        Object[] objetos = new Object[6];
        objetos[0] = estoque.getDataEstoque();
        objetos[1] = estoque.getFinanceiro();
        objetos[2] = estoque.getFisico();
        objetos[3] = estoque.getMaterial().getId();
        objetos[4] = estoque.getLocalEstoqueOrcamentario().getId();
        objetos[5] = estoque.getId();
        return objetos;
    }
}
