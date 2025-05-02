package br.com.webpublico.negocios;

import br.com.webpublico.controle.TransferenciaParametrosTributarioControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.AssuntoLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.CategoriaAssuntoLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.ParametroLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.SecretarioLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.*;
import br.com.webpublico.enums.TipoITBI;
import br.com.webpublico.negocios.tributario.AssuntoLicenciamentoAmbientalFacade;
import br.com.webpublico.negocios.tributario.ParametroLicenciamentoAmbientalFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcFaceServico;
import br.com.webpublico.negocios.tributario.dao.JdbcFaceValor;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class TransferenciaParametrosTributarioFacade extends AbstractFacade<ConfiguracaoTributario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    public static final Logger logger = LoggerFactory.getLogger(TransferenciaParametrosTributarioFacade.class.getName());
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private IndiceEconomicoFacade indiceEconomicoFacade;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private EnquadramentoTributoExercicioFacade enquadramentoTributoExercicioFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private PontosFacade pontosFacade;
    @EJB
    private FaceFacade faceFacade;
    @EJB
    private FaixaAlvaraFacade faixaAlvaraFacade;
    @EJB
    private ParametroTipoDividaDiversaFacade parametroTipoDividaDiversaFacade;
    @EJB
    private ParametroFiscalizacaoFacade parametroFiscalizacaoFacade;
    @EJB
    private ParametroITBIFacade parametroITBIFacade;
    @EJB
    private ParametrosDividaAtivaFacade parametrosDividaAtivaFacade;
    @EJB
    private ParametroCobrancaAdministrativaFacade parametroCobrancaAdministrativaFacade;
    @EJB
    private OpcaoPagamentoFacade opcaoPagamentoFacade;
    @EJB
    private ParametroRendasPatrimoniaisFacade parametroRendasPatrimoniaisFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametroSimplesNacionalFacade parametroSimplesNacionalFacade;
    private JdbcFaceValor faceValorDAO;
    private JdbcFaceServico faceServicoDAO;
    @EJB
    private HabiteseClassesConstrucaoFacade habiteseClassesConstrucaoFacade;
    @EJB
    private HabiteseGruposConstrucaoFacade habiteseGruposConstrucaoFacade;
    @EJB
    private HabitesePadroesConstrucaoFacade habitesePadroesConstrucaoFacade;
    @EJB
    private HabiteseFaixaDeValoresFacade habiteseFaixaDeValoresFacade;
    @EJB
    private ServicoConstrucaoFacade servicoConstrucaoFacade;
    @EJB
    private ItemServicoConstrucaoFacade itemServicoConstrucaoFacade;
    @EJB
    private ParametroRegularizacaoFacade parametroRegularizacaoFacade;
    @EJB
    private ParametroAlvaraImediatoFacade parametroAlvaraImediatoFacade;
    @EJB
    private ParametroMalaDiretaIptuFacade parametroMalaDiretaIptuFacade;
    @EJB
    private ParametroMarcaFogoFacade parametroMarcaFogoFacade;
    @EJB
    private ParametroLicenciamentoAmbientalFacade parametroLicenciamentoAmbientalFacade;
    @EJB
    private AssuntoLicenciamentoAmbientalFacade assuntoLicenciamentoAmbientalFacade;
    @EJB
    private ParametroInformacaoRBTransFacade parametroInformacaoRBTransFacade;

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        faceValorDAO = (JdbcFaceValor) ap.getBean("persisteFaceValor");
        faceServicoDAO = (JdbcFaceServico) ap.getBean("persisteFaceServico");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TransferenciaParametrosTributarioFacade() {
        super(ConfiguracaoTributario.class);
    }

    public void cadastrarUfm(Exercicio destino, BigDecimal ufmDestino) {
        IndiceEconomico ufm = indiceEconomicoFacade.recuperaPorDescricao("UFM");
        Moeda moeda = moedaFacade.getMoedaPorIndiceAno(ufm, destino.getAno());
        if (moeda == null) {
            logger.debug("cadastrando UFM");
            for (int i = 1; i <= 12; i++) {
                moeda = new Moeda(ufm.getDescricao() + " " + StringUtil.cortaOuCompletaEsquerda(i + "", 2, "0") + "/" + destino.getAno(), ufm, destino.getAno(), i, ufmDestino);
                moedaFacade.salvarNovo(moeda);
            }
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirEnquadramentosAndContas(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        List<EnquadramentoTributoExerc> enquadramentoOrigem = enquadramentoTributoExercicioFacade.recuperaPorExercicioVigente(origem);
        List<EnquadramentoTributoExerc> enquadramentoDestino = enquadramentoTributoExercicioFacade.recuperaPorExercicioVigente(destino);
        if (enquadramentoDestino.isEmpty() && !enquadramentoOrigem.isEmpty()) {

            for (EnquadramentoTributoExerc enquadramento : enquadramentoOrigem) {

                EnquadramentoTributoExerc eq = new EnquadramentoTributoExerc();
                eq.setCodigo(destino.getAno().longValue());
                eq.setDescricao("ENQUADRAMENTO " + destino.getAno());
                eq.setExercicioVigente(destino);
                eq.setExercicioDividaInicial(destino);
                eq.setExercicioDividaFinal(enquadramento.getExercicioDividaFinal());

                List<ContaTributoReceita> contasTributo = enquadramentoTributoExercicioFacade.contasDoEnquadramento(enquadramento);
                assistenteBarraProgresso.setTotal(contasTributo.size());
                assistenteBarraProgresso.removerProcessoDoAcompanhamento();
                for (ContaTributoReceita contaTributo : contasTributo) {
                    ContaTributoReceita contaTributoReceita = new ContaTributoReceita();
                    contaTributoReceita.setEnquadramento(eq);
                    contaTributoReceita.setTributo(contaTributo.getTributo());
                    contaTributoReceita.setInicioVigencia(DataUtil.adicionaAnos(contaTributo.getInicioVigencia(), (destino.getAno() - origem.getAno())));
                    contaTributoReceita.setFimVigencia(DataUtil.adicionaAnos(contaTributo.getFimVigencia(), (destino.getAno() - origem.getAno())));

                    contaTributoReceita.setOperacaoArrecadacaoExercicio(contaTributo.getOperacaoArrecadacaoExercicio());
                    if (contaTributo.getContaExercicio() != null) {
                        contaTributoReceita.setContaExercicio(buscarContaReceitaEquivalentePorExercicio(contaTributo.getContaExercicio(), destino));
                        contaTributoReceita.setFonteDeRecursosExercicio(contaTributo.getFonteDeRecursosExercicio());
                    }

                    contaTributoReceita.setOperacaoArrecadacaoDivAtiva(contaTributo.getOperacaoArrecadacaoDivAtiva());
                    if (contaTributo.getContaDividaAtiva() != null) {
                        contaTributoReceita.setContaDividaAtiva(buscarContaReceitaEquivalentePorExercicio(contaTributo.getContaDividaAtiva(), destino));
                        contaTributoReceita.setFonteDeRecursosDividaAtiva(contaTributo.getFonteDeRecursosDividaAtiva());
                    }

                    contaTributoReceita.setOperacaoRenunciaExercicio(contaTributo.getOperacaoRenunciaExercicio());
                    if (contaTributo.getContaRenunciaExercicio() != null) {
                        contaTributoReceita.setContaRenunciaExercicio(buscarContaReceitaEquivalentePorExercicio(contaTributo.getContaRenunciaExercicio(), destino));
                        contaTributoReceita.setFonteDeRecursosRenunciaExercicio(contaTributo.getFonteDeRecursosRenunciaExercicio());
                    }

                    contaTributoReceita.setOperacaoRenunciaDivAtiva(contaTributo.getOperacaoRenunciaDivAtiva());
                    if (contaTributo.getContaRenunciaDividaAtiva() != null) {
                        contaTributoReceita.setContaRenunciaDividaAtiva(buscarContaReceitaEquivalentePorExercicio(contaTributo.getContaRenunciaDividaAtiva(), destino));
                        contaTributoReceita.setFonteDeRecursosRenunciaDividaAtiva(contaTributo.getFonteDeRecursosRenunciaDividaAtiva());
                    }

                    contaTributoReceita.setOperacaoRestituicaoExercicio(contaTributo.getOperacaoRestituicaoExercicio());
                    if (contaTributo.getContaRestituicaoExercicio() != null) {
                        contaTributoReceita.setContaRestituicaoExercicio(buscarContaReceitaEquivalentePorExercicio(contaTributo.getContaRestituicaoExercicio(), destino));
                        contaTributoReceita.setFonteDeRecursosRestituicaoExercicio(contaTributo.getFonteDeRecursosRestituicaoExercicio());
                    }

                    contaTributoReceita.setOperacaoRestituicaoDivAtiva(contaTributo.getOperacaoRestituicaoDivAtiva());
                    if (contaTributo.getContaRestituicaoDividaAtiva() != null) {
                        contaTributoReceita.setContaRestituicaoDividaAtiva(buscarContaReceitaEquivalentePorExercicio(contaTributo.getContaRestituicaoDividaAtiva(), destino));
                        contaTributoReceita.setFonteDeRecursosRestituicaoDividaAtiva(contaTributo.getFonteDeRecursosRestituicaoDividaAtiva());
                    }

                    contaTributoReceita.setOperacaoDescConcedidoExercicio(contaTributo.getOperacaoDescConcedidoExercicio());
                    if (contaTributo.getContaDescontoExercicio() != null) {
                        contaTributoReceita.setContaDescontoExercicio(buscarContaReceitaEquivalentePorExercicio(contaTributo.getContaDescontoExercicio(), destino));
                        contaTributoReceita.setFonteDeRecursosDescontoExercicio(contaTributo.getFonteDeRecursosDescontoExercicio());
                    }

                    contaTributoReceita.setOperacaoDescConcedidoDivAtiva(contaTributo.getOperacaoDescConcedidoDivAtiva());
                    if (contaTributo.getContaDescontoDividaAtiva() != null) {
                        contaTributoReceita.setContaDescontoDividaAtiva(buscarContaReceitaEquivalentePorExercicio(contaTributo.getContaDescontoDividaAtiva(), destino));
                        contaTributoReceita.setFonteDeRecursosDescontoDividaAtiva(contaTributo.getFonteDeRecursosDescontoDividaAtiva());
                    }

                    contaTributoReceita.setOperacaoOutraDeducaoExercicio(contaTributo.getOperacaoOutraDeducaoExercicio());
                    if (contaTributo.getContaDeducoesExercicio() != null) {
                        contaTributoReceita.setContaDeducoesExercicio(buscarContaReceitaEquivalentePorExercicio(contaTributo.getContaDeducoesExercicio(), destino));
                        contaTributoReceita.setFonteDeRecursosDeducoesExercicio(contaTributo.getFonteDeRecursosDeducoesExercicio());
                    }

                    contaTributoReceita.setOperacaoOutraDeducaoDivAtiva(contaTributo.getOperacaoOutraDeducaoDivAtiva());
                    if (contaTributo.getContaDeducoesDividaAtiva() != null) {
                        contaTributoReceita.setContaDeducoesDividaAtiva(buscarContaReceitaEquivalentePorExercicio(contaTributo.getContaDeducoesDividaAtiva(), destino));
                        contaTributoReceita.setFonteDeRecursosDeducoesDividaAtiva(contaTributo.getFonteDeRecursosDeducoesDividaAtiva());
                    }

                    assistenteBarraProgresso.conta();
                    eq.getContas().add(contaTributoReceita);
                }
                enquadramentoTributoExercicioFacade.salvarNovo(eq);
            }
        }
        return assistenteBarraProgresso;
    }

    private ContaReceita buscarContaReceitaEquivalentePorExercicio(ContaReceita contaReceitaOrigem, Exercicio exercicioDestivo) {
        List<Conta> contas = contaFacade.recuperarContaReceitaEquivalentePorId(contaReceitaOrigem, exercicioDestivo);
        if (contas != null && !contas.isEmpty()) {
            return (ContaReceita) contas.get(0);
        }
        return null;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirPontuacoesIptu(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        List<Pontuacao> listaPontuacaoOrigem = pontosFacade.listaPontuacoesPorExercicio(origem);
        List<Pontuacao> listaPontuacaoDestino = pontosFacade.listaPontuacoesPorExercicio(destino);
        assistenteBarraProgresso.setTotal(listaPontuacaoOrigem.size());
        assistenteBarraProgresso.removerProcessoDoAcompanhamento();

        if (!listaPontuacaoOrigem.isEmpty() && listaPontuacaoDestino.isEmpty()) {
            for (Pontuacao pontuacao : listaPontuacaoOrigem) {
                assistenteBarraProgresso.conta();
                pontosFacade.duplicar(pontuacao, pontuacao.getIdentificacao(), destino);

            }
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 7)
    public AssistenteBarraProgresso transferirValoresFaceDeQuadra(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso, TransferenciaParametrosTributarioControlador.TransferenciaParametroTributario transferencia) {
        boolean jaPossuiNoExercicioDestino = faceFacade.possuiFaceValorNoExercicio(destino);
        List<FaceValor> listaFaceValorOrigem = Lists.newArrayList();
        if (!jaPossuiNoExercicioDestino) {
            listaFaceValorOrigem = faceFacade.buscarFaceValorPorExercicio(origem);
            assistenteBarraProgresso.setTotal(listaFaceValorOrigem.size());
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
        }

        if (!listaFaceValorOrigem.isEmpty() && !jaPossuiNoExercicioDestino) {
            for (FaceValor faceValor : listaFaceValorOrigem) {
                FaceValor novo = new FaceValor();
                novo.setExercicio(destino);
                novo.setFace(faceValor.getFace());
                novo.setQuadra(faceValor.getQuadra());
                novo.setValor(faceValor.getValor());
                assistenteBarraProgresso.conta();
                faceValorDAO.salvar(novo);
            }
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirServicosFaceDeQuadra(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso, TransferenciaParametrosTributarioControlador.TransferenciaParametroTributario transferencia) {

        boolean jaPossuiNoExercicioDestino = faceFacade.possuiFaceServicoNoExercicio(destino);
        List<FaceServico> listaFaceServicoOrigem = Lists.newArrayList();
        if (!jaPossuiNoExercicioDestino) {
            listaFaceServicoOrigem = faceFacade.buscarFaceServicoPorExercicio(origem);
        }
        if (!listaFaceServicoOrigem.isEmpty() && !jaPossuiNoExercicioDestino) {
            assistenteBarraProgresso.setTotal(listaFaceServicoOrigem.size());
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            for (FaceServico faceServico : listaFaceServicoOrigem) {
                assistenteBarraProgresso.setDescricaoProcesso(transferencia.getDescricao());
                FaceServico novo = new FaceServico();
                novo.setAno(destino.getAno());
                novo.setFace(faceServico.getFace());
                novo.setDataRegistro(new Date());
                novo.setServicoUrbano(faceServico.getServicoUrbano());
                faceServicoDAO.salvar(novo);
                assistenteBarraProgresso.conta();
            }
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirFaixasDeAlvara(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        List<FaixaAlvara> listaFaixaAlvaraOrigem = faixaAlvaraFacade.buscarFaixasPorExercicio(origem);
        List<FaixaAlvara> listaFaixaAlvaraDestino = faixaAlvaraFacade.buscarFaixasPorExercicio(destino);
        if (!listaFaixaAlvaraOrigem.isEmpty() && listaFaixaAlvaraDestino.isEmpty()) {
            assistenteBarraProgresso.setTotal(listaFaixaAlvaraOrigem.size());
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            for (FaixaAlvara faixa : listaFaixaAlvaraOrigem) {
                FaixaAlvara novo = new FaixaAlvara();
                novo.setExercicioVigencia(destino);
                novo.setClassificacaoAtividade(faixa.getClassificacaoAtividade());

                for (ItemFaixaAlvara item : faixa.getItemFaixaAlvaras()) {
                    ItemFaixaAlvara novoItem = new ItemFaixaAlvara();
                    novoItem.setFaixaAlvara(novo);
                    novoItem.setTipoAlvara(item.getTipoAlvara());
                    novoItem.setAreaMetroInicial(item.getAreaMetroInicial());
                    novoItem.setAreaMetroFinal(item.getAreaMetroFinal());
                    novoItem.setValorTaxaUFMAno(item.getValorTaxaUFMAno());
                    novoItem.setValorTaxaUFMMes(item.getValorTaxaUFMMes());
                    novoItem.setGrauDeRisco(item.getGrauDeRisco());
                    novo.getItemFaixaAlvaras().add(novoItem);
                }
                faixaAlvaraFacade.salvar(novo);
                assistenteBarraProgresso.conta();
            }
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirParametrosTipoDividaDiversa(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        List<ParametroTipoDividaDiversa> listaParametroOrigem = parametroTipoDividaDiversaFacade.buscarParametroTipoDividaDiversaPorExercicio(origem);
        List<ParametroTipoDividaDiversa> listaParametroDestino = parametroTipoDividaDiversaFacade.buscarParametroTipoDividaDiversaPorExercicio(destino);
        if (!listaParametroOrigem.isEmpty() && listaParametroDestino.isEmpty()) {
            assistenteBarraProgresso.setTotal(listaParametroOrigem.size());
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            for (ParametroTipoDividaDiversa parametro : listaParametroOrigem) {
                ParametroTipoDividaDiversa novo = new ParametroTipoDividaDiversa();
                novo.setExercicio(destino);
                novo.setTipoDividaDiversa(parametro.getTipoDividaDiversa());

                for (ItemParametroTipoDividaDiv item : parametro.getItensParametro()) {
                    ItemParametroTipoDividaDiv novoItem = new ItemParametroTipoDividaDiv();
                    novoItem.setParametroTipoDividaDiversa(novo);
                    novoItem.setValorInicial(item.getValorInicial());
                    novoItem.setValorFinal(item.getValorFinal());
                    novoItem.setPercentualDescontoJuros(item.getPercentualDescontoJuros());
                    novoItem.setPercentualDescontoMulta(item.getPercentualDescontoMulta());
                    novoItem.setPercentualDescontoCorrecao(item.getPercentualDescontoCorrecao());
                    novoItem.setQuantidadeMaximaParcela(item.getQuantidadeMaximaParcela());
                    novo.getItensParametro().add(novoItem);

                }
                assistenteBarraProgresso.conta();
                parametroTipoDividaDiversaFacade.salvar(novo);
            }
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirParametrosFiscalizacaoIss(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {

        ParametroFiscalizacao parametroOrigem = parametroFiscalizacaoFacade.recuperarParametroFiscalizacao(origem);
        ParametroFiscalizacao parametroDestino = parametroFiscalizacaoFacade.recuperarParametroFiscalizacao(destino);
        assistenteBarraProgresso.setTotal(1);
        assistenteBarraProgresso.removerProcessoDoAcompanhamento();
        if (parametroOrigem != null && parametroDestino == null) {
            parametroDestino = new ParametroFiscalizacao();
            parametroDestino.setExercicio(destino);
            parametroDestino.setSecretaria(parametroOrigem.getSecretaria());
            parametroDestino.setTipoDoctoTermoInicio(parametroOrigem.getTipoDoctoTermoInicio());
            parametroDestino.setTipoDoctoTermoFim(parametroOrigem.getTipoDoctoTermoFim());
            parametroDestino.setTipoDoctoTermoHomologacao(parametroOrigem.getTipoDoctoTermoHomologacao());
            parametroDestino.setTipoDoctoAutoInfracao(parametroOrigem.getTipoDoctoAutoInfracao());
            parametroDestino.setTipoDoctoOrdemServico(parametroOrigem.getTipoDoctoOrdemServico());
            parametroDestino.setDiretor(parametroOrigem.getDiretor());
            parametroDestino.setNomeSecretario(parametroOrigem.getNomeSecretario());
            parametroDestino.setVencimentoAutoInfracao(parametroOrigem.getVencimentoAutoInfracao());
            parametroDestino.setVencimentoAutoInfracaoRevelia(parametroOrigem.getVencimentoAutoInfracaoRevelia());
            parametroDestino.setPrazoFiscalizacao(parametroOrigem.getPrazoFiscalizacao());
            parametroDestino.setPrazoApresentacaoDocto(parametroOrigem.getPrazoApresentacaoDocto());
            parametroDestino.setDiaVencimentoISS(parametroOrigem.getDiaVencimentoISS());
            parametroDestino.setDescontoAteVencimento(parametroOrigem.getDescontoAteVencimento());
            parametroDestino.setDescontoDeMulta(parametroOrigem.getDescontoDeMulta());
            parametroDestino.setDescontoMultaTrintaDias(parametroOrigem.getDescontoMultaTrintaDias());
            parametroDestino.setMensagemDam(parametroOrigem.getMensagemDam());
            parametroDestino.setTipoDoctoRelatorioFiscal(parametroOrigem.getTipoDoctoRelatorioFiscal());
            parametroDestino.setUltimoNumLevantamento(0);
            parametroDestino.setUltimoNumAutoInfracao(0);
            parametroDestino.setUltimoNumProgramacao(0);
            parametroDestino.setUltimoNumTermoInicio(0);
            parametroDestino.setUltimoNumTermoFim(0);
            parametroDestino.setUltimoNumOrdemServico(0);
            parametroDestino.setUltimoNumHomologacao(0);
            for (ParametroFiscalizacaoDivida pfd : parametroOrigem.getDividasIssqn()) {
                ParametroFiscalizacaoDivida novoPfd = new ParametroFiscalizacaoDivida();
                novoPfd.setDivida(pfd.getDivida());
                novoPfd.setParametroFiscalizacao(parametroDestino);
                parametroDestino.getDividasIssqn().add(novoPfd);
            }
            parametroFiscalizacaoFacade.salvarNovo(parametroDestino);
            assistenteBarraProgresso.conta();
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirParametrosItbi(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        for (TipoITBI tipo : TipoITBI.values()) {
            ParametrosITBI parametroOrigem = parametroITBIFacade.getParametroVigente(origem, tipo);
            ParametrosITBI parametroDestino = parametroITBIFacade.getParametroVigente(destino, tipo);
            if (parametroOrigem != null && parametroDestino == null) {
                parametroDestino = new ParametrosITBI();
                parametroDestino.setExercicio(destino);
                parametroDestino.setTipoITBI(tipo);
                parametroDestino.setDiasVencimentoSegundaViaItbi(parametroOrigem.getDiasVencimentoSegundaViaItbi());
                parametroDestino.setTributoITBI(parametroOrigem.getTributoITBI());
                parametroDestino.setVerificarDebitosImovel(parametroOrigem.getVerificarDebitosImovel());
                parametroDestino.setTipoBaseCalculo(parametroOrigem.getTipoBaseCalculo());
                parametroDestino.setPercentualReajuste(parametroOrigem.getPercentualReajuste());
                parametroDestino.setDiaFixoVencimento(parametroOrigem.getDiaFixoVencimento());
                parametroDestino.setVencLaudoAvaliacaoEmDias(parametroOrigem.getVencLaudoAvaliacaoEmDias());
                parametroDestino.setVencLaudoDeAvaliacao(parametroOrigem.getVencLaudoDeAvaliacao());
                parametroDestino.setDivida(parametroOrigem.getDivida());

                if (!assistenteBarraProgresso.getDescricaoProcesso().contains("Faixa")) {
                    assistenteBarraProgresso.setDescricaoProcesso(assistenteBarraProgresso.getDescricaoProcesso() + " - Faixa de Valores do Parcelamento");
                }

                for (FaixaValorParcelamento faixaValorParcelamento : parametroOrigem.getListaDeFaixaValorParcelamento()) {
                    assistenteBarraProgresso.setTotal(parametroOrigem.getListaDeFaixaValorParcelamento().size());
                    assistenteBarraProgresso.removerProcessoDoAcompanhamento();
                    FaixaValorParcelamento novaFaixa = new FaixaValorParcelamento();
                    novaFaixa.setParametrosITBI(parametroDestino);
                    novaFaixa.setValorInicial(faixaValorParcelamento.getValorInicial());
                    novaFaixa.setValorFinal(faixaValorParcelamento.getValorFinal());
                    novaFaixa.setQtdParcela(faixaValorParcelamento.getQtdParcela());
                    parametroDestino.getListaDeFaixaValorParcelamento().add(novaFaixa);
                    assistenteBarraProgresso.conta();
                }

                if (!assistenteBarraProgresso.getDescricaoProcesso().contains("Funcionários")) {
                    assistenteBarraProgresso.setDescricaoProcesso(assistenteBarraProgresso.getDescricaoProcesso() + " - Funcionários");
                }

                for (ParametrosFuncionarios funcionarios : parametroOrigem.getListaFuncionarios()) {
                    ParametrosFuncionarios novoFuncionario = new ParametrosFuncionarios();
                    assistenteBarraProgresso.setTotal(parametroOrigem.getListaFuncionarios().size());
                    assistenteBarraProgresso.removerProcessoDoAcompanhamento();
                    novoFuncionario.setParametros(parametroDestino);
                    novoFuncionario.setPessoa(funcionarios.getPessoa());
                    novoFuncionario.setFuncaoParametrosITBI(funcionarios.getFuncaoParametrosITBI());
                    novoFuncionario.setVigenciaInicial(DataUtil.getPrimeiroDiaAno(destino.getAno()));
                    novoFuncionario.setVigenciaFinal(DataUtil.getUltimoDiaAno(destino.getAno()));
                    novoFuncionario.setDetentorArquivoComposicao(funcionarios.getDetentorArquivoComposicao());
                    parametroDestino.getListaFuncionarios().add(novoFuncionario);
                    assistenteBarraProgresso.conta();
                }

                if (parametroOrigem.getDocumentos() != null) {
                    parametroDestino.setDocumentos(Lists.newArrayList());
                    for (ParametrosITBIDocumento documento : parametroOrigem.getDocumentos()) {
                        ParametrosITBIDocumento novoDocumento = new ParametrosITBIDocumento();
                        novoDocumento.setParametrosITBI(parametroDestino);
                        novoDocumento.setNaturezaDocumento(documento.getNaturezaDocumento());
                        novoDocumento.setDescricao(documento.getDescricao());
                        novoDocumento.setExtensoesPermitidas(documento.getExtensoesPermitidas());
                        novoDocumento.setAtivo(documento.getAtivo());
                        novoDocumento.setObrigatorio(documento.getObrigatorio());
                        parametroDestino.getDocumentos().add(novoDocumento);
                    }
                }
                parametroITBIFacade.salvar(parametroDestino);
            }
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirParametrosDividaAtiva(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        ParametrosDividaAtiva parametroOrigem = parametrosDividaAtivaFacade.parametrosDividaAtivaPorExercicio(origem);
        ParametrosDividaAtiva parametroDestino = parametrosDividaAtivaFacade.parametrosDividaAtivaPorExercicio(destino);
        if (parametroOrigem != null && parametroDestino == null) {
            parametroDestino = new ParametrosDividaAtiva();
            parametroDestino.setExercicio(destino);
            parametroDestino.setAtoLegal(parametroOrigem.getAtoLegal());
            parametroDestino.setTipoDoctoOficialImobiliario(parametroOrigem.getTipoDoctoOficialImobiliario());
            parametroDestino.setTipoDoctoOficialMobiliario(parametroOrigem.getTipoDoctoOficialMobiliario());
            parametroDestino.setTipoDoctoOficialRural(parametroOrigem.getTipoDoctoOficialRural());
            parametroDestino.setTipoDoctoOficialContribPF(parametroOrigem.getTipoDoctoOficialContribPF());
            parametroDestino.setTipoDoctoOficialContribPJ(parametroOrigem.getTipoDoctoOficialContribPJ());
            parametroDestino.setTipoDoctoCertidaoImob(parametroOrigem.getTipoDoctoCertidaoImob());
            parametroDestino.setTipoDoctoCertidaoMob(parametroOrigem.getTipoDoctoCertidaoMob());
            parametroDestino.setTipoDoctoCertidaoRural(parametroOrigem.getTipoDoctoCertidaoRural());
            parametroDestino.setTipoDoctoCertidaoContribPF(parametroOrigem.getTipoDoctoCertidaoContribPF());
            parametroDestino.setTipoDoctoCertidaoContribPJ(parametroOrigem.getTipoDoctoCertidaoContribPJ());
            assistenteBarraProgresso.setTotal(parametroOrigem.getDividasEnvioCDA().size());
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            for (ParamDividaAtivaDivida paramDividaAtivaDivida : parametroOrigem.getDividasEnvioCDA()) {
                ParamDividaAtivaDivida novaDivida = new ParamDividaAtivaDivida();
                novaDivida.setParametrosDividaAtiva(parametroDestino);
                novaDivida.setDivida(paramDividaAtivaDivida.getDivida());
                parametroDestino.getDividasEnvioCDA().add(novaDivida);
                assistenteBarraProgresso.conta();
            }
            parametroDestino.setAgrupadoresCDA(Lists.newArrayList());
            for (AgrupadorCDA agrupadorCDA : parametroOrigem.getAgrupadoresCDA()) {
                AgrupadorCDA novoAgrupadorCDA = new AgrupadorCDA();
                novoAgrupadorCDA.setTitulo(agrupadorCDA.getTitulo());
                novoAgrupadorCDA.setParametrosDividaAtiva(parametroDestino);
                novoAgrupadorCDA.setDividas(Lists.newArrayList());
                for (AgrupadorCDADivida agrupadorCDADivida : agrupadorCDA.getDividas()) {
                    AgrupadorCDADivida novoAgrupadorCDADivida = new AgrupadorCDADivida();
                    novoAgrupadorCDADivida.setAgrupadorCDA(novoAgrupadorCDA);
                    novoAgrupadorCDADivida.setDivida(agrupadorCDADivida.getDivida());
                    novoAgrupadorCDA.getDividas().add(novoAgrupadorCDADivida);
                }
                parametroDestino.getAgrupadoresCDA().add(novoAgrupadorCDA);
            }
            parametrosDividaAtivaFacade.salvar(parametroDestino);
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirParametrosCobrancasAdmininstrativas(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        ParametrosCobrancaAdministrativa parametroOrigem = parametroCobrancaAdministrativaFacade.parametrosCobrancaAdministrativaPorExercicio(origem);
        ParametrosCobrancaAdministrativa parametroDestino = parametroCobrancaAdministrativaFacade.parametrosCobrancaAdministrativaPorExercicio(destino);
        assistenteBarraProgresso.setTotal(1);
        assistenteBarraProgresso.removerProcessoDoAcompanhamento();
        if (parametroOrigem != null && parametroDestino == null) {
            parametroDestino = new ParametrosCobrancaAdministrativa();
            parametroDestino.setExercicio(destino);
            parametroDestino.setDoctoImobiliarioAviso(parametroOrigem.getDoctoImobiliarioAviso());
            parametroDestino.setDoctoEconomicoAviso(parametroOrigem.getDoctoEconomicoAviso());
            parametroDestino.setDoctoRuralAviso(parametroOrigem.getDoctoRuralAviso());
            parametroDestino.setDoctoContribPFAviso(parametroOrigem.getDoctoContribPFAviso());
            parametroDestino.setDoctoContribPJAviso(parametroOrigem.getDoctoContribPJAviso());
            parametroDestino.setDoctoImobiliarioNotificacao(parametroOrigem.getDoctoImobiliarioNotificacao());
            parametroDestino.setDoctoEconomicoNotificacao(parametroOrigem.getDoctoEconomicoNotificacao());
            parametroDestino.setDoctoRuralNotificacao(parametroOrigem.getDoctoRuralNotificacao());
            parametroDestino.setDoctoContribPFNotificacao(parametroOrigem.getDoctoContribPFNotificacao());
            parametroDestino.setDoctoContribPJNotificacao(parametroOrigem.getDoctoContribPJNotificacao());
            parametroDestino.setDiretorDepartamento(parametroOrigem.getDiretorDepartamento());
            parametroDestino.setChefeDaDivisao(parametroOrigem.getChefeDaDivisao());
            parametroDestino.setCargo(parametroOrigem.getCargo());
            parametroDestino.setPortaria(parametroOrigem.getPortaria());
            parametroDestino.setDiasAposNotificacao(parametroOrigem.getDiasAposNotificacao());
            assistenteBarraProgresso.conta();
            parametroCobrancaAdministrativaFacade.salvar(parametroDestino);
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirParametrosOpcaoPagamento(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {

        List<OpcaoPagamento> opcaoPagamentoOrigem = opcaoPagamentoFacade.buscarOpcaoPagamentoFixaPorExercicio(origem);
        List<OpcaoPagamento> opcaoPagamentDestino = opcaoPagamentoFacade.buscarOpcaoPagamentoFixaPorExercicio(destino);
        if ((opcaoPagamentoOrigem != null && !opcaoPagamentoOrigem.isEmpty()) && (opcaoPagamentDestino != null && opcaoPagamentDestino.isEmpty())) {
            assistenteBarraProgresso.setTotal(opcaoPagamentoOrigem.size());
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            for (OpcaoPagamento opcaoPagamento : opcaoPagamentoOrigem) {
                OpcaoPagamento op = new OpcaoPagamento();
                op.setDescontos(new ArrayList<DescontoOpcaoPagamento>());
                op.setParcelas(new ArrayList<Parcela>());
                op.setDescricao(opcaoPagamento.getDescricao().replace(origem.getAno().toString(), destino.getAno().toString()));
                op.setNumeroParcelas(opcaoPagamento.getNumeroParcelas());
                op.setValorMinimoParcela(opcaoPagamento.getValorMinimoParcela());
                op.setPermiteAtraso(opcaoPagamento.getPermiteAtraso());
                op.setPromocional(opcaoPagamento.getPromocional());
                op.setTipoParcela(opcaoPagamento.getTipoParcela());
                Calendar c = Calendar.getInstance();
                c.setTime(opcaoPagamento.getDataVerificacaoDebito());
                c.set(Calendar.YEAR, destino.getAno());
                op.setDataVerificacaoDebito(c.getTime());

                criarDescontoOpcaoDePagamento(opcaoPagamento, op);

                criarParcelaFixaOpcaoPagamento(destino, opcaoPagamento, op);

                op = opcaoPagamentoFacade.salvarOpcaoPagamento(op);

                criarOpcaoPagamentoNaDivida(origem, destino, opcaoPagamento, op);
                assistenteBarraProgresso.conta();
            }
        }
        return assistenteBarraProgresso;
    }

    private void criarOpcaoPagamentoNaDivida(Exercicio origem, Exercicio destino, OpcaoPagamento opcaoPagamento, OpcaoPagamento op) {
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcaoPagamentoFacade.buscarOpcaoPagamentoDivida(opcaoPagamento)) {
            Divida divida = dividaFacade.recuperar(opcaoPagamentoDivida.getDivida().getId());
            opcaoPagamentoDivida.setFinalVigencia(DataUtil.getUltimoDiaAno(origem.getAno()));
            OpcaoPagamentoDivida opDivida = new OpcaoPagamentoDivida();
            opDivida.setDivida(divida);
            opDivida.setOpcaoPagamento(op);
            opDivida.setInicioVigencia(DataUtil.getPrimeiroDiaAno(destino.getAno()));
            opDivida.setFinalVigencia(DataUtil.getUltimoDiaAno(destino.getAno()));
            divida.getOpcaoPagamentosDivida().add(opDivida);
            dividaFacade.salvar(divida);
        }
    }

    private void criarParcelaFixaOpcaoPagamento(Exercicio destino, OpcaoPagamento opcaoPagamento, OpcaoPagamento op) {
        for (Parcela parcela : opcaoPagamento.getParcelas()) {
            ParcelaFixa p = new ParcelaFixa();
            p.setOpcaoPagamento(op);
            p.setMensagem(parcela.getMensagem());
            p.setPercentualValorTotal(parcela.getPercentualValorTotal());
            p.setSequenciaParcela(parcela.getSequenciaParcela());
            p.setDataRegistro(new Date());
            Calendar data = Calendar.getInstance();
            data.setTime(parcela.getVencimento());
            data.set(Calendar.YEAR, destino.getAno());
            p.setVencimento(data.getTime());
            op.getParcelas().add(p);
        }
    }

    private void criarDescontoOpcaoDePagamento(OpcaoPagamento opcaoPagamento, OpcaoPagamento op) {
        for (DescontoOpcaoPagamento descontoOpcaoPagamento : opcaoPagamento.getDescontos()) {
            DescontoOpcaoPagamento desconto = new DescontoOpcaoPagamento();
            desconto.setOpcaoPagamento(op);
            desconto.setPercentualDescontoAdimplente(descontoOpcaoPagamento.getPercentualDescontoAdimplente());
            desconto.setTributo(descontoOpcaoPagamento.getTributo());
            desconto.setDataRegistro(new Date());
            desconto.setPercentualDescontoInadimplente(descontoOpcaoPagamento.getPercentualDescontoInadimplente());
            op.getDescontos().add(desconto);
        }
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirParametrosRendasCeasa(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        List<ParametroRendas> listaParametroOrigem = parametroRendasPatrimoniaisFacade.buscarParametroRendasPorExercicio(origem);
        List<ParametroRendas> listaParametroDestino = parametroRendasPatrimoniaisFacade.buscarParametroRendasPorExercicio(destino);
        if (!listaParametroOrigem.isEmpty() && listaParametroDestino.isEmpty()) {
            assistenteBarraProgresso.setTotal(listaParametroOrigem.size());
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            for (ParametroRendas parametroOrigem : listaParametroOrigem) {
                ParametroRendas parametroDestino = new ParametroRendas();
                parametroDestino.setExercicio(destino);

                Calendar dataInicioContrato = Calendar.getInstance();
                dataInicioContrato.setTime(parametroOrigem.getDataInicioContrato());
                dataInicioContrato.set(Calendar.YEAR, destino.getAno());
                parametroDestino.setDataInicioContrato(dataInicioContrato.getTime());

                Calendar dataFimContrato = Calendar.getInstance();
                dataFimContrato.setTime(parametroOrigem.getDataFimContrato());
                dataFimContrato.set(Calendar.YEAR, destino.getAno());
                parametroDestino.setDataFimContrato(dataFimContrato.getTime());

                parametroDestino.setLotacaoVistoriadora(parametroOrigem.getLotacaoVistoriadora());
                parametroDestino.setIndiceEconomico(parametroOrigem.getIndiceEconomico());
                parametroDestino.setQuantidadeMesesVigencia(parametroOrigem.getQuantidadeMesesVigencia());
                parametroDestino.setQuantidadeParcelas(parametroOrigem.getQuantidadeParcelas());
                parametroDestino.setDiaVencimentoParcelas(parametroOrigem.getDiaVencimentoParcelas());
                parametroDestino.setAreaTotal(parametroOrigem.getAreaTotal());

                for (ServicoRateioCeasa servicoRateioCeasa : parametroOrigem.getListaServicoRateioCeasa()) {
                    ServicoRateioCeasa novoServico = new ServicoRateioCeasa();
                    novoServico.setCodigo(servicoRateioCeasa.getCodigo());
                    novoServico.setDescricao(servicoRateioCeasa.getDescricao());
                    novoServico.setParametroCeasa(parametroDestino);
                    novoServico.setValor(servicoRateioCeasa.getValor());
                    parametroDestino.getListaServicoRateioCeasa().add(novoServico);
                }
                parametroRendasPatrimoniaisFacade.salvar(parametroDestino);
                assistenteBarraProgresso.conta();
            }
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirParametrosSimplesNacional(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        ParametroSimplesNacional parametroOrigem = parametroSimplesNacionalFacade.buscarParametroSimplesNacionalPorExercicio(origem);
        ParametroSimplesNacional parametroDestino = parametroSimplesNacionalFacade.buscarParametroSimplesNacionalPorExercicio(destino);
        assistenteBarraProgresso.setTotal(1);
        assistenteBarraProgresso.removerProcessoDoAcompanhamento();

        if (parametroOrigem != null && parametroDestino == null) {
            parametroDestino = new ParametroSimplesNacional();
            parametroDestino.setExercicio(destino);
            parametroDestino.setValorUFMRB(parametroOrigem.getValorUFMRB());

            for (ParamSimplesNacionalDivida divida : parametroOrigem.getDividas()) {
                ParamSimplesNacionalDivida novoParametroDivida = new ParamSimplesNacionalDivida();
                novoParametroDivida.setDivida(divida.getDivida());
                novoParametroDivida.setParametroSimplesNacional(parametroDestino);

                parametroDestino.getDividas().add(novoParametroDivida);
            }
            assistenteBarraProgresso.conta();
            parametroSimplesNacionalFacade.salvar(parametroDestino);
        }
        return assistenteBarraProgresso;
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirParametroRegularizacao(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        ParametroRegularizacao parametroOrigem = parametroRegularizacaoFacade.buscarParametroRegularizacaoPorExercicio(origem);
        ParametroRegularizacao parametroDestino = parametroRegularizacaoFacade.buscarParametroRegularizacaoPorExercicio(destino);
        assistenteBarraProgresso.setTotal(1);
        if (parametroOrigem != null && parametroDestino == null) {
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            ParametroRegularizacao novo = new ParametroRegularizacao();
            novo.setExercicio(destino);
            novo.setSecretaria(parametroOrigem.getSecretaria());
            novo.setSecretario(parametroOrigem.getSecretario());
            novo.setDiretor(parametroOrigem.getDiretor());
            novo.setTributoHabitese(parametroOrigem.getTributoHabitese());
            novo.setTributoTaxaVistoria(parametroOrigem.getTributoTaxaVistoria());
            novo.setDividaAlvara(parametroOrigem.getDividaAlvara());
            novo.setDividaHabitese(parametroOrigem.getDividaHabitese());
            novo.setDividaTaxaVistoria(parametroOrigem.getDividaTaxaVistoria());
            novo.setDiaDoMesWebService(parametroOrigem.getDiaDoMesWebService());
            novo.setPrazoCartaz(parametroOrigem.getPrazoCartaz());
            novo.setPrazoTaxaAlvara(parametroOrigem.getPrazoTaxaAlvara());
            novo.setPrazoTaxaVistoria(parametroOrigem.getPrazoTaxaVistoria());
            novo.setPrazoComunicacaoContribuinte(parametroOrigem.getPrazoComunicacaoContribuinte());
            novo.setServicoConstrucao(parametroOrigem.getServicoConstrucao());
            novo.setTipoDoctoOficial(parametroOrigem.getTipoDoctoOficial());
            novo.setTipoDoctoOficialHabitese(parametroOrigem.getTipoDoctoOficialHabitese());
            novo.setTipoDoctoOficialAlvaraImediato(parametroOrigem.getTipoDoctoOficialAlvaraImediato());
            novo.setValorAliquotaISSQN(parametroOrigem.getValorAliquotaISSQN());
            for (VinculoServicoTributo vinculosServicosTributo : parametroOrigem.getVinculosServicosTributos()) {
                VinculoServicoTributo novoVinculo = new VinculoServicoTributo();
                novoVinculo.setTributo(vinculosServicosTributo.getTributo());
                novoVinculo.setParametroRegularizacao(novo);
                novoVinculo.setServicoConstrucao(vinculosServicosTributo.getServicoConstrucao());
                novo.getVinculosServicosTributos().add(novoVinculo);
            }
            parametroRegularizacaoFacade.salvar(novo);
            assistenteBarraProgresso.conta();
        }
        return assistenteBarraProgresso;
    }

    private void transferirItemServicoConstrucao(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistente) {
        List<ItemServicoConstrucao> itensOrigem = itemServicoConstrucaoFacade.buscarItensServico();
        assistente.setTotal(itensOrigem.size());
        for (ItemServicoConstrucao itemOrigem : itensOrigem) {
            List<FaixaDeValoresSCL> faixasParaAdicionar = Lists.newArrayList();
            for (FaixaDeValoresSCL faixaOrigem : itemOrigem.getListaFaixaDeValoresSCL()) {
                if (faixaOrigem.getExercicio().equals(origem)) {
                    FaixaDeValoresSCL faixaDestino = itemServicoConstrucaoFacade.buscarFaixaDestinoIgualFaixaOrigem(faixaOrigem, destino);
                    if (faixaDestino == null) {
                        faixaDestino = new FaixaDeValoresSCL();
                        faixaDestino.setExercicio(destino);
                        faixaDestino.setItemServicoConstrucao(faixaOrigem.getItemServicoConstrucao());
                        faixaDestino.setTipoMedida(faixaOrigem.getTipoMedida());
                        faixaDestino.setValorUFM(faixaOrigem.getValorUFM());
                        faixaDestino.setAreaInicial(faixaOrigem.getAreaInicial());
                        faixaDestino.setAreaFinal(faixaOrigem.getAreaFinal());
                        faixasParaAdicionar.add(faixaDestino);
                    }
                }
            }
            itemOrigem.getListaFaixaDeValoresSCL().addAll(faixasParaAdicionar);
            itemServicoConstrucaoFacade.salvar(itemOrigem);
            assistente.conta();
        }

    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirHabiteseFaixaDeValores(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        List<HabiteseFaixaDeValores> listaFaixasOrigem = habiteseFaixaDeValoresFacade.buscarFaixasPorExercicio(origem);
        transferirItemServicoConstrucao(origem, destino, assistenteBarraProgresso);
        if (!listaFaixasOrigem.isEmpty()) {
            assistenteBarraProgresso.setTotal(listaFaixasOrigem.size());
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            if (habiteseFaixaDeValoresFacade.buscarFaixasPorExercicio(destino).isEmpty()) {
                for (HabiteseFaixaDeValores faixa : listaFaixasOrigem) {
                    HabiteseFaixaDeValores novo = new HabiteseFaixaDeValores();
                    novo.setExercicio(destino);
                    novo.setDescricao(faixa.getDescricao());
                    novo.setCodigo(habiteseFaixaDeValoresFacade.ultimoCodigoMaisUm());
                    novo.setHabiteseClassesConstrucao(faixa.getHabiteseClassesConstrucao());
                    novo.setHabiteseGruposConstrucao(faixa.getHabiteseGruposConstrucao());

                    for (FaixaDeValoresHL listaF : faixa.getListaFaixaDeValoresHL()) {
                        FaixaDeValoresHL novaFaixa = new FaixaDeValoresHL();
                        novaFaixa.setHabitesePadroesConstrucao(listaF.getHabitesePadroesConstrucao());
                        novaFaixa.setAreaInicial(listaF.getAreaInicial());
                        novaFaixa.setAreaFinal(listaF.getAreaFinal());
                        novaFaixa.setValorUFM(listaF.getValorUFM());
                        novaFaixa.setHabiteseFaixaDeValores(novo);
                        novo.getListaFaixaDeValoresHL().add(novaFaixa);
                    }
                    habiteseFaixaDeValoresFacade.salvar(novo);
                    assistenteBarraProgresso.conta();
                }
            }
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirParametroAlvaraImediato(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        ParametroAlvaraImediato parametroExOrigem = parametroAlvaraImediatoFacade.findByExercicio(origem);
        ParametroAlvaraImediato parametroExDestino = parametroAlvaraImediatoFacade.findByExercicio(destino);
        if (parametroExOrigem != null && parametroExDestino == null) {
            assistenteBarraProgresso.setTotal(1);
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            ParametroAlvaraImediato novoParametro = new ParametroAlvaraImediato();
            novoParametro.setServicoConstrucao(parametroExOrigem.getServicoConstrucao());
            novoParametro.setExercicio(destino);
            novoParametro.setEmail(parametroExOrigem.getEmail());
            novoParametro.setFormulario(parametroExOrigem.getFormulario());
            novoParametro.setDetentorArquivoComposicao(parametroExOrigem.getDetentorArquivoComposicao());
            novoParametro.setTipoImovel(parametroExOrigem.getTipoImovel());
            novoParametro.setUtilizacaoImovel(parametroExOrigem.getUtilizacaoImovel());
            novoParametro = parametroAlvaraImediatoFacade.salvarRetornando(novoParametro);

            List<ParametroAlvaraImediatoCoibicao> novasCoibicoes = Lists.newArrayList();
            if (parametroExOrigem.getCoibicoes() != null && !parametroExOrigem.getCoibicoes().isEmpty()) {
                for (ParametroAlvaraImediatoCoibicao coibicao : parametroExOrigem.getCoibicoes()) {
                    ParametroAlvaraImediatoCoibicao novaCoibicao = new ParametroAlvaraImediatoCoibicao();
                    novaCoibicao.setParametro(novoParametro);
                    novaCoibicao.setResposta(coibicao.getResposta());
                    novaCoibicao.setFormularioCampo(coibicao.getFormularioCampo());
                    novaCoibicao.setRespostaList(coibicao.getRespostaList());
                    novasCoibicoes.add(novaCoibicao);
                }
            }

            novoParametro.setCoibicoes(novasCoibicoes);
            parametroAlvaraImediatoFacade.salvar(novoParametro);
            assistenteBarraProgresso.conta();
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirParametroMalaDiretaIptu(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        ParametroMalaDiretaIPTU parametroExOrigem = parametroMalaDiretaIptuFacade.buscarParametroPeloExercicio(origem);
        ParametroMalaDiretaIPTU parametroExDestino = parametroMalaDiretaIptuFacade.buscarParametroPeloExercicio(destino);
        if (parametroExOrigem != null && parametroExDestino == null) {
            assistenteBarraProgresso.setTotal(1);
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            ParametroMalaDiretaIPTU novoParametro = new ParametroMalaDiretaIPTU();

            novoParametro.setExercicio(destino);
            novoParametro.setPosicaoXDadosContribuinteNaImagem(parametroExOrigem.getPosicaoXDadosContribuinteNaImagem());
            novoParametro.setPosicaoYDadosContribuinteNaImagem(parametroExOrigem.getPosicaoYDadosContribuinteNaImagem());
            novoParametro.setDetentorArquivoComposicao(DetentorArquivoComposicao.clonar(parametroExOrigem.getDetentorArquivoComposicao()));
            novoParametro.setTextoMalaDireta(parametroExOrigem.getTextoMalaDireta());

            parametroMalaDiretaIptuFacade.salvar(novoParametro);
            assistenteBarraProgresso.conta();
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirParametroLicenciamentoAmbiental(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        ParametroLicenciamentoAmbiental parametroExOrigem = parametroLicenciamentoAmbientalFacade.buscarParametroPeloExercicio(origem);
        ParametroLicenciamentoAmbiental parametroExDestino = parametroLicenciamentoAmbientalFacade.buscarParametroPeloExercicio(destino);
        if (parametroExOrigem != null && parametroExDestino == null) {
            assistenteBarraProgresso.setTotal(1);
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            ParametroLicenciamentoAmbiental novoParametro = new ParametroLicenciamentoAmbiental();

            novoParametro.setExercicio(destino);
            novoParametro.setSecretaria(parametroExOrigem.getSecretaria());

            SecretarioLicenciamentoAmbiental novoSecretario = new SecretarioLicenciamentoAmbiental();
            novoSecretario.setSecretario(parametroExOrigem.getSecretario().getSecretario());
            novoSecretario.setArquivo(Arquivo.clonar(parametroExOrigem.getSecretario().getArquivo()));
            novoSecretario.setDecretoNomeacao(parametroExOrigem.getSecretario().getDecretoNomeacao());
            novoParametro.setSecretario(novoSecretario);

            SecretarioLicenciamentoAmbiental novoSecretarioAdj = new SecretarioLicenciamentoAmbiental();
            novoSecretarioAdj.setSecretario(parametroExOrigem.getSecretarioAdjunto().getSecretario());
            novoSecretarioAdj.setArquivo(Arquivo.clonar(parametroExOrigem.getSecretarioAdjunto().getArquivo()));
            novoSecretarioAdj.setDecretoNomeacao(parametroExOrigem.getSecretarioAdjunto().getDecretoNomeacao());
            novoParametro.setSecretarioAdjunto(novoSecretarioAdj);

            SecretarioLicenciamentoAmbiental novoDiretor = new SecretarioLicenciamentoAmbiental();
            novoDiretor.setSecretario(parametroExOrigem.getDiretor().getSecretario());
            novoDiretor.setArquivo(Arquivo.clonar(parametroExOrigem.getDiretor().getArquivo()));
            novoDiretor.setDecretoNomeacao(parametroExOrigem.getDiretor().getDecretoNomeacao());
            novoParametro.setDiretor(novoDiretor);

            parametroLicenciamentoAmbientalFacade.salvar(novoParametro);
            assistenteBarraProgresso.conta();
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirCategoriaAssuntoLicenciamentoAmbiental(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        List<AssuntoLicenciamentoAmbiental> assuntos = assuntoLicenciamentoAmbientalFacade.lista();

        assistenteBarraProgresso.setTotal(1);
        assistenteBarraProgresso.removerProcessoDoAcompanhamento();
        for (AssuntoLicenciamentoAmbiental assunto : assuntos) {
            List<CategoriaAssuntoLicenciamentoAmbiental> categoriasExOrigem = Lists.newArrayList();
            List<CategoriaAssuntoLicenciamentoAmbiental> categoriasExDestino = Lists.newArrayList();

            for (CategoriaAssuntoLicenciamentoAmbiental categoria : assunto.getCategorias()) {
                if (categoria.getExercicio().equals(origem)) {
                    categoriasExOrigem.add(categoria);
                }
                if (categoria.getExercicio().equals(destino)) {
                    categoriasExDestino.add(categoria);
                }
            }

            for (CategoriaAssuntoLicenciamentoAmbiental categoria : categoriasExOrigem) {
                CategoriaAssuntoLicenciamentoAmbiental categoriaDestino = categoriasExDestino.stream().filter(c -> c.getCategoria().equals(categoria.getCategoria()) && c.getExercicio().equals(destino)).findFirst().orElse(null);
                if (categoriaDestino == null) {
                    CategoriaAssuntoLicenciamentoAmbiental novaCategoria = new CategoriaAssuntoLicenciamentoAmbiental();
                    novaCategoria.setCategoria(categoria.getCategoria());
                    novaCategoria.setExercicio(destino);
                    novaCategoria.setValorUFM(categoria.getValorUFM());
                    novaCategoria.setAssunto(assunto);
                    assuntoLicenciamentoAmbientalFacade.salvarCategoria(novaCategoria);
                }
            }
        }
        assistenteBarraProgresso.conta();
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirParametroMarcaFogo(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        ParametroMarcaFogo parametroExOrigem = parametroMarcaFogoFacade.buscarParametroPeloExercicio(origem);
        ParametroMarcaFogo parametroExDestino = parametroMarcaFogoFacade.buscarParametroPeloExercicio(destino);
        if (parametroExOrigem != null && parametroExDestino == null) {
            assistenteBarraProgresso.setTotal(1);
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            ParametroMarcaFogo novoParametro = new ParametroMarcaFogo();

            novoParametro.setExercicio(destino);

            List<TaxaMarcaFogo> novasTaxas = Lists.newArrayList();
            if (parametroExOrigem.getTaxas() != null && !parametroExOrigem.getTaxas().isEmpty()) {
                for (TaxaMarcaFogo taxa : parametroExOrigem.getTaxas()) {
                    TaxaMarcaFogo novaTaxa = new TaxaMarcaFogo();
                    novaTaxa.setParametroMarcaFogo(novoParametro);
                    novaTaxa.setTipoEmissao(taxa.getTipoEmissao());
                    novaTaxa.setTributo(taxa.getTributo());
                    novasTaxas.add(novaTaxa);
                }
            }

            List<DocumentoMarcaFogo> novosDocumentos = Lists.newArrayList();
            if (parametroExOrigem.getDocumentos() != null && !parametroExOrigem.getDocumentos().isEmpty()) {
                for (DocumentoMarcaFogo documento : parametroExOrigem.getDocumentos()) {
                    DocumentoMarcaFogo novoDocumento = new DocumentoMarcaFogo();
                    novoDocumento.setParametroMarcaFogo(novoParametro);
                    novoDocumento.setNaturezaDocumento(documento.getNaturezaDocumento());
                    novoDocumento.setObrigatorio(documento.getObrigatorio());
                    novoDocumento.setExtensoesPermitidas(documento.getExtensoesPermitidas());
                    novoDocumento.setDescricao(documento.getDescricao());
                    novoDocumento.setAtivo(documento.getAtivo());
                    novosDocumentos.add(novoDocumento);
                }
            }

            List<CertidaoMarcaFogo> novasCertidoes = Lists.newArrayList();
            if (parametroExOrigem.getCertidoes() != null && !parametroExOrigem.getCertidoes().isEmpty()) {
                for (CertidaoMarcaFogo certidao : parametroExOrigem.getCertidoes()) {
                    CertidaoMarcaFogo novaCertidao = new CertidaoMarcaFogo();
                    novaCertidao.setParametroMarcaFogo(novoParametro);
                    novaCertidao.setTipoEmissao(certidao.getTipoEmissao());
                    novaCertidao.setTipoDoctoOficial(certidao.getTipoDoctoOficial());
                    novasCertidoes.add(novaCertidao);
                }
            }

            novoParametro.setTaxas(novasTaxas);
            novoParametro.setDocumentos(novosDocumentos);
            novoParametro.setCertidoes(novasCertidoes);
            parametroMarcaFogoFacade.salvar(novoParametro);
            assistenteBarraProgresso.conta();
        }
        return assistenteBarraProgresso;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteBarraProgresso transferirParametroInformacoesRBTrans(Exercicio origem, Exercicio destino, AssistenteBarraProgresso assistenteBarraProgresso) {
        ParametrosInformacoesRBTrans parametroExOrigem = parametroInformacaoRBTransFacade.buscarParametroPeloExercicio(origem);
        ParametrosInformacoesRBTrans parametroExDestino = parametroInformacaoRBTransFacade.buscarParametroPeloExercicio(destino);
        if (parametroExOrigem != null && parametroExDestino == null) {
            assistenteBarraProgresso.setTotal(1);
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            ParametrosInformacoesRBTrans novoParametro = new ParametrosInformacoesRBTrans();

            novoParametro.setExercicio(destino);
            novoParametro.setSecretaria(parametroExOrigem.getSecretaria());
            novoParametro.setSecretario(parametroExOrigem.getSecretario());
            novoParametro.setCargoSecretario(parametroExOrigem.getCargoSecretario());
            novoParametro.setDecretoNomeacao(parametroExOrigem.getDecretoNomeacao());
            novoParametro.setDepartamento(parametroExOrigem.getDepartamento());
            novoParametro.setResponsavel(parametroExOrigem.getResponsavel());
            novoParametro.setCargoResponsavel(parametroExOrigem.getCargoResponsavel());
            novoParametro.setCnpj(parametroExOrigem.getCnpj());
            novoParametro.setEmail(parametroExOrigem.getEmail());
            novoParametro.setEndereco(parametroExOrigem.getEndereco());
            novoParametro.setTelFax(parametroExOrigem.getTelFax());
            novoParametro.setCep(parametroExOrigem.getCep());
            novoParametro.setDetentorArquivoComposicao(DetentorArquivoComposicao.clonar(parametroExOrigem.getDetentorArquivoComposicao()));

            parametroInformacaoRBTransFacade.salvar(novoParametro);
            assistenteBarraProgresso.conta();
        }
        return assistenteBarraProgresso;
    }
}
