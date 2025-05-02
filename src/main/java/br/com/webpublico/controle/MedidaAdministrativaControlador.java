/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.MedidaAdministrativa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MedidaAdministrativaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author java
 */
@ManagedBean(name = "medidaAdministrativaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoMedidaAdministrativa", pattern = "/medidaadministrativa/novo/", viewId = "/faces/tributario/rbtrans/fiscalizacao/medidasadministrativas/edita.xhtml"),
    @URLMapping(id = "editarMedidaAdministrativa", pattern = "/medidaadministrativa/editar/#{medidaAdministrativaControlador.id}/", viewId = "/faces/tributario/rbtrans/fiscalizacao/medidasadministrativas/edita.xhtml"),
    @URLMapping(id = "listarMedidaAdministrativa", pattern = "/medidaadministrativa/listar/", viewId = "/faces/tributario/rbtrans/fiscalizacao/medidasadministrativas/lista.xhtml"),
    @URLMapping(id = "verMedidaAdministrativa", pattern = "/medidaadministrativa/ver/#{medidaAdministrativaControlador.id}/", viewId = "/faces/tributario/rbtrans/fiscalizacao/medidasadministrativas/visualizar.xhtml")
})
public class MedidaAdministrativaControlador extends PrettyControlador<MedidaAdministrativa> implements Serializable, CRUD {

    public MedidaAdministrativaControlador() {
        super(MedidaAdministrativa.class);
    }
    @EJB
    private MedidaAdministrativaFacade medidaAdministrativaFacade;

    @URLAction(mappingId = "novoMedidaAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verMedidaAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarMedidaAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();

    }

    @Override
    public AbstractFacade getFacede() {
        return medidaAdministrativaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/medidaadministrativa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public boolean validaCampos() {
        if (esteSelecionado().getCodigo() == null || esteSelecionado().equals("")) {
            FacesUtil.addError("Não foi possível continuar!", "Por favor, informe o Código.");
            return false;
        }
        if (esteSelecionado().getDescricao().isEmpty() || esteSelecionado().getDescricao() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Por favor, informe o Descrição.");
            return false;
        }

        if (operacao == Operacoes.NOVO) {
            if (codigoRepetido()) {
                FacesUtil.addError("Não foi possível continuar!", "O Código informado já existe.");
                return false;
            }
        }
        return true;
    }

    private boolean codigoRepetido() {
        List listaFiltrandoInteiro = getFacede().listaFiltrandoInteiro("" + esteSelecionado().getCodigo(), "codigo");
        if (!listaFiltrandoInteiro.isEmpty()) {
            return true;
        }
        return false;
    }

    public MedidaAdministrativa esteSelecionado() {
        return (MedidaAdministrativa) selecionado;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }
}
