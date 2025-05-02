/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.ClassificacaoContaAuxiliar;
import br.com.webpublico.enums.IndicadorSuperavitFinanceiro;
import br.com.webpublico.enums.TipoMovimentoTCE;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CLPFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
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
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author venon
 */
@ManagedBean(name = "cLPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoclp", pattern = "/clp/novo/", viewId = "/faces/financeiro/clp/configuracaoclp/edita.xhtml"),
    @URLMapping(id = "editaclp", pattern = "/clp/editar/#{cLPControlador.id}/", viewId = "/faces/financeiro/clp/configuracaoclp/edita.xhtml"),
    @URLMapping(id = "verclp", pattern = "/clp/ver/#{cLPControlador.id}/", viewId = "/faces/financeiro/clp/configuracaoclp/visualizar.xhtml"),
    @URLMapping(id = "listarclp", pattern = "/clp/listar/", viewId = "/faces/financeiro/clp/configuracaoclp/lista.xhtml")
})
public class CLPControlador extends PrettyControlador<CLP> implements Serializable, CRUD {

    @EJB
    private CLPFacade cLPConfiguracaoFacade;
    private ConverterAutoComplete converterContaContabil;
    private LCP lcp;
    private ConverterAutoComplete converterTag;
    private ConverterAutoComplete converterTipoContaAuxiliar;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private CLP clpNaoAlterada;
    private Boolean validarCategriaDaContaAoSelecionar;

