/*
 * Codigo gerado automaticamente em Wed Jun 29 13:47:09 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.rh.esocial.ProvimentoEstatutarioEsocial;
import br.com.webpublico.enums.rh.esocial.TipoPrazoContrato;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ModalidadeContratoFPFacade;
import br.com.webpublico.negocios.TipoAfastamentoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "modalidadeContratoFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoModalidadeContratoFP", pattern = "/modalidade-contrato/novo/", viewId = "/faces/rh/administracaodepagamento/modalidadecontratofp/edita.xhtml"),
    @URLMapping(id = "editarModalidadeContratoFP", pattern = "/modalidade-contrato/editar/#{modalidadeContratoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/modalidadecontratofp/edita.xhtml"),
    @URLMapping(id = "listarModalidadeContratoFP", pattern = "/modalidade-contrato/listar/", viewId = "/faces/rh/administracaodepagamento/modalidadecontratofp/lista.xhtml"),
    @URLMapping(id = "verModalidadeContratoFP", pattern = "/modalidade-contrato/ver/#{modalidadeContratoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/modalidadecontratofp/visualizar.xhtml")
})
public class ModalidadeContratoFPControlador extends PrettyControlador<ModalidadeContratoFP> implements Serializable, CRUD {

    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoServidorFacade;
    @EJB
    private TipoAfastamentoFacade tipoAfastamentoFacade;
    private RegraDeducaoDDF regraDeducaoDDFSelecionada;
    private ItemRegraDeducaoDDF itemRegraDeducaoDDFSelecionado;
    private RegraModalidadeTipoAfast regraModalidadeTipoAfastSelecionada;

    public ModalidadeContratoFPControlador() {
        super(ModalidadeContratoFP.class);
    }

    public ModalidadeContratoFPFacade getFacade() {
        return modalidadeContratoServidorFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return modalidadeContratoServidorFacade;
    }

    @Override
    @URLAction(mappingId = "novoModalidadeContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setCodigo(modalidadeContratoServidorFacade.retornaUltimoCodigoLong());
    }

    @Override
    @URLAction(mappingId = "editarModalidadeContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "verModalidadeContratoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public RegraDeducaoDDF getRegraDeducaoDDFSelecionada() {
        return regraDeducaoDDFSelecionada;
    }

    public void setRegraDeducaoDDFSelecionada(RegraDeducaoDDF regraDeducaoDDFSelecionada) {
        this.regraDeducaoDDFSelecionada = regraDeducaoDDFSelecionada;
    }

    public ItemRegraDeducaoDDF getItemRegraDeducaoDDFSelecionado() {
        return itemRegraDeducaoDDFSelecionado;
    }

    public void setItemRegraDeducaoDDFSelecionado(ItemRegraDeducaoDDF itemRegraDeducaoDDFSelecionado) {
        this.itemRegraDeducaoDDFSelecionado = itemRegraDeducaoDDFSelecionado;
    }

    public RegraModalidadeTipoAfast getRegraModalidadeTipoAfastSelecionada() {
        return regraModalidadeTipoAfastSelecionada;
    }

    public void setRegraModalidadeTipoAfastSelecionada(RegraModalidadeTipoAfast regraModalidadeTipoAfastSelecionada) {
        this.regraModalidadeTipoAfastSelecionada = regraModalidadeTipoAfastSelecionada;
    }

    @Override
    public void salvar() {
        if (podeSalvar()) {
            super.salvar();
        }
    }

    private Boolean podeSalvar() {
        return Util.validaCampos(selecionado);
    }

    public List<SelectItem> getTipoAfastamentoRegraModalidade() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAfastamento object : tipoAfastamentoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoAfastamentoRegraDeducao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAfastamento object : tipoAfastamentoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/modalidade-contrato/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public boolean isVisualizar() {
        return Operacoes.VER.equals(operacao);
    }

    public void novaRegraModalidadeTipoAfastamento() {
        regraModalidadeTipoAfastSelecionada = new RegraModalidadeTipoAfast();
        regraModalidadeTipoAfastSelecionada.setModalidadeContratoFP(selecionado);
    }

    public void confirmaRegraModalidadeTipoAfastamento() {
        if (podeConfirmarRegraModalidadeTipoAfastamento()) {
            selecionado.setRegrasModalidadesTiposAfasts(Util.adicionarObjetoEmLista(selecionado.getRegrasModalidadesTiposAfasts(), regraModalidadeTipoAfastSelecionada));
            cancelaRegraModalidadeTipoAfastamento();
        }
    }

    private boolean podeConfirmarRegraModalidadeTipoAfastamento() {
        if (!validarConfirmacao(regraModalidadeTipoAfastSelecionada)) {
            return false;
        }
        if (!DataUtil.isVigenciaValida(regraModalidadeTipoAfastSelecionada, selecionado.getRegrasModalidadesTiposAfasts())) {
            return false;
        }
        return true;
    }

    private boolean validaDatasRegraModalidadeTipoAfastamentoSelecionada() {
        if (regraModalidadeTipoAfastSelecionada.temDataFinalVigenciaInformada()) {
            if (regraModalidadeTipoAfastSelecionada.getInicioVigencia().after(regraModalidadeTipoAfastSelecionada.getFinalVigencia())) {
                FacesUtil.addOperacaoNaoPermitida("A data inicial da vigência deve ser anterior a data de final da vigência.");
                return false;
            }
        }
        return true;
    }

    public void cancelaRegraModalidadeTipoAfastamento() {
        if (regraModalidadeTipoAfastSelecionada.isEditando) {
            regraModalidadeTipoAfastSelecionada.setEditando(false);
            selecionado.setRegrasModalidadesTiposAfasts(Util.adicionarObjetoEmLista(selecionado.getRegrasModalidadesTiposAfasts(), regraModalidadeTipoAfastSelecionada));
        }
        regraModalidadeTipoAfastSelecionada = null;
    }

    public void selecionaRegraModalidadeTipoAfastamento(RegraModalidadeTipoAfast regraModalidadeTipoAfast) {
        regraModalidadeTipoAfastSelecionada = (RegraModalidadeTipoAfast) Util.clonarObjeto(regraModalidadeTipoAfast);
        regraModalidadeTipoAfastSelecionada.setEditando(true);
        selecionado.getRegrasModalidadesTiposAfasts().remove(regraModalidadeTipoAfast);
    }

    public void removeRegraModalidadeTipoAfastamento(RegraModalidadeTipoAfast regraModalidadeTipoAfast) {
        selecionado.getRegrasModalidadesTiposAfasts().remove(regraModalidadeTipoAfast);
    }

    public boolean validarConfirmacao(ValidadorEntidade obj) {
        if (!Util.validaCampos(obj)) {
            return false;
        }

        try {
            obj.validarConfirmacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return false;
        }
        return true;
    }

    public void novaRegraDeducaoDDF() {
        regraDeducaoDDFSelecionada = new RegraDeducaoDDF();
        regraDeducaoDDFSelecionada.setModalidadeContratoFP(selecionado);
    }

    public void confirmaRegraDeducaoDDF() {
        if (podeConfirmarRegraDeducaoDDF()) {
            selecionado.setRegrasDeducoesDDFs(Util.adicionarObjetoEmLista(selecionado.getRegrasDeducoesDDFs(), regraDeducaoDDFSelecionada));
            cancelaRegraDeducaoDDF();
        }
    }

    private boolean podeConfirmarRegraDeducaoDDF() {
        if (!validarConfirmacao(regraDeducaoDDFSelecionada)) {
            return false;
        }
        if (!DataUtil.isVigenciaValida(regraDeducaoDDFSelecionada, selecionado.getRegrasDeducoesDDFs())) {
            return false;
        }
        return true;
    }

    private boolean validaDatasRegraDeducaoDDFSelecionada() {
        if (regraDeducaoDDFSelecionada.temDataFinalVigenciaInformada()) {
            if (regraDeducaoDDFSelecionada.getInicioVigencia().after(regraDeducaoDDFSelecionada.getFinalVigencia())) {
                FacesUtil.addOperacaoNaoPermitida("A data inicial da vigência deve ser anterior a data de final da vigência.");
                return false;
            }
        }
        return true;
    }

    public void cancelaRegraDeducaoDDF() {
        if (regraDeducaoDDFSelecionada.isEditando()) {
            regraDeducaoDDFSelecionada.setEditando(false);
            selecionado.setRegrasDeducoesDDFs(Util.adicionarObjetoEmLista(selecionado.getRegrasDeducoesDDFs(), regraDeducaoDDFSelecionada));
        }
        regraDeducaoDDFSelecionada = null;
    }

    public void selecionaRegraDeducaoDDF(RegraDeducaoDDF regraDeducaoDDF) {
        regraDeducaoDDFSelecionada = (RegraDeducaoDDF) Util.clonarObjeto(regraDeducaoDDF);
        regraDeducaoDDFSelecionada.setEditando(true);
        selecionado.getRegrasDeducoesDDFs().remove(regraDeducaoDDF);
    }

    public void informaItensRegraDeducaoDDF(RegraDeducaoDDF regraDeducaoDDF) {
        regraDeducaoDDFSelecionada = (RegraDeducaoDDF) Util.clonarObjeto(regraDeducaoDDF);
    }

    public void removeRegraDeducaoDDF(RegraDeducaoDDF regraDeducaoDDF) {
        selecionado.getRegrasDeducoesDDFs().remove(regraDeducaoDDF);
    }

    public void novoItemRegraDeducaoDDF() {
        itemRegraDeducaoDDFSelecionado = new ItemRegraDeducaoDDF();
        itemRegraDeducaoDDFSelecionado.setRegraDeducaoDDF(regraDeducaoDDFSelecionada);
    }

    public void confirmaItemRegraDeducaoDDF() {
        if (podeConfirmarItemRegraDeducaoDDF()) {
            regraDeducaoDDFSelecionada.setItensRegraDeducaoDDF(Util.adicionarObjetoEmLista(regraDeducaoDDFSelecionada.getItensRegraDeducaoDDF(), itemRegraDeducaoDDFSelecionado));
            cancelaItemRegraDeducaoDDF();
        }
    }

    private boolean podeConfirmarItemRegraDeducaoDDF() {
        if (!validarConfirmacao(itemRegraDeducaoDDFSelecionado)) {
            return false;
        }
        return true;
    }

    public void cancelaItemRegraDeducaoDDF() {
        itemRegraDeducaoDDFSelecionado = null;
    }

    public void selecionaItemRegraDeducaoDDF(ItemRegraDeducaoDDF itemRegraDeducaoDDF) {
        itemRegraDeducaoDDFSelecionado = (ItemRegraDeducaoDDF) Util.clonarObjeto(itemRegraDeducaoDDF);
    }

    public void removeItemRegraDeducaoDDF(ItemRegraDeducaoDDF itemRegraDeducaoDDF) {
        regraDeducaoDDFSelecionada.getItensRegraDeducaoDDF().remove(itemRegraDeducaoDDF);
    }

    public List<SelectItem> getTipoPrazoContrato() {
        return Util.getListSelectItem(TipoPrazoContrato.values());
    }

    public List<SelectItem> getTipoProvimentoEstatutario() {
        return Util.getListSelectItem(ProvimentoEstatutarioEsocial.values(), false);
    }

}
