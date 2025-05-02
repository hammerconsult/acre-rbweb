/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.EntidadeOCC;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoClasseCredor;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.OrigemOCCFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Edi
 */
@ManagedBean(name = "occClassePessoaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-occ-classe-pessoa", pattern = "/occ/classe-pessoa/novo/", viewId = "/faces/financeiro/origemcontacontabil/classepessoa/edita.xhtml"),
    @URLMapping(id = "editar-occ-classe-pessoa", pattern = "/occ/classe-pessoa/editar/#{occClassePessoaControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/classepessoa/edita.xhtml"),
    @URLMapping(id = "ver-occ-classe-pessoa", pattern = "/occ/classe-pessoa/ver/#{occClassePessoaControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/classepessoa/visualizar.xhtml"),
    @URLMapping(id = "listar-occ-classe-pessoa", pattern = "/occ/classe-pessoa/listar/", viewId = "/faces/financeiro/origemcontacontabil/classepessoa/listar.xhtml"),})
public class OccClassePessoaControlador extends PrettyControlador<OccClassePessoa> implements Serializable, CRUD {

    @EJB
    private OrigemOCCFacade origemOCCFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private EntidadeOCC entidadeOCC;
    private ConverterAutoComplete converterContaBancariaEntidade;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterSubConta;
    private ConverterAutoComplete converterTagOCC;
    private ConverterAutoComplete converterClassePessoa;
    private OrigemContaContabil origemNaoAlterada;
    private TipoClasseCredor tipoClasseCredor;

