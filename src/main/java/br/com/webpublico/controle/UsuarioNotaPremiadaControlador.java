package br.com.webpublico.controle;


import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.UsuarioNotaPremiadaFacade;
import br.com.webpublico.nfse.domain.DadosPessoaisNfse;
import br.com.webpublico.nfse.domain.UsuarioNotaPremiada;
import br.com.webpublico.nfse.domain.dtos.PessoaNfseDTO;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.ws.model.ConsultaCEP;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.logging.log4j.util.Strings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(
        id = "usuarioNotaPremiadaNovo",
        pattern = "/usuario-nota-premiada/novo/",
        viewId = "/faces/tributario/nfse/notapremiada/usuario/edita.xhtml"),
    @URLMapping(
        id = "usuarioNotaPremiadaListar",
        pattern = "/usuario-nota-premiada/listar/",
        viewId = "/faces/tributario/nfse/notapremiada/usuario/lista.xhtml"),
    @URLMapping(
        id = "usuarioNotaPremiadaEditar",
        pattern = "/usuario-nota-premiada/editar/#{usuarioNotaPremiadaControlador.id}/",
        viewId = "/faces/tributario/nfse/notapremiada/usuario/edita.xhtml"),
    @URLMapping(
        id = "usuarioNotaPremiadaVer",
        pattern = "/usuario-nota-premiada/ver/#{usuarioNotaPremiadaControlador.id}/",
        viewId = "/faces/tributario/nfse/notapremiada/usuario/visualizar.xhtml"),
})
public class UsuarioNotaPremiadaControlador extends PrettyControlador<UsuarioNotaPremiada> implements Serializable, CRUD {

    @EJB
    private UsuarioNotaPremiadaFacade facade;
    private String cpfCnpj;

    public UsuarioNotaPremiadaControlador() {
        super(UsuarioNotaPremiada.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/usuario-nota-premiada/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "usuarioNotaPremiadaListar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "usuarioNotaPremiadaNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "usuarioNotaPremiadaEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        this.cpfCnpj = selecionado.getDadosPessoais().getCpfCnpj();
    }

    @Override
    @URLAction(mappingId = "usuarioNotaPremiadaVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void selecionarPessoa(ActionEvent evento) {
        Pessoa pessoa = (Pessoa) evento.getComponent().getAttributes().get("objeto");
        handlePessoa(pessoa.getCpf_Cnpj(), pessoa);
    }

    public void buscarPessoa() {
        Pessoa pessoa = null;
        if (!Strings.isEmpty(this.cpfCnpj)) {
            pessoa = facade.getPessoaFacade().buscarPessoaPorCpfOrCnpj(StringUtil.retornaApenasNumeros(this.cpfCnpj));
        }
        handlePessoa(this.cpfCnpj, pessoa);
    }

    public void handlePessoa(String cpfCnpj, Pessoa pessoa) {
        cpfCnpj = StringUtil.retornaApenasNumeros(cpfCnpj);
        if (cpfCnpj.equals(selecionado.getDadosPessoais().getCpfCnpj()))
            return;
        if (pessoa != null) {
            PessoaNfseDTO pessoaNfseDTO = pessoa.toNfseDto();
            selecionado.setDadosPessoais(new DadosPessoaisNfse(pessoaNfseDTO.getDadosPessoais()));
        } else {
            selecionado.setDadosPessoais(new DadosPessoaisNfse());
            selecionado.getDadosPessoais().setCpfCnpj(cpfCnpj);
        }
        selecionado.setLogin(StringUtil.retornaApenasNumeros(selecionado.getDadosPessoais().getCpfCnpj()));
    }

    @Override
    public void salvar() {
        try {
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void ativarDesativarUsuario() {
        selecionado.setAtivo(!selecionado.getAtivo());
        selecionado = facade.salvarRetornando(selecionado);
        FacesUtil.addOperacaoRealizada("Usuário " + (selecionado.getAtivo() ? "Ativado" : "Inativado") + " com sucesso!");
    }


    public void handleCEP() {
        ConsultaCEP consultaCEP = facade.getConsultaCepFacade().buscarCEP(selecionado.getDadosPessoais().getCep());
        if (consultaCEP != null) {
            selecionado.getDadosPessoais().setBairro(consultaCEP.getBairro());
            selecionado.getDadosPessoais().setCep(consultaCEP.getCep());
            selecionado.getDadosPessoais().setLogradouro(consultaCEP.getLogradouro());
            selecionado.getDadosPessoais().setUf(consultaCEP.getUf());
            selecionado.getDadosPessoais().setMunicipio(consultaCEP.getLocalidade());
        }
    }

    @Override
    public List<UsuarioNotaPremiada> completarEstaEntidade(String parte) {
        return facade.listaFiltrando(parte.trim(), "dadosPessoais.cpfCnpj", "dadosPessoais.nomeRazaoSocial");
    }
}
