package br.com.webpublico.controle;

import br.com.webpublico.entidades.Fase;
import br.com.webpublico.entidades.GrupoRecurso;
import br.com.webpublico.entidades.RecursoSistema;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FaseFacade;
import br.com.webpublico.negocios.RecursoSistemaFacade;
import br.com.webpublico.seguranca.service.RecursoSistemaService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 15/10/13
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "faseControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-fase", pattern = "/fase/novo/", viewId = "/faces/admin/controleusuario/fase/edita.xhtml"),
        @URLMapping(id = "editar-fase", pattern = "/fase/editar/#{faseControlador.id}/", viewId = "/faces/admin/controleusuario/fase/edita.xhtml"),
        @URLMapping(id = "ver-fase", pattern = "/fase/ver/#{faseControlador.id}/", viewId = "/faces/admin/controleusuario/fase/visualizar.xhtml"),
        @URLMapping(id = "listar-fase", pattern = "/fase/listar/", viewId = "/faces/admin/controleusuario/fase/lista.xhtml")
})
public class FaseControlador extends PrettyControlador<Fase> implements Serializable, CRUD {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private FaseFacade faseFacade;
    private RecursoSistema recursoSistema;
    private GrupoRecurso grupoRecurso;
    private Boolean filtrarPorRecurso;
    @ManagedProperty(name = "recursoSistemaService", value = "#{recursoSistemaService}")
    private RecursoSistemaService recursoSistemaService;

    public FaseControlador() {
        super(Fase.class);
    }

    public List<RecursoSistema> completaRecursoSistema(String parte) {
        return faseFacade.getRecursoSistemaFacade().listaFiltrando(parte.trim(), "nome", "caminho");
    }

    public List<GrupoRecurso> completaGrupoRecurso(String parte) {
        return faseFacade.getGrupoRecursoFacade().listaFiltrando(parte.trim(), "nome");
    }

    public void selecionarRecurso(ActionEvent evento) {
        recursoSistema = ((RecursoSistema) evento.getComponent().getAttributes().get("objeto"));
    }

    public void selecionarGrupoRecurso(ActionEvent evento) {
        grupoRecurso = ((GrupoRecurso) evento.getComponent().getAttributes().get("objeto"));
    }


    public void removeRecurso(RecursoSistema recurso) {
        selecionado.getRecursos().remove(recurso);
    }

    public void excluirTodos() {
        selecionado.getRecursos().clear();
    }

    public void addRecurso() {
        if (filtrarPorRecurso) {
            if (recursoSistema == null) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Recurso deve ser informado para adicionar.");
            } else {
                adicionarRecurso(recursoSistema);
                recursoSistema = null;
            }
        } else {
            if (grupoRecurso == null) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Grupo de Recurso deve ser informado para adicionar.");
            } else {
                grupoRecurso = faseFacade.getGrupoRecursoFacade().recuperar(grupoRecurso.getId());
                for (RecursoSistema sistema : grupoRecurso.getRecursos()) {
                    adicionarRecurso(sistema);
                }
                grupoRecurso = null;
            }
        }
    }

    private void adicionarRecurso(RecursoSistema recursoSistema) {
        if (!selecionado.getRecursos().contains(recursoSistema)) {
            selecionado.getRecursos().add(recursoSistema);
        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Recurso: " + recursoSistema.getNome() + " já foi adicionado na lista.");
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/fase/";
    }

    @URLAction(mappingId = "novo-fase", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        setOperacao(Operacoes.NOVO);
        filtrarPorRecurso = Boolean.TRUE;
    }

    @URLAction(mappingId = "ver-fase", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-fase", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        filtrarPorRecurso = Boolean.TRUE;
    }

    @Override
    public void excluir() {
        super.excluir();
        recursoSistemaService.getSingletonRecursosSistema().getFases().clear();
    }

    @Override
    public void salvar() {
        try {
            if (validaCampos()) {
                if (operacao.equals(Operacoes.NOVO)) {
                    this.faseFacade.salvarNovo2(selecionado);
                } else {
                    this.faseFacade.salvar2(selecionado);
                }
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro salvo com sucesso.");
                recursoSistemaService.getSingletonRecursosSistema().getFases().clear();
                redireciona();
            }
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Erro: " + e.getMessage());
        }
    }

    public boolean validaCampos() {
        if (selecionado.getNome().isEmpty()) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O campo Nome deve ser informado.");
            return false;
        }
        if (selecionado.getRecursos().isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Para salvar, é obrigatório adicionar ao menos um recurso na lista.");
            return false;
        }
        return true;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return faseFacade;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public RecursoSistema getRecursoSistema() {
        return recursoSistema;
    }

    public void setRecursoSistema(RecursoSistema recursoSistema) {
        this.recursoSistema = recursoSistema;
    }

    public RecursoSistemaService getRecursoSistemaService() {
        return recursoSistemaService;
    }

    public void setRecursoSistemaService(RecursoSistemaService recursoSistemaService) {
        this.recursoSistemaService = recursoSistemaService;
    }

    public GrupoRecurso getGrupoRecurso() {
        return grupoRecurso;
    }

    public void setGrupoRecurso(GrupoRecurso grupoRecurso) {
        this.grupoRecurso = grupoRecurso;
    }

    public Boolean getFiltrarPorRecurso() {
        return filtrarPorRecurso;
    }

    public void setFiltrarPorRecurso(Boolean filtrarPorRecurso) {
        this.filtrarPorRecurso = filtrarPorRecurso;
    }
}
