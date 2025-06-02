/*
 * Codigo gerado automaticamente em Wed Dec 14 09:49:45 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LiquidacaoEstornoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ManagedBean(name = "liquidacaoEstornoControlador")
@ViewScoped
@URLMappings(mappings = {
//        Mapeamento Estorno de liquidação
    @URLMapping(id = "novo-liquidacao-estorno", pattern = "/liquidacao-estorno/novo/", viewId = "/faces/financeiro/orcamentario/liquidacaoestorno/edita.xhtml"),
    @URLMapping(id = "editar-liquidacao-estorno", pattern = "/liquidacao-estorno/editar/#{liquidacaoEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/liquidacaoestorno/edita.xhtml"),
    @URLMapping(id = "ver-liquidacao-estorno", pattern = "/liquidacao-estorno/ver/#{liquidacaoEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/liquidacaoestorno/visualizar.xhtml"),
    @URLMapping(id = "listar-liquidacao-estorno", pattern = "/liquidacao-estorno/listar/", viewId = "/faces/financeiro/orcamentario/liquidacaoestorno/lista.xhtml"),

//        Mapeamento Estorno de liquidação de resto a pagar
    @URLMapping(id = "novo-liquidacao-estorno-resto", pattern = "/liquidacao-estorno/resto-a-pagar/novo/", viewId = "/faces/financeiro/orcamentario/liquidacaoestorno/edita.xhtml"),
    @URLMapping(id = "editar-liquidacao-estorno-resto", pattern = "/liquidacao-estorno/resto-a-pagar/editar/#{liquidacaoEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/liquidacaoestorno/edita.xhtml"),
    @URLMapping(id = "ver-liquidacao-estorno-resto", pattern = "/liquidacao-estorno/resto-a-pagar/ver/#{liquidacaoEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/liquidacaoestorno/visualizar.xhtml"),
    @URLMapping(id = "listar-liquidacao-estorno-resto", pattern = "/liquidacao-estorno/resto-a-pagar/listar/", viewId = "/faces/financeiro/orcamentario/liquidacaoestorno/listarestopagar.xhtml")
})
public class LiquidacaoEstornoControlador extends PrettyControlador<LiquidacaoEstorno> implements Serializable, CRUD {

    @EJB
    private LiquidacaoEstornoFacade facade;
    private DesdobramentoLiquidacaoEstorno desdobramento;
    private LiquidacaoEstornoDoctoFiscal documentoFiscal;
    private DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar;
    private ConverterAutoComplete converterDocumentoFiscal;
    private ConverterAutoComplete converterDesdobramentoObrigacao;
    private SolicitacaoLiquidacaoEstorno solicitacaoLiquidacaoEstorno;
    private List<SolicitacaoLiquidacaoEstorno> solicitacoesLiquidacaoEstorno;
    private Integer indiceAba;
    private List<DocumentoFiscalIntegracao> documentosIntegracao;
    private Boolean hasTransferenciasBens;

    public LiquidacaoEstornoControlador() {
        super(LiquidacaoEstorno.class);
    }

    public LiquidacaoEstornoFacade getFacade() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        if (isLiquidacaoEstornoCategoriaNormal()) {
            return "/liquidacao-estorno/";
        } else {
            return "/liquidacao-estorno/resto-a-pagar/";
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public void redirecionaParaLista() {
        if (isLiquidacaoEstornoCategoriaNormal()) {
            FacesUtil.redirecionamentoInterno("/liquidacao-estorno/listar/");
        } else {
            FacesUtil.redirecionamentoInterno("/liquidacao-estorno/resto-a-pagar/listar/");
        }
    }

    public String getTituloLiquidacaoEstorno() {
        if (isLiquidacaoEstornoCategoriaNormal()) {
            return "Estorno de Liquidação";
        } else {
            return "Estorno de Liquidação de Resto a Pagar";
        }
    }

    public String getTituloLiquidacao() {
        if (isLiquidacaoEstornoCategoriaNormal()) {
            return "Liquidação";
        } else {
            return "Liquidação de Resto a Pagar";
        }
    }

    @URLAction(mappingId = "novo-liquidacao-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCategoriaOrcamentaria(CategoriaOrcamentaria.NORMAL);
        parametrosIniciais();
    }

    @URLAction(mappingId = "ver-liquidacao-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-liquidacao-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "novo-liquidacao-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRestoPagar() {
        super.novo();
        selecionado.setCategoriaOrcamentaria(CategoriaOrcamentaria.RESTO);
        parametrosIniciais();
    }

    @URLAction(mappingId = "ver-liquidacao-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verRestoPagar() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-liquidacao-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarRestoPagar() {
        super.editar();
        recuperarEditarVer();
    }

    private void parametrosIniciais() {
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setDataEstorno(facade.getSistemaFacade().getDataOperacao());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        if (facade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", facade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
        solicitacoesLiquidacaoEstorno = facade.getSolicitacaoLiquidacaoFacade().buscarSolicitacaoLiquidacaoEstorno(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(), selecionado.getCategoriaOrcamentaria());
        indiceAba = 0;
        documentosIntegracao = Lists.newArrayList();
    }

    public Boolean isLiquidacaoEstornoCategoriaNormal() {
        return selecionado.isLiquidacaoEstornoCategoriaNormal();
    }

    public void recuperarEditarVer() {
        selecionado = facade.recuperar(getId());
        selecionado.setLiquidacao(facade.getLiquidacaoFacade().recuperar(selecionado.getLiquidacao().getId()));
        facade.recuperarTransferenciasLiquidacaoDoctoFiscal(selecionado);
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                validarIntegracaoDocumentoFiscal();
                validarSelecionado();
                selecionado = facade.salvarNovoEstorno(selecionado, solicitacaoLiquidacaoEstorno, documentosIntegracao);
                FacesUtil.addOperacaoRealizada(" Estorno de liquidação " + selecionado.toString() + " foi salvo com sucesso. ");
                redirecionarParaVer();
            } else {
                facade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada(" Estorno de liquidação " + selecionado.toString() + " foi alterado com sucesso.");
                redireciona();
            }
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            facade.getSingletonConcorrenciaContabil().desbloquear(selecionado.getLiquidacao());
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            facade.getSingletonConcorrenciaContabil().desbloquear(selecionado.getLiquidacao());
            descobrirETratarException(e);
        }
    }

    private void validarSelecionado() {
        ValidacaoException ve = new ValidacaoException();
        validarRegrasEspecificas(ve);
        validarSolicitacoesEstornoIntegracaoMateriais();
    }

    private void validarIntegracaoDocumentoFiscal() {
        if (isBemMovel() || isEstoque()) {
            ValidacaoException ve = new ValidacaoException();
            if (!hasDocumentosFiscaisIntegracao() && !Util.isListNullOrEmpty(selecionado.getDesdobramentos())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Documento de integração não encontrado para o desdobramento " + selecionado.getDesdobramentos().get(0).getConta() + ".");
            }
            ve.lancarException();

            if (documentosIntegracao.stream().noneMatch(DocumentoFiscalIntegracao::getSelecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um documento fiscal para continuar.");
            }
            if (documentosIntegracao.stream().noneMatch(DocumentoFiscalIntegracao::getIntegrador)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Integração não Realizada!", "Verifique os grupos  integração entre o documento comprobatório e o desdobramento da liquidação");
            }
            ve.lancarException();

            documentosIntegracao.stream().filter(grupo -> grupo.getIntegrador() && grupo.hasInconsistencia())
                .map(grupo -> "Inconsistência encontrada na integração com documento comprobatório. Verifique os dados na tabela 'Documentos Fiscais Integração'.")
                .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);

            ve.lancarException();
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getLiquidacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo " + getTituloLiquidacao() + " deve ser informado.");
        }
        if (selecionado.getComplementoHistorico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Histórico deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarRegrasEspecificas(ValidacaoException ve) {

        if (selecionado.getDesdobramentos() == null || selecionado.getDesdobramentos().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para salvar é obrigatório adicionar um detalhamento.");
        }
        if (selecionado.getDocumentosFiscais() == null || selecionado.getDesdobramentos().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para salvar é obrigatório adicionar um documento comprobatório.");
        }
        if (selecionado.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo valor deve ser maior que zero(0).");
        }
        if (selecionado.getLiquidacao() != null) {
            Liquidacao liquidacao = facade.getLiquidacaoFacade().recuperar(selecionado.getLiquidacao().getId());
            if (liquidacao.getSaldo().compareTo(selecionado.getValor()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor a ser estornado de <b> " + Util.formataValor(selecionado.getValor()) + "</b> é maior que o saldo de <b> " + Util.formataValor(liquidacao.getSaldo()) + " </b> disponível na liquidação.");
            }
            if (DataUtil.dataSemHorario(selecionado.getDataEstorno()).compareTo(DataUtil.dataSemHorario(selecionado.getLiquidacao().getDataLiquidacao())) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" A data do estorno de liquidação (" + DataUtil.getDataFormatada(selecionado.getDataEstorno()) + ") deve ser maior ou igual a data da liquidação selecionada. Data da liquidação: <b>" + DataUtil.getDataFormatada(selecionado.getLiquidacao().getDataLiquidacao()) + "</b>.");
            }
            if (selecionado.getLiquidacao().getEmpenho().getTipoContaDespesa().isBemMovel()) {
                if (selecionado.getValor().compareTo(liquidacao.getSaldo()) != 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Foram desbloqueados bens no Patrimônio para essa liquidação. Portanto, o valor a ser estornado deve ser igual ao saldo da liquidação.");
                }
            }
            facade.getLiquidacaoFacade().getHierarquiaOrganizacionalFacade()
                .validarUnidadesIguais(selecionado.getLiquidacao().getUnidadeOrganizacional(), selecionado.getUnidadeOrganizacional()
                    , ve
                    , "A Unidade Organizacional do estorno de liquidação deve ser a mesma da liquidação.");
        }
        if (selecionado.getValorTotalDetalhamentos().compareTo(selecionado.getValorTotalDocumentosFiscais()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O(s) detalhamento(s) e documento(s) comprobatório(s) devem possuir os mesmos valores.");
        }
        if (solicitacaoLiquidacaoEstorno != null && selecionado.getValor().compareTo(solicitacaoLiquidacaoEstorno.getValor()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do estorno da liquidação deve ser igual ao valor da solicitação de estorno.");
        }
        ve.lancarException();
    }

    public void adicionarDetalhamento() {
        try {
            validarDetalhamento();
            desdobramento.setLiquidacaoEstorno(selecionado);
            desdobramento.setLiquidacao(selecionado.getLiquidacao());
            EventoContabil eventoContabil = definirEventoContabil();
            if (eventoContabil != null) {
                desdobramento.setEventoContabil(eventoContabil);
            }
            validarEventoContabil();
            selecionado.setDesdobramentos(Util.adicionarObjetoEmLista(selecionado.getDesdobramentos(), desdobramento));
            selecionado.setValor(selecionado.getValorTotalDetalhamentos());
            cancelarDetalhamento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
        FacesUtil.executaJavaScript("setaFoco('Formulario:historico_input')");
    }

    private void validarEventoContabil() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getLiquidacao().isLiquidacaoRestoNaoProcessada() && desdobramento.getEventoContabil() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo evento contábil deve ser informado.");
        }
        ve.lancarException();
    }

    private void adicionarContaDetalhamentoObrigacaoPagarAoDesdobramentoLiquidacao() {
        ValidacaoException ve = new ValidacaoException();
        if (desdobramentoObrigacaoPagar == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Despesa deve ser informado.");
        }
        ve.lancarException();
        if (desdobramentoObrigacaoPagar.getConta() != null) {
            desdobramento.setDesdobramentoObrigacaoPagar(desdobramentoObrigacaoPagar);
            desdobramento.setConta(desdobramentoObrigacaoPagar.getConta());
        }
    }

    private void validarMesmoDetalhamentoEmLista() {
        if (selecionado.getLiquidacao().isLiquidacaoPossuiObrigacoesPagar()) {
            selecionado.validarMesmoDetalhamentoEmListaPoObrigacaoPagar(desdobramento, desdobramentoObrigacaoPagar.getObrigacaoAPagar());
        } else {
            selecionado.validarMesmoDetalhamentoEmLista(desdobramento);
        }
    }

    private EventoContabil definirEventoContabil() {
        if (desdobramento != null) {
            return facade.buscarEventoContabil(desdobramento);
        }
        return null;
    }

    public void novoDetalhamento() {
        try {
            validarNovoDesdobramento();
            desdobramento = new DesdobramentoLiquidacaoEstorno();
            if (selecionado.getLiquidacao() != null && selecionado.getLiquidacao().isLiquidacaoPossuiObrigacoesPagar()) {
                novoDetalhamentoObrigacaoPagar();
            }
            FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:contaDesdobramento_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void novoDetalhamentoObrigacaoPagar() {
        desdobramentoObrigacaoPagar = new DesdobramentoObrigacaoPagar();
    }


    public void novoDocumentoFiscal() {
        try {
            validarNovoDesdobramento();
            documentoFiscal = new LiquidacaoEstornoDoctoFiscal();
            FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:doctoFiscal_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<DoctoFiscalLiquidacao> completarDocumentosFiscais(String parte) {
        if (selecionado.getLiquidacao() != null) {
            return facade.getDoctoFiscalLiquidacaoFacade().buscarDocumentoFiscalPorLiquidacao(selecionado.getLiquidacao(), parte.trim());
        }
        return Lists.newArrayList();
    }


    private void definirFocoBtnNovoDocumentoFiscal() {
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:btnNovoDocumento')");
    }

    private void definirFocoBtnNovoDesdobramento() {
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:btnNovoDesdobramento')");
    }


    public void cancelarDocumentoFiscal() {
        documentoFiscal = null;
        definirFocoBtnNovoDocumentoFiscal();
    }

    public void adicionarDocumentoFiscal() {
        try {
            validarDocumentoFiscal();
            documentoFiscal.setLiquidacaoEstorno(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getDocumentosFiscais(), documentoFiscal);
            cancelarDocumentoFiscal();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDocumentoFiscal() {
        ValidacaoException ve = new ValidacaoException();
        validarNovoDesdobramento();
        if (documentoFiscal.getDocumentoFiscal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo documento fiscal deve ser informado.");
        }
        if (documentoFiscal.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser maior que zero(0)");
        }
        if (documentoFiscal.getValor().compareTo(selecionado.getLiquidacao().getSaldo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do detalhamento deve ser menor ou igual ao saldo da liquidação.");
        }
        selecionado.getLiquidacao().validarSaldoDisponivelPorDocumentoFiscal(documentoFiscal.getValor(), documentoFiscal.getDocumentoFiscal());
        for (LiquidacaoEstornoDoctoFiscal doctoFiscal : selecionado.getDocumentosFiscais()) {
            if (!doctoFiscal.equals(documentoFiscal) && doctoFiscal.getDocumentoFiscal().equals(documentoFiscal.getDocumentoFiscal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O documento fiscal já foi adicionado na lista.");
            }
        }
        ve.lancarException();
    }

    private void validarNovoDesdobramento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getLiquidacao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo liquidação deve ser informado.");
        }
        ve.lancarException();
    }

    public void cancelarDetalhamento() {
        desdobramento = null;
        definirFocoBtnNovoDesdobramento();
    }

    private void validarDetalhamento() {
        if (selecionado.getLiquidacao() != null && selecionado.getLiquidacao().isLiquidacaoPossuiObrigacoesPagar()) {
            adicionarContaDetalhamentoObrigacaoPagarAoDesdobramentoLiquidacao();
        }
        ValidacaoException ve = new ValidacaoException();
        if (desdobramento.getConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Conta de Despesa deve ser informado.");
        }
        if (desdobramento.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser maior que zero(0).");
        }
        ve.lancarException();
        validarMesmoDetalhamentoEmLista();
        validarSaldoDesdobramento();
    }

    public void removerDetalhamento(DesdobramentoLiquidacaoEstorno desd) {
        selecionado.getDesdobramentos().remove(desd);
        selecionado.setValor(selecionado.getValorTotalDetalhamentos());
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:conta_input')");
    }

    public void editarDetalhamento(DesdobramentoLiquidacaoEstorno desd) {
        this.desdobramento = (DesdobramentoLiquidacaoEstorno) Util.clonarObjeto(desd);
        if (selecionado.getLiquidacao().isLiquidacaoPossuiObrigacoesPagar()) {
            setDesdobramentoObrigacaoPagar(desdobramento.getDesdobramentoObrigacaoPagar());
        }
    }

    public List<Liquidacao> completarLiquidacao(String parte) {
        if (isLiquidacaoEstornoCategoriaNormal()) {
            return facade.getLiquidacaoFacade().buscarPorPessoa(
                parte.trim(),
                selecionado.getExercicio().getAno(),
                selecionado.getUnidadeOrganizacional());
        } else {
            return facade.getLiquidacaoFacade().buscarLiquidacaRestoPagarPorPessoaNaoTransportada(
                parte.trim(),
                selecionado.getExercicio().getAno(),
                selecionado.getUnidadeOrganizacional());
        }
    }

    private void validarSaldoDesdobramento() {
        ValidacaoException ve = new ValidacaoException();
        BigDecimal saldoLiquidacao = selecionado.getLiquidacao().getSaldo();
        if (saldoLiquidacao != null && desdobramento.getValor().compareTo(saldoLiquidacao) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor a ser estornado de <b>" + Util.formataValor(desdobramento.getValor()) + "</b> é maior que o saldo de <b>" + Util.formataValor(saldoLiquidacao) + "</b> disponível na liquidação .");
        }
        validarSaldoDisponivelPorDetalhamento();
        ve.lancarException();
    }


    private void validarSaldoDisponivelPorDetalhamento() {
        if (selecionado.getLiquidacao() != null) {
            if (selecionado.getLiquidacao().isLiquidacaoPossuiObrigacoesPagar()) {
                ObrigacaoAPagar obrigacaoAPagar = facade.getObrigacaoAPagarFacade().recuperar(desdobramentoObrigacaoPagar.getObrigacaoAPagar().getId());
                selecionado.getLiquidacao().validarSaldoDisponivelPorContaDespesa(desdobramento.getValor(), desdobramento.getConta(), obrigacaoAPagar);
            } else {
                selecionado.getLiquidacao().validarSaldoDisponivelPorContaDespesa(desdobramento.getValor(), desdobramento.getConta(), null);
            }
        }
    }

    public Boolean isRegistroEditavel() {
        return isOperacaoEditar();
    }

    public Boolean isBemMovel() {
        return selecionado.getLiquidacao() != null && selecionado.getLiquidacao().getEmpenho().getTipoContaDespesa().isBemMovel();
    }

    public Boolean isEstoque() {
        return selecionado.getLiquidacao() != null && selecionado.getLiquidacao().getEmpenho().getTipoContaDespesa().isEstoque();
    }

    public void gerarNotaOrcamentaria(boolean isDownload) {
        try {
            List<NotaExecucaoOrcamentaria> notas = facade.buscarNotaLiquidacaoEstorno(" AND NOTA.ID = " + selecionado.getId(), selecionado.getCategoriaOrcamentaria());
            if (notas != null && !notas.isEmpty()) {
                facade.getNotaOrcamentariaFacade().imprimirDocumentoOficial(notas.get(0), selecionado.getCategoriaOrcamentaria().isResto() ? ModuloTipoDoctoOficial.NOTA_RESTO_ESTORNO_LIQUIDACAO : ModuloTipoDoctoOficial.NOTA_ESTORNO_LIQUIDACAO, selecionado, isDownload);
            }
        } catch (Exception ex) {
            logger.error("Erro ao imprimir nota: " + ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void definirLiquidacaoComoNull() {
        selecionado.setLiquidacao(null);
    }

    public void selecionarLiquidacao(ActionEvent evento) {
        selecionado.setLiquidacao((Liquidacao) evento.getComponent().getAttributes().get("objeto"));
        definirLiquidacao();
    }

    public void definirLiquidacao() {
        try {
            if (selecionado.getLiquidacao() != null) {
                selecionado.setLiquidacao(facade.getLiquidacaoFacade().recuperar(selecionado.getLiquidacao().getId()));
                selecionado.setComplementoHistorico(selecionado.getLiquidacao().getComplemento());
                validarSolicitacoesEstornoIntegracaoMateriais();
                adicionarDocumentoFiscalDesdobramentoIntegracaoBemMovelAndEstoque();
                FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:conta_input')");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            selecionado.setLiquidacao(null);
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private void adicionarDocumentoFiscalDesdobramentoIntegracaoBemMovelAndEstoque() {
        if (isBemMovel() || isEstoque()) {
            selecionado.setDesdobramentos(Lists.newArrayList());
            for (Desdobramento desd : selecionado.getLiquidacao().getDesdobramentos()) {
                novoDetalhamento();
                desdobramento.setLiquidacaoEstorno(selecionado);
                desdobramento.setLiquidacao(selecionado.getLiquidacao());
                desdobramento.setConta(desd.getConta());
                desdobramento.setValor(desd.getValor());
                desdobramento.setEventoContabil(definirEventoContabil());
                selecionado.getDesdobramentos().add(desdobramento);
            }
            criarDocumentoFiscalIntegracao();
            cancelarDetalhamento();
        }
    }

    private void criarDocumentoFiscalIntegracao() {
        try {
            documentosIntegracao = Lists.newArrayList();
            selecionado.setDocumentosFiscais(Lists.newArrayList());
            DocumentoFiscalIntegracaoAssistente filtro = new DocumentoFiscalIntegracaoAssistente();
            filtro.setLancamentoNormal(false);
            filtro.setDesdobramentoEstorno(desdobramento);
            filtro.setEmpenho(selecionado.getLiquidacao().getEmpenho());
            filtro.setData(selecionado.getDataEstorno());
            filtro.setExercicio(selecionado.getExercicio());
            filtro.setUnidadeOrganizacional(selecionado.getUnidadeOrganizacional());
            List<LiquidacaoDoctoFiscal> documentosLiquidacao = selecionado.getLiquidacao().getDoctoFiscais();
            filtrarDocumentosFiscaisSolicitacaoEstornoLiquidacao(documentosLiquidacao);

            documentosLiquidacao.forEach(doc -> {
                filtro.setDoctoFiscalLiquidacao(doc.getDoctoFiscalLiquidacao());
                filtro.setValorEstornarDocumento(doc.getSaldo());
                DocumentoFiscalIntegracao docInteg = facade.getIntegradorDocumentoFiscalLiquidacaoFacade().criarDocumentoFiscalIntegracao(filtro);
                docInteg.verificarInconsistencias();
                documentosIntegracao.add(docInteg);
                criarLiquidacaoDoctoFiscal(docInteg);
            });
            desdobramento.setValor(getValorTotalALiquidarGrupoIntegrador());
            selecionado.setValor(getValorTotalALiquidarGrupoIntegrador());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    private void criarLiquidacaoDoctoFiscal(DocumentoFiscalIntegracao docInt) {
        if (docInt.getSelecionado()) {
            LiquidacaoEstornoDoctoFiscal novoDocEst = new LiquidacaoEstornoDoctoFiscal();
            novoDocEst.setLiquidacaoEstorno(selecionado);
            novoDocEst.setDocumentoFiscal(docInt.getDoctoFiscalLiquidacao());
            novoDocEst.setValor(docInt.getValorALiquidar());
            novoDocEst.getTransferenciasBens().clear();
            docInt.getEstornoTransferenciasBens().forEach(transferencia -> {
                transferencia.setLiquidacaoEstDoctoFiscal(novoDocEst);
                novoDocEst.getTransferenciasBens().add(transferencia);
            });
            selecionado.getDocumentosFiscais().add(novoDocEst);
        }
    }

    private void filtrarDocumentosFiscaisSolicitacaoEstornoLiquidacao(List<LiquidacaoDoctoFiscal> documentosLiquidacao) {
        if (solicitacaoLiquidacaoEstorno != null) {
            SolicitacaoEstornoEntradaMaterial solEstEntrada = facade.getEntradaMaterialFacade().buscarSolicitacaoEstornoEntradaPorSolicitacaoLiquidacaoEstorno(solicitacaoLiquidacaoEstorno);
            if (solEstEntrada != null) {
                List<DoctoFiscalLiquidacao> documentosEntrada = facade.getDoctoFiscalLiquidacaoFacade().buscarDoctoFiscalLiquidacao(solEstEntrada.getEntradaCompraMaterial());
                documentosLiquidacao.removeIf(docLiq -> !documentosEntrada.contains(docLiq.getDoctoFiscalLiquidacao()));
            }
        }
    }

    public void distribuirValorEAdicionarOuRemoverDocumentoFiscal(DocumentoFiscalIntegracao docInt) {
        if (docInt.getSelecionado()) {
            criarLiquidacaoDoctoFiscal(docInt);
        } else {
            LiquidacaoEstornoDoctoFiscal documentoParaRemover = null;
            for (LiquidacaoEstornoDoctoFiscal liquidacaoEstornoDoctoFiscal : selecionado.getDocumentosFiscais()) {
                if (liquidacaoEstornoDoctoFiscal.getDocumentoFiscal().equals(docInt.getDoctoFiscalLiquidacao())) {
                    documentoParaRemover = liquidacaoEstornoDoctoFiscal;
                    break;
                }
            }
            selecionado.getDocumentosFiscais().remove(documentoParaRemover);
        }
        distribuirValorLiquidadoGrupoIntegracao(docInt);
    }

    public void distribuirValorLiquidadoGrupoIntegracao(DocumentoFiscalIntegracao docInt) {
        docInt.setValorALiquidar(docInt.getSelecionado()
            ? (docInt.getValorALiquidar().compareTo(BigDecimal.ZERO) > 0 ? docInt.getValorALiquidar() : docInt.getSaldo())
            : BigDecimal.ZERO);
        atualizarValorDosItensETransferencias(docInt);
        docInt.verificarInconsistencias();
        if (docInt.hasInconsistencia()) {
            docInt.setValorALiquidar(BigDecimal.ZERO);
            selecionado.setValor(getValorTotalALiquidarGrupoIntegrador());
        }
        docInt.getDesdobramentoEstorno().setValor(getValorTotalALiquidarGrupoIntegrador());
        selecionado.setValor(getValorTotalALiquidarGrupoIntegrador());
        hasTransferenciasBens = !selecionado.getDocumentosFiscais().isEmpty() && selecionado.getDocumentosFiscais().stream().anyMatch(docto -> !docto.getTransferenciasBens().isEmpty());
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
            selecionado.getDocumentosFiscais().forEach(documentoFiscalEstorno -> {
                if (documentoFiscalEstorno.getDocumentoFiscal().equals(docInt.getDoctoFiscalLiquidacao())) {
                    documentoFiscalEstorno.getTransferenciasBens().forEach(transferencia -> {
                        transferencia.setValor(valorPorGrupo.get(transferencia.getIdGrupoOrigem()));
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

    public boolean hasDocumentosFiscaisIntegracao() {
        return !Util.isListNullOrEmpty(documentosIntegracao);
    }


    public void editarDocumentoFiscal(LiquidacaoEstornoDoctoFiscal doc) {
        this.documentoFiscal = (LiquidacaoEstornoDoctoFiscal) Util.clonarObjeto(doc);
    }

    public void removerDocumentoFiscal(LiquidacaoEstornoDoctoFiscal doc) {
        selecionado.getDocumentosFiscais().remove(doc);
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:doctoFiscal_input')");
    }

    public ConverterAutoComplete getConverterDesdobramentoObrigacao() {
        if (converterDesdobramentoObrigacao == null) {
            converterDesdobramentoObrigacao = new ConverterAutoComplete(DesdobramentoObrigacaoPagar.class, facade.getDesdobramentoObrigacaoAPagarFacade());
        }
        return converterDesdobramentoObrigacao;
    }

    public ConverterAutoComplete getConverterDocumentoFiscal() {
        if (converterDocumentoFiscal == null) {
            converterDocumentoFiscal = new ConverterAutoComplete(DoctoFiscalLiquidacao.class, facade.getDoctoFiscalLiquidacaoFacade());
        }
        return converterDocumentoFiscal;
    }

    public List<Conta> completarContaDespesaDesdobrada(String parte) {
        if (selecionado.getLiquidacao() != null) {
            return facade.getLiquidacaoFacade().getContaDespesaDesdobradaFacade().listaContasPorLiquidacao(
                parte.trim(),
                selecionado.getLiquidacao(),
                facade.getSistemaFacade().getExercicioCorrente());
        }
        return new ArrayList<Conta>();
    }

    public List<DesdobramentoObrigacaoPagar> completarDestalhamentoObrigacaoPagar(String parte) {
        if (selecionado.getLiquidacao() != null && selecionado.getLiquidacao().isLiquidacaoPossuiObrigacoesPagar()) {
            return facade.getDesdobramentoObrigacaoAPagarFacade().buscarDesdobramentoObrigacaoPagarPorLiquidacao(
                parte.trim(),
                selecionado.getLiquidacao());
        }
        return new ArrayList<>();
    }

    public void definirSaldoDocumentoFiscal() {
        if (selecionado.getLiquidacao() != null) {
            for (LiquidacaoDoctoFiscal doctoLiquidacao : selecionado.getLiquidacao().getDoctoFiscais()) {
                if (documentoFiscal.getDocumentoFiscal().equals(doctoLiquidacao.getDoctoFiscalLiquidacao())) {
                    documentoFiscal.setValor(doctoLiquidacao.getSaldo());
                }
            }
        }
    }

    public void definirSaldoDetalhamento() {
        if (selecionado.getLiquidacao() != null) {
            for (Desdobramento desdLiquidacao : selecionado.getLiquidacao().getDesdobramentos()) {
                if (!selecionado.getLiquidacao().isLiquidacaoPossuiObrigacoesPagar()) {
                    if (desdobramento.getConta().equals(desdLiquidacao.getConta())) {
                        desdobramento.setValor(desdLiquidacao.getSaldo());
                    }
                } else {
                    if (desdobramentoObrigacaoPagar.getConta().equals(desdLiquidacao.getConta())
                        && desdLiquidacao.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(desdobramentoObrigacaoPagar.getObrigacaoAPagar())) {
                        desdobramento.setValor(desdLiquidacao.getSaldo());
                    }
                }
            }
        }
    }

    public void estornarSolicitacao(SolicitacaoLiquidacaoEstorno solicitacao) {
        try {
            solicitacaoLiquidacaoEstorno = solicitacao;
            solicitacaoLiquidacaoEstorno.setLiquidacaoEstorno(selecionado);
            selecionado.setLiquidacao(solicitacaoLiquidacaoEstorno.getLiquidacao());
            definirLiquidacao();
            selecionado.setComplementoHistorico(solicitacaoLiquidacaoEstorno.getHistorico());
            selecionado.setValor(solicitacaoLiquidacaoEstorno.getValor());
            indiceAba = 0;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void validarSolicitacoesEstornoIntegracaoMateriais() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getLiquidacao() != null && solicitacaoLiquidacaoEstorno == null) {
            List<SolicitacaoLiquidacaoEstorno> solicitacoes = facade.getSolicitacaoLiquidacaoFacade().buscarSolicitacoesPendentesPorUnidadeAndLiquidacao(selecionado.getUnidadeOrganizacional(), selecionado.getLiquidacao(), selecionado.getCategoriaOrcamentaria());
            if (!solicitacoes.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existe(m) solicitação(ões) de estorno de liquidação para a liquidação: " + selecionado.getLiquidacao().getNumero()
                    + ". Utilize a aba 'Solicitações' para selecioná-la.");
                selecionado.setLiquidacao(null);
                indiceAba = 1;
                FacesUtil.atualizarComponente("Formulario:tabGeral");
            }
        }
        ve.lancarException();
        if (selecionado.getLiquidacao().getEmpenho().getTipoContaDespesa().isEstoque()) {
            List<EntradaCompraMaterial> entradas = Lists.newArrayList();
            for (LiquidacaoDoctoFiscal docto : selecionado.getLiquidacao().getDoctoFiscais()) {
                entradas.add(facade.getEntradaMaterialFacade().recuperarEntradaMaterialPorDocumentoFiscal(docto.getDoctoFiscalLiquidacao()));
            }

            if (!Util.isListNullOrEmpty(entradas) && solicitacaoLiquidacaoEstorno == null) {
                entradas.forEach(ent -> {
                    String url = FacesUtil.getRequestContextPath() + "/entrada-por-compra/ver/" + ent.getId() + "/";
                    String mensagem = "Esta liquidacão possui integração com a entrada por compra " + ent + "." +
                        " " + "<b><a href='" + url + "'target='_blank' style='color: blue;'>Clique aqui</a></b>" +
                        " para gerar a solicitação de estorno da liquidação referente a está liquidação. ";
                    ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
                    selecionado.setLiquidacao(null);
                });

            }
        }
        ve.lancarException();
    }


    public DesdobramentoLiquidacaoEstorno getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(DesdobramentoLiquidacaoEstorno desdobramento) {
        this.desdobramento = desdobramento;
    }

    public LiquidacaoEstornoDoctoFiscal getDocumentoFiscal() {
        return documentoFiscal;
    }

    public void setDocumentoFiscal(LiquidacaoEstornoDoctoFiscal documentoFiscal) {
        this.documentoFiscal = documentoFiscal;
    }

    public DesdobramentoObrigacaoPagar getDesdobramentoObrigacaoPagar() {
        return desdobramentoObrigacaoPagar;
    }

    public void setDesdobramentoObrigacaoPagar(DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar) {
        this.desdobramentoObrigacaoPagar = desdobramentoObrigacaoPagar;
    }

    public SolicitacaoLiquidacaoEstorno getSolicitacaoLiquidacaoEstorno() {
        return solicitacaoLiquidacaoEstorno;
    }

    public void setSolicitacaoLiquidacaoEstorno(SolicitacaoLiquidacaoEstorno solicitacaoLiquidacaoEstorno) {
        this.solicitacaoLiquidacaoEstorno = solicitacaoLiquidacaoEstorno;
    }

    public List<SolicitacaoLiquidacaoEstorno> getSolicitacoesLiquidacaoEstorno() {
        return solicitacoesLiquidacaoEstorno;
    }

    public void setSolicitacoesLiquidacaoEstorno(List<SolicitacaoLiquidacaoEstorno> solicitacoesLiquidacaoEstorno) {
        this.solicitacoesLiquidacaoEstorno = solicitacoesLiquidacaoEstorno;
    }

    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    public List<DocumentoFiscalIntegracao> getDocumentosIntegracao() {
        return documentosIntegracao;
    }

    public void setDocumentosIntegracao(List<DocumentoFiscalIntegracao> documentosIntegracao) {
        this.documentosIntegracao = documentosIntegracao;
    }

    public boolean hasTransferenciasGruposBens() {
        if (!selecionado.getDocumentosFiscais().isEmpty()) {
            for (LiquidacaoEstornoDoctoFiscal doc : selecionado.getDocumentosFiscais()) {
                if (!doc.getTransferenciasBens().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
}
