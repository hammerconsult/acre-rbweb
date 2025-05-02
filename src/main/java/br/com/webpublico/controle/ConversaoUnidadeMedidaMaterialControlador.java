package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoMovimentoMaterial;
import br.com.webpublico.enums.StatusMaterial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConvercaoUnidadeMedidaMaterialFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-conversao", pattern = "/conversao-unidade-medida-material/novo/", viewId = "/faces/administrativo/materiais/conversao-unid-med-mat/edita.xhtml"),
    @URLMapping(id = "editar-conversao", pattern = "/conversao-unidade-medida-material/editar/#{conversaoUnidadeMedidaMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/conversao-unid-med-mat/edita.xhtml"),
    @URLMapping(id = "listar-conversao", pattern = "/conversao-unidade-medida-material/listar/", viewId = "/faces/administrativo/materiais/conversao-unid-med-mat/lista.xhtml"),
    @URLMapping(id = "ver-conversao", pattern = "/conversao-unidade-medida-material/ver/#{conversaoUnidadeMedidaMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/conversao-unid-med-mat/visualizar.xhtml")
})
public class ConversaoUnidadeMedidaMaterialControlador extends PrettyControlador<ConversaoUnidadeMedidaMaterial> implements Serializable, CRUD {

    @EJB
    private ConvercaoUnidadeMedidaMaterialFacade facade;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private ItemConversaoUnidadeMedidaMaterial itemConversao;

