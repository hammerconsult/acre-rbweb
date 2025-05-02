/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EstornoAlteracaoOrcFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Edi
 */
@ManagedBean(name = "estornoAlteracaoOrcControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-estorno-alteracao-orcamentaria", pattern = "/estorno-alteracao-orcamentaria/novo/", viewId = "/faces/financeiro/orcamentario/estornoalteracaoorc/edita.xhtml"),
    @URLMapping(id = "editar-estorno-alteracao-orcamentaria", pattern = "/estorno-alteracao-orcamentaria/editar/#{estornoAlteracaoOrcControlador.id}/", viewId = "/faces/financeiro/orcamentario/estornoalteracaoorc/edita.xhtml"),
    @URLMapping(id = "ver-estorno-alteracao-orcamentaria", pattern = "/estorno-alteracao-orcamentaria/ver/#{estornoAlteracaoOrcControlador.id}/", viewId = "/faces/financeiro/orcamentario/estornoalteracaoorc/visualizar.xhtml"),
    @URLMapping(id = "listar-estorno-alteracao-orcamentaria", pattern = "/estorno-alteracao-orcamentaria/listar/", viewId = "/faces/financeiro/orcamentario/estornoalteracaoorc/lista.xhtml")
})
public class EstornoAlteracaoOrcControlador extends PrettyControlador<EstornoAlteracaoOrc> implements Serializable, CRUD {

    @EJB
    private EstornoAlteracaoOrcFacade estornoAlteracaoOrcFacade;
    private ConverterAutoComplete converterAlteracaoOrc;
    private MoneyConverter moneyConverter;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private AlteracaoORC alteracaoORC;
    private List<SuplementacaoORC> suplementacaoORCs;
    private List<AnulacaoORC> anulacaoORCs;
    private List<ReceitaAlteracaoORC> receitaAlteracaoORCs;

