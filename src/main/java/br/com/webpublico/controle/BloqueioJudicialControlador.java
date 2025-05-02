package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteBloqueioJudicial;
import br.com.webpublico.entidadesauxiliares.VOCertidaDividaAtiva;
import br.com.webpublico.entidadesauxiliares.ValoresAtualizadosCDA;
import br.com.webpublico.enums.AutorizacaoTributario;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BloqueioJudicialFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listaBloqueio", pattern = "/tributario/bloqueio-judicial/listar/", viewId = "/faces/tributario/contacorrente/bloqueiojudicial/lista.xhtml"),
    @URLMapping(id = "novoBloqueio", pattern = "/tributario/bloqueio-judicial/novo/", viewId = "/faces/tributario/contacorrente/bloqueiojudicial/edita.xhtml"),
    @URLMapping(id = "verBloqueio", pattern = "/tributario/bloqueio-judicial/ver/#{bloqueioJudicialControlador.id}/", viewId = "/faces/tributario/contacorrente/bloqueiojudicial/visualizar.xhtml"),
    @URLMapping(id = "editarBloqueio", pattern = "/tributario/bloqueio-judicial/editar/#{bloqueioJudicialControlador.id}/", viewId = "/faces/tributario/contacorrente/bloqueiojudicial/edita.xhtml"),
})
public class BloqueioJudicialControlador extends PrettyControlador<BloqueioJudicial> implements CRUD {
    private final BigDecimal CEM = new BigDecimal(100);
    private final int PARCELA = 0;
    private final int IMPOSTO = 1;
    private final int TAXA = 2;
    private final int JUROS = 3;
    private final int MULTA = 4;
    private final int CORRECAO = 5;
    private final int HONORARIOS = 6;
    private final BigDecimal UM_CENTAVO = new BigDecimal("0.01");
    @EJB
    private BloqueioJudicialFacade bloqueioJudicialFacade;
    private BloqueioJudicial bloqueioSalvo;
    private ConverterAutoComplete converterProcessoJudicial;
    private BigDecimal valorTotalParcelas;
    private BigDecimal valorTotalResidual;
    private List<ResultadoParcela> parcelas;
    private Map<ResultadoParcela, Set<Long>> mapaParcelas;
    private List<ResultadoParcela> parcelasDemonstrativo;
    private List<ResultadoParcela> parcelasResiduais;
    private List<VOCertidaDividaAtiva> cdas;
    private boolean editarCampos;
    private boolean hasPermissaoEfetivar;

    private AssistenteBloqueioJudicial assistente;
    private Future<AssistenteBloqueioJudicial> future;
    private Map<Long, ValoresAtualizadosCDA> mapaValoresCDA;

    public BloqueioJudicialControlador() {
        super(BloqueioJudicial.class);
        hasPermissaoEfetivar = false;
    }

    @Override
    public AbstractFacade getFacede() {
        return bloqueioJudicialFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/bloqueio-judicial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<ResultadoParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<ResultadoParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public List<ResultadoParcela> getParcelasDemonstrativo() {
        return parcelasDemonstrativo;
    }

    public void setParcelasDemonstrativo(List<ResultadoParcela> parcelasDemonstrativo) {
        this.parcelasDemonstrativo = parcelasDemonstrativo;
    }

    public List<ResultadoParcela> getParcelasResiduais() {
        return parcelasResiduais;
    }

    public void setParcelasResiduais(List<ResultadoParcela> parcelasResiduais) {
        this.parcelasResiduais = parcelasResiduais;
    }

    public List<VOCertidaDividaAtiva> getCdas() {
        return cdas;
    }

    public void setCdas(List<VOCertidaDividaAtiva> cdas) {
        this.cdas = cdas;
    }

    public BigDecimal getValorTotalParcelas() {
        return valorTotalParcelas;
    }

    public void setValorTotalParcelas(BigDecimal valorTotalParcelas) {
        this.valorTotalParcelas = valorTotalParcelas;
    }

    public BigDecimal getValorTotalResidual() {
        return valorTotalResidual;
    }

    public void setValorTotalResidual(BigDecimal valorTotalResidual) {
        this.valorTotalResidual = valorTotalResidual;
    }

    public boolean isEditarCampos() {
        return editarCampos;
    }

    public void setEditarCampos(boolean editarCampos) {
        this.editarCampos = editarCampos;
    }

    public AssistenteBloqueioJudicial getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBloqueioJudicial assistente) {
        this.assistente = assistente;
    }

    public Converter converterProcessoJudicial() {
        if (converterProcessoJudicial == null) {
            converterProcessoJudicial = new ConverterAutoComplete(ProcessoJudicial.class, bloqueioJudicialFacade.getProcessoJudicialFacade());
        }
        return converterProcessoJudicial;
    }

    @Override
    @URLAction(mappingId = "novoBloqueio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        inicializarDadosNovo();
    }

