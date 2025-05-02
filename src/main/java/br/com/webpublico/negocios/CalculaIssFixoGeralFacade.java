package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoProcessoCalculoGeralIssFixo;
import br.com.webpublico.negocios.tributario.singletons.SingletonLancamentoGeralISSFixo;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 05/07/13
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class CalculaIssFixoGeralFacade extends AbstractFacade<ProcessoCalculoGeralIssFixo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CalculaISSFacade calculaISSFacade;

    public CalculaIssFixoGeralFacade() {
        super(ProcessoCalculoGeralIssFixo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProcessoCalculoGeralIssFixo recarregar(ProcessoCalculoGeralIssFixo processo) {
        processo = super.recarregar(processo);
        processo.getProcessoCalculoISS().getCalculos().size();

        if (processo.getProcessoCalculoISS().getCalculos() != null) {
            for (CalculoISS calculo : processo.getProcessoCalculoISS().getCalculos()) {
                calculo.getCadastroEconomico();
            }
        }

        return processo;
    }

    public ProcessoCalculoGeralIssFixo recarregarCompleto(ProcessoCalculoGeralIssFixo processo) {
        processo = super.recarregar(processo);
        processo.getProcessoCalculoISS().getCalculos().size();

        if (processo.getProcessoCalculoISS().getCalculos() != null) {
            for (CalculoISS calculo : processo.getProcessoCalculoISS().getCalculos()) {
                calculo.getCadastroEconomico();
                calculo.getItemCalculoIsss().size();
            }
        }

        return processo;
    }

    @Override
    public void remover(ProcessoCalculoGeralIssFixo processo) {
        ProcessoCalculoISS processoCalculoISS = em.merge(processo.getProcessoCalculoISS());

        super.remover(em.merge(processo));
        em.remove(processoCalculoISS);
    }

    public void removerOcorrencias(ProcessoCalculoGeralIssFixo processo) {
        processo.setOcorrencias(recuperarOcorrencias(processo));
        processo.getOcorrencias().clear();
    }

    public void abortarLancamento(ProcessoCalculoGeralIssFixo selecionado) {
        SingletonLancamentoGeralISSFixo.getInstance().cancelarLancamento();
        SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().setExecutando(false);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (selecionado == null) {
            selecionado = (ProcessoCalculoGeralIssFixo) SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().getSelecionado();
        }

        removerOcorrencias(selecionado);
        remover(selecionado);
    }

    private List<OcorrenciaProcessoCalculoGeralIssFixo> recuperarOcorrencias(ProcessoCalculoGeralIssFixo processo) {
        return em.createQuery("from OcorrenciaProcessoCalculoGeralIssFixo where processoCalculoGeral = :processo").setParameter("processo", processo).getResultList();
    }

    public ProcessoCalculoGeralIssFixo salva(ProcessoCalculoGeralIssFixo processo) {
        if (processo.getId() != null) {
            processo.setOcorrencias(recuperarOcorrencias(processo));
        } else {
            ProcessoCalculoISS merge = em.merge(processo.getProcessoCalculoISS());
            em.persist(merge);

            processo.setProcessoCalculoISS(merge);
        }

        processo = em.merge(processo);
        em.persist(processo);
        return processo;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void lancarIssFixoGeral(List<String> inscricoes, ProcessoCalculoGeralIssFixo processoGeral) {
        for (String inscricao : inscricoes) {
            if (!SingletonLancamentoGeralISSFixo.getInstance().podeLancar()) {
                break;
            }

            CadastroEconomico ce = cadastroEconomicoFacade.recuperaPorInscricao(inscricao);
            ce = cadastroEconomicoFacade.recuperar(ce.getId());

            try {
                CalculoISS calculoISS = calculaISSFacade.criarCalculoFixo(processoGeral.getProcessoCalculoISS(), ce, processoGeral.getExercicio(), SingletonLancamentoGeralISSFixo.getInstance().usuarioSistema, null);
                SingletonLancamentoGeralISSFixo.getInstance().acrescentaMaisUmGerado(calculoISS);
                SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().conta();
            } catch (ExcecaoNegocioGenerica eng) {
                SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().conta();
                SingletonLancamentoGeralISSFixo.getInstance().adicionarOcorrenciaDeFalha(ce, eng.getMessage());
            }

            if (SingletonLancamentoGeralISSFixo.getInstance().getCalculos().size() > 100) {
                aguardar();
                persistirCalculosGerados();
                aguardar();
                persistirOcorrenciasGeradas();
            }
        }

        if (!SingletonLancamentoGeralISSFixo.getInstance().getCalculos().isEmpty()) {
            aguardar();
            persistirCalculosGerados();
        }

        if (!SingletonLancamentoGeralISSFixo.getInstance().getOcorrencias().isEmpty()) {
            aguardar();
            persistirOcorrenciasGeradas();
        }
    }

    private void aguardar() {
        while (SingletonLancamentoGeralISSFixo.getInstance().persistindoCalculo || SingletonLancamentoGeralISSFixo.getInstance().persistindoOcorrencia) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void persistirCalculosGerados() {
        SingletonLancamentoGeralISSFixo.getInstance().persistindoCalculo = true;
        List<CalculoISS> calculos = SingletonLancamentoGeralISSFixo.getInstance().getCalculos();
        SingletonLancamentoGeralISSFixo.getInstance().novaListaDeCalculos();
        SingletonLancamentoGeralISSFixo.getInstance().getCalculoGeralISSFixoDao().salvar(calculos);
        SingletonLancamentoGeralISSFixo.getInstance().persistindoCalculo = false;
    }

    private synchronized void persistirOcorrenciasGeradas() {
        SingletonLancamentoGeralISSFixo.getInstance().persistindoOcorrencia = true;
        List<OcorrenciaProcessoCalculoGeralIssFixo> ocorrencias = SingletonLancamentoGeralISSFixo.getInstance().getOcorrencias();
        SingletonLancamentoGeralISSFixo.getInstance().novaListaDeOcorrenciasSucesso();
        try {
            SingletonLancamentoGeralISSFixo.getInstance().getCalculoGeralISSFixoDao().salvarOcorrencias(ocorrencias);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SingletonLancamentoGeralISSFixo.getInstance().persistindoOcorrencia = false;
    }

    public String[] obterProcessoParaExibir(ProcessoCalculoGeralIssFixo processo) {
        String sql = " select p.cmcinicial, " +
            "             p.cmcfinal, " +
            "             p.totaldeinscricoes," +
            "             (select count(1) from ocorreproccalcgeralissfixo oc where oc.calculoiss_id is not null and oc.processocalculogeral_id = p.id) as sucesso," +
            "             (select count(1) from ocorreproccalcgeralissfixo oc where oc.calculoiss_id is null and oc.processocalculogeral_id = p.id) as falhos," +
            "             p.tempodecorrido," +
            "             p.situacaoprocesso" +
            "        from proccalcgeralissfixo p" +
            "       where p.id = :id_processo";

        Object[] result = (Object[]) em.createNativeQuery(sql).setParameter("id_processo", processo.getId()).getSingleResult();

        String[] retorno = new String[]{};

        if (result != null) {
            retorno = new String[]{result[0] != null ? result[0].toString() : "",
                result[1] != null ? result[1].toString() : "",
                result[2] != null ? result[2].toString() : "",
                result[3] != null ? result[3].toString() : "",
                result[4] != null ? result[4].toString() : "",
                result[5] != null ? result[5].toString() : "",
                String.valueOf(SituacaoProcessoCalculoGeralIssFixo.valueOf(result[6].toString()).descricao)};
        }


        return retorno;
    }

    private String obterToString(Object o) {
        return o != null ? o.toString() : "";
    }

    public List<String[]> obterListaMotivosDeLancamentosFalhos(ProcessoCalculoGeralIssFixo processo) {
        String sql = " select case" +
            "                 when pf.id is not null then 'PF'" +
            "                 else 'PJ'" +
            "             end as tipo," +
            "             coalesce(ce.inscricaocadastral,'A inscrição cadastral deste C.M.C. não foi informada.')," +
            "             coalesce(pf.nome, pj.razaosocial, 'O nome/razão social deste contribuinte não foi informado.')," +
            "             coalesce(pf.cpf, pj.cnpj, 'O cpf/cnpj social deste contribuinte não foi informado.')," +
            "             oc.conteudo" +
            "        from cadastroeconomico ce " +
            "  inner join ocorreproccalcgeralissfixo ocorrencia on ocorrencia.cadastroeconomico_id = ce.id " +
            "  inner join ocorrencia oc on oc.id = ocorrencia.ocorrencia_id " +
            "  inner join proccalcgeralissfixo p on p.id = ocorrencia.processocalculogeral_id " +
            "   left join pessoajuridica pj on pj.id = ce.pessoa_id" +
            "   left join pessoafisica pf on pf.id = ce.pessoa_id" +
            "       where p.id = :id_processo " +
            "    order by 3";

        List lista = em.createNativeQuery(sql).setParameter("id_processo", processo.getId()).getResultList();

        List<String[]> retorno = new ArrayList<>();

        for (Object objeto : lista) {
            Object[] obj = (Object[]) objeto;
            java.sql.Clob c = (Clob) obj[4];
            try {
                String conteudo = new BufferedReader(c.getCharacterStream()).readLine();
                String inscricaoCadastral = obj[1].toString();
                String nomeRazao = obj[2].toString();
                String cpfCnpj = getCpfCnpjFormatado(obj[0].toString(), obj[3] != null ? obj[3].toString() : "");

                retorno.add(new String[]{inscricaoCadastral,
                    nomeRazao,
                    cpfCnpj,
                    conteudo.substring(conteudo.indexOf("!") + 2, conteudo.length())});

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return retorno;
    }

    public List<String[]> obterListaInfoDeLancamentosRealizados(ProcessoCalculoGeralIssFixo processo) {
        String sql = "               select case" +
            "                               when pf.id is not null then 'PF'" +
            "                               else 'PJ'" +
            "                           end as tipo," +
            "                           coalesce(ce.inscricaocadastral,'A inscrição cadastral deste C.M.C. não foi informada.')," +
            "                           coalesce(pf.nome, pj.razaosocial, 'O nome/razão social deste contribuinte não foi informado.')," +
            "                           coalesce(pf.cpf, pj.cnpj, 'O cpf/cnpj social deste contribuinte não foi informado.')," +
            "                           coalesce(calc.valorcalculado, 0)" +
            "                      from proccalcgeralissfixo pgeral" +
            "                inner join processocalculoiss processoiss on processoiss.id = pgeral.processocalculoiss_id" +
            "                inner join calculoiss calc on calc.processocalculoiss_id = processoiss.id" +
            "                inner join cadastroeconomico ce on ce.id = calc.cadastroeconomico_id" +
            "                 left join pessoajuridica pj on pj.id = ce.pessoa_id " +
            "                 left join pessoafisica pf on pf.id = ce.pessoa_id" +
            "                     where pgeral.id = :id_processo  " +
            "                  order by 3";

        List lista = em.createNativeQuery(sql).setParameter("id_processo", processo.getId()).getResultList();

        List<String[]> retorno = new ArrayList<>();

        for (Object objeto : lista) {
            Object[] obj = (Object[]) objeto;

            String inscricaoCadastral = obj[1].toString();
            String nomeRazao = obj[2].toString();
            String cpfCnpj = getCpfCnpjFormatado(obj[0].toString(), obj[3] != null ? obj[3].toString() : "");
            String valor = obj[4].toString();

            retorno.add(new String[]{inscricaoCadastral,
                nomeRazao,
                cpfCnpj,
                valor});
        }

        return retorno;
    }

    public String getCpfCnpjFormatado(String tipoPessoa, String cpfCnpj) {
        if (tipoPessoa.equalsIgnoreCase("PF")) {
            return Util.formatarCpf(cpfCnpj);
        } else {
            return Util.formatarCnpj(cpfCnpj);
        }
    }

    public boolean temAlgumProcesso() {
        String hql = "select pro from ProcessoCalculoGeralIssFixo pro";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        return !q.getResultList().isEmpty();
    }
}
