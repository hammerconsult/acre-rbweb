package br.com.webpublico.controle;

/**
 * Created by Fabio on 06/08/2018.
 */

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "processoCreditoContaCorrenteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "lista-processo-de-credito-em-conta-corrente",
        pattern = "/tributario/processo-de-credito-em-conta-corrente/listar/",
        viewId = "/faces/tributario/contacorrente/processocreditocontacorrente/lista.xhtml"),
    @URLMapping(id = "novo-processo-de-credito-em-conta-corrente",
        pattern = "/tributario/processo-de-credito-em-conta-corrente/novo/",
        viewId = "/faces/tributario/contacorrente/processocreditocontacorrente/edita.xhtml"),
    @URLMapping(id = "ver-processo-de-credito-em-conta-corrente",
        pattern = "/tributario/processo-de-credito-em-conta-corrente/ver/#{processoCreditoContaCorrenteControlador.id}/",
        viewId = "/faces/tributario/contacorrente/processocreditocontacorrente/visualizar.xhtml"),
    @URLMapping(id = "editar-processo-de-credito-em-conta-corrente",
        pattern = "/tributario/processo-de-credito-em-conta-corrente/editar/#{processoCreditoContaCorrenteControlador.id}/",
        viewId = "/faces/tributario/contacorrente/processocreditocontacorrente/edita.xhtml"),
})
public class ProcessoCreditoContaCorrenteControlador extends PrettyControlador<CreditoContaCorrente> implements Serializable, CRUD {

    @EJB
    private ProcessoCreditoContaCorrenteFacade processoCreditoContaCorrenteFacade;
    @EJB
    private CompensacaoFacade compensacaoFacade;
    @EJB
    private RestituicaoFacade restituicaoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private DAMFacade damFacade;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterDivida;
    private ConverterAutoComplete converterExercicio;
    //FILTROS PARA CONSULTA
    private TipoCadastroTributario filtroTipoCadastro;
    private Pessoa filtroPessoa;
    private Cadastro filtroCadastro;
    private Divida filtroDivida;
    private Exercicio filtroExercicioInicio;
    private Exercicio filtroExercicioFinal;
    private boolean dividaDoExercicio;
    private boolean dividaAtiva;
    private boolean dividaAtivaAzuijada;
    private Exercicio filtroAnoDAM;
    private Long filtroNumeroDAM;
    private ConverterAutoComplete converterCadastroImobiliario;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private LoteFacade loteFacade;
    private List<ResultadoParcela> resultadoConsulta;
    private SituacaoParcela situacaoParcela;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
    private ResultadoParcela[] resultados;
    private SituacaoCadastralPessoa situacaoCadastroPessoa;

    public ProcessoCreditoContaCorrenteControlador() {
        super(CreditoContaCorrente.class);
        resultadoConsulta = Lists.newArrayList();
    }

    public List<ResultadoParcela> getResultadoConsulta() {
        return resultadoConsulta;
    }

