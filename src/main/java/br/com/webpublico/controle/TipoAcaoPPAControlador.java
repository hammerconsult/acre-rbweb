/*
 * Codigo gerado automaticamente em Fri Nov 18 15:19:16 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoAcao;
import br.com.webpublico.entidades.TipoAcaoPPA;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoAcaoPPAFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
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

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novotipoacao", pattern = "/tipo-acao-ppa/novo/", viewId = "/faces/financeiro/ppa/tipoacaoppa/edita.xhtml"),
    @URLMapping(id = "editartipoacao", pattern = "/tipo-acao-ppa/editar/#{tipoAcaoPPAControlador.id}/", viewId = "/faces/financeiro/ppa/tipoacaoppa/edita.xhtml"),
    @URLMapping(id = "vertipoacao", pattern = "/tipo-acao-ppa/ver/#{tipoAcaoPPAControlador.id}/", viewId = "/faces/financeiro/ppa/tipoacaoppa/visualizar.xhtml"),
    @URLMapping(id = "listartipoacao", pattern = "/tipo-acao-ppa/listar/", viewId = "/faces/financeiro/ppa/tipoacaoppa/lista.xhtml")
})
public class TipoAcaoPPAControlador extends PrettyControlador<TipoAcaoPPA> implements Serializable, CRUD {

    @EJB
    private TipoAcaoPPAFacade tipoAcaoPPAFacade;

    public TipoAcaoPPAControlador() {
        super(TipoAcaoPPA.class);
    }

    public TipoAcaoPPAFacade getFacade() {
        return tipoAcaoPPAFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoAcaoPPAFacade;
    }

    public List<SelectItem> getTipoAcao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAcao ta : TipoAcao.values()) {
            toReturn.add(new SelectItem(ta, ta.toString()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "novotipoacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "vertipoacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editartipoacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-acao-ppa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)
                && validarCodigoRepetido()) {
                if (operacao.equals(Operacoes.NOVO)) {
                    tipoAcaoPPAFacade.salvarNovo(selecionado);
                    FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                } else {
                    tipoAcaoPPAFacade.salvar(selecionado);
                    FacesUtil.addOperacaoRealizada("Registro alterado com sucesso.");
                }
                redireciona();
            }
        } catch (Exception e) {
            FacesUtil.addError("Erro", e.getMessage());
        }
    }

    private boolean validarCodigoRepetido() {
        if (!tipoAcaoPPAFacade.validaCodigoRepetido(selecionado)) {
            FacesUtil.addOperacaoNaoPermitida("O Código " + selecionado.getCodigo() + " já foi utilizado.");
            return false;
        }
        return true;
    }

    @Override
    public List<TipoAcaoPPA> completarEstaEntidade(String parte) {
        return tipoAcaoPPAFacade.listaFiltrando(parte.trim(), "descricao");
    }
}
