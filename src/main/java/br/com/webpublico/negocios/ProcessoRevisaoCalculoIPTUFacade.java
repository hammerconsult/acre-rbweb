package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ExercicioRevisaoIPTU;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoLancamentoIsencaoIPTU;
import br.com.webpublico.negocios.tributario.auxiliares.AssistenteCalculadorIPTU;
import br.com.webpublico.negocios.tributario.auxiliares.AssistenteEfetivacaoIPTU;
import br.com.webpublico.negocios.tributario.auxiliares.CalculadorIPTU;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoIptuDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ProcessoRevisaoCalculoIPTUFacade extends AbstractFacade<ProcessoRevisaoCalculoIPTU> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private NovoCalculoIPTUFacade calculoIPTUFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private GeraValorDividaIPTU geraValorDividaIPTU;
    @EJB
    private GeraValorDividaRevisaoIPTU geraValorDividaRevisaoIPTU;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    private JdbcCalculoIptuDAO calculoDAO;

    public ProcessoRevisaoCalculoIPTUFacade() {
        super(ProcessoRevisaoCalculoIPTU.class);
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        calculoDAO = (JdbcCalculoIptuDAO) ap.getBean("jdbcCalculoIptuDAO");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NovoCalculoIPTUFacade getCalculoIPTUFacade() {
        return calculoIPTUFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    @Override
    public ProcessoRevisaoCalculoIPTU recuperar(Object id) {
        ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU = super.recuperar(id);
        Hibernate.initialize(processoRevisaoCalculoIPTU.getExercicios());
        Hibernate.initialize(processoRevisaoCalculoIPTU.getProcessoRevisao());
        return processoRevisaoCalculoIPTU;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<Map<CadastroImobiliario, List<ExercicioRevisaoIPTU>>> revisarIPTU(AssistenteBarraProgresso assistente,
                                                                                    ProcessoRevisaoCalculoIPTU processo,
                                                                                    Exercicio exercicioCorrente) {
        Map<CadastroImobiliario, List<ExercicioRevisaoIPTU>> retorno = new HashMap<>();
        assistente.setDescricaoProcesso("Buscando cadastros imobiliários para revisão de cálculo de IPTU.");
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        List<CadastroImobiliario> cadastros = cadastroImobiliarioFacade.recuperaPorInscricaoPorIntervalo(processo.getInscricaoInicial(),
            processo.getInscricaoFinal());
        if (!cadastros.isEmpty()) {
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Revisando I.P.T.U dos cadastros imobiliários encontrados.");
            assistente.setTotal(cadastros.size());
            for (CadastroImobiliario cadastro : cadastros) {
                retorno.put(cadastro, new ArrayList<>());
                for (ProcessoRevisaoCalculoIPTUExercicio processoRevisaoCalculoIPTUExercicio : processo.getExercicios()) {
                    try {
                        ExercicioRevisaoIPTU exercicioRevisaoIPTU = revisarIPTUPorExercico(processo,
                            processoRevisaoCalculoIPTUExercicio.getExercicio(),
                            cadastro, configuracaoTributario.getDividaIPTU(), exercicioCorrente);
                        retorno.get(cadastro).add(exercicioRevisaoIPTU);
                    } catch (Exception e) {
                        logger.error("Erro ao revisar calculo de iptu para o exercício {}. {}",
                            processoRevisaoCalculoIPTUExercicio.getExercicio(), e.getMessage());
                        logger.debug("DEtalhes do erro ao revisar calculo de iptu para o exercício {}.",
                            processoRevisaoCalculoIPTUExercicio.getExercicio(), e);
                    }
                }
            }
        }
        return new AsyncResult<>(retorno);
    }

    private ExercicioRevisaoIPTU revisarIPTUPorExercico(ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU,
                                                        Exercicio exercicio,
                                                        CadastroImobiliario cadastroImobiliario,
                                                        Divida dividaIPTU,
                                                        Exercicio exercicioCorrente) throws Exception {
        ExercicioRevisaoIPTU exercicioRevisaoIPTU = new ExercicioRevisaoIPTU();
        exercicioRevisaoIPTU.setExercicio(exercicio);

        ConsultaParcela consulta = criarConsultaDebitoIPTU(exercicio, cadastroImobiliario, dividaIPTU);
        exercicioRevisaoIPTU.setParcelas(consulta.executaConsulta().getResultados());
        exercicioRevisaoIPTU.setPermiteRevisao(Boolean.TRUE);

        if (exercicioRevisaoIPTU.getParcelas() == null || exercicioRevisaoIPTU.getParcelas().isEmpty()) {
            exercicioRevisaoIPTU.setPermiteRevisao(Boolean.FALSE);
            exercicioRevisaoIPTU.setMensagemRevisaoNaoPermitida("Não há débitos de I.P.T.U. lançados para o cadastro" +
                " no exercício informado, a revisão de I.P.T.U.");
        } else {
            IsencaoCadastroImobiliario isencao = cadastroImobiliarioFacade.buscarIsencaoCadastroImobiliarioPorExercicio(cadastroImobiliario,
                exercicio);
            if (isencao != null &&
                ProcessoIsencaoIPTU.Situacao.DEFERIDO.equals(isencao.getProcessoIsencaoIPTU().getSituacao()) &&
                TipoLancamentoIsencaoIPTU.ISENTO_IPTU_TSU.equals(isencao.getTipoLancamentoIsencao())) {
                exercicioRevisaoIPTU.setPermiteRevisao(Boolean.FALSE);
                exercicioRevisaoIPTU.setMensagemRevisaoNaoPermitida("Há uma isenção (" + isencao.getTipoLancamentoIsencao().getDescricao() +
                    ") para esse Cadastro Imobiliário.");
            }
            for (ResultadoParcela parcela : exercicioRevisaoIPTU.getParcelas()) {
                if (!parcela.getSituacaoNameEnum().equals(SituacaoParcela.EM_ABERTO.name()) &&
                    !parcela.getSituacaoNameEnum().equals(SituacaoParcela.AGUARDANDO_REVISAO.name())) {
                    exercicioRevisaoIPTU.setPermiteRevisao(Boolean.FALSE);
                    exercicioRevisaoIPTU.setMensagemRevisaoNaoPermitida("Há uma ou mais parcelas de I.P.T.U do exercício" +
                        " informado com situação de " + parcela.getSituacaoDescricaoEnum() + " para revisar o I.P.T.U todos os" +
                        " débitos devem estar Em Aberto ou Aguardando Revisão!");
                    break;
                }
            }
        }

        if (exercicioRevisaoIPTU.getPermiteRevisao()) {
            revisarCalculoIPTU(processoRevisaoCalculoIPTU, cadastroImobiliario, exercicioRevisaoIPTU, dividaIPTU);
        }

        return exercicioRevisaoIPTU;
    }

    private ConsultaParcela criarConsultaDebitoIPTU(Exercicio exercicio, CadastroImobiliario cadastroImobiliario, Divida dividaIPTU) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cadastroImobiliario.getId());
        consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IGUAL, exercicio.getAno());
        consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, dividaIPTU.getId());

        List<SituacaoParcela> situacaoes = Lists.newArrayList(SituacaoParcela.EM_ABERTO,
            SituacaoParcela.AGUARDANDO_REVISAO,
            SituacaoParcela.PAGO,
            SituacaoParcela.PAGO_REFIS,
            SituacaoParcela.PAGO_SUBVENCAO,
            SituacaoParcela.BAIXADO,
            SituacaoParcela.BAIXADO_OUTRA_OPCAO,
            SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA,
            SituacaoParcela.COMPENSACAO,
            SituacaoParcela.SUSPENSAO,
            SituacaoParcela.BAIXADO_OUTRA_OPCAO
        );

        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, situacaoes);
        return consulta;
    }

    private void revisarCalculoIPTU(ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU,
                                    CadastroImobiliario cadastroImobiliario,
                                    ExercicioRevisaoIPTU exercicioRevisaoIPTU,
                                    Divida dividaIPTU) {
        Long idValorDivida = exercicioRevisaoIPTU.getParcelas().get(0).getIdValorDivida();
        ValorDivida valorDivida = calculoIPTUFacade.recuperarValorDivida(idValorDivida);
        exercicioRevisaoIPTU.setValorDividaOriginal(valorDivida);
        CalculoIPTU calculo = calculoIPTUFacade.recuperaIPTU(valorDivida.getCalculo().getId());

        ProcessoCalculoIPTU processoCalculo = new ProcessoCalculoIPTU();
        processoCalculo.setDescricao("Revisão de Cálculo de I.P.T.U. ");
        processoCalculo.setAnoProtocolo(processoRevisaoCalculoIPTU.getAnoProtocolo());
        processoCalculo.setNumeroProtocolo(processoRevisaoCalculoIPTU.getNumeroProtocolo());
        processoCalculo.setConfiguracaoEventoIPTU(calculo.getProcessoCalculoIPTU().getConfiguracaoEventoIPTU());
        processoCalculo.setDivida(dividaIPTU);
        processoCalculo.setConfiguracaoEventoIPTU(calculoIPTUFacade.getConfiguracaoEventoFacade().recuperar(processoCalculo.getConfiguracaoEventoIPTU().getId()));
        processoCalculo.setCadastroInicial(cadastroImobiliario.getInscricaoCadastral());
        processoCalculo.setCadastroFinal(cadastroImobiliario.getInscricaoCadastral());
        processoCalculo.setExercicio(exercicioRevisaoIPTU.getExercicio());
        processoCalculo.setUsuarioSistema(processoRevisaoCalculoIPTU.getUsuario());
        AssistenteCalculadorIPTU assistente = new AssistenteCalculadorIPTU(processoCalculo, 1);
        assistente.setPersisteCalculo(false);
        List<Propriedade> propriedades = calculoIPTUFacade.getProcessoIsencaoIPTUFacade()
            .getCadastroImobiliarioFacade().recuperarProprietariosVigentes(cadastroImobiliario);
        cadastroImobiliario.setIdMaiorProprietario(propriedades.get(0).getPessoa().getId());
        List<CadastroImobiliario> cadastros = Lists.newArrayList(cadastroImobiliario);
        CalculadorIPTU calculadorIPTU = new CalculadorIPTU();
        calculadorIPTU.calcularIPTU(cadastros, assistente, null);

        exercicioRevisaoIPTU.setProcessoCalculoIPTU(processoCalculo);

        AssistenteEfetivacaoIPTU assistenteEfetivacaoIPTU = new AssistenteEfetivacaoIPTU();
        assistenteEfetivacaoIPTU.inicializar(processoCalculo.getCalculos().size(), AssistenteEfetivacaoIPTU.TipoProcesso.EFETIVACAO);
        List<ValorDivida> valoresDivida = geraValorDividaRevisaoIPTU.geraDebito(assistenteEfetivacaoIPTU,
            processoCalculo.getCalculosIPTU(), exercicioRevisaoIPTU.getExercicio(),
            processoRevisaoCalculoIPTU.getTipoVencimentoRevisaoIPTU());
        exercicioRevisaoIPTU.setValorDividaOriginado(valoresDivida.get(0));
    }

    private Integer getQuantidadeRevisaoParaEfetivar(Map<CadastroImobiliario, List<ExercicioRevisaoIPTU>> mapaRevisao) {
        Integer count = 0;
        for (CadastroImobiliario cadastroImobiliario : mapaRevisao.keySet()) {
            List<ExercicioRevisaoIPTU> exerciciosRevisaoIPTU = mapaRevisao.get(cadastroImobiliario);
            for (ExercicioRevisaoIPTU exercicioRevisaoIPTU : exerciciosRevisaoIPTU) {
                if (exercicioRevisaoIPTU.getPermiteRevisao() && exercicioRevisaoIPTU.getValorDividaOriginado() != null) {
                    count++;
                }
            }
        }
        return count;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<ProcessoRevisaoCalculoIPTU> efetivarRevisarIPTU(AssistenteBarraProgresso assistente,
                                                                  ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU,
                                                                  Map<CadastroImobiliario, List<ExercicioRevisaoIPTU>> mapaRevisao) throws Exception {
        assistente.setDescricaoProcesso("Efetivando Revisão de I.P.T.U");
        assistente.setTotal(getQuantidadeRevisaoParaEfetivar(mapaRevisao));

        processoRevisaoCalculoIPTU.setCodigo(calculoIPTUFacade.getSingletonGeradorCodigo().getProximoCodigo(ProcessoRevisaoCalculoIPTU.class,
            "codigo"));
        processoRevisaoCalculoIPTU = em.merge(processoRevisaoCalculoIPTU);

        for (CadastroImobiliario cadastroImobiliario : mapaRevisao.keySet()) {
            List<ExercicioRevisaoIPTU> exerciciosRevisaoIPTU = mapaRevisao.get(cadastroImobiliario);
            for (ExercicioRevisaoIPTU exercicioRevisaoIPTU : exerciciosRevisaoIPTU) {
                if (exercicioRevisaoIPTU.getPermiteRevisao() && exercicioRevisaoIPTU.getValorDividaOriginado() != null) {
                    efetivarRevisaoIPTUPorExercicio(processoRevisaoCalculoIPTU,
                        cadastroImobiliario, exercicioRevisaoIPTU);
                    assistente.conta();
                }
            }
        }

        return new AsyncResult<>(processoRevisaoCalculoIPTU);
    }

    private void efetivarRevisaoIPTUPorExercicio(ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU,
                                                 CadastroImobiliario cadastroImobiliario,
                                                 ExercicioRevisaoIPTU exercicioRevisaoIPTU) throws Exception {

        registrarDebitoIPTU(exercicioRevisaoIPTU.getProcessoCalculoIPTU(), exercicioRevisaoIPTU.getValorDividaOriginado());

        RevisaoCalculoIPTU revisaoCalculoIPTU = registrarRevisaoCalculoIPTU(processoRevisaoCalculoIPTU,
            cadastroImobiliario, exercicioRevisaoIPTU);

        registrarVinculoRevisaoIPTU(processoRevisaoCalculoIPTU, revisaoCalculoIPTU);

        alterarDebitoOriginal(revisaoCalculoIPTU);
    }

    private void alterarDebitoOriginal(RevisaoCalculoIPTU revisaoCalculoIPTU) {
        for (ParcelaValorDivida pvd : revisaoCalculoIPTU.getValorDivida().getParcelaValorDividas()) {
            if (!SituacaoParcela.AGUARDANDO_REVISAO.equals(pvd.getUltimaSituacao().getSituacaoParcela())
                && !SituacaoParcela.CANCELADO_RECALCULO.equals(pvd.getUltimaSituacao().getSituacaoParcela())
                && !SituacaoParcela.CANCELADO_ISENCAO.equals(pvd.getUltimaSituacao().getSituacaoParcela())) {

                SituacaoParcela situacao = SituacaoParcela.CANCELADO_RECALCULO;
                Calculo calculo = revisaoCalculoIPTU.getValorDivida().getCalculo();
                calculo = initializeAndUnproxy(calculo);
                if (((CalculoIPTU) calculo).getIsencaoCadastroImobiliario() != null) {
                    situacao = SituacaoParcela.CANCELADO_ISENCAO;
                }
                pvd.getSituacoes().add(new SituacaoParcelaValorDivida(situacao, pvd, pvd.getValor()));
                em.merge(pvd);
            }
        }
    }

    private void registrarVinculoRevisaoIPTU(ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU, RevisaoCalculoIPTU revisaoCalculoIPTU) {
        ProcessoRevisao processoRevisao = new ProcessoRevisao();
        processoRevisao.setProcessoRevisaoCalculoIPTU(processoRevisaoCalculoIPTU);
        processoRevisao.setRevisaoCalculoIPTU(revisaoCalculoIPTU);
        em.merge(processoRevisao);
    }

    private RevisaoCalculoIPTU registrarRevisaoCalculoIPTU(ProcessoRevisaoCalculoIPTU processoRevisaoCalculoIPTU, CadastroImobiliario cadastroImobiliario, ExercicioRevisaoIPTU exercicioRevisaoIPTU) {
        RevisaoCalculoIPTU revisaoCalculoIPTU = new RevisaoCalculoIPTU();
        revisaoCalculoIPTU.setCodigo(calculoIPTUFacade.getSingletonGeradorCodigo().getProximoCodigo(RevisaoCalculoIPTU.class, "codigo"));
        revisaoCalculoIPTU.setExercicio(exercicioRevisaoIPTU.getExercicio());
        revisaoCalculoIPTU.setDataCriacao(processoRevisaoCalculoIPTU.getDataProcesso());
        revisaoCalculoIPTU.setUsuario(processoRevisaoCalculoIPTU.getUsuario());
        revisaoCalculoIPTU.setAnoProtocolo(processoRevisaoCalculoIPTU.getAnoProtocolo());
        revisaoCalculoIPTU.setNumeroProtocolo(processoRevisaoCalculoIPTU.getNumeroProtocolo());
        revisaoCalculoIPTU.setProcesso(processoRevisaoCalculoIPTU.getAnoProtocolo() + "/" + processoRevisaoCalculoIPTU.getNumeroProtocolo());
        revisaoCalculoIPTU.setCadastro(cadastroImobiliario);
        revisaoCalculoIPTU.setObservacao(processoRevisaoCalculoIPTU.getObservacao());
        revisaoCalculoIPTU.setValorDivida(exercicioRevisaoIPTU.getValorDividaOriginal());
        revisaoCalculoIPTU.setProcessoCalculo(exercicioRevisaoIPTU.getProcessoCalculoIPTU());
        revisaoCalculoIPTU = em.merge(revisaoCalculoIPTU);
        return revisaoCalculoIPTU;
    }

    private void registrarDebitoIPTU(ProcessoCalculoIPTU processoCalculoIPTU, ValorDivida valorDivida) throws Exception {
        calculoDAO.geraProcessoCalculo(processoCalculoIPTU);
        calculoDAO.persisteTudo(processoCalculoIPTU.getCalculosIPTU());
        CalculoIPTU calculo = processoCalculoIPTU.getCalculosIPTU().get(0);
        valorDivida.setCalculo(calculo);
        geraValorDividaRevisaoIPTU.salvaValorDivida(valorDivida);
    }
}
