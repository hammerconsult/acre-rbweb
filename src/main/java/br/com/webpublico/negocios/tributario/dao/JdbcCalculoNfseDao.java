package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.tributario.setter.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.negocios.tributario.singletons.SingletonLancamentoGeralISSFixo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Repository(value = "calculoNfseDao")
public class JdbcCalculoNfseDao extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcCalculoNfseDao(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void salvar(ProcessoCalculoNfse processoCalculoNfse) {
        try {
            processoCalculoNfse = salvarProcesso(processoCalculoNfse);

            List<CalculoNfse> calculos = (List<CalculoNfse>) processoCalculoNfse.getCalculos();

            String sql = "INSERT INTO CALCULO " +
                "(ID, DATACALCULO, SIMULACAO, VALORREAL, VALOREFETIVO, ISENTO, CADASTRO_ID, SUBDIVIDA, TIPOCALCULO, CONSISTENTE, PROCESSOCALCULO_ID, REFERENCIA, ISENTAACRESCIMOS) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            getJdbcTemplate().batchUpdate(sql, new CalculoSetter(calculos, geradorDeIds));

            sql = "INSERT INTO CALCULONFSE " +
                "(ID, PROCESSOCALCULO_ID, IDENTIFICACAODAGUIA, MESDEREFERENCIA, ANODEREFERENCIA, DATAVENCIMENTODEBITO, VALORTOTALDODEBITO, VALORDAMULTA, VALORDOSJUROS, VALORDACORRECAO, TIPODOMOVIMENTO, DATADOMOVIMENTO, VALORTOTALPAGO, NOSSONUMERO, VENCIMENTODAM) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            getJdbcTemplate().batchUpdate(sql, new CalculoNfseSetter(calculos));

            for (CalculoNfse calculo : calculos) {
                sql = "INSERT INTO ITEMCALCULONFSE " +
                    "(ID, CALCULONFSE_ID, TRIBUTO_ID) " +
                    "VALUES (?,?,?)";
                getJdbcTemplate().batchUpdate(sql, new ItemCalculoNfseSetter(calculo.getItensCalculo()));

                sql = "INSERT INTO CALCULOPESSOA " +
                    "(ID, CALCULO_ID, PESSOA_ID) " +
                    "VALUES (?,?,?)";
                getJdbcTemplate().batchUpdate(sql, new CalculoPessoaSetter(calculo.getPessoas(), geradorDeIds.getIds(calculo.getPessoas().size())));
            }


        } catch (Exception e) {
            throw e;
        }
    }


    public ProcessoCalculoNfse salvarProcesso(ProcessoCalculoNfse processo) {
        String sql = "INSERT INTO PROCESSOCALCULO " +
            "(ID, EXERCICIO_ID, DIVIDA_ID, DATALANCAMENTO, DESCRICAO, USUARIOSISTEMA_ID, NUMEROPROTOCOLO, ANOPROTOCOLO) " +
            "VALUES (?,?,?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new ProcessoCalculoSetter(processo, geradorDeIds));

        sql = "INSERT INTO PROCESSOCALCULONFSE " +
            "(ID) " +
            "VALUES (?)";
        getJdbcTemplate().batchUpdate(sql, new ProcessoCalculoNfseSetter(processo));
        return processo;
    }
}
