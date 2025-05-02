/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrupoRecurso;
import br.com.webpublico.entidades.RecursoSistema;
import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrupoRecursoFacade;
import br.com.webpublico.seguranca.menu.LeitorMenuFacade;
import br.com.webpublico.seguranca.service.RecursoSistemaService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author fabio
 */
@ManagedBean(name = "grupoRecursoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoGrupoRecurso", pattern = "/admin/gruporecursosistema/novo/", viewId = "/faces/admin/controleusuario/gruporecurso/edita.xhtml"),
        @URLMapping(id = "editarGrupoRecurso", pattern = "/admin/gruporecursosistema/editar/#{grupoRecursoControlador.id}/", viewId = "/faces/admin/controleusuario/gruporecurso/edita.xhtml"),
        @URLMapping(id = "duplicarGrupoRecurso", pattern = "/admin/gruporecursosistema/duplicar/#{grupoRecursoControlador.id}/", viewId = "/faces/admin/controleusuario/gruporecurso/edita.xhtml"),
        @URLMapping(id = "listarGrupoRecurso", pattern = "/admin/gruporecursosistema/listar/", viewId = "/faces/admin/controleusuario/gruporecurso/lista.xhtml"),
        @URLMapping(id = "verGrupoRecurso", pattern = "/admin/gruporecursosistema/ver/#{grupoRecursoControlador.id}/", viewId = "/faces/admin/controleusuario/gruporecurso/visualizar.xhtml")
})
public class GrupoRecursoControlador extends PrettyControlador<GrupoRecurso> implements Serializable, CRUD {

    @EJB
    private GrupoRecursoFacade grupoRecursoFacade;
    @ManagedProperty(name = "recursoSistemaService", value = "#{recursoSistemaService}")
    private RecursoSistemaService recursoSistemaService;
    private RecursoSistema recursoSistema;
    private ConverterAutoComplete converterRecursoSistema;
    private String nomeRecuso;
    private ModuloSistema moduloRecurso;
    private List<RecursoSistema> listaRecursoSistemas;

    public GrupoRecursoControlador() {
        super(GrupoRecurso.class);
    }

    public RecursoSistemaService getRecursoSistemaService() {
        return recursoSistemaService;
    }

    public void setRecursoSistemaService(RecursoSistemaService recursoSistemaService) {
        this.recursoSistemaService = recursoSistemaService;
    }

    public Converter getConverterRecursoSistema() {
        if (this.converterRecursoSistema == null) {
            this.converterRecursoSistema = new ConverterAutoComplete(RecursoSistema.class, grupoRecursoFacade.getRecursoSistemaFacade());
        }
        return this.converterRecursoSistema;
    }

    public String getNomeRecuso() {
        return nomeRecuso;
    }

    public void setNomeRecuso(String nomeRecuso) {
        this.nomeRecuso = nomeRecuso;
    }

    public ModuloSistema getModuloRecurso() {
        return moduloRecurso;
    }

    public void setModuloRecurso(ModuloSistema moduloRecurso) {
        this.moduloRecurso = moduloRecurso;
    }

    public List<RecursoSistema> getListaRecursoSistemas() {
        return listaRecursoSistemas;
    }

    public void setListaRecursoSistemas(List<RecursoSistema> listaRecursoSistemas) {
        this.listaRecursoSistemas = listaRecursoSistemas;
    }

    @URLAction(mappingId = "novoGrupoRecurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        this.selecionado = new GrupoRecurso();
        recursoSistema = new RecursoSistema();
        this.listaRecursoSistemas = new ArrayList<>();
        moduloRecurso = null;
        nomeRecuso = "";
    }

    @URLAction(mappingId = "duplicarGrupoRecurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void duplicar() {
        editar();
        GrupoRecurso clone = new GrupoRecurso();
        clone.setNome("Cópia de " + selecionado.getNome());
        for (RecursoSistema r : selecionado.getRecursos()) {
            clone.getRecursos().add(r);
        }
        this.selecionado = clone;
    }

    public RecursoSistema getRecursoSistema() {
        return recursoSistema;
    }

    public void setRecursoSistema(RecursoSistema recursoSistema) {
        this.recursoSistema = recursoSistema;
    }

    @URLAction(mappingId = "verGrupoRecurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarGrupoRecurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recursoSistema = new RecursoSistema();
        moduloRecurso = null;
        nomeRecuso = "";
    }

    public List<GrupoRecurso> getLista() {
        return this.grupoRecursoFacade.lista();
    }

