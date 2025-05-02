package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoEntradaMaterial;
import br.com.webpublico.enums.SituacaoMovimentoMaterial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
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
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 13/11/13
 * Time: 18:13
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "entradaTransferenciaMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaEntradaTransferencia", pattern = "/entrada-por-transferencia/novo/", viewId = "/faces/administrativo/materiais/entradamaterial/transferencia/edita.xhtml"),
    @URLMapping(id = "editarEntradaTransferencia", pattern = "/entrada-por-transferencia/editar/#{entradaTransferenciaMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/entradamaterial/transferencia/edita.xhtml"),
    @URLMapping(id = "listarEntradaTransferencia", pattern = "/entrada-por-transferencia/listar/", viewId = "/faces/administrativo/materiais/entradamaterial/transferencia/lista.xhtml"),
    @URLMapping(id = "verEntradaTransferencia", pattern = "/entrada-por-transferencia/ver/#{entradaTransferenciaMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/entradamaterial/transferencia/visualizar.xhtml")
})
public class EntradaTransferenciaMaterialControlador extends EntradaMaterialControlador {

    private static final Logger logger = LoggerFactory.getLogger(EntradaTransferenciaMaterialControlador.class);
    private List<LocalEstoque> filhos = new ArrayList<>();
    private LocalEstoque selecionarfilho;

    public EntradaTransferenciaMaterialControlador() {
        metadata = new EntidadeMetaData(EntradaTransferenciaMaterial.class);
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
        this.selecionarfilho = selecionarfilho;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/entrada-por-transferencia/";
    }

    @Override
    @URLAction(mappingId = "novaEntradaTransferencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setSituacao(SituacaoEntradaMaterial.CONCLUIDA);
    }

    @Override
    @URLAction(mappingId = "editarEntradaTransferencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verEntradaTransferencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void preencherListaDeItemTransferencia(SelectEvent event) {
        selecionado.getItens().clear();
        criarItensTransferenciaAdicionandoNaListaDoSelecionado((SaidaRequisicaoMaterial) facade.getSaidaMaterialFacade().recarregar(((SaidaRequisicaoMaterial) event.getObject())));
    }

    private void criarItensTransferenciaAdicionandoNaListaDoSelecionado(SaidaRequisicaoMaterial saida) {
        for (ItemSaidaMaterial ism : saida.getListaDeItemSaidaMaterial()) {
            selecionado.getItens().add(new ItemEntradaMaterial(selecionado, ism));
        }
    }

    public void recuperarLocaisDeEstoqueFilhos(ItemEntradaMaterial item) {
        try {
            itemEntradaMaterial = item;
            validarRecuperarLocalEstoqueFilho();
            setFilhos(facade.getLocalEstoqueFacade().recuperarLocalEstoqueAbertosDaHierarquiaDoLocalDeEstoque(
                item.getLocalEstoque(),
                item.getMaterial(),
                facade.getSistemaFacade().getUsuarioCorrente()));
            FacesUtil.executaJavaScript("dialogDialogEstoque.show()");
            FacesUtil.atualizarComponente("formDlgLocalEstoque");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarRecuperarLocalEstoqueFilho() {
        ValidacaoException ve = new ValidacaoException();
        if (itemEntradaMaterial.getMaterial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Material não encontrado para item selecionado.");
        }
        ve.lancarException();
    }

    public void adicionarSobrescreverItemEntradaMaterial() {
        try {
            validarSelecionarLocalEstoque();
            this.itemEntradaMaterial.setEntradaMaterial(selecionado);
            this.itemEntradaMaterial.getLocalEstoqueOrcamentario().setLocalEstoque(selecionarfilho);
            this.itemEntradaMaterial.setMaterial(itemEntradaMaterial.getItemTransferenciaMaterial().getItemSaidaMaterial().getMaterial());
            selecionarfilho = null;
            this.itemEntradaMaterial.getItemTransferenciaMaterial().setItemEntradaMaterial(this.itemEntradaMaterial);
            selecionado.setItens(Util.adicionarObjetoEmLista(selecionado.getItens(), this.itemEntradaMaterial));
            novoItemMaterial();
            FacesUtil.executaJavaScript("dialogDialogEstoque.hide()");
            FacesUtil.atualizarComponente("Formulario:painelMaterial");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarSelecionarLocalEstoque() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionarfilho == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um Local de Estoque.");
        }
        ve.lancarException();
    }

    public void cancelarItemMaterial() {
        novoItemMaterial();
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        if (selecionado instanceof EntradaTransferenciaMaterial
            && ((EntradaTransferenciaMaterial) selecionado).getSaidaRequisicaoMaterial() != null) {
            return facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                ((EntradaTransferenciaMaterial) selecionado).getSaidaRequisicaoMaterial().getRequisicaoMaterial().getUnidadeRequerente(),
                selecionado.getDataEntrada());
        }
        return new HierarquiaOrganizacional();
    }

    public void gerarRelatorioEntradaPorTransferencia() {
        try {
            String nomeRelatorio = "RELATÓRIO DE ENTRADA DE MATERIAIS POR TRANSFERÊNCIA";
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOME_RELATORIO", nomeRelatorio);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("idEntrada", selecionado.getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/entrada-material-transferencia/");
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
}