    public void setResultadoConsulta(List<ResultadoParcela> resultadoConsulta) {
        this.resultadoConsulta = resultadoConsulta;
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public ResultadoParcela[] getResultados() {
        return resultados;
    }

    public void setResultados(ResultadoParcela[] resultados) {
        this.resultados = resultados;
    }

    public Date getVencimentoFinal() {
        return vencimentoFinal;
    }

    public void setVencimentoFinal(Date vencimentoFinal) {
        this.vencimentoFinal = vencimentoFinal;
    }

    public Date getVencimentoInicial() {
        return vencimentoInicial;
    }

    public void setVencimentoInicial(Date vencimentoInicial) {
        this.vencimentoInicial = vencimentoInicial;
    }

    public boolean isDividaAtiva() {
        return dividaAtiva;
    }

    public void setDividaAtiva(boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public boolean isDividaAtivaAzuijada() {
        return dividaAtivaAzuijada;
    }

    public void setDividaAtivaAzuijada(boolean dividaAtivaAzuijada) {
        this.dividaAtivaAzuijada = dividaAtivaAzuijada;
    }

    public boolean isDividaDoExercicio() {
        return dividaDoExercicio;
    }

    public void setDividaDoExercicio(boolean dividaDoExercicio) {
        this.dividaDoExercicio = dividaDoExercicio;
    }

    public Exercicio getFiltroAnoDAM() {
        return filtroAnoDAM;
    }

    public void setFiltroAnoDAM(Exercicio filtroAnoDAM) {
        this.filtroAnoDAM = filtroAnoDAM;
    }

    public Long getFiltroNumeroDAM() {
        return filtroNumeroDAM;
    }

    public void setFiltroNumeroDAM(Long filtroNumeroDAM) {
        this.filtroNumeroDAM = filtroNumeroDAM;
    }

    public TipoCadastroTributario getFiltroTipoCadastro() {
        return filtroTipoCadastro;
    }

    public void setFiltroTipoCadastro(TipoCadastroTributario filtroTipoCadastro) {
        this.filtroTipoCadastro = filtroTipoCadastro;
    }

    public Pessoa getFiltroPessoa() {
        return filtroPessoa;
    }

    public void setFiltroPessoa(Pessoa filtroPessoa) {
        this.filtroPessoa = filtroPessoa;
    }

    public Cadastro getFiltroCadastro() {
        return filtroCadastro;
    }

    public void setFiltroCadastro(Cadastro filtroCadastro) {
        this.filtroCadastro = filtroCadastro;
    }

    public Divida getFiltroDivida() {
        return filtroDivida;
    }

    public void setFiltroDivida(Divida filtroDivida) {
        this.filtroDivida = filtroDivida;
    }

    public Exercicio getFiltroExercicioFinal() {
        return filtroExercicioFinal;
    }

    public void setFiltroExercicioFinal(Exercicio filtroExercicioFinal) {
        this.filtroExercicioFinal = filtroExercicioFinal;
    }

    public Exercicio getFiltroExercicioInicio() {
        return filtroExercicioInicio;
    }

    public void setFiltroExercicioInicio(Exercicio filtroExercicioInicio) {
        this.filtroExercicioInicio = filtroExercicioInicio;
    }

    public SituacaoCadastralPessoa getSituacaoCadastroPessoa() {
        return situacaoCadastroPessoa;
    }

    public void setSituacaoCadastroPessoa(SituacaoCadastralPessoa situacaoCadastroPessoa) {
        this.situacaoCadastroPessoa = situacaoCadastroPessoa;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, processoCreditoContaCorrenteFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, processoCreditoContaCorrenteFacade.getPessoaFacade());
        }
        return converterDivida;
    }

