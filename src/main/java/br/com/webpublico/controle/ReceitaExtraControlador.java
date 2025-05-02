/*
 * Codigo gerado automaticamente em Thu Dec 22 17:19:38 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.IConciliar;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ReceitaExtraFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "receitaExtraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-receita-extra", pattern = "/receita-extra/novo/", viewId = "/faces/financeiro/extraorcamentario/receitaextra/edita.xhtml"),
    @URLMapping(id = "editar-receita-extra", pattern = "/receita-extra/editar/#{receitaExtraControlador.id}/", viewId = "/faces/financeiro/extraorcamentario/receitaextra/edita.xhtml"),
    @URLMapping(id = "ver-receita-extra", pattern = "/receita-extra/ver/#{receitaExtraControlador.id}/", viewId = "/faces/financeiro/extraorcamentario/receitaextra/visualizar.xhtml"),
    @URLMapping(id = "listar-receita-extra", pattern = "/receita-extra/listar/", viewId = "/faces/financeiro/extraorcamentario/receitaextra/lista.xhtml")
})
public class ReceitaExtraControlador extends PrettyControlador<ReceitaExtra> implements Serializable, CRUD, IConciliar {

    @EJB
    private ReceitaExtraFacade receitaExtraFacade;
    private MoneyConverter moneyConverter;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private UnidadeOrganizacional unidadeOrg;
    private ConverterAutoComplete converterPessoa;
    private BigDecimal saldoContaExtra;
    private BigDecimal saldoContaFinanceira;
    private ConfigReceitaExtra configReceitaExtra;
    private ContaBancariaEntidade contaBancariaEntidade;
    private Boolean gestorFinanceiro;

    public ReceitaExtraControlador() {
        super(ReceitaExtra.class);
    }

    @URLAction(mappingId = "novo-receita-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
        hierarquiaOrganizacionalSelecionada = new HierarquiaOrganizacional();
        unidadeOrg = sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente();
        selecionado.setSituacaoReceitaExtra(SituacaoReceitaExtra.ABERTO);
        selecionado.setDataReceita(sistemaControlador.getDataOperacao());
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setDataReceita(sistemaControlador.getDataOperacao());
        selecionado.setTipoConsignacao(TipoConsignacao.NAO_APLICAVEL);
        saldoContaExtra = BigDecimal.ZERO;
        saldoContaFinanceira = BigDecimal.ZERO;
        configReceitaExtra = null;

        if (receitaExtraFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", receitaExtraFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
        gestorFinanceiro = receitaExtraFacade.getUsuarioSistemaFacade().isGestorFinanceiro(getSistemaControlador().getUsuarioCorrente(), selecionado.getUnidadeOrganizacionalAdm(), selecionado.getUnidadeOrganizacional(), getSistemaControlador().getDataOperacao());
    }

    public void setarPessoa(ActionEvent evento) {
        selecionado.setPessoa((Pessoa) evento.getComponent().getAttributes().get("objeto"));
    }


    public String caminhoVisualizar() {
        return "edita";
    }

    @URLAction(mappingId = "ver-receita-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-receita-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
        gestorFinanceiro = receitaExtraFacade.getUsuarioSistemaFacade().isGestorFinanceiro(getSistemaControlador().getUsuarioCorrente(), selecionado.getUnidadeOrganizacionalAdm(), selecionado.getUnidadeOrganizacional(), getSistemaControlador().getDataOperacao());
    }

    public void recuperarEditarVer() {
        hierarquiaOrganizacionalSelecionada = receitaExtraFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataReceita(), selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        unidadeOrg = sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente();
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        recuperaSaldos();
        recuperaConfiguracaoEventoContabil();
        setarContaBancaria();
    }

    @Override
    public void salvar() {
        setarEventoReceitaExtra(selecionado);
        try {
            validarReceitaExtra();
            if (operacao.equals(Operacoes.NOVO)) {
                if (!selecionado.getTransportado()) {
                    selecionado.setEventoContabil(configReceitaExtra.getEventoContabil());
                    receitaExtraFacade.salvarNovo(selecionado);
                } else {
                    receitaExtraFacade.salvarReceitaSemContabilizar(selecionado);
                }
                FacesUtil.addOperacaoRealizada("Registro " + selecionado + " salvo com sucesso.");
                redirecionarParaVerOrEditar(selecionado.getId(), "editar");
            } else {
                receitaExtraFacade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada("Registro " + selecionado + " alterado com sucesso.");
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            receitaExtraFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(ex);
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarReceitaExtra() {
        selecionado.realizarValidacoes();
        validarCampos();
    }


    private void setarEventoReceitaExtra(ReceitaExtra re) {
        if (configReceitaExtra != null) {
            if (configReceitaExtra.getEventoContabil() != null) {
                re.setEventoContabil(configReceitaExtra.getEventoContabil());
            }
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser maior que zero (0).");
        }
        if (!selecionado.getTransportado()) {
            if (selecionado.getEventoContabil() == null || configReceitaExtra.getEventoContabil() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Nenhum evento contábil encontrado para este lançamento.");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    @Override
    public AbstractFacade getFacede() {
        return receitaExtraFacade;
    }

    public List<SubConta> completaSubContas(String parte) {
        return receitaExtraFacade.getSubContaFacade().listaPorContaEUnidadeOrganizacional(parte.trim(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
    }

    public List<SelectItem> getContasDeDestinacaoPorSubConta() {
        List<SelectItem> contas = Lists.newArrayList();
        contas.add(new SelectItem(null, ""));
        if (selecionado.getSubConta() != null) {
            SubConta subConta = receitaExtraFacade.getSubContaFacade().recuperar(selecionado.getSubConta().getId());
            if (subConta.getSubContaFonteRecs() != null && !subConta.getSubContaFonteRecs().isEmpty()) {
                for (SubContaFonteRec sfr : subConta.getSubContaFonteRecs()) {
                    if (selecionado.getExercicio().equals(sfr.getContaDeDestinacao().getExercicio())) {
                        contas.add(new SelectItem(sfr.getContaDeDestinacao(), sfr.getContaDeDestinacao().toString()));
                    }
                }
            }
        }
        return contas;
    }

    public void atualizarFonteComContaDeDestinacao() {
        if (selecionado.getContaDeDestinacao() != null) {
            selecionado.setFonteDeRecursos(selecionado.getContaDeDestinacao().getFonteDeRecursos());
        }
    }

    public List<FonteDeRecursos> completaFonteDeRecursos(String parte) {
        return receitaExtraFacade.getFonteDeRecursosFacade().listaFiltrandoPorContaFinanceiraExercicio(parte.trim(), ((ReceitaExtra) selecionado).getSubConta(), sistemaControlador.getExercicioCorrente());
    }

    public List<Conta> completaContaExtraorcamentaria(String parte) {
        return receitaExtraFacade.getContaFacade().listaFiltrandoExtraorcamentarioSemConsignacao(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public Boolean getVerificaEdicao() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        } else {
            return false;
        }
    }

    public void setaSaldo() {
        ReceitaExtra re = ((ReceitaExtra) selecionado);
        re.setSaldo(re.getValor());
    }

    public List<Pessoa> completaPessoa(String parte) {
        return receitaExtraFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public void setaPessoa(SelectEvent evt) {
        Pessoa p = (Pessoa) evt.getObject();
        ((ReceitaExtra) selecionado).setPessoa(p);
        ((ReceitaExtra) selecionado).setClasseCredor(null);
        FacesUtil.executaJavaScript("setaFoco('Formulario:classeCredor_input')");
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, receitaExtraFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return receitaExtraFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte, ((ReceitaExtra) selecionado).getPessoa());
    }

    public void setaContaExtraOrcamentario(SelectEvent evt) {
        ((ReceitaExtra) selecionado).setContaExtraorcamentaria((Conta) evt.getObject());
        recuperaSaldos();
        recuperaConfiguracaoEventoContabil();
    }

    public void validaCategoriaContaExtra(FacesContext context, UIComponent component, Object value) {
        FacesMessage msg = new FacesMessage();
        Conta cc = (Conta) value;
        cc = receitaExtraFacade.getContaFacade().recuperar(cc.getId());
        CategoriaConta categoriaDaConta = receitaExtraFacade.getContaFacade().recuperaCategoriaDaConta(cc);
        if (categoriaDaConta.equals(CategoriaConta.SINTETICA)) {
            msg.setDetail("Conta Sintética, não poderá ser utilizada.");
            msg.setSummary("Operação não Permitida! ");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void validaDataReceitaExtra(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date data = (Date) value;
        Calendar dataReceita = Calendar.getInstance();
        dataReceita.setTime(data);
        Integer ano = sistemaControlador.getExercicioCorrente().getAno();
        if (dataReceita.get(Calendar.YEAR) != ano) {
            message.setDetail("Ano diferente do exercício corrente.");
            message.setSummary("Operação não Permitida! ");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    private void recuperaSaldoContaFinanceira() {
        BigDecimal saldo = BigDecimal.ZERO;
        if (selecionado.getFonteDeRecursos() != null && selecionado.getUnidadeOrganizacional() != null && selecionado.getSubConta() != null && selecionado.getDataReceita() != null) {
            SaldoSubConta saldoSubConta = receitaExtraFacade.getSaldoSubContaFacade().recuperaUltimoSaldoSubContaPorData(selecionado.getUnidadeOrganizacional(), selecionado.getSubConta(), selecionado.getContaDeDestinacao(), selecionado.getDataReceita());
            if (saldoSubConta != null && saldoSubConta.getId() != null) {
                saldo = saldoSubConta.getTotalCredito().subtract(saldoSubConta.getTotalDebito());
            }
        }
        saldoContaFinanceira = saldo;
    }

    public void recuperaSaldoContaExtraorcamentaria() {
        BigDecimal saldo = BigDecimal.ZERO;
        if (selecionado.getContaDeDestinacao() != null && selecionado.getUnidadeOrganizacional() != null && selecionado.getContaExtraorcamentaria() != null && selecionado.getDataReceita() != null) {
            SaldoExtraorcamentario saldoExtraorcamentario = receitaExtraFacade.getSaldoExtraorcamentarioFacade().recuperaUltimoSaldoPorData(selecionado.getDataReceita(), selecionado.getContaExtraorcamentaria(), selecionado.getContaDeDestinacao(), selecionado.getUnidadeOrganizacional());
            if (saldoExtraorcamentario != null && saldoExtraorcamentario.getId() != null) {
                saldo = saldoExtraorcamentario.getValor();
            }
        }
        saldoContaExtra = saldo;
    }

    public void recuperaSaldos() {
        recuperaSaldoContaExtraorcamentaria();
        recuperaSaldoContaFinanceira();
    }

    public void recuperaConfiguracaoEventoContabil() {
        try {
            if (!selecionado.getTransportado()) {
                configReceitaExtra = receitaExtraFacade.getConfigReceitaExtraFacade().recuperaConfiguracaoEventoContabil(selecionado);
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addError("Evento Contábil não encontrado! ", e.getMessage());
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/receita-extra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void setarContaBancaria() {
        try {
            contaBancariaEntidade = selecionado.getSubConta().getContaBancariaEntidade();
        } catch (Exception e) {

        }
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public void gerarNotaOrcamentaria(boolean isDownload, ReceitaExtra receitaExtra) {
        try {
            NotaExecucaoOrcamentaria notaExecucaoOrcamentaria = receitaExtraFacade.buscarNotaReceitaExtra(receitaExtra, sistemaControlador.getUsuarioCorrente());
            if (notaExecucaoOrcamentaria != null) {
                receitaExtraFacade.getNotaOrcamentariaFacade().imprimirDocumentoOficial(notaExecucaoOrcamentaria, notaExecucaoOrcamentaria.getHasIdRetencaoPagamento() ? ModuloTipoDoctoOficial.NOTA_RECEITA_EXTRA_PAGAMENTO : ModuloTipoDoctoOficial.NOTA_RECEITA_EXTRA, selecionado, isDownload);
            }
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public Boolean mostrarBotaoBaixar() {
        return selecionado.getId() != null
            && SituacaoReceitaExtra.ABERTO.equals(selecionado.getSituacaoReceitaExtra());
    }

    public Boolean mostrarBotaoEstornarBaixar() {
        return selecionado.getId() != null
            && SituacaoReceitaExtra.EFETUADO.equals(selecionado.getSituacaoReceitaExtra()) &&
            receitaExtraFacade.canEstornarBaixa(selecionado);
    }

    @Override
    public void baixar() {
        try {
            validarReceitaExtra();
            validarDataConciliacao();
            receitaExtraFacade.baixar(selecionado);
            FacesUtil.addOperacaoRealizada(" A Receita Extraorçamentária N° <b>" + selecionado.getNumero() + "</b> foi baixada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarDataConciliacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataConciliacao() != null && DataUtil.getAno(selecionado.getDataReceita()).compareTo(DataUtil.getAno(selecionado.getDataConciliacao())) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conciliação está com o exercício diferente da data do registro.");
        }
        ve.lancarException();
    }

    @Override
    public void estornarBaixa() {
        try {
            validarReceitaExtra();
            receitaExtraFacade.estornarBaixa(selecionado);
            FacesUtil.addOperacaoRealizada(" A Receita Extraorçamentária N° <b>" + selecionado.getNumero() + "</b> foi estornada a baixa com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            descobrirETratarException(ex);
        }
    }

    public ConfigReceitaExtra getConfigReceitaExtra() {
        return configReceitaExtra;
    }

    public void setConfigReceitaExtra(ConfigReceitaExtra configReceitaExtra) {
        this.configReceitaExtra = configReceitaExtra;
    }

    public ReceitaExtraFacade getReceitaExtraFacade() {
        return receitaExtraFacade;
    }

    public void setReceitaExtraFacade(ReceitaExtraFacade receitaExtraFacade) {
        this.receitaExtraFacade = receitaExtraFacade;
    }

    public BigDecimal getSaldoContaExtra() {
        return saldoContaExtra;
    }

    public void setSaldoContaExtra(BigDecimal saldoContaExtra) {
        this.saldoContaExtra = saldoContaExtra;
    }

    public BigDecimal getSaldoContaFinanceira() {
        return saldoContaFinanceira;
    }

    public void setSaldoContaFinanceira(BigDecimal saldoContaFinanceira) {
        this.saldoContaFinanceira = saldoContaFinanceira;
    }

    public ReceitaExtraFacade getFacade() {
        return receitaExtraFacade;
    }

    public UnidadeOrganizacional getUnidadeOrg() {
        return unidadeOrg;
    }

    public void setUnidadeOrg(UnidadeOrganizacional unidadeOrg) {
        this.unidadeOrg = unidadeOrg;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public Boolean getGestorFinanceiro() {
        return gestorFinanceiro;
    }

    public void setGestorFinanceiro(Boolean gestorFinanceiro) {
        this.gestorFinanceiro = gestorFinanceiro;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    @Override
    public void cleanScoped() {
        super.cleanScoped();
        moneyConverter = null;
        sistemaControlador = null;
        hierarquiaOrganizacionalSelecionada = null;
        unidadeOrg = null;
        converterPessoa = null;
        saldoContaExtra = null;
        saldoContaFinanceira = null;
        configReceitaExtra = null;
        contaBancariaEntidade = null;
        gestorFinanceiro = null;
    }


    public void redirecionaSemLimpar() {
        FacesUtil.navegaEmbora(selecionado, ((CRUD) this).getCaminhoPadrao());
    }
}
