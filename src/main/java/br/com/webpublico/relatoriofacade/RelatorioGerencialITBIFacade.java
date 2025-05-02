package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.CalculoITBI;
import br.com.webpublico.enums.SituacaoITBI;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.entidadesauxiliares.VORelatorioITBI;
import br.com.webpublico.entidadesauxiliares.VORelatorioITBIPessoa;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Criado por Mateus
 * Data: 18/04/2017.
 */
@Stateless
public class RelatorioGerencialITBIFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Asynchronous
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public Future<List<VORelatorioITBI>> buscarItbs(String filtros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select itbi.sequencia as numero, ");
        sql.append("       ex.ano as exercicio, ");
        sql.append("       itbi.datalancamento, ");
        sql.append("       coalesce(imob.inscricaoCadastral, cast(cr.codigo as varchar(255))) as imovel, ");
        sql.append("       itbi.situacao, ");
        sql.append("       itbi.baseCalculo, ");
        sql.append("       calc.valorReal, ");
        sql.append("       calc.id as calculoId, ");
        sql.append("       itbi.id as itbiId, ");
        sql.append("       laudo.dataImpressaoLaudo as dataImpressao, ");
        sql.append("       pf.nome as usuario ");
        sql.append("  from CalculoITBI itbi ");
        sql.append(" inner join calculo calc on itbi.id = calc.id ");
        sql.append("  left join cadastroimobiliario imob on itbi.cadastroImobiliario_id = imob.id ");
        sql.append("  left join lote lote on imob.lote_id = lote.id ");
        sql.append("  left join setor setor on setor.id = lote.setor_id ");
        sql.append("  left join quadra quadra on quadra.id = lote.quadra_id ");
        sql.append("  left join CadastroRural cr on itbi.cadastrorural_id = cr.id ");
        sql.append("  left join LaudoAvaliacaoITBI laudo on laudo.calculoitbi_id = itbi.id ");
        sql.append("  left join UsuarioSistema usu on usu.id = laudo.usuarioImpressaoLaudo_id ");
        sql.append("  left join PessoaFisica pf on pf.id = usu.pessoaFisica_id ");
        sql.append(" inner join exercicio ex on itbi.exercicio_id = ex.id ");
        sql.append(filtros);
        sql.append(" order by dataLancamento, itbi.sequencia ");
        Query q = em.createNativeQuery(sql.toString());
        List<VORelatorioITBI> retorno = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                VORelatorioITBI voRelatorioITBI = new VORelatorioITBI();
                voRelatorioITBI.setNumeroITBI(((BigDecimal) obj[0]).intValue());
                voRelatorioITBI.setAnoExercicio(((BigDecimal) obj[1]).intValue());
                voRelatorioITBI.setDataLancamento(obj[2] != null ? (Date) obj[2] : null);
                voRelatorioITBI.setImovel((String) obj[3]);
                voRelatorioITBI.setSituacao(SituacaoITBI.valueOf((String) obj[4]).getDescricao());
                voRelatorioITBI.setBaseCalculo((BigDecimal) obj[5]);
                voRelatorioITBI.setValorITBI((BigDecimal) obj[6]);
                voRelatorioITBI.setAdquirentes(buscarAdquirentesOrTransmitentes((BigDecimal) obj[8], false));
                voRelatorioITBI.setTransmitentes(buscarAdquirentesOrTransmitentes((BigDecimal) obj[8], true));
                voRelatorioITBI.setValorPago(recuperarValorPago(voRelatorioITBI.getAdquirentes().get(0).getPessoaId(), ((BigDecimal) obj[7]).longValue()));
                voRelatorioITBI.setDataEmissaoLaudo(obj[9] != null ? (Date) obj[9] : null);
                voRelatorioITBI.setUsuarioLaudo(obj[10] != null ? (String) obj[10] : "");
                retorno.add(voRelatorioITBI);
            }
        }
        return new AsyncResult<>(retorno);
    }

    public BigDecimal recuperarValorPago(Long primeiroAdquirenteId, Long calculoId) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculoId);
        if (primeiroAdquirenteId != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, primeiroAdquirenteId);
        }
        BigDecimal resultado = BigDecimal.ZERO;
        for (ResultadoParcela p : consultaParcela.executaConsulta().getResultados()) {
            resultado = resultado.add(p.getValorPago());
        }
        return resultado;
    }

    public List<VORelatorioITBIPessoa> buscarAdquirentesOrTransmitentes(BigDecimal idItbi, Boolean buscarTransmitente) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpfCnpjAdquirente, ");
        sql.append("        coalesce(pf.nome, pj.razaosocial) as nomeAdquirente, ");
        sql.append("        pes.id as pessoaId ");
        if (buscarTransmitente) {
            sql.append("   from TransmitentesCalculoITBI transmi ");
            sql.append("  inner join pessoa pes on transmi.pessoa_id = pes.id ");
        } else {
            sql.append("   from AdquirentesCalculoITBI adquirentes");
            sql.append("  inner join pessoa pes on adquirentes.adquirente_id = pes.id ");
        }
        sql.append("   left join pessoafisica pf on pes.id = pf.id ");
        sql.append("   left join pessoajuridica pj on pes.id = pj.id ");
        sql.append(buscarTransmitente ? " where transmi.calculoitbi_id = :idItbi " : " where adquirentes.calculoitbi_id = :idItbi ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idItbi", idItbi);
        List<VORelatorioITBIPessoa> retorno = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                VORelatorioITBIPessoa voRelatorioITBIPessoa = new VORelatorioITBIPessoa();
                voRelatorioITBIPessoa.setNome((String) obj[0] + " - " + (String) obj[1]);
                voRelatorioITBIPessoa.setPessoaId(((BigDecimal) (obj[2])).longValue());
                retorno.add(voRelatorioITBIPessoa);
            }
        }
        return retorno;
    }
}
