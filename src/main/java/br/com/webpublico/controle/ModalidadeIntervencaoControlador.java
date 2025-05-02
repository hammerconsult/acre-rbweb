package br.com.webpublico.controle;

import br.com.webpublico.entidades.ModalidadeIntervencao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ModalidadeIntervencaoFacade;
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
@ManagedBean(name = "modalidadeIntervencaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-modalidada-intervencao",   pattern = "/planejamento/modalidada-intervencao/novo/",                                          viewId = "/faces/financeiro/emenda/modalidade-intervencao/edita.xhtml"),
        @URLMapping(id = "editar-modalidada-intervencao", pattern = "/planejamento/modalidada-intervencao/editar/#{modalidadeIntervencaoControlador.id}/", viewId = "/faces/financeiro/emenda/modalidade-intervencao/edita.xhtml"),
        @URLMapping(id = "ver-modalidada-intervencao",    pattern = "/planejamento/modalidada-intervencao/ver/#{modalidadeIntervencaoControlador.id}/",    viewId = "/faces/financeiro/emenda/modalidade-intervencao/visualizar.xhtml"),
        @URLMapping(id = "listar-modalidada-intervencao", pattern = "/planejamento/modalidada-intervencao/listar/",                                        viewId = "/faces/financeiro/emenda/modalidade-intervencao/lista.xhtml")
})
public class ModalidadeIntervencaoControlador extends PrettyControlador<ModalidadeIntervencao> implements Serializable, CRUD {

    @EJB
    private ModalidadeIntervencaoFacade modalidadeIntervencaoFacade;

    public ModalidadeIntervencaoControlador() {
        super(ModalidadeIntervencao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/planejamento/modalidada-intervencao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return modalidadeIntervencaoFacade;
    }

    @URLAction(mappingId = "novo-modalidada-intervencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-modalidada-intervencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-modalidada-intervencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)) {
                if (operacao.equals(Operacoes.NOVO)) {
                    modalidadeIntervencaoFacade.salvarNovo(selecionado);
                } else {
                    modalidadeIntervencaoFacade.salvar(selecionado);
                }
                FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                redireciona();
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }
}
