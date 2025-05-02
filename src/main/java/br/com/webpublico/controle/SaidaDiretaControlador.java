package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FormularioLocalEstoque;
import br.com.webpublico.entidadesauxiliares.FormularioLoteMaterial;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoIngressoBaixa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 17/04/14
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "saidaDiretaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSaidaDireta", pattern = "/saida-direta/novo/", viewId = "/faces/administrativo/materiais/saidaMaterial/direta/edita.xhtml"),
    @URLMapping(id = "editarSaidaDireta", pattern = "/saida-direta/editar/#{saidaDiretaControlador.id}/", viewId = "/faces/administrativo/materiais/saidaMaterial/direta/edita.xhtml"),
    @URLMapping(id = "listarSaidaDireta", pattern = "/saida-direta/listar/", viewId = "/faces/administrativo/materiais/saidaMaterial/direta/lista.xhtml"),
    @URLMapping(id = "verSaidaDireta", pattern = "/saida-direta/ver/#{saidaDiretaControlador.id}/", viewId = "/faces/administrativo/materiais/saidaMaterial/direta/visualizar.xhtml")
})
public class SaidaDiretaControlador extends SaidaMaterialControlador {

    private BigDecimal quantidadeDisponivel;

    public SaidaDiretaControlador() {
        super();
        metadata = new EntidadeMetaData(SaidaDireta.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/saida-direta/";
    }

    @Override
    @URLAction(mappingId = "novaSaidaDireta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        novoItemSaidaDireta();
    }

    private void novoItemSaidaDireta() {
        itemSaidaMaterialSelecionado = new ItemSaidaMaterial(selecionado, new ItemSaidaDireta());
    }

    @Override
    @URLAction(mappingId = "editarSaidaDireta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verSaidaDireta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            getEsteSelecionadoDireta().realizarValidacoes();
            if (getEsteSelecionadoDireta().temMaterialMedicoHospitalar()) {
                FacesUtil.executaJavaScript("infoadicional.show()");
                FacesUtil.atualizarComponente("formDlgMedicoHosp");
            } else {
                super.salvar();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void cancelarOperacaoMaterialHospitalar() {
        getEsteSelecionadoDireta().setCrm(null);
        getEsteSelecionadoDireta().setCns(null);
        getEsteSelecionadoDireta().setObservacao(null);
    }

    public void salvarVerificandoMaterialHospitalar() {
        try {
            validarSaidaDiretaPorMaterialHospitalar();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarSaidaDiretaPorMaterialHospitalar() {
        if (getEsteSelecionadoDireta().temMaterialMedicoHospitalar()) {
            ValidacaoException ve = new ValidacaoException();
            validarCrm(ve);
            validarCns(ve);
            if (Util.isStringNulaOuVazia(getEsteSelecionadoDireta().getObservacao())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Observação deve ser informado.");
            }
            ve.lancarException();
        }
    }

    private void validarCns(ValidacaoException ve) {
        if (Util.isStringNulaOuVazia(getEsteSelecionadoDireta().getCns())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo CNS (Número do Cartão do SUS) deve ser informado.");
        }
        ve.lancarException();
        if (!getEsteSelecionadoDireta().cnsEhValida()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O CNS (Número do Cartão do SUS) informado é inválido.");
        }
        ve.lancarException();
    }

    private void validarCrm(ValidacaoException ve) {
        if (Util.isStringNulaOuVazia(getEsteSelecionadoDireta().getCrm())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo CRM deve ser informado.");
        }
        ve.lancarException();
        if (!getEsteSelecionadoDireta().crmEhValido()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O CRM informado é inválido.");
        }
        ve.lancarException();
    }

    private void validarItemSaidaMaterial() {
        ValidacaoException ve = new ValidacaoException();
        if (itemSaidaMaterialSelecionado.getItemSaidaDireta().getMaterial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Material deve ser informado.");

        } else if (itemSaidaMaterialSelecionado.requerLote() && itemSaidaMaterialSelecionado.getLoteMaterial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Lote deve ser informado.");
        }
        if (getLocalEstoque() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Local de Estoque deve ser informado.");
        }
        if (itemSaidaMaterialSelecionado.getQuantidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Quantidade que Saiu deve ser informado.");

        } else if (itemSaidaMaterialSelecionado.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade de saída deve ser maior que zero (0).");
        }
        ve.lancarException();
    }

    public boolean getTemMaterialMedicoHospitalar() {
        return getEsteSelecionadoDireta().temMaterialMedicoHospitalar();
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    private void validarSeMaterialJaFoiAdicionado(ValidacaoException ve) {
        if (selecionado.getListaDeItemSaidaMaterial() != null && !selecionado.getListaDeItemSaidaMaterial().isEmpty()) {
            for (ItemSaidaMaterial itens : selecionado.getListaDeItemSaidaMaterial()) {
                if (itens.getItemSaidaDireta().getMaterial().equals(material)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O material " + material.getDescricao() + " já foi adicionado na lista.");
                }
                if (itens.getMaterial().equals(material)
                    && facade.getLocalEstoqueFacade().recuperaRaiz(itens.getLocalEstoqueOrcamentario().getLocalEstoque()).equals(localEstoque)
                    && itens.getLocalEstoqueOrcamentario().getUnidadeOrcamentaria().equals(hierarquiaOrcamentaria.getSubordinada())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O material já foi adicionado na lista para saída do local de estoque " + localEstoque.toString() + ". ");
                }
            }
        }
        ve.lancarException();
    }

    private void validarFiltrosIniciaisParaConsultaDeLocaisDeEstoque() {
        ValidacaoException ve = new ValidacaoException();
        if (localEstoque == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Local de Estoque deve ser informado.");
        }
        if (material == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Material deve ser informado.");
        }
        if (hierarquiaOrcamentaria == null) {
            ve.adicionarMensagemDeCampoObrigatorio("o campo Unidade Orçamentária deve ser informado.");
        }
        ve.lancarException();
        validarSeMaterialJaFoiAdicionado(ve);
    }

    public void recuperarLocaisDeEstoqueFilhos() {
        try {
            validarFiltrosIniciaisParaConsultaDeLocaisDeEstoque();
            setFormularioLocalEstoquesRecuperados(facade.getLocalEstoqueFacade().recuperarLocaisEstoquesPermitidosMovimentacoes(
                localEstoque,
                Lists.newArrayList(material.getId()),
                null,
                facade.getSistemaFacade().getUsuarioCorrente(),
                hierarquiaOrcamentaria.getSubordinada(),
                facade.getSistemaFacade().getDataOperacao()));

            HashSet<Material> materiais = new HashSet<>(Lists.newArrayList(material));
            quantidadeDisponivel = facade.getLocalEstoqueFacade().getNovoConsolidadorComReservaEstoque(localEstoque, materiais, facade.getSistemaFacade().getDataOperacao()).getFisicoConsolidado(material);
            quantidadeDisponivel = quantidadeDisponivel.subtract(calcularQuantidaDeMaterialSelecionadoPorLocal(material, localEstoque));
            FacesUtil.executaJavaScript("dialogLocalEstoque.show()");
            FacesUtil.atualizarComponente("formDlgEstoque");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarFormulariosLocalEstoque(ValidacaoException ve) {
        if (formularioLocalEstoquesSelecionados == null || formularioLocalEstoquesSelecionados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um local de estoque de armazenamento.");
        }
        ve.lancarException();
        for (FormularioLocalEstoque localSelecionado : formularioLocalEstoquesSelecionados) {
            if (!localSelecionado.getControleLote()) {
                BigDecimal quantidadeEmEstoque = localSelecionado.getFisico();
                BigDecimal quantidadeDesejada = localSelecionado.getQtdeDesejada();
                if (quantidadeDesejada == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Informe a quantidade desejada para o local de estoque " + localSelecionado.getDescricaLocalEstoque() + ".");
                }
                ve.lancarException();
                if (quantidadeDesejada.compareTo(quantidadeEmEstoque) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade em estoque é menor do que a desejada no local de estoque " + facade.getLocalEstoqueFacade().recuperar(localSelecionado.getIdLocalEstoque().longValue()).toString() + ".");

                } else if (quantidadeDesejada.compareTo(BigDecimal.ZERO) == 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Informe a quantidade desejada no local de estoque " + localSelecionado.getDescricaLocalEstoque() + ".");

                } else if (quantidadeDesejada.compareTo(quantidadeDisponivel) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade disponível é menor do que a desejada.");
                }
            }
        }
        ve.lancarException();
    }

    public void processarItemSaida() {
        try {
            validarItemSaida();
            for (FormularioLocalEstoque localSelecionado : formularioLocalEstoquesSelecionados) {
                if (localSelecionado.getControleLote()) {
                    for (FormularioLoteMaterial lote : localSelecionado.getListaLotes()) {
                        BigDecimal quantidadeDesejada = lote.getQuantidadeSaida();
                        LocalEstoque localEstoque = facade.getLocalEstoqueFacade().recuperar(lote.getIdLocalEstoque());
                        itemSaidaMaterialSelecionado.setQuantidade(quantidadeDesejada);
                        itemSaidaMaterialSelecionado.getItemSaidaDireta().setMaterial(material);
                        itemSaidaMaterialSelecionado.setLoteMaterial(facade.getLoteMaterialFacade().recuperar(lote.getIdLote()));
                        itemSaidaMaterialSelecionado.setLocalEstoqueOrcamentario(facade.getLocalEstoqueFacade().recuperarLocalEstoqueOrcamentario(localEstoque, hierarquiaOrcamentaria.getSubordinada(), facade.getSistemaFacade().getDataOperacao()));
                        setLocalEstoque(localEstoque);
                        adicionarItem();
                    }
                } else {
                    BigDecimal quantidadeDesejada = localSelecionado.getQtdeDesejada();
                    itemSaidaMaterialSelecionado.setQuantidade(quantidadeDesejada);
                    itemSaidaMaterialSelecionado.getItemSaidaDireta().setMaterial(material);
                    itemSaidaMaterialSelecionado.setLoteMaterial(null);
                    itemSaidaMaterialSelecionado.setLocalEstoqueOrcamentario(facade.getLocalEstoqueFacade().recuperarLocalEstoqueOrcamentario(facade.getLocalEstoqueFacade().recuperar(localSelecionado.getIdLocalEstoque().longValue()), hierarquiaOrcamentaria.getSubordinada(), facade.getSistemaFacade().getDataOperacao()));
                    setLocalEstoque(facade.getLocalEstoqueFacade().recuperar(localSelecionado.getIdLocalEstoque()));
                    adicionarItem();
                }
            }
            super.limparCampos();
            FacesUtil.executaJavaScript("dialogLocalEstoque.hide()");
            FacesUtil.atualizarComponente("Formulario:tabViewPrincipal:panelItensSaida");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarItemSaida() {
        ValidacaoException ve = new ValidacaoException();
        validarFormulariosLocalEstoque(ve);
        validarLoteMaterialDoFormulariosLocalEstoque(ve);
    }


    public void adicionarItem() {
        try {
            validarItemSaidaMaterial();
            itemSaidaMaterialSelecionado.setSaidaMaterial(selecionado);
            itemSaidaMaterialSelecionado.getItemSaidaDireta().setItemSaidaMaterial(itemSaidaMaterialSelecionado);
            itemSaidaMaterialSelecionado.calcularValorTotal();
            selecionado.getListaDeItemSaidaMaterial().add(itemSaidaMaterialSelecionado);
            itemSaidaMaterialSelecionado = new ItemSaidaMaterial(selecionado, new ItemSaidaDireta());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void cancelarItens() {
        novoItemSaidaDireta();
        limparCampos();
        if (formularioLocalEstoquesSelecionados != null) {
            formularioLocalEstoquesSelecionados.clear();
        }
        FacesUtil.executaJavaScript("dialogLocalEstoque.hide()");
        FacesUtil.atualizarComponente("Formulario:tabViewPrincipal:panelItensSaida");
    }


    public void limparCampos() {
        setMaterial(null);
        formularioLocalEstoquesSelecionados = new ArrayList<>();
        formularioLocalEstoquesRecuperados = new ArrayList<>();
    }

    public BigDecimal calcularQuantidaDeMaterialSelecionadoPorLocal(Material material, LocalEstoque localEstoque) {
        LocalEstoque pai = facade.getLocalEstoqueFacade().recuperaRaiz(localEstoque);
        BigDecimal qtde = BigDecimal.ZERO;
        for (ItemSaidaMaterial ism : selecionado.getListaDeItemSaidaMaterial()) {
            if (ism.getMaterial().equals(material) &&
                facade.getLocalEstoqueFacade().recuperaRaiz(ism.getLocalEstoqueOrcamentario().getLocalEstoque()).equals(pai)) {
                qtde = qtde.add(ism.getQuantidade());
            }
        }
        return qtde;
    }


    public void setarFormularioLocalEstoqueSelecionado(FormularioLocalEstoque formularioLocalEstoque) {
        formularioLoteMaterialRecuperados = null;
        localEstoqueSelecionado = formularioLocalEstoque;
        if (formularioLocalEstoque != null) {
            formularioLoteMaterialRecuperados = facade.getLoteMaterialFacade().buscarLoteMaterialPorMaterialAndLocalEstoqueAndOrcamentariaAndFiltro(formularioLocalEstoque.getIdLocalEstoque(), material.getId(), hierarquiaOrcamentaria.getSubordinada().getId(), "");
        }
    }

    private void validarFormulariosLoteMaterial() {
        ValidacaoException ve = new ValidacaoException();
        if (formularioLoteMaterialSelecionados == null || formularioLoteMaterialSelecionados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um lote do material de armazenamento.");
        }
        ve.lancarException();
        for (FormularioLoteMaterial formularioLote : formularioLoteMaterialSelecionados) {
            BigDecimal quantidadeEmEstoque = formularioLote.getQuantidadeLote();
            BigDecimal quantidadeDesejada = formularioLote.getQuantidadeSaida();

            if (quantidadeDesejada == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe a quantidade desejada do lote " + formularioLote.getIdentificacao());
            }
            ve.lancarException();
            if (quantidadeDesejada.compareTo(quantidadeEmEstoque) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade do lote " + formularioLote.getIdentificacao() + " é menor do que a desejada");

            } else if (quantidadeDesejada.compareTo(BigDecimal.ZERO) == 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Informe a quantidade desejada do lote " + formularioLote.getIdentificacao());

            } else if (quantidadeDesejada.compareTo(quantidadeDisponivel) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O lote " + formularioLote.getIdentificacao() + " não tem a quantidade desejada disponível.");
            }
        }
        ve.lancarException();
    }

    private void validarLoteMaterialDoFormulariosLocalEstoque(ValidacaoException ve) {

        if (material.getControleDeLote()) {
            for (FormularioLocalEstoque local : formularioLocalEstoquesSelecionados) {
                if (local.getListaLotes() == null || local.getListaLotes().isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Informe os lote(s) de Material(is) para o local de estoque " + local.getDescricaLocalEstoque() + ".");
                }
                ve.lancarException();

                for (FormularioLoteMaterial formularioLote : local.getListaLotes()) {
                    BigDecimal quantidadeEmEstoque = formularioLote.getQuantidadeLote();
                    BigDecimal quantidadeDesejada = formularioLote.getQuantidadeSaida();
                    if (quantidadeDesejada == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("Informe a quantidade desejada do lote " + formularioLote.getIdentificacao() + ".");

                    } else if (quantidadeDesejada.compareTo(quantidadeEmEstoque) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade do lote " + formularioLote.getIdentificacao() + " é menor do que a desejada.");

                    } else if (quantidadeDesejada.compareTo(BigDecimal.ZERO) == 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Informe a quantidade desejada do lote " + formularioLote.getIdentificacao() + ".");

                    } else if (quantidadeDesejada.compareTo(quantidadeDisponivel) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O lote " + formularioLote.getIdentificacao() + " não tem a quantidade desejada disponível.");

                    }
                }
            }
        }
        ve.lancarException();
    }

    public void processarItemSaidaMaterialPorLoteSelecionado() {
        try {
            validarFormulariosLoteMaterial();

            localEstoqueSelecionado.getListaLotes().addAll(formularioLoteMaterialSelecionados);
            formularioLoteMaterialSelecionados.clear();
            FacesUtil.executaJavaScript("dialogEstoqueLotes.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void selecionar(FormularioLocalEstoque localEstoque) {
        formularioLocalEstoquesSelecionados.add(localEstoque);
        setarFormularioLocalEstoqueSelecionado(localEstoque);
        if (localEstoque.getControleLote()) {
            FacesUtil.executaJavaScript("dialogEstoqueLotes.show()");
        }
        FacesUtil.atualizarComponente("formDlgMaterial");
    }

    public List<TipoBaixaBens> completarTipoBaixaPorConsumo(String parte) {
        return facade.getTipoBaixaBensFacade().buscarFiltrandoPorTipoBemAndTipoIngresso(parte.trim(), TipoBem.ESTOQUE, TipoIngressoBaixa.CONSUMO);
    }

    public void gerarRelatorioSaidaDiretaMateriais() {
        try {
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            abstractReport.setGeraNoDialog(true);
            String arquivoJasper = "RelatorioSaidaDiretaMateriais.jasper";
            HashMap parameters = new HashMap();
            parameters.put("NOME_RELATORIO", "RELATÓRIO DE SAÍDA DIRETA DE MATERIAIS");
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            parameters.put("BRASAO", abstractReport.getCaminhoImagem());
            parameters.put("MODULO", "MATERIAIS");
            parameters.put("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome());
            abstractReport.gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, new JRBeanCollectionDataSource(facade.buscarDadosRelatorioSaidaDiretaMateriais(selecionado)));
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            logger.error(ex.getMessage());
        }
    }
}
