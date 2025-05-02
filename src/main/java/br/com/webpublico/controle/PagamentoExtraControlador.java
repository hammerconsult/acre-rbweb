/*
 * Codigo gerado automaticamente em Tue Jan 03 10:09:55 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.GuiaPagamentoItem;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.IConciliar;
import br.com.webpublico.interfaces.IGuiaArquivoOBN600;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.PagamentoExtraFacade;
import br.com.webpublico.util.CarneUtil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@ManagedBean(name = "pagamentoExtraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-despesa-extra", pattern = "/despesa-extra/novo/", viewId = "/faces/financeiro/extraorcamentario/pagamentoextra/edita.xhtml"),
    @URLMapping(id = "editar-despesa-extra", pattern = "/despesa-extra/editar/#{pagamentoExtraControlador.id}/", viewId = "/faces/financeiro/extraorcamentario/pagamentoextra/edita.xhtml"),
    @URLMapping(id = "ver-despesa-extra", pattern = "/despesa-extra/ver/#{pagamentoExtraControlador.id}/", viewId = "/faces/financeiro/extraorcamentario/pagamentoextra/visualizar.xhtml"),
    @URLMapping(id = "listar-despesa-extra", pattern = "/despesa-extra/listar/", viewId = "/faces/financeiro/extraorcamentario/pagamentoextra/lista.xhtml")
})
public class PagamentoExtraControlador extends PrettyControlador<PagamentoExtra> implements Serializable, CRUD, IConciliar {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private PagamentoExtraFacade pagamentoExtraFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private Integer indiceAba;
    private List<ReceitaExtra> listaReceitaExtras;
    private ReceitaExtra[] listaRecSelecionadas;
    private ContaBancariaEntidade contaBancariaEntidade;
    private GuiaPagamentoExtra guiaPagamentoExtra;
    private GuiaPagamentoItem guiaPagamentoItem;
    private Boolean gestorFinanceiro;
    private Date dataInicialFiltroReceita;
    private Date dataFinalFiltroReceita;

    public PagamentoExtraControlador() {
        super(PagamentoExtra.class);
    }

    @URLAction(mappingId = "novo-despesa-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        parametrosIniciais();
        gestorFinanceiro = isGestorFinanceiro();
    }

    @URLAction(mappingId = "ver-despesa-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-despesa-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    public void recuperarEditarVer() {
        contaBancariaEntidade = selecionado.getSubConta().getContaBancariaEntidade();
        cancelarGuiaPagamento();
        gestorFinanceiro = isGestorFinanceiro();
    }

    private void parametrosIniciais() {
        selecionado.setUsuarioSistema(getUsuarioSistema());
        selecionado.setPrevistoPara(sistemaControlador.getDataOperacao());
        selecionado.setStatus(StatusPagamento.ABERTO);
        selecionado.setTipoDocumento(TipoDocPagto.ORDEMPAGAMENTO);
        indiceAba = 0;
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        if (pagamentoExtraFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", pagamentoExtraFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    public void gerarNotaOrcamentaria(boolean isDownload) {
        try {
            NotaExecucaoOrcamentaria notaExecucaoOrcamentaria = pagamentoExtraFacade.buscarNotaPagamentoExtra(selecionado, getSaldoContaExtra());
            if (notaExecucaoOrcamentaria != null) {
                pagamentoExtraFacade.getNotaOrcamentariaFacade().imprimirDocumentoOficial(notaExecucaoOrcamentaria, ModuloTipoDoctoOficial.NOTA_PAGAMENTO_EXTRA, selecionado, isDownload);
            }
        } catch (Exception ex) {
            logger.error("Erro ao gerar nota de pagamento extra: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void atribuirEventoContabil() {
        try {
            selecionado.setEventoContabil(null);
            if (selecionado.getContaExtraorcamentaria() != null) {
                if (((ContaExtraorcamentaria) selecionado.getContaExtraorcamentaria()).getTipoContaExtraorcamentaria() != null) {
                    ConfigDespesaExtra configuracao = pagamentoExtraFacade.getConfigDespesaExtraFacade().recuperaEvento(TipoLancamento.NORMAL, ((ContaExtraorcamentaria) selecionado.getContaExtraorcamentaria()).getTipoContaExtraorcamentaria(), selecionado.getPrevistoPara());
                    Preconditions.checkNotNull(configuracao, " Não foi encontrada uma configuração para os parametros informados ");
                    selecionado.setEventoContabil(configuracao.getEventoContabil());
                }
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void recuperaEventoContabilReceitaExtra() {
        try {
            for (PagamentoReceitaExtra pag : selecionado.getPagamentoReceitaExtras()) {
                if (pag.getReceitaExtra() != null
                    && !pag.getReceitaExtra().getTransportado()) {
                    pagamentoExtraFacade.getReceitaExtraFacade().getConfigReceitaExtraFacade().recuperaConfiguracaoEventoContabil(pag.getReceitaExtra());
                }
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Evento Contábil não encontrado! ", e.getMessage()));
        }
    }

    @Override
    public void salvar() {
        try {
            validarDespesaExtra();
            pagamentoExtraFacade.getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(selecionado.getUnidadeOrganizacionalAdm(), selecionado.getUnidadeOrganizacional(), selecionado.getPrevistoPara());
            if (isOperacaoNovo()) {
                pagamentoExtraFacade.salvarNovo(selecionado);
                redirecionarParaVer();
            } else {
                pagamentoExtraFacade.salvar(selecionado);
                redireciona();
            }
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
        cleanScoped();
    }

    private void validarDespesaExtra() {
        selecionado.realizarValidacoes();
        validarCampos();
    }

    public void prepararDespesaParaDeferir() {
        try {
            validarDespesaExtra();
            validarGuias();
            selecionado = pagamentoExtraFacade.atualizarRetornandoEmJdbc(selecionado);
            selecionado.setDataPagto(sistemaControlador.getDataOperacao());
            FacesUtil.executaJavaScript("dialogDeferir.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("atualizarPagamento.hide()");
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }


    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero(0).");
        }
        if (selecionado.getContaExtraorcamentaria() != null) {
            if (((ContaExtraorcamentaria) selecionado.getContaExtraorcamentaria()).getTipoContaExtraorcamentaria() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" A Conta: " + selecionado.getContaExtraorcamentaria() + " não possui um Tipo de Conta Extraorçamentária.");
            }
        }
        for (PagamentoReceitaExtra pagamentoReceitaExtra : selecionado.getPagamentoReceitaExtras()) {
            if (pagamentoReceitaExtra.getReceitaExtra() != null) {
                pagamentoExtraFacade.getHierarquiaOrganizacionalFacade()
                    .validarUnidadesIguais(pagamentoReceitaExtra.getReceitaExtra().getUnidadeOrganizacional(), selecionado.getUnidadeOrganizacional()
                        , ve
                        , "A Unidade Organizacional da despesa extra " + pagamentoReceitaExtra.getReceitaExtra().getNumero() + " deve ser a mesma da receita extra.");
            }
        }
        if (selecionado.isDespesaExtraComGuia()
            && selecionado.getGuiaPagamentoExtras().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Quando utilizado o tipo de documento <b> " + selecionado.getTipoDocumento().toString() + " </b>, é obrigatório ter pelo menos uma guia de " + selecionado.getTipoDocumento().toString() + ".");
        }
        if (selecionado.getTipoDocumento().isDepju() && (selecionado.getIdentificadorDeposito() == null || selecionado.getIdentificadorDeposito().isEmpty())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo ID / Identificador deve ser informado.");
        }
        if (selecionado.getPagamentoReceitaExtras() == null || selecionado.getPagamentoReceitaExtras().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Deve ser adicionado ao menos uma receita extra.");
        }
        ve.lancarException();
    }

    private void validarGuias() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.getGuiaPagamentoExtras().isEmpty()) {
            if (selecionado.getValor().compareTo(selecionado.getValorTotalGuiasPagamento()) != 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor total para as guias do tipo: " + selecionado.getTipoDocumento().getDescricao() + " (" + selecionado.getTipoDocumento().getCodigo() + ") de <b>" + Util.formataValor(selecionado.getValorTotalGuiasPagamento()) + "</b>, está diferente do valor da despesa extra de <b>" + Util.formataValor(selecionado.getValor()) + "</b>.");
            }
        }
        ve.lancarException();
    }

    public Boolean desabilitarTipoDocumentoParaGuias() {
        return !selecionado.getGuiaPagamentoExtras().isEmpty();
    }

    private void validarDeferir() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataPagto() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Data de Pagamento deve ser informado.");
        }
        ve.lancarException();
    }

    public void indeferirPagamento() {
        try {
            pagamentoExtraFacade.indeferirPagamento(selecionado);
            FacesUtil.addOperacaoRealizada(" O Pagamento N° <b>" + selecionado.getNumero() + "</b> foi indeferido com sucesso.");
            redireciona();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void deferirPagamentoExtra() {
        try {
            validarDeferir();
            FacesUtil.executaJavaScript("dialogDeferir.hide()");
            selecionado = pagamentoExtraFacade.deferirPagamento(selecionado);
            finalizaDeferirDespesaExtraVerificandoOperacaoBaixaParaRegularizacao();
            pagamentoExtraFacade.desbloquearDeferir(selecionado);

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            pagamentoExtraFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(e);
            pagamentoExtraFacade.desbloquearDeferir(selecionado);
        } catch (Exception ex) {
            pagamentoExtraFacade.desbloquearDeferir(selecionado);
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void finalizaDeferirDespesaExtraVerificandoOperacaoBaixaParaRegularizacao() {
        if (TipoOperacaoPagto.BAIXA_PARA_REGULARIZACAO.equals(selecionado.getTipoOperacaoPagto())) {
            selecionado = pagamentoExtraFacade.recuperar(selecionado.getId());
            baixar();
        } else {
            FacesUtil.executaJavaScript("dialogDeferir.hide()");
            FacesUtil.executaJavaScript("dialogImprimirNota.show()");
        }
    }

    public Boolean getHabilitarCampos() {
        return !StatusPagamento.INDEFERIDO.equals(selecionado.getStatus()) &&
            !StatusPagamento.DEFERIDO.equals(selecionado.getStatus()) &&
            !StatusPagamento.ABERTO.equals(selecionado.getStatus());
    }

    public Boolean getHabilitarBotaoEditar() {
        return StatusPagamento.INDEFERIDO.equals(selecionado.getStatus())
            || StatusPagamento.DEFERIDO.equals(selecionado.getStatus())
            || StatusPagamento.ABERTO.equals(selecionado.getStatus());
    }

    public List<FinalidadePagamento> completarFinalidadesDePagamento(String parte) {
        return pagamentoExtraFacade.getFinalidadePagamentoFacade().completaFinalidadesPorSituacao(parte.trim(), SituacaoCadastralContabil.ATIVO);
    }

    public List<Pessoa> completarFornecedores(String parte) {
        return pagamentoExtraFacade.getFornecedorFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<Conta> completarContasExtraorcamentarias(String parte) {
        return pagamentoExtraFacade.getContaExtraorcamentariaFacade().listaFiltrandoExtraorcamentario(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<SelectItem> getContasDeDestinacaoPorSubConta() {
        List<SelectItem> dados = Lists.newArrayList();
        dados.add(new SelectItem(null, ""));
        if (selecionado.getSubConta() != null && selecionado.getSubConta().getId() != null) {
            selecionado.setSubConta(pagamentoExtraFacade.getSubContaFacade().recuperar(selecionado.getSubConta().getId()));
            for (SubContaFonteRec subContaFonteRec : selecionado.getSubConta().getSubContaFonteRecs()) {
                if (subContaFonteRec.getContaDeDestinacao().getExercicio().equals(sistemaControlador.getExercicioCorrente())) {
                    dados.add(new SelectItem(subContaFonteRec.getContaDeDestinacao(), subContaFonteRec.getContaDeDestinacao().toString()));
                }
            }
        }
        return Util.ordenaSelectItem(dados);
    }

    public void atualizarFonteComContaDeDestinacao() {
        if (selecionado.getContaDeDestinacao() != null) {
            selecionado.setFonteDeRecursos(selecionado.getContaDeDestinacao().getFonteDeRecursos());
        }
    }

    public UsuarioSistema getUsuarioSistema() {
        return pagamentoExtraFacade.getSistemaFacade().getUsuarioCorrente();
    }

    public BigDecimal getSaldoContaExtra() {
        BigDecimal saldo = BigDecimal.ZERO;
        if (selecionado.getContaDeDestinacao() != null && selecionado.getUnidadeOrganizacional() != null && selecionado.getContaExtraorcamentaria() != null && selecionado.getPrevistoPara() != null) {
            SaldoExtraorcamentario saldoExtraorcamentario = pagamentoExtraFacade.getSaldoExtraorcamentarioFacade().recuperaUltimoSaldoPorData(selecionado.getPrevistoPara(), selecionado.getContaExtraorcamentaria(), selecionado.getContaDeDestinacao(), selecionado.getUnidadeOrganizacional());
            if (saldoExtraorcamentario != null && saldoExtraorcamentario.getId() != null) {
                saldo = saldoExtraorcamentario.getValor();
            }
        }
        return saldo;
    }

    public BigDecimal getSomaReceitaExtras() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (PagamentoReceitaExtra pagamentoReceitaExtra : selecionado.getPagamentoReceitaExtras()) {
            valor = valor.add(pagamentoReceitaExtra.getReceitaExtra().getValor());
        }
        return valor;
    }

    public Boolean getVerificarEdicao() {
        return isOperacaoEditar() && !StatusPagamento.ABERTO.equals(selecionado.getStatus());
    }

    public boolean isAberto() {
        return StatusPagamento.ABERTO.equals(selecionado.getStatus());
    }

    public void removerReceitaExtra(PagamentoReceitaExtra pagamentoReceitaExtra) {
        if (pagamentoReceitaExtra != null) {
            selecionado.setValor(selecionado.getValor().subtract(pagamentoReceitaExtra.getReceitaExtra().getSaldo()));
            selecionado.getPagamentoReceitaExtras().remove(pagamentoReceitaExtra);
            listaRecSelecionadas = null;
        }
    }

    public List<ReceitaExtra> getListaReceitaExtras() {
        return listaReceitaExtras;
    }

    public void setListaReceitaExtras(List<ReceitaExtra> listaReceitaExtras) {
        this.listaReceitaExtras = listaReceitaExtras;
    }

    public void carregarReceitasExtras() {
        if (validaFiltrosListaReceitaExtra(selecionado)) {
            listaReceitaExtras = pagamentoExtraFacade.getReceitaExtraFacade().listaReceitaPorUnidadeContaExtraSubContaFonte(
                selecionado.getUnidadeOrganizacional(), selecionado.getContaExtraorcamentaria(), selecionado.getSubConta(), selecionado.getFonteDeRecursos(), dataInicialFiltroReceita, dataFinalFiltroReceita
            );
        }
    }

    public ReceitaExtra[] getListaRecSelecionadas() {
        return listaRecSelecionadas;
    }

    public void setListaRecSelecionadas(ReceitaExtra[] listaRecSelecionadas) {
        this.listaRecSelecionadas = listaRecSelecionadas;
    }

    public void adicionarReceitasExtras() {
        if (listaRecSelecionadas.length == 0) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " Para continuar a operação selecione uma ou mais receita(s) extraorçamentária(s).");
            return;
        }
        for (int i = 0; i < this.listaRecSelecionadas.length; i++) {
            Boolean achou = false;
            for (PagamentoReceitaExtra pag : selecionado.getPagamentoReceitaExtras()) {
                if (pag.getReceitaExtra().equals(listaRecSelecionadas[i])) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A receita extraorçamentária: " + pag.getReceitaExtra() + " já foi adicionada na lista.");
                    achou = true;
                }
            }
            PagamentoReceitaExtra pagamentoReceitaExtra = new PagamentoReceitaExtra();
            pagamentoReceitaExtra.setReceitaExtra(listaRecSelecionadas[i]);
            pagamentoReceitaExtra.setPagamentoExtra(selecionado);
            if (!achou) {
                Util.adicionarObjetoEmLista(selecionado.getPagamentoReceitaExtras(), pagamentoReceitaExtra);
            }
        }
        selecionado.setValor(getSomaReceitaExtras());
        getSaldoContaExtra();
        recuperaEventoContabilReceitaExtra();
    }

    public void atribuirPessoa(SelectEvent evt) {
        Pessoa p = (Pessoa) evt.getObject();
        selecionado.setFornecedor(p);
        selecionado.setClasseCredor(null);
        selecionado.setContaCorrenteBancaria(null);
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:classeCredor_input')");
    }

    public void setarContaCorrentePessoa() {
        Pessoa pessoa = pagamentoExtraFacade.getFornecedorFacade().recuperar(selecionado.getFornecedor().getId());
        ClasseCredor classeCredor = pagamentoExtraFacade.getClasseCredorFacade().recuperar(selecionado.getClasseCredor().getId());
        ConfigContaBancariaPessoa configContaBancariaPessoa = pagamentoExtraFacade.getConfigContaBancariaPessoaFacade().recuperarConfiguracaoContaBancariaPessoa(pessoa, classeCredor);
        if (configContaBancariaPessoa != null) {
            selecionado.setContaCorrenteBancaria(configContaBancariaPessoa.getContaCorrenteBancaria());
        } else {
            for (ContaCorrenteBancPessoa c : pessoa.getContaCorrenteBancPessoas()) {
                if (c.getPrincipal() != null) {
                    if (c.getContaCorrenteBancaria().getSituacao() != null) {
                        if (c.getContaCorrenteBancaria().getSituacao().equals(SituacaoConta.ATIVO)) {
                            selecionado.setContaCorrenteBancaria(c.getContaCorrenteBancaria());
                            selecionarContaBancaria(c);
                        }
                    }
                }
            }
        }
        if (selecionado.getContaCorrenteBancaria() == null) {
            FacesUtil.addWarn(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Não foi possivel encontrar uma Conta Bancária marcada como Principal para o(a) " + pessoa.getNomeCpfCnpj() + " .");
        }
    }

    public List<ClasseCredor> completarClassesCredor(String parte) {
        return pagamentoExtraFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte, ((PagamentoExtra) selecionado).getFornecedor());
    }

    public void selecionarContaBancaria(ContaCorrenteBancPessoa contaCorrenteBancPessoa) {
        selecionado.setContaCorrenteBancaria(contaCorrenteBancPessoa.getContaCorrenteBancaria());
        setaNullTipoOperacaoPagamento();
    }

    public void setaNullTipoOperacaoPagamento() {
        selecionado.setTipoOperacaoPagto(null);
    }

    public List<ContaCorrenteBancPessoa> contaBancariaPessoaDespesaExtra() {
        if (selecionado.getFornecedor() != null) {
            return pagamentoExtraFacade.getContaCorrenteBancPessoaFacade().listaContasBancariasPorPessoa(selecionado.getFornecedor(), SituacaoConta.ATIVO);
        }
        return new ArrayList<>();
    }

    private boolean isContasMesmoBanco() {
        return selecionado.getContaCorrenteBancaria() != null &&
            selecionado.getContaCorrenteBancaria().getAgencia() != null &&
            selecionado.getContaCorrenteBancaria().getAgencia().getBanco() != null &&
            selecionado.getSubConta() != null &&
            selecionado.getSubConta().getContaBancariaEntidade() != null &&
            selecionado.getSubConta().getContaBancariaEntidade().getAgencia() != null &&
            selecionado.getSubConta().getContaBancariaEntidade().getAgencia().getBanco() != null &&
            selecionado.getContaCorrenteBancaria().getAgencia().getBanco().equals(selecionado.getSubConta().getContaBancariaEntidade().getAgencia().getBanco());
    }

    public List<SelectItem> getTipoOperacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoOperacaoPagto.BAIXA_PARA_REGULARIZACAO, TipoOperacaoPagto.BAIXA_PARA_REGULARIZACAO.getDescricao()));
        if (selecionado.getSubConta() != null) {
            if (selecionado.getContaCorrenteBancaria() == null) {
                if (!selecionado.getSubConta().isContaUnica()) {
                    toReturn.add(new SelectItem(TipoOperacaoPagto.LIQUIDACAO_NO_CAIXA_NAO_CONTA_UNICA, TipoOperacaoPagto.LIQUIDACAO_NO_CAIXA_NAO_CONTA_UNICA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.PAGAMENTO_NAO_AUTENTICADO_NAO_CONTA_UNICA, TipoOperacaoPagto.PAGAMENTO_NAO_AUTENTICADO_NAO_CONTA_UNICA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA_NAO_CONTA_UNICA, TipoOperacaoPagto.PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA_NAO_CONTA_UNICA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA_NAO_CONTA_UNICA, TipoOperacaoPagto.PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA_NAO_CONTA_UNICA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.TRANSFERENCIA_DE_RECURSOS_OB_LISTA_NAO_CONTA_UNICA, TipoOperacaoPagto.TRANSFERENCIA_DE_RECURSOS_OB_LISTA_NAO_CONTA_UNICA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.CREDOR_MESMO_BANCO_NAO_CONTA_UNICA, TipoOperacaoPagto.CREDOR_MESMO_BANCO_NAO_CONTA_UNICA.getDescricao()));
                } else {
                    toReturn.add(new SelectItem(TipoOperacaoPagto.LIQUIDACAO_NO_CAIXA, TipoOperacaoPagto.LIQUIDACAO_NO_CAIXA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.PAGAMENTO_COM_AUTENTICACAO, TipoOperacaoPagto.PAGAMENTO_COM_AUTENTICACAO.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA, TipoOperacaoPagto.PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA, TipoOperacaoPagto.PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.TRANSFERENCIA_DE_RECURSOS_OB_LISTA, TipoOperacaoPagto.TRANSFERENCIA_DE_RECURSOS_OB_LISTA.getDescricao()));
                    toReturn.add(new SelectItem(TipoOperacaoPagto.CREDOR_MESMO_BANCO, TipoOperacaoPagto.CREDOR_MESMO_BANCO.getDescricao()));
                }
            } else {
                if (isContasMesmoBanco()) {
                    if (!selecionado.getSubConta().isContaUnica()) {
                        toReturn.add(new SelectItem(TipoOperacaoPagto.CREDOR_MESMO_BANCO_NAO_CONTA_UNICA, TipoOperacaoPagto.CREDOR_MESMO_BANCO_NAO_CONTA_UNICA.getDescricao()));
                    } else {
                        toReturn.add(new SelectItem(TipoOperacaoPagto.CREDOR_MESMO_BANCO, TipoOperacaoPagto.CREDOR_MESMO_BANCO.getDescricao()));
                    }
                } else {
                    if (!selecionado.getSubConta().isContaUnica()) {
                        toReturn.add(new SelectItem(TipoOperacaoPagto.CREDOR_OUTRO_BANCO_NAO_CONTA_UNICA, TipoOperacaoPagto.CREDOR_OUTRO_BANCO_NAO_CONTA_UNICA.getDescricao()));
                    } else {
                        toReturn.add(new SelectItem(TipoOperacaoPagto.CREDOR_OUTRO_BANCO, TipoOperacaoPagto.CREDOR_OUTRO_BANCO.getDescricao()));
                    }
                }
            }
        }
        Collections.sort(toReturn, new Comparator<SelectItem>() {
            @Override
            public int compare(SelectItem o1, SelectItem o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }
        });
        return toReturn;
    }

    public List<SelectItem> getTipoDocumento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getTipoOperacaoPagto() != null) {
            switch (selecionado.getTipoOperacaoPagto()) {
                case PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA:
                case PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA_NAO_CONTA_UNICA:
                    toReturn.add(new SelectItem(TipoDocPagto.FATURA, TipoDocPagto.FATURA.getDescricao()));
                    toReturn.add(new SelectItem(TipoDocPagto.CONVENIO, TipoDocPagto.CONVENIO.getDescricao()));
                    toReturn.add(new SelectItem(TipoDocPagto.GRU, TipoDocPagto.GRU.getDescricao()));
                    break;
                case PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA:
                case PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA_NAO_CONTA_UNICA:
                    toReturn.add(new SelectItem(TipoDocPagto.GPS, TipoDocPagto.GPS.getDescricao()));
                    toReturn.add(new SelectItem(TipoDocPagto.DARF, TipoDocPagto.DARF.getDescricao()));
                    toReturn.add(new SelectItem(TipoDocPagto.DARF_SIMPLES, TipoDocPagto.DARF_SIMPLES.getDescricao()));
                    break;
                case TRANSFERENCIA_DE_RECURSOS_OB_LISTA:
                case TRANSFERENCIA_DE_RECURSOS_OB_LISTA_NAO_CONTA_UNICA:
                    toReturn.add(new SelectItem(TipoDocPagto.GRU, TipoDocPagto.GRU.getDescricao()));
                    break;
                case CREDOR_MESMO_BANCO:
                    toReturn.add(new SelectItem(TipoDocPagto.ORDEMPAGAMENTO, TipoDocPagto.ORDEMPAGAMENTO.getDescricao()));
                    toReturn.add(new SelectItem(TipoDocPagto.DEPJU, TipoDocPagto.DEPJU.getDescricao()));
                    break;
                case CREDOR_MESMO_BANCO_NAO_CONTA_UNICA:
                    toReturn.add(new SelectItem(TipoDocPagto.ORDEMPAGAMENTO, TipoDocPagto.ORDEMPAGAMENTO.getDescricao()));
                    toReturn.add(new SelectItem(TipoDocPagto.DEPJU, TipoDocPagto.DEPJU.getDescricao()));
                    break;
                default:
                    toReturn.add(new SelectItem(TipoDocPagto.ORDEMPAGAMENTO, TipoDocPagto.ORDEMPAGAMENTO.getDescricao()));
                    break;
            }
        }
        return toReturn;
    }

    private boolean validaFiltrosListaReceitaExtra(PagamentoExtra p) {
        Boolean retorno;
        if (p.getContaExtraorcamentaria() != null
            && p.getSubConta() != null
            && p.getFonteDeRecursos() != null) {
            retorno = true;
            FacesUtil.executaJavaScript("dialog.show()");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Não foi possível filtrar. Por favor, verifique se a conta extraorçamentária, conta financeira e fonte de recursos foram informados.");
            retorno = false;
        }
        return retorno;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/despesa-extra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private Boolean isGestorFinanceiro() {
        return pagamentoExtraFacade.getUsuarioSistemaFacade().isGestorFinanceiro(sistemaControlador.getUsuarioCorrente(), sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), sistemaControlador.getDataOperacao());
    }


    public void cancelaEfetivarPgto() {
        if (selecionado.getTipoDocumento().equals(TipoDocPagto.DEBITOEMCONTA)) {
            FacesUtil.executaJavaScript("dialogDeferir.hide()");
            FacesUtil.executaJavaScript("dialogBaixar.show()");
        } else {
            super.cancelar();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
        }
    }

    public void setarContaBancaria() {
        try {
            contaBancariaEntidade = selecionado.getSubConta().getContaBancariaEntidade();
            selecionado.setFonteDeRecursos(null);
        } catch (Exception e) {

        }
    }

    public Boolean mostrarBotaoDeferir() {
        return selecionado.getId() != null
            && StatusPagamento.ABERTO.equals(selecionado.getStatus());
    }

    public Boolean podeIndeferirPagamento() {
        return selecionado.getId() != null && gestorFinanceiro && (selecionado.isDespesaExtraDeferida()
            || selecionado.isDespesaExtraEfetuada());
    }

    public Boolean mostrarBotaoBaixar() {
        if (selecionado.getId() != null) {
            if (selecionado.isDespesaExtraDeferida() || selecionado.isDespesaExtraIndeferida()) {
                return true;
            }
            return gestorFinanceiro && (selecionado.isDespesaExtraIndeferida()
                || selecionado.isDespesaExtraDeferida()
                || selecionado.isDespesaExtraEfetuada());
        }
        return false;
    }

    public Boolean mostrarBotaoEstornarBaixar() {
        return selecionado.getId() != null && gestorFinanceiro && StatusPagamento.PAGO.equals(selecionado.getStatus());
    }

    @Override
    public void baixar() {
        try {
            validarDespesaExtra();
            validarDataConciliacao();
            pagamentoExtraFacade.baixar(selecionado, StatusPagamento.PAGO);
            FacesUtil.addOperacaoRealizada(" A Despesa Extraorçamentária N° <b>" + selecionado.getNumero() + "</b> foi baixada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarDataConciliacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataConciliacao() != null && DataUtil.getAno(selecionado.getDataPagto()).compareTo(DataUtil.getAno(selecionado.getDataConciliacao())) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conciliação está com o exercício diferente da data do registro.");
        }
        ve.lancarException();
    }

    @Override
    public void estornarBaixa() {
        try {
            validarDespesaExtra();
            pagamentoExtraFacade.estornarBaixa(selecionado, StatusPagamento.DEFERIDO);
            FacesUtil.addOperacaoRealizada(" A Despesa Extraorçamentária N° <b>" + selecionado.getNumero() + "</b> foi estornada a baixa com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public Boolean renderizarGuias() {
        return TipoDocPagto.FATURA.equals(selecionado.getTipoDocumento())
            || TipoDocPagto.CONVENIO.equals(selecionado.getTipoDocumento())
            || TipoDocPagto.GPS.equals(selecionado.getTipoDocumento())
            || TipoDocPagto.DARF.equals(selecionado.getTipoDocumento())
            || TipoDocPagto.DARF_SIMPLES.equals(selecionado.getTipoDocumento())
            || TipoDocPagto.GRU.equals(selecionado.getTipoDocumento());
    }

    public Boolean mostrarPainelGuiaFatura() {
        return TipoDocPagto.FATURA.equals(selecionado.getTipoDocumento());
    }

    public Boolean mostrarPainelGuiaConvenio() {
        return TipoDocPagto.CONVENIO.equals(selecionado.getTipoDocumento());
    }

    public Boolean mostrarPainelGuiaGPS() {
        return TipoDocPagto.GPS.equals(selecionado.getTipoDocumento());
    }

    public Boolean mostrarPainelGuiaDARF() {
        return TipoDocPagto.DARF.equals(selecionado.getTipoDocumento());
    }

    public Boolean mostrarPainelGuiaDARFSimples() {
        return TipoDocPagto.DARF_SIMPLES.equals(selecionado.getTipoDocumento());
    }

    public Boolean mostrarPainelGuiaGRU() {
        return TipoDocPagto.GRU.equals(selecionado.getTipoDocumento());
    }

    public void cancelarGuiaPagamento() {
        guiaPagamentoExtra = null;
    }

    public void novaGuiaPagamento() {
        guiaPagamentoExtra = new GuiaPagamentoExtra(selecionado);
        if (!selecionado.getTipoDocumento().isDepju()) {
            selecionado.setIdentificadorDeposito("");
        }
    }


    private void validarCodigoBarrasGuiaFatura() {
        ValidacaoException ve = new ValidacaoException();
        String codigoBarras = guiaPagamentoExtra.getGuiaFatura().getCodigoBarra();
        if (codigoBarras.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo código de barras deve ser informado.");
        }
        ve.lancarException();
        if (codigoBarras.startsWith("8")) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O primeiro dígito informado(8), não pertence a uma guia do tipo " + selecionado.getTipoDocumento().getDescricao() + ".");
        }
        if (codigoBarras.length() >= 3) {
            String tresPrimeirosDigitos = codigoBarras.substring(0, 3);
            if (!pagamentoExtraFacade.getBancoFacade().verificarNumeroBanco(tresPrimeirosDigitos)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Os três primeiros dígitos informados<b>(" + tresPrimeirosDigitos + ")</b>, não fazem referência a um código dos bancos cadastrados no sistema.");
            }
        }
        if (codigoBarras.length() >= 4) {
            String quartoDigito = codigoBarras.substring(3, 4);
            if (!quartoDigito.equals("9")) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O quarto dígito informado(" + quartoDigito + "), não faz referência ao código da moeda(R$) estabelecido pelo Banco Central. Por padrão esse dígito dever ser '9'.");
            }
        }
        if (codigoBarras.length() < 54) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Código de barras não foi preenchido com todos os dígitos necessários.");
        }
        for (GuiaPagamentoExtra guia : selecionado.getGuiaPagamentoExtras()) {
            if (codigoBarras.equals(guia.getGuiaFatura().getCodigoBarra()) && !guia.equals(guiaPagamentoExtra)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Código de barras informado pertence a uma guia adicionada na lista.");
            }
        }
        ve.lancarException();
        String bloco01 = codigoBarras.substring(0, 10).replace(".", "");
        Integer digitoBloco01 = new Integer(codigoBarras.substring(10, 11));

        String bloco02 = codigoBarras.substring(12, 23).replace(".", "");
        Integer digitoBloco02 = new Integer(codigoBarras.substring(23, 24));

        String bloco03 = codigoBarras.substring(25, 36).replace(".", "");
        Integer digitoBloco03 = new Integer(codigoBarras.substring(36, 37));

        if (!digitoBloco01.equals(CarneUtil.modulo10(bloco01))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Primeiro bloco de código apresenta uma inconsistência, verifique o mesmo na guia informada.");
        }
        if (!digitoBloco02.equals(CarneUtil.modulo10(bloco02))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Segundo bloco de código apresenta uma inconsistência, verifique o mesmo na guia informada.");
        }
        if (!digitoBloco03.equals(CarneUtil.modulo10(bloco03))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Terceiro bloco de código apresenta uma inconsistência, verifique o mesmo na guia informada.");
        }
        ve.lancarException();
    }

    public boolean isBancoCaixaEconomica() {
        if (selecionado.getSubConta() != null
            && contaBancariaEntidade.getAgencia() != null) {
            Banco banco = selecionado.getSubConta().getContaBancariaEntidade().getAgencia().getBanco();
            if (pagamentoExtraFacade.getBancoFacade().isBancoCaixaEconomica(banco)) {
                return true;
            }
        }
        return false;
    }

    private void validarCodigoBarrasGuiaConvenio() {
        ValidacaoException ve = new ValidacaoException();
        String codigoBarras = guiaPagamentoExtra.getGuiaConvenio().getCodigoBarra();
        String terceiroDigitoDoCodigo = "";
        if (codigoBarras.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo código de barras deve ser informado.");
        }
        ve.lancarException();
        if (!codigoBarras.startsWith("8")) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O primeiro dígito informado(" + codigoBarras.substring(0, 1) + "), não pertence a uma guia do tipo " + selecionado.getTipoDocumento().getDescricao() + ".");
        }
        if (codigoBarras.length() >= 3) {
            terceiroDigitoDoCodigo = codigoBarras.substring(2, 3);
            if (!"6".equals(terceiroDigitoDoCodigo) && !"7".equals(terceiroDigitoDoCodigo) && !"8".equals(terceiroDigitoDoCodigo)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O terceiro dígito informado(" + terceiroDigitoDoCodigo + "), não faz referência ao padrão de guia do tipo " + selecionado.getTipoDocumento().getDescricao() + ". Por padrão, esse dígito deve ser '6' ou '8'");
            }
        }
        if (codigoBarras.length() < 55) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Código de barras não foi preenchido com todos os dígitos.");
        }
        for (GuiaPagamentoExtra guia : selecionado.getGuiaPagamentoExtras()) {
            if (codigoBarras.equals(guia.getGuiaConvenio().getCodigoBarra()) && !guia.equals(guiaPagamentoExtra)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Código de barras informado pertence a uma guia adicionada na lista.");
            }
        }
        ve.lancarException();
        if (!terceiroDigitoDoCodigo.startsWith("8")) {
            String bloco01 = codigoBarras.substring(0, 11).replace(".", "");
            Integer digitoBloco01 = new Integer(codigoBarras.substring(12, 13));

            String bloco02 = codigoBarras.substring(14, 25).replace(".", "");
            Integer digitoBloco02 = new Integer(codigoBarras.substring(26, 27));

            String bloco03 = codigoBarras.substring(28, 39).replace(".", "");
            Integer digitoBloco03 = new Integer(codigoBarras.substring(40, 41));

            String bloco04 = codigoBarras.substring(42, 53).replace(".", "");
            Integer digitoBloco04 = new Integer(codigoBarras.substring(54, 55));

            if (!digitoBloco01.equals(CarneUtil.modulo10(bloco01))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Primeiro bloco de código apresenta uma inconsistência, verifique o mesmo na guia informada.");
            }
            if (!digitoBloco02.equals(CarneUtil.modulo10(bloco02))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Segundo bloco de código apresenta uma inconsistência, verifique o mesmo na guia informada.");
            }
            if (!digitoBloco03.equals(CarneUtil.modulo10(bloco03))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Terceiro bloco de código apresenta uma inconsistência, verifique o mesmo na guia informada.");
            }
            if (!digitoBloco04.equals(CarneUtil.modulo10(bloco04))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Quarto bloco de código apresenta uma inconsistência, verifique o mesmo na guia informada.");
            }
        }
        ve.lancarException();
    }

    public void getValorGuiaFatura() {
        try {
            validarCodigoBarrasGuiaFatura();
            if (TipoDocPagto.FATURA.equals(selecionado.getTipoDocumento())) {
                String valorCodigoBarras = guiaPagamentoExtra.getGuiaFatura().getCodigoBarra().substring(44, 54);
                valorCodigoBarras = valorCodigoBarras.substring(0, valorCodigoBarras.length() - 2) + "." + valorCodigoBarras.substring(valorCodigoBarras.length() - 2, valorCodigoBarras.length());
                guiaPagamentoExtra.getGuiaFatura().setValorNominal(new BigDecimal(valorCodigoBarras));
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void getValorGuiaConvenio() {
        try {
            validarCodigoBarrasGuiaConvenio();
            if (TipoDocPagto.CONVENIO.equals(selecionado.getTipoDocumento())) {
                if (!guiaPagamentoExtra.getGuiaConvenio().getCodigoBarra().trim().isEmpty()) {
                    String valorCodigoBarras = guiaPagamentoExtra.getGuiaConvenio().getCodigoBarra().substring(4, 11);
                    valorCodigoBarras += guiaPagamentoExtra.getGuiaConvenio().getCodigoBarra().substring(14, 18);
                    valorCodigoBarras = valorCodigoBarras.substring(0, valorCodigoBarras.length() - 2) + "." + valorCodigoBarras.substring(valorCodigoBarras.length() - 2, valorCodigoBarras.length());
                    guiaPagamentoExtra.getGuiaConvenio().setValor(new BigDecimal(valorCodigoBarras));
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarGuiaGRU() {
        try {
            validarGuiaGRU();
            adicionarGuiaDespesaExtra();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarGuiaGRU() {
        ValidacaoException ve = new ValidacaoException();
        if (guiaPagamentoExtra.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Pessoa deve ser informado.");
        }
        ve.lancarException();
        Util.validarCampos(guiaPagamentoExtra.getGuiaGRU());
    }

    public void adicionarGuiaFatura() {
        try {
            Util.validarCampos(guiaPagamentoExtra.getGuiaFatura());
            validarCodigoBarrasGuiaFatura();
            adicionarGuiaDespesaExtra();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarGuiaConvenio() {
        try {
            Util.validarCampos(guiaPagamentoExtra.getGuiaConvenio());
            validarCodigoBarrasGuiaConvenio();
            adicionarGuiaDespesaExtra();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarGuiaGPS() {
        try {
            Util.validarCampos(guiaPagamentoExtra);
            Util.validarCampos(guiaPagamentoExtra.getGuiaGPS());
            adicionarGuiaDespesaExtra();
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:pessoaGps_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarGuiaDARF() {
        try {
            Util.validarCampos(guiaPagamentoExtra);
            Util.validarCampos(guiaPagamentoExtra.getGuiaDARF());
            adicionarGuiaDespesaExtra();
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:pessoaDarf_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarGuiaDARFSimples() {
        try {
            Util.validarCampos(guiaPagamentoExtra);
            Util.validarCampos(guiaPagamentoExtra.getGuiaDARFSimples());
            adicionarGuiaDespesaExtra();
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabView:pessoaDarfSimples_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void adicionarGuiaDespesaExtra() {
        guiaPagamentoExtra.setPagamentoExtra(selecionado);
        Util.adicionarObjetoEmLista(selecionado.getGuiaPagamentoExtras(), guiaPagamentoExtra);
        cancelarGuiaPagamento();
    }

    public void editarGuiaPagamento(GuiaPagamentoExtra guia) {
        guiaPagamentoExtra = (GuiaPagamentoExtra) Util.clonarObjeto(guia);
    }

    public void removerGuiaPagamento(GuiaPagamentoExtra guia) {
        selecionado.getGuiaPagamentoExtras().remove(guia);
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public void setaNullContaCorrente() {
        selecionado.setContaCorrenteBancaria(null);
        selecionado.setFornecedor(null);
    }

    public void removeContaBancPessoa() {
        selecionado.setContaCorrenteBancaria(null);
    }

    public void setarFinalidade(ActionEvent evento) {
        selecionado.setFinalidadePagamento((FinalidadePagamento) evento.getComponent().getAttributes().get("objeto"));
    }

    public GuiaPagamentoExtra getGuiaPagamentoExtra() {
        return guiaPagamentoExtra;
    }

    public void setGuiaPagamentoExtra(GuiaPagamentoExtra guiaPagamentoExtra) {
        this.guiaPagamentoExtra = guiaPagamentoExtra;
    }

    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<SelectItem> getTiposIdentificacaoGuia() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        if (guiaPagamentoExtra != null) {
            IGuiaArquivoOBN600 guia = guiaPagamentoExtra;
            if (guia.getPessoa() != null) {
                if (guia.getPessoa() instanceof PessoaFisica) {
                    retorno.add(new SelectItem(TipoIdentificacaoGuia.CPF, TipoIdentificacaoGuia.CPF.getDescricao()));
                    if (!isBancoCaixaEconomica()) {
                        retorno.add(new SelectItem(TipoIdentificacaoGuia.PIS_PASEP, TipoIdentificacaoGuia.PIS_PASEP.getDescricao()));
                        retorno.add(new SelectItem(TipoIdentificacaoGuia.CEI, TipoIdentificacaoGuia.CEI.getDescricao()));
                    }
                } else {
                    retorno.add(new SelectItem(TipoIdentificacaoGuia.CNPJ, TipoIdentificacaoGuia.CNPJ.getDescricao()));
                    if (!isBancoCaixaEconomica()) {
                        retorno.add(new SelectItem(TipoIdentificacaoGuia.CEI, TipoIdentificacaoGuia.CEI.getDescricao()));
                    }
                }
            }
        }
        return retorno;
    }

    public void definirNullTipoIdentificacaoGuia() {
        if (guiaPagamentoExtra != null) {
            guiaPagamentoExtra.setTipoIdentificacaoGuia(null);
            guiaPagamentoExtra.setCodigoIdentificacao(null);
        }
    }

    public void atribuirCodigoIdentificacao() {
        if (guiaPagamentoExtra != null) {

            if (guiaPagamentoExtra.getPessoa() != null) {
                if (guiaPagamentoExtra.getTipoIdentificacaoGuia().equals(TipoIdentificacaoGuia.CPF) || guiaPagamentoExtra.getTipoIdentificacaoGuia().equals(TipoIdentificacaoGuia.CNPJ)) {
                    guiaPagamentoExtra.setCodigoIdentificacao(guiaPagamentoExtra.getPessoa().getCpf_Cnpj());
                }
                if (guiaPagamentoExtra.getTipoIdentificacaoGuia().equals(TipoIdentificacaoGuia.PIS_PASEP)) {
                    if (guiaPagamentoExtra.getPessoa() instanceof PessoaFisica) {
                        try {
                            guiaPagamentoExtra.setPessoa(pagamentoExtraFacade.getPessoaFisicaFacade().recuperar(guiaPagamentoExtra.getPessoa().getId()));
                            guiaPagamentoExtra.setCodigoIdentificacao(((PessoaFisica) guiaPagamentoExtra.getPessoa()).getCarteiraDeTrabalho().getPisPasep());
                        } catch (Exception e) {
                            guiaPagamentoExtra.setCodigoIdentificacao("Pessoa não possui PIS/PASEP em seu cadastro");
                        }
                    }
                }
                if (guiaPagamentoExtra.getTipoIdentificacaoGuia().equals(TipoIdentificacaoGuia.CEI)) {
                    guiaPagamentoExtra.setCodigoIdentificacao("");
                }
            }
        }
    }

    public void prepararGuiaParaImprimir(GuiaPagamentoExtra guia) {
        guia.getPagamentoExtra().setSubConta(pagamentoExtraFacade.getSubContaFacade().recuperar(guia.getPagamentoExtra().getSubConta().getId()));
        guiaPagamentoItem = new GuiaPagamentoItem(guia, hierarquiaOrganizacionalFacade);
    }

    public void imprimirGuiaPagamento(GuiaPagamentoItem guiaPagamento) throws JRException, IOException {
        AbstractReport abstractReport = AbstractReport.getAbstractReport();
        HashMap parameters = new HashMap();
        abstractReport.setGeraNoDialog(true);
        String nomeArquivo = "RelatorioGuiaPagamentoAutenticada.jasper";
        parameters.put("IMAGEM", abstractReport.getCaminhoImagem());
        parameters.put("NOME_RELATORIO", headerDialogGuia());
        parameters.put("TIPO_DOCUMENTO", tipoDeDocumentoGuiaExtra());
        parameters.put("TITULO_GUIA", tituloGuiaPagamentoExtra());
        parameters.put("MODULO", "Execução Orçamentária");
        parameters.put("PESSOA_CODBARRA", renderizarGuiaComCodigoBarras() ? "Código de Barras " : "Pessoa ");
        parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        List<GuiaPagamentoItem> listaGuiaPagamentos = new ArrayList<>();
        listaGuiaPagamentos.add(guiaPagamento);
        JRBeanCollectionDataSource jrb = new JRBeanCollectionDataSource(listaGuiaPagamentos);
        abstractReport.gerarRelatorioComDadosEmCollection(nomeArquivo, parameters, jrb);
    }

    private String tipoDeDocumentoGuiaExtra() {
        return "Tipo de Documento: " + selecionado.getTipoDocumento().getDescricao();
    }

    private String tituloGuiaPagamentoExtra() {
        return "Nota de Despesa Extra-Orçamentária " + selecionado.getNumero()
            + ", de " + DataUtil.getDataFormatada(selecionado.getDataPagto());
    }

    public void setarNullGuiaPagamentoItem() {
        guiaPagamentoItem = null;
    }

    public Boolean podeImprimirGuia(GuiaPagamentoExtra guiaPag) {
        return guiaPag.getNumeroAutenticacao() != null;
    }

    public String headerDialogGuia() {
        if (TipoDocPagto.CONVENIO.equals(selecionado.getTipoDocumento())
            || TipoDocPagto.FATURA.equals(selecionado.getTipoDocumento())) {
            return "Pagamento de guia com código de barras";
        } else {
            return "Pagamento de guia sem código de barras";
        }
    }

    public boolean renderizarGuiaComCodigoBarras() {
        return TipoDocPagto.CONVENIO.equals(selecionado.getTipoDocumento())
            || TipoDocPagto.FATURA.equals(selecionado.getTipoDocumento());
    }

    public boolean renderizarDarfDarfSimples() {
        return renderizarCamposGuiaDarf() || renderizarCamposGuiaDarfSimples();
    }

    public boolean renderizarGpsDarfDarfSimples() {
        return renderizarCamposGuiaGps() || renderizarCamposGuiaDarf() || renderizarCamposGuiaDarfSimples();
    }


    public boolean renderizarCamposGuiaGps() {
        return TipoDocPagto.GPS.equals(selecionado.getTipoDocumento());
    }

    public boolean renderizarCamposGuiaDarf() {
        return TipoDocPagto.DARF.equals(selecionado.getTipoDocumento());
    }

    public boolean renderizarCamposGuiaDarfSimples() {
        return TipoDocPagto.DARF_SIMPLES.equals(selecionado.getTipoDocumento());
    }

    public boolean renderizarGuiaNaoConvenio() {
        return !TipoDocPagto.CONVENIO.equals(selecionado.getTipoDocumento());
    }

    public GuiaPagamentoItem getGuiaPagamentoItem() {
        return guiaPagamentoItem;
    }

    public void setGuiaPagamentoItem(GuiaPagamentoItem guiaPagamentoItem) {
        this.guiaPagamentoItem = guiaPagamentoItem;
    }

    public ParametroEvento getParametroEvento() {
        return pagamentoExtraFacade.getParametroEvento(selecionado);
    }

    public PagamentoExtraFacade getFacade() {
        return pagamentoExtraFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return pagamentoExtraFacade;
    }

    @Override
    public void cleanScoped() {
        super.cleanScoped();
        indiceAba = null;
        listaReceitaExtras = null;
        listaRecSelecionadas = null;
        contaBancariaEntidade = null;
        guiaPagamentoExtra = null;
        guiaPagamentoItem = null;
        gestorFinanceiro = null;
    }

    public void redirecionaSemLimpar() {
        FacesUtil.navegaEmbora(selecionado, ((CRUD) this).getCaminhoPadrao());
    }


    private void validarCodigoBarrasGuiaGRU() {
        ValidacaoException ve = new ValidacaoException();

        String codigoBarras = guiaPagamentoExtra.getGuiaGRU().getCodigoBarra();
        if (codigoBarras.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo código de barras deve ser informado.");
        }
        ve.lancarException();
        if (!codigoBarras.startsWith("8")) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O primeiro dígito informado(" + codigoBarras.substring(0, 1) + "), não pertence a uma guia do tipo " + selecionado.getTipoDocumento().getDescricao() + ".");
        }
        if (codigoBarras.length() < 55) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Código de barras não foi preenchido com todos os dígitos.");
        }
        for (GuiaPagamentoExtra guia : selecionado.getGuiaPagamentoExtras()) {
            if (codigoBarras.equals(guia.getGuiaGRU().getCodigoBarra()) && !guia.equals(guiaPagamentoExtra)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Código de barras informado pertence a uma guia adicionada na lista.");
            }
        }
        ve.lancarException();
    }

    public void getValorGuiaGRU() {
        try {
            validarCodigoBarrasGuiaGRU();
            if (TipoDocPagto.GRU.equals(selecionado.getTipoDocumento())) {
                String valorCodigoBarras = guiaPagamentoExtra.getGuiaGRU().getCodigoBarra().substring(4, 11);
                valorCodigoBarras += guiaPagamentoExtra.getGuiaGRU().getCodigoBarra().substring(14, 18);
                valorCodigoBarras = valorCodigoBarras.substring(0, valorCodigoBarras.length() - 2) + "." + valorCodigoBarras.substring(valorCodigoBarras.length() - 2, valorCodigoBarras.length());
                guiaPagamentoExtra.getGuiaGRU().setValorPrincipal(new BigDecimal(valorCodigoBarras));

            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public Date getDataInicialFiltroReceita() {
        return dataInicialFiltroReceita;
    }

    public void setDataInicialFiltroReceita(Date dataInicialFiltroReceita) {
        this.dataInicialFiltroReceita = dataInicialFiltroReceita;
    }

    public Date getDataFinalFiltroReceita() {
        return dataFinalFiltroReceita;
    }

    public void setDataFinalFiltroReceita(Date dataFinalFiltroReceita) {
        this.dataFinalFiltroReceita = dataFinalFiltroReceita;
    }

    public void limparDatasFiltros() {
        dataInicialFiltroReceita = null;
        dataFinalFiltroReceita = null;
    }
}
