/*
 * Codigo gerado automaticamente em Thu Apr 28 09:09:59 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidaLDOExeption;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "lOAControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoloa", pattern = "/loa/loa/novo/", viewId = "/faces/financeiro/ppa/loa/edita.xhtml"),
    @URLMapping(id = "editarloa", pattern = "/loa/loa/editar/#{lOAControlador.id}/", viewId = "/faces/financeiro/ppa/loa/edita.xhtml"),
    @URLMapping(id = "verloa", pattern = "/loa/loa/ver/#{lOAControlador.id}/", viewId = "/faces/financeiro/ppa/loa/visualizar.xhtml"),
    @URLMapping(id = "listarloa", pattern = "/loa/loa/listar/", viewId = "/faces/financeiro/ppa/loa/lista.xhtml")
})
public class LOAControlador extends PrettyControlador<LOA> implements Serializable, CRUD {

    @EJB
    private LOAFacade lOAFacade;
    @EJB
    private LDOFacade ldoFacade;
    private ConverterGenerico converterLdo;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;
    private MoneyConverter moneyConverter;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @ManagedProperty(name = "componenteTreeDespesaORC", value = "#{componente}")
    private ComponenteTreeDespesaORC componenteTreeDespesaORC;
    private Exercicio exercicioVersaoPPA;

    public LOAControlador() {
        super(LOA.class);
    }

    public LOAFacade getFacade() {
        return lOAFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return lOAFacade;
    }

    public void aprovaLOA() throws ExcecaoNegocioGenerica {
        if (validaAprovacao()) {
            lOAFacade.salvar(selecionado);
            RequestContext.getCurrentInstance().execute("dialogDataAprovacao.hide()");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "LOA aprovada!"));
            redireciona();
        }
    }

    public void cancelaAprovacaoLOA() {
        selecionado.setAprovacao(null);
        RequestContext.getCurrentInstance().execute("dialogDataAprovacao.hide()");
    }

    private Boolean validaAprovacao() {
        if (selecionado.getAprovacao() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "O campo Data de Aprovação é obrigatório"));
            return false;
        }
        if (selecionado.getAtoLegal() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "O campo Lei é obrigatório"));
            return false;
        }
        return true;
    }

    public Boolean validaLOAContabilizacao() {
        Boolean controle = true;
        LOA ll = ((LOA) selecionado);
        if (ll.getEfetivada() == null || ll.getEfetivada() == false) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro: LOA não efetivada, favor efetivar a LOA antes de realizar a contabilização", ""));
            controle = false;
        } else {
            RequestContext.getCurrentInstance().execute("dialogCont.show()");
            selecionado.setDataContabilizacao(sistemaControlador.getDataOperacao());
        }
        return controle;
    }

    public void abrirDialogAprova() {
        RequestContext.getCurrentInstance().execute("dialogDataAprovacao.show()");
        selecionado.setAprovacao(sistemaControlador.getDataOperacao());
    }

    public void geraContabilizacao() throws JRException, IOException, ExcecaoNegocioGenerica {
        LOA ll = ((LOA) selecionado);
        try {
//            if (selecionado.getDataContabilizacao().before(ll.getAprovacao())) {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro: ", "A Data de Contabilização não pode ser menor que a Data da Aprovação."));
//            } else {
            RequestContext.getCurrentInstance().execute("dialogCont.hide()");
            ll.setContabilizado(Boolean.TRUE);
            lOAFacade.validaLoa(ll);

            lOAFacade.geraContabilizacaoNovo(ll);

            lOAFacade.salvar(ll);
            redireciona();
            FacesUtil.addInfo("Contabilização efetuada com sucesso!", "");
//            }
        } catch (ExcecaoNegocioGenerica | ValidaLDOExeption e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            if (validaValores()) {
                super.salvar();
            }
        }
    }

    private Boolean validaValores() {
        if (selecionado.getValorDaDespesa().compareTo(BigDecimal.ZERO) <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao Salvar!", "O campo Valor da Despesa deve ser Maior que Zero!"));
            return false;
        }
        if (selecionado.getValorDaReceita().compareTo(BigDecimal.ZERO) <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao Salvar!", "O campo Valor da Receita deve ser Maior que Zero!"));
            return false;
        }
        return true;
    }

    public List<SelectItem> getLdo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (LDO object : ldoFacade.listLDOExercicio(sistemaControlador.getExercicioCorrente())) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterLdo() {
        if (converterLdo == null) {
            converterLdo = new ConverterGenerico(LDO.class, ldoFacade);
        }
        return converterLdo;
    }

    public List<SelectItem> getAtoLegal() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (AtoLegal object : atoLegalFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.buscarAtosLegaisPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void geraEfetivacaoLoa() {
        try {
            if (selecionado.getEfetivada()) {
                FacesUtil.addOperacaoNaoRealizada("A loa já está efetivada!");
                return;
            }
            lOAFacade.efetivacaoLoa(selecionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Gerada com sucesso!", "Gerada com sucesso!"));
            redireciona();
        } catch (ValidaLDOExeption e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
        }
    }

    public void reverteEfetivacaoLoa() {
        try {
            lOAFacade.validaReversaoLoa(((LOA) selecionado));
            lOAFacade.reverteEfetivacaoLoa(((LOA) selecionado));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Revertida com sucesso!", "Revertida com sucesso!"));
        } catch (ExcecaoNegocioGenerica | ValidaLDOExeption e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
    }


    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ComponenteTreeDespesaORC getComponenteTreeDespesaORC() {
        return componenteTreeDespesaORC;
    }

    public void setComponenteTreeDespesaORC(ComponenteTreeDespesaORC componenteTreeDespesaORC) {
        this.componenteTreeDespesaORC = componenteTreeDespesaORC;
    }

    public Exercicio getExercicioVersaoPPA() {
        return exercicioVersaoPPA;
    }

    public void setExercicioVersaoPPA(Exercicio exercicioVersaoPPA) {
        this.exercicioVersaoPPA = exercicioVersaoPPA;
    }

    @URLAction(mappingId = "novoloa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setVersao("1");
    }

    @URLAction(mappingId = "editarloa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "verloa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    private void recuperaEditarVer() {
        selecionado = lOAFacade.recuperar(selecionado.getId());

        componenteTreeDespesaORC.setCodigo("");
        componenteTreeDespesaORC.setDespesaORCSelecionada(new DespesaORC());
        componenteTreeDespesaORC.setDespesaSTR("");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/loa/loa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void gerarNovaVersao() {
        try {
            lOAFacade.gerarNovaVersao(selecionado, exercicioVersaoPPA);
            FacesUtil.addOperacaoRealizada("Foi transportado os projeto/atividades para " + exercicioVersaoPPA.getAno() + "!");
            redireciona();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }
}
