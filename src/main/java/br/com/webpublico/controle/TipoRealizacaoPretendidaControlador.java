package br.com.webpublico.controle;

import br.com.webpublico.entidades.ModalidadeIntervencao;
import br.com.webpublico.entidades.TipoRealizacaoPretendida;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ModalidadeIntervencaoFacade;
import br.com.webpublico.negocios.TipoRealizacaoPretendidaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 16/06/15
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "tipoRealizacaoPretendidaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-tipo-realizacao-pretendida",   pattern = "/planejamento/tipo-realizacao-pretendida/novo/",                                             viewId = "/faces/financeiro/emenda/realizacao-pretendida/edita.xhtml"),
        @URLMapping(id = "editar-tipo-realizacao-pretendida", pattern = "/planejamento/tipo-realizacao-pretendida/editar/#{tipoRealizacaoPretendidaControlador.id}/", viewId = "/faces/financeiro/emenda/realizacao-pretendida/edita.xhtml"),
        @URLMapping(id = "ver-tipo-realizacao-pretendida",    pattern = "/planejamento/tipo-realizacao-pretendida/ver/#{tipoRealizacaoPretendidaControlador.id}/",    viewId = "/faces/financeiro/emenda/realizacao-pretendida/visualizar.xhtml"),
        @URLMapping(id = "listar-tipo-realizacao-pretendida", pattern = "/planejamento/tipo-realizacao-pretendida/listar/",                                           viewId = "/faces/financeiro/emenda/realizacao-pretendida/lista.xhtml")
})
public class TipoRealizacaoPretendidaControlador extends PrettyControlador<TipoRealizacaoPretendida> implements Serializable, CRUD {

    @EJB
    private TipoRealizacaoPretendidaFacade tipoRealizacaoPretendidaFacade;

    public TipoRealizacaoPretendidaControlador() {
        super(TipoRealizacaoPretendida.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/planejamento/tipo-realizacao-pretendida/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoRealizacaoPretendidaFacade;
    }

    @URLAction(mappingId = "novo-tipo-realizacao-pretendida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-tipo-realizacao-pretendida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-tipo-realizacao-pretendida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)) {
                if (operacao.equals(Operacoes.NOVO)) {
                    tipoRealizacaoPretendidaFacade.salvarNovo(selecionado);
                } else {
                    tipoRealizacaoPretendidaFacade.salvar(selecionado);
                }
                FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                redireciona();
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }
}
