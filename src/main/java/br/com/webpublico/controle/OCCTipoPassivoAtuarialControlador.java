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
@ManagedBean(name = "oCCTipoPassivoAtuarialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-occ-tipo-passivo-atuarial", pattern = "/occ/tipo-passivo-atuarial/novo/", viewId = "/faces/financeiro/origemcontacontabil/tipopassivoatuarial/edita.xhtml"),
    @URLMapping(id = "editar-occ-tipo-passivo-atuarial", pattern = "/occ/tipo-passivo-atuarial/editar/#{oCCTipoPassivoAtuarialControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/tipopassivoatuarial/edita.xhtml"),
    @URLMapping(id = "ver-occ-tipo-passivo-atuarial", pattern = "/occ/tipo-passivo-atuarial/ver/#{oCCTipoPassivoAtuarialControlador.id}/", viewId = "/faces/financeiro/origemcontacontabil/tipopassivoatuarial/visualizar.xhtml"),
    @URLMapping(id = "listar-occ-tipo-passivo-atuarial", pattern = "/occ/tipo-passivo-atuarial/listar/", viewId = "/faces/financeiro/origemcontacontabil/tipopassivoatuarial/listar.xhtml"),})
public class OCCTipoPassivoAtuarialControlador extends PrettyControlador<OCCTipoPassivoAtuarial> implements Serializable, CRUD {

    @EJB
    private OrigemOCCFacade origemOCCFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private EntidadeOCC entidadeOCC;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterTagOCC;
    private ConverterAutoComplete converterTipoPassivo;
    private OrigemContaContabil origemNaoAlterada;

    public OCCTipoPassivoAtuarialControlador() {
        super(OCCTipoPassivoAtuarial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return origemOCCFacade;
    }

    @URLAction(mappingId = "novo-occ-tipo-passivo-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = new OCCTipoPassivoAtuarial();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
        setEntidadeOCC(EntidadeOCC.TIPOPASSIVOATUARIAL);
    }

    @URLAction(mappingId = "ver-occ-tipo-passivo-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-occ-tipo-passivo-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/occ/tipo-passivo-atuarial/";
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
            message.setSummary("Operação não Permitida!");
            message.setDetail(" O ano da data é diferente do exercício corrente.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void recuperaEditarVer() {
        OCCTipoPassivoAtuarial occ = (OCCTipoPassivoAtuarial) selecionado;
        setEntidadeOCC(EntidadeOCC.TIPOPASSIVOATUARIAL);
        selecionado = (OCCTipoPassivoAtuarial) origemOCCFacade.recuperar(OCCTipoPassivoAtuarial.class, occ.getId());
        origemNaoAlterada = (OCCTipoPassivoAtuarial) origemOCCFacade.recuperar(OCCTipoPassivoAtuarial.class, occ.getId());
    }

    public OrigemContaContabil verificaConfiguracaoExistente() {
        return origemOCCFacade.buscarConfiguracaoPorTipoPassivo(selecionado);
    }

    public List<Conta> completaContaContabil(String parte) {
        if (sistemaControlador != null) {
            return origemOCCFacade.getContaFacade().listaContasContabeis(parte.trim(), sistemaControlador.getExercicioCorrente());
        }
        return null;
    }

    public List<TipoPassivoAtuarial> completaTipoPassivo(String parte) {
        return origemOCCFacade.getTipoPassivoAtuarialFacade().listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public Boolean validaOrigemContaContabil() {
        Boolean controle = Boolean.TRUE;
        OrigemContaContabil origemContaContabil = verificaConfiguracaoExistente();
        if (selecionado.getContaContabil() == null && selecionado.getContaInterEstado() == null && selecionado.getContaInterMunicipal() == null
            && selecionado.getContaInterUniao() == null && selecionado.getContaIntra() == null) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " É necessário informar uma Conta Contábil para continuar a operação.");
            controle = Boolean.FALSE;
        }
        if (origemContaContabil != null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe uma configuração para a Origem Conta Contábil, " +
                "TAG: " + ((OCCTipoPassivoAtuarial) origemContaContabil).getTagOCC().getCodigo() + " - " + ((OCCTipoPassivoAtuarial) origemContaContabil).getTagOCC().getDescricao()
                + ", Conta: " + ((OCCTipoPassivoAtuarial) origemContaContabil).getTipoPassivoAtuarial().getCodigo() + " - " + ((OCCTipoPassivoAtuarial) origemContaContabil).getTipoPassivoAtuarial().getDescricao()
                + ", com início de vigência em: " + new SimpleDateFormat("dd/MM/yyyy").format(((OCCTipoPassivoAtuarial) origemContaContabil).getInicioVigencia()));

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
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Início de Vigência deve ser informado.");
        }
        if (selecionado.getTagOCC() == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo TAG deve ser informado.");
        }
        if (((OCCTipoPassivoAtuarial) selecionado).getTipoPassivoAtuarial() == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Tipo Passivo Atuarial deve ser informado.");
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

    public List<TagOCC> completaTagsOCCPorEntidadeOCC(String parte) {
        return origemOCCFacade.getTagOCCFacade().listaPorEntidadeOCC(parte, EntidadeOCC.TIPOPASSIVOATUARIAL);
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, origemOCCFacade.getContaFacade());
        }
        return converterConta;
    }

    public ConverterAutoComplete getConverterTagOCC() {
        if (converterTagOCC == null) {
            converterTagOCC = new ConverterAutoComplete(TagOCC.class, origemOCCFacade.getTagOCCFacade());
        }
        return converterTagOCC;
    }

    public ConverterAutoComplete getConverterTipoPassivo() {
        if (converterTipoPassivo == null) {
            converterTipoPassivo = new ConverterAutoComplete(TipoPassivoAtuarial.class, origemOCCFacade.getTipoPassivoAtuarialFacade());
        }
        return converterTipoPassivo;
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

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
