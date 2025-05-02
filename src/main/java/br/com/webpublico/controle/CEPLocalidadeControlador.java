package br.com.webpublico.controle;

import br.com.webpublico.entidades.CEPLocalidade;
import br.com.webpublico.entidades.CEPUF;
import br.com.webpublico.negocios.CEPLocalidadeFacade;
import br.com.webpublico.negocios.CEPUFFacade;
import br.com.webpublico.util.ConverterGenerico;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Terminal-2
 */
@SessionScoped
@ManagedBean
public class CEPLocalidadeControlador implements Serializable {

    private CEPLocalidade selecionado;
    @EJB
    private CEPLocalidadeFacade facade;

    @EJB
    private CEPUFFacade facadeCepUf;
    private ConverterGenerico converterUf;

    public Converter getConverterUf() {
        if (converterUf == null) {
            converterUf = new ConverterGenerico(CEPUF.class, facadeCepUf);
        }
        return converterUf;
    }


    public void novo() {
        this.selecionado = new CEPLocalidade();
    }

    public void salvar() {
        facade.salvar(selecionado);
    }

    public void alterar(ActionEvent event) {
        selecionado = (CEPLocalidade) event.getComponent().getAttributes().get("objeto");
    }

    public List<CEPLocalidade> getList() {
        return facade.lista();
    }

    public void excluir() {
        facade.remover(selecionado);
    }

    public CEPLocalidade getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(CEPLocalidade selecionado) {
        this.selecionado = selecionado;
    }

    public List<SelectItem> getListUf() {
        List<SelectItem> listUf = new ArrayList<SelectItem>();

        for (CEPUF uf : facadeCepUf.lista()) {
            listUf.add(new SelectItem(uf, uf.getNome()));
        }

        return listUf;
    }

}
