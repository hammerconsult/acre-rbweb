package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoRequisicaoCompra;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RequisicaoCompraEstornoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-requisicao-compra-estorno", pattern = "/estorno-requisicao-compra/novo/", viewId = "/faces/administrativo/licitacao/requisicaodecompraestorno/edita.xhtml"),
    @URLMapping(id = "ver-requisicao-compra-estorno", pattern = "/estorno-requisicao-compra/ver/#{requisicaoCompraEstornoControlador.id}/", viewId = "/faces/administrativo/licitacao/requisicaodecompraestorno/visualizar.xhtml"),
    @URLMapping(id = "listar-requisicao-compra-estorno", pattern = "/estorno-requisicao-compra/listar/", viewId = "/faces/administrativo/licitacao/requisicaodecompraestorno/lista.xhtml")
})
public class RequisicaoCompraEstornoControlador extends PrettyControlador<RequisicaoCompraEstorno> implements Serializable, CRUD {

    @EJB
    private RequisicaoCompraEstornoFacade facade;
    private RequisicaoDeCompra requisicaoDeCompra;

    public RequisicaoCompraEstornoControlador() {
        super(RequisicaoCompraEstorno.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/estorno-requisicao-compra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    @URLAction(mappingId = "novo-requisicao-compra-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
    }

    @Override
    @URLAction(mappingId = "ver-requisicao-compra-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        requisicaoDeCompra = facade.getRequisicaoDeCompraFacade().recuperar(selecionado.getRequisicaoDeCompra().getId());
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            validarItens();
            selecionado.getItens().removeIf(item -> !item.hasQuantidade());
            selecionado = facade.salvarEstorno(selecionado);
            FacesUtil.addOperacaoRealizada("O estorno de requisição de compra foi salva com sucesso.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
            logger.error(" erro ao salvar selecionado.: {} cause.: {} ", ex);
        }
    }

    private void validarItens() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getItens().stream().noneMatch(ItemRequisicaoCompraEstorno::hasQuantidade)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para realizar o estorno, é necessário que menos um (1) item tenha quantidade informada maior que zero(0).");
        }
        ve.lancarException();
        for (ItemRequisicaoCompraEstorno itemEstorno : selecionado.getItens()) {
            if (itemEstorno.getQuantidade().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo quantidade deve ser maior que zero (0) para o item " + itemEstorno.getItemRequisicaoCompra().getNumero() + ".");
            } else if (itemEstorno.getQuantidadeDisponivel().subtract(itemEstorno.getQuantidade()).compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade que deseja estornar é maior que a quantidade disponível para o item Nº <b>" + itemEstorno.getItemRequisicaoCompra().getNumero() + "</b>.");
            }
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            UnidadeOrganizacional unidadeAdm = facade.getRequisicaoDeCompraFacade().getUnidadeAdministrativa(selecionado.getRequisicaoDeCompra(), selecionado.getDataLancamento());
            HierarquiaOrganizacional hoAdm = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unidadeAdm, selecionado.getDataLancamento());
            UsuarioSistema usuarioCorrente = facade.getSistemaFacade().getUsuarioCorrente();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio("RELATÓRIO-ESTORNO-REQUISIÇÃO-COMPRA");
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + facade.getSistemaFacade().getMunicipio().toUpperCase());
            dto.adicionarParametro("ID_ESTORNO", selecionado.getId());
            dto.adicionarParametro("TIPO_REQUISICAO", selecionado.getRequisicaoDeCompra().getTipoRequisicao().getDescricao());
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SECRETARIA", hoAdm.toString().toUpperCase());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE ESTORNO REQUISIÇÃO DE COMPRA");
            dto.adicionarParametro("USUARIO", usuarioCorrente.getNome(), false);
            dto.setApi("administrativo/requisicao-compra-estorno/");
            ReportService.getInstance().gerarRelatorio(usuarioCorrente, dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public List<RequisicaoDeCompra> completarRequisicaoCompra(String parte) {
        return facade.getRequisicaoDeCompraFacade().buscarRequisicaoCompraComSaldoPorSituacoes(parte.trim(), SituacaoRequisicaoCompra.getSituacoesPermiteMovimentacoes());
    }

    public void recuperarRequisicaoCompra() {
        if (selecionado.getRequisicaoDeCompra() != null) {
            requisicaoDeCompra = facade.getRequisicaoDeCompraFacade().recuperar(selecionado.getRequisicaoDeCompra().getId());
            selecionado.setRequisicaoDeCompra(requisicaoDeCompra);
            for (ItemRequisicaoDeCompra itemRequisicao : requisicaoDeCompra.getItens()) {
                for (ItemRequisicaoCompraExecucao itemReqExec : itemRequisicao.getItensRequisicaoExecucao()) {

                    ItemRequisicaoCompraEstorno item = new ItemRequisicaoCompraEstorno();
                    item.setRequisicaoCompraEstorno(selecionado);
                    item.setItemRequisicaoCompraExec(itemReqExec);
                    BigDecimal qtdeEstornada = facade.buscarQuantidadeEstornadaRequisicao(itemReqExec);
                    item.setQuantidadeDisponivel(itemReqExec.getQuantidade().subtract(qtdeEstornada));
                    item.setQuantidade(item.getQuantidadeDisponivel());
                    Util.adicionarObjetoEmLista(selecionado.getItens(), item);
                }
            }
        }
    }

    public boolean hasRequisicaoSelecionada() {
        return requisicaoDeCompra != null;
    }

    public boolean isTipoContrato() {
        return selecionado.getRequisicaoDeCompra() != null && selecionado.getRequisicaoDeCompra().isTipoContrato();
    }

    public void atribuirNullRequisicao() {
        selecionado.setRequisicaoDeCompra(null);
        setRequisicaoDeCompra(null);
        selecionado.setItens(Lists.<ItemRequisicaoCompraEstorno>newArrayList());
    }

    public RequisicaoDeCompra getRequisicaoDeCompra() {
        return requisicaoDeCompra;
    }

    public void setRequisicaoDeCompra(RequisicaoDeCompra requisicaoDeCompra) {
        this.requisicaoDeCompra = requisicaoDeCompra;
    }
}
