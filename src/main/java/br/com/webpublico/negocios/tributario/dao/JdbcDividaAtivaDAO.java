package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.interfaces.GeradorDeID;
import br.com.webpublico.negocios.tributario.rowmapper.*;
import br.com.webpublico.negocios.tributario.setter.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.UltimoLinhaDaPaginaDoLivroDividaAtiva;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Repository("dividaAtivaDAO")
public class JdbcDividaAtivaDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId singletonGeradorId;

    @Autowired
    public JdbcDividaAtivaDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public LivroDividaAtiva findLivroByDividaAndExercicio(Long idDivida, Integer exercicio) {
        try {
            String sql = "SELECT DISTINCT LIVRO.* FROM LIVRODIVIDAATIVA LIVRO " +
                    "INNER JOIN EXERCICIO ON EXERCICIO.ID = LIVRO.EXERCICIO_ID " +
                    "INNER JOIN DIVIDA ON divida.nrlivrodividaativa = LIVRO.NUMERO AND livro.tipocadastrotributario = divida.tipocadastro " +
                    "WHERE EXERCICIO.ANO = ? " +
                    "AND DIVIDA.ID = ? " +
                    "AND ROWNUM = 1 ";
            LivroDividaAtiva livro = (LivroDividaAtiva) getJdbcTemplate().queryForObject(sql, new Object[]{exercicio, idDivida}, new LivroDARowMapper());
            livro.setItensLivros(findItensLivroByLivro(livro.getId()));
            return livro;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<ItemLivroDividaAtiva> findItensLivroByLivro(Long idLivro) {
        try {
            String sql = "SELECT ITEM.* FROM ITEMLIVRODIVIDAATIVA ITEM " +
                    " WHERE ITEM.LIVRODIVIDAATIVA_ID = ? ";
            return (List<ItemLivroDividaAtiva>) getJdbcTemplate().query(sql, new Object[]{idLivro}, new ItemLivroDARowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<ValorPorTributoRowMapper.ValorPorTributo> findValoresPorTributoByParcela(Long idParcela, Calculo.TipoCalculo tipoCalculo) {
        try {
            String sql = "select tributo.id ID, sum(ipvd.valor) VALOR from itemparcelavalordivida ipvd\n" +
                    "inner join itemvalordivida ivd on ivd.id = ipvd.itemvalordivida_id\n" +
                    "inner join tributo on tributo.id = ivd.tributo_id\n" +
                    "where ipvd.parcelavalordivida_id = ?\n";
            if (Calculo.TipoCalculo.NFSE.equals(tipoCalculo)) {
                sql += "and tributo.tipoTributo not in ('"+Tributo.TipoTributo.MULTA.name()+"','"+
                                                           Tributo.TipoTributo.JUROS.name()+"','"+
                                                           Tributo.TipoTributo.CORRECAO.name()+"','"+
                                                           Tributo.TipoTributo.HONORARIOS.name()+"')\n";
            }
            sql += "group by tributo.id";

            return (List<ValorPorTributoRowMapper.ValorPorTributo>) getJdbcTemplate().query(sql, new Object[]{idParcela}, new ValorPorTributoRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Divida> findAll() {
        try {
            String sql = "SELECT * FROM divida ORDER BY descricao";
            return (List<Divida>) getJdbcTemplate().query(sql, new Object[]{}, new DividaRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Divida findDividaById(Long idDivida) {
        try {
            String sql = "SELECT * FROM divida WHERE divida.id = ?";
            return (Divida) getJdbcTemplate().queryForObject(sql, new Object[]{idDivida}, new DividaRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Divida findDividaDestinoByDivida(Long idDivida) {
        try {
            String sql = "select * from divida where id in(select d.divida_id from divida d where id = ?)";
            return (Divida) getJdbcTemplate().queryForObject(sql, new Object[]{idDivida}, new DividaRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UltimoLinhaDaPaginaDoLivroDividaAtiva findUltimaLinhaByLivro(Long idLivro) {
        try {
            String sql = "select max(linha.numeroDaLinha) as LINHA, " +
                    " max(linha.sequencia) as SEQUENCIA, " +
                    " linha.pagina AS PAGINA," +
                    " livro.numero as NUMEROLIVRO " +
                    " from livrodividaativa livro " +
                    " inner join itemlivrodividaativa item on item.livrodividaativa_id = livro.id " +
                    " inner join linhadolivrodividaativa linha on linha.itemlivrodividaativa_id = item.id " +
                    " where livro.id = ? " +
                    " and linha.pagina = (select max(l.pagina) from LinhaDoLivroDividaAtiva l " +
                    " inner join itemlivrodividaativa i on i.id = l.itemlivrodividaativa_id " +
                    " inner join livrodividaativa lda on lda.id = i.livrodividaativa_id and lda.id = ?)" +
                    " group by linha.pagina, livro.numero ";
            return (UltimoLinhaDaPaginaDoLivroDividaAtiva) getJdbcTemplate().queryForObject(sql, new Object[]{idLivro, idLivro}, new LinhaLivroDARowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public SituacaoParcelaValorDivida findUltimaSituacaoParcelaValorDividaByIdParcela(Long idParcela) {
        try {
            String sql = "select spvd.* from ParcelaValorDivida pvd " +
                " inner join SituacaoParcelaValorDivida spvd on spvd.id = pvd.situacaoAtual_id " +
                "where pvd.id = ? ";
            return (SituacaoParcelaValorDivida) getJdbcTemplate().queryForObject(sql, new Object[]{idParcela}, new SituacaoParcelaValorDividaRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String buscarDeTodosParcelamentos(Long idParcela) {
        try {
            String sql = "SELECT CASE \n" +
                "   WHEN pp.numeroReparcelamento IS NULL THEN coalesce(pp.numero || '/' || ex.ano,'') \n" +
                "   ELSE coalesce(pp.numero || '-' || pp.numeroReparcelamento || '/' || ex.ano,'') \n" +
                "END AS numeroParcelamento FROM itemprocessoparcelamento ipp \n" +
                "INNER JOIN processoparcelamento pp ON pp.id = ipp.processoparcelamento_id \n" +
                "INNER JOIN exercicio ex ON ex.id = pp.exercicio_id \n" +
                "WHERE ipp.id = (SELECT max(ipp.id) FROM itemprocessoparcelamento ipp \n" +
                "INNER JOIN processoparcelamento pp ON pp.id = ipp.processoparcelamento_id \n" +
                "WHERE ipp.parcelavalordivida_id = ? \n" +
                "  AND pp.situacaoparcelamento in ('CANCELADO', 'ESTORNADO', 'PAGO') \n" +
                ")";
            String retorno = getJdbcTemplate().queryForObject(sql, String.class, idParcela);
            if (retorno != null) {
                return " Parcelamento: " + retorno;
            }
            return "";
        } catch (EmptyResultDataAccessException e) {
            return "";
        }
    }

    public String buscarDosParcelamentoPagos(Long idParcela) {
        try {
            String sql = "SELECT CASE \n" +
                "   WHEN pp.numeroReparcelamento IS NULL THEN coalesce(pp.numero || '/' || ex.ano,'') \n" +
                "   ELSE coalesce(pp.numero || '-' || pp.numeroReparcelamento || '/' || ex.ano,'') \n" +
                "END AS numeroParcelamento FROM itemprocessoparcelamento ipp \n" +
                "INNER JOIN processoparcelamento pp ON pp.id = ipp.processoparcelamento_id \n" +
                "INNER JOIN exercicio ex ON ex.id = pp.exercicio_id \n" +
                "WHERE ipp.id = (SELECT max(ipp.id) FROM itemprocessoparcelamento ipp \n" +
                "INNER JOIN processoparcelamento pp ON pp.id = ipp.processoparcelamento_id \n" +
                "WHERE ipp.parcelavalordivida_id = ? \n" +
                "  AND pp.situacaoparcelamento in ('PAGO', 'FINALIZADO'))";
            String retorno = getJdbcTemplate().queryForObject(sql, String.class, idParcela);
            if (retorno != null) {
                return " Parcelamento: " + retorno;
            }
            return "";
        } catch (EmptyResultDataAccessException e) {
            return "";
        }
    }

    public IdETipoCalculoParcelaOriginalRowMapper recuperaIdParcelaOriginalETipoCalculo(Long idParcela) {
        String sql = "select pvdOriginal.id, calculoOriginal.tipocalculo\n" +
                "from parcelavalordivida pvd \n" +
                "inner join valordivida vd on vd.id = pvd.valordivida_id\n" +
                "inner join iteminscricaodividaativa itemda on itemda.id = vd.calculo_id\n" +
                "inner join inscricaodividaparcela ipvd on ipvd.iteminscricaodividaativa_id = itemda.id\n" +
                "inner join parcelavalordivida pvdOriginal on pvdOriginal.id = ipvd.parcelavalordivida_id\n" +
                "inner join valordivida vdOriginal on vdOriginal.id = pvdOriginal.valordivida_id \n" +
                "inner join calculo calculoOriginal on calculoOriginal.id = vdOriginal.calculo_id\n" +
                "where pvd.id = ? and rownum = 1";
        return (IdETipoCalculoParcelaOriginalRowMapper) getJdbcTemplate().queryForObject(sql, new Object[]{idParcela}, new IdETipoCalculoParcelaOriginalRowMapper());
    }

    public IdETipoCalculoParcelaOriginalRowMapper recuperaIdParcelaOriginalETipoCalculoCancelamentoParcelamento(Long idParcela) {
        String sql = "select pcanc.parcela_id as id, calc.tipocalculo from PARCELASCANCELPARCELAMENTO pcanc\n" +
            "inner join cancelamentoparcelamento canc on canc.id = pcanc.CANCELAMENTOPARCELAMENTO_ID\n" +
            "inner join calculo calc on calc.id = canc.id\n" +
            "inner join valordivida vd on vd.calculo_id = canc.id\n" +
            "inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id\n" +
            "where pvd.id = ? " +
            "  and pcanc.TIPOPARCELACANCELAMENTO = 'PARCELAS_EM_ABERTO'";
        return (IdETipoCalculoParcelaOriginalRowMapper) getJdbcTemplate().queryForObject(sql, new Object[]{idParcela}, new IdETipoCalculoParcelaOriginalRowMapper());
    }

    public void persisteCertidao(List<CertidaoDividaAtiva> certidoes) {
        String sql = "INSERT INTO CERTIDAODIVIDAATIVA " +
            "(ID, NUMERO, DATACERTIDAO, " +
            " FUNCIONARIOASSINANTE_ID, FUNCIONARIOEMISSAO_ID, " +
            " EXERCICIO_ID, SITUACAOCERTIDAODA, CADASTRO_ID, PESSOA_ID, SITUACAOJUDICIAL) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new CertidaoDividaAtivaSetter(certidoes, singletonGeradorId.getIds(certidoes.size())));
        for (CertidaoDividaAtiva certidao : certidoes) {
            sql = "INSERT INTO ITEMCERTIDAODIVIDAATIVA " +
                "(ID, CERTIDAO_ID, ITEMINSCRICAODIVIDAATIVA_ID) " +
                "VALUES (?,?,?)";
            getJdbcTemplate().batchUpdate(sql, new ItemCertidaoDividaAtivaSetter(certidao.getItensCertidaoDividaAtiva(), singletonGeradorId.getIds(certidao.getItensCertidaoDividaAtiva().size())));
        }
    }

    public void persisteItemInscricaoDividaAtiva(List<ItemInscricaoDividaAtiva> itens) {
        String sql = "INSERT INTO CALCULO " +
            "(ID, DATACALCULO, SIMULACAO, VALORREAL, VALOREFETIVO, ISENTO, CADASTRO_ID, SUBDIVIDA, TIPOCALCULO, CONSISTENTE, PROCESSOCALCULO_ID, REFERENCIA) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new CalculoDividaAtivaSetter(itens, singletonGeradorId.getIds(itens.size())));

        sql = "INSERT INTO ITEMINSCRICAODIVIDAATIVA " +
            "(ID, INSCRICAODIVIDAATIVA_ID, DIVIDA_ID, PESSOA_ID, SITUACAO, EXERCICIO_ID) " +
            "VALUES (?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new ItemInscricaoDividaAtivaSetter(itens));

        for (ItemInscricaoDividaAtiva item : itens) {
            sql = "INSERT INTO INSCRICAODIVIDAPARCELA " +
                "(ID, ITEMINSCRICAODIVIDAATIVA_ID, PARCELAVALORDIVIDA_ID) " +
                "VALUES (?,?,?)";
            getJdbcTemplate().batchUpdate(sql, new ItemInscricaoParcelaDividaAtivaSetter(item.getItensInscricaoDividaParcelas(), singletonGeradorId.getIds(item.getItensInscricaoDividaParcelas().size())));

            sql = "INSERT INTO SITUACAOPARCELAVALORDIVIDA" +
                "(ID, DATALANCAMENTO, SITUACAOPARCELA, PARCELA_ID, SALDO, REFERENCIA) " +
                "VALUES (?,?,?,?,?,?)";
            getJdbcTemplate().batchUpdate(sql, new SituacaoParcelaValorDividaSetter(item.getItensInscricaoDividaParcelas(), SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA, singletonGeradorId.getIds(item.getItensInscricaoDividaParcelas().size())));
            for (InscricaoDividaParcela parc : item.getItensInscricaoDividaParcelas()) {

                sql = "INSERT INTO SITUACAOPARCELAVALORDIVIDA " +
                    "(ID, DATALANCAMENTO, SITUACAOPARCELA, PARCELA_ID, SALDO, REFERENCIA) " +
                    " SELECT hibernate_sequence.nextval, sysdate, 'CANCELAMENTO', PVD.ID, 0 , SPVD.REFERENCIA " +
                    "FROM parcelavalordivida pvd " +
                    " INNER JOIN SITUACAOPARCELAVALORDIVIDA SPVD ON SPVD.id = PVD.SITUACAOATUAL_ID " +
                    " INNER JOIN opcaopagamento op ON op.id = pvd.OPCAOPAGAMENTO_ID AND op.PROMOCIONAL = 1 " +
                    "WHERE pvd.VALORDIVIDA_ID = (SELECT valordivida_id FROM parcelavalordivida WHERE id  = ?)";
                getJdbcTemplate().update(sql, new Object[]{parc.getIdParcela()});
            }
            sql = "INSERT INTO CALCULOPESSOA " +
                "(ID, CALCULO_ID, PESSOA_ID) " +
                "VALUES (?,?,?)";
            getJdbcTemplate().batchUpdate(sql, new CalculoPessoaSetter(item.getPessoas(), singletonGeradorId.getIds(item.getPessoas().size())));

            persisteValorDivida(item.getValorDivida());
        }
    }


    public void persisteLivroDividaAtiva(LivroDividaAtiva livro) {
        String sql = "INSERT INTO LIVRODIVIDAATIVA " +
            "(ID, NUMERO, TOTALPAGINAS, EXERCICIO_ID, TIPOCADASTROTRIBUTARIO, SEQUENCIA, TIPOORDENACAO) " +
            "VALUES (?,?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new LivroSetter(livro, singletonGeradorId.getIds(1)));

        persisteItemLivroDividaAtiva(livro);
    }

    public void mergeLivroDividaAtiva(LivroDividaAtiva livro) {
        String sql = "UPDATE LIVRODIVIDAATIVA SET " +
                " TOTALPAGINAS = ? " +
                " WHERE ID = ? ";
        getJdbcTemplate().update(sql, livro.getTotalPaginas(), livro.getId());

        persisteItemLivroDividaAtiva(livro);
    }

    public void mergeReferenciaSituacaoParcelaValorDivida(Long idSituacao, String referencia) {
        String sql = "UPDATE SITUACAOPARCELAVALORDIVIDA SET " +
            " REFERENCIA = ? " +
            " WHERE ID = ? ";
        getJdbcTemplate().update(sql, referencia, idSituacao);
    }

    public void mergeDataPrescicaoParcelaValorDivida(Long idParcela, Date dataPrescricao) {
        String sql = "UPDATE PARCELAVALORDIVIDA SET " +
            " DATAPRESCRICAO = ? " +
            " WHERE ID = ? ";
        getJdbcTemplate().update(sql, dataPrescricao, idParcela);
    }

    public void persisteItemLivroDividaAtiva(LivroDividaAtiva livro) {
        List<ItemLivroDividaAtiva> itens = Lists.newArrayList();
        for (ItemLivroDividaAtiva item : livro.getItensLivros()) {
            if (item.getId() == null) {
                itens.add(item);
            }
        }

        String sql = "INSERT INTO ITEMLIVRODIVIDAATIVA " +
            "(ID, LIVRODIVIDAATIVA_ID, INSCRICAODIVIDAATIVA_ID, PROCESSADO) " +
            "VALUES (?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new ItemLivroSetter(itens, singletonGeradorId.getIds(itens.size())));

        for (ItemLivroDividaAtiva item : livro.getItensLivros()) {
            persisteLinhaLivroDividaAtiva(item);
        }
    }

    public void persisteLinhaLivroDividaAtiva(ItemLivroDividaAtiva item) {
        String sql = "INSERT INTO TERMOINSCRICAODIVIDAATIVA " +
            "(ID, NUMERO) " +
            "VALUES (?,?)";
        getJdbcTemplate().batchUpdate(sql, new TermoInscricaoSetter(item.getLinhasDoLivro(), singletonGeradorId.getIds(item.getLinhasDoLivro().size())));

        sql = "INSERT INTO LINHADOLIVRODIVIDAATIVA " +
            "(ID, SEQUENCIA, PAGINA, NUMERODALINHA, ITEMINSCRICAODIVIDAATIVA_ID, ITEMLIVRODIVIDAATIVA_ID, TERMOINSCRICAODIVIDAATIVA_ID) " +
            "VALUES (?,?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new LinhaLivroSetter(item.getLinhasDoLivro(), singletonGeradorId.getIds(item.getLinhasDoLivro().size())));
    }

    public void persisteValorDivida(ValorDivida valorDivida) {
        persisteValorDivida(valorDivida, true);
    }

    public void persisteValorDivida(ValorDivida valorDivida, boolean gerarReferencia) {
        String sql = "INSERT INTO VALORDIVIDA" +
            "(ID, EMISSAO, VALOR, DIVIDA_ID, EXERCICIO_ID, CALCULO_ID) " +
            " VALUES(?,?,?,?,?,?) ";
        getJdbcTemplate().batchUpdate(sql, new ValorDividaSetter(valorDivida, singletonGeradorId.getProximoId()));
        persisteItemValorDivida(valorDivida.getItemValorDividas());
        persisteParcelaValorDivida(valorDivida.getParcelaValorDividas(), gerarReferencia);
    }

    public void persisteItemValorDivida(List<ItemValorDivida> itens) {
        String sql = "INSERT INTO ITEMVALORDIVIDA" +
            "(ID, VALOR, TRIBUTO_ID, VALORDIVIDA_ID, ISENTO, IMUNE) " +
            " VALUES(?,?,?,?,?,?) ";
        getJdbcTemplate().batchUpdate(sql, new ItemValorDividaSetter(itens, singletonGeradorId.getIds(itens.size())));
    }

    public void persisteParcelaValorDivida(List<ParcelaValorDivida> parcelas, boolean gerarReferencia) {
        String sql = "INSERT INTO PARCELAVALORDIVIDA" +
            "(ID, OPCAOPAGAMENTO_ID, VALORDIVIDA_ID, VENCIMENTO, PERCENTUALVALORTOTAL, DATAREGISTRO, SEQUENCIAPARCELA, DIVIDAATIVA, DIVIDAATIVAAJUIZADA, VALOR, DATAPRESCRICAO ) " +
            " VALUES(?,?,?,?,?,?,?,?,?,?,?) ";
        getJdbcTemplate().batchUpdate(sql, new ParcelaValorDividaSetter(parcelas, singletonGeradorId.getIds(parcelas.size())));
        for (ParcelaValorDivida parcela : parcelas) {
            persisteItemParcelaValorDivida(parcela.getItensParcelaValorDivida());
        }
        persisteSituacaoParcelaValorDivida(parcelas, gerarReferencia);
    }

    public void persisteItemParcelaValorDivida(List<ItemParcelaValorDivida> itens) {
        String sql = "INSERT INTO ITEMPARCELAVALORDIVIDA" +
            "(ID, PARCELAVALORDIVIDA_ID, ITEMVALORDIVIDA_ID, VALOR) " +
            " VALUES(?,?,?,?) ";
        getJdbcTemplate().batchUpdate(sql, new ItemParcelaValorDividaSetter(itens, singletonGeradorId.getIds(itens.size())));
    }

    public void persisteSituacaoParcelaValorDivida(List<ParcelaValorDivida> parcelas, boolean gerarReferencia) {
        String sql = "INSERT INTO SITUACAOPARCELAVALORDIVIDA" +
            "(ID, DATALANCAMENTO, SITUACAOPARCELA, PARCELA_ID, SALDO, REFERENCIA ) " +
            " VALUES(?,?,?,?,?,?) ";
        getJdbcTemplate().batchUpdate(sql, new SituacaoParcelaValorDividaSetter(parcelas, SituacaoParcela.EM_ABERTO, singletonGeradorId.getIds(parcelas.size()), gerarReferencia));
    }

    public void persistirSituacaoParcelaValorDividaComMesmaReferencia(Long parcela, SituacaoParcela situacaoParcela, Date data) {
        String sql = "INSERT INTO situacaoparcelavalordivida " +
            " (id, datalancamento, situacaoparcela, parcela_id, saldo, inconsistente, referencia) " +
            " VALUES (hibernate_sequence.nextval, ?, ?, ?, 0, 0, " +
            "(SELECT referencia FROM situacaoparcelavalordivida spvd INNER JOIN parcelavalordivida pvd ON pvd.situacaoatual_id = spvd.id WHERE pvd.id = ?)) ";
        getJdbcTemplate().update(sql, new Object[]{data, situacaoParcela.name(), parcela, parcela});

    }

}
