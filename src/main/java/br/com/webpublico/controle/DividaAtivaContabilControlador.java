/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.IntegracaoTributarioContabilFiltros;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DividaAtivaContabilFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.*;
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

/**
 * @author juggernaut
 */
@ManagedBean(name = "dividaAtivaContabilControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-Divida-Ativa", pattern = "/divida-ativa/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/dividaativa/edita.xhtml"),
    @URLMapping(id = "editar-Divida-Ativa", pattern = "/divida-ativa/editar/#{dividaAtivaContabilControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/dividaativa/edita.xhtml"),
    @URLMapping(id = "ver-Divida-Ativa", pattern = "/divida-ativa/ver/#{dividaAtivaContabilControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/dividaativa/visualizar.xhtml"),
    @URLMapping(id = "listar-Divida-Ativa", pattern = "/divida-ativa/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/dividaativa/lista.xhtml"),
    @URLMapping(id = "integrar-Divida-Ativa", pattern = "/divida-ativa/integracao/tributario-contabil/", viewId = "/faces/financeiro/lancamentocontabilmanual/dividaativa/integracao.xhtml")
})
public class DividaAtivaContabilControlador extends PrettyControlador<DividaAtivaContabil> implements Serializable, CRUD {

    @EJB
    private DividaAtivaContabilFacade dividaAtivaContabilFacade;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ConverterAutoComplete converterContaReceita;
    private ConverterAutoComplete converterClasseCredor;
    private ConverterAutoComplete converterReceitaLOA;
    private MoneyConverter moneyConverter;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private TiposCredito tiposCredito;
    //    Atributos para Integração
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ContaReceita contaReceita;
    private Date dataInicial;
    private Date dataFinal;
    private List<DividaAtivaContabil> lancamentoIntegracao;
    private List<DividaAtivaContabil> bloqueadasPorFase;
    private Date novaDataContabilizacao;

