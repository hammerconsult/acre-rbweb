/*
 * Codigo gerado automaticamente em Tue Nov 29 17:31:17 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.reinf.NaturezaRendimentoReinf;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.contabil.reinf.TipoServicoReinf;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.nfse.domain.NotaFiscal;
import br.com.webpublico.reinf.eventos.EventoReinfDTO;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
//        Mapeamento liquidação
    @URLMapping(id = "novoliquidacao", pattern = "/liquidacao/novo/", viewId = "/faces/financeiro/orcamentario/liquidacao/edita.xhtml"),
    @URLMapping(id = "editarliquidacao", pattern = "/liquidacao/editar/#{liquidacaoControlador.id}/", viewId = "/faces/financeiro/orcamentario/liquidacao/edita.xhtml"),
    @URLMapping(id = "verliquidacao", pattern = "/liquidacao/ver/#{liquidacaoControlador.id}/", viewId = "/faces/financeiro/orcamentario/liquidacao/visualizar.xhtml"),
    @URLMapping(id = "listarliquidacao", pattern = "/liquidacao/listar/", viewId = "/faces/financeiro/orcamentario/liquidacao/lista.xhtml"),

//        Mapeamento liquidação de resto a pagar
    @URLMapping(id = "novoliquidacao-resto", pattern = "/liquidacao/resto-a-pagar/novo/", viewId = "/faces/financeiro/orcamentario/liquidacao/edita.xhtml"),
    @URLMapping(id = "editarliquidacao-resto", pattern = "/liquidacao/resto-a-pagar/editar/#{liquidacaoControlador.id}/", viewId = "/faces/financeiro/orcamentario/liquidacao/edita.xhtml"),
    @URLMapping(id = "verliquidacao-resto", pattern = "/liquidacao/resto-a-pagar/ver/#{liquidacaoControlador.id}/", viewId = "/faces/financeiro/orcamentario/liquidacao/visualizar.xhtml"),
    @URLMapping(id = "listarliquidacao-resto", pattern = "/liquidacao/resto-a-pagar/listar/", viewId = "/faces/financeiro/orcamentario/liquidacao/listarestopagar.xhtml"),

    @URLMapping(id = "consultar-notas-fiscais-liquidacao", pattern = "/liquidacao/#{liquidacaoControlador.id}/notas-fiscais/", viewId = "/faces/financeiro/orcamentario/liquidacao/consultar-notas-fiscais-da-liquidacao.xhtml")
})
public class LiquidacaoControlador extends PrettyControlador<Liquidacao> implements Serializable, CRUD {
    @EJB
    private LiquidacaoFacade facade;
    private static final BigDecimal CEM = new BigDecimal("100");
    private ConverterAutoComplete converterDesdobramentoObrigacao;
    private ConverterAutoComplete converterDesdobramentoEmpenho;
    private MoneyConverter moneyConverter;
    private LiquidacaoDoctoFiscal documentoFiscal;
    private Desdobramento desdobramento;
    private HistoricoContabil historicoPadrao;
    private List<Conta> detalhamentos;
    private String filtroDetalhamento;
    private BigDecimal saldoLiquidar;
    private LiquidacaoObrigacaoPagar liquidacaoObrigacaoPagar;
    private DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar;
    private DesdobramentoEmpenho desdobramentoEmpenho;
    private Pagamento pagamento;
    private PagamentoEstorno pagamentoEstorno;
    private List<DocumentoFiscalIntegracao> documentosIntegracao;
    private DocumentoFiscalIntegracao documentoIntegracao;
    private DocumentoFiscalIntegracaoAssistente assistenteDocumentoFiscal;
    private boolean empenhoComObrigacaoAPagar;
    private boolean isUnidadeBloqueadaParaNovoDocBensEstoque;
    private List<NotaFiscal> notasFiscais;
    private boolean renderizarCamposNovoDocumentoEstoque;
    private boolean integracaoDocumentoFiscal;
    private ConfiguracaoContabil configuracaoContabil;
    private Boolean renderizarCamposReinf2010;
    private Boolean renderizarCamposReinf4020;
    private Boolean isContaDespesaEventoReinf;
    private Boolean hasPagamentoNaoEstornado;
    private EventoReinfDTO eventoR2010Atual;
    private EventoReinfDTO eventoR4020Atual;

    public LiquidacaoControlador() {
        super(Liquidacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        if (selecionado.isLiquidacaoNormal()) {
            return "/liquidacao/";
        } else {
            return "/liquidacao/resto-a-pagar/";
        }
    }

    public void redirecionarParaLista() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
        cleanScoped();
    }

    public void redirecionarParaVer(Liquidacao liquidacao) {
        selecionado = liquidacao;
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        cleanScoped();
    }

    public String getTituloLiquidacao() {
        if (selecionado.isLiquidacaoNormal()) {
            return "Liquidação";
        } else {
            return "Liquidação de Resto a Pagar";
        }
    }

    public String getTituloEmpenho() {
        if (selecionado.isLiquidacaoNormal()) {
            return "Empenho";
        } else {
            return "Empenho de Resto a Pagar";
        }
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public LiquidacaoFacade getFacade() {
        return facade;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    //    Liquidação
    @URLAction(mappingId = "novoliquidacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            super.novo();
            parametrosIniciais();
            selecionado.setCategoriaOrcamentaria(CategoriaOrcamentaria.NORMAL);
            selecionado.setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.SEM_RECONHECIMENTO_OBRIGACAO);
            empenhoComObrigacaoAPagar = false;
            buscarIsUnidadeBloqueadaParaNovoDocBensEstoque();
            notasFiscais = Lists.newArrayList();
            renderizarCamposNovoDocumentoEstoque = false;
            integracaoDocumentoFiscal = false;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void recuperarConfiguracaoContabil() {
        configuracaoContabil = facade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
        if (configuracaoContabil == null) {
            throw new ValidacaoException("Configurção contábil não encontrada para continuar a liquidação.");
        }
    }

    @URLAction(mappingId = "editarliquidacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        empenhoComObrigacaoAPagar = facade.getEmpenhoFacade().verificarSeEmpenhoPossuiObrigacaoPagar(selecionado.getEmpenho());
        pagamento = null;
        pagamentoEstorno = null;
        if (selecionado.getCategoriaOrcamentaria().isResto()) {
            FacesUtil.addOperacaoNaoPermitida("A Liquidação selecionada é de Categoria " + selecionado.getCategoriaOrcamentaria().getDescricao() + ", portanto não será possível editar na tela de Liquidação Normal.");
            redireciona();
        }
        buscarIsUnidadeBloqueadaParaNovoDocBensEstoque();
        notasFiscais = Lists.newArrayList();
        renderizarCamposNovoDocumentoEstoque = false;
        integracaoDocumentoFiscal = false;
        recuperarConfiguracaoContabil();
        atualizarIsContaDespesaEventoReinfComEmpenho();
        atualizarRenderizarEventoReinfVerEditar();
        atualizarHasPagamentoNaoEstornado();
        facade.recuperarTransferenciasLiquidacaoDoctoFiscal(selecionado);
    }

    @URLAction(mappingId = "verliquidacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        empenhoComObrigacaoAPagar = facade.getEmpenhoFacade().verificarSeEmpenhoPossuiObrigacaoPagar(selecionado.getEmpenho());
        pagamento = null;
        pagamentoEstorno = null;
        recuperarConfiguracaoContabil();
        atualizarIsContaDespesaEventoReinfComEmpenho();
        atualizarRenderizarEventoReinfVerEditar();
        if (selecionado.getCategoriaOrcamentaria().isResto()) {
            FacesUtil.addOperacaoNaoPermitida("A Liquidação selecionada é de Categoria " + selecionado.getCategoriaOrcamentaria().getDescricao() + ", portanto não será possível visualizar na tela de Liquidação Normal.");
            redireciona();
        }
        facade.recuperarTransferenciasLiquidacaoDoctoFiscal(selecionado);
    }

    @URLAction(mappingId = "consultar-notas-fiscais-liquidacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void consultarDocumentosNotasFiscaisLiquidacao() {
        super.editar();
    }

    //    Liquidação  de Resto a Pagar
    @URLAction(mappingId = "novoliquidacao-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRestoPagar() {
        super.novo();
        parametrosIniciais();
        buscarIsUnidadeBloqueadaParaNovoDocBensEstoque();
        selecionado.setCategoriaOrcamentaria(CategoriaOrcamentaria.RESTO);
        selecionado.setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.SEM_RECONHECIMENTO_OBRIGACAO);
    }

    @URLAction(mappingId = "editarliquidacao-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarRestoPagar() {
        super.editar();
        if (selecionado.getCategoriaOrcamentaria().isNormal()) {
            FacesUtil.addOperacaoNaoPermitida("A Liquidação selecionado é de Categoria " + selecionado.getCategoriaOrcamentaria().getDescricao() + ", portanto não será possível editar na tela de Restos a Pagar.");
            redireciona();
        }
        buscarIsUnidadeBloqueadaParaNovoDocBensEstoque();
        recuperarConfiguracaoContabil();
        atualizarIsContaDespesaEventoReinfComEmpenho();
        atualizarRenderizarEventoReinfVerEditar();
        atualizarHasPagamentoNaoEstornado();
        facade.recuperarTransferenciasLiquidacaoDoctoFiscal(selecionado);
    }

    @URLAction(mappingId = "verliquidacao-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verRestoPagar() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        recuperarConfiguracaoContabil();
        atualizarIsContaDespesaEventoReinfComEmpenho();
        atualizarRenderizarEventoReinfVerEditar();
        if (selecionado.getCategoriaOrcamentaria().isNormal()) {
            FacesUtil.addOperacaoNaoPermitida("A Liquidação selecionado é de Categoria " + selecionado.getCategoriaOrcamentaria().getDescricao() + ", portanto não será possível visualizar na tela de Restos a Pagar.");
            redireciona();
        }
        facade.recuperarTransferenciasLiquidacaoDoctoFiscal(selecionado);
    }

    private void parametrosIniciais() {
        documentoFiscal = null;
        saldoLiquidar = BigDecimal.ZERO;
        selecionado.setDataLiquidacao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataRegistro(SistemaFacade.getDataCorrente());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        if (facade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", facade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
        recuperarConfiguracaoContabil();
        atualizarHasPagamentoNaoEstornado();
    }

    public Boolean getCategoriaOrcamentoriaLiquidacao() {
        return selecionado.isLiquidacaoNormal();
    }

    public Boolean renderizarAtributosDiaria() {
        return selecionado.isEmpenhoIntegracaoDiaria()
            && TipoProposta.CONCESSAO_DIARIA.equals(selecionado.getEmpenho().getPropostaConcessaoDiaria().getTipoProposta());
    }

    public Boolean permitiNovoDocumentoFiscalEstoque() {
        if (facade.getEmpenhoFacade().getExecucaoContratoFacade().isEmpenhoReajustePosExecucao(selecionado.getEmpenho())) {
            return true;
        }
        return isEstoque() && !isUnidadeBloqueadaParaNovoDocBensEstoque;
    }

    public Boolean hasEmpenhoSelecionado() {
        return selecionado.getEmpenho() != null;
    }

    public boolean hasTransferenciasGruposBens() {
        return selecionado.getDoctoFiscais().stream().anyMatch(docto -> !docto.getTransferenciasBens().isEmpty());
    }

    public List<SelectItem> getListaUfs() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (UF obj : facade.getUfFacade().lista()) {
            toReturn.add(new SelectItem(obj, obj.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoServicoReinf() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoServicoReinf obj : TipoServicoReinf.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    private void validarDocumentoFiscal() {
        ValidacaoException ve = new ValidacaoException();
        if (isDocumentoFiscalIntegracaoContratoOrObrigacaoPagar()) {
            if (documentoFiscal.getDoctoFiscalLiquidacao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Documento Fiscal deve ser informado.");
            }
            if (documentoFiscal.getValorLiquidado().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O Valor a Liquidar deve ser maior que zero(0).");
            }
            ve.lancarException();
            for (LiquidacaoDoctoFiscal doc : selecionado.getDoctoFiscais()) {
                if (!doc.equals(documentoFiscal) && documentoFiscal.getDoctoFiscalLiquidacao().equals(doc.getDoctoFiscalLiquidacao())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(" O documento " + documentoFiscal.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal() + " já foi adicionado na lista.");
                }
            }
            if (saldoLiquidar.compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não há saldo disponível para liquidar o documento: " + documentoFiscal.getDoctoFiscalLiquidacao());
            }
            ve.lancarException();
            if (documentoFiscal.getValorLiquidado().compareTo(saldoLiquidar) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor a Liquidar deve ser menor ou igual ao saldo disponível de " + Util.formataValor(saldoLiquidar));
            }
            ve.lancarException();
        } else {
            if (documentoFiscal.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal() == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Tipo Documento deve ser informado.");
            }
            if (documentoFiscal.getDoctoFiscalLiquidacao().getDataDocto() == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Data do Documento deve ser informado.");
            }
            if (documentoFiscal.getDoctoFiscalLiquidacao().getDataAtesto() == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Data do Atesto deve ser informado.");
            }
            if (documentoFiscal.getDoctoFiscalLiquidacao().getNumero() == null || documentoFiscal.getDoctoFiscalLiquidacao().getNumero().trim().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Número do Documento deve ser informado.");
            }
            if (documentoFiscal.getDoctoFiscalLiquidacao().getValor() == null || documentoFiscal.getDoctoFiscalLiquidacao().getValor().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero (0). ");
            }
            ve.lancarException();
            selecionado.validarMesmoDocumentoEmLista(documentoFiscal.getDoctoFiscalLiquidacao());
            ve.lancarException();
        }
        validarChaveDeAcesso(ve);
        validarReinf(ve);
    }

    private void validarChaveDeAcesso(ValidacaoException ve) {
        if (isTipoDocumentoFiscalObrigaChaveAcesso()) {
            if (documentoFiscal.getDoctoFiscalLiquidacao().getChaveDeAcesso() == null || documentoFiscal.getDoctoFiscalLiquidacao().getChaveDeAcesso().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Quando o Tipo de Documento for " + documentoFiscal.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal().toString() +
                    ", o campo Chave de Acesso da NF-e/Cód. Verif. NFS-e deve ser informado.");
            }
        }
        if (documentoFiscal.getDoctoFiscalLiquidacao().getSerie() == null || documentoFiscal.getDoctoFiscalLiquidacao().getSerie().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Série deve ser informado.");
        }
        if (documentoFiscal.getDoctoFiscalLiquidacao().getUf() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo UF deve ser informado.");
        }
        ve.lancarException();

        for (LiquidacaoDoctoFiscal doctoLiquidacao : selecionado.getDoctoFiscais()) {
            if (!doctoLiquidacao.equals(documentoFiscal) &&
                doctoLiquidacao.getDoctoFiscalLiquidacao().getChaveDeAcesso() != null && !doctoLiquidacao.getDoctoFiscalLiquidacao().getChaveDeAcesso().isEmpty() &&
                documentoFiscal.getDoctoFiscalLiquidacao().getChaveDeAcesso() != null && !documentoFiscal.getDoctoFiscalLiquidacao().getChaveDeAcesso().isEmpty() &&
                doctoLiquidacao.getDoctoFiscalLiquidacao().getChaveDeAcesso().equals(documentoFiscal.getDoctoFiscalLiquidacao().getChaveDeAcesso())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não deve ter mais de um documento comprobatório com a mesma Chave de Acesso.");
            }
        }

        validarDocumentoFiscalComChaveJaCadastradaParaMesmoEmpenho(documentoFiscal.getDoctoFiscalLiquidacao(), ve);
        ve.lancarException();
    }

    private void validarDocumentoFiscalComChaveJaCadastradaParaMesmoEmpenho(DoctoFiscalLiquidacao doctoFiscalLiquidacao, ValidacaoException ve) {
        if (doctoFiscalLiquidacao.getChaveDeAcesso() != null && !doctoFiscalLiquidacao.getChaveDeAcesso().isEmpty()) {
            List<Liquidacao> liquidacoesComChaveDeAcessoJaCadastrada = facade.buscarLiquidacaoPorChaveDeAcessoEEmpenhoNaoEstornado(selecionado, doctoFiscalLiquidacao.getChaveDeAcesso());
            if (!liquidacoesComChaveDeAcessoJaCadastrada.isEmpty()) {
                liquidacoesComChaveDeAcessoJaCadastrada.forEach(liq ->
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Liquidação <b>" + liq.toString() + "</b> já possuí um documento comprobatório com essa Chave de Acesso cadastrada.")
                );
            }
        }
    }

    private void validarReinf() {
        ValidacaoException ve = new ValidacaoException();
        validarReinf(ve);
        ve.lancarException();
    }

    private void validarReinf(ValidacaoException ve) {
        if (isContaDespesaEventoReinf) {
            BigDecimal porcentagem = configuracaoContabil.getPorcentagemMinimaCalculoBase();
            BigDecimal valorMinimoDocumentoFiscal = documentoFiscal.getDoctoFiscalLiquidacao().getValor().multiply(porcentagem).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

            if (documentoFiscal.getRetencaoPrevidenciaria()) {
                if (documentoFiscal.getContaExtra() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Conta Extraorçamentária INSS padrão na configuração da REINF.");
                }
                if (documentoFiscal.getPorcentagemRetencaoMaxima() == null || documentoFiscal.getPorcentagemRetencaoMaxima().compareTo(BigDecimal.ZERO) == 0) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo % INSS Retido deve ser informado e maior que 0.");
                }
                ve.lancarException();
                BigDecimal porcentagemRetencaoMaximaContaExtraInss = facade.getRetencaoMaximaContaExtra(documentoFiscal.getContaExtra());
                if (Util.isNotNull(porcentagemRetencaoMaximaContaExtraInss) && porcentagemRetencaoMaximaContaExtraInss.compareTo(BigDecimal.ZERO) != 0) {
                    if (documentoFiscal.getPorcentagemRetencaoMaxima().compareTo(porcentagemRetencaoMaximaContaExtraInss) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A porcentagem máxima de retenção permitida para Conta Extraorçamentária " + documentoFiscal.getContaExtra().toString() + " é de " + porcentagemRetencaoMaximaContaExtraInss + "%.");
                    }
                }

                if (documentoFiscal.getValorBaseCalculo().compareTo(documentoFiscal.getDoctoFiscalLiquidacao().getValor()) > 0 || documentoFiscal.getValorBaseCalculo().compareTo(valorMinimoDocumentoFiscal) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor base de cálculo <b>" + Util.formataValor(documentoFiscal.getValorBaseCalculo()) +
                        "</b> deve ser menor ou igual o valor do documento <b>" + Util.formataValor(documentoFiscal.getDoctoFiscalLiquidacao().getValor()) +
                        "</b> e maior que " + porcentagem + "% do valor do documento <b>" + Util.formataValor(valorMinimoDocumentoFiscal) + "</b>.");
                }

                if (documentoFiscal.getTipoServicoReinf() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Tipo de Serviço Reinf deve ser informado.");
                }
                if (configuracaoContabil.getPessoaInssPadraoDocLiq() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Configuração REINF para Pessoa Padrão INSS.");
                }
                if (configuracaoContabil.getClasseCredInssPadraoDocLiq() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Configuração REINF para Classe Padrão INSS.");
                }
            }

            if (!documentoFiscal.getOptanteSimplesNacional()) {
                if (documentoFiscal.getContaExtraIrrf() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Conta Extraorçamentária IRRF padrão na configuração da REINF.");
                }
                if (documentoFiscal.getPorcentagemRetencaoMaximaIrrf() == null || documentoFiscal.getPorcentagemRetencaoMaximaIrrf().compareTo(BigDecimal.ZERO) == 0) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo % IR Retido deve ser informado e maior que 0.");
                }
                ve.lancarException();
                BigDecimal porcentagemRetencaoMaximaContaExtraIrrf = facade.getRetencaoMaximaContaExtra(documentoFiscal.getContaExtraIrrf());
                if (Util.isNotNull(porcentagemRetencaoMaximaContaExtraIrrf) && porcentagemRetencaoMaximaContaExtraIrrf.compareTo(BigDecimal.ZERO) != 0) {
                    if (documentoFiscal.getPorcentagemRetencaoMaximaIrrf().compareTo(porcentagemRetencaoMaximaContaExtraIrrf) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A porcentagem máxima de retenção permitida para Conta Extraorçamentária " + documentoFiscal.getContaExtraIrrf().toString() + " é de " + porcentagemRetencaoMaximaContaExtraIrrf + "%.");
                    }
                }

                if (documentoFiscal.getValorBaseCalculoIrrf().compareTo(documentoFiscal.getDoctoFiscalLiquidacao().getValor()) > 0 || documentoFiscal.getValorBaseCalculoIrrf().compareTo(valorMinimoDocumentoFiscal) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor base de cálculo <b>" + Util.formataValor(documentoFiscal.getValorBaseCalculoIrrf()) +
                        "</b> deve ser menor ou igual o valor do documento <b>" + Util.formataValor(documentoFiscal.getDoctoFiscalLiquidacao().getValor()) +
                        "</b> e maior que " + porcentagem + "% do valor do documento <b>" + Util.formataValor(valorMinimoDocumentoFiscal) + "</b>.");
                }

                if (documentoFiscal.getNaturezaRendimentoReinf() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Natureza de Rendimentos Reinf deve ser informado.");
                }
                if (configuracaoContabil.getPessoaIrrfPadraoDocLiq() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Configuração REINF para Pessoa Padrão IRRF.");
                }
                if (configuracaoContabil.getClasseCredIrrfPadraoDocLiq() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Configuração REINF para Classe Padrão IRRF.");
                }
            }

            UnidadeOrganizacional uo = selecionado.getUnidadeOrganizacional() != null ? selecionado.getUnidadeOrganizacional() : facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente();
            Date dataDocto = documentoFiscal.getDoctoFiscalLiquidacao().getDataDocto();
            TipoDocumentoFiscal tipoDocumentoFiscal = documentoFiscal.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal();
            if (tipoDocumentoFiscal.getValidarCopetencia() && facade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/orcamentario/liquidacao/edita.xhtml", dataDocto, uo, facade.getExercicioFacade().getExercicioPorAno(DataUtil.getAno(dataDocto)))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data do documento comprobatório <b>" + DataUtil.getDataFormatada(dataDocto) + "</b> está fora do período fase para a unidade <b>" +
                    facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(dataDocto, uo, TipoHierarquiaOrganizacional.ORCAMENTARIA).toString() + "</b>");
            }

            for (LiquidacaoDoctoFiscal doctoLiquidacao : selecionado.getDoctoFiscais()) {
                if (!doctoLiquidacao.equals(documentoFiscal)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não deve ter mais de um documento comprobatório quando Tipo de Despesa do Empenho for de <b>" + selecionado.getEmpenho().getTipoContaDespesa().getDescricao() + "</b>.");
                }
            }
            ve.lancarException();
        }
    }

    public void novoDocumentoFiscal() {
        instanciarDocumentoFiscal();
        if (!isDocumentoFiscalIntegracaoContratoOrObrigacaoPagar()) {
            documentoFiscal.setDoctoFiscalLiquidacao(new DoctoFiscalLiquidacao());
            if (isContaDespesaEventoReinf) {
                documentoFiscal.limparCamposDocumentoFiscalEventosReinf4000(buscarContaIrrf(), hasPagamentoNaoEstornado);
                if (!selecionado.getDesdobramentos().isEmpty()) {
                    documentoFiscal.setNaturezaRendimentoReinf(facade.getNaturezaRendimentoReinfFacade().buscarNaturezaPorConta(selecionado.getDesdobramentos().get(0).getConta()));
                }
            }
        }
        if (selecionado.isEmpenhoIntegracaoDiaria()) {
            novoDocumentoFiscalPorDiaria(selecionado.getEmpenho());
        }
    }

    public void novoDocumentoFiscalEstoque() {
        instanciarDocumentoFiscal();
        documentoFiscal.setDoctoFiscalLiquidacao(new DoctoFiscalLiquidacao());
        renderizarCamposNovoDocumentoEstoque = true;
    }

    private void novoDocumentoFiscalPorDiaria(Empenho empenho) {
        if (empenho.getPropostaConcessaoDiaria().getAtoLegal() != null && empenho.getPropostaConcessaoDiaria().getAtoLegal().getDataPublicacao() != null) {
            documentoFiscal.getDoctoFiscalLiquidacao().setDataDocto(empenho.getPropostaConcessaoDiaria().getAtoLegal().getDataPublicacao());
            documentoFiscal.getDoctoFiscalLiquidacao().setAtoLegal(empenho.getPropostaConcessaoDiaria().getAtoLegal());
        } else {
            documentoFiscal.getDoctoFiscalLiquidacao().setDataDocto(empenho.getDataEmpenho());
        }
        if (empenho.getPropostaConcessaoDiaria().getAtoLegal().getNumero() != null) {
            documentoFiscal.getDoctoFiscalLiquidacao().setNumero(empenho.getPropostaConcessaoDiaria().getAtoLegal().getNumero());
        } else {
            documentoFiscal.getDoctoFiscalLiquidacao().setNumero("Número não informado");
        }
        documentoFiscal.getDoctoFiscalLiquidacao().setValor(empenho.getSaldo());
    }

    public void cancelarDocumentoFiscal() {
        documentoFiscal = null;
        renderizarCamposNovoDocumentoEstoque = false;
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:btnNovoDocto')");
    }

    private void buscarGrupoIntegracaoDocumentoFiscal() {
        try {
            if (selecionado.isEmpenhoIntegracaoContratoOrReconhecimentoDividaOrAta() && (isEstoque() || isBemMovel())) {
                selecionado.setDoctoFiscais(Lists.newArrayList());
                documentosIntegracao = Lists.newArrayList();
                assistenteDocumentoFiscal = new DocumentoFiscalIntegracaoAssistente();
                Desdobramento desdobramento = selecionado.getDesdobramentos().get(0);
                desdobramento.setValor(BigDecimal.ZERO);
                desdobramento.setSaldo(BigDecimal.ZERO);
                assistenteDocumentoFiscal.setDesdobramento(desdobramento);
                assistenteDocumentoFiscal.setEmpenho(selecionado.getEmpenho());
                assistenteDocumentoFiscal.setData(selecionado.getDataLiquidacao());
                assistenteDocumentoFiscal.setExercicio(selecionado.getExercicio());
                assistenteDocumentoFiscal.setUnidadeOrganizacional(selecionado.getUnidadeOrganizacional());

                List<DoctoFiscalLiquidacao> documentos = buscarDocumentosFiscais(selecionado.getEmpenho(), "");
                documentos.forEach(doc -> {
                    assistenteDocumentoFiscal.setDoctoFiscalLiquidacao(doc);
                    DocumentoFiscalIntegracao docInt = facade.getIntegradorDocumentoFiscalLiquidacaoFacade().criarDocumentoFiscalIntegracao(assistenteDocumentoFiscal);
                    ajustaValorALiquidarItemDocIntQuandoExisteDiferencaComTotalDocumento(docInt);
                    docInt.verificarInconsistencias();
                    documentosIntegracao.add(docInt);

                    docInt.getDesdobramento().setValor(getValorTotalALiquidarGrupoIntegrador());
                    docInt.getDesdobramento().setSaldo(getValorTotalALiquidarGrupoIntegrador());
                    criarLiquidacaoDoctoFiscal(docInt);
                });
                selecionado.setValor(getValorTotalALiquidarGrupoIntegrador());
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void selecionarDocumentoIntegracao(DocumentoFiscalIntegracao docInt){
        documentoIntegracao = docInt;
    }

    private void criarLiquidacaoDoctoFiscal(DocumentoFiscalIntegracao docInt) {
        if (docInt.getSelecionado()) {
            LiquidacaoDoctoFiscal documentoFiscal = new LiquidacaoDoctoFiscal();
            documentoFiscal.setLiquidacao(selecionado);
            documentoFiscal.setDoctoFiscalLiquidacao(docInt.getDoctoFiscalLiquidacao());
            documentoFiscal.setValorLiquidado(docInt.getValorALiquidar());
            documentoFiscal.setSaldo(docInt.getValorALiquidar());
            documentoFiscal.getTransferenciasBens().clear();
            docInt.getTransferenciasBens().forEach(transferencia -> {
                transferencia.setLiquidacaoDoctoFiscal(documentoFiscal);
                documentoFiscal.getTransferenciasBens().add(transferencia);
            });
            selecionado.getDoctoFiscais().add(documentoFiscal);
        }
    }

    private void ajustaValorALiquidarItemDocIntQuandoExisteDiferencaComTotalDocumento(DocumentoFiscalIntegracao docInt) {
        BigDecimal diferenca = docInt.getValorALiquidar().subtract(docInt.getValorTotalALiquidarGrupoIntegrador());
        if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
            List<DocumentoFiscalIntegracaoGrupoItem> itensDoc = docInt.getGrupos().stream().flatMap(grupo -> grupo.getItens().stream()).collect(Collectors.toList());
            DocumentoFiscalIntegracaoGrupoItem ultimoItemDoc = itensDoc.get(itensDoc.size() - 1);

            boolean isDiferencaPositiva = diferenca.compareTo(BigDecimal.ZERO) > 0;
            BigDecimal valorALiquidarAtual = ultimoItemDoc.getValorALiquidar();

            BigDecimal valorALiquidarAjustado = isDiferencaPositiva
                ? valorALiquidarAtual.add(diferenca)
                : valorALiquidarAtual.subtract(diferenca.abs());

            ultimoItemDoc.setValorALiquidar(valorALiquidarAjustado);

            if (valorALiquidarAtual.compareTo(ultimoItemDoc.getValorLancamento()) > 0) {
                ultimoItemDoc.setValorALiquidar(ultimoItemDoc.getValorLancamento().subtract(ultimoItemDoc.getValorLiquidado()));
            }

            selecionado.getDoctoFiscais().forEach(documentoFiscal -> {
                if (documentoFiscal.getDoctoFiscalLiquidacao().equals(docInt.getDoctoFiscalLiquidacao())) {
                    documentoFiscal.getTransferenciasBens().forEach(transferencia -> {
                        if (transferencia.getIdGrupoDestino().equals(ultimoItemDoc.getIdGrupo())) {
                            transferencia.setValor(transferencia.getValor().subtract(valorALiquidarAtual));
                            transferencia.setValor(transferencia.getValor().add(ultimoItemDoc.getValorALiquidar()));
                        }
                    });
                }
            });
        }
    }

    public void filtrarSomenteDocumentoFiscalIntegracao() {
        if (assistenteDocumentoFiscal.getIntegrado()) {
            documentosIntegracao.removeIf(doc -> !doc.getIntegrador());
        } else {
            buscarGrupoIntegracaoDocumentoFiscal();
        }
    }

    public void filtrarSomenteDocumentoFiscalComSaldo() {
        if (assistenteDocumentoFiscal.getComSaldo()) {
            documentosIntegracao.removeIf(doc -> !doc.hasSaldo());
        } else {
            buscarGrupoIntegracaoDocumentoFiscal();
        }
    }

    public Boolean mostrarBotaoSelecionarTodos() {
        try {
            Boolean todosSelecionados = true;
            if (documentosIntegracao != null) {
                for (DocumentoFiscalIntegracao documentoFiscalIntegracao : documentosIntegracao) {
                    if (documentoFiscalIntegracao.getSelecionado() && documentoFiscalIntegracao.getIntegrador()) {
                        todosSelecionados = false;
                    }
                }
            }
            return todosSelecionados;
        } catch (NullPointerException ex) {
            return Boolean.FALSE;
        }
    }

    public void desmarcarTodos() {
        if (documentosIntegracao != null) {
            for (DocumentoFiscalIntegracao documentoFiscalIntegracao : documentosIntegracao) {
                if (documentoFiscalIntegracao.getIntegrador()) {
                    documentoFiscalIntegracao.setSelecionado(false);
                    distribuirValorEAdicionarOuRemoverDocumentoFiscal(documentoFiscalIntegracao);
                }
            }
        }
    }

    public void selecionarTodos() {
        if (documentosIntegracao != null) {
            for (DocumentoFiscalIntegracao documentoFiscalIntegracao : documentosIntegracao) {
                if (documentoFiscalIntegracao.getIntegrador()) {
                    documentoFiscalIntegracao.setSelecionado(true);
                    distribuirValorEAdicionarOuRemoverDocumentoFiscal(documentoFiscalIntegracao);
                }
            }
        }
    }

    public void distribuirValorEAdicionarOuRemoverDocumentoFiscal(DocumentoFiscalIntegracao docInt) {
        if (docInt.getSelecionado()) {
            distribuirValorLiquidadoGrupoIntegracao(docInt);
            criarLiquidacaoDoctoFiscal(docInt);
        } else {
            LiquidacaoDoctoFiscal documentoParaRemover = null;
            for (LiquidacaoDoctoFiscal liquidacaoDoctoFiscal : selecionado.getDoctoFiscais()) {
                if (liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().equals(docInt.getDoctoFiscalLiquidacao())) {
                    documentoParaRemover = liquidacaoDoctoFiscal;
                    break;
                }
            }
            selecionado.getDoctoFiscais().remove(documentoParaRemover);
            distribuirValorLiquidadoGrupoIntegracao(docInt);
        }
    }

    public void distribuirValorLiquidadoGrupoIntegracao(DocumentoFiscalIntegracao docInt) {
        docInt.setValorALiquidar(docInt.getSelecionado()
            ? (docInt.getValorALiquidar().compareTo(BigDecimal.ZERO) > 0 ? docInt.getValorALiquidar() : docInt.getSaldo())
            : BigDecimal.ZERO);

        atualizarValorDosItensETransferencias(docInt);
        ajustaValorALiquidarItemDocIntQuandoExisteDiferencaComTotalDocumento(docInt);

        docInt.verificarInconsistencias();
        if (docInt.hasInconsistencia()) {
            docInt.setValorALiquidar(BigDecimal.ZERO);
            selecionado.setValor(getValorTotalALiquidarGrupoIntegrador());
        }

        docInt.getDesdobramento().setValor(getValorTotalALiquidarGrupoIntegrador());
        docInt.getDesdobramento().setSaldo(getValorTotalALiquidarGrupoIntegrador());

        selecionado.getDoctoFiscais().forEach(doc -> {
            if (doc.getDoctoFiscalLiquidacao().getId().equals(docInt.getDoctoFiscalLiquidacao().getId())) {
                doc.setValorLiquidado(docInt.getValorALiquidar());
                doc.setSaldo(docInt.getValorALiquidar());
            }
        });
        selecionado.setValor(getValorTotalALiquidarGrupoIntegrador());
    }

    private void atualizarValorDosItensETransferencias(DocumentoFiscalIntegracao docInt) {
        HashMap<Long, BigDecimal> valorPorGrupo = new HashMap<>();
        for (DocumentoFiscalIntegracaoGrupo grupo : docInt.getGrupos()) {
            if (grupo.getIntegrador()) {
                grupo.getItens().forEach(item -> {
                    item.setValorALiquidar(DocumentoFiscalIntegracaoGrupoItem.getValorProporcionalAoItem(grupo.getValorGrupo(), docInt.getValorALiquidar(), item.getValorLancamento()));
                    if (!valorPorGrupo.containsKey(item.getIdGrupo())) {
                        valorPorGrupo.put(item.getIdGrupo(), item.getValorALiquidar());
                    } else {
                        BigDecimal valorAtual = valorPorGrupo.get(item.getIdGrupo()).add(item.getValorALiquidar());
                        valorPorGrupo.put(item.getIdGrupo(), item.getValorALiquidar().add(valorAtual));
                    }
                });
            }
        }

        if (!valorPorGrupo.isEmpty()) {
            selecionado.getDoctoFiscais().forEach(documentoFiscal -> {
                if (documentoFiscal.getDoctoFiscalLiquidacao().equals(docInt.getDoctoFiscalLiquidacao())) {
                    documentoFiscal.getTransferenciasBens().forEach(transferencia -> {
                        transferencia.setValor(valorPorGrupo.get(transferencia.getIdGrupoDestino()));
                    });
                }
            });
        }
    }

    public BigDecimal getValorTotalALiquidarGrupoIntegrador() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasDocumentosFiscaisIntegracao()) {
            for (DocumentoFiscalIntegracao doc : documentosIntegracao) {
                if (doc.getIntegrador()) {
                    valor = valor.add(doc.getValorALiquidar());
                }
            }
        }
        return valor;
    }

    public void expandirGrupoDocumento(DocumentoFiscalIntegracao doc, boolean expandir) {
        doc.setExpandirGrupos(expandir);
    }

    public boolean hasDocumentosFiscaisIntegracao() {
        return !Util.isListNullOrEmpty(documentosIntegracao);
    }

    public Boolean getBloquearNovoDesdobramento() {
        return isOperacaoEditar() || (configuracaoContabil.getBloquearUmDesdobramento() && !selecionado.getDesdobramentos().isEmpty());
    }

    public void adicionarDocumentoFiscal() {
        try {
            validarDocumentoFiscal();
            documentoFiscal.setLiquidacao(selecionado);
            if (!isDocumentoFiscalIntegracaoContratoOrObrigacaoPagar()) {
                documentoFiscal.getDoctoFiscalLiquidacao().setTotal(documentoFiscal.getDoctoFiscalLiquidacao().getValor());
                documentoFiscal.getDoctoFiscalLiquidacao().setSaldo(documentoFiscal.getDoctoFiscalLiquidacao().getValor());
                documentoFiscal.setValorLiquidado(documentoFiscal.getDoctoFiscalLiquidacao().getValor());
                documentoFiscal.setSaldo(documentoFiscal.getDoctoFiscalLiquidacao().getValor());
            }
            if (isDocumentoFiscalIntegracaoContratoOrObrigacaoPagar()) {
                documentoFiscal.setSaldo(documentoFiscal.getValorLiquidado());
                documentoFiscal.getDoctoFiscalLiquidacao().setSaldo(documentoFiscal.getDoctoFiscalLiquidacao().getSaldo().add(documentoFiscal.getValorLiquidado()));
            }
            Util.adicionarObjetoEmLista(selecionado.getDoctoFiscais(), documentoFiscal);
            atualizarValoresDesdobramentoComDocumentoFiscal();
            selecionado.setValor(selecionado.getTotalDocumentos());
            renderizarCamposReinf4020 = !documentoFiscal.getOptanteSimplesNacional();
            renderizarCamposReinf2010 = documentoFiscal.getRetencaoPrevidenciaria();
            cancelarDocumentoFiscal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void adicionarDocumentoFiscalReinf() {
        try {
            validarReinf();
            Util.adicionarObjetoEmLista(selecionado.getDoctoFiscais(), documentoFiscal);
            renderizarCamposReinf4020 = !documentoFiscal.getOptanteSimplesNacional();
            renderizarCamposReinf2010 = documentoFiscal.getRetencaoPrevidenciaria();
            cancelarDocumentoFiscal();
            FacesUtil.executaJavaScript("dialogReinf.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void atualizarValoresDesdobramentoComDocumentoFiscal() {
        if (selecionado.getDesdobramentos() != null && !selecionado.getDesdobramentos().isEmpty()) {
            BigDecimal valorTotalDoc = BigDecimal.ZERO;
            BigDecimal valorTotalSaldoDoc = BigDecimal.ZERO;
            if (selecionado.getDoctoFiscais() != null && !selecionado.getDoctoFiscais().isEmpty()) {
                for (LiquidacaoDoctoFiscal doc : selecionado.getDoctoFiscais()) {
                    valorTotalDoc = valorTotalDoc.add(doc.getValorLiquidado());
                    valorTotalSaldoDoc = valorTotalSaldoDoc.add(doc.getSaldo());
                }
            }
            selecionado.getDesdobramentos().get(0).setValor(valorTotalDoc);
            selecionado.getDesdobramentos().get(0).setSaldo(valorTotalSaldoDoc);
        }
    }

    public boolean isDocumentoFiscalIntegracaoContratoOrObrigacaoPagar() {
        if (documentoFiscal != null) {
            if (renderizarCamposNovoDocumentoEstoque || (documentoFiscal.getDoctoFiscalLiquidacao() != null && documentoFiscal.getDoctoFiscalLiquidacao().getId() == null)) {
                return false;
            }
            return selecionado.isLiquidacaoPossuiObrigacoesPagar();
        }
        return false;
    }

    public boolean canHabilitarNovoDocumento() {
        boolean canHabilitar = true;
        if (selecionado.getEmpenho() != null) {
            Empenho empenho = facade.getEmpenhoFacade().recuperar(selecionado.getEmpenho().getId());
            if (empenho.getTipoContaDespesa().isServicoTerceiro() && !empenho.getDesdobramentos().isEmpty()) {
                for (DesdobramentoEmpenho desdobramentoEmpenho : empenho.getDesdobramentos()) {
                    if (!facade.hasConfiguracaoExcecaoVigente(selecionado.getDataLiquidacao(), desdobramentoEmpenho.getConta())) {
                        if (facade.getSolicitacaoEmpenhoFacade().recuperarSolicitacaoEmpenhoPorEmpenho(selecionado.isLiquidacaoRestoPagar() ? empenho.getEmpenho() : empenho) == null) {
                            canHabilitar = false;
                            break;
                        }
                    }
                }
            }
        }
        return canHabilitar;
    }

    private void instanciarDocumentoFiscal() {
        documentoFiscal = new LiquidacaoDoctoFiscal();
    }

    public void adicionarDetalhamento() {
        try {
            if (selecionado.isLiquidacaoPossuiObrigacoesPagar()) {
                adicionarContaDetalhamentoObrigacaoPagarAoDesdobramentoLiquidacao();
            } else if (selecionado.hasEmpenhoComDesdobramento()) {
                adicionarContaDetalhamentoEmpenhoAoDesdobramentoLiquidacao();
            }
            validarAdicionarDetalhamento();
            desdobramento.setLiquidacao(selecionado);
            desdobramento.setSaldo(desdobramento.getValor());
            definirEvento(desdobramento);
            Util.adicionarObjetoEmLista(selecionado.getDesdobramentos(), desdobramento);
            cancelarDetalhamento();
            FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:btnAddDetalhamento')");
            FacesUtil.atualizarComponente("Formulario:viewGeral:painelDoctos");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void definirEvento(Desdobramento desdobramento) {
        try {
            EventoContabil eventoContabil = facade.buscarEventoContabil(selecionado, (ContaDespesa) desdobramento.getConta());
            if (eventoContabil != null) {
                desdobramento.setEventoContabil(eventoContabil);
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public boolean isDetalhamentoWithObrigacaoPagar() {
        if (selecionado.getDesdobramentos() == null || selecionado.getDesdobramentos().isEmpty()) {
            return true;
        }
        for (Desdobramento desd : selecionado.getDesdobramentos()) {
            if (desd.getDesdobramentoObrigacaoPagar() != null) {
                return true;
            }
        }
        return false;
    }

    public void novoDetalhamento() {
        desdobramento = new Desdobramento();
        if (selecionado.isLiquidacaoPossuiObrigacoesPagar()) {
            novoDetalhamentoObrigacaoPagar();
        }
        desdobramento.setValor(selecionado.getTotalDocumentos());
    }

    public void calcularSaldoDetalhamentoObrigacaoPagar() {
        if (selecionado.isLiquidacaoPossuiObrigacoesPagar()) {
            if (desdobramentoObrigacaoPagar.getObrigacaoAPagar().getEmpenho() != null) {
                desdobramento.setValor(desdobramentoObrigacaoPagar.getSaldo());
            } else {
                Empenho empenho = facade.getEmpenhoFacade().recuperarDetalhamento(selecionado.getEmpenho().getId());
                for (DesdobramentoEmpenho desdEmp : empenho.getDesdobramentos()) {
                    if (desdEmp.getConta().equals(desdobramentoObrigacaoPagar.getConta())
                        && desdobramentoObrigacaoPagar.getObrigacaoAPagar().equals(desdEmp.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar())) {
                        desdobramento.setValor(desdEmp.getSaldo());
                    }
                }
            }
        }
    }

    public void calcularSaldoDetalhamentoEmpenho() {
        desdobramento.setValor(desdobramentoEmpenho.getSaldo());
    }

    public void cancelarDetalhamento() {
        desdobramento = null;
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:btnNovoDetalhamento')");
    }

    private void adicionarContaDetalhamentoObrigacaoPagarAoDesdobramentoLiquidacao() {
        ValidacaoException ve = new ValidacaoException();
        if (desdobramentoObrigacaoPagar == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Despesa deve ser informado.");
        }
        ve.lancarException();
        if (desdobramentoObrigacaoPagar.getConta() != null) {
            desdobramento.setDesdobramentoObrigacaoPagar(desdobramentoObrigacaoPagar);
            if (desdobramentoObrigacaoPagar.getDesdobramentoEmpenho() != null) {
                desdobramento.setDesdobramentoEmpenho(desdobramentoObrigacaoPagar.getDesdobramentoEmpenho());
            }
            desdobramento.setConta(desdobramentoObrigacaoPagar.getConta());
        }
    }

    private void adicionarContaDetalhamentoEmpenhoAoDesdobramentoLiquidacao() {
        ValidacaoException ve = new ValidacaoException();
        if (desdobramentoEmpenho == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Despesa deve ser informado.");
        }
        ve.lancarException();
        if (desdobramentoEmpenho.getConta() != null) {
            desdobramento.setDesdobramentoEmpenho(desdobramentoEmpenho);
            if (desdobramentoEmpenho.getDesdobramentoObrigacaoPagar() != null) {
                desdobramento.setDesdobramentoObrigacaoPagar(desdobramentoEmpenho.getDesdobramentoObrigacaoPagar());
            }
            desdobramento.setConta(desdobramentoEmpenho.getConta());
        }
    }

    public void removerDocumentoFiscal(LiquidacaoDoctoFiscal ldf) {
        documentoFiscal = ldf;
        selecionado.getDoctoFiscais().remove(ldf);
        selecionado.setValor(selecionado.getTotalDocumentos());
        atualizarValoresDesdobramentoComDocumentoFiscal();
        renderizarCamposReinf4020 = Boolean.TRUE;
        renderizarCamposReinf2010 = Boolean.FALSE;
    }

    public void editarDocumentoFiscal(LiquidacaoDoctoFiscal obj) {
        documentoFiscal = (LiquidacaoDoctoFiscal) Util.clonarObjeto(obj);
        atualizarEventosReinfAtuais();
    }

    public List<TipoDocumentoFiscal> completarTipoDoctos(String parte) {
        if (selecionado != null && selecionado.getEmpenho() != null) {
            List<TipoDocumentoFiscal> resultado = facade.buscarTiposDeDocumentosPorContaDeDespesa((ContaDespesa) selecionado.getEmpenho().getContaDespesa(), selecionado.getEmpenho().getTipoContaDespesa(), parte);
            if (!resultado.isEmpty()) {
                return resultado;
            }
        }
        return facade.getTipoDocumentoFiscalFacade().buscarTiposDeDocumentosAtivos(parte.trim());
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            if (isOperacaoNovo()) {
                validarIntegracaoDocumentoFiscal();
                validarLiquidacao();
                selecionado = facade.salvarNovaLiquidacao(selecionado, documentosIntegracao);
                FacesUtil.addOperacaoRealizada(getTituloLiquidacao() + " " + selecionado.toString() + " foi salva com sucesso.");
                redirecionarParaVer(selecionado);
            } else {
                Util.validarCampos(selecionado);
                facade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada(getTituloLiquidacao() + " " + selecionado.toString() + " foi alterada com sucesso.");
                redirecionarParaLista();
            }
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            logger.error("Erro ao salvar liquidação", ex);
            logger.debug("Problema ao salvar liquidação: {}", ex.getMessage());
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Erro ao salvar liquidação", ex);
            logger.debug("Problema ao salvar liquidação: {}", ex.getMessage());
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarLiquidacao() {
        ValidacaoException ve = new ValidacaoException();
        validarEmpenhoUnidadeEmpenho(ve);
        validarRegrasEspecificas(ve);
        validarTipoEmpenho(ve);
        validarValoresAoSalvar(ve);
        validarIntegracaoComServicoDeTerceiros(ve);
        ve.lancarException();
    }

    private void validarEmpenhoUnidadeEmpenho(ValidacaoException validacaoException) {
        facade.getHierarquiaOrganizacionalFacade()
            .validarUnidadesIguais(selecionado.getEmpenho().getUnidadeOrganizacional(), selecionado.getUnidadeOrganizacional()
                , validacaoException
                , "A unidade organizacional da liquidação deve ser a mesma do empenho.");
    }

    private void validarTipoEmpenho(ValidacaoException va) {
        String msg = " O Tipo do Empenho é Ordinário. Por isso o valor da sua liquidação deve ser igual ao seu saldo.";
        selecionado.getEmpenho().validarTipoEmpenho(va, msg, selecionado.getValor(), selecionado.getEmpenho().getSaldo());
        va.lancarException();
    }

    private void validarRegrasEspecificas(ValidacaoException va) {
        if (selecionado.getDoctoFiscais().isEmpty()) {
            va.adicionarMensagemDeCampoObrigatorio(" A liquidação deve possuir ao menos um documento comprobatório.");

        } else if (selecionado.getDesdobramentos().isEmpty()) {
            va.adicionarMensagemDeCampoObrigatorio(" A liquidação deve possuir ao menos um detalhamento.");

        } else if (selecionado.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            va.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero (0).");

        } else if (DataUtil.dataSemHorario(selecionado.getDataLiquidacao()).compareTo(DataUtil.dataSemHorario(selecionado.getEmpenho().getDataEmpenho())) < 0) {
            va.adicionarMensagemDeOperacaoNaoPermitida(" A data da liquidação deve ser maior ou igual a data do empenho selecionado. Data do empenho: <b>" + DataUtil.getDataFormatada(selecionado.getEmpenho().getDataEmpenho()) + "</b>.");
        }

        selecionado.getDoctoFiscais().forEach(doc -> {
            validarDocumentoFiscalComChaveJaCadastradaParaMesmoEmpenho(doc.getDoctoFiscalLiquidacao(), va);
        });
        if (selecionado.getEmpenho().getConvenioDespesa() != null &&
            !facade.getConvenioDespesaFacade().isConvenioDespesaVigente(selecionado.getDataLiquidacao(), selecionado.getEmpenho().getConvenioDespesa())) {
            va.adicionarMensagemDeOperacaoNaoPermitida("O Convênio de Despesa <b>" + selecionado.getEmpenho().getConvenioDespesa().toString() + "</b> deve estar vigênte.");
        }
    }

    public void recuperarDetalhamentos() {
        detalhamentos = buscarContasDesdobramento(filtroDetalhamento.trim(), true);
    }

    public boolean hasEmpenhoComDesdobramento() {
        if (selecionado.getEmpenho() != null) {
            if (!Hibernate.isInitialized(selecionado.getEmpenho().getDesdobramentos())) {
                selecionado.setEmpenho(facade.getEmpenhoFacade().recuperar(selecionado.getEmpenho().getId()));
            }
            return selecionado.hasEmpenhoComDesdobramento();
        }
        return false;
    }

    public List<Conta> completarContaDespesaDesdobradaEmpenho(String parte) {
        return buscarContasDesdobramento(parte, false);
    }

    private List<Conta> buscarContasDesdobramento(String parte, Boolean trazerTodasContas) {
        validarDadosEmpenhoLiquidacao(selecionado);
        try {
            return facade.getContaDespesaDesdobradaFacade().buscarContasFilhasDespesaORCPorTipo(
                parte.trim(),
                selecionado.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(),
                selecionado.getExercicio(),
                selecionado.getEmpenho().getTipoContaDespesa(),
                trazerTodasContas);
        } catch (Exception ex) {
            logger.error("Erro ao recuperar contas por grupo bem: " + ex.getMessage());
        }
        return new ArrayList<>();
    }

    private void ordenarContas(List<Conta> contas) {
        if (!contas.isEmpty()) {
            Collections.sort(contas, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Conta s1 = (Conta) o1;
                    Conta s2 = (Conta) o2;
                    if (s1 != null && s1.getCodigo() != null && s2 != null && s2.getCodigo() != null) {
                        return s1.getCodigo().compareTo(s2.getCodigo());
                    } else {
                        return 0;
                    }
                }
            });
        }
    }

    public List<DesdobramentoObrigacaoPagar> completarContaDespesaPorObrigacaoPagar(String parte) {
        validarDadosEmpenhoLiquidacao(selecionado);
        List<DesdobramentoObrigacaoPagar> toReturn = Lists.newArrayList();
        if (selecionado.getEmpenho() != null && selecionado.isLiquidacaoPossuiObrigacoesPagar()) {
            toReturn.addAll(facade.getDesdobramentoObrigacaoAPagarFacade().buscarDesdobramentoObrigacaoPagar(
                parte.trim(),
                selecionado.getExercicio(),
                getIdsObrigacaoPagar()));
        }
        return toReturn;
    }

    public List<DesdobramentoEmpenho> completarContasDeDespesaPorEmpenho(String parte) {
        validarDadosEmpenhoLiquidacao(selecionado);
        List<DesdobramentoEmpenho> retorno = Lists.newArrayList();
        if (selecionado.getEmpenho() != null && selecionado.hasEmpenhoComDesdobramento()) {
            retorno.addAll(facade.getDesdobramentoEmpenhoFacade().buscarDesdobramentoObrigacaoPagarPorEmpenho(
                parte.trim(),
                selecionado.getEmpenho()));
        }
        return retorno;
    }


    private List<Long> getIdsObrigacaoPagar() {
        List<Long> toReturn = new ArrayList<>();
        for (LiquidacaoObrigacaoPagar liqOp : selecionado.getObrigacoesPagar()) {

            toReturn.add(liqOp.getObrigacaoAPagar().getId());
        }
        return toReturn;
    }

    private void validarDadosEmpenhoLiquidacao(Liquidacao li) {
        try {
            Preconditions.checkNotNull(li.getEmpenho(), "O empenho está nulo para esta liquidação");
            Preconditions.checkNotNull(li.getEmpenho().getDespesaORC(), "Problemas ao recuperar a Despesa Orçamentária do empenho, esta null");
            Preconditions.checkNotNull(li.getEmpenho().getDespesaORC().getProvisaoPPADespesa(), "Problema ao recuperar a Provisão PPA da Despesa");
            Preconditions.checkNotNull(li.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), "A conta de despesa do empenho vinculado a migração esta null");
            Preconditions.checkNotNull(li.getEmpenho().getTipoContaDespesa(), "O tipo da conta de despesa do empenho esta Null");
        } catch (Exception ex) {
            FacesUtil.addError("Erro com os dados do Empenho vinculado a Liquidação", ex.getMessage());
        }
    }

    public void gerarNotaOrcamentaria(boolean isDownload) {
        try {
            List<NotaExecucaoOrcamentaria> notas = facade.buscarNotaLiquidacao(" and nota.id = " + selecionado.getId(), selecionado.getCategoriaOrcamentaria(), getFraseDocumento());
            if (notas != null && !notas.isEmpty()) {
                facade.getNotaOrcamentariaFacade().imprimirDocumentoOficial(notas.get(0), selecionado.getCategoriaOrcamentaria().isResto() ? ModuloTipoDoctoOficial.NOTA_RESTO_LIQUIDACAO : ModuloTipoDoctoOficial.NOTA_LIQUIDACAO, selecionado, isDownload);
            }
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getFraseDocumento() {
        String retorno = "NOTA DE LIQUIDAÇÃO";
        if (selecionado.getCategoriaOrcamentaria().isResto()) {
            if (selecionado.getTransportado() != null && selecionado.getTransportado()) {
                retorno = "NOTA DE RESTOS A PAGAR PROCESSADOS INSCRITOS";
            } else {
                retorno = "NOTA DE LIQUIDAÇÃO DE RESTOS A PAGAR";
            }
        }
        return retorno;
    }

    public List<HistoricoContabil> completarHistoricoContabil(String parte) {
        return facade.getHistoricoContabilFacade().listaFiltrando(parte, "codigo", "descricao");
    }

    public List<Empenho> completarEmpenho(String parte) {
        if (selecionado.isLiquidacaoNormal()) {
            return facade.getEmpenhoFacade().buscarEmpenhosPorUnidadeEUsuarioVinculados(
                parte.trim(),
                selecionado.getUsuarioSistema(),
                selecionado.getExercicio(),
                selecionado.getUnidadeOrganizacional());
        } else {
            return facade.getEmpenhoFacade().buscarEmpenhoPorNumeroPessoaAndCategoriaResto(
                parte.trim(),
                selecionado.getExercicio().getAno(),
                selecionado.getUnidadeOrganizacional());
        }
    }

    public Boolean renderizarRetencao() {
        if (!getPagamentos().isEmpty()) {
            for (Pagamento pagamento : getPagamentos()) {
                Pagamento p = facade.getPagamentoFacade().recuperar(pagamento.getId());
                if (!p.getRetencaoPgtos().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean renderizarRetencaoEstorno() {
        if (!getEstornoPagamentos().isEmpty()) {
            for (PagamentoEstorno pagamentoEstorno : getEstornoPagamentos()) {
                List<ReceitaExtraEstorno> receitas = facade.getPagamentoEstornoFacade().listaReceitaExtraEstornadaPorEstornoPagamento(pagamentoEstorno);
                if (!receitas.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean renderizarRetencaoEstornoIndividual(PagamentoEstorno pagamentoEstorno) {
        return !facade.getPagamentoEstornoFacade().listaReceitaExtraEstornadaPorEstornoPagamento(pagamentoEstorno).isEmpty();
    }

    public Boolean renderizarRetencaoIndividual(Pagamento pagamento) {
        Pagamento p = facade.getPagamentoFacade().recuperar(pagamento.getId());
        return !p.getRetencaoPgtos().isEmpty();
    }

    public void selecionarPagamentoEstorno(PagamentoEstorno pagamentoEstorno) {
        this.pagamentoEstorno = pagamentoEstorno;
        this.pagamento = null;
    }

    public void selecionarPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
        this.pagamentoEstorno = null;
    }

    public List<ReceitaExtra> getReceitaExtras() {
        try {
            if (pagamento != null) {
                return facade.getPagamentoFacade().buscarReceitasExtrasPorPagamento(pagamento);
            }
            return new ArrayList<ReceitaExtra>();
        } catch (Exception e) {
            return new ArrayList<ReceitaExtra>();
        }
    }

    public List<ReceitaExtraEstorno> getEstornoReceitaExtras() {
        try {
            if (pagamentoEstorno != null) {
                return facade.getPagamentoFacade().buscarReceitasExtrasEstornoPorPagamento(pagamentoEstorno.getPagamento());
            } else if (pagamento != null) {
                return facade.getPagamentoFacade().buscarReceitasExtrasEstornoPorPagamento(pagamento);
            }
            return Lists.newArrayList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public List<Pagamento> getPagamentos() {
        if (selecionado.getId() != null) {
            return facade.getPagamentoFacade().listaPorLiquidacao(selecionado);
        }
        return new ArrayList<>();
    }

    public BigDecimal getSomaReceitaExtra() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (ReceitaExtra receitaExtra : getReceitaExtras()) {
            valor = valor.add(receitaExtra.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaEstornoReceitaExtra() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (ReceitaExtraEstorno receitaExtraEstorno : getEstornoReceitaExtras()) {
            valor = valor.add(receitaExtraEstorno.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaPagamentos() {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (Pagamento p : getPagamentos()) {
            soma = soma.add(p.getValorFinal());
        }
        return soma;
    }

    public BigDecimal getCalculaValorPagar() {
        if (selecionado.getEmpenho() != null) {
            return getValorEmpenho().subtract(calcularTotalPago());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calcularTotalPago() {
        BigDecimal soma = BigDecimal.ZERO;
        for (Pagamento p : getPagamentos()) {
            soma = soma.add(p.getValorFinal());
        }
        return soma.subtract(calcularEstornosPagamentos());
    }

    public BigDecimal getSomaEstornoLiquidacoes() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        if (selecionado.getId() != null) {
            for (LiquidacaoEstorno le : facade.getLiquidacaoEstornoFacade().listaPorLiquidacao(selecionado)) {
                valor = valor.add(le.getValor());
            }
        }
        return valor;
    }

    public List<PagamentoEstorno> getEstornoPagamentos() {
        if (selecionado.getId() != null) {
            return facade.getPagamentoEstornoFacade().listaPorLiquidacao(selecionado);
        }
        return new ArrayList<>();
    }

    public List<LiquidacaoEstorno> getEstornoLiquidacoes() {
        if (selecionado.getId() != null) {
            return facade.getLiquidacaoEstornoFacade().listaPorLiquidacao(selecionado);
        }
        return new ArrayList<>();
    }

    public BigDecimal calcularEstornosPagamentos() {
        BigDecimal estornos = BigDecimal.ZERO;
        for (PagamentoEstorno pe : getEstornoPagamentos()) {
            estornos = estornos.add(pe.getValorFinal());
        }
        return estornos;
    }

    public BigDecimal getValorTotalDocumentosLiquidados() {
        if (documentoFiscal != null
            && documentoFiscal.getDoctoFiscalLiquidacao() != null
            && documentoFiscal.getDoctoFiscalLiquidacao().getId() != null) {
            return facade.getValorTotalDocumentosFicaisLiquidado(documentoFiscal.getDoctoFiscalLiquidacao());
        }
        return BigDecimal.ZERO;
    }

    public void listenerDocumentoFiscal() {
        if (documentoFiscal != null
            && documentoFiscal.getDoctoFiscalLiquidacao().getId() != null) {
            if (!facade.getDocumentosLiquidados(documentoFiscal.getDoctoFiscalLiquidacao()).isEmpty()) {
                for (LiquidacaoDoctoFiscal docto : facade.getDocumentosLiquidados(documentoFiscal.getDoctoFiscalLiquidacao())) {
                    saldoLiquidar = docto.getDoctoFiscalLiquidacao().getTotal().subtract(getValorTotalDocumentosLiquidados());
                }
            } else {
                saldoLiquidar = documentoFiscal.getDoctoFiscalLiquidacao().getTotal();
            }
            documentoFiscal.setValorLiquidado(saldoLiquidar);
        }
    }

    public void definirEmpenho() {
        try {
            atribuirValoresAoEmpenho(selecionado.getEmpenho());
            buscarGrupoIntegracaoDocumentoFiscal();
            setIntegracaoDocumentoFiscal(hasDocumentosFiscaisIntegracao());
            validarIntegracaoDocumentoFiscal();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void selecionarEmpenho(ActionEvent evento) {
        selecionado.setEmpenho((Empenho) evento.getComponent().getAttributes().get("objeto"));
        atribuirValoresAoEmpenho(selecionado.getEmpenho());
    }

    private void atribuirValoresAoEmpenho(Empenho empenho) {
        try {
            validarDadosEmpenhoLiquidacao(selecionado);
            selecionado.setEmpenho(facade.getEmpenhoFacade().recuperar(empenho.getId()));
            selecionado.getDesdobramentos().clear();
            selecionado.getDoctoFiscais().clear();
            selecionado.setComplemento(selecionado.getEmpenho().getComplementoHistorico());
            if (selecionado.isEmpenhoIntegracaoDiaria()) {
                adicionarDetalhamentoPorConfigDiaria(selecionado.getEmpenho());
                FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:tipoDocFiscalPortaria_input')");
            }
            empenhoComObrigacaoAPagar = facade.getEmpenhoFacade().verificarSeEmpenhoPossuiObrigacaoPagar(selecionado.getEmpenho());
            if (isEmpenhoWithObrigacaoPagar()) {
                novaObrigacaoPagar();
                selecionado.setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.COM_RECONHECIMENTO_OBRIGACAO);
            }
            adicionarDesdobramentoEmpenhoNaLiquidacao();
            atualizarIsContaDespesaEventoReinfComEmpenho();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void adicionarDesdobramentoEmpenhoNaLiquidacao() {
        if (selecionado.hasEmpenhoComDesdobramento()) {
            selecionado.getDesdobramentos().clear();
            for (DesdobramentoEmpenho desdobramentoEmpenho : selecionado.getEmpenho().getDesdobramentos()) {
                Desdobramento desdobramento = new Desdobramento();
                desdobramento.setConta(desdobramentoEmpenho.getConta());
                desdobramento.setValor((isEstoque() || isBemMovel()) ? BigDecimal.ZERO : desdobramentoEmpenho.getSaldo());
                desdobramento.setSaldo(desdobramento.getValor());
                desdobramento.setDesdobramentoEmpenho(desdobramentoEmpenho);
                desdobramento.setLiquidacao(selecionado);
                definirEvento(desdobramento);
                Util.adicionarObjetoEmLista(selecionado.getDesdobramentos(), desdobramento);
            }
        }
    }

    private void adicionarDetalhamentoPorConfigDiaria(Empenho empenho) {
        if (!TipoProposta.SUPRIMENTO_FUNDO.equals(empenho.getPropostaConcessaoDiaria().getTipoProposta())) {
            ConfigTipoViagemContaDespesa configuracaoDiaria = buscarConfiguracaoDiaria();
            if (configuracaoDiaria != null) {
                adicionarDesdobramentoPorConfiguracaoDiaria(configuracaoDiaria);
            }
        }
    }

    private ConfigTipoViagemContaDespesa buscarConfiguracaoDiaria() {
        ConfigTipoViagemContaDespesa configuracao = facade.getConfigTipoViagemContaDespesaFacade().recuperaConfiguracaoVigente(
            selecionado.getEmpenho().getPropostaConcessaoDiaria().getTipoViagem(),
            facade.getSistemaFacade().getDataOperacao());
        if (configuracao != null) {
            return configuracao;
        }
        FacesUtil.addWarn("Aviso!", "Configuração não encontrada para o detalhamento da despesa referente a diária: " + selecionado.getEmpenho().getPropostaConcessaoDiaria().getCodigo()
            + " e tipo de viagem: " + selecionado.getEmpenho().getPropostaConcessaoDiaria().getTipoViagem().getDescricao()
            + ". Clique em novo para adicionar o(s) detalhamento(s) da despesa.");
        return null;
    }

    private void adicionarDesdobramentoPorConfiguracaoDiaria(ConfigTipoViagemContaDespesa configDiaria) {
        for (TipoViagemContaDespesa tipoViagem : configDiaria.getListaContaDespesa()) {
            if (tipoViagem.getTipoViagem().equals(selecionado.getEmpenho().getPropostaConcessaoDiaria().getTipoViagem())) {
                novoDetalhamento();
                desdobramento.setConta(tipoViagem.getContaDespesa());
                desdobramento.setValor(selecionado.getEmpenho().getSaldo());
                adicionarDetalhamento();
            }
        }
        cancelarDetalhamento();
    }

    public List<DoctoFiscalLiquidacao> completarDocumentosFiscais(String parte) {
        return buscarDocumentosFiscais(selecionado.getEmpenho(), parte.trim());
    }

    private List<DoctoFiscalLiquidacao> buscarDocumentosFiscais(Empenho empenho, String filtro) {
        List<DoctoFiscalLiquidacao> documentos = Lists.newArrayList();
        Set<DoctoFiscalLiquidacao> documentosEncontrados = new HashSet<>();
        if (selecionado.isEmpenhoIntegracaoContratoOrReconhecimentoDividaOrAta()) {
            switch (empenho.getTipoContaDespesa()) {
                case BEM_MOVEL:
                case BEM_IMOVEL:
                    documentosEncontrados.addAll(facade.getDoctoFiscalLiquidacaoFacade().buscarDocumentoFiscalPorGrupoBem(filtro.trim(), empenho));
                    break;
                case BEM_ESTOQUE:
                    documentosEncontrados.addAll(facade.getDoctoFiscalLiquidacaoFacade().buscarDocumentoFiscalPorGrupoMaterial(filtro.trim(), empenho));
                    break;
            }
        }
        if (empenho.getEmpenho() != null) {
            documentosEncontrados.addAll(buscarDocumentosFiscais(empenho.getEmpenho(), filtro));
        }
        if (selecionado.isLiquidacaoPossuiObrigacoesPagar()) {
            documentosEncontrados.addAll(facade.getDoctoFiscalLiquidacaoFacade().buscarDocumentoFiscalPorObrigacoesPagar(getIdsObrigacaoPagar(), filtro.trim()));
        }
        documentos.addAll(documentosEncontrados);
        return documentos;
    }

    public BigDecimal getValorEmpenho() {
        if (selecionado.getEmpenho() != null) {
            BigDecimal valorDosEstornosDoEmpenho = BigDecimal.ZERO;
            for (EmpenhoEstorno empenhoEstorno : selecionado.getEmpenho().getEmpenhoEstornos()) {
                valorDosEstornosDoEmpenho = valorDosEstornosDoEmpenho.add(empenhoEstorno.getValor());
            }
            return selecionado.getEmpenho().getValor().subtract(valorDosEstornosDoEmpenho);
        }
        return BigDecimal.ZERO;
    }

    public Boolean getVerificaEdicao() {
        return selecionado.getId() != null;
    }

    public void limparCamposDoEmpenho() {
        selecionado.setEmpenho(null);
        selecionado.setComplemento(null);
        cancelarDetalhamento();
        cancelarDocumentoFiscal();
        selecionado.getObrigacoesPagar().clear();
    }

    public void definirContaDoDetalhamento(Conta conta) {
        desdobramento.setConta(conta);
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:contaDesdobramento_input')");
    }

    private void validarIntegracaoDocumentoFiscal() {
        if ((isBemMovel() || isEstoque()) && !permitiNovoDocumentoFiscalEstoque()) {
            ValidacaoException ve = new ValidacaoException();
            if (!hasDocumentosFiscaisIntegracao()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Documento de integração não encontrado para o desdobramento " + selecionado.getDesdobramentos().get(0).getConta() + ".");

            } else if (documentosIntegracao.stream().noneMatch(DocumentoFiscalIntegracao::getIntegrador)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Integração não Realizada!", "Verifique os grupos  integração entre o documento comprobatório e o desdobramento da liquidação");

                documentosIntegracao.forEach(doc -> {
                    doc.getGrupos().forEach(grupo -> {
                        String descGrupo = doc.isEstoque() ? grupo.getGrupoMaterial().toString() : grupo.getGrupoBem().toString();
                        doc.getMensagens().add("Desdobramento não encontrado para o grupo integrador " + descGrupo + ".");
                    });
                });

            } else if (documentosIntegracao.stream().noneMatch(DocumentoFiscalIntegracao::getSelecionado)
                && documentosIntegracao.stream().anyMatch(DocumentoFiscalIntegracao::hasSaldo)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um documento fiscal para continuar.");
            }
            ve.lancarException();
            documentosIntegracao.stream().filter(grupo -> grupo.getIntegrador() && grupo.hasInconsistencia())
                .map(grupo -> "Inconsistência encontrada na integração com documento comprobatório: " + grupo.getDoctoFiscalLiquidacao() +
                    ".  Verifique os dados na tabela 'Documentos Comprobatórios da Integração'.")
                .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);

            documentosIntegracao.stream().filter(DocumentoFiscalIntegracao::getSelecionado).forEach(doc -> validarDocumentoFiscalComChaveJaCadastradaParaMesmoEmpenho(doc.getDoctoFiscalLiquidacao(), ve));
            ve.lancarException();
        }
    }

    private void validarIntegracaoComServicoDeTerceiros(ValidacaoException ve) {
        for (Desdobramento desdobramento : selecionado.getDesdobramentos()) {
            if (selecionado.getEmpenho().getTipoContaDespesa().isServicoTerceiro() &&
                !facade.hasConfiguracaoExcecaoVigente(selecionado.getDataLiquidacao(), desdobramento.getConta()) &&
                facade.getSolicitacaoEmpenhoFacade().recuperarSolicitacaoEmpenhoPorEmpenho(selecionado.isLiquidacaoRestoPagar() ? selecionado.getEmpenho().getEmpenho() : selecionado.getEmpenho()) == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Liquidações de Serviços de Terceiros para este desdobramento é necessário vinculo do empenho com o Contrato, utilize a tela de vinculação de contrato com empenho para dar continuidade na liquidação");
            }
        }
    }

    private void validarValoresAoSalvar(ValidacaoException va) {
        if (!selecionado.getDoctoFiscais().isEmpty()) {
            if (selecionado.getTotalDocumentos().compareTo(selecionado.getValor()) != 0) {
                va.adicionarMensagemDeOperacaoNaoPermitida(" Valor total do(s) Documento(s) de <b>" + Util.formataValor(selecionado.getTotalDocumentos()) + "</b> é diferente do valor de <b> " + Util.formataValor(selecionado.getValor()) + " </b> da liquidação.");
            }
        }
        if (selecionado.getTotalDesdobramentos().compareTo(selecionado.getValor()) > 0) {
            va.adicionarMensagemDeOperacaoNaoPermitida("O total dos desdobramentos não pode ser maior que o valor da liquidação.");
        }
        if (selecionado.getTotalDesdobramentos().compareTo(selecionado.getTotalDocumentos()) != 0) {
            va.adicionarMensagemDeOperacaoNaoPermitida("O total do(s) desdobramento(s) deve ser igual ao total do(s) documento(s) comprobatório(s).");
        }
        if (isOperacaoNovo() && !selecionado.isLiquidacaoPossuiObrigacoesPagar()
            && selecionado.getEmpenho().getSaldo().compareTo(selecionado.getValor()) < 0) {
            va.adicionarMensagemDeOperacaoNaoPermitida("O valor a ser liquidado de <b>" + Util.formataValor(selecionado.getValor()) + "</b> é maior que o saldo de <b>" + Util.formataValor(selecionado.getEmpenho().getSaldo()) + "</b> disponível no empenho.");
        }
        if (isEmpenhoWithObrigacaoPagar()
            && !selecionado.isLiquidacaoPossuiObrigacoesPagar()
            && selecionado.getValor().compareTo(selecionado.getEmpenho().getSaldoDisponivelEmpenhoComObrigacao()) > 0) {
            va.adicionarMensagemDeOperacaoNaoPermitida("O valor do lançamento de <b>" + Util.formataValor(selecionado.getValor()) + "</b> não pode ser maior que o saldo de <b>" + Util.formataValor(selecionado.getEmpenho().getSaldoDisponivelEmpenhoComObrigacao()) + "</b> disponível para o empenho selecionado.");
        }
        for (Desdobramento desdobramento : selecionado.getDesdobramentos()) {
            if (desdobramento.getDesdobramentoEmpenho() != null) {
                if (!desdobramento.getConta().equals(desdobramento.getDesdobramentoEmpenho().getConta())) {
                    va.adicionarMensagemDeOperacaoNaoPermitida("A conta de desdobramento informada (" + desdobramento.getConta().getCodigo() + ") está diferente da informada no Empenho (" + desdobramento.getDesdobramentoEmpenho().getConta().getCodigo() + ").");
                } else if (desdobramento.getValor().compareTo(desdobramento.getDesdobramentoEmpenho().getSaldo()) > 0) {
                    va.adicionarMensagemDeOperacaoNaoPermitida("O valor do desdobramento <b>" + Util.formataValor(desdobramento.getValor()) + "</b> é maior que o saldo restante do desdobramento do empenho <b>" + Util.formataValor(desdobramento.getDesdobramentoEmpenho().getSaldo()) + "</b>.");
                }
            }
        }
    }

    private void validarAdicionarDetalhamento() {
        ValidacaoException ve = new ValidacaoException();
        if (desdobramento.getConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Despesa deve ser informado");
        }
        if (desdobramento.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero (0).");
        }
        ve.lancarException();
        validarMesmoDetalhamentoEmLista();
        validarSaldoDisponivelPorDetalhamento();
        ve.lancarException();
    }

    public boolean isBemMovel() {
        return selecionado.getEmpenho().getTipoContaDespesa().isBemMovel();
    }

    public boolean isEstoque() {
        return selecionado.getEmpenho().getTipoContaDespesa().isEstoque();
    }

    private void validarMesmoDetalhamentoEmLista() {
        if (selecionado.isLiquidacaoPossuiObrigacoesPagar()) {
            selecionado.validarMesmoDetalhamentoEmListaPoObrigacaoPagar(desdobramento, desdobramentoObrigacaoPagar.getObrigacaoAPagar());
        } else {
            selecionado.validarMesmoDetalhamentoEmLista(desdobramento);
        }
    }

    private void validarSaldoDisponivelPorDetalhamento() {
        if (selecionado.isLiquidacaoPossuiObrigacoesPagar()) {
            if (desdobramentoObrigacaoPagar.getObrigacaoAPagar().getEmpenho() == null) {
                Empenho empenho = facade.getEmpenhoFacade().recuperarDetalhamento(selecionado.getEmpenho().getId());
                empenho.validarSaldoDisponivelPorContaDespesa(desdobramento.getValor(), desdobramentoObrigacaoPagar.getConta(), desdobramentoObrigacaoPagar.getObrigacaoAPagar());
            } else {
                ObrigacaoAPagar obrigacaoAPagar = facade.getObrigacaoAPagarFacade().recuperar(desdobramentoObrigacaoPagar.getObrigacaoAPagar().getId());
                obrigacaoAPagar.validarSaldoDisponivelPorContaDespesa(desdobramento.getValor(), desdobramentoObrigacaoPagar.getConta());
            }
        }
    }

    public void removerDetalhamento(Desdobramento desd) {
        selecionado.getDesdobramentos().remove(desd);
    }

    public void editarDetalhamento(Desdobramento desd) {
        this.desdobramento = (Desdobramento) Util.clonarObjeto(desd);
        if (selecionado.isLiquidacaoPossuiObrigacoesPagar()) {
            setDesdobramentoObrigacaoPagar(desdobramento.getDesdobramentoObrigacaoPagar());
        } else if (hasEmpenhoComDesdobramento()) {
            setDesdobramentoEmpenho(desdobramento.getDesdobramentoEmpenho());
        }
    }

    public void copiarHistoricoUsuario() {
        if (getHistoricoPadrao() != null) {
            selecionado.setComplemento(getHistoricoPadrao().toStringAutoComplete());
            setHistoricoPadrao(null);
        }
    }

    public ConverterAutoComplete getConverterDesdobramentoObrigacao() {
        if (converterDesdobramentoObrigacao == null) {
            converterDesdobramentoObrigacao = new ConverterAutoComplete(DesdobramentoObrigacaoPagar.class, facade.getDesdobramentoObrigacaoAPagarFacade());
        }
        return converterDesdobramentoObrigacao;
    }

    public ConverterAutoComplete getConverterDesdobramentoEmpenho() {
        if (converterDesdobramentoEmpenho == null) {
            converterDesdobramentoEmpenho = new ConverterAutoComplete(DesdobramentoEmpenho.class, facade.getDesdobramentoEmpenhoFacade());
        }
        return converterDesdobramentoEmpenho;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public boolean isEmpenhoWithObrigacaoPagar() {
        return selecionado.getEmpenho() != null && empenhoComObrigacaoAPagar;
    }

    public List<ObrigacaoAPagar> completarObrigacaoPagar(String parte) {
        if (selecionado.getEmpenho() != null) {
            return facade.getObrigacaoAPagarFacade().buscarObrigacaoPagarPorEmpenho(parte.trim(), selecionado.getEmpenho());
        }
        return new ArrayList<>();
    }

    public boolean desabilitarRemoerObrigacaoPagar() {
        return !selecionado.getDesdobramentos().isEmpty() || !selecionado.getDoctoFiscais().isEmpty();
    }

    public void removerObrigacaoPagar(LiquidacaoObrigacaoPagar obj) {
        selecionado.getObrigacoesPagar().remove(obj);
        if (selecionado.getObrigacoesPagar().isEmpty()) {
            selecionado.getDoctoFiscais().clear();
            selecionado.getDesdobramentos().clear();
            cancelarDocumentoFiscal();
            cancelarDetalhamento();
            selecionado.setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.SEM_RECONHECIMENTO_OBRIGACAO);
        }
    }

    public void adicionarObrigacaoPagar() {
        try {
            validarObrigacaoPagar();
            liquidacaoObrigacaoPagar.setLiquidacao(selecionado);
            liquidacaoObrigacaoPagar.setObrigacaoAPagar(facade.getObrigacaoAPagarFacade().recuperar(liquidacaoObrigacaoPagar.getObrigacaoAPagar().getId()));
            Util.adicionarObjetoEmLista(selecionado.getObrigacoesPagar(), liquidacaoObrigacaoPagar);
            selecionado.setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.COM_RECONHECIMENTO_OBRIGACAO);
            if (!selecionado.getDesdobramentos().isEmpty() && hasEmpenhoComDesdobramento()) {
                for (Desdobramento desd : selecionado.getDesdobramentos()) {
                    definirEvento(desd);
                    for (DesdobramentoObrigacaoPagar desdobramentoObrigacao : liquidacaoObrigacaoPagar.getObrigacaoAPagar().getDesdobramentos()) {
                        if ((desdobramentoObrigacao.getDesdobramentoEmpenho() != null && desd.getDesdobramentoEmpenho().equals(desdobramentoObrigacao.getDesdobramentoEmpenho())
                            || desd.getDesdobramentoEmpenho().getDesdobramentoObrigacaoPagar().equals(desdobramentoObrigacao))) {
                            desd.setDesdobramentoObrigacaoPagar(desdobramentoObrigacao);
                        }
                    }
                }
            }
            novaObrigacaoPagar();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
            selecionado.getObrigacoesPagar().remove(liquidacaoObrigacaoPagar);
            novaObrigacaoPagar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void novaObrigacaoPagar() {
        liquidacaoObrigacaoPagar = new LiquidacaoObrigacaoPagar();
    }

    private void novoDetalhamentoObrigacaoPagar() {
        desdobramentoObrigacaoPagar = new DesdobramentoObrigacaoPagar();
    }

    private void validarObrigacaoPagar() {
        ValidacaoException ve = new ValidacaoException();
        if (liquidacaoObrigacaoPagar.getObrigacaoAPagar() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo obrigação a pagar deve ser informado.");
        }
        for (LiquidacaoObrigacaoPagar liqOp : selecionado.getObrigacoesPagar()) {
            if (liqOp.getObrigacaoAPagar().equals(liquidacaoObrigacaoPagar.getObrigacaoAPagar())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A obrigação a pagar: " + liquidacaoObrigacaoPagar.getObrigacaoAPagar() + " já foi adicionada na lista.");
            }
        }
        ve.lancarException();
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return facade.getEmpenhoFacade();
    }

    public String getFiltroDetalhamento() {
        return filtroDetalhamento;
    }

    public void setFiltroDetalhamento(String filtroDetalhamento) {
        this.filtroDetalhamento = filtroDetalhamento;
    }

    public HistoricoContabil getHistoricoPadrao() {
        return historicoPadrao;
    }

    public void setHistoricoPadrao(HistoricoContabil historicoPadrao) {
        this.historicoPadrao = historicoPadrao;
    }

    public Desdobramento getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(Desdobramento desdobramento) {
        this.desdobramento = desdobramento;
    }

    public List<Conta> getDetalhamentos() {
        return detalhamentos;
    }

    public void setDetalhamentos(List<Conta> detalhamentos) {
        this.detalhamentos = detalhamentos;
    }

    public LiquidacaoDoctoFiscal getDocumentoFiscal() {
        return documentoFiscal;
    }

    public void setDocumentoFiscal(LiquidacaoDoctoFiscal documentoFiscal) {
        this.documentoFiscal = documentoFiscal;
    }

    public BigDecimal getSaldoLiquidar() {
        return saldoLiquidar;
    }

    public void setSaldoLiquidar(BigDecimal saldoLiquidar) {
        this.saldoLiquidar = saldoLiquidar;
    }

    public ParametroEvento getParametroEvento(Desdobramento desdobramento) {
        return facade.criarParametroEvento(desdobramento);
    }

    public LiquidacaoObrigacaoPagar getLiquidacaoObrigacaoPagar() {
        return liquidacaoObrigacaoPagar;
    }

    public void setLiquidacaoObrigacaoPagar(LiquidacaoObrigacaoPagar liquidacaoObrigacaoPagar) {
        this.liquidacaoObrigacaoPagar = liquidacaoObrigacaoPagar;
    }

    public DesdobramentoObrigacaoPagar getDesdobramentoObrigacaoPagar() {
        return desdobramentoObrigacaoPagar;
    }

    public void setDesdobramentoObrigacaoPagar(DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar) {
        this.desdobramentoObrigacaoPagar = desdobramentoObrigacaoPagar;
    }

    public DesdobramentoEmpenho getDesdobramentoEmpenho() {
        return desdobramentoEmpenho;
    }

    public void setDesdobramentoEmpenho(DesdobramentoEmpenho desdobramentoEmpenho) {
        this.desdobramentoEmpenho = desdobramentoEmpenho;
    }

    public List<DocumentoFiscalIntegracao> getDocumentosIntegracao() {
        return documentosIntegracao;
    }

    public void setDocumentosIntegracao(List<DocumentoFiscalIntegracao> documentosIntegracao) {
        this.documentosIntegracao = documentosIntegracao;
    }

    public boolean isUnidadeBloqueadaParaNovoDocBensEstoque() {
        return isUnidadeBloqueadaParaNovoDocBensEstoque;
    }

    public void setUnidadeBloqueadaParaNovoDocBensEstoque(boolean unidadeBloqueadaParaNovoDocBensEstoque) {
        isUnidadeBloqueadaParaNovoDocBensEstoque = unidadeBloqueadaParaNovoDocBensEstoque;
    }

    private void buscarIsUnidadeBloqueadaParaNovoDocBensEstoque() {
        isUnidadeBloqueadaParaNovoDocBensEstoque = facade.getConfiguracaoContabilFacade().isUnidadeOrcamentariaBloqueada(selecionado.getUnidadeOrganizacional());
    }

    @Override
    public void cleanScoped() {
        super.cleanScoped();
        converterDesdobramentoObrigacao = null;
        converterDesdobramentoEmpenho = null;
        moneyConverter = null;
        documentoFiscal = null;
        desdobramento = null;
        historicoPadrao = null;
        detalhamentos = null;
        filtroDetalhamento = null;
        saldoLiquidar = null;
        liquidacaoObrigacaoPagar = null;
        desdobramentoObrigacaoPagar = null;
        desdobramentoEmpenho = null;
        pagamento = null;
        pagamentoEstorno = null;
        documentosIntegracao = null;
        empenhoComObrigacaoAPagar = false;
        isUnidadeBloqueadaParaNovoDocBensEstoque = false;
    }

    public boolean isTipoDocumentoFiscalObrigaChaveAcesso() {
        return documentoFiscal != null
            && documentoFiscal.getDoctoFiscalLiquidacao() != null
            && documentoFiscal.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal() != null
            && documentoFiscal.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal().getObrigarChaveDeAcesso();
    }

    public List<NotaFiscal> getNotasFiscais() {
        return notasFiscais;
    }

    public void setNotasFiscais(List<NotaFiscal> notasFiscais) {
        this.notasFiscais = notasFiscais;
    }

    public void buscarNotasFiscais() {
        notasFiscais = facade.buscarNotasFiscais(selecionado.getEmpenho().getFornecedor().getId(), selecionado.getExercicio(), documentoFiscal);
        FacesUtil.executaJavaScript("dialogNotasFiscais.show()");
    }

    public void atribuirNotaFiscalAoDocumentoComprobatorio(NotaFiscal notaFiscal) {
        documentoFiscal.setNotaFiscal(notaFiscal);
        documentoFiscal.getDoctoFiscalLiquidacao().setChaveDeAcesso(notaFiscal.getCodigoVerificacao());
        notasFiscais.clear();
        FacesUtil.executaJavaScript("dialogNotasFiscais.hide()");
    }

    public void imprimirNotaFiscal(NotaFiscal notaFiscal) {
        try {
            byte[] bytes = facade.getNotaFiscalFacade().gerarImpressaoNotaFiscalSistemaNfse(notaFiscal.getId());
            AbstractReport report = new AbstractReport();
            report.setGeraNoDialog(true);
            report.escreveNoResponse("Nota Fiscal Eletrônica", bytes);
            facade.getNotaFiscalFacade().inserirLogImpressao(notaFiscal.getId(), "Documento Comprobatório da Liquidação");
        } catch (Exception e) {
            logger.error("Erro ao imprimir Nfs-e. Erro: {}", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao imprimir Nfs-e. Erro: " + e.getMessage());
        }
    }

    public List<NaturezaRendimentoReinf> completarNaturezasDosRendimentosREINF(String parte) {
        return facade.getNaturezaRendimentoReinfFacade().buscarNaturezasVigentes(parte, facade.getSistemaFacade().getDataOperacao());
    }

    public DocumentoFiscalIntegracaoAssistente getAssistenteDocumentoFiscal() {
        return assistenteDocumentoFiscal;
    }

    public void setAssistenteDocumentoFiscal(DocumentoFiscalIntegracaoAssistente assistenteDocumentoFiscal) {
        this.assistenteDocumentoFiscal = assistenteDocumentoFiscal;
    }

    public boolean isIntegracaoDocumentoFiscal() {
        return integracaoDocumentoFiscal;
    }

    public void setIntegracaoDocumentoFiscal(boolean integracaoDocumentoFiscal) {
        this.integracaoDocumentoFiscal = integracaoDocumentoFiscal;
    }

    public void atualizarValorBaseCalculoQuandoNaoRetencaoPrevidenciaria() {
        documentoFiscal.setValorBaseCalculoIrrf(documentoFiscal.getDoctoFiscalLiquidacao().getValor());
        documentoFiscal.calcularValorRetidoIrrf();
    }

    private Conta recuperarContaDoExercicio(Conta contaConfigurada) {
        if (contaConfigurada.getExercicio().equals(facade.getSistemaFacade().getExercicioCorrente())) {
            return contaConfigurada;
        }
        return facade.getContaDespesaDesdobradaFacade().buscarContaExtraOrcamentariaPorCodigoExercicio(contaConfigurada.getCodigo(), facade.getSistemaFacade().getExercicioCorrente());
    }

    public Conta buscarContaInss() {
        if (configuracaoContabil != null && configuracaoContabil.getContaExtraInssPadraoDocLiq() != null) {
            return recuperarContaDoExercicio(configuracaoContabil.getContaExtraInssPadraoDocLiq());
        }
        return null;
    }

    public Conta buscarContaIrrf() {
        if (configuracaoContabil != null && configuracaoContabil.getContaExtraIrrfPadraoDocLiq() != null) {
            return recuperarContaDoExercicio(configuracaoContabil.getContaExtraIrrfPadraoDocLiq());
        }
        return null;
    }

    public boolean isRenderizarCamposReinf2010() {
        if (renderizarCamposReinf2010 == null) {
            renderizarCamposReinf2010 = Boolean.FALSE;
        }
        return renderizarCamposReinf2010;
    }

    public void setRenderizarCamposReinf2010(boolean renderizarCamposReinf2010) {
        this.renderizarCamposReinf2010 = renderizarCamposReinf2010;
    }

    public boolean isRenderizarCamposReinf4020() {
        if (renderizarCamposReinf4020 == null) {
            renderizarCamposReinf4020 = isContaDespesaEventoReinf;
        }
        return renderizarCamposReinf4020;
    }

    public void setRenderizarCamposReinf4020(boolean renderizarCamposReinf4020) {
        this.renderizarCamposReinf4020 = renderizarCamposReinf4020;
    }

    public Boolean getContaDespesaEventoReinf() {
        if (isContaDespesaEventoReinf == null) {
            isContaDespesaEventoReinf = Boolean.FALSE;
        }
        return isContaDespesaEventoReinf;
    }

    public void setContaDespesaEventoReinf(Boolean contaDespesaEventoReinf) {
        isContaDespesaEventoReinf = contaDespesaEventoReinf;
    }

    private void atualizarIsContaDespesaEventoReinfComEmpenho() {
        isContaDespesaEventoReinf = Boolean.FALSE;
        if (selecionado.getEmpenho() != null && selecionado.getEmpenho().getTipoContaDespesa() != null &&
            configuracaoContabil != null && configuracaoContabil.getTiposContasDespesasReinf() != null && !configuracaoContabil.getTiposContasDespesasReinf().isEmpty()) {
            for (ConfiguracaoContabilTipoContaDespesaReinf ccTipo : configuracaoContabil.getTiposContasDespesasReinf()) {
                if (selecionado.getEmpenho().getTipoContaDespesa().equals(ccTipo.getTipoContaDespesa())) {
                    isContaDespesaEventoReinf = Boolean.TRUE;
                    break;
                }
            }
        }
    }

    private void atualizarRenderizarEventoReinfVerEditar() {
        renderizarCamposReinf4020 = isContaDespesaEventoReinf;
        renderizarCamposReinf2010 = Boolean.FALSE;
        if (selecionado.getDoctoFiscais() != null && !selecionado.getDoctoFiscais().isEmpty()) {
            selecionado.getDoctoFiscais().forEach(doc -> {
                if (doc.getRetencaoPrevidenciaria()) renderizarCamposReinf2010 = Boolean.TRUE;
                if (doc.getOptanteSimplesNacional()) renderizarCamposReinf4020 = Boolean.FALSE;
            });
        }
    }

    public void editarReinfDocumentoFiscal(LiquidacaoDoctoFiscal obj) {
        documentoFiscal = (LiquidacaoDoctoFiscal) Util.clonarObjeto(obj);
        documentoFiscal.setContaExtraIrrf(documentoFiscal.getContaExtraIrrf() != null ? documentoFiscal.getContaExtraIrrf() : buscarContaIrrf());
        documentoFiscal.setValorBaseCalculoIrrf(documentoFiscal.getDoctoFiscalLiquidacao().getValor());
        if (documentoFiscal.getValorRetidoIrrf() == null) {
            documentoFiscal.calcularValorRetidoIrrf();
        }
        if (hasPagamentoNaoEstornado) {
            documentoFiscal = facade.atualizarValoresReinfComRetencoesDoPagamento(obj, configuracaoContabil);
        }
        atualizarEventosReinfAtuais();
    }

    public void atualizarHasPagamentoNaoEstornado() {
        hasPagamentoNaoEstornado = facade.getPagamentoFacade().hasPagamentoNaoEstornadoParaLiquidacao(selecionado);
    }

    public Boolean getHasPagamentoNaoEstornado() {
        return hasPagamentoNaoEstornado == null ? Boolean.FALSE : hasPagamentoNaoEstornado;
    }

    public void setHasPagamentoNaoEstornado(Boolean hasPagamentoNaoEstornado) {
        this.hasPagamentoNaoEstornado = hasPagamentoNaoEstornado;
    }

    private void atualizarEventosReinfAtuais() {
        if (documentoFiscal != null) {
            eventoR2010Atual = facade.getDoctoFiscalLiquidacaoFacade().buscarEventoAtualReinfDoDoctoFiscalPorTipo(documentoFiscal.getDoctoFiscalLiquidacao(), TipoArquivoReinf.R2010V2);
            eventoR4020Atual = facade.getDoctoFiscalLiquidacaoFacade().buscarEventoAtualReinfDoDoctoFiscalPorTipo(documentoFiscal.getDoctoFiscalLiquidacao(), TipoArquivoReinf.R4020);
        }
    }

    public EventoReinfDTO getEventoR2010Atual() {
        return eventoR2010Atual;
    }

    public void setEventoR2010Atual(EventoReinfDTO eventoR2010Atual) {
        this.eventoR2010Atual = eventoR2010Atual;
    }

    public EventoReinfDTO getEventoR4020Atual() {
        return eventoR4020Atual;
    }

    public void setEventoR4020Atual(EventoReinfDTO eventoR4020Atual) {
        this.eventoR4020Atual = eventoR4020Atual;
    }

    public DocumentoFiscalIntegracao getDocumentoIntegracao() {
        return documentoIntegracao;
    }

    public void setDocumentoIntegracao(DocumentoFiscalIntegracao documentoIntegracao) {
        this.documentoIntegracao = documentoIntegracao;
    }
}
