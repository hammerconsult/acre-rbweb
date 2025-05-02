package br.com.webpublico.controle.rh.esocial;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.esocial.CodigoAliquotaFPAEsocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.esocial.CodigoAliquotaFPAEsocialFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by William on 22/06/2018.
 */
@ManagedBean(name = "codigoAliquotaFPAEsocialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-codigo-aliquota", pattern = "/e-social/codigo-aliquota/novo/", viewId = "/faces/rh/esocial/codigo-aliquota/edita.xhtml"),
    @URLMapping(id = "editar-codigo-aliquota", pattern = "/e-social/codigo-aliquota/editar/#{codigoAliquotaFPAEsocialControlador.id}/", viewId = "/faces/rh/esocial/codigo-aliquota/edita.xhtml"),
    @URLMapping(id = "ver-codigo-aliquota", pattern = "/e-social/codigo-aliquota/ver/#{codigoAliquotaFPAEsocialControlador.id}/", viewId = "/faces/rh/esocial/codigo-aliquota/visualizar.xhtml"),
    @URLMapping(id = "listar-codigo-aliquota", pattern = "/e-social/codigo-aliquota/listar/", viewId = "/faces/rh/esocial/codigo-aliquota/lista.xhtml")
})
public class CodigoAliquotaFPAEsocialControlador extends PrettyControlador<CodigoAliquotaFPAEsocial> implements CRUD {
    @EJB
    private CodigoAliquotaFPAEsocialFacade codigoAliquotaFPAEsocialFacade;

    public CodigoAliquotaFPAEsocialControlador() {
        super(CodigoAliquotaFPAEsocial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return codigoAliquotaFPAEsocialFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/e-social/codigo-aliquota/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novo-codigo-aliquota", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editar-codigo-aliquota", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-codigo-aliquota", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarCodigo();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCodigo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCodigo() != null) {
            if (codigoAliquotaFPAEsocialFacade.hasCodigoCadastrado(selecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um Códigos e Alíquotas de FPAS/Terceiros com o código <b>" + selecionado.getCodigo() + "</b>");
            }
            if (selecionado.getCodigo() < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O código deve ser maior que zero");
            }
            ve.lancarException();
        }
    }
}
