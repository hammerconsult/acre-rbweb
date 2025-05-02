/*
 * Codigo gerado automaticamente em Sat Nov 26 15:13:39 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.enums.administrativo.frotas.SituacaoSolicitacaoObjetoFrota;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.ReservaObjetoFrotaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "reservaObjetoFrotaNovo", pattern = "/frota/reserva/novo/", viewId = "/faces/administrativo/frota/reserva/edita.xhtml"),
    @URLMapping(id = "reservaObjetoFrotaListar", pattern = "/frota/reserva/listar/", viewId = "/faces/administrativo/frota/reserva/lista.xhtml"),
    @URLMapping(id = "reservaObjetoFrotaEditar", pattern = "/frota/reserva/editar/#{reservaObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/reserva/edita.xhtml"),
    @URLMapping(id = "reservaObjetoFrotaVer", pattern = "/frota/reserva/ver/#{reservaObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/reserva/visualizar.xhtml"),
})
public class ReservaObjetoFrotaControlador extends PrettyControlador<ReservaObjetoFrota> implements Serializable, CRUD {

    @EJB
    private ReservaObjetoFrotaFacade facade;
    private boolean reservaSemSolicitacao;
    private DiarioTrafego diarioTrafego;
    private ItemDiarioTrafego itemDiarioTrafego;
    private SituacaoSolicitacaoObjetoFrota situacaoSolicitacao;

    public ReservaObjetoFrotaControlador() {
        super(ReservaObjetoFrota.class);
    }

    public ReservaObjetoFrotaFacade getFacade() {
        return facade;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/reserva/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "reservaObjetoFrotaNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializar();
    }

    public List<SelectItem> getSituacoesSolicitacao() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(SituacaoSolicitacaoObjetoFrota.APROVADO, SituacaoSolicitacaoObjetoFrota.APROVADO.getDescricao()));
        toReturn.add(new SelectItem(SituacaoSolicitacaoObjetoFrota.REJEITADO, SituacaoSolicitacaoObjetoFrota.REJEITADO.getDescricao()));
        return toReturn;
    }

    public void apagarMotivoAndObjetoFrota() {
        if (selecionado.getSolicitacaoObjetoFrota() != null) {
            selecionado.getSolicitacaoObjetoFrota().setMotivo("");
            selecionado.setObjetoFrota(null);
        }
    }

    @URLAction(mappingId = "reservaObjetoFrotaEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        setSituacaoSolicitacao(selecionado.getSolicitacaoObjetoFrota().getSituacao());
    }

    @URLAction(mappingId = "reservaObjetoFrotaVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    private void inicializar() {
        selecionado.setRealizadaEm(new Date());
        setReservaSemSolicitacao(false);
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            selecionado.getSolicitacaoObjetoFrota().setSituacao(situacaoSolicitacao);
            if(situacaoSolicitacao.isAprovada()) {
                selecionado = facade.salvarReservaAprovada(selecionado, itemDiarioTrafego);
            }else {
                selecionado = facade.salvarReservaRejeitada(selecionado);
            }
            redirecionarParaReserva(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            descobrirETratarException(ex);
        }
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        Util.validarCampos(selecionado.getSolicitacaoObjetoFrota());
        if(situacaoSolicitacao.isAprovada()) {
            Util.validarCampos(itemDiarioTrafego);
            Util.validarCampos(diarioTrafego);
        }
        ValidacaoException ve = new ValidacaoException();
        if (situacaoSolicitacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Situação é deve ser informado!");

        } else if (situacaoSolicitacao.isRejeitada() && Strings.isNullOrEmpty(selecionado.getSolicitacaoObjetoFrota().getMotivo())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo é deve ser informado!");

        } else if (situacaoSolicitacao.isAprovada() && selecionado.getObjetoFrota() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Veículo/Equipamento deve ser informado!");
        }
        ve.lancarException();
    }

    public boolean isSolicitacaoAprovada() {
        return situacaoSolicitacao != null
            && situacaoSolicitacao.isAprovada();
    }

    public boolean isSolicitacaoRejeitada() {
        return situacaoSolicitacao != null
            && situacaoSolicitacao.isRejeitada();
    }

    public void definirNullParaSolicitacao() {
        apagarMotivoAndObjetoFrota();
        selecionado.setSolicitacaoObjetoFrota(null);
        situacaoSolicitacao = null;
    }

    public void redirecionarParaReserva(ReservaObjetoFrota selecionado) {
        this.selecionado = selecionado;
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public List<ReservaObjetoFrota> completarReservaFrota(String parte) {
        return facade.buscarReservaPorUnidadeAdministrativa(parte, facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
    }

    public SituacaoSolicitacaoObjetoFrota getSituacaoSolicitacao() {
        return situacaoSolicitacao;
    }

    public void setSituacaoSolicitacao(SituacaoSolicitacaoObjetoFrota situacaoSolicitacao) {
        this.situacaoSolicitacao = situacaoSolicitacao;
    }

    public boolean isReservaSemSolicitacao() {
        return reservaSemSolicitacao;
    }

    public void setReservaSemSolicitacao(boolean reservaSemSolicitacao) {
        this.reservaSemSolicitacao = reservaSemSolicitacao;
        if (reservaSemSolicitacao == true) {
            selecionado.setSolicitacaoObjetoFrota(new SolicitacaoObjetoFrota());
            selecionado.getSolicitacaoObjetoFrota().setTipoObjetoFrota(TipoObjetoFrota.VEICULO);
            setSituacaoSolicitacao(SituacaoSolicitacaoObjetoFrota.APROVADO);
        } else {
            selecionado.setSolicitacaoObjetoFrota(new SolicitacaoObjetoFrota());
            selecionado.setObjetoFrota(null);
            setItemDiarioTrafego(null);
        }
    }

    public void montaDiarioTrafego(ObjetoFrota objetoFrota) {
        if (facade.validaSolicitacao(selecionado)) {
            LocalDate data = selecionado.getRealizadaEm().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
            Integer mes = data.getMonth().getValue();
            Integer ano = data.getYear();
            DiarioTrafego diarioTrafegoRecuperado = facade.buscarDiarioTrafego(objetoFrota.getVeiculo().getId(), mes, ano);
            diarioTrafegoRecuperado.setAno(ano);
            diarioTrafegoRecuperado.setMes(mes);
            montaItemDiarioTrafego(diarioTrafegoRecuperado, objetoFrota);
            if (selecionado.getSolicitacaoObjetoFrota().getRealizadaEm() == null) {
                selecionado.getSolicitacaoObjetoFrota().setRealizadaEm(selecionado.getRealizadaEm());
            }
        } else {
            selecionado.setObjetoFrota(null);
        }
    }

    public void montaItemDiarioTrafego(DiarioTrafego diarioTrafegoRecuperado, ObjetoFrota objetoFrota) {
        itemDiarioTrafego =  facade.inicializarItem(selecionado);
        if (Util.isNotNull(diarioTrafegoRecuperado.getId())) {
            diarioTrafego = diarioTrafegoRecuperado;
            itemDiarioTrafego.setDiarioTrafego(diarioTrafegoRecuperado);
        } else {
            diarioTrafegoRecuperado.setAno(diarioTrafegoRecuperado.getAno());
            diarioTrafegoRecuperado.setMes(diarioTrafegoRecuperado.getMes());
            diarioTrafegoRecuperado.setVeiculo(objetoFrota.getVeiculo());
            diarioTrafego = diarioTrafegoRecuperado;
            itemDiarioTrafego.setDiarioTrafego(diarioTrafegoRecuperado);
        }
        FacesUtil.atualizarComponente("formItemDiarioTrafego:panel-edicao-item");
        FacesUtil.executaJavaScript("dlgItemDiarioTrafego.show()");
    }

    public DiarioTrafego getDiarioTrafego() {
        return diarioTrafego;
    }

    public void setDiarioTrafego(DiarioTrafego diarioTrafego) {
        this.diarioTrafego = diarioTrafego;
    }

    public ItemDiarioTrafego getItemDiarioTrafego() {
        return itemDiarioTrafego;
    }

    public void setItemDiarioTrafego(ItemDiarioTrafego itemDiarioTrafego) {
        this.itemDiarioTrafego = itemDiarioTrafego;
    }

    public String montaItem() {
        return facade.montaItem(itemDiarioTrafego);
    }

    public void concluirReserva() {
        selecionado.getSolicitacaoObjetoFrota().setSituacao(SituacaoSolicitacaoObjetoFrota.RESERVADO);
        facade.salvarSolicitacao(selecionado.getSolicitacaoObjetoFrota());
        redireciona();
    }
    @Override
    public void excluir(){
        try {
            facade.remover(selecionado);
            redireciona();
        }catch (Exception e){
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Falha ao tentar Excluir Reserva de Frota, caso o erro persista entre em contato com suporte!");
        }
    }
}
