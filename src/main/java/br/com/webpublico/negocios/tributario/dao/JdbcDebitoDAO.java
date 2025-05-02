package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class JdbcDebitoDAO extends NamedParameterJdbcDaoSupport {

    @Autowired
    public JdbcDebitoDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public static JdbcDebitoDAO getInstance() {
        return (JdbcDebitoDAO) Util.getSpringBeanPeloNome("jdbcDebitoDAO");
    }

    public List<Object[]> buscarDados(Integer ano) {
        String sql = "select " +
            "  div.codigo as tributo, " +
            "  case when cal.cadastro_id is not null then '1'  " +
            "    when pf.id is not null then '2' " +
            "    when pj.id is not null then '3' " +
            "  end as tipodainscricao, " +
            "  coalesce(ci.inscricaoCadastral, ce.inscricaocadastral, cast(pes.id as varchar(20))) as matricula, " +
            "  ex.ano as ano, " +
            "  pvd.sequenciaparcela as parcela, " +
            "  pvd.vencimento as vencimento, " +
            "  (select sum(ipvd.valor) from itemparcelavalordivida ipvd " +
            "    inner join parcelavalordivida pvd2 on ipvd.parcelavalordivida_id = pvd2.id " +
            "    inner join itemvalordivida ivd on ipvd.itemvalordivida_id = ivd.id " +
            "    inner join tributo tr on ivd.tributo_id = tr.id " +
            "    inner join situacaoparcelavalordivida spvd2 on pvd2.situacaoatual_id = spvd2.id " +
            "    where pvd2.id = pvd.id " +
            "      and tr.tipotributo in ('IMPOSTO','TAXA')) as valorprincipal, " +
            "  0 as valorjuros, " +
            "  0 as valormulta, " +
            "  0 as valorhonorario, " +
            "  0 as valorcorrecao, " +
            "  spvd.situacaoparcela as situacaododebito, " +
            "  case when cal.cadastro_id is null then '3' " +
            "       when ci.id is not null then '2' " +
            "       when ce.id is not null then '1' " +
            "   end as origemdodebito, " +
            "  replace(replace(replace(coalesce(pf.cpf, pj.cnpj, ''), '.', ''),'-', ''), '/', '') as cpfcnpjcontribuinte, " +
            "  spvd.situacaoparcela as statusparcela, " +
            "  pvd.id as idParcela, " +
            "  cal.tipoCalculo as tipoCalculo, " +
            "  dva.ACRESCIMO_ID, " +
            "  ex.ano as exercicio, " +
            "  pvd.dividaAtivaAjuizada as ajuizada, " +
            "  trunc(cal.dataCalculo) as dataCalculo, " +
            "  div.id as idDivida, " +
            "  cal.id as idCalculo, " +
            "  pvd.debitoProtestado as protestado " +
            " from parcelavalordivida pvd " +
            "  inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "  inner join calculo cal on cal.id = vd.calculo_id " +
            "  inner join divida div on div.id = vd.divida_id " +
            "  inner join exercicio ex on ex.id = vd.exercicio_id " +
            "  inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "  left join cadastroimobiliario ci on ci.id = cal.cadastro_id " +
            "  left join cadastroeconomico ce on ce.id = cal.cadastro_id " +
            "  inner join calculopessoa cp on cp.calculo_id = cal.id and cp.id = (select max(cp2.id) from calculopessoa cp2 where cp2.calculo_id = cal.id) " +
            "  inner join pessoa pes on pes.id = cp.pessoa_id " +
            "  left join pessoafisica pf on pf.id = pes.id " +
            "  left join pessoajuridica pj on pj.id = pes.id " +
            "  inner join dividaacrescimo dva on dva.divida_id = vd.divida_id AND CURRENT_DATE BETWEEN DVA.INICIOVIGENCIA AND COALESCE(DVA.FINALVIGENCIA, CURRENT_DATE) " +
            " where ex.ano = :ano " +
            "   and cal.tipocalculo = :tipoDividaAtiva " +
            "   and spvd.SITUACAOPARCELA in (:situacoes) " +
            "   and pvd.DIVIDAATIVA = :dividaAtiva " +
            "   and not REGEXP_LIKE(coalesce(ci.inscricaoCadastral, ce.inscricaocadastral, cast(pes.id as varchar(20))), '[a-z]') ";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("ano", ano);
        mapSqlParameterSource.addValue("tipoDividaAtiva",
            Calculo.TipoCalculo.INSCRICAO_DA.name());
        mapSqlParameterSource.addValue("situacoes",
            SituacaoParcela.getSituacoesParaExportacaoDebitosGeraisArquivoBI()
                .stream().map(Enum::name).collect(Collectors.toList()));
        mapSqlParameterSource.addValue("dividaAtiva", 1);
        return getNamedParameterJdbcTemplate().query(sql, mapSqlParameterSource, (resultSet, i) -> {
            ResultSetMetaData metaData = resultSet.getMetaData();
            Object[] obj = new Object[metaData.getColumnCount()];
            for (int index = 1; index <= metaData.getColumnCount(); index++){
                obj[index-1] = resultSet.getObject(index);
            }
            return obj;
        });
    }
}