    @Override
    @URLAction(mappingId = "editarBloqueio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = bloqueioJudicialFacade.recuperar(getId());
        if (isFinalizado(selecionado) || isEstornado(selecionado)) {
            FacesUtil.addOperacaoNaoPermitida("O Processo de Bloqueio Judicial de Débitos está " + selecionado.getSituacao().getDescricao() +
                ". Portanto não pode mais ser alterado.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } else {
            inicializarDadosEdita();
        }
    }

    @Override
    @URLAction(mappingId = "verBloqueio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = bloqueioJudicialFacade.recuperar(super.getId());
        inicializarDadosVer();
    }

    @Override
    public void salvar() {
        try {
            validarBloqueioJudicial();
            validarProcesso();
            adicionarCdasAoBloqueioJudicial();
            selecionado = bloqueioJudicialFacade.salvarBloqueio(selecionado);
            redirecionarParaVisualiza(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar Processo de Bloqueio Judicial. Detalhes: " + e.getMessage());
            logger.error("Erro ao salvar processo de bloqueio judicial. ", e);
        }
    }

    private void adicionarCdasAoBloqueioJudicial() {
        if (cdas != null) {
            selecionado.getCdasBloqueioJudicial().clear();
            for (VOCertidaDividaAtiva cda : cdas) {
                CDABloqueioJudicial cdaBloqueioJudicial = new CDABloqueioJudicial();
                cdaBloqueioJudicial.setBloqueioJudicial(selecionado);
                cdaBloqueioJudicial.setIdCda(cda.getIdCda());

                selecionado.getCdasBloqueioJudicial().add(cdaBloqueioJudicial);
            }
        }
    }

    private void validarBloqueioJudicial() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(selecionado.getMotivo())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo ou Fundamentação Legal deve ser informado.");
        }

        if (bloqueioSalvo != null && editarCampos) {
            boolean alterou = hasAlteracoesCampos();
            alterou = hasAlteracoesParcelas(alterou);
            if (alterou) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("As informações do Bloqueio Judicial foram alteradas. É necessário adicionar novamente as informções ao " +
                    "quadro de resumo do processo.");
            }
        }
        ve.lancarException();
    }

    private boolean hasAlteracoesParcelas(boolean alterou) {
        if (!alterou) {
            for (ParcelaBloqueioJudicial parcelaSalva : bloqueioSalvo.getParcelas()) {
                boolean contem = false;
                for (ParcelaBloqueioJudicial parcela : selecionado.getParcelas()) {
                    if (parcelaSalva.getIdParcela().equals(parcela.getIdParcela())) {
                        contem = true;
                        break;
                    }
                }
                if (!contem) {
                    alterou = true;
                    break;
                }
            }

        }
        return alterou;
    }

    private boolean hasAlteracoesCampos() {
        return !DataUtil.getDataFormatada(bloqueioSalvo.getDataBloqueio()).equals(DataUtil.getDataFormatada(selecionado.getDataBloqueio())) ||
            !bloqueioSalvo.getContribuinteBloqueio().equals(selecionado.getContribuinteBloqueio()) ||
            !bloqueioSalvo.getValorPenhora().setScale(2, RoundingMode.HALF_UP).equals(selecionado.getValorPenhora().setScale(2, RoundingMode.HALF_UP)) ||
            !bloqueioSalvo.getValorBloqueio().setScale(2, RoundingMode.HALF_UP).equals(selecionado.getValorBloqueio().setScale(2, RoundingMode.HALF_UP)) ||
            bloqueioSalvo.getParcelas().size() != selecionado.getParcelas().size();
    }

    private void redirecionarParaVisualiza(String msg) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        FacesUtil.addOperacaoRealizada(msg);
    }

    private void inicializarDadosNovo() {
        this.parcelas = Lists.newArrayList();
        this.parcelasDemonstrativo = Lists.newArrayList();
        this.cdas = Lists.newArrayList();
        this.valorTotalParcelas = BigDecimal.ZERO;
        this.editarCampos = true;

        selecionado.setDataProcesso(new Date());
        selecionado.setDataPenhora(new Date());
        selecionado.setDataBloqueio(new Date());
        selecionado.setUsuarioBloqueio(bloqueioJudicialFacade.buscarUsuarioCorrente());
        selecionado.setExercicio(bloqueioJudicialFacade.buscarExercicioCorrente());
        selecionado.setCodigo(bloqueioJudicialFacade.buscarProximoCodigoPorExercicio(selecionado.getExercicio().getId()));
        selecionado.setSituacao(SituacaoProcessoDebito.EM_ABERTO);
        selecionado.setValorPenhora(BigDecimal.ZERO);
        selecionado.setValorBloqueio(BigDecimal.ZERO);
    }

    private void inicializarDadosVer() {
        mapaValoresCDA = Maps.newHashMap();
        mapaParcelas = Maps.newHashMap();
        parcelas = buscarParcelas();
        atribuirValoresSalvosParcela();
        adicionarCdas();
        atualizarValorTotalParcelasVer();
        criarParcelasDemonstrativas();
        parcelasResiduais = bloqueioJudicialFacade.buscarParcelasResiduais(selecionado.getId());
        ordenarParcelas(parcelasResiduais);
        hasPermissaoEfetivar = bloqueioJudicialFacade.validaAutorizacaoUsuario(bloqueioJudicialFacade.buscarUsuarioCorrente(), AutorizacaoTributario.PERMITIR_EFETIVAR_PROCESSO_BLOQUEIO_JUDICIAL);
    }

    private void atribuirValoresSalvosParcela() {
        if (SituacaoProcessoDebito.FINALIZADO.equals(selecionado.getSituacao()) || SituacaoProcessoDebito.ESTORNADO.equals(selecionado.getSituacao())) {
            for (ResultadoParcela parcela : parcelas) {
                Optional<ParcelaBloqueioJudicial> parcelaBloqueio = selecionado.getParcelas().stream().filter(p -> p.getIdParcela().equals(parcela.getIdParcela())).findFirst();
                if (parcelaBloqueio.isPresent()) {
                    ParcelaBloqueioJudicial parcelaBloqueioJudicial = parcelaBloqueio.get();
                    parcela.setValorImposto(parcelaBloqueioJudicial.getImposto());
                    parcela.setValorTaxa(parcelaBloqueioJudicial.getTaxa());
                    parcela.setValorJuros(parcelaBloqueioJudicial.getJuros());
                    parcela.setValorMulta(parcelaBloqueioJudicial.getMulta());
                    parcela.setValorCorrecao(parcelaBloqueioJudicial.getCorrecao());
                    parcela.setValorHonorarios(parcelaBloqueioJudicial.getHonorarios());
                    parcela.setValorDesconto(parcelaBloqueioJudicial.getDesconto());
                    parcela.setTotal(parcelaBloqueioJudicial.getTotal());
                }
            }
        }
    }

    private void adicionarCdas() {
        cdas = Lists.newArrayList();
        if (isOperacaoVer()) {
            for (CDABloqueioJudicial cdaBloqueioJudicial : selecionado.getCdasBloqueioJudicial()) {
                preencherVoCda(true, cdaBloqueioJudicial.getIdCda());
            }
        } else {
            for (ResultadoParcela parcela : parcelas) {
                for (ParcelaBloqueioJudicial parcelaBloqueio : selecionado.getParcelas()) {
                    if (parcela.getIdParcela().equals(parcelaBloqueio.getIdParcela())) {
                        adicionarCda(parcela);
                        break;
                    }
                }
            }
        }
    }

    private void inicializarDadosEdita() {
        bloqueioSalvo = new BloqueioJudicial();
        bloqueioSalvo.setDataBloqueio(selecionado.getDataBloqueio());
        bloqueioSalvo.setContribuinteBloqueio(selecionado.getContribuinteBloqueio());
        bloqueioSalvo.setValorPenhora(selecionado.getValorPenhora());
        bloqueioSalvo.setValorBloqueio(selecionado.getValorBloqueio());

        for (ParcelaBloqueioJudicial parcela : selecionado.getParcelas()) {
            ParcelaBloqueioJudicial parcelaBloqueioJudicial = new ParcelaBloqueioJudicial();
            parcelaBloqueioJudicial.setIdParcela(parcela.getIdParcela());
            bloqueioSalvo.getParcelas().add(parcelaBloqueioJudicial);
        }

        this.editarCampos = true;
        this.parcelas = Lists.newArrayList();
        if (selecionado != null && selecionado.getProcessoJudicial() != null) {
            mapaParcelas = bloqueioJudicialFacade.buscarParcelasCDA(selecionado, true);
            parcelas = Lists.newArrayList(mapaParcelas.keySet());
        }
        ordenarParcelas(parcelas);
        adicionarValorResidual();
        atualizarValorTotalParcelasVer();
        criarParcelasDemonstrativas();
        atribuirValoresSalvosParcela();
        adicionarCdas();
    }

    public void validarDataPenhora() {
        if (selecionado != null && selecionado.getDataPenhora() != null && selecionado.getDataPenhora().after(new Date())) {
            selecionado.setDataPenhora(new Date());
            FacesUtil.addOperacaoNaoPermitida("A Data de Penhora deve ser menor ou igual a data atual.");
        }
    }

    public void validarDataBloqueio() {
        if (selecionado != null && selecionado.getDataBloqueio() != null && selecionado.getDataBloqueio().after(new Date())) {
            selecionado.setDataBloqueio(new Date());
            FacesUtil.addOperacaoNaoPermitida("A Data de Bloqueio deve ser menor ou igual a data atual.");
        }
    }

    public void validarValorPenhora() {
        if (selecionado != null && selecionado.getValorPenhora() != null && selecionado.getValorPenhora().compareTo(BigDecimal.ZERO) < 0) {
            selecionado.setValorPenhora(BigDecimal.ZERO);
            FacesUtil.addOperacaoNaoPermitida("O Valor de Penhora deve ser maior que zero.");
        }
    }

    public void validarValorBloqueio() {
        if ((selecionado != null && selecionado.getValorBloqueio() != null && selecionado.getValorPenhora() != null) && (selecionado.getValorBloqueio().compareTo(BigDecimal.ZERO) < 0 ||
            selecionado.getValorBloqueio().compareTo(selecionado.getValorPenhora()) > 0)) {
            selecionado.setValorBloqueio(BigDecimal.ZERO);
            FacesUtil.addOperacaoNaoPermitida("O Valor de Bloqueio deve ser maior que zero e menor ou igual ao Valor Penhorado.");
        }
    }

    public void validarContribuinteBloqueio() {
        if (selecionado != null && selecionado.getContribuintePenhora() != null &&
            selecionado.getContribuinteBloqueio() != null && !selecionado.getContribuinteBloqueio().equals(selecionado.getContribuintePenhora())) {
            selecionado.setContribuinteBloqueio(null);
            FacesUtil.addOperacaoNaoPermitida("O Contribuinte do Bloqueio Judicial de Débitos deve ser o mesmo selecionado na Decisão Judicial de Penhora.");
        }
    }

    public List<ProcessoJudicial> buscarProcessosJudiciais(String parte) {
        if (selecionado.getContribuintePenhora() != null) {
            return bloqueioJudicialFacade.buscarProcessosJudiciais(parte, selecionado.getContribuintePenhora());
        }
        return bloqueioJudicialFacade.buscarProcessosJudiciais(parte);
    }

    public void buscarDadosProcessoJudicial() {
        try {
            validarBuscarDadosProcesso();
            if (selecionado != null && selecionado.getProcessoJudicial() != null) {
                selecionado.getParcelas().clear();
                mapaParcelas = bloqueioJudicialFacade.buscarParcelasCDA(selecionado, false);
                parcelas = Lists.newArrayList(mapaParcelas.keySet());
                ordenarParcelas(parcelas);
                if (parcelas.isEmpty()) {
                    FacesUtil.addOperacaoNaoRealizada("Não foram encontradas parcelas Em Aberto referentes ao Processo Judicial N° " + selecionado.getProcessoJudicial().getNumero() + " e Contribuinte " +
                        selecionado.getContribuintePenhora().getNomeCpfCnpj());
                    selecionado.setProcessoJudicial(null);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao buscar parcelas do processo judicial. Detalhes: " + e.getMessage());
            logger.error("Erro ao buscar parcelas. ", e);
        }
    }

    private void validarBuscarDadosProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContribuintePenhora() == null) {
            selecionado.setProcessoJudicial(null);
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Contribuinte do bloqueio deve ser selecionado para consultar os débitos.");
        }
        ve.lancarException();
    }

    public void adicionarTodasParcelas() {
        for (ResultadoParcela parcela : parcelas) {
            adicionarParcela(parcela);
        }
    }

    public void removerTodasParcelas() {
        cdas.clear();
        selecionado.getParcelas().clear();
        atualizarValorTotalParcelasVer();
    }

    public boolean hasTodasParcelas() {
        return parcelas != null && selecionado != null && selecionado.getParcelas() != null && !parcelas.isEmpty() && !selecionado.getParcelas().isEmpty() &&
            parcelas.size() == selecionado.getParcelas().size();
    }

    public void adicionarParcela(ResultadoParcela parcela) {
        boolean contem = false;
        for (ParcelaBloqueioJudicial parcelaBloqueioJudicial : selecionado.getParcelas()) {
            if (parcelaBloqueioJudicial.getIdParcela().equals(parcela.getIdParcela())) {
                contem = true;
                break;
            }
        }
        if (!contem) {
            ParcelaBloqueioJudicial parcelaBloqueio = new ParcelaBloqueioJudicial();
            parcelaBloqueio.setBloqueioJudicial(selecionado);
            parcelaBloqueio.setIdParcela(parcela.getIdParcela());
            parcelaBloqueio.setValor(parcela.getValorTotal());

            selecionado.getParcelas().add(parcelaBloqueio);
            adicionarCda(parcela);
        }
        atualizarValorTotalParcelasVer();
    }

    public void removerParcela(ResultadoParcela parcela) {
        ParcelaBloqueioJudicial parcelaRemover = null;
        for (ParcelaBloqueioJudicial parcelaSelecionado : selecionado.getParcelas()) {
            if (parcelaSelecionado.getIdParcela().equals(parcela.getIdParcela())) {
                parcelaRemover = parcelaSelecionado;
                break;
            }
        }
        if (parcelaRemover != null) {
            removerCda(parcela);
            selecionado.getParcelas().remove(parcelaRemover);
        }
        atualizarValorTotalParcelasVer();
    }

    private void adicionarCda(ResultadoParcela parcela) {
        boolean operacaoVer = isOperacaoVer();
        List<Long> idsCda = buscarIdsCdaNoMapa(parcela);
        for (Long idCda : idsCda) {
            preencherVoCda(operacaoVer, idCda);
        }
    }

    private void preencherVoCda(boolean operacaoVer, Long idCda) {
        VOCertidaDividaAtiva cda = bloqueioJudicialFacade.buscarVOCertidaoDividaAtiva(idCda);
        if (cda != null && !cdas.contains(cda)) {
            if (operacaoVer) {
                cda.setValorTotal(buscarValorCDA(cda.getIdCda()));
            }
            cdas.add(cda);
        }
        ordenarCdas(cdas);
    }

    private void removerCda(ResultadoParcela parcela) {
        List<Long> idsCda = buscarIdsCdaNoMapa(parcela);
        for (Long idCda : idsCda) {
            boolean podeRemover = true;
            loopParcelas:
            for (ParcelaBloqueioJudicial parcelaSelecionado : selecionado.getParcelas()) {
                if (!parcelaSelecionado.getIdParcela().equals(parcela.getIdParcela())) {
                    ResultadoParcela resultadoParcelaSelecionado = bloqueioJudicialFacade.buscarResultadoParcelaPeloIdParcela(parcelaSelecionado.getIdParcela());
                    List<Long> idsCdaSelecionada = buscarIdsCdaNoMapa(resultadoParcelaSelecionado);
                    for (Long idCdaSelecionada : idsCdaSelecionada) {
                        if (idCda.equals(idCdaSelecionada)) {
                            podeRemover = false;
                            break loopParcelas;
                        }
                    }
                }
            }
            if (podeRemover) {
                VOCertidaDividaAtiva cda = bloqueioJudicialFacade.buscarVOCertidaoDividaAtiva(idCda);
                cdas.remove(cda);
            }
        }
    }

    private List<Long> buscarIdsCdaNoMapa(ResultadoParcela parcela) {
        Set<Long> ids = mapaParcelas.get(parcela);
        return ids != null ? Lists.newArrayList(ids) : Collections.<Long>emptyList();
    }

    public boolean hasParcela(ResultadoParcela parcela) {
        boolean contem = false;
        for (ParcelaBloqueioJudicial selecionadoParcela : selecionado.getParcelas()) {
            if (selecionadoParcela.getIdParcela().equals(parcela.getIdParcela())) {
                contem = true;
                break;
            }
        }
        return contem;
    }

    private void atualizarValorTotalParcelasVer() {
        valorTotalParcelas = BigDecimal.ZERO;
        if (parcelas != null) {
            for (ResultadoParcela parcela : parcelas) {
                Optional<ParcelaBloqueioJudicial> parcelaBloqueio = selecionado.getParcelas().stream().filter(p -> p.getIdParcela().equals(parcela.getIdParcela())).findFirst();
                if (parcelaBloqueio.isPresent()) {
                    valorTotalParcelas = valorTotalParcelas.add(parcela.getValorTotal() != null ? parcela.getValorTotal() : BigDecimal.ZERO);
                }
            }
        }
        adicionarValorResidual();
    }

    public void adicionarDadosAoResumoDoProcesso() {
        try {
            validarProcesso();
            this.editarCampos = false;
            adicionarValorResidual();
            criarParcelasDemonstrativas();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void adicionarValorResidual() {
        if (selecionado != null) {
            this.valorTotalResidual = this.valorTotalParcelas != null && selecionado.getValorBloqueio() != null ?
                this.valorTotalParcelas.subtract(selecionado.getValorBloqueio()) : BigDecimal.ZERO;

            selecionado.setValorSomaParcelas(valorTotalParcelas != null ? valorTotalParcelas : BigDecimal.ZERO);
            selecionado.setValorResidual(valorTotalResidual != null ? valorTotalResidual : BigDecimal.ZERO);
        }
    }

    private void validarProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValorBloqueio() != null &&
            this.valorTotalParcelas != null && selecionado.getValorBloqueio().compareTo(this.valorTotalParcelas) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor Bloqueado deve ser menor ou igual ao Valor Total dos débitos.");
        }
        if (selecionado.getDataPenhora() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data da Penhora deve ser informado.");
        }
        if (selecionado.getValorPenhora() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Penhorado deve ser informado.");
        } else if (selecionado.getValorPenhora().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor Penhorado deve ser maior que zero");
        }
        if (selecionado.getContribuintePenhora() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Contribuinte da Penhora deve ser informado.");
        }
        if (selecionado.getProcessoJudicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Processo Judicial deve ser informado.");
        }
        if (selecionado.getDataBloqueio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Bloqueio deve ser informado.");
        }
        if (selecionado.getValorBloqueio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Bloqueado deve ser informado.");
        } else if (selecionado.getValorBloqueio().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor Bloqueado deve ser maior que zero");
        }
        if (selecionado.getContribuinteBloqueio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Contribuinte do Bloqueio deve ser informado.");
        }
        if (selecionado.getParcelas().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione ao menos uma parcela!");
        }
        ve.lancarException();
    }

    private void criarParcelasDemonstrativas() {
        List<ResultadoParcela> parcelasOriginais = isOperacaoVer() ? parcelas : bloqueioJudicialFacade.buscarParcelasPeloId(bloqueioJudicialFacade.montarIdsParcelaBloqueio(selecionado));
        BigDecimal valorTotal = BigDecimal.ZERO;
        BigDecimal valorBloqueado = selecionado.getValorBloqueio();
        parcelasDemonstrativo = Lists.newArrayList();

        ordenarParcelas(parcelasOriginais);
        for (ResultadoParcela parcelaOriginal : parcelasOriginais) {
            BigDecimal percentual = parcelaOriginal.getValorTotal().multiply(CEM).divide(valorTotalParcelas, 8, RoundingMode.HALF_UP);
            BigDecimal valorProporcional = valorBloqueado.multiply(percentual).divide(CEM, 8, RoundingMode.HALF_UP);
            ResultadoParcela demonstrativo = criarParcelaAndProporcionalizarValor(parcelaOriginal, valorProporcional);
            valorTotal = valorTotal.add(demonstrativo.getValorTotal());
        }

        ordenarParcelas(parcelasDemonstrativo);
        ajustarValoresDaParcela(valorTotal);
        adicionarValoresNaParcelaDoBloqueio(parcelasOriginais);
    }

    private void adicionarValoresNaParcelaDoBloqueio(List<ResultadoParcela> parcelasOriginais) {
        if (!isFinalizado(selecionado) && !isEstornado(selecionado)) {
            for (ParcelaBloqueioJudicial parcelaBloqueio : selecionado.getParcelas()) {
                adicionarValoresParcelaOriginal(parcelaBloqueio, parcelasOriginais);
                adicionarValoresParcelaOriginada(parcelaBloqueio);
            }
        }
    }

    private void adicionarValoresParcelaOriginada(ParcelaBloqueioJudicial parcelaBloqueio) {
        for (ResultadoParcela parcelaOriginada : parcelasDemonstrativo) {
            if (parcelaBloqueio.getIdParcela().equals(parcelaOriginada.getIdParcela())) {
                parcelaBloqueio.setImpostoOriginada(parcelaOriginada.getValorImposto());
                parcelaBloqueio.setTaxaOriginada(parcelaOriginada.getValorTaxa());
                parcelaBloqueio.setJurosOriginada(parcelaOriginada.getValorJuros());
                parcelaBloqueio.setMultaOriginada(parcelaOriginada.getValorMulta());
                parcelaBloqueio.setCorrecaoOriginada(parcelaOriginada.getValorCorrecao());
                parcelaBloqueio.setHonorariosOriginada(parcelaOriginada.getValorHonorarios());
                parcelaBloqueio.setDescontoOriginada(parcelaOriginada.getValorDesconto());
                parcelaBloqueio.setTotalOriginada(parcelaOriginada.getValorTotal());
            }
        }
    }

    private void adicionarValoresParcelaOriginal(ParcelaBloqueioJudicial parcelaBloqueio, List<ResultadoParcela> parcelasOriginais) {
        for (ResultadoParcela parcelaOriginal : parcelasOriginais) {
            if (parcelaBloqueio.getIdParcela().equals(parcelaOriginal.getIdParcela())) {
                parcelaBloqueio.setImposto(parcelaOriginal.getValorImposto());
                parcelaBloqueio.setTaxa(parcelaOriginal.getValorTaxa());
                parcelaBloqueio.setJuros(parcelaOriginal.getValorJuros());
                parcelaBloqueio.setMulta(parcelaOriginal.getValorMulta());
                parcelaBloqueio.setCorrecao(parcelaOriginal.getValorCorrecao());
                parcelaBloqueio.setHonorarios(parcelaOriginal.getValorHonorarios());
                parcelaBloqueio.setDesconto(parcelaOriginal.getValorDesconto());
                parcelaBloqueio.setTotal(parcelaOriginal.getValorTotal());
            }
        }
    }

    private void ajustarValoresDaParcela(BigDecimal valorTotal) {
        HashMap<ResultadoParcela, Integer> parcelasAjustadas = Maps.newHashMap();
        if (parcelasDemonstrativo != null && !parcelasDemonstrativo.isEmpty()) {
            if (valorTotal.compareTo(selecionado.getValorBloqueio()) != 0) {
                BigDecimal diferenca = valorTotal.subtract(selecionado.getValorBloqueio());
                if (diferenca.compareTo(BigDecimal.ZERO) < 0) {
                    diferenca = diferenca.negate();
                }
                if (diferenca.compareTo(CEM) < 0) {
                    abaterValores(diferenca, parcelasDemonstrativo, parcelasAjustadas);
                }
            }
        }
    }

    private void abaterValores(BigDecimal diferenca, List<ResultadoParcela> parcelas, HashMap<ResultadoParcela, Integer> parcelasAjustadas) {
        for (int i = 0; i < parcelas.size(); i++) {
            ResultadoParcela parcela = parcelas.get(i);
            if (!parcelasAjustadas.containsKey(parcela)) parcelasAjustadas.put(parcela, PARCELA);
            if (diferenca.compareTo(BigDecimal.ZERO) <= 0) break;
            diferenca = diferenca.subtract(UM_CENTAVO);
            logger.error("Parcela:{} - Diferenca: {}", parcela.getIdParcela(), diferenca);

            if (parcela.getValorImposto().compareTo(UM_CENTAVO) > 0 && parcelasAjustadas.get(parcela) < IMPOSTO) {
                parcela.setValorImposto(parcela.getValorImposto().subtract(UM_CENTAVO));
                parcelasAjustadas.put(parcela, IMPOSTO);
            } else if (parcela.getValorTaxa().compareTo(UM_CENTAVO) > 0 && parcelasAjustadas.get(parcela) < TAXA) {
                parcela.setValorTaxa(parcela.getValorTaxa().subtract(UM_CENTAVO));
                parcelasAjustadas.put(parcela, TAXA);
            } else if (parcela.getValorJuros().compareTo(UM_CENTAVO) > 0 && parcelasAjustadas.get(parcela) < JUROS) {
                parcela.setValorJuros(parcela.getValorJuros().subtract(UM_CENTAVO));
                parcelasAjustadas.put(parcela, JUROS);
            } else if (parcela.getValorMulta().compareTo(UM_CENTAVO) > 0 && parcelasAjustadas.get(parcela) < MULTA) {
                parcela.setValorMulta(parcela.getValorMulta().subtract(UM_CENTAVO));
                parcelasAjustadas.put(parcela, MULTA);
            } else if (parcela.getValorCorrecao().compareTo(UM_CENTAVO) > 0 && parcelasAjustadas.get(parcela) < CORRECAO) {
                parcela.setValorCorrecao(parcela.getValorCorrecao().subtract(UM_CENTAVO));
                parcelasAjustadas.put(parcela, CORRECAO);
            } else if (parcela.getValorHonorarios().compareTo(UM_CENTAVO) > 0 && parcelasAjustadas.get(parcela) < HONORARIOS) {
                parcela.setValorHonorarios(parcela.getValorHonorarios().subtract(UM_CENTAVO));
                parcelasAjustadas.put(parcela, PARCELA);
            }

            if (i == parcelas.size() - 1 && diferenca.compareTo(BigDecimal.ZERO) > 0) {
                abaterValores(diferenca, parcelas, parcelasAjustadas);
            }
        }
    }

    private ResultadoParcela criarParcelaAndProporcionalizarValor(ResultadoParcela parcela, BigDecimal valorProporcional) {
        ResultadoParcela demonstrativo = new ResultadoParcela();

        demonstrativo.setIdParcela(parcela.getIdParcela());
        demonstrativo.setCadastro(parcela.getCadastro());
        demonstrativo.setReferencia(parcela.getReferencia());
        demonstrativo.setIdDivida(parcela.getIdDivida());
        demonstrativo.setIdCadastro(parcela.getIdCadastro());
        demonstrativo.setIdPessoa(parcela.getIdPessoa());
        demonstrativo.setDivida(parcela.getDivida());
        demonstrativo.setVencimento(parcela.getVencimento());
        demonstrativo.setExercicio(parcela.getExercicio());
        demonstrativo.setDividaAtiva(parcela.getDividaAtiva());
        demonstrativo.setDividaAtivaAjuizada(parcela.getDividaAtivaAjuizada());
        demonstrativo.setDividaIsDividaAtiva(parcela.getDividaIsDividaAtiva());
        demonstrativo.setOrdemApresentacao(parcela.getOrdemApresentacao());
        demonstrativo.setSituacao(SituacaoParcela.PAGO_BLOQUEIO_JUDICIAL.equals(parcela.getSituacaoEnumValue()) ? parcela.getSituacaoNameEnum() : SituacaoParcela.AGUARDANDO_PAGAMENTO_BLOQUEIO_JUDICIAL.name());
        demonstrativo.setParcela(parcela.getParcela());

        demonstrativo.setValorImposto(proporcionalizarValor(parcela.getValorTotal(), parcela.getValorImposto(), valorProporcional));
        demonstrativo.setValorTaxa(proporcionalizarValor(parcela.getValorTotal(), parcela.getValorTaxa(), valorProporcional));
        demonstrativo.setValorJuros(proporcionalizarValor(parcela.getValorTotal(), parcela.getValorJuros(), valorProporcional));
        demonstrativo.setValorMulta(proporcionalizarValor(parcela.getValorTotal(), parcela.getValorMulta(), valorProporcional));
        demonstrativo.setValorCorrecao(proporcionalizarValor(parcela.getValorTotal(), parcela.getValorCorrecao(), valorProporcional));
        demonstrativo.setValorHonorarios(proporcionalizarValor(parcela.getValorTotal(), parcela.getValorHonorarios(), valorProporcional));
        if (parcela.getValorDesconto().compareTo(BigDecimal.ZERO) > 0) {
            demonstrativo.setValorDesconto(proporcionalizarValor(parcela.getValorTotal(), parcela.getValorDesconto(), valorProporcional));
        } else {
            demonstrativo.setValorDesconto(BigDecimal.ZERO);
        }

        BigDecimal diferenca = valorProporcional.subtract(demonstrativo.getValorTotal());
        if (diferenca.compareTo(BigDecimal.ZERO) > 0) {
            demonstrativo.setValorImposto(demonstrativo.getValorImposto().add(diferenca));
        } else {
            demonstrativo.setValorImposto(demonstrativo.getValorImposto().subtract(diferenca));
        }
        parcelasDemonstrativo.add(demonstrativo);
        return demonstrativo;
    }

    private BigDecimal proporcionalizarValor(BigDecimal valorTotal, BigDecimal valorParcial, BigDecimal valorProporcional) {
        BigDecimal percentual = valorParcial.multiply(CEM).divide(valorTotal, 8, RoundingMode.HALF_UP);
        return valorProporcional.multiply(percentual).divide(CEM, 8, RoundingMode.HALF_UP);
    }

    private void ordenarParcelas(List<ResultadoParcela> parcelas) {
        final Ordering<Comparable<?>> nullsLast = Ordering.natural().nullsLast();
        parcelas.sort(new Comparator<ResultadoParcela>() {
            @Override
            public int compare(ResultadoParcela o1, ResultadoParcela o2) {
                return ComparisonChain.start()
                    .compare(o1.getCadastro(), o2.getCadastro(), nullsLast)
                    .compare(o1.getOrdemApresentacao(), o2.getOrdemApresentacao(), nullsLast)
                    .compare(o1.getDivida(), o2.getDivida(), nullsLast)
                    .compare(o1.getExercicio(), o2.getExercicio(), nullsLast)
                    .compare(o1.getReferencia(), o2.getReferencia(), nullsLast)
                    .compare(o1.getSd(), o2.getSd(), nullsLast)
                    .compare(o1.getCotaUnica(), o2.getCotaUnica(), nullsLast)
                    .compare(o1.getVencimento(), o2.getVencimento(), nullsLast)
                    .compare(o1.getParcela(), o2.getParcela(), nullsLast)
                    .compare(o1.getSituacao(), o2.getSituacao())
                    .result();
            }
        });
    }

    private void ordenarCdas(List<VOCertidaDividaAtiva> cdas) {
        cdas.sort(new Comparator<VOCertidaDividaAtiva>() {
            @Override
            public int compare(VOCertidaDividaAtiva o1, VOCertidaDividaAtiva o2) {
                return ComparisonChain.start()
                    .compare(o1.getAno(), o2.getAno())
                    .compare(o1.getNumeroCda(), o2.getNumeroCda())
                    .compare(o1.getDataCda(), o2.getDataCda())
                    .result();
            }
        });
    }

    public void imprimirDAM(ResultadoParcela parcela) {
        try {
            bloqueioJudicialFacade.imprimirDAM(gerarDamIndividual(parcela));
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao imprimir dam. Detalhes: " + e.getMessage());
            logger.error("Erro ao imprimir dam. ", e);
        }
    }

    private DAM gerarDamIndividual(ResultadoParcela parcela) throws Exception {
        return bloqueioJudicialFacade.buscarOuGerarDam(parcela);
    }

    private List<ResultadoParcela> buscarParcelas() {
        return bloqueioJudicialFacade.buscarParcelasPeloId(bloqueioJudicialFacade.montarIdsParcelaBloqueio(selecionado));
    }

    private void verificarSituacaoParcelas() {
        ValidacaoException ve = new ValidacaoException();
        for (ParcelaBloqueioJudicial parcelaBloqueio : selecionado.getParcelas()) {
            ParcelaValorDivida pvd = bloqueioJudicialFacade.recuperarParcela(parcelaBloqueio.getIdParcela());
            if (pvd != null && pvd.getSituacaoAtual().getSituacaoParcela() != SituacaoParcela.EM_ABERTO) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Todas as parcelas Devem estar com a situação \"Em Aberto\".");
                break;
            }
        }
        ve.lancarException();
    }

    public void processarBloqueioJudicial() {
        try {
            verificarSituacaoParcelas();
            bloqueioJudicialFacade.alterarSituacaoParcelas(selecionado);
            inicializarAssistente();
            future = bloqueioJudicialFacade.processarBloqueioJudicial(assistente);
            abrirDialogProgressBar();
            executarPoll();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao processar Bloqueio Judicial. Detalhes: " + e.getMessage());
            logger.error("Erro ao processar bloqueio judicial. ", e);
        }
    }

    private void inicializarAssistente() {
        assistente = new AssistenteBloqueioJudicial();
        assistente.setExecutando(true);
        assistente.setSelecionado(selecionado);
        assistente.setTotalParcelasResiduais(0);
        assistente.setDescricaoProcesso("Processando Bloqueio Judicial de Débitos");
        assistente.setUsuarioSistema(bloqueioJudicialFacade.buscarUsuarioCorrente());
        assistente.setVencimentoDam(bloqueioJudicialFacade.montarVencimentoDam());
        assistente.setExercicio(bloqueioJudicialFacade.buscarExercicioCorrente());
        assistente.setDataAtual(bloqueioJudicialFacade.buscarDataOperacao());
        assistente.setParcelasOriginais(parcelas);
        assistente.setParcelasOriginadas(parcelasDemonstrativo);

        for (ParcelaBloqueioJudicial parcela : selecionado.getParcelas()) {
            if (parcela.getTotal().compareTo(parcela.getTotalOriginada()) > 0) {
                assistente.setTotalParcelasResiduais(assistente.getTotalParcelasResiduais() + 1);
            }
        }
    }

    private void abrirDialogProgressBar() {
        FacesUtil.executaJavaScript("dialogProgres.show()");
    }

    private void executarPoll() {
        FacesUtil.executaJavaScript("pollProcesso.start()");
    }

    public void abortar() {
        if (future != null) {
            future.cancel(true);
            assistente.setExecutando(false);
        }
    }

    public void finalizarProcesso() {
        if (future != null && future.isDone()) {
            fecharDialog();
            try {
                if (future.get().getSelecionado() != null) {
                    assistente.setExecutando(false);
                    selecionado = (BloqueioJudicial) future.get().getSelecionado();
                    abaterValoresParcelasResiduais();
                    redirecionarParaVisualiza("Bloqueio Judicial processado com sucesso!");
                }
            } catch (Exception e) {
                FacesUtil.addError("Erro ao processar bloqueio judicial...", e.getMessage());
            } finally {
                future = null;
            }
        }
    }

    private void abaterValoresParcelasResiduais() {
        List<ResultadoParcela> parcelasResiduais = bloqueioJudicialFacade.buscarParcelasResiduais(selecionado.getId());
        BigDecimal totalResidual = BigDecimal.ZERO;
        if (parcelasResiduais != null && !parcelasResiduais.isEmpty()) {
            ordenarParcelas(parcelasResiduais);
            for (ResultadoParcela parcelaResidual : parcelasResiduais) {
                totalResidual = totalResidual.add(parcelaResidual.getValorTotal());
            }

            if (totalResidual.compareTo(selecionado.getValorResidual()) != 0) {
                BigDecimal diferenca = totalResidual.subtract(selecionado.getValorResidual());
                bloqueioJudicialFacade.adicionarValorAbaterCalculo(parcelasResiduais.get(0).getIdCalculo(), diferenca);
            }
        }
    }

    private void fecharDialog() {
        FacesUtil.executaJavaScript("dialogProgres.hide()");
        FacesUtil.atualizarComponente("Formulario");
    }

    public boolean canProcessarBloqueioJudicial() {
        if (isFinalizado(selecionado) || isEstornado(selecionado)) {
            return false;
        }
        return hasPermissaoEfetivar;
    }

    public void estornarProcesso() {
        try {
            ValidacaoException ve = new ValidacaoException();
            selecionado = bloqueioJudicialFacade.estornarProcesso(selecionado, ve);
            redirecionarParaVisualiza("Estorno realizado com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao estornar processo. Detalhes: " + e.getMessage());
            logger.error("Erro ao estornar processo. ", e);
        }
    }

    public boolean canEditarPretty(Object objeto) {
        return objeto instanceof BloqueioJudicial && !isFinalizado((BloqueioJudicial) objeto) && !isEstornado((BloqueioJudicial) objeto);
    }

    public boolean canEditarBloqueio() {
        return selecionado != null && !isFinalizado(selecionado) && !isEstornado(selecionado);
    }

    private boolean isFinalizado(BloqueioJudicial bloqueioJudicial) {
        return SituacaoProcessoDebito.FINALIZADO.equals(bloqueioJudicial.getSituacao());
    }

    public boolean isEstornado(BloqueioJudicial bloqueioJudicial) {
        return SituacaoProcessoDebito.ESTORNADO.equals(bloqueioJudicial.getSituacao());
    }

    public boolean isEmAberto(BloqueioJudicial bloqueioJudicial) {
        return SituacaoProcessoDebito.EM_ABERTO.equals(bloqueioJudicial.getSituacao());
    }

    public boolean canEstornarBloqueio() {
        return selecionado != null && !isFinalizado(selecionado);
    }

    public void atualizarContribuinteBloqueio() {
        if (selecionado.getContribuintePenhora() == null) {
            selecionado.setContribuinteBloqueio(null);
            FacesUtil.atualizarComponente("Formulario:tabViewGeral:gridTabBloqueio");
            FacesUtil.atualizarComponente("Formulario:tabViewGeral:panelInfo");
        }
    }

    public String buscarValorCDA(Long idCda) {
        if (mapaValoresCDA == null) {
            mapaValoresCDA = Maps.newHashMap();
        }
        if (!mapaValoresCDA.containsKey(idCda)) {
            CertidaoDividaAtiva certidao = bloqueioJudicialFacade.recuperarCdaPeloId(idCda);
            mapaValoresCDA.put(idCda, bloqueioJudicialFacade.buscarValorAtualizadoCDA(certidao));
        }
        return Util.formataValor(mapaValoresCDA.get(idCda).getValorTotal());
    }

    public void redirecionarParaLoteArrecadacao() {
        FacesUtil.redirecionamentoExterno(FacesUtil.getRequestContextPath() + "/tributario/arrecadacao/ver/" + selecionado.getLoteBaixa().getId(), true);
    }
}
