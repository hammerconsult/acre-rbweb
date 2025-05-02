package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoReducao;
import br.com.webpublico.enums.TipoReducaoValorBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.TipoReducaoFacade;
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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 03/10/14
 * Time: 10:57
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-tipo-reducao-grupo-bem", pattern = "/tipo-reducao-grupo-bem/novo/", viewId = "/faces/administrativo/patrimonio/tiporeducaogrupobem/edita.xhtml"),
    @URLMapping(id = "editar-tipo-reducao-grupo-bem", pattern = "/tipo-reducao-grupo-bem/editar/#{tipoReducaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/tiporeducaogrupobem/edita.xhtml"),
    @URLMapping(id = "ver-tipo-reducao-grupo-bem", pattern = "/tipo-reducao-grupo-bem/ver/#{tipoReducaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/tiporeducaogrupobem/visualizar.xhtml"),
    @URLMapping(id = "listar-tipo-reducao-grupo-bem", pattern = "/tipo-reducao-grupo-bem/listar/", viewId = "/faces/administrativo/patrimonio/tiporeducaogrupobem/lista.xhtml")
})
public class TipoReducaoControlador extends PrettyControlador<TipoReducao> implements Serializable, CRUD {

    @EJB
    private TipoReducaoFacade tipoReducaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public TipoReducaoControlador() {
        super(TipoReducao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoReducaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-reducao-grupo-bem/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novo-tipo-reducao-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        inicializarDados();
    }

    private void inicializarDados() {
        selecionado.setValorResidual(BigDecimal.ZERO);
        selecionado.setInicioVigencia(sistemaFacade.getDataOperacao());
    }

    @Override
    @URLAction(mappingId = "ver-tipo-reducao-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-tipo-reducao-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        processarTaxaDeReducaoAnual();
    }


    @Override
    public boolean validaRegrasEspecificas() {
        return validarConfirmacao(selecionado) && validarVigencia() && validarValorResidual() &&
            DataUtil.isVigenciaValida(selecionado, tipoReducaoFacade.recuperarListaDeTipoReducaoDoGrupoBem(selecionado.getGrupoBem()));

    }

    private boolean validarVigencia() {
        if (selecionado.getInicioVigencia() != null && selecionado.getFimVigencia() != null) {
            if (selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
                FacesUtil.addOperacaoNaoPermitida("O Inicío de Vigência não pode ser maior que a data de  Fim de Vigência");
                return false;
            }
        }
        return true;
    }


    private boolean validarValorResidual() {
        if (selecionado.getValorResidual().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addOperacaoNaoPermitida("O valor residual deve ser maior que zero (0).");
            return false;
        }
        return true;
    }

    public List<SelectItem> getTiposDeReducao() {
        return Util.getListSelectItem(Arrays.asList(TipoReducaoValorBem.values()));
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

    public void processarTaxaDeReducaoAnual() {
        selecionado.setTaxaReducaoAnual(selecionado.processarTaxaDeReducaoAnual());
    }

    public void verificaTipoReducao() {
        if (TipoReducaoValorBem.NAO_APLICAVEL.equals(selecionado.getTipoReducaoValorBem())) {
            selecionado.setVidaUtilEmAnos(new BigDecimal(0));
            selecionado.setValorResidual(new BigDecimal(0));
            selecionado.setTaxaReducaoAnual(new BigDecimal(0));
        }
    }

    public void removerTipoReducao(TipoReducao tipoReducao) {
        tipoReducaoFacade.remover(tipoReducao);
    }

    public void carregarInformacaoToTipoNaoAplicavel(){
        if (selecionado.getTipoReducaoValorBem().equals(TipoReducaoValorBem.NAO_APLICAVEL)){
            selecionado.setVidaUtilEmAnos(BigDecimal.ZERO);
            selecionado.setValorResidual(BigDecimal.ZERO);
            selecionado.setTaxaReducaoAnual(BigDecimal.ZERO);


        }
    }
}
