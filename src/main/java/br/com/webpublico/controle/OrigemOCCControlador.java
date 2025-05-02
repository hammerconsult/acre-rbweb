/*
 * Codigo gerado automaticamente em Tue Jun 26 09:40:23 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.EntidadeOCC;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.OrigemOCCFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "origemOCCControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoorigemocc", pattern = "/origem-conta-contabil/novo/", viewId = "/faces/financeiro/clp/origemocc/edita.xhtml"),
    @URLMapping(id = "editaorigemocc", pattern = "/origem-conta-contabil/editar/#{origemOCCControlador.id}/", viewId = "/faces/financeiro/clp/origemocc/edita.xhtml"),
    @URLMapping(id = "verorigemocc", pattern = "/origem-conta-contabil/ver/#{origemOCCControlador.id}/", viewId = "/faces/financeiro/clp/origemocc/visualizar.xhtml"),
    @URLMapping(id = "listarorigemocc", pattern = "/origem-conta-contabil/listar/", viewId = "/faces/financeiro/clp/origemocc/lista.xhtml")
})
public class OrigemOCCControlador extends PrettyControlador<OrigemContaContabil> implements Serializable, CRUD {

    @EJB
    private OrigemOCCFacade origemOCCFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private EntidadeOCC entidadeOCC;
    private ConverterAutoComplete converterContaBancariaEntidade;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterSubConta;
    private ConverterAutoComplete converterTagOCC;
    private ContaBancariaEntidade cbe;
    private List<OrigemContaContabil> origenscc;
    private OrigemContaContabil origemcc;
    private TagOCC tagOCCLista;
    private Conta contaContabilLista;
    private String palavra;
    private Conta[] arrayConta;
    private List<Conta> listConta;
    private CategoriaDividaPublica[] arrayCategoria;
    private List<CategoriaDividaPublica> listCategorias;
    private OrigemContaContabil origemNaoAlterada;

    public OrigemOCCControlador() {
        super(OrigemContaContabil.class);
        operacao = Operacoes.LISTAR;
    }

    public OrigemOCCFacade getFacade() {
        return origemOCCFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return origemOCCFacade;
    }

    public List<SelectItem> getEntidadesOCC() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (EntidadeOCC object : EntidadeOCC.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "novoorigemocc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        cbe = new ContaBancariaEntidade();
        recuperaEntidadeOCCSessao();
        if (entidadeOCC.equals(EntidadeOCC.CONTAFINANCEIRA)) {
            origemcc = new OCCBanco();
        } else if (entidadeOCC.equals(EntidadeOCC.CONTADESPESA) || entidadeOCC.equals(EntidadeOCC.CONTAEXTRAORCAMENTARIA)
                || entidadeOCC.equals(EntidadeOCC.CONTARECEITA) || entidadeOCC.equals(EntidadeOCC.DESTINACAO)) {
            origemcc = new OCCConta();
        } else if (entidadeOCC.equals(EntidadeOCC.NATUREZADIVIDAPUBLICA)) {
            origemcc = new OCCNaturezaDividaPublica();
        }
        origemcc.setInicioVigencia(new Date());
        origenscc = new ArrayList<>();
        tagOCCLista = null;
        listConta = new ArrayList<>();
    }

    @URLAction(mappingId = "editaorigemocc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "verorigemocc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "listarorigemocc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        recuperaEntidadeOCCSessao();
    }

    @Override
    public void cancelar() {
        super.cancelar();
        Web.poeNaSessao(entidadeOCC);
        limpaTagOccLista();
        listaOrigemPorTag();
    }

    public void recuperaEditarVer() {
        OrigemContaContabil occ = (OrigemContaContabil) selecionado;
        entidadeOCC = occ.getTagOCC().getEntidadeOCC();
        if (entidadeOCC.equals(EntidadeOCC.CONTAFINANCEIRA)) {
            origemcc = (OCCBanco) origemOCCFacade.recuperar(OCCBanco.class, occ.getId());
            origemNaoAlterada = (OCCBanco) origemOCCFacade.recuperar(OCCBanco.class, occ.getId());
            if (((OCCBanco) origemcc).getSubConta() != null) {
                cbe = ((OCCBanco) origemcc).getSubConta().getContaBancariaEntidade();
            }
        } else if (entidadeOCC.equals(EntidadeOCC.CONTADESPESA) || entidadeOCC.equals(EntidadeOCC.CONTAEXTRAORCAMENTARIA)
                || entidadeOCC.equals(EntidadeOCC.CONTARECEITA) || entidadeOCC.equals(EntidadeOCC.DESTINACAO)) {
            origemcc = (OCCConta) origemOCCFacade.recuperar(OCCConta.class, occ.getId());
            origemNaoAlterada = (OCCConta) origemOCCFacade.recuperar(OCCConta.class, occ.getId());
        } else if (entidadeOCC.equals(EntidadeOCC.NATUREZADIVIDAPUBLICA)) {
            origemcc = (OCCNaturezaDividaPublica) origemOCCFacade.recuperar(OCCNaturezaDividaPublica.class, occ.getId());
            origemNaoAlterada = (OCCNaturezaDividaPublica) origemOCCFacade.recuperar(OCCNaturezaDividaPublica.class, occ.getId());
        }
        selecionado = origemcc;
        origenscc = new ArrayList<>();
        origenscc.add(origemcc);
    }

    public boolean podeEditarOrigem() {
        if (origemcc.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(origemcc.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(new Date())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public void validaDataEncerraVigencia(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        if (origemcc.getFimVigencia() != null) {
            if (origemcc.getFimVigencia().before(origemcc.getInicioVigencia())) {
                message.setDetail("Data inválida!");
                message.setSummary("A Data do Fim da Vigência não pode ser menor que a data de Início da Vigência!");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
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

    public void removeConta(ActionEvent evento) {
        OrigemContaContabil occ = (OrigemContaContabil) evento.getComponent().getAttributes().get("occ");
        origenscc.remove(occ);
    }

    public void alterarConta(ActionEvent evento) {
        OrigemContaContabil occ = (OrigemContaContabil) evento.getComponent().getAttributes().get("occ");
        origemcc = occ;
    }

    @Override
    public void excluir() {
        super.excluir();
    }

    public void limpaCamposVariacao() {
        origemcc.setContaInterEstado(null);
        origemcc.setContaInterMunicipal(null);
        origemcc.setContaInterUniao(null);
        origemcc.setContaIntra(null);
    }

    public OrigemContaContabil verificaConfiguracaoExistente() {
        OrigemContaContabil configuracaoEncontrada = null;
        if ((operacao.equals(Operacoes.NOVO)) && (entidadeOCC.equals(EntidadeOCC.CONTADESPESA) || entidadeOCC.equals(EntidadeOCC.CONTAEXTRAORCAMENTARIA) || entidadeOCC.equals(EntidadeOCC.CONTARECEITA)
                || entidadeOCC.equals(EntidadeOCC.DESTINACAO))) {
            for (OrigemContaContabil occ : origenscc) {
                configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorConta(occ);
            }
        } else if ((operacao.equals(Operacoes.EDITAR)) && (entidadeOCC.equals(EntidadeOCC.CONTADESPESA) || entidadeOCC.equals(EntidadeOCC.CONTAEXTRAORCAMENTARIA) || entidadeOCC.equals(EntidadeOCC.CONTARECEITA)
                || entidadeOCC.equals(EntidadeOCC.DESTINACAO))) {
            configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorConta(origemcc);

        } else if (entidadeOCC.equals(EntidadeOCC.NATUREZADIVIDAPUBLICA)) {
            for (OrigemContaContabil occ : origenscc) {
                configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorNaturezaDividaPublica(occ);
            }
        } else {
            if (origemcc instanceof OCCBanco) {
                configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorBanco(origemcc);
            }
            if (origemcc instanceof OCCNaturezaDividaPublica) {
                configuracaoEncontrada = origemOCCFacade.buscarConfiguracaoPorNaturezaDividaPublica(origemcc);
            }
        }
        return configuracaoEncontrada;
    }

    public Boolean validaOrigemContaContabil() {
        Boolean controle = Boolean.TRUE;

        if (entidadeOCC.equals(EntidadeOCC.CONTAFINANCEIRA)) {
            if (origemcc.getContaContabil() == null && origemcc.getContaInterEstado() == null && origemcc.getContaInterMunicipal() == null
                    && origemcc.getContaInterUniao() == null && origemcc.getContaIntra() == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção:", "Informe alguma Conta Contábil"));
                controle = Boolean.FALSE;
            }
        } else if (entidadeOCC.equals(EntidadeOCC.CONTADESPESA) || entidadeOCC.equals(EntidadeOCC.CONTAEXTRAORCAMENTARIA) || entidadeOCC.equals(EntidadeOCC.CONTARECEITA) || entidadeOCC.equals(EntidadeOCC.DESTINACAO)) {
            for (OrigemContaContabil occ : origenscc) {
                if (occ.getContaContabil() == null && occ.getContaInterEstado() == null && occ.getContaInterMunicipal() == null
                        && occ.getContaInterUniao() == null && occ.getContaIntra() == null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção:", "Informe alguma Conta Contábil"));
                    controle = Boolean.FALSE;
                }
            }
        }
        if (verificaConfiguracaoExistente() != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro:", "Já existe uma configuração com essa descrição ativa"));
            controle = Boolean.FALSE;
        }
        return controle;
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
            if (operacao.equals(Operacoes.NOVO)) {
                if (entidadeOCC.equals(EntidadeOCC.CONTADESPESA) || entidadeOCC.equals(EntidadeOCC.CONTAEXTRAORCAMENTARIA)
                        || entidadeOCC.equals(EntidadeOCC.CONTARECEITA) || entidadeOCC.equals(EntidadeOCC.DESTINACAO) || entidadeOCC.equals(EntidadeOCC.NATUREZADIVIDAPUBLICA)) {
                    for (OrigemContaContabil occ : origenscc) {
                        origemOCCFacade.salvar(occ);
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com sucesso!", "A Origem Conta Contábil com inico de vigência em: " + new SimpleDateFormat("dd/MM/yyyy").format(origemcc.getInicioVigencia()) + " foi salvo com sucesso!"));
                    }
                } else {
                    origemOCCFacade.salvarNovo(origemcc);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com sucesso!", "A Origem Conta Contábil com inico de vigência em: " + new SimpleDateFormat("dd/MM/yyyy").format(origemcc.getInicioVigencia()) + " foi salvo com sucesso!"));
                }
            } else {
                origemOCCFacade.salvar(origemcc);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Alterado com sucesso!", "A Origem Conta Contábil com inico de vigência em: " + new SimpleDateFormat("dd/MM/yyyy").format(origemcc.getInicioVigencia()) + " foi alterado com sucesso!"));
            }
//            entidadeOCC = null;
            limpaTagOccLista();
            redireciona();
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Problema ao salvar!" + e.getMessage(), "Problema ao salvar!" + e.getMessage()));
        }
    }

    public Boolean validaCampos() {
        boolean retorno = true;
        if (entidadeOCC.equals(EntidadeOCC.CONTAFINANCEIRA)) {
            if (origemcc.getTagOCC() == null) {
                retorno = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro:", "O campo TAG é obrigatório!"));
            } else {
                if (origemcc.getInicioVigencia() == null) {
                    retorno = false;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro:", "O campo Data de Início de Vigência é obrigatório!"));
                }
                if (origemcc instanceof OCCBanco) {
                    if (((OCCBanco) origemcc).getSubConta() == null) {
                        retorno = false;
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro:", "O campo Conta Financeira é obrigatório!"));
                    }
                }
            }
        }
        return retorno;
    }

    private void verificaAlteracoesDaOrigem() {
        try {
            boolean alteroOrigem = false;
            if (!origemNaoAlterada.meuEquals(origemcc)) {
                alteroOrigem = true;
            }
            if (alteroOrigem) {
                for (EventoContabil ec : origemOCCFacade.retornaEventosPorTag(origemcc.getTagOCC())) {
                    origemOCCFacade.getEventoContabilFacade().geraEventosReprocessar(ec, origemcc.getId(), origemcc.getClass().getSimpleName());
                }

            }
        } catch (Exception e) {
        }
    }

    public Boolean renderizaPainelConta() {
        if (entidadeOCC == null) {
            return false;
        }
        if ((entidadeOCC.equals(EntidadeOCC.CONTADESPESA) || entidadeOCC.equals(EntidadeOCC.CONTAEXTRAORCAMENTARIA) || entidadeOCC.equals(EntidadeOCC.CONTARECEITA) || entidadeOCC.equals(EntidadeOCC.DESTINACAO))) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean renderizaPainelContaEditar() {
        if ((operacao.equals(Operacoes.EDITAR)) && (entidadeOCC.equals(EntidadeOCC.CONTADESPESA) || entidadeOCC.equals(EntidadeOCC.CONTAEXTRAORCAMENTARIA) || entidadeOCC.equals(EntidadeOCC.CONTARECEITA) || entidadeOCC.equals(EntidadeOCC.DESTINACAO))) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean renderizaColunasContaListar() {
        if (entidadeOCC != null) {
            if ((operacao.equals(Operacoes.LISTAR)) && (entidadeOCC.equals(EntidadeOCC.CONTADESPESA) || entidadeOCC.equals(EntidadeOCC.CONTAEXTRAORCAMENTARIA) || entidadeOCC.equals(EntidadeOCC.CONTARECEITA) || entidadeOCC.equals(EntidadeOCC.DESTINACAO))) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Boolean renderizaPainelContaFinanceira() {
        if (entidadeOCC == null) {
            return false;
        }
        if (entidadeOCC.equals(EntidadeOCC.CONTAFINANCEIRA)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean renderizaPainelNaturezaDaDivida() {
        if (entidadeOCC == null) {
            return false;
        }
        if (entidadeOCC.equals(EntidadeOCC.NATUREZADIVIDAPUBLICA)) {
            return true;

        } else {
            return false;
        }
    }

    public String retornaLabel() {
        if (entidadeOCC != null) {
            switch (entidadeOCC) {
                case CONTAFINANCEIRA:
                    return "Conta Financeira ";
                case CONTADESPESA:
                    return "Conta de Despesa ";
                case CONTAEXTRAORCAMENTARIA:
                    return "Conta Extraorçamentária ";
                case CONTARECEITA:
                    return "Conta de Receita ";
                case DESTINACAO:
                    return "Conta de Destinação ";
                case NATUREZADIVIDAPUBLICA:
                    return "Natureza da Dívida Pública ";
                default:
                    return "Informe ";
            }
        } else {
            return "Informe ";
        }
    }

    public void limpaCampos() {
        contaContabilLista = null;
        tagOCCLista = null;
        listaOrigemPorTag();
    }

    public void listaOrigemPorTag() {
        if (entidadeOCC != null) {
            if (tagOCCLista != null && contaContabilLista == null) {
                origenscc = origemOCCFacade.listaOCCPorTag(tagOCCLista);
            } else if (tagOCCLista != null || contaContabilLista != null) {
                origenscc = origemOCCFacade.listaOrigemContaContabilPorContaContabil(contaContabilLista, entidadeOCC, tagOCCLista);
            } else {
                origenscc = origemOCCFacade.listaPorEntidadeOCC(entidadeOCC);
            }
        } else {
            origenscc = new ArrayList<>();
        }
    }

    public String retornaContaNaLista(OrigemContaContabil or) {
        try {
            if (or instanceof OCCBanco) {
                return ((OCCBanco) or).getSubConta().toString();
            } else if (or instanceof OCCConta) {
                return ((OCCConta) or).getContaOrigem().toString();
            } else if (or instanceof OCCNaturezaDividaPublica) {
                return ((OCCNaturezaDividaPublica) or).getCategoriaDividaPublica().toString();
            }
            return " ";
        } catch (Exception e) {
            return " ";
        }
    }

    public void limpaTagOccLista() {
        tagOCCLista = null;
        origenscc = null;
        listaOrigemPorTag();
    }

    public List<TagOCC> completaTagsOCC(String parte) {
        if (entidadeOCC != null) {
            return origemOCCFacade.getTagOCCFacade().listaPorEntidadeOCC(parte, entidadeOCC);
        } else {
            return new ArrayList<TagOCC>();
        }
    }

    public List<Conta> completaContaDespesa(String parte) {
        return origemOCCFacade.getContaFacade().listaFiltrandoContaDespesa(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<Conta> completaContaReceita(String parte) {
        return origemOCCFacade.getContaFacade().listaFiltrandoReceita(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<Conta> completaContaExtra(String parte) {
        return origemOCCFacade.getContaFacade().listaFiltrandoExtraorcamentario(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<Conta> completaContaDestinacao(String parte) {
        return origemOCCFacade.getContaFacade().listaFiltrandoDestinacaoDeRecursos(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<CategoriaDividaPublica> completaNaturezaDaDivida(String parte) {
        return origemOCCFacade.getCategoriaDividaPublicaFacade().listaFiltrando(parte.trim(), "codigo", "descricao", "naturezaDividaPublica");
    }

    public List<Conta> completaContas(String parte) {
        if (entidadeOCC.equals(EntidadeOCC.CONTADESPESA)) {
            return origemOCCFacade.getContaFacade().listaFiltrandoContaDespesa(parte.trim(), sistemaControlador.getExercicioCorrente());
        } else if (entidadeOCC.equals(EntidadeOCC.CONTARECEITA)) {
            return origemOCCFacade.getContaFacade().listaFiltrandoReceita(parte.trim(), sistemaControlador.getExercicioCorrente());
        } else if (entidadeOCC.equals(EntidadeOCC.CONTAEXTRAORCAMENTARIA)) {
            return origemOCCFacade.getContaFacade().listaFiltrandoExtraorcamentario(parte.trim(), sistemaControlador.getExercicioCorrente());
        } else if (entidadeOCC.equals(EntidadeOCC.DESTINACAO)) {
            return origemOCCFacade.getContaFacade().listaFiltrandoDestinacaoDeRecursos(parte.trim(), sistemaControlador.getExercicioCorrente());
        } else {
            return null;
        }
    }

    public void filtrarContas() {
        listConta = new ArrayList<Conta>();
        if (entidadeOCC.equals(EntidadeOCC.CONTADESPESA)) {
            listConta = completaContaDespesa(palavra);
        } else if (entidadeOCC.equals(EntidadeOCC.CONTARECEITA)) {
            listConta = completaContaReceita(palavra);
        } else if (entidadeOCC.equals(EntidadeOCC.CONTAEXTRAORCAMENTARIA)) {
            listConta = completaContaExtra(palavra);
        } else if (entidadeOCC.equals(EntidadeOCC.DESTINACAO)) {
            listConta = completaContaDestinacao(palavra);
        } else if (entidadeOCC.equals(EntidadeOCC.NATUREZADIVIDAPUBLICA)) {
            listCategorias = completaNaturezaDaDivida(palavra);
        }
    }

    public void addAllCategorias() {
        if (origemcc.getTagOCC() != null
                && (origemcc.getContaContabil() != null
                || origemcc.getContaInterEstado() != null
                || origemcc.getContaInterMunicipal() != null
                || origemcc.getContaInterUniao() != null
                || origemcc.getContaIntra() != null)) {
            if (arrayCategoria.length <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção:", "Selecione pelo menos uma Natureza da Dívida"));
            } else {
                for (int i = 0; i < arrayCategoria.length; i++) {
                    OCCNaturezaDividaPublica origemccNova = new OCCNaturezaDividaPublica();
                    origemccNova.setTagOCC(origemcc.getTagOCC());
                    origemccNova.setContaContabil(origemcc.getContaContabil());
                    origemccNova.setContaInterEstado(origemcc.getContaInterEstado());
                    origemccNova.setContaInterMunicipal(origemcc.getContaInterMunicipal());
                    origemccNova.setContaInterUniao(origemcc.getContaInterUniao());
                    origemccNova.setContaIntra(origemcc.getContaIntra());
                    origemccNova.setOrigem(origemcc.getOrigem());
                    origemccNova.setReprocessar(origemcc.getReprocessar());
                    origemccNova.setCategoriaDividaPublica(arrayCategoria[i]);
                    origemccNova.setInicioVigencia(origemcc.getInicioVigencia());
                    origemccNova.setFimVigencia(origemcc.getFimVigencia());
                    if (podeAdicionarContaNatureza(origemccNova)) {
                        origenscc.add(origemccNova);
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Não foi possível adicionar a conta:", "A conta " + origemccNova.getCategoriaDividaPublica() + " já foi adicionada."));
                    }
                }
                if (entidadeOCC.equals(EntidadeOCC.NATUREZADIVIDAPUBLICA)) {
                    origemcc = new OCCNaturezaDividaPublica();
                }
                RequestContext.getCurrentInstance().update("Formulario");
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção:", "Preencha uma TAG e uma Conta antes de prosseguir"));
        }
    }

    public void addAllContas() {
        if (origemcc.getTagOCC() != null
                && (origemcc.getContaContabil() != null
                || origemcc.getContaInterEstado() != null
                || origemcc.getContaInterMunicipal() != null
                || origemcc.getContaInterUniao() != null
                || origemcc.getContaIntra() != null)) {
            if (arrayConta.length <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção:", "Selecione pelo menos uma Conta Contábil"));
            } else {
                for (int i = 0; i < arrayConta.length; i++) {
                    OCCConta origemccNova = new OCCConta();
                    origemccNova.setTagOCC(origemcc.getTagOCC());
                    origemccNova.setContaContabil(origemcc.getContaContabil());
                    origemccNova.setContaInterEstado(origemcc.getContaInterEstado());
                    origemccNova.setContaInterMunicipal(origemcc.getContaInterMunicipal());
                    origemccNova.setContaInterUniao(origemcc.getContaInterUniao());
                    origemccNova.setContaIntra(origemcc.getContaIntra());
                    origemccNova.setOrigem(origemcc.getOrigem());
                    origemccNova.setReprocessar(origemcc.getReprocessar());
                    origemccNova.setContaOrigem(arrayConta[i]);
                    origemccNova.setInicioVigencia(origemcc.getInicioVigencia());
                    origemccNova.setFimVigencia(origemcc.getFimVigencia());
                    if (podeAdicionarConta(origemccNova)) {
                        origenscc.add(origemccNova);
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Não foi possível adicionar a Conta", "A Conta " + origemccNova.getContaOrigem() + " já foi adicionada."));
                    }
                }
                if (entidadeOCC.equals(EntidadeOCC.CONTADESPESA) || entidadeOCC.equals(EntidadeOCC.CONTAEXTRAORCAMENTARIA) || entidadeOCC.equals(EntidadeOCC.CONTARECEITA)
                        || entidadeOCC.equals(EntidadeOCC.DESTINACAO)) {
                    origemcc = new OCCConta();
                }
                RequestContext.getCurrentInstance().update("Formulario");
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção:", "Selecione uma TAG e uma Conta antes de prosseguir"));
        }
    }

    public void adicionarContaAlterada() {
        if (origemcc.getTagOCC() != null
                && (origemcc.getContaContabil() != null
                || origemcc.getContaInterEstado() != null
                || origemcc.getContaInterMunicipal() != null
                || origemcc.getContaInterUniao() != null
                || origemcc.getContaIntra() != null)) {
            origenscc.set(origenscc.indexOf(origemcc), origemcc);
            if (origemcc instanceof OCCNaturezaDividaPublica) {
                RequestContext.getCurrentInstance().execute("dialogNatureza.hide()");
            } else {
                RequestContext.getCurrentInstance().execute("dialog.hide()");
            }
            RequestContext.getCurrentInstance().update("Formulario");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Alteração: ", " Contas Contábeis alteradas com sucesso! "));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção:", "Selecione uma TAG e uma Conta antes de prosseguir"));
        }
    }

    public boolean podeAdicionarConta(OCCConta occConta) {
        for (OrigemContaContabil origemContaContabil : origenscc) {
            if (origemContaContabil.getTagOCC().equals(occConta.getTagOCC())) {
                OCCConta occDaVez = (OCCConta) origemContaContabil;
                if (occDaVez.getContaOrigem().equals(occConta.getContaOrigem())) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean podeAdicionarContaNatureza(OCCNaturezaDividaPublica occNaturezaDividaPublica) {
        for (OrigemContaContabil origemContaContabil : origenscc) {
            if (origemContaContabil.getTagOCC().equals(occNaturezaDividaPublica.getTagOCC())) {
                OCCNaturezaDividaPublica occDaVez = (OCCNaturezaDividaPublica) origemContaContabil;
                if (occDaVez.getCategoriaDividaPublica().equals(occNaturezaDividaPublica.getCategoriaDividaPublica())) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<ContaBancariaEntidade> completaContaBancariaEntidade(String parte) {
        return origemOCCFacade.getContaBancariaEntidadeFacade().listaPorCodigo(parte.trim());
    }

    public List<Conta> completaContaContabil(String parte) {
        return origemOCCFacade.getContaFacade().listaContasContabeis(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<SubConta> completaSubContas(String parte) {
        if (cbe != null) {
            if (cbe.getId() != null) {
                return origemOCCFacade.getSubContaFacade().listaPorContaBancariaEntidade(parte.trim(), cbe);
            }
        }
        return origemOCCFacade.getSubContaFacade().listaTodas(parte.trim());
    }

    public Conta getContaContabilLista() {
        return contaContabilLista;
    }

    public void setContaContabilLista(Conta contaContabilLista) {
        this.contaContabilLista = contaContabilLista;
    }

    public ConverterAutoComplete getConverterContabancariaEntidade() {
        if (converterContaBancariaEntidade == null) {
            converterContaBancariaEntidade = new ConverterAutoComplete(ContaBancariaEntidade.class, origemOCCFacade.getContaBancariaEntidadeFacade());
        }
        return converterContaBancariaEntidade;
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, origemOCCFacade.getContaFacade());
        }
        return converterConta;
    }

    public ConverterAutoComplete getConverterSubconta() {
        if (converterSubConta == null) {
            converterSubConta = new ConverterAutoComplete(SubConta.class, origemOCCFacade.getSubContaFacade());
        }
        return converterSubConta;
    }

    public ConverterAutoComplete getConverterTagOCC() {
        if (converterTagOCC == null) {
            converterTagOCC = new ConverterAutoComplete(TagOCC.class, origemOCCFacade.getTagOCCFacade());
        }
        return converterTagOCC;
    }

    public boolean isTipoContaReceita() {
        if (entidadeOCC == null) {
            return false;
        }
        if (entidadeOCC.equals(EntidadeOCC.CONTARECEITA)) {
            return true;
        }
        return false;
    }

    public boolean isTipoContaDespesa() {
        if (entidadeOCC == null) {
            return false;
        }
        if (entidadeOCC.equals(EntidadeOCC.CONTADESPESA)) {
            return true;
        }
        return false;
    }

    public boolean isTipoContaEXTRAORCAMENTARIA() {
        if (entidadeOCC == null) {
            return false;
        }
        if (entidadeOCC.equals(EntidadeOCC.CONTAEXTRAORCAMENTARIA)) {
            return true;
        }
        return false;
    }

    public boolean isTipoContaDestinacao() {
        if (entidadeOCC == null) {
            return false;
        }
        if (entidadeOCC.equals(EntidadeOCC.DESTINACAO)) {
            return true;
        }
        return false;
    }

    public boolean isTipoContaFinanceira() {
        if (entidadeOCC == null) {
            return false;
        }
        if (entidadeOCC != null) {
            if (entidadeOCC.equals(EntidadeOCC.CONTAFINANCEIRA)) {
                return true;
            }
        }
        return false;
    }

    public void validaCategoriaConta(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Conta c = (Conta) value;
        if (c.getCategoria() == null) {
            return;
        }
        if (c.getCategoria().equals(CategoriaConta.SINTETICA)) {
            message.setDetail("Conta Sintética. Não pode ser utilizada!");
            message.setSummary("Conta Sintética. Não pode ser utilizada!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void setaNullContaFinanceira() {
        if (isTipoContaFinanceira()) {
            ((OCCBanco) origemcc).setSubConta(null);
        }
    }

    public void setaNullTagOCCLista() {
        tagOCCLista = null;
    }

    public void setaIdNullContaBancaria() {
        if (cbe == null) {
            cbe = new ContaBancariaEntidade();
        } else {
            cbe.setId(null);
        }
    }

    public void recuperaContaBancariaApartirDaContaFinanceira() {
        if (isTipoContaFinanceira()) {
            cbe = retornoContaBancaria(origemcc);
        }
    }

    public ContaBancariaEntidade retornoContaBancaria(OrigemContaContabil occ) {
        try {
            return ((OCCBanco) occ).getSubConta().getContaBancariaEntidade();
        } catch (Exception e) {
            return new ContaBancariaEntidade();
        }

    }

    @Override
    public String getCaminhoPadrao() {
        return "/origem-conta-contabil/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void recuperaEntidadeOCCSessao() {
        entidadeOCC = (EntidadeOCC) Web.pegaDaSessao(EntidadeOCC.class);
        if (entidadeOCC == null) {
            entidadeOCC = EntidadeOCC.CONTAFINANCEIRA;
        }
        listaOrigemPorTag();
    }

    public List<CategoriaDividaPublica> getListCategorias() {
        return listCategorias;
    }

    public void setListCategorias(List<CategoriaDividaPublica> listCategorias) {
        this.listCategorias = listCategorias;
    }

    public CategoriaDividaPublica[] getArrayCategoria() {
        return arrayCategoria;
    }

    public void setArrayCategoria(CategoriaDividaPublica[] arrayCategoria) {
        this.arrayCategoria = arrayCategoria;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public EntidadeOCC getEntidadeOCC() {
        return entidadeOCC;
    }

    public void setEntidadeOCC(EntidadeOCC entidadeOCC) {
        this.entidadeOCC = entidadeOCC;
    }

    public ContaBancariaEntidade getCbe() {
        return cbe;
    }

    public void setCbe(ContaBancariaEntidade cbe) {
        this.cbe = cbe;
    }

    public List<OrigemContaContabil> getOrigenscc() {
        return origenscc;
    }

    public void setOrigenscc(List<OrigemContaContabil> origenscc) {
        this.origenscc = origenscc;
    }

    public OrigemContaContabil getOrigemcc() {
        return origemcc;
    }

    public void setOrigemcc(OrigemContaContabil origemcc) {
        this.origemcc = origemcc;
    }

    public TagOCC getTagOCCLista() {
        return tagOCCLista;
    }

    public void setTagOCCLista(TagOCC tagOCCLista) {
        this.tagOCCLista = tagOCCLista;
    }

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public Conta[] getArrayConta() {
        return arrayConta;
    }

    public void setArrayConta(Conta[] arrayConta) {
        this.arrayConta = arrayConta;
    }

    public List<Conta> getListConta() {
        return listConta;
    }

    public void setListConta(List<Conta> listConta) {
        this.listConta = listConta;
        listCategorias = new ArrayList<>();
        origemcc = new OrigemContaContabil();
    }
}
