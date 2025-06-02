/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.nfse.domain.TemplateNfse;
import br.com.webpublico.nfse.domain.dtos.IssqnFmLancamentoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.IssqnFmTipoLancamentoNfseDTO;
import br.com.webpublico.nfse.domain.template.TrocaTagNfseEmailIssForaMunicipio;
import br.com.webpublico.nfse.enums.TipoTemplateNfse;
import br.com.webpublico.nfse.facades.ConfiguracaoNfseFacade;
import br.com.webpublico.nfse.facades.TemplateNfseFacade;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.EmailService;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author daniel
 */
@Stateless
public class CalculaISSFacade extends AbstractFacade<CalculoISS> {

    private static final BigDecimal CEM = new BigDecimal("100");
    private static final int NUMERO_CASAS_DECIMAIS = 4;
    private static final RoundingMode MODO_ARREDONDAMENTO = RoundingMode.HALF_EVEN;
    private static final int PRIMEIRO_LANCAMENTO_MES = 1;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private IndiceEconomicoFacade indiceEconomicoFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    private Date dataAtual = new Date();
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ConfiguracaoNfseFacade configuracaoNfseFacade;
    @EJB
    private GeraValorDividaISSForaMunicipio geraValorDividaISSForaMunicipio;
    @EJB
    private TemplateNfseFacade templateNfseFacade;

