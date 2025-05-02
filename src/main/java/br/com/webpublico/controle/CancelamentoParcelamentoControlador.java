package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.TermoProcessoParcelamento;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DemonstrativoCancelamentoParcelamento;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ManagedBean(name = "cancelamentoParcelamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCancelamentoPorParcelamento",
        pattern = "/parcelamento/cancelamento/novo/#{cancelamentoParcelamentoControlador.idParcelamento}/",
        viewId = "/faces/tributario/contacorrente/cancelamentoparcelamento/edita.xhtml"),
    @URLMapping(id = "novoCancelamentoParcelamento",
        pattern = "/parcelamento/cancelamento/novo/",
        viewId = "/faces/tributario/contacorrente/cancelamentoparcelamento/edita.xhtml"),
    @URLMapping(id = "verCancelamentoParcelamento",
        pattern = "/parcelamento/cancelamento/ver/#{cancelamentoParcelamentoControlador.id}/",
        viewId = "/faces/tributario/contacorrente/cancelamentoparcelamento/visualizar.xhtml"),
    @URLMapping(id = "listarCancelamentoParcelamento",
        pattern = "/parcelamento/cancelamento/listar/",
        viewId = "/faces/tributario/contacorrente/cancelamentoparcelamento/lista.xhtml"),
})
public class CancelamentoParcelamentoControlador extends PrettyControlador<CancelamentoParcelamento> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(CancelamentoParcelamentoControlador.class);
    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;
    @EJB
    private CancelamentoParcelamentoFacade cancelamentoParcelamentoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private DAMFacade damFacade;
    private TipoCadastroTributario tipoCadastro;
    private List<EnderecoCorreio> enderecos;
    private List<Telefone> telefones;
    private List<SociedadeCadastroEconomico> socios;
    private List<Propriedade> propriedadesBCI;
    private List<PropriedadeRural> propriedadesBCR;
    private EnderecoCorreio localizacao;
    private Lote lote;
    private Long idParcelamento;
    private String mensagemCancelamento;

    public CancelamentoParcelamentoControlador() {
        metadata = new EntidadeMetaData(CancelamentoParcelamento.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return cancelamentoParcelamentoFacade;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public List<EnderecoCorreio> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<EnderecoCorreio> enderecos) {
        this.enderecos = enderecos;
    }

    public List<Telefone> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<Telefone> telefones) {
        this.telefones = telefones;
    }

    public List<SociedadeCadastroEconomico> getSocios() {
        return socios;
    }

    public void setSocios(List<SociedadeCadastroEconomico> socios) {
        this.socios = socios;
    }

    public List<Propriedade> getPropriedadesBCI() {
        return propriedadesBCI;
    }

    public void setPropriedadesBCI(List<Propriedade> propriedadesBCI) {
        this.propriedadesBCI = propriedadesBCI;
    }

    public List<PropriedadeRural> getPropriedadesBCR() {
        return propriedadesBCR;
    }

    public void setPropriedadesBCR(List<PropriedadeRural> propriedadesBCR) {
        this.propriedadesBCR = propriedadesBCR;
    }

    public EnderecoCorreio getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(EnderecoCorreio localizacao) {
        this.localizacao = localizacao;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Long getIdParcelamento() {
        return idParcelamento;
    }

    public void setIdParcelamento(Long idParcelamento) {
        this.idParcelamento = idParcelamento;
    }

    public List<ParcelasCancelamentoParcelamento> getOriginais() {
        return processoParcelamentoFacade.getOriginais(selecionado);
    }

    public List<ParcelasCancelamentoParcelamento> getOriginaisAtualizadas() {
        return processoParcelamentoFacade.getOriginaisAtualizadas(selecionado);
    }

    public List<ParcelasCancelamentoParcelamento> getOriginadasPagas() {
        return processoParcelamentoFacade.getOriginadasPagas(selecionado);
    }

    public List<ParcelasCancelamentoParcelamento> getOriginadasPagasAtualizadas() {
        return processoParcelamentoFacade.getOriginadasPagasAtualizadas(selecionado);
    }

    public List<ParcelasCancelamentoParcelamento> getParcelasAbatidas() {
        return processoParcelamentoFacade.getParcelasAbatidas(selecionado);
    }

    public List<ParcelasCancelamentoParcelamento> getParcelasEmAbertas() {
        return selecionado.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_EM_ABERTO);
    }

    public String getMensagemCancelamento() {
        return mensagemCancelamento;
    }

    public void setMensagemCancelamento(String mensagemCancelamento) {
        this.mensagemCancelamento = mensagemCancelamento;
    }

    @Override
    @URLAction(mappingId = "verCancelamentoParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        processoParcelamentoFacade.atualizarValorNovaParcelaAberta(selecionado);
    }

    @Override
    public void editar() {
        super.editar();
        recuperarCadastros();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parcelamento/cancelamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoCancelamentoParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setExercicio(Util.getSistemaControlador().getExercicioCorrente());
        selecionado.setDataCancelamento(new Date());
        selecionado.setUsuario(Util.getSistemaControlador().getUsuarioCorrente());
    }

    public List<ProcessoParcelamento> completarProcessosParcelamento(String parte) {
        return processoParcelamentoFacade.buscarParcelamentosFinalizadosPorNumero(parte.trim());
    }

    private void validarCancelamento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getProcessoParcelamento() == null || selecionado.getProcessoParcelamento().getId() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Processo de Parcelamento deve ser informado!");
        } else {
            if (!selecionado.getProcessoParcelamento().getSituacaoParcelamento().isAtivo()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Processo de Parcelamento informado encontra-se " + selecionado.getProcessoParcelamento().getSituacaoParcelamento().getDescricao() + ". Somente processos de Parcelamento FINALIZADOS/REATIVADOS podem ser cancelados.");
            }
            if (selecionado.getMotivo().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Favor informar o motivo do cancelamento!");
            }
        }
        ve.lancarException();
    }

    public void cancelarProcessoParcelamento() {
        try {
            validarCancelamento();
            verificarSeExisteParcelasVencidasDeAcordoComParametro();
            FacesUtil.executaJavaScript("dialogoConfirmaCancelamento.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void verificarSeExisteParcelasVencidasDeAcordoComParametro() {
        ParamParcelamento paramParcelamento = selecionado.getProcessoParcelamento().getParamParcelamento();
        if (paramParcelamento.getParcelasInadimplencia() != null &&
            paramParcelamento.getParcelasInadimplencia() > 0) {

            int diasVencidosCancelamento = paramParcelamento.getDiasVencidosCancelamento() != null ? paramParcelamento.getDiasVencidosCancelamento() : 0;

            List<ResultadoParcela> resultados = new ConsultaParcela()
                .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO)
                .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, selecionado.getProcessoParcelamento().getId())
                .addComplementoDoWhere(" AND (" + ConsultaParcela.Campo.PARCELA_VENCIMENTO.getCampo() + " + " + diasVencidosCancelamento + ") " +
                    ConsultaParcela.Operador.MENOR_IGUAL.getOperador() + " to_date('" + DataUtil.getDataFormatada(selecionado.getDataCancelamento()) + "', 'dd/MM/yyyy')")
                .executaConsulta().getResultados();

            if (resultados.size() <= paramParcelamento.getParcelasInadimplencia()) {
                if (paramParcelamento.isInadimplenciaSucessiva() || paramParcelamento.isInadimplenciaIntercadala() && resultados.size() > 1) {
                    List<Integer> sequencias = Lists.newArrayList();
                    for (ResultadoParcela resultado : resultados) {
                        sequencias.add(resultado.getSequenciaParcela());
                    }
                    if (paramParcelamento.isInadimplenciaSucessiva() && !Util.isConsecutive(sequencias, paramParcelamento.getParcelasInadimplencia())) {
                        mensagemCancelamento = "O Parcelamento de Débitos ainda não possui " + paramParcelamento.getParcelasInadimplencia() + " parcelas sucessivas vencidas. Deseja efetivar o processamento do Cancelamento do Parcelamento de Débitos?";
                    }
                    if (paramParcelamento.isInadimplenciaIntercadala() && Util.isConsecutive(sequencias, paramParcelamento.getParcelasInadimplencia())) {
                        mensagemCancelamento = "O Parcelamento de Débitos ainda não possui " + paramParcelamento.getParcelasInadimplencia() + " parcelas intercaladas vencidas. Deseja efetivar o processamento do Cancelamento do Parcelamento de Débitos?";
                    }
                } else {
                    if (resultados.size() < paramParcelamento.getParcelasInadimplencia() && resultados.size() > 1) {
                        mensagemCancelamento = "O Parcelamento de Débitos ainda não possui " + paramParcelamento.getParcelasInadimplencia() + " parcelas vencidas. Deseja efetivar o processamento do Cancelamento do Parcelamento de Débitos?";
                    }
                }
                FacesUtil.atualizarComponente("formDialogoConfirmacao");
            }
        }
    }

    public void confirmarCancelamentoProcessoParcelamento() {
        try {
            selecionado.setCodigo(cancelamentoParcelamentoFacade.buscarProximoCodigoPorExercicio(selecionado.getExercicio()));
            selecionado.getProcessoParcelamento().setCancelamentoParcelamento(selecionado);
            if (!getParcelasEmAbertas().isEmpty()) {
                selecionado.setReferencia(getParcelasEmAbertas().get(0).getReferencia());
            }

            ProcessoParcelamento parcelamento = processoParcelamentoFacade.cancelarProcessoParcelamento(selecionado.getProcessoParcelamento());
            processoParcelamentoFacade.corrigirReferenciaDasParcelas(parcelamento.getCancelamentoParcelamento());
            selecionado = parcelamento.getCancelamentoParcelamento();

            if (selecionado != null) {
                processoParcelamentoFacade.salvarDataDePrescricaoParcelaValorDivida(parcelamento);
                FacesUtil.addInfo("Cancelamento do Processo de Parcelamento concluído com sucesso!", "");
                corrigirCancelamentoQuandoNaoGerouDebitoResidual(selecionado);
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            } else {
                FacesUtil.addError("Houve um erro ao cancelar o Processo de Parcelamento!", "");
            }
        } catch (Exception e) {
            FacesUtil.executaJavaScript("dialogoConfirmaCancelamento.hide()");
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoRealizada("Não foi possível cancelar o Processo de Parcelamento! " + e.getMessage());
        }
    }

    public void voltar() {
        super.cancelar();
    }

    public void emitirDemonstrativoCancelamento() {
        try {
            DemonstrativoCancelamentoParcelamento demonstrativo = new DemonstrativoCancelamentoParcelamento();
            demonstrativo.setParcelamento(selecionado.getProcessoParcelamento());
            demonstrativo.setCancelamentoParcelamento(selecionado);

            demonstrativo.setOriginais(selecionado.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ORIGINAIS));
            demonstrativo.setPagas(selecionado.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_PAGAS));
            demonstrativo.setPagasAtualizadas(selecionado.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_PAGAS_ATUALIZADAS));
            demonstrativo.setOriginaisAtualizadas(selecionado.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ORIGINAIS_ATUALIZADAS));
            demonstrativo.setAbatidas(selecionado.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ABATIDAS));
            demonstrativo.setAbertas(selecionado.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_EM_ABERTO));
            new TermoProcessoParcelamento().emitirDemonstrativoCancelamento(demonstrativo);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    @URLAction(mappingId = "novoCancelamentoPorParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void cancelamentoParcelamento() {
        novo();
        selecionado.setProcessoParcelamento(processoParcelamentoFacade.recuperar(idParcelamento));

        if (!isVerificarSePermiteCancelarProcesso()
            && selecionado.getProcessoParcelamento().getSituacaoParcelamento().isAtivo()) {
            FacesUtil.addOperacaoNaoPermitida("Esse parcelamento não pode ser cancelado!");
            FacesUtil.redirecionamentoInterno("/parcelamento/ver/" + selecionado.getProcessoParcelamento().getId() + "/");
        } else {
            recuperarCadastros();
            if (selecionado.getId() == null || selecionado.getParcelas().isEmpty()) {
                processoParcelamentoFacade.inicializarCancelametoDoParcelamento(selecionado,
                    processoParcelamentoFacade.getSistemaFacade().getUsuarioCorrente(),
                    Util.getSistemaControlador().getExercicioCorrente());
            }
        }
    }

    public void carregarParcelamentoParaCancelamento() {
        selecionado.setProcessoParcelamento(processoParcelamentoFacade.recuperar(selecionado.getProcessoParcelamento().getId()));
        recuperarCadastros();
    }

    public void carregarParcelasParaCancelamento() {
        try {
            validarBuscarParcelas();
            selecionado.setProcessoParcelamento(processoParcelamentoFacade.recuperar(selecionado.getProcessoParcelamento().getId()));
            processoParcelamentoFacade.inicializarCancelametoDoParcelamento(selecionado,
                processoParcelamentoFacade.getSistemaFacade().getUsuarioCorrente(),
                Util.getSistemaControlador().getExercicioCorrente());
            validarParcelasOriginais();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarParcelasOriginais() {
        ValidacaoException ve = new ValidacaoException();
        for (ParcelasCancelamentoParcelamento original : getOriginais()) {
            if (SituacaoParcela.ISOLAMENTO.equals(original.getSituacaoParcela())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existe(m) parcela(s) original(is) isolada(s)!");
                break;
            }
        }
        ve.lancarException();
    }

    private void validarBuscarParcelas() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getProcessoParcelamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Processo de Parcelamento!");
        }
        ve.lancarException();
    }

    public boolean isVerificarSePermiteCancelarProcesso() {
        return selecionado.getProcessoParcelamento() != null &&
            (selecionado.getProcessoParcelamento().getPermitirCancelarParcelamento() || selecionado.getProcessoParcelamento().isCancelado());
    }

    private void recuperarCadastros() {
        if (selecionado.getProcessoParcelamento() != null) {
            if (selecionado.getProcessoParcelamento().getCadastro() instanceof CadastroImobiliario) {
                CadastroImobiliario ci = processoParcelamentoFacade.getCadastroImobiliarioFacade()
                    .recuperarSemCalcular(selecionado.getProcessoParcelamento().getCadastro().getId());
                lote = ci.getLote();
                propriedadesBCI = ci.getPropriedadeVigente();
            }
            if (selecionado.getProcessoParcelamento().getCadastro() instanceof CadastroEconomico) {
                CadastroEconomico ce = processoParcelamentoFacade.getCadastroEconomicoFacade()
                    .recuperar(selecionado.getProcessoParcelamento().getCadastro().getId());
                socios = ce.getSociedadeCadastroEconomico();
                localizacao = ce.getLocalizacao().converterEnderecoCadastroEconomicoPrincipalParaEnderecoCorreio();
            }
            if (selecionado.getProcessoParcelamento().getCadastro() instanceof CadastroRural) {
                CadastroRural cr = processoParcelamentoFacade.getCadastroRuralFacade()
                    .recuperar(selecionado.getProcessoParcelamento().getCadastro().getId());
                propriedadesBCR = cr.getPropriedadesAtuais();
            }
            if (selecionado.getProcessoParcelamento().getCadastro() == null && selecionado.getProcessoParcelamento().getPessoa() != null) {
                Pessoa recuperar = processoParcelamentoFacade.getPessoaFacade()
                    .recuperar(selecionado.getProcessoParcelamento().getPessoa().getId());
                enderecos = recuperar.getEnderecos();
                telefones = recuperar.getTelefones();
            }
            tipoCadastro = selecionado.getProcessoParcelamento().getParamParcelamento().getTipoCadastroTributario();
        }
    }

    public BigDecimal getValorTotalDasOriginais() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginais()) {
            total = total.add(parcela.getValorTotalSemDesconto());
        }
        return total;
    }

    public BigDecimal getValorTotalImpostoDasOriginais() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginais()) {
            total = total.add(parcela.getValorImposto());
        }
        return total;
    }

    public BigDecimal getValorTotalTaxaDasOriginais() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginais()) {
            total = total.add(parcela.getValorTaxa());
        }
        return total;
    }

    public BigDecimal getValorTotalJurosDasOriginais() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginais()) {
            total = total.add(parcela.getValorJuros());
        }
        return total;
    }

    public BigDecimal getValorTotalMultaDasOriginais() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginais()) {
            total = total.add(parcela.getValorMulta());
        }
        return total;
    }

    public BigDecimal getValorTotalCorrecaoDasOriginais() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginais()) {
            total = total.add(parcela.getValorCorrecao());
        }
        return total;
    }

    public BigDecimal getValorTotalHonorariosDasOriginais() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginais()) {
            total = total.add(parcela.getValorHonorarios());
        }
        return total;
    }

    public BigDecimal getValorTotalDasPagas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagas()) {
            total = total.add(parcela.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorTotalImpostoDasPagas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagas()) {
            total = total.add(parcela.getValorImposto());
        }
        return total;
    }

    public BigDecimal getValorTotalTaxaDasPagas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagas()) {
            total = total.add(parcela.getValorTaxa());
        }
        return total;
    }

    public BigDecimal getValorTotalJurosDasPagas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagas()) {
            total = total.add(parcela.getValorJuros());
        }
        return total;
    }

    public BigDecimal getValorTotalMultaDasPagas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagas()) {
            total = total.add(parcela.getValorMulta());
        }
        return total;
    }

    public BigDecimal getValorTotalCorrecaoDasPagas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagas()) {
            total = total.add(parcela.getValorCorrecao());
        }
        return total;
    }

    public BigDecimal getValorTotalHonorariosDasPagas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagas()) {
            total = total.add(parcela.getValorHonorarios());
        }
        return total;
    }

    public BigDecimal getValorTotalDescontoDasPagas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagas()) {
            total = total.add(parcela.getValorDesconto());
        }
        return total;
    }

    public BigDecimal getValorTotalDasPagasAtualizadas() {
        return processoParcelamentoFacade.getValorTotalPago(selecionado);
    }

    public BigDecimal getValorTotalImpostoDasPagasAtualizadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagasAtualizadas()) {
            total = total.add(parcela.getValorImposto());
        }
        return total;
    }

    public BigDecimal getValorTotalTaxaDasPagasAtualizadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagasAtualizadas()) {
            total = total.add(parcela.getValorTaxa());
        }
        return total;
    }

    public BigDecimal getValorTotalJurosDasPagasAtualizadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagasAtualizadas()) {
            total = total.add(parcela.getValorJuros());
        }
        return total;
    }

    public BigDecimal getValorTotalMultaDasPagasAtualizadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagasAtualizadas()) {
            total = total.add(parcela.getValorMulta());
        }
        return total;
    }

    public BigDecimal getValorTotalCorrecaoDasPagasAtualizadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagasAtualizadas()) {
            total = total.add(parcela.getValorCorrecao());
        }
        return total;
    }

    public BigDecimal getValorTotalHonorariosDasPagasAtualizadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagasAtualizadas()) {
            total = total.add(parcela.getValorHonorarios());
        }
        return total;
    }

    public BigDecimal getValorTotalDescontoDasPagasAtualizadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginadasPagasAtualizadas()) {
            total = total.add(parcela.getValorDesconto());
        }
        return total;
    }

    public BigDecimal getValorTotalDasOriginaisAtualizadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginaisAtualizadas()) {
            total = total.add(parcela.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorTotalImpostoDasOriginaisAtualizadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginaisAtualizadas()) {
            total = total.add(parcela.getValorImposto());
        }
        return total;
    }

    public BigDecimal getValorTotalTaxaDasOriginaisAtualizadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginaisAtualizadas()) {
            total = total.add(parcela.getValorTaxa());
        }
        return total;
    }

    public BigDecimal getValorTotalJurosDasOriginaisAtualizadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginaisAtualizadas()) {
            total = total.add(parcela.getValorJuros());
        }
        return total;
    }

    public BigDecimal getValorTotalMultaDasOriginaisAtualizadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginaisAtualizadas()) {
            total = total.add(parcela.getValorMulta());
        }
        return total;
    }

    public BigDecimal getValorTotalCorrecaoDasOriginaisAtualizadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginaisAtualizadas()) {
            total = total.add(parcela.getValorCorrecao());
        }
        return total;
    }

    public BigDecimal getValorTotalHonorariosDasOriginaisAtualizadas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getOriginaisAtualizadas()) {
            total = total.add(parcela.getValorHonorarios());
        }
        return total;
    }

    public BigDecimal getValorTotalDasAbatidas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getParcelasAbatidas()) {
            total = total.add(parcela.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorTotalPagoDasAbatidas() {
        return getParcelasAbatidas().stream().map(ParcelasCancelamentoParcelamento::getValorPago).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorTotalImpostoDasAbatidas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getParcelasAbatidas()) {
            total = total.add(parcela.getValorImposto());
        }
        return total;
    }

    public BigDecimal getValorTotalTaxaDasAbatidas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getParcelasAbatidas()) {
            total = total.add(parcela.getValorTaxa());
        }
        return total;
    }

    public BigDecimal getValorTotalJurosDasAbatidas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getParcelasAbatidas()) {
            total = total.add(parcela.getValorJuros());
        }
        return total;
    }

    public BigDecimal getValorTotalMultaDasAbatidas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getParcelasAbatidas()) {
            total = total.add(parcela.getValorMulta());
        }
        return total;
    }

    public BigDecimal getValorTotalCorrecaoDasAbatidas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getParcelasAbatidas()) {
            total = total.add(parcela.getValorCorrecao());
        }
        return total;
    }

    public BigDecimal getValorTotalHonorariosDasAbatidas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getParcelasAbatidas()) {
            total = total.add(parcela.getValorHonorarios());
        }
        return total;
    }

    public BigDecimal getValorTotalDasAbertas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getParcelasEmAbertas()) {
            total = total.add(parcela.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorTotalImpostoDasAbertas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getParcelasEmAbertas()) {
            total = total.add(parcela.getValorImposto());
        }
        return total;
    }

    public BigDecimal getValorTotalTaxaDasAbertas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getParcelasEmAbertas()) {
            total = total.add(parcela.getValorTaxa());
        }
        return total;
    }

    public BigDecimal getValorTotalJurosDasAbertas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getParcelasEmAbertas()) {
            total = total.add(parcela.getValorJuros());
        }
        return total;
    }

    public BigDecimal getValorTotalMultaDasAbertas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getParcelasEmAbertas()) {
            total = total.add(parcela.getValorMulta());
        }
        return total;
    }

    public BigDecimal getValorTotalCorrecaoDasAbertas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getParcelasEmAbertas()) {
            total = total.add(parcela.getValorCorrecao());
        }
        return total;
    }

    public BigDecimal getValorTotalHonorariosDasAbertas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelasCancelamentoParcelamento parcela : getParcelasEmAbertas()) {
            total = total.add(parcela.getValorHonorarios());
        }
        return total;
    }

    public void redirecionarVoltar() {
        if (idParcelamento == null) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
        } else {
            FacesUtil.redirecionamentoInterno("/parcelamento/ver/" + idParcelamento + "/");
        }
    }

    public boolean isPossivelProcessar() {
        boolean retorno = false;
        if (selecionado.getProcessoParcelamento() != null && selecionado.getProcessoParcelamento().getSituacaoParcelamento().isAtivo()) {
            retorno = true;
        }
        for (ParcelasCancelamentoParcelamento original : getOriginais()) {
            if (SituacaoParcela.ISOLAMENTO.equals(original.getSituacaoParcela())) {
                retorno = false;
            }
        }
        return retorno;
    }

    public void uploadArquivo(FileUploadEvent file) {
        try {
            Arquivo arq = new Arquivo();
            arq.setNome(file.getFile().getFileName());
            arq.setMimeType(arquivoFacade.getMimeType(file.getFile().getFileName()));
            arq.setDescricao(new Date().toString());
            arq.setTamanho(file.getFile().getSize());

            CancelamentoParcelamentoArquivo proArq = new CancelamentoParcelamentoArquivo();
            proArq.setArquivo(arquivoFacade.novoArquivoMemoria(arq, file.getFile().getInputstream()));
            proArq.setCancelamentoParcelamento(selecionado);

            selecionado.getArquivos().add(proArq);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public void removerArquivo(CancelamentoParcelamentoArquivo arquivo) {
        selecionado.getArquivos().remove(arquivo);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void corrigirParcelasDeTodosCancelamentos() {
        List<BigDecimal> ids = cancelamentoParcelamentoFacade.buscarIdsCancelamentos();
        for (BigDecimal id : ids) {
            CancelamentoParcelamento cancelamento = cancelamentoParcelamentoFacade.recuperar(id.longValue());
            cancelamentoParcelamentoFacade.corrigirTributosDoCancelamento(cancelamento);
            logger.debug("Corrigiu o cancelamento: " + (ids.indexOf(id) + 1) + "/" + ids.size());
        }
    }

    public void corrigirCancelamentoQuandoNaoGerouDebitoResidual(CancelamentoParcelamento cancelamentoParcelamento) {
        if (cancelamentoParcelamento.getProcessoCalculo() != null) {
            ValorDivida valorDivida = damFacade.buscarUltimoValorDividaDoCalculo(cancelamentoParcelamento.getId());
            cancelamentoParcelamentoFacade.corrigirCancelamentoQuandoNaoGerouDebitoResidual(cancelamentoParcelamento, valorDivida);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void corrigirTodosCancelamentoQuandoNaoGerouDebitoResidual() {
        List<BigDecimal> ids = cancelamentoParcelamentoFacade.buscarIdsCancelamentosQueNaoGerouResidual();
        for (BigDecimal id : ids) {
            CancelamentoParcelamento cancelamento = cancelamentoParcelamentoFacade.recuperar(id.longValue());
            corrigirCancelamentoQuandoNaoGerouDebitoResidual(cancelamento);
        }
    }

    private void corrigirCancelamentoQuandoNaoGerouDebitoResidual(CancelamentoParcelamento cancelamentoParcelamento, ValorDivida valorDivida) {
        try {
            if (valorDivida == null) {
                valorDivida = new ValorDivida();
                valorDivida.setCalculo(cancelamentoParcelamento);
                valorDivida.setValor(cancelamentoParcelamento.getValorReal());
                valorDivida.setDivida(cancelamentoParcelamento.getParcelaValorDivida().getValorDivida().getDivida());
                valorDivida.setEmissao(cancelamentoParcelamento.getDataCancelamento());
                valorDivida.setExercicio(cancelamentoParcelamento.getParcelaValorDivida().getValorDivida().getExercicio());

                cancelamentoParcelamentoFacade.criarItemValorDivida(cancelamentoParcelamento, valorDivida);
                cancelamentoParcelamentoFacade.criarParcelaValorDivida(cancelamentoParcelamento, valorDivida);

                cancelamentoParcelamentoFacade.getDividaAtivaDAO().persisteValorDivida(valorDivida, false);
            } else {
                if (valorDivida.getItemValorDividas().isEmpty()) {
                    cancelamentoParcelamentoFacade.criarItemValorDivida(cancelamentoParcelamento, valorDivida);
                    cancelamentoParcelamentoFacade.getDividaAtivaDAO().persisteItemValorDivida(valorDivida.getItemValorDividas());
                }
                if (valorDivida.getParcelaValorDividas().isEmpty()) {
                    cancelamentoParcelamentoFacade.criarParcelaValorDivida(cancelamentoParcelamento, valorDivida);
                    cancelamentoParcelamentoFacade.getDividaAtivaDAO().persisteParcelaValorDivida(valorDivida.getParcelaValorDividas(), false);
                }
            }
            processoParcelamentoFacade.corrigirReferenciaDasParcelas(cancelamentoParcelamento);
            cancelamentoParcelamentoFacade.corrigirTributosDoCancelamento(cancelamentoParcelamento);
        } catch (Exception ex) {
            logger.error("Erro ao corrigir o cancelamento do parcelamento: {}", ex);
        }
    }

    public void mostrarParcelamentosCanceladosErrados() {
        processoParcelamentoFacade.buscarParcelamentosCanceladosErrados();
    }

}