    public EstornoAlteracaoOrcControlador() {
        super(EstornoAlteracaoOrc.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return estornoAlteracaoOrcFacade;
    }

    @URLAction(mappingId = "novo-estorno-alteracao-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        parametrosIniciais();
    }

    @URLAction(mappingId = "ver-estorno-alteracao-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "editar-estorno-alteracao-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/estorno-alteracao-orcamentaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void parametrosIniciais() {
        selecionado = new EstornoAlteracaoOrc();
        suplementacaoORCs = new ArrayList<>();
        anulacaoORCs = new ArrayList<>();
        receitaAlteracaoORCs = new ArrayList<>();
        operacao = Operacoes.NOVO;
        selecionado.setDataEstorno(sistemaControlador.getDataOperacao());
        selecionado.setUnidadeOrganizacionalOrc(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setValor(BigDecimal.ZERO);
    }

    private void recuperaEditaVer() {
        alteracaoORC = estornoAlteracaoOrcFacade.getAlteracaoORCFacade().recuperar(selecionado.getAlteracaoORC().getId());
        getSuplementacao();
        getAnulacao();
        getReceita();
    }

    public Boolean habilitaEditar() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        }
        return false;
    }

    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            if (isOperacaoNovo()) {
                estornoAlteracaoOrcFacade.salvarNovo(selecionado);
            } else {
                estornoAlteracaoOrcFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e){
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }


    public List<AlteracaoORC> completaAlteracaoOrc(String parte) {
        try {
            return estornoAlteracaoOrcFacade.getAlteracaoORCFacade().listaAlteracaoORCDeferidaPorExercicio(parte.trim(), selecionado.getExercicio());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public BigDecimal totalSuplementacoes() {
        BigDecimal total = BigDecimal.ZERO;
        if (alteracaoORC != null) {
            for (SuplementacaoORC suplementacaoORC : alteracaoORC.getSuplementacoesORCs()) {
                total = total.add(suplementacaoORC.getValor());
            }
        }
        return total;
    }

    public BigDecimal totalAnulacoes() {
        BigDecimal total = BigDecimal.ZERO;
        if (alteracaoORC != null) {
            for (AnulacaoORC anulacaoORC : alteracaoORC.getAnulacoesORCs()) {
                total = total.add(anulacaoORC.getValor());
            }
        }
        return total;
    }

    public BigDecimal totalReceitas() {
        BigDecimal total = BigDecimal.ZERO;
        if (alteracaoORC != null) {
            for (ReceitaAlteracaoORC receitaAlteracaoORC : alteracaoORC.getReceitasAlteracoesORCs()) {
                total = total.add(receitaAlteracaoORC.getValor());
            }
        }
        return total;
    }

    public ConverterAutoComplete getConverterAlteracaoOrc() {
        if (converterAlteracaoOrc == null) {
            converterAlteracaoOrc = new ConverterAutoComplete(AlteracaoORC.class, estornoAlteracaoOrcFacade.getAlteracaoORCFacade());
        }
        return converterAlteracaoOrc;
    }


    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }


    public void selecionarAlteracaoORC(ActionEvent evento) {
        alteracaoORC = (AlteracaoORC) evento.getComponent().getAttributes().get("objeto");
        recuperarAlteracaoORC();
    }

    public AlteracaoORC setarAlteracaoORC() {
        alteracaoORC = estornoAlteracaoOrcFacade.getAlteracaoORCFacade().recuperar(alteracaoORC.getId());
        recuperarAlteracaoORC();
        return alteracaoORC;
    }

    private void recuperarAlteracaoORC() {
        getSuplementacao();
        getAnulacao();
        getReceita();
        selecionado.setAlteracaoORC(alteracaoORC);
        selecionado.setExercicio(alteracaoORC.getExercicio());
        selecionado.setUnidadeOrganizacionalAdm(alteracaoORC.getUnidadeOrganizacionalAdm());
        selecionado.setUnidadeOrganizacionalOrc(alteracaoORC.getUnidadeOrganizacionalOrc());
        if (!alteracaoORC.getSuplementacoesORCs().isEmpty()) {
            selecionado.setValor(totalSuplementacoes());
        } else {
            selecionado.setValor(totalAnulacoes());
        }
        getEstornosSuplementacaoOrcs();
        getEstornosAnulacaoORC();
        getEstornoReceitaAlteracaoORC();
    }

    public void getSuplementacao() {
        if (alteracaoORC != null) {
            suplementacaoORCs = estornoAlteracaoOrcFacade.getAlteracaoORCFacade().recuperarSuplementacaoPorAlteracaoORC(alteracaoORC);
        }
    }

    public void getAnulacao() {
        if (alteracaoORC != null) {
            anulacaoORCs = estornoAlteracaoOrcFacade.getAlteracaoORCFacade().recuperarAnulacaoPorAlteracaoORC(alteracaoORC);
        }
    }

    public void getReceita() {
        if (alteracaoORC != null) {
            receitaAlteracaoORCs = estornoAlteracaoOrcFacade.getAlteracaoORCFacade().recuperarReceitaPorAlteracaoORC(alteracaoORC);
        }
    }

    public void getEstornosSuplementacaoOrcs() {
        for (SuplementacaoORC suplementacaoORC : suplementacaoORCs) {
            EstornoSuplementacaoOrc estornoSuplementacaoOrc = new EstornoSuplementacaoOrc();
            estornoSuplementacaoOrc.setValor(suplementacaoORC.getValor());
            estornoSuplementacaoOrc.setSuplementacaoORC(suplementacaoORC);
            estornoSuplementacaoOrc.setEstornoAlteracaoOrc(selecionado);
            selecionado.getListaEstornoSuplementacaoOrc().add(estornoSuplementacaoOrc);
        }
    }

    public void getEstornosAnulacaoORC() {
        for (AnulacaoORC anulacaoORC : anulacaoORCs) {
            EstornoAnulacaoOrc estornoAnulacaoOrc = new EstornoAnulacaoOrc();
            estornoAnulacaoOrc.setValor(anulacaoORC.getValor());
            estornoAnulacaoOrc.setAnulacaoORC(anulacaoORC);
            estornoAnulacaoOrc.setEstornoAlteracaoOrc(selecionado);
            selecionado.getListaEstornoAnulacaoOrc().add(estornoAnulacaoOrc);
        }
    }

    public void getEstornoReceitaAlteracaoORC() {
        for (ReceitaAlteracaoORC receitaAlteracaoORC : receitaAlteracaoORCs) {
            EstornoReceitaAlteracaoOrc estornoReceitaAlteracaoOrc = new EstornoReceitaAlteracaoOrc();
            estornoReceitaAlteracaoOrc.setValor(receitaAlteracaoORC.getValor());
            estornoReceitaAlteracaoOrc.setReceitaAlteracaoORC(receitaAlteracaoORC);
            estornoReceitaAlteracaoOrc.setEstornoAlteracaoOrc(selecionado);
            selecionado.getListaEstornoReceitaAlteracaoOrc().add(estornoReceitaAlteracaoOrc);
        }
    }

    public void limparlista() {
        suplementacaoORCs = new ArrayList<>();
        anulacaoORCs = new ArrayList<>();
        receitaAlteracaoORCs = new ArrayList<>();
        selecionado.setListaEstornoSuplementacaoOrc(new ArrayList<EstornoSuplementacaoOrc>());
        selecionado.setListaEstornoAnulacaoOrc(new ArrayList<EstornoAnulacaoOrc>());
        selecionado.setListaEstornoReceitaAlteracaoOrc(new ArrayList<EstornoReceitaAlteracaoOrc>());
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public AlteracaoORC getAlteracaoORC() {
        return alteracaoORC;
    }

    public void setAlteracaoORC(AlteracaoORC alteracaoORC) {
        this.alteracaoORC = alteracaoORC;
    }

    public List<SuplementacaoORC> getSuplementacaoORCs() {
        return suplementacaoORCs;
    }

    public void setSuplementacaoORCs(List<SuplementacaoORC> suplementacaoORCs) {
        this.suplementacaoORCs = suplementacaoORCs;
    }

    public List<AnulacaoORC> getAnulacaoORCs() {
        return anulacaoORCs;
    }

    public void setAnulacaoORCs(List<AnulacaoORC> anulacaoORCs) {
        this.anulacaoORCs = anulacaoORCs;
    }

    public List<ReceitaAlteracaoORC> getReceitaAlteracaoORCs() {
        return receitaAlteracaoORCs;
    }

    public void setReceitaAlteracaoORCs(List<ReceitaAlteracaoORC> receitaAlteracaoORCs) {
        this.receitaAlteracaoORCs = receitaAlteracaoORCs;
    }
}
