/*
 * Codigo gerado automaticamente em Fri Aug 31 14:22:19 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.IntegracaoTributarioContabilFiltros;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CreditoReceberFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "creditoReceberControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-Credito-Receber", pattern = "/credito-receber/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/creditoreceber/edita.xhtml"),
    @URLMapping(id = "editar-Credito-Receber", pattern = "/credito-receber/editar/#{creditoReceberControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/creditoreceber/edita.xhtml"),
    @URLMapping(id = "ver-Credito-Receber", pattern = "/credito-receber/ver/#{creditoReceberControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/creditoreceber/visualizar.xhtml"),
    @URLMapping(id = "listar-Credito-Receber", pattern = "/credito-receber/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/creditoreceber/lista.xhtml"),
    @URLMapping(id = "integrar-Credito-Receber", pattern = "/credito-receber/integracao/tributario-contabil", viewId = "/faces/financeiro/lancamentocontabilmanual/creditoreceber/integracao.xhtml")
})
public class CreditoReceberControlador extends PrettyControlador<CreditoReceber> implements Serializable, CRUD {

    @EJB
    private CreditoReceberFacade creditoReceberFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterClasseCredor;
    private ConverterAutoComplete converterReceitaLOA;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ConverterAutoComplete converterContaReceita;
    private MoneyConverter moneyConverter;
    private TiposCredito tiposCredito;

    //    Atributos Integração
    private ContaReceita contaReceita;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Date dataInicial;
    private Date dataFinal;
    private List<CreditoReceber> lancamentoIntegracao;
    private List<CreditoReceber> bloqueadasPorFase;
    private Date novaDataContabilizacao;

    public CreditoReceberControlador() {
        super(CreditoReceber.class);
    }

    public CreditoReceberFacade getFacade() {
        return creditoReceberFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return creditoReceberFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/credito-receber/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-Credito-Receber", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        tiposCredito = null;
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        selecionado.setDataCredito(sistemaControlador.getDataOperacao());
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setIntegracao(Boolean.FALSE);
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());

        if (creditoReceberFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", creditoReceberFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "ver-Credito-Receber", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-Credito-Receber", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "integrar-Credito-Receber", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void integrar() {
        selecionado = new CreditoReceber();
        contaReceita = new ContaReceita();
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        operacao = Operacoes.NOVO;
        limparCampos();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                creditoReceberFacade.salvarNovo(selecionado);
            } else {
                creditoReceberFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor() != null
            && selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero(0).");
        }
        if (!selecionado.getIntegracao()
            && selecionado.getDataReferencia() != null
            && selecionado.getDataReferencia().compareTo(selecionado.getDataCredito()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Referência deve ser menor ou igual a Data de Lançamento.");
        }
        ve.lancarException();
    }

    public void recuperarEditarVer() {
        operacao = Operacoes.EDITAR;
        if (selecionado.getReceitaLOA() != null) {
            tiposCredito = ((ContaReceita) selecionado.getReceitaLOA().getContaDeReceita()).getTiposCredito();
        } else {
            tiposCredito = null;
        }
        definirEventoContabil();
    }

    public String definirEventoContabil() {
        ConfigCreditoReceber configCreditoReceber;
        selecionado.setEventoContabil(null);
        try {
            if (selecionado.getTipoLancamento() != null
                && selecionado.getOperacaoCreditoReceber() != null
                && selecionado.getDataCredito() != null
                && selecionado.getReceitaLOA() != null) {
                configCreditoReceber = creditoReceberFacade.getConfigCreditoReceberFacade().recuperarEventoContabil(selecionado);
                selecionado.setEventoContabil(configCreditoReceber.getEventoContabil());
                return selecionado.getEventoContabil().toString();
            } else {
                return "Nenhum evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            return "Nenhum Evento encontrado";
        }
    }

    public void definirNullParaValoresReceita() {
        selecionado.setReceitaLOA(null);
        tiposCredito = null;
        if (selecionado.getOperacaoCreditoReceber() != null) {
            switch (selecionado.getOperacaoCreditoReceber()) {
                case RECONHECIMENTO_CREDITO_A_RECEBER:
                case DEDUCAO_RECONHECIMENTO_CREDITO_A_RECEBER:
                case ATUALIZACAO_DE_CREDITOS_A_RECEBER:
                case RECEBIMENTO:
                case BAIXA_RECONHECIMENTO_CREDITO_A_RECEBER:
                case BAIXA_DEDUCAO_RECONHECIMENTO_CREDITO_A_RECEBER:
                case TRANSFERENCIA_CREDITO_A_RECEBER_CURTO_PARA_LONGO_PRAZO:
                case TRANSFERENCIA_CREDITO_A_RECEBER_LONGO_PARA_CURTO_PRAZO:
                case AJUSTE_CREDITOS_A_RECEBER_AUMENTATIVO:
                case AJUSTE_CREDITOS_A_RECEBER_AUMENTATIVO_EMPRESA_PUBLICA:
                case AJUSTE_CREDITOS_A_RECEBER_DIMINUTIVO:
                case AJUSTE_CREDITOS_A_RECEBER_DIMINUTIVO_EMPRESA_PUBLICA:
                    selecionado.setNaturezaCreditoReceber(NaturezaDividaAtivaCreditoReceber.ORIGINAL);
                    break;
                case AJUSTE_PERDAS_CREDITOS_A_RECEBER:
                case REVERSAO_AJUSTE_PERDAS_CREDITO_A_RECEBER:
                case AJUSTE_PERDAS_CREDITOS_A_RECEBER_LONGO_PRAZO:
                case REVERSAO_AJUSTE_PERDAS_CREDITO_A_RECEBER_LONGO_PRAZO:
                case TRANSFERENCIA_AJUSTE_PERDAS_CREDITO_A_RECEBER_CURTO_PARA_LONGO_PRAZO:
                case TRANSFERENCIA_AJUSTE_PERDAS_CREDITO_A_RECEBER_LONGO_PARA_CURTO_PRAZO:
                    selecionado.setNaturezaCreditoReceber(NaturezaDividaAtivaCreditoReceber.AJUSTE_DE_PERDAS);
                    break;
            }
            switch (selecionado.getOperacaoCreditoReceber()) {
                case REVERSAO_AJUSTE_PERDAS_CREDITO_A_RECEBER_LONGO_PRAZO:
                case AJUSTE_PERDAS_CREDITOS_A_RECEBER_LONGO_PRAZO:
                case TRANSFERENCIA_CREDITO_A_RECEBER_LONGO_PARA_CURTO_PRAZO:
                case TRANSFERENCIA_AJUSTE_PERDAS_CREDITO_A_RECEBER_LONGO_PARA_CURTO_PRAZO:
                    selecionado.setIntervalo(Intervalo.LONGO_PRAZO);
                    break;
                default:
                    selecionado.setIntervalo(Intervalo.CURTO_PRAZO);
                    break;
            }
        }
    }

    public List<Pessoa> completaPessoa(String parte) {
        return creditoReceberFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<Conta> completarContasDeDestinacaoDeRecursos(String filtro) {
        return creditoReceberFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(filtro.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<SelectItem> getListaOperacaoCredito() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (OperacaoCreditoReceber object : OperacaoCreditoReceber.values()) {
            if (!object.equals(OperacaoCreditoReceber.RECEBIMENTO)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getPessoa() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Pessoa object : creditoReceberFacade.getPessoaFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getReceitaLOA() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (ReceitaLOA object : creditoReceberFacade.getReceitaLOAFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<TipoLancamento> getTiposLancamento() {
        List<TipoLancamento> lista = new ArrayList<>();
        for (TipoLancamento tipo : TipoLancamento.values()) {
            lista.add(tipo);
        }
        return lista;
    }

    public ConverterAutoComplete getConverterReceitaLOA() {
        if (converterReceitaLOA == null) {
            converterReceitaLOA = new ConverterAutoComplete(ReceitaLOA.class, creditoReceberFacade.getReceitaLOAFacade());
        }
        return converterReceitaLOA;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, creditoReceberFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }


    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, creditoReceberFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public ConverterAutoComplete getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, creditoReceberFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public ConverterAutoComplete getConverterContaReceita() {
        if (converterContaReceita == null) {
            converterContaReceita = new ConverterAutoComplete(Conta.class, creditoReceberFacade.getContaFacade());
        }
        return converterContaReceita;
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return creditoReceberFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte, ((CreditoReceber) selecionado).getPessoa());
    }

    public List<ReceitaLOA> completaReceitaLOA(String parte) {
        return creditoReceberFacade.getReceitaLOAFacade().buscarReceitasLoaPorCodigoReduzidoCodigoDescricaoCredito(parte, sistemaControlador.getExercicioCorrente(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), OperacaoReceita.RECEITA_CREDITOS_RECEBER_BRUTA);
    }

    public List<Conta> completaContaReceita(String parte) {
        return creditoReceberFacade.getContaFacade().listaFiltrandoReceita(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<HierarquiaOrganizacional> completaUnidadeOrganizacional(String parte) {
        return creditoReceberFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorTipo(parte.trim(), sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), "ORCAMENTARIA");
    }

    public void atribuirTiposCredito(SelectEvent evento) {
        ReceitaLOA rl = (ReceitaLOA) evento.getObject();
        if (rl != null) {
            if (rl.getContaDeReceita() != null) {
                tiposCredito = ((ContaReceita) rl.getContaDeReceita()).getTiposCredito();
            }
        }
        definirEventoContabil();
    }

    public void importarReceitasTributarias() {
        List<CreditoReceber> lancamentoIntegracaoDeuCerto = new ArrayList<>();

        if (validarCreditoReceberBloqueadosAoSalvar()) {
            if (!lancamentoIntegracao.isEmpty()) {
                for (CreditoReceber c : lancamentoIntegracao) {
                    try {
                        selecionado = c;
                        c.setIntegracao(Boolean.TRUE);
                        definirEventoContabil();
                        validarCampos();
                        creditoReceberFacade.salvarNovo(c);
                        lancamentoIntegracaoDeuCerto.add(c);
                    } catch (ValidacaoException ve) {
                        FacesUtil.printAllFacesMessages(ve.getMensagens());
                    } catch (Exception ex) {
                        FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
                    }
                }
                if (lancamentoIntegracaoDeuCerto.size() > 0) {
                    FacesUtil.addOperacaoRealizada(" Foram importados " + lancamentoIntegracaoDeuCerto.size() + " Créditos a Receber de Arrecadações Tributárias.");
                }
                redireciona();
            } else {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Não foi encontrado créditos a receber tributários para importação.");
            }
        }
    }

    public void buscarReceitaTributarias() {
        try {
            IntegracaoTributarioContabilFiltros filtros = new IntegracaoTributarioContabilFiltros(selecionado.getUnidadeOrganizacional(), dataInicial, dataFinal, contaReceita, null);

            if (validaFiltrosIntegrecao()) {
                lancamentoIntegracao = creditoReceberFacade.getIntegracaoTributarioContabilFacade().getCreditoReceberContabilizar(filtros);

                if (!lancamentoIntegracao.isEmpty()) {
                    ConfiguracaoContabil configuracaoContabil = creditoReceberFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
                    Preconditions.checkNotNull(configuracaoContabil.getClasseTribContCreditoRec(), "A Classe Credor configurada para Integração Contábil/Tributário para Créditos a Receber não foi informada.");

                    bloqueadasPorFase = Lists.newArrayList();
                    for (CreditoReceber c : lancamentoIntegracao) {
                        if (creditoReceberFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/lancamentocontabilmanual/creditoreceber/integracao.xhtml", c.getDataCredito(), selecionado.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
                            bloqueadasPorFase.add(c);
                        } else {
                            validarIntegracaoCreditosReceber(configuracaoContabil, c);
                        }
                    }
                    if (!bloqueadasPorFase.isEmpty()) {
                        FacesUtil.atualizarComponente("formMudaData");
                        FacesUtil.executaJavaScript("mudaData.show()");
                    }
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Não foi encontrado créditos a receber tributários para importação.");
                }
            }
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    private boolean validarIntegracaoCreditosReceber(ConfiguracaoContabil configuracaoContabil, CreditoReceber c) {
        try {
            c.setPessoa(creditoReceberFacade.getPessoaFacade().recuperaPessoaDaEntidade(c.getUnidadeOrganizacional(), c.getDataCredito()));
            c.setClasseCredor(configuracaoContabil.getClasseTribContCreditoRec());
            tiposCredito = ((ContaReceita) c.getReceitaLOA().getContaDeReceita()).getTiposCredito();

            ConfigCreditoReceber configuracao = creditoReceberFacade.getConfigCreditoReceberFacade().recuperarEventoContabil(c);
            if (configuracao == null) {
                lancamentoIntegracao.clear();
                return false;
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            return false;
        }
        return true;
    }


    public void mudaDatasIntegracoes() {
        ConfiguracaoContabil configuracaoContabil = creditoReceberFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
        List bloqueadasPorFase = Lists.newArrayList();
        if (validarNovaDataContabilizacao()) {
            for (CreditoReceber creditoReceber : this.bloqueadasPorFase) {
                creditoReceber.setDataCredito(novaDataContabilizacao);
                creditoReceber.setPessoa(creditoReceberFacade.getPessoaFacade().recuperaPessoaDaEntidade(creditoReceber.getUnidadeOrganizacional(), creditoReceber.getDataCredito()));
                creditoReceber.setClasseCredor(configuracaoContabil.getClasseTribContReceitaRea());
                if (creditoReceberFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/lancamentocontabilmanual/creditoreceber/integracao.xhtml", creditoReceber.getDataCredito(), creditoReceber.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
                    bloqueadasPorFase.add(creditoReceber);
//                } else {
//                    validarIntegracaoCreditosReceber(configuracaoContabil, creditoReceber);
                }
            }
            if (!bloqueadasPorFase.isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("A data informada ainda se encontra em um bloqueio de fase para o período em que os lotes de arrecadação filtrados\n" +
                    " serão contabilizados, informe uma nova data para a contabilização e uma justificativa.");
            }
            if (bloqueadasPorFase.isEmpty()) {
                FacesUtil.executaJavaScript("mudaData.hide()");
            }
        }
    }

    public Boolean validarNovaDataContabilizacao() {
        if (novaDataContabilizacao == null) {
            FacesUtil.addCampoObrigatorio("O campo Data deve ser informado.");
            return false;
        }
        return true;
    }


    public Boolean marcarCreditoReceberComoBloqueado(CreditoReceber creditoReceber) {
        if (creditoReceberFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/lancamentocontabilmanual/creditoreceber/integracao.xhtml", creditoReceber.getDataCredito(), creditoReceber.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
            return true;
        }
        return false;
    }

    public Boolean validarCreditoReceberBloqueadosAoSalvar() {
        for (CreditoReceber creditoReceber : lancamentoIntegracao) {
            if (creditoReceberFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/lancamentocontabilmanual/creditoreceber/integracao.xhtml", creditoReceber.getDataCredito(), selecionado.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
                FacesUtil.executaJavaScript("mudaData.show()");
                return false;
            }
        }
        return true;
    }

    public void limparCampos() {
        hierarquiaOrganizacional = null;
        contaReceita = null;
        dataInicial = sistemaControlador.getDataOperacao();
        Calendar c = Calendar.getInstance();
        c.setTime(dataInicial);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
        dataInicial = c.getTime();
        dataFinal = sistemaControlador.getDataOperacao();
    }

    private boolean validaFiltrosIntegrecao() {
        Boolean retorno = true;
        if (selecionado.getUnidadeOrganizacional() == null || selecionado.getUnidadeOrganizacional().getId() == null
            || dataInicial == null
            || dataFinal == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Os campos obrigatórios devem ser informados para filtrar as arrecadações tributárias.");
            retorno = false;
        }
        if (dataFinal.before(dataInicial)) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A data final deve ser maior que a data inicial.");
            retorno = false;
        }
        return retorno;
    }


    public void adicionaUnidadeHierarquiaSelecionada() {
        if (hierarquiaOrganizacional.getSubordinada() != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public BigDecimal getValorTotal() {
        BigDecimal valor = BigDecimal.ZERO;
        if (lancamentoIntegracao != null) {
            for (CreditoReceber creditoReceber : lancamentoIntegracao) {
                if (creditoReceber != null) {
                    valor = valor.add(creditoReceber.getValor());
                }
            }
        }
        return valor;
    }

    public Date getNovaDataContabilizacao() {
        return novaDataContabilizacao;
    }

    public void setNovaDataContabilizacao(Date novaDataContabilizacao) {
        this.novaDataContabilizacao = novaDataContabilizacao;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }


    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public TiposCredito getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TiposCredito tiposCredito) {
        this.tiposCredito = tiposCredito;
    }


    public List<CreditoReceber> getLancamentoIntegracao() {
        return lancamentoIntegracao;
    }

    public void setLancamentoIntegracao(List<CreditoReceber> lancamentoIntegracao) {
        this.lancamentoIntegracao = lancamentoIntegracao;
    }
}
