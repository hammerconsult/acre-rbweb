package br.com.webpublico.controle;

import br.com.webpublico.entidades.FinalidadePagamento;
import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FinalidadePagamentoFacade;
import br.com.webpublico.interfaces.CRUD;
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

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 16/05/14
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "finalidadePagamentoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-finalidade-pagamento", pattern = "/finalidade-pagamento/novo/", viewId = "/faces/financeiro/orcamentario/finalidadepagamento/edita.xhtml"),
        @URLMapping(id = "editar-finalidade-pagamento", pattern = "/finalidade-pagamento/editar/#{finalidadePagamentoControlador.id}/", viewId = "/faces/financeiro/orcamentario/finalidadepagamento/edita.xhtml"),
        @URLMapping(id = "ver-finalidade-pagamento", pattern = "/finalidade-pagamento/ver/#{finalidadePagamentoControlador.id}/", viewId = "/faces/financeiro/orcamentario/finalidadepagamento/visualizar.xhtml"),
        @URLMapping(id = "listar-finalidade-pagamento", pattern = "/finalidade-pagamento/listar/", viewId = "/faces/financeiro/orcamentario/finalidadepagamento/lista.xhtml")
})
public class FinalidadePagamentoControlador extends PrettyControlador<FinalidadePagamento> implements Serializable, CRUD {
    @EJB
    private FinalidadePagamentoFacade finalidadePagamentoFacade;

    public FinalidadePagamentoControlador() {
        super(FinalidadePagamento.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/finalidade-pagamento/";
    }

    @Override
    @URLAction(mappingId = "novo-finalidade-pagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "ver-finalidade-pagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-finalidade-pagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return finalidadePagamentoFacade;
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> retorno = new ArrayList<>();
        for (SituacaoCadastralContabil situacaoCadastralContabil : SituacaoCadastralContabil.values()) {
            retorno.add(new SelectItem(situacaoCadastralContabil, situacaoCadastralContabil.getDescricao()));
        }

        return retorno;
    }

    public List<FinalidadePagamento> completaFinalidadesPorSituacao(String parte) {
        return finalidadePagamentoFacade.completaFinalidadesPorSituacao(parte.trim(), SituacaoCadastralContabil.ATIVO);
    }
}
