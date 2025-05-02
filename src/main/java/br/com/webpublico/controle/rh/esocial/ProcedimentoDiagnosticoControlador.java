package br.com.webpublico.controle.rh.esocial;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.esocial.ProcedimentoDiagnostico;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.esocial.ProcedimentoDiagnosticoFacade;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "procedimentoDiagnosticoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProcedimentoDiagnos", pattern = "/procedimento-diagnostico/novo/", viewId = "/faces/rh/saudeservidor/procedimento-diagnostico/edita.xhtml"),
    @URLMapping(id = "editarProcedimentoDiagnos", pattern = "/procedimento-diagnostico/editar/#{cATControlador.id}/", viewId = "/faces/rh/saudeservidor/procedimento-diagnostico/edita.xhtml"),
    @URLMapping(id = "listarProcedimentoDiagnos", pattern = "/procedimento-diagnostico/listar/", viewId = "/faces/rh/saudeservidor/procedimento-diagnostico/lista.xhtml"),
    @URLMapping(id = "verProcedimentoDiagnos", pattern = "/procedimento-diagnostico/ver/#{cATControlador.id}/", viewId = "/faces/rh/saudeservidor/procedimento-diagnostico/visualizar.xhtml")
})
public class ProcedimentoDiagnosticoControlador extends PrettyControlador<ProcedimentoDiagnostico> implements Serializable, CRUD {


    public ProcedimentoDiagnosticoControlador() {
        super(ProcedimentoDiagnostico.class);
    }

    @EJB
    private ProcedimentoDiagnosticoFacade procedimentoDiagnosticoFacade;

    @Override
    public AbstractFacade getFacede() {
        return procedimentoDiagnosticoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/procedimento-diagnostico/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public List<ProcedimentoDiagnostico> completarEstaEntidade(String parte) {
        return procedimentoDiagnosticoFacade.listaFiltrando(parte.trim(), "descricao", "codigo");
    }
}