    public DividaAtivaContabilControlador() {
        metadata = new EntidadeMetaData(DividaAtivaContabil.class);
        dataInicial = new Date();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/divida-ativa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-Divida-Ativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        selecionado.setDataDivida(sistemaControlador.getDataOperacao());
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setIntegracao(Boolean.FALSE);
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        tiposCredito = null;

        if (dividaAtivaContabilFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", dividaAtivaContabilFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    public List<SelectItem> getIntervalos() {
        return Util.getListSelectItem(Intervalo.values(), false);
    }

    @URLAction(mappingId = "ver-Divida-Ativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "editar-Divida-Ativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "integrar-Divida-Ativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void integrar() {
        selecionado = new DividaAtivaContabil();
        contaReceita = new ContaReceita();
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        operacao = Operacoes.NOVO;
        limparCampos();
    }

    @Override
    public AbstractFacade getFacede() {
        return dividaAtivaContabilFacade;
    }

    public void recuperaEditaVer() {
        try {
            operacao = Operacoes.EDITAR;
            if (selecionado.getReceitaLOA() != null) {
                verificarValoresDaDivida(selecionado.getReceitaLOA());
                if (selecionado.getOperacaoDividaAtiva().equals(OperacaoDividaAtiva.INSCRICAO)) {
                    tiposCredito = ((ContaReceita) selecionado.getReceitaLOA().getContaDeReceita()).getCorrespondente().getTiposCredito();
                } else {
                    tiposCredito = ((ContaReceita) selecionado.getReceitaLOA().getContaDeReceita()).getTiposCredito();
                }
            }
        } catch (Exception ex) {
            FacesUtil.addAtencao(ex.getMessage());
        }
    }

    public String definirEventoContabil() {
        selecionado.setEventoContabil(null);
        try {
            if (selecionado.getTipoLancamento() != null
                && selecionado.getReceitaLOA() != null
                && selecionado.getDataDivida() != null
                && selecionado.getOperacaoDividaAtiva() != null) {
                ConfigDividaAtivaContabil configuracao = dividaAtivaContabilFacade.getConfigDividaAtivaContabilFacade().recuperaEvento(selecionado);
                selecionado.setEventoContabil(configuracao.getEventoContabil());
                return selecionado.getEventoContabil().toString();
            } else {
                return "Nenhum evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addError(" Evento contábil não recuperado! ", e.getMessage());
            return "Nenhum Evento encontrado!";
        }
    }


    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            if (operacao.equals(Operacoes.NOVO)) {
                dividaAtivaContabilFacade.salvarNovo(selecionado);
            } else {
                dividaAtivaContabilFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso.");
            redireciona();
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, dividaAtivaContabilFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, dividaAtivaContabilFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public ConverterAutoComplete getConverterContaReceita() {
        if (converterContaReceita == null) {
            converterContaReceita = new ConverterAutoComplete(Conta.class, dividaAtivaContabilFacade.getContaFacade());
        }
        return converterContaReceita;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, dividaAtivaContabilFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public ConverterAutoComplete getConverterReceitaLOA() {
        if (converterReceitaLOA == null) {
            converterReceitaLOA = new ConverterAutoComplete(ReceitaLOA.class, dividaAtivaContabilFacade.getReceitaLOAFacade());
        }
        return converterReceitaLOA;
    }

    public List<SelectItem> getPessoa() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Pessoa object : dividaAtivaContabilFacade.getPessoaFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return dividaAtivaContabilFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte, ((DividaAtivaContabil) selecionado).getPessoa());
    }

    public List<HierarquiaOrganizacional> completaUnidadeOrganizacional(String parte) {
        return dividaAtivaContabilFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorTipo(parte.trim(), sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), "ORCAMENTARIA");
    }

    public List<Conta> completaContaReceita(String parte) {
        return dividaAtivaContabilFacade.getContaFacade().listaFiltrandoReceita(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<Conta> completarContasDeDestinacaoDeRecursos(String filtro) {
        return dividaAtivaContabilFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(filtro.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<ReceitaLOA> completaReceitaLOA(String parte) {
        if (OperacaoDividaAtiva.INSCRICAO.equals(selecionado.getOperacaoDividaAtiva())) {
            return dividaAtivaContabilFacade.getReceitaLOAFacade().buscarReceitasLoaPorCodigoReduzidoCodigoDescricaoCredito(parte, sistemaControlador.getExercicioCorrente(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), OperacaoReceita.RECEITA_CREDITOS_RECEBER_BRUTA);
        }
        return dividaAtivaContabilFacade.getReceitaLOAFacade().listaPorCodigoReduzidoCodigoDescricaoDivida(parte, sistemaControlador.getExercicioCorrente(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
    }

    public List<SelectItem> getOperacoesDividaAtiva() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (OperacaoDividaAtiva object : OperacaoDividaAtiva.values()) {
            if (!OperacaoDividaAtiva.RECEBIMENTO.equals(object) && !OperacaoDividaAtiva.A_INSCREVER.equals(object)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> toReturn = new ArrayList<>();
        for (TipoLancamento tipo : TipoLancamento.values()) {
            toReturn.add(tipo);
        }
        return toReturn;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return dividaAtivaContabilFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public void definirTiposCredito(SelectEvent evento) {
        ReceitaLOA rl = (ReceitaLOA) evento.getObject();
        try {
            verificarValoresDaDivida(rl);
            definirEventoContabil();
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    private void verificarValoresDaDivida(ReceitaLOA receitaLOA) {
        String toReturn = "";
        if (receitaLOA != null) {
            selecionado.setReceitaLOA(receitaLOA);
            if (receitaLOA.getContaDeReceita() != null) {
                if (selecionado.getOperacaoDividaAtiva().equals(OperacaoDividaAtiva.INSCRICAO)) {
                    ContaReceita receita = ((ContaReceita) receitaLOA.getContaDeReceita());
                    if (receita.getCorrespondente() != null) {
                        if (receita.getCorrespondente().getTiposCredito() != null) {
                            tiposCredito = ((ContaReceita) receitaLOA.getContaDeReceita()).getCorrespondente().getTiposCredito();
                        } else {
                            toReturn += " Tipo de crédito da conta correspondente associado a conta de receita está vazio!";
                            throw new RuntimeException(toReturn);
                        }
                    } else {
                        toReturn += " Não foi encontrado tipo de crédito para a conta correspondente.";
                        throw new RuntimeException(toReturn);
                    }
                } else {
                    tiposCredito = ((ContaReceita) receitaLOA.getContaDeReceita()).getTiposCredito();
                }
            } else {
                toReturn += "Receita Loa não possui conta! ";
                throw new RuntimeException(toReturn);
            }
        }
    }

    public void definirNullParaValoresReceita() {
        selecionado.setReceitaLOA(null);
        tiposCredito = null;
        if (selecionado.getOperacaoDividaAtiva() != null) {
            switch (selecionado.getOperacaoDividaAtiva()) {
                case A_INSCREVER:
                case INSCRICAO:
                case ATUALIZACAO:
                case RECEBIMENTO:
                case BAIXA:
                case TRANSFERENCIA_CURTO_PARA_LONGO_PRAZO:
                case TRANSFERENCIA_LONGO_PARA_CURTO_PRAZO:
                case AUMENTATIVO:
                case DIMINUTIVO:
                    selecionado.setNaturezaDividaAtiva(NaturezaDividaAtivaCreditoReceber.ORIGINAL);
                    break;
                case AJUSTE_PERDAS_LONGO_PRAZO:
                case REVERSAO:
                case REVERSAO_AJUSTE_LONGO_PRAZO:
                case PROVISAO:
                case TRANSFERENCIA_AJUSTE_PERDAS_CURTO_PARA_LONGO_PRAZO:
                case TRANSFERENCIA_AJUSTE_PERDAS_LONGO_PARA_CURTO_PRAZO:
                    selecionado.setNaturezaDividaAtiva(NaturezaDividaAtivaCreditoReceber.AJUSTE_DE_PERDAS);
                    break;
            }

            switch (selecionado.getOperacaoDividaAtiva()) {
                case TRANSFERENCIA_LONGO_PARA_CURTO_PRAZO:
                case TRANSFERENCIA_AJUSTE_PERDAS_LONGO_PARA_CURTO_PRAZO:
                    selecionado.setIntervalo(Intervalo.LONGO_PRAZO);
                    break;
                default:
                    selecionado.setIntervalo(Intervalo.CURTO_PRAZO);
                    break;
            }
        }
    }

    public List<DividaAtivaContabil> getLancamentoIntegracao() {
        return lancamentoIntegracao;
    }

    public void setLancamentoIntegracao(List<DividaAtivaContabil> lancamentoIntegracao) {
        this.lancamentoIntegracao = lancamentoIntegracao;
    }


    public void importarDividaAtivaTributarias() {
        List<DividaAtivaContabil> lancamentoIntegracaoDeuCerto = new ArrayList<>();

        if (!lancamentoIntegracao.isEmpty()) {
            if (validarDividaAtivaBloqueadasAoSalvar()) {
                for (DividaAtivaContabil d : lancamentoIntegracao) {
                    try {
                        selecionado = d;
                        d.setIntegracao(Boolean.TRUE);
                        definirEventoContabil();

                        if (Util.validaCampos(selecionado)) {
                            dividaAtivaContabilFacade.salvarNovo(d);
                            lancamentoIntegracaoDeuCerto.add(d);
                        }
                    } catch (Exception ex) {
                        FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
                    }
                }
                if (lancamentoIntegracaoDeuCerto.size() > 0) {
                    FacesUtil.addOperacaoRealizada(" Foram importadas " + lancamentoIntegracaoDeuCerto.size() + " Dívidas Ativas de Arrecadações Tributárias.");
                }
                redireciona();
            }
        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Não foi encontrado dívidas ativas tributárias para importação.");
        }
    }

    public void buscarDividaAtivaTributaria() {
        try {
            IntegracaoTributarioContabilFiltros filtros = new IntegracaoTributarioContabilFiltros(selecionado.getUnidadeOrganizacional(), dataInicial, dataFinal, contaReceita, null);

            if (validaFiltrosIntegrecao()) {
                lancamentoIntegracao = dividaAtivaContabilFacade.getIntegracaoTributarioContabilFacade().getDividaAtivaContabilizar(filtros);

                if (!lancamentoIntegracao.isEmpty()) {
                    ConfiguracaoContabil configuracaoContabil = dividaAtivaContabilFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
                    Preconditions.checkNotNull(configuracaoContabil.getClasseTribContDividaAtiv(), "A Classe Credor configurada para Integração Contábil/Tributário para Dívida Ativa não foi informada.");

                    bloqueadasPorFase = Lists.newArrayList();
                    for (DividaAtivaContabil divida : lancamentoIntegracao) {
                        if (dividaAtivaContabilFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/lancamentocontabilmanual/dividaativa/integracao.xhtml", divida.getDataDivida(), selecionado.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
                            bloqueadasPorFase.add(divida);
                        } else {
                            validarIntegracaoDividaAtiva(configuracaoContabil, divida);
                        }
                    }
                    if (!bloqueadasPorFase.isEmpty()) {
                        FacesUtil.atualizarComponente("formMudaData");
                        FacesUtil.executaJavaScript("mudaData.show()");
                    }
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Não foi encontrado dívidas ativas tributárias para importação.");
                }
            }
        } catch (Exception ex) {
            lancamentoIntegracao.clear();
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    private boolean validarIntegracaoDividaAtiva(ConfiguracaoContabil configuracaoContabil, DividaAtivaContabil d) {
        try {
            d.setPessoa(dividaAtivaContabilFacade.getPessoaFacade().recuperaPessoaDaEntidade(d.getUnidadeOrganizacional(), d.getDataDivida()));
            d.setClasseCredorPessoa(configuracaoContabil.getClasseTribContDividaAtiv());

            ConfigDividaAtivaContabil configDividaAtivaContabil = dividaAtivaContabilFacade.getConfigDividaAtivaContabilFacade().recuperaEvento(d);
            if (configDividaAtivaContabil == null) {
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
        ConfiguracaoContabil configuracaoContabil = dividaAtivaContabilFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
        List bloqueadasPorFase = Lists.newArrayList();
        if (validarNovaDataContabilizacao()) {
            for (DividaAtivaContabil divida : this.bloqueadasPorFase) {
                divida.setDataDivida(novaDataContabilizacao);
                divida.setPessoa(dividaAtivaContabilFacade.getPessoaFacade().recuperaPessoaDaEntidade(divida.getUnidadeOrganizacional(), divida.getDataDivida()));
                divida.setClasseCredorPessoa(configuracaoContabil.getClasseTribContReceitaRea());
                if (dividaAtivaContabilFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/lancamentocontabilmanual/dividaativa/integracao.xhtml", divida.getDataDivida(), selecionado.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
                    bloqueadasPorFase.add(divida);
//                } else {
//                    validarIntegracaoDividaAtiva(configuracaoContabil, divida);
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


    public Boolean marcarDividaAtivaBloqueada(DividaAtivaContabil divida) {
        if (dividaAtivaContabilFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/lancamentocontabilmanual/dividaativa/integracao.xhtml", divida.getDataDivida(), selecionado.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
            return true;
        }
        return false;
    }

    public Boolean validarDividaAtivaBloqueadasAoSalvar() {
        for (DividaAtivaContabil divida : lancamentoIntegracao) {
            if (dividaAtivaContabilFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/lancamentocontabilmanual/dividaativa/integracao.xhtml", divida.getDataDivida(), selecionado.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
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

    public BigDecimal getValorTotal() {
        BigDecimal valor = BigDecimal.ZERO;
        if (lancamentoIntegracao != null) {
            for (DividaAtivaContabil dividaAtivaContabil : lancamentoIntegracao) {
                if (dividaAtivaContabil != null) {
                    valor = valor.add(dividaAtivaContabil.getValor());
                }
            }
        }
        return valor;
    }

    public Boolean verificaEdicao() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        } else {
            return false;
        }

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

    public TiposCredito getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TiposCredito tiposCredito) {
        this.tiposCredito = tiposCredito;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Date getNovaDataContabilizacao() {
        return novaDataContabilizacao;
    }

    public void setNovaDataContabilizacao(Date novaDataContabilizacao) {
        this.novaDataContabilizacao = novaDataContabilizacao;
    }
}
