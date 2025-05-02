package br.com.webpublico.negocios;

import br.com.webpublico.BarCode;
import br.com.webpublico.DateUtils;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.tributario.arrecadacao.CalculoExecutorDepoisDePagar;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcDamDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcParcelaValorDividaDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import br.com.webpublico.ws.model.WSItbi;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class CalculoITBIFacade extends CalculoExecutorDepoisDePagar<ProcessoCalculoITBI> {

    private static final BigDecimal CEM = new BigDecimal(100);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TipoIsencaoITBIFacade tipoIsencaoITBIFacade;
    @EJB
    private CadastroFacade cadastroFacade;
    @EJB
    private TipoNegociacaoFacade tipoNegociacaoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ParametroITBIFacade parametroITBIFacade;
    @EJB
    private TributoTaxasDividasDiversasFacade tributoTaxasDividasDiversasFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private GeraValorDividaITBI geraValorDividaITBI;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private MalaDiretaGeralFacade malaDiretaGeralFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private ComunicaSofPlanFacade comunicaSofPlanFacade;
    @EJB
    private InscricaoDividaAtivaFacade inscricaoDividaAtivaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private ParametroParcelamentoFacade parametroParcelamentoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ArrecadacaoFacade arrecadacaoFacade;
    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;
    @EJB
    private CompensacaoFacade compensacaoFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private BloqueioTransferenciaProprietarioFacade bloqueioTransferenciaProprietarioFacade;

    public CalculoITBIFacade() {
        super(ProcessoCalculoITBI.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BloqueioTransferenciaProprietarioFacade getBloqueioTransferenciaProprietarioFacade() {
        return bloqueioTransferenciaProprietarioFacade;
    }

    public Date buscarDataOperacao() {
        return sistemaFacade.getDataOperacao();
    }

    public Exercicio buscarExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public UsuarioSistema buscarUsuarioSistema() {
        return Util.recuperarUsuarioCorrente();
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    @Override
    public ProcessoCalculoITBI recuperar(Object id) {
        ProcessoCalculoITBI processoCalculoITBI = em.find(ProcessoCalculoITBI.class, id);
        Hibernate.initialize(processoCalculoITBI.getCalculos());
        Hibernate.initialize(processoCalculoITBI.getRetificacoes());
        if (processoCalculoITBI.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(processoCalculoITBI.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        for (CalculoITBI calculo : processoCalculoITBI.getCalculos()) {
            Hibernate.initialize(calculo.getAdquirentesCalculo());
            Hibernate.initialize(calculo.getTransmitentesCalculo());
            Hibernate.initialize(calculo.getArquivos());
            Hibernate.initialize(calculo.getItensCalculo());
            Hibernate.initialize(calculo.getHistoricosLaudo());
            Hibernate.initialize(calculo.getPessoas());
            Hibernate.initialize(calculo.getProprietariosSimulacao());
        }
        return processoCalculoITBI;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AssistenteBarraProgresso processar(AssistenteBarraProgresso assistente) {
        try {
            ProcessoCalculoITBI selecionado = (ProcessoCalculoITBI) assistente.getSelecionado();
            selecionado.setSituacao(SituacaoITBI.PROCESSADO);
            int countIsentos = 0;
            for (CalculoITBI calculo : selecionado.getCalculos()) {
                if (calculo.getIsento()) {
                    countIsentos++;
                }
            }
            if (countIsentos == selecionado.getCalculos().size()) {
                transferirProprietariosCadastro(selecionado, assistente);
            } else {
                assistente.setDescricaoProcesso("Gerando débitos...");
                geraValorDividaITBI.gerarDebito(selecionado, true);
            }
            selecionado = salvarRetornando(selecionado);
            assistente.setSelecionado(selecionado);
        } catch (Exception e) {
            assistente.getMensagensValidacaoFacesUtil().add(new FacesMessage("Erro ao processar ITBI. Detalhes: " + e.getMessage()));
            logger.error("Erro ao processar lançamento de ITBI. ", e);
        }
        return assistente;
    }

    public DAM gerarDAMAgrupado(List<ResultadoParcela> parcelas, Exercicio exercicio, UsuarioSistema usuarioSistema) {
        return geraValorDividaITBI.getDamFacade().gerarDamAgrupado(parcelas, definirVencimentoDAM(parcelas), exercicio, usuarioSistema);
    }

    public Date definirVencimentoDAM(List<ResultadoParcela> parcelas) {
        Date dataVencimento = null;
        for (ResultadoParcela parcela : parcelas) {
            if (parcela.getVencimento().compareTo(new Date()) >= 0) {
                if (dataVencimento == null) {
                    dataVencimento = parcela.getVencimento();
                } else if (dataVencimento.after(parcela.getVencimento())) {
                    dataVencimento = parcela.getVencimento();
                }
            }
        }
        return dataVencimento != null ? dataVencimento : DataUtil.ultimoDiaMes(new Date()).getTime();
    }

    public List<ResultadoParcela> buscarParcelas(List<CalculoITBI> calculos, Integer ano) {
        List<Long> idsCaculo = Lists.newArrayList();
        for (CalculoITBI calculo : calculos) {
            idsCaculo.add(calculo.getId());
        }

        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IGUAL, ano);
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consulta.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, idsCaculo);

        return consulta.executaConsulta().getResultados();
    }

    public void transferirProprietariosCadastro(ProcessoCalculoITBI processoCalculoITBI) {
        transferirProprietariosCadastro(processoCalculoITBI, null);
    }

    public void transferirProprietariosCadastro(ProcessoCalculoITBI processoCalculoITBI, AssistenteBarraProgresso assistente) {
        if (processoCalculoITBI.isImobiliario()) {
            transferirProprietariosCadastroImobiliario(processoCalculoITBI);
        } else {
            transferirProprietariosCadastroRural(processoCalculoITBI, assistente);
        }
    }

    public Map<Integer, List<PropriedadeRural>> simulacaoDasProporcoesRural(CadastroRural cadastro, List<CalculoITBI> calculos) {

        Map<Integer, List<PropriedadeRural>> novasPropriedadesDoCalculoSimulacao = new HashMap<>();
        ordenarCalculos(calculos);
        for (CalculoITBI calculo : calculos) {
            novasPropriedadesDoCalculoSimulacao.put(calculo.getOrdemCalculo(), Lists.newArrayList());
            List<PropriedadeRural> propriedadesQueNaoSaoTransmitentes = Lists.newArrayList();
            List<PropriedadeRural> propriedadesRural = cadastroRuralFacade.recuperarPropriedadesVigentes(cadastro);
            List<PropriedadeRural> propriedadesImovelParaSimulacao = Lists.newArrayList();
            List<PropriedadeRural> adquirentesCalculoAnterior = Lists.newArrayList();

            if (calculo.getOrdemCalculo() > 1) {
                Integer key = calculo.getOrdemCalculo() - 1;
                if (novasPropriedadesDoCalculoSimulacao.containsKey(key)) {
                    adquirentesCalculoAnterior = novasPropriedadesDoCalculoSimulacao.get(key);
                }
            }

            if (!adquirentesCalculoAnterior.isEmpty()) {
                propriedadesImovelParaSimulacao.addAll(adquirentesCalculoAnterior);
            } else {
                for (PropriedadeRural p : propriedadesRural) {
                    PropriedadeRural propriedade = new PropriedadeRural();
                    propriedade.setDataRegistro(p.getDataRegistro());
                    propriedade.setImovel(p.getImovel());
                    propriedade.setInicioVigencia(p.getInicioVigencia());
                    propriedade.setPessoa(p.getPessoa());
                    propriedade.setProporcao(p.getProporcao());
                    propriedade.setTipoProprietario(p.getTipoProprietario());

                    propriedadesImovelParaSimulacao.add(propriedade);
                }
            }

            List<PropriedadeRural> propriedadesQueSaoTransmitentes = Lists.newArrayList();

            for (PropriedadeRural propriedade : propriedadesImovelParaSimulacao) {
                for (TransmitentesCalculoITBI transmitenteCaculo : calculo.getTransmitentesCalculo()) {
                    if (propriedade.getPessoa().equals(transmitenteCaculo.getPessoa())) {
                        propriedadesQueSaoTransmitentes.add(propriedade);
                    }
                }
                if (!propriedadesQueSaoTransmitentes.contains(propriedade)) {
                    propriedadesQueNaoSaoTransmitentes.add(propriedade);
                }
            }

            BigDecimal proporcaoAjustada = calculo.getTotalPercentualAdquirentes().setScale(15, RoundingMode.DOWN)
                .divide(BigDecimal.valueOf(propriedadesQueSaoTransmitentes.isEmpty() ? calculo.getTransmitentesCalculo().size() : propriedadesQueSaoTransmitentes.size()), 15, RoundingMode.DOWN);

            for (PropriedadeRural propriedadeAjustada : propriedadesQueSaoTransmitentes) {

                PropriedadeRural propriedadeSimulacao = new PropriedadeRural();
                propriedadeSimulacao.setDataRegistro(propriedadeAjustada.getDataRegistro());
                propriedadeSimulacao.setImovel(propriedadeAjustada.getImovel());
                propriedadeSimulacao.setInicioVigencia(propriedadeAjustada.getInicioVigencia());
                propriedadeSimulacao.setPessoa(propriedadeAjustada.getPessoa());
                propriedadeSimulacao.setTipoProprietario(propriedadeAjustada.getTipoProprietario());
                propriedadeSimulacao.setFinalVigencia(propriedadeAjustada.getFinalVigencia());

                if (proporcaoAjustada.doubleValue() > propriedadeAjustada.getProporcao() ||
                    propriedadeAjustada.getProporcao().equals(proporcaoAjustada.doubleValue()) ||
                    calculo.getTotalPercentualAdquirentes().compareTo(buscarProporcaoDosProprietarioJaVigentesRural(propriedadesQueSaoTransmitentes)) == 0) {
                    propriedadeSimulacao.setFinalVigencia(new Date());
                    propriedadeSimulacao.setProporcao(0.0);
                } else {
                    propriedadeSimulacao.setProporcao(BigDecimal.valueOf(propriedadeAjustada.getProporcao()).subtract(proporcaoAjustada).setScale(15, RoundingMode.UP).doubleValue());
                    novasPropriedadesDoCalculoSimulacao.get(calculo.getOrdemCalculo()).add(propriedadeSimulacao);
                }
            }


            for (AdquirentesCalculoITBI ad : calculo.getAdquirentesCalculo()) {
                BigDecimal proprocaoJaExisteAdquirente = BigDecimal.ZERO;
                for (PropriedadeRural propriedadesQueNaoSaoTransmitente : propriedadesQueNaoSaoTransmitentes) {
                    if (ad.getAdquirente().equals(propriedadesQueNaoSaoTransmitente.getPessoa())) {
                        proprocaoJaExisteAdquirente = proprocaoJaExisteAdquirente.add(BigDecimal.valueOf(propriedadesQueNaoSaoTransmitente.getProporcao()));
                        propriedadesQueNaoSaoTransmitente.setFinalVigencia(new Date());
                    }
                }
                PropriedadeRural propriedade = new PropriedadeRural();
                propriedade.setDataRegistro(new Date());
                propriedade.setImovel(cadastro);
                propriedade.setInicioVigencia(new Date());
                propriedade.setPessoa(ad.getAdquirente());
                propriedade.setProporcao(ad.getPercentual().add(proprocaoJaExisteAdquirente).doubleValue());
                propriedade.setTipoProprietario(TipoProprietario.COMPRAVENDA);
                novasPropriedadesDoCalculoSimulacao.get(calculo.getOrdemCalculo()).add(propriedade);
            }

            for (PropriedadeRural propriedadesQueNaoSaoTransmitente : propriedadesQueNaoSaoTransmitentes) {
                boolean naoEhAdquirente = true;
                for (AdquirentesCalculoITBI adquirentesCalculoITBI : calculo.getAdquirentesCalculo()) {
                    if (adquirentesCalculoITBI.getAdquirente().equals(propriedadesQueNaoSaoTransmitente.getPessoa())) {
                        naoEhAdquirente = false;
                    }
                }
                if (naoEhAdquirente) {
                    novasPropriedadesDoCalculoSimulacao.get(calculo.getOrdemCalculo()).add(propriedadesQueNaoSaoTransmitente);
                }
            }
        }
        return novasPropriedadesDoCalculoSimulacao;
    }

    public void transferirProprietariosCadastroRural(ProcessoCalculoITBI processo, AssistenteBarraProgresso assistente) {
        processo = recuperar(processo.getId());
        CadastroRural cadastro = em.find(CadastroRural.class, processo.getIdCadastro());
        List<CalculoITBI> calculos = processo.getCalculos();
        ordenarCalculos(calculos);
        CalculoITBI ultimoCalculoITBI = calculos.get(calculos.size() - 1);
        for (CalculoITBI calculo : calculos) {
            if (calculo.getId().equals(ultimoCalculoITBI.getId())) {
                ajustarProprietariosVigentesCadastroRural(cadastro, ultimoCalculoITBI);
            } else {
                registrarProprietariosAnterioresCadastroRural(cadastro, calculos);
            }
        }
    }

    public Map<Integer, List<Propriedade>> simulacaoDasProporcoesImobiliario(CadastroImobiliario cadastro, List<CalculoITBI> calculos) {

        Map<Integer, List<Propriedade>> novasPropriedadesDoCalculoSimulacao = new HashMap<>();
        ordenarCalculos(calculos);
        for (CalculoITBI calculo : calculos) {
            novasPropriedadesDoCalculoSimulacao.put(calculo.getOrdemCalculo(), Lists.newArrayList());
            List<Propriedade> propriedadesQueNaoSaoTransmitentes = Lists.newArrayList();
            List<Propriedade> propriedadesImovel = cadastroImobiliarioFacade.recuperarProprietariosVigentes(cadastro);
            List<Propriedade> propriedadesImovelParaSimulacao = Lists.newArrayList();
            List<Propriedade> adquirentesCalculoAnterior = Lists.newArrayList();

            if (calculo.getOrdemCalculo() > 1) {
                Integer key = calculo.getOrdemCalculo() - 1;
                if (novasPropriedadesDoCalculoSimulacao.containsKey(key)) {
                    adquirentesCalculoAnterior = novasPropriedadesDoCalculoSimulacao.get(key);
                }
            }

            if (!adquirentesCalculoAnterior.isEmpty()) {
                propriedadesImovelParaSimulacao.addAll(adquirentesCalculoAnterior);
            } else {
                for (Propriedade p : propriedadesImovel) {
                    Propriedade propriedade = new Propriedade();
                    propriedade.setDataRegistro(p.getDataRegistro());
                    propriedade.setImovel(p.getImovel());
                    propriedade.setInicioVigencia(p.getInicioVigencia());
                    propriedade.setPessoa(p.getPessoa());
                    propriedade.setProporcao(p.getProporcao());
                    propriedade.setTipoProprietario(p.getTipoProprietario());
                    propriedade.setVeioPorITBI(p.getVeioPorITBI());

                    propriedadesImovelParaSimulacao.add(propriedade);
                }
            }

            List<Propriedade> propriedadesQueSaoTransmitentes = Lists.newArrayList();

            for (Propriedade propriedade : propriedadesImovelParaSimulacao) {
                for (TransmitentesCalculoITBI transmitenteCaculo : calculo.getTransmitentesCalculo()) {
                    if (propriedade.getPessoa().equals(transmitenteCaculo.getPessoa())) {
                        propriedadesQueSaoTransmitentes.add(propriedade);
                    }
                }
                if (!propriedadesQueSaoTransmitentes.contains(propriedade)) {
                    propriedadesQueNaoSaoTransmitentes.add(propriedade);
                }
            }

            BigDecimal proporcaoAjustada = calculo.getTotalPercentualAdquirentes().setScale(15, RoundingMode.DOWN)
                .divide(BigDecimal.valueOf(propriedadesQueSaoTransmitentes.isEmpty() ? calculo.getTransmitentesCalculo().size() : propriedadesQueSaoTransmitentes.size()), 15, RoundingMode.DOWN);

            for (Propriedade propriedadeAjustada : propriedadesQueSaoTransmitentes) {

                Propriedade propriedadeSimulacao = new Propriedade();
                propriedadeSimulacao.setDataRegistro(propriedadeAjustada.getDataRegistro());
                propriedadeSimulacao.setImovel(propriedadeAjustada.getImovel());
                propriedadeSimulacao.setInicioVigencia(propriedadeAjustada.getInicioVigencia());
                propriedadeSimulacao.setPessoa(propriedadeAjustada.getPessoa());
                propriedadeSimulacao.setTipoProprietario(propriedadeAjustada.getTipoProprietario());
                propriedadeSimulacao.setVeioPorITBI(propriedadeAjustada.getVeioPorITBI());
                propriedadeSimulacao.setFinalVigencia(propriedadeAjustada.getFinalVigencia());

                if (proporcaoAjustada.doubleValue() > propriedadeAjustada.getProporcao() ||
                    propriedadeAjustada.getProporcao().equals(proporcaoAjustada.doubleValue()) ||
                    calculo.getTotalPercentualAdquirentes().compareTo(buscarProporcaoDosProprietarioJaVigentesImovel(propriedadesQueSaoTransmitentes)) == 0) {
                    propriedadeSimulacao.setFinalVigencia(new Date());
                    propriedadeSimulacao.setProporcao(0.0);
                } else {
                    propriedadeSimulacao.setProporcao(BigDecimal.valueOf(propriedadeAjustada.getProporcao()).subtract(proporcaoAjustada).setScale(15, RoundingMode.UP).doubleValue());
                    novasPropriedadesDoCalculoSimulacao.get(calculo.getOrdemCalculo()).add(propriedadeSimulacao);
                }
            }

            for (AdquirentesCalculoITBI ad : calculo.getAdquirentesCalculo()) {
                BigDecimal proprocaoJaExisteAdquirente = BigDecimal.ZERO;
                for (Propriedade propriedadesQueNaoSaoTransmitente : propriedadesQueNaoSaoTransmitentes) {
                    if (ad.getAdquirente().equals(propriedadesQueNaoSaoTransmitente.getPessoa())) {
                        proprocaoJaExisteAdquirente = proprocaoJaExisteAdquirente.add(BigDecimal.valueOf(propriedadesQueNaoSaoTransmitente.getProporcao()));
                        propriedadesQueNaoSaoTransmitente.setFinalVigencia(new Date());
                    }
                }
                Propriedade propriedade = new Propriedade();
                propriedade.setDataRegistro(new Date());
                propriedade.setImovel(cadastro);
                propriedade.setInicioVigencia(new Date());
                propriedade.setPessoa(ad.getAdquirente());
                propriedade.setProporcao(ad.getPercentual().add(proprocaoJaExisteAdquirente).doubleValue());
                propriedade.setTipoProprietario(TipoProprietario.COMPRAVENDA);
                propriedade.setVeioPorITBI(true);
                novasPropriedadesDoCalculoSimulacao.get(calculo.getOrdemCalculo()).add(propriedade);
            }

            for (Propriedade propriedadesQueNaoSaoTransmitente : propriedadesQueNaoSaoTransmitentes) {
                boolean naoEhAdquirente = true;
                for (AdquirentesCalculoITBI adquirentesCalculoITBI : calculo.getAdquirentesCalculo()) {
                    if (adquirentesCalculoITBI.getAdquirente().equals(propriedadesQueNaoSaoTransmitente.getPessoa())) {
                        naoEhAdquirente = false;
                        break;
                    }
                }
                if (naoEhAdquirente) {
                    novasPropriedadesDoCalculoSimulacao.get(calculo.getOrdemCalculo()).add(propriedadesQueNaoSaoTransmitente);
                }
            }
        }
        return novasPropriedadesDoCalculoSimulacao;
    }

    private void salvarProprietariosAtuaisImobiliarioParaEstorno(ProcessoCalculoITBI processo, List<Propriedade> propriedadesAtuais) {
        for (Propriedade propriedadeAtual : propriedadesAtuais) {
            PropriedadeOriginalITBI propriedadeOriginal = new PropriedadeOriginalITBI();
            propriedadeOriginal.setProcessoCalculoITBI(processo);
            propriedadeOriginal.setPropriedade(propriedadeAtual);
            em.merge(propriedadeOriginal);
        }
    }

    private void salvarProprietariosAtuaisRuralParaEstorno(ProcessoCalculoITBI processo, List<PropriedadeRural> propriedadesAtuais) {
        for (PropriedadeRural propriedadeAtual : propriedadesAtuais) {
            PropriedadeOriginalITBI propriedadeOriginal = new PropriedadeOriginalITBI();
            propriedadeOriginal.setProcessoCalculoITBI(processo);
            propriedadeOriginal.setPropriedadeRural(propriedadeAtual);
            em.merge(propriedadeOriginal);
        }
    }

    public void ajustarProprietariosVigentesCadastroImobiliario(CadastroImobiliario cadastroImobiliario,
                                                                CalculoITBI ultimoCalculoITBI) {
        List<Propriedade> propriedadesParaTransferencia = Lists.newArrayList();
        for (PropriedadeSimulacaoITBI propriedadeSimulacaoITBI : ultimoCalculoITBI.getProprietariosSimulacao()) {

            Propriedade propriedade = new Propriedade();
            propriedade.setDataRegistro(new Date());
            propriedade.setImovel(cadastroImobiliario);
            propriedade.setInicioVigencia(new Date());
            propriedade.setPessoa(propriedadeSimulacaoITBI.getPessoa());
            propriedade.setProporcao(propriedadeSimulacaoITBI.getProporcao());
            propriedade.setTipoProprietario(TipoProprietario.COMPRAVENDA);
            propriedade.setVeioPorITBI(true);

            propriedadesParaTransferencia.add(propriedade);
        }
        List<Propriedade> propriedadesAtuais = cadastroImobiliarioFacade.recuperarProprietariosVigentes(cadastroImobiliario);
        salvarProprietariosAtuaisImobiliarioParaEstorno(ultimoCalculoITBI.getProcessoCalculoITBI(), propriedadesAtuais);
        for (Propriedade propriedadeParaTransferencia : propriedadesParaTransferencia) {
            Propriedade propriedadeCadastro = null;
            for (Propriedade propriedadeAtual : propriedadesAtuais) {
                if (propriedadeAtual.getPessoa().equals(propriedadeParaTransferencia.getPessoa())) {
                    propriedadeCadastro = propriedadeAtual;
                    break;
                }
            }
            if (propriedadeCadastro != null) {
                propriedadeCadastro.setProporcao(propriedadeParaTransferencia.getProporcao());
                if (((Double) 0.0).equals(propriedadeCadastro.getProporcao())) {
                    propriedadeCadastro.setFinalVigencia(new Date());
                }
                em.merge(propriedadeCadastro);
            } else {
                if (((Double) 0.0).equals(propriedadeParaTransferencia.getProporcao())) {
                    propriedadeParaTransferencia.setFinalVigencia(new Date());
                }
                em.persist(propriedadeParaTransferencia);
            }
        }
        Iterator<Propriedade> propriedadesAtuaisIterador = propriedadesAtuais.iterator();
        while (propriedadesAtuaisIterador.hasNext()) {
            Propriedade propriedadeAtual = propriedadesAtuaisIterador.next();
            Boolean temPropriedade = Boolean.FALSE;
            for (Propriedade propriedadeParaTransferencia : propriedadesParaTransferencia) {
                if (propriedadeParaTransferencia.getPessoa().equals(propriedadeAtual.getPessoa())) {
                    temPropriedade = Boolean.TRUE;
                    break;
                }
            }
            if (!temPropriedade) {
                propriedadeAtual.setFinalVigencia(new Date());
                em.merge(propriedadeAtual);
            }
        }
    }

    public void registrarProprietariosAnterioresCadastroImobiliario(CadastroImobiliario cadastroImobiliario,
                                                                    List<CalculoITBI> calculosITBI) {
        List<Propriedade> propriedadesAnteriores = cadastroImobiliarioFacade.recuperarProprietariosNaoVigentes(cadastroImobiliario);
        ordenarCalculos(calculosITBI);
        for (CalculoITBI calculoITBI : calculosITBI) {
            List<Propriedade> propriedadesTransmitentes = Lists.newArrayList();
            for (TransmitentesCalculoITBI transmitentesCalculoITBI : calculoITBI.getTransmitentesCalculo()) {
                Propriedade propriedade = new Propriedade();
                propriedade.setDataRegistro(new Date());
                propriedade.setImovel(cadastroImobiliario);
                propriedade.setInicioVigencia(new Date());
                propriedade.setFinalVigencia(new Date());
                propriedade.setPessoa(transmitentesCalculoITBI.getPessoa());
                propriedade.setProporcao(transmitentesCalculoITBI.getPercentual().doubleValue());
                propriedade.setTipoProprietario(TipoProprietario.COMPRAVENDA);
                propriedade.setVeioPorITBI(true);
                propriedadesTransmitentes.add(propriedade);
            }
            for (Propriedade propriedadeTransmitente : propriedadesTransmitentes) {
                if (!propriedadeTransmitente.temPropriedade(propriedadesAnteriores, propriedadeTransmitente)) {
                    em.persist(propriedadeTransmitente);
                }
            }
        }
    }

    public void ajustarProprietariosVigentesCadastroRural(CadastroRural cadastroRural,
                                                          CalculoITBI ultimoCalculoITBI) {
        List<PropriedadeRural> propriedadesParaTransferencia = Lists.newArrayList();
        for (PropriedadeSimulacaoITBI propriedadeSimulacaoITBI : ultimoCalculoITBI.getProprietariosSimulacao()) {
            PropriedadeRural propriedade = new PropriedadeRural();
            propriedade.setDataRegistro(new Date());
            propriedade.setImovel(cadastroRural);
            propriedade.setInicioVigencia(new Date());
            propriedade.setPessoa(propriedadeSimulacaoITBI.getPessoa());
            propriedade.setProporcao(propriedadeSimulacaoITBI.getProporcao());
            propriedade.setTipoProprietario(TipoProprietario.COMPRAVENDA);

            propriedadesParaTransferencia.add(propriedade);
        }
        List<PropriedadeRural> propriedadesAtuais = cadastroRuralFacade.recuperarPropriedadesVigentes(cadastroRural);
        salvarProprietariosAtuaisRuralParaEstorno(ultimoCalculoITBI.getProcessoCalculoITBI(), propriedadesAtuais);
        for (PropriedadeRural propriedadeParaTransferencia : propriedadesParaTransferencia) {
            PropriedadeRural propriedadeCadastro = null;
            for (PropriedadeRural propriedadeAtual : propriedadesAtuais) {
                if (propriedadeAtual.getPessoa().equals(propriedadeParaTransferencia.getPessoa())) {
                    propriedadeCadastro = propriedadeAtual;
                    break;
                }
            }
            if (propriedadeCadastro != null) {
                propriedadeCadastro.setProporcao(propriedadeParaTransferencia.getProporcao());
                if (((Double) 0.0).equals(propriedadeCadastro.getProporcao())) {
                    propriedadeCadastro.setFinalVigencia(new Date());
                }
                em.merge(propriedadeCadastro);
            } else {
                if (((Double) 0.0).equals(propriedadeParaTransferencia.getProporcao())) {
                    propriedadeParaTransferencia.setFinalVigencia(new Date());
                }
                em.persist(propriedadeParaTransferencia);
            }
        }
        Iterator<PropriedadeRural> propriedadesAtuaisIterador = propriedadesAtuais.iterator();
        while (propriedadesAtuaisIterador.hasNext()) {
            PropriedadeRural propriedadeAtual = propriedadesAtuaisIterador.next();
            Boolean temPropriedade = Boolean.FALSE;
            for (PropriedadeRural propriedadeParaTransferencia : propriedadesParaTransferencia) {
                if (propriedadeParaTransferencia.getPessoa().equals(propriedadeAtual.getPessoa())) {
                    temPropriedade = Boolean.TRUE;
                    break;
                }
            }
            if (!temPropriedade) {
                propriedadeAtual.setFinalVigencia(new Date());
                em.merge(propriedadeAtual);
            }
        }
    }

    public void registrarProprietariosAnterioresCadastroRural(CadastroRural cadastroRural,
                                                              List<CalculoITBI> calculosITBI) {
        List<PropriedadeRural> propriedadesAnteriores = cadastroRuralFacade.recuperarPropriedadesVigentes(cadastroRural);
        ordenarCalculos(calculosITBI);
        for (CalculoITBI calculoITBI : calculosITBI) {
            List<PropriedadeRural> propriedadesTransmitentes = Lists.newArrayList();
            for (TransmitentesCalculoITBI propriedadeSimulacaoITBI : calculoITBI.getTransmitentesCalculo()) {
                PropriedadeRural propriedade = new PropriedadeRural();
                propriedade.setDataRegistro(new Date());
                propriedade.setImovel(cadastroRural);
                propriedade.setInicioVigencia(new Date());
                propriedade.setPessoa(propriedadeSimulacaoITBI.getPessoa());
                propriedade.setProporcao(propriedadeSimulacaoITBI.getPercentual().doubleValue());
                propriedade.setTipoProprietario(TipoProprietario.COMPRAVENDA);

                propriedadesTransmitentes.add(propriedade);
            }
            for (PropriedadeRural propriedadeSimulacao : propriedadesTransmitentes) {
                if (!propriedadeSimulacao.temPropriedade(propriedadesAnteriores, propriedadeSimulacao)) {
                    propriedadeSimulacao.setFinalVigencia(new Date());
                    em.persist(propriedadeSimulacao);
                }
            }
        }
    }

    public void transferirProprietariosCadastroImobiliario(ProcessoCalculoITBI processo) {
        processo = recuperar(processo.getId());
        CadastroImobiliario cadastro = em.find(CadastroImobiliario.class, processo.getIdCadastro());
        List<CalculoITBI> calculos = processo.getCalculos();
        ordenarCalculos(calculos);
        CalculoITBI ultimoCalculoITBI = calculos.get(calculos.size() - 1);
        for (CalculoITBI calculo : calculos) {
            if (calculo.getId().equals(ultimoCalculoITBI.getId())) {
                ajustarProprietariosVigentesCadastroImobiliario(cadastro, ultimoCalculoITBI);
            } else {
                registrarProprietariosAnterioresCadastroImobiliario(cadastro, calculos);
            }
        }
    }

    private void ordenarCalculos(List<CalculoITBI> calculos) {
        Collections.sort(calculos, new Comparator<CalculoITBI>() {
            @Override
            public int compare(CalculoITBI o1, CalculoITBI o2) {
                return ComparisonChain.start().compare(o1.getOrdemCalculo(), o2.getOrdemCalculo()).result();
            }
        });
    }

    public Long buscarSequeciaCodigoITBI(ProcessoCalculoITBI processoCalculoITBI) {
        try {
            String sql = " select coalesce(pcitbi.codigo, citbi.sequencia, 0) from processocalculoitbi pcitbi " +
                " inner join processocalculo pc on pcitbi.id = pc.id " +
                " inner join calculoitbi citbi on pcitbi.id = citbi.processocalculoitbi_id " +
                " where pc.exercicio_id = :exercicio " +
                " order by coalesce(pcitbi.codigo, citbi.sequencia, 0) desc fetch first 1 rows only ";

            Query q = em.createNativeQuery(sql);
            q.setParameter("exercicio", processoCalculoITBI.getExercicio().getId());

            List<BigDecimal> codigos = q.getResultList();

            if (codigos != null && !codigos.isEmpty()) {
                BigDecimal codigo = codigos.get(0);
                if (codigo != null) {
                    return codigo.longValue() + 1;
                }
            }
            return 1L;
        } catch (Exception e) {
            logger.error("Erro ao buscar codigo sequencial do processo de calculo de ITBI. ", e);
        }
        return 1L;
    }

    public List<TipoIsencaoITBI> buscarTiposDeIsencaoITBI(String parte) {
        return tipoIsencaoITBIFacade.listaFiltrando(parte.trim(), "nome", "descricao");
    }

    public List<? extends Cadastro> buscarCadastroPorTipo(String parte, boolean isImobiliario) {
        return isImobiliario ? cadastroFacade.getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "inscricaoCadastral") :
            cadastroFacade.getCadastroRuralFacade().listaFiltrando(parte.trim(), "codigo", "nomePropriedade");
    }

    public List<TipoNegociacao> buscarTiposNegociacaoAtivas() {
        return tipoNegociacaoFacade.buscarTiposNegociacaoAtivas();
    }

    public List<VOProprietarioITBI> buscarProprietarios(boolean isImobiliario, Long idCadastro) {
        String sql = " select p.id as idpessoa, prop.imovel_id, coalesce(pf.nome, pj.razaosocial), coalesce(pf.cpf, pj.cnpj) " +
            " from " + (isImobiliario ? "propriedade" : "propriedaderural") + " prop " +
            " inner join pessoa p on prop.pessoa_id = p.id " +
            " left join pessoajuridica pj on p.id = pj.id " +
            " left join pessoafisica pf on p.id = pf.id " +
            " where prop.imovel_id = :idCadastro " +
            " and (prop.finalvigencia is null or trunc(prop.finalvigencia) > trunc(current_date))  " +
            " order by prop.proporcao ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);

        List<Object[]> dados = q.getResultList();
        List<VOProprietarioITBI> proprietarios = Lists.newArrayList();

        if (dados != null && !dados.isEmpty()) {
            for (Object[] obj : dados) {
                VOProprietarioITBI proprietario = new VOProprietarioITBI();
                proprietario.setIdPessoa(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
                proprietario.setIdCadastro(obj[1] != null ? ((BigDecimal) obj[1]).longValue() : null);
                proprietario.setNomeRazaoSocial(obj[2] != null ? (String) obj[2] : "");
                proprietario.setCpfCnpj(obj[3] != null ? formatarCpfCnpj((String) obj[3]) : "");

                proprietarios.add(proprietario);
            }
        }
        return proprietarios;
    }

    public VODadosCadastroITBI montarDadosProprietarioImobiliario(Long idCadastro) {
        String sql = " select setor.codigo as codigo_setor, quadra.codigo as codigo_quadra, lote.codigolote as codigo_lote, " +
            "       lote.descricaoloteamento as descricao_lote, tl.descricao as tipo_logradouro, log.nome as logradouro, " +
            "       ci.numero, lote.complemento as complemento_lote, ci.complementoendereco as complemento, " +
            "       bairro.descricao as bairro, lote.arealote as area_terreno, ci.areatotalconstruida as area_construida, " +
            "       (select ev.valor from eventocalculobci ev " +
            "        inner join eventoconfiguradobci evconfig on evconfig.id = ev.eventocalculo_id " +
            "        inner join eventocalculo calc on evconfig.eventocalculo_id = calc.id " +
            "        where ev.cadastroimobiliario_id = ci.id " +
            "        and calc.identificacao = :eventoCalculo " +
            "        order by ev.id desc fetch first 1 rows only)  as valor_venal " +
            " from cadastroimobiliario ci " +
            " left join lote on ci.lote_id = lote.id " +
            " left join setor on lote.setor_id = setor.id " +
            " left join quadra on lote.quadra_id = quadra.id " +
            " left join testada on lote.id = testada.lote_id " +
            " left join face on testada.face_id = face.id " +
            " left join logradourobairro lb on face.logradourobairro_id = lb.id " +
            " left join logradouro log on lb.logradouro_id = log.id " +
            " left join tipologradouro tl on log.tipologradouro_id = tl.id " +
            " left join bairro on lb.bairro_id = bairro.id " +
            " where ci.id = :idCadastro " +
            " and testada.id = (select t.id from testada t " +
            "                   where t.lote_id = lote.id " +
            "                   order by t.principal desc fetch first 1 rows only) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);
        q.setParameter("eventoCalculo", "valorVenalImovelBCI");

        List<Object[]> dados = q.getResultList();

        if (dados != null && !dados.isEmpty()) {
            Object[] obj = dados.get(0);
            VODadosCadastroITBI proprietario = new VODadosCadastroITBI();

            proprietario.setCodigoSetor(obj[0] != null ? (String) obj[0] : "");
            proprietario.setCodigoQuadra(obj[1] != null ? (String) obj[1] : "");
            proprietario.setCodigoLote(obj[2] != null ? (String) obj[2] : "");
            proprietario.setDescricaoLote(obj[3] != null ? (String) obj[3] : "");
            proprietario.setTipoLogradouro(obj[4] != null ? (String) obj[4] : "");
            proprietario.setLogradouro(obj[5] != null ? (String) obj[5] : "");
            proprietario.setNumero(obj[6] != null ? (String) obj[6] : "");
            proprietario.setComplementoLote(obj[7] != null ? (String) obj[7] : "");
            proprietario.setComplemento(obj[8] != null ? (String) obj[8] : "");
            proprietario.setBairro(obj[9] != null ? (String) obj[9] : "");
            proprietario.setAreaTerreno(obj[10] != null ? (BigDecimal) obj[10] : BigDecimal.ZERO);
            proprietario.setAreaConstruida(obj[11] != null ? (BigDecimal) obj[11] : BigDecimal.ZERO);
            proprietario.setValorVenal(obj[12] != null ? (BigDecimal) obj[12] : BigDecimal.ZERO);
            proprietario.setCodigoPropriedade("");
            proprietario.setNomePropriedade("");

            return proprietario;
        }
        return null;
    }

    public VODadosCadastroITBI montarDadosProprietarioRural(Long idCadastro) {
        String sql = " SELECT CR.CODIGO as codigo, " +
            " CR.NOMEPROPRIEDADE as nome_propriedade, " +
            " CR.LOCALIZACAOLOTE as localizacao, " +
            " CR.AREALOTE as area_lote, " +
            " CR.VALORVENALLOTE as valor_venal " +
            " FROM CADASTRORURAL CR " +
            " WHERE CR.ID = :idCadastro ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);

        List<Object[]> dados = q.getResultList();

        if (dados != null && !dados.isEmpty()) {
            Object[] obj = dados.get(0);
            VODadosCadastroITBI proprietario = new VODadosCadastroITBI();

            proprietario.setCodigoPropriedade(obj[0] != null ? obj[0].toString() : "");
            proprietario.setNomePropriedade(obj[1] != null ? (String) obj[1] : "");
            proprietario.setLogradouro(obj[2] != null ? (String) obj[2] : "");
            proprietario.setAreaTerreno(obj[3] != null ? new BigDecimal(StringUtil.retornaApenasNumerosComPontoVirgula(obj[3].toString().replace(",", "."))) : BigDecimal.ZERO);
            proprietario.setValorVenal(obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);

            proprietario.setCodigoSetor("");
            proprietario.setCodigoQuadra("");
            proprietario.setCodigoLote("");
            proprietario.setDescricaoLote("");
            proprietario.setTipoLogradouro("");
            proprietario.setNumero("");
            proprietario.setComplementoLote("");
            proprietario.setComplemento("");
            proprietario.setBairro("");
            proprietario.setAreaConstruida(BigDecimal.ZERO);


            return proprietario;
        }
        return null;
    }

    private String formatarCpfCnpj(String cpfCnpj) {
        if (!Strings.isNullOrEmpty(cpfCnpj)) {
            if (StringUtil.retornaApenasNumeros(cpfCnpj).length() == 11) {
                return Util.formatarCpf(cpfCnpj);
            } else {
                return Util.formatarCnpj(cpfCnpj);
            }
        }
        return "";
    }

    public List<PessoaFisica> buscarAuditorFiscal(String parte) {
        return pessoaFacade.getPessoaFisicaFacade().listaFiscalTributario(parte);
    }

    public boolean hasOutraPessoaComMesmoDocto(Pessoa pessoa) {
        return pessoa.isPessoaFisica() ? pessoaFacade.hasOutraPessoaComMesmoCpf((PessoaFisica) pessoa, true) :
            pessoaFacade.hasOutraPessoaComMesmoCnpj((PessoaJuridica) pessoa, true);
    }

    public ParametrosITBI recuperarParametroITBIVigente(Exercicio exercicio, TipoITBI tipoITBI) {
        return parametroITBIFacade.getParametroVigente(exercicio, tipoITBI);
    }

    public List<ParametrosFuncionarios> buscarFuncionariosPorFuncao(ParametrosITBI parametro, TipoFuncaoParametrosITBI funcao) {
        return parametroITBIFacade.getFuncaoParametroITBIFacade().buscarFuncionariosPorFuncao(parametro, funcao, "");
    }

    public boolean isCadastroComDebitos(Cadastro cadastro, boolean aVencer) {
        List<ResultadoParcela> parcelas = buscarParcelasDoCadastro(cadastro);
        List<ResultadoParcela> debitos = Lists.newArrayList();

        for (ResultadoParcela parcela : parcelas) {
            if ((parcela.getVencido() && !Calculo.TipoCalculo.TAXAS_DIVERSAS.equals(parcela.getTipoCalculoEnumValue()) &&
                !Calculo.TipoCalculo.DOCTO_OFICIAL.equals(parcela.getTipoCalculoEnumValue()) && !Calculo.TipoCalculo.NFS_AVULSA.equals(parcela.getTipoCalculoEnumValue()))) {
                debitos.add(parcela);
            } else if (parcela.getVencido() && Calculo.TipoCalculo.TAXAS_DIVERSAS.equals(parcela.getTipoCalculoEnumValue())) {
                if (tributoTaxasDividasDiversasFacade.parcelaComAlvaraBloqueada(parcela.getIdParcela())) {
                    debitos.add(parcela);
                }
            } else {
                if (aVencer) {
                    debitos.add(parcela);
                }
            }
        }
        return !debitos.isEmpty();
    }

    public boolean isCadastroComDebitosAjuizados(Cadastro cadastro) {
        List<ResultadoParcela> listaDebitos = buscarParcelasDoCadastro(cadastro);
        List<ResultadoParcela> debitos = Lists.newArrayList();
        for (ResultadoParcela p : listaDebitos) {
            if (p.getDividaAtivaAjuizada()) {
                debitos.add(p);
            }
        }
        return !debitos.isEmpty();
    }

    public boolean isCadastroPossuiDebitosItbi(Cadastro cadastro) {
        return !buscarParcelasItbiDoCadastro(cadastro).isEmpty();
    }

    private List<ResultadoParcela> buscarParcelasDoCadastro(Cadastro cadastro) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cadastro.getId());
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        return consultaParcela.executaConsulta().getResultados();
    }

    private List<ResultadoParcela> buscarParcelasItbiDoCadastro(Cadastro cadastro) {
        List<Long> idsCalculos = buscarIdsCalculosDosCadastros(cadastro);
        if (!idsCalculos.isEmpty()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, idsCalculos);
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
            consultaParcela.addParameter(ConsultaParcela.Campo.TIPO_CALCULO, ConsultaParcela.Operador.IGUAL, Calculo.TipoCalculo.ITBI);
            return consultaParcela.executaConsulta().getResultados();
        }
        return Lists.newArrayList();
    }

    private List<Long> buscarIdsCalculosDosCadastros(Cadastro cadastro) {
        String sql = " select c.id from calculoitbi c" +
            " where (c.cadastroimobiliario_id = :idCadastro or c.cadastrorural_id = :idCadastro) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", cadastro.getId());

        List<BigDecimal> ids = q.getResultList();
        List<Long> idsCalculos = Lists.newArrayList();
        if (ids != null && !ids.isEmpty()) {
            for (BigDecimal id : ids) {
                idsCalculos.add(id.longValue());
            }
        }
        return idsCalculos;
    }

    public boolean hasTransferir(CalculoITBI calculoITBI) {
        String sql = " select pvd.id from parcelavalordivida pvd " +
            " inner join valordivida vd on pvd.valordivida_id = vd.id" +
            " inner join calculoitbi citbi on vd.calculo_id = citbi.id" +
            " inner join situacaoparcelavalordivida spvd on pvd.situacaoatual_id = spvd.id " +
            " where spvd.situacaoparcela = :aberto" +
            " and citbi.id = :idCalculo ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("aberto", SituacaoParcela.EM_ABERTO.name());
        q.setParameter("idCalculo", calculoITBI.getId());

        return q.getResultList().isEmpty();
    }

    private List<Pessoa> recuperarProprietariosImovel(CadastroImobiliario cadastro) {
        List<Pessoa> proprietarios = Lists.newArrayList();
        for (Propriedade propriedade : cadastro.getPropriedadeVigente()) {
            proprietarios.add(propriedade.getPessoa());
        }
        return proprietarios;
    }

    private List<Pessoa> recuperarProprietariosRurais(CadastroRural cadastro) {
        List<Pessoa> proprietarios = Lists.newArrayList();
        for (PropriedadeRural propriedades : cadastro.getPropriedadesAtuais()) {
            proprietarios.add(propriedades.getPessoa());
        }
        return proprietarios;
    }

    private List<Pessoa> recuperarPessoasDaPropriedadeRural(CadastroRural cr) {
        List<Pessoa> proprietarios = Lists.newArrayList();
        for (PropriedadeRural propriedades : cr.getPropriedade()) {
            proprietarios.add(propriedades.getPessoa());
        }
        return proprietarios;
    }

    private List<Pessoa> recuperarPessoasAdquirentes(CalculoITBI calculoITBI) {
        List<Pessoa> adquirentes = Lists.newArrayList();
        for (AdquirentesCalculoITBI ad : calculoITBI.getAdquirentesCalculo()) {
            adquirentes.add(ad.getAdquirente());
        }
        return adquirentes;
    }

    private BigDecimal buscarProporcaoDosProprietarioJaVigentesImovel(List<Propriedade> propriedadesImovel) {
        BigDecimal proporcaoDosProprietarioJaVigentes = BigDecimal.ZERO;
        for (Propriedade propriedade : propriedadesImovel) {
            proporcaoDosProprietarioJaVigentes = proporcaoDosProprietarioJaVigentes.add(BigDecimal.valueOf(propriedade.getProporcao()));
        }
        return proporcaoDosProprietarioJaVigentes;
    }

    private BigDecimal buscarProporcaoDosProprietarioJaVigentesRural(List<PropriedadeRural> propriedadesRurais) {
        BigDecimal proporcaoDosProprietarioJaVigentes = BigDecimal.ZERO;
        for (PropriedadeRural propriedade : propriedadesRurais) {
            proporcaoDosProprietarioJaVigentes = proporcaoDosProprietarioJaVigentes.add(BigDecimal.valueOf(propriedade.getProporcao()));
        }
        return proporcaoDosProprietarioJaVigentes;
    }

    private void arredondarProporcao(Cadastro cadastro) {
        BigDecimal proporcaoAtual;
        if (cadastro instanceof CadastroImobiliario) {
            CadastroImobiliario imovel = cadastroImobiliarioFacade.recuperarSemCalcular(cadastro.getId());
            List<Propriedade> proprietariosVigentes = cadastroImobiliarioFacade.recuperarProprietariosVigentes(imovel);
            proporcaoAtual = buscarProporcaoDosProprietarioJaVigentesImovel(proprietariosVigentes);
            ajustarProporcaoQuandoMenorQueCemPorcentoImovel(proporcaoAtual, imovel);
            ajustarProporcaoQuandoMaiorQueCemPorcentoImovel(proporcaoAtual, imovel);
        } else if (cadastro instanceof CadastroRural) {
            CadastroRural rural = cadastroRuralFacade.recuperar(cadastro.getId());
            List<PropriedadeRural> proprietariosVigentes = cadastroRuralFacade.recuperarPropriedadesVigentes(rural);
            proporcaoAtual = buscarProporcaoDosProprietarioJaVigentesRural(proprietariosVigentes);
            ajustarProporcaoMenorQueCemPorcentoQuandoRural(proporcaoAtual, rural);
            ajudarProporcaoQuandoMaiorQueCemPorcentoRural(proporcaoAtual, rural);
        }
    }

    private void ajustarProporcaoQuandoMenorQueCemPorcentoImovel(BigDecimal proporcaoAtual, CadastroImobiliario imovel) {
        if (proporcaoAtual.compareTo(CEM) < 0) {
            BigDecimal diferencaProporcao = CEM.subtract(proporcaoAtual).setScale(2, RoundingMode.HALF_UP);
            Propriedade primeiroProprietarioImovel = buscarPrimeiroProprietarioVigenteImovel(imovel);
            if (primeiroProprietarioImovel != null) {
                BigDecimal proporcaoPrimeiroProprietarioImovel = BigDecimal.valueOf(primeiroProprietarioImovel.getProporcao());
                primeiroProprietarioImovel.setProporcao((proporcaoPrimeiroProprietarioImovel.add(diferencaProporcao)).doubleValue());
                em.merge(primeiroProprietarioImovel);
            }
        }
    }

    private void ajustarProporcaoQuandoMenorQueCemPorcentoImovelSimulacao(List<Propriedade> propriedades) {
        BigDecimal proporcaoAtual = BigDecimal.ZERO;
        for (Propriedade propriedade : propriedades) {
            proporcaoAtual = proporcaoAtual.add(BigDecimal.valueOf(propriedade.getProporcao()));
        }
        if (proporcaoAtual.compareTo(CEM) < 0) {
            BigDecimal diferencaProporcao = CEM.subtract(proporcaoAtual).setScale(2, RoundingMode.HALF_UP);
            ordenarPropriedadesPelaVigencia(propriedades);
            if (propriedades.get(0) != null) {
                BigDecimal proporcaoPrimeiroProprietarioImovel = BigDecimal.valueOf(propriedades.get(0).getProporcao());
                propriedades.get(0).setProporcao((proporcaoPrimeiroProprietarioImovel.add(diferencaProporcao)).doubleValue());
            }
        }
    }

    private void ordenarPropriedadesPelaVigencia(List<Propriedade> propriedades) {
        propriedades.sort(new Comparator<Propriedade>() {
            @Override
            public int compare(Propriedade o1, Propriedade o2) {
                return o1.getInicioVigencia().compareTo(o2.getInicioVigencia());
            }
        });
    }

    private void ordenarPropriedadesRuralPelaVigencia(List<PropriedadeRural> propriedades) {
        propriedades.sort(new Comparator<PropriedadeRural>() {
            @Override
            public int compare(PropriedadeRural o1, PropriedadeRural o2) {
                return o1.getInicioVigencia().compareTo(o2.getInicioVigencia());
            }
        });
    }

    public Pessoa buscarPessoaDoImovel(Cadastro cadastro) {
        if (cadastro instanceof CadastroImobiliario) {
            Propriedade propriedade = buscarPrimeiroProprietarioVigenteImovel((CadastroImobiliario) cadastro);
            return propriedade.getPessoa();
        } else if (cadastro instanceof CadastroRural) {
            PropriedadeRural propriedadeRural = buscarPrimeiroProprietarioVigenteRural((CadastroRural) cadastro);
            return propriedadeRural.getPessoa();
        }
        return null;
    }

    public Propriedade buscarPrimeiroProprietarioVigenteImovel(CadastroImobiliario imovel) {
        String sql = " select propriedade.* from propriedade " +
            " where imovel_id = :imovel_id " +
            " and to_date(:dataAtual, 'dd/MM/yyyy') between trunc(iniciovigencia) " +
            " and trunc(coalesce(finalvigencia, to_date(:dataAtual, 'dd/MM/yyyy'))) " +
            " order by iniciovigencia ";

        Query q = em.createNativeQuery(sql, Propriedade.class);
        q.setParameter("imovel_id", imovel.getId());
        q.setParameter("dataAtual", DataUtil.getDataFormatada(new Date()));

        List<Propriedade> propriedades = q.getResultList();
        return (propriedades != null && !propriedades.isEmpty()) ? propriedades.get(0) : null;
    }

    public PropriedadeRural buscarPrimeiroProprietarioVigenteRural(CadastroRural rural) {
        String sql = " select propriedaderural.* from propriedaderural " +
            " where imovel_id = :imovel_id " +
            " and to_date(:dataAtual, 'dd/MM/yyyy') between trunc(iniciovigencia) and trunc(coalesce(finalvigencia, to_date(:dataAtual, 'dd/MM/yyyy'))) " +
            " order by iniciovigencia ";
        Query q = em.createNativeQuery(sql, PropriedadeRural.class);
        q.setParameter("imovel_id", rural.getId());
        q.setParameter("dataAtual", DataUtil.getDataFormatada(new Date()));

        List<PropriedadeRural> propriedades = q.getResultList();
        return (propriedades != null && !propriedades.isEmpty()) ? propriedades.get(0) : null;
    }

    public LaudoAvaliacaoITBI recuperarLaudo(ProcessoCalculoITBI processoCalculoITBI) {
        if (processoCalculoITBI.getId() != null) {
            String sql = " select laudo.* from laudoavaliacaoitbi laudo " +
                " where laudo.processocalculoitbi_id = :idProcesso ";

            Query q = em.createNativeQuery(sql, LaudoAvaliacaoITBI.class);
            q.setParameter("idProcesso", processoCalculoITBI.getId());

            List<LaudoAvaliacaoITBI> laudos = q.getResultList();
            if (laudos != null && !laudos.isEmpty()) {
                LaudoAvaliacaoITBI laudo = laudos.get(0);
                recuperarDependenciasLaudo(laudo);
                return laudo;
            }
        }
        return null;
    }

    public LaudoAvaliacaoITBI recuperarLaudoPeloId(Long idLaudoItbi) {
        String sql = " select laudo.* from laudoavaliacaoitbi laudo " +
            " where laudo.id = :idLaudoItbi ";

        Query q = em.createNativeQuery(sql, LaudoAvaliacaoITBI.class);
        q.setParameter("idLaudoItbi", idLaudoItbi);

        List<LaudoAvaliacaoITBI> laudos = q.getResultList();
        if (laudos != null && !laudos.isEmpty()) {
            LaudoAvaliacaoITBI laudo = laudos.get(0);
            recuperarDependenciasLaudo(laudo);
            return laudo;
        }
        return null;
    }

    public ProcessoCalculoITBI recuperarProcessoPeloIdCalculo(Long idCalculo) {
        String sql = " select pc.*, proc.* from processocalculoitbi proc " +
            " inner join calculoitbi itbi on proc.id = itbi.processocalculoitbi_id " +
            " inner join processocalculo pc on proc.id = pc.id " +
            " where itbi.id = :idCalculo ";

        Query q = em.createNativeQuery(sql, ProcessoCalculoITBI.class);
        q.setParameter("idCalculo", idCalculo);

        List<ProcessoCalculoITBI> processos = q.getResultList();

        if (processos != null && !processos.isEmpty()) {
            ProcessoCalculoITBI processo = processos.get(0);
            Hibernate.initialize(processo.getCalculos());
            return processo;
        }
        return null;
    }

    private void ajustarProporcaoQuandoMaiorQueCemPorcentoImovel(BigDecimal proporcaoAtual, CadastroImobiliario imovel) {
        if (proporcaoAtual.compareTo(CEM) > 0) {
            BigDecimal diferencaProporcao = proporcaoAtual.subtract(CEM).setScale(2, RoundingMode.HALF_UP);
            Propriedade primeiroProprietarioImovel = buscarPrimeiroProprietarioVigenteImovel(imovel);
            if (primeiroProprietarioImovel != null) {
                BigDecimal proporcaoPrimeiroProprietarioImovel = BigDecimal.valueOf(primeiroProprietarioImovel.getProporcao());
                primeiroProprietarioImovel.setProporcao((proporcaoPrimeiroProprietarioImovel.subtract(diferencaProporcao)).doubleValue());
                em.merge(primeiroProprietarioImovel);
            }
        }
    }

    private void ajustarProporcaoQuandoMaiorQueCemPorcentoImovelSimulacao(List<Propriedade> propriedades) {
        BigDecimal proporcaoAtual = BigDecimal.ZERO;
        for (Propriedade propriedade : propriedades) {
            proporcaoAtual = proporcaoAtual.add(BigDecimal.valueOf(propriedade.getProporcao()));
        }
        if (proporcaoAtual.compareTo(CEM) > 0) {
            BigDecimal diferencaProporcao = proporcaoAtual.subtract(CEM).setScale(2, RoundingMode.HALF_UP);
            ordenarPropriedadesPelaVigencia(propriedades);
            if (propriedades.get(0) != null) {
                BigDecimal proporcaoPrimeiroProprietarioImovel = BigDecimal.valueOf(propriedades.get(0).getProporcao());
                propriedades.get(0).setProporcao((proporcaoPrimeiroProprietarioImovel.subtract(diferencaProporcao)).doubleValue());
            }
        }
    }

    private void ajustarProporcaoMenorQueCemPorcentoQuandoRural(BigDecimal proporcaoAtual, CadastroRural rural) {
        if (proporcaoAtual.compareTo(CEM) < 0) {
            BigDecimal diferencaProporcao = CEM.subtract(proporcaoAtual).setScale(2, RoundingMode.HALF_UP);
            PropriedadeRural primeiroProprietarioRural = buscarPrimeiroProprietarioVigenteRural(rural);
            if (primeiroProprietarioRural != null) {
                BigDecimal proporcaoPrimeiroProprietarioRural = new BigDecimal(primeiroProprietarioRural.getProporcao());
                primeiroProprietarioRural.setProporcao((proporcaoPrimeiroProprietarioRural.add(diferencaProporcao)).doubleValue());
                em.merge(primeiroProprietarioRural);
            }
        }
    }

    private void ajustarProporcaoMenorQueCemPorcentoQuandoRuralSimulacao(List<PropriedadeRural> propriedades) {
        BigDecimal proporcaoAtual = BigDecimal.ZERO;
        for (PropriedadeRural propriedade : propriedades) {
            proporcaoAtual = proporcaoAtual.add(BigDecimal.valueOf(propriedade.getProporcao()));
        }
        if (proporcaoAtual.compareTo(CEM) < 0) {
            BigDecimal diferencaProporcao = CEM.subtract(proporcaoAtual).setScale(2, RoundingMode.HALF_UP);
            ordenarPropriedadesRuralPelaVigencia(propriedades);
            if (propriedades.get(0) != null) {
                BigDecimal proporcaoPrimeiroProprietarioRural = BigDecimal.valueOf(propriedades.get(0).getProporcao());
                propriedades.get(0).setProporcao((proporcaoPrimeiroProprietarioRural.add(diferencaProporcao)).doubleValue());
            }
        }
    }

    private void ajudarProporcaoQuandoMaiorQueCemPorcentoRural(BigDecimal proporcaoAtual, CadastroRural rural) {
        if (proporcaoAtual.compareTo(CEM) > 0) {
            BigDecimal diferencaProporcao = proporcaoAtual.subtract(CEM).setScale(2, RoundingMode.HALF_UP);
            PropriedadeRural primeiroProprietarioRural = buscarPrimeiroProprietarioVigenteRural(rural);
            if (primeiroProprietarioRural != null) {
                BigDecimal proporcaoPrimeiroProprietarioRural = new BigDecimal(primeiroProprietarioRural.getProporcao());
                primeiroProprietarioRural.setProporcao((proporcaoPrimeiroProprietarioRural.subtract(diferencaProporcao)).doubleValue());
                em.merge(primeiroProprietarioRural);
            }
        }
    }

    private void ajudarProporcaoQuandoMaiorQueCemPorcentoRuralSimulacao(List<PropriedadeRural> propriedades) {
        BigDecimal proporcaoAtual = BigDecimal.ZERO;
        for (PropriedadeRural propriedade : propriedades) {
            proporcaoAtual = proporcaoAtual.add(BigDecimal.valueOf(propriedade.getProporcao()));
        }
        if (proporcaoAtual.compareTo(CEM) > 0) {
            BigDecimal diferencaProporcao = proporcaoAtual.subtract(CEM).setScale(2, RoundingMode.HALF_UP);
            ordenarPropriedadesRuralPelaVigencia(propriedades);
            if (propriedades.get(0) != null) {
                BigDecimal proporcaoPrimeiroProprietarioRural = BigDecimal.valueOf(propriedades.get(0).getProporcao());
                propriedades.get(0).setProporcao((proporcaoPrimeiroProprietarioRural.subtract(diferencaProporcao)).doubleValue());
            }
        }
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public void transferirDebitosCadastroImobiliario(CadastroImobiliario ci) {
        try {
            List<ResultadoParcela> resultados = new ConsultaParcela()
                .addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, ci.getId())
                .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO)
                .executaConsulta().getResultados();


            List<Long> idsCalculos = Lists.newArrayList();
            for (ResultadoParcela resultado : resultados) {
                if (!idsCalculos.contains(resultado.getIdCalculo())) {
                    idsCalculos.add(resultado.getIdCalculo());
                }
            }
            for (Long idCalculo : idsCalculos) {
                Calculo calc = em.find(Calculo.class, idCalculo);
                calc.getPessoas().clear();
                for (Propriedade propriedade : ci.getPropriedadeVigente()) {
                    CalculoPessoa cp = new CalculoPessoa();
                    cp.setPessoa(propriedade.getPessoa());
                    cp.setCalculo(calc);
                    calc.getPessoas().add(cp);
                }
                em.merge(calc);
            }
        } catch (Exception ex) {
            logger.error("Erro ao transferir os débitos do imovel " + ci.getInscricaoCadastral() + ": ", ex);
        }
        transferirCDAs(ci);
    }

    private void transferirCDAs(CadastroImobiliario ci) {
        try {
            List<CertidaoDividaAtiva> certidoes = certidaoDividaAtivaFacade.buscarCertidoesAbertasPorCadastro(ci);
            for (CertidaoDividaAtiva certidao : certidoes) {
                certidao.setPessoa(ci.getPropriedadeVigente().get(0).getPessoa());
                certidao = em.merge(certidao);
                logger.debug("Alterou a CDA " + certidao.getNumero() + "/" + certidao.getExercicio().getAno() + " -> " + certidao.getPessoa());
            }
            enviarCdasSoftplan(certidoes);
        } catch (Exception e) {
            logger.error("Não foi possível comunicar a procuradoria sobre a troca de proprietários.", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void enviarCdasSoftplan(List<CertidaoDividaAtiva> certidoes) {
        try {
            comunicaSofPlanFacade.enviarCDA(certidoes);
        } catch (Exception e) {
            logger.error("Erro ao enviar CDA(s) softplan. {}", e.getMessage());
            logger.debug("Detalhes do erro ao enviar CDA(s) softplan.", e);
        }
    }

    public DAM buscarOuGerarDam(ResultadoParcela parcela, Exercicio exercicio) throws Exception {
        return damFacade.buscarOuGerarDam(parcela);
    }

    public List<DAM> buscarDamsAgrupadosDaParcela(Long idParcela) {
        return damFacade.buscarDamsAgrupadosDaParcela(idParcela);
    }

    public DAM gerarDAM(List<ParcelaValorDivida> parcelas, Date vencimento) {
        return damFacade.geraDAM(parcelas, vencimento);
    }

    public ParcelaValorDivida recuperarParcela(Long idParcela) {
        ParcelaValorDivida parcela = em.find(ParcelaValorDivida.class, idParcela);
        Hibernate.initialize(parcela.getItensParcelaValorDivida());
        return parcela;
    }

    public ResultadoParcela buscarParcelaDividaAtivaDaParcelaOriginal(Long idParcela) {
        return inscricaoDividaAtivaFacade.buscarParcelaDividaAtivaDaParcelaOriginal(idParcela);
    }

    public Divida recuperarDivida(Long idDivida) {
        return dividaFacade.recuperar(idDivida);
    }

    public List<Long> buscarIdsDividasParcelamento(Divida divida) {
        return parametroParcelamentoFacade.buscarIdsDividasParcelamento(divida);
    }

    public boolean isUsuarioComPermissaoParaEmitirLaudo() {
        UsuarioSistema usuarioSistema = Util.recuperarUsuarioCorrente();
        if (usuarioSistema != null) {
            return usuarioSistemaFacade.validaAutorizacaoUsuario(usuarioSistema, AutorizacaoTributario.EMISSAO_LAUDO_ITBI);
        }
        return false;
    }

    private void recuperarDependenciasLaudo(LaudoAvaliacaoITBI laudo) {
        if (laudo.getDiretorChefeDeparTributo() != null &&
            laudo.getDiretorChefeDeparTributo().getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(laudo.getDiretorChefeDeparTributo().getDetentorArquivoComposicao().getArquivosComposicao());
            for (ArquivoComposicao arquivoComposicao : laudo.getDiretorChefeDeparTributo().getDetentorArquivoComposicao().getArquivosComposicao()) {
                Hibernate.initialize(arquivoComposicao.getArquivo().getPartes());
            }
        }
        if (laudo.getRespComissaoAvaliadora() != null &&
            laudo.getRespComissaoAvaliadora().getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(laudo.getRespComissaoAvaliadora().getDetentorArquivoComposicao().getArquivosComposicao());
            for (ArquivoComposicao arquivoComposicao : laudo.getRespComissaoAvaliadora().getDetentorArquivoComposicao().getArquivosComposicao()) {
                Hibernate.initialize(arquivoComposicao.getArquivo().getPartes());
            }
        }
    }

    public LaudoAvaliacaoITBI salvarLaudoAvaliacao(LaudoAvaliacaoITBI laudoAvaliacaoITBI) {
        LaudoAvaliacaoITBI laudo = em.merge(laudoAvaliacaoITBI);
        recuperarDependenciasLaudo(laudo);
        return laudo;
    }

    public ByteArrayOutputStream imprimirLaudo(Long idProcessoCalculo, List<Long> damsParcela) {
        InputStream inputStreamAssinaturaAvaliacaoComissao = null;
        InputStream inputStreamAssinaturaDiretorChefeTributo = null;

        ProcessoCalculoITBI processoCalculoITBI = recuperar(idProcessoCalculo);
        LaudoAvaliacaoITBI laudoAvaliacaoITBI = recuperarLaudo(processoCalculoITBI);

        if (laudoAvaliacaoITBI.getRespComissaoAvaliadora() != null &&
            laudoAvaliacaoITBI.getRespComissaoAvaliadora().getDetentorArquivoComposicao() != null &&
            laudoAvaliacaoITBI.getRespComissaoAvaliadora().getDetentorArquivoComposicao().getArquivosComposicao() != null) {
            laudoAvaliacaoITBI.getRespComissaoAvaliadora().getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo().montarImputStream();
            inputStreamAssinaturaAvaliacaoComissao = laudoAvaliacaoITBI.getRespComissaoAvaliadora().getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo().getInputStream();
        }
        if (laudoAvaliacaoITBI.getDiretorChefeDeparTributo() != null &&
            laudoAvaliacaoITBI.getDiretorChefeDeparTributo().getDetentorArquivoComposicao() != null &&
            laudoAvaliacaoITBI.getDiretorChefeDeparTributo().getDetentorArquivoComposicao().getArquivosComposicao() != null) {
            laudoAvaliacaoITBI.getDiretorChefeDeparTributo().getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo().montarImputStream();
            inputStreamAssinaturaDiretorChefeTributo = laudoAvaliacaoITBI.getDiretorChefeDeparTributo().getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo().getInputStream();
        }
        try {
            AbstractReport abstractReport = AbstractReport.getAbstractReport();

            HashMap<String, Object> parametros = Maps.newHashMap();
            parametros.put("ASSINATURA_COMISSAO_AVALIACAO", inputStreamAssinaturaAvaliacaoComissao);
            parametros.put("ASSINATURA_DIRETOR_CHEFE_TRIBUTOS", inputStreamAssinaturaDiretorChefeTributo);
            parametros.put("URL_PORTAL", configuracaoTributarioFacade.recuperarUrlPortal());

            ImpressaoLaudoITBI impressaoLaudoITBI = buscarDadosLaudoITBI(processoCalculoITBI, damsParcela);
            abstractReport.setGeraNoDialog(true);

            if (FacesContext.getCurrentInstance() == null) {
                String separator = System.getProperty("file.separator");
                String subReport = Util.getApplicationPath("/WEB-INF/") + separator + "report" + separator;
                String img = Util.getApplicationPath("/img/") + separator;
                parametros.put("CAMINHOBRASAO", img);
                return abstractReport.gerarBytesEmPdfDeRelatorioComDadosEmCollectionViewSemCabecalhoPadrao(subReport, "LaudoAvaliacaoITBI.jasper", parametros,
                    new JRBeanCollectionDataSource(Lists.newArrayList(impressaoLaudoITBI)));
            }
            parametros.put("CAMINHOBRASAO", abstractReport.getCaminhoImagem());
            return abstractReport.gerarBytesEmPdfDeRelatorioComDadosEmCollectionView(abstractReport.getCaminho(), "LaudoAvaliacaoITBI.jasper", parametros,
                new JRBeanCollectionDataSource(Lists.newArrayList(impressaoLaudoITBI)));
        } catch (Exception ex) {
            logger.error("Erro ao imprimir laudo de avaliacao de itbi. ", ex);
        }
        return null;
    }

    public void gerarLaudoAndEnviarEmail(ProcessoCalculoITBI processo, List<Long> damsParcela, String[] emailsSeparados, String mensagemEmail) {
        ByteArrayOutputStream outputStream = imprimirLaudo(processo.getId(), damsParcela);
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        String assunto = "Prefeitura Municipal de " + configuracaoTributario.getCidade().getNome() + " / " + configuracaoTributario.getCidade().getUf() + " - ITBI";
        AnexoEmailDTO anexoDto = new AnexoEmailDTO(outputStream, "anexo", "application/pdf", ".pdf");
        EmailService.getInstance()
            .enviarEmail(emailsSeparados, assunto, mensagemEmail, anexoDto);

    }

    public void removerProprietariosAtuaisERecuperarAnteriores(ProcessoCalculoITBI processo, List<DAMResultadoParcela> parcelasCalculo) {
        Date pagamentoDam = null;
        for (DAMResultadoParcela damResultadoParcela : parcelasCalculo) {
            if (damResultadoParcela.getResultadoParcela().getIdCalculo().equals(processo.getUltimoCalculo().getId())) {
                pagamentoDam = damResultadoParcela.getResultadoParcela().getPagamento();
            }
        }
        if (processo.getTipoITBI().equals(TipoITBI.IMOBILIARIO)) {
            removerProprietariosAtuaisImobiliario(processo, pagamentoDam);
        } else {
            removerProprietariosAtuaisRural(processo, pagamentoDam);
        }
    }

    private void removerProprietariosAtuaisImobiliario(ProcessoCalculoITBI processo, Date pagamentoDam) {
        List<Propriedade> proprietariosVigentes = cadastroImobiliarioFacade.recuperarProprietariosVigentes(processo.getCadastroImobiliario());
        for (Propriedade proprietarioVigente : proprietariosVigentes) {
            em.remove(proprietarioVigente);
        }
        restaurarProprietariosAteriores(processo);
        if (pagamentoDam != null) {
            List<Propriedade> proprietariosNaoVigentes = cadastroImobiliarioFacade.recuperarProprietariosNaoVigentes(processo.getCadastroImobiliario());
            for (Propriedade proprietarioNaoVigente : proprietariosNaoVigentes) {
                if (DateUtils.getDataFormatada(proprietarioNaoVigente.getFinalVigencia()).equals(DateUtils.getDataFormatada(pagamentoDam))) {
                    em.remove(proprietarioNaoVigente);
                }
            }
        }
    }

    private void removerProprietariosAtuaisRural(ProcessoCalculoITBI processo, Date pagamentoDam) {
        List<Propriedade> proprietariosVigentes = cadastroImobiliarioFacade.recuperarProprietariosVigentes(processo.getCadastroImobiliario());
        for (Propriedade proprietarioVigente : proprietariosVigentes) {
            em.remove(proprietarioVigente);
        }
        restaurarProprietariosAteriores(processo);
        if (pagamentoDam != null) {
            List<Propriedade> proprietariosNaoVigentes = cadastroImobiliarioFacade.recuperarProprietariosNaoVigentes(processo.getCadastroImobiliario());
            for (Propriedade proprietarioNaoVigente : proprietariosNaoVigentes) {
                if (DateUtils.getDataFormatada(proprietarioNaoVigente.getFinalVigencia()).equals(DateUtils.getDataFormatada(pagamentoDam))) {
                    em.remove(proprietarioNaoVigente);
                }
            }
        }
    }

    private void restaurarProprietariosAteriores(ProcessoCalculoITBI processo) {
        try {
            String sql = "SELECT " + (processo.getTipoITBI().equals(TipoITBI.IMOBILIARIO)
                ? " PROPORIG.PROPRIEDADE_ID "
                : " PROPORIG.PROPRIEDADERURAL_ID ") +
                " FROM PROPRIEDADEORIGINALITBI PROPORIG " +
                " WHERE PROPORIG.PROCESSOCALCULOITBI_ID = :idProcesso";

            Query q = em.createNativeQuery(sql);
            q.setParameter("idProcesso", processo.getId());

            List<Object> propriedades = q.getResultList();
            if (propriedades != null && !propriedades.isEmpty()) {
                for (Object idPropriedade : propriedades) {
                    if (processo.getTipoITBI().equals(TipoITBI.IMOBILIARIO)) {
                        em.createNativeQuery(" UPDATE PROPRIEDADE " +
                            " SET FINALVIGENCIA = NULL " +
                            " WHERE ID = " + idPropriedade).executeUpdate();
                    } else {
                        em.createNativeQuery(" UPDATE PROPRIEDADERURAL " +
                            " SET FINALVIGENCIA = NULL " +
                            " WHERE ID = " + idPropriedade).executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao restaurar proprietários anteriores. ", e);
        }
    }

    public void cancelarParcelasItbi(List<ResultadoParcela> parcelas) {
        JdbcParcelaValorDividaDAO parcelaValorDividaDAO = (JdbcParcelaValorDividaDAO) Util.getSpringBeanPeloNome("jdbcParcelaValorDividaDAO");
        Set<DAM> dans = Sets.newHashSet();
        for (ResultadoParcela parcela : parcelas) {
            if (parcela.isEmAberto()) {
                parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(
                    parcela.getIdParcela(),
                    parcela.getReferencia(),
                    arrecadacaoFacade.recuperarSaldoUltimaSituacao(parcela.getIdParcela(), SituacaoParcela.EM_ABERTO),
                    SituacaoParcela.CANCELAMENTO
                );

                dans.add(damFacade.recuperaDAMPeloIdParcela(parcela.getIdParcela()));
                dans.addAll(damFacade.buscarDamsAgrupadosDaParcela(parcela.getIdParcela()));
            }
        }
        if (!dans.isEmpty()) {
            JdbcDamDAO damDAO = (JdbcDamDAO) Util.getSpringBeanPeloNome("damDAO");
            for (DAM dam : dans) {
                if (dam != null && dam.getId() != null) {
                    damDAO.atualizar(dam.getId(), DAM.Situacao.CANCELADO);
                }
            }
        }
    }

    public List<ResultadoParcela> buscarParcelaDoParcelamentoDaDividaAtiva(Long idParcela) {
        return processoParcelamentoFacade.buscarParcelaDoParcelamentoDaDividaAtiva(idParcela);
    }

    public List<ResultadoParcela> buscarParcelasDaCompensacao(Long idParcela) {
        return compensacaoFacade.buscarParcelaDaCompensacao(idParcela);
    }

    public DAM recuperarDAMPeloIdParcela(Long idParcela) {
        return damFacade.recuperaUltimoDamParcela(idParcela);
    }

    public void depoisDePagar(Calculo calculo) {
        CalculoITBI calculoITBI = em.find(CalculoITBI.class, calculo.getId());
        ProcessoCalculoITBI processoCalculoITBI = em.find(ProcessoCalculoITBI.class, calculoITBI.getProcessoCalculo().getId());
        if (!calculo.getId().equals(processoCalculoITBI.getUltimoCalculo().getId())) {
            return;
        }
        ConsultaParcela consultaITBI = new ConsultaParcela();
        consultaITBI.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, processoCalculoITBI.getUltimoCalculo().getId());
        consultaITBI.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        List<ResultadoParcela> parcelasITBI = consultaITBI.executaConsulta().getResultados();
        if (parcelasITBI.isEmpty()) {
            transferirProprietariosCadastro(processoCalculoITBI);
            JdbcCalculoDAO calculoDAO = (JdbcCalculoDAO) Util.getSpringBeanPeloNome("calculoDAO");
            calculoDAO.atualizarSituacaoITBI(processoCalculoITBI.getId(), SituacaoITBI.PAGO.name());
        }
    }

    public ImpressaoLaudoITBI buscarDadosLaudoITBI(ProcessoCalculoITBI processo, List<Long> damsParcela) {
        String sql = " select pcitbi.id as idCalculo, " +
            " pcitbi.cadastroimobiliario_id as idCadastroImobiliario, " +
            " pcitbi.cadastrorural_id as idCadastroRural, " +
            "       pcitbi.codigo as sequencia, " +
            " ex.ano as exercicio, " +
            " rownum as linha, " +
            " sum(coalesce(c.valorreal, 0)) as total, " +
            "       sum(coalesce(itbi.basecalculo, 0)) as baseCalculo, " +
            " max(itbi.valorvenal) as valorvenal, " +
            " pcitbi.tipoitbi as tipoItbi, " +
            "       isencao.nome as isencao, " +
            (processo.getNumeroProtocolo() != null && processo.getAnoProtocolo() != null ?
                "       (select p.numero || ' - ' || p.ano || ' ' || p.assunto from processo p " +
                    "            where coalesce(p.protocolo, 0) = :protocolo " +
                    "            and p.numero = :numero " +
                    "            and p.ano = :ano " +
                    "            order by p.id desc fetch first 1 rows only) " : "null") + " as processo, " +
            "    pcitbi.observacao, " +
            " pcitbi.codigoverificacao, " +
            " lpad(cast(coalesce(pcitbi.codigo, 1) as varchar2(5)), 5, '0') as sequenciaZero, " +
            "       coalesce(pf_rca.nome, pj_rca.razaosocial) as respcomissaoavaliadora, " +
            " coalesce(pf_dct.nome, pj_dct.razaosocial) as diretorchefetributos, " +
            "       laudo.datavencimento as vencimentoLaudo, " +
            " pc.datalancamento as lancamento, " +
            " laudo.dataimpressaolaudo as dataImpressao, " +
            "       laudo.dataimpressao2via as dataImpressao2Via, " +
            " coalesce(pcitbi.isentoimune, 0) as isento, " +
            "       coalesce(laudo.laudoimpresso, 0) as laudoImpresso, " +
            " coalesce(laudo.laudosegundaviaimpresso, 0) as segundaVia," +
            "       func_rca.descricao as funcaoresponsavel, " +
            " func_dct.descricao as funcaodiretor, " +
            " laudo.id as idLaudo " +
            " from calculoitbi itbi " +
            " inner join calculo c on itbi.id = c.id " +
            " inner join processocalculoitbi pcitbi on itbi.processocalculoitbi_id = pcitbi.id " +
            " inner join processocalculo pc on pcitbi.id = pc.id " +
            " inner join exercicio ex on pc.exercicio_id = ex.id " +
            " left join valordivida vd on c.id = vd.calculo_id " +
            " left join tipoisencaoitbi isencao on itbi.tipoisencaoitbi_id = isencao.id " +
            " left join laudoavaliacaoitbi laudo on pcitbi.id = laudo.processocalculoitbi_id " +
            " left join parametrosfuncionarios param_rca on laudo.respcomissaoavaliadora_id = param_rca.id " +
            " left join funcaoparametrositbi func_rca on param_rca.funcaoparametrositbi_id = func_rca.id " +
            " left join pessoafisica pf_rca on param_rca.pessoa_id = pf_rca.id " +
            " left join pessoajuridica pj_rca on param_rca.pessoa_id = pj_rca.id " +
            " left join parametrosfuncionarios param_dct on laudo.diretorchefedepartributo_id = param_dct.id " +
            " left join funcaoparametrositbi func_dct on param_dct.funcaoparametrositbi_id = func_dct.id " +
            " left join pessoafisica pf_dct on param_dct.pessoa_id = pf_dct.id " +
            " left join pessoajuridica pj_dct on param_dct.pessoa_id = pj_dct.id " +
            " where pcitbi.id = :idProcesso " +
            " group by pcitbi.id, pcitbi.cadastroimobiliario_id, pcitbi.cadastrorural_id, pcitbi.codigo, ex.ano, rownum, pcitbi.tipoitbi, isencao.nome, pcitbi.observacao, pcitbi.codigoverificacao, " +
            "          lpad(cast(coalesce(pcitbi.codigo, 1) as varchar2(5)), 5, '0'), coalesce(pf_rca.nome, pj_rca.razaosocial), " +
            "          coalesce(pf_dct.nome, pj_dct.razaosocial), laudo.datavencimento, pc.datalancamento, laudo.dataimpressaolaudo, " +
            "          laudo.dataimpressao2via, coalesce(pcitbi.isentoimune, 0), coalesce(laudo.laudoimpresso, 0), coalesce(laudo.laudosegundaviaimpresso, 0)," +
            "          func_rca.descricao, func_dct.descricao, laudo.id ";

        Query q = em.createNativeQuery(sql);
        if (processo.getNumeroProtocolo() != null && processo.getAnoProtocolo() != null) {
            q.setParameter("protocolo", 1);
            q.setParameter("numero", processo.getNumeroProtocolo());
            q.setParameter("ano", processo.getAnoProtocolo());
        }
        q.setParameter("idProcesso", processo.getId());

        List<Object[]> laudos = q.getResultList();

        if (laudos != null && !laudos.isEmpty()) {

            Object[] laudo = laudos.get(0);
            ImpressaoLaudoITBI impressao = new ImpressaoLaudoITBI();
            impressao.setIdProcesso(laudo[0] != null ? ((BigDecimal) laudo[0]).longValue() : null);
            impressao.setIdCadastroImobiliario(laudo[1] != null ? ((BigDecimal) laudo[1]).longValue() : null);
            impressao.setIdCadastroRural(laudo[2] != null ? ((BigDecimal) laudo[2]).longValue() : null);
            impressao.setSequencia(laudo[3] != null ? ((BigDecimal) laudo[3]).longValue() : null);
            impressao.setExercicio(laudo[4] != null ? ((BigDecimal) laudo[4]).intValue() : null);
            impressao.setLinha(laudo[5] != null ? ((BigDecimal) laudo[5]).intValue() : null);
            impressao.setBaseCalculo(laudo[7] != null ? (BigDecimal) laudo[7] : BigDecimal.ZERO);
            impressao.setValorVenal(laudo[8] != null ? (BigDecimal) laudo[8] : BigDecimal.ZERO);
            if (laudo[9] != null) {
                impressao.setTipoITBI(TipoITBI.valueOf((String) laudo[9]).getDescricao());
            }
            impressao.setProcesso(laudo[11] != null ? laudo[11].toString() : "");
            impressao.setObservacao(laudo[12] != null ? (String) laudo[12] : null);
            impressao.setCodigoVerificacao(laudo[13] != null ? (String) laudo[13] : null);
            impressao.setSequenciaZero(laudo[14] != null ? (String) laudo[14] : null);
            impressao.setResponsavelComissao(laudo[15] != null ? (String) laudo[15] : null);
            impressao.setDiretor(laudo[16] != null ? (String) laudo[16] : null);
            impressao.setVencimentoLaudo(laudo[17] != null ? (Date) laudo[17] : null);
            impressao.setLancamento(laudo[18] != null ? (Date) laudo[18] : null);
            impressao.setDataImpressao(laudo[19] != null ? (Date) laudo[19] : null);
            impressao.setDataImpressao2Via(laudo[20] != null ? (Date) laudo[20] : null);
            impressao.setLaudoImpresso(laudo[22] != null && ((BigDecimal) laudo[22]).intValue() == 1);
            impressao.setSegundaVia(laudo[23] != null && ((BigDecimal) laudo[23]).intValue() == 1);
            impressao.setFuncaoResponsavel(laudo[24] != null ? (String) laudo[24] : null);
            impressao.setFuncaoDiretor(laudo[25] != null ? (String) laudo[25] : null);
            impressao.setTransmissoes(buscarTransmissoesDoLaudo(processo));
            impressao.temIsencao();
            impressao.setRetificacoes(buscarRetificacoesLaudo(processo));
            impressao.ordenarRetificacoes();
            if (processo.isImobiliario()) {
                impressao.setDadosImovel(buscarDadosDoImovelDoLaudo(processo));
            } else {
                impressao.setDadosImovelRural(buscarDadosDoImovelRuralDoLaudo(processo));
            }
            impressao.setValoresLaudo(buscarValoresLaudo(processo.getId()));
            if (damsParcela != null && !damsParcela.isEmpty()) {
                impressao.setDans(buscarDansLaudo(damsParcela));
            }
            if (laudo[26] != null) {
                impressao.setQrCode(gerarQrCode(montarQrCode(((BigDecimal) laudo[26]).longValue())));
            }
            BigDecimal valorTotal = BigDecimal.ZERO;
            for (TransmissoesImpressaoLaudoITBI transmissao : impressao.getTransmissoes()) {
                if (!transmissao.getIsento()) {
                    valorTotal = valorTotal.add(transmissao.getValorReal());
                }
            }
            impressao.setValorTotal(valorTotal);
            return impressao;
        }
        return null;
    }

    private InputStream gerarQrCode(String qrCodeTxt) {
        return BarCode.generateInputStreamQRCodePng(qrCodeTxt, 350, 350);
    }

    private String montarQrCode(Long idLaudo) {
        return configuracaoTributarioFacade.recuperarUrlPortal() + "/autenticidade-de-documentos/autenticar-itbi/" + idLaudo + "/";
    }

    private List<TransmissoesImpressaoLaudoITBI> buscarTransmissoesDoLaudo(ProcessoCalculoITBI processo) {
        String sql = " SELECT CALC.ID, " +
            " CALC.ORDEMCALCULO, " +
            " C.ISENTO, " +
            " C.VALORREAL, " +
            " ISENCAO.DESCRICAO " +
            " FROM CALCULOITBI CALC " +
            " INNER JOIN CALCULO C ON CALC.ID = C.ID " +
            " LEFT JOIN TIPOISENCAOITBI ISENCAO ON CALC.TIPOISENCAOITBI_ID = ISENCAO.ID " +
            " WHERE CALC.PROCESSOCALCULOITBI_ID = :idProcesso " +
            " ORDER BY CALC.ORDEMCALCULO ASC";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcesso", processo.getId());

        List<Object[]> resultados = q.getResultList();
        List<TransmissoesImpressaoLaudoITBI> transmissoes = Lists.newArrayList();

        if (resultados != null && !resultados.isEmpty()) {
            for (Object[] obj : resultados) {
                TransmissoesImpressaoLaudoITBI transmissao = new TransmissoesImpressaoLaudoITBI();

                transmissao.setId(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
                transmissao.setOrdem(obj[1] != null ? ((BigDecimal) obj[1]).intValue() : null);
                transmissao.setIsento(obj[2] != null && (((BigDecimal) obj[2]).intValue() == 1));
                transmissao.setValorReal(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
                transmissao.setIsencao(obj[4] != null ? obj[4].toString() : "");
                transmissao.setAdquirentes(buscarAdquirentesDaTransmissao(transmissao.getId()));
                transmissao.setTransmitentes(buscarTransmitentesDaTransmissao(transmissao.getId()));

                transmissoes.add(transmissao);
            }
        }
        return transmissoes;
    }

    private List<PessoasImpressaoLaudoITBI> buscarAdquirentesDaTransmissao(Long idCalculo) {
        String sql = " select coalesce(pf.nome, pj.razaosocial) as adquirente, coalesce(pf.cpf, pj.cnpj) as cpfCnpj, " +
            "       coalesce(adquirente.percentual, 0) as percentual " +
            " from adquirentescalculoitbi adquirente " +
            " inner join calculoitbi itbi on adquirente.calculoitbi_id = itbi.id " +
            " inner join pessoa pes on adquirente.adquirente_id = pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " where itbi.id = :idCalculo " +
            " order by coalesce(adquirente.percentual, 0) desc ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCalculo", idCalculo);

        List<Object[]> resultados = q.getResultList();
        List<PessoasImpressaoLaudoITBI> adquirentes = Lists.newArrayList();

        if (resultados != null && !resultados.isEmpty()) {
            for (Object[] obj : resultados) {
                PessoasImpressaoLaudoITBI adquirente = new PessoasImpressaoLaudoITBI();

                adquirente.setNome(obj[0] != null ? (String) obj[0] : null);
                adquirente.setCpfCnpj(obj[1] != null ? (String) obj[1] : null);

                String percentual = obj[2].toString();
                if (!percentual.contains(".")) percentual += ",00";
                adquirente.setPercentual(percentual.replace(".", ",") + "%");

                adquirentes.add(adquirente);
            }
        }
        return adquirentes;
    }

    private List<PessoasImpressaoLaudoITBI> buscarTransmitentesDaTransmissao(Long idCalculo) {
        String sql = " select coalesce(pf.nome, pj.razaosocial) as adquirente, coalesce(pf.cpf, pj.cnpj) as cpfCnpj " +
            " from transmitentescalculoitbi transmitente " +
            " inner join calculoitbi itbi on transmitente.calculoitbi_id = itbi.id " +
            " inner join pessoa pes on transmitente.pessoa_id = pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " where itbi.id = :idCalculo ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCalculo", idCalculo);

        List<Object[]> resultados = q.getResultList();
        List<PessoasImpressaoLaudoITBI> transmitentes = Lists.newArrayList();

        if (resultados != null && !resultados.isEmpty()) {
            for (Object[] obj : resultados) {
                PessoasImpressaoLaudoITBI transmitente = new PessoasImpressaoLaudoITBI();

                transmitente.setNome(obj[0] != null ? (String) obj[0] : null);
                transmitente.setCpfCnpj(obj[1] != null ? (String) obj[1] : null);

                transmitentes.add(transmitente);
            }
        }
        return transmitentes;
    }

    private List<DadosImovelLaudoITBI> buscarDadosDoImovelDoLaudo(ProcessoCalculoITBI processo) {
        String sql = " select " +
            "       ci.id as idcadastro, " +
            "       trim(tl.descricao) ||' ' ||trim(l.nome) || ', ' || coalesce(lote.NUMEROCORREIO,'s/n') || ', Bairro: ' || b.descricao as endereco, " +
            "       ci.inscricaocadastral, " +
            "       lote.codigolote, " +
            "       lote.arealote, " +
            "       lote.complemento, " +
            "       lote.descricaoloteamento, " +
            "       setor.codigo as setor, " +
            "       quadra.descricao as quadra " +
            " from cadastroimobiliario ci " +
            " inner join lote on ci.lote_id = lote.id " +
            " inner join setor on lote.setor_id = setor.id " +
            " inner join quadra on lote.quadra_id = quadra.id " +
            " inner join propriedade prop on ci.id = prop.imovel_id " +
            " left join testada on lote.id = testada.lote_id " +
            " left join face on testada.face_id = face.id " +
            " left join logradourobairro lb on face.logradourobairro_id = lb.id " +
            " left join logradouro l on lb.logradouro_id = l.id " +
            " left join bairro b on lb.bairro_id = b.id " +
            " left join tipologradouro tl on l.tipologradouro_id = tl.id " +
            " where ci.id = :idCadastro " +
            " order by coalesce(prop.proporcao, 0) desc fetch first 1 rows only ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", processo.getIdCadastro());

        List<Object[]> dados = q.getResultList();

        if (dados != null && !dados.isEmpty()) {
            Object[] obj = dados.get(0);

            DadosImovelLaudoITBI imovel = new DadosImovelLaudoITBI();
            imovel.setIdCadastro(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
            imovel.setEndereco(obj[1] != null ? (String) obj[1] : null);
            imovel.setInscricaoCadastral(obj[2] != null ? (String) obj[2] : null);
            imovel.setLote(obj[3] != null ? (String) obj[3] : null);
            imovel.setAreaLote(obj[4] != null ? obj[4].toString() : null);
            imovel.setComplemento(obj[5] != null ? (String) obj[5] : null);
            imovel.setLoteamento(obj[6] != null ? (String) obj[6] : null);
            imovel.setSetor(obj[7] != null ? (String) obj[7] : null);
            imovel.setQuadra(obj[8] != null ? (String) obj[8] : null);

            imovel.setConstrucoes(buscarConstrucoes(imovel.getIdCadastro()));

            return Lists.newArrayList(imovel);
        }

        return Lists.newArrayList();
    }

    private List<DadosImovelLaudoITBI> buscarDadosDoImovelRuralDoLaudo(ProcessoCalculoITBI processo) {
        String sql = " select to_char(cr.codigo), cr.numeroincra, cr.arealote, tar.descricao as tipoarea, cr.nomepropriedade, cr.localizacaolote as lote " +
            " from cadastrorural cr " +
            " inner join propriedaderural pr on cr.id = pr.imovel_id " +
            " left join tipoarearural tar on cr.tipoarearural_id = tar.id " +
            " where cr.id = :idCadastro ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", processo.getIdCadastro());

        List<Object[]> dados = q.getResultList();

        if (dados != null && !dados.isEmpty()) {
            Object[] obj = dados.get(0);

            DadosImovelLaudoITBI imovelRural = new DadosImovelLaudoITBI();
            imovelRural.setInscricaoCadastral(obj[0] != null ? (String) obj[0] : null);
            imovelRural.setIncra(obj[1] != null ? (String) obj[1] : null);
            imovelRural.setAreaLote(obj[2] != null ? obj[2].toString() : null);
            imovelRural.setTipoArea(obj[3] != null ? (String) obj[3] : null);
            imovelRural.setPropriedade(obj[4] != null ? (String) obj[4] : null);
            imovelRural.setLote(obj[5] != null ? (String) obj[5] : null);

            return Lists.newArrayList(imovelRural);
        }

        return Lists.newArrayList();
    }

    public List<DadosConstrucaoImovelLaudoITBI> buscarConstrucoes(Long idCadastro) {
        String sql = " select " +
            "       c.descricao, c.areaconstruida, " +
            "       (select vp.valor from construcao c2 " +
            "        left join construcao_valoratributo cva on c2.id = cva.construcao_id " +
            "        left join atributo atr on cva.atributos_key = atr.id " +
            "        left join valoratributo va on cva.atributos_id = va.id " +
            "        left join valorpossivel vp on va.valordiscreto_id = vp.id " +
            "        where c2.imovel_id = c.imovel_id" +
            "        and atr.identificacao = :identificacao " +
            "        order by atr.id desc fetch first 1 rows only) as atributo " +
            " from construcao c " +
            " where coalesce(c.cancelada, 0) = :cancelada " +
            " and c.imovel_id = :idCadastro ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);
        q.setParameter("identificacao", "utilizacao_imovel");
        q.setParameter("cancelada", 0);

        List<Object[]> dados = q.getResultList();
        List<DadosConstrucaoImovelLaudoITBI> construcoes = Lists.newArrayList();

        if (dados != null && !dados.isEmpty()) {
            for (Object[] obj : dados) {
                DadosConstrucaoImovelLaudoITBI construcao = new DadosConstrucaoImovelLaudoITBI();
                construcao.setDescricao(obj[0] != null ? (String) obj[0] : null);
                construcao.setAreaConstruida(obj[1] != null ? (BigDecimal) obj[1] : null);
                construcao.setAtributo(obj[2] != null ? (String) obj[2] : null);

                construcoes.add(construcao);
            }
        }

        return construcoes;
    }

    public List<DadosValoresLaudoITBI> buscarValoresLaudo(Long idProcesso) {
        String sql = " select tn.descricao, tn.aliquota, item.valorinformado, item.valorcalculado, itbi.ORDEMCALCULO, calc.ISENTO" +
            " from itemcalculoitbi item " +
            " inner join calculoitbi itbi on item.calculoitbi_id = itbi.id " +
            " inner join calculo calc on calc.ID = itbi.id " +
            " left join tiponegociacao tn on item.tiponegociacao_id = tn.id " +
            " where itbi.processocalculoitbi_id = :idProcesso " +
            " order by itbi.ORDEMCALCULO ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcesso", idProcesso);

        List<Object[]> resultados = q.getResultList();
        List<DadosValoresLaudoITBI> valores = Lists.newArrayList();

        if (resultados != null && !resultados.isEmpty()) {
            for (Object[] obj : resultados) {
                DadosValoresLaudoITBI valor = new DadosValoresLaudoITBI();
                valor.setTipoNegociacao(obj[0] != null ? (String) obj[0] : null);
                valor.setAliquota(obj[1] != null ? (BigDecimal) obj[1] : BigDecimal.ZERO);
                valor.setValorNegociacao(obj[2] != null ? (BigDecimal) obj[2] : BigDecimal.ZERO);
                valor.setValorITBI(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
                valor.setNumeroTransmissao(obj[4] != null ? ((BigDecimal) obj[4]).intValue() : 1);
                valor.setIsento(obj[5] != null ? (((BigDecimal) obj[5]).intValue() == 1 ? Boolean.TRUE : Boolean.FALSE) : Boolean.FALSE);
                if (valor.getIsento()) {
                    valor.setValorITBI(BigDecimal.ZERO);
                }
                valores.add(valor);
            }
        }
        return valores;
    }

    public List<DadosDAMsLaudoITBI> buscarDansLaudo(List<Long> damsParcela) {
        String sql = " select dam.numerodam, lb.datapagamento, " +
            "       (dam.valororiginal + dam.juros + dam.multa) - dam.desconto as damvalor " +
            " from lotebaixa lb " +
            " inner join itemlotebaixa ilb on lb.id = ilb.lotebaixa_id " +
            " inner join dam on ilb.dam_id = dam.id " +
            " where dam.id in (:idsDam) " +
            " and dam.situacao = :pago " +
            " and lb.situacaolotebaixa in (:situacoesBaixado) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idsDam", damsParcela);
        q.setParameter("pago", DAM.Situacao.PAGO.name());
        q.setParameter("situacoesBaixado", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(), SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));

        List<Object[]> resultados = q.getResultList();
        List<DadosDAMsLaudoITBI> dans = Lists.newArrayList();

        if (resultados != null && !resultados.isEmpty()) {
            for (Object[] obj : resultados) {
                DadosDAMsLaudoITBI dam = new DadosDAMsLaudoITBI();
                dam.setNumeroDAM(obj[0] != null ? (String) obj[0] : null);
                dam.setPagamento(obj[1] != null ? (Date) obj[1] : null);
                dam.setValorPago(obj[2] != null ? (BigDecimal) obj[2] : null);

                dans.add(dam);
            }
        }
        return dans;
    }

    private List<DadosRetificacoesLaudoITBI> buscarRetificacoesLaudo(ProcessoCalculoITBI processo) {
        List<DadosRetificacoesLaudoITBI> retificacoes = Lists.newArrayList();
        for (RetificacaoCalculoITBI retificacaoItbi : processo.getRetificacoes()) {
            DadosRetificacoesLaudoITBI retificacao = new DadosRetificacoesLaudoITBI();
            retificacao.setDataRetificacao(retificacaoItbi.getDataRetificacao());
            retificacao.setNumeroRetificacao(retificacaoItbi.getNumeroRetificacao());
            retificacao.setMotivoRetificacao(retificacaoItbi.getMotivoRetificacao());

            retificacoes.add(retificacao);
        }

        return retificacoes;
    }

    public WSItbi montarWsItbi(Exercicio exercicio, AutenticaITBI autenticaITBI) {
        String sql = " select pcitbi.*, pc.* from processocalculoitbi pcitbi " +
            " inner join processocalculo pc on pcitbi.id = pc.id " +
            " inner join exercicio ex on pc.exercicio_id = ex.id " +
            " inner join laudoavaliacaoitbi laudo on pcitbi.id = laudo.processocalculoitbi_id " +
            " where trunc(laudo.datavencimento) = to_date(:vencimento, 'dd/MM/yyyy') " +
            " and pcitbi.codigo = :sequencia " +
            " and pcitbi.codigoverificacao = :codigoVerificacao " +
            " and ex.ano = :ano " +
            " and pcitbi.situacao <> :cancelado ";

        Query q = em.createNativeQuery(sql, ProcessoCalculoITBI.class);
        q.setParameter("vencimento", DataUtil.getDataFormatada(autenticaITBI.getDataVencimento()));
        q.setParameter("sequencia", autenticaITBI.getSequencia());
        q.setParameter("codigoVerificacao", StringUtil.removeCaracteresEspeciaisSemEspaco(autenticaITBI.getCodigoAutenticacao()).trim());
        q.setParameter("ano", exercicio.getAno());
        q.setParameter("cancelado", SituacaoITBI.CANCELADO.name());

        List<ProcessoCalculoITBI> itbis = q.getResultList();

        if (itbis != null && !itbis.isEmpty()) {
            return new WSItbi(itbis.get(0));
        }
        return null;
    }

    public WSItbi montarWsItbiPorId(Long idLaudo) {
        String sql = " select pcitbi.*, pc.* from processocalculoitbi pcitbi " +
            " inner join processocalculo pc on pcitbi.id = pc.id " +
            " inner join laudoavaliacaoitbi laudo on pcitbi.id = laudo.processocalculoitbi_id " +
            " where laudo.id = :idLaudo" +
            " and pcitbi.situacao <> :cancelado " +
            " and trunc(laudo.datavencimento) >= to_date(:dataAtual, 'dd/MM/yyyy') ";

        Query q = em.createNativeQuery(sql, ProcessoCalculoITBI.class);
        q.setParameter("idLaudo", idLaudo);
        q.setParameter("cancelado", SituacaoITBI.CANCELADO.name());
        q.setParameter("dataAtual", DataUtil.getDataFormatada(new Date()));

        List<ProcessoCalculoITBI> itbis = q.getResultList();
        if (itbis != null && !itbis.isEmpty()) {
            return new WSItbi(itbis.get(0));
        }
        return null;
    }

    public List<WSItbi> montarWsItbiAssinado(Pessoa pessoa, Integer inicio, Integer max) {
        String sql = " select pcitbi.*, pc.ANOPROTOCOLO, " +
            "       pc.COMPLETO, " +
            "       pc.DATALANCAMENTO, " +
            "       pc.DESCRICAO, " +
            "       pc.DIVIDA_ID, " +
            "       pc.EXERCICIO_ID, " +
            "       pc.NUMEROPROTOCOLO, " +
            "       pc.USUARIOSISTEMA_ID from processocalculoitbi pcitbi " +
            " inner join processocalculo pc on pcitbi.id = pc.id " +
            " inner join calculoitbi itbi on pcitbi.id = itbi.processocalculoitbi_id " +
            " inner join adquirentescalculoitbi adquirente on itbi.id = adquirente.calculoitbi_id " +
            " inner join transmitentescalculoitbi transmitente on itbi.id = transmitente.calculoitbi_id " +
            " inner join laudoavaliacaoitbi laudo on pcitbi.id = laudo.processocalculoitbi_id " +
            " where (adquirente.adquirente_id = :idPessoa or transmitente.pessoa_id = :idPessoa)" +
            " and pcitbi.situacao = :assinado ";

        Query q = em.createNativeQuery(sql, ProcessoCalculoITBI.class);

        if (inicio != null && max != null) {
            q.setMaxResults(max);
            q.setFirstResult(inicio);
        }
        q.setParameter("idPessoa", pessoa.getId());
        q.setParameter("assinado", SituacaoITBI.ASSINADO.name());

        List<ProcessoCalculoITBI> processos = q.getResultList();
        List<WSItbi> wsItbis = Lists.newArrayList();

        List<Long> idsProcessos = Lists.newArrayList();
        if (processos != null && !processos.isEmpty()) {
            for (ProcessoCalculoITBI processo : processos) {
                if (!idsProcessos.contains(processo.getId())) {
                    WSItbi wsItbi = new WSItbi(processo);
                    idsProcessos.add(processo.getId());
                    wsItbis.add(wsItbi);
                }
            }
        }
        return wsItbis;
    }

    public boolean isAtenticouLaudoItbi(Exercicio exercicio, AutenticaITBI autenticaITBI) {
        return montarWsItbi(exercicio, autenticaITBI) != null;
    }

    public Integer contarCalculosComLaudosAssinados(Pessoa pessoa) {
        String sql = " select count(pcitbi.id) from processocalculoitbi pcitbi " +
            " inner join laudoavaliacaoitbi laudo on pcitbi.id = laudo.processocalculoitbi_id " +
            " inner join calculoitbi itbi on pcitbi.id = itbi.processocalculoitbi_id  " +
            " inner join adquirentescalculoitbi adquirente on itbi.id = adquirente.calculoitbi_id  " +
            " where adquirente.adquirente_id = :idPessoa " +
            " and pcitbi.situacao = :assinado ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idPessoa", pessoa.getId());
        q.setParameter("assinado", SituacaoITBI.ASSINADO.name());

        List<BigDecimal> ids = q.getResultList();
        return (ids != null && !ids.isEmpty() && ids.get(0) != null) ? ids.get(0).intValue() : 0;
    }

    public String gerarAssinaturaDigital(ProcessoCalculoITBI processo) {
        String assinaturaDigital = processo.getDataLancamento().toString() + processo.getCadastro().getNumeroCadastro();
        assinaturaDigital = StringUtil.cortarOuCompletarEsquerda(Seguranca.md5(assinaturaDigital).toUpperCase(), 32, "0");
        return assinaturaDigital;
    }

    public Date montarDataVencimentoLaudo(ParametrosITBI parametro,
                                          Date dataLancamentoItbi,
                                          Date dataEmissaoLaudo) {
        Calendar c = Calendar.getInstance();
        if (parametro.getVencLaudoDeAvaliacao().equals(VencimentoLaudoDeAvaliacao.DATA_LANCAMENTO)) {
            c.setTime(dataLancamentoItbi);
            c.add(Calendar.DAY_OF_MONTH, parametro.getVencLaudoAvaliacaoEmDias());
        } else if (parametro.getVencLaudoDeAvaliacao().equals(VencimentoLaudoDeAvaliacao.DATA_VENCIMENTO_PRIMEIRA_PARCELA)) {
            c.setTime(dataLancamentoItbi);
            c.add(Calendar.DAY_OF_MONTH, parametro.getVencLaudoAvaliacaoEmDias());
            c.add(Calendar.DAY_OF_MONTH, parametro.getDiaFixoVencimento() + parametro.getVencLaudoAvaliacaoEmDias());
        } else if (parametro.getVencLaudoDeAvaliacao().equals(VencimentoLaudoDeAvaliacao.DATA_EMISSAO_LAUDO)) {
            c.setTime(dataEmissaoLaudo);
            c.add(Calendar.DAY_OF_MONTH, parametro.getVencLaudoAvaliacaoEmDias());
        }
        return c.getTime();
    }

    public void gerarLaudo(ProcessoCalculoITBI processoCalculoITBI) {
        ParametrosITBI parametro = recuperarParametroITBIVigente(processoCalculoITBI.getExercicio(), processoCalculoITBI.getTipoITBI());
        LaudoAvaliacaoITBI laudo = new LaudoAvaliacaoITBI();
        laudo.setProcessoCalculoITBI(processoCalculoITBI);
        laudo.setDataImpressaoLaudo(new Date());
        laudo.setDataVencimento(montarDataVencimentoLaudo(parametro, processoCalculoITBI.getDataLancamento(),
            laudo.getDataImpressaoLaudo()));
        em.persist(laudo);
    }
}
