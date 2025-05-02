/*
 * Codigo gerado automaticamente em Thu Jun 30 13:39:08 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContaBancariaEntidadeFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-conta-bancaria", pattern = "/conta-bancaria/novo/", viewId = "/faces/tributario/cadastromunicipal/contabancariaentidade/edita.xhtml"),
    @URLMapping(id = "editar-conta-bancaria", pattern = "/conta-bancaria/editar/#{contaBancariaEntidadeControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/contabancariaentidade/edita.xhtml"),
    @URLMapping(id = "ver-conta-bancaria", pattern = "/conta-bancaria/ver/#{contaBancariaEntidadeControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/contabancariaentidade/visualizar.xhtml"),
    @URLMapping(id = "listar-conta-bancaria", pattern = "/conta-bancaria/listar/", viewId = "/faces/tributario/cadastromunicipal/contabancariaentidade/lista.xhtml")
})
public class ContaBancariaEntidadeControlador extends PrettyControlador<ContaBancariaEntidade> implements Serializable, CRUD {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    private ConverterAutoComplete converterAgencia;
    private ConverterAutoComplete converterFonteDeRecursos;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private ConverterAutoComplete converterEntidade;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterBanco;
    private SubConta subConta;
    private Boolean liberado;
    private MoneyConverter moneyConverter;
    private Banco banco;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private FonteDeRecursos fonteDeRecursos;
    private Boolean hasContaPrincipalFP;

    public ContaBancariaEntidadeControlador() {
        metadata = new EntidadeMetaData(ContaBancariaEntidade.class);
    }

    public ContaBancariaEntidadeFacade getFacade() {
        return contaBancariaEntidadeFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return contaBancariaEntidadeFacade;
    }

    @URLAction(mappingId = "ver-conta-bancaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-conta-bancaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        setaBancoDaAgenciaDaConta();
        hasContaPrincipalFP = contaBancariaEntidadeFacade.hasContaBancariaPrincipalFP(selecionado.getId());
    }

    private void setaBancoDaAgenciaDaConta() {
        banco = contaBancariaEntidadeFacade.recuperaBancoPorAgencica(selecionado.getAgencia());
    }

    @URLAction(mappingId = "novo-conta-bancaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ContaBancariaEntidade cbe = ((ContaBancariaEntidade) selecionado);
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        liberado = false;
        banco = null;
        subConta = new SubConta();
        fonteDeRecursos = new FonteDeRecursos();
        cbe.setDataAbertura(new Date());
        cbe.setSituacao(SituacaoConta.ATIVO);
        hasContaPrincipalFP = contaBancariaEntidadeFacade.hasContaBancariaPrincipalFP(selecionado.getId());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/conta-bancaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void selecionar(ActionEvent evento) {
        operacao = Operacoes.EDITAR;
        ContaBancariaEntidade conta = (ContaBancariaEntidade) evento.getComponent().getAttributes().get("objeto");
        selecionado = contaBancariaEntidadeFacade.recuperar(conta.getId());
        conta = ((ContaBancariaEntidade) selecionado);
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        banco = conta.getAgencia().getBanco();
        liberado = false;
        subConta = new SubConta();
        subConta.setContaBancariaEntidade(conta);
    }

    public List getLista() {
        return contaBancariaEntidadeFacade.lista();
    }

    @Override
    public void salvar() {
        try {
            if (!validaCampos()) {
                return;
            }
            if (Util.validaCampos(selecionado) && validaDuplicarCodigoContaBancaria()) {
                if (operacao.equals(Operacoes.NOVO)) {
                    contaBancariaEntidadeFacade.salvarNovo(selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                } else {
                    contaBancariaEntidadeFacade.salvar(selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro alterado com sucesso."));
                }
                redireciona();
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError("Operação não Realizada! ", " Erro ao Salvar: " + ex.getMessage());
        }
    }

    public boolean validaCampos() {
        if (banco == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Banco é obrigatório"));
            return false;
        } else if (selecionado.getAgencia() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Agência é obrigatório"));
            return false;
        }
        return true;
    }

    @Override
    public void cancelar() {
        super.cancelar();
        liberado = false;
    }

    public boolean validaDuplicarCodigoContaBancaria() {
        if (contaBancariaEntidadeFacade.validaContaBancMesmoCodigoEAgencia(selecionado)) {
            return true;
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao salvar ", " Já existe uma Conta Bancária cadastrada com o código: "
            + selecionado.getNumeroConta() + " - " + selecionado.getDigitoVerificador() + " para a Agência: " + selecionado.getAgencia().agenciaEDigitoVerificador() + ", " + selecionado.getAgencia().getNomeAgencia()));
        return false;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public ConverterAutoComplete getConverterAgencia() {
        if (converterAgencia == null) {
            converterAgencia = new ConverterAutoComplete(Agencia.class, contaBancariaEntidadeFacade.getAgenciaFacade());
        }
        return converterAgencia;
    }

    public ConverterAutoComplete getConverterBanco() {
        if (converterBanco == null) {
            converterBanco = new ConverterAutoComplete(Banco.class, contaBancariaEntidadeFacade.getBancoFacade());
        }
        return converterBanco;
    }


    public List<Banco> completaBanco(String parte) {
        return contaBancariaEntidadeFacade.getBancoFacade().listaBancoPorCodigoOuNome(parte.trim());
    }

    public List<Agencia> completaAgencias(String parte) {
        return contaBancariaEntidadeFacade.getAgenciaFacade().listaFiltrandoPorBanco(parte.trim(), banco);
    }

    public Converter getConverterFonteDeRecursos() {
        if (converterFonteDeRecursos == null) {
            converterFonteDeRecursos = new ConverterAutoComplete(FonteDeRecursos.class, contaBancariaEntidadeFacade.getFonteDeRecursosFacade());
        }
        return converterFonteDeRecursos;
    }

    public List<FonteDeRecursos> completaFonteDeRecursos(String parte) {
        return contaBancariaEntidadeFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public Converter getConverterEntidades() {
        if (converterEntidade == null) {
            converterEntidade = new ConverterAutoComplete(Entidade.class, contaBancariaEntidadeFacade.getEntidadeFacade());
        }
        return converterEntidade;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, contaBancariaEntidadeFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return contaBancariaEntidadeFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", "ORCAMENTARIA", sistemaControlador.getDataOperacao());
    }

    public List<Entidade> completaEntidades(String parte) {
        return contaBancariaEntidadeFacade.getEntidadeFacade().listaFiltrando(parte.trim(), "nome");
    }

    public void setaEntidade(SelectEvent evt) {
        Entidade e = (Entidade) evt.getObject();
        ((ContaBancariaEntidade) selecionado).setEntidade(e);
    }

    public List<Conta> completaContaContabil(String parte) {
        return contaBancariaEntidadeFacade.getContaFacade().listaContasContabeis(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, contaBancariaEntidadeFacade.getContaFacade());
        }
        return converterConta;
    }

    public List<SelectItem> getTiposContaBancaria() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoContaBancaria obj : TipoContaBancaria.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getModalidadesConta() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(ModalidadeConta.CONTA_CORRENTE, ModalidadeConta.CONTA_CORRENTE.getDescricao()));
        toReturn.add(new SelectItem(ModalidadeConta.CONTA_POUPANCA, ModalidadeConta.CONTA_POUPANCA.getDescricao()));
        toReturn.add(new SelectItem(ModalidadeConta.CONTA_POUPANCA_PESSOA_JURIDICA, ModalidadeConta.CONTA_POUPANCA_PESSOA_JURIDICA.getDescricao()));
        toReturn.add(new SelectItem(ModalidadeConta.ENTIDADES_PUBLICAS, ModalidadeConta.ENTIDADES_PUBLICAS.getDescricao()));
        return toReturn;
    }

    public void removeUnidadeOrganizacional(ActionEvent evt) {
        SubContaUniOrg s = (SubContaUniOrg) evt.getComponent().getAttributes().get("un");
        subConta.getUnidadesOrganizacionais().remove(s);
    }

    public List<SubContaUniOrg> unidadesOrganizacionais(SubConta sub) {
        if (sub.getId() != null) {
            return contaBancariaEntidadeFacade.getSubContaFacade().listaUnidadesPorSubConta(sub);
        } else {
            return sub.getUnidadesOrganizacionais();
        }
    }

    public List<SubContaFonteRec> fontesRecurso(SubConta sub) {
        if (sub.getId() != null) {
            return contaBancariaEntidadeFacade.getSubContaFacade().listaFontesPorSubConta(sub);
        } else {
            return sub.getSubContaFonteRecs();
        }
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoConta object : SituacaoConta.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void validaCategoriaConta(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Conta c = (Conta) value;
        c = contaBancariaEntidadeFacade.getContaFacade().recuperar(c);
        if (c.getCategoria().equals(CategoriaConta.SINTETICA)) {
            message.setDetail("Conta Sintética. Não pode ser utilizada!");
            message.setSummary("Conta Sintética. Não pode ser utilizada!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public boolean liberaTabContaFinanceira() {
//        if (fonteDeRecursos != null || hierarquiaOrganizacional != null) {
        if (selecionado.getSubContas() == null || selecionado.getSubContas().isEmpty()) {
            return false;
        }
//        }
        return true;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Boolean getLiberado() {
        return liberado;
    }

    public void setLiberado(Boolean liberado) {
        this.liberado = liberado;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public Boolean getHasContaPrincipalFP() {
        return hasContaPrincipalFP;
    }

    public void setHasContaPrincipalFP(Boolean hasContaPrincipalFP) {
        this.hasContaPrincipalFP = hasContaPrincipalFP;
    }

    public boolean isCaixaEconomica() {
        return banco != null && "104".equals(banco.getNumeroBanco());
    }

    public boolean isBancoDoBrasil() {
        return banco != null && "001".equals(banco.getNumeroBanco());
    }

    public boolean isBradesco() {
        return banco != null && "237".equals(banco.getNumeroBanco());
    }
}
