package br.com.webpublico.controle;

/**
 * Created by mga on 23/06/2017.
 */

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ObrigacaoAPagarFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "obrigacaoAPagarControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaObrigacaoPagar", pattern = "/obrigacao-a-pagar/novo/", viewId = "/faces/financeiro/orcamentario/obrigacaopagar/edita.xhtml"),
    @URLMapping(id = "editarObrigacaoPagar", pattern = "/obrigacao-a-pagar/editar/#{obrigacaoAPagarControlador.id}/", viewId = "/faces/financeiro/orcamentario/obrigacaopagar/edita.xhtml"),
    @URLMapping(id = "verObrigacaoPagar", pattern = "/obrigacao-a-pagar/ver/#{obrigacaoAPagarControlador.id}/", viewId = "/faces/financeiro/orcamentario/obrigacaopagar/visualizar.xhtml"),
    @URLMapping(id = "listarObrigacaoPagar", pattern = "/obrigacao-a-pagar/listar/", viewId = "/faces/financeiro/orcamentario/obrigacaopagar/lista.xhtml"),
})
public class ObrigacaoAPagarControlador extends PrettyControlador<ObrigacaoAPagar> implements Serializable, CRUD {

    @EJB
    private ObrigacaoAPagarFacade facade;
    private ObrigacaoAPagarDoctoFiscal obrigacaoDocumentoFiscal;
    private DesdobramentoObrigacaoPagar desdobramento;
    private ConverterAutoComplete converterFonteDespesaORC;
    private String filtroDetalhamento;
    private HistoricoContabil historicoPadrao;
    private List<Conta> desdobramentos;
    private List<TipoContaDespesa> tiposContaDespesa;

