/*
 * Codigo gerado automaticamente em Tue Dec 06 15:05:05 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.PermissaoEmissaoDAM;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "dividaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaDivida", pattern = "/divida/novo/", viewId = "/faces/tributario/divida/edita.xhtml"),
    @URLMapping(id = "editarDivida", pattern = "/divida/editar/#{dividaControlador.id}/", viewId = "/faces/tributario/divida/edita.xhtml"),
    @URLMapping(id = "listarDivida", pattern = "/divida/listar/", viewId = "/faces/tributario/divida/lista.xhtml"),
    @URLMapping(id = "verDivida", pattern = "/divida/ver/#{dividaControlador.id}/", viewId = "/faces/tributario/divida/visualizar.xhtml")
})
public class DividaControlador extends PrettyControlador<Divida> implements Serializable, CRUD {

    @EJB
    private DividaFacade dividaFacade;
    private ConverterAutoComplete converterEntidade;
    private ConverterGenerico converterFormaCobranca, converterOpcaoPagamento;
    private FormaCobrancaDivida formaCobrancaDivida;
    private OpcaoPagamentoDivida opcaoPagamentoDivida;
    private ConverterAutoComplete converterDivida;
    private DividaAcrescimo dividaAcrescimo;
    private ConverterAutoComplete converterConfiguracaoAcrescimo;
    private ConverterAutoComplete converterConfiguracaoDAM;
    private List<Number> dividasSelecionadas;
    private PermissaoEmissaoDAM permissaoEmissaoDAM;

    public DividaControlador() {
        super(Divida.class);
    }

    public List<Number> getDividasSelecionadas() {
        return dividasSelecionadas;
    }

    public void setDividasSelecionadas(List<Number> dividasSelecionadas) {
        this.dividasSelecionadas = dividasSelecionadas;
    }

    public PermissaoEmissaoDAM getPermissaoEmissaoDAM() {
        return permissaoEmissaoDAM;
    }

    public void setPermissaoEmissaoDAM(PermissaoEmissaoDAM permissaoEmissaoDAM) {
        this.permissaoEmissaoDAM = permissaoEmissaoDAM;
    }

    @Override
    public AbstractFacade getFacede() {
        return dividaFacade;
    }

    public Converter getConverterConfiguracaoacrescimo() {
        if (converterConfiguracaoAcrescimo == null) {
            converterConfiguracaoAcrescimo = new ConverterAutoComplete(ConfiguracaoAcrescimos.class, dividaFacade.getConfiguracaoAcrescimosFacade());
        }
        return converterConfiguracaoAcrescimo;
    }

    public Converter getConverterConfiguracaoDAM() {
        if (converterConfiguracaoDAM == null) {
            converterConfiguracaoDAM = new ConverterAutoComplete(ConfiguracaoDAM.class, dividaFacade.getConfiguracaoDAMFacade());
        }
        return converterConfiguracaoDAM;
    }

    public DividaAcrescimo getDividaAcrescimo() {
        return dividaAcrescimo;
    }

    public void setDividaAcrescimo(DividaAcrescimo dividaAcrescimo) {
        this.dividaAcrescimo = dividaAcrescimo;
    }

    public ConverterAutoComplete getConverterEntidade() {
        if (converterEntidade == null) {
            converterEntidade = new ConverterAutoComplete(Entidade.class, dividaFacade.getEntidadeFacade());
        }
        return converterEntidade;
    }

    public Converter getConverterFormaCobranca() {
        if (converterFormaCobranca == null) {
            converterFormaCobranca = new ConverterGenerico(FormaCobranca.class, dividaFacade.getFormaCobrancaFacade());
        }
        return converterFormaCobranca;
    }

    public Converter getConverterOpcaoPagamento() {
        if (converterOpcaoPagamento == null) {
            converterOpcaoPagamento = new ConverterGenerico(OpcaoPagamento.class, dividaFacade.getOpcaoPagamentoFacade());
        }
        return converterOpcaoPagamento;
    }

    public OpcaoPagamentoDivida getOpcaoPagamentoDivida() {
        return opcaoPagamentoDivida;
    }

    public void setOpcaoPagamentoDivida(OpcaoPagamentoDivida opcaoPagamentoDivida) {
        this.opcaoPagamentoDivida = opcaoPagamentoDivida;
    }

    public FormaCobrancaDivida getFormaCobrancaDivida() {
        return formaCobrancaDivida;
    }

    public void setFormaCobrancaDivida(FormaCobrancaDivida formaCobrancaDivida) {
        this.formaCobrancaDivida = formaCobrancaDivida;
    }

    public List<Entidade> completeEntidade(String parte) {
        return dividaFacade.getEntidadeFacade().listaEntidades(parte.trim());
    }

    public List<ConfiguracaoDAM> completaConfiguracaoDAM(String parte) {
        return dividaFacade.getConfiguracaoDAMFacade().listaFiltrando(parte.trim(), "descricao", "codigoFormatado");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/divida/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaDivida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        formaCobrancaDivida = new FormaCobrancaDivida();
        opcaoPagamentoDivida = new OpcaoPagamentoDivida();
        dividaAcrescimo = new DividaAcrescimo();
    }

    @URLAction(mappingId = "editarDivida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        formaCobrancaDivida = new FormaCobrancaDivida();
        opcaoPagamentoDivida = new OpcaoPagamentoDivida();
        dividaAcrescimo = new DividaAcrescimo();
    }

    @URLAction(mappingId = "verDivida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "listarDivida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        dividasSelecionadas = Lists.newArrayList();
    }

    public void addFormaCobracaDivida() {
        if (!validaFormaCobranca(this.formaCobrancaDivida)) {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "Forma de Cobrança já cadastrada no período informado.");
        }
        formaCobrancaDivida.setDivida(selecionado);
        selecionado.getFormaCobrancaDividas().add(formaCobrancaDivida);
        formaCobrancaDivida = new FormaCobrancaDivida();
    }

    public void addOpcaoPagamentoDivida() {
        if (this.opcaoPagamentoDivida.getInicioVigencia() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o Inicio de vigência para a opção de pagamento.");
        } else if (opcaoPagamentoDivida.getFinalVigencia() == null || opcaoPagamentoDivida.getInicioVigencia().before(opcaoPagamentoDivida.getFinalVigencia())) {
            opcaoPagamentoDivida.setDivida(selecionado);
            selecionado.getOpcaoPagamentosDivida().add(opcaoPagamentoDivida);
            opcaoPagamentoDivida = new OpcaoPagamentoDivida();
        } else {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "Verifique as datas.");
        }
    }

    public void removeFormaCobracaDivida(ActionEvent e) {
        formaCobrancaDivida = (FormaCobrancaDivida) e.getComponent().getAttributes().get("objeto");
        selecionado.getFormaCobrancaDividas().remove(formaCobrancaDivida);
        formaCobrancaDivida = new FormaCobrancaDivida();
    }

    public void removeOpcaoPagamentoDivida(ActionEvent e) {
        opcaoPagamentoDivida = (OpcaoPagamentoDivida) e.getComponent().getAttributes().get("objeto");
        selecionado.getOpcaoPagamentosDivida().remove(opcaoPagamentoDivida);
        opcaoPagamentoDivida = new OpcaoPagamentoDivida();
    }

    public List<SelectItem> getFormaCobrancas() {
        return Util.getListSelectItem(dividaFacade.getFormaCobrancaFacade().lista());
    }

    public List<SelectItem> getOpcaoPagamentos() {
        return Util.getListSelectItem(dividaFacade.getOpcaoPagamentoFacade().lista());
    }

    public List<SelectItem> getListSelectItemDivida() {
        return Util.getListSelectItem(dividaFacade.lista());
    }

    private boolean validaVigencia(Date inicio, Date fim) {
        if (selecionado.getOpcaoPagamentosDivida().isEmpty()) {
            return true;
        }
        Date ultimaFinal = selecionado.getOpcaoPagamentosDivida().get(selecionado.getOpcaoPagamentosDivida().size() - 1).getFinalVigencia();
        if (inicio.before(fim)) {
            return true;
        } else if (inicio.after(ultimaFinal)) {
            return true;
        }
        return false;
    }

    private boolean validaFormaCobranca(FormaCobrancaDivida formaSendoAdicionada) {
        boolean resultado = true;
        for (FormaCobrancaDivida forma : selecionado.getFormaCobrancaDividas()) {
            if (forma.equals(formaSendoAdicionada)) {
                if (((formaSendoAdicionada.getFormaCobranca().getInicioVigencia().after(forma.getFormaCobranca().getInicioVigencia()))
                    && (formaSendoAdicionada.getFormaCobranca().getInicioVigencia().before(forma.getFormaCobranca().getFinalVigencia())))
                    || ((formaSendoAdicionada.getFormaCobranca().getFinalVigencia().after(forma.getFormaCobranca().getInicioVigencia()))
                    && (formaSendoAdicionada.getFormaCobranca().getFinalVigencia().before(forma.getFormaCobranca().getFinalVigencia())))) {
                    resultado = false;
                    break;
                }
            }
        }
        return resultado;
    }

    private boolean validaOpcaoPagamento(OpcaoPagamentoDivida opcaoSendoAdicionada) {
        boolean resultado = true;
        for (OpcaoPagamentoDivida opcao : selecionado.getOpcaoPagamentosDivida()) {
            if (opcao.equals(opcaoSendoAdicionada)) {
                if (((opcaoSendoAdicionada.getInicioVigencia().after(opcao.getInicioVigencia()))
                    && (opcaoSendoAdicionada.getInicioVigencia().before(opcao.getFinalVigencia())))
                    || ((opcaoSendoAdicionada.getFinalVigencia().after(opcao.getInicioVigencia()))
                    && (opcaoSendoAdicionada.getFinalVigencia().before(opcao.getFinalVigencia())))) {
                    resultado = false;
                    break;
                }
            }
        }
        return resultado;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (selecionado.getCodigo() == null) {
                selecionado.setCodigo(dividaFacade.retornaUltimoCodigoLong().intValue());
            }
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<Divida> completaDivida(String parte) {
        return dividaFacade.listaDividasAtivas(parte.trim(), selecionado.getTipoCadastro());
    }

    public List<Divida> completaDividaDoTipoCadastroPessoa(String parte) {
        return dividaFacade.listaDividasDoTipoCadastro(parte.trim(), TipoCadastroTributario.PESSOA);
    }

    public List<Divida> completarDividasDeCadastroEconomico(String parte) {
        return dividaFacade.listaDividasDoTipoCadastro(parte.trim(), TipoCadastroTributario.ECONOMICO);
    }

    public List<Divida> completarDividasDeCadastroImobiliario(String parte) {
        return dividaFacade.listaDividasDoTipoCadastro(parte.trim(), TipoCadastroTributario.IMOBILIARIO);
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, dividaFacade);
        }
        return converterDivida;
    }

    public void limparDividaAtiva() {
        if ((selecionado != null) && (selecionado.getIsDividaAtiva())) {
            selecionado.setDivida(null);
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().length() <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a descrição.");
        }
        if (selecionado.getTipoCadastro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de cadastro.");
        }
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data de inicio de Vigência.");
        }
        if (selecionado.getEntidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Entidade.");
        }
        if (selecionado.getDivida() != null && (selecionado.getNrLivroDividaAtiva() == null || selecionado.getNrLivroDividaAtiva().isEmpty())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Nr. do Livro D.A.");
        }
        if (selecionado.getConfiguracaoDAM() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Configuração de DAM.");
        }
        if (selecionado.getPermissaoEmissaoDAM() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a permissão de emissão de DAM.");
        }
        ve.lancarException();
    }

    public void limpaDividaAtiva() {
        selecionado.setDivida(null);
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getPermissoesEmissaoDAM() {
        List<SelectItem> retorno = new ArrayList<>();
        for (PermissaoEmissaoDAM permissao : PermissaoEmissaoDAM.values()) {
            retorno.add(new SelectItem(permissao, permissao.getDescricao()));
        }
        return retorno;
    }

    public List<ConfiguracaoAcrescimos> completaConfiguracaoAcrescimo(String parte) {
        return dividaFacade.getConfiguracaoAcrescimosFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public void addAcrescimo() {
        try {
            validarAcrescimo();
            dividaAcrescimo.setDivida(selecionado);
            selecionado.getAcrescimos().add(dividaAcrescimo);
            dividaAcrescimo = new DividaAcrescimo();
            dividaAcrescimo = new DividaAcrescimo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removeAcrescimo(ActionEvent e) {
        dividaAcrescimo = (DividaAcrescimo) e.getComponent().getAttributes().get("objeto");
        selecionado.getAcrescimos().remove(dividaAcrescimo);
        dividaAcrescimo = new DividaAcrescimo();
    }

    public void fechaVigenciaAcrescimo(ActionEvent e) {
        DividaAcrescimo dv = (DividaAcrescimo) e.getComponent().getAttributes().get("objeto");
        selecionado.getAcrescimos().get(selecionado.getAcrescimos().indexOf(dv)).setFinalVigencia(new Date());
    }

    private void validarAcrescimo() {
        ValidacaoException ve = new ValidacaoException();
        if (dividaAcrescimo.getAcrescimo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe uma configuração de acréscimos.");
        }
        if (dividaAcrescimo.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o início da Vigência.");
        }
        for (DividaAcrescimo acrescimo : selecionado.getAcrescimos()) {
            if (acrescimo.getFinalVigencia() == null || acrescimo.getFinalVigencia().after(new Date())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível adicionar uma nova configuração enquanto houver outra vigente!");
                break;
            }
        }
        ve.lancarException();
    }

    public List<Divida> buscarDividasDeDividasDiversas(String parte) {
        return dividaFacade.buscarDividasDeDividasDiversas(parte);
    }

    public List<Divida> completarDivida(String parte) {
        return dividaFacade.buscarDividas(parte);
    }

    public void iniciarAlteracaoDividas() {
        permissaoEmissaoDAM = null;
        FacesUtil.executaJavaScript("openDialog('#dialogAlteracaoDividas')");
    }

    public void salvarAlteracaoDividas() {
        try {
            for (Number dividaId : dividasSelecionadas) {
                Divida divida = dividaFacade.recuperar(dividaId.longValue());
                divida.setPermissaoEmissaoDAM(permissaoEmissaoDAM);
                dividaFacade.salvar(divida);
            }
            FacesUtil.addOperacaoRealizada("Dívidas alteradas com sucesso!");
            FacesUtil.executaJavaScript("closeDialog('#dialogAlteracaoDividas')");
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }
}
