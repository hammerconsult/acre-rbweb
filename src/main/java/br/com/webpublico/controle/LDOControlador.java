/*
 * Codigo gerado automaticamente em Wed Apr 27 16:29:29 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LDOFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@ManagedBean(name = "lDOControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-ldo", pattern = "/ldo/novo/", viewId = "/faces/financeiro/ppa/ldo/edita.xhtml"),
    @URLMapping(id = "editar-ldo", pattern = "/ldo/editar/#{lDOControlador.id}/", viewId = "/faces/financeiro/ppa/ldo/edita.xhtml"),
    @URLMapping(id = "ver-ldo", pattern = "/ldo/ver/#{lDOControlador.id}/", viewId = "/faces/financeiro/ppa/ldo/visualizar.xhtml"),
    @URLMapping(id = "listar-ldo", pattern = "/ldo/listar/", viewId = "/faces/financeiro/ppa/ldo/lista.xhtml"),})

public class LDOControlador extends PrettyControlador<LDO> implements Serializable, CRUD {

    @EJB
    private LDOFacade lDOFacade;
    private ConverterGenerico converterProvisaoPPA;
    private ConverterGenerico converterExercicio;
    private ConverterAutoComplete converterPpa;
    private ConverterAutoComplete converterAtoLegal;
    private String exercicio;
    private Date dtContabilizacao;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Map<ProgramaPPA, List<ProvisaoPPALDO>> mapa;


    public LDOControlador() {
        super(LDO.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return lDOFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/ldo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-ldo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setPpa(null);
        selecionado.setProvisaoPPALDOs(new ArrayList<ProvisaoPPALDO>());
        exercicio = null;
        mapa = null;
    }

    @URLAction(mappingId = "ver-ldo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-ldo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        mapa = new HashMap<>();
        recuperarEditarVer();
    }

    public void recuperarEditarVer() {
        selecionado = lDOFacade.recuperar(super.getId());
        exercicio = selecionado.getExercicio().toString();
        recuperarProvisoesPPALDO();
    }

    public Boolean validaPPAContabilizacao() {
        Boolean controle = true;
        LDO ld = selecionado;
        if (ld.getPpa().getAprovacao() == null && ld.getPpa().getSomenteLeitura() == false) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro: PPA não aprovado!", "Favor aprovar o PPA antes de realizar a contabilização."));
            controle = false;
        } else {
            RequestContext.getCurrentInstance().execute("dialogCont.show()");
        }
        return controle;
    }

    public Boolean validaCamposContabilizacao() {
        Boolean controle = true;
        if (selecionado.getAtoLegal() == null) {
            FacesUtil.addCampoObrigatorio("O campo Lei deve ser informado para contabilizar o LDO");
            controle = false;
        } else {
            if (dtContabilizacao == null) {
                FacesUtil.addCampoObrigatorio("O campo Data deve ser informado para contabilizar o LDO");
                controle = false;
            }
            if (selecionado.getPpa().getAprovacao() == null) {
                FacesUtil.addOperacaoNaoPermitida("PPA não foi aprovado. Por favor, aprovar o PPA antes de realizar a contabilzação");
                controle = false;
            }
            if (dtContabilizacao != null && selecionado.getPpa().getAprovacao() != null) {
                if (dtContabilizacao.before(selecionado.getPpa().getAprovacao())) {
                    FacesUtil.addOperacaoNaoPermitida("A Data de Contabilização não pode ser menor que a Data da Aprovação do PPA");
                    controle = false;
                }
            }
        }
        return controle;
    }

    public void geraContabilizacaoLDO() throws ExcecaoNegocioGenerica {
        LDO ldo = selecionado;
        try {
            if (!validaCamposContabilizacao()) {
            } else {
                ldo.setAlocado(Boolean.TRUE);
                ldo.setAprovacao(dtContabilizacao);
                lDOFacade.salvar(ldo);
                FacesUtil.addOperacaoRealizada("A LDO foi contabilizada com sucesso");
                redireciona();
            }

        } catch (ExcecaoNegocioGenerica ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
        }
    }

    public Converter getConverterPpa() {
        if (converterPpa == null) {
            converterPpa = new ConverterAutoComplete(PPA.class, lDOFacade.getPpaFacade());
        }
        return converterPpa;
    }

    public List<PPA> completaPpa(String parte) {
        return lDOFacade.getPpaFacade().listaFiltrandoPPA(parte.toLowerCase().trim());
    }

    public ConverterGenerico getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, lDOFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<SelectItem> getAtoLegal() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (AtoLegal object : lDOFacade.getAtoLegalFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, lDOFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return lDOFacade.getAtoLegalFacade().buscarAtosLegaisPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Exercicio object : lDOFacade.getPpaFacade().listaPpaEx(selecionado.getPpa())) {
            toReturn.add(new SelectItem(object, object.getAno().toString()));
        }
        return toReturn;
    }

    public void recuperarProvisoesPPALDO() {
        mapa = new HashMap<>();
        if (selecionado.getExercicio() != null) {
            List<ProdutoPPA> produtos = lDOFacade.getProdutoPPAFacade().listaProdutoPPAPorExercicioEPPA(selecionado.getExercicio(), selecionado.getPpa());
            List<ProvisaoPPALDO> provisoes = Lists.newArrayList();
            for (ProdutoPPA produto : produtos) {
                ProvisaoPPALDO provisaoPPALDO = new ProvisaoPPALDO();
                for (ProvisaoPPALDO provisaoPPALDOSelecionado : selecionado.getProvisaoPPALDOs()) {
                    if (provisaoPPALDOSelecionado.getProdutoPPA().equals(produto)) {
                        provisaoPPALDO = provisaoPPALDOSelecionado;
                    }
                }
                if (provisaoPPALDO.getProdutoPPA() == null) {
                    provisaoPPALDO.setProdutoPPA(produto);
                    provisaoPPALDO.setLdo(selecionado);
                    provisaoPPALDO.setDataRegistro(new Date());
                    provisaoPPALDO.setTotalFisico(produto.getTotalFisico());
                    provisaoPPALDO.setTotalFinanceiroCapital(recuperarTotalFinanceiroCapital(produto));
                    provisaoPPALDO.setTotalFinanceiroCorrente(recuperarTotalFinanceiroCorrente(produto));
                }
                provisoes.add(provisaoPPALDO);
            }

            preencherMapaProvisaoPrograma(provisoes);
            if (isOperacaoNovo()) {
                for (ProvisaoPPALDO provisao : provisoes) {
                    adicionarProvisaoPPA(provisao);
                }
            }
        }
    }

    private BigDecimal recuperarTotalFinanceiroCapital(ProdutoPPA produto) {
        for (ProvisaoPPA provisaoPPA : produto.getProvisoes()) {
            if (provisaoPPA.getExercicio().equals(selecionado.getExercicio())) {
                return provisaoPPA.getMetaFinanceiraCapital();
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal recuperarTotalFinanceiroCorrente(ProdutoPPA produto) {
        for (ProvisaoPPA provisaoPPA : produto.getProvisoes()) {
            if (provisaoPPA.getExercicio().equals(selecionado.getExercicio())) {
                return provisaoPPA.getMetaFinanceiraCorrente();
            }
        }
        return BigDecimal.ZERO;
    }

    public List<ProgramaPPA> recuperaProgramas() {
        if (mapa != null) {
            return new ArrayList<>(mapa.keySet());
        }
        return new ArrayList<ProgramaPPA>();
    }

    public List<ProvisaoPPALDO> recuperarSubAcoesPPADoPrograma(ProgramaPPA programa) {
        if (mapa != null) {
            return mapa.get(programa);
        }
        return new ArrayList<ProvisaoPPALDO>();
    }

    public void exercicioDoPpa(SelectEvent event) {
        if (selecionado.getPpa() != null) {
            List<SelectItem> exercicios = getExercicios();
        } else {
            FacesContext.getCurrentInstance().addMessage(":Formulario:ppa", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção! Nenhum PPA selecionado.", "Por favor, selecione."));
        }
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            if (lDOFacade.existeOutraLdoNoMesmoExercicio(selecionado) == null) {
                super.salvar();
            } else {
                FacesUtil.addOperacaoNaoPermitida("Já existe uma LDO para o exercício de " + selecionado.getExercicio().getAno() + ".");
            }
        }
    }


    public void validaExercicio(FacesContext context, UIComponent component, Object value) {
        Exercicio ex = null;
        FacesMessage message = new FacesMessage();
        try {
            Integer ano = Integer.parseInt(value.toString());
            List<Exercicio> exer = lDOFacade.getPpaFacade().listaPpaEx(selecionado.getPpa());
            for (Exercicio e : exer) {
                if (ano.compareTo(e.getAno()) == 0) {
                    ex = e;
                    selecionado.setExercicio(ex);
                }
            }
        } catch (Exception e) {
        }
        if (ex == null && !value.toString().trim().isEmpty()) {
            selecionado.setExercicio(null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            message.setSummary("Operação não Permitida!");
            message.setDetail("Informe um Exercício válido para o PPA selecionado.");
            throw new ValidatorException(message);
        }

    }

    private void preencherMapaProvisaoPrograma(List<ProvisaoPPALDO> provisoes) {
        for (ProvisaoPPALDO produtoPPA : provisoes) {
            if (produtoPPA.getProdutoPPA() != null) {
                ProgramaPPA programa = produtoPPA.getProdutoPPA().getAcaoPrincipal().getPrograma();
                if (mapa.containsKey(programa)) {
                    List<ProvisaoPPALDO> get = mapa.get(programa);
                    if (get == null) {
                        get = Lists.newArrayList();
                    }
                    if (podeAdicionarProvisaoNoMapa(produtoPPA, programa)) {
                        get.add(produtoPPA);
                    }
                } else {
                    if (podeAdicionarProvisaoNoMapa(produtoPPA, programa)) {
                        List<ProvisaoPPALDO> ProdutoPPAs1 = new ArrayList<>();
                        ProdutoPPAs1.add(produtoPPA);
                        mapa.put(programa, ProdutoPPAs1);
                    }
                }
            }
        }
    }

    private Boolean podeAdicionarProvisaoNoMapa(ProvisaoPPALDO provisao, ProgramaPPA programa) {
        if (mapa.get(programa) == null) {
            return true;
        }
        for (ProvisaoPPALDO provisaoPPALDO : mapa.get(programa)) {
            if (provisaoPPALDO.getProdutoPPA().equals(provisao.getProdutoPPA())) {
                return false;
            }
        }
        return true;
    }

    public boolean podeAdicionarProvisao(ProvisaoPPALDO provisao) {
        if (selecionado.getProvisaoPPALDOs() != null) {
            for (ProvisaoPPALDO produtoPPA : selecionado.getProvisaoPPALDOs()) {
                if (produtoPPA.getProdutoPPA().equals(provisao.getProdutoPPA())) {
                    return false;
                }
            }
        }
        return true;
    }

    public void adicionarProvisaoPPA(ProvisaoPPALDO provisao) {
        if (podeAdicionarProvisao(provisao)) {
            selecionado.getProvisaoPPALDOs().add(provisao);
        }
    }

    public void removerProvisaoPPA(ProvisaoPPALDO provisao) {
        selecionado.getProvisaoPPALDOs().remove(provisao);
    }

    public void removerProvisaoPPADoPrograma(ProgramaPPA programa) {
        for (ProvisaoPPALDO ProdutoPPA : mapa.get(programa)) {
            removerProvisaoPPA(ProdutoPPA);
        }
    }

    public Boolean renderizaBotaoAdicionarTodos(ProgramaPPA programaPPA) {
        for (ProvisaoPPALDO provisao : recuperarSubAcoesPPADoPrograma(programaPPA)) {
            if (!selecionado.getProvisaoPPALDOs().contains(provisao)) {
                return true;
            }
        }
        return false;
    }

    public void adiciarProvisaoPPADoPrograma(ProgramaPPA programa) {
        for (ProvisaoPPALDO ProdutoPPA : mapa.get(programa)) {
            adicionarProvisaoPPA(ProdutoPPA);
        }
    }

    public Map<ProgramaPPA, List<ProvisaoPPALDO>> getMapa() {
        return mapa;
    }

    public void setMapa(Map<ProgramaPPA, List<ProvisaoPPALDO>> mapa) {
        this.mapa = mapa;
    }

    public Date getDtContabilizacao() {
        return dtContabilizacao;
    }

    public void setDtContabilizacao(Date dtContabilizacao) {
        this.dtContabilizacao = dtContabilizacao;
    }

    public List<ProvisaoPPALDO> getProvisaoPPALDOs() {
        return selecionado.getProvisaoPPALDOs();
    }

    public LDOFacade getFacade() {
        return lDOFacade;
    }

    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