    public OccClassePessoaControlador() {
        super(OccClassePessoa.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return origemOCCFacade;
    }

    @URLAction(mappingId = "novo-occ-classe-pessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = new OccClassePessoa();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        setEntidadeOCC(EntidadeOCC.TIPOCLASSEPESSOA);
    }

    @URLAction(mappingId = "ver-occ-classe-pessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-occ-classe-pessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    @Override
    public void cancelar() {
        super.cancelar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/occ/classe-pessoa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            verificaAlteracoesDaOrigem();
            if (!validaCampos()) {
                return;
            }
            if (!validaOrigemContaContabil()) {
                return;
            }
            if (selecionado.getId() == null) {
                origemOCCFacade.salvarNovo(selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso"));
            } else {
                origemOCCFacade.salvar(selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro alterado com sucesso"));
            }
            redireciona();
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }


    public void validaDataVigencia(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date dataVigencia = (Date) value;
        Calendar dataInicioVigencia = Calendar.getInstance();
        dataInicioVigencia.setTime(dataVigencia);
        Integer ano = sistemaControlador.getExercicioCorrente().getAno();
        if (dataInicioVigencia.get(Calendar.YEAR) != ano) {
            message.setSummary("Operação não Permitida!");
            message.setDetail(" O ano da data é diferente do exercício corrente.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void recuperaEditarVer() {
        OccClassePessoa occ = (OccClassePessoa) selecionado;
        selecionado = (OccClassePessoa) origemOCCFacade.recuperar(OccClassePessoa.class, occ.getId());
        origemNaoAlterada = (OccClassePessoa) origemOCCFacade.recuperar(OccClassePessoa.class, occ.getId());
        setEntidadeOCC(EntidadeOCC.TIPOCLASSEPESSOA);
        tipoClasseCredor = selecionado.getClassePessoa().getTipoClasseCredor();
    }

    public void setaNullClassePessoa() {
        ((OccClassePessoa) selecionado).setClassePessoa(null);
    }

    public OrigemContaContabil verificaConfiguracaoExistente() {
        return origemOCCFacade.buscarConfiguracaoPorClassePessoa(selecionado);
    }

    public List<Conta> completaContaContabil(String parte) {
        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        if (sistemaControlador != null) {
            return origemOCCFacade.getContaFacade().listaContasContabeis(parte.trim(), sistemaControlador.getExercicioCorrente());
        }
        return null;
    }

    public List<ClasseCredor> completaClassePessoa(String parte) {
        if (tipoClasseCredor != null) {
            return origemOCCFacade.listaFiltrandoPorTipoClasse(parte.trim(), tipoClasseCredor);
        }
        return new ArrayList<>();
    }

    public Boolean validaOrigemContaContabil() {
        Boolean controle = Boolean.TRUE;
        OrigemContaContabil origemContaContabil = verificaConfiguracaoExistente();
        if (selecionado.getContaContabil() == null && selecionado.getContaInterEstado() == null && selecionado.getContaInterMunicipal() == null
            && selecionado.getContaInterUniao() == null && selecionado.getContaIntra() == null) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " É necessário informar uma Conta Contábil para continuar a operação.");
            controle = Boolean.FALSE;
        } else {
            if (origemContaContabil != null) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe uma configuração para a Origem Conta Contábil, TAG: "
                    + ((OccClassePessoa) origemContaContabil).getTagOCC().getCodigo() + " - " + ((OccClassePessoa) origemContaContabil).getTagOCC().getDescricao() + ", Conta: "
                    + ((OccClassePessoa) origemContaContabil).getClassePessoa().getCodigo() + " - " + ((OccClassePessoa) origemContaContabil).getClassePessoa().getDescricao()
                    + ", com início de vigência em: " + new SimpleDateFormat("dd/MM/yyyy").format(((OccClassePessoa) origemContaContabil).getInicioVigencia()));
                controle = Boolean.FALSE;
            }
        }
        return controle;
    }

    public void validaCategoriaConta(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Conta c = (Conta) value;
        if (c.getCategoria() == null) {
            return;
        }
        if (c.getCategoria().equals(CategoriaConta.SINTETICA)) {
            message.setDetail("Conta Sintética: " + c.getCodigo() + " - " + c.getDescricao() + ", " + "não pode ser utilizada.");
            message.setSummary("Operação não Permitida! ");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void encerrarVigencia() {
        try {
            validarEncerramentoVigencia();
            origemOCCFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada(" Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarEncerramentoVigencia() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFimVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Fim de Vigência é obrigatório!");
        }
        ve.lancarException();
        if (selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O Fim de Vigência deve ser superior ao Início de Vigência.");
        }
        ve.lancarException();
    }

    public boolean podeEditarOrigem() {
        if (selecionado.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(selecionado.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getInicioVigencia() == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Início de Vigência deve ser informado.");
        }
        if (selecionado.getTagOCC() == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo TAG deve ser informado.");
        }
        if (tipoClasseCredor == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Tipo Classe de Pessoa deve ser informado.");
        }
        if (((OccClassePessoa) selecionado).getClassePessoa() == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Classe de Pessoa deve ser informado.");
        }
        return retorno;
    }

    private void verificaAlteracoesDaOrigem() {
        try {
            boolean alteroOrigem = false;
            if (!origemNaoAlterada.meuEquals(selecionado)) {
                alteroOrigem = true;
            }
            if (alteroOrigem) {
                for (EventoContabil ec : origemOCCFacade.retornaEventosPorTag(selecionado.getTagOCC())) {
                    origemOCCFacade.getEventoContabilFacade().geraEventosReprocessar(ec, selecionado.getId(), selecionado.getClass().getSimpleName());
                }
            }
        } catch (Exception ex) {
        }
    }

    public List<SelectItem> getTiposClasseCredor() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoClasseCredor tcc : TipoClasseCredor.values()) {
            toReturn.add(new SelectItem(tcc, tcc.toString()));
        }
        return toReturn;
    }

    public List<TagOCC> completaTagsOCCPorEntidadeOCC(String parte) {
        return origemOCCFacade.getTagOCCFacade().listaPorEntidadeOCC(parte, EntidadeOCC.TIPOCLASSEPESSOA);
    }

    public ConverterAutoComplete getConverterSubconta() {
        if (converterSubConta == null) {
            converterSubConta = new ConverterAutoComplete(SubConta.class, origemOCCFacade.getSubContaFacade());
        }
        return converterSubConta;
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, origemOCCFacade.getContaFacade());
        }
        return converterConta;
    }

    public ConverterAutoComplete getConverterContabancariaEntidade() {
        if (converterContaBancariaEntidade == null) {
            converterContaBancariaEntidade = new ConverterAutoComplete(ContaBancariaEntidade.class, origemOCCFacade.getContaBancariaEntidadeFacade());
        }
        return converterContaBancariaEntidade;
    }

    public ConverterAutoComplete getConverterTagOCC() {
        if (converterTagOCC == null) {
            converterTagOCC = new ConverterAutoComplete(TagOCC.class, origemOCCFacade.getTagOCCFacade());
        }
        return converterTagOCC;
    }

    public ConverterAutoComplete getConverterClassePessoa() {
        if (converterClassePessoa == null) {
            converterClassePessoa = new ConverterAutoComplete(ClasseCredor.class, origemOCCFacade.getClasseCredorFacade());
        }
        return converterClassePessoa;
    }

    public EntidadeOCC getEntidadeOCC() {
        return entidadeOCC;
    }

    public void setEntidadeOCC(EntidadeOCC entidadeOCC) {
        this.entidadeOCC = entidadeOCC;
    }

    public OrigemContaContabil getOrigemNaoAlterada() {
        return origemNaoAlterada;
    }

    public void setOrigemNaoAlterada(OrigemContaContabil origemNaoAlterada) {
        this.origemNaoAlterada = origemNaoAlterada;
    }

    public TipoClasseCredor getTipoClasseCredor() {
        return tipoClasseCredor;
    }

    public void setTipoClasseCredor(TipoClasseCredor tipoClasseCredor) {
        this.tipoClasseCredor = tipoClasseCredor;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
