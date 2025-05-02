/*
 * Codigo gerado automaticamente em Thu Nov 10 16:24:46 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CategoriaFuncional;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CategoriaFuncionalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "categoriaFuncionalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCategoriaFuncional", pattern = "/categoriafuncional/novo/", viewId = "/faces/rh/administracaodepagamento/cadastrofuncional/edita.xhtml"),
    @URLMapping(id = "editarCategoriaFuncional", pattern = "/categoriafuncional/editar/#{categoriaFuncionalControlador.id}/", viewId = "/faces/rh/administracaodepagamento/cadastrofuncional/edita.xhtml"),
    @URLMapping(id = "listarCategoriaFuncional", pattern = "/categoriafuncional/listar/", viewId = "/faces/rh/administracaodepagamento/cadastrofuncional/lista.xhtml"),
    @URLMapping(id = "verCategoriaFuncional", pattern = "/categoriafuncional/ver/#{categoriaFuncionalControlador.id}/", viewId = "/faces/rh/administracaodepagamento/cadastrofuncional/visualizar.xhtml")
})
public class CategoriaFuncionalControlador extends PrettyControlador<CategoriaFuncional> implements Serializable, CRUD {

    @EJB
    private CategoriaFuncionalFacade categoriaFuncionalFacade;

    public CategoriaFuncionalControlador() {
        super(CategoriaFuncional.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return categoriaFuncionalFacade;
    }

    @Override
    public CategoriaFuncional getSelecionado() {
        return ((CategoriaFuncional) selecionado);
    }

    @URLAction(mappingId = "novoCategoriaFuncional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        getSelecionado().setCodigo(categoriaFuncionalFacade.retornaUltimoCodigoLong());
    }

    @URLAction(mappingId = "editarCategoriaFuncional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verCategoriaFuncional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            try {
                if (operacao == Operacoes.NOVO) {
                    Long novoCodigo = categoriaFuncionalFacade.retornaUltimoCodigoLong();
                    if (!novoCodigo.equals(getSelecionado().getCodigo())) {
                        FacesUtil.addWarn("Atenção!", "O Código " + getSelecionado().getCodigo() + " já está sendo usado e foi gerado o novo código " + novoCodigo + " !");
                        getSelecionado().setCodigo(novoCodigo);
                    }

                }
                super.salvar();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage().toString()));
            }
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/categoriafuncional/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
