/*
 * Codigo gerado automaticamente em Wed Sep 05 10:41:50 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoOperacaoReceitaLoa;
import br.com.webpublico.enums.TipoSaldoReceitaORC;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EstornoReceitaLOAFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-estorno-receita-loa", pattern = "/estorno-receita-loa/novo/", viewId = "/faces/financeiro/ppa/estornoreceitaloa/edita.xhtml"),
    @URLMapping(id = "editar-estorno-receita-loa", pattern = "/estorno-receita-loa/editar/#{estornoReceitaLOAControlador.id}/", viewId = "/faces/financeiro/ppa/estornoreceitaloa/edita.xhtml"),
    @URLMapping(id = "ver-estorno-receita-loa", pattern = "/estorno-receita-loa/ver/#{estornoReceitaLOAControlador.id}/", viewId = "/faces/financeiro/ppa/estornoreceitaloa/visualizar.xhtml"),
    @URLMapping(id = "lista-estorno-receita-loa", pattern = "/estorno-receita-loa/listar/", viewId = "/faces/financeiro/ppa/estornoreceitaloa/lista.xhtml")
})
public class EstornoReceitaLOAControlador extends PrettyControlador<EstornoReceitaLOA> implements Serializable, CRUD {

    @EJB
    private EstornoReceitaLOAFacade estornoReceitaLOAFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private MoneyConverter moneyConverter;
    private ConverterAutoComplete converterReceitaLOA;
    private BigDecimal saldo;

    public EstornoReceitaLOAControlador() {
        super(EstornoReceitaLOA.class);
    }

    public EstornoReceitaLOAFacade getFacade() {
        return estornoReceitaLOAFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return estornoReceitaLOAFacade;
    }

    @URLAction(mappingId = "novo-estorno-receita-loa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ((EstornoReceitaLOA) selecionado).setDataEstorno(new Date());
        saldo = null;
    }

    @URLAction(mappingId = "ver-estorno-receita-loa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "editar-estorno-receita-loa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
//        selecionar(evento);
        EstornoReceitaLOA estornoReceitaLOA = (EstornoReceitaLOA) selecionado;
        saldo = estornoReceitaLOA.getReceitaLOA().getSaldo();
    }

    public ReceitaLOA baixaSaldoReceitaLOA(ReceitaLOA rl) {
        EstornoReceitaLOA estornoReceitaLOA = ((EstornoReceitaLOA) selecionado);
        if (estornoReceitaLOA.getReceitaLOA() != null) {
            rl.setSaldo(rl.getSaldo().subtract(estornoReceitaLOA.getValor()));
        }
        return rl;
    }

    public void validaValores(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        EstornoReceitaLOA estornoReceitaLOA = ((EstornoReceitaLOA) selecionado);
        BigDecimal valor = (BigDecimal) value;
        if (estornoReceitaLOA.getReceitaLOA() != null) {
            BigDecimal saldoLocal = new BigDecimal(BigInteger.ZERO);
            if (estornoReceitaLOA.getReceitaLOA().getSaldo() != null) {
                saldoLocal = estornoReceitaLOA.getReceitaLOA().getSaldo();
            }
            if (saldoLocal.compareTo(valor) < 0) {
                message.setDetail("Valor do Estorno maior que o saldo disponível na Receita LOA!");
                message.setSummary("Valor do Estorno maior que o saldo disponível na Receita LOA!");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public Boolean getVerificaEdicao() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void salvar() {
        EstornoReceitaLOA estornoReceitaLOA = (EstornoReceitaLOA) selecionado;
        if (((EstornoReceitaLOA) selecionado).getId() == null) {
            ((EstornoReceitaLOA) selecionado).setNumero(getNumeroMaiorEstornoReceitaLOA().toString());
        }
        if (Util.validaCampos(selecionado)) {
            if (validaValor()) {
                try {
//                Comentado por impedimento de alterações nesse cadastro
//                calculaSaldoRecetaORC(estornoReceitaLOA);
                    estornoReceitaLOAFacade.getReceitaLOAFacade().salvar(baixaSaldoReceitaLOA(estornoReceitaLOA.getReceitaLOA()));
                    if (operacao == Operacoes.NOVO) {
                        estornoReceitaLOA.setNumero(getNumeroMaiorEstornoReceitaLOA().toString());
                        this.getFacede().salvarNovo(selecionado);
                    } else {
                        this.getFacede().salvar(selecionado);
                    }
                    redireciona();
//                lista = null;

                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
//                return "lista.xhtml";

                } catch (Exception e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage()));
//                return "edita.xhtml";
                }
            }
        }
    }

    private Boolean validaValor() {
        boolean retorno = true;
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            retorno = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro.", "O valor deve ser maior que zero(0)!"));
        }
        return retorno;
    }

    private void calculaSaldoRecetaORC(EstornoReceitaLOA estornoReceitaLOA) throws ExcecaoNegocioGenerica {
        // geração do SaldoReceitaORC
        List<ReceitaLOAFonte> listaReceitaLoaFontes = estornoReceitaLOAFacade.getReceitaLOAFacade().listaReceitaLoaFontes(estornoReceitaLOA.getReceitaLOA());
        BigDecimal somaValorCalc = new BigDecimal(BigInteger.ZERO);
        BigDecimal diferenca = new BigDecimal(BigInteger.ZERO);
        for (ReceitaLOAFonte rf : listaReceitaLoaFontes) {
            BigDecimal valorCalculado = new BigDecimal(BigInteger.ZERO);
            valorCalculado = estornoReceitaLOA.getValor().multiply(rf.getPercentual().divide(new BigDecimal(100)));
            somaValorCalc = somaValorCalc.add(valorCalculado);
            estornoReceitaLOAFacade.getSaldoReceitaORCFacade().geraSaldo(TipoSaldoReceitaORC.ESTORNORECEITALOA, estornoReceitaLOA.getDataEstorno(), estornoReceitaLOA.getReceitaLOA().getEntidade(), estornoReceitaLOA.getReceitaLOA().getContaDeReceita(), rf.getDestinacaoDeRecursos().getFonteDeRecursos(), valorCalculado);
        }
        if (!somaValorCalc.equals(estornoReceitaLOA.getValor())) {
            for (ReceitaLOAFonte rf : listaReceitaLoaFontes) {
                if (rf.getRounding()) {
                    diferenca = estornoReceitaLOA.getValor().subtract(somaValorCalc);
                    estornoReceitaLOAFacade.getSaldoReceitaORCFacade().geraSaldo(TipoSaldoReceitaORC.ESTORNORECEITALOA, estornoReceitaLOA.getDataEstorno(), estornoReceitaLOA.getReceitaLOA().getEntidade(), estornoReceitaLOA.getReceitaLOA().getContaDeReceita(), rf.getDestinacaoDeRecursos().getFonteDeRecursos(), diferenca);
                }
            }
        }
    }

    public void setaSaldoReceitaLOA(SelectEvent evt) {
        ReceitaLOA rl = estornoReceitaLOAFacade.getReceitaLOAFacade().recuperar(((ReceitaLOA) evt.getObject()).getId());
        saldo = rl.getSaldo();
    }

    public Integer getNumeroMaiorEstornoReceitaLOA() {
        BigDecimal maior = new BigDecimal(estornoReceitaLOAFacade.getUltimoNumero());
        maior = maior.add(BigDecimal.ONE);
        return maior.intValue();
    }

    public List<SelectItem> getListaTipoOperacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoOperacaoReceitaLoa tp : TipoOperacaoReceitaLoa.values()) {
            toReturn.add(new SelectItem(tp, tp.getDescricao()));
        }
        return toReturn;
    }

    public boolean verificaLOAEfetivada() {
        EstornoReceitaLOA estornoReceitaLOA = (EstornoReceitaLOA) selecionado;
        if (estornoReceitaLOAFacade.getReceitaLOAFacade().getLoaFacade().hasLoaEfetivadaNoExercicio(sistemaControlador.getExercicioCorrente())) {
            return false;
        } else {
            return true;
        }
    }

    public List<ReceitaLOA> completaReceitaLOA(String parte) {
        return estornoReceitaLOAFacade.getReceitaLOAFacade().listaPorCodigoReduzidoCodigoDescricao(parte, null);
    }

    public ConverterAutoComplete getConverterReceitaLOA() {
        if (converterReceitaLOA == null) {
            converterReceitaLOA = new ConverterAutoComplete(ReceitaLOA.class, estornoReceitaLOAFacade.getReceitaLOAFacade());
        }
        return converterReceitaLOA;
    }

    private boolean existeEstornoReceitaLOAComNumero() {
        return estornoReceitaLOAFacade.existeEstornoReceitaLOAComNumero(((EstornoReceitaLOA) selecionado).getNumero());
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/estorno-receita-loa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