    public ConversaoUnidadeMedidaMaterialControlador() {
        super(ConversaoUnidadeMedidaMaterial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/conversao-unidade-medida-material/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "nova-conversao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setSituacao(SituacaoMovimentoMaterial.EM_ELABORACAO);
        selecionado.setDataMovimento(DataUtil.getDataComHoraAtual(facade.getSistemaFacade().getDataOperacao()));
        selecionado.setUsuario(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @Override
    @URLAction(mappingId = "editar-conversao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        if (selecionado.getSituacao().isConcluida()) {
            FacesUtil.addOperacaoNaoPermitida("Esta conversão encontra-se concluída e não poderá sofrer alterações.");
            redirecionarParaVer();
        }
        setHierarquiaAdministrativa(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            selecionado.getUnidadeAdministrativa(),
            selecionado.getDataMovimento()));
        atrualizarEstoque();
    }

    private void atrualizarEstoque() {
        for (ItemConversaoUnidadeMedidaMaterial itemConversao : selecionado.getItens()) {
            itemConversao.setEstoqueSaida(getEstoque(itemConversao.getMaterialSaida()));
        }
    }

    @URLAction(mappingId = "ver-conversao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        atrualizarEstoque();
    }

    @Override
    public void salvar() {
        try {
            validarRegrasEspecificas();
            selecionado.ordernarItem();
            selecionado = facade.salvarRetornando(selecionado);
            redirecionarParaVer();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void concluir() {
        try {
            validarRegrasEspecificas();
            selecionado = facade.concluir(selecionado);
            redirecionarParaVer();
            FacesUtil.addOperacaoRealizada("Registro concluído com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (OperacaoEstoqueException op) {
            FacesUtil.addOperacaoNaoPermitida(op.getMessage());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno("/conversao-unidade-medida-material/ver/" + selecionado.getId() + "/");
    }

    public void adicionarMaterial() {
        try {
            validarMaterial();
            Util.adicionarObjetoEmLista(selecionado.getItens(), itemConversao);
            cancelarMaterial();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void editarMaterial(ItemConversaoUnidadeMedidaMaterial item) {
        itemConversao = (ItemConversaoUnidadeMedidaMaterial) Util.clonarObjeto(item);
    }

    public void removerMaterial(ItemConversaoUnidadeMedidaMaterial item) {
        selecionado.getItens().remove(item);
    }

    public void cancelarMaterial() {
        itemConversao = null;
    }

    public void novoMaterial() {
        try {
            Util.validarCampos(selecionado);
            itemConversao = new ItemConversaoUnidadeMedidaMaterial();
            itemConversao.setConversaoUnidadeMedida(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void atribuirValoresEstoqueMaterialSaida() {
        try {
            atribuirNullValoresMaterialSaida();
            Estoque estoque = getEstoque(itemConversao.getMaterialSaida());
            validarEstoque(estoque);
            itemConversao.setEstoqueSaida(estoque);
            itemConversao.setQuantidadeSaida(estoque.getFisico());
            itemConversao.setValorTotalSaida(estoque.getFinanceiro());
            itemConversao.setValorUnitarioSaida(estoque.getCustoMedio());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private Estoque getEstoque(Material material) {
        return facade.getEstoqueFacade().recuperarEstoque(selecionado.getLocalEstoque(), selecionado.getUnidadeOrcamentaria(), material, selecionado.getDataMovimento());
    }

    private void atribuirNullValoresMaterialSaida() {
        itemConversao.setQuantidadeSaida(null);
        itemConversao.setValorTotalSaida(null);
        itemConversao.setValorUnitarioSaida(null);
    }

    private void validarEstoque(Estoque estoque) {
        ValidacaoException ve = new ValidacaoException();
        if (estoque == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Estoque não encontrado para o material " + itemConversao.getMaterialSaida().toStringAutoComplete() + ".");
        }
        if (estoque != null && estoque.getFinanceiro().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O material " + itemConversao.getMaterialSaida().toStringAutoComplete() + " não possui valor em estoque para conversão.");
        }
        ve.lancarException();
    }

    public void validarRegrasEspecificas() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (!hasMateriaisAdicionado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar, a conversão deve ter materiais adicionados na lista.");
        }
        ve.lancarException();
    }

    private void validarMaterial() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(itemConversao);
        if (itemConversao.getValorTotalSaida().compareTo(itemConversao.getEstoqueSaida().getFinanceiro()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total da saída deve ser menor ou igual ao valor total do estoque atual.");
        }
        BigDecimal valorTotalEntrada = itemConversao.getValorTotalEntrada().setScale(2, RoundingMode.FLOOR);
        BigDecimal valorTotalSaida = itemConversao.getValorTotalSaida().setScale(2, RoundingMode.FLOOR);
        if (valorTotalEntrada.compareTo(valorTotalSaida) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total da entrada deve ser igual ao valor total da saída.");
        }
        if (itemConversao.getMaterialSaida().equals(itemConversao.getMaterialEntrada())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O material de entrada deve ser diferente do material de saída.");
        }
        for (ItemConversaoUnidadeMedidaMaterial item : selecionado.getItens()) {
            if (!itemConversao.equals(item) && itemConversao.getMaterialSaida().equals(item.getMaterialSaida())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O material " + itemConversao.getMaterialSaida().toStringAutoComplete() + " já foi adicionado na lista.");
            }
        }
        ve.lancarException();
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalAdministrativaOndeUsuarioEhGestorMaterial(parte.trim(), null,
            facade.getSistemaFacade().getDataOperacao(), facade.getSistemaFacade().getUsuarioCorrente());
    }

    public List<Material> completarMaterialSaida(String parte) {
        if (selecionado.getLocalEstoque() != null) {
            return facade.getMaterialFacade().completarMaterialPermitidoNaHierarquiaDoLocalEstoque(
                null, selecionado.getLocalEstoque(), parte.trim(), StatusMaterial.getSituacoesDeferidoInativo());
        }
        return Lists.newArrayList();
    }

    public List<Material> completarMaterialEntrada(String parte) {
        if (selecionado.getLocalEstoque() != null) {
            return facade.getMaterialFacade().completarMaterialPermitidoNaHierarquiaDoLocalEstoque(
                itemConversao.getMaterialSaida().getGrupo(),
                selecionado.getLocalEstoque(),
                parte.trim(),
                Lists.newArrayList(StatusMaterial.DEFERIDO));
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getLocaisEstoque() {
        List<SelectItem> toReturn = Lists.newArrayList();
        if (selecionado.getUnidadeAdministrativa() != null) {
            toReturn.add(new SelectItem(null, ""));
            List<LocalEstoque> locaisEstoque = facade.getLocalEstoqueFacade().buscarLocaisEstoquePorUnidade(" ", selecionado.getUnidadeAdministrativa());
            for (LocalEstoque local : locaisEstoque) {
                toReturn.add(new SelectItem(local, local.toStringAutoComplete()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getUnidadesOrcamentarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getUnidadeAdministrativa() != null) {
            List<HierarquiaOrganizacional> unidades = facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(
                selecionado.getUnidadeAdministrativa(),
                facade.getSistemaFacade().getDataOperacao());
            for (HierarquiaOrganizacional obj : unidades) {
                toReturn.add(new SelectItem(obj.getSubordinada(), obj.toString()));
            }
            if (unidades.size() == 1) {
                selecionado.setUnidadeOrcamentaria(unidades.get(0).getSubordinada());
            }
        }
        return toReturn;
    }

    public boolean hasMateriaisAdicionado() {
        return !selecionado.getItens().isEmpty();
    }

    public void limparDadosUnidadeAdm() {
        selecionado.setUnidadeAdministrativa(null);
        selecionado.setUnidadeOrcamentaria(null);
        selecionado.setLocalEstoque(null);
        cancelarMaterial();
    }

    public void limparDadosUnidadeOrc() {
        selecionado.setUnidadeOrcamentaria(null);
        selecionado.setLocalEstoque(null);
    }

    public void gerarRelatorio() {
        try {
            String nomeRelatorio = "RELATÓRIO DE CONVERSÃO DE UNIDADE DE MEDIDA MATERIAIS";
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOME_RELATORIO", nomeRelatorio);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("idConversao", selecionado.getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/conversao-unidade-medida-materiais/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            logger.error(ex.getMessage());
        }
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        if (hierarquiaAdministrativa != null) {
            selecionado.setUnidadeAdministrativa(hierarquiaAdministrativa.getSubordinada());
        }
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public ItemConversaoUnidadeMedidaMaterial getItemConversao() {
        return itemConversao;
    }

    public void setItemConversao(ItemConversaoUnidadeMedidaMaterial itemConversao) {
        this.itemConversao = itemConversao;
    }
}
