package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FeiraFacade;
import br.com.webpublico.util.StringUtil;
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
    @URLMapping(id = "nova-feira", pattern = "/feira/novo/", viewId = "/faces/administrativo/feiras/feira/edita.xhtml"),
    @URLMapping(id = "editar-feira", pattern = "/feira/editar/#{feiraControlador.id}/", viewId = "/faces/administrativo/feiras/feira/edita.xhtml"),
    @URLMapping(id = "listar-feira", pattern = "/feira/listar/", viewId = "/faces/administrativo/feiras/feira/lista.xhtml"),
    @URLMapping(id = "ver-feira", pattern = "/feira/ver/#{feiraControlador.id}/", viewId = "/faces/administrativo/feiras/feira/visualizar.xhtml"),
})
public class FeiraControlador extends PrettyControlador<Feira> implements Serializable, CRUD {

    @EJB
    private FeiraFacade facade;
    private String enderecoFeira;
    private List<Feirante> feirantes;

    public FeiraControlador() {
        super(Feira.class);
    }

    @Override
    @URLAction(mappingId = "nova-feira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setEndereco(new EnderecoCorreio());
    }

    @Override
    @URLAction(mappingId = "ver-feira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        feirantes = facade.getFeiranteFacade().buscarFeirante(selecionado, "");
    }

    @Override
    @URLAction(mappingId = "editar-feira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/feira/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public boolean validaRegrasEspecificas() {
        validarCampos();
        return super.validaRegrasEspecificas();
    }

    public List<PessoaFisica> completarResponsavel(String parte) {
        return facade.getPessoaFisicaFacade().listaFiltrandoTodasPessoasByNomeAndCpf(parte);
    }

    public List<String> completarCEP(String parte) {
        return facade.getPessoaFisicaFacade().getConsultaCepFacade().consultaLogradouroPorParteCEPByString(parte.trim());
    }

    public void atualizarLogradouros() {
        facade.getPessoaFisicaFacade().getConsultaCepFacade().atualizarLogradouros(selecionado.getEndereco());
    }

    public List<CEPUF> getListaUF() {
        return facade.getPessoaFisicaFacade().getConsultaCepFacade().listaUf();
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getEndereco().getCep() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo CEP é de preenchimento obrigatório.");
        }
        if (selecionado.getEndereco().getUf().trim().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Estado é de preenchimento obrigatório.");
        }
        if (selecionado.getEndereco().getLocalidade().trim().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cidade é de preenchimento obrigatório.");
        }
        if (selecionado.getEndereco().getBairro().trim().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Bairro é de preenchimento obrigatório.");
        }
        if (selecionado.getEndereco().getLogradouro().trim().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Logradouro é de preenchimento obrigatório.");
        }
        ve.lancarException();
    }

    public String getEnderecoFeira() {
        return enderecoFeira = selecionado.getEndereco().getLogradouro() +
            " - " + selecionado.getEndereco().getBairro() +
            " - " + selecionado.getEndereco().getLocalidade() +
            ", " + selecionado.getEndereco().getUf() +
            " - " + selecionado.getEndereco().getCep();
    }

    public void setEnderecoFeira(String enderecoFeira) {
        this.enderecoFeira = enderecoFeira;
    }

    public String getEnderecoFeiraLimpo() {
        if (enderecoFeira != null) {
            return StringUtil.removeAcentuacao(enderecoFeira).replace(" ", "+");
        }
        return "";
    }

    public String getUrlMapa() {
        if (enderecoFeira != null) {
            return "https://www.google.com.br/maps?q=" + getEnderecoFeiraLimpo() + "&output=embed&z=18";
        }
        return "";
    }

    public List<Feirante> getFeirantes() {
        return feirantes;
    }

    public void setFeirantes(List<Feirante> feirantes) {
        this.feirantes = feirantes;
    }
}
