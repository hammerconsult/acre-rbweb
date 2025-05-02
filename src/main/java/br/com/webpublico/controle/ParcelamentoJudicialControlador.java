package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteSimulacaoParcelamento;
import br.com.webpublico.entidadesauxiliares.CDAResultadoParcela;
import br.com.webpublico.entidadesauxiliares.CadastroDebito;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.negocios.ParcelamentoJudicialFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParcelamentoJudicial",
        pattern = "/tributario/divida-ativa/solicitacao-parcelamento-judicial/novo/",
        viewId = "/faces/tributario/dividaativa/parcelamentojudicial/edita.xhtml"),
    @URLMapping(id = "verParcelamentoJudicial",
        pattern = "/tributario/divida-ativa/solicitacao-parcelamento-judicial/ver/#{parcelamentoJudicialControlador.id}/",
        viewId = "/faces/tributario/dividaativa/parcelamentojudicial/visualizar.xhtml"),
    @URLMapping(id = "listarParcelamentoJudicial",
        pattern = "/tributario/divida-ativa/solicitacao-parcelamento-judicial/listar/",
        viewId = "/faces/tributario/dividaativa/parcelamentojudicial/lista.xhtml")
})
public class ParcelamentoJudicialControlador extends PrettyControlador<ParcelamentoJudicial> implements Serializable, CRUD {

    @EJB
    private ParcelamentoJudicialFacade facade;
    private AssistenteBarraProgresso assistente;
    private List<CadastroDebito> debitos;
    private CompletableFuture<List<CadastroDebito>> futureDebitos;
    private CompletableFuture<List<ItemParcelamentoJudicial>> futureItens;
    private CompletableFuture<ParcelamentoJudicial> futureSelecionado;
    private Integer tipoEmissaoDAM;
    private Integer numeroParcela;
    private String email;
    private ProcessoParcelamento parcelamentoSelecionado;

