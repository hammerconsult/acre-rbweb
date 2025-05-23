package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.entidadesauxiliares.administrativo.FiltroOrdemCompraServicoContratoVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-contrato-adm",
        pattern = "/contrato-adm/novo/",
        viewId = "/faces/administrativo/contrato/contrato-adm/edita.xhtml"),

    @URLMapping(id = "editar-contrato-adm",
        pattern = "/contrato-adm/editar/#{contratoAdmControlador.id}/",
        viewId = "/faces/administrativo/contrato/contrato-adm/edita.xhtml"),

    @URLMapping(id = "ver-contrato-adm",
        pattern = "/contrato-adm/ver/#{contratoAdmControlador.id}/",
        viewId = "/faces/administrativo/contrato/contrato-adm/visualizar.xhtml"),

    @URLMapping(id = "listar-contrato-adm",
        pattern = "/contrato-adm/listar/",
        viewId = "/faces/administrativo/contrato/contrato-adm/lista.xhtml")
})
public class ContratoAdmControlador extends PrettyControlador<Contrato> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ContratoAdmControlador.class);
    @EJB
    private ContratoFacade facade;
    private AtaRegistroPreco ataRegistroPreco;
    private ExecucaoContrato execucaoContratoSelecionada;
    private OrdemDeServicoContrato ordemDeServicoSelecionada;
    private CaucaoContrato caucaoContratoSelecionada;
    private FiscalContrato fiscalContrato;
    private PenalidadeContrato penalidadeContratoSelecionado;
    private List<Pessoa> fornecedoresGanhadoresLicitacao;
    private NotaFiscalExecucaoContrato notaFiscalExecucaoContratoSelecionado;
    private ContatoContrato contatoContratoSelecionado;
    private DocumentoContrato documentoContratoSelecionado;
    private ConverterAutoComplete converterItemContrato;
    private OrdemDeCompraContrato ordemDeCompraSelecionada;
    private List<ItemOrdemDeCompra> itensOrdemDeCompra;
    private RescisaoContrato rescisaoContratoSelecionado;
    private List<ResultadoParcela> resultadoParcelaCalculoDividaDiversaDaPenalidade;
    private TipoCaucaoContrato tipoCaucaoContrato;
    private CaucaoContrato caucaoContratoAtivo;
    private TipoMovimentacaoContrato tipoMovimentacaoContratoSelecionado;
    private ItemContrato itemContrato;
    private boolean todosItensValorSelecionados;
    private boolean todosItensQuantidadeSelecionados;
    private Integer indiceAba;
    private GestorContrato gestorContrato;
    private List<ItemContrato> itensControleValor;
    private List<ItemContrato> itensControleQuantidade;
    private ObjetoContrato objetoContrato;
    private List<ResultadoParcela> resultadoParcelas;
    private ConsultaDebitoControlador.TotalTabelaParcelas resultadoParcelaTotalizador;
    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;
    private FiltroOrdemCompraServicoContratoVO filtroOrdemCompraVO;
    private FiltroResumoExecucaoVO filtroResumoExecucaoVO;
    private boolean responsavelUnidadePessoaFisica;
    private List<TipoObjetoCompraDeferimentoContrato> tiposObjetoCompraDeferimento;
    private String numeroTermo;
    private ContratoAprovacaoVO contratoAprovacaoVO;
    private List<MovimentoItemContrato> movimentosItensRescisaoContrato;

    public ContratoAdmControlador() {
        super(Contrato.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-contrato-adm", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
        selecionado.setExercicioContrato(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setSituacaoContrato(SituacaoContrato.EM_ELABORACAO);
        selecionado.setTipoAquisicao(TipoAquisicaoContrato.LICITACAO);
        objetoContrato = new ObjetoContrato();
        tipoCaucaoContrato = TipoCaucaoContrato.EM_DINHEIRO;
        itemContrato = null;
        indiceAba = 0;
        instanciarListasItemContrato();
        setResponsavelUnidadePessoaFisica(false);
    }

    @URLAction(mappingId = "editar-contrato-adm", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarDadosEditar();
        popularObjetoContrato();
        recuperarItensFornecedorVencedorProcessoLicitatorio();
        recuperarAtaRegistroPreco();
        setResponsavelUnidadePessoaFisica(facade.getConfiguracaoLicitacaoFacade().verificarUnidadeTercerializadaRh(selecionado.getUnidadeAdministrativa().getSubordinada()));
        Collections.sort(selecionado.getAlteracoesContratuais());
        Collections.sort(selecionado.getAjustes());
    }

    @URLAction(mappingId = "ver-contrato-adm", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        instanciarListasItemContrato();
        carregarQuantidadesValoresDosItens(selecionado.getItens());
        separarItensContratoValorQuantidade(selecionado.getItens());
        ordenarExecucoes();
        popularObjetoContrato();
        buscarParcelas();
        Collections.sort(selecionado.getAlteracoesContratuais());
        Collections.sort(selecionado.getAjustes());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contrato-adm/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void recuperarDadosEditar() {
        carregarQuantidadesValoresDosItens(selecionado.getItens());
        tipoCaucaoContrato = TipoCaucaoContrato.EM_DINHEIRO;
        if (selecionado.getCaucoes() != null && !selecionado.getCaucoes().isEmpty()) {
            Collections.sort(selecionado.getCaucoes());
        }
        ordenarExecucoes();
    }

    private void ordenarExecucoes() {
        if (selecionado.hasExecucoes()) {
            Collections.sort(selecionado.getExecucoes());
        }
    }

    @Override
    public void salvar() {
        try {
            validarRegraisGeraisContrato();
            adicionarItemContrato();
            selecionado = facade.salvarNovoContrato(selecionado);
            FacesUtil.addOperacaoRealizada("O contrato " + selecionado + " foi salvo com sucesso.");
            redirecionarParaVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void adicionarItemContrato() {
        selecionado.setItens(new ArrayList<ItemContrato>());
        adicionarItensSelecionadosNoItemContrato(getItensControleValor());
        adicionarItensSelecionadosNoItemContrato(getItensControleQuantidade());
    }

    private void adicionarItensSelecionadosNoItemContrato(List<ItemContrato> itens) {
        for (ItemContrato item : itens) {
            if (item.hasValorTotalMaiorQueZero() && item.getSelecionado()) {
                if (item.getControleValor()) {
                    item.setValorUnitario(item.getValorTotal());
                }
                selecionado.getItens().add(item);
            }
        }
    }

    public void aprovarContrato() {
        try {
            validarRegraisGeraisContrato();
            validarGestorAndFiscalVigente();
            facade.validarUnidadeSingleton(selecionado);
            selecionado.setDataAprovacao(facade.getSistemaFacade().getDataOperacao());
            definirNumeroAnoTermoContrato();
            contratoAprovacaoVO = facade.simularAprovacaoContratoVO(selecionado);
            FacesUtil.executaJavaScript("dlgAprovarContrato.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("dlgAprovarContrato.hide()");
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void cancelarAprovacaoContrato() {
        selecionado.setDataAprovacao(null);
    }

    public void confirmarAprovacaoContrato() {
        try {
            validarNumeroTermo();
            selecionado.setNumeroTermo(numeroTermo);
            validarDataAprovacao();
            FacesUtil.executaJavaScript("dlgAprovarContrato.hide()");
            adicionarItemContrato();
            facade.validarUnidadeSingleton(selecionado);
            selecionado = facade.aprovarContrato(selecionado);
            finalizarAprovacaoContrato();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            facade.desbloquearUnidadeSingleton(selecionado);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            facade.desbloquearUnidadeSingleton(selecionado);
            descobrirETratarException(e);
        }
    }

    private void finalizarAprovacaoContrato() {
        if (!selecionado.getContratoConcessao() && DataUtil.dataSemHorario(selecionado.getInicioExecucao()).compareTo(DataUtil.dataSemHorario(facade.getSistemaFacade().getDataOperacao())) >= 0) {
            FacesUtil.executaJavaScript("dlgFinalizarProcesso.show()");
        } else {
            FacesUtil.addOperacaoRealizada("Contrato aprovado com sucesso.");
            redirecionarParaVer();
        }
    }

    private void validarNumeroTermo() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(numeroTermo)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo numero do termo deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarDataAprovacao() {
        facade.validarDataAprovacao(new ContratoValidacao(selecionado));
    }

    private void validarCamposSelecionado(ValidacaoException ve) {
        Util.validarCampos(selecionado);
        if (!selecionado.isContratoEmElaboracao()) {
            validarDataAprovacao();
        }
        if (selecionado.getTerminoVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Término de Vigência Atual deve ser informado.");
        }
        if (selecionado.getInicioExecucao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início de Execução deve ser informado.");
        }
        if (selecionado.getTerminoExecucao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Término de Execução deve ser informado.");
        }
        if (!selecionado.isDeRegistroDePrecoExterno()) {
            if (Strings.isNullOrEmpty(selecionado.getRegimeExecucao())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Regime de Execução deve ser informado.");
            }
        }
        if (!hasItensContratoPorQuantidade() && !hasItensContratoPorValor()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato não possui itens para continuar.");
        }
        ve.lancarException();
    }

    private void validarRegraisGeraisContrato() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        validarCamposSelecionado(ve);
        validarAtaRegistroPreco();
        if (selecionado.getSituacaoContrato().isDeferido()) {
            facade.getConfiguracaoLicitacaoFacade().validarAnexoObrigatorio(selecionado.getDetentorDocumentoLicitacao(), selecionado.getTipoAnexo());
        }
        validarDatasVigenciaAndExecucao(ve);
        validarInicioVigenciaContrato(ve);
        validarSelecionarItemContrato();
        ve.lancarException();
    }

    private void validarInicioVigenciaContrato(ValidacaoException ve) {
        Date dataProcesso;
        switch (selecionado.getTipoAquisicao()) {
            case LICITACAO:
            case ADESAO_ATA_REGISTRO_PRECO_INTERNA:
            case INTENCAO_REGISTRO_PRECO:
                dataProcesso = selecionado.getLicitacao().getAbertaEm();
                break;
            case PROCEDIMENTO_AUXILIARES:
                dataProcesso = selecionado.getLicitacao().getEmitidaEm();
                break;
            case DISPENSA_DE_LICITACAO:
                dataProcesso = selecionado.getDispensaDeLicitacao().getDataDaDispensa();
                break;
            case REGISTRO_DE_PRECO_EXTERNO:
                dataProcesso = selecionado.getRegistroSolicitacaoMaterialExterno().getDataRegistroCarona();
                break;
            default:
                dataProcesso = new Date();
        }
        if (!selecionado.isContratoVigente() && DataUtil.dataSemHorario(selecionado.getInicioVigencia()).before(DataUtil.dataSemHorario(dataProcesso))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O início de vigência do contrato deve ser posterior ou igual a data: " + DataUtil.getDataFormatada(dataProcesso)
                + ", para o processo de " + selecionado.getTipoAquisicao().getDescricao() + ".");
        }
    }

    private void validarDatasVigenciaAndExecucao(ValidacaoException ve) {
        if (DataUtil.dataSemHorario(selecionado.getTerminoVigencia()).before(DataUtil.dataSemHorario(selecionado.getInicioVigencia()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo término de vigência deve ser posterior ou igual ao início de vigência do contrato.");
        }
        if (DataUtil.dataSemHorario(selecionado.getTerminoExecucao()).before(DataUtil.dataSemHorario(selecionado.getInicioExecucao()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo término de execução deve ser posterior ou igual ao início de execução do contrato.");
        }
        if (DataUtil.dataSemHorario(selecionado.getInicioExecucao()).before(DataUtil.dataSemHorario(selecionado.getInicioVigencia()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo início de execução deve ser posterior ou igual ao início de vigência do contrato.");
        }
        if (DataUtil.dataSemHorario(selecionado.getTerminoVigencia()).before(DataUtil.dataSemHorario(selecionado.getInicioVigencia()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo término de execução deve ser posterior ou igual ao início de vigência do contrato.");
        }
        Calendar calendar = Calendar.getInstance();
        if (DataUtil.dataSemHorario(selecionado.getTerminoExecucaoAtual()) != null
            && selecionado.getTerminoExecucaoAtual().after(DataUtil.getUltimoDiaAno(calendar.get(Calendar.YEAR)))
            && TipoContrato.MATERIAL.equals(selecionado.getTipoContrato())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O vencimento do contrato ultrapassa o exercício vigente.");
        }
    }

    private void preencherDadosDeferimento() {
        try {
            Map<TipoObjetoCompra, BigDecimal> mapValorItemContrato = new HashMap<>();
            for (ItemContrato item : selecionado.getItens()) {
                TipoObjetoCompra tipoObjetoCompra = item.getItemAdequado().getObjetoCompra().getTipoObjetoCompra();
                if (item.hasValorTotalMaiorQueZero() && tipoObjetoCompra.isPermanenteOrConsumo()) {
                    if (!mapValorItemContrato.containsKey(tipoObjetoCompra)) {
                        mapValorItemContrato.put(tipoObjetoCompra, BigDecimal.ZERO);
                    }
                    mapValorItemContrato.put(tipoObjetoCompra, mapValorItemContrato.get(tipoObjetoCompra).add(item.getValorTotal()));
                }
            }
            tiposObjetoCompraDeferimento = Lists.newArrayList();
            for (Map.Entry<TipoObjetoCompra, BigDecimal> entry : mapValorItemContrato.entrySet()) {
                TipoObjetoCompra tipoObjetoCompra = entry.getKey();
                if (tipoObjetoCompra.isPermanenteOrConsumo()) {
                    TipoObjetoCompraDeferimentoContrato dc = new TipoObjetoCompraDeferimentoContrato();
                    dc.setTipoObjetoCompra(tipoObjetoCompra);
                    dc.setValorContratado(entry.getValue());
                    dc.setValorExecutado(facade.getExecucaoContratoFacade().buscarValorExecutadoPorTipoObjetoCompra(selecionado, tipoObjetoCompra));
                    dc.setValorEmpenhado(facade.getEmpenhoFacade().buscarValorEmpenhadoPorTipoObjetoCompra(selecionado, tipoObjetoCompra, CategoriaOrcamentaria.NORMAL));
                    dc.setValorEstornado(facade.getExecucaoContratoEstornoFacade().buscarValorEstornadoPorTipoObjetoCompra(selecionado, tipoObjetoCompra));

                    tiposObjetoCompraDeferimento.add(dc);
                }
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void validarDeferimentoContrato() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.getContratoConcessao()) {
            if (selecionado.getExecucoes().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para deferir o contrato é necessário lançar ao menos uma execução.");
            }
            if (!selecionado.isContratoEmElaboracao()) {
                validarDataAprovacao();
            }
            ve.lancarException();
        }
    }

    private void validarExecucaoEmpenhada() {
        ValidacaoException ve = new ValidacaoException();

        boolean lancarMsg = false;
        for (ExecucaoContrato execucaoContrato : selecionado.getExecucoes()) {
            for (ExecucaoContratoEmpenho exeEmp : execucaoContrato.getEmpenhos()) {
                if (exeEmp.getEmpenho() == null && !exeEmp.getSolicitacaoEmpenho().getEstornada()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A solitação de empenho: " + exeEmp.getSolicitacaoEmpenho() + " não foi empenhada.");
                    lancarMsg = true;
                }
            }
        }
        if (lancarMsg) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Para continuar, é necessário que todas execuções estejam empenhadas.");
        }
        ve.lancarException();
    }

    private void validarSaldoContratoExecutado() {
        if (hasDiferencaContratoExecutado()) {
            for (TipoObjetoCompraDeferimentoContrato dc : tiposObjetoCompraDeferimento) {
                dc.getMensagens().add("O contrato possui uma diferença entre o valor contratado e o valor executado. Diferença: (<b>" + Util.formataValor(dc.getSaldoRestante()) + "</b>.)");
                if (dc.getSaldoRestante().compareTo(BigDecimal.ZERO) > 0) {
                    dc.getMensagens().add("Nesse caso, é necessário realizar as execuções/empenhos do saldo restante (<b>" + Util.formataValor(dc.getSaldoRestante()) + "</b>) para continuar com o deferimento do contrato. ");
                }
                if (dc.getSaldoRestante().compareTo(BigDecimal.ZERO) < 0) {
                    dc.getMensagens().add("Nesse caso, o valor executado/empenhado foi maior que o valor contratado. Entre em contato com o suporte para verificar a diferença. ");
                }
            }
        }
    }

    public boolean hasDiferencaContratoExecutado() {
        for (TipoObjetoCompraDeferimentoContrato dc : tiposObjetoCompraDeferimento) {
            return dc.hasDiferenca();
        }
        return false;
    }

    private void validarDataDeferimentoContrato() {
        facade.validarDataDeferimentoContrato(new ContratoValidacao(selecionado));
    }

    public void deferirContrato() {
        try {
            facade.getConfiguracaoLicitacaoFacade().validarAnexoObrigatorio(selecionado.getDetentorDocumentoLicitacao(), selecionado.getTipoAnexo());
            validarDeferimentoContrato();
            preencherDadosDeferimento();
            validarSaldoContratoExecutado();
            if (hasDiferencaContratoExecutado()) {
                FacesUtil.executaJavaScript("dlgTipoObetoDeferimento.show()");
                FacesUtil.atualizarComponente("formDlgTipoObetoDeferimento");
                return;
            }
            validarExecucaoEmpenhada();
            selecionado.setDataDeferimento(facade.getSistemaFacade().getDataOperacao());
            FacesUtil.executaJavaScript("dlgDeferirContrato.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void cancelarDeferimentoContrato() {
        selecionado.setDataDeferimento(null);
        selecionado.setDataAssinatura(null);
    }

    public void confirmarDeferimentoContrato() {
        try {
            validarDataDeferimentoContrato();
            selecionado = facade.deferirContrato(selecionado);
            FacesUtil.addOperacaoRealizada("O contrato " + selecionado + " foi deferido com sucesso.");
            redirecionarParaVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public void redirecionarParaNovaExecucao() {
        Web.poeNaSessao("CONTRATO", selecionado);
        FacesUtil.redirecionamentoInterno("/execucao-contrato-adm/novo/");
    }

    public List<Contrato> completaContratoUnidadeLogada(String filtro) {
        return facade.listaFiltrandoContrato(filtro, facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
    }

    public List<ParticipanteIRP> completarUnidadeParticipante(String parte) {
        if (selecionado.getLicitacao() != null) {
            return facade.getParticipanteIRPFacade().buscarParticipantesIRPAprovado(parte.trim(), selecionado.getLicitacao());
        }
        return Lists.newArrayList();
    }

    public List<HierarquiaOrganizacional> completarUnidadeContrato(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaHorganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public void validarUnidadeAtaRegistroPrecoUtilizadaContrato() {
        if (isRegistroDePrecos()
            && selecionado.getLicitacao() != null
            && ataRegistroPreco != null
            && selecionado.getUnidadeAdministrativa() != null) {
            List<Contrato> contratos = facade.buscarContratoPorAtaAndUnidade(selecionado.getUnidadeAdministrativa().getSubordinada(), ataRegistroPreco);
            if (contratos != null && !contratos.isEmpty()) {
                if (!selecionado.getObjetoAdequado().getProcessoDeCompra().getUnidadeOrganizacional().equals(selecionado.getUnidadeAdministrativa().getSubordinada())) {
                    for (Contrato contrato : contratos) {
                        FacesUtil.addOperacaoNaoPermitida("A unidade " + selecionado.getUnidadeAdministrativa() + " referente a ata já foi utilizada no contrato: " + contrato + ".");
                        selecionado.setUnidadeAdministrativa(null);
                    }
                }
            }
        }
        recuperarAtaRegistroPreco();
        if (selecionado.getUnidadeAdministrativa() != null) {
            setResponsavelUnidadePessoaFisica(facade.getConfiguracaoLicitacaoFacade().verificarUnidadeTercerializadaRh(selecionado.getUnidadeAdministrativa().getSubordinada()));
        }
    }

    public List<UnidadeOrganizacional> completarResponsavelGarantia(String parte) {
        return facade.getUnidadeOrganizacionalFacade().filtrarUnidadeOrganizacionaisAtivas(parte.trim());
    }

    public List<Pessoa> completaContratado(String parte) {
        return facade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public List<PessoaFisica> completarPessoaFisica(String parte) {
        return facade.getPessoaFisicaFacade().listaFiltrandoTodasPessoasByNomeAndCpf(parte.trim());
    }

    public List<ModeloDocumentoContrato> completaModeloDocumentoContrato(String parte) {
        return facade.getModeloDocumentoContratoFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<ContratoFP> completarContratosFPVigentes(String parte) {
        return facade.getContratoFPFacade().recuperarFiltrandoContratosVigentesEm(parte.trim(), UtilRH.getDataOperacao());
    }

    public List<ContratoFP> completarContratosFP(String parte) {
        return facade.getContratoFPFacade().buscarContratos(parte.trim());
    }

    public List<Contrato> completaContrato(String parte) {
        return facade.buscarContratoPorNumeroOrExercicio(parte.trim());
    }

    public List<Pessoa> completaPessoaFisica(String parte) {
        return facade.getPessoaFacade().listaPessoasFisicas(parte.trim());
    }

    public List<VeiculoDePublicacao> completaVeiculoDePublicacao(String parte) {
        return facade.getVeiculoDePublicacaoFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<SelectItem> getTiposAquisicaoContrato() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoAquisicaoContrato object : TipoAquisicaoContrato.values()) {
            if (!TipoAquisicaoContrato.CONTRATOS_VIGENTE.equals(object)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTiposDeCaucoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoCaucaoContrato object : TipoCaucaoContrato.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposOrdemDeServico() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoOrdemDeServicoContrato object : TipoOrdemDeServicoContrato.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposFiscal() {
        return Util.getListSelectItemSemCampoVazio(TipoFiscalContrato.values(), false);
    }

    public List<SelectItem> getTiposDePenalidade() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoPenalidadeContrato object : TipoPenalidadeContrato.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public MoneyConverter getMoneyConverter() {
        return new MoneyConverter();
    }

    public ConverterAutoComplete getConverterItemContrato() {
        if (converterItemContrato == null) {
            converterItemContrato = new ConverterAutoComplete(ItemContrato.class, facade.getItemContratoFacade());
        }
        return converterItemContrato;
    }

    public ConverterAutoComplete getConverterPessoa() {
        return new ConverterAutoComplete(Pessoa.class, facade.getPessoaFacade());
    }

    public ConverterAutoComplete getConverterPessoaJuridica() {
        return new ConverterAutoComplete(PessoaJuridica.class, facade.getPessoaJuridicaFacade());
    }

    @Deprecated
    private boolean validarConfirmacao(ValidadorEntidade obj) {
        if (!Util.validaCampos(obj)) {
            return false;
        }
        try {
            obj.validarConfirmacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return false;
        }
        return true;
    }

    public void novoOrdemDeServico() {
        this.ordemDeServicoSelecionada = new OrdemDeServicoContrato();
        this.ordemDeServicoSelecionada.setContrato(selecionado);
        this.ordemDeServicoSelecionada.setNumero(getProximoNumero(selecionado.getOrdensDeServico()));
        this.ordemDeServicoSelecionada.setOrigem(OrigemOrdemServico.CONTRATO);
    }

    public void cancelarOrdemDeServico() {
        this.ordemDeServicoSelecionada = null;
    }

    public void confirmarOrdemDeServico() {
        if (!validarConfirmacao(this.ordemDeServicoSelecionada)) {
            return;
        }
        selecionado.setOrdensDeServico(Util.adicionarObjetoEmLista(selecionado.getOrdensDeServico(), this.ordemDeServicoSelecionada));
        cancelarOrdemDeServico();
    }

    public void selecionarOrdemDeServico(OrdemDeServicoContrato os) {
        this.ordemDeServicoSelecionada = os;
    }

    public void removerOrdemDeServico(OrdemDeServicoContrato os) {
        selecionado.getOrdensDeServico().remove(os);
    }

    public void definirCaucaoContratoPorTipo() {
        switch (this.getTipoCaucaoContrato()) {
            case EM_DINHEIRO:
                this.caucaoContratoSelecionada = new DinheiroCaucao();
                this.caucaoContratoSelecionada.setTipo(TipoCaucaoContrato.EM_DINHEIRO);
                this.caucaoContratoSelecionada.setContrato(selecionado);
                recuperarUltimaGarantiaAdicionada();
                break;
            case TITULOS_DA_DIVIDA_PUBLICA:
                this.caucaoContratoSelecionada = new TituloDividaPublicaCaucao();
                this.caucaoContratoSelecionada.setTipo(TipoCaucaoContrato.TITULOS_DA_DIVIDA_PUBLICA);
                this.caucaoContratoSelecionada.setContrato(selecionado);
                recuperarUltimaGarantiaAdicionada();
                break;
            case SEGURO_GARANTIA:
                this.caucaoContratoSelecionada = new SeguroGarantiaCaucao();
                this.caucaoContratoSelecionada.setTipo(TipoCaucaoContrato.SEGURO_GARANTIA);
                this.caucaoContratoSelecionada.setContrato(selecionado);
                recuperarUltimaGarantiaAdicionada();
                break;
            case FIANCA_BANCARIA:
                this.caucaoContratoSelecionada = new FiancaBancariaCaucao();
                this.caucaoContratoSelecionada.setTipo(TipoCaucaoContrato.FIANCA_BANCARIA);
                this.caucaoContratoSelecionada.setContrato(selecionado);
                recuperarUltimaGarantiaAdicionada();
                break;
        }
    }

    public void definirFiscalPorTipo() {
        switch (this.fiscalContrato.getTipo()) {
            case INTERNO:
                this.fiscalContrato = new FiscalInternoContrato();
                this.fiscalContrato.setTipo(TipoFiscalContrato.INTERNO);
                this.fiscalContrato.setContrato(selecionado);
                break;
            case EXTERNO:
                this.fiscalContrato = new FiscalExternoContrato();
                this.fiscalContrato.setTipo(TipoFiscalContrato.EXTERNO);
                this.fiscalContrato.setContrato(selecionado);
                break;
        }
        this.fiscalContrato.setInicioVigencia(selecionado.getInicioVigencia());
        this.fiscalContrato.setFinalVigencia(selecionado.getTerminoVigenciaAtual());
    }

    public void definirPenalidadePorTipo() {
        switch (this.penalidadeContratoSelecionado.getTipo()) {
            case ADVERTENCIA:
                this.penalidadeContratoSelecionado = new PenalidadeContrato();
                this.penalidadeContratoSelecionado.setTipo(TipoPenalidadeContrato.ADVERTENCIA);
                this.penalidadeContratoSelecionado.setContrato(selecionado);
                this.penalidadeContratoSelecionado.setOperacao(Operacoes.NOVO);
                break;
            case MULTA:
                this.penalidadeContratoSelecionado = new MultaPenalidadeContrato();
                this.penalidadeContratoSelecionado.setTipo(TipoPenalidadeContrato.MULTA);
                this.penalidadeContratoSelecionado.setContrato(selecionado);
                this.penalidadeContratoSelecionado.setOperacao(Operacoes.NOVO);
                break;
            case DECLARACAO_INIDONEIDADE:
                this.penalidadeContratoSelecionado = new SuspensaoIdoneidadePenalidadeContrato();
                this.penalidadeContratoSelecionado.setTipo(TipoPenalidadeContrato.DECLARACAO_INIDONEIDADE);
                this.penalidadeContratoSelecionado.setContrato(selecionado);
                this.penalidadeContratoSelecionado.setOperacao(Operacoes.NOVO);
                break;
            case SUSPENSAO_TEMPORARIA:
                this.penalidadeContratoSelecionado = new SuspensaoIdoneidadePenalidadeContrato();
                this.penalidadeContratoSelecionado.setTipo(TipoPenalidadeContrato.SUSPENSAO_TEMPORARIA);
                this.penalidadeContratoSelecionado.setContrato(selecionado);
                this.penalidadeContratoSelecionado.setOperacao(Operacoes.NOVO);
                break;
        }
    }

    public void novoCaucaoContrato() {
        definirCaucaoContratoPorTipo();
    }

    public void cancelarCaucaoContrato() {
        this.caucaoContratoSelecionada = null;
        this.tipoMovimentacaoContratoSelecionado = null;
    }

    private boolean validarMovimentacao() {
        boolean retorno = true;
        if (tipoMovimentacaoContratoSelecionado == null) {
            FacesUtil.addOperacaoNaoPermitida("O Tipo de Movimentação é obrigatório");
            return false;
        }
        if (tipoMovimentacaoContratoSelecionado.equals(TipoMovimentacaoContrato.REDUCAO)) {
            if (caucaoContratoSelecionada.getValor().compareTo(caucaoContratoAtivo.getValor()) >= 0) {
                FacesUtil.addOperacaoNaoPermitida("O valor do Contrato deve ser menor que o valor do contrato ativo");
                retorno = false;
            }
        }
        if (tipoMovimentacaoContratoSelecionado.equals(TipoMovimentacaoContrato.REFORCO)) {
            if (caucaoContratoSelecionada.getValor().compareTo(caucaoContratoAtivo.getValor()) <= 0) {
                FacesUtil.addOperacaoNaoPermitida("O valor do Contrato deve ser maior que o valor do contrato ativo");
                retorno = false;
            }
        }
        return retorno;
    }


    public void confirmarCaucaoContrato() {
        if (validarMovimentacao()) {
            if (!validarConfirmacao(this.caucaoContratoSelecionada)) {
                return;
            }
            caucaoContratoSelecionada.setDataDeCriacao(new Date());
            caucaoContratoSelecionada.setTipoMovimentacaoContrato(tipoMovimentacaoContratoSelecionado);
            caucaoContratoSelecionada.setTipo(tipoCaucaoContrato);
            selecionado.setCaucoes(Util.adicionarObjetoEmLista(selecionado.getCaucoes(), caucaoContratoSelecionada));
            Collections.sort(selecionado.getCaucoes());
            cancelarCaucaoContrato();
        }
    }

    public void recuperarUltimaGarantiaAdicionada() {
        if (selecionado.getCaucoes() != null) {
            for (CaucaoContrato caucaoContrato : selecionado.getCaucoes()) {
                if (caucaoContratoAtivo == null) {
                    caucaoContratoAtivo = caucaoContrato;
                } else if (caucaoContratoAtivo.getDataDeCriacao().before(caucaoContrato.getDataDeCriacao())) {
                    caucaoContratoAtivo = caucaoContrato;
                }
            }
            if (caucaoContratoAtivo != null) {
                criarNovaGarantia(caucaoContratoAtivo);
            }
        }
    }

    public void criarNovaGarantia(CaucaoContrato caucao) {
        caucaoContratoSelecionada.setTipo(tipoCaucaoContrato);
        copiarInformacoesEmComum(caucao);
        switch (this.getTipoCaucaoContrato()) {
            case EM_DINHEIRO:
                if (caucao instanceof DinheiroCaucao) {
                    caucaoContratoSelecionada = (DinheiroCaucao) Util.clonarObjeto(caucao);
                    caucaoContratoSelecionada.setCriadoEm(System.nanoTime());
                    caucaoContratoSelecionada.setId(null);
                }
                break;
            case TITULOS_DA_DIVIDA_PUBLICA:
                if (caucao instanceof TituloDividaPublicaCaucao) {
                    caucaoContratoSelecionada = (TituloDividaPublicaCaucao) Util.clonarObjeto(caucao);
                    caucaoContratoSelecionada.setCriadoEm(System.nanoTime());
                    caucaoContratoSelecionada.setId(null);
                }
                break;
            case SEGURO_GARANTIA:
                if (caucao instanceof SeguroGarantiaCaucao) {
                    caucaoContratoSelecionada = (SeguroGarantiaCaucao) Util.clonarObjeto(caucao);
                    caucaoContratoSelecionada.setCriadoEm(System.nanoTime());
                    caucaoContratoSelecionada.setId(null);
                }
                break;
            case FIANCA_BANCARIA:
                if (caucao instanceof FiancaBancariaCaucao) {
                    caucaoContratoSelecionada = (FiancaBancariaCaucao) Util.clonarObjeto(caucao);
                    caucaoContratoSelecionada.setCriadoEm(System.nanoTime());
                    caucaoContratoSelecionada.setId(null);
                }
                break;
        }
    }

    private void copiarInformacoesEmComum(CaucaoContrato caucao) {
        caucaoContratoSelecionada.setValor(caucao.getValor());
        caucaoContratoSelecionada.setOrgao(caucao.getOrgao());
        caucaoContratoSelecionada.setContrato(caucao.getContrato());
        caucaoContratoSelecionada.setDataCaucao(caucao.getDataCaucao());
    }


    public void selecionarCaucaoContrato(CaucaoContrato cc) {
        this.caucaoContratoSelecionada = cc;
        tipoMovimentacaoContratoSelecionado = caucaoContratoSelecionada.getTipoMovimentacaoContrato();
        cc.getBanco();
        this.caucaoContratoSelecionada.setOperacao(Operacoes.EDITAR);
    }

    public List<Banco> completaBanco(String parte) {
        return facade.getBancoFacade().listaBancoPorCodigoOuNome(parte.trim());
    }

    public List<Agencia> completaAgenciaDoBancoCaucao(String parte) {
        Banco banco = caucaoContratoSelecionada.getBanco();
        if (banco != null) {
            return facade.getAgenciaFacade().listaFiltrandoPorBanco(parte.trim(), banco);
        }
        return new ArrayList<>();
    }

    public List<ContaCorrenteBancaria> completaContaCorrenteBancaria(String parte) {
        Agencia agencia = ((DinheiroCaucao) caucaoContratoSelecionada).getAgencia();
        if (agencia != null) {
            return facade.getContaCorrenteBancariaFacade().listaFiltrandoPorAgencia(parte.trim(), agencia);
        }
        return new ArrayList<>();
    }

    public List<Field> recuperarCamposDe(Object obj) {
        if (obj == null) {
            return null;
        }
        Class c = obj.getClass();
        List<Field> campos = new ArrayList<>();

        for (Field f : Persistencia.getAtributos(c)) {
            if (f.isAnnotationPresent(Tabelavel.class)) {
                campos.add(f);
            }
        }
        Collections.sort(campos, new Comparator<Field>() {
            @Override
            public int compare(Field f1, Field f2) {
                Integer ordem1 = f1.getAnnotation(Tabelavel.class).ordemApresentacao();
                Integer ordem2 = f2.getAnnotation(Tabelavel.class).ordemApresentacao();
                return ordem1.compareTo(ordem2);
            }
        });
        return campos;
    }

    public String recuperarNomeDe(Field f) {
        String etiqueta = "";
        if (f.isAnnotationPresent(Etiqueta.class)) {
            Etiqueta e = f.getAnnotation(Etiqueta.class);
            etiqueta = e.value();
        } else {
            String nome = f.getName();
            etiqueta = nome.substring(0, 1).toUpperCase() + nome.substring(1);
        }
        return etiqueta;
    }

    public String recuperarValorDe(Field f, Object dono) throws
        IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        f.setAccessible(true);
        AtributoMetadata amd = new AtributoMetadata(f);
        if (f.getType().isEnum()) {
            Enum<?> e = Enum.valueOf((Class<? extends Enum>) f.getType(), "" + f.get(dono));
            Object valorRecuperado = e.getClass().getMethod("getDescricao", new Class[]{}).invoke(e, new Object[]{});
            return valorRecuperado.toString();
        } else {
            return amd.getString(dono);
        }
    }

    public void novoFiscalContrato() {
        try {
            Util.validarCampos(selecionado);
            this.fiscalContrato = new FiscalContrato();
            this.fiscalContrato.setTipo(TipoFiscalContrato.INTERNO);
            definirFiscalPorTipo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarFiscalContrato() {
        this.fiscalContrato = null;
    }

    public void confirmarFiscalContrato() {
        try {
            validarFiscalContrato();
            Util.adicionarObjetoEmLista(selecionado.getFiscais(), fiscalContrato);
            cancelarFiscalContrato();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarFiscalContrato() {
        Util.validarCampos(fiscalContrato);
        validarFiscalContratoInterno();
        validarFiscalContratoExterno();

        ValidacaoException ve = new ValidacaoException();
        if (fiscalContrato.getFinalVigencia() != null) {
            if (fiscalContrato.getFinalVigencia().before(fiscalContrato.getInicioVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O fim de vigência deve ser superior ao início de vigência.");
            }
            if (fiscalContrato.getFinalVigencia().after(selecionado.getTerminoVigenciaAtual())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O fim de vigência deve ser anterior ao término de vigência atual do contrato.");
            }
        }
        if (selecionado.getFiscais().stream()
            .anyMatch(gc -> !gc.equals(fiscalContrato)
                && DataUtil.isBetween(gc.getInicioVigencia(), gc.getFinalVigencia(), fiscalContrato.getInicioVigencia()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É permitido somente um fiscal vigente por contrato.");
        }
        ve.lancarException();
    }

    private void validarFiscalContratoInterno() {
        ValidacaoException ve = new ValidacaoException();
        if (fiscalContrato.getTipo().isInterno()) {
            if (fiscalContrato.getFiscalInternoContrato().getServidor() == null && fiscalContrato.getFiscalInternoContrato().getServidorPF() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor deve ser informado.");
            }
            if (fiscalContrato.getAtoLegal() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Ato Legal deve ser informado.");
            } else if (fiscalContrato.getAtoLegal().getArquivo() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Ato legal selecionado deve possuir um anexo.");
            }
        }
        ve.lancarException();
    }

    private void validarFiscalContratoExterno() {
        ValidacaoException ve = new ValidacaoException();
        if (fiscalContrato.getTipo().isExterno() && fiscalContrato.getFiscalExternoContrato().getContratoFiscal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo contrato firmado deve ser informado.");
        }
        ve.lancarException();
    }


    public void selecionarFiscalContrato(FiscalContrato fc) {
        this.fiscalContrato = fc;
    }

    public void removerFiscalContrato(FiscalContrato fc) {
        selecionado.getFiscais().remove(fc);
    }

    public List<PessoaJuridica> completaPessoaJuridica(String parte) {
        return facade.getPessoaJuridicaFacade().listaFiltrandoRazaoSocialCNPJ(parte.trim());
    }

    public void novoPenalidade() {
        this.penalidadeContratoSelecionado = new PenalidadeContrato();
        this.penalidadeContratoSelecionado.setTipo(TipoPenalidadeContrato.ADVERTENCIA);
        definirPenalidadePorTipo();
    }

    public void cancelarPenalidade() {
        this.penalidadeContratoSelecionado = null;
    }

    public void confirmarPenalidade() {
        if (!validarConfirmacao(this.penalidadeContratoSelecionado)) {
            return;
        }
        if (penalidadeContratoSelecionado instanceof MultaPenalidadeContrato
            && ((MultaPenalidadeContrato) penalidadeContratoSelecionado).getPercentual() != null
            && ((MultaPenalidadeContrato) penalidadeContratoSelecionado).getPercentual().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addOperacaoNaoRealizada("O campo Percentual deve ser maior que zero!");
            return;
        }

        selecionado.setPenalidades(Util.adicionarObjetoEmLista(selecionado.getPenalidades(), this.penalidadeContratoSelecionado));
        cancelarPenalidade();
    }

    public void selecionarPenalidade(PenalidadeContrato pc) {
        this.penalidadeContratoSelecionado = pc;
        this.penalidadeContratoSelecionado.setOperacao(Operacoes.EDITAR);
    }

    public void removerPenalidade(PenalidadeContrato pc) {
        selecionado.getPenalidades().remove(pc);
    }

    public void novoPreposto() {
        this.contatoContratoSelecionado = new ContatoContrato();
        this.contatoContratoSelecionado.setContrato(selecionado);
    }

    public void cancelarPreposto() {
        this.contatoContratoSelecionado = null;
    }

    public void confirmarPreposto() {
        if (!validarConfirmacao(this.contatoContratoSelecionado)) {
            return;
        }

        selecionado.setContatos(Util.adicionarObjetoEmLista(selecionado.getContatos(), this.contatoContratoSelecionado));
        cancelarPreposto();
    }

    public void selecionarPreposto(ContatoContrato cc) {
        this.contatoContratoSelecionado = cc;
    }

    public void removerPreposto(ContatoContrato cc) {
        selecionado.getContatos().remove(cc);
    }

    public void novoDocumento() {
        this.documentoContratoSelecionado = new DocumentoContrato();
        this.documentoContratoSelecionado.setContrato(selecionado);
    }

    public void cancelarDocumento() {
        this.documentoContratoSelecionado = null;
    }

    public void confirmarDocumento() {
        if (!validarConfirmacao(this.documentoContratoSelecionado)) {
            return;
        }

        selecionado.setDocumentos(Util.adicionarObjetoEmLista(selecionado.getDocumentos(), this.documentoContratoSelecionado));
        cancelarDocumento();
    }

    public void selecionarDocumento(DocumentoContrato dc) {
        this.documentoContratoSelecionado = dc;
    }

    public void removerDocumento(DocumentoContrato dc) {
        selecionado.getDocumentos().remove(dc);
    }

    public void limparCamposGerais() {
        selecionado.limparDadosGerais();
        selecionado.setItens(Lists.<ItemContrato>newArrayList());
        setAtaRegistroPreco(null);
        instanciarListasItemContrato();
        setTodosItensQuantidadeSelecionados(false);
        setTodosItensValorSelecionados(false);
        objetoContrato.setLicitacao(null);
        objetoContrato.setRegistroPrecoExterno(null);
        objetoContrato.setSolicitacaoMaterialExterno(null);
        objetoContrato.setDispensaDeLicitacao(null);
        objetoContrato.setParticipanteIRP(null);
        updateDadosGerais();
        updateItens();
    }

    public boolean isControleQuantidade() {
        if (selecionado != null && selecionado.hasItens()) {
            for (ItemContrato item : selecionado.getItens()) {
                return item.getControleQuantidade();
            }
        }
        return false;
    }

    public boolean desabilitarAutoCompleteUnidadeContrato() {
        if (!selecionado.isContratoEmElaboracao()) {
            return true;
        }
        return selecionado.isDeIrp() || selecionado.isDeAdesaoAtaRegistroPrecoInterna() || selecionado.isDeRegistroDePrecoExterno();
    }

    private void validarLicitacao(Licitacao licitacao) {
        ValidacaoException ve = new ValidacaoException();
        if (licitacao == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível localizar o processo de licitação, por favor verifique as informações digitadas e tente novamente.");
        }
        ve.lancarException();
    }

    public void recuperarAtaRegistroPreco() {
        try {
            if (selecionado.isRegistroDePrecos() && selecionado.isProcessoLicitatorio()) {
                UnidadeOrganizacional unidadeAta = null;
                if (selecionado.isDeLicitacao()) {
                    unidadeAta = selecionado.getLicitacao().getProcessoDeCompra().getUnidadeOrganizacional();
                }
                if (selecionado.isDeIrp()) {
                    unidadeAta = selecionado.getParticipanteIRP().getHierarquiaOrganizacional().getSubordinada();
                }
                if (selecionado.isDeAdesaoAtaRegistroPrecoInterna()) {
                    unidadeAta = selecionado.getSolicitacaoAdesaoAtaInterna().getAtaRegistroPreco().getUnidadeOrganizacional();
                }
                if (unidadeAta != null) {
                    ataRegistroPreco = facade.getAtaRegistroPrecoFacade().recuperarAtaRegistroPrecoPorUnidade(unidadeAta, selecionado.getLicitacao(), null);
                }
                validarAtaRegistroPreco();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAtaRegistroPreco() {
        ValidacaoException ve = new ValidacaoException();
        if (ataRegistroPreco == null && selecionado.isRegistroDePrecos() && selecionado.isProcessoLicitatorio()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Ata Registro de Preços não cadastrada!", "Por favor, faça a ata de registro de preço para a unidade " + selecionado.getUnidadeAdministrativa() + " e tente novamente.");
        }
        ve.lancarException();
    }

    private void validarGestorAndFiscalVigente() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFiscais().stream().noneMatch(fiscal -> DataUtil.isBetween(fiscal.getInicioVigencia(), fiscal.getFinalVigencia(), selecionado.getTerminoVigenciaAtual()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar, o contrato deve ter um fiscal vigente. 'Clique na aba 'Gestor e Fiscal' para adicionar.");
        }
        if (selecionado.getGestores().stream().noneMatch(gestor -> DataUtil.isBetween(gestor.getInicioVigencia(), gestor.getFinalVigencia(), selecionado.getTerminoVigenciaAtual()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar, o contrato deve ter um gestor vigente. 'Clique na aba 'Gestor e Fiscal' para adicionar.");
        }
        ve.lancarException();
    }

    private void validarDispensaDeLicitacao() {
        ValidacaoException ve = new ValidacaoException();
        if (objetoContrato.getDispensaDeLicitacao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível localizar o processo de compra " + selecionado.getNumeroProcesso() + "/" + selecionado.getAnoProcesso()
                + " vinculado a uma dispensa de licitação, por favor verifique as informações digitadas e tente novamente.");
        }
        ve.lancarException();
    }

    public void iniciarProcessoSelecaoFornecedor() {
        if (selecionado.getTipoContrato() == null) {
            return;
        }
        recuperarVencedoresDoObjetoLicitatorio();
        exibirDialogParaEscolhaDeFornecedorVencedor();

        if (selecionado.getUnidadeAdministrativa() != null) {
            setResponsavelUnidadePessoaFisica(facade.getConfiguracaoLicitacaoFacade().verificarUnidadeTercerializadaRh(selecionado.getUnidadeAdministrativa().getSubordinada()));
        }
        updateDadosGerais();
    }

    private void definirTipoDoContratoRegistroPrecoExterno() {
        if (facade.getRegistroSolicitacaoMaterialExternoFacade().isRegistroPrecoExternoComItensDeMateriais(selecionado.getRegistroSolicitacaoMaterialExterno())
            && facade.getRegistroSolicitacaoMaterialExternoFacade().isRegistroPrecoExternoComItensDeServicos(selecionado.getRegistroSolicitacaoMaterialExterno())) {
            FacesUtil.executaJavaScript("dialogEscolhaMaterialServico.show();");
            return;
        }
        if (facade.getRegistroSolicitacaoMaterialExternoFacade().isRegistroPrecoExternoComItensDeMateriais(selecionado.getRegistroSolicitacaoMaterialExterno())) {
            selecionado.setTipoContrato(TipoContrato.MATERIAL);
            return;
        }
        if (facade.getRegistroSolicitacaoMaterialExternoFacade().isRegistroPrecoExternoComItensDeServicos(selecionado.getRegistroSolicitacaoMaterialExterno())) {
            selecionado.setTipoContrato(TipoContrato.SERVICO);
            return;
        }
    }

    private void definirTipoDoContratoDispensaLicitacao() {
        SolicitacaoMaterial sm = facade.getLicitacaoFacade().recuperarSolicitacaoProcessoDeCompra(selecionado.getDispensaDeLicitacao().getProcessoDeCompra());
        if (sm.getTipoSolicitacao().equals(TipoSolicitacao.OBRA_SERVICO_DE_ENGENHARIA)) {
            selecionado.setTipoContrato(TipoContrato.OBRAS_ENGENHARIA);
            return;
        }

        if (facade.getDispensaDeLicitacaoFacade().isDispensaDeLicitacaoComItensDefinidoPorTipoObjetoCompra(selecionado.getDispensaDeLicitacao(), TipoObjetoCompra.getTiposObjetoCompraMaterial())
            && facade.getDispensaDeLicitacaoFacade().isDispensaDeLicitacaoComItensDefinidoPorTipoObjetoCompra(selecionado.getDispensaDeLicitacao(), TipoObjetoCompra.getTiposObjetoCompraServico())) {
            FacesUtil.executaJavaScript("dialogEscolhaMaterialServico.show();");
            return;
        }

        if (facade.getDispensaDeLicitacaoFacade().isDispensaDeLicitacaoComItensDefinidoPorTipoObjetoCompra(selecionado.getDispensaDeLicitacao(), TipoObjetoCompra.getTiposObjetoCompraMaterial())) {
            selecionado.setTipoContrato(TipoContrato.MATERIAL);
            return;
        }

        if (facade.getDispensaDeLicitacaoFacade().isDispensaDeLicitacaoComItensDefinidoPorTipoObjetoCompra(selecionado.getDispensaDeLicitacao(), TipoObjetoCompra.getTiposObjetoCompraServico())) {
            selecionado.setTipoContrato(TipoContrato.SERVICO);
        }
    }

    private void definirTipoDoContratoLicitacao() {
        SolicitacaoMaterial sm = facade.getLicitacaoFacade().recuperarSolicitacaoProcessoDeCompra(selecionado.getLicitacao().getProcessoDeCompra());
        if (sm.getTipoSolicitacao().equals(TipoSolicitacao.OBRA_SERVICO_DE_ENGENHARIA)) {
            selecionado.setTipoContrato(TipoContrato.OBRAS_ENGENHARIA);
            return;
        }
        if (facade.getLicitacaoFacade().isLicitacaoComItensDefinidoPorTipoObjetoCompra(selecionado.getLicitacao(), TipoObjetoCompra.getTiposObjetoCompraMaterial())
            && facade.getLicitacaoFacade().isLicitacaoComItensDefinidoPorTipoObjetoCompra(selecionado.getLicitacao(), TipoObjetoCompra.getTiposObjetoCompraServico())) {
            FacesUtil.executaJavaScript("dialogEscolhaMaterialServico.show();");
            return;
        }
        if (facade.getLicitacaoFacade().isLicitacaoComItensDefinidoPorTipoObjetoCompra(selecionado.getLicitacao(), TipoObjetoCompra.getTiposObjetoCompraMaterial())) {
            selecionado.setTipoContrato(TipoContrato.MATERIAL);
            return;
        }

        if (facade.getLicitacaoFacade().isLicitacaoComItensDefinidoPorTipoObjetoCompra(selecionado.getLicitacao(), TipoObjetoCompra.getTiposObjetoCompraServico())) {
            selecionado.setTipoContrato(TipoContrato.SERVICO);
        }
    }

    private void updateDadosGerais() {
        FacesUtil.atualizarComponente("Formulario:tab-view-geral:panel-dados-gerais");
    }

    private void updateItens() {
        FacesUtil.atualizarComponente("Formulario:tab-view-geral:panel-itens");
    }

    private void updateValorTotalContrato() {
        FacesUtil.atualizarComponente("Formulario:tab-view-geral:valor-total-contrato");
    }

    private void carregarQuantidadesValoresDosItens(List<ItemContrato> itensContrato) {
        for (ItemContrato ic : itensContrato) {
            if (ic.getValorTotal() == null) {
                ic.setValorTotal(BigDecimal.ZERO);
            }
            if (ic.getValorUnitario() == null) {
                recuperarValorUnitarioDoItem(ic);
            }
            if (ic.getControleQuantidade()) {
                ic.setQuantidadeEntregue(facade.getItemContratoFacade().getQuantidadeEntregue(ic));
                ic.setQuantidadeEmRequisicao(facade.getItemContratoFacade().getQuantidadeEmRequisicao(ic));
                ic.setQuantidadeEmOutrosContratos(facade.getItemContratoFacade().getQuantidadeOrValorUtilizadoEmOutrosContratos(ic));
                ic.calcularValorTotalItemContrato();
            } else {
                ic.setValorLicitadoItem(facade.getItemContratoFacade().getValorLicitadoItem(ic));
                ic.setValorEmOutrosContratos(facade.getItemContratoFacade().getQuantidadeOrValorUtilizadoEmOutrosContratos(ic));
            }
            facade.atribuirGrupoContaDespesa(ic);
        }
    }

    public void processarValorTotalItemContrato(ItemContrato itemContrato) {
        try {
            validarValorTotalItemContrato(itemContrato);
            itemContrato.setValorUnitario(itemContrato.getValorTotal());
            calcularValorTotal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            updateItens();
            updateValorTotalContrato();
        }
    }

    public void processarQuantidadeItemContrato(ItemContrato itemContrato) {
        try {
            validarQuantidadeItemContrato(itemContrato);
            itemContrato.calcularValorTotalItemContrato();
            calcularValorTotal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            itemContrato.setQuantidadeTotalContrato(BigDecimal.ZERO);
            itemContrato.calcularValorTotalItemContrato();
            updateItens();
            updateValorTotalContrato();
        }
    }

    public void validarQuantidadeItemContrato(ItemContrato ic) {
        ValidacaoException ve = new ValidacaoException();
        if (ic.getSelecionado() && ic.getControleQuantidade()) {
            if (!ic.hasQuantidadeTotalMaiorQueZero()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade do item " + ic + " deve ser informada com um valor maior que zero(0).");
            }
            ve.lancarException();

            BigDecimal quantidadeDisponivel = ic.getQuantidadeLicitada().subtract(ic.getQuantidadeEmOutrosContratos());
            if (ic.getQuantidadeTotalContrato() != null && ic.getQuantidadeTotalContrato().compareTo(quantidadeDisponivel) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade informada excede a quantidade disponível de <b>" + quantidadeDisponivel + "</b> item.");
                ic.setQuantidadeTotalContrato(quantidadeDisponivel);
                ic.calcularValorTotalItemContrato();
            }
        }
        ve.lancarException();
    }

    private void validarValorTotalItemContrato(ItemContrato itemContrato) {
        ValidacaoException ve = new ValidacaoException();
        if (itemContrato.getSelecionado() && itemContrato.getControleValor()) {
            if (!itemContrato.hasValorTotalMaiorQueZero()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total do item " + itemContrato + " deve ser informado com um valor maior que zero(0).");
            }
            if (selecionado.isDeAdesaoAtaRegistroPrecoInterna()) {
                if (itemContrato.getItemContratoAdesaoAtaInt() != null
                    && itemContrato.getItemContratoAdesaoAtaInt().getItemSolicitacaoExterno() != null
                    && itemContrato.getItemContratoAdesaoAtaInt().getItemSolicitacaoExterno().isControleValor()) {
                    ItemSolicitacaoExterno itemSolExt = itemContrato.getItemContratoAdesaoAtaInt().getItemSolicitacaoExterno();
                    BigDecimal total = itemSolExt.getQuantidade().multiply(itemSolExt.getValorUnitario());
                    BigDecimal valorDisponivel = total.subtract(itemContrato.getValorEmOutrosContratos());

                    if (itemContrato.getValorTotal() != null && itemContrato.getValorTotal().compareTo(valorDisponivel) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Valor Total' do item " + itemContrato + " ultrapassa o valor disponível de " + Util.formataValor(valorDisponivel));
                        itemContrato.setValorTotal(valorDisponivel);
                    }
                }
            } else if (itemContrato.getValorDisponivel() != null
                && itemContrato.getValorTotal() != null
                && itemContrato.getValorTotal().compareTo(itemContrato.getValorDisponivel()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Valor Total' do item " + itemContrato + " ultrapassa o valor disponível de " + Util.formataValor(itemContrato.getValorDisponivel()));
                itemContrato.setValorTotal(itemContrato.getValorDisponivel());
            }
        }
        ve.lancarException();
    }


    private void redirecionarParaAbaItens() {
        if (hasItensContratoPorQuantidade() || hasItensContratoPorValor()) {
            indiceAba = isOperacaoNovo() ? 1 : 0;
        }
    }

    public void redirecionarParaAbaPrincipal() {
        indiceAba = 0;
    }

    private void selecionarItemParaEdicao(List<ItemContrato> itensLicitados) {
        if (isOperacaoEditar()) {
            for (ItemContrato itemSalvo : selecionado.getItens()) {
                itemSalvo.setSelecionado(true);
                itemSalvo.setUtilizadoProcesso(facade.getItemContratoFacade().getUtilizadoProcesso(itemSalvo));

                Iterator<ItemContrato> itemLicitado = itensLicitados.iterator();
                while (itemLicitado.hasNext()) {
                    ItemContrato itemNovo = itemLicitado.next();
                    if (selecionado.isDeRegistroDePrecoExterno()) {
                        if (facade.isMesmoItemObjetoCompra(
                            itemNovo.getItemAdequado().getDescricaoComplementar(),
                            itemNovo.getItemAdequado().getObjetoCompra(),
                            itemNovo.getItemAdequado().getNumeroLote(),
                            itemSalvo.getItemAdequado().getDescricaoComplementar(),
                            itemSalvo.getItemAdequado().getObjetoCompra(),
                            itemSalvo.getItemAdequado().getNumeroLote())) {
                            itemLicitado.remove();
                        }
                    } else {
                        if (itemNovo.getItemAdequado().getItemProcessoCompra() != null && itemNovo.getItemAdequado().getItemProcessoCompra().equals(itemSalvo.getItemAdequado().getItemProcessoCompra())) {
                            itemLicitado.remove();
                        }
                    }
                }
            }
        }
    }

    private void separarItensContratoValorQuantidade(List<ItemContrato> itensContrato) {
        for (ItemContrato item : itensContrato) {
            if (item.getTipoControle().isTipoControlePorValor()) {
                getItensControleValor().add(item);
                continue;
            }
            getItensControleQuantidade().add(item);
        }
        Collections.sort(getItensControleValor());
        Collections.sort(getItensControleQuantidade());
    }

    private void calcularValorTotal() {
        selecionado.setValorTotal(getValorTotalItensContratoSelecionados().setScale(2, RoundingMode.HALF_EVEN));
        if (selecionado.getSituacaoContrato().isAprovado()) {
            selecionado.setValorAtualContrato(selecionado.getValorTotal());
            selecionado.setSaldoAtualContrato(selecionado.getValorTotal().subtract(selecionado.getValorTotalExecucaoLiquido()));
        }
        updateDadosGerais();
    }

    public boolean hasItensContratoPorValor() {
        return getItensControleValor() != null && !getItensControleValor().isEmpty();
    }

    public boolean hasItensContratoPorQuantidade() {
        return getItensControleQuantidade() != null && !getItensControleQuantidade().isEmpty();
    }

    public void validarSelecionarItemContrato() {
        ValidacaoException ve = new ValidacaoException();
        boolean selecionou = false;
        selecionou = hasItemSelecionado(getItensControleValor(), selecionou);
        selecionou = hasItemSelecionado(getItensControleQuantidade(), selecionou);
        if (!selecionou) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum item foi selecionado.");
        }
        ve.lancarException();
        for (ItemContrato item : getItensControleQuantidade()) {
            validarQuantidadeItemContrato(item);
        }
        for (ItemContrato item : getItensControleValor()) {
            validarValorTotalItemContrato(item);
        }
        ve.lancarException();
    }

    private boolean hasItemSelecionado(List<ItemContrato> itens, boolean selecionou) {
        for (ItemContrato item : itens) {
            if (item.getSelecionado()) {
                selecionou = true;
                break;
            }
        }
        return selecionou;
    }

    public void selecionarItemContrato(ItemContrato item, boolean trueOrFalse) {
        item.setSelecionado(trueOrFalse);
        if (item.getControleQuantidade()) {
            BigDecimal qtdeDisponivel = item.getQuantidadeLicitada().subtract(item.getQuantidadeEmOutrosContratos());
            item.setQuantidadeTotalContrato(item.getSelecionado() ? qtdeDisponivel : BigDecimal.ZERO);
            item.calcularValorTotalItemContrato();
        } else if (item.getControleValor()) {
            item.setQuantidadeTotalContrato(BigDecimal.ONE);
            item.setValorTotal(item.getSelecionado() ? item.getValorDisponivel() : BigDecimal.ZERO);
            item.setValorUnitario(item.getValorTotal());
        }
        calcularValorTotal();
    }

    public void selecionarTodosItensValor(boolean trueOrFalse) {
        todosItensValorSelecionados = trueOrFalse;
        for (ItemContrato item : getItensControleValor()) {
            selecionarItemContrato(item, trueOrFalse);
        }
    }

    public void selecionarTodosItensQuantidade(boolean trueOrFalse) {
        todosItensQuantidadeSelecionados = trueOrFalse;
        for (ItemContrato item : getItensControleQuantidade()) {
            selecionarItemContrato(item, trueOrFalse);
        }
    }

    private void exibirDialogParaEscolhaDeFornecedorVencedor() {
        if (fornecedoresGanhadoresLicitacao != null) {
            if (this.fornecedoresGanhadoresLicitacao.size() > 1) {
                RequestContext.getCurrentInstance().update("form-fornecedores-vencedores");
                RequestContext.getCurrentInstance().execute("fornecedoresVencedores.show()");
            }
            if (this.fornecedoresGanhadoresLicitacao.size() == 1) {
                selecionado.setContratado(this.fornecedoresGanhadoresLicitacao.get(0));
                if (!selecionado.isDeIrp()) {
                    recuperarItensFornecedorVencedorProcessoLicitatorio();
                }
                redirecionarParaAbaItens();
                FacesUtil.atualizarComponente("Formulario:tab-view-geral");
            }
        }
    }

    public void recuperarVencedoresDoObjetoLicitatorio() {
        switch (selecionado.getTipoAquisicao()) {
            case LICITACAO:
            case ADESAO_ATA_REGISTRO_PRECO_INTERNA:
            case INTENCAO_REGISTRO_PRECO:
            case PROCEDIMENTO_AUXILIARES:
                fornecedoresGanhadoresLicitacao = facade.buscarFornecedoresVencedoresComItensHomologados(selecionado);
                break;
            case DISPENSA_DE_LICITACAO:
                fornecedoresGanhadoresLicitacao = facade.getDispensaDeLicitacaoFacade().recuperarFornecedoresDispensaDeLicitacao(selecionado.getDispensaDeLicitacao());
                break;
            case REGISTRO_DE_PRECO_EXTERNO:
                fornecedoresGanhadoresLicitacao = facade.getRegistroSolicitacaoMaterialExternoFacade().recuperarFornecedoresRegistroPrecoExterno(selecionado.getRegistroSolicitacaoMaterialExterno());
                break;
        }
        if (fornecedoresGanhadoresLicitacao == null || fornecedoresGanhadoresLicitacao.isEmpty()) {
            FacesUtil.addAtencao("Não foram encontrados fornecedores com itens homologados para a licitação selecionada.");
        }
        Collections.sort(fornecedoresGanhadoresLicitacao);
        FacesUtil.atualizarComponente("form-fornecedores-vencedores");
    }

    public void popularObjetoContrato() {
        switch (selecionado.getTipoAquisicao()) {
            case LICITACAO:
            case PROCEDIMENTO_AUXILIARES:
                objetoContrato = new ObjetoContrato();
                objetoContrato.setLicitacao(selecionado.getLicitacao());
                break;
            case DISPENSA_DE_LICITACAO:
                objetoContrato = new ObjetoContrato();
                objetoContrato.setDispensaDeLicitacao(selecionado.getDispensaDeLicitacao());
                break;
            case INTENCAO_REGISTRO_PRECO:
                atribuirHierarquiaAdmParticipanteIrp();
                objetoContrato = new ObjetoContrato();
                objetoContrato.setLicitacao(selecionado.getLicitacao());
                objetoContrato.setParticipanteIRP(selecionado.getParticipanteIRP());
                break;
            case ADESAO_ATA_REGISTRO_PRECO_INTERNA:
                objetoContrato = new ObjetoContrato();
                objetoContrato.setLicitacao(selecionado.getLicitacao());
                objetoContrato.setSolicitacaoMaterialExterno(selecionado.getSolicitacaoAdesaoAtaInterna());
                break;
            case REGISTRO_DE_PRECO_EXTERNO:
                objetoContrato = new ObjetoContrato();
                objetoContrato.setRegistroPrecoExterno(selecionado.getRegistroSolicitacaoMaterialExterno());
                break;
        }
    }

    public void listenerTipoAquisicao() {
        selecionado.limparDadosGerais();
        setAtaRegistroPreco(null);
        objetoContrato = new ObjetoContrato();
    }

    public void novoObjetoLicitatorioContrato() {
        selecionado.limparDadosGerais();
        switch (selecionado.getTipoAquisicao()) {
            case LICITACAO:
            case INTENCAO_REGISTRO_PRECO:
            case PROCEDIMENTO_AUXILIARES:
                ContratoLicitacao contratoLic = new ContratoLicitacao();
                contratoLic.setContrato(selecionado);
                contratoLic.setLicitacao(objetoContrato.getLicitacao());
                selecionado.setContratoLicitacao(contratoLic);
                break;
            case DISPENSA_DE_LICITACAO:
                ContratoDispensaDeLicitacao contratoDisp = new ContratoDispensaDeLicitacao();
                contratoDisp.setContrato(selecionado);
                contratoDisp.setDispensaDeLicitacao(objetoContrato.getDispensaDeLicitacao());
                selecionado.setContratoDispensaDeLicitacao(contratoDisp);
                break;
            case ADESAO_ATA_REGISTRO_PRECO_INTERNA:
                ContratoLicitacao contratoLicAta = new ContratoLicitacao();
                contratoLicAta.setContrato(selecionado);
                contratoLicAta.setLicitacao(objetoContrato.getSolicitacaoMaterialExterno().getAtaRegistroPreco().getLicitacao());
                contratoLicAta.setSolicitacaoMaterialExterno(objetoContrato.getSolicitacaoMaterialExterno());
                selecionado.setContratoLicitacao(contratoLicAta);
                break;
            case REGISTRO_DE_PRECO_EXTERNO:
                ContratoRegistroPrecoExterno contratoRegExt = new ContratoRegistroPrecoExterno();
                contratoRegExt.setContrato(selecionado);
                contratoRegExt.setRegistroSolicitacaoMaterialExterno(objetoContrato.getRegistroPrecoExterno());
                selecionado.setContratoRegistroPrecoExterno(contratoRegExt);
                break;
        }
    }

    public void cancelarSelecaoFornecedor() {
        FacesUtil.executaJavaScript("fornecedoresVencedores.hide()");
        if (isOperacaoNovo()) {
            novo();
        }
    }

    public void confirmarSelecaoFornecedor() {
        if (selecionado.getContratado() == null) {
            FacesUtil.addOperacaoNaoPermitida("Selecione um fornecedor (contratado) para o contrato.");
            return;
        }
        instanciarListasItemContrato();
        if (!selecionado.isDeIrp()) {
            recuperarItensFornecedorVencedorProcessoLicitatorio();
        }
        redirecionarParaAbaItens();
        FacesUtil.atualizarComponente("Formulario:tab-view-geral");
        FacesUtil.executaJavaScript("fornecedoresVencedores.hide()");
    }

    private void instanciarListasItemContrato() {
        itensControleValor = Lists.newArrayList();
        itensControleQuantidade = Lists.newArrayList();
    }

    public void confirmarSelecaoTipoContrato() {
        if (selecionado.getTipoContrato() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Tipo de Contrato deve ser informado.");
            return;
        }
        FacesUtil.atualizarComponente("Formulario:tab-view-geral:panel-dados-gerais");
        FacesUtil.executaJavaScript("dialogEscolhaMaterialServico.hide()");
        iniciarProcessoSelecaoFornecedor();
    }

    private BigDecimal recuperarValorUnitarioDoItem(ItemContrato ic) {
        try {
            ic.setValorUnitario(facade.getItemContratoFacade().getValorUnitario(ic));
        } catch (NullPointerException npe) {
            ic.setValorUnitario(null);
        }
        return ic.getValorUnitario();
    }

    private Integer getProximoNumero(List<? extends Object> lista) {
        try {
            return lista.size() + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    public BigDecimal getValorTotalItensContratoSelecionados() {
        return getValorTotalItensContratoPorValor().add(getValorTotalItensContratoPorQuantidade());
    }

    public BigDecimal getValorTotalItensContratoPorValor() {
        BigDecimal valor = BigDecimal.ZERO;
        try {
            for (ItemContrato item : getItensControleValor()) {
                if (item.hasValorTotalMaiorQueZero()) {
                    valor = valor.add(item.getValorTotal());
                }
            }
            return valor;
        } catch (NullPointerException e) {
            return valor;
        }
    }

    public BigDecimal getValorTotalItensContratoPorQuantidade() {
        BigDecimal valor = BigDecimal.ZERO;
        try {
            for (ItemContrato item : getItensControleQuantidade()) {
                if (item.hasValorTotalMaiorQueZero()) {
                    valor = valor.add(item.getValorTotal());
                }
            }
            return valor;
        } catch (NullPointerException e) {
            return valor;
        }
    }

    public void confirmarNotaFiscalExecucaoContrato() {
        if (!validarConfirmacao(notaFiscalExecucaoContratoSelecionado)) {
            return;
        }
        ExecucaoContrato ec = this.notaFiscalExecucaoContratoSelecionado.getExecucaoContrato();
        ec.setNotasFiscais(Util.adicionarObjetoEmLista(ec.getNotasFiscais(), this.notaFiscalExecucaoContratoSelecionado));
        FacesUtil.executaJavaScript("novaNotaFiscalExecucao.hide();");
    }

    public void cancelarNotaFiscalExecucaoContrato() {
        this.notaFiscalExecucaoContratoSelecionado = null;
    }

    public String cortarString(String s, Integer limite) {
        return Util.cortarString(s, limite);
    }

    public Boolean isRegistroDePrecos() {
        return selecionado != null && selecionado.isRegistroDePrecos() && !selecionado.isDeProcedimentosAuxiliares();
    }

    public List<Contrato> buscarContratoPorNumeroOrAno(String numeroOrAno) {
        return facade.buscarContratoPorNumeroOrExercicio(numeroOrAno);
    }

    public List<TipoContrato> getTiposDeContratoParaEsteContrato() {
        if (selecionado.getLicitacao() != null || selecionado.getDispensaDeLicitacao() != null) {
            SolicitacaoMaterial sm = facade.getLicitacaoFacade().recuperarSolicitacaoProcessoDeCompra(selecionado.getObjetoAdequado().getProcessoDeCompra());
            if (sm != null) {
                return TipoContrato.getTipoContratoQueSeEnquadraCom(sm.getTipoSolicitacao());
            }
        }

        return Lists.newArrayList(TipoContrato.values());
    }

    private void gerarPDF(ModeloDocumentoContrato modelo) {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "inline;filename=" + modelo.getNome() + ".pdf");
            response.setCharacterEncoding("UTF-8");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
                + " <html>"
                + " <head>"
                + " <style type=\"text/css\">@page{size: A4 portrait;}</style>"
                + " <title>"
                + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">"
                + modelo.getNome()
                + " </title>"
                + " </head>"
                + " <body>"
                + modelo.getModelo()
                + " </body>"
                + " </html>", baos);
            byte[] bytes = baos.toByteArray();
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();

            facesContext.responseComplete();
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }

    }

    public void gerarDocumentoPadrao(DocumentoContrato documentoContrato) {
        if (!Util.validaCampos(selecionado)) {
            return;
        }

        ModeloDocumentoContrato novoModelo = new ModeloDocumentoContrato();

        novoModelo.setModelo(mesclaTagsModelo(documentoContrato.getModeloDocumentoContrato()));
        novoModelo.setNome(documentoContrato.getModeloDocumentoContrato().getNome());

        novoModelo.setModelo(FacesUtil.alteraURLImagens(novoModelo.getModelo()));

        gerarPDF(novoModelo);
    }

    public String mesclaTagsModelo(ModeloDocumentoContrato modelo) {
        CharSequence string1 = "$NUMERO[if !mso]";
        CharSequence string2 = "$NUMERO";

        modelo.setModelo(modelo.getModelo().replace(string1, string2));

        Properties p = new Properties();
        p.setProperty("resource.loader", "string");
        p.setProperty("string.resource.loader.class",
            "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        VelocityEngine ve = new VelocityEngine();
        ve.init(p);

        StringResourceRepository repo = StringResourceLoader.getRepository();
        repo.putStringResource("myTemplate", modelo.getModelo());
        repo.setEncoding("UTF-8");

        VelocityContext context = new VelocityContext();
        context = adicionarTagsNoContexto(context);
        Template template = ve.getTemplate("myTemplate", "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private VelocityContext adicionarTagsNoContexto(VelocityContext context) {
        context.put("DATA_HOJE_ANO", DataUtil.getAno(new Date()));
        context.put("DATA_HOJE_MES", StringUtil.preencheString(DataUtil.getMes(new Date()) + "", 2, '0'));
        context.put("DATA_HOJE_DIA", StringUtil.preencheString(DataUtil.getDia(new Date()) + "", 2, '0'));
        context.put("DATA_HOJE_MES_EXTENSO", Mes.getMesToInt(DataUtil.getMes(new Date())).getDescricao());
        context.put("QUEBRA_PAGINA", "<div class=\"break\"></div>");


        context.put(TagsModeloDocumentoContrato.NUMERO_CONTRATO.name(), selecionado.getNumeroTermo());
        context.put(TagsModeloDocumentoContrato.ANO_CONTRATO.name(), selecionado.getExercicioContrato().getAno());
        context.put(TagsModeloDocumentoContrato.TIPO_DE_AQUISICAO.name(), selecionado.getTipoAquisicao().getDescricao());
        context.put(TagsModeloDocumentoContrato.NUMERO_DO_PROCESSO_DE_COMPRA.name(), selecionado.getNumeroProcesso());
        context.put(TagsModeloDocumentoContrato.ANO_DO_PROCESSO_DE_COMPRA.name(), selecionado.getAnoProcesso());
        context.put(TagsModeloDocumentoContrato.ORGAO_ENTIDADE_FUNDO.name(), selecionado.getUnidadeAdministrativa().getSubordinada());
        context.put(TagsModeloDocumentoContrato.REGIME_DE_EXECUCAO.name(), selecionado.getRegimeExecucao());

        return context;
    }

    public List<Licitacao> completarLicitacoes(String parte) {
        if (selecionado.isDeIrp()) {
            return facade.getLicitacaoFacade().buscarLicitacoesIrpUsuarioGestorUnidade(parte.trim());
        } else if (selecionado.isDeProcedimentosAuxiliares()) {
            return facade.getLicitacaoFacade().buscarLicitacaoHomologadaPorModalidade(parte.trim(), ModalidadeLicitacao.CREDENCIAMENTO);
        }
        return facade.getLicitacaoFacade().buscarLicitacoesUsuarioGestorUnidade(parte.trim());
    }

    public void recuperarItensFornecedorVencedorProcessoLicitatorio() {
        instanciarListasItemContrato();
        List<ItemContrato> itensLicitados = facade.criarItensContratoDe(selecionado);
        selecionarItemParaEdicao(itensLicitados);
        carregarQuantidadesValoresDosItens(itensLicitados);
        separarItensContratoValorQuantidade(itensLicitados);
        if (isOperacaoEditar()) {
            carregarQuantidadesValoresDosItens(selecionado.getItens());
            separarItensContratoValorQuantidade(selecionado.getItens());
        }
    }

    public void listenerRegistroPrecoExterno() {
        try {
            novoObjetoLicitatorioContrato();
            selecionado.transferirDadosObjetoLicitatorio();
            definirTipoDoContratoRegistroPrecoExterno();
            iniciarProcessoSelecaoFornecedor();
            atribuirHierarquiaAoContrato(selecionado.getRegistroSolicitacaoMaterialExterno().getUnidadeOrganizacional());
        } catch (ValidacaoException ve) {
            limparCamposGerais();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void listenerCredenciamento() {
        try {
            validarLicitacao(objetoContrato.getLicitacao());
            instanciarListasItemContrato();
            novoObjetoLicitatorioContrato();
            selecionado.transferirDadosObjetoLicitatorio();
            definirTipoDoContratoLicitacao();
            iniciarProcessoSelecaoFornecedor();
            atribuirHierarquiaAoContrato(selecionado.getLicitacao().getProcessoDeCompra().getUnidadeOrganizacional());
        } catch (ValidacaoException ve) {
            limparCamposGerais();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }


    public void listenerLicitacao() {
        try {
            validarLicitacao(objetoContrato.getLicitacao());
            instanciarListasItemContrato();
            novoObjetoLicitatorioContrato();
            if (selecionado.isDeLicitacao()) {
                atribuirHierarquiaAoContrato(selecionado.getLicitacao().getProcessoDeCompra().getUnidadeOrganizacional());
                recuperarAtaRegistroPreco();
            }
            selecionado.transferirDadosObjetoLicitatorio();
            definirTipoDoContratoLicitacao();
            iniciarProcessoSelecaoFornecedor();
        } catch (ValidacaoException ve) {
            limparCamposGerais();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void listenerAdesaoAtaInterna() {
        try {
            instanciarListasItemContrato();
            novoObjetoLicitatorioContrato();
            validarLicitacao(objetoContrato.getSolicitacaoMaterialExterno().getAtaRegistroPreco().getLicitacao());
            selecionado.transferirDadosObjetoLicitatorio();
            atribuirHierarquiaAoContrato(selecionado.getSolicitacaoAdesaoAtaInterna().getUnidadeOrganizacional());
            definirTipoDoContratoLicitacao();
            recuperarAtaRegistroPreco();
            iniciarProcessoSelecaoFornecedor();
        } catch (ValidacaoException ve) {
            limparCamposGerais();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void listenerIrp() {
        try {
            selecionado.getContratoLicitacao().setParticipanteIRP(objetoContrato.getParticipanteIRP());
            atribuirHierarquiaAdmParticipanteIrp();
            selecionado.setUnidadeAdministrativa(selecionado.getParticipanteIRP().getHierarquiaOrganizacional());
            selecionado.transferirDadosObjetoLicitatorio();
            recuperarAtaRegistroPreco();
            recuperarItensFornecedorVencedorProcessoLicitatorio();
            redirecionarParaAbaItens();
            FacesUtil.atualizarComponente("Formulario:tab-view-geral");
        } catch (ValidacaoException ve) {
            limparCamposGerais();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void atribuirHierarquiaAdmParticipanteIrp() {
        selecionado.getParticipanteIRP().setHierarquiaOrganizacional(facade.getParticipanteIRPFacade().buscarUnidadeParticipanteVigente(selecionado.getParticipanteIRP()));
    }

    public void listenerDispensaLicitacao() {
        try {
            if (objetoContrato.getDispensaDeLicitacao() != null) {
                validarDispensaDeLicitacao();
                novoObjetoLicitatorioContrato();
                selecionado.transferirDadosObjetoLicitatorio();
                atribuirHierarquiaAoContrato(selecionado.getDispensaDeLicitacao().getProcessoDeCompra().getUnidadeOrganizacional());
                definirTipoDoContratoDispensaLicitacao();
                iniciarProcessoSelecaoFornecedor();
            }
        } catch (ValidacaoException ve) {
            limparCamposGerais();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void atribuirHierarquiaAoContrato(UnidadeOrganizacional unidadeOrganizacional) {
        if (unidadeOrganizacional != null) {
            selecionado.setUnidadeAdministrativa(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                unidadeOrganizacional,
                facade.getSistemaFacade().getDataOperacao()));
        }
    }

    public List<DispensaDeLicitacao> buscarDispensaDeLicitacao(String parte) {
        return facade.getDispensaDeLicitacaoFacade().buscarDispensaDeLicitacaoPorNumeroOrExercicioOrResumoAndUsuarioAndGestor(parte.trim(), facade.getSistemaFacade().getUsuarioCorrente(), Boolean.TRUE);
    }

    public List<RegistroSolicitacaoMaterialExterno> buscarRegistroDePrecoExterno(String parte) {
        return facade.getRegistroSolicitacaoMaterialExternoFacade().buscarRegistroSolicitacaoMaterialExternoPorNumeroOrExercicioAndUsuarioAndGestor(parte.trim(), facade.getSistemaFacade().getUsuarioCorrente(), Boolean.TRUE);
    }

    public ConverterAutoComplete getConverterRegistroPrecoExterno() {
        return new ConverterAutoComplete(RegistroSolicitacaoMaterialExterno.class, facade.getRegistroSolicitacaoMaterialExternoFacade());
    }

    public void novoOrdemDeCompra() {
        if (!permiteCriarOrdemDeCompra()) {
            return;
        }
        this.ordemDeCompraSelecionada = new OrdemDeCompraContrato();
        this.ordemDeCompraSelecionada.setContrato(selecionado);
        this.ordemDeCompraSelecionada.setNumero(getProximoNumero(selecionado.getOrdensDeCompra()));
        this.ordemDeCompraSelecionada.setCriadaEm(new Date());
        confirmarOrdemDeCompra();
    }

    public void cancelarOrdemDeCompra() {
        this.ordemDeCompraSelecionada = null;
    }

    public void confirmarOrdemDeCompra() {
        if (!validarConfirmacao(this.ordemDeCompraSelecionada)) {
            return;
        }

        selecionado.setOrdensDeCompra(Util.adicionarObjetoEmLista(selecionado.getOrdensDeCompra(), this.ordemDeCompraSelecionada));
        cancelarOrdemDeCompra();
    }

    public void selecionarOrdemDeCompra(OrdemDeCompraContrato oc) {
        this.ordemDeCompraSelecionada = oc;
    }

    public void removerOrdemDeCompra(OrdemDeCompraContrato oc) {
        selecionado.getOrdensDeCompra().remove(oc);
    }

    private boolean permiteCriarOrdemDeCompra() {
        boolean resultado = true;
        if (selecionado.getExecucoes() == null || selecionado.getExecucoes().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Para criar ordens de compra, é necessário que existam execuções neste contrato. Por favor, informe ao menos uma execução para poder continuar.");
            resultado = false;
        }
        return resultado;
    }

    private void criarItensDeCompraBaseContratoSelecionado(OrdemDeCompraContrato occ) {
        itensOrdemDeCompra = new ArrayList<>();
        for (ItemContrato ic : selecionado.getItens()) {
            if (occ.possuiItemContrato(ic)) {
                ItemOrdemDeCompra ioc = occ.getItemOrdemDeCompraDoItemContrato(ic);
                ioc.setQuantidadeEmOutrasOrdensDeCompra(BigDecimal.ZERO);
                ioc.setQuantidadeEmExecucao(BigDecimal.ZERO);
                itensOrdemDeCompra.add(ioc);
            } else {
                ItemOrdemDeCompra ioc = new ItemOrdemDeCompra();
                ioc.setItemContrato(ic);
                ioc.setOrdemDeCompraContrato(occ);
                ioc.setQuantidade(BigDecimal.ZERO);
                ioc.setQuantidadeEmOutrasOrdensDeCompra(BigDecimal.ZERO);
                ioc.setQuantidadeEmExecucao(BigDecimal.ZERO);
                itensOrdemDeCompra.add(ioc);
            }
        }
    }

    private void carregarQuantidadeUtilizadaOutrasOrdensCompra() {
        for (OrdemDeCompraContrato ordemCompra : selecionado.getOrdensDeCompra()) {
            if (ordemCompra.equals(ordemDeCompraSelecionada)) {
                continue;
            }
            for (ItemOrdemDeCompra ioc : ordemCompra.getItens()) {
                for (ItemOrdemDeCompra iocLocal : itensOrdemDeCompra) {
                    if (ioc.getItemContrato().equals(iocLocal.getItemContrato())) {
                        iocLocal.setQuantidadeEmOutrasOrdensDeCompra(iocLocal.getQuantidadeEmOutrasOrdensDeCompra().add(ioc.getQuantidade()));
                    }
                }
            }
        }
    }

    public void carregarItensParaOrdemDeCompra(OrdemDeCompraContrato occ) {
        selecionarOrdemDeCompra(occ);
        criarItensDeCompraBaseContratoSelecionado(occ);
        carregarQuantidadeUtilizadaOutrasOrdensCompra();
    }

    private void criarAtualizarItensDaOrdemDeCompra() {
        for (ItemOrdemDeCompra ioc : itensOrdemDeCompra) {
            if (ioc.getQuantidade().compareTo(BigDecimal.ZERO) > 0) {
                ordemDeCompraSelecionada.setItens(Util.adicionarObjetoEmLista(ordemDeCompraSelecionada.getItens(), ioc));
            } else {
                try {
                    ordemDeCompraSelecionada.getItens().remove(ioc);
                } catch (Exception e) {
                }
            }
        }
        selecionado.setOrdensDeCompra(Util.adicionarObjetoEmLista(selecionado.getOrdensDeCompra(), ordemDeCompraSelecionada));
    }

    public void confirmarItensOrdemDeCompra() {
        criarAtualizarItensDaOrdemDeCompra();
        cancelarOrdemDeCompra();
    }

    public String getCaminhoBrasao() {
        String imagem = FacesUtil.geraUrlImagemDir();
        imagem += "/img/escudo.png";
        return imagem;
    }

    public void imprimirOrdemDeCompraSelecionadaDialog() {
        criarAtualizarItensDaOrdemDeCompra();
        gerarPDFDaOrdemDeCompraSelecionada();
    }

    public void imprimirOrdemDeCompraSelecionadaTabela(OrdemDeCompraContrato oc) {
        selecionarOrdemDeCompra(oc);
        gerarPDFDaOrdemDeCompraSelecionada();
    }

    private void gerarPDFDaOrdemDeCompraSelecionada() {
        Util.geraPDF("Ordem_de_compra_nr_" + ordemDeCompraSelecionada.getNumero(), gerarConteudoImpressaoOrdemDeCompraSelecionada(), FacesContext.getCurrentInstance());
    }

    public String gerarConteudoImpressaoOrdemDeCompraSelecionada() {
        if (ordemDeCompraSelecionada == null) {
            return "";
        }
        String content = "";
        content += "<?xml version='1.0' encoding='iso-8859-1'?>\n";
        content += "<!DOCTYPE HTML PUBLIC 'HTML 4.01 Transitional//PT' 'http://www.w3.org/TR/html4/loose.dtd'>\n";
        content += "<html>\n";
        content += " <head>";
        content += " <title>Ordem de Compra</title>";
        content += " </head>";

        content += "<style type='text/css'>\n";
        content += " body{font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 12px;}\n";
        content += "</style>\n";
        content += " <body>";
        content += "<div style='border: 1px solid black;text-align: left; padding : 3px;'>\n";
        content += " <table>" + "<tr>";
        content += " <td style='text-align: center!important;'><img src='" + getCaminhoBrasao() + "' alt='Smiley face' height='90' width='85' /></td>   ";
        content += " <td><b> PREFEITURA MUNICIPAL DE RIO BRANCO <br/>\n";
        content += "         MUNICÍPIO DE RIO BRANCO<br/>\n";
        content += "         ORDENS DE COMPRA</b></td>\n";
        content += "</tr>" + "</table>";
        content += "</div>";
        content += "<div style='border: 1px solid black;text-align: left; margin-top: -1px; padding : 3px;'>\n";
        content += " <table style='font-size: 11px;'>";

        content += "<tr>";
        content += " <td><b>Ordem de Compra Nº: </b></td>";
        if (ordemDeCompraSelecionada.getId() == null) {
            content += " <td>" + ordemDeCompraSelecionada.getNumero() + " <font style='color: red;'>** ORDEM DE COMPRA AINDA NÃO GRAVADA **</font></td>";
        } else {
            content += " <td>" + ordemDeCompraSelecionada.getNumero() + "</td>";
        }
        content += "</tr>";

        content += "<tr>";
        content += " <td><b>Contrato: </b></td>";
        if (!Strings.isNullOrEmpty(selecionado.getNumeroTermo())) {
            content += " <td>" + selecionado.getNumeroTermo() + "/" + selecionado.getExercicioContrato() + "</td>";
        } else {
            content += " <td style='color:red'>** CONTRATO AINDA NÃO GRAVADO **</td>";
        }

        content += "</tr>";

        content += "<tr>";
        content += " <td ><b>Objeto: </b></td>";
        content += " <td >" + selecionado.getObjeto() + "</td>";
        content += "</tr>";

        content += "<tr>";
        content += " <td ><b>Fornecedor: </b></td>";
        content += " <td>" + selecionado.getContratado() + "</td>";
        content += "</tr>";

        content += "<tr>";
        content += " <td ><b>Unidade Administrativa: </b></td>";
        content += " <td >" + selecionado.getUnidadeAdministrativa() + "</td>";
        content += "</tr>";

        content += "<tr>";
        content += " <td ><b>Local de Entrega: </b></td>";
        content += " <td >" + selecionado.getObjetoAdequado().getLocalDeEntrega() + "</td>";
        content += "</tr>";

        content += "</table>";
        content += "</div>";

        content += "<br/>";

        if (ordemDeCompraSelecionada.getItens().isEmpty()) {
            content += "<div style='text-align:center!important;'><b>ESTA ORDEM DE COMPRA NÃO POSSUI ITENS</b></div>";
        } else {
            content += "<div style='text-align:center!important;'><b>ITENS A SEREM ENTREGUES</b></div>";

            content += " <table cellpadding='4' cellspacing='0' style='margin-top: 10px; border-collapse: collapse' align='center'>";
            content += "<tr >";
            content += "  <td style='border: 1px solid black;'><b>Item</b></td>";
            content += "  <td style='border: 1px solid black;'><b>Descrição</b></td>";
            content += "  <td style='border: 1px solid black;'><b>Quantidade</b></td>";
            content += "</tr>";

            for (ItemOrdemDeCompra ioc : ordemDeCompraSelecionada.getItens()) {
                content += "<tr>";
                Integer indice = ordemDeCompraSelecionada.getItens().indexOf(ioc) + 1;
                content += " <td style='text-align: center; border: 1px solid black;'>" + indice.intValue() + "</td>";
                content += " <td style='text-align: left; border: 1px solid black;'>" + ioc.getItemContrato() + "</td>";
                content += " <td style='text-align: center; border: 1px solid black;'>" + ioc.getQuantidade() + "</td>";
                content += "</tr>";
            }
        }
        content += "</table>";

        content += "<br/><br/><br/><br/><br/><br/><br/><br/><br/>";

        content += "<div style='text-align:center!important;'>";
        content += "_______________________________________<br/>";
        content += "Assinatura do Responsável<br/>";

        content += "Rio Branco " + new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        content += "</div>";

        content += "</body>";

        content += "</html>";

        return content;
    }

    public void validarQuantidadeOrdemDeCompra(AjaxBehaviorEvent ev) {
        ItemOrdemDeCompra ioc = (ItemOrdemDeCompra) ev.getComponent().getAttributes().get("ioc");
        if (ioc.getQuantidade().add(ioc.getQuantidadeEmOutrasOrdensDeCompra()).compareTo(ioc.getQuantidadeEmExecucao()) > 0) {
            FacesUtil.addOperacaoNaoPermitida("A quantidade informada excede a quantidade disponível em execução. Verifique as quantidades informadas e tente novamente");
            ioc.setQuantidade(BigDecimal.ZERO);
            RequestContext.getCurrentInstance().update(ev.getComponent().getClientId());
        }
    }

    public List<SelectItem> getTipoMovimentacaoContrato() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoMovimentacaoContrato tipo : TipoMovimentacaoContrato.values()) {
            if (selecionado.getCaucoes() != null && !selecionado.getCaucoes().isEmpty() && !tipo.equals(TipoMovimentacaoContrato.ORDINARIA)) {
                retorno.add(new SelectItem(tipo, tipo.getDescricao()));
            } else if ((selecionado.getCaucoes() == null || selecionado.getCaucoes().isEmpty()) && tipo.equals(TipoMovimentacaoContrato.ORDINARIA)) {
                retorno.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return retorno;
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        return facade.getAtoLegalFacade().listaFiltrandoAtoLegalPorTipoExercicio(parte.trim(), facade.getSistemaFacade().getExercicioCorrente(), TipoAtoLegal.PORTARIA);
    }

    public BigDecimal getValorTotalItensRescisao() {
        BigDecimal valor = BigDecimal.ZERO;
        if (!Util.isListNullOrEmpty(movimentosItensRescisaoContrato)) {
            for (MovimentoItemContrato item : movimentosItensRescisaoContrato) {
                valor = valor.add(item.getValorTotal());
            }
        }
        return valor;
    }

    public void novaRescisaoContrato() {
        try {
            validarNovaRescisaoContrato();
            if (selecionado.getRescisaoContrato() == null) {
                RescisaoContrato rescisaoContrato = new RescisaoContrato();
                rescisaoContrato.setContrato(selecionado);
                rescisaoContrato.setRescindidoEm(facade.getSistemaFacade().getDataOperacao());
                selecionado.setRescisaoContrato(rescisaoContrato);
            }

            movimentosItensRescisaoContrato = Lists.newArrayList();
            for (ItemContrato item : selecionado.getItens()) {
                MovimentoItemContrato movItemExec = new MovimentoItemContrato();
                movItemExec.setItemContrato(item);
                movItemExec.setDataMovimento(DataUtil.getDataComHoraAtual(facade.getSistemaFacade().getDataOperacao()));
                movItemExec.setIndice(facade.getSaldoItemContratoFacade().gerarIndiceMovimentoItemContrato(item));
                movItemExec.setIdOrigem(selecionado.getId());
                movItemExec.setIdMovimento(selecionado.getId());
                movItemExec.setOrigem(OrigemSaldoItemContrato.CONTRATO);
                movItemExec.setTipo(TipoSaldoItemContrato.RESCISAO);
                movItemExec.setOperacao(OperacaoSaldoItemContrato.PRE_EXECUCAO);
                movItemExec.setSubTipo(SubTipoSaldoItemContrato.EXECUCAO);
                movItemExec.setValorUnitario(item.getValorUnitario());

                BigDecimal qtdeOriginal = item.getQuantidadeTotalContrato();
                BigDecimal qtdeExecOriginal = facade.getExecucaoContratoFacade().getQuantidadeExecucaoOriginal(item);
                BigDecimal qtdeEstExecOriginal = facade.getExecucaoContratoFacade().getQuantidadeEstornadaExecucaoOriginal(item);
                movItemExec.setQuantidade(qtdeOriginal.subtract(qtdeExecOriginal).add(qtdeEstExecOriginal));
                item.setQuantidadeRescisao(movItemExec.getQuantidade());

                BigDecimal valorOriginal = item.getValorTotal();
                BigDecimal valorExecOriginal = facade.getExecucaoContratoFacade().getValorExecutadoOriginal(item);
                BigDecimal valorEstExecOriginal = facade.getExecucaoContratoFacade().getValorEstornadoExecucaoOriginal(item);
                movItemExec.setValorTotal(valorOriginal.subtract(valorExecOriginal).add(valorEstExecOriginal));
                item.setValorTotalRescisao(movItemExec.getValorTotal());
                if (movItemExec.getValorTotal().compareTo(BigDecimal.ZERO) > 0) {
                    movimentosItensRescisaoContrato.add(movItemExec);
                }
            }
            FacesUtil.executaJavaScript("dlgRescisao.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarNovaRescisaoContrato() {
        ValidacaoException ve = new ValidacaoException();
        validarExecucaoEmpenhada();
        if (!Util.isListNullOrEmpty(selecionado.getAlteracoesContratuais())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido rescindir um contrato com aditivo/apostilamento ");
        }
        ve.lancarException();
    }

    private void validarRescisaoContrato() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getSaldoAtualContrato().compareTo(getValorTotalItensRescisao()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor a rescindir deve ser igual ao saldo atual do contrato.");
        }
        ve.lancarException();
    }

    public void confirmarRescisaoContrato() {
        try {
            validarRescisaoContrato();
            Util.validarCampos(selecionado.getRescisaoContrato());
            selecionado = facade.salvarRescisaoContrato(selecionado, movimentosItensRescisaoContrato);
            FacesUtil.addOperacaoRealizada("Contrato rescindido com sucesso!");
            redirecionarParaVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarRescisaoContrato() {
        if (selecionado.getRescisaoContrato().getId() == null) {
            selecionado.setRescisaoContrato(null);
            selecionado.getItens().forEach(item ->item.setValorTotalRescisao(BigDecimal.ZERO));
            selecionado.getItens().forEach(item ->item.setQuantidadeRescisao(BigDecimal.ZERO));
        }
    }

    public void atribuirNullDadosIRP() {
        selecionado.setUnidadeAdministrativa(null);
        instanciarListasItemContrato();
        setAtaRegistroPreco(null);
        updateDadosGerais();
        updateItens();
    }

    public List<ResultadoParcela> getResultadoParcelaCalculoDividaDiversaDaPenalidade() {
        return resultadoParcelaCalculoDividaDiversaDaPenalidade;
    }

    public void setResultadoParcelaCalculoDividaDiversaDaPenalidade
        (List<ResultadoParcela> resultadoParcelaCalculoDividaDiversaDaPenalidade) {
        this.resultadoParcelaCalculoDividaDiversaDaPenalidade = resultadoParcelaCalculoDividaDiversaDaPenalidade;
    }

    public void buscarResultadoParcelaDoCalculoDividaDividaDaPenalidade() {
        resultadoParcelaCalculoDividaDiversaDaPenalidade = Lists.newArrayList();
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, ((MultaPenalidadeContrato) penalidadeContratoSelecionado).getCalculoDividaDiversa().getId());
        resultadoParcelaCalculoDividaDiversaDaPenalidade = consultaParcela.executaConsulta().getResultados();
    }

    @Override
    public void validarExclusaoEntidade() {
        facade.validarExclusaoContrato(selecionado);
    }

    public void excluir() {
        try {
            facade.remover(selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public boolean permitiDigitarQuantidadeOrValor(ItemContrato item) {
        if (item.getSelecionado() && !item.getUtilizadoProcesso()) {
            return isRegistroDePrecos() || selecionado.isDeProcedimentosAuxiliares();
        }
        return false;
    }

    public boolean isTipoApuracaoPorLote() {
        return selecionado.isTipoApuracaoPorLote();
    }

    public boolean isTipoAvaliacaoMaiorDesconto() {
        return selecionado.isTipoAvaliacaoMaiorDesconto();
    }

    public void visualizarReservaExecucao(ExecucaoContrato execucaoContrato) {
        this.execucaoContratoSelecionada = execucaoContrato;
        FacesUtil.executaJavaScript("expandirRowToggler();");
    }

    public void atribuirTerminoVigenciaAtual() {
        if (selecionado.getTerminoVigencia() != null) {
            selecionado.setTerminoVigenciaAtual(selecionado.getTerminoVigencia());
        }
    }

    public void definirNumeroAnoTermoContrato() {
        try {
            setNumeroTermo(facade.gerarNumeroTermoContrato(selecionado));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void atribuirTerminoExecucaoAtual() {
        if (selecionado.getTerminoExecucao() != null) {
            selecionado.setTerminoExecucaoAtual(selecionado.getTerminoExecucao());
        }
    }

    public void editarItemContrato(ItemContrato item) {
        this.itemContrato = item;
    }

    public void confirmarCodigoCMED() {
        if (Strings.isNullOrEmpty(itemContrato.getCodigoCmed())) {
            FacesUtil.addCampoObrigatorio("O campo código CMED deve ser informado.");
            return;
        }
        FacesUtil.executaJavaScript("dialogCodigoCmed.hide()");
        Util.adicionarObjetoEmLista(selecionado.getItens(), itemContrato);
    }

    public void cancelarCodigoCMED() {
        itemContrato = null;
    }

    public void redirecionarParaContrato(Contrato contrato) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + contrato.getId() + "/");
    }

    public RescisaoContrato getRescisaoContratoSelecionado() {
        return rescisaoContratoSelecionado;
    }

    public void setRescisaoContratoSelecionado(RescisaoContrato rescisaoContratoSelecionado) {
        this.rescisaoContratoSelecionado = rescisaoContratoSelecionado;
    }

    public CaucaoContrato getCaucaoContratoAtivo() {
        return caucaoContratoAtivo;
    }

    public void setCaucaoContratoAtivo(CaucaoContrato caucaoContratoAtivo) {
        this.caucaoContratoAtivo = caucaoContratoAtivo;
    }

    public List<ItemOrdemDeCompra> getItensOrdemDeCompra() {
        return itensOrdemDeCompra;
    }

    public void setItensOrdemDeCompra(List<ItemOrdemDeCompra> itensOrdemDeCompra) {
        this.itensOrdemDeCompra = itensOrdemDeCompra;
    }

    public OrdemDeCompraContrato getOrdemDeCompraSelecionada() {
        return ordemDeCompraSelecionada;
    }

    public void setOrdemDeCompraSelecionada(OrdemDeCompraContrato ordemDeCompraSelecionada) {
        this.ordemDeCompraSelecionada = ordemDeCompraSelecionada;
    }

    public DocumentoContrato getDocumentoContratoSelecionado() {
        return documentoContratoSelecionado;
    }

    public void setDocumentoContratoSelecionado(DocumentoContrato documentoContratoSelecionado) {
        this.documentoContratoSelecionado = documentoContratoSelecionado;
    }

    public ContatoContrato getContatoContratoSelecionado() {
        return contatoContratoSelecionado;
    }

    public void setContatoContratoSelecionado(ContatoContrato contatoContratoSelecionado) {
        this.contatoContratoSelecionado = contatoContratoSelecionado;
    }

    public TipoMovimentacaoContrato getTipoMovimentacaoContratoSelecionado() {
        return tipoMovimentacaoContratoSelecionado;
    }

    public void setTipoMovimentacaoContratoSelecionado(TipoMovimentacaoContrato tipoMovimentacaoContratoSelecionado) {
        this.tipoMovimentacaoContratoSelecionado = tipoMovimentacaoContratoSelecionado;
    }

    public NotaFiscalExecucaoContrato getNotaFiscalExecucaoContratoSelecionado() {
        return notaFiscalExecucaoContratoSelecionado;
    }

    public void setNotaFiscalExecucaoContratoSelecionado(NotaFiscalExecucaoContrato
                                                             notaFiscalExecucaoContratoSelecionado) {
        this.notaFiscalExecucaoContratoSelecionado = notaFiscalExecucaoContratoSelecionado;
    }

    public ContratoFacade getFacade() {
        return facade;
    }

    public TipoCaucaoContrato getTipoCaucaoContrato() {
        return tipoCaucaoContrato;
    }

    public void setTipoCaucaoContrato(TipoCaucaoContrato tipoCaucaoContrato) {
        this.tipoCaucaoContrato = tipoCaucaoContrato;
    }

    public List<Pessoa> getFornecedoresGanhadoresLicitacao() {
        return fornecedoresGanhadoresLicitacao;
    }

    public void setFornecedoresGanhadoresLicitacao(List<Pessoa> fornecedoresGanhadoresLicitacao) {
        this.fornecedoresGanhadoresLicitacao = fornecedoresGanhadoresLicitacao;
    }

    public PenalidadeContrato getPenalidadeContratoSelecionado() {
        return penalidadeContratoSelecionado;
    }

    public void setPenalidadeContratoSelecionado(PenalidadeContrato penalidadeContratoSelecionado) {
        this.penalidadeContratoSelecionado = penalidadeContratoSelecionado;
    }

    public FiscalContrato getFiscalContrato() {
        return fiscalContrato;
    }

    public void setFiscalContrato(FiscalContrato fiscalContrato) {
        this.fiscalContrato = fiscalContrato;
    }

    public CaucaoContrato getCaucaoContratoSelecionada() {
        return caucaoContratoSelecionada;
    }

    public void setCaucaoContratoSelecionada(CaucaoContrato caucaoContratoSelecionada) {
        this.caucaoContratoSelecionada = caucaoContratoSelecionada;
    }

    public OrdemDeServicoContrato getOrdemDeServicoSelecionada() {
        return ordemDeServicoSelecionada;
    }

    public void setOrdemDeServicoSelecionada(OrdemDeServicoContrato ordemDeServicoSelecionada) {
        this.ordemDeServicoSelecionada = ordemDeServicoSelecionada;
    }

    public ExecucaoContrato getExecucaoContratoSelecionada() {
        return execucaoContratoSelecionada;
    }

    public void setExecucaoContratoSelecionada(ExecucaoContrato execucaoContratoSelecionada) {
        this.execucaoContratoSelecionada = execucaoContratoSelecionada;
    }

    public ItemContrato getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(ItemContrato itemContrato) {
        this.itemContrato = itemContrato;
    }

    public boolean isTodosItensValorSelecionados() {
        return todosItensValorSelecionados;
    }

    public void setTodosItensValorSelecionados(boolean todosItensValorSelecionados) {
        this.todosItensValorSelecionados = todosItensValorSelecionados;
    }

    public List<SolicitacaoMaterialExterno> completarSolicitacaoMaterial(String parte) {
        return facade.getSolicitacaoMaterialExternoFacade().buscarSolicitacaoMaterialExternoPorAnoOrNumeroAndTipoOndeUsuarioGestorLicitacao(parte,
            TipoSolicitacaoRegistroPreco.INTERNA, facade.getSistemaFacade().getUsuarioCorrente());
    }

    public boolean isTodosItensQuantidadeSelecionados() {
        return todosItensQuantidadeSelecionados;
    }

    public void setTodosItensQuantidadeSelecionados(boolean todosItensQuantidadeSelecionados) {
        this.todosItensQuantidadeSelecionados = todosItensQuantidadeSelecionados;
    }

    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    public AtaRegistroPreco getAtaRegistroPreco() {
        return ataRegistroPreco;
    }

    public void setAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
    }

    public GestorContrato getGestorContrato() {
        return gestorContrato;
    }

    public void setGestorContrato(GestorContrato gestorContrato) {
        this.gestorContrato = gestorContrato;
    }

    public void novoGestorContrato() {
        try {
            Util.validarCampos(selecionado);
            gestorContrato = new GestorContrato();
            gestorContrato.setContrato(selecionado);
            gestorContrato.setInicioVigencia(selecionado.getInicioVigencia());
            gestorContrato.setFinalVigencia(selecionado.getTerminoVigenciaAtual());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerGestorContrato(GestorContrato gc) {
        selecionado.getGestores().remove(gc);
    }

    public void editarGestorContrato(GestorContrato gc) {
        gestorContrato = (GestorContrato) Util.clonarObjeto(gc);
    }

    public void confirmarGestorContrato() {
        try {
            Util.validarCampos(gestorContrato);
            validarGestor();
            Util.adicionarObjetoEmLista(selecionado.getGestores(), gestorContrato);
            cancelarGestorContrato();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarGestorContrato() {
        gestorContrato = null;
    }

    private void validarGestor() {
        ValidacaoException ve = new ValidacaoException();
        if (gestorContrato.getServidor() == null && gestorContrato.getServidorPF() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor deve ser informado.");
        }

        if (gestorContrato.getFinalVigencia() != null) {
            if (gestorContrato.getFinalVigencia().before(gestorContrato.getInicioVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O fim de vigência deve ser superior ao início de vigência.");
            }
            if (gestorContrato.getFinalVigencia().after(selecionado.getTerminoVigenciaAtual())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O fim de vigência deve ser anterior ao término de vigência atual do contrato.");
            }
        }
        if (selecionado.getGestores().stream()
            .anyMatch(gc -> !gc.equals(gestorContrato)
                && DataUtil.isBetween(gc.getInicioVigencia(), gc.getFinalVigencia(), gestorContrato.getInicioVigencia()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É permitido somente um gestor vigente por contrato.");
        }
        ve.lancarException();
    }

    public ObjetoContrato getObjetoContrato() {
        return objetoContrato;
    }

    public void setObjetoContrato(ObjetoContrato objetoContrato) {
        this.objetoContrato = objetoContrato;
    }

    public List<ItemContrato> getItensControleValor() {
        return itensControleValor;
    }

    public void setItensControleValor(List<ItemContrato> itensControleValor) {
        this.itensControleValor = itensControleValor;
    }

    public List<ItemContrato> getItensControleQuantidade() {
        return itensControleQuantidade;
    }

    public void setItensControleQuantidade(List<ItemContrato> itensControleQuantidade) {
        this.itensControleQuantidade = itensControleQuantidade;
    }

    public void gerarDam(ResultadoParcela parcela) {
        facade.getLancamentoDebitoContratoFacade().gerarDam(parcela);
    }

    public void buscarParcelas() {
        if (selecionado.getContratoConcessao()) {
            resultadoParcelas = facade.getLancamentoDebitoContratoFacade().buscarParcelas(selecionado);
            resultadoParcelaTotalizador = new ConsultaDebitoControlador.TotalTabelaParcelas();
            for (ResultadoParcela resultado : resultadoParcelas) {
                resultadoParcelaTotalizador.soma(resultado);
            }
        }
    }

    public List<ResultadoParcela> getResultadoParcelas() {
        return resultadoParcelas;
    }

    public void setResultadoParcelas(List<ResultadoParcela> resultadoParcelas) {
        this.resultadoParcelas = resultadoParcelas;
    }

    public ConsultaDebitoControlador.TotalTabelaParcelas getResultadoParcelaTotalizador() {
        return resultadoParcelaTotalizador;
    }

    public void setResultadoParcelaTotalizador(ConsultaDebitoControlador.TotalTabelaParcelas resultadoParcelaTotalizador) {
        this.resultadoParcelaTotalizador = resultadoParcelaTotalizador;
    }

    private void novoFiltroOrdemCompraServico() {
        filtroOrdemCompraVO = new FiltroOrdemCompraServicoContratoVO(selecionado, isOperacaoVer());
    }

    private void novoFiltroHistoricoProcesso() {
        Long idMovimento;
        TipoMovimentoProcessoLicitatorio tipoProcesso;
        switch (selecionado.getTipoAquisicao()) {
            case LICITACAO:
                tipoProcesso = TipoMovimentoProcessoLicitatorio.LICITACAO;
                idMovimento = selecionado.getLicitacao().getId();
                break;
            case DISPENSA_DE_LICITACAO:
                tipoProcesso = TipoMovimentoProcessoLicitatorio.DISPENSA_LICITACAO_INEXIGIBILIDADE;
                idMovimento = selecionado.getDispensaDeLicitacao().getId();
                break;
            case INTENCAO_REGISTRO_PRECO:
                tipoProcesso = TipoMovimentoProcessoLicitatorio.IRP;
                idMovimento = selecionado.getParticipanteIRP().getIntencaoRegistroDePreco().getId();
                break;
            case ADESAO_ATA_REGISTRO_PRECO_INTERNA:
                tipoProcesso = TipoMovimentoProcessoLicitatorio.ADESAO_INTERNA;
                idMovimento = selecionado.getSolicitacaoAdesaoAtaInterna().getId();
                break;
            case REGISTRO_DE_PRECO_EXTERNO:
                tipoProcesso = TipoMovimentoProcessoLicitatorio.ADESAO_EXTERNA;
                idMovimento = selecionado.getRegistroSolicitacaoMaterialExterno().getId();
                break;
            case CONTRATOS_VIGENTE:
                tipoProcesso = TipoMovimentoProcessoLicitatorio.CONTRATO_VIGENTE;
                idMovimento = selecionado.getContratosVigente().getCotacao().getId();
                break;
            default:
                tipoProcesso = TipoMovimentoProcessoLicitatorio.CONTRATO;
                idMovimento = selecionado.getId();
        }
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(idMovimento, tipoProcesso, selecionado.getId());
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
        if ("tab-oc".equals(tab)) {
            novoFiltroOrdemCompraServico();
        }
        if ("tab-execucao".equals(tab)) {
            filtroResumoExecucaoVO = new FiltroResumoExecucaoVO(TipoResumoExecucao.CONTRATO);
            filtroResumoExecucaoVO.setContrato(selecionado);
            filtroResumoExecucaoVO.setIdProcesso(selecionado.getId());
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

    public boolean isResponsavelUnidadePessoaFisica() {
        return responsavelUnidadePessoaFisica;
    }

    public void setResponsavelUnidadePessoaFisica(boolean responsavelUnidadePessoaFisica) {
        this.responsavelUnidadePessoaFisica = responsavelUnidadePessoaFisica;
    }

    public List<TipoObjetoCompraDeferimentoContrato> getTiposObjetoCompraDeferimento() {
        return tiposObjetoCompraDeferimento;
    }

    public void setTiposObjetoCompraDeferimento(List<TipoObjetoCompraDeferimentoContrato> tiposObjetoCompraDeferimento) {
        this.tiposObjetoCompraDeferimento = tiposObjetoCompraDeferimento;
    }

    public RequisicaoDeCompra getRequisicaoDeCompra(Long id) {
        return facade.getRequisicaoDeCompraFacade().recuperar(id);
    }

    public String getNumeroTermo() {
        return numeroTermo;
    }

    public void setNumeroTermo(String numeroTermo) {
        this.numeroTermo = numeroTermo;
    }

    public List<Pessoa> completarFiscalContrato(String parte) {
        return facade.completarFiscalContrato(parte);
    }

    public List<Pessoa> completarGestorContrato(String parte) {
        return facade.completarGestorContrato(parte);
    }

    public FiltroOrdemCompraServicoContratoVO getFiltroOrdemCompraVO() {
        return filtroOrdemCompraVO;
    }

    public void setFiltroOrdemCompraVO(FiltroOrdemCompraServicoContratoVO filtroOrdemCompraVO) {
        this.filtroOrdemCompraVO = filtroOrdemCompraVO;
    }

    public FiltroResumoExecucaoVO getFiltroResumoExecucaoContrato() {
        return filtroResumoExecucaoVO;
    }

    public void setFiltroResumoExecucaoContrato(FiltroResumoExecucaoVO filtroResumoExecucaoVO) {
        this.filtroResumoExecucaoVO = filtroResumoExecucaoVO;
    }

    public ContratoAprovacaoVO getContratoAprovacaoVO() {
        return contratoAprovacaoVO;
    }

    public void setContratoAprovacaoVO(ContratoAprovacaoVO contratoAprovacaoVO) {
        this.contratoAprovacaoVO = contratoAprovacaoVO;
    }

    public List<MovimentoItemContrato> getMovimentosItensRescisaoContrato() {
        return movimentosItensRescisaoContrato;
    }

    public void setMovimentosItensRescisaoContrato(List<MovimentoItemContrato> movimentosItensRescisaoContrato) {
        this.movimentosItensRescisaoContrato = movimentosItensRescisaoContrato;
    }
}
