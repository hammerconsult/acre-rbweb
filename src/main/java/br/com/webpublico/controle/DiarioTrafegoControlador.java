package br.com.webpublico.controle;

import br.com.webpublico.entidades.DiarioTrafego;
import br.com.webpublico.entidades.ItemDiarioTrafego;
import br.com.webpublico.entidades.ReservaObjetoFrota;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DiarioTrafegoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import io.minio.messages.Item;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 16/10/14
 * Time: 08:27
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "diarioTrafegoNovo", pattern = "/diario-trafego/novo/", viewId = "/faces/administrativo/frota/trafego/edita.xhtml"),
    @URLMapping(id = "diarioTrafegoListar", pattern = "/diario-trafego/listar/", viewId = "/faces/administrativo/frota/trafego/lista.xhtml"),
    @URLMapping(id = "diarioTrafegoEditar", pattern = "/diario-trafego/editar/#{diarioTrafegoControlador.id}/", viewId = "/faces/administrativo/frota/trafego/edita.xhtml"),
    @URLMapping(id = "diarioTrafegoVer", pattern = "/diario-trafego/ver/#{diarioTrafegoControlador.id}/", viewId = "/faces/administrativo/frota/trafego/visualizar.xhtml"),
})
public class DiarioTrafegoControlador extends PrettyControlador<DiarioTrafego> implements Serializable, CRUD {

    @EJB
    private DiarioTrafegoFacade diarioTrafegoFacade;
    private ItemDiarioTrafego itemDiarioTrafego;
    private ItemDiarioTrafego itemDiarioTrafegoAlteracao;

    @Override
    public AbstractFacade getFacede() {
        return diarioTrafegoFacade;
    }

