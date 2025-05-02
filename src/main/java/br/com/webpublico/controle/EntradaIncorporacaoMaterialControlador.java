package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 08/11/13
 * Time: 17:50
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "entradaIncorporacaoMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaEntradaIncorporacao", pattern = "/entrada-por-incorporacao/novo/", viewId = "/faces/administrativo/materiais/entradamaterial/incorporacao/edita.xhtml"),
    @URLMapping(id = "editarEntradaIncorporacao", pattern = "/entrada-por-incorporacao/editar/#{entradaIncorporacaoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/entradamaterial/incorporacao/edita.xhtml"),
    @URLMapping(id = "listarEntradaIncorporacao", pattern = "/entrada-por-incorporacao/listar/", viewId = "/faces/administrativo/materiais/entradamaterial/incorporacao/lista.xhtml"),
    @URLMapping(id = "verEntradaIncorporacao", pattern = "/entrada-por-incorporacao/ver/#{entradaIncorporacaoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/entradamaterial/incorporacao/visualizar.xhtml")
})
public class EntradaIncorporacaoMaterialControlador extends EntradaMaterialControlador {

    private static final Logger logger = LoggerFactory.getLogger(EntradaMaterialCompraControlador.class);
    private List<LocalEstoque> filhos = new ArrayList<>();
    private LocalEstoque selecionarfilho;