    public List<Divida> completarDivida(String parte) {
        return processoCreditoContaCorrenteFacade.getDividaFacade().listaFiltrandoDividas(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, processoCreditoContaCorrenteFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    @Override
    public AbstractFacade getFacede() {
        return processoCreditoContaCorrenteFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/processo-de-credito-em-conta-corrente/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void inicializarProcesso() {
        operacao = Operacoes.NOVO;
        try {
            selecionado.setUsuarioIncluiu(processoCreditoContaCorrenteFacade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setSituacao(SituacaoProcessoDebito.EM_ABERTO);
            selecionado.setLancamento(processoCreditoContaCorrenteFacade.getSistemaFacade().getDataOperacao());
            selecionado.setExercicio(processoCreditoContaCorrenteFacade.getSistemaFacade().getExercicioCorrente());
            selecionado.setCodigo(processoCreditoContaCorrenteFacade.recuperarProximoCodigoPorExercicio(selecionado.getExercicio()));
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro inesperado durante o processo! " + ex.getMessage());
        }
    }

    @URLAction(mappingId = "novo-processo-de-credito-em-conta-corrente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        inicializarProcesso();
    }


    @URLAction(mappingId = "ver-processo-de-credito-em-conta-corrente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verCreditoContaCorrente() {
        super.ver();
        carregarInformacoesProcesso();
        recuperarParcelaDoProcesso();
    }

    private void recuperarParcelaDoProcesso() {
        for (ItemCreditoContaCorrente item : selecionado.getItens()) {
            item.setParcela(processoCreditoContaCorrenteFacade.recuperarParcelaValorDivida(item.getParcela().getId()));
        }
    }

    @Override
    @URLAction(mappingId = "editar-processo-de-credito-em-conta-corrente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        carregarInformacoesProcesso();
        recuperarParcelaDoProcesso();
    }

    public MoneyConverter getMoneyConverter() {
        return new MoneyConverter();
    }

    private void carregarInformacoesProcesso() {
        selecionado.setDataEstorno(processoCreditoContaCorrenteFacade.getSistemaFacade().getDataOperacao());
        for (ItemCreditoContaCorrente item : selecionado.getItens()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, item.getParcela().getId());
            consultaParcela.executaConsulta();
            processoCreditoContaCorrenteFacade.buscarValorPagoNaArrecadacao(consultaParcela.getResultados(), ContaCorrenteTributaria.Origem.ARRECADACAO);
            item.setResultadoParcela(consultaParcela.getResultados().get(0));
        }
    }

    @Override
    public void salvar() {
        try {
            validarProcesso();

            if (Operacoes.NOVO.equals(operacao)) {
                processoCreditoContaCorrenteFacade.salvarNovo(selecionado);
            } else {
                processoCreditoContaCorrenteFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Registro Salvo com sucesso!");
            navegarParaVisualizar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    public void validarProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCodigo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Código deve ser preenchido!");
        } else if (selecionado.getCodigo() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código deve ser maio que zero!");
        } else if (processoCreditoContaCorrenteFacade.verificarCodigoEmUso(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Código informado em uso! Foi gerado um novo código.");
        }
        if (selecionado.getLancamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data de Lançamento deve ser preenchida!");
        }
        if (selecionado.getMotivo() == null || selecionado.getMotivo().trim().length() <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O Motivo ou Fundamentação Legal deve ser preenchido!");
        }
        if (selecionado.getItens() == null || selecionado.getItens().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo deve conter ao menos uma parcela!");
        } else {
            for (ItemCreditoContaCorrente itemCreditoContaCorrente : selecionado.getItens()) {
                if (itemCreditoContaCorrente.getDiferencaContaCorrente().compareTo(BigDecimal.ZERO) <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Informe um valor maior que zero no campo diferença!");
                    break;
                }
            }
        }
        if (selecionado.getUsuarioIncluiu() == null || selecionado.getUsuarioIncluiu().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O usuário que incluiu o processo deve ser informado!");
        }
        if (selecionado.getSituacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Situação do Processo deve ser preenchida!");
        } else if (selecionado.getId() != null && selecionado.getSituacao() != SituacaoProcessoDebito.EM_ABERTO) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo selecionado encontra-se " + selecionado.getSituacao().getDescricao() + ". Somente processos EM ABERTO podem ser alterados.");
        }
        if (selecionado.getTipoDiferencaContaCorrente() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Tipo de Diferença deve ser informado!");
        }
        if (selecionado.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Contribuinte deve ser informado!");
        } else {
            if (selecionado.getPessoa() instanceof PessoaFisica) {
                if (!Util.valida_CpfCnpj(((PessoaFisica) selecionado.getPessoa()).getCpf())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Contribuinte selecionado não possui um CPF válido!");
                } else if (processoCreditoContaCorrenteFacade.getPessoaFacade().hasOutraPessoaComMesmoCpf((PessoaFisica) selecionado.getPessoa(), true)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O CPF do contribuinte selecionado pertence a mais de uma pessoa física!");
                }
            } else {
                if (!Util.valida_CpfCnpj(((PessoaJuridica) selecionado.getPessoa()).getCnpj())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Contribuinte selecionado não possui um CNPJ válido!");
                } else if (processoCreditoContaCorrenteFacade.getPessoaFacade().hasOutraPessoaComMesmoCnpj((PessoaJuridica) selecionado.getPessoa(), true)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O CNPJ do contribuinte selecionado pertence a mais de uma pessoa jurídica!");
                }
            }
        }

        ve.lancarException();
    }

    private void navegarParaVisualizar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void encerrarProcesso() {
        try {
            ResultadoValidacao resultado = processoCreditoContaCorrenteFacade.encerrarProcesso(selecionado);
            if (resultado.isValidado()) {
                FacesUtil.addOperacaoRealizada("Processo de Débitos processado com sucesso!");
            } else {
                FacesUtil.printAllMessages(resultado.getMensagens());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao processar: " + e.getMessage());
        }
    }

    public void limparEstorno() {
        selecionado.setMotivoEstorno(null);
    }

    public void estornarProcesso() {
        try {
            validarEstorno();
            processoCreditoContaCorrenteFacade.estornarProcesso(selecionado, SituacaoProcessoDebito.ESTORNADO);
            FacesUtil.addOperacaoRealizada("Processo estornado com sucesso!");
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    private void validarEstorno() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getMotivoEstorno() == null || selecionado.getMotivoEstorno().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o motivo do estorno");
        }
        if (selecionado.getDataEstorno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data do estorno");
        }
        for (ItemCreditoContaCorrente itemCreditoContaCorrente : selecionado.getItens()) {
            Compensacao compensacao = compensacaoFacade.buscarCompensacaoFinalizadaParaItemCreditoContaCorrente(itemCreditoContaCorrente);
            if (compensacao != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Antes de fazer o estorno do lançamento do crédito, deve ser estornado o processo de compensação de número " + compensacao.getCodigo());
            }
        }
        for (ItemCreditoContaCorrente itemCreditoContaCorrente : selecionado.getItens()) {
            Restituicao restituicao = restituicaoFacade.buscarRestituicaoFinalizadaParaItemCreditoContaCorrente(itemCreditoContaCorrente);
            if (restituicao != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Antes de fazer o estorno do lançamento do crédito, deve ser estornado o processo de compensação de número " + restituicao.getCodigo());
            }
        }
        ve.lancarException();
    }


    @Override
    public void excluir() {
        if (selecionado.getSituacao().ESTORNADO.equals(selecionado.getSituacao()) || (selecionado.getSituacao().FINALIZADO.equals(selecionado.getSituacao()))) {
            FacesUtil.addError("O registro selecionado está finalizado e não pode ser excluido!", "");
        } else {
            try {
                getFacede().remover(selecionado);
                FacesUtil.addInfo("Registro excluído com sucesso!", "");
                cancelar();
            } catch (Exception e) {
                FacesUtil.addError("Não foi possível realizar a exclusão!", "");
            }
        }
    }

    private void validarParcelasProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (resultados.length == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe uma parcela para adicionar ao processo.");
        } else {
            for (ResultadoParcela resultado : resultados) {
                DAM damPago = damFacade.buscarDamPagoDaParcela(resultado.getIdParcela());
                if (damPago == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi localizado o DAM pago referente à parcela " + resultado.getParcela() + " de " + resultado.getDivida() + " selecionada!");
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarParcelas() {
        try {
            validarParcelasProcesso();
            for (ResultadoParcela resultadoParcela : resultados) {
                if (processoCreditoContaCorrenteFacade.validarItemProcesso(selecionado, resultadoParcela)) {
                    processoCreditoContaCorrenteFacade.adicionarItem(resultadoParcela, selecionado, resultadoConsulta);
                } else {
                    FacesUtil.addError("Não foi possível continuar!", "A parcela selecionada já foi adicionada ao processo!");
                }
            }
            FacesUtil.addOperacaoRealizada("Parcelas adicionadas no Processo de Débitos!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerItem(ItemCreditoContaCorrente item) {
        selecionado.getItens().remove(item);
    }

    public void pesquisar() {
        try {
            validarCamposPreenchidos();
            resultadoConsulta.clear();
            ConsultaParcela consulta = new ConsultaParcela();
            adicionarParametro(consulta);
            consulta.executaConsulta();
            resultadoConsulta.addAll(consulta.getResultados());
            removerParcelasJaAdicionadasAoProcesso();
            processoCreditoContaCorrenteFacade.buscarValorPagoNaArrecadacao(resultadoConsulta, ContaCorrenteTributaria.Origem.PROCESSO);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        Collections.sort(resultadoConsulta, ResultadoParcela.getComparatorByValuePadrao());
        try {
            if (resultadoConsulta.isEmpty()) {
                FacesUtil.addError("A pesquisa não encontrou nenhuma Parcela!", "");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Impossível continuar", e.getMessage());
        }
    }

    private void removerParcelasJaAdicionadasAoProcesso() {
        List<ResultadoParcela> adicionadas = Lists.newArrayList();
        for (ItemCreditoContaCorrente itemCreditoContaCorrente : selecionado.getItens()) {
            adicionadas.add(itemCreditoContaCorrente.getResultadoParcela());
        }
        for (ResultadoParcela adicionada : adicionadas) {
            if (resultadoConsulta.contains(adicionada)) {
                resultadoConsulta.remove(adicionada);
            }
        }
    }

    public void adicionarParametro(ConsultaParcela consulta) {
        if ((TipoCadastroTributario.IMOBILIARIO.equals(filtroTipoCadastro) ||
            TipoCadastroTributario.ECONOMICO.equals(filtroTipoCadastro) ||
            TipoCadastroTributario.RURAL.equals(filtroTipoCadastro)) &&
            filtroCadastro != null && filtroCadastro.getId() != null) {
            consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, filtroCadastro.getId());
        } else if (TipoCadastroTributario.PESSOA.equals(filtroTipoCadastro) &&
            filtroPessoa != null && filtroPessoa.getId() != null) {
            consulta.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, filtroPessoa.getId());
        }

        if (filtroExercicioInicio != null && filtroExercicioFinal != null) {
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, filtroExercicioInicio.getAno());
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, filtroExercicioFinal.getAno());
        }

        if (filtroDivida != null) {
            consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, filtroDivida.getId());
        }

        if (dividaDoExercicio && !dividaAtiva && !dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (dividaDoExercicio && dividaAtiva && !dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (dividaDoExercicio && !dividaAtiva && dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (!dividaDoExercicio && dividaAtiva && dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
        } else if (!dividaDoExercicio && dividaAtiva && !dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
        } else if (!dividaDoExercicio && !dividaAtiva && dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
        }

        if (vencimentoInicial != null && vencimentoFinal != null) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, vencimentoFinal);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, vencimentoInicial);
        }
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, Lists.newArrayList(SituacaoParcela.PAGO,
            SituacaoParcela.PAGO_SUBVENCAO));

        if (!selecionado.getItens().isEmpty()) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.NOT_IN, buscarIdsParcelasAdicionadas(selecionado.getItens()));
        }
        String whereItemCredito = " and VW.PARCELA_ID not in (select iccc.parcela_id from ItemCreditoContaCorrente iccc " +
            " inner join CreditoContaCorrente ccc on iccc.creditoContaCorrente_id = ccc.id  " +
            " where ccc.situacao in ('EM_ABERTO','FINALIZADO') ";
        if (selecionado.getId() != null) {
            whereItemCredito += " and ccc.id <> " + selecionado.getId();
        }
        whereItemCredito += " )";
        consulta.addComplementoDoWhere(whereItemCredito);

        if (filtroAnoDAM != null || filtroNumeroDAM != null) {
            String whereDAM = " and vw.parcela_id in (select id.parcela_id " +
                "   from dam d " +
                "  inner join itemdam id on id.dam_id = d.id " +
                "  inner join exercicio e on e.id = d.exercicio_id ";
            String juncao = " where ";
            if (filtroAnoDAM != null) {
                whereDAM += juncao + " e.ano = " + filtroAnoDAM;
                juncao = " and ";
            }
            if (filtroNumeroDAM != null) {
                whereDAM += juncao + " d.numero = " + filtroNumeroDAM;
                juncao = " and ";
            }
            whereDAM += ") ";
            consulta.addComplementoDoWhere(whereDAM);
        }
    }

    private List<Long> buscarIdsParcelasAdicionadas(List<ItemCreditoContaCorrente> itens) {
        List<Long> listaIdsParcelas = Lists.newArrayList();
        for (ItemCreditoContaCorrente itemCreditoContaCorrente : itens) {
            listaIdsParcelas.add(itemCreditoContaCorrente.getParcela().getId());
        }
        return listaIdsParcelas;
    }

    public void inicializarFiltros() {
        filtroTipoCadastro = null;
        filtroCadastro = null;
        filtroPessoa = null;
        resultadoConsulta = Lists.newArrayList();
        filtroDivida = null;
        filtroExercicioFinal = null;
        filtroExercicioInicio = null;
        dividaAtiva = false;
        dividaAtivaAzuijada = false;
        dividaDoExercicio = false;
    }

    private void validarCamposPreenchidos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroTipoCadastro == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de cadastro.");
        }
        if (filtroCadastro == null && filtroPessoa == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o cadastro.");
        }
        if (filtroExercicioInicio != null && filtroExercicioFinal != null) {
            if (filtroExercicioFinal.getAno().compareTo(filtroExercicioInicio.getAno()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício final não pode ser posterior ao exercício inicial.");
            }
        }
        if (vencimentoInicial != null && vencimentoFinal != null) {
            if (vencimentoInicial.compareTo(vencimentoFinal) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Final não pode ser maior que a Data de Vencimento Inicial.");
            }
        }
        ve.lancarException();
    }


    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }


    public boolean habilitarBotaoSalvar() {
        inicializarFiltros();
        return selecionado.getSituacao() == null || SituacaoProcessoDebito.EM_ABERTO.equals(selecionado.getSituacao());
    }

    public boolean naoProcessado() {
        return selecionado == null || selecionado.getSituacao() != null && SituacaoProcessoDebito.EM_ABERTO.equals(selecionado.getSituacao()) && selecionado.getId() != null;
    }

    public boolean isHabilitarBotaoEstornar() {
        return selecionado.getSituacao() != null
            && SituacaoProcessoDebito.FINALIZADO.equals(selecionado.getSituacao())
            && selecionado.getId() != null;
    }

    public void limparFiltroCadastro() {
        selecionado.setPessoa(null);
    }

    public String retornarSituacaoDaDivida(ResultadoParcela resultadoParcela) {
        if (resultadoParcela == null) {
            return "-";
        }
        if (resultadoParcela.getDividaIsDividaAtiva()) {
            return "Dívida Ativa";
        }
        if (resultadoParcela.getDividaAtivaAjuizada()) {
            return "Dívida Ativa Ajuizada";
        }
        return "Do Exercício";
    }


    public String recuperarUltimaSituacao(ParcelaValorDivida parcela) {
        SituacaoParcelaValorDivida ultimaSituacao = processoCreditoContaCorrenteFacade.getConsultaDebitoFacade().getUltimaSituacao(parcela);
        return ultimaSituacao.getSituacaoParcela().getDescricao();
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroImobiliarioFacade);
        }
        return converterCadastroImobiliario;
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, processoCreditoContaCorrenteFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        return processoCreditoContaCorrenteFacade.getAtoLegalFacade().listaFiltrando(parte.trim(), "nome");
    }