    public List<SelectItem> getCarregaModulos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ModuloSistema object : ModuloSistema.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            try {
                if (this.selecionado.getId() == null) {
                    this.grupoRecursoFacade.salvarNovo2(selecionado);
                } else {
                    this.grupoRecursoFacade.salvar2(selecionado);
                }
                FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                recursoSistemaService.getSingletonRecursosSistema().getGrupos().clear();
                LeitorMenuFacade leitorMenuFacade = (LeitorMenuFacade) Util.getSpringBeanPeloNome("leitorMenuFacade");
                leitorMenuFacade.limparTodosMenus();
                redireciona();
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoRealizada("Erro ao salvar. Detalhes do erro:  " + e.getMessage());
            }
        }
    }

    public void excluirSelecionado() {
        if (grupoRecursoFacade.existeGrupoUsuario(selecionado)) {
            FacesUtil.addOperacaoNaoPermitida("Este registro não pode ser excluído porque possui dependências.");
        } else {
            try {
                this.grupoRecursoFacade.remover(selecionado);
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoPermitida("Este registro não pode ser excluído porque possui dependências.");
            }
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (this.selecionado.getNome() == null || "".equals(this.selecionado.getNome())) {
            FacesUtil.addCampoObrigatorio( "O campo Nome do Grupo de Recurso deve ser informado.");
            retorno = false;
        } else if (grupoRecursoFacade.jaExisteNomeGrupoRecurso(selecionado)) {
            FacesUtil.addOperacaoNaoPermitida("Já existe um Grupo de Recurso com esse nome.");
            retorno = false;
        }
        if (this.selecionado.getRecursos().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Informe pelo menos um Recurso do Sistema.");
            retorno = false;
        }
        return retorno;
    }

    public void addRecurso() {
        if (recursoSistema != null) {
            if (!selecionado.getRecursos().contains(recursoSistema)) {
                selecionado.getRecursos().add(recursoSistema);
                recursoSistema = new RecursoSistema();
            } else {
                FacesUtil.addOperacaoNaoPermitida("Este recurso já foi adicionado na lista.");
            }
        } else {
            FacesUtil.addOperacaoNaoPermitida( "O campo Recurso deve ser informado para adicionar.");
        }
    }

    public List<RecursoSistema> completaRecursoSistema(String parte) {
        return grupoRecursoFacade.getRecursoSistemaFacade().listaFiltrando(parte.trim(), "nome", "caminho");
    }

    public void removeRecurso(RecursoSistema recurso) {
        selecionado.getRecursos().remove(recurso);
    }

    public void filtrarRecursos() {
        this.listaRecursoSistemas = new ArrayList<>();
        this.listaRecursoSistemas.addAll(grupoRecursoFacade.getRecursoSistemaFacade().listaRecursos(nomeRecuso.trim(), moduloRecurso));
    }

    public void addAllRecursos() {
        for (RecursoSistema r : listaRecursoSistemas) {
            if (r.getSelecionado()) {
                selecionado.getRecursos().add(r);
            }
        }

    }

    public boolean jahRecursoJaExiste(RecursoSistema rs) {
        return selecionado.getRecursos().contains(rs);
    }

    public void inicializaLista() {
        this.nomeRecuso = "";
        this.moduloRecurso = null;
        this.listaRecursoSistemas = new ArrayList<>();
    }

    public void marcarTodosItensVencedores() {
        for (RecursoSistema r : listaRecursoSistemas) {
            if (!selecionado.getRecursos().contains(r)) {
                r.setSelecionado(Boolean.TRUE);
            }
        }
    }

    public void desmarcarTodosItensVencedores() {
        for (RecursoSistema r : listaRecursoSistemas) {
            r.setSelecionado(Boolean.FALSE);
        }
    }

    public boolean todosOsItensEstaoSelecionados() {
        try {
            for (RecursoSistema r : listaRecursoSistemas) {
                if (!selecionado.getRecursos().contains(r)) {
                    if (r.getSelecionado() == null || !r.getSelecionado()) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<RecursoSistema> getOrdenarRecursos() {
        Collections.sort(selecionado.getRecursos(), new Comparator<RecursoSistema>() {
            @Override
            public int compare(RecursoSistema o1, RecursoSistema o2) {
                return o1.getNome().compareTo(o2.getNome());
            }
        });
        return selecionado.getRecursos();
    }

    public void excluirTodos() {
        selecionado.setRecursos(new ArrayList<RecursoSistema>());
    }

    @Override
    public AbstractFacade getFacede() {
        return grupoRecursoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/admin/gruporecursosistema/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