    public CalculaISSFacade() {
        super(CalculoISS.class);
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProcessoCalculoISS calcularIssHomologado(CadastroEconomico cadastroEconomico, Exercicio exercicio, Integer mesReferencia, String observacoes, BigDecimal taxaSobreIss, boolean ausenciaMovimento, boolean salvar) throws ExcecaoNegocioGenerica {
        return calcularIssMensalEletronica(cadastroEconomico, exercicio, mesReferencia, observacoes, taxaSobreIss, ausenciaMovimento, TipoProcessoCalculoIss.NORMAL, salvar);
    }

    public ProcessoCalculoISS calcularIssMensalEletronica(CadastroEconomico cadastroEconomico, Exercicio exercicio, Integer mesReferencia, String observacoes, BigDecimal taxaSobreIss, boolean ausenciaMovimento, TipoProcessoCalculoIss tipoProcessoCalculoIss, boolean salvar) throws ExcecaoNegocioGenerica {
        cadastroEconomico = cadastroEconomicoFacade.recuperar(cadastroEconomico.getId());
        ProcessoCalculoISS processo = criarProcessoHomologado(cadastroEconomico, exercicio, mesReferencia, tipoProcessoCalculoIss);
        CalculoISS retorno = criarCalculoHomologado(processo, cadastroEconomico, taxaSobreIss, ausenciaMovimento, tipoProcessoCalculoIss, sistemaFacade.getUsuarioCorrente(), observacoes);
        retorno.setNotaEletronica(TipoProcessoCalculoIss.NFSE.equals(tipoProcessoCalculoIss));
        processo.getCalculos().add(retorno);
        processo.setObservacoes(observacoes);
        if (salvar) {
            em.persist(processo);
        }
        return processo;
    }

    public ProcessoCalculoISS calcularIssEstimado(CadastroEconomico cadastroEconomico, Exercicio exercicio, Integer mesReferencia) throws ExcecaoNegocioGenerica {
        cadastroEconomico = cadastroEconomicoFacade.recuperar(cadastroEconomico.getId());
        ProcessoCalculoISS processo = criarProcessoEstimado(cadastroEconomico, exercicio, mesReferencia);
        processo.getCalculos().add(criarCalculoEstimado(processo, cadastroEconomico, exercicio));
        return processo;
    }


    public ProcessoCalculoISS calcularIssFixo(CadastroEconomico cadastroEconomico, Exercicio exercicio, CalculoISS calculoISS) throws ExcecaoNegocioGenerica {
        cadastroEconomico = cadastroEconomicoFacade.recuperar(cadastroEconomico.getId());
        ProcessoCalculoISS processo = criarProcessoFixo(cadastroEconomico, exercicio);
        calculoISS = criarCalculoFixo(processo, cadastroEconomico, exercicio, sistemaFacade.getUsuarioCorrente(), calculoISS);
        processo.getCalculos().add(calculoISS);

        return em.merge(processo);
    }

    private ProcessoCalculoISS criarProcessoHomologado(CadastroEconomico cadastroEconomico,
                                                       Exercicio exercicio, Integer mesReferencia, TipoProcessoCalculoIss tipoProcesso) {
        if (exercicio == null) {
            exercicio = sistemaFacade.getExercicioCorrente();
        }
        String descricao = "ISS Mensal 'pessoa não identificada' " + mesReferencia + "/" + exercicio.getAno();
        if (cadastroEconomico != null && cadastroEconomico.getPessoa() != null) {
            descricao = "ISS Mensal " + cadastroEconomico.getPessoa().getNome() + " " + mesReferencia + "/" + exercicio.getAno();
        }
        return criarProcessoHomologado(descricao, exercicio, mesReferencia, tipoProcesso);
    }

    private ProcessoCalculoISS criarProcessoHomologado(String descricao,
                                                       Exercicio exercicio,
                                                       Integer mesReferencia,
                                                       TipoProcessoCalculoIss tipoProcesso) {
        ProcessoCalculoISS processo = new ProcessoCalculoISS();
        processo.setDataLancamento(this.dataAtual);
        processo.setExercicio(exercicio);
        processo.setMesReferencia(mesReferencia);
        processo.setDescricao(descricao);
        switch (tipoProcesso) {
            case FORA_MUNICIPIO:
                processo.setDivida(configuracaoNfseFacade.recuperarConfiguracao().getConfiguracaoIssForaMun().getDivida());
                break;
            case NORMAL:
                processo.setDivida(configuracaoTributarioFacade.retornaUltimo().getDividaISSHomologado());
                break;
            case SIMPLES_NACIONAL:
                processo.setDivida(configuracaoTributarioFacade.retornaUltimo().getDividaSimplesNacional());
                break;
        }

        processo.setCalculos(new ArrayList<CalculoISS>());
        return processo;
    }

    private ProcessoCalculoMultaAcessoria criarProcessoMultaAcessoria(Exercicio exercicio, Divida divida) {
        ProcessoCalculoMultaAcessoria processo = new ProcessoCalculoMultaAcessoria();
        processo.setDataLancamento(this.dataAtual);
        processo.setExercicio(exercicio);
        processo.setDivida(divida);
        processo.setCalculos(new ArrayList<CalculoMultaAcessoria>());
        return processo;
    }

    private ProcessoCalculoISS criarProcessoEstimado(CadastroEconomico cadastroEconomico, Exercicio exercicio, Integer mesReferencia) {
        Calendar c = Calendar.getInstance();
        c.setTime(cadastroEconomico.getAbertura() != null ? cadastroEconomico.getAbertura() : new Date());
        ProcessoCalculoISS processo = new ProcessoCalculoISS();
        processo.setDataLancamento(this.dataAtual);
        processo.setExercicio(exercicio);
        processo.setMesReferencia(mesReferencia);
        processo.setDescricao("ISS Estimado" + cadastroEconomico.getPessoa().getNome() + "/" + processo.getExercicio().getAno());
        processo.setDivida(configuracaoTributarioFacade.retornaUltimo().getDividaISSEstimado());
        processo.setCalculos(new ArrayList<CalculoISS>());
        return processo;
    }

    public ProcessoCalculoISS criarProcessoFixo(CadastroEconomico cadastroEconomico, Exercicio exercicio) {
        Calendar c = Calendar.getInstance();
        c.setTime((cadastroEconomico != null && cadastroEconomico.getAbertura() != null) ? cadastroEconomico.getAbertura() : new Date());
        ProcessoCalculoISS processo = new ProcessoCalculoISS();
        processo.setDataLancamento(this.dataAtual);
        processo.setExercicio(exercicio);
        processo.setMesReferencia(-1);

        if (cadastroEconomico != null
            && cadastroEconomico.getPessoa() != null
            && cadastroEconomico.getPessoa().getNome() != null
            && processo != null
            && processo.getExercicio() != null
            && processo.getExercicio().getAno() != null) {
            processo.setDescricao("ISS Fixo" + cadastroEconomico.getPessoa().getNome() + "/" + processo.getExercicio().getAno());
        }

        processo.setDivida(configuracaoTributarioFacade.retornaUltimo().getDividaISSFixo());
        processo.setCalculos(new ArrayList<CalculoISS>());
        return processo;
    }

    public ProcessoCalculoMultaAcessoria calcularMultaAcessoria(CalculoISS calculo, Exercicio exercicio, ConfiguracaoTributario configuracao, UsuarioSistema usuarioSistema, String descricao, BigDecimal valor, Calculo.TipoCalculo tipoCalculo, Boolean issComMOvimento, boolean salvar) throws ExcecaoNegocioGenerica {
        try {
            ProcessoCalculoMultaAcessoria processo = criarProcessoMultaAcessoria(exercicio, configuracao.getDividaMultaAcessoria());
            processo.setDescricao(descricao);
            processo.setDataLancamento(sistemaFacade.getDataOperacao());
            processo.setCalculoISS(calculo);
            processo.setConfiguracaoTributario(configuracao);
            processo.getCalculos().add(criarCalculoMultaAcessoria(calculo.getCadastroEconomico(), usuarioSistema, processo, configuracao, valor, tipoCalculo, issComMOvimento));
            if (salvar) {
                processo = em.merge(processo);
            }
            return processo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CalculoMultaAcessoria criarCalculoMultaAcessoria(CadastroEconomico cmc, UsuarioSistema usuarioSistema, ProcessoCalculoMultaAcessoria processo, ConfiguracaoTributario configuracao, BigDecimal valor, Calculo.TipoCalculo tipoCalculo, Boolean issComMovimento) throws ExcecaoNegocioGenerica {
        CalculoMultaAcessoria calculoMultaAcessoria = new CalculoMultaAcessoria();
        calculoMultaAcessoria.setIssComMovimento(issComMovimento);
        calculoMultaAcessoria.setCadastro(cmc);
        calculoMultaAcessoria.setTipoCalculo(tipoCalculo);
        calculoMultaAcessoria.setUsuarioLancamento(usuarioSistema);
        calculoMultaAcessoria.setProcessoCalculoMultaAcessoria(processo);
        calculoMultaAcessoria.setValorEfetivo(valor);
        calculoMultaAcessoria.setValorReal(valor);
        calculoMultaAcessoria.setListaItemCalculoMultaAcessoria(new ArrayList<ItemCalculoMultaAcessoria>());
        ItemCalculoMultaAcessoria item = new ItemCalculoMultaAcessoria();
        item.setTributo(configuracao.getTributoMultaAcessoria());
        item.setValor(valor);
        item.setCalculoMultaAcessoria(calculoMultaAcessoria);
        calculoMultaAcessoria.getListaItemCalculoMultaAcessoria().add(item);

        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setPessoa(cmc.getPessoa());
        calculoPessoa.setCalculo(calculoMultaAcessoria);
        if (calculoMultaAcessoria.getPessoas() == null) {
            calculoMultaAcessoria.setPessoas(new ArrayList<CalculoPessoa>());
        }
        calculoMultaAcessoria.getPessoas().add(calculoPessoa);
        return calculoMultaAcessoria;
    }

    private CalculoISS criarCalculoHomologado(ProcessoCalculoISS processoCalculo,
                                              CadastroEconomico cadastroEconomico,
                                              BigDecimal taxaSobreIss,
                                              boolean ausenciaMovimento,
                                              TipoProcessoCalculoIss tipoProcessoCalculoIss,
                                              UsuarioSistema usuario,
                                              String observacao) throws ExcecaoNegocioGenerica {
        CalculoISS retorno = new CalculoISS();
        retorno.setObservacao(observacao);
        retorno.setSubDivida(Long.valueOf(processoCalculo.getMesReferencia()));
        retorno.setSequenciaLancamento(buscarSequenciaDeLancamentoDoCalculoIssEstimadoMensalOuHomologado(cadastroEconomico, processoCalculo.getExercicio(), processoCalculo.getMesReferencia(), TipoCalculoISS.MENSAL));
        if (cadastroEconomico.getEnquadramentoVigente() != null) {
            retorno.setIsento(cadastroEconomico.getEnquadramentoVigente().isIsento());
        }
        retorno.setAusenciaMovimento(ausenciaMovimento);
        retorno.setValorEfetivo(taxaSobreIss);
        retorno.setValorReal(taxaSobreIss);
        retorno.setValorCalculado(taxaSobreIss);
        retorno.setTaxaSobreIss(taxaSobreIss);
        retorno.setBaseCalculo(BigDecimal.ZERO);
        retorno.setFaturamento(BigDecimal.ZERO);
        retorno.setCadastroEconomico(cadastroEconomico);
        retorno.setCadastro(cadastroEconomico);
        retorno.setDataCalculo(this.dataAtual);
        retorno.setProcessoCalculoISS(processoCalculo);
        retorno.setSimulacao(Boolean.TRUE);
        retorno.setTipoCalculoISS(TipoCalculoISS.MENSAL);
        retorno.setTipoSituacaoCalculoISS(TipoSituacaoCalculoISS.LANCADO);
        try {
            retorno.setUsuarioLancamento(usuario);
        } catch (Exception e) {
            retorno.setUsuarioLancamento(null);
        }
        if (retorno.getItemCalculoIsss().isEmpty()) {
            ConfiguracaoTributario configuracao = configuracaoTributarioFacade.retornaUltimo();

            for (Servico servico : cadastroEconomico.getServico()) {
                ItemCalculoIss item = new ItemCalculoIss();
                item.setCalculo(retorno);
                item.setAliquota(BigDecimal.valueOf(5));
                item.setBaseCalculo(BigDecimal.ZERO);
                item.setFaturamento(BigDecimal.ZERO);
                item.setValorCalculado(BigDecimal.ZERO);
                item.setServico(servico);
//                if (TipoProcessoCalculoIss.NFSE.equals(tipoProcessoCalculoIss)) {
//                    item.setTributo(configuracao.getTributoNfseIssqn());
//                } else {
                item.setTributo(configuracao.getTributoISS());
//                }
                retorno.getItemCalculoIsss().add(item);
            }
        }

        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setPessoa(cadastroEconomico.getPessoa());
        calculoPessoa.setCalculo(retorno);
        if (retorno.getPessoas() == null) {
            retorno.setPessoas(new ArrayList<CalculoPessoa>());
        }
        retorno.getPessoas().add(calculoPessoa);
        return retorno;

    }

    private CalculoISS criarCalculoForaMunicipio(ProcessoCalculoISS processoCalculo,
                                                 Pessoa pessoa,
                                                 BigDecimal faturamento,
                                                 BigDecimal baseCalculo,
                                                 BigDecimal aliquota,
                                                 BigDecimal valorIss,
                                                 String observacao,
                                                 IssqnFmTipoLancamentoNfseDTO issqnFmTipoLancamentoNfse) throws ExcecaoNegocioGenerica {
        CalculoISS retorno = new CalculoISS();
        retorno.setObservacao(observacao);
        retorno.setMesdeReferencia(Mes.getMesToInt(processoCalculo.getMesReferencia()));
        retorno.setSequenciaLancamento(buscarSequenciaDeLancamentoDoCalculoForPorPessoa(pessoa, processoCalculo.getExercicio(), processoCalculo.getMesReferencia(), TipoCalculoISS.MENSAL));
        retorno.setSubDivida(Long.valueOf(processoCalculo.getMesReferencia()));
        retorno.setIsento(Boolean.FALSE);
        retorno.setAusenciaMovimento(Boolean.FALSE);
        retorno.setFaturamento(faturamento);
        retorno.setBaseCalculo(baseCalculo);
        retorno.setAliquota(aliquota);
        retorno.setValorEfetivo(valorIss);
        retorno.setValorReal(valorIss);
        retorno.setValorCalculado(valorIss);
        retorno.setTaxaSobreIss(valorIss);
        retorno.setCadastroEconomico(cadastroEconomicoFacade.buscarCadastroEconomicoAtivoPorPessoa(pessoa));
        retorno.setCadastro(null);
        retorno.setDataCalculo(this.dataAtual);
        retorno.setProcessoCalculoISS(processoCalculo);
        retorno.setSimulacao(Boolean.FALSE);
        retorno.setTipoCalculoISS(TipoCalculoISS.MENSAL);
        retorno.setTipoSituacaoCalculoISS(TipoSituacaoCalculoISS.LANCADO);
        retorno.setGeradoPortalContribuinte(Boolean.TRUE);
        retorno.setIssqnFmTipoLancamentoNfse(issqnFmTipoLancamentoNfse);
        retorno.setItemCalculoIsss(new ArrayList());
        ConfiguracaoNfse configuracaoNfse = configuracaoNfseFacade.recuperarConfiguracao();

        ItemCalculoIss item = new ItemCalculoIss();
        item.setCalculo(retorno);
        item.setAliquota(aliquota);
        item.setBaseCalculo(baseCalculo);
        item.setFaturamento(faturamento);
        item.setValorCalculado(valorIss);
        item.setTributo(configuracaoNfse.getConfiguracaoIssForaMun().getTributo());
        retorno.getItemCalculoIsss().add(item);

        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setPessoa(pessoa);
        calculoPessoa.setCalculo(retorno);
        if (retorno.getPessoas() == null) {
            retorno.setPessoas(new ArrayList<CalculoPessoa>());
        }
        retorno.getPessoas().add(calculoPessoa);
        return retorno;

    }

    public void criarItensCalculoISSMensal(CalculoISS calculoISS, ProcessoCalculoISS processoCalculo, List<Servico> servicos, String motivo, boolean salvar) throws ValidacaoException {
        ConfiguracaoTributario configuracao = configuracaoTributarioFacade.retornaUltimo();
        BigDecimal valorCalculado = BigDecimal.ZERO;
        for (Servico servico : servicos) {
            ItemCalculoIss item = new ItemCalculoIss();
            item.setAliquota(servico.getAliquotaISSHomologado());
            item.setBaseCalculo(servico.getValorBase());
            item.setFaturamento(servico.getFaturamento());
            item.setServico(servico);
            item.setCalculo(calculoISS);
            item.setValorCalculado(calcularValorHomologado(servico));
            item.setTributo(configuracao.getTributoISS());
            calculoISS.getItemCalculoIsss().add(item);
            valorCalculado = valorCalculado.add(item.getValorCalculado());
            calculoISS.setBaseCalculo(calculoISS.getBaseCalculo().add(servico.getValorBase()));
            calculoISS.setFaturamento(calculoISS.getFaturamento().add(servico.getFaturamento()));
        }
        if (calculoISS.getTaxaSobreIss().compareTo(BigDecimal.ZERO) > 0) {
            ItemCalculoIss item = new ItemCalculoIss();
            item.setBaseCalculo(BigDecimal.ZERO);
            item.setCalculo(calculoISS);
            item.setFaturamento(BigDecimal.ZERO);
            item.setAliquota(BigDecimal.ZERO);
            item.setValorCalculado(calculoISS.getTaxaSobreIss());
            item.setTributo(configuracao.getTributoTaxaSobreISS());
            calculoISS.getItemCalculoIsss().add(item);
            valorCalculado = valorCalculado.add(item.getValorCalculado());
        }
        calculoISS.setValorCalculado(valorCalculado);
        calculoISS.setValorReal(valorCalculado);
        calculoISS.setValorEfetivo(valorCalculado);
        calculoISS.setMotivoLancamentoValorMenor(motivo);
    }

    private boolean isPrimeiroLancamentoNoMes(ProcessoCalculoISS processoCalculo) {
        return processoCalculo.getCalculos().get(0).getSequenciaLancamento() == PRIMEIRO_LANCAMENTO_MES;
    }


    public boolean isPassivelMultaAcessoria(ProcessoCalculoISS processoCalculo) {
        if (!isPrimeiroLancamentoNoMes(processoCalculo)) {
            return false;
        }
        ConfiguracaoTributario configuracao = configuracaoTributarioFacade.retornaUltimo();
        if (configuracao.getDividaISSHomologado() != null) {
            Divida divISS = em.find(Divida.class, configuracao.getDividaISSHomologado().getId());
            if (!divISS.getOpcaoPagamentosDivida().isEmpty()) {
                OpcaoPagamentoDivida opd = divISS.getOpcaoPagamentosDivida().get(0);
                if (opd != null && !opd.getOpcaoPagamento().getParcelas().isEmpty()) {
                    Parcela parcela = opd.getOpcaoPagamento().getParcelas().get(0);
                    if (parcela instanceof ParcelaFixaPeriodica) {
                        Calendar limite = Calendar.getInstance();
                        int dia = ((ParcelaFixaPeriodica) parcela).getDiaVencimento();
                        limite.set(Calendar.DAY_OF_MONTH, dia);
                        limite.set(Calendar.MONTH, processoCalculo.getMesReferencia() - 1);
                        limite.set(Calendar.YEAR, processoCalculo.getExercicio().getAno());
                        if (processoCalculo.getDataLancamento().after(limite.getTime())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public ProcessoCalculoMultaAcessoria processoCalculoMultaAcessoria(CalculoISS calculoISS) {
        ConfiguracaoTributario configuracao = configuracaoTributarioFacade.retornaUltimo();
        ProcessoCalculoISS processoCalculo = calculoISS.getProcessoCalculoISS();
        ProcessoCalculoMultaAcessoria processo = null;
        if (configuracao.getDividaISSHomologado() != null) {
            if (isPassivelMultaAcessoria(processoCalculo)) {
                BigDecimal valor = BigDecimal.ZERO;
                if (configuracao.getMultaAcessoriaComMovimento() != null && configuracao.getMultaAcessoriaComMovimento().compareTo(BigDecimal.ZERO) > 0) {
                    valor = configuracao.getMultaAcessoriaComMovimento().multiply(moedaFacade.recuperaValorVigenteUFM());

                } else if (configuracao.getMultaAcessoriaSemMovimento() != null && configuracao.getMultaAcessoriaSemMovimento().compareTo(BigDecimal.ZERO) > 0) {
                    valor = configuracao.getMultaAcessoriaSemMovimento().multiply(moedaFacade.recuperaValorVigenteUFM());
                }
                processo = calcularMultaAcessoria(calculoISS, processoCalculo.getExercicio(), configuracao, sistemaFacade.getUsuarioCorrente(), "Declaração de Ausência de Movimento com Aplicação de Multa Acessória por Não Declaração de ISS", valor, Calculo.TipoCalculo.MULTA_ACESSORIA, false, false);
            }
        } else {
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível encontrar a Dívida para o ISS Mensal! Por favor, verifique as Configurações do Tributário.");
            ve.lancarException();
        }
        return processo;
    }


    private BigDecimal calcularValorHomologado(Servico servico) {
        BigDecimal aliquota = servico.getAliquotaISSHomologado().divide(CEM, 6, RoundingMode.DOWN);
        BigDecimal retorno = servico.getValorBase();
        retorno = retorno.multiply(aliquota);
        retorno = retorno.setScale(2, MODO_ARREDONDAMENTO);
        return retorno;
    }

    public ValorDivida buscarValorDividaDoCalculo(CalculoISS calculo) {
        Query q = em.createQuery("from ValorDivida vd where vd.calculo = :calculo");
        q.setParameter("calculo", calculo);
        List<ValorDivida> resultado = q.getResultList();
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            ValorDivida vd = resultado.get(0);
            vd.getParcelaValorDividas().size();
            for (ParcelaValorDivida pvd : vd.getParcelaValorDividas()) {
                pvd.getOpcaoPagamento().getParcelas().size();
            }
            return vd;
        }
    }

    public CalculoISS criarCalculoFixo(ProcessoCalculoISS processo, CadastroEconomico cmc, Exercicio
        exercicio, UsuarioSistema usuarioSistema, CalculoISS calculoISS) throws ExcecaoNegocioGenerica {
        BigDecimal ufmPorProfissional = BigDecimal.ZERO;
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();

        if (configuracaoTributario == null) {
            throw new ExcecaoNegocioGenerica("Não foi possível lançar ISSQN Fixo para o C.M.C. " +
                cmc.getInscricaoCadastral() + "! Não há Configuração do Tributário válida!");
        } else if (configuracaoTributario.getDividaISSFixo() == null) {
            throw new ExcecaoNegocioGenerica("Não foi possível lançar ISSQN Fixo para o C.M.C. " +
                cmc.getInscricaoCadastral() + "! Não há configuração de Dívida de ISS Fixo nas Configurações do Tributário!");
        } else if (configuracaoTributario.getDividaISSFixo().getConfiguracaoDAM() == null) {
            throw new ExcecaoNegocioGenerica("Não foi possível lançar ISSQN Fixo para o C.M.C. " +
                cmc.getInscricaoCadastral() + "! Não há Configuração de DAM para a dívida " + configuracaoTributario.getDividaISSFixo() + "!");
        }

        if (cmc.getPessoa() instanceof PessoaFisica) {
            if (cmc.getTipoAutonomo() == null || cmc.getTipoAutonomo().getId() == null) {
                throw new ExcecaoNegocioGenerica("Não foi possível lançar ISSQN Fixo para o C.M.C. " +
                    cmc.getInscricaoCadastral() + "! O C.M.C. não possui Tipo de Autônomo cadastrado!");
            }

            if (!cmc.getTipoAutonomo().getGeraISS()) {
                throw new ExcecaoNegocioGenerica("Não foi possível lançar ISSQN Fixo para o C.M.C. " +
                    cmc.getInscricaoCadastral() + "! O Tipo de Autônomo do C.M.C. não gera ISSQN!");
            }

            if (cmc.getNaturezaJuridica() != null && !cmc.getNaturezaJuridica().getAutonomo()) {
                throw new ExcecaoNegocioGenerica("Não foi possível lançar ISSQN Fixo para o C.M.C. " +
                    cmc.getInscricaoCadastral() + "! O Tipo de Pessoa do C.M.C. não é Autônomo!");
            }
        } else {
            ufmPorProfissional = configuracaoTributario.getQtdeUfmIssFixoProfSuperior();
        }
        if (calculoISS == null) {
            calculoISS = new CalculoISS();
            calculoISS.setQuantidadeProfissionais(cadastroEconomicoFacade.numeroDeSocios(cmc));
            calculoISS.setQtdeUFMProfissionalSocio(configuracaoTributario.getQtdeUfmIssFixoProfSuperior());
        }
        BigDecimal valorBase;
        if (cmc.getPessoa() instanceof PessoaFisica) {
            valorBase = cmc.getTipoAutonomo().getValorUFMRB();
        } else {
            valorBase = calculoISS != null ? new BigDecimal(calculoISS.getQuantidadeProfissionais()).multiply(ufmPorProfissional) : BigDecimal.ZERO;
        }
        if (valorBase == null) {
            throw new ExcecaoNegocioGenerica("Não foi possível lançar ISSQN Fixo para o C.M.C. " +
                cmc.getInscricaoCadastral() + "! O Tipo de Autônomo não possui valor do ISSQN informado!");
        }

        if (cmc.getEnquadramentoVigente().getTipoIssqn() == null) {
            throw new ExcecaoNegocioGenerica("Não foi possível lançar ISSQN Fixo para o C.M.C. " +
                cmc.getInscricaoCadastral() + "! O Tipo de ISSQN não foi informado!");
        } else if (!cmc.getEnquadramentoVigente().getTipoIssqn().equals(TipoIssqn.FIXO)) {
            throw new ExcecaoNegocioGenerica("Não foi possível lançar ISSQN Fixo para o C.M.C. " +
                cmc.getInscricaoCadastral() + "! O Tipo de ISSQN do C.M.C. não é Fixo!");
        }


        if (exercicio.getAno() < cmc.getAnoDeAbertura()) {
            throw new ExcecaoNegocioGenerica("Não foi possível lançar ISSQN Fixo para o C.M.C. " +
                cmc.getInscricaoCadastral() + "! O exercício de lançamento deve ser maior ou igual ao ano de abertura do C.M.C!");
        }

        if (moedaFacade.recuperaValorUFMPorExercicio(exercicio.getAno()).compareTo(BigDecimal.ZERO) == 0) {
            throw new ExcecaoNegocioGenerica("Não foi possível lançar ISSQN Fixo para o C.M.C. " +
                cmc.getInscricaoCadastral() + "! Não existe valor de UFM para o exercício!");
        }

        BigDecimal valorUFMRB = calcularValorUFMRB(cmc, exercicio);
        BigDecimal valorISS = calcularValorAnualEmUFM(valorBase, cmc, exercicio);
        valorBase = valorBase.multiply(valorUFMRB);
        valorISS.setScale(NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
        CalculoISS retorno = new CalculoISS();
        retorno.setSubDivida(Long.valueOf(1));
        retorno.setSequenciaLancamento(buscarSequenciaDeLancamentoDoCalculoIssFixoOuEstimadoAnual(cmc, exercicio, TipoCalculoISS.FIXO));
        retorno.setBaseCalculo(valorBase);
        retorno.setCadastro(cmc);
        retorno.setCadastroEconomico(cmc);
        retorno.setDataCalculo(this.dataAtual);
        retorno.setProcessoCalculoISS(processo);
        retorno.setSimulacao(Boolean.TRUE);
        retorno.setTipoCalculoISS(TipoCalculoISS.FIXO);
        retorno.setValorCalculado(valorISS);
        retorno.setValorReal(valorISS);
        retorno.setValorEfetivo(valorISS);
        retorno.setTipoSituacaoCalculoISS(TipoSituacaoCalculoISS.LANCADO);
        retorno.setUsuarioLancamento(usuarioSistema);
        if (calculoISS != null && calculoISS.getQuantidadeProfissionais() != null) {
            retorno.setQuantidadeProfissionais(calculoISS.getQuantidadeProfissionais());
        }
        if (calculoISS != null && calculoISS.getQtdeUFMProfissionalSocio() != null) {
            retorno.setQtdeUFMProfissionalSocio(calculoISS.getQtdeUFMProfissionalSocio());
        }
        ItemCalculoIss item = new ItemCalculoIss();
        item.setAliquota(null);
        item.setBaseCalculo(valorISS);
        item.setFaturamento(valorISS);
        item.setServico(null);
        item.setCalculo(retorno);
        item.setValorCalculado(valorISS);
        item.setTributo(configuracaoTributario.getTributoISS());

        retorno.getItemCalculoIsss().add(item);

        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setPessoa(cmc.getPessoa());
        calculoPessoa.setCalculo(retorno);

        if (retorno.getPessoas() == null) {
            retorno.setPessoas(new ArrayList<CalculoPessoa>());
        }
        retorno.getPessoas().add(calculoPessoa);
        return retorno;
    }

    private Long buscarSequenciaDeLancamentoDoCalculoIssFixoOuEstimadoAnual(CadastroEconomico cmc, Exercicio
        exercicio, TipoCalculoISS tipoCalculoISS) {
        String sql = " SELECT count(ciss.id) + 1 " +
            "        FROM calculoiss ciss " +
            "  INNER JOIN processocalculo pc ON pc.id = ciss.processocalculoiss_id " +
            "  INNER JOIN cadastroeconomico ce ON ce.id = ciss.cadastroeconomico_id " +
            "  INNER JOIN exercicio ex ON ex.id = pc.exercicio_id " +
            "       WHERE ciss.tipocalculoiss = :tipo_iss " +
            "         AND ex.id = :exercicio_id " +
            "         AND ce.id = :cmc_id";

        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo_iss", tipoCalculoISS.name());
        q.setParameter("exercicio_id", exercicio.getId());
        q.setParameter("cmc_id", cmc.getId());

        BigDecimal retorno = BigDecimal.ZERO;

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            retorno = (BigDecimal) resultList.get(0);
        }

        return retorno.longValue();
    }

    private Long buscarSequenciaDeLancamentoDoCalculoForPorPessoa(Pessoa pessoa, Exercicio exercicio, Integer mesReferencia, TipoCalculoISS tipoCalculoISS) {
        String sql = " SELECT count(distinct ciss.id) + 1 " +
            "        FROM calculoiss ciss " +
            "  INNER JOIN processocalculo pc ON pc.id = ciss.processocalculoiss_id " +
            "  INNER JOIN processocalculoiss pci ON pci.id = pc.id " +
            "  INNER JOIN exercicio ex ON ex.id = pc.exercicio_id " +
            "  inner join Calculopessoa cp on cp.CALCULO_ID = ciss.id " +
            "       WHERE ciss.tipocalculoiss = :tipo_iss " +
            "         AND ex.id = :exercicio_id " +
            "         AND cp.pessoa_id = :pessoa_id ";
        if (mesReferencia != null) {
            sql += " AND pci.mesreferencia = :mes_referencia";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo_iss", tipoCalculoISS.name());
        q.setParameter("exercicio_id", exercicio.getId());
        q.setParameter("pessoa_id", pessoa.getId());
        if (mesReferencia != null) {
            q.setParameter("mes_referencia", mesReferencia);
        }

        BigDecimal retorno = BigDecimal.ZERO;

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            retorno = (BigDecimal) resultList.get(0);
        }

        return retorno.longValue();
    }

    public Long buscarSequenciaDeLancamentoDoCalculoIssEstimadoMensalOuHomologado(CadastroEconomico
                                                                                      cadastroEconomico, Exercicio exercicio, Integer mesReferencia, TipoCalculoISS tipoCalculoISS) {
        String sql = " SELECT count(ciss.id) + 1 " +
            "        FROM calculoiss ciss " +
            "  INNER JOIN calculo cal ON cal.id = ciss.id " +
            "  INNER JOIN processocalculo pc ON pc.id = ciss.processocalculoiss_id " +
            "  INNER JOIN processocalculoiss pci ON pci.id = pc.id " +
            "  INNER JOIN cadastroeconomico ce ON ce.id = ciss.cadastroeconomico_id " +
            "  INNER JOIN exercicio ex ON ex.id = pc.exercicio_id " +
            "       WHERE ciss.tipocalculoiss = :tipo_iss " +
            "         AND ex.id = :exercicio_id " +
            "         AND ce.id = :cmc_id ";
        if (mesReferencia != null) {
            sql += " AND pci.mesreferencia = :mes_referencia";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo_iss", tipoCalculoISS.name());
        q.setParameter("exercicio_id", exercicio.getId());
        q.setParameter("cmc_id", cadastroEconomico.getId());
        if (mesReferencia != null) {
            q.setParameter("mes_referencia", mesReferencia);
        }

        BigDecimal retorno = BigDecimal.ZERO;

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            retorno = (BigDecimal) resultList.get(0);
        }

        return retorno.longValue();
    }

    private CalculoISS criarCalculoEstimado(ProcessoCalculoISS processo, CadastroEconomico
        cadastroEconomico, Exercicio exercicio) throws ExcecaoNegocioGenerica {
        BigDecimal valorBase = cadastroEconomico.getEnquadramentoVigente().getIssEstimado();
        BigDecimal valorISS;
        BigDecimal valorUFMRB = calcularValorUFMRB(cadastroEconomico, exercicio);

        Long subdivida = Long.valueOf(0);
        Long sequencia = Long.valueOf(0);

        if (TipoPeriodoValorEstimado.ANUAL.equals(cadastroEconomico.getEnquadramentoVigente().getTipoPeriodoValorEstimado())) {
            valorISS = calcularValorAnualEmUFM(valorBase, cadastroEconomico, exercicio);
            valorBase = valorBase.multiply(valorUFMRB);

            subdivida = Long.valueOf(1);
            sequencia = buscarSequenciaDeLancamentoDoCalculoIssFixoOuEstimadoAnual(cadastroEconomico, exercicio, TipoCalculoISS.ESTIMADO);
        } else {
            valorBase = valorBase.multiply(valorUFMRB);
            valorISS = valorBase;

            subdivida = Long.valueOf(processo.getMesReferencia());
            sequencia = buscarSequenciaDeLancamentoDoCalculoIssEstimadoMensalOuHomologado(cadastroEconomico, exercicio, processo.getMesReferencia(), TipoCalculoISS.ESTIMADO);
        }

        valorISS.setScale(NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);

        CalculoISS retorno = new CalculoISS();
        retorno.setSubDivida(subdivida);
        retorno.setSequenciaLancamento(sequencia);
        retorno.setBaseCalculo(valorBase);
        retorno.setCadastro(cadastroEconomico);
        retorno.setCadastroEconomico(cadastroEconomico);
        retorno.setAliquota(null);
        retorno.setDataCalculo(this.dataAtual);
        retorno.setProcessoCalculoISS(processo);
        retorno.setSimulacao(Boolean.TRUE);
        retorno.setTipoCalculoISS(TipoCalculoISS.ESTIMADO);
        retorno.setValorCalculado(valorISS);
        retorno.setValorReal(valorISS);
        retorno.setValorEfetivo(valorISS);
        retorno.setTipoSituacaoCalculoISS(TipoSituacaoCalculoISS.LANCADO);
        retorno.setUsuarioLancamento(sistemaFacade.getUsuarioCorrente());
        ItemCalculoIss item = new ItemCalculoIss();
        item.setAliquota(null);
        item.setBaseCalculo(valorISS);
        item.setFaturamento(valorISS);
        item.setServico(null);
        item.setCalculo(retorno);
        item.setValorCalculado(valorISS);
        item.setTributo(configuracaoTributarioFacade.retornaUltimo().getTributoISS());
        retorno.getItemCalculoIsss().add(item);
        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setPessoa(cadastroEconomico.getPessoa());
        calculoPessoa.setCalculo(retorno);
        if (retorno.getPessoas() == null) {
            retorno.setPessoas(new ArrayList<CalculoPessoa>());
        }
        retorno.getPessoas().add(calculoPessoa);
        return retorno;
    }

    private BigDecimal calcularValorAnualEmUFM(BigDecimal valorBase, CadastroEconomico bce, Exercicio exercicio) {
        BigDecimal retorno;
        BigDecimal valorUFMRB = calcularValorUFMRB(bce, exercicio);
        retorno = valorBase.multiply(valorUFMRB).setScale(2, MODO_ARREDONDAMENTO);
        retorno = calcularProporcao(retorno, bce, exercicio);
        return retorno;
    }

    private BigDecimal calcularProporcao(BigDecimal valor, CadastroEconomico ce, Exercicio exercicio) {
        Calendar dataAbertura = Calendar.getInstance();
        Calendar hoje = Calendar.getInstance();
        BigDecimal valorProporcional = valor;
        dataAbertura.setTime(ce.getAbertura());
        if (isCadastroEconomicoProporcional(ce, exercicio) && (dataAbertura.get(Calendar.MONTH) != Calendar.JANUARY)) {
            valorProporcional = valor.divide(new BigDecimal("12"), 6, MODO_ARREDONDAMENTO);
            BigDecimal quantidadeMeses = new BigDecimal("12").subtract(new BigDecimal(dataAbertura.get(Calendar.MONTH)));
            valorProporcional = valorProporcional.multiply(quantidadeMeses);
            valorProporcional = valorProporcional.setScale(2, MODO_ARREDONDAMENTO);
        }
        return valorProporcional;
    }

    private BigDecimal calcularValorUFMRB(CadastroEconomico cadastroEconomico, Exercicio exercicio) {
        Calendar anoAtual = Calendar.getInstance();
        Calendar abertura = Calendar.getInstance();
        anoAtual.set(Calendar.MONTH, Calendar.JANUARY);
        abertura.setTime(cadastroEconomico.getAbertura());
        IndiceEconomico UFMRB = indiceEconomicoFacade.recuperaPorDescricao("UFM");
        if (UFMRB == null || UFMRB.getId() == null) {
            return BigDecimal.ZERO;
        }
        Moeda retorno = moedaFacade.getMoedaPorIndiceData(UFMRB, getDataProporcao(cadastroEconomico, exercicio));
        if (retorno != null && retorno.getValor() != null) {
            return retorno.getValor();
        } else {
            return BigDecimal.ZERO;
        }
    }

    private boolean isCadastroEconomicoProporcional(CadastroEconomico cadastroEconomico, Exercicio exercicio) {
        return exercicio.equals(getExercicioAberturaCadastroEconomico(cadastroEconomico));
    }

    private Exercicio getExercicioAberturaCadastroEconomico(CadastroEconomico cadastroEconomico) {
        Calendar abertura = Calendar.getInstance();
        abertura.setTime(cadastroEconomico.getAbertura());
        return exercicioFacade.getExercicioPorAno(abertura.get(Calendar.YEAR));
    }

    private Date getDataProporcao(CadastroEconomico cadastroEconomico, Exercicio exercicio) {
        if (isCadastroEconomicoProporcional(cadastroEconomico, exercicio)) {
            Calendar abertura = Calendar.getInstance();
            abertura.setTime(cadastroEconomico.getAbertura());
            return abertura.getTime();
        } else {
            Calendar retorno = Calendar.getInstance();
            retorno.set(Calendar.DAY_OF_MONTH, 1);
            retorno.set(Calendar.MONTH, Calendar.JANUARY);
            retorno.set(Calendar.YEAR, exercicio.getAno().intValue());
            return retorno.getTime();
        }
    }

    public List<ValorDivida> jaLancado(CadastroEconomico cadastroEconomico, Integer mes, Exercicio
        exercicio, TipoIssqn tipoIssqn) {
        List<ValorDivida> retorno = null;

        String sql = "  SELECT vd.id, vd.valor, vd.emissao, vd.calculo_id, vd.divida_id, vd.entidade_id, vd.exercicio_id, vd.dataquitacao " +
            "         FROM VWCONSULTADEDEBITOS vw inner join valordivida vd on vd.id = vw.valordivida_id "
            + " INNER JOIN calculoiss calculo             ON vd.calculo_id = calculo.id"
            + " INNER JOIN processocalculo processo       ON calculo.processocalculoiss_id = processo.id"
            + " INNER JOIN processocalculoiss processoiss ON processo.id = processoiss.id"
            + "      WHERE calculo.cadastroeconomico_id = :bce_id"
            + "        AND processo.exercicio_id        = :exercicio_id" +
            " and (vw.situacaoparcela != :estornado AND vw.situacaoparcela != :cancelado) AND CALCULO.TIPOCALCULOISS = :tipoiss ";

        if (mes != null && mes > 0) {
            sql += "        AND processoiss.mesreferencia    = :mesReferencia";
        }

        Query q = em.createNativeQuery(sql, ValorDivida.class);
        q.setParameter("bce_id", cadastroEconomico.getId());
        q.setParameter("exercicio_id", exercicio.getId());
        q.setParameter("tipoiss", tipoIssqn.name());
        q.setParameter("estornado", SituacaoParcela.ESTORNADO.name());
        q.setParameter("cancelado", SituacaoParcela.CANCELAMENTO.name());
        if (mes != null && mes > 0) {
            q.setParameter("mesReferencia", mes);
        }
        if (!q.getResultList().isEmpty()) {
            retorno = q.getResultList();
            retorno.size();
        }

        return retorno;
    }

    public ValorDivida recuperarValorDivida(ValorDivida vd) {
        vd = em.find(ValorDivida.class, vd.getId());
        vd.getParcelaValorDividas().size();
        vd.getId();
        return vd;
    }

    public Long buscarSubDividaCalculo(CadastroEconomico cadastroEconomico, Exercicio exercicio, Integer mes) {
        String sql = " SELECT coalesce(max(calc.subdivida), 0) + 1                               "
            + "    FROM processocalculoiss pciss                                             "
            + "   INNER JOIN processocalculo pc ON pciss.id = pc.id                          "
            + "   INNER JOIN calculoiss calciss ON calciss.processocalculoiss_id = pciss.id  "
            + "   INNER JOIN calculo calc ON calciss.id = calc.id                            "
            + " WHERE pc.exercicio_id     =:exercicio_id                                     "
            + "   AND calc.cadastro_id    =:cadastro_id                                      ";
        if (mes != null) {
            sql += " and pciss.mesreferencia =:mes ";
        }
        Query q = em.createNativeQuery(sql);
        if (mes != null) {
            q.setParameter("mes", mes);
        }
        q.setParameter("exercicio_id", exercicio.getId());
        q.setParameter("cadastro_id", cadastroEconomico.getId());
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            return ((BigDecimal) q.getResultList().get(0)).longValue();
        }
        return new Long(1);
    }

    public CalculoISS recuperar(CalculoISS calculoISS) {
        calculoISS = em.find(CalculoISS.class, calculoISS.getId());
        calculoISS.getItemCalculoIsss();
        calculoISS.getItemValor();
        calculoISS.getProcessoMultaAcessoria();
        return calculoISS;
    }

    public CalculoISS recuperarID(Object id) {
        CalculoISS c = em.find(CalculoISS.class, id);
        c.getItemCalculoIsss().size();
        c.getItemValor().size();
        c.getPessoas().size();
        return c;
    }

    public void salvar(ProcessoCalculoISS processo) {
        em.persist(processo);
    }

    public ProcessoCalculoISS salva(ProcessoCalculoISS processo) {
        processo = em.merge(processo);
        if (!processo.getCalculos().isEmpty()) {
            processo.getCalculos().get(0).getCadastroEconomico().getServico().size();
        }
        return processo;
    }

    public ProcessoCalculoMultaAcessoria salvaMultaAcessoria(ProcessoCalculoMultaAcessoria processo) {
        return em.merge(processo);
    }

    public CalculoISSEstorno salvarEstorno(CalculoISSEstorno estorno) {
        return em.merge(estorno);
    }

    @Override
    public List<CalculoISS> lista() {
        List<CalculoISS> retorno = new ArrayList<>();
        String hql = "select c from CalculoISS c ";
        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno = q.getResultList();
        }

        return retorno;
    }

    public String incluiTexto(String texto, int tamanho, boolean alinhaEsquerda) {
        String retorno = null;
        if (texto == null || tamanho < 0) {
            return "";
        }
        if (texto.length() >= tamanho) {
            return texto.substring(0, tamanho);
        } else {
            int diferenca = tamanho - texto.length();
            String espacos = "";
            for (int i = 0; i < diferenca; i++) {
                espacos += " ";
            }
            if (alinhaEsquerda) {
                retorno = texto + espacos;
            } else {
                retorno = espacos + texto;
            }
        }
        return retorno;
    }

    @Override
    public CalculoISS recarregar(CalculoISS calculo) {
        calculo = recuperar(calculo);
        calculo.getItemCalculoIsss().size();
        calculo.getPessoas().size();

        return calculo;
    }

    public List<ItemCalculoIss> itensCalculoISS(CalculoISS calculo) {
        List<ItemCalculoIss> retorno = new ArrayList();
        String hql = " select item from ItemCalculoIss item "
            + " where item.calculo =:calculo ";
        Query q = em.createQuery(hql);
        q.setParameter("calculo", calculo);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno = q.getResultList();
        }
        return retorno;
    }

    public List<CalculoISS> lista(String filtro, Integer quantidadeMaximaRegistros) {

        List<CalculoISS> retorno = new ArrayList<>();
        String hql = "select c from CalculoISS c ";
        if (filtro != null) {
            hql += " where lower(to_char(c.tipoCalculoISS)) like :filtro";
        }
        Query q = em.createQuery(hql);
        if (filtro != null) {
            q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        }
        q.setMaxResults(quantidadeMaximaRegistros);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno = q.getResultList();
        }

        return retorno;
    }

    public CalculoISS recuperarObj(Object id) {
        CalculoISS ci = em.find(CalculoISS.class, id);
        ci.getItemCalculoIsss().size();
        ci.getPessoas().size();
        return ci;
    }

    public ProcessoCalculoISS recarregar(ProcessoCalculoISS processoCalculoISS) {
        processoCalculoISS = em.find(ProcessoCalculoISS.class, processoCalculoISS.getId());
        processoCalculoISS.getCalculos().size();
        return processoCalculoISS;
    }

    public ProcessoCalculoMultaAcessoria recarregar(ProcessoCalculoMultaAcessoria processo) {
        processo = em.find(ProcessoCalculoMultaAcessoria.class, processo.getId());
        processo.getCalculos().size();
        for (CalculoMultaAcessoria c : processo.getCalculos()) {
            c.getListaItemCalculoMultaAcessoria().size();
        }
        return processo;
    }

    public CalculoMultaAcessoria recuperarCalculoMultaAcessoria(ProcessoCalculoISS processo) {
        String hql = "select calc from CalculoMultaAcessoria calc where calc.procCalcMultaAcessoria.calculoISS.processoCalculoISS = :pc";
        Query q = em.createQuery(hql);
        q.setParameter("pc", processo);
        try {
            CalculoMultaAcessoria clc = (CalculoMultaAcessoria) q.getSingleResult();
            clc.getListaItemCalculoMultaAcessoria().size();
            return (CalculoMultaAcessoria) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public ProcessoCalculoISS recarregarSemAListaDeCalculos(ProcessoCalculoISS processoCalculoISS) {
        processoCalculoISS = em.find(ProcessoCalculoISS.class, processoCalculoISS.getId());
        return processoCalculoISS;
    }

    public List<ParcelaValorDivida> buscarParcelasCalculo(Calculo calculo) {
        List<ParcelaValorDivida> toReturn = new ArrayList<>();
        String hql = " select p from ParcelaValorDivida p "
            + " inner join p.valorDivida valor "
            + " where valor.calculo = :calculo";
        Query q = em.createQuery(hql);
        q.setParameter("calculo", calculo);
        List<ParcelaValorDivida> parcelas = q.getResultList();
        if (!parcelas.isEmpty()) {
            toReturn.addAll(parcelas);
        }
        return parcelas;
    }

    public Boolean existeParcelaEmAberto(CalculoISS calculo) {
        Query q = em.createNativeQuery("SELECT parcela.id FROM parcelavalordivida parcela"
            + " INNER JOIN situacaoparcelavalordivida situacao ON situacao.id = parcela.situacaoatual_id "
            + " INNER JOIN valordivida ON parcela.valordivida_id = valordivida.id"
            + " INNER JOIN calculoiss calculo ON valordivida.calculo_id = calculo.id"
            + " WHERE situacao.situacaoparcela = 'EM_ABERTO'"
            + " AND calculo.id = :idCalculo");
        q.setParameter("idCalculo", calculo.getId());
        return q.getResultList().isEmpty();
    }

    public void salvarSituacaoParcelaValorDivida(SituacaoParcelaValorDivida situacao) {
        em.merge(situacao);
    }

    public boolean hasAcaoFiscalParaCadastroEconomico(CadastroEconomico cadastroEconomico, Date data) {
        String sql = "SELECT 1 " +
            "       FROM acaofiscal ac " +
            " INNER JOIN programacaofiscal p ON p.id = ac.programacaofiscal_id " +
            "      WHERE ac.cadastroeconomico_id = :cadastroEconomico " +
            "        AND (ac.situacaoacaofiscal  = :situacao_programado" +
            "         OR ac.situacaoacaofiscal   = :situacao_executando)" +
            "        AND (to_number(to_char(ac.datainicial,'yyyymm')) <= to_number(to_char(:datacalculoiss,'yyyymm')) AND " +
            "             to_number(to_char(ac.datafinal,'yyyymm')) >= to_number(to_char(:datacalculoiss,'yyyymm'))) ";
        data = DataUtil.dataSemHorario(data);
        Query q = em.createNativeQuery(sql);
        q.setParameter("cadastroEconomico", cadastroEconomico.getId());
        q.setParameter("situacao_programado", SituacaoAcaoFiscal.PROGRAMADO.name());
        q.setParameter("situacao_executando", SituacaoAcaoFiscal.EXECUTANDO.name());
        q.setParameter("datacalculoiss", data);
        return !q.getResultList().isEmpty();
    }

    public boolean temLancamentoNaReferencia(Integer mesReferencia, Exercicio exercicio, CadastroEconomico
        cadastroEconomico) {
        String hql = "select iss from CalculoISS  iss where " +
            " iss.processoCalculoISS.mesReferencia = :referencia  " +
            " and iss.processoCalculoISS.exercicio = :exercicio " +
            " and iss.cadastroEconomico = :cadastroEconomico";

        Query q = em.createQuery(hql);
        q.setParameter("referencia", mesReferencia);
        q.setParameter("exercicio", exercicio);
        q.setParameter("cadastroEconomico", cadastroEconomico);
        return !q.getResultList().isEmpty();
    }

    public ProcessoCalculoMultaAcessoria salvarProcessoCalculoMultaAcessoria(ProcessoCalculoMultaAcessoria
                                                                                 processoCalculoMultaAcessoria) {
        return em.merge(processoCalculoMultaAcessoria);
    }

    public CalculoISSEstorno recuperarEstornoPorId(Long id) {
        Long idEstorno = buscarIdEstornoPeloCalculo(id);
        CalculoISSEstorno calculoISSEstorno = em.find(CalculoISSEstorno.class, idEstorno);
        calculoISSEstorno.getCalculos().size();
        return calculoISSEstorno;
    }

    public Long buscarIdEstornoPeloCalculo(Long idCalculo) {
        String sql = " select c.calculoissestorno_id from calculoiss c " +
            " where c.id = :idCalculo ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCalculo", idCalculo);

        List<BigDecimal> ids = q.getResultList();
        return (ids != null && !ids.isEmpty() && ids.get(0) != null) ? ids.get(0).longValue() : null;
    }

    public List<CalculoISS> buscarCalculosParaEstorno(TipoCalculoISS tipoCalculoISS, CadastroEconomico
        cadastroEconomico) {
        String hql = "select iss from CalculoISS iss " +
            " where iss.cadastroEconomico = :cadastroEconomico " +
            "  and iss.tipoSituacaoCalculoISS = :situacao " +
            "  and iss.tipoCalculoISS = :tipoCalculoISS" +
            "  and exists (select pvd.id from ParcelaValorDivida pvd " +
            "                 join pvd.valorDivida vd " +
            "                 join pvd.situacaoAtual spvd " +
            "                 where vd.calculo = iss " +
            "                   and spvd.situacaoParcela = :situacaoParcela)" +
            " order by iss.processoCalculo.exercicio.ano, iss.sequenciaLancamento";
        Query q = em.createQuery(hql);
        q.setParameter("cadastroEconomico", cadastroEconomico);
        q.setParameter("situacao", TipoSituacaoCalculoISS.LANCADO);
        q.setParameter("tipoCalculoISS", tipoCalculoISS);
        q.setParameter("situacaoParcela", SituacaoParcela.EM_ABERTO);
        return q.getResultList();
    }

    public ProcessoCalculoISS criarCalculoParaDaf607(Exercicio exercicio, CadastroEconomico cadastroEconomico, Mes mes, BigDecimal valorCalculado, Date dataOperacao, UsuarioSistema usuarioSistema) {
        CalculoISS calculoISS = criarLancamentoDeIssMensal(exercicio, mes, cadastroEconomico, TipoProcessoCalculoIss.SIMPLES_NACIONAL, usuarioSistema, "");

        calculoISS.setValorCalculado(valorCalculado);
        calculoISS.setValorEfetivo(valorCalculado);
        calculoISS.setValorReal(valorCalculado);
        if (calculoISS.getItemCalculoIsss() != null && !calculoISS.getItemCalculoIsss().isEmpty()) {
            calculoISS.getItemCalculoIsss().get(0).setValorCalculado(valorCalculado);
        }

        return calculoISS.getProcessoCalculoISS();
    }

    private CalculoISS criarLancamentoDeIssMensal(Exercicio exercicio, Mes mes, CadastroEconomico ce, TipoProcessoCalculoIss tipoProcessoCalculoIss, UsuarioSistema usuarioSistema, String observacao) {
        ProcessoCalculoISS processoCalculoISS = criarProcessoHomologado(ce,
            exercicio, mes.getNumeroMes(), tipoProcessoCalculoIss);
        CalculoISS calculoISS = criarCalculoHomologado(processoCalculoISS, ce, BigDecimal.ZERO, false, tipoProcessoCalculoIss, usuarioSistema, observacao);
        processoCalculoISS.getCalculos().add(calculoISS);
        return calculoISS;
    }

    private void criarItensValorDividaParcela(Calculo calculo, ValorDivida valorDivida, ParcelaValorDivida parcelaValorDivida, Tributo tributo) {
        ItemValorDivida ivd = new ItemValorDivida();
        ivd.setTributo(tributo);
        ivd.setValor(calculo.getValorEfetivo());
        ivd.setValorDivida(valorDivida);
        valorDivida.getItemValorDividas().add(ivd);

        ItemParcelaValorDivida ipvd = new ItemParcelaValorDivida();
        ipvd.setItemValorDivida(ivd);
        ipvd.setParcelaValorDivida(parcelaValorDivida);
        ipvd.setValor(ivd.getValor());
        parcelaValorDivida.getItensParcelaValorDivida().add(ipvd);

    }

    private ParcelaValorDivida criarParcela(Calculo calculo, ValorDivida valorDivida, Date vencimento) {
        ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
        parcelaValorDivida.setDividaAtiva(Boolean.FALSE);
        parcelaValorDivida.setSequenciaParcela("1");
        parcelaValorDivida.setItensParcelaValorDivida(new ArrayList<ItemParcelaValorDivida>());
        parcelaValorDivida.setOpcaoPagamento(buscarOpcaoPagamento(valorDivida.getDivida(), new Date()));
        parcelaValorDivida.setValorDivida(valorDivida);
        parcelaValorDivida.setValor(calculo.getValorEfetivo());
        parcelaValorDivida.setVencimento(vencimento);
        parcelaValorDivida.setPercentualValorTotal(BigDecimal.valueOf(100));
        parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.AGUARDANDO, parcelaValorDivida, parcelaValorDivida.getValor()));
        valorDivida.getParcelaValorDividas().add(parcelaValorDivida);
        return parcelaValorDivida;
    }

    public OpcaoPagamento buscarOpcaoPagamento(Divida divida, Date dataBase) throws IllegalArgumentException {
        Query q = em.createQuery("select op.opcaoPagamento from OpcaoPagamentoDivida op "
            + " where op.divida = :divida "
            + " and :inicioVigencia between coalesce(op.inicioVigencia, current_date)  "
            + " and trunc(coalesce(op.finalVigencia, current_date))");
        q.setParameter("divida", divida);
        q.setParameter("inicioVigencia", DataUtil.dataSemHorario(dataBase));
        if (q.getResultList().isEmpty()) {
            throw new IllegalArgumentException("Nenhuma opção de pagamento válida para a dívida selecionada: " + divida.getDescricao());
        }
        return (OpcaoPagamento) q.getResultList().get(0);
    }


    public ValorDivida criarValorDivida(Calculo calculo, Date vencimento, Tributo tributo) {
        ValorDivida valorDivida = new ValorDivida();
        valorDivida.setOcorrenciaValorDivida(new ArrayList<OcorrenciaValorDivida>());
        valorDivida.setCalculo(calculo);
        valorDivida.setItemValorDividas(new ArrayList<ItemValorDivida>());
        valorDivida.setParcelaValorDividas(new ArrayList<ParcelaValorDivida>());
        valorDivida.setDivida(calculo.getProcessoCalculo().getDivida());
        valorDivida.setEmissao(new Date());
        valorDivida.setExercicio(calculo.getProcessoCalculo().getExercicio());
        valorDivida.setValor(calculo.getValorEfetivo());
        ParcelaValorDivida parcelaValorDivida = criarParcela(calculo, valorDivida, vencimento);
        criarItensValorDividaParcela(calculo, valorDivida, parcelaValorDivida, tributo);
        return valorDivida;
    }

    @Asynchronous
    public void enviarEmailDamIssForaMunicipio(AssistenteBarraProgresso assistente,
                                               IssqnFmLancamentoNfseDTO lancamento,
                                               ByteArrayOutputStream dam) {
        if (lancamento.getPessoa().getDadosPessoais().getEmail() == null ||
            lancamento.getPessoa().getDadosPessoais().getEmail().trim().isEmpty()) {
            return;
        }
        assistente.setTotal(1);
        TemplateNfse templateEmail = templateNfseFacade.buscarPorTipo(TipoTemplateNfse.EMAIL_ISS_FORA_MUNICIPIO);
        if (templateEmail != null) {
            String conteudoEmail = new TrocaTagNfseEmailIssForaMunicipio(lancamento, configuracaoNfseFacade.recuperarConfiguracao())
                .trocarTags(templateEmail.getConteudo());

            EmailService.getInstance().enviarEmail(lancamento.getPessoa().getDadosPessoais().getEmail(),
                "Iss Fora do Município", conteudoEmail, dam);
        }
        assistente.conta();
    }

    public static enum TipoProcessoCalculoIss {
        NORMAL, NFSE, SIMPLES_NACIONAL, FORA_MUNICIPIO;
    }

    public ProcessoCalculoISS calcularIssForaMunicipio(IssqnFmLancamentoNfseDTO lancamento) {
        Pessoa pessoa = pessoaFacade.criarOrAtualizarPessoa(lancamento.getPessoa());
        Exercicio exercicio = exercicioFacade.recuperarExercicioPeloAno(lancamento.getExercicio());
        ProcessoCalculoISS processo = criarProcessoHomologado("ISS Fora Município Contribuinte " + lancamento.getPessoa().getDadosPessoais().getCpfCnpj() + " - " +
            lancamento.getPessoa().getDadosPessoais().getNomeRazaoSocial(), exercicio, lancamento.getMes().getMes(), TipoProcessoCalculoIss.FORA_MUNICIPIO);
        CalculoISS retorno = criarCalculoForaMunicipio(processo, pessoa, lancamento.getFaturamento(),
            lancamento.getBaseCalculo(), lancamento.getAliquota(), lancamento.getValor(), lancamento.getObservacao(), lancamento.getTipoLancamento());
        processo.getCalculos().add(retorno);
        processo.setObservacoes(lancamento.getObservacao());
        return em.merge(processo);
    }

    public void lancarDebitoIssForaMunicipio(ProcessoCalculoISS processoCalculoISS) throws Exception {
        geraValorDividaISSForaMunicipio.geraDebito(processoCalculoISS);
    }

    public boolean hasCalculoIssFixoAberto(CadastroEconomico cadastroEconomico) {
        return !em.createNativeQuery(" select pvd.id as origem " +
                "   from calculoiss calc " +
                "  inner join valordivida vd on vd.calculo_id = calc.id " +
                "  inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
                "  inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
                " where calc.tipocalculoiss = :tipo_calculo_iss " +
                "   and calc.cadastroeconomico_id = :cadastro_id " +
                "   and spvd.situacaoparcela = :situacao_em_aberto " +
                " union all " +
                " select pvd.id as origem " +
                "    from calculoiss calc " +
                "   inner join valordivida vd on vd.calculo_id = calc.id " +
                "   inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
                "   inner join itemprocessoparcelamento ipp on ipp.parcelavalordivida_id = pvd.id " +
                "   inner join processoparcelamento pp on pp.id = ipp.processoparcelamento_id " +
                "   inner join valordivida vd_p on vd_p.calculo_id = pp.id " +
                "   inner join parcelavalordivida pvd_p on pvd_p.valordivida_id = vd_p.id " +
                "   inner join situacaoparcelavalordivida spvd_p on spvd_p.id = pvd_p.situacaoatual_id " +
                " where calc.tipocalculoiss = :tipo_calculo_iss " +
                "   and calc.cadastroeconomico_id = :cadastro_id " +
                "   and spvd_p.situacaoparcela = :situacao_em_aberto " +
                "   and not exists (select 1 " +
                "                      from processoparcelamento s_pp " +
                "                     inner join valordivida s_vd on s_vd.calculo_id = s_pp.id " +
                "                     inner join parcelavalordivida s_pvd on s_pvd.valordivida_id = s_vd.id " +
                "                     inner join situacaoparcelavalordivida s_spvd on s_spvd.id = s_pvd.situacaoatual_id " +
                "                   where s_pp.id = pp.id " +
                "                     and s_spvd.situacaoparcela = :situacao_pago) " +
                " union all " +
                " select pvd.id as origem " +
                "    from calculoiss calc " +
                "   inner join valordivida vd on vd.calculo_id = calc.id " +
                "   inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
                "   inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
                "   inner join inscricaodividaparcela idp on idp.parcelavalordivida_id = pvd.id " +
                "   inner join valordivida vd_da on vd_da.calculo_id = idp.iteminscricaodividaativa_id " +
                "   inner join parcelavalordivida pvd_da on pvd_da.valordivida_id = vd_da.id " +
                "   inner join situacaoparcelavalordivida spvd_da on spvd_da.id = pvd_da.situacaoatual_id " +
                " where calc.tipocalculoiss = :tipo_calculo_iss " +
                "   and calc.cadastroeconomico_id = :cadastro_id " +
                "   and spvd_da.situacaoparcela = :situacao_em_aberto " +
                " union all " +
                " select pvd.id as origem " +
                "    from calculoiss calc " +
                "   inner join valordivida vd on vd.calculo_id = calc.id " +
                "   inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
                "   inner join inscricaodividaparcela idp on idp.parcelavalordivida_id = pvd.id " +
                "   inner join valordivida vd_da on vd_da.calculo_id = idp.iteminscricaodividaativa_id " +
                "   inner join parcelavalordivida pvd_da on pvd_da.valordivida_id = vd_da.id " +
                "   inner join itemprocessoparcelamento ipp on ipp.parcelavalordivida_id = pvd_da.id " +
                "   inner join processoparcelamento pp on pp.id = ipp.processoparcelamento_id " +
                "   inner join valordivida vd_p on vd_p.calculo_id = pp.id " +
                "   inner join parcelavalordivida pvd_p on pvd_p.valordivida_id = vd_p.id " +
                "   inner join situacaoparcelavalordivida spvd_p on spvd_p.id = pvd_p.situacaoatual_id " +
                " where calc.tipocalculoiss = :tipo_calculo_iss " +
                "   and calc.cadastroeconomico_id = :cadastro_id " +
                "   and spvd_p.situacaoparcela = :situacao_em_aberto " +
                "   and not exists (select 1 " +
                "                      from processoparcelamento s_pp " +
                "                     inner join valordivida s_vd on s_vd.calculo_id = s_pp.id " +
                "                     inner join parcelavalordivida s_pvd on s_pvd.valordivida_id = s_vd.id " +
                "                     inner join situacaoparcelavalordivida s_spvd on s_spvd.id = s_pvd.situacaoatual_id " +
                "                  where s_pp.id = pp.id " +
                "                    and s_spvd.situacaoparcela = :situacao_pago) ")
            .setParameter("tipo_calculo_iss", TipoCalculoISS.FIXO.name())
            .setParameter("cadastro_id", cadastroEconomico.getId())
            .setParameter("situacao_em_aberto", SituacaoParcela.EM_ABERTO.name())
            .setParameter("situacao_pago", SituacaoParcela.PAGO.name())
            .getResultList().isEmpty();
    }

    public boolean hasCalculoIssFixoPago(Exercicio exercicio,
                                         CadastroEconomico cadastroEconomico) {
        return !em.createNativeQuery(" select pvd.id as origem " +
                "   from calculoiss calc " +
                "  inner join valordivida vd on vd.calculo_id = calc.id " +
                "  inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
                "  inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
                " where calc.tipocalculoiss = :tipoCalculIss " +
                "   and vd.exercicio_id = :exercicioId " +
                "   and calc.cadastroeconomico_id = :cadastroId " +
                "   and spvd.situacaoparcela in (:situacoesPago) " +
                " union all " +
                " select pvd.id as origem " +
                "    from calculoiss calc " +
                "   inner join valordivida vd on vd.calculo_id = calc.id " +
                "   inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
                "   inner join itemprocessoparcelamento ipp on ipp.parcelavalordivida_id = pvd.id " +
                "   inner join processoparcelamento pp on pp.id = ipp.processoparcelamento_id " +
                "   inner join valordivida vd_p on vd_p.calculo_id = pp.id " +
                "   inner join parcelavalordivida pvd_p on pvd_p.valordivida_id = vd_p.id " +
                "   inner join situacaoparcelavalordivida spvd_p on spvd_p.id = pvd_p.situacaoatual_id " +
                " where calc.tipocalculoiss = :tipoCalculoIss " +
                "   and calc.cadastroeconomico_id = :cadastroId " +
                "   and spvd_p.situacaoparcela in (:situacoesPago) ")
            .setParameter("tipoCalculoIss", TipoCalculoISS.FIXO.name())
            .setParameter("cadastroId", cadastroEconomico.getId())
            .setParameter("situacoesPago", SituacaoParcela.getsituacoesPago())
            .getResultList().isEmpty();
    }
}
