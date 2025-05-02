package br.com.webpublico.controle.tributario.regularizacaoconstrucao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ServicoConstrucao;
import br.com.webpublico.enums.tributario.regularizacaoconstrucao.TipoConstrucao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.ServicoConstrucaoFacade;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@ManagedBean(name = "servicoConstrucaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoServicoConstrucao", pattern = "/regularizacao-construcao/servico-constucao/novo/", viewId = "/faces/tributario/regularizacaoconstrucao/servicosconstrucao/edita.xhtml"),
    @URLMapping(id = "editarServicoConstrucao", pattern = "/regularizacao-construcao/servico-constucao/editar/#{servicoConstrucaoControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/servicosconstrucao/edita.xhtml"),
    @URLMapping(id = "listarServicoConstrucao", pattern = "/regularizacao-construcao/servico-constucao/listar/", viewId = "/faces/tributario/regularizacaoconstrucao/servicosconstrucao/lista.xhtml"),
    @URLMapping(id = "verServicoConstrucao", pattern = "/regularizacao-construcao/servico-constucao/ver/#{servicoConstrucaoControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/servicosconstrucao/visualizar.xhtml")
})

public class ServicoConstrucaoControlador extends PrettyControlador<ServicoConstrucao> implements Serializable, CRUD {

    @EJB
    private ServicoConstrucaoFacade servicoConstrucaoFacade;

    public ServicoConstrucaoControlador() {
        super(ServicoConstrucao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return servicoConstrucaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/regularizacao-construcao/servico-constucao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoServicoConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verServicoConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarServicoConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public List<ServicoConstrucao> completarEstaEntidade(String parte) {
        List<ServicoConstrucao> lista = Lists.newArrayList(super.completarEstaEntidade(parte));
        Collections.sort(lista, new Comparator<ServicoConstrucao>() {
            @Override
            public int compare(ServicoConstrucao o1, ServicoConstrucao o2) {
                return Integer.compare(o1.getCodigo(), o2.getCodigo());
            }
        });
        return lista;
    }

    public List<SelectItem> getTipoConstrucao() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (TipoConstrucao tipo : TipoConstrucao.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }
}
