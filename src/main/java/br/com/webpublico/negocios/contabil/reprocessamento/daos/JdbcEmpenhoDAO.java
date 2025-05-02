package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.EmpenhoSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.repositorios.jdbc.util.JDBCUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;


@Repository
public class JdbcEmpenhoDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcEmpenhoDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persistir(Empenho empenho) {
        List<Empenho> empenhos = Lists.newArrayList();
        empenhos.add(empenho);
        getJdbcTemplate().batchUpdate(EmpenhoSetter.SQL_INSERT, new EmpenhoSetter(empenhos, geradorDeIds));
    }

    public void atualizar(Empenho emp) {
        Object[] objetos = new Object[54];

        objetos[0] = emp.getDataEmpenho();
        objetos[1] = emp.getValor();
        objetos[2] = emp.getSaldo();
        objetos[3] = emp.getDespesaORC().getId();
        objetos[4] = emp.getFonteDespesaORC().getId();
        objetos[5] = emp.getUsuarioSistema() != null ? emp.getUsuarioSistema().getId() : null;
        objetos[6] = emp.getUnidadeOrganizacional() != null ? emp.getUnidadeOrganizacional().getId() : null;
        objetos[7] = emp.getHistoricoContabil() != null ? emp.getHistoricoContabil().getId() : null;
        objetos[8] = emp.getFornecedor() != null ? emp.getFornecedor().getId() : null;
        objetos[9] = emp.getMovimentoDespesaORC() != null ? emp.getMovimentoDespesaORC().getId() : null;
        objetos[10] = emp.getNumero();
        objetos[11] = emp.getComplementoHistorico();
        objetos[12] = emp.getTipoEmpenho().name();
        objetos[13] = emp.getSolicitacaoEmpenho() != null ? emp.getSolicitacaoEmpenho().getId() : null;
        objetos[14] = emp.getCategoriaOrcamentaria().name();
        objetos[15] = emp.getEmpenho() != null ? emp.getEmpenho().getId() : null;
        objetos[16] = emp.getExercicio() != null ? emp.getExercicio().getId() : null;
        objetos[17] = emp.getNumeroOriginal();
        objetos[18] = emp.getExercicioOriginal() != null ? emp.getExercicioOriginal().getId() : null;
        objetos[19] = emp.getImportado();
        objetos[20] =  emp.getDividaPublica() != null ? emp.getDividaPublica().getId() : null;
        objetos[21] =  emp.getConvenioDespesa() != null ? emp.getConvenioDespesa().getId() : null;
        objetos[22] =  emp.getConvenioReceita() != null ? emp.getConvenioReceita().getId() : null;
        objetos[23] =  emp.getTipoEmpenhoIntegracao() != null ? emp.getTipoEmpenhoIntegracao().name() : null;
        objetos[24] =  emp.getClasseCredor() != null ? emp.getClasseCredor().getId() : null;
        objetos[25] =  emp.getEventoContabil() != null ? emp.getEventoContabil().getId() : null;
        objetos[26] =  emp.getFolhaDePagamento() != null ? emp.getFolhaDePagamento().getId() : null;
        objetos[27] =  emp.getPropostaConcessaoDiaria() != null ? emp.getPropostaConcessaoDiaria().getId() : null;
        objetos[28] =  emp.getUnidadeOrganizacionalAdm() != null ? emp.getUnidadeOrganizacionalAdm().getId() : null;
        objetos[29] =  emp.getSaldoDisponivel();
        objetos[30] =  emp.getContrato() != null ? emp.getContrato().getId() : null;
        objetos[31] =  emp.getTipoContaDespesa() != null ? emp.getTipoContaDespesa().name() : null;
        objetos[32] =  emp.getHistoricoNota();
        objetos[33] =  emp.getHistoricoRazao();
        objetos[34] =  emp.getTipoRestosProcessados() != null ? emp.getTipoRestosProcessados().name() : null;
        objetos[35] =  emp.getTipoRestosInscritos() != null ? emp.getTipoRestosInscritos().name() : null;
        objetos[36] =  emp.getSubTipoDespesa() != null ? emp.getSubTipoDespesa().name() : null;
        objetos[37] =  emp.getSemDisponibilidadeFinanceira();
        objetos[38] =  emp.getTransportado();
        objetos[39] =  emp.getModalidadeLicitacao() != null ? emp.getModalidadeLicitacao().name() : null;
        objetos[40] =  emp.getParcelaDividaPublica() != null ? emp.getParcelaDividaPublica().getId() : null;
        objetos[41] =  emp.getTipoReconhecimento() != null ? emp.getTipoReconhecimento().name() : null;
        objetos[42] =  emp.getSaldoObrigacaoPagarAntesEmp();
        objetos[43] =  emp.getSaldoObrigacaoPagarDepoisEmp();
        objetos[44] =  emp.getOperacaoDeCredito() != null ? emp.getOperacaoDeCredito().getId() : null;
        objetos[45] =  emp.getExtensaoFonteRecurso() != null ? emp.getExtensaoFonteRecurso().getId() : null;
        objetos[46] =  emp.getContaDespesa() != null ? emp.getContaDespesa().getId() : null;
        objetos[47] =  emp.getFonteDeRecursos() != null ? emp.getFonteDeRecursos().getId() : null;
        objetos[48] =  emp.getContaDeDestinacao() != null ? emp.getContaDeDestinacao().getId() : null;
        objetos[49] =  emp.getCodigoContaTCE();
        objetos[50] =  emp.getVersao();
        objetos[51] =  emp.getReconhecimentoDivida() != null ? emp.getReconhecimentoDivida().getId() : null;
        objetos[52] =  emp.getExecucaoProcesso() != null ? emp.getExecucaoProcesso().getId() : null;
        objetos[53] = emp.getId();

        getJdbcTemplate().update(EmpenhoSetter.SQL_UPDATE, objetos);
    }
}