    public void uploadArquivo(FileUploadEvent file) {
        try {
            Arquivo arq = new Arquivo();
            arq.setNome(file.getFile().getFileName());
            arq.setMimeType(arquivoFacade.getMimeType(file.getFile().getFileName()));
            arq.setDescricao(new Date().toString());
            arq.setTamanho(file.getFile().getSize());

            CreditoContaCorrenteArquivo proArq = new CreditoContaCorrenteArquivo();
            proArq.setArquivo(arquivoFacade.novoArquivoMemoria(arq, file.getFile().getInputstream()));
            proArq.setCreditoContaCorrente(selecionado);

            selecionado.getArquivos().add(proArq);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public void removerArquivo(CreditoContaCorrenteArquivo arquivo) {
        selecionado.getArquivos().remove(arquivo);
    }

    public void setaInscricaoCadastro() {
        filtroCadastro = null;
        filtroPessoa = null;
    }

    public List<SelectItem> getTiposDiferencaContaCorrente() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoDiferencaContaCorrente tipo : TipoDiferencaContaCorrente.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public void redirecionarParaContaCorrente() {
        ContaCorrenteTributaria contaCorrente = processoCreditoContaCorrenteFacade.getContaCorrenteTributariaFacade().buscarContaCorrentePorPessoa(selecionado.getPessoa());
        if (contaCorrente != null) {
            FacesUtil.redirecionamentoInterno("/tributario/conta-corrente-tributaria/ver/" + contaCorrente.getId() + "/");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Não foi possível localizar a conta corrente desse contribuinte!");
        }
    }

    public void limpaCadastro() {
        selecionado.setPessoa(null);
    }

    public List<SituacaoCadastralPessoa> getSituacoesDisponiveis() {
        if (getSituacaoCadastroPessoa() == null) {
            return Lists.newArrayList(SituacaoCadastralPessoa.values());
        }
        return Lists.newArrayList(getSituacaoCadastroPessoa());
    }

    public void imprimir() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("Processo de Crédito em Conta Corrente");
            dto.adicionarParametro("USUARIO", processoCreditoContaCorrenteFacade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA DE RIO BRANCO");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA DE FINANÇAS");
            dto.adicionarParametro("TITULO", "Processo de Crédito em Conta Corrente");
            dto.adicionarParametro("ID", selecionado.getId());
            dto.setApi("tributario/credito-conta-corrente/");
            ReportService.getInstance().gerarRelatorio(processoCreditoContaCorrenteFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public BigDecimal getValorTotal() {
        return selecionado.getItens()
            .stream()
            .map(ItemCreditoContaCorrente::getResultadoParcela)
            .map(ResultadoParcela::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorTotalPago() {
        return selecionado.getItens()
            .stream()
            .map(ItemCreditoContaCorrente::getResultadoParcela)
            .map(ResultadoParcela::getValorPago)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDiferencaTotal() {
        return selecionado.getItens()
            .stream()
            .map(ItemCreditoContaCorrente::getDiferencaContaCorrente)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
