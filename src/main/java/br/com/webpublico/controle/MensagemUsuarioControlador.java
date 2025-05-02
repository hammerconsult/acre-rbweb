/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.MensagemUsuario;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MensagemUsuarioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

@ManagedBean(name = "mensagemUsuarioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaMensagemUsuario", pattern = "/mensagem-todos-usuarios/novo/", viewId = "/faces/admin/mensagemtodosusuarios/edita.xhtml"),
    @URLMapping(id = "editarMensagemUsuario", pattern = "/mensagem-todos-usuarios/editar/#{mensagemUsuarioControlador.id}/", viewId = "/faces/admin/mensagemtodosusuarios/edita.xhtml"),
    @URLMapping(id = "verMensagemUsuario", pattern = "/mensagem-todos-usuarios/ver/#{mensagemUsuarioControlador.id}/", viewId = "/faces/admin/mensagemtodosusuarios/visualizar.xhtml"),
    @URLMapping(id = "listarMensagemUsuario", pattern = "/mensagem-todos-usuarios/listar/", viewId = "/faces/admin/mensagemtodosusuarios/lista.xhtml")
})
public class MensagemUsuarioControlador extends PrettyControlador<MensagemUsuario> implements Serializable, CRUD {

    @EJB
    private MensagemUsuarioFacade mensagemUsuarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    private Boolean usaCron = false;
    private String horaInicio, minutoInicio, horaFim, minutoFim;

    public MensagemUsuarioControlador() {
        super(MensagemUsuario.class);
    }

    @URLAction(mappingId = "novaMensagemUsuario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        this.selecionado.setEmitidaEm(new Date());
        this.selecionado.setEnviadaPor(sistemaFacade.getUsuarioCorrente());
    }

    @URLAction(mappingId = "editarMensagemUsuario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (!Strings.isNullOrEmpty(selecionado.getCronPublicar())) {
            String[] parts = selecionado.getCronPublicar().split(" ");
            minutoInicio = parts[1];
            horaInicio = parts[2];
        }
        if (!Strings.isNullOrEmpty(selecionado.getCronRemover())) {
            String[] parts = selecionado.getCronRemover().split(" ");
            minutoFim = parts[1];
            horaFim = parts[2];
        }
    }

    @URLAction(mappingId = "verMensagemUsuario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/mensagem-todos-usuarios/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return mensagemUsuarioFacade;
    }

    @Override
    protected String getMensagemSucessoAoSalvar() {
        return "A mensagemUsuario " + selecionado.getTitulo() + " foi salva com sucesso.";
    }

    @Override
    protected String getMensagemSucessoAoExcluir() {
        return "A mensagemUsuario " + selecionado.getConteudo() + " foi excluída com sucesso.";
    }

    @Override
    public void salvar() {
        super.salvar();
        mensagemUsuarioFacade.getComunicacaoTodosUsuariosSingleton().agendaTarefas();
    }

    public void utilizarCron() {
        usaCron = !usaCron;
//        calcularCron();
    }

    public void calcularCron() {
        if (!Strings.isNullOrEmpty(horaInicio) && !Strings.isNullOrEmpty(minutoInicio)) {
            selecionado.setCronPublicar("0 " + minutoInicio + " " + horaInicio + " ? * *");
        }
        if (!Strings.isNullOrEmpty(horaFim) && !Strings.isNullOrEmpty(minutoFim)) {
            selecionado.setCronRemover("0 " + minutoFim + " " + horaFim + " ? * *");
        }
    }

    public Boolean getUsaCron() {
        return usaCron;
    }

    public void setUsaCron(Boolean usaCron) {
        this.usaCron = usaCron;
    }

    public void removerPublicacao() {
        mensagemUsuarioFacade.remover(selecionado);
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getMinutoInicio() {
        return minutoInicio;
    }

    public void setMinutoInicio(String minutoInicio) {
        this.minutoInicio = minutoInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }

    public String getMinutoFim() {
        return minutoFim;
    }

    public void setMinutoFim(String minutoFim) {
        this.minutoFim = minutoFim;
    }
}
