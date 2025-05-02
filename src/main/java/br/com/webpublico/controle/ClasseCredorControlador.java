/*
 * Codigo gerado automaticamente em Thu Apr 05 08:46:51 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ClasseCredor;
import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.enums.TipoClasseCredor;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ClasseCredorFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "classeCredorControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-classe-credor", pattern = "/classe-credor/novo/", viewId = "/faces/tributario/cadastromunicipal/classecredor/edita.xhtml"),
        @URLMapping(id = "editar-classe-credor", pattern = "/classe-credor/editar/#{classeCredorControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/classecredor/edita.xhtml"),
        @URLMapping(id = "ver-classe-credor", pattern = "/classe-credor/ver/#{classeCredorControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/classecredor/visualizar.xhtml"),
        @URLMapping(id = "listar-classe-credor", pattern = "/classe-credor/listar/", viewId = "/faces/tributario/cadastromunicipal/classecredor/lista.xhtml")
})
public class ClasseCredorControlador extends PrettyControlador<ClasseCredor> implements Serializable, CRUD {

    @EJB
    private ClasseCredorFacade classeCredorFacade;

    public ClasseCredorControlador() {
        super(ClasseCredor.class);
    }

    public ClasseCredorFacade getFacade() {
        return classeCredorFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return classeCredorFacade;
    }

    @URLAction(mappingId = "novo-classe-credor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-classe-credor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-classe-credor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<SelectItem> getListaSituacao() {
        List<SelectItem> listaSit = new ArrayList<SelectItem>();
        for (SituacaoCadastralContabil scc : SituacaoCadastralContabil.values()) {
            listaSit.add(new SelectItem(scc, scc.getDescricao()));
        }
        return listaSit;
    }

    public List<SelectItem> getListaTipo() {
        List<SelectItem> listaDest = new ArrayList<SelectItem>();
        listaDest.add(new SelectItem(null, ""));
        List<TipoClasseCredor> values = Arrays.asList(TipoClasseCredor.values());
        Collections.sort(values, new Comparator<TipoClasseCredor>() {
            @Override
            public int compare(TipoClasseCredor o1, TipoClasseCredor o2) {
                return o1.getDescricao().compareTo(o2.getDescricao());
            }
        });
        for (TipoClasseCredor tcc : values) {
            listaDest.add(new SelectItem(tcc, tcc.getDescricao()));
        }
        return listaDest;
    }

    @Override
    public void salvar() {
        if (!validarCodigoRepetido()) {
            FacesUtil.addError("Não foi possível salvar.", " O Código '" + ((ClasseCredor) selecionado).getCodigo() + "' já foi utilizada por outra Classe Credor.");
            return;
        }
        if (Util.validaCampos(selecionado)) {
            if (operacao.equals(Operacoes.NOVO)) {
                classeCredorFacade.salvarNovo(selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com sucesso: ", "A Classe de Pessoa " + ((ClasseCredor) selecionado).getDescricao() + " foi salva com sucesso"));
                redireciona();
            } else {
                classeCredorFacade.salvar(selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Alterado com sucesso:", "A Classe de Pessoa " + ((ClasseCredor) selecionado).getDescricao() + " foi alterada com sucesso"));
                redireciona();
            }
        }
    }

    private boolean validarCodigoRepetido() {
        return classeCredorFacade.validarCodigoRepetido((ClasseCredor) selecionado);
    }

    @Override
    public void cancelar() {
        super.cancelar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/classe-credor/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
