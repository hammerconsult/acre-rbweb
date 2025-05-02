/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 *
 */
package br.com.webpublico.controle;


import br.com.webpublico.entidades.AbastecimentoObjetoFrota;
import br.com.webpublico.entidades.ItemAbastecObjetoFrota;
import br.com.webpublico.entidades.ItemCotaAbastecimento;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbastecimentoObjetoFrotaFacade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CotaAbastecimentoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@ManagedBean(name = "abastecimentoObjetoFrotaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "abastecimentoObjetoFrotaNovo", pattern = "/frota/abastecimento/novo/", viewId = "/faces/administrativo/frota/abastecimento/edita.xhtml"),
    @URLMapping(id = "abastecimentoObjetoFrotaListar", pattern = "/frota/abastecimento/listar/", viewId = "/faces/administrativo/frota/abastecimento/lista.xhtml"),
    @URLMapping(id = "abastecimentoObjetoFrotaEditar", pattern = "/frota/abastecimento/editar/#{abastecimentoObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/abastecimento/edita.xhtml"),
    @URLMapping(id = "abastecimentoObjetoFrotaVer", pattern = "/frota/abastecimento/ver/#{abastecimentoObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/abastecimento/visualizar.xhtml"),
})
public class AbastecimentoObjetoFrotaControlador extends PrettyControlador<AbastecimentoObjetoFrota> implements Serializable, CRUD {

    @EJB
    private AbastecimentoObjetoFrotaFacade abastecimentoObjetoFrotaFacade;
    @EJB
    private CotaAbastecimentoFacade cotaAbastecimentoFacade;
    private ConverterAutoComplete converterAbastecimentoVeiculo;
    private ItemAbastecObjetoFrota itemAbastecObjetoFrota;


    public AbastecimentoObjetoFrotaControlador() {
        super(AbastecimentoObjetoFrota.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/abastecimento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return abastecimentoObjetoFrotaFacade;
    }


    private void inicializar() {
        selecionado.setNumeroAbastecimento("Gerado automaticamente ao salvar.");
        selecionado.setDataEmissao(abastecimentoObjetoFrotaFacade.getSistemaFacade().getDataOperacao());
        selecionado.setHorasUso(BigDecimal.ZERO);
    }

    @URLAction(mappingId = "abastecimentoObjetoFrotaNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializar();
    }

    @URLAction(mappingId = "abastecimentoObjetoFrotaEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "abastecimentoObjetoFrotaVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public ItemAbastecObjetoFrota getItemAbastecObjetoFrota() {
        return itemAbastecObjetoFrota;
    }

    public void setItemAbastecObjetoFrota(ItemAbastecObjetoFrota itemAbastecObjetoFrota) {
        this.itemAbastecObjetoFrota = itemAbastecObjetoFrota;
    }

    public void novoItemAbastecObjetoFrota() {
        if (selecionado.getCotaAbastecimento() == null) {
            FacesUtil.addCampoObrigatorio("O campo 'Cota' deve ser informado.");
            return;
        }
        itemAbastecObjetoFrota = new ItemAbastecObjetoFrota();
    }

    public ConverterAutoComplete getConverterAbastecimentoVeiculo() {
        if (converterAbastecimentoVeiculo == null) {
            converterAbastecimentoVeiculo = new ConverterAutoComplete(AbastecimentoObjetoFrota.class, abastecimentoObjetoFrotaFacade);
        }
        return converterAbastecimentoVeiculo;
    }

    public void processaMudancaTipoObjetoFrota() {
        selecionado.setObjetoFrota(null);
        selecionado.setMotorista(null);
        selecionado.setOperador(null);
        selecionado.setKm(BigDecimal.ZERO);
    }

    public boolean isVeiculo() {
        return selecionado.getTipoObjetoFrota() != null && selecionado.getTipoObjetoFrota().isVeiculo();
    }

    public boolean isEquipamento() {
        return selecionado.getTipoObjetoFrota() != null && selecionado.getTipoObjetoFrota().isEquipamento();
    }

    private void validaInformacoesDeVeiculo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getKm() == null || selecionado.getKm().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("A Quilometragem deve ser informada com um valor maior que 0(zero).");
        }
        if (selecionado.getMotorista() == null || selecionado.getMotorista().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O motorista deve ser informado.");
        }
        ve.lancarException();
    }

