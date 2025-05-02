package br.com.webpublico.controle;

import br.com.webpublico.entidades.AgendamentoViagemSaud;
import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.UsuarioSaud;
import br.com.webpublico.enums.SituacaoViagemSaud;
import br.com.webpublico.enums.tributario.MotivoViagemSaud;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AgendamentoViagemSaudFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.ws.model.ConsultaCEP;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Pedro
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarAgendamentoViagemSaud",
        pattern = "/tributario/saud/agendamento-viagem-saud/listar/",
        viewId = "/faces/tributario/saud/agendamentoviagemsaud/lista.xhtml"),
    @URLMapping(id = "novoAgendamentoViagemSaud",
        pattern = "/tributario/saud/agendamento-viagem-saud/novo/",
        viewId = "/faces/tributario/saud/agendamentoviagemsaud/edita.xhtml"),
    @URLMapping(id = "editarAgendamentoViagemSaud",
        pattern = "/tributario/saud/agendamento-viagem-saud/editar/#{agendamentoViagemSaudControlador.id}/",
        viewId = "/faces/tributario/saud/agendamentoviagemsaud/edita.xhtml"),
    @URLMapping(id = "verAgendamentoViagemSaud",
        pattern = "/tributario/saud/agendamento-viagem-saud/ver/#{agendamentoViagemSaudControlador.id}/",
        viewId = "/faces/tributario/saud/agendamentoviagemsaud/visualizar.xhtml")
})
public class AgendamentoViagemSaudControlador extends PrettyControlador<AgendamentoViagemSaud> implements Serializable, CRUD {

    @EJB
    private AgendamentoViagemSaudFacade facade;
    private ConfiguracaoTributario configuracaoTributario;

    public AgendamentoViagemSaudControlador() {
        super(AgendamentoViagemSaud.class);
    }

    @Override
    public AgendamentoViagemSaudFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/saud/agendamento-viagem-saud/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoAgendamentoViagemSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        buscarConfiguracaoTributaria();
        selecionado.getEnderecoOrigem().setCidade(configuracaoTributario.getCidade());
        selecionado.getEnderecoOrigem().setUf(configuracaoTributario.getCidade().getUf());
        selecionado.getEnderecoDestino().setCidade(configuracaoTributario.getCidade());
        selecionado.getEnderecoDestino().setUf(configuracaoTributario.getCidade().getUf());
    }


    @URLAction(mappingId = "editarAgendamentoViagemSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionado.getEnderecoOrigem().setUf(selecionado.getEnderecoOrigem().getCidade().getUf());
        selecionado.getEnderecoDestino().setUf(selecionado.getEnderecoDestino().getCidade().getUf());
    }

    @URLAction(mappingId = "verAgendamentoViagemSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionado.getEnderecoOrigem().setUf(selecionado.getEnderecoOrigem().getCidade().getUf());
        selecionado.getEnderecoDestino().setUf(selecionado.getEnderecoDestino().getCidade().getUf());
    }

    private void buscarConfiguracaoTributaria() {
        if (configuracaoTributario == null) {
            configuracaoTributario = facade.getConfiguracaoTributarioFacade().retornaUltimo();
        }
    }

    public List<Cidade> completarCidadeOrigem(String parte) {
        if (selecionado.getEnderecoOrigem().getUf() == null) return Lists.newArrayList();
        return facade.getCidadeFacade().consultaCidade(selecionado.getEnderecoOrigem().getUf().getUf(), parte);
    }

    public List<Cidade> completarCidadeDestino(String parte) {
        if (selecionado.getEnderecoDestino().getUf() == null) return Lists.newArrayList();
        return facade.getCidadeFacade().consultaCidade(selecionado.getEnderecoDestino().getUf().getUf(), parte);
    }

    public List<UsuarioSaud> completarUsuarioSAUD(String parte) {
        return facade.getUsuarioSaudFacade().buscarFiltrando(parte);
    }

    public String formatarHoraMinuto(Integer hh, Integer mm) {
        return DataUtil.formatarHoraMinuto(hh, mm);
    }

    public void mudouUfOrigem() {
        selecionado.getEnderecoOrigem().setCidade(null);
    }

    public void mudouUfDestino() {
        selecionado.getEnderecoDestino().setCidade(null);
    }

    public List<SelectItem> motivosViagem() {
        return Util.getListSelectItem(MotivoViagemSaud.values(), true);
    }

    public void consultarCEP(boolean enderecoOrigem) {
        if (enderecoOrigem) {
            if (Strings.isNullOrEmpty(selecionado.getEnderecoOrigem().getCep()) ||
                selecionado.getEnderecoOrigem().getCep().length() < 8) return;
            selecionado.getEnderecoOrigem().setBairro("");
            selecionado.getEnderecoOrigem().setLogradouro("");
            selecionado.getEnderecoOrigem().setCidade(null);
            selecionado.getEnderecoOrigem().setUf(null);
            ConsultaCEP consultaCEP = facade.getConsultaCepFacade().buscarCEP(selecionado.getEnderecoOrigem().getCep());
            if (consultaCEP != null) {
                selecionado.getEnderecoOrigem().setBairro(consultaCEP.getBairro());
                selecionado.getEnderecoOrigem().setLogradouro(consultaCEP.getLogradouro());
                Cidade cidade = facade.getCidadeFacade().recuperaCidadePorNomeEEstado(consultaCEP.getLocalidade(),
                    consultaCEP.getUf());
                if (cidade != null) {
                    selecionado.getEnderecoOrigem().setCidade(cidade);
                    selecionado.getEnderecoOrigem().setUf(cidade.getUf());

                }
            }
        } else {
            if (Strings.isNullOrEmpty(selecionado.getEnderecoDestino().getCep()) ||
                selecionado.getEnderecoDestino().getCep().length() < 8) return;
            selecionado.getEnderecoDestino().setBairro("");
            selecionado.getEnderecoDestino().setLogradouro("");
            selecionado.getEnderecoDestino().setCidade(null);
            selecionado.getEnderecoDestino().setUf(null);
            ConsultaCEP consultaCEP = facade.getConsultaCepFacade().buscarCEP(selecionado.getEnderecoDestino().getCep());
            if (consultaCEP != null) {
                selecionado.getEnderecoDestino().setBairro(consultaCEP.getBairro());
                selecionado.getEnderecoDestino().setLogradouro(consultaCEP.getLogradouro());
                Cidade cidade = facade.getCidadeFacade().recuperaCidadePorNomeEEstado(consultaCEP.getLocalidade(),
                    consultaCEP.getUf());
                if (cidade != null) {
                    selecionado.getEnderecoDestino().setCidade(cidade);
                    selecionado.getEnderecoDestino().setUf(cidade.getUf());

                }
            }
        }
    }

    public void aprovar() {
        facade.aprovar(selecionado);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public void iniciarRejeicao() {
        selecionado.setObservacaoAvaliacao("");
    }

    public void salvarRejeicao() {
        if (Strings.isNullOrEmpty(selecionado.getObservacaoAvaliacao())) {
            FacesUtil.addCampoObrigatorio("O campo Motivo deve ser informado.");
            return;
        }
        facade.rejeitar(selecionado);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }
}
