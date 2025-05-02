/*
 * Codigo gerado automaticamente em Thu Jan 12 14:49:52 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OrigemReservaFonte;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "reservaDotacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-reserva-dotacao", pattern = "/reserva-dotacao/novo/", viewId = "/faces/financeiro/orcamentario/reservadotacao/edita.xhtml"),
    @URLMapping(id = "editar-reserva-dotacao", pattern = "/reserva-dotacao/editar/#{reservaDotacaoControlador.id}/", viewId = "/faces/financeiro/orcamentario/reservadotacao/edita.xhtml"),
    @URLMapping(id = "ver-reserva-dotacao", pattern = "/reserva-dotacao/ver/#{reservaDotacaoControlador.id}/", viewId = "/faces/financeiro/orcamentario/reservadotacao/visualizar.xhtml"),
    @URLMapping(id = "listar-reserva-dotacao", pattern = "/reserva-dotacao/listar/", viewId = "/faces/financeiro/orcamentario/reservadotacao/lista.xhtml")
})
public class ReservaDotacaoControlador extends PrettyControlador<ReservaDotacao> implements Serializable, CRUD {

    @EJB
    private ReservaDotacaoFacade reservaDotacaoFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    private ConverterAutoComplete converterFonteDespesaORC;
    private MoneyConverter moneyConverter;
    @ManagedProperty(name = "componenteTreeDespesaORC", value = "#{componente}")
    private ComponenteTreeDespesaORC componenteTreeDespesaORC;
    private DespesaORC despesaORC;

    public ReservaDotacaoControlador() {
        super(ReservaDotacao.class);
    }

    public ReservaDotacaoFacade getFacade() {
        return reservaDotacaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return reservaDotacaoFacade;
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarAtoLegal();
            if (isOperacaoNovo()) {
                reservaDotacaoFacade.salvarNovo(selecionado);
            } else {
                reservaDotacaoFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica e){
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarAtoLegal() {
        ValidacaoException ve = new ValidacaoException();
        if (OrigemReservaFonte.ATO_LEGAL.equals(selecionado.getOrigemReservaFonte())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ato Legal deve ser informado quando o Tipo é Ato Legal.");
        }
        ve.lancarException();
    }

    public void geraLiberacao() {
        try {
            ReservaDotacao res = ((ReservaDotacao) selecionado);
            reservaDotacaoFacade.geraLiberacao(res);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Efetivado com Sucesso!", " "));
            redireciona();
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "Problema ao Efetivar! Consulte o suporte do Sistema!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public BigDecimal getSaldoFonteDespesaORC() {
        BigDecimal saldo = new BigDecimal(BigInteger.ZERO);
        ReservaDotacao rd = ((ReservaDotacao) selecionado);
        if (rd.getFonteDespesaORC() != null) {
            saldo = fonteDespesaORCFacade.saldoRealPorFonte(rd.getFonteDespesaORC(), rd.getDataReserva(), rd.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional());
            if (saldo == null) {
                saldo = new BigDecimal(BigInteger.ZERO);
            }
            return saldo;
        }
        return saldo;
    }

    public void validaSaldoFonteDespesa(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        BigDecimal saldo = getSaldoFonteDespesaORC();
        BigDecimal valor = (BigDecimal) value;
        if (((ReservaDotacao) selecionado).getFonteDespesaORC() != null) {
            if (valor.compareTo(saldo) > 0) {
                message.setDetail("Valor Informado não pode ser maior que o saldo da Fonte de Despesa Orçamentária!");
                message.setSummary("Valor Informado não pode ser maior que o saldo da Fonte de Despesa Orçamentária!");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public Boolean getVerificaLiberado() {
        if (((ReservaDotacao) selecionado).getLiberado() == true) {
            return true;
        }
        return false;
    }

    public List<SelectItem> getPropositosAtoLegal() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (PropositoAtoLegal object : PropositoAtoLegal.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        return atoLegalFacade.buscarAtosLegaisPorPropositoAtoLegal(parte.trim(), PropositoAtoLegal.RESERVA_DOTACAO);
    }

    public List<SelectItem> getOrigensRecursos() {
        return Util.getListSelectItem(OrigemReservaFonte.values());
    }

    public List<FonteDespesaORC> completaFonteDespesaORC(String parte) {
        despesaORC = componenteTreeDespesaORC.getDespesaORCSelecionada();
        if (despesaORC.getId() != null) {
            return fonteDespesaORCFacade.completaFonteDespesaORC(parte.trim(), despesaORC);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Selecione uma despesa orçamentária antes de buscar a Fonte de Despesa");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return new ArrayList<FonteDespesaORC>();
        }
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, fonteDespesaORCFacade);
        }
        return converterFonteDespesaORC;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public ComponenteTreeDespesaORC getComponenteTreeDespesaORC() {
        return componenteTreeDespesaORC;
    }

    public void setComponenteTreeDespesaORC(ComponenteTreeDespesaORC componenteTreeDespesaORC) {
        this.componenteTreeDespesaORC = componenteTreeDespesaORC;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    @URLAction(mappingId = "novo-reserva-dotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        componenteTreeDespesaORC.setCodigo("");
        componenteTreeDespesaORC.setDespesaORCSelecionada(new DespesaORC());
        componenteTreeDespesaORC.setDespesaSTR("");
        selecionado.setDataReserva(getSistemaControlador().getDataOperacao());
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "ver-reserva-dotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarComponenteTreeDespesaOrc();
    }

    @URLAction(mappingId = "editar-reserva-dotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarComponenteTreeDespesaOrc();
    }

    private void recuperarComponenteTreeDespesaOrc() {
        componenteTreeDespesaORC.setCodigo(selecionado.getFonteDespesaORC().getDespesaORC().getCodigoReduzido());
        componenteTreeDespesaORC.setDespesaORCSelecionada(selecionado.getFonteDespesaORC().getDespesaORC());
        componenteTreeDespesaORC.setDespesaSTR(reservaDotacaoFacade.getDespesaORCFacade().recuperaStrDespesaPorId(selecionado.getFonteDespesaORC().getDespesaORC().getId()).getConta());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/reserva-dotacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
