package br.com.webpublico.controle;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.entidadesauxiliares.LoteTransferenciaBemVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LoteTransferenciaFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-transf-bem-hierarquia", pattern = "/transferencia-de-bens-hierarquia-encerrada/novo/", viewId = "/faces/administrativo/patrimonio/solicitacaotransferencia/movel/transf-hierarquia.xhtml")
})
public class TransferenciaBemHierarquiaEncerradaControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(TransferenciaBemHierarquiaEncerradaControlador.class);
    @EJB
    private LoteTransferenciaFacade facade;
    private FiltroPesquisaBem filtroPesquisaBem;
    private AssistenteMovimentacaoBens assistente;
    private ConfigMovimentacaoBem configMovimentacaoBem;
    private HierarquiaOrganizacional unidadeAdministrativaSelecionada;
    private Map<HierarquiaOrganizacional, List<Bem>> map;
    private List<LoteTransferenciaBemVO> transferenciasVos;

    @URLAction(mappingId = "nova-transf-bem-hierarquia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaTransf() {
        try {
            novoAssistenteMovimentacao();
            novoFiltroPesquisaBem();
            map = Maps.newHashMap();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void salvar() {
        try {
            novoAssistenteMovimentacao();
            validarSalvar();
            CompletableFuture<AssistenteMovimentacaoBens> completableFuture = AsyncExecutor.getInstance().execute(assistente,
                () -> facade.salvarTransferenciaHierarquiaEncerrada(transferenciasVos, assistente));
            assistente.setCompletableFuture(completableFuture);
            FacesUtil.executaJavaScript("iniciarFutureSalvar()");
        } catch (ValidacaoException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            assistente.descobrirETratarException(e);
        }
    }

    public void pesquisarBens() {
        try {
            novoAssistenteMovimentacao();
            if (filtroPesquisaBem.getHierarquiaOrcamentaria() == null) {
                throw new ValidacaoException("O campo unidade orçamentária deve ser informado.");
            }
            assistente.setBensDisponiveis(Lists.newArrayList());
            setTransferenciasVos(Lists.newArrayList());
            CompletableFuture<List<Bem>> completableFuture = AsyncExecutor.getInstance().execute(assistente, () -> facade.pesquisarBemHierarquiaEncerrada(filtroPesquisaBem, assistente));
            assistente.setCompletableFuturePesquisaBem(completableFuture);
            FacesUtil.executaJavaScript("iniciarFuturePesquisa();");
        } catch (MovimentacaoBemException me) {
            FacesUtil.printAllFacesMessages(me.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao gerar arquivo nfse tre {}", e);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(getDataOperacao(), OperacaoMovimentacaoBem.SOLICITACAO_TRANSFERENCIA_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    private void novoFiltroPesquisaBem() {
        filtroPesquisaBem = new FiltroPesquisaBem();
        filtroPesquisaBem.setTipoBem(TipoBem.MOVEIS);
        filtroPesquisaBem.setDataOperacao(getDataOperacao());
    }

    public List<SelectItem> getHierarquiasOrcamentariaDestino() {
        if (!Util.isListNullOrEmpty(transferenciasVos)) {
            List<SelectItem> toReturn = new ArrayList<>();
            toReturn.add(new SelectItem(null, " "));
            transferenciasVos.stream()
                .filter(obj -> !Util.isListNullOrEmpty(obj.getOrcamentariasDestino()))
                .flatMap(obj -> obj.getOrcamentariasDestino().stream())
                .map(ho -> new SelectItem(ho, ho.toString())).forEach(toReturn::add);
            return toReturn;
        }
        return null;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrigemBens() {
        return Lists.newArrayList(map.keySet());
    }

    public void criarTransferenciaBemVO() {
        try {
            List<Bem> bens = map.get(unidadeAdministrativaSelecionada);

            Set<Date> datasEventos = bens
                .stream()
                .map(bem -> DataUtil.dataSemHorario(bem.getEventoBem().getDataLancamento()))
                .collect(Collectors.toSet());

            for (Date dataEvento : datasEventos) {
                LoteTransferenciaBemVO novaTransfVo = new LoteTransferenciaBemVO();

                Date dataTransf = filtroPesquisaBem.getHirarquiaEncerrada() ? dataEvento : getDataOperacao();
                novaTransfVo.setDataHoraCriacao(DataUtil.adicionaDias(dataTransf, 1));

                novaTransfVo.setDescricao("Transferência gerada para correção de unidade administrativa com vigência encerrada.");
                novaTransfVo.setTipoTransferencia(TipoTransferenciaUnidadeBem.INTERNA);
                novaTransfVo.setTipoBem(TipoBem.MOVEIS);
                novaTransfVo.setHierarquiaOrigem(unidadeAdministrativaSelecionada);
                novaTransfVo.setOrcamentariasOrigem(Lists.newArrayList(filtroPesquisaBem.getHierarquiaOrcamentaria()));
                novaTransfVo.setResponsavelOrigem(facade.getParametroPatrimonioFacade().recuperarResponsavelVigente(unidadeAdministrativaSelecionada.getSubordinada(), getDataOperacao()).getResponsavel());
                for (Bem bem : bens) {
                    if (DataUtil.dataSemHorario(bem.getEventoBem().getDataLancamento()).equals(DataUtil.dataSemHorario(dataEvento))) {
                        novaTransfVo.getBens().add(bem);
                    }
                }
                transferenciasVos.add(novaTransfVo);
                FacesUtil.atualizarComponente("Formulario:panel-bens");
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void preencherMapHierarquiasOrigemBens() {
        map = Maps.newHashMap();
        transferenciasVos = Lists.newArrayList();
        for (Bem bem : assistente.getBensDisponiveis()) {
            HierarquiaOrganizacional hoAdm = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), bem.getEventoBem().getEstadoResultante().getDetentoraAdministrativa(), bem.getEventoBem().getDataLancamento());
            bem.setAdministrativa(hoAdm.toString());

            if (!map.containsKey(hoAdm)) {
                map.put(hoAdm, Lists.newArrayList());
            }
            List<Bem> bensMap = map.get(hoAdm);
            bensMap.add(bem);
            map.put(hoAdm, bensMap);
        }
        if (isUnicaHierarquiaOrigemBens()) {
            unidadeAdministrativaSelecionada = map.keySet().stream().findFirst().orElse(null);
            if (unidadeAdministrativaSelecionada != null) {
                criarTransferenciaBemVO();
            }
        } else {
            FacesUtil.executaJavaScript("dlgSelecionarTransf.show()");
            FacesUtil.atualizarComponente("form-transf");
        }
    }

    public void cancelarSelecaoUnidadeOrigem() {
        FacesUtil.executaJavaScript("dlgSelecionarTransf.hide()");
        setTransferenciasVos(Lists.newArrayList());
    }

    public void confirmarSelecaoUnidadeOrigem() {
        if (unidadeAdministrativaSelecionada == null) {
            FacesUtil.addOperacaoNaoPermitida("Selecione uma unidade de origem dos bens para continuar.");
            return;
        }
        transferenciasVos = Lists.newArrayList();
        criarTransferenciaBemVO();
        FacesUtil.executaJavaScript("dlgSelecionarTransf.hide()");
    }

    public void recuperarResponsavelAndOrcamentariasDestino(LoteTransferenciaBemVO transfVO) {
        try {
            transfVO.setResponsavelDestino(facade.getParametroPatrimonioFacade().recuperarResponsavelVigente(transfVO.getHierarquiaDestino().getSubordinada(), getDataOperacao()).getResponsavel());
            transfVO.setResponsavelOrigem(transfVO.getResponsavelDestino());
            transfVO.setOrcamentariasDestino(facade.buscarUnidadesOrcamentaria(transfVO.getHierarquiaDestino().getSubordinada().getId()));
            if (!Util.isListNullOrEmpty(transfVO.getOrcamentariasDestino()) && transfVO.getOrcamentariasDestino().size() == 1) {
                transfVO.setHierarquiaOrcamentariaDestino(transfVO.getOrcamentariasDestino().get(0));
                listenerUnidadeOrcDestino(transfVO);
            }
        } catch (ExcecaoNegocioGenerica ex) {
            transfVO.setResponsavelDestino(null);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public boolean hasTransferneicasVos() {
        return !Util.isListNullOrEmpty(transferenciasVos);
    }

    public boolean isUnicaHierarquiaOrigemBens() {
        return !Util.isListNullOrEmpty(getHierarquiasOrigemBens()) && getHierarquiasOrigemBens().size() == 1;
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (!hasTransferneicasVos()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar é necessário gerar as solicitações de transferências para os bens.");
            ve.lancarException();
        }
        for (LoteTransferenciaBemVO transf : transferenciasVos) {
            validarCamposObrigatorios(transf, ve);
        }
        ve.lancarException();
    }

    private void validarCamposObrigatorios(LoteTransferenciaBemVO transferenciaBemVO, ValidacaoException ve) {
        if (transferenciaBemVO.getHierarquiaOrigem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa de Origem deve ser informado.");
        }
        if (transferenciaBemVO.getResponsavelOrigem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Responsável pela Unidade de Origem deve ser informado.");
        }
        if (transferenciaBemVO.getHierarquiaDestino() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa de Destino deve ser informado.");
        }
        if (transferenciaBemVO.getResponsavelDestino() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Responsável pela Unidade de Destino deve ser informado.");
        }
        if (Util.isStringNulaOuVazia(transferenciaBemVO.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
        }
        if (transferenciaBemVO.getHierarquiaOrcamentariaDestino() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo unidade orçamentária de destino deve ser informado.");
        }
        ve.lancarException();
    }

    public void listenerUnidadeOrcDestino(LoteTransferenciaBemVO transfVO) {
        transfVO.setTipoTransferencia(TipoTransferenciaUnidadeBem.INTERNA);
        Optional<HierarquiaOrganizacional> any = transfVO.getOrcamentariasOrigem().stream().filter(hoOrc -> hoOrc.getSubordinada().equals(transfVO.getHierarquiaOrcamentariaDestino().getSubordinada())).findAny();
        if (!any.isPresent()) {
            transfVO.setTipoTransferencia(TipoTransferenciaUnidadeBem.EXTERNA);
        }
    }


    public void consultarTransferencia() {
        try {
            if (assistente.getCompletableFuture() != null && assistente.getCompletableFuture().isDone()) {
                assistente = assistente.getCompletableFuture().get();
                if (assistente != null) {
                    FacesUtil.executaJavaScript("finalizarFutureSalvar()");
                }
                assistente.setCompletableFuturePesquisaBem(null);
            }
        } catch (Exception ex) {
            assistente.setBloquearAcoesTela(true);
            FacesUtil.executaJavaScript("clearInterval(timer)");
            FacesUtil.executaJavaScript("closeDialog(dlgPesquisa)");
            FacesUtil.atualizarComponente("Formulario");
            assistente.setCompletableFuture(null);
            assistente.descobrirETratarException(ex);
        }
    }

    public void finalizarTransferencia() {
        FacesUtil.addOperacaoRealizada("Solicitação de transferência de bens salva com sucesso.");
        if (!Util.isListNullOrEmpty(assistente.getTransferenciasBens()) && assistente.getTransferenciasBens().size() == 1) {
            FacesUtil.redirecionamentoInterno("/lote-de-transferencia-de-bens-moveis/ver/" + assistente.getTransferenciasBens().get(0).getId() + "/");
        } else {
            FacesUtil.redirecionamentoInterno("/lote-de-transferencia-de-bens-moveis/listar/");
        }
    }

    private void novoAssistenteMovimentacao() {
        if (configMovimentacaoBem == null) {
            recuperarConfiguracaoMovimentacaoBem();
        }
        assistente = new AssistenteMovimentacaoBens(getDataOperacao(), Operacoes.NOVO);
        assistente.zerarContadoresProcesso();
        assistente.setConfigMovimentacaoBem(configMovimentacaoBem);
        assistente.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistente.setDataAtual(getDataOperacao());
    }

    public void consultarPesquisa() {
        if (assistente.getCompletableFuturePesquisaBemVo() != null && assistente.getCompletableFuturePesquisaBemVo().isDone()) {
            FacesUtil.executaJavaScript("finalizarFuturePesquisa()");
        }
    }

    public void finalizarPesquisa() throws ExecutionException, InterruptedException {
        assistente.setBensDisponiveis(assistente.getCompletableFuturePesquisaBemVo().get());
        Collections.sort(assistente.getBensDisponiveis());
        FacesUtil.atualizarComponente("Formulario:panel-bens");
    }

    public BigDecimal getValorTotalAjuste() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (assistente.getBensDisponiveis() != null && !assistente.getBensDisponiveis().isEmpty()) {
            for (Bem bem : assistente.getBensDisponiveis()) {
                valorTotal = valorTotal.add(bem.getValorDosAjustes());
            }
        }
        return valorTotal;
    }

    public BigDecimal getValorTotalOriginal() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (assistente.getBensDisponiveis() != null && !assistente.getBensDisponiveis().isEmpty()) {
            for (Bem bem : assistente.getBensDisponiveis()) {
                valorTotal = valorTotal.add(bem.getValorOriginal());
            }
        }
        return valorTotal;
    }


    public AssistenteMovimentacaoBens getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteMovimentacaoBens assistente) {
        this.assistente = assistente;
    }


    public FiltroPesquisaBem getFiltroPesquisaBem() {
        return filtroPesquisaBem;
    }

    public void setFiltroPesquisaBem(FiltroPesquisaBem filtroPesquisaBem) {
        this.filtroPesquisaBem = filtroPesquisaBem;
    }

    public HierarquiaOrganizacional getUnidadeAdministrativaSelecionada() {
        return unidadeAdministrativaSelecionada;
    }

    public void setUnidadeAdministrativaSelecionada(HierarquiaOrganizacional unidadeAdministrativaSelecionada) {
        this.unidadeAdministrativaSelecionada = unidadeAdministrativaSelecionada;
    }

    public List<LoteTransferenciaBemVO> getTransferenciasVos() {
        return transferenciasVos;
    }

    public void setTransferenciasVos(List<LoteTransferenciaBemVO> transferenciasVos) {
        this.transferenciasVos = transferenciasVos;
    }
}
