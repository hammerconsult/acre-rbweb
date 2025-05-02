package br.com.webpublico.controle;

import br.com.webpublico.entidades.Profissao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProfissaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 03/03/14
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "profissaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "cadastrarProfissao",
                pattern = "/tributario/profissao/novo/",
                viewId = "/faces/tributario/cadastromunicipal/profissao/edita.xhtml"),
        @URLMapping(id = "alterarProfissao",
                pattern = "/tributario/profissao/editar/#{profissaoControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/profissao/edita.xhtml"),
        @URLMapping(id = "listarProfissao",
                pattern = "/tributario/profissao/listar/",
                viewId = "/faces/tributario/cadastromunicipal/profissao/lista.xhtml"),
        @URLMapping(id = "visualizarProfissao",
                pattern = "/tributario/profissao/ver/#{profissaoControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/profissao/visualizar.xhtml")
})
public class ProfissaoControlador extends PrettyControlador<Profissao> implements Serializable, CRUD {

    @EJB
    private ProfissaoFacade profissaoFacade;

    public ProfissaoControlador() {
        super(Profissao.class);
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public Boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getCodigo() == null) {
            FacesUtil.addWarn("Não foi possível continuar!", "O código é obrigatório!");
            retorno = false;
        }
        if (selecionado.getCodigo() != null && selecionado.getCodigo() <= 0) {
            FacesUtil.addWarn("Não foi possível continuar!", "O código deve ser maior que Zero!");
            retorno = false;
        }
        if (selecionado.getDescricao().trim().equals("")) {
            FacesUtil.addWarn("Não foi possível continuar!", "A descrição é obrigatório!");
            retorno = false;
        }
        if(selecionado.getCodigo() != null && profissaoFacade.existeCodigo(selecionado.getId(), selecionado.getCodigo())){
            FacesUtil.addWarn("Não foi possível continuar!", "O código já existe!!");
            retorno = false;
        }
        if(!selecionado.getDescricao().trim().equals("")&& profissaoFacade.existeDescricao(selecionado.getId(), selecionado.getDescricao())){
            FacesUtil.addWarn("Não foi possível continuar!", "A descrição já existe!!");
            retorno = false;
        }
        return retorno;
    }

    @Override
    @URLAction(mappingId = "cadastrarProfissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setCodigo(profissaoFacade.retornaUltimoCodigo());
    }

    @URLAction(mappingId = "alterarProfissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "visualizarProfissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/profissao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return profissaoFacade;
    }
}