    private void validaInformacoesDeEquipamento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getOperador() == null || selecionado.getOperador().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O operador deve ser informado.");
        }
        if (selecionado.getHorasUso() == null || selecionado.getHorasUso().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("A hora(s) deve ser informada.");
        }
        ve.lancarException();
    }

    public void validarAbastecimento() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);
        ve.lancarException();
        if (selecionado.getTipoObjetoFrota() != null &&
            selecionado.getTipoObjetoFrota().equals(TipoObjetoFrota.VEICULO)) {
            validaInformacoesDeVeiculo();
        } else {
            validaInformacoesDeEquipamento();
        }
        if (selecionado.getItensAbastecimentoObjetoFrota() == null || selecionado.getItensAbastecimentoObjetoFrota().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Nenhum combustível foi registrado.");
        }
        if (selecionado.getDataAbastecimento().compareTo(selecionado.getObjetoFrota().getDataAquisicao()) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data do Abastecimento não pode ser inferior a Data de Aquisição do Veículo ou Equipamento/Máquina.");
        }
        ve.lancarException();
        validarComUltimoAbastecimento();
    }

    private void validarComUltimoAbastecimento() {
        ValidacaoException ve = new ValidacaoException();

        AbastecimentoObjetoFrota ultimoAbastecimento = abastecimentoObjetoFrotaFacade.buscarUltimoAbastecimento(selecionado.getObjetoFrota(), selecionado);
        if (ultimoAbastecimento != null) {
            if (selecionado.getDataAbastecimento().before(ultimoAbastecimento.getDataAbastecimento())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Abastecimento não pode ser inferior a " +
                    "Data de Abastecimento registrada no último abastecimento. Data de Abastecimento anterior: " + Util.dateToString(ultimoAbastecimento.getDataAbastecimento()));
            }
            if (TipoObjetoFrota.EQUIPAMENTO.equals(selecionado.getTipoObjetoFrota())) {
                if (selecionado.getHorasUso().compareTo(ultimoAbastecimento.getHorasUso()) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Hora(s) de Uso Atual não pode ser inferior a " +
                        "Hora(s) de Uso registrada no último abastecimento. Hora(s) de Uso anterior: " + ultimoAbastecimento.getHorasUso());
                }
            } else {
                if (selecionado.getKm().compareTo(ultimoAbastecimento.getKm()) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Quilometragem Atual não pode ser inferior a " +
                        "Quilometragem registrada no último abastecimento. Quilometragem anterior: " + Util.formataNumero(ultimoAbastecimento.getKm()));
                }
            }
        }

        ve.lancarException();
    }

    private void criarNumeroAbastecimento() {
        Calendar dataCadastro = Calendar.getInstance();
        dataCadastro.setTime(abastecimentoObjetoFrotaFacade.getSistemaFacade().getDataOperacao());
        Integer ano = dataCadastro.get(Calendar.YEAR);
        Integer numero = abastecimentoObjetoFrotaFacade.recuperarProximoNumeroAbastecimento(ano);
        selecionado.setAno(ano);
        selecionado.setNumero(numero);
    }


    public List<ItemCotaAbastecimento> itensCotaAbastecimento() {
        if (selecionado.getCotaAbastecimento() != null) {
            return selecionado.getCotaAbastecimento().getItensCotaAbastecimento();
        }
        return new ArrayList();
    }

    public void processaSelecaoCota() {
        selecionado.setItensAbastecimentoObjetoFrota(new ArrayList());
    }

    public void processaSelecaoItemCotaAbastecimento() {
        itemAbastecObjetoFrota.setQuantidadeCota(cotaAbastecimentoFacade.buscarQuantidadeItemCota(itemAbastecObjetoFrota.getItemCotaAbastecimento()));
        itemAbastecObjetoFrota.setQuantidadeEmOutrosAbastecimento(abastecimentoObjetoFrotaFacade.quantidadeItemCotaEmOutrosAbastecimentos(selecionado, itemAbastecObjetoFrota.getItemCotaAbastecimento()));
    }

    private boolean existeItemCotaRegistrado(ItemCotaAbastecimento itemCotaAbastecimento) {
        if (itemCotaAbastecimento != null && itemCotaAbastecimento.getId() != null) {
            if (selecionado.getItensAbastecimentoObjetoFrota() != null) {
                for (ItemAbastecObjetoFrota item : selecionado.getItensAbastecimentoObjetoFrota()) {
                    if (item.getItemCotaAbastecimento().getId().equals(itemCotaAbastecimento.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void validaItemAbastecObjetoFrota() {
        ValidacaoException ve = new ValidacaoException();
        if (itemAbastecObjetoFrota.getItemCotaAbastecimento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Item Cota deve ser informado.");
        } else if (itemAbastecObjetoFrota.getAbastecimentoObjetoFrota() == null && existeItemCotaRegistrado(itemAbastecObjetoFrota.getItemCotaAbastecimento())) {
            ve.adicionarMensagemDeCampoObrigatorio("O Item Cota selecionado já foi registrado.");
        }
        if (itemAbastecObjetoFrota.getQuantidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Qtd do Abastecimento deve ser informada.");
        } else {
            if (itemAbastecObjetoFrota.getQuantidade().compareTo(BigDecimal.ZERO) <= 0 ||
                itemAbastecObjetoFrota.getQuantidade().compareTo(itemAbastecObjetoFrota.getQuantidadeDisponivel()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Qtd do Abastecimento deve ser um valor maior que zero(0) e menor ou igual a Qtd Disponível(" + itemAbastecObjetoFrota.getQuantidadeDisponivel() + ").");
            }
        }
        if (itemAbastecObjetoFrota.getValorUnitario() == null || itemAbastecObjetoFrota.getValorUnitario().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O 'Valor Unitário' deve ser informado com um valor maior ou igual a zero.");
        }
        ve.lancarException();
    }


    public void confirmarItemAbastecObjetoFrota(ItemAbastecObjetoFrota itemAbastecObjetoFrota) {
        try {
            validaItemAbastecObjetoFrota();
            if (selecionado.getItensAbastecimentoObjetoFrota() == null) {
                selecionado.setItensAbastecimentoObjetoFrota(new ArrayList());
            }
            itemAbastecObjetoFrota.setAbastecimentoObjetoFrota(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getItensAbastecimentoObjetoFrota(), itemAbastecObjetoFrota);
            cancelarItemAbastecObjetoFrota();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarItemAbastecObjetoFrota() {
        itemAbastecObjetoFrota = null;
    }

    public void excluirItemAbastecObjetoFrota(ItemAbastecObjetoFrota itemAbastecObjetoFrota) {
        selecionado.getItensAbastecimentoObjetoFrota().remove(itemAbastecObjetoFrota);
    }

    @Override
    public void salvar() {
        try {
            validarAbastecimento();
            criarNumeroAbastecimento();
            selecionado = abastecimentoObjetoFrotaFacade.salvarRetornando(selecionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void alterarItemAbastecObjetoFrota(ItemAbastecObjetoFrota itemAbastecObjetoFrota) {
        this.itemAbastecObjetoFrota = (ItemAbastecObjetoFrota) Util.clonarObjeto(itemAbastecObjetoFrota);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", abastecimentoObjetoFrotaFacade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SECRETARIA", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("NOMERELATORIO", "CONTROLE DE ABASTECIMENTO");
            dto.adicionarParametro("condicao", " where abast.id = " + selecionado.getId());
            dto.adicionarParametro("FILTROS", " Abastecimento: " + selecionado.getNumeroAbastecimento());
            dto.adicionarParametro("MODULO", "FROTAS");
            dto.setNomeRelatorio("CONTROLE DE ABASTECIMENTO");
            dto.setApi("administrativo/abastecimento-objeto-frota/");
            ReportService.getInstance().gerarRelatorio(abastecimentoObjetoFrotaFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatrio: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
}
