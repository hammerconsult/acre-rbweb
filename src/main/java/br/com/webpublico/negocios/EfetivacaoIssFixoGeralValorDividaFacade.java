package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.exception.EfetivacaoIssFixoGeralException;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonEfetivacaoIssFixoGeral;
import com.google.common.collect.Lists;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.*;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 07/08/13
 * Time: 08:38
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class EfetivacaoIssFixoGeralValorDividaFacade extends GeraValorDividaISS {
    @EJB
    private PersisteValorDivida persisteValorDivida;
    private JdbcDividaAtivaDAO dao;


    public List<List<? extends Calculo>> particionarEmCinco(List<? extends Calculo> calculos) {
        List<List<? extends Calculo>> retorno = new ArrayList<>();
        int parte = calculos.size() / 5;
        retorno.add(calculos.subList(0, parte));
        retorno.add(calculos.subList(parte, parte * 2));
        retorno.add(calculos.subList(parte * 2, parte * 3));
        retorno.add(calculos.subList(parte * 3, parte * 4));
        retorno.add(calculos.subList(parte * 4, calculos.size()));

        return retorno;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void gerarDebitoEfetivacaoIssFixoGeral(EfetivacaoProcessoIssFixoGeral efetivacao) throws Exception {
        List<OpcaoPagamentoDivida> opcoesPagamento = validaOpcoesPagamento(efetivacao.getProcesso().getProcessoCalculoISS().getDivida());
        geraValoresDivida(efetivacao.getProcesso().getProcessoCalculoISS(), opcoesPagamento);
    }

    @Override
    protected void geraValoresDivida(ProcessoCalculo processoCalculo, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        gerarValoresDividaParaLista((List<CalculoISS>) processoCalculo.getCalculos());
    }

    @Asynchronous
//    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void gerarValoresDividaParaLista(List<CalculoISS> lista) {
        for (CalculoISS calculo : lista) {
            geraDebito(calculo);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void geraDebito(CalculoISS calculo) {
        try {
            List<OpcaoPagamentoDivida> opcoesPagamento = validaOpcoesPagamento(calculo.getProcessoCalculoISS().getDivida());
            geraValorDivida(calculo, opcoesPagamento);
        } catch (EfetivacaoIssFixoGeralException ex) {
            SingletonEfetivacaoIssFixoGeral.getInstance().adicionarFalha(ex.getMessage(), ex.calculoISS, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            SingletonEfetivacaoIssFixoGeral.getInstance().adicionarFalha("Erro " + ex, calculo, null);
        }
        SingletonEfetivacaoIssFixoGeral.getInstance().getAssistenteBarraProgresso().conta();
    }

    @Override
    public synchronized void geraValorDivida(Calculo calculo, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        validarDebitoLancado((CalculoISS) calculo);
        super.geraValorDivida(calculo, opcoesPagamento);

    }

    private void validarDebitoLancado(CalculoISS calculo) throws EfetivacaoIssFixoGeralException {
        List<ResultadoParcela> resultados = new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.TIPO_CALCULO, ConsultaParcela.Operador.IGUAL, Calculo.TipoCalculo.ISS)
            .addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, calculo.getCadastroEconomico().getId())
            .addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IGUAL, calculo.getProcessoCalculoISS().getExercicio().getAno())
            .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.NOT_IN, Lists.newArrayList(SituacaoParcela.SUBSTITUIDO,
                SituacaoParcela.CANCELAMENTO))
            .executaConsulta().getResultados();

        if (!resultados.isEmpty()) {
            for (ResultadoParcela resultado : resultados) {
                throw new EfetivacaoIssFixoGeralException("O CMC " + calculo.getCadastroEconomico().getInscricaoCadastral()
                    + " já possui lançamento de ISSQN Fixo para o exercício informado coma situação de " +
                    resultado.getSituacaoEnumValue().getDescricao(), calculo);
            }
        }
    }


    @Override
    protected OpcaoPagamento recarregarOpcaoPagamento(OpcaoPagamento opcao) {
        opcao = em.find(OpcaoPagamento.class, opcao.getId());
        opcao.setParcelas(recuperarParcelasDaOpcaoDePagamento(opcao));
        return opcao;
    }

    @Override
    public void salvaValorDivida(ValorDivida valordivida) throws Exception {
        getDAO().persisteValorDivida(valordivida);
    }

    public CalculoISS recuperarCalculoIssEfetivadoDoCmcNoExercicio(CadastroEconomico ce, Exercicio exercicio) {
        String hql = " select ciss" +
            "        from " + CalculoISS.class.getName() + " ciss, " + ValorDivida.class.getName() + " vd" +
            "  inner join ciss.cadastroEconomico cmc" +
            "  inner join ciss.processoCalculoISS processo" +
            "  inner join processo.exercicio exercicio" +
            "       where vd.calculo = ciss" +
            "         and cmc = :cmc" +
            "         and exercicio = :exercicio";

        Query q = em.createQuery(hql);
        q.setParameter("cmc", ce);
        q.setParameter("exercicio", exercicio);

        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }

        return (CalculoISS) resultList.get(0);
    }


    private JdbcDividaAtivaDAO getDAO() {
        if (dao == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            dao = (JdbcDividaAtivaDAO) ap.getBean("dividaAtivaDAO");
        }
        return dao;
    }
}
