package br.com.webpublico.controle.rh.concursos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.concursos.AvaliacaoConcurso;
import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.concursos.AvaliacaoConcursoFacade;
import br.com.webpublico.negocios.rh.concursos.ConcursoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
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

@ManagedBean(name = "avaliacaoConcursoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-avaliacao-concurso", pattern = "/concursos/avaliacao-concurso/novo/", viewId = "/faces/rh/concursos/avaliacao/edita.xhtml"),
        @URLMapping(id = "editar-avaliacao-concurso", pattern = "/concursos/avaliacao-concurso/editar/#{avaliacaoConcursoControlador.id}/", viewId = "/faces/rh/concursos/avaliacao/edita.xhtml"),
        @URLMapping(id = "ver-avaliacao-concurso", pattern = "/concursos/avaliacao-concurso/ver/#{avaliacaoConcursoControlador.id}/", viewId = "/faces/rh/concursos/avaliacao/edita.xhtml"),
        @URLMapping(id = "listar-avaliacao-concurso", pattern = "/concursos/avaliacao-concurso/listar/", viewId = "/faces/rh/concursos/avaliacao/lista.xhtml")
})
public class AvaliacaoConcursoControlador extends PrettyControlador<AvaliacaoConcurso> implements Serializable, CRUD {

    @EJB
    private AvaliacaoConcursoFacade avaliacaoConcursoFacade;
    @EJB
    private ConcursoFacade concursoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public AvaliacaoConcursoControlador() {
        super(AvaliacaoConcurso.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return avaliacaoConcursoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/concursos/avaliacao-concurso/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-avaliacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataAvaliacao(UtilRH.getDataOperacao());
        selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
    }

    @URLAction(mappingId = "ver-avaliacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-avaliacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public ConverterAutoComplete getConverterConcurso() {
        return new ConverterAutoComplete(Concurso.class, concursoFacade);
    }

    public List<SelectItem> getConcursos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Concurso c : concursoFacade.getUltimosConcursosNaoAvaliados()) {
            toReturn.add(new SelectItem(c, "" + c));
        }
        return toReturn;
    }

    public List<SelectItem> getStatusParaAvaliacaoConcurso() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(true, "SIM"));
        toReturn.add(new SelectItem(false, "NÃO"));
        return toReturn;
    }
}
