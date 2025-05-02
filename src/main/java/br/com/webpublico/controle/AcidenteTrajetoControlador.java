package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.PercursoAcidente;
import br.com.webpublico.enums.TipoTransporte;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AcidenteTrajetoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created by carlos on 05/10/15.
 */
@ManagedBean(name = "acidenteTrajetoControlador")
@ViewScoped
@URLMappings(
        mappings = {
                @URLMapping(id = "listarAcidenteTrajeto", pattern = "/acidente-trajeto/listar/", viewId = "/faces/rh/administracaodepagamento/acidentetrajeto/lista.xhtml"),
                @URLMapping(id = "criarAcidenteTrajeto", pattern = "/acidente-trajeto/novo/", viewId = "/faces/rh/administracaodepagamento/acidentetrajeto/edita.xhtml"),
                @URLMapping(id = "editarAcidenteTrajeto", pattern = "/acidente-trajeto/editar/#{acidenteTrajetoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/acidentetrajeto/edita.xhtml"),
                @URLMapping(id = "verAcidenteTrajeto", pattern = "/acidente-trajeto/ver/#{acidenteTrajetoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/acidentetrajeto/visualizar.xhtml")
        }
)
public class AcidenteTrajetoControlador extends PrettyControlador<AcidenteTrajeto> implements Serializable, CRUD {

    @EJB
    private AcidenteTrajetoFacade acidenteTrajetoFacade;
    private Integer indiceAba;
    private Integer jornadaTrabalho;
    private ConverterAutoComplete converterAutoCompleteContratoFP;
    private String endereco;
    private String numero;
    private String uf;
    private String bairro;
    private String cep;
    private String complemento;
    private String cidade;
    private Boolean isMostrarPanelDados;
    private Boolean isMostrarPanelEndereco;
    private Boolean isMostrarAbaDadosAcidente;


    @Override
    public AbstractFacade getFacede() {
        return acidenteTrajetoFacade;
    }

    public AcidenteTrajetoControlador() {
        super(AcidenteTrajeto.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/acidente-trajeto/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "criarAcidenteTrajeto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        indiceAba = 0;
        isMostrarPanelDados = false;
        isMostrarPanelEndereco = false;
        isMostrarAbaDadosAcidente = false;


    }

    @Override
    @URLAction(mappingId = "verAcidenteTrajeto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }


    @Override
    @URLAction(mappingId = "editarAcidenteTrajeto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        isMostrarPanelDados = true;
        isMostrarPanelEndereco = true;
        adicionarEnderecoServidor();
        isMostrarAbaDadosAcidente = true;
    }

    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    @Override
    public void salvar() {
        if (isValidarRegrasParaSalvar()) {
            super.salvar();
        }

    }


    public Boolean isValidarRegrasParaSalvar() {
        Boolean isValido = true;
        if (selecionado.getContratoFP() == null) {
            FacesUtil.addCampoObrigatorio("O campo Servidor deve ser informado!");
            isValido = false;
        }
        if (selecionado.getPercursoAcidente() == null || selecionado.getPercursoAcidente().getDescricao().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Percurso Acidente deve ser informado!");
            isValido = false;
        }
        if (selecionado.getOcorrencia() == null || selecionado.getOcorrencia().toString().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Data da Ocorrência deve ser informado!");
            isValido = false;
        }
        if (selecionado.getHorario() == null || selecionado.getHorario().toString().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Horário do Acidente deve ser informado!");
        }
        if (selecionado.getSaidaLocal() == null || selecionado.getSaidaLocal().toString().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Horário de Saida do Local do Acidente deve ser informado!");
            isValido = false;
        }
        if (selecionado.getCargaHoraria() == null || selecionado.getCargaHoraria().toString().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Carga Horário Acidente deve ser informado!");
            isValido = false;
        }
        if (selecionado.getTrajeto() == null || selecionado.getTrajeto().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Trajeto do Servidor deve ser informado!");
            isValido = false;
        }
        if (selecionado.getTipoVeiculo() == null || selecionado.getTipoVeiculo().toString().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Meio de Locomoção deve ser informado!");
            isValido = false;
        }
        if (selecionado.getLocal() == null || selecionado.getLocal().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Local do Acidente deve ser informado!");
            isValido = false;
        }
        if (selecionado.getIsMudancaTrajeto() == null || selecionado.getIsMudancaTrajeto().toString().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Houve Mudança no Trajeto deve ser informado!");
            isValido = false;
        }
        if (selecionado.getIsConhecimentoPolicial() == null || selecionado.getIsConhecimentoPolicial().toString().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Autoridade Policial com Conhecimento da Ocorrência deve ser informado!");
            isValido = false;
        }
        if (selecionado.getIsDispositivoLegal() == null || selecionado.getIsDispositivoLegal().toString().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Enquadramento do Acidente nos Dispositivos Legais deve ser informado!");
            isValido = false;
        }
        if (selecionado.getConsideracao() == null || selecionado.getConsideracao().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Outras Considerações deve ser informado!");
            isValido = false;
        }
        return isValido;
    }

    public List<ContratoFP> completaServidor(String parte) {
        return acidenteTrajetoFacade.getContratoFPFacade().recuperaContratoMatricula(parte.trim());
    }

    public List<UnidadeOrganizacional> completaUnidade(String parte) {
        return acidenteTrajetoFacade.getUnidadeOrganizacionalFacade().listaTodosPorFiltro(parte);
    }

    public List<SelectItem> getTipoVeiculo() {
        return Util.getListSelectItem(TipoTransporte.values());
    }

    public List<SelectItem> getHouveMudanca() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(false, "Não"));
        retorno.add(new SelectItem(true, "Sim"));
        return retorno;
    }

