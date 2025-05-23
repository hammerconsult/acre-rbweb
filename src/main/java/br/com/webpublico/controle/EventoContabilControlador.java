/*
 * Codigo gerado automaticamente em Wed Jun 27 14:28:00 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "eventoContabilControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoeventocontabil", pattern = "/eventocontabil/novo/", viewId = "/faces/financeiro/clp/eventocontabil/edita.xhtml"),
    @URLMapping(id = "editaeventocontabil", pattern = "/eventocontabil/editar/#{eventoContabilControlador.id}/", viewId = "/faces/financeiro/clp/eventocontabil/edita.xhtml"),
    @URLMapping(id = "listareventocontabil", pattern = "/eventocontabil/listar/", viewId = "/faces/financeiro/clp/eventocontabil/lista.xhtml"),
    @URLMapping(id = "vereventocontabil", pattern = "/eventocontabil/ver/#{eventoContabilControlador.id}/", viewId = "/faces/financeiro/clp/eventocontabil/visualizar.xhtml")
})
public class EventoContabilControlador extends PrettyControlador<EventoContabil> implements Serializable, CRUD {
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private CLPFacade clpFacade;
    private EventoContabil eventoContabil;
    private ConverterAutoComplete converterCLP;
    private ConverterAutoComplete converterHistorico;
    private ItemEventoCLP itemEventoCLP;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private EventoContabil eventoNaoAlterado;

    public EventoContabilControlador() {
        super(EventoContabil.class);
    }

    public EventoContabilFacade getFacade() {
        return eventoContabilFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return eventoContabilFacade;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ItemEventoCLP getItemEventoCLP() {
        return itemEventoCLP;
    }

    public void setItemEventoCLP(ItemEventoCLP itemEventoCLP) {
        this.itemEventoCLP = itemEventoCLP;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    @URLAction(mappingId = "vereventocontabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void veri() {

        operacao = Operacoes.VER;
        selecionado = getFacade().recuperar(this.getId());
        //        super.ver();
    }

    @URLAction(mappingId = "editaeventocontabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        eventoNaoAlterado = eventoContabilFacade.recuperar(selecionado.getId());
        itemEventoCLP = new ItemEventoCLP();
    }

    @URLAction(mappingId = "novoeventocontabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        eventoContabil = new EventoContabil();
        itemEventoCLP = new ItemEventoCLP();
        ((EventoContabil) selecionado).setInicioVigencia(new Date());
        atualizarComValoresDaSessao();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            eventoContabilFacade.validaCodigoNomeVigenciaMesmoData((EventoContabil) selecionado);
            eventoContabilFacade.meuSalvar((EventoContabil) selecionado, eventoNaoAlterado);
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível salvar. Detalhes do erro: " + ex.getMessage() + " Contate o suporte.");
        }
    }

    @Override
    public List<EventoContabil> completarEstaEntidade(String parte) {
        return eventoContabilFacade.listaFiltrando(parte.trim(), "descricao", "codigo");
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCodigo() == null || selecionado.getCodigo().trim().length() <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Código deve ser informado.");
        }
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().length() <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
        }
        if (selecionado.getTipoEventoContabil() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Evento Contábil deve ser informado.");
        }
        if (selecionado.getTipoLancamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Lançamento deve ser informado.");
        }
        if (selecionado.getTipoBalancete() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Contabilização deve ser informado.");
        }
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início da Vigência deve ser informado.");
        }
        ve.lancarException();
        if (isTipoEventoAjusteManual() && (selecionado.getEventoTce() == null || selecionado.getEventoTce().trim().isEmpty())) {
            ve.adicionarMensagemDeCampoObrigatorio("Quando o <b>Tipo de Evento Contábil</b> for de <b>"
                + TipoEventoContabil.AJUSTE_CONTABIL_MANUAL.getDescricao() +
                "</b>, o campo <b>Evento TCE</b> deve ser informado.");
        }
        if (selecionado.getItemEventoCLPs() != null && !selecionado.getItemEventoCLPs().isEmpty()) {
            for (ItemEventoCLP item : selecionado.getItemEventoCLPs()) {
                validarCategoriaDasContasDasLcps(item, ve);
            }
        }
        ve.lancarException();
    }

    public List<ClpHistoricoContabil> completaHistoricoContabil(String parte) {
        return eventoContabilFacade.getClpHistoricoContabilFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterHistorico() {
        if (converterHistorico == null) {
            converterHistorico = new ConverterAutoComplete(ClpHistoricoContabil.class, eventoContabilFacade.getClpHistoricoContabilFacade());
        }
        return converterHistorico;
    }

    public List<CLP> completaCLP(String parte) {
        return eventoContabilFacade.getClpFacade().listaFiltrandoClpConfiguracaoVigentes(parte.trim(), sistemaControlador.getDataOperacao());
    }

    public ConverterAutoComplete getConverterCLP() {
        if (converterCLP == null) {
            converterCLP = new ConverterAutoComplete(CLP.class, eventoContabilFacade.getClpFacade());
        }
        return converterCLP;
    }

    public List<SelectItem> getTipoBalancete() {
        List<SelectItem> listaDest = new ArrayList<SelectItem>();
        listaDest.add(new SelectItem(null, ""));
        for (TipoBalancete dest : TipoBalancete.values()) {
            listaDest.add(new SelectItem(dest, dest.getDescricao()));
        }
        return listaDest;
    }

    public List<SelectItem> getTagValor() {
        List<SelectItem> listaDest = new ArrayList<SelectItem>();
//        listaDest.add(new SelectItem(null, ""));
        listaDest.add(new SelectItem(TagValor.LANCAMENTO, TagValor.LANCAMENTO.getDescricao()));
//        for (TagValor dest : TagValor.values()) {
//            listaDest.add(new SelectItem(dest, dest.getDescricao()));
//        }
        return listaDest;
    }

    public void recuperarClp() {
        try {
            if (itemEventoCLP.getClp().getId() != null) {
                itemEventoCLP.setClp(clpFacade.recuperar(itemEventoCLP.getClp().getId()));
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void adicionarItem() {
        try {
            recuperarClp();
            validarItemEventoCLP();
            itemEventoCLP.setEventoContabil(selecionado);
            selecionado.getItemEventoCLPs().add(itemEventoCLP);
            itemEventoCLP = new ItemEventoCLP();

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarItemEventoCLP() {
        ValidacaoException ve = new ValidacaoException();
        if (itemEventoCLP.getTagValor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tag Valor deve ser informado.");
        }
        if (itemEventoCLP.getClp() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo CLP deve ser informado.");
        }
        for (ItemEventoCLP item : selecionado.getItemEventoCLPs()) {
            if (item.getTagValor().equals(itemEventoCLP.getTagValor())
                && !DataUtil.isVigenciaValida(itemEventoCLP, selecionado.getItemEventoCLPs())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já foi adicionada um TAG Valor vigente do tipo " + itemEventoCLP.getTagValor().getDescricao() + ". ");
                ve.lancarException();
            }
        }
        ve.lancarException();
        validarCategoriaDasContasDasLcps(itemEventoCLP, ve);
        ve.lancarException();
    }

    public void validarCategoriaDasContasDasLcps(ItemEventoCLP item, ValidacaoException ve) {
        if (!TipoEventoContabil.AJUSTE_CONTABIL_MANUAL.equals(selecionado.getTipoEventoContabil())) {
            for (LCP lcp : item.getClp().getLcps()) {
                validarContaSintetica(lcp.getContaDebito(), "Conta Extra OFSS de Débito", ve);
                validarContaSintetica(lcp.getContaCredito(), "Conta Extra OFSS de Crédito", ve);
                validarContaSintetica(lcp.getContaDebitoIntra(), "Conta Intra OFSS de Débito", ve);
                validarContaSintetica(lcp.getContaCreditoIntra(), "Conta Intra OFSS de Crédito", ve);
                validarContaSintetica(lcp.getContaDebitoInterUniao(), "Conta Inter OFSS União de Débito", ve);
                validarContaSintetica(lcp.getContaCreditoInterUniao(), "Conta Inter OFSS União de Crédito", ve);
                validarContaSintetica(lcp.getContaDebitoInterEstado(), "Conta Inter OFSS Estado de Débito", ve);
                validarContaSintetica(lcp.getContaCreditoInterEstado(), "Conta Inter OFSS Estado de Crédito", ve);
                validarContaSintetica(lcp.getContaDebitoInterMunicipal(), "Conta Inter OFSS Município de Débito", ve);
                validarContaSintetica(lcp.getContaCreditoInterMunicipal(), "Conta Inter OFSS Município de Crédito", ve);
            }
        }
    }

    private void validarContaSintetica(Conta conta, String nomeCampo, ValidacaoException ve) {
        if (conta != null && CategoriaConta.SINTETICA.equals(conta.getCategoria())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A " + nomeCampo + " da LCP não pode ser sintética quando Tipo de Evento Contábil for "
                + selecionado.getTipoEventoContabil().getDescricao());
        }
    }


    public void excluiItem(ActionEvent evt) {
        if (operacao.equals(Operacoes.EDITAR)) {
            try {
                ItemEventoCLP item = (ItemEventoCLP) evt.getComponent().getAttributes().get("excluirItem");
                if (eventoContabilFacade.consultaLancamentosContabilPorEventoECLP((EventoContabil) selecionado, item.getClp(), new Date()).isEmpty()) {
                    item.setDataVigencia(new Date());
                    ((EventoContabil) selecionado).getItemEventoCLPs().remove(item);
                }
            } catch (ExcecaoNegocioGenerica e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível excluir.", e.getMessage()));
            }
        }
        if (operacao.equals(Operacoes.NOVO)) {
            ItemEventoCLP item = (ItemEventoCLP) evt.getComponent().getAttributes().get("excluirItem");
            item.setDataVigencia(new Date());
            ((EventoContabil) selecionado).getItemEventoCLPs().remove(item);
        }
        itemEventoCLP.setClp(null);
    }

    @Override
    public void excluir() {
        try {
            EventoContabil eve = ((EventoContabil) selecionado);
            eventoContabilFacade.excluirEvento(eve);
            FacesUtil.addOperacaoRealizada("Registro excluído com sucesso.");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida("O evento possui configurações ou lançamentos contabeis vinculados a ele.");
        }

    }

    public List<SelectItem> getListaTipoEvento() {
        return Util.getListSelectItem(TipoEventoContabil.values(), true);
    }

    public List<SelectItem> getListaOperacaoConciliacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem("", null));
        for (TipoOperacaoConcilicaoBancaria tec : TipoOperacaoConcilicaoBancaria.values()) {
            toReturn.add(new SelectItem(tec, tec.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoRegistro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem("", null));
        for (TipoLancamento tipo : TipoLancamento.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public void montaChave() {
        EventoContabil ev = (EventoContabil) selecionado;
        if ("".equals(ev.getChave()) || ev.getChave() == null) {
            String chave = StringUtil.removeCaracteresEspeciais(ev.getDescricao());
            chave = chave.toLowerCase();
            chave = chave.replaceAll(" a ", " ").replaceAll(" o ", " ").replaceAll(" e ", " ").replaceAll(" de ", " ").replaceAll(" do ", " ").replaceAll(" da ", " ").replaceAll(" em ", " ").replaceAll(",", " ").replaceAll("\\.", " ");
            chave = chave.replaceAll("  ", " ");
            ev.setChave(chave.replaceAll(" ", "_").toUpperCase());
            RequestContext.getCurrentInstance().execute("setaFoco('Formulario:eventotce');");
        }

    }

    public EventoContabil getEventoNaoAlterado() {
        return eventoNaoAlterado;
    }

    public void setEventoNaoAlterado(EventoContabil eventoNaoAlterado) {
        this.eventoNaoAlterado = eventoNaoAlterado;
    }

    public boolean podeEditarEvento() {
        EventoContabil evento = (EventoContabil) selecionado;
        if (evento.getFimVigencia() == null) {
            return true;
        }
        if (Util.getDataHoraMinutoSegundoZerado(evento.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao())) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public void validaDataEncerraVigencia(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date data = (Date) value;
        EventoContabil obj = ((EventoContabil) selecionado);
        if (Util.getDataHoraMinutoSegundoZerado(data).compareTo(Util.getDataHoraMinutoSegundoZerado(obj.getInicioVigencia())) < 0) {
            message.setSummary("Operação não Permitida");
            message.setDetail("A Data Fim de Vigência, deve ser superior a data de Início de Vigência.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void encerrarVigencia() {
        try {
            if (eventoContabilFacade.consultaLancamentosContabilPorEvento((EventoContabil) selecionado, ((EventoContabil) selecionado).getFimVigencia()).isEmpty()) {
                eventoContabilFacade.salvar((EventoContabil) selecionado);
                FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
                redireciona();
            } else {
                FacesUtil.addOperacaoNaoPermitida("Não foi possível encerrar a vigência para este evento contábil. Para excluir este Evento Contábil, é necessário que seja excluidos primeiros os Lançamentos Contabéis deste Evento Contábil.");
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void atualizarComValoresDaSessao() {
        Boolean isLancamentoManual = (Boolean) Web.pegaDaSessao("IS_LANCAMENTO_MANUAL");
        if (isLancamentoManual != null && isLancamentoManual) {
            selecionado.setTipoEventoContabil(TipoEventoContabil.AJUSTE_CONTABIL_MANUAL);
            selecionado.setTipoLancamento(TipoLancamento.NORMAL);
            selecionado.setTipoBalancete(TipoBalancete.MENSAL);
        }
    }

    public boolean isTipoEventoAjusteManual() {
        return TipoEventoContabil.AJUSTE_CONTABIL_MANUAL.equals(selecionado.getTipoEventoContabil());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/eventocontabil/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
