/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 *
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ParametrosInformacoesRBTrans;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametroInformacaoRBTransFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "parametroInformacoesRBTransControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametroInformacoes",
        pattern = "/rbtrans/parametrotransito/informacoes/novo/",
        viewId = "/faces/tributario/rbtrans/parametros/informacoes/edita.xhtml"),
    @URLMapping(id = "visualizarParametroInformacoes",
        pattern = "/rbtrans/parametrotransito/informacoes/ver/#{parametroInformacoesRBTransControlador.id}/",
        viewId = "/faces/tributario/rbtrans/parametros/informacoes/visualiza.xhtml"),
    @URLMapping(id = "editarParametroInformacoes",
        pattern = "/rbtrans/parametrotransito/informacoes/editar/#{parametroInformacoesRBTransControlador.id}/",
        viewId = "/faces/tributario/rbtrans/parametros/informacoes/edita.xhtml"),
    @URLMapping(id = "listarParametroInformacoes",
        pattern = "/rbtrans/parametrotransito/informacoes/listar/",
        viewId = "/faces/tributario/rbtrans/parametros/informacoes/lista.xhtml")}
)
public class ParametroInformacoesRBTransControlador extends PrettyControlador<ParametrosInformacoesRBTrans> implements Serializable, CRUD {

    @EJB
    private ParametroInformacaoRBTransFacade parametrosInformacoesRBTransFacade;

    public ParametroInformacoesRBTransControlador() {
        super(ParametrosInformacoesRBTrans.class);
    }

    @URLAction(mappingId = "novoParametroInformacoes", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoParametro() {
        super.novo();
    }

    @URLAction(mappingId = "editarParametroInformacoes", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarParametro() {
        super.editar();
    }

    @URLAction(mappingId = "visualizarParametroInformacoes", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verParametro() {
        super.ver();
    }

    @Override
    public AbstractFacade getFacede() {
        return parametrosInformacoesRBTransFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rbtrans/parametrotransito/informacoes/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public boolean validaRegrasEspecificas() {
        if(selecionado.getDetentorArquivoComposicao().getArquivoComposicao() == null) {
            FacesUtil.addOperacaoNaoPermitida("Informe a assinatura do secretário.");
            return false;
        }
        ParametrosInformacoesRBTrans param = parametrosInformacoesRBTransFacade.buscarParametroPeloExercicio(selecionado.getExercicio());
        if (param != null && !param.getId().equals(selecionado.getId())) {
            FacesUtil.addOperacaoNaoPermitida("Já existe um parâmetro cadastrado com o exercício de " + selecionado.getExercicio().getAno() + ".");
            return false;
        }
        return true;
    }
}