    public EntradaIncorporacaoMaterialControlador() {
        metadata = new EntidadeMetaData(EntradaIncorporacaoMaterial.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/entrada-por-incorporacao/";
    }

    @Override
    @URLAction(mappingId = "novaEntradaIncorporacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setSituacao(SituacaoEntradaMaterial.CONCLUIDA);
    }

    @Override
    @URLAction(mappingId = "editarEntradaIncorporacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        FacesUtil.addOperacaoNaoPermitida("Não é permitido editar entrada de materiais.");
    }

    @URLAction(mappingId = "verEntradaIncorporacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
       operacao = Operacoes.VER;
       recuperarObjeto();
    }

    @Override
    public EntradaIncorporacaoMaterial getSelecionado() {
        return (EntradaIncorporacaoMaterial) selecionado;
    }

    public void adicionarSobrescreverItemEntradaMaterial() {
        try {
            validarHierarquiaUnidade();
            validarItemIncorporacao();
            this.itemEntradaMaterial.setEntradaMaterial(selecionado);
            this.itemEntradaMaterial.getLocalEstoqueOrcamentario().setLocalEstoque(selecionarfilho);
            this.itemEntradaMaterial.setMaterial(itemEntradaMaterial.getItemIncorporacaoMaterial().getMaterial());
            selecionarfilho = null;
            this.itemEntradaMaterial.getItemIncorporacaoMaterial().setItemEntradaMaterial(this.itemEntradaMaterial);
            selecionado.setItens(Util.adicionarObjetoEmLista(selecionado.getItens(), this.itemEntradaMaterial));
            novoItemMaterial();
            cancelarMaterial();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarItemIncorporacao() {
        ValidacaoException ve = new ValidacaoException();
        validarBuscaLocalEstoqueFilhos();

        itemEntradaMaterial.getItemIncorporacaoMaterial().realizarValidacoes();
        ve.lancarException();

        itemEntradaMaterial.getLocalEstoqueOrcamentario().realizarValidacoes();
        ve.lancarException();

        if (itemEntradaMaterial.requerLote() && itemEntradaMaterial.getLoteMaterial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo  Lote de Material deve ser informado.");
        }
        ve.lancarException();

        if (itemEntradaMaterial.getQuantidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Quantidade deve ser informado.");
        } else if (itemEntradaMaterial.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade  deve ser maior que zero (0).");
        }
        ve.lancarException();
        if (itemEntradaMaterial.getValorUnitario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Unitário deve ser informado.");
        } else if (itemEntradaMaterial.getValorUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor unitário deve ser maior que zero (0).");
        }
        ve.lancarException();
    }

    public void removerItem(ItemEntradaMaterial iem) {
        selecionado.getItens().remove(iem);
    }

    public void alterarItem(ItemEntradaMaterial item) {
        itemEntradaMaterial = (ItemEntradaMaterial) Util.clonarObjeto(item);
        recuperarLocaisDeEstoqueFilhos();
        selecionarfilho = item.getLocalEstoqueOrcamentario().getLocalEstoque();
    }

    public void cancelarMaterial() {
        novoItemMaterial();
        filhos.clear();
    }


    public boolean getDesabilitarUnidadeOrganizacional() {
        return getSelecionado() != null && getSelecionado().getUnidadeOrganizacional() != null && getSelecionado().getItens().size() > 0;
    }

    public String getDescricaoMaterial() {
        if (itemEntradaMaterial != null && itemEntradaMaterial.getMaterial() != null && itemEntradaMaterial.getMaterial().getId() != null) {
            return itemEntradaMaterial.getMaterial().toString() + " - " + itemEntradaMaterial.getMaterial().getDescricaoComplementar();
        }
        return "Selecione um material.";
    }

    public List<LocalEstoque> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<LocalEstoque> filhos) {
        this.filhos = filhos;
    }

    public LocalEstoque getSelecionarfilho() {
        return selecionarfilho;
    }

    public void setSelecionarfilho(LocalEstoque selecionarfilho) {
        if (selecionarfilho != null) {
            itemEntradaMaterial.getLocalEstoqueOrcamentario().setLocalEstoque(selecionarfilho);
            this.selecionarfilho = selecionarfilho;
        }
    }

    public void recuperarLocaisDeEstoqueFilhos() {
        try {
            validarBuscaLocalEstoqueFilhos();
            if (filhos != null) {
                filhos.clear();
            }
            List<LocalEstoque> locaisEstoque = facade.getLocalEstoqueFacade().recuperarLocalEstoqueAbertosDaHierarquiaDoLocalDeEstoque(
                itemEntradaMaterial.getLocalEstoquePai(),
                itemEntradaMaterial.getMaterial(),
                facade.getSistemaFacade().getUsuarioCorrente());

            validarAndDefinirLocalEstoque(locaisEstoque);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAndDefinirLocalEstoque(List<LocalEstoque> locaisEstoque) {
        if (locaisEstoque == null || locaisEstoque.isEmpty()) {
            FacesUtil.addAtencao("Nenhum local de armazenamento encontrado para o local de estoque pai: <b>" + itemEntradaMaterial.getLocalEstoquePai()
                + "</b> e material: <b>" + itemEntradaMaterial.getMaterial() + "</b>.");
        } else {
            setFilhos(locaisEstoque);
        }
    }

    private void validarBuscaLocalEstoqueFilhos() {
        ValidacaoException ve = new ValidacaoException();

        if (itemEntradaMaterial.getLocalEstoquePai() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Local de Estoque deve ser informado.");
        }
        if (itemEntradaMaterial.getMaterial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Material deve ser informado.");
        }
        if (itemEntradaMaterial.getMaterial() != null && itemEntradaMaterial.requerLote() && itemEntradaMaterial.getLoteMaterial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Lote de Material deve ser informado.");
        }
        if (itemEntradaMaterial.getUnidadeOrcamentaria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Orçamentária deve ser informado.");
        }
        if (itemEntradaMaterial.getQuantidade() == null || itemEntradaMaterial.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Quantidade deve ser informado com quantidade maior que zero(0).");
        }
        if (itemEntradaMaterial.getValorUnitario() == null || itemEntradaMaterial.getValorUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Unitário deve ser informado com valor maior que zero(0).");
        }
        ve.lancarException();
    }

    public HierarquiaOrganizacional unidadeAdministrativaDoLocalEstoque(UnidadeOrganizacional unidadeOrganizacional) {
        if (unidadeOrganizacional != null) {
            return facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unidadeOrganizacional, facade.getSistemaFacade().getDataOperacao());
        }
        return null;
    }

    public HierarquiaOrganizacional unidadeOrcamentariaDoLocalEstoque(UnidadeOrganizacional unidadeOrganizacional) {
        if (unidadeOrganizacional != null) {
            return facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), unidadeOrganizacional, facade.getSistemaFacade().getDataOperacao());
        }
        return null;
    }

    public void definirLocalEstoqueComoNull() {
        getItemEntradaMaterial().setLocalEstoquePai(null);
        getSelecionado().setUnidadeOrganizacional(null);
        getItemEntradaMaterial().getItemIncorporacaoMaterial().setMaterial(null);
    }

    public void gerarRelatorioEntradaIncorporacao() {
        try {
            String nomeRelatorio = "RELATÓRIO DE ENTRADA DE MATERIAIS POR INCORPORAÇÃO";
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOME_RELATORIO", nomeRelatorio);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("idEntrada", selecionado.getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/entrada-material-incorporacao/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            logger.error(ex.getMessage());
        }
    }

    private void validarHierarquiaUnidade() {
        ValidacaoException validacaoException = new ValidacaoException();
        if (selecionado.getUnidadeOrganizacional() == null) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa deve ser informado.");
        }
        validacaoException.lancarException();
    }

}
