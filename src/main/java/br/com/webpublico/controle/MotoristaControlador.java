package br.com.webpublico.controle;

import br.com.webpublico.converter.ConverterMotorista;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.administrativo.frotas.TipoMotorista;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 03/09/14
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "motoristaNovo", pattern = "/frota/motorista/novo/", viewId = "/faces/administrativo/frota/motorista/edita.xhtml"),
        @URLMapping(id = "motoristaListar", pattern = "/frota/motorista/listar/", viewId = "/faces/administrativo/frota/motorista/lista.xhtml"),
        @URLMapping(id = "motoristaEditar", pattern = "/frota/motorista/editar/#{motoristaControlador.id}/", viewId = "/faces/administrativo/frota/motorista/edita.xhtml"),
        @URLMapping(id = "motoristaVer", pattern = "/frota/motorista/ver/#{motoristaControlador.id}/", viewId = "/faces/administrativo/frota/motorista/visualizar.xhtml"),
})
public class MotoristaControlador extends PrettyControlador<Motorista> implements Serializable, CRUD {

    @EJB
    private MotoristaFacade motoristaFacade;
    private RG rgPessoaSelecionada;
    private Habilitacao habilitacaoPessoaSelecionada;
    private List<Telefone> telefonesPessoaSelecionado;
    private List<EnderecoCorreio> enderecoPessoaSelecionado;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ConverterMotorista converterMotorista;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    public MotoristaControlador() {
        super(Motorista.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/motorista/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return motoristaFacade;
    }

    @URLAction(mappingId = "motoristaNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        recuperaDadosPessoa();
        carregarUnidadeOrganizacional();
    }

    @URLAction(mappingId = "motoristaVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaDadosPessoa();
        carregarUnidadeOrganizacional();
    }

    @URLAction(mappingId = "motoristaEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaDadosPessoa();
        carregarUnidadeOrganizacional();
    }

    private void carregarUnidadeOrganizacional() {
        if (selecionado != null && selecionado.getUnidadeOrganizacional() != null) {
            selecionado.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(),
                    selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
        }
    }

    public RG getRgPessoaSelecionada() {
        return rgPessoaSelecionada;
    }

    public void setRgPessoaSelecionada(RG rgPessoaSelecionada) {
        this.rgPessoaSelecionada = rgPessoaSelecionada;
    }

    public Habilitacao getHabilitacaoPessoaSelecionada() {
        return habilitacaoPessoaSelecionada;
    }

    public void setHabilitacaoPessoaSelecionada(Habilitacao habilitacaoPessoaSelecionada) {
        this.habilitacaoPessoaSelecionada = habilitacaoPessoaSelecionada;
    }

    public List<Telefone> getTelefonesPessoaSelecionado() {
        return telefonesPessoaSelecionado;
    }

    public void setTelefonesPessoaSelecionado(List<Telefone> telefonesPessoaSelecionado) {
        this.telefonesPessoaSelecionado = telefonesPessoaSelecionado;
    }

    public List<EnderecoCorreio> getEnderecoPessoaSelecionado() {
        return enderecoPessoaSelecionado;
    }

    public void setEnderecoPessoaSelecionado(List<EnderecoCorreio> enderecoPessoaSelecionado) {
        this.enderecoPessoaSelecionado = enderecoPessoaSelecionado;
    }

    public List<SelectItem> tipos() {
        return Util.getListSelectItem(Arrays.asList(TipoMotorista.values()));
    }

    public void processaMudancaDeTipo() {
        selecionado.setPessoaFisica(null);
        rgPessoaSelecionada = new RG();
        habilitacaoPessoaSelecionada = new Habilitacao();
        telefonesPessoaSelecionado = new ArrayList();
        enderecoPessoaSelecionado = new ArrayList();
    }

    public void recuperaDadosPessoa() {
        if (selecionado.getPessoaFisica() != null) {
            PessoaFisica pessoaFisica = (PessoaFisica) motoristaFacade.getPessoaFacade().recuperarPF(selecionado.getPessoaFisica().getId());
            rgPessoaSelecionada = pessoaFisica.getRg();
            habilitacaoPessoaSelecionada = pessoaFisica.getHabilitacao();
            telefonesPessoaSelecionado = pessoaFisica.getTelefones();
            enderecoPessoaSelecionado = pessoaFisica.getEnderecoscorreio();
            if (rgPessoaSelecionada == null) {
                rgPessoaSelecionada = new RG();
            }
            if (habilitacaoPessoaSelecionada == null) {
                habilitacaoPessoaSelecionada = new Habilitacao();
            }
            FacesUtil.atualizarComponente("Formulario:panel-geral");
        }
    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean validou = true;
        if (motoristaFacade.existeMotoristaRegistrado(selecionado)) {
            validou = false;
            FacesUtil.addOperacaoNaoPermitida("O motorista já encontra-se cadastrado.");
        }
        if (habilitacaoPessoaSelecionada == null || habilitacaoPessoaSelecionada.getId() == null) {
            validou = false;
            FacesUtil.addOperacaoNaoPermitida("O motorista selecionado não possui uma habilitação registrada.");
        } else if (habilitacaoPessoaSelecionada.getValidade() == null) {
            validou = false;
            FacesUtil.addOperacaoNaoPermitida("A habilitação do motorista selecionado não possui data de validade.");
        } else if (habilitacaoPessoaSelecionada.getValidade().compareTo(sistemaFacade.getDataOperacao()) < 0) {
            validou = false;
            FacesUtil.addOperacaoNaoPermitida("A habilitação do motorista selecionado está vencida.");
        }
        if (selecionado.getHierarquiaOrganizacional() == null) {
            validou = false;
            FacesUtil.addCampoObrigatorio("O Órgão/Entidade/Fundo deve ser informado.");
        } else {
            selecionado.setUnidadeOrganizacional(selecionado.getHierarquiaOrganizacional().getSubordinada());
        }
        if (selecionado.getInicioVigencia() != null && selecionado.getFinalVigencia() != null &&
                selecionado.getInicioVigencia().compareTo(selecionado.getFinalVigencia()) > 0) {
            validou = false;
            FacesUtil.addCampoObrigatorio("O Início de Vigência não pode ser superior ao Final de Vigência.");
        }
        if (selecionado.getId() == null &&
                selecionado.getFinalVigencia() != null &&
                selecionado.getFinalVigencia().compareTo(sistemaFacade.getDataOperacao()) < 0) {
            validou = false;
            FacesUtil.addCampoObrigatorio("O Final de Vigência deve ser superior a data atual.");
        }
        return validou;
    }

    public List<Motorista> completaMotorista(String parte) {
        return motoristaFacade.buscarMotoristas(parte);
    }

    public void poeNaSessao() throws IllegalAccessException {
        Web.poeNaSessao(selecionado);
    }

    public ConverterMotorista getConverterMotorista() {
        if (converterMotorista == null) {
            converterMotorista = new ConverterMotorista(motoristaFacade);
        }
        return converterMotorista;

    }

    public List<ContratoFP> cargosDoServidor() {
        if(selecionado.getPessoaFisica() == null){
            return new ArrayList();
        }
        return contratoFPFacade.listaContratosVigentesPorPessoaFisica(selecionado.getPessoaFisica());
    }
}
