package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.CalculoISS;
import br.com.webpublico.entidades.CalculoPessoa;
import br.com.webpublico.entidades.ItemCalculoIss;
import br.com.webpublico.entidades.OcorrenciaProcessoCalculoGeralIssFixo;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.negocios.tributario.singletons.SingletonLancamentoGeralISSFixo;
import br.com.webpublico.negocios.tributario.setter.CalculoPessoaSetter;
import br.com.webpublico.negocios.tributario.setter.CalculoSetter;
import br.com.webpublico.negocios.tributario.setter.CalculoIssSetter;
import br.com.webpublico.negocios.tributario.setter.ItemCalculoIssSetter;
import br.com.webpublico.negocios.tributario.setter.OcorrenciaProcessoCalculoGeralIssFixoSetter;
import br.com.webpublico.negocios.tributario.setter.OcorrenciaSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Repository(value = "persisteCalculoGeralISSFixo")
public class JdbcCalculoGeralISSFixoDao extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    private JdbcCalculoISSDao jdbcCalculoISSDao;

    @Autowired
    public JdbcCalculoGeralISSFixoDao(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public synchronized void salvar(List<CalculoISS> calculos) {
        if (!SingletonLancamentoGeralISSFixo.getInstance().podeLancar()) {
            return;
        }

        jdbcCalculoISSDao.salvar(calculos);
        SingletonLancamentoGeralISSFixo.getInstance().adicionarOcorrenciasDeSucesso(calculos);
    }

    public synchronized void salvarOcorrencias(List<OcorrenciaProcessoCalculoGeralIssFixo> ocorrenciasProcesso) throws Exception {
        if (!SingletonLancamentoGeralISSFixo.getInstance().podeLancar()
                && !SingletonLancamentoGeralISSFixo.getInstance().todosCadastrosForamProcessados()) {
            return;
        }

        String sql = "INSERT INTO OCORRENCIA " +
                "(ID, CONTEUDO, DATAREGISTRO, NIVELOCORRENCIA, TIPOOCORRENCIA, DETALHESTECNICOS) " +
                "VALUES (?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new OcorrenciaSetter(ocorrenciasProcesso, geradorDeIds));

        sql = "INSERT INTO OCORREPROCCALCGERALISSFIXO " +
                "(ID, PROCESSOCALCULOGERAL_ID, CADASTROECONOMICO_ID, CALCULOISS_ID, OCORRENCIA_ID) " +
                "VALUES (?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new OcorrenciaProcessoCalculoGeralIssFixoSetter(ocorrenciasProcesso));
    }
}