    public ParcelamentoJudicialControlador() {
        super(ParcelamentoJudicial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/divida-ativa/solicitacao-parcelamento-judicial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public List<CadastroDebito> getDebitos() {
        return debitos;
    }

    public void setDebitos(List<CadastroDebito> debitos) {
        this.debitos = debitos;
    }

    public Integer getTipoEmissaoDAM() {
        return tipoEmissaoDAM;
    }

    public void setTipoEmissaoDAM(Integer tipoEmissaoDAM) {
        this.tipoEmissaoDAM = tipoEmissaoDAM;
    }

    public Integer getNumeroParcela() {
        return numeroParcela;
    }

    public void setNumeroParcela(Integer numeroParcela) {
        this.numeroParcela = numeroParcela;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ProcessoParcelamento getParcelamentoSelecionado() {
        return parcelamentoSelecionado;
    }

    public void setParcelamentoSelecionado(ProcessoParcelamento parcelamentoSelecionado) {
        this.parcelamentoSelecionado = parcelamentoSelecionado;
    }

    @URLAction(mappingId = "novoParcelamentoJudicial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setDataSolicitacao(new Date());
        selecionado.setSituacao(ParcelamentoJudicial.Situacao.ABERTO);
        selecionado.setUsuarioSolicitacao(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @URLAction(mappingId = "verParcelamentoJudicial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        recuperarObjeto();
        operacao = Operacoes.VER;
        if (ParcelamentoJudicial.Situacao.PROCESSADO.equals(selecionado.getSituacao())) {
            selecionado = facade.recuperarParcelasParcelamentos(selecionado);
            if (selecionado.getPessoa() == null) {
                ProcessoJudicial processoJudicial = facade.getProcessoJudicialFacade().recuperarProcessoPorNumeroForum(selecionado.getNumeroProcessoForum(), ProcessoJudicial.Situacao.ATIVO);
                Pessoa p = null;
                for (ProcessoJudicialCDA processo : processoJudicial.getProcessos()) {
                    if (processo.getCertidaoDividaAtiva().getPessoa() != null) {
                        p = processo.getCertidaoDividaAtiva().getPessoa();
                        break;
                    }
                }
                if (p != null) {
                    selecionado.setPessoa(p);
                    facade.salvar(selecionado);
                }
            }
        }
        for (ItemParcelamentoJudicial item : selecionado.getItens()) {
            item.setProcessoParcelamento(facade.getProcessoParcelamentoFacade().recuperar(item.getProcessoParcelamento().getId()));
        }
    }

    public void buscarProcessoJudicial() {
        try {
            selecionado.setItens(Lists.newArrayList());
            validarNumeroProcessoJudicial();
            assistente = new AssistenteBarraProgresso(facade.getSistemaFacade().getUsuarioCorrente(),
                "Buscando Débitos relacionadas ao processo judicial " + selecionado.getNumeroProcessoForum(), 0);
            futureDebitos = AsyncExecutor.getInstance().execute(assistente,
                () -> facade.buscarDebitosProcessoJudicial(selecionado.getNumeroProcessoForum()));
            FacesUtil.executaJavaScript("acompanharConsultaDebitos()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void acompanharConsultaDebitos() {
        if (futureDebitos != null && futureDebitos.isDone()) {
            FacesUtil.executaJavaScript("finalizarConsultaDebitos()");
        }
    }

    public void finalizarConsultaDebitos() {
        try {
            debitos = futureDebitos.get();
            FacesUtil.executaJavaScript("closeDialog(dlgAcompanhamento)");
            if (debitos == null || debitos.isEmpty()) {
                FacesUtil.addWarn("Atenção!", "Nenhum débito Em Aberto encontrado para o processo judicial " +
                    selecionado.getNumeroProcessoForum());
            } else {
                FacesUtil.executaJavaScript("openDialogLarge(dlgDebitos)");
                FacesUtil.atualizarComponente("FormularioDebitos");
            }
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
            FacesUtil.executaJavaScript("closeDialog(dlgAcompanhamento)");
        }
    }

    private void validarNumeroProcessoJudicial() {
        if (Strings.isNullOrEmpty(selecionado.getNumeroProcessoForum())) {
            throw new ValidacaoException("O campo N° Processo Fórum deve ser informado.");
        }
        ProcessoJudicial processoJudicial = facade.getProcessoJudicialFacade()
            .recuperarProcessoPorNumeroForum(selecionado.getNumeroProcessoForum(), ProcessoJudicial.Situacao.ATIVO);
        if (processoJudicial == null) {
            throw new ValidacaoException("Nenhum processo judicial encontrado com o número informado.");
        }
    }

    public String getTituloAgrupamento(ItemParcelamentoJudicial itemParcelamentoJudicial) {
        return itemParcelamentoJudicial.getTituloAgrupamento();
    }

    public List<SelectItem> getSelectItensParametros(List<ParamParcelamento> parametros) {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (ParamParcelamento parametro : parametros) {
            toReturn.add(new SelectItem(parametro, parametro.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public void salvar() {
        super.salvar(Redirecionar.VER);
    }

    public void adicionarDebitos() {
        List<CDAResultadoParcela> cdaResultadoParcelas = Lists.newArrayList();
        for (CadastroDebito cadastroDebito : debitos) {
            List<CDAResultadoParcela> lista = cadastroDebito.getDebitos()
                .stream()
                .filter(deb -> deb.isSelecionado())
                .collect(Collectors.toList());
            if (!lista.isEmpty()) {
                cdaResultadoParcelas.addAll(lista);
            }
        }
        if (cdaResultadoParcelas.isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Nenhum débito selecionado para adicionar ao parcelamento judicial.");
        } else {
            FacesUtil.executaJavaScript("closeDialog(dlgDebitos)");
            FacesUtil.executaJavaScript("openDialog(dlgAcompanhamento)");
            assistente = new AssistenteBarraProgresso(facade.getSistemaFacade().getUsuarioCorrente(),
                "Adicionando débitos ao parcelamento judicial... ", 0);
            assistente.setDataAtual(new Date());
            assistente.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
            futureItens = AsyncExecutor.getInstance().execute(assistente,
                () -> facade.criarItensParcelamentoJudicial(assistente, cdaResultadoParcelas));
            FacesUtil.executaJavaScript("acompanharGeracaoItens()");
        }
    }

    public void acompanharGeracaoItens() {
        if (futureItens != null && futureItens.isDone()) {
            FacesUtil.executaJavaScript("finalizarGeracaoItens()");
        }
    }

    public void finalizarGeracaoItens() {
        try {
            selecionado.getItens().clear();
            List<ItemParcelamentoJudicial> itens = futureItens.get();
            for (ItemParcelamentoJudicial item : itens) {
                item.setParcelamentoJudicial(selecionado);
            }
            selecionado.getItens().addAll(itens);
            FacesUtil.executaJavaScript("closeDialog(dlgAcompanhamento)");
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
            FacesUtil.executaJavaScript("closeDialog(dlgAcompanhamento)");
        }
    }

    public BigDecimal getValorImpostoSelecionado() {
        BigDecimal valor = BigDecimal.ZERO;
        for (CadastroDebito debito : this.debitos) {
            for (CDAResultadoParcela cdaResultadoParcela : debito.getDebitos()) {
                if (cdaResultadoParcela.isSelecionado()) {
                    valor = valor.add(cdaResultadoParcela.getResultadoParcela().getValorImposto());
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorTaxaSelecionado() {
        BigDecimal valor = BigDecimal.ZERO;
        for (CadastroDebito debito : this.debitos) {
            for (CDAResultadoParcela cdaResultadoParcela : debito.getDebitos()) {
                if (cdaResultadoParcela.isSelecionado()) {
                    valor = valor.add(cdaResultadoParcela.getResultadoParcela().getValorTaxa());
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorDescontoSelecionado() {
        BigDecimal valor = BigDecimal.ZERO;
        for (CadastroDebito debito : this.debitos) {
            for (CDAResultadoParcela cdaResultadoParcela : debito.getDebitos()) {
                if (cdaResultadoParcela.isSelecionado()) {
                    valor = valor.add(cdaResultadoParcela.getResultadoParcela().getValorTaxa());
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorJurosSelecionado() {
        BigDecimal valor = BigDecimal.ZERO;
        for (CadastroDebito debito : this.debitos) {
            for (CDAResultadoParcela cdaResultadoParcela : debito.getDebitos()) {
                if (cdaResultadoParcela.isSelecionado()) {
                    valor = valor.add(cdaResultadoParcela.getResultadoParcela().getValorJuros());
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorMultaSelecionado() {
        BigDecimal valor = BigDecimal.ZERO;
        for (CadastroDebito debito : this.debitos) {
            for (CDAResultadoParcela cdaResultadoParcela : debito.getDebitos()) {
                if (cdaResultadoParcela.isSelecionado()) {
                    valor = valor.add(cdaResultadoParcela.getResultadoParcela().getValorMulta());
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorCorrecaoSelecionado() {
        BigDecimal valor = BigDecimal.ZERO;
        for (CadastroDebito debito : this.debitos) {
            for (CDAResultadoParcela cdaResultadoParcela : debito.getDebitos()) {
                if (cdaResultadoParcela.isSelecionado()) {
                    valor = valor.add(cdaResultadoParcela.getResultadoParcela().getValorCorrecao());
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorHonorariosSelecionado() {
        BigDecimal valor = BigDecimal.ZERO;
        for (CadastroDebito debito : this.debitos) {
            for (CDAResultadoParcela cdaResultadoParcela : debito.getDebitos()) {
                if (cdaResultadoParcela.isSelecionado()) {
                    valor = valor.add(cdaResultadoParcela.getResultadoParcela().getValorHonorarios());
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalSelecionado() {
        BigDecimal valor = BigDecimal.ZERO;
        for (CadastroDebito debito : this.debitos) {
            for (CDAResultadoParcela cdaResultadoParcela : debito.getDebitos()) {
                if (cdaResultadoParcela.isSelecionado()) {
                    valor = valor.add(cdaResultadoParcela.getResultadoParcela().getValorTotal());
                }
            }
        }
        return valor;
    }

    public void processar() {
        try {
            validarProcessar();
            assistente = new AssistenteBarraProgresso(facade.getSistemaFacade().getUsuarioCorrente(),
                "Processando solicitação de parcelamento judicial", 0);
            assistente.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
            assistente.setDataAtual(new Date());
            assistente.setIp(facade.getSistemaFacade().getIp());
            futureSelecionado = AsyncExecutor.getInstance().execute(assistente,
                () -> {
                    HashMap<String, AssistenteSimulacaoParcelamento> mapAssistenteSimulacao =
                        Maps.newHashMap();
                    for (ItemParcelamentoJudicial itemParcelamentoJudicial : selecionado.getItens()) {
                        mapAssistenteSimulacao.put(itemParcelamentoJudicial.getTituloAgrupamento(),
                            itemParcelamentoJudicial.getAssistenteSimulacaoParcelamento());
                    }

                    facade.salvarParcelamentos(selecionado);
                    ParcelamentoJudicial parcelamentoJudicial = facade.salvarRetornando(selecionado);
                    facade.gerarParcelasParcelamentos(assistente, parcelamentoJudicial, mapAssistenteSimulacao);
                    parcelamentoJudicial = facade.alterarSituacao(parcelamentoJudicial, ParcelamentoJudicial.Situacao.PROCESSADO);
                    return parcelamentoJudicial;
                });
            FacesUtil.executaJavaScript("iniciarProcessamento()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void validarProcessar() {
        selecionado.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        for (ItemParcelamentoJudicial itemParcelamentoJudicial : selecionado.getItens()) {
            if (itemParcelamentoJudicial.getProcessoParcelamento().getParamParcelamento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Parâmetro de Parcelamento deve ser informado para o cadastro " +
                    getTituloAgrupamento(itemParcelamentoJudicial));
            }
        }
        ve.lancarException();
    }

    public void acompanharProcessamento() {
        if (futureSelecionado != null) {
            if (futureSelecionado.isDone()) {
                FacesUtil.executaJavaScript("finalizarProcessamento()");
            } else if (futureSelecionado.isCancelled()) {
                FacesUtil.addOperacaoNaoRealizada("Erro inesperado ao processar a solicitação de parcelamento judicial!");
                FacesUtil.executaJavaScript("abortarProcessamento()");
            }
        }
    }

    public void finalizarProcessamento() throws ExecutionException, InterruptedException {
        selecionado = futureSelecionado.get();
        FacesUtil.addOperacaoRealizada("Processamento realizado com sucesso!");
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void visualizarParcelamento(Long id) {
        FacesUtil.redirecionamentoInterno("/parcelamento/ver/" + id + "/");
    }

    public void emitirDemonstrativo(ProcessoParcelamento processoParcelamento) {
        try {
            byte[] bytes = facade.getProcessoParcelamentoFacade().gerarBytesDemonstrativo(processoParcelamento);
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            abstractReport.setGeraNoDialog(Boolean.TRUE);
            abstractReport.escreveNoResponse("DemonstrativoParcelamento", bytes);
        } catch (Exception ex) {
            logger.error("Erro ao emitir demonstrativo. ", ex);
        }
    }

    public void comunicarProcuradoria(ItemParcelamentoJudicial itemParcelamentoJudicial) {
        try {
            facade.getProcessoParcelamentoFacade().comunicarProcuradoria(itemParcelamentoJudicial.getProcessoParcelamento(),
                itemParcelamentoJudicial.getParcelasParcelamento());
            FacesUtil.addOperacaoRealizada("Parcelamento enviado a procuradoria.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void iniciarEmissaoDAM() {
        tipoEmissaoDAM = 1;
        numeroParcela = 1;
    }

    public void emitirDAM() {
        try {
            validarEmissaoDAM();
            List<ResultadoParcela> parcelas = Lists.newArrayList();
            for (ItemParcelamentoJudicial itemParcelamentoJudicial : selecionado.getItens()) {
                for (ResultadoParcela resultadoParcela : itemParcelamentoJudicial.getParcelasParcelamento()) {
                    if (resultadoParcela.isEmAberto()) {
                        parcelas.add(resultadoParcela);
                    }
                }
            }
            if (tipoEmissaoDAM.equals(1)) {
                parcelas = parcelas.stream().filter(p -> p.getSequenciaParcela().equals(numeroParcela)).collect(Collectors.toList());
            }
            if (parcelas.isEmpty()) {
                throw new ValidacaoException("Nenhuma parcela Em Aberto encontrada para emissão do dam.");
            }
            DAM dam = facade.gerarDamAgrupado(parcelas);
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamCompostoViaApi(dam);
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um problema no servidor, tente de novo, se o problema persistir entre em contato com o suporte.");
            logger.error("Erro ao emitir o dam do parcelamento judicial. {}", e.getMessage());
            logger.debug("Detalhes do erro ao emitir o dam do parcelamento judicial.", e);
        }
    }

    private void validarEmissaoDAM() {
        if (tipoEmissaoDAM.equals(2)) {
            if (numeroParcela == null) {
                throw new ValidacaoException("O campo N° da Parcela deve ser informado.");
            }
        }
    }

    public void iniciarEnvioEmail() {
        email = "";
        ItemParcelamentoJudicial itemParcelamentoJudicial = selecionado.getItens().stream().findFirst().orElse(null);
        if (itemParcelamentoJudicial != null) {
            DebitoParcelamentoJudicial debitoParcelamentoJudicial = itemParcelamentoJudicial.getDebitos().stream().findFirst().orElse(null);
            if (debitoParcelamentoJudicial != null
                && debitoParcelamentoJudicial.getCertidaoDividaAtiva() != null
                && debitoParcelamentoJudicial.getCertidaoDividaAtiva().getPessoa() != null) {
                email = debitoParcelamentoJudicial.getCertidaoDividaAtiva().getPessoa().getEmail();
            }
        }
    }

    public void enviarPorEmail() {
        try {
            facade.enviarPorEmail(selecionado, email);
            FacesUtil.addOperacaoRealizada("E-mail enviado com sucesso.");
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um problema no servidor, tente de novo, se o problema persistir entre em contato com o suporte.");
            logger.error("Erro ao enviar por e-mail o parcelamento judicial. {}", e.getMessage());
            logger.debug("Detalhes do erro ao enviar por e-mail o parcelamento judicial.", e);
        }
    }

    public void recalcularParcelamento(ItemParcelamentoJudicial itemParcelamentoJudicial) {
        facade.recalcularParcelamento(facade.getSistemaFacade().getUsuarioCorrente(), itemParcelamentoJudicial);
    }

    public void calcularValoresAposAlteracaoQuantidadeParcelas(ItemParcelamentoJudicial itemParcelamentoJudicial) {
        facade.calcularValoresAposAlteracaoQuantidadeParcelas(itemParcelamentoJudicial);
    }

    public void gerarTermo(ProcessoParcelamento processoParcelamento, boolean novo) {
        try {
            if (processoParcelamento == null) {
                if (!temMaisDeUmItem()) return;
                facade.gerarTermoUnificado(selecionado, novo);
            } else {
                processoParcelamento.setTermo(facade.getProcessoParcelamentoFacade().gerarTermoParcelamento(processoParcelamento, null, novo ? null : processoParcelamento.getTermo()));
                facade.getProcessoParcelamentoFacade().salvar(processoParcelamento);
            }
        } catch (Exception e) {
            logger.error("Erro ao gerar termo do parcelamento. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar Termo do Parcelamento. Detalhes: " + e.getMessage());
        } finally {
            FacesUtil.executaJavaScript("gerarNovoTermo.hide()");
        }
    }

    public void gerarTermoAssinado() {
        try {
            Arquivo arquivo = facade.getProcessoParcelamentoFacade().gerarTermoAssinado(parcelamentoSelecionado);
            byte[] bytes = facade.getArquivoFacade().montarArquivoParaDownload(arquivo);
            AbstractReport.poeRelatorioNaSessao(bytes, arquivo.getNome());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public boolean temMaisDeUmItem() {
        return selecionado.getItens().size() > 1;
    }

    public void poeNaSessao() {
        Web.poeTodosFieldsNaSessao(this);
    }
}
