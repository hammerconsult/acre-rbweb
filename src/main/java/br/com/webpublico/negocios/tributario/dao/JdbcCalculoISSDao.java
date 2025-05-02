package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.tributario.setter.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Repository(value = "calculoISSDao")
public class JdbcCalculoISSDao extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcCalculoISSDao(DataSource dataSource) {
        setDataSource(dataSource);
    }


    public ProcessoCalculoISS salvar(ProcessoCalculoISS processo) {
        String sql = "INSERT INTO PROCESSOCALCULO " +
            "(ID, EXERCICIO_ID, DIVIDA_ID, DATALANCAMENTO, DESCRICAO, USUARIOSISTEMA_ID, NUMEROPROTOCOLO, ANOPROTOCOLO) " +
            "VALUES (?,?,?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new ProcessoCalculoSetter(processo, geradorDeIds));

        sql = "INSERT INTO PROCESSOCALCULOISS " +
            "(ID, MESREFERENCIA) " +
            "VALUES (?,?)";
        getJdbcTemplate().batchUpdate(sql, new ProcessoCalculoISSSetter(processo));
        salvar(processo.getCalculos());
        return processo;
    }

    public void salvar(List<CalculoISS> calculos) {

        String sql = "INSERT INTO CALCULO " +
            "(ID, DATACALCULO, SIMULACAO, VALORREAL, VALOREFETIVO, ISENTO, CADASTRO_ID, SUBDIVIDA, TIPOCALCULO, CONSISTENTE, PROCESSOCALCULO_ID, REFERENCIA, ISENTAACRESCIMOS) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new CalculoSetter(calculos, geradorDeIds));

        sql = "INSERT INTO CALCULOISS " +
            "(ID, PROCESSOCALCULOISS_ID, CADASTROECONOMICO_ID, TIPOCALCULOISS, ALIQUOTA, BASECALCULO, VALORCALCULADO, FATURAMENTO, " +
            "  SEQUENCIALANCAMENTO, TAXASOBREISS, AUSENCIAMOVIMENTO, TIPOSITUACAOCALCULOISS, USUARIOLANCAMENTO_ID, NOTAELETRONICA, ISSQNFMTIPOLANCAMENTONFSE) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new CalculoIssSetter(calculos));

        sql = "INSERT INTO ITEMCALCULOISS " +
            "(ID, SERVICO_ID, CALCULO_ID, ALIQUOTA, BASECALCULO, FATURAMENTO, TRIBUTO_ID, VALORCALCULADO) " +
            "VALUES (?,?,?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new ItemCalculoIssSetter(getItensCalculoIss(calculos)));

        sql = "INSERT INTO CALCULOPESSOA " +
            "(ID, CALCULO_ID, PESSOA_ID) " +
            "VALUES (?,?,?)";

        getJdbcTemplate().batchUpdate(sql, new CalculoPessoaSetter(getCalculosPessoa(calculos), geradorDeIds.getIds(calculos.size())));

    }

    private List<CalculoPessoa> getCalculosPessoa(List<CalculoISS> calculos) {
        List<CalculoPessoa> retorno = new ArrayList<>();
        for (CalculoISS calculo : calculos) {
            retorno.addAll(calculo.getPessoas());
        }
        return retorno;
    }

    private List<ItemCalculoIss> getItensCalculoIss(List<CalculoISS> calculos) {
        List<ItemCalculoIss> retorno = new ArrayList<>();
        for (CalculoISS calculo : calculos) {
            retorno.addAll(calculo.getItemCalculoIsss());
        }
        return retorno;
    }
}
