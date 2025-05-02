package br.com.webpublico.controle;

import br.com.webpublico.entidades.Chamado;
import br.com.webpublico.entidades.ConfiguracaoChamado;
import br.com.webpublico.entidades.ConfiguracaoChamadoUsuario;
import br.com.webpublico.entidades.NivelChamado;
import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.negocios.ConfiguracaoChamadoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

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
 * Date: 14/08/14
 * Time: 11:01
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "configuracaoChamadoControlador")
@ViewScoped
@URLMapping(id = "novo-configuracao-chamado", pattern = "/configuracao-chamado/", viewId = "/faces/admin/configuracao-chamado/edita.xhtml")
public class ConfiguracaoChamadoControlador implements Serializable {
    @EJB
    private ConfiguracaoChamadoFacade configuracaoChamadoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private ConfiguracaoChamado selecionado;
    private ConfiguracaoChamadoUsuario usuario;


    @URLAction(mappingId = "novo-configuracao-chamado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = new ConfiguracaoChamado();
        List<ConfiguracaoChamado> configuracoes = configuracaoChamadoFacade.listaDecrescente();
        if (configuracoes != null) {
            if (!configuracoes.isEmpty()) {
                selecionado = configuracaoChamadoFacade.recuperar(configuracoes.get(0).getId());
            }
        }
        usuario = new ConfiguracaoChamadoUsuario();
    }

    //Getters e Setters

    public ConfiguracaoChamado getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ConfiguracaoChamado selecionado) {
        this.selecionado = selecionado;
    }

    public ConfiguracaoChamadoUsuario getUsuario() {
        return usuario;
    }

    public void setUsuario(ConfiguracaoChamadoUsuario usuario) {
        this.usuario = usuario;
    }

    public List<SelectItem> getModulos() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (ModuloSistema moduloSistema : ModuloSistema.values()) {
            retorno.add(new SelectItem(moduloSistema, moduloSistema.getDescricao()));
        }
        return retorno;
    }

    public SelectItem[] getModulosView() {
        SelectItem[] opcoes = new SelectItem[ModuloSistema.values().length + 1];
        opcoes[0] = new SelectItem("", "TODOS");
        int i = 1;
        for (ModuloSistema tipo : ModuloSistema.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.toString());
            i++;
        }
        return opcoes;
    }

    public List<SelectItem> getNiveis() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (NivelChamado nivel : NivelChamado.values()) {
            retorno.add(new SelectItem(nivel, nivel.getDescricao()));
        }
        return retorno;
    }

    public SelectItem[] getNiveisView() {
        SelectItem[] opcoes = new SelectItem[NivelChamado.values().length + 1];
        opcoes[0] = new SelectItem("", "TODOS");
        int i = 1;
        for (NivelChamado tipo : NivelChamado.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.toString());
            i++;
        }
        return opcoes;
    }

    public void salvar() {
        try {
            usuario.setConfigucao(selecionado);
            if (!Util.validaCampos(usuario)) {
                return;
            }

            selecionado.setUsuarios(Util.adicionarObjetoEmLista(selecionado.getUsuarios(), usuario));
            usuario = new ConfiguracaoChamadoUsuario();

            if (selecionado.getId() == null) {
                configuracaoChamadoFacade.salvarNovo(selecionado);
            } else {
                configuracaoChamadoFacade.salvar(selecionado);
            }
            ConfiguracaoChamadoUsuario configuracaoChamadoUsuario = selecionado.getUsuarios().get(0);
            sistemaFacade.getSistemaService().getSingletonRecursosSistema().limparChamados(configuracaoChamadoUsuario.getModulo(), configuracaoChamadoUsuario.getNivel());
            FacesUtil.addOperacaoRealizada("Usuário Salvo com sucesso!");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void alterar(ConfiguracaoChamadoUsuario configuracaoChamadoUsuario) {
        this.usuario = configuracaoChamadoUsuario;
    }

    public void remover(ConfiguracaoChamadoUsuario configuracaoChamadoUsuario) {
        try {
            selecionado.getUsuarios().remove(configuracaoChamadoUsuario);
            configuracaoChamadoFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada("Usuário removido com sucesso!");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }
}