    public CLPControlador() {
        super(CLP.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return cLPConfiguracaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/clp/";
    }

    @Override
    public Object getUrlKeyValue() {
        return ((CLP) selecionado).getId();
    }


    @URLAction(mappingId = "novoclp", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = new CLP();
        lcp = new LCP();
        lcp.setClp((CLP) selecionado);
        ((CLP) selecionado).setInicioVigencia(sistemaControlador.getDataOperacao());
        validarCategriaDaContaAoSelecionar = Boolean.FALSE;
    }

    @URLAction(mappingId = "editaclp", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        clpNaoAlterada = cLPConfiguracaoFacade.recuperar(selecionado.getId());
        lcp = new LCP();
        lcp.setClp((CLP) selecionado);
        validarCategriaDaContaAoSelecionar = cLPConfiguracaoFacade.isCLPUtilizadaEmEventoContabilQueNaoForDeAjusteManual(selecionado);
    }

    @URLAction(mappingId = "verclp", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        CLP clpConf = ((CLP) selecionado);
        if (validaCampos() == true) {
//            if (cLPConfiguracaoFacade.validaCodigo(clpConf, sistemaControlador.getExercicioCorrente())) {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                        "Erro: ", "O código da Configuração da CLP já existe para esse exercício"));
//                return "edita";
//            }
            try {
                cLPConfiguracaoFacade.validaCodigoNomeVigenciaMesmoData(clpConf, sistemaControlador.getExercicioCorrente());
                cLPConfiguracaoFacade.meuSalvar(clpConf, clpNaoAlterada);
                FacesUtil.addOperacaoRealizada(" A CLP " + clpConf.toString() + " foi salva com sucesso.");
                redireciona();

            } catch (ExcecaoNegocioGenerica e) {
                FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoRealizada(" Erro ao salvar: " + e.getMessage());
            }
        }
    }

    public Boolean validaSelecionouContaCredito() {
        boolean controle = true;
        if (lcp.getContaCredito() == null
            && lcp.getContaCreditoInterEstado() == null
            && lcp.getContaCreditoInterMunicipal() == null
            && lcp.getContaCreditoInterUniao() == null
            && lcp.getContaCreditoIntra() == null) {
            controle = false;
        }
        return controle;
    }

    public Boolean validaSelecionouContaDebito() {
        boolean controle = true;
        if (lcp.getContaDebito() == null
            && lcp.getContaDebitoInterEstado() == null
            && lcp.getContaDebitoInterMunicipal() == null
            && lcp.getContaDebitoInterUniao() == null
            && lcp.getContaDebitoIntra() == null) {
            controle = false;
        }
        return controle;
    }

    public void navegarParaTipoContaAuxiliar() {
        FacesUtil.redirecionamentoInterno("/tipo-conta-auxiliar/novo/");
    }

    public void navegarParaTagOCC() {
        FacesUtil.redirecionamentoInterno("/tagocc/novo/");
    }

    public Boolean validaAddItem() {
        boolean controle = true;
        if (lcp.getCodigo() == null || lcp.getCodigo().equals("")) {
            FacesUtil.addCampoObrigatorio("O campo Código da LCP deve ser informado.");
            controle = false;
        } else if (!validaSelecionouContaDebito() && lcp.getTagOCCDebito() == null) {
            FacesUtil.addCampoObrigatorio("O campo Conta de Débito ou uma Tag deve ser informado.");
            controle = false;
        } else if (!validaSelecionouContaCredito() && lcp.getTagOCCCredito() == null) {
            FacesUtil.addCampoObrigatorio("O campo Conta de Crédito ou uma Tag deve ser informado.");
            controle = false;
        } else if (validaSelecionouContaDebito() && lcp.getTagOCCDebito() != null) {
            FacesUtil.addOperacaoNaoPermitida("Não é possível adicionar uma Conta de Débito e uma Tag.");
            controle = false;
        } else if (validaSelecionouContaCredito() && lcp.getTagOCCCredito() != null) {
            FacesUtil.addOperacaoNaoPermitida("Não é possível adicionar uma Conta de Cŕedito e uma Tag.");
            controle = false;
        }
//        for (LCP l : selecionado.getLcps()) {
//            if (l.equals(lcp)) {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", "A LCP que esta tentando adicionar possui as mesmas contas que a LCP " + l.getCodigo()));
//                controle = false;
//            }
//        }
        for (LCP l : selecionado.getLcps()) {
            if (l.getCodigo().equals(lcp.getCodigo()) && !l.equals(lcp)) {
                FacesUtil.addOperacaoNaoPermitida("Já existe uma LCP com o código " + lcp.getCodigo() + " adicionada na lista. ");
                controle = false;
            }
        }
        return controle;
    }

    public void adicionaItem() {
        CLP cl = ((CLP) selecionado);
        if (validaAddItem()) {
            List<LCP> l = cl.getLcps();
            if (!l.contains(lcp)) {
                lcp.setItem(cl.getLcps().size() + 1);
            }
            cl.setLcps(Util.adicionarObjetoEmLista(cl.getLcps(), lcp));
            lcp = new LCP();
            lcp.setClp((CLP) selecionado);
        }
    }

    public void alteraCLPPraBaixo(ActionEvent event) {
        LCP lcp = (LCP) event.getComponent().getAttributes().get("objetoParaBaixo");
//        CLP cl = ((CLP) selecionado);
        int indiceAtual = selecionado.getLcps().indexOf(lcp);
        selecionado.getLcps().remove(lcp);
        int novoIndice = indiceAtual + 1;


        selecionado.getLcps().add(novoIndice, lcp);


        selecionado.getLcps().get(indiceAtual).setItem(indiceAtual + 1);
        selecionado.getLcps().get(novoIndice).setItem(novoIndice + 1);

    }

    public void alteraCLPPraCima(ActionEvent event) {
        LCP lcp = (LCP) event.getComponent().getAttributes().get("objetoParaCima");
//        CLP cl = ((CLP) selecionado);
        int indiceAtual = selecionado.getLcps().indexOf(lcp);
        selecionado.getLcps().remove(lcp);
        int novoIndice = indiceAtual - 1;

        selecionado.getLcps().add(novoIndice, lcp);

        selecionado.getLcps().get(indiceAtual).setItem(indiceAtual + 1);
        selecionado.getLcps().get(novoIndice).setItem(novoIndice + 1);
    }

    public boolean disabledBotaoAlteraParaBaixo(LCP lcp) {
        CLP cl = ((CLP) selecionado);
        if (cl.getLcps().size() == 1) {
            return true;
        }
        int indice = cl.getLcps().indexOf(lcp);
        if (indice == -1) {
            return true;
        }
        if (indice != (cl.getLcps().size() - 1)) {
            return false;
        }
        return true;
    }

    public boolean disabledBotaoAlteraParaCima(LCP lcp) {
        CLP cl = ((CLP) selecionado);
        if (cl.getLcps().size() == 1) {
            return true;
        }
        int indice = cl.getLcps().indexOf(lcp);
        if (indice == -1) {
            return true;
        }
        if (indice != 0) {
            return false;
        }
        return true;
    }

    public void limparItem() {
        lcp = new LCP();
    }

    //    public void alterarVigencia(ActionEvent evt) {
//        if (operacao.equals(Operacoes.EDITAR)) {
//        LCP lcpVelho = new LCP();
//        CLP clpNova = new CLP();
//        lcpVelho = (LCP) evt.getComponent().getAttributes().get("objeto");
//        lcpVelho.getClp().setDataVigencia(new Date());
//        lcp = new LCP();
//        clpNova.setCodigo(lcpVelho.getClp().getCodigo());
//        clpNova.setDescricao(lcpVelho.getClp().getDescricao());
//        clpNova.setExercicio(lcpVelho.getClp().getExercicio());
//        clpNova.setNome(lcpVelho.getClp().getNome());
//
//        lcp.setClp(lcpVelho.getClp());
//        lcp.setCodigo(lcpVelho.getCodigo());
//        lcp.setContaCredito(lcpVelho.getContaCredito());
//        lcp.setContaCredito(lcpVelho.getContaDebito());
//        lcp.setObrigatorio(lcpVelho.getObrigatorio());
//        lcp.setTagOCCCredito(lcpVelho.getTagOCCCredito());
//        lcp.setTagOCCDebito(lcpVelho.getTagOCCDebito());
//        lcp.setTipoContaAuxiliarCredito(lcpVelho.getTipoContaAuxiliarCredito());
//        lcp.setTipoContaAuxiliarDebito(lcpVelho.getTipoContaAuxiliarDebito());
//        lcp.setVariacao(lcp.getVariacao());
//        }
//    }
    public void alterarItem(ActionEvent evt) {
        lcp = (LCP) evt.getComponent().getAttributes().get("objeto");
    }

    public void excluiItem(ActionEvent evt) {
        LCP item = (LCP) evt.getComponent().getAttributes().get("excluirItem");
        ((CLP) selecionado).getLcps().remove(item);
    }

    public List<TagOCC> completaTag(String parte) {
        return cLPConfiguracaoFacade.getcLPTagFacade().listaFiltrando(parte.trim(), "descricao", "codigo");
    }

    public ConverterAutoComplete getConverterTag() {
        if (converterTag == null) {
            converterTag = new ConverterAutoComplete(TagOCC.class, cLPConfiguracaoFacade.getcLPTagFacade());
        }
        return converterTag;
    }

    public List<ClpHistoricoContabil> completaHistoricoContabil(String parte) {
        return cLPConfiguracaoFacade.getClpHistoricoContabilFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<Conta> completaContaContabil(String parte) {
        return cLPConfiguracaoFacade.getContaFacade().listaContasContabeis(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<TipoContaAuxiliar> completaTipoContaAux(String parte) {
        return cLPConfiguracaoFacade.getTipoContaAuxiliarFacade().listaTodos(parte, ClassificacaoContaAuxiliar.SISTEMA);
    }

    public List<TipoContaAuxiliar> completaTipoContaAuxSiconfi(String parte) {
        return cLPConfiguracaoFacade.getTipoContaAuxiliarFacade().listaTodos(parte, ClassificacaoContaAuxiliar.SICONFI);
    }

    public ConverterAutoComplete getConverterTipoContaAuxiliar() {
        if (converterTipoContaAuxiliar == null) {
            converterTipoContaAuxiliar = new ConverterAutoComplete(TipoContaAuxiliar.class, cLPConfiguracaoFacade.getTipoContaAuxiliarFacade());
        }
        return converterTipoContaAuxiliar;
    }

    public ConverterAutoComplete getConverterContaContabil() {
        if (converterContaContabil == null) {
            converterContaContabil = new ConverterAutoComplete(Conta.class, cLPConfiguracaoFacade.getContaFacade());
        }
        return converterContaContabil;
    }

    public void limparTipoContaAuxiliarDebitoSiconfi() {
        lcp.setTipoContaAuxDebSiconfi(null);
    }

    public void limparTipoContaAuxiliarCreditoSiconfi() {
        lcp.setTipoContaAuxCredSiconfi(null);
    }

    public List<SelectItem> getItensMovimentoTCE() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoMovimentoTCE tipoMovimentoTCE : TipoMovimentoTCE.values()) {
            retorno.add(new SelectItem(tipoMovimentoTCE, tipoMovimentoTCE.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getIndicadoresSuperavitFinanceiro() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (IndicadorSuperavitFinanceiro objeto : IndicadorSuperavitFinanceiro.values()) {
            retorno.add(new SelectItem(objeto, objeto.getDescricao()));
        }
        return retorno;
    }

    public Boolean validaCampos() {
        CLP clp = ((CLP) selecionado);
        boolean retorno = true;

        if (clp.getCodigo() == null || clp.getCodigo().trim().length() <= 0) {
            retorno = false;
            FacesUtil.addCampoObrigatorio(" O campo Código deve ser informado.");
        }
        if (clp.getNome() == null || clp.getNome().trim().length() <= 0) {
            retorno = false;
            FacesUtil.addCampoObrigatorio(" O campo Descrição deve ser informado.");
        }
        if (clp.getInicioVigencia() == null) {
            retorno = false;
            FacesUtil.addCampoObrigatorio("O campo Início Vigência deve ser informado.");
        }
        if (clp.getDescricao().length() > 3000) {
            retorno = false;
            FacesUtil.addOperacaoNaoPermitida("Excedeu no número de caracteres para o campo Função.");
        }
        return retorno;
    }

    public void validaDataEncerraVigencia(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date data = (Date) value;
        CLP obj = ((CLP) selecionado);
        if (data.compareTo(obj.getInicioVigencia()) < 0) {
            message.setDetail("A data de Fim de Vigência não pode ser inferior a data de Início de Vigência.");
            message.setSummary("Operação não Permitida!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void encerrarVigencia() {
        try {
            if (validaLancamentosContabeis()) {
                cLPConfiguracaoFacade.meuSalvar(selecionado, clpNaoAlterada);
                FacesUtil.addOperacaoRealizada("Vigência foi encerrada com sucesso na data " + DataUtil.getDataFormatada(selecionado.getFimVigencia()) + ".");
                redireciona();
            } else {
                FacesUtil.addOperacaoNaoPermitida("Para excluir esta CLP, é necessário que seja excluidos primeiros os Lançamentos Contabéis desta CLP.");
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro do sistema: " + e.getMessage());
        }
    }

    private boolean validaLancamentosContabeis() {
        return cLPConfiguracaoFacade.validaLancamentosContabeis((CLP) selecionado);
    }

    public boolean podeEditarClp() {
        CLP clp = (CLP) selecionado;
        if (clp.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(clp.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public void excluirSelecionado() {
        List<EventoContabil> eventosRecuperados = cLPConfiguracaoFacade.validaVinculoComEventoContabil((CLP) selecionado);
        if (!validaLancamentosContabeis()) {
            FacesUtil.addOperacaoNaoPermitida("Para excluir esta CLP, é necessário que seja excluidos primeiros os Lançamentos Contabéis desta CLP.");
        } else if (!eventosRecuperados.isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Para excluir esta CLP, é necessário que seja excluido primeiro o Evento Contábil " + eventosRecuperados.get(0).getCodigo() + " - " + eventosRecuperados.get(0).getDescricao() + ".");
        } else {
            super.excluir();
        }
    }

    public void validarCategoriaDaConta(FacesContext context, UIComponent component, Object value) {
        if (validarCategriaDaContaAoSelecionar) {
            FacesMessage message = new FacesMessage();
            Conta c = (Conta) value;
            if (c.getCategoria() == null) {
                return;
            }
            if (c.getCategoria().equals(CategoriaConta.SINTETICA)) {
                message.setDetail("Conta Sintética. Não pode ser utilizada!");
                message.setSummary("Operação não Permitida");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public LCP getLcp() {
        return lcp;
    }

    public void setLcp(LCP lcp) {
        this.lcp = lcp;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
