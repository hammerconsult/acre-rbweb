package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoProcessoCalculoGeralIssFixo;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 30/07/13
 * Time: 18:08
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class EfetivacaoIssFixoGeralFacade extends AbstractFacade<EfetivacaoProcessoIssFixoGeral> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EfetivacaoIssFixoGeralValorDividaFacade efetivacaoIssFixoGeralValorDividaFacade;
    @EJB
    private CalculaISSFacade calculaISSFacade;
    @EJB
    private CalculaIssFixoGeralFacade calculaIssFixoGeralFacade;
    @EJB
    private PersisteValorDivida persisteValorDivida;

    public EfetivacaoIssFixoGeralFacade() {
        super(EfetivacaoProcessoIssFixoGeral.class);
    }

    protected EntityManager getEntityManager() {
        return em;
    }

    public CalculaIssFixoGeralFacade getCalculaIssFixoGeralFacade() {
        return calculaIssFixoGeralFacade;
    }

    public EfetivacaoIssFixoGeral recarregar(EfetivacaoIssFixoGeral entity) {
        EfetivacaoIssFixoGeral toReturn = em.find(EfetivacaoIssFixoGeral.class, entity.getId());
        toReturn.getListaDeProcessos().size();
        return toReturn;
    }

    public List<ProcessoCalculoGeralIssFixo> obterProcessosFiltrados(Exercicio exercicio, Date dataLancamentoInicial, Date dataLancamentoFinal) {
        String sql = montaQueryProcessosFiltrados(exercicio, dataLancamentoInicial, dataLancamentoFinal);
        Query q = criarEInicializarQueryProcessosFiltrados(exercicio, dataLancamentoInicial, dataLancamentoFinal, sql);

        return q.getResultList();
    }

    private Query criarEInicializarQueryProcessosFiltrados(Exercicio exercicio, Date dataLancamentoInicial, Date dataLancamentoFinal, String sql) {
        Query q = em.createNativeQuery(sql, ProcessoCalculoGeralIssFixo.class);
        q.setParameter("situacao", SituacaoProcessoCalculoGeralIssFixo.SIMULACAO.name());

        if (exercicio != null) {
            q.setParameter("exercicio", exercicio);
        }

        if (dataLancamentoInicial != null) {
            q.setParameter("datainicial", dataLancamentoInicial);
        }

        if (dataLancamentoFinal != null) {
            q.setParameter("datafinal", dataLancamentoFinal);
        }
        return q;
    }

    private String montaQueryProcessosFiltrados(Exercicio exercicio, Date dataLancamentoInicial, Date dataLancamentoFinal) {
        String sql = " select *" +
            "        from proccalcgeralissfixo p ";

        if (dataLancamentoInicial != null || dataLancamentoFinal != null) {
            sql += " inner join processocalculoiss piss on piss.id = p.processocalculoiss_id" +
                " inner join processocalculo pc on pc.id = piss.id ";
        }

        sql += "       where p.situacaoprocesso = :situacao " +
            "        and (select count(1) " +
            "                       from calculoiss calculoiss \n" +
            "                 inner join processocalculoiss processoiss on processoiss.id = calculoiss.processocalculoiss_id \n" +
            "                 inner join proccalcgeralissfixo processo on processo.processocalculoiss_id = processoiss.id \n" +
            "                      where processo.id = p.id) > 0 ";

        if (exercicio != null) {
            sql += " and p.exercicio_id = :exercicio";
        }

        if (dataLancamentoInicial != null) {
            sql += " and pc.datalancamento >= :datainicial";
        }

        if (dataLancamentoFinal != null) {
            sql += " and pc.datalancamento <= :datafinal";
        }

        sql += " order by p.id";

        return sql;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EfetivacaoIssFixoGeral efetivarProcessos(EfetivacaoIssFixoGeral efetivacao, List<BigDecimal> calculos) {
        try {
            for (BigDecimal id : calculos) {
                CalculoISS calculoISS = em.find(CalculoISS.class, id.longValue());
                calculoISS.setItemCalculoIsss(buscarItensCalculoIss(calculoISS));
                efetivacaoIssFixoGeralValorDividaFacade.geraDebito(calculoISS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return efetivacao;
    }

    private List<ItemCalculoIss> buscarItensCalculoIss(CalculoISS calculoISS) {
        return em.createQuery("select i from ItemCalculoIss i where i.calculo = :calculo")
            .setParameter("calculo", calculoISS)
            .getResultList();
    }

    public EfetivacaoIssFixoGeral salvarEfetivacao(EfetivacaoIssFixoGeral efetivacao) {
        return persisteValorDivida.salvarEfetivacao(efetivacao);
    }

    public List<ProcessoCalculoGeralIssFixo> obterProcessosEfetivados(EfetivacaoIssFixoGeral efetivacaoIssFixoGeral) {
        String sql = " select p.* " +
            "        from proccalcgeralissfixo p " +
            "  inner join efetprocissfixogeral ef on ef.processo_id = p.id " +
            "       where ef.efetivacao_id = :efetivacao_id";

        return em.createNativeQuery(sql, ProcessoCalculoGeralIssFixo.class).setParameter("efetivacao_id", efetivacaoIssFixoGeral.getId()).getResultList();
    }

    public List<String[]> obterInformacoesDosLancamentosEfetivados(ProcessoCalculoGeralIssFixo processo) {
        String sql = "               select case" +
            "                               when pf.id is not null then 'PF'" +
            "                               else 'PJ'" +
            "                           end as tipo," +
            "                           ce.inscricaocadastral," +
            "                           coalesce(pf.nome, pj.razaosocial)," +
            "                           coalesce(pf.cpf, pj.cnpj)," +
            "                           calc.valorcalculado" +
            "                      from proccalcgeralissfixo pgeral" +
            "                inner join EFETPROCISSFIXOGERAL efe on efe.processo_id = pgeral.id " +
            "                inner join processocalculoiss processoiss on processoiss.id = pgeral.processocalculoiss_id" +
            "                inner join calculoiss calc on calc.processocalculoiss_id = processoiss.id" +
            "                inner join cadastroeconomico ce on ce.id = calc.cadastroeconomico_id" +
            "                inner join valordivida vd on vd.calculo_id = calc.id" +
            "                 left join pessoajuridica pj on pj.id = ce.pessoa_id " +
            "                 left join pessoafisica pf on pf.id = ce.pessoa_id" +
            "                     where pgeral.id = :id_processo " +
            "                  order by 3";
        List lista = em.createNativeQuery(sql).setParameter("id_processo", processo.getId()).getResultList();
        List<String[]> retorno = new ArrayList<>();
        for (Object objeto : lista) {
            Object[] obj = (Object[]) objeto;
            String inscricaoCadastral = obj[1] != null ? obj[1].toString() : "";
            String nomeRazao = obj[2] != null ? obj[2].toString() : "";
            String cpfCnpj = "";
            try {
                cpfCnpj = calculaIssFixoGeralFacade.getCpfCnpjFormatado(obj[0].toString(), obj[3].toString());
            } catch (NullPointerException ex) {
                cpfCnpj = "";
            }
            String valor = obj[4] != null ? obj[4].toString() : "0";
            retorno.add(new String[]{inscricaoCadastral,
                nomeRazao,
                cpfCnpj,
                valor});
        }

        return retorno;
    }

    public List<String[]> obterInformacoesDosLancamentosNaoEfetivados(ProcessoCalculoGeralIssFixo p) {
        String sql = "                select case  " +
            "                                 when pf.id is not null then 'PF'  " +
            "                                 else 'PJ'  " +
            "                            end as tipo,  " +
            "                            cmc.inscricaocadastral, " +
            "                            coalesce(pf.nome, pj.razaosocial),  " +
            "                            coalesce(pf.cpf, pj.cnpj),  " +
            "                            ocorrencia.conteudo  " +
            "                       from efetprocissfixogeral efetivacaoprocesso  " +
            "                 inner join proccalcgeralissfixo processo on processo.id = efetivacaoprocesso.processo_id  " +
            "                 inner join ocorreefetissfixogeral ocorrenciaefetivacao on ocorrenciaefetivacao.efetivacaoprocesso_id = efetivacaoprocesso.id  " +
            "                 inner join ocorrencia ocorrencia on ocorrencia.id = ocorrenciaefetivacao.ocorrencia_id  " +
            "                 inner join calculo calculo on calculo.id = ocorrenciaefetivacao.calculo_id  " +
            "                 inner join cadastroeconomico cmc on cmc.id = calculo.cadastro_id " +
            "                  left join pessoajuridica pj on pj.id = cmc.pessoa_id  " +
            "                  left join pessoafisica pf on pf.id = cmc.pessoa_id" +
            "                      where processo.id = :processo_id" +
            "                   order by 3";
        List lista = em.createNativeQuery(sql).setParameter("processo_id", p.getId()).getResultList();
        List<String[]> retorno = new ArrayList<>();
        for (Object objeto : lista) {
            Object[] obj = (Object[]) objeto;
            String inscricao = obj[1] != null ? obj[1].toString() : "";
            String nomeRazao = obj[2] != null ? obj[2].toString() : "";
            String cpfCnpj = "";
            try {
                cpfCnpj = calculaIssFixoGeralFacade.getCpfCnpjFormatado(obj[0].toString(), obj[3].toString());
            } catch (NullPointerException ex) {
                cpfCnpj = "";
            }
            String conteudo = getConteudo(obj[4] != null ? obj[4] : "");
            retorno.add(new String[]{inscricao, nomeRazao, cpfCnpj, conteudo});
        }
        return retorno;
    }

    private String getConteudo(Object o) {
        java.sql.Clob c = (Clob) o;

        BufferedReader br = null;
        try {
            return new BufferedReader(c.getCharacterStream()).readLine();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return "";
    }

    public List<BigDecimal> buscarIdsDeCalculosParaEfetivar(List<ProcessoCalculoGeralIssFixo> processos) {
        String sql = "select calculoiss.id" +
            "       from calculoiss calculoiss" +
            " inner join processocalculoiss processoiss on processoiss.id = calculoiss.processocalculoiss_id" +
            " inner join proccalcgeralissfixo processo on processo.processocalculoiss_id = processoiss.id" +
            "      where processo.id in (";

        String ids = "";
        for (ProcessoCalculoGeralIssFixo p : processos) {
            ids += "" + p.getId() + ",";
        }
        ids = ids.substring(0, ids.length() - 1);
        sql += ids + ")";
        List<BigDecimal> resultList = em.createNativeQuery(sql).getResultList();
        return resultList;
    }

    public ProcessoCalculoGeralIssFixo obterProcessosFiltrados(Long id) {
        return em.find(ProcessoCalculoGeralIssFixo.class, id);
    }

    public List<CalculoISS> buscarCalculosDosProcessos(List<EfetivacaoProcessoIssFixoGeral> listaDeProcessos) {
        Query query = em.createQuery("select calculo from CalculoISS calculo where calculo.processoCalculo in :processos");
        List<ProcessoCalculo> processos = Lists.newArrayList();
        for (EfetivacaoProcessoIssFixoGeral efetivacao : listaDeProcessos) {
            processos.add(efetivacao.getProcesso().getProcessoCalculoISS());
        }
        query.setParameter("processos", processos);
        return query.getResultList();
    }

    public void salvarOcorrencias(List<OcorrenciaEfetivacaoIssFixoGeral> ocorrencias) {
        for (OcorrenciaEfetivacaoIssFixoGeral ocorrencia : ocorrencias) {
            em.merge(ocorrencia);
        }
    }
}