    public List<SelectItem> getConhecimentoPolicial() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(false, "Não"));
        retorno.add(new SelectItem(true, "Sim"));
        return retorno;
    }

    public List<SelectItem> getEnquadramentoLegal() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(false, "Não"));
        retorno.add(new SelectItem(true, "Sim"));
        return retorno;
    }

    public void carregarDadosServidor() {
        isMostrarPanelDados = true;
        definirEnderecoVazio();
        adicionarEnderecoServidor();
        if (selecionado.getContratoFP() != null) {
            isMostrarAbaDadosAcidente = true;
        }
        recuperarUnidadeServidor();

    }


    private void recuperarUnidadeServidor() {
        UnidadeOrganizacional unidade = acidenteTrajetoFacade.getUnidadeOrganizacionalFacade().recuperar(
                selecionado.getContratoFP().getUnidadeOrganizacional().getId());
        selecionado.setUnidadeOrganizacional(unidade);
    }

    public void adicionarEnderecoServidor() {
        EnderecoCorreio endereco = recuperarEnderecoPrincipalOrNaoPrincipal();
        if (endereco != null) {
            isMostrarPanelEndereco = true;
            definirEndereco(endereco);
        } else {
            definirEnderecoVazio();
            FacesUtil.addAtencao("Não foi possível localizar endereço cadastrado para o servidor");
            isMostrarPanelEndereco = false;
        }
    }

    private void definirEnderecoVazio() {
        setEndereco("");
        setComplemento("");
        setCep("");
        setBairro("");
        setUf("");
        setCidade("");
        setNumero("");
    }

    private EnderecoCorreio recuperarEnderecoNaoPrincipal() {
        List<EnderecoCorreio> enderecos;
        Pessoa pessoa = selecionado.getContratoFP().getMatriculaFP().getPessoa();
        enderecos = acidenteTrajetoFacade.getPessoaFisicaFacade().buscarEnderecoPessoa(pessoa);
        if (!enderecos.isEmpty()) {
            return enderecos.get(0);
        }
        return null;
    }


    private void definirEndereco(EnderecoCorreio enderecoPrincipal) {

        if (enderecoPrincipal.getBairro() != null) {
            setBairro(enderecoPrincipal.getBairro());
        } else {
            setBairro("");
        }
        if (enderecoPrincipal.getCep() != null) {
            setCep(enderecoPrincipal.getCep());
        } else {
            setCep("");
        }
        if (enderecoPrincipal.getLocalidade() != null) {
            setCidade(enderecoPrincipal.getLocalidade());
        } else {
            setCidade("");
        }
        if (enderecoPrincipal.getComplemento() != null) {
            setComplemento(enderecoPrincipal.getComplemento());
        } else {
            setComplemento("");
        }
        if (enderecoPrincipal.getNumero() != null) {
            setNumero(enderecoPrincipal.getNumero());
        } else {
            setNumero("");
        }
        if (enderecoPrincipal.getLogradouro() != null) {
            setEndereco(enderecoPrincipal.getLogradouro());
        } else {
            setEndereco("");
        }
        if (enderecoPrincipal.getUf() != null) {
            setUf(enderecoPrincipal.getUf());
        } else {
            setUf("");
        }

    }


    private EnderecoCorreio recuperarEnderecoPrincipalOrNaoPrincipal() {
        EnderecoCorreio enderecoCorreio = selecionado.getContratoFP().getMatriculaFP().getPessoa().getEnderecoPrincipal();
        if (enderecoCorreio != null) {
            return enderecoCorreio;
        } else {
            enderecoCorreio = recuperarEnderecoNaoPrincipal();
            if (enderecoCorreio != null) {
                return enderecoCorreio;
            }
        }
        return null;
    }

    public List<SelectItem> percursoAcidentes() {
        return Util.getListSelectItem(PercursoAcidente.values());
    }


    public Integer getJornadaTrabalho() {
        return jornadaTrabalho;
    }

    public void setJornadaTrabalho(Integer jornadaTrabalho) {
        this.jornadaTrabalho = jornadaTrabalho;
    }

    public ConverterAutoComplete getConverterAutoCompleteContratoFP() {
        if (converterAutoCompleteContratoFP == null) {
            converterAutoCompleteContratoFP = new ConverterAutoComplete(ContratoFP.class, acidenteTrajetoFacade.getContratoFPFacade());
        }
        return converterAutoCompleteContratoFP;
    }

    public void setConverterAutoCompleteContratoFP(ConverterAutoComplete converterAutoCompleteContratoFP) {
        this.converterAutoCompleteContratoFP = converterAutoCompleteContratoFP;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Boolean getIsMostrarPanelDados() {
        return isMostrarPanelDados;
    }

    public void setIsMostrarPanelDados(Boolean isMostrarPanelDados) {
        this.isMostrarPanelDados = isMostrarPanelDados;
    }

    public Boolean getIsMostrarPanelEndereco() {
        return isMostrarPanelEndereco;
    }

    public void setIsMostrarPanelEndereco(Boolean isMostrarPanelEndereco) {
        this.isMostrarPanelEndereco = isMostrarPanelEndereco;
    }

    public Boolean getIsMostrarAbaDadosAcidente() {
        return isMostrarAbaDadosAcidente;
    }

    public void setIsMostrarAbaDadosAcidente(Boolean isMostrarAbaDadosAcidente) {
        this.isMostrarAbaDadosAcidente = isMostrarAbaDadosAcidente;
    }
}
