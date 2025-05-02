/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.EntidadeOCC;
import br.com.webpublico.enums.SummaryMessages;
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
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Edi
 */
@ManagedBean(name = "oCCContaFinanceiraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-occ-conta-financeira", pattern = "/occ/conta-financeira/novo/", viewId = "/faces/financeiro/origemcontacontabil/contafinanceira/edita.xhtml"),
    @URLMapping(id = "editar-occ-conta-financeira", pattern = "/occ/conta-financeira/editar/#{oCCContaFinanceiraControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/contafinanceira/edita.xhtml"),
    @URLMapping(id = "ver-occ-conta-financeira", pattern = "/occ/conta-financeira/ver/#{oCCContaFinanceiraControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/contafinanceira/visualizar.xhtml"),
    @URLMapping(id = "listar-occ-conta-financeira", pattern = "/occ/conta-financeira/listar/", viewId = "/faces/financeiro/origemcontacontabil/contafinanceira/listar.xhtml"),})
public class OCCContaFinanceiraControlador extends PrettyControlador<OCCBanco> implements Serializable, CRUD {

    @EJB
    private OrigemOCCFacade origemOCCFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ContaBancariaEntidade cbe;
    private EntidadeOCC entidadeOCC;
    private ConverterAutoComplete converterContaBancariaEntidade;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterSubConta;
    private ConverterAutoComplete converterTagOCC;
    private OrigemContaContabil origemNaoAlterada;

    public OCCContaFinanceiraControlador() {
        super(OCCBanco.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return origemOCCFacade;
    }

    @URLAction(mappingId = "novo-occ-conta-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        setEntidadeOCC(EntidadeOCC.CONTAFINANCEIRA);
    }

    @URLAction(mappingId = "ver-occ-conta-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-occ-conta-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
        return "/occ/conta-financeira/";
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
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
            } else {
                origemOCCFacade.salvar(selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro alterado com sucesso."));
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
            message.setSummary("Operação não Permitida! ");
            message.setDetail(" o ano da data é diferente do exercício corrente.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void recuperaEditarVer() {
        OCCBanco occ = (OCCBanco) selecionado;
        setEntidadeOCC(EntidadeOCC.CONTAFINANCEIRA);
        selecionado = (OCCBanco) origemOCCFacade.recuperar(OCCBanco.class, occ.getId());
        origemNaoAlterada = (OCCBanco) origemOCCFacade.recuperar(OCCBanco.class, occ.getId());
        if (selecionado.getSubConta() != null) {
            cbe = ((OCCBanco) selecionado).getSubConta().getContaBancariaEntidade();
        }
    }

    public OrigemContaContabil verificaConfiguracaoExistente() {
        return origemOCCFacade.buscarConfiguracaoPorBanco(selecionado);
    }

    public List<Conta> completaContaContabil(String parte) {
        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        if (sistemaControlador != null) {
            return origemOCCFacade.getContaFacade().listaContasContabeis(parte.trim(), sistemaControlador.getExercicioCorrente());
        }
        return null;
    }

    public Boolean validaOrigemContaContabil() {
        Boolean controle = Boolean.TRUE;
        OrigemContaContabil origemContaContabil = verificaConfiguracaoExistente();
        if (selecionado.getContaContabil() == null && selecionado.getContaInterEstado() == null && selecionado.getContaInterMunicipal() == null
            && selecionado.getContaInterUniao() == null && selecionado.getContaIntra() == null) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " É necessário informar uma Conta Contábil para continuar a operação.");
            controle = Boolean.FALSE;
        } else if (origemContaContabil != null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe uma configuração para a Origem Conta Contábil, TAG: " + ((OCCBanco) origemContaContabil).getTagOCC().getCodigo() + " - " + ((OCCBanco) origemContaContabil).getTagOCC().getDescricao() + ", Conta: " + ((OCCBanco) origemContaContabil).getSubConta().getCodigo() + " - " + ((OCCBanco) origemContaContabil).getSubConta().getDescricao() + ", com início de vigência em: " + new SimpleDateFormat("dd/MM/yyyy").format(((OCCBanco) origemContaContabil).getInicioVigencia()));
            controle = Boolean.FALSE;
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
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Inicio de Vigência deve ser informado.");
        }
        if (selecionado.getTagOCC() == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo TAG deve ser informado.");
        }
        if (cbe == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Conta Bancária deve ser informado.");

        }
        if (((OCCBanco) selecionado).getSubConta() == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Conta Financeira deve ser informado.");
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
        } catch (Exception e) {
        }
    }


    public void setaNullContaFinanceira() {
        selecionado.setSubConta(null);
    }

    public void setaIdNullContaBancaria() {
        cbe = null;
    }

    public void recuperaContaBancariaApartirDaContaFinanceira() {
        cbe = retornoContaBancaria(selecionado);
    }

    public List<ContaBancariaEntidade> completaContaBancariaEntidade(String parte) {
        return origemOCCFacade.getContaBancariaEntidadeFacade().listaPorCodigo(parte.trim());
    }

    public ContaBancariaEntidade retornoContaBancaria(OCCBanco occ) {
        try {
            return ((OCCBanco) occ).getSubConta().getContaBancariaEntidade();
        } catch (Exception e) {
            return new ContaBancariaEntidade();
        }
    }

    public List<SubConta> completaSubContas(String parte) {
        if (cbe != null) {
            if (cbe.getId() != null) {
                return origemOCCFacade.getSubContaFacade().listaPorContaBancariaEntidade(parte.trim(), cbe);
            }
        }
        return origemOCCFacade.getSubContaFacade().listaTodas(parte.trim());
    }

    public List<TagOCC> completaTagsOCCPorEntidadeOCC(String parte) {
        return origemOCCFacade.getTagOCCFacade().listaPorEntidadeOCC(parte, EntidadeOCC.CONTAFINANCEIRA);
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

    public ContaBancariaEntidade getCbe() {
        return cbe;
    }

    public void setCbe(ContaBancariaEntidade cbe) {
        this.cbe = cbe;
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

    public void setOrigemNaoAlterada(OCCBanco origemNaoAlterada) {
        this.origemNaoAlterada = origemNaoAlterada;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
