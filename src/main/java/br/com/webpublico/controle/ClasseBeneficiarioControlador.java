/*
 * Codigo gerado automaticamente em Thu Apr 05 08:47:08 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ClasseBeneficiario;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ClasseBeneficiarioFacade;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "classeBeneficiarioControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-classe-beneficiario", pattern = "/classe-beneficiario/novo/", viewId = "/faces/financeiro/convenios/despesa/classebeneficiario/edita.xhtml"),
        @URLMapping(id = "editar-classe-beneficiario", pattern = "/classe-beneficiario/editar/#{classeBeneficiarioControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/classebeneficiario/edita.xhtml"),
        @URLMapping(id = "ver-classe-beneficiario", pattern = "/classe-beneficiario/ver/#{classeBeneficiarioControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/classebeneficiario/visualizar.xhtml"),
        @URLMapping(id = "listar-classe-beneficiario", pattern = "/classe-beneficiario/listar/", viewId = "/faces/financeiro/convenios/despesa/classebeneficiario/lista.xhtml")
})
public class ClasseBeneficiarioControlador extends PrettyControlador<ClasseBeneficiario> implements Serializable, CRUD {

    @EJB
    private ClasseBeneficiarioFacade classeBeneficiarioFacade;
    private ClasseBeneficiario classeBeneficiarioSelecioanado;

    public ClasseBeneficiarioControlador() {
        super(ClasseBeneficiario.class);
    }

    public ClasseBeneficiarioFacade getFacade() {
        return classeBeneficiarioFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return classeBeneficiarioFacade;
    }


    @Override
    public void salvar() {
        if (Operacoes.NOVO.equals(operacao)) {
            classeBeneficiarioSelecioanado.setCodigo(codigoSequencial() + "");
        }
        super.salvar();
    }

    private int codigoSequencial() {
        return classeBeneficiarioFacade.codigoSequencial();
    }

    @URLAction(mappingId = "novo-classe-beneficiario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        classeBeneficiarioSelecioanado = (ClasseBeneficiario) selecionado;
        classeBeneficiarioSelecioanado.setCodigo(codigoSequencial() + "");
    }


    @URLAction(mappingId = "ver-classe-beneficiario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        classeBeneficiarioSelecioanado = (ClasseBeneficiario) selecionado;
    }

    @URLAction(mappingId = "editar-classe-beneficiario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        classeBeneficiarioSelecioanado = (ClasseBeneficiario) selecionado;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/classe-beneficiario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
