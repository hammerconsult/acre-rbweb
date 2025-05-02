/*
 * Codigo gerado automaticamente em Tue Apr 12 15:18:29 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRException;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "pPAControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoppa",   pattern = "/ppa/ppa/novo/",                        viewId = "/faces/financeiro/ppa/ppa/edita.xhtml"),
        @URLMapping(id = "editarppa", pattern = "/ppa/ppa/editar/#{pPAControlador.id}/", viewId = "/faces/financeiro/ppa/ppa/edita.xhtml"),
        @URLMapping(id = "verppa",    pattern = "/ppa/ppa/ver/#{pPAControlador.id}/",    viewId = "/faces/financeiro/ppa/ppa/visualizar.xhtml"),
        @URLMapping(id = "listarppa", pattern = "/ppa/ppa/listar/",                      viewId = "/faces/financeiro/ppa/ppa/lista.xhtml")
})
public class PPAControlador extends PrettyControlador<PPA> implements Serializable, CRUD {

    @EJB
    private PPAFacade pPAFacade;
    protected ConverterGenerico converterPlanejamentoEstrategico;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    protected ConverterAutoComplete converterAtoLegal;
    private Exercicio exercicioSelecionado;
    private PPA pPASelecionado;
    private Date dtContabilizacao;
    private Exercicio exercicioVersaoPPA;


    @Override
    public String getCaminhoPadrao() {
        return "/ppa/ppa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verppa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editarppa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    public Boolean validaPPAContabilizacao() {
        Boolean controle = true;
        PPA pp = ((PPA) selecionado);
        if (pp.getAprovacao() == null && pp.getSomenteLeitura() == false) {
            FacesUtil.addOperacaoNaoPermitida("Por favor, aprovar o PPA, antes de realizar a contabilização.");
            controle = false;
        } else {
            RequestContext.getCurrentInstance().execute("dialogCont.show()");
        }
        return controle;
    }


    private void recuperaEditarVer() {
        selecionado = pPAFacade.recuperar(selecionado.getId());
    }

    public void geraContabilizacao() throws JRException, IOException, ExcecaoNegocioGenerica {
        PPA pp = ((PPA) selecionado);
        try {
            if (validaContabilizarPPA()) {
                pp.setContabilizado(Boolean.TRUE);
                pPAFacade.salvar(pp);

                pPAFacade.geraContabilizacao(pp, dtContabilizacao);
                RequestContext.getCurrentInstance().update(":Formulario");
                RequestContext.getCurrentInstance().execute("dialogCont.hide()");
                redireciona();
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public Boolean validaContabilizarPPA() {
        if (dtContabilizacao == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Data de Contabilização deve ser informado.");
            return false;
        }
        if (dtContabilizacao.before(selecionado.getAprovacao())) {
            FacesUtil.addOperacaoNaoPermitida("A Data de Contabilização não pode ser menor que a Data da Aprovação.");
            return false;
        }
        return true;
    }

    @Override
    public AbstractFacade getFacede() {
        return pPAFacade;
    }

    @Override
    public void salvar() {
        if (((PPA) selecionado).getSomenteLeitura() == true) {
            FacesUtil.addOperacaoNaoPermitida("PPA aprovado, não pode sofrer modificações.");
            redireciona();
        } else {
            super.salvar();
        }
    }

    @URLAction(mappingId = "novoppa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ((PPA) selecionado).setVersao(geraVersaoPpa());
    }
//
//    @Override
//    public void selecionar(ActionEvent evento) {
//        super.selecionar(evento);
//        if (((PPA) selecionado).getAprovacao() != null) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Atenção! PPA Aprovado. Não pode sofrer modificações.", " "));
//        }
//    }

    public List<SelectItem> getPlanejamentoEstrategico() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (operacao == Operacoes.NOVO) {
            for (PlanejamentoEstrategico object : pPAFacade.getPlanejamentoEstrategicoFacade().listaDisponiveis()) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        } else {
            for (PlanejamentoEstrategico object : pPAFacade.getPlanejamentoEstrategicoFacade().lista()) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getListaPPAs() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PPA object : pPAFacade.listaTodosPpaExericicioCombo(exercicioSelecionado)) {
            toReturn.add(new SelectItem(object, object.getDescricao() + " --- " + object.getVersao() + " --- " + object.getAprovacao()));
        }
        return toReturn;
    }

    public List<SelectItem> getAtoLegal() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (AtoLegal object : pPAFacade.getAtoLegalFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterPlanejamentoEstrategico() {
        if (converterPlanejamentoEstrategico == null) {
            converterPlanejamentoEstrategico = new ConverterGenerico(PlanejamentoEstrategico.class, pPAFacade.getPlanejamentoEstrategicoFacade());
        }
        return converterPlanejamentoEstrategico;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, pPAFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return pPAFacade.getAtoLegalFacade().listAtoPorPropositoAtoLegal(parte.trim(), PropositoAtoLegal.PPA);
    }

    public void geraNovaVersao() {
        try {
            if (exercicioVersaoPPA == null) {
                FacesUtil.addOperacaoNaoPermitida("O campo exercício deve ser informado.");
            } else {
                pPAFacade.geraNovaVersao(selecionado, exercicioVersaoPPA);
                FacesUtil.addOperacaoRealizada("Nova versão do PPA gerada para o exercício de " + exercicioVersaoPPA.getAno() + ".");
                redireciona();
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public Boolean verificaAprovacao(String s) {
        if (selecionado.getAprovacao() != null) {
            if (existemVersoes(selecionado, selecionado.getPlanejamentoEstrategico())) {
                return false;
            } else {
                if (s.equals("versao")) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            if (existeAlgumAprovado()) {
                if (s.equals("versao")) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    public void aprovarPPA() {
        selecionado.setAprovacao(sistemaControlador.getDataOperacao());
        RequestContext.getCurrentInstance().execute("dialogDataAprovacao.show()");
    }

    public void cancelaAprovacaoPPA() {
        selecionado.setAprovacao(null);
        RequestContext.getCurrentInstance().execute("dialogDataAprovacao.hide()");
    }

    public void abrirDialogAprovarPPA() {
        selecionado.setAprovacao(sistemaControlador.getDataOperacao());
        RequestContext.getCurrentInstance().execute("dialogDataAprovacao.show()");
    }

    public Boolean existeAlgumAprovado() {
        for (PPA p : pPAFacade.listaPorPlanejamentoEstrategico(((PPA) selecionado).getPlanejamentoEstrategico())) {
            if (p.getAprovacao() != null) {
                return true;
            }
        }
        return false;
    }

    public Boolean existemVersoes(PPA p, PlanejamentoEstrategico pe) {
        List<PPA> listaOrigens = new ArrayList<PPA>();
        listaOrigens = pPAFacade.verificaVersoes(p, pe);
        if (listaOrigens.isEmpty()) {
            return false;
        }
        return true;
    }

    public Boolean validaAprovacao() {
        PPA p = ((PPA) selecionado);
        if (p.getAprovacao() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Data e Hora Aprovação deve ser informado.");
            return false;
        }
        if (p.getAtoLegal() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Lei deve ser informado.");
            return false;
        }
        return true;
    }

    @Override
    public void excluir() {
        try {
            pPAFacade.removerPPA(selecionado);
            redireciona();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Registro excluído com sucesso!", ""));
//            super.excluir();
        } catch (Exception ex) {
            if (ex instanceof EJBTransactionRolledbackException) {
                FacesUtil.addOperacaoNaoPermitida("Não foi possível excluir o registro, pois possui relacionamento com outras tabelas.");
            } else {
                FacesUtil.addOperacaoNaoRealizada("Erro: " + ex.getMessage());
            }
        }
    }

    public void aprovaPpa() {
        try {
            if (validaAprovacao()) {
                pPAFacade.aprovaPpa((PPA) selecionado);
                RequestContext.getCurrentInstance().update("Formulario");
                RequestContext.getCurrentInstance().execute("dialogDataAprovacao.hide()");
                FacesUtil.addOperacaoRealizada("PPA foi aprovado com sucesso.");
            }
        } catch (Exception e) {
            FacesUtil.addError("Erro ao gerar o PPA!", e.getMessage());
        }
    }

    public String geraVersaoPpa() {
        Long codigoMaior = 0L;
        Long codigoSub = 0L;
        for (PPA p : pPAFacade.listaPorPlanejamentoEstrategico(((PPA) selecionado).getPlanejamentoEstrategico())) {
            codigoSub = Long.parseLong(p.getVersao());
            if (codigoSub > codigoMaior) {
                codigoMaior = codigoSub;
            }
        }
        return String.valueOf(codigoMaior + 1);
    }

    public Date getDtContabilizacao() {
        return dtContabilizacao;
    }

    public void setDtContabilizacao(Date dtContabilizacao) {
        this.dtContabilizacao = dtContabilizacao;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public PPA getPPASelecionado() {
        return pPASelecionado;
    }

    public void setPPASelecionado(PPA pPASelecionado) {
        this.pPASelecionado = pPASelecionado;
    }

    public Exercicio getExercicioSelecionado() {
        return exercicioSelecionado;
    }

    public void setExercicioSelecionado(Exercicio exercicioSelecionado) {
        this.exercicioSelecionado = exercicioSelecionado;
    }

    public PPAControlador() {
        super(PPA.class);
    }

    public PPAFacade getFacade() {
        return pPAFacade;
    }

    public Exercicio getExercicioVersaoPPA() {
        return exercicioVersaoPPA;
    }

    public void setExercicioVersaoPPA(Exercicio exercicioVersaoPPA) {
        this.exercicioVersaoPPA = exercicioVersaoPPA;
    }
}