    public DiarioTrafegoControlador() {
        super(DiarioTrafego.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/diario-trafego/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "diarioTrafegoNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributosDaTela();
    }

    @URLAction(mappingId = "diarioTrafegoVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        inicializarAtributosDaTela();
    }

    @URLAction(mappingId = "diarioTrafegoEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarAtributosDaTela();
    }

    public ItemDiarioTrafego getItemDiarioTrafego() {
        return itemDiarioTrafego;
    }

    public void setItemDiarioTrafego(ItemDiarioTrafego itemDiarioTrafego) {
        this.itemDiarioTrafego = itemDiarioTrafego;
    }

    private void inicializarAtributosDaTela() {
        itemDiarioTrafego = new ItemDiarioTrafego();
    }

    private boolean podeInserirItemDiarioTrafego() {
        if (!Util.validaCampos(selecionado)) {
            return false;
        }
        ItemDiarioTrafego ultimoItemDiarioTrafego = retornaUltimoItemDiarioTrafego();
        if (ultimoItemDiarioTrafego != null && Boolean.FALSE.equals(ultimoItemDiarioTrafego.getHouveRetorno())) {
            FacesUtil.addOperacaoNaoPermitida("Não foi informado os dados de retorno para o último registro lançado.");
            return false;
        }
        return true;
    }

    public void novoItemDiarioTrafego() {
        if (!podeInserirItemDiarioTrafego()) {
            return;
        }
        itemDiarioTrafego = new ItemDiarioTrafego();
        itemDiarioTrafegoAlteracao = null;
        FacesUtil.atualizarComponente("formItemDiarioTrafego:panel-edicao-item");
        FacesUtil.executaJavaScript("dlgItemDiarioTrafego.show()");
    }

    public void removerItemDiarioTrafego(ItemDiarioTrafego itemDiarioTrafego) {
        if(Util.isNull(itemDiarioTrafego.getReservaObjetoFrota())) {
            selecionado.getItensDiarioTrafego().remove(itemDiarioTrafego);
        }else{
            FacesUtil.addOperacaoNaoRealizada("Itens Diário de Trafego criado apartir de uma reseva não podem ser excluidos");
        }
    }

    public void editarItemDiarioTrafego(ItemDiarioTrafego itemDiarioTrafego) {
        selecionado.getItensDiarioTrafego().remove(itemDiarioTrafego);
        this.itemDiarioTrafegoAlteracao = itemDiarioTrafego;
        this.itemDiarioTrafego = ItemDiarioTrafego.clonar(itemDiarioTrafego);

        FacesUtil.atualizarComponente("formItemDiarioTrafego");
        FacesUtil.executaJavaScript("dlgItemDiarioTrafego.show()");
    }

    private ItemDiarioTrafego retornaUltimoItemDiarioTrafego() {
        if (selecionado.getItensDiarioTrafego() != null &&
            selecionado.getItensDiarioTrafego().size() > 0) {
            return selecionado.getItensDiarioTrafego().get(selecionado.getItensDiarioTrafego().size() - 1);
        }
        return null;
    }

    private ItemDiarioTrafego retornaItemDiarioTrafegoAnterior(ItemDiarioTrafego itemDiarioTrafego) {
        ItemDiarioTrafego itemAnterior = null;
        if (selecionado.getItensDiarioTrafego() != null && !selecionado.getItensDiarioTrafego().isEmpty()) {
            for (ItemDiarioTrafego itemLaco : selecionado.getItensDiarioTrafego()) {
                if (itemLaco.getDataLancamento().compareTo(itemDiarioTrafego.getDataLancamento()) <= 0 &&
                    itemLaco.getHoraSaida().compareTo(itemDiarioTrafego.getHoraSaida()) <= 0) {
                    itemAnterior = itemLaco;
                } else {
                    break;
                }
            }
        }
        return itemAnterior;
    }

    private ItemDiarioTrafego retornaItemDiarioTrafegoPosterior(ItemDiarioTrafego itemDiarioTrafego) {
        ItemDiarioTrafego itemPosterior = null;
        if (selecionado.getItensDiarioTrafego() != null && !selecionado.getItensDiarioTrafego().isEmpty()) {
            for (ItemDiarioTrafego itemLaco : selecionado.getItensDiarioTrafego()) {
                if (itemLaco.getDataLancamento().compareTo(itemDiarioTrafego.getDataLancamento()) >= 0 &&
                    itemLaco.getHoraSaida().compareTo(itemDiarioTrafego.getHoraSaida()) >= 0) {
                    itemPosterior = itemLaco;
                    break;
                }
            }
        }
        return itemPosterior;
    }

    private boolean validaItemDiarioTrafego() {
        boolean retorno = true;
        if (!Util.validaCampos(itemDiarioTrafego)) {
            return false;
        }
        if (itemDiarioTrafego.getDataLancamento().compareTo(selecionado.getVeiculo().getDataAquisicao()) < 0) {
            retorno = false;
            FacesUtil.addOperacaoNaoPermitida("A Data de Lançamento não pode ser inferior a Data de Aquisição do veículo.");
        }
        Integer anoDataLancamento = Integer.parseInt(Util.getAnoDaData(itemDiarioTrafego.getDataLancamento()));
        Integer mesDataLancamento = Integer.parseInt(Util.getMesDaData(itemDiarioTrafego.getDataLancamento()));
        if (selecionado.getAno().compareTo(anoDataLancamento) != 0 ||
            selecionado.getMes().compareTo(mesDataLancamento) != 0) {
            retorno = false;
            FacesUtil.addOperacaoNaoPermitida("A Data de Lançamento deve ser referente ao ano/mês do diário de trafego.");
        }
        if (Boolean.TRUE.equals(itemDiarioTrafego.getHouveRetorno())) {
            if (itemDiarioTrafego.getLocalChegada() == null ||
                itemDiarioTrafego.getLocalChegada().isEmpty()) {
                retorno = false;
                FacesUtil.addCampoObrigatorio("O Local de Chegada deve ser informado.");
            }
            if (itemDiarioTrafego.getHoraChegada() == null) {
                retorno = false;
                FacesUtil.addCampoObrigatorio("A Hora de Chegada deve ser informada.");
            }
            if (itemDiarioTrafego.getKmChegada() == null) {
                retorno = false;
                FacesUtil.addCampoObrigatorio("O Km de Chegada deve ser informado.");
            }
            if (itemDiarioTrafego.getKmChegada() != null && itemDiarioTrafego.getKmChegada().compareTo(itemDiarioTrafego.getKmSaida()) <= 0) {
                retorno = false;
                FacesUtil.addOperacaoNaoPermitida("O Km de Chegada deve ser superior ao Km de Saída.");
            }
            if (itemDiarioTrafego.getHoraChegada() != null && itemDiarioTrafego.getHoraChegada().compareTo(itemDiarioTrafego.getHoraSaida()) <= 0) {
                retorno = false;
                FacesUtil.addOperacaoNaoPermitida("A Hora de Chegada deve ser superior a Hora de Saída.");
            }
        }
        if (retorno) {
            ItemDiarioTrafego itemAnterior = retornaItemDiarioTrafegoAnterior(itemDiarioTrafego);
            if (itemAnterior != null) {
                if (Boolean.FALSE.equals(itemAnterior.getHouveRetorno())) {
                    retorno = false;
                    FacesUtil.addOperacaoNaoPermitida("Os dados de retorno do registro anterior ainda não foram informados.");
                } else {
                    if (itemDiarioTrafego.getHoraSaida().compareTo(itemAnterior.getHoraChegada()) < 0
                        && itemDiarioTrafego.getDataLancamento().compareTo(itemAnterior.getDataLancamento()) == 0) {
                        retorno = false;
                        FacesUtil.addOperacaoNaoPermitida("A Hora de Saída não pode ser menor ou igual a última Hora de Chegada.");
                    }
                    if (itemDiarioTrafego.getKmSaida().compareTo(itemAnterior.getKmChegada()) < 0) {
                        retorno = false;
                        FacesUtil.addOperacaoNaoPermitida("A Km de Saída não pode ser inferior a última Km de Chegada.");
                    }
                }
            }

            ItemDiarioTrafego itemPosterior = retornaItemDiarioTrafegoPosterior(itemDiarioTrafego);
            if (itemPosterior != null) {
                if (Boolean.FALSE.equals(itemDiarioTrafego.getHouveRetorno())) {
                    retorno = false;
                    FacesUtil.addOperacaoNaoPermitida("Os dados de retorno devem ser informados.");
                } else {
                    if (itemDiarioTrafego.getHoraChegada().compareTo(itemPosterior.getHoraSaida()) > 0) {
                        retorno = false;
                        FacesUtil.addOperacaoNaoPermitida("A Hora de Chegada não pode ser superior a próxima Hora de Saída.");
                    }
                    if (itemDiarioTrafego.getKmChegada().compareTo(itemPosterior.getKmSaida()) > 0) {
                        retorno = false;
                        FacesUtil.addOperacaoNaoPermitida("A Km de Chegada não pode ser superior a próxima Km de Saída.");
                    }
                }
            }
        }
        return retorno;
    }

    public void salvarItemDiarioTrafego() {
        if (!validaItemDiarioTrafego()) {
            return;
        }

        itemDiarioTrafego.setDiarioTrafego(selecionado);
        selecionado.getItensDiarioTrafego().add(itemDiarioTrafego);
        FacesUtil.executaJavaScript("dlgItemDiarioTrafego.hide()");
        FacesUtil.atualizarComponente("formItemDiarioTrafego:panel-edicao-item");
    }

    public void cancelarItemDiarioTrafego() {
        if (itemDiarioTrafegoAlteracao != null) {
            selecionado.getItensDiarioTrafego().add(itemDiarioTrafegoAlteracao);
        }
    }

    public void processaSelecaoHouveRetorno() {
        itemDiarioTrafego.setLocalChegada(null);
        itemDiarioTrafego.setHoraChegada(null);
        itemDiarioTrafego.setKmChegada(null);
    }

    public List<SelectItem> meses() {
        return Util.getListSelectItem(Arrays.asList(Mes.values()));
    }

    private boolean lancamentoAnteriorAquisicao() {
        Integer anoAquisicao = Integer.parseInt(Util.getAnoDaData(selecionado.getVeiculo().getDataAquisicao()));
        Integer mesAquisicao = Integer.parseInt(Util.getMesDaData(selecionado.getVeiculo().getDataAquisicao()));
        return selecionado.getAno().compareTo(anoAquisicao) <= 0 && selecionado.getMes().compareTo(mesAquisicao) < 0;
    }

    private void validarDiarioDeTrafego() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (diarioTrafegoFacade.existeDiarioTrafego(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Diário de trafego já registro para este veículo/ano/mês.");
        }
        if (lancamentoAnteriorAquisicao()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Ano/Mês do diário de tráfego é inferior ao Ano/Mês de aquisição do veículo.");
        }
        ve.lancarException();
    }

    public boolean podeAlterarDiarioTrafego() {
        return selecionado.getItensDiarioTrafego() == null || selecionado.getItensDiarioTrafego().isEmpty();
    }

    @Override
    public void salvar() {
        try {
            validarDiarioDeTrafego();
            selecionado = diarioTrafegoFacade.salvarRetornando(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }


    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", diarioTrafegoFacade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SECRETARIA", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("NOMERELATORIO", "BOLETIM DIÁRIO DE TRAFEGO - BDT");
            dto.adicionarParametro("condicao", " WHERE DT.ID = " + selecionado.getId());
            dto.adicionarParametro("FILTROS", " Diário de Trafego: " + selecionado);
            dto.adicionarParametro("MODULO", "FROTAS");
            dto.setNomeRelatorio("BOLETIM DIÁRIO DE TRAFEGO - BDT");
            dto.setApi("administrativo/diario-trafego/");
            ReportService.getInstance().gerarRelatorio(diarioTrafegoFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public boolean isSituacaoReservaCancelada(ItemDiarioTrafego itemDiarioTrafego){
        return itemDiarioTrafego.getReservaObjetoFrota() != null
            && itemDiarioTrafego.getReservaObjetoFrota().getSolicitacaoObjetoFrota() != null
            && itemDiarioTrafego.getReservaObjetoFrota().getSolicitacaoObjetoFrota().getSituacao().isCancelada();
    }
}
