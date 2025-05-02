package br.com.webpublico.negocios;

import br.com.webpublico.entidades.IsencaoAcrescimoParcela;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.entidades.ProcessoIsencaoAcrescimos;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.services.ServiceConsultaDebitos;
import br.com.webpublico.negocios.tributario.singletons.SingletonProcessoIsencaoAcrescimos;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.calculadores.*;
import br.com.webpublico.tributario.consultadebitos.dtos.ConfiguracaoAcrescimosDTO;
import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;

@Stateless
public class LancamentoIsencaoAcrescimoFacade extends AbstractFacade<ProcessoIsencaoAcrescimos> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private SingletonProcessoIsencaoAcrescimos singletonProcessoIsencaoAcrescimos;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LancamentoIsencaoAcrescimoFacade() {
        super(ProcessoIsencaoAcrescimos.class);
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
    }

    @Override
    public ProcessoIsencaoAcrescimos recuperar(Object id) {
        ProcessoIsencaoAcrescimos ld = em.find(ProcessoIsencaoAcrescimos.class, id);
        ld.getIsencoesParcela().size();

        return ld;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public SingletonProcessoIsencaoAcrescimos getSingletonProcessoIsencaoAcrescimos() {
        return singletonProcessoIsencaoAcrescimos;
    }

    public IsencaoAcrescimoParcela criarOrAtualizarIsencaoAcrescimoParcela(ProcessoIsencaoAcrescimos processoIsencaoAcrescimos,
                                                                           ResultadoParcela resultadoParcela,
                                                                           IsencaoAcrescimoParcela isencaoAcrescimoParcela) {
        ParcelaValorDivida parcelaValorDivida = em.find(ParcelaValorDivida.class, resultadoParcela.getIdParcela());
        if (isencaoAcrescimoParcela == null) {
            isencaoAcrescimoParcela = new IsencaoAcrescimoParcela();
            isencaoAcrescimoParcela.setProcessoIsencaoAcrescimos(processoIsencaoAcrescimos);
            isencaoAcrescimoParcela.setParcela(parcelaValorDivida);
        }
        isencaoAcrescimoParcela.setDividaOriginal(parcelaValorDivida.getValorDivida().getDivida());
        isencaoAcrescimoParcela.setExercicio(parcelaValorDivida.getValorDivida().getExercicio());
        isencaoAcrescimoParcela.setNumeroParcelaOriginal(parcelaValorDivida.getSequenciaParcela());
        isencaoAcrescimoParcela.setReferenciaOriginal(parcelaValorDivida.getSituacaoAtual().getReferencia());
        isencaoAcrescimoParcela.setSdOriginal(parcelaValorDivida.getValorDivida().getCalculo().getSubDivida().toString());
        isencaoAcrescimoParcela.setSituacaoParcelaOriginal(parcelaValorDivida.getSituacaoAtual().getSituacaoParcela());
        isencaoAcrescimoParcela.setTipoDebitoOriginal(resultadoParcela.getTipoDeDebito());
        isencaoAcrescimoParcela.setValorImpostoOriginal(resultadoParcela.getValorImposto());
        isencaoAcrescimoParcela.setValorTaxaOriginal(resultadoParcela.getValorTaxa());
        isencaoAcrescimoParcela.setValorJurosOriginal(resultadoParcela.getValorJuros());
        isencaoAcrescimoParcela.setValorMultaOriginal(resultadoParcela.getValorMulta());
        isencaoAcrescimoParcela.setValorCorrecaoOriginal(resultadoParcela.getValorCorrecao());
        isencaoAcrescimoParcela.setValorDescontoOriginal(resultadoParcela.getValorDesconto());
        isencaoAcrescimoParcela.setValorHonorariosOriginal(resultadoParcela.getValorHonorarios());
        isencaoAcrescimoParcela.setVencimentoOriginal(resultadoParcela.getVencimento());
        return isencaoAcrescimoParcela;
    }

    public void calcularDeducaoAcrescimos(ProcessoIsencaoAcrescimos processo) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        CalculadorAcrescimos calculadorAcrescimos = new CalculadorAcrescimos(consultaParcela.getServiceConsultaDebitos());
        CalculadorJuros calculadorJuros = new CalculadorJuros(calculadorAcrescimos);
        CalculadorMulta calculadorMulta = new CalculadorMulta(calculadorAcrescimos);
        CalculadorCorrecao calculadorCorrecao = new CalculadorCorrecao(calculadorAcrescimos);
        CalculadorHonorarios calculadorHonorarios = new CalculadorHonorarios(calculadorAcrescimos);

        LocalDate dataCorrecao = LocalDate.now();
        if (processo.getExercicioCorrecao() != null) {
            dataCorrecao = dataCorrecao.year().setCopy(processo.getExercicioCorrecao().getAno());
        }

        for (IsencaoAcrescimoParcela isencaoAcrescimoParcela : processo.getIsencoesParcela()) {
            consultaParcela.limpaConsulta();
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, isencaoAcrescimoParcela.getParcela().getId());
            ResultadoParcela resultadoParcela = consultaParcela.executaConsulta().getResultados().get(0);
            calculadorAcrescimos.simularIsencaoAcrescimos(resultadoParcela, isencaoAcrescimoParcela.toDto());
            resultadoParcela.setValorCorrecao(null);
            resultadoParcela.setValorMulta(null);
            resultadoParcela.setValorJuros(null);


            isencaoAcrescimoParcela.setValorCorrecaoDeduzido(calculadorCorrecao.calculaCorrecao(resultadoParcela, dataCorrecao.toDate(), null, null, null, null));
            isencaoAcrescimoParcela.setValorJurosDeduzido(calculadorJuros.calculaJuros(resultadoParcela, LocalDate.now().toDate(), null, null, null, null));
            isencaoAcrescimoParcela.setValorMultaDeduzido(calculadorMulta.calculaMulta(resultadoParcela, LocalDate.now().toDate(), null, null, null, null));

            if (isencaoAcrescimoParcela.getValorHonorariosOriginal().compareTo(BigDecimal.ZERO) > 0) {
                ConfiguracaoAcrescimosDTO configuracaoAcrescimo = calculadorAcrescimos.getConfiguracaoAcrescimo(resultadoParcela.getIdConfiguracaoAcrescimo());
                isencaoAcrescimoParcela.setValorHonorariosAtualizado(calculadorHonorarios.calculaHonorarios(configuracaoAcrescimo,
                    LocalDate.now().toDate(), isencaoAcrescimoParcela.getValorTotalDeduzidoSemHonorarios()));
            } else {
                isencaoAcrescimoParcela.setValorHonorariosAtualizado(BigDecimal.ZERO);
            }
        }
    }

    @Override
    public void salvarNovo(ProcessoIsencaoAcrescimos processoIsencaoAcrescimos) {
        processoIsencaoAcrescimos.setCodigo(singletonProcessoIsencaoAcrescimos.getProximoNumero(processoIsencaoAcrescimos.getExercicio()));
        super.salvarNovo(processoIsencaoAcrescimos);
    }

    public void estornarProcessoDeducaoAcrescimos(ProcessoIsencaoAcrescimos processoIsencaoAcrescimos) {
        processoIsencaoAcrescimos.setSituacao(ProcessoIsencaoAcrescimos.Situacao.ESTORNADO);
        em.merge(processoIsencaoAcrescimos);
    }

    public void efetivarProcessoDeducaoAcrescimos(ProcessoIsencaoAcrescimos processoIsencaoAcrescimos) {
        processoIsencaoAcrescimos.setSituacao(ProcessoIsencaoAcrescimos.Situacao.EFETIVADO);
        em.merge(processoIsencaoAcrescimos);
    }

    public void atualizarProcessoDeducaoAcrescimos(ProcessoIsencaoAcrescimos processoIsencaoAcrescimos) {
        if (!ProcessoIsencaoAcrescimos.Situacao.ATIVO.equals(processoIsencaoAcrescimos.getSituacao())) {
            return;
        }
        for (IsencaoAcrescimoParcela isencaoAcrescimoParcela : processoIsencaoAcrescimos.getIsencoesParcela()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, isencaoAcrescimoParcela.getParcela().getId());
            ResultadoParcela resultadoParcela = consultaParcela.executaConsulta().getResultados().get(0);
            criarOrAtualizarIsencaoAcrescimoParcela(processoIsencaoAcrescimos, resultadoParcela, isencaoAcrescimoParcela);

        }
        calcularDeducaoAcrescimos(processoIsencaoAcrescimos);
    }
}
