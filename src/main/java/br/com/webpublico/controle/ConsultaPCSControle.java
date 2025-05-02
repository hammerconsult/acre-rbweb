/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

/**
 * @author mga
 */

import br.com.webpublico.entidades.CategoriaPCS;
import br.com.webpublico.entidades.EnquadramentoPCS;
import br.com.webpublico.entidades.PlanoCargosSalarios;
import br.com.webpublico.entidades.ProgressaoPCS;
import br.com.webpublico.entidadesauxiliares.FiltroPCS;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean(name = "consultaPCSControle")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConsultaPcs", pattern = "/consulta-pccr/novo/", viewId = "/faces/rh/administracaodepagamento/consultapcs/lista.xhtml")
})
public class ConsultaPCSControle extends SuperControladorCRUD implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaPCSControle.class);

    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    private ConverterAutoComplete converterCategoriaPCS;
    private ConverterGenerico converterCategoriaPCSg;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    private PlanoCargosSalarios planoCargosSalarios;
    private ConverterGenerico converterPlanoCargosSalarios;
    private List<PlanoCargosSalarios> listaPlanoCargosSalarios;
    private List<EnquadramentoPCS> itens;
    private List<ProgressaoPCS> progressaoPCSs;
    private List<CategoriaPCS> categoriaPCSs;
    private Date dataVigencia;
    private MoneyConverter moneyConverter;
    private EnquadramentoPCS enquadramentoPCSSelecionado;
    private EnquadramentoPCS itemEnquadramentoPCS;
    private List<CategoriaPCS> listaCategoriaPCS;
    private CategoriaPCS selecionadoCategoriaPCS;
    private CategoriaPCS selecionadoCategoriaPCSFilha;
    private List<ProgressaoPCS> listaProgressaoPCS;
    private ProgressaoPCS selecionadoProgressaoPCS;
    private Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> valores;
    private BigDecimal valorReajuste;
    private Date inicioVigenciaReajuste;
    private Date finalVigenciaReajuste;
    private PercentualConverter percentualConverter;
    //    private CategoriaPCS categoriaPCS;
    private ConverterGenerico converterProgressao;
    private List<FiltroPCS> listaFiltroPCSs;

    @Override
    public String salvar() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        String pathReal = servletContext.getContextPath();

        try {
            for (CategoriaPCS c : valores.keySet()) {
                for (ProgressaoPCS p : valores.get(c).keySet()) {
                    EnquadramentoPCS ePCS = valores.get(c).get(p);

                    if (ePCS.getVencimentoBase().compareTo(ePCS.getVencimentoBaseAntigo()) != 0) {
                        ePCS.setInicioVigencia(c.getPlanoCargosSalarios().getInicioVigencia());
                        ePCS.setFinalVigencia(c.getPlanoCargosSalarios().getFinalVigencia());
                        this.getFacede().salvar(ePCS);
                    }
                }
            }
            FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com Sucesso!", ""));
            novo();
            novoReajuste();
            return "lista.xhtml";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage().toString()));
            return "edita.xhtml";
        }
    }

    @URLAction(mappingId = "novaConsultaPcs", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            setAtributosReajusteNull();

            selecionado = new EnquadramentoPCS();
            selecionadoCategoriaPCS = null;
            //if (dataVigencia == null) {
            dataVigencia = SistemaFacade.getDataCorrente();
            //}

            //if (planoCargosSalarios == null) {
            planoCargosSalarios = new PlanoCargosSalarios();
            selecionadoCategoriaPCS = null;
            selecionadoCategoriaPCSFilha = null;
            selecionadoProgressaoPCS = null;
            listaFiltroPCSs = new ArrayList<>();
            //}

            //os metodos buscam percorrem as arvores dos objetos
            listaCategoriaPCS = buscaCategoriaObjeto();
            listaProgressaoPCS = buscaProgressoesObjeto();

            itens = new ArrayList<EnquadramentoPCS>();
            //itens = enquadramentoPCSFacade.listaEnquadramentosVigentes(dataVigencia);

            valores = inicializaValores(listaCategoriaPCS, listaProgressaoPCS);
        } catch (Exception ex) {
            logger.error("Erro ao instanciar nova entidade", ex);
        }
    }

    public void novoReajuste() {
        try {
            setAtributosReajusteNull();

            selecionado = new EnquadramentoPCS();

            //if (dataVigencia == null) {
            dataVigencia = SistemaFacade.getDataCorrente();
            //}

            //if (planoCargosSalarios == null) {
            planoCargosSalarios = new PlanoCargosSalarios();
            //}

            //os metodos buscam percorrem as arvores dos objetos
            listaCategoriaPCS = buscaCategoriaObjeto();
            listaProgressaoPCS = buscaProgressoesObjeto();

            itens = new ArrayList<EnquadramentoPCS>();
            //itens = enquadramentoPCSFacade.listaEnquadramentosVigentes(dataVigencia);

            valores = inicializaValores(listaCategoriaPCS, listaProgressaoPCS);
        } catch (Exception ex) {
            logger.error("Erro ao instanciar nova entidade", ex);
        }
    }

    public List<EnquadramentoPCS> getItens() {
        return itens;
    }

    public void setItens(List<EnquadramentoPCS> enquadramentoPCSs) {
        this.itens = enquadramentoPCSs;
    }

    public List<ProgressaoPCS> getProgressaoPCSs() {
        return progressaoPCSs;
    }

    public void setProgressaoPCSs(List<ProgressaoPCS> progressaoPCSs) {
        this.progressaoPCSs = progressaoPCSs;
    }

    public PlanoCargosSalarios getPlanoCargosSalarios() {
        return planoCargosSalarios;
    }

    public void setPlanoCargosSalarios(PlanoCargosSalarios planoCargosSalarios) {
        this.planoCargosSalarios = planoCargosSalarios;
    }

    public ConsultaPCSControle() {
        metadata = new EntidadeMetaData(EnquadramentoPCS.class);
    }

    public EnquadramentoPCSFacade getFacade() {
        return enquadramentoPCSFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return enquadramentoPCSFacade;
    }

    public List<SelectItem> getPlanos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ProgressaoPCS object : progressaoPCSFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterPlano() {
        if (converterPlanoCargosSalarios == null) {
            converterPlanoCargosSalarios = new ConverterGenerico(PlanoCargosSalarios.class, planoCargosSalariosFacade);
        }
        return converterPlanoCargosSalarios;
    }

    public List<SelectItem> getProgressaoPCS() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (CategoriaPCS object : categoriaPCSFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void addItens() {
        itens = new ArrayList<EnquadramentoPCS>();
        for (ProgressaoPCS p : progressaoPCSs) {
            EnquadramentoPCS epcs = enquadramentoPCSFacade.recuperaValor(((EnquadramentoPCS) selecionado).getCategoriaPCS(), p, ((EnquadramentoPCS) selecionado).getInicioVigencia());
            if (epcs == null) {
                epcs = new EnquadramentoPCS();
                epcs.setProgressaoPCS(p);
                epcs.setCategoriaPCS(((EnquadramentoPCS) selecionado).getCategoriaPCS());
                epcs.setVencimentoBase(BigDecimal.ZERO);
            }
            itens.add(epcs);
        }
    }

    public List<SelectItem> getPc() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PlanoCargosSalarios object : planoCargosSalariosFacade.listaFiltrandoVigencia(dataVigencia)) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterP() {
        if (converterPlanoCargosSalarios == null) {
            converterPlanoCargosSalarios = new ConverterGenerico(PlanoCargosSalarios.class, planoCargosSalariosFacade);
        }
        return converterPlanoCargosSalarios;
    }

    public List<SelectItem> getProgressao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (ProgressaoPCS object : progressaoPCSFacade.lista()) {

            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategorias() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (planoCargosSalarios.getId() != null) {
            for (CategoriaPCS object : categoriaPCSFacade.getFolhas(planoCargosSalarios)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public CategoriaPCS getSelecionadoCategoriaPCSFilha() {
        return selecionadoCategoriaPCSFilha;
    }

    public void setSelecionadoCategoriaPCSFilha(CategoriaPCS selecionadoCategoriaPCSFilha) {
        this.selecionadoCategoriaPCSFilha = selecionadoCategoriaPCSFilha;
    }

    public List<SelectItem> getCategoriasSelect() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (planoCargosSalarios.getId() != null) {
            for (CategoriaPCS object : categoriaPCSFacade.getRaizPorPlano(planoCargosSalarios)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getCategoriasFilhas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (planoCargosSalarios.getId() != null && selecionadoCategoriaPCS != null) {
            for (CategoriaPCS object : categoriaPCSFacade.getFilhosDe(selecionadoCategoriaPCS)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getProgressoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (planoCargosSalarios.getId() != null && selecionadoCategoriaPCSFilha != null) {
            for (ProgressaoPCS object : progressaoPCSFacade.getRaizPorPlanoECategoria(planoCargosSalarios, selecionadoCategoriaPCSFilha)) {
//                if(progressaoPCSFacade.verificaProgressoesVigente(object)){
                toReturn.add(new SelectItem(object, object.getDescricao()));
//                }
            }
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterCategoriaPCS() {
        if (converterCategoriaPCS == null) {
            converterCategoriaPCS = new ConverterAutoComplete(CategoriaPCS.class, categoriaPCSFacade);
        }
        return converterCategoriaPCS;
    }

    public ConverterGenerico getConverterCategoriaPCSg() {
        if (converterCategoriaPCSg == null) {
            converterCategoriaPCSg = new ConverterGenerico(CategoriaPCS.class, categoriaPCSFacade);
        }
        return converterCategoriaPCSg;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    //    public List<CategoriaPCS> completaCategoriaPCS(String parte) {
//        long id = -1l;
//        if (planoCargosSalarios.getId() != null) {
//            return categoriaPCSFacade.listaFiltrandoFk("planoCargosSalarios", planoCargosSalarios.getId(), parte.trim(), "descricao");
//        } else {
//            return categoriaPCSFacade.listaFiltrandoFk("planoCargosSalarios", id, parte.trim(), "descricao");
//        }
//    }
    public List<ProgressaoPCS> buscaProgressoesObjeto() {
        progressaoPCSs = new ArrayList<ProgressaoPCS>();
        ProgressaoPCS prog = new ProgressaoPCS();

        if (selecionadoProgressaoPCS != null) {
//        for (ProgressaoPCS o : progressaoPCSFacade.listaFiltrandoPCSVigente(planoCargosSalarios, dataVigencia)) {
            String nome = "";
            //prog = o;
            //metodo recuperar, faz com que a lista de filhos seja instanciada
            prog = progressaoPCSFacade.recuperar(selecionadoProgressaoPCS.getId());
            nome = prog.getDescricao();
            //a verificação do equals é para quando acontece de um superior ser o proprio objeto.
            //ai cai em loop infinito
//            if (prog.getFilhos().isEmpty()) {
//                while ((prog.getSuperior() != null) && (!prog.equals(prog.getSuperior()))) {
//                    prog = prog.getSuperior();
//                    nome = prog.getDescricao() + "/" + nome;
//                    selecionadoProgressaoPCS.setDescricao(nome);
//                }
//                progressaoPCSs.add(selecionadoProgressaoPCS);
//            }
            for (ProgressaoPCS pro : prog.getFilhos()) {
                progressaoPCSs.add(pro);
            }
//        }
        }
        Collections.sort(progressaoPCSs, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {

                ProgressaoPCS p1 = (ProgressaoPCS) o1;
                ProgressaoPCS p2 = (ProgressaoPCS) o2;
                if (p1.getDescricao() == null) {
                    p1.setDescricao("");
                }
                if (p2.getDescricao() == null) {
                    p2.setDescricao("");
                }
                return p1.getDescricao().compareTo(p2.getDescricao());
            }
        });

        return progressaoPCSs;
    }

    public EnquadramentoPCS getEnquadramentoPCSSelecionado() {
        return enquadramentoPCSSelecionado;
    }

    public void setEnquadramentoPCSSelecionado(EnquadramentoPCS enquadramentoPCSSelecionado) {
        this.enquadramentoPCSSelecionado = enquadramentoPCSSelecionado;
    }

    public EnquadramentoPCS recuperarItemDois(CategoriaPCS vp1, ProgressaoPCS vp2) {
        for (EnquadramentoPCS ePCS : itens) {
            if ((ePCS.getCategoriaPCS() == vp1) && (ePCS.getProgressaoPCS() == vp2)) {
                return ePCS;
            }
        }

        return new EnquadramentoPCS();
    }

    public void setaItensEnquadramentoPCS(EnquadramentoPCS item) {
        itemEnquadramentoPCS = item;
        int x = itens.indexOf(itemEnquadramentoPCS);
        itens.set(x, itemEnquadramentoPCS);
    }

    public List<CategoriaPCS> getListaCategoriaPCS() {
        return listaCategoriaPCS;
    }

    public void setListaCategoriaPCS(List<CategoriaPCS> listaCategoriaPCS) {
        this.listaCategoriaPCS = listaCategoriaPCS;
    }

    public List<ProgressaoPCS> getListaProgressaoPCS() {
        return listaProgressaoPCS;
    }

    public void setListaProgressaoPCS(List<ProgressaoPCS> listaProgressaoPCS) {
        this.listaProgressaoPCS = listaProgressaoPCS;
    }

    public CategoriaPCS getSelecionadoCategoriaPCS() {
        return selecionadoCategoriaPCS;
    }

    public void setSelecionadoCategoriaPCS(CategoriaPCS selecionadoCategoriaPCS) {
        this.selecionadoCategoriaPCS = selecionadoCategoriaPCS;
    }

    public ProgressaoPCS getSelecionadoProgressaoPCS() {
        return selecionadoProgressaoPCS;
    }

    public void setSelecionadoProgressaoPCS(ProgressaoPCS selecionadoProgressaoPCS) {
        this.selecionadoProgressaoPCS = selecionadoProgressaoPCS;
    }

    public ConverterGenerico getConverterProgressao() {
        if (converterProgressao == null) {
            converterProgressao = new ConverterGenerico(ProgressaoPCS.class, progressaoPCSFacade);
        }
        return converterProgressao;
    }

    public Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> getValores() {
        return valores;
    }

    public void setValores(Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> valores) {
        this.valores = valores;
    }

    private Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> inicializaValores(
        List<CategoriaPCS> pCategorias, List<ProgressaoPCS> pProgressoes) {
        Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> retorno = new HashMap<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>>();

        boolean encontrouEnquadramento = false;

        for (CategoriaPCS c : pCategorias) {
            Map<ProgressaoPCS, EnquadramentoPCS> v = new HashMap<ProgressaoPCS, EnquadramentoPCS>();
            for (ProgressaoPCS p : pProgressoes) {

                for (EnquadramentoPCS ePCS : itens) {
                    if ((ePCS.getCategoriaPCS().equals(c)) && (ePCS.getProgressaoPCS().equals(p))) {
                        v.put(p, ePCS);
                        encontrouEnquadramento = true;
                    }
                }

                if (!encontrouEnquadramento) {
                    v.put(p, new EnquadramentoPCS(c.getPlanoCargosSalarios().getInicioVigencia(), c.getPlanoCargosSalarios().getFinalVigencia(), p, c, BigDecimal.ZERO));
                }
                encontrouEnquadramento = false;
            }
            retorno.put(c, v);
        }
        return retorno;
    }

    public void limpaCategoria() {
        selecionadoCategoriaPCSFilha = null;
    }

    public List<CategoriaPCS> buscaCategoriaObjeto() {
        categoriaPCSs = new ArrayList<CategoriaPCS>();
        CategoriaPCS cat = new CategoriaPCS();
        if (selecionadoCategoriaPCS != null && selecionadoCategoriaPCSFilha != null) {
//            for (CategoriaPCS o : categoriaPCSFacade.listaFiltrandoPCSVigente(planoCargosSalarios, dataVigencia)) {
//                String nome = "";
//                cat = categoriaPCSFacade.recuperar(o.getId());
//                nome = cat.getDescricao();
//                //a verificação do equals é para quando acontece de um superior ser o proprio objeto.
//                //ai cai em loop infinito
//
//                if (cat.getFilhos().isEmpty()) {
//                    while ((cat.getSuperior() != null) && (!cat.equals(cat.getSuperior()))) {
//                        cat = cat.getSuperior();
//                        nome = cat.getDescricao() + "/" + nome;
//                        o.setDescricao(nome);
//                    }
//                    categoriaPCSs.add(o);
//                }
//            }
//        } else {
            cat = categoriaPCSFacade.recuperar(selecionadoCategoriaPCSFilha.getId());
            CategoriaPCS novo = null;

            if (cat.getFilhos().isEmpty()) {
                categoriaPCSs.add(cat);
            } else {
                for (CategoriaPCS o : cat.getFilhos()) {
//                    novo = categoriaPCSFacade.recuperar(o.getId());
//                    String nome = selecionadoCategoriaPCS.getDescricao();
//                    while ((novo.getSuperior() != null) && (!novo.equals(novo.getSuperior()))) {
//                        novo = novo.getSuperior();
//                        nome = novo.getDescricao() + "/" + nome;
//                        o.setDescricao(nome);
//                    }
                    categoriaPCSs.add(o);
                }
            }

        } else if (selecionadoCategoriaPCS != null && selecionadoCategoriaPCSFilha == null) {
            cat = categoriaPCSFacade.recuperar(selecionadoCategoriaPCS.getId());
            CategoriaPCS novo = null;

            if (cat.getFilhos().isEmpty()) {
                categoriaPCSs.add(cat);
            } else {
                for (CategoriaPCS o : cat.getFilhos()) {
//                    novo = categoriaPCSFacade.recuperar(o.getId());
//                    String nome = selecionadoCategoriaPCS.getDescricao();
//                    while ((novo.getSuperior() != null) && (!novo.equals(novo.getSuperior()))) {
//                        novo = novo.getSuperior();
//                        nome = novo.getDescricao() + "/" + nome;
//                        o.setDescricao(nome);
//                    }
                    categoriaPCSs.add(o);
                }
            }
        }

        Collections.sort(categoriaPCSs, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                CategoriaPCS p1 = (CategoriaPCS) o1;
                CategoriaPCS p2 = (CategoriaPCS) o2;

                return p1.getDescricao().compareTo(p2.getDescricao());
            }
        });

        return categoriaPCSs;
    }

    public void fixaCat(SelectEvent iten) {
        selecionadoCategoriaPCS = (CategoriaPCS) iten.getObject();
    }

    public Date getDataVigencia() {
        return dataVigencia;
    }

    public void setDataVigencia(Date dataVigencia) {
        this.dataVigencia = dataVigencia;
    }

    public BigDecimal getValorReajuste() {
        return valorReajuste;
    }

    public void setValorReajuste(BigDecimal valorReajuste) {
        this.valorReajuste = valorReajuste;
    }

    public Date getFinalVigenciaReajuste() {
        return finalVigenciaReajuste;
    }

    public void setFinalVigenciaReajuste(Date finalVigenciaReajuste) {
        this.finalVigenciaReajuste = finalVigenciaReajuste;
    }

    public Date getInicioVigenciaReajuste() {
        return inicioVigenciaReajuste;
    }

    public void setInicioVigenciaReajuste(Date inicioVigenciaReajuste) {
        this.inicioVigenciaReajuste = inicioVigenciaReajuste;
    }

    public EnquadramentoPCS reajustarSalario(EnquadramentoPCS enquadramentoPCS) {
        BigDecimal valorVencimentoBase;
        EnquadramentoPCS enquadramentoReajuste = new EnquadramentoPCS();

        valorVencimentoBase = valorReajuste.divide(BigDecimal.valueOf(100));
        valorVencimentoBase = valorVencimentoBase.multiply(enquadramentoPCS.getVencimentoBase());
        valorVencimentoBase = valorVencimentoBase.add(enquadramentoPCS.getVencimentoBase());

        enquadramentoReajuste.setVencimentoBase(valorVencimentoBase);
        enquadramentoReajuste.setCategoriaPCS(enquadramentoPCS.getCategoriaPCS());
        enquadramentoReajuste.setProgressaoPCS(enquadramentoPCS.getProgressaoPCS());
        enquadramentoReajuste.setInicioVigencia(inicioVigenciaReajuste);
        enquadramentoReajuste.setFinalVigencia(finalVigenciaReajuste);

        return enquadramentoReajuste;
    }

    public PercentualConverter getPercentualConverter() {
        if (percentualConverter == null) {
            percentualConverter = new PercentualConverter();
        }
        return percentualConverter;
    }

    public boolean validaEnquadramentoPCSReajuste() {
        boolean valida = false;

        if (valorReajuste != null) {
            valida = true;

            if (valorReajuste.compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addMessageWarn("Formulario:msgs", "Atenção !", "O Valor do reajuste deve ser maior que 0 (Zero) !");
                valida = false;
            }

            if (inicioVigenciaReajuste == null) {
                FacesUtil.addMessageWarn("Formulario:msgs", "Atenção !", "A data de inicio da vigência do reajuste é obrigatório !");
                return false;
            }

            if (finalVigenciaReajuste != null) {
                if (finalVigenciaReajuste.before(inicioVigenciaReajuste)) {
                    FacesUtil.addMessageWarn("Formulario:msgs", "Atenção !", "A data final de vigência do reajuste deve ser superior a data inicial de vigência do reajuste !");
                    valida = false;
                }
            }
        }

        return valida;
    }

    private void setAtributosReajusteNull() {
        valorReajuste = null;
        inicioVigenciaReajuste = null;
        finalVigenciaReajuste = null;
    }

    public void limpaCampos() {
        valorReajuste = null;
        inicioVigenciaReajuste = null;
        finalVigenciaReajuste = null;
        planoCargosSalarios = new PlanoCargosSalarios();
        selecionadoCategoriaPCS = new CategoriaPCS();


        if (inicioVigenciaReajuste == null) {
            inicioVigenciaReajuste = new Date();
        }
    }

    @Override
    public void cancelar() {
        setAtributosReajusteNull();
        super.cancelar();
    }

    public String getInicioVigenciaEnquadramentos() {
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        if (valores != null) {
            for (CategoriaPCS c : valores.keySet()) {
                for (ProgressaoPCS p : valores.get(c).keySet()) {
                    EnquadramentoPCS ePCS = valores.get(c).get(p);

                    return sf.format(ePCS.getInicioVigencia());
                }
            }
        }
        return null;
    }

    public String getFinalVigenciaEnquadramentos() {
        try {
            if (valores != null) {
                SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
                for (CategoriaPCS c : valores.keySet()) {
                    for (ProgressaoPCS p : valores.get(c).keySet()) {
                        EnquadramentoPCS ePCS = valores.get(c).get(p);
                        return sf.format(ePCS.getFinalVigencia());
                    }
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public String salvarReajuste() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        String pathReal = servletContext.getContextPath();

        try {
            boolean valida = validaEnquadramentoPCSReajuste();

            if (!valida) {
                return "";
            }

            for (CategoriaPCS c : valores.keySet()) {
                for (ProgressaoPCS p : valores.get(c).keySet()) {
                    EnquadramentoPCS ePCS = valores.get(c).get(p);

                    this.getFacede().salvar(reajustarSalario(ePCS));
                    Calendar calendario = Calendar.getInstance();
                    calendario.setTime(inicioVigenciaReajuste);
                    calendario.set(Calendar.DAY_OF_YEAR, calendario.get(Calendar.DAY_OF_YEAR) - 1);
                    ePCS.setFinalVigencia(calendario.getTime());
                    this.getFacede().salvar(ePCS);
                }
            }

            FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com Sucesso!", ""));
            listaReajustado();
            return "lista.xhtml";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage().toString()));
            return "edita.xhtml";
        }
    }

    public void listaReajustado() {
        try {
            dataVigencia = inicioVigenciaReajuste;

            setAtributosReajusteNull();

            selecionado = new EnquadramentoPCS();


            if (planoCargosSalarios == null) {
                planoCargosSalarios = new PlanoCargosSalarios();
            }

            //os metodos buscam percorrem as arvores dos objetos
            listaCategoriaPCS = buscaCategoriaObjeto();
            listaProgressaoPCS = buscaProgressoesObjeto();

            itens = new ArrayList<EnquadramentoPCS>();
            itens = enquadramentoPCSFacade.listaEnquadramentosVigentes(dataVigencia);

            valores = inicializaValores(listaCategoriaPCS, listaProgressaoPCS);
        } catch (Exception ex) {
            logger.error("Erro ao instanciar nova entidade", ex);
        }
    }

    public void filtrarEnquadramentos() {
        try {
            setAtributosReajusteNull();

            selecionado = new EnquadramentoPCS();
//            selecionadoCategoriaPCS = null;
            if (dataVigencia == null) {
                dataVigencia = SistemaFacade.getDataCorrente();
            }

            if (planoCargosSalarios == null) {
                planoCargosSalarios = new PlanoCargosSalarios();
            }

            //os metodos buscam percorrem as arvores dos objetos
            listaCategoriaPCS = buscaCategoriaObjeto();
            listaProgressaoPCS = buscaProgressoesObjeto();

            itens = new ArrayList<EnquadramentoPCS>();
            itens = enquadramentoPCSFacade.listaEnquadramentosVigentes(dataVigencia);
            //System.out.println("tamanho: " + itens.size());

            for (CategoriaPCS cat : categoriaPCSs) {
                for (ProgressaoPCS prog : listaProgressaoPCS) {
                    for (EnquadramentoPCS eq : itens) {
                        if ((eq.getCategoriaPCS().equals(cat) && eq.getProgressaoPCS().equals(prog))) {
                            //System.out.println("Opa!!: " + eq);
                        }
                    }
                }
            }

            valores = inicializaValores(listaCategoriaPCS, listaProgressaoPCS);
        } catch (Exception ex) {
            logger.error("Erro ao instanciar nova entidade", ex);
        }
    }

    public void filtrarReajusteEnquadramentos() {
        try {
            setAtributosReajusteNull();

            selecionado = new EnquadramentoPCS();

            if (dataVigencia == null) {
                dataVigencia = SistemaFacade.getDataCorrente();
            }

            if (planoCargosSalarios == null) {
                planoCargosSalarios = new PlanoCargosSalarios();
            }

            //os metodos buscam percorrem as arvores dos objetos
            listaCategoriaPCS = buscaCategoriaObjeto();
            listaProgressaoPCS = buscaProgressoesObjeto();

            itens = new ArrayList<EnquadramentoPCS>();
            itens = enquadramentoPCSFacade.listaEnquadramentosVigentes(dataVigencia);

            valores = inicializaValores(listaCategoriaPCS, listaProgressaoPCS);
        } catch (Exception ex) {
            logger.error("Erro ao instanciar nova entidade", ex);
        }
    }

    public List<CategoriaPCS> completaCategoriaPCS(String parte) {
        planoCargosSalarios = planoCargosSalariosFacade.recuperar(planoCargosSalarios.getId());
        return categoriaPCSFacade.listaFiltrandoDescricaoComPCSVigente(planoCargosSalarios, parte.trim(), dataVigencia);
    }

    public void filtrar() {
        if (podeExecutarAConsulta()) {
            listaFiltroPCSs = enquadramentoPCSFacade.filtraEnquadramento(selecionadoProgressaoPCS, selecionadoCategoriaPCSFilha);
        }
    }

    private boolean podeExecutarAConsulta() {
        if (selecionadoCategoriaPCSFilha == null) {
            FacesUtil.addCampoObrigatorio("Informe o campo categoria.");
            return false;
        }
        if (selecionadoProgressaoPCS == null) {
            FacesUtil.addCampoObrigatorio("Informe o campo progressão.");
            return false;
        }
        return true;
    }

    public List<FiltroPCS> getListaFiltroPCSs() {
        return listaFiltroPCSs;
    }

    public void setListaFiltroPCSs(List<FiltroPCS> listaFiltroPCSs) {
        this.listaFiltroPCSs = listaFiltroPCSs;
    }
}
