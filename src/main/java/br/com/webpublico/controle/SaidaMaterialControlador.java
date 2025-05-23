/*
 * Codigo gerado automaticamente em Fri Feb 24 09:19:35 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FormularioLocalEstoque;
import br.com.webpublico.entidadesauxiliares.FormularioLoteMaterial;
import br.com.webpublico.entidadesauxiliares.ItemSaidaMaterialVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SaidaMaterialFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.component.UIInput;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class SaidaMaterialControlador extends PrettyControlador<SaidaMaterial> implements Serializable, CRUD {

    @EJB
    protected SaidaMaterialFacade facade;
    protected ItemSaidaMaterial itemSaidaMaterialSelecionado;
    protected ItemSaidaMaterialVO itemSaidaMaterialVO;
    protected LocalEstoque localEstoque;
    protected GrupoMaterial grupoMaterial;
    protected HierarquiaOrganizacional hierarquiaOrcamentaria;
    protected HierarquiaOrganizacional hierarquiaAdministrativa;
    protected Material material;
    protected LoteMaterial loteMaterial;
    protected List<Material> materiais;
    protected List<ItemRequisicaoMaterial> itensRequisicao;
    protected List<FormularioLocalEstoque> formularioLocalEstoquesRecuperados;
    protected List<FormularioLocalEstoque> formularioLocalEstoquesSelecionados;
    protected List<FormularioLoteMaterial> formularioLoteMaterialRecuperados;
    protected List<FormularioLoteMaterial> formularioLoteMaterialSelecionados;
    protected List<ItemSaidaMaterialVO> itensSaidaMaterialVO;
    protected FormularioLocalEstoque localEstoqueSelecionado;

    public SaidaMaterialControlador() {
        super(SaidaMaterial.class);
        formularioLocalEstoquesRecuperados = new ArrayList<>();
        formularioLocalEstoquesSelecionados = new ArrayList<>();
        formularioLoteMaterialRecuperados = new ArrayList<>();
        formularioLoteMaterialSelecionados = new ArrayList<>();
        itensRequisicao = new ArrayList<>();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void novo() {
        super.novo();
        criarParametrosIniciaisGenericos();
    }

    protected void criarParametrosIniciaisGenericos() {
        selecionado.setUsuario(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataSaida(facade.getSistemaFacade().getDataOperacao());
    }

    @Override
    public void validarExclusaoEntidade() {
        super.validarExclusaoEntidade();
        ValidacaoException ve = new ValidacaoException();
        EntradaTransferenciaMaterial entrada = facade.recuperarEntradaDaSaida(selecionado);
        if (entrada != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Está saída de material possui vínculo com a entrada de material Nrº : " + entrada.getNumero() + ".");
        }
        ve.lancarException();
    }

    private void validarPin() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado instanceof SaidaDireta) {
            if (isNecessitaPin() && "".equals(selecionado.getPin())) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o Pin para continuar.");
            }
            if (isNecessitaPin() && facade.verificarSeExistePinCadastrado(selecionado.getPin())) {
                ve.adicionarMensagemDeCampoObrigatorio("Esse PIN já foi cadastrado, informe outro Pin para continuar.");
            }
        }
        ve.lancarException();
    }

    public boolean isNecessitaPin() {
        if (selecionado.getListaDeItemSaidaMaterial() != null && selecionado.getListaDeItemSaidaMaterial().isEmpty()) {
            for (ItemSaidaMaterial itemSaidaMaterial : selecionado.getListaDeItemSaidaMaterial()) {
                if (itemSaidaMaterial.getLocalEstoque().getUtilizarPin() != null && itemSaidaMaterial.getLocalEstoque().getUtilizarPin()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            atribuirLocalEstoqueOrcamentario();
            validarRegrasDeNegocio();
            definirQuantidadeDevolvida();
            validarPin();
            atribuirNumeroItemSaida();
            facade.validarEstoquePorItem(selecionado);
            selecionado = facade.salvarNovaSaidaMaterial(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            Web.limpaNavegacao();
            redirecionarParaVer();
        } catch (ValidacaoException vex) {
            FacesUtil.printAllFacesMessages(vex.getMensagens());
        } catch (ParseException ex) {
            logger.error(ex.getMessage());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void salvarSemMovimentar() {
        try {
            Util.validarCampos(selecionado);
            atribuirLocalEstoqueOrcamentario();
            atribuirNumeroItemSaida();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public void concluir() {
        try {
            atualizarValorUnitarioItemSaida();
            facade.validarRequisicaoMaterialBloqueadaSingleton(selecionado);
            selecionado = facade.concluirSaidaMaterial(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            Web.limpaNavegacao();
            redirecionarParaVer();
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            facade.desbloquearRequisicaoMaterialSingleton(selecionado);
            descobrirETratarException(ex);
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    public void preConcluir() {
        try {
            Util.validarCampos(selecionado);
            validarRegrasDeNegocio();
            validarMaterialDuplicado();
            facade.validarEstoquePorItem(selecionado);
            definirValorUnitarioComBaseEstoqueAtualizado();
            selecionado.setDataConclusao(facade.getSistemaFacade().getDataOperacao());
            validarDiferencaValorTotalComEstoqueAtual();
            FacesUtil.executaJavaScript("dlgConcluir.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void definirValorUnitarioComBaseEstoqueAtualizado() throws OperacaoEstoqueException {
        for (ItemSaidaMaterialVO ism : itensSaidaMaterialVO) {
            Estoque estoque = facade.getEstoqueFacade().recuperarEstoque(ism.getLocalEstoqueOrcamentario(), ism.getMaterial(), facade.getSistemaFacade().getDataOperacao());
            BigDecimal custoMedio = facade.calcularCustoMedio(estoque);
            ism.setValorUnitario(custoMedio);
        }
    }

    public void cancelarConcluir() {
        selecionado.setDataConclusao(null);
        FacesUtil.executaJavaScript("dlgConcluir.hide()");
    }

    public void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    protected void atribuirLocalEstoqueOrcamentario() {
        ValidacaoException ve = new ValidacaoException();
        for (ItemSaidaMaterial ism : selecionado.getListaDeItemSaidaMaterial()) {
            if (ism.getLocalEstoqueOrcamentario().getId() == null) {
                try {
                    ism.setLocalEstoqueOrcamentario(facade.getLocalEstoqueFacade().recuperarLocalEstoqueOrcamentario(ism.getLocalEstoque(), ism.getUnidadeOrcamentaria(), ism.getDataMovimento()));
                } catch (ExcecaoNegocioGenerica ex) {
                    ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_REALIZADA, ex.getMessage());
                } catch (ValidacaoException vx) {
                    ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_REALIZADA, "A unidade " + ism.getUnidadeOrcamentaria() + " não é orçamentária ou não está vigênte.");
                }
            }
        }
        ve.lancarException();
    }

    protected void definirQuantidadeDevolvida() {
        if (esteSelecionadoEInstanciaDeSaidaDevolucao()) {
            esteSelecionadoDevolucao().setarQuantidadeDevolvida();
        }
    }

    protected void novaListaDeItemSaida() {
        selecionado.setListaDeItemSaidaMaterial(new ArrayList<ItemSaidaMaterial>());
    }

    public void validarMaterialDuplicado() {
        Collections.sort(selecionado.getListaDeItemSaidaMaterial());
        Map<Material, Long> map = selecionado.getListaDeItemSaidaMaterial().stream().
            collect(Collectors.groupingBy(ItemSaidaMaterial::getMaterial, Collectors.counting()));

        ValidacaoException ve = new ValidacaoException();
        map.entrySet().stream()
            .filter(entry -> entry.getValue() > 1)
            .map(entry -> "O material <b>" + entry.getKey() + "</b>, está duplicado na lista de itens.")
            .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);

        ve.lancarException();
    }

    protected void validarRegrasDeNegocio() {
        ValidacaoException ve = new ValidacaoException();
        validarItensDaSaida(ve);
        validarMaterialLocalEstoqueEstaBloqueado(ve);
        ve.lancarException();
    }

    protected void validarItensDaSaida(ValidacaoException ve) {
        if (selecionado.getListaDeItemSaidaMaterial() == null || selecionado.getListaDeItemSaidaMaterial().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum item de saída foi informado.");
        }
        ve.lancarException();

        for (ItemSaidaMaterial ism : selecionado.getListaDeItemSaidaMaterial()) {
            if (!ism.getQuantidade().equals(BigDecimal.ZERO)) {
                if (ism.getLocalEstoque() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o local de estoque do item " + ism.getDescricao().toUpperCase() + ".");
                }

                if (ism.requerLote()) {
                    if (ism.getLoteMaterial() == null) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o lote do item " + ism.getDescricao().toUpperCase() + ".");

                    } else if (ism.loteVencidoNaDataDaSaida()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O lote " + ism.getLoteMaterial().getIdentificacao().toUpperCase() + ", do item " + ism.getDescricao().toUpperCase() + ", está vencido. Validade: " + formatarDateDDMMYYYY(ism.getLoteMaterial().getValidade()));
                    }
                }
            }
        }
        ve.lancarException();
    }

    protected void validarMaterialLocalEstoqueEstaBloqueado(ValidacaoException ve) {
        for (ItemSaidaMaterial itemSaidaMaterial : selecionado.getListaDeItemSaidaMaterial()) {
            if (facade.getEstoqueFacade().estoqueBloqueado(itemSaidaMaterial.getMaterial(), itemSaidaMaterial.getLocalEstoqueOrcamentario(), itemSaidaMaterial.getTipoEstoque(), selecionado.getDataSaida())) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_REALIZADA, "O local de estoque " + itemSaidaMaterial.getLocalEstoque().toStringAutoComplete().toUpperCase() + " está bloqueado.");
            }
        }
        ve.lancarException();
    }

    public SaidaRequisicaoMaterial getSaidaRequisicaoMaterial() {
        return (SaidaRequisicaoMaterial) selecionado;
    }

    public SaidaDevolucaoMaterial esteSelecionadoDevolucao() {
        return (SaidaDevolucaoMaterial) selecionado;
    }

    public SaidaDireta getEsteSelecionadoDireta() {
        return (SaidaDireta) selecionado;
    }

    public boolean esteSelecionadoEInstanciaDeSaidaDevolucao() {
        return selecionado instanceof SaidaDevolucaoMaterial;
    }

    public boolean esteSelecionadoEInstanciaDeSaidaInutilizaveis() {
        return selecionado != null && TipoSaidaMaterial.DESINCORPORACAO.equals(selecionado.getTipoDestaSaida());
    }

    public List<EntradaCompraMaterial> completaEntradaRequisicaoMaterial(String parte) {
        return facade.getEntradaMaterialFacade().recuperarEntradaPorRequisicao(parte.trim());
    }

    public List<LocalEstoque> completarLocalEstoqueOndeUsuarioLogadoEhGestor(String parte) {
        return facade.completaLocalEstoque(parte, facade.getSistemaFacade().getUsuarioCorrente());
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public LoteMaterial getLoteMaterial() {
        return loteMaterial;
    }

    public void setLoteMaterial(LoteMaterial loteMaterial) {
        this.loteMaterial = loteMaterial;
    }


    public void preencherListaDeItemSaidaDeEntrada(SelectEvent event) {
        EntradaMaterial entrada = (EntradaMaterial) event.getObject();
        novaListaDeItemSaida();
        preencherListaDeItemSaidaAPartirDeUmaEntrada(entrada, event);
    }

    public void preencherListaDeItemSaidaAPartirDeUmaEntrada(EntradaMaterial entrada, SelectEvent event) {
        entrada.setItens(facade.getEntradaMaterialFacade().recuperarItensEntradaMaterial(entrada));

        if (validarUsoDaEntradaSelecionada(entrada)) {
            esteSelecionadoDevolucao().setEntradaMaterial(entrada);
            novaListaDeItemSaida();
            criarItensSaidaComItensEntradaAdicionandoNaListaDoSelecionado();
        } else {
            ((UIInput) event.getComponent()).setValue(null);
        }
    }

    protected void criarItensSaidaComItensEntradaAdicionandoNaListaDoSelecionado() {
        for (ItemEntradaMaterial iem : esteSelecionadoDevolucao().getEntradaMaterial().getItens()) {
            selecionado.getListaDeItemSaidaMaterial().add(new ItemSaidaMaterial(selecionado, iem));
        }
    }

    public boolean exibirTableItensSaida() {
        if (selecionado == null) {
            return false;
        }
        if (selecionado.getListaDeItemSaidaMaterial() == null) {
            return false;
        }

        if (selecionado.getListaDeItemSaidaMaterial().isEmpty()) {
            return false;
        }

        if (esteSelecionadoEInstanciaDeSaidaInutilizaveis()) {
            return false;
        }

        return true;
    }

    protected boolean validarUsoDaEntradaSelecionada(EntradaMaterial entrada) {
        boolean validou = true;

        if (entrada.entradaDevolvidaTotalmente()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A entrada selecionada já foi totalmente devolvida.");
            validou = false;
        }

        if (!entrada.ehEntradaCompra()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A entrada selecionada não é oriunda de uma requisição de compra.");
            validou = false;
        }

        return validou;
    }

    public void setarMaterialEmFoco(ItemSaidaMaterial ism) {
        if (ism != null) {
            itemSaidaMaterialSelecionado = ism;
        }
    }

    public void removerItem(ItemSaidaMaterial ism) {
        selecionado.getListaDeItemSaidaMaterial().remove(ism);
    }

    public String formatarDateDDMMYYYY(Date data) {
        return DataUtil.getDataFormatada(data);
    }

    public ItemSaidaMaterial getItemSaidaMaterialSelecionado() {
        return itemSaidaMaterialSelecionado;
    }

    public void setItemSaidaMaterialSelecionado(ItemSaidaMaterial itemSaidaMaterialSelecionado) {
        this.itemSaidaMaterialSelecionado = itemSaidaMaterialSelecionado;
    }

    public void atribuirUnidadeAdministrativaLocalEstoque() {
        if (localEstoque != null) {
            setHierarquiaAdministrativa(getHierarquiaDaUnidade(localEstoque.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
        }
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentariaPorUnidadeAdmnistrativaDoLocalEstoque(String parte) {
        UnidadeOrganizacional unidadeAdm = null;
        if (selecionado.isSaidaConsumo()) {
            SaidaRequisicaoMaterial saidaRequisicaoMaterial = (SaidaRequisicaoMaterial) selecionado;
            unidadeAdm = saidaRequisicaoMaterial.getRequisicaoMaterial().getLocalEstoqueOrigem().getUnidadeOrganizacional();
        } else {
            if (localEstoque != null && localEstoque.getUnidadeOrganizacional() != null) {
                unidadeAdm = localEstoque.getUnidadeOrganizacional();
            }
        }
        if (unidadeAdm != null) {
            return facade.getHierarquiaOrganizacionalFacade().buscarFiltrandoHierarquiaOrcamentariaPorUnidadeAdministrativa(
                parte.trim(),
                unidadeAdm,
                facade.getSistemaFacade().getDataOperacao());
        }
        return new ArrayList<>();
    }

    public void validarCampaLocalEstoque() {
        ValidacaoException ve = new ValidacaoException();
        if (localEstoque == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Local de Estoque deve ser informado.");
        }
        ve.lancarException();
    }

    public List<Material> completarMaterialPermitidoNaHierarquiaDoLocalEstoque(String parte) {
        if (localEstoque != null) {
            return facade.getMaterialFacade().completarMaterialPermitidoNaHierarquiaDoLocalEstoque(null, localEstoque, parte, StatusMaterial.getSituacoesDeferidoInativo());
        }
        return null;
    }

    public String retornarDescricaoDoEnumTipoEstoque(String tipo) {
        return TipoEstoque.valueOf(tipo).getDescricao();
    }

    public String calcularTotalNaHierarquia() {
        try {
            BigDecimal qtde = BigDecimal.ZERO;
            for (FormularioLocalEstoque form : formularioLocalEstoquesRecuperados) {
                qtde = qtde.add(form.getFisico());
            }
            return Util.formataQuandoDecimal(qtde, material.getMascaraQuantidade());
        } catch (NullPointerException nu) {
            return "0,00";
        }
    }

    public String calcularTotalQuantidadeDesejadaNosFormulariosDeLote() {
        BigDecimal qtde = BigDecimal.ZERO;
        try {
            for (FormularioLoteMaterial lote : formularioLoteMaterialSelecionados) {
                qtde = qtde.add(lote.getQuantidadeSaida());
            }
            return Util.formataQuandoDecimal(qtde, material.getMascaraQuantidade());
        } catch (NullPointerException nu) {
            return "0,00";
        }
    }

    public String calcularTotalQuantidadeDisponivelEmLotePorLocalEstoque() {
        BigDecimal qtde = BigDecimal.ZERO;
        try {
            for (FormularioLoteMaterial lote : formularioLoteMaterialRecuperados) {
                qtde = qtde.add(lote.getQuantidadeLote());
            }
            return Util.formataQuandoDecimal(qtde, material.getMascaraQuantidade());
        } catch (NullPointerException nu) {
            return "0,00";
        }
    }

    public String getTotalQuantidadeDesejada() {
        try {
            BigDecimal qtde = BigDecimal.ZERO;
            for (FormularioLocalEstoque form : formularioLocalEstoquesRecuperados) {
                qtde = qtde.add(form.getQtdeDesejada());
            }
            return Util.formataQuandoDecimal(qtde, material.getMascaraQuantidade());
        } catch (NullPointerException nu) {
            return "0,00";
        }
    }

    public Boolean isMostrarBotaoSelecionar(FormularioLocalEstoque localEstoque) {
        return !formularioLocalEstoquesSelecionados.contains(localEstoque);
    }

    public Boolean isMostrarBotaoSelecionarLotes(FormularioLoteMaterial loteMaterial) {
        return formularioLoteMaterialSelecionados != null &&
            loteMaterial != null &&
            !formularioLoteMaterialSelecionados.contains(loteMaterial);
    }

    public void desmarcarLocal(FormularioLocalEstoque local) {
        local.getListaLotes().clear();
        local.setQtdeDesejada(BigDecimal.ZERO);
        formularioLocalEstoquesSelecionados.remove(local);
    }

    public void selecionarLote(FormularioLoteMaterial loteMaterial) {
        formularioLoteMaterialSelecionados.add(loteMaterial);
    }

    public void desmarcarLote(FormularioLoteMaterial lote) {
        formularioLoteMaterialSelecionados.remove(lote);
        lote.setQuantidadeSaida(BigDecimal.ZERO);
    }

    public Boolean isSelecionadoLote(FormularioLoteMaterial obj) {
        boolean retorno = Boolean.FALSE;
        for (FormularioLoteMaterial loteSelecionado : formularioLoteMaterialSelecionados) {
            if (loteSelecionado.equals(obj)) {
                retorno = Boolean.TRUE;
                break;
            }
        }
        return retorno;
    }

    public void cancelarLote() {
        if (formularioLoteMaterialSelecionados != null) {
            formularioLoteMaterialSelecionados.clear();
        } else {
            formularioLoteMaterialSelecionados = new ArrayList<>();
        }
    }

    public void limparCampos() {
        setLocalEstoque(null);
        setLoteMaterial(null);
        setMaterial(null);
        formularioLocalEstoquesSelecionados = new ArrayList<>();
        formularioLocalEstoquesRecuperados = new ArrayList<>();
        setHierarquiaOrcamentaria(null);
    }

    public FormularioLocalEstoque getLocalEstoqueSelecionado() {
        return localEstoqueSelecionado;
    }

    public void setLocalEstoqueSelecionado(FormularioLocalEstoque localEstoqueSelecionado) {
        this.localEstoqueSelecionado = localEstoqueSelecionado;
    }

    public List<FormularioLoteMaterial> getFormularioLoteMaterialSelecionados() {
        return formularioLoteMaterialSelecionados;
    }

    public void setFormularioLoteMaterialSelecionados(List<FormularioLoteMaterial> formularioLoteMaterialSelecionados) {
        this.formularioLoteMaterialSelecionados = formularioLoteMaterialSelecionados;
    }

    public List<FormularioLoteMaterial> getFormularioLoteMaterialRecuperados() {
        return formularioLoteMaterialRecuperados;
    }

    public void setFormularioLoteMaterialRecuperados(List<FormularioLoteMaterial> formularioLoteMaterialRecuperados) {
        this.formularioLoteMaterialRecuperados = formularioLoteMaterialRecuperados;
    }

    public List<FormularioLocalEstoque> getFormularioLocalEstoquesSelecionados() {
        return formularioLocalEstoquesSelecionados;
    }

    public void setFormularioLocalEstoquesSelecionados(List<FormularioLocalEstoque> formularioLocalEstoquesSelecionados) {
        this.formularioLocalEstoquesSelecionados = formularioLocalEstoquesSelecionados;
    }

    public List<FormularioLocalEstoque> getFormularioLocalEstoquesRecuperados() {
        return formularioLocalEstoquesRecuperados;
    }

    public void setFormularioLocalEstoquesRecuperados(List<FormularioLocalEstoque> formularioLocalEstoquesRecuperados) {
        this.formularioLocalEstoquesRecuperados = formularioLocalEstoquesRecuperados;
    }

    public String calcularQuantidadeTotalItensDeSaida() {
        try {
            BigDecimal total = BigDecimal.ZERO;
            TipoMascaraUnidadeMedida tipoMascaraUnidadeMedida = material != null ? material.getMascaraQuantidade() : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
            for (ItemSaidaMaterial itemSaidaMaterial : selecionado.getListaDeItemSaidaMaterial()) {
                total = total.add(itemSaidaMaterial.getQuantidade());
            }
            return Util.formataQuandoDecimal(total, tipoMascaraUnidadeMedida);
        } catch (ArithmeticException ex) {
            return "0.00";
        }
    }

    public BigDecimal calcularValorTotalSaida() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemSaidaMaterial itemSaidaMaterial : selecionado.getListaDeItemSaidaMaterial()) {
            total = total.add(itemSaidaMaterial.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorTotalItemSaidaVo() {
        BigDecimal total = BigDecimal.ZERO;
        if (!Util.isListNullOrEmpty(getItensSaidaMaterialVO())) {
            for (ItemSaidaMaterialVO item : getItensSaidaMaterialVO()) {
                total = total.add(item.getValorTotal());
            }
        }
        return total;
    }


    public List<FormularioLocalEstoque> getLocaisEstoquePermitidoMovimentacoes(Material material, List<HierarquiaOrganizacional> unidadesOrcamentarias) {
        List<FormularioLocalEstoque> locais = Lists.newArrayList();

        for (HierarquiaOrganizacional hoOrc : unidadesOrcamentarias) {
            List<FormularioLocalEstoque> locaisRecurados = facade.getLocalEstoqueFacade().recuperarLocaisEstoquesPermitidosMovimentacoes(
                localEstoque,
                Lists.newArrayList(material.getId()),
                grupoMaterial,
                facade.getSistemaFacade().getUsuarioCorrente(),
                hoOrc.getSubordinada(),
                facade.getSistemaFacade().getDataOperacao());
            locais.addAll(locaisRecurados);
        }
        return locais;
    }

    public void preencherItemSaidaVoParaVisualizacao() {
        try {
            itensSaidaMaterialVO = Lists.newArrayList();
            for (ItemSaidaMaterial itemSaida : selecionado.getListaDeItemSaidaMaterial()) {
                ItemSaidaMaterialVO itemVo = new ItemSaidaMaterialVO();
                itemVo.setNumero(itemSaida.getNumeroItem());
                itemVo.setMaterial(itemSaida.getMaterial());
                itemVo.setHierarquiaAdm(getHierarquiaDaUnidade(itemSaida.getLocalEstoqueOrcamentario().getLocalEstoque().getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
                itemVo.setHierarquiaOrc(getHierarquiaDaUnidade(itemSaida.getUnidadeOrcamentaria(), TipoHierarquiaOrganizacional.ORCAMENTARIA));
                itemVo.setLocalEstoque(itemSaida.getLocalEstoque());
                itemVo.setLocalEstoqueOrcamentario(itemSaida.getLocalEstoqueOrcamentario());
                itemVo.setLoteMaterial(itemSaida.getLoteMaterial());
                itemVo.setItemSaidaMaterial(itemSaida);
                itemVo.setObservacao(itemSaida.getItemSaidaDesincorporacao() != null ? itemSaida.getItemSaidaDesincorporacao().getObservacao() : null);


                itemVo.setQuantidade(itemSaida.getQuantidade());
                if (selecionado.getSituacao().isEmElaboracao()) {
                    Estoque estoque = gestEstoqueAtual(itemVo);
                    itemVo.setValorEstoqueAtual(estoque.getFinanceiro());
                    itemVo.setQuantidadeEstoqueAtual(estoque.getFisico());
                    itemVo.setValorUnitario(facade.calcularCustoMedio(estoque));
                    itemVo.calcularValorTotal();
                } else {
                    itemVo.setValorUnitario(itemSaida.getValorUnitario());
                    itemVo.setValorTotal(itemSaida.getValorTotal());
                }
                itensSaidaMaterialVO.add(itemVo);
            }
            Collections.sort(itensSaidaMaterialVO);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private Estoque gestEstoqueAtual(ItemSaidaMaterialVO itemVo) {
        try {
            return facade.getEstoqueFacade().recuperarEstoque(itemVo.getLocalEstoqueOrcamentario(), itemVo.getMaterial(), facade.getSistemaFacade().getDataOperacao());
        } catch (OperacaoEstoqueException e) {
            throw new ValidacaoException("Estoque não encontrado para o mateiral " + itemVo.getMaterial() + ".");
        }
    }

    public void validarDiferencaValorTotalComEstoqueAtual() {
        ValidacaoException ve = new ValidacaoException();
        for (ItemSaidaMaterialVO item : itensSaidaMaterialVO) {
            if (item.getDiferencaFinanceiroEstoqueNoUltimoMovimento()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existe uma diferença no Valor Total (Quantidade x Valor Unitário) com o valor do Estoque. Essa diferença acontece devido aos arredondamentos feito por saídas parciais do material.");
                ve.adicionarMensagemDeOperacaoNaoPermitida("", "Ajuste o campo valor total para ficar igual ao valor do estoque.");
            }
        }
        ve.lancarException();
    }

    public List<HierarquiaOrganizacional> getOrcamentariasDaAdministrativa(UnidadeOrganizacional unidadeAdm) {
        return facade.getHierarquiaOrganizacionalFacade().buscarFiltrandoHierarquiaOrcamentariaPorUnidadeAdministrativa(
            "",
            unidadeAdm,
            facade.getSistemaFacade().getDataOperacao());
    }

    public List<Material> completarMaterialPorLocalEstoque(String parte) {
        if (localEstoque != null) {
            return facade.getMaterialFacade().completarMaterialNaHierarquiaDoLocalEstoquePorDataAndComEstoque(localEstoque, grupoMaterial, parte, StatusMaterial.getSituacoesDeferidoInativo());
        }
        return Lists.newArrayList();
    }

    public Boolean hasItemSelecionado(List<ItemSaidaMaterialVO> itensSaidaVo) {
        return itensSaidaVo.stream().anyMatch(ItemSaidaMaterialVO::hasQuantidade);
    }

    public HierarquiaOrganizacional getHierarquiaDaUnidade(UnidadeOrganizacional unidade, TipoHierarquiaOrganizacional tipoHo) {
        return facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            tipoHo.name(),
            unidade,
            selecionado.getDataSaida());
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    protected void atribuirNumeroItemSaida() {
        Iterator<ItemSaidaMaterial> itens = selecionado.getListaDeItemSaidaMaterial().iterator();
        while (itens.hasNext()) {
            ItemSaidaMaterial proximo = itens.next();
            int i = selecionado.getListaDeItemSaidaMaterial().indexOf(proximo);
            proximo.setNumeroItem(i + 1);
            Util.adicionarObjetoEmLista(selecionado.getListaDeItemSaidaMaterial(), proximo);
        }
    }

    private void atualizarValorUnitarioItemSaida() {
        selecionado.getListaDeItemSaidaMaterial().forEach(itemSaida -> {
            itensSaidaMaterialVO.stream()
                .filter(itemVo -> itemVo.getItemSaidaMaterial().equals(itemSaida)).forEach(itemVo -> {
                    itemSaida.setValorUnitario(itemVo.getValorUnitario());
                    itemSaida.setValorTotal(itemVo.getValorTotal());
                });
        });
    }

    public List<ItemRequisicaoMaterial> getItensRequisicao() {
        return itensRequisicao;
    }

    public void setItensRequisicao(List<ItemRequisicaoMaterial> itensRequisicao) {
        this.itensRequisicao = itensRequisicao;
    }

    public ItemSaidaMaterialVO getItemSaidaMaterialVO() {
        return itemSaidaMaterialVO;
    }

    public void setItemSaidaMaterialVO(ItemSaidaMaterialVO itemSaidaMaterialVO) {
        this.itemSaidaMaterialVO = itemSaidaMaterialVO;
    }

    public List<ItemSaidaMaterialVO> getItensSaidaMaterialVO() {
        return itensSaidaMaterialVO;
    }

    public void setItensSaidaMaterialVO(List<ItemSaidaMaterialVO> itensSaidaMaterialVO) {
        this.itensSaidaMaterialVO = itensSaidaMaterialVO;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public List<Material> getMateriais() {
        return materiais;
    }

    public void setMateriais(List<Material> materiais) {
        this.materiais = materiais;
    }
}
