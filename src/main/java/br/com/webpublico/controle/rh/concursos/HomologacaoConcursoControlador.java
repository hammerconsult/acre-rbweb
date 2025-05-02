package br.com.webpublico.controle.rh.concursos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.entidades.rh.concursos.HomologacaoConcurso;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.concursos.HomologacaoConcursoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
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
 * Created by Buzatto on 02/09/2015.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-homologacao-concurso", pattern = "/concursos/homologacao/novo/", viewId = "/faces/rh/concursos/homologacao/edita.xhtml"),
        @URLMapping(id = "editar-homologacao-concurso", pattern = "/concursos/homologacao/editar/#{homologacaoConcursoControlador.id}/", viewId = "/faces/rh/concursos/homologacao/edita.xhtml"),
        @URLMapping(id = "ver-homologacao-concurso", pattern = "/concursos/homologacao/ver/#{homologacaoConcursoControlador.id}/", viewId = "/faces/rh/concursos/homologacao/edita.xhtml"),
        @URLMapping(id = "listar-homologacao-concurso", pattern = "/concursos/homologacao/listar/", viewId = "/faces/rh/concursos/homologacao/lista.xhtml")
})
public class HomologacaoConcursoControlador extends PrettyControlador<HomologacaoConcurso> implements Serializable, CRUD {

    @EJB
    private HomologacaoConcursoFacade homologacaoConcursoFacade;

    public HomologacaoConcursoControlador() {
        super(HomologacaoConcurso.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/concursos/homologacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return homologacaoConcursoFacade;
    }

    @Override
    @URLAction(mappingId = "novo-homologacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataHomologacao(UtilRH.getDataOperacao());
        selecionado.setUsuarioSistema(homologacaoConcursoFacade.getSistemaFacade().getUsuarioCorrente());
    }

    @Override
    @URLAction(mappingId = "ver-homologacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-homologacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    public List<SelectItem> getConcursos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Selecione um Concurso..."));
        for (Concurso concurso : homologacaoConcursoFacade.getConcursoFacade().getUltimosConcursosAprovadosSemRecurso()) {
            toReturn.add(new SelectItem(concurso, concurso.toString()));
        }
        return toReturn;
    }

    public void validaConcursoSelecionado() {
        HomologacaoConcurso homologacao = homologacaoConcursoFacade.getHomologacaoDoConcurso(selecionado.getConcurso());
        if (homologacao != null) {
            FacesUtil.addOperacaoNaoPermitida("O concurso informado já foi homologado no dia " + DataUtil.getDataFormatada(homologacao.getDataHomologacao()));
            selecionado.setConcurso(null);
        }
    }
}
