package br.com.webpublico.controle;

import br.com.webpublico.entidades.AprovacaoEmenda;
import br.com.webpublico.entidades.AprovacaoEmendaUsuario;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.TipoAprovacaoEmendaUsuario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AprovacaoEmendaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-aprovacao-emenda", pattern = "/aprovacao-emenda/novo/", viewId = "/faces/financeiro/emenda/aprovacao-emenda/edita.xhtml"),
    @URLMapping(id = "editar-aprovacao-emenda", pattern = "/aprovacao-emenda/editar/#{aprovacaoEmendaControlador.id}/", viewId = "/faces/financeiro/emenda/aprovacao-emenda/edita.xhtml"),
    @URLMapping(id = "listar-aprovacao-emenda", pattern = "/aprovacao-emenda/listar/", viewId = "/faces/financeiro/emenda/aprovacao-emenda/lista.xhtml"),
    @URLMapping(id = "ver-aprovacao-emenda", pattern = "/aprovacao-emenda/ver/#{aprovacaoEmendaControlador.id}/", viewId = "/faces/financeiro/emenda/aprovacao-emenda/visualizar.xhtml")
})
public class AprovacaoEmendaControlador extends PrettyControlador<AprovacaoEmenda> implements Serializable, CRUD {

    @EJB
    private AprovacaoEmendaFacade facade;
    private AprovacaoEmendaUsuario usuarioCamara;
    private AprovacaoEmendaUsuario usuarioPrefeitura;

    public AprovacaoEmendaControlador() {
        super(AprovacaoEmenda.class);
    }

    @URLAction(mappingId = "nova-aprovacao-emenda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
        cancelarUsuarioCamara();
        cancelarUsuarioPrefeitura();
    }

    @URLAction(mappingId = "editar-aprovacao-emenda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        cancelarUsuarioCamara();
        cancelarUsuarioPrefeitura();
    }

    @URLAction(mappingId = "ver-aprovacao-emenda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<UsuarioSistema> completarUsuarios(String filtro) {
        return facade.getUsuarioSistemaFacade().buscarTodosUsuariosPorLoginOuNomeOuCpf(filtro);
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarCampos() {
        selecionado.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFimVigencia() != null && selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Fim de Vigência deve ser maior que o Início de Vigência.");
        }
        if (selecionado.getUsuariosCamara().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário selecionar pelo menos um(1) usuário para a Câmara.");
        }
        if (selecionado.getUsuariosPrefeitura().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário selecionar pelo menos um(1) usuário para a Prefeitura.");
        }
        ve.lancarException();
    }

    public void cancelarUsuarioCamara() {
        usuarioCamara = null;
    }

    public void instanciarUsuarioCamara() {
        usuarioCamara = new AprovacaoEmendaUsuario();
    }

    public void cancelarUsuarioPrefeitura() {
        usuarioPrefeitura = null;
    }

    public void instanciarUsuarioPrefeitura() {
        usuarioPrefeitura = new AprovacaoEmendaUsuario();
    }

    public void editarUsuarioCamara(AprovacaoEmendaUsuario usuarioCamara) {
        this.usuarioCamara = (AprovacaoEmendaUsuario) Util.clonarObjeto(usuarioCamara);
    }

    public void editarUsuarioPrefeitura(AprovacaoEmendaUsuario usuarioPrefeitura) {
        this.usuarioPrefeitura = (AprovacaoEmendaUsuario) Util.clonarObjeto(usuarioPrefeitura);
    }

    public void removerUsuario(AprovacaoEmendaUsuario usuario) {
        selecionado.getUsuarios().remove(usuario);
    }

    public void adicionarUsuarioCamara() {
        try {
            validarUsuarioCamara();
            usuarioCamara.setAprovacaoEmenda(selecionado);
            usuarioCamara.setTipoAprovacaoEmendaUsuario(TipoAprovacaoEmendaUsuario.CAMARA);
            Util.adicionarObjetoEmLista(selecionado.getUsuarios(), usuarioCamara);
            cancelarUsuarioCamara();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarUsuarioCamara() {
        ValidacaoException ve = new ValidacaoException();
        if (usuarioCamara.getUsuarioSistema() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Usuário deve ser informado.");
        }
        ve.lancarException();
        for (AprovacaoEmendaUsuario aprovacaoEmendaUsuario : selecionado.getUsuariosCamara()) {
            if (!aprovacaoEmendaUsuario.equals(usuarioCamara) && aprovacaoEmendaUsuario.getUsuarioSistema().equals(usuarioCamara.getUsuarioSistema())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O usuário selecionado já está adicionado.");
            }
        }
        ve.lancarException();
    }

    public void adicionarUsuarioPrefeitura() {
        try {
            validarUsuarioPrefeitura();
            usuarioPrefeitura.setAprovacaoEmenda(selecionado);
            usuarioPrefeitura.setTipoAprovacaoEmendaUsuario(TipoAprovacaoEmendaUsuario.PREFEITURA);
            Util.adicionarObjetoEmLista(selecionado.getUsuarios(), usuarioPrefeitura);
            cancelarUsuarioPrefeitura();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarUsuarioPrefeitura() {
        ValidacaoException ve = new ValidacaoException();
        if (usuarioPrefeitura.getUsuarioSistema() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Usuário deve ser informado.");
        }
        ve.lancarException();
        for (AprovacaoEmendaUsuario aprovacaoEmendaUsuario : selecionado.getUsuariosPrefeitura()) {
            if (!aprovacaoEmendaUsuario.equals(usuarioPrefeitura) && aprovacaoEmendaUsuario.getUsuarioSistema().equals(usuarioPrefeitura.getUsuarioSistema())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O usuário selecionado já está adicionado.");
            }
        }
        ve.lancarException();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/aprovacao-emenda/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public AprovacaoEmendaUsuario getUsuarioCamara() {
        return usuarioCamara;
    }

    public void setUsuarioCamara(AprovacaoEmendaUsuario usuarioCamara) {
        this.usuarioCamara = usuarioCamara;
    }

    public AprovacaoEmendaUsuario getUsuarioPrefeitura() {
        return usuarioPrefeitura;
    }

    public void setUsuarioPrefeitura(AprovacaoEmendaUsuario usuarioPrefeitura) {
        this.usuarioPrefeitura = usuarioPrefeitura;
    }
}
