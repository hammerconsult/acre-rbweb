/*
 * Codigo gerado automaticamente em Thu Mar 31 11:57:45 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Funcao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FuncaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "funcaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-funcao", pattern = "/funcao-ppa/novo/", viewId = "/faces/financeiro/ppa/funcao/edita.xhtml"),
        @URLMapping(id = "editar-funcao", pattern = "/funcao-ppa/editar/#{funcaoControlador.id}/", viewId = "/faces/financeiro/ppa/funcao/edita.xhtml"),
        @URLMapping(id = "ver-funcao", pattern = "/funcao-ppa/ver/#{funcaoControlador.id}/", viewId = "/faces/financeiro/ppa/funcao/visualizar.xhtml"),
        @URLMapping(id = "listar-funcao", pattern = "/funcao-ppa/listar/", viewId = "/faces/financeiro/ppa/funcao/lista.xhtml")
})
public class FuncaoControlador extends PrettyControlador<Funcao> implements Serializable, CRUD {

    @EJB
    private FuncaoFacade funcaoFacade;

    public FuncaoControlador() {
        super(Funcao.class);
    }

    public FuncaoFacade getFacade() {
        return funcaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return funcaoFacade;
    }

    @URLAction(mappingId = "novo-funcao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-funcao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-funcao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (!funcaoFacade.validaCodigoIgual(selecionado)) {
            FacesUtil.addOperacaoNaoPermitida("Existe outra Função cadastrada com o código " + selecionado.getCodigo() + ".");
            return;
        }
        super.salvar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/funcao-ppa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<Funcao> completarFuncoes(String parte) {
        return funcaoFacade.listaFiltrandoFuncao(parte);
    }
}