    public ObrigacaoAPagarControlador() {
        super(ObrigacaoAPagar.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/obrigacao-a-pagar/";
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "novaObrigacaoPagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        parametrosIniciais();
    }

    @URLAction(mappingId = "editarObrigacaoPagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        definirTiposContaDespesa();
    }

    @URLAction(mappingId = "verObrigacaoPagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();

    }

    private void parametrosIniciais() {
        selecionado.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setSubTipoDespesa(SubTipoDespesa.NAO_APLICAVEL);
        selecionado.setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA);

        if (facade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", facade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    public void novoDocumentoFiscal() {
        try {
            validarNovoDesdobramento();
            obrigacaoDocumentoFiscal = new ObrigacaoAPagarDoctoFiscal();
            obrigacaoDocumentoFiscal.setDocumentoFiscal(new DoctoFiscalLiquidacao());
            obrigacaoDocumentoFiscal.getDocumentoFiscal().setValor(selecionado.getSaldo());
            FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:tipoDocumentoFiscal_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarDocumentoFiscal() {
        obrigacaoDocumentoFiscal = null;
        definirFocoBtnNovoDocumento();
    }

    public void novoDesdobramento() {
        try {
            validarNovoDesdobramento();
            desdobramento = new DesdobramentoObrigacaoPagar();
            desdobramento.setValor(selecionado.getValorTotalDocumentos());
            FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:contaDesdobramento_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarDesdobramento() {
        desdobramento = null;
        definirFocoBtnNovoDesdobramento();
    }

    public void adicionarDesdobramento() {
        try {
            validarDesdobramento();
            desdobramento.setObrigacaoAPagar(selecionado);
            desdobramento.setSaldo(desdobramento.getValor());
            atualizarDesdobramentoPeloEmpenho();
            definirEventoContabil();
            Util.adicionarObjetoEmLista(selecionado.getDesdobramentos(), desdobramento);
            cancelarDesdobramento();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void atualizarDesdobramentoPeloEmpenho() {
        if (selecionado.getEmpenho() != null) {
            selecionado.setEmpenho(facade.getEmpenhoFacade().recuperar(selecionado.getEmpenho().getId()));
            if (!selecionado.getEmpenho().getDesdobramentos().isEmpty()) {
                for (DesdobramentoEmpenho desdobramentoEmpenho : selecionado.getEmpenho().getDesdobramentos()) {
                    if (desdobramentoEmpenho.getConta().equals(desdobramento.getConta())) {
                        desdobramento.setDesdobramentoEmpenho(desdobramentoEmpenho);
                        break;
                    }
                }
            }
        }
    }

    private void validarDesdobramento() {
        ValidacaoException ve = new ValidacaoException();
        validarNovoDesdobramento();
        desdobramento.realizarValidacoes();
        if (desdobramento.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser maior que zero(0)");
        }
        for (DesdobramentoObrigacaoPagar desd : selecionado.getDesdobramentos()) {
            if (!desd.equals(desdobramento) && desd.getConta().equals(desdobramento.getConta())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A conta de despesa já foi adicionada na lista.");
            }
        }
        ve.lancarException();
    }

    public Boolean getBloquearNovoDesdobramento() {
        return isOperacaoEditar() || (facade.getConfiguracaoContabilFacade().configuracaoContabilVigente().getBloquearUmDesdobramento() && !selecionado.getDesdobramentos().isEmpty());
    }

    private void validarNovoDesdobramento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDespesaORC() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo elemento de despesa deve ser informado.");
        }
        if (selecionado.getTipoContaDespesa() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo tipo conta de despesa deve ser informado.");
        }
        ve.lancarException();
    }

    private void definirEventoContabil() {
        EventoContabil eventoContabil = facade.buscarEventoContabil(desdobramento);
        if (eventoContabil != null) {
            desdobramento.setEventoContabil(eventoContabil);
        }
    }

    public void adicionarDocumentoFiscal() {
        try {
            obrigacaoDocumentoFiscal.realizarValidacoes();
            selecionado.validarMesmoDocumentoEmLista(obrigacaoDocumentoFiscal.getDocumentoFiscal());
            obrigacaoDocumentoFiscal.setObrigacaoAPagar(selecionado);
            obrigacaoDocumentoFiscal.getDocumentoFiscal().setTotal(obrigacaoDocumentoFiscal.getDocumentoFiscal().getValor());
            obrigacaoDocumentoFiscal.getDocumentoFiscal().setSaldo(obrigacaoDocumentoFiscal.getDocumentoFiscal().getValor());
            obrigacaoDocumentoFiscal.setSaldo(obrigacaoDocumentoFiscal.getDocumentoFiscal().getValor());
            obrigacaoDocumentoFiscal.setValor(obrigacaoDocumentoFiscal.getDocumentoFiscal().getValor());
            Util.adicionarObjetoEmLista(selecionado.getDocumentosFiscais(), obrigacaoDocumentoFiscal);
            selecionado.setValor(selecionado.getValorTotalDocumentos());
            cancelarDocumentoFiscal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void definirFocoBtnNovoDocumento() {
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:btnNovoDocto')");
    }

    private void definirFocoBtnNovoDesdobramento() {
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:btnNovoDesdobramento')");
    }

    public void removerDocumentoFiscal(ObrigacaoAPagarDoctoFiscal documento) {
        documento.setValor(documento.getValor().subtract(documento.getDocumentoFiscal().getValor()));
        documento.getDocumentoFiscal().setTotal(documento.getDocumentoFiscal().getTotal().subtract(documento.getDocumentoFiscal().getValor()));
        selecionado.getDocumentosFiscais().remove(documento);
        selecionado.setValor(selecionado.getValorTotalDocumentos());
    }

    public void editarDocumentoFiscal(ObrigacaoAPagarDoctoFiscal documento) {
        this.obrigacaoDocumentoFiscal = (ObrigacaoAPagarDoctoFiscal) Util.clonarEmNiveis(documento, 1);
    }

    public List<TipoContaDespesa> getTiposContaDespesaObrigacao() {
        return TipoContaDespesa.retornaTipoContaObrigacaoPagar();
    }

    public List<SelectItem> getListaUfs() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (UF obj : facade.getUfFacade().lista()) {
            toReturn.add(new SelectItem(obj, obj.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoContas() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (tiposContaDespesa != null) {
            for (TipoContaDespesa tipo : tiposContaDespesa) {
                toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return toReturn;
    }

    public void definirTiposContaDespesa() {
        tiposContaDespesa = Lists.newArrayList();
        if (getContaDespesaSelecionada() != null) {
            selecionado.setContaDespesa(getContaDespesaSelecionada());
            TipoContaDespesa tipoContaElemento = getContaDespesaSelecionada().getTipoContaDespesa();
            if (!TipoContaDespesa.NAO_APLICAVEL.equals(tipoContaElemento)) {
                selecionado.setTipoContaDespesa(tipoContaElemento);
                tiposContaDespesa.add(tipoContaElemento);
            } else {
                List<TipoContaDespesa> tiposContaFilhas = facade.getContaFacade().buscarTiposContasDespesaNosFilhosDaConta(getContaDespesaSelecionada());
                for (TipoContaDespesa tp : tiposContaFilhas) {
                    if (!tp.equals(TipoContaDespesa.NAO_APLICAVEL)) {
                        tiposContaDespesa.add(tp);
                    }
                }
            }
        }

    }

    public void atribuirFonteDeRecursos() {
        if (selecionado.getFonteDespesaORC() != null) {
            selecionado.setContaDeDestinacao(selecionado.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao());
            selecionado.setFonteDeRecursos(selecionado.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos());
        }
    }

    private ContaDespesa getContaDespesaSelecionada() {
        try {
            if (selecionado.getDespesaORC() != null) {
                if (selecionado.isObrigacaoPagarDepoisEmpenho()) {
                    return (ContaDespesa) selecionado.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa();
                }
                return (ContaDespesa) selecionado.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean renderizarSubTipoDespesa() {
        return selecionado.getTipoContaDespesa() != null && TipoContaDespesa.retornaTipoContaObrigacaoPagar().contains(selecionado.getTipoContaDespesa());
    }

    public List<SelectItem> getSubTipoContas() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (SubTipoDespesa subTipo : buscarSubTiposContaDespesa()) {
            toReturn.add(new SelectItem(subTipo, subTipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SubTipoDespesa> buscarSubTiposContaDespesa() {
        List<SubTipoDespesa> lista = new ArrayList<>();
        if (TipoContaDespesa.VARIACAO_PATRIMONIAL_DIMINUTIVA.equals(selecionado.getTipoContaDespesa()) ||
            TipoContaDespesa.SERVICO_DE_TERCEIRO.equals(selecionado.getTipoContaDespesa())) {
            lista.add(SubTipoDespesa.NAO_APLICAVEL);

        } else if (TipoContaDespesa.PESSOAL_ENCARGOS.equals(selecionado.getTipoContaDespesa())) {
            lista.add(SubTipoDespesa.RGPS);
            lista.add(SubTipoDespesa.RPPS);

        } else {
            for (SubTipoDespesa std : SubTipoDespesa.values()) {
                lista.add(std);
            }
        }
        return lista;
    }

    public List<SelectItem> getFontesDespesaORC() {
        List<SelectItem> list = new ArrayList<SelectItem>();
        list.add(new SelectItem(null, ""));
        if (selecionado.getDespesaORC() != null) {
            for (FonteDespesaORC object : facade.getFonteDespesaORCFacade().completaFonteDespesaORC("", selecionado.getDespesaORC())) {
                list.add(new SelectItem(object, "" + object.getProvisaoPPAFonte().getDestinacaoDeRecursos()));
            }
        }
        return list;
    }
    public List<SelectItem> getOperacoes() {
        List<SelectItem> list = new ArrayList<SelectItem>();
        list.add(new SelectItem(null, ""));
        if (selecionado.isObrigacaoPagarDepoisEmpenho()) {
            list.add(new SelectItem(OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_EM_LIQUIDACAO, OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_EM_LIQUIDACAO.getDescricao()));
            list.add(new SelectItem(OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RGPS_EM_LIQUIDACAO, OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RGPS_EM_LIQUIDACAO.getDescricao()));
            list.add(new SelectItem(OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RPPS_EM_LIQUIDACAO, OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RPPS_EM_LIQUIDACAO.getDescricao()));
        } else {
            list.add(new SelectItem(OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA, OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA.getDescricao()));
            list.add(new SelectItem(OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RGPS, OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RGPS.getDescricao()));
            list.add(new SelectItem(OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RPPS, OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RPPS.getDescricao()));
        }
        return list;
    }

    public List<TipoDocumentoFiscal> completarTipoDocumentos(String parte) {
        List<TipoDocumentoFiscal> resultado = facade.buscarTiposDeDocumentosPorContaDeDespesa((ContaDespesa) selecionado.getContaDespesa(), selecionado.getTipoContaDespesa(), parte);
        if (!resultado.isEmpty()) {
            return resultado;
        }
        return facade.getTipoDocumentoFiscalFacade().buscarTiposDeDocumentosAtivos(parte.trim());
    }

    public List<Pessoa> completarPessoa(String parte) {
        return facade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<SelectItem> getClassesCredor() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        if (selecionado.getTipoContaDespesa() != null && selecionado.getSubTipoDespesa() != null && selecionado.getPessoa() != null) {
            ConfiguracaoTipoContaDespesaClasseCredor configClasseCredor = facade.getConfiguracaoTipoContaDespesaClasseCredorFacade().buscarConfiguracaoVigente(
                selecionado.getTipoContaDespesa(),
                selecionado.getSubTipoDespesa(),
                selecionado.getDataLancamento());
            List<ClasseCredor> classesDaPessoa = facade.getClasseCredorFacade().buscarClassesPorPessoa("", selecionado.getPessoa());
            if (configClasseCredor != null) {
                for (TipoContaDespesaClasseCredor tipoContaDespesaClasseCredor : configClasseCredor.getListaDeClasseCredor()) {
                    for (ClasseCredor classeCredor : classesDaPessoa) {
                        if (classeCredor.equals(tipoContaDespesaClasseCredor.getClasseCredor())) {
                            retorno.add(new SelectItem(tipoContaDespesaClasseCredor.getClasseCredor()));
                        }
                    }
                }
            } else {
                for (ClasseCredor classeCredor : classesDaPessoa) {
                    retorno.add(new SelectItem(classeCredor));
                }
            }
        }

        return retorno;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                selecionado = facade.salvarObrigacao(selecionado);
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            } else {
                facade.salvar(selecionado);
                redireciona();
            }
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        validarSelecionado(ve);
        validarTipoEmpenho(ve);
        validarUnidadeEmpenho(ve);
        if (isOperacaoNovo()) {
            validarValoresAoSalvar(ve);
        }
        ve.lancarException();
    }

    private void validarUnidadeEmpenho(ValidacaoException validacaoException) {
        if (selecionado.isObrigacaoPagarDepoisEmpenho()) {
            facade.getHierarquiaOrganizacionalFacade().validarUnidadesIguais(
                selecionado.getEmpenho().getUnidadeOrganizacional(),
                selecionado.getUnidadeOrganizacional(),
                validacaoException
                , "A Unidade Organizacional da liquidação deve ser a mesma do empenho.");
        }
    }


    private void validarTipoEmpenho(ValidacaoException va) {
        if (selecionado.getEmpenho() != null) {
            String msg = "O tipo do empenho é ordinário, por isso o valor da sua obrigação a pagar deve ser igual ao seu saldo.";
            selecionado.getEmpenho().validarTipoEmpenho(va, msg, selecionado.getValor(), selecionado.getEmpenho().getSaldo());
        }
    }

    private void validarSelecionado(ValidacaoException va) {
        if (selecionado.getDocumentosFiscais().isEmpty()) {
            va.adicionarMensagemDeCampoObrigatorio(" A obrigação a pagar deve possuir ao menos um documento fiscal.");
        }
        if (selecionado.getDesdobramentos().isEmpty()) {
            va.adicionarMensagemDeCampoObrigatorio(" A obrigação a pagar deve possuir ao menos um detalhamento.");
        }
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            va.adicionarMensagemDeOperacaoNaoPermitida(" O campo valor deve ser maior que zero (0).");
        }
        if (selecionado.isObrigacaoPagarDepoisEmpenho() && selecionado.getDataLancamento().compareTo(selecionado.getEmpenho().getDataEmpenho()) < 0) {
            va.adicionarMensagemDeOperacaoNaoPermitida(" A data do lançamento deve ser superior ou igual a data do empenho selecionado. Data do empenho: <b>" + DataUtil.getDataFormatada(selecionado.getEmpenho().getDataEmpenho()) + "</b>.");
        }
    }

    public void recuperarDesdobramento() {
        desdobramentos = selecionado.getEmpenho() == null || (selecionado.getEmpenho() != null && selecionado.getEmpenho().getDesdobramentos().isEmpty()) ?
            facade.getContaFacade().buscarContasFilhasDespesaORCPorTipo(
                filtroDetalhamento.trim(),
                selecionado.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(),
                selecionado.getExercicio(),
                selecionado.getTipoContaDespesa(),
                true) :
            facade.getContaFacade().buscarContasDesdobradasNoEmpenho(
                filtroDetalhamento.trim(),
                selecionado.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(),
                selecionado.getExercicio(),
                selecionado.getTipoContaDespesa(),
                true,
                selecionado.getEmpenho());
    }

    public List<Conta> completarDesdobramento(String parte) {
        if (selecionado.getEmpenho() == null || (selecionado.getEmpenho() != null && selecionado.getEmpenho().getDesdobramentos().isEmpty())) {
            return facade.getContaFacade().buscarContasFilhasDespesaORCPorTipo(
                parte.trim(),
                selecionado.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(),
                selecionado.getExercicio(),
                selecionado.getTipoContaDespesa(),
                false);
        } else {
            return facade.getContaFacade().buscarContasDesdobradasNoEmpenho(
                parte.trim(),
                selecionado.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(),
                selecionado.getExercicio(),
                selecionado.getTipoContaDespesa(),
                false,
                selecionado.getEmpenho());
        }
    }

    private void validarDadosEmpenho() {
        try {
            Preconditions.checkNotNull(selecionado.getEmpenho().getDespesaORC(), "A despesa orçamentária do empenho está vazia.");
            Preconditions.checkNotNull(selecionado.getEmpenho().getTipoContaDespesa(), "O tipo de conta de despeesa do empenho está vazia.");
            Preconditions.checkNotNull(selecionado.getEmpenho().getSubTipoDespesa(), "O subtipo conta de despesa está vazio.");
            Preconditions.checkNotNull(selecionado.getEmpenho().getFonteDespesaORC(), "A fonte de despesa do empenho está vazia.");
        } catch (Exception ex) {
            FacesUtil.addError("Erro com os dados do empenho", ex.getMessage());
        }
    }

    public void gerarNotaOrcamentaria(boolean isDownload) {
        try {
            List<NotaExecucaoOrcamentaria> notas = facade.buscarNotasDeObrigacoesAPagar(" and nota.id = " + selecionado.getId());
            if (notas != null && !notas.isEmpty()) {
                facade.getNotaOrcamentariaFacade().imprimirDocumentoOficial(notas.get(0), ModuloTipoDoctoOficial.NOTA_OBRIGACAO_A_PAGAR, selecionado, isDownload);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Erro ao gerar nota de estorno de obrigação a pagar: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<HistoricoContabil> completarHistoricoContabil(String parte) {
        return facade.getHistoricoContabilFacade().listaFiltrando(parte, "codigo", "descricao");
    }

    public List<Empenho> completarEmpenho(String parte) {
        return facade.getEmpenhoFacade().buscarEmpenhoObrigacaoPagar(
            parte.trim(),
            selecionado.getUsuarioSistema(),
            selecionado.getExercicio(),
            selecionado.getUnidadeOrganizacional()
        );
    }

    public void selecionarEmpenho(ActionEvent evento) {
        Empenho empenho = (Empenho) evento.getComponent().getAttributes().get("objeto");
        if (empenho != null) {
            selecionado.setEmpenho(empenho);
        }
        definirEmpenho();
    }

    public void selecionarPessoa(ActionEvent evento) {
        Pessoa pessoa = (Pessoa) evento.getComponent().getAttributes().get("objeto");
        if (pessoa != null) {
            selecionado.setPessoa(pessoa);
        }
    }

    public void definirEmpenho() {
        if (selecionado.getDesdobramentos() != null) {
            selecionado.getDesdobramentos().clear();
        }
        if (selecionado.getDocumentosFiscais() != null) {
            selecionado.getDocumentosFiscais().clear();
        }
        if (selecionado.isObrigacaoPagarDepoisEmpenho()) {
            selecionado.setEmpenho(facade.getEmpenhoFacade().recuperar(selecionado.getEmpenho().getId()));
            selecionado.setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.RECONHECIMENTO_OBRIGACAO_APOS_EMPENHO_DESPESA);
            validarDadosEmpenho();
            atribuirVariaveisDoEmpenhoParaObrigacaoPagar(selecionado.getEmpenho());
            atribuirFonteDeRecursos();
        }
    }

    private void atribuirVariaveisDoEmpenhoParaObrigacaoPagar(Empenho empenho) {
        selecionado.setDespesaORC(empenho.getDespesaORC());
        selecionado.setFonteDespesaORC(empenho.getFonteDespesaORC());
        selecionado.setHistorico(empenho.getComplementoHistorico());
        selecionado.setPessoa(empenho.getFornecedor());
        selecionado.setClasseCredor(empenho.getClasseCredor());
        definirTiposContaDespesa();
        selecionado.setTipoContaDespesa(empenho.getTipoContaDespesa());
        selecionado.setSubTipoDespesa(empenho.getSubTipoDespesa());
        for (DesdobramentoEmpenho desdobramentoEmpenho : empenho.getDesdobramentos()) {
            desdobramento = new DesdobramentoObrigacaoPagar();
            desdobramento.setConta(desdobramentoEmpenho.getConta());
            desdobramento.setDesdobramentoEmpenho(desdobramentoEmpenho);
            desdobramento.setValor(desdobramentoEmpenho.getValor());
            adicionarDesdobramento();
        }
    }

    public void atribuirNullParaVariaveisDoSelecionado() {
        atribuirNullParaDespesa();
        selecionado.setHistorico(null);
        selecionado.setPessoa(null);
        selecionado.setClasseCredor(null);
        selecionado.setEmpenho(null);
        selecionado.setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA);
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:empenho')");
    }

    private void atribuirNullParaDespesa() {
        selecionado.setDespesaORC(null);
        selecionado.setTipoContaDespesa(null);
        selecionado.setSubTipoDespesa(null);
        selecionado.setFonteDespesaORC(null);
    }

    public void atribuirNullParaClasseAndPessoa() {
        selecionado.setClasseCredor(null);
    }

    public Boolean isRegistroEditavel() {
        return selecionado != null && selecionado.getId() != null;
    }

    public boolean isEmpenhoSelecionado() {
        return isOperacaoEditar() || selecionado.isObrigacaoPagarDepoisEmpenho();
    }

    public void definirContaDesdobrada(Conta conta) {
        desdobramento.setConta(conta);
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:contaDesdobramento_input')");
    }

    private void validarValoresAoSalvar(ValidacaoException va) {
        if (!selecionado.getDocumentosFiscais().isEmpty()) {
            if (selecionado.getValorTotalDocumentos().compareTo(selecionado.getValor()) != 0) {
                va.adicionarMensagemDeOperacaoNaoPermitida(" O valor total do(s) documento(s) de <b>" + Util.formataValor(selecionado.getValorTotalDocumentos()) + "</b> é diferente do valor de <b> " + Util.formataValor(selecionado.getValor()) + " </b> do lançamento.");
            }
        }
        if (selecionado.getValorTotalDesdobramentos().compareTo(selecionado.getValor()) > 0) {
            va.adicionarMensagemDeOperacaoNaoPermitida(" O valor total do(s) desdobramento(s) de <b>" + Util.formataValor(selecionado.getValorTotalDesdobramentos()) + "</b> é diferente do valor de <b> " + Util.formataValor(selecionado.getValor()) + " </b> do lançamento.");
        }
        if (selecionado.getValorTotalDesdobramentos().compareTo(selecionado.getValorTotalDocumentos()) != 0) {
            va.adicionarMensagemDeOperacaoNaoPermitida("O total do(s) desdobramento(s) deve ser igual ao total do(s) documento(s) fiscal(is).");
        }
        if (selecionado.isObrigacaoPagarDepoisEmpenho() && selecionado.getValor().compareTo(selecionado.getEmpenho().getSaldoDisponivelEmpenhoComObrigacao()) > 0) {
            va.adicionarMensagemDeOperacaoNaoPermitida("O valor do lançamento de <b>" + Util.formataValor(selecionado.getValor()) + "</b> não pode ser maior que o saldo de <b>" + Util.formataValor(selecionado.getEmpenho().getSaldoDisponivelEmpenhoComObrigacao()) + "</b> disponível para o empenho selecionado.");
        }
    }

    public void removerDesdobramento(DesdobramentoObrigacaoPagar desdobramento) {
        selecionado.getDesdobramentos().remove(desdobramento);
        FacesUtil.executaJavaScript("setaFoco('Formulario:viewGeral:contaDesdobramento_input')");
    }

    public void editarDesdobramento(DesdobramentoObrigacaoPagar desdobramento) {
        this.desdobramento = (DesdobramentoObrigacaoPagar) Util.clonarObjeto(desdobramento);
    }

    public void copiarHistoricoPadrao() {
        if (getHistoricoPadrao() != null) {
            selecionado.setHistorico(getHistoricoPadrao().toStringAutoComplete());
        }
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, facade.getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }


    public ParametroEvento getParametroEvento(DesdobramentoObrigacaoPagar desdobramento) {
        try {
            ParametroEvento parametroEvento = facade.criarParametroEvento(desdobramento);
            if (parametroEvento != null) {
                return parametroEvento;
            }
            throw new ExcecaoNegocioGenerica("Parametro evento não encontrado para visualizar o evento contábil.");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (RuntimeException ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
        return null;
    }


    public ObrigacaoAPagarDoctoFiscal getObrigacaoDocumentoFiscal() {
        return obrigacaoDocumentoFiscal;
    }

    public void setObrigacaoDocumentoFiscal(ObrigacaoAPagarDoctoFiscal obrigacaoDocumentoFiscal) {
        this.obrigacaoDocumentoFiscal = obrigacaoDocumentoFiscal;
    }

    public DesdobramentoObrigacaoPagar getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(DesdobramentoObrigacaoPagar desdobramento) {
        this.desdobramento = desdobramento;
    }

    public HistoricoContabil getHistoricoPadrao() {
        return historicoPadrao;
    }

    public void setHistoricoPadrao(HistoricoContabil historicoPadrao) {
        this.historicoPadrao = historicoPadrao;
    }

    public String getFiltroDetalhamento() {
        return filtroDetalhamento;
    }

    public void setFiltroDetalhamento(String filtroDetalhamento) {
        this.filtroDetalhamento = filtroDetalhamento;
    }

    public List<Conta> getDesdobramentos() {
        return desdobramentos;
    }

    public void setDesdobramentos(List<Conta> desdobramentos) {
        this.desdobramentos = desdobramentos;
    }

    public List<TipoMovimentoExecucaoOrcamentaria> getAbasExecucaoOrcamentaria() {
        return Arrays.asList(
            TipoMovimentoExecucaoOrcamentaria.EMPENHO,
            TipoMovimentoExecucaoOrcamentaria.EMPENHO_ESTORNO,
            TipoMovimentoExecucaoOrcamentaria.LIQUIDACAO,
            TipoMovimentoExecucaoOrcamentaria.LIQUIDACAO_ESTORNO,
            TipoMovimentoExecucaoOrcamentaria.PAGAMENTO,
            TipoMovimentoExecucaoOrcamentaria.PAGAMENTO_ESTORNO);
    }

    public List<Empenho> getEmpenhos() {
        if (selecionado.getId() != null) {
            return facade.getEmpenhoFacade().buscarEmpenhoPorObrigacaoPagar(selecionado);
        }
        return Lists.newArrayList();
    }

    public List<EmpenhoEstorno> getEstornosEmpenho() {
        if (selecionado.getId() != null) {
            return facade.getEmpenhoEstornoFacade().buscarEstornoEmpenhoPorObrigacaoPagar(selecionado);
        }
        return Lists.newArrayList();
    }

    public List<Liquidacao> getLiquidacoes() {
        if (selecionado.isObrigacaoPagarDepoisEmpenho()) {
            return facade.getLiquidacaoFacade().listaPorEmpenho(selecionado.getEmpenho());
        }
        return Lists.newArrayList();
    }

    public List<LiquidacaoEstorno> getEstornosLiquidacao() {
        if (selecionado.isObrigacaoPagarDepoisEmpenho()) {
            return facade.getLiquidacaoEstornoFacade().listaPorEmpenho(selecionado.getEmpenho());
        }
        return Lists.newArrayList();
    }

    public List<Pagamento> getPagamentos() {
        if (selecionado.isObrigacaoPagarDepoisEmpenho()) {
            return facade.getPagamentoFacade().listaPorEmpenho(selecionado.getEmpenho());
        }
        return Lists.newArrayList();
    }

    public List<PagamentoEstorno> getEstornosPagamento() {
        if (selecionado.isObrigacaoPagarDepoisEmpenho()) {
            return facade.getPagamentoEstornoFacade().listaPorEmpenho(selecionado.getEmpenho());
        }
        return Lists.newArrayList();
    }
}
