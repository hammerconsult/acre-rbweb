/*
 * Codigo gerado automaticamente em Sat Jul 02 11:00:55 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.EstruturaPCS;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLActions;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean(name = "enquadramentoPCSReajusteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoReajusteEnquadramentoPCSReajuste", pattern = "/reajuste-enquadramento-pccr/novo/", viewId = "/faces/rh/administracaodepagamento/reajusteenquadramentopcs/edita.xhtml"),
    @URLMapping(id = "listarReajusteEnquadramentoPCSReajuste", pattern = "/reajuste-enquadramento-pccr/listar/", viewId = "/faces/rh/administracaodepagamento/reajusteenquadramentopcs/lista.xhtml"),
    @URLMapping(id = "visualizarReajusteEnquadramentoPCSReajuste", pattern = "/reajuste-enquadramento-pccr/ver/#{enquadramentoPCSReajusteControlador.id}/", viewId = "/faces/rh/administracaodepagamento/reajusteenquadramentopcs/visualizar.xhtml"),
    @URLMapping(id = "editarReajusteEnquadramentoPCSReajuste", pattern = "/reajuste-enquadramento-pccr/editar/#{enquadramentoPCSReajusteControlador.id}/", viewId = "/faces/rh/administracaodepagamento/reajusteenquadramentopcs/editarReajuste.xhtml")
})
public class EnquadramentoPCSReajusteControlador extends PrettyControlador<ReajustePCS> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(EnquadramentoPCSReajusteControlador.class);

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
    private MoneyConverter moneyConverter;
    private EnquadramentoPCS enquadramentoPCSSelecionado;
    private EnquadramentoPCS itemEnquadramentoPCS;
    private List<CategoriaPCS> listaCategoriaPCS;
    private CategoriaPCS selecionadoCategoriaPCS;
    private CategoriaPCS selecionadoCategoriaPCSFilha;
    private List<ProgressaoPCS> listaProgressaoPCS;
    private ProgressaoPCS selecionadoProgressaoPCS;
    private Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> valores;
    private Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> valoresRejustados;
    private BigDecimal valorReajuste;
    private Date inicioVigenciaReajuste;
    private Date finalVigenciaReajuste;
    private PercentualConverter percentualConverter;
    private ConverterGenerico converterProgressao;
    private List<EstruturaPCS> estruturaPCSList;
    private Date dataReferencia;
    private String conteudo;
    private TipoEdicaoReajuste tipoEdicaoReajuste;
    @EJB
    private ReajustePCSFacade reajustePCSFacade;

    @Override
    public void salvar() {
        try {

            DateTime dateTime = getDataInicioVigenciaNovoEnquadramentopcs();
            selecionado.setDataReajuste(dataReferencia);
            dataReferencia = dateTime.minusDays(1).toDate();
            selecionado.setPercentual(valorReajuste);
            selecionado.setPlanoCargosSalarios(planoCargosSalarios);
            selecionado.setCategoriaPCS(selecionadoCategoriaPCS);
            selecionado.setProgressaoPCS(selecionadoProgressaoPCS);

            if (estruturaPCSList != null && !estruturaPCSList.isEmpty()) {
                for (EstruturaPCS estruturaPCS : estruturaPCSList) {
                    if (estruturaPCS.getFinalVigencia() != null) {
                        if (dateTime.toDate().before(estruturaPCS.getFinalVigencia()) || dateTime.toDate().equals(estruturaPCS.getFinalVigencia())) {
                            FacesUtil.addError("Atenção", "A data do reajuste não pode ser menor que a do ultimo pcs");
                            return;
                        }
                    }
                }

            }

            if (dateTime != null) {
                filtrarReajusteEnquadramentos();
                if (temLacunasEntreOsPCSs()) {
                    finalizarVigenciaAutomaticamente();
                }
            }


            if (valores != null && !valores.isEmpty() && valoresRejustados != null && !valoresRejustados.isEmpty()) {
                List<EnquadramentoPCS> salvos = new ArrayList<>();
                for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> keys : valores.entrySet()) {
                    for (Map.Entry<ProgressaoPCS, EnquadramentoPCS> values : keys.getValue().entrySet()) {
                        enquadramentoPCSFacade.salvarComRetorno(values.getValue());
                    }
                }
                for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> keys : valoresRejustados.entrySet()) {
                    for (Map.Entry<ProgressaoPCS, EnquadramentoPCS> values : keys.getValue().entrySet()) {
                        salvos.add(enquadramentoPCSFacade.salvarComRetorno(values.getValue()));
                    }
                }
                if (!salvos.isEmpty()) {
                    criarReajustes(salvos);
                    super.salvar();
                }

            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage()));
        }
    }

    @Override
    public void excluir() {
        try {
            List<EnquadramentoPCS> enquadramentos = Lists.newArrayList();
            for (ReajusteEnquadramentoPCS reajusteEnquadramentoPC : selecionado.getReajusteEnquadramentoPCS()) {
                enquadramentos.add(reajusteEnquadramentoPC.getEnquadramentoPCS());
            }
            reajustePCSFacade.remover(selecionado);
            enquadramentoPCSFacade.reverterEnquadramentosPcs(enquadramentos);
            redireciona();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoExcluir()));
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao tentar excluir registro. ", e);
            descobrirETratarException(e);
        }
    }

    public void selecionar(ActionEvent evento) {
        operacao = Operacoes.EDITAR;
        ReajustePCS a = (ReajustePCS) evento.getComponent().getAttributes().get("objeto");
        selecionado = reajustePCSFacade.recuperar(a.getId());
    }

    @URLAction(mappingId = "visualizarReajusteEnquadramentoPCSReajuste", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionado = reajustePCSFacade.recuperar(getId());
    }

    @URLAction(mappingId = "editarReajusteEnquadramentoPCSReajuste", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionado = reajustePCSFacade.recuperar(getId());
        valores = new LinkedHashMap<>();
        Set<ProgressaoPCS> progressoes = Sets.newLinkedHashSet();
        Set<CategoriaPCS> categorias = Sets.newLinkedHashSet();
        for (ReajusteEnquadramentoPCS pcs : selecionado.getReajusteEnquadramentoPCS()) {
            progressoes.add(pcs.getEnquadramentoPCS().getProgressaoPCS());
            categorias.add(pcs.getEnquadramentoPCS().getCategoriaPCS());
            if (valores.containsKey(pcs.getEnquadramentoPCS().getCategoriaPCS())) {
                valores.get(pcs.getEnquadramentoPCS().getCategoriaPCS()).put(pcs.getEnquadramentoPCS().getProgressaoPCS(), pcs.getEnquadramentoPCS());
            } else {
                Map<ProgressaoPCS, EnquadramentoPCS> map = new LinkedHashMap<ProgressaoPCS, EnquadramentoPCS>();
                map.put(pcs.getEnquadramentoPCS().getProgressaoPCS(), pcs.getEnquadramentoPCS());
                valores.put(pcs.getEnquadramentoPCS().getCategoriaPCS(), map);
            }
        }
        listaProgressaoPCS = Lists.newLinkedList(progressoes);
        listaCategoriaPCS = Lists.newLinkedList(categorias);

    }

    private void criarReajustes(List<EnquadramentoPCS> salvos) {
        for (EnquadramentoPCS salvo : salvos) {
            ReajusteEnquadramentoPCS itemReajuste = new ReajusteEnquadramentoPCS();
            itemReajuste.setEnquadramentoPCS(salvo);
            itemReajuste.setReajustePCS(selecionado);
            selecionado.getReajusteEnquadramentoPCS().add(itemReajuste);
        }
    }

    private boolean temLacunasEntreOsPCSs() {
        if (valores != null && !valores.isEmpty()) {
            for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> keys : valores.entrySet()) {
                for (Map.Entry<ProgressaoPCS, EnquadramentoPCS> values : keys.getValue().entrySet()) {
                    if (values.getValue().getId() != null && values.getValue().getFinalVigencia() == null) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    private void finalizarVigenciaAutomaticamente() {
        if (valores != null && !valores.isEmpty() && valoresRejustados != null && !valores.isEmpty()) {
            DateTime inicioNovo = getDataInicioVigenciaNovoEnquadramentopcs();
            if (inicioNovo != null) {
                for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> keys : valores.entrySet()) {
                    for (Map.Entry<ProgressaoPCS, EnquadramentoPCS> values : keys.getValue().entrySet()) {
                        values.getValue().setFinalVigencia(inicioNovo.minusDays(1).toDate());
                    }
                }
            }
        }
    }

    public DateTime getDataInicioVigenciaNovoEnquadramentopcs() {
        if (valoresRejustados != null && !valoresRejustados.isEmpty()) {
            for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> keys : valoresRejustados.entrySet()) {
                for (Map.Entry<ProgressaoPCS, EnquadramentoPCS> values : keys.getValue().entrySet()) {
                    return new DateTime(values.getValue().getInicioVigencia());
                }
            }
        }
        return null;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/reajuste-enquadramento-pccr/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLActions(actions = {
        @URLAction(mappingId = "novoReajusteEnquadramentoPCSReajuste", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    })
    @Override
    public void novo() {
        try {
            setAtributosReajusteNull();
            super.novo();
            selecionado = new ReajustePCS();
            selecionadoCategoriaPCS = null;
            dataReferencia = UtilRH.getDataOperacao();

            planoCargosSalarios = new PlanoCargosSalarios();
            selecionadoCategoriaPCS = null;
            selecionadoCategoriaPCSFilha = null;
            selecionadoProgressaoPCS = null;


            listaCategoriaPCS = buscaCategoriaObjeto();
            listaProgressaoPCS = buscaProgressoesObjeto();

            itens = new ArrayList<EnquadramentoPCS>();

            valores = new LinkedHashMap<>();
        } catch (Exception ex) {
            logger.error("Erro ao instanciar nova entidade", ex);
        }
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> getValoresRejustados() {
        return valoresRejustados;
    }

    public void setValoresRejustados(Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> valoresRejustados) {
        this.valoresRejustados = valoresRejustados;
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

    public EnquadramentoPCSReajusteControlador() {
        super(ReajustePCS.class);
    }

    public ReajustePCSFacade getFacade() {
        return reajustePCSFacade;
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

    public List<SelectItem> getPc() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PlanoCargosSalarios object : planoCargosSalariosFacade.listaFiltrandoVigencia(UtilRH.getDataOperacao())) {
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
        if (planoCargosSalarios != null && planoCargosSalarios.getId() != null) {
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
        if (planoCargosSalarios != null) {
            if (planoCargosSalarios.getId() != null) {
                for (CategoriaPCS object : categoriaPCSFacade.getRaizPorPlano(planoCargosSalarios)) {
                    toReturn.add(new SelectItem(object, object.getDescricao()));
                }
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
        if (planoCargosSalarios != null && selecionadoCategoriaPCS != null) {
            if (planoCargosSalarios.getId() != null) {
                for (ProgressaoPCS object : progressaoPCSFacade.buscaProgressoesSuperioresEnquadramentoPCSPorCategoriaSuperior(selecionadoCategoriaPCS, dataReferencia)) {
                    toReturn.add(new SelectItem(object, (object.getCodigo() != null ? object.getCodigo() + " - " : "") + "" + object.getDescricao()));
                }
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

    public List<ProgressaoPCS> buscaProgressoesObjeto() {
        progressaoPCSs = new ArrayList<ProgressaoPCS>();
        ProgressaoPCS prog = new ProgressaoPCS();

        if (selecionadoProgressaoPCS != null) {
            String nome = "";
            prog = progressaoPCSFacade.recuperar(selecionadoProgressaoPCS.getId());
            nome = prog.getDescricao();
            for (ProgressaoPCS pro : prog.getFilhos()) {
                progressaoPCSs.add(pro);
            }
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
        Date inivioVigencia = dataReferencia;
        Date finalVigencia = null;
        for (CategoriaPCS c : pCategorias) {
            Map<ProgressaoPCS, EnquadramentoPCS> v = new HashMap<ProgressaoPCS, EnquadramentoPCS>();
            for (ProgressaoPCS p : pProgressoes) {

                for (EnquadramentoPCS ePCS : itens) {
                    if ((ePCS.getCategoriaPCS().equals(c)) && (ePCS.getProgressaoPCS().equals(p))) {
                        inivioVigencia = ePCS.getInicioVigencia();
                        finalVigencia = ePCS.getFinalVigencia();
                        v.put(p, ePCS);
                        encontrouEnquadramento = true;
                    }
                }

                if (!encontrouEnquadramento) {
                    v.put(p, new EnquadramentoPCS(inivioVigencia, finalVigencia, p, c, BigDecimal.ZERO));
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

            cat = categoriaPCSFacade.recuperar(selecionadoCategoriaPCSFilha.getId());
            CategoriaPCS novo = null;

            if (cat.getFilhos().isEmpty()) {
                categoriaPCSs.add(cat);
            } else {
                for (CategoriaPCS o : cat.getFilhos()) {
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

    public boolean isVigenciaFechada() {
        if (valores != null && !valores.isEmpty()) {
            for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> keys : valores.entrySet()) {
                for (Map.Entry<ProgressaoPCS, EnquadramentoPCS> values : keys.getValue().entrySet()) {
                    if (values.getValue().getFinalVigencia() != null) return true;
                }
            }
        }
        return false;
    }

    public void aplicarReajuste() {
        try {
            validarReajuste();
            if (valorReajuste != null) {
                valoresRejustados = new LinkedHashMap<>();
                for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> keys : valores.entrySet()) {
                    valoresRejustados.put(keys.getKey(), reajustaValores(keys.getValue(), keys.getKey()));
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.addOperacaoNaoPermitida(ve.getMessage());
        }
    }

    public void aplicarReajusteEdicao() {
        try {
            validarReajuste();
            if (valorReajuste != null && valorReajuste != BigDecimal.ZERO) {
                valoresRejustados = new LinkedHashMap<>();
                for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> keys : valores.entrySet()) {
                    valoresRejustados.put(keys.getKey(), getMapaValorReajustado(keys.getValue(), keys.getKey()));
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.addOperacaoNaoPermitida(ve.getMessage());
        }
    }

    private Map<ProgressaoPCS, EnquadramentoPCS> getMapaValorReajustado(Map<ProgressaoPCS, EnquadramentoPCS> value, CategoriaPCS key) {
        Map<ProgressaoPCS, EnquadramentoPCS> valores = new LinkedHashMap<>();
        for (Map.Entry<ProgressaoPCS, EnquadramentoPCS> values : value.entrySet()) {
            EnquadramentoPCS pcs = values.getValue();
            BigDecimal valorAntigo = getValorParaEdicaoReajuste(pcs);
            pcs.setVencimentoBaseAntigo(valorAntigo);
            if (valorAntigo.compareTo(BigDecimal.ZERO) == 0) {
                pcs.setVencimentoBase(BigDecimal.ZERO);
                pcs.setPercentualReajuste(valorReajuste);
            } else {
                pcs.setVencimentoBase(valorAntigo.add(valorAntigo.multiply(valorReajuste).divide(new BigDecimal("100"))));
                pcs.setPercentualReajuste(valorReajuste);
            }
            valores.put(values.getKey(), pcs);
            valores.put(values.getKey(), pcs);
        }
        return valores;
    }

    private BigDecimal getValorParaEdicaoReajuste(EnquadramentoPCS pcs) {
        if (TipoEdicaoReajuste.BASE_VALORES_ANTERIORES.equals(tipoEdicaoReajuste)) {
            DateTime dateTime = new DateTime(pcs.getInicioVigencia());
            dateTime = dateTime.minusDays(1);
            EnquadramentoPCS pcsAnterior = enquadramentoPCSFacade.buscarEnquadramentoPorProgressaoCategoriaEVigencia(pcs.getProgressaoPCS(), pcs.getCategoriaPCS(), dateTime.toDate());
            if (pcsAnterior != null) {
                return pcsAnterior.getVencimentoBase();
            }
            return BigDecimal.ZERO;
        } else {
            return pcs.getVencimentoBase();
        }
    }

    private Map<ProgressaoPCS, EnquadramentoPCS> reajustaValores(Map<ProgressaoPCS, EnquadramentoPCS> value, CategoriaPCS key) {
        Map<ProgressaoPCS, EnquadramentoPCS> valores = new LinkedHashMap<>();
        for (Map.Entry<ProgressaoPCS, EnquadramentoPCS> values : value.entrySet()) {
            EnquadramentoPCS pcs = new EnquadramentoPCS(inicioVigenciaReajuste, finalVigenciaReajuste, values.getKey(), key, BigDecimal.ZERO);
            BigDecimal valorAntigo = values.getValue().getVencimentoBase();
            pcs.setVencimentoBaseAntigo(valorAntigo);
            if (valorAntigo.compareTo(BigDecimal.ZERO) == 0) {
                pcs.setVencimentoBase(BigDecimal.ZERO);
                pcs.setPercentualReajuste(valorReajuste);
            } else {
                if (valorReajuste.compareTo(BigDecimal.ZERO) <= 0) {
                    pcs.setVencimentoBase(valorAntigo);
                } else {
                    pcs.setVencimentoBase(valorAntigo.add(valorAntigo.multiply(valorReajuste).divide(new BigDecimal("100"))));
                }
                pcs.setPercentualReajuste(valorReajuste);
            }
            valores.put(values.getKey(), pcs);
        }
        return valores;  //To change body of created methods use File | Settings | File Templates.
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

    public void validarReajuste() {

        if (valorReajuste == null) {
            throw new ValidacaoException("Valor do reajuste não informado.");
        }

        if (valorReajuste.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("O Valor do reajuste não pode ser negativo.");
        }

        if (!isOperacaoEditar()) {

            if (inicioVigenciaReajuste == null) {
                throw new ValidacaoException("A data de inicio da vigência do reajuste é obrigatório.");
            }

            if (finalVigenciaReajuste != null) {
                if (finalVigenciaReajuste.before(inicioVigenciaReajuste)) {
                    throw new ValidacaoException("A data final de vigência do reajuste deve ser superior a data inicial de vigência do reajuste.");
                }
            }

            if (inicioVigenciaReajuste != null) {
                for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> keys : valores.entrySet()) {
                    for (Map.Entry<ProgressaoPCS, EnquadramentoPCS> values : keys.getValue().entrySet()) {
                        if (values.getValue().getInicioVigencia().compareTo(inicioVigenciaReajuste) == 0 || inicioVigenciaReajuste.before(values.getValue().getInicioVigencia())) {
                            throw new ValidacaoException("Operação não permitida! A data de início de vigência do reajuste deve ser maior que a data de início de vigência da tabela atual.");
                        }
                    }
                }
            }
        }
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


    public void filtrarReajusteEnquadramentos() {
        try {
            setAtributosReajusteNull();

            if (dataReferencia == null) {
                dataReferencia = UtilRH.getDataOperacao();
            }

            if (planoCargosSalarios == null) {
                planoCargosSalarios = new PlanoCargosSalarios();
            }

            //os metodos buscam percorrem as arvores dos objetos
            listaCategoriaPCS = buscaCategoriaObjeto();
            listaProgressaoPCS = buscaProgressoesObjeto();
            Collections.sort(listaCategoriaPCS);
            Collections.sort(listaProgressaoPCS);
            itens = new ArrayList<EnquadramentoPCS>();
            itens = enquadramentoPCSFacade.listaEnquadramentosVigentes(dataReferencia);

            valores = inicializaValores(listaCategoriaPCS, listaProgressaoPCS);
        } catch (Exception ex) {
            logger.error("Erro ao instanciar nova entidade", ex);
        }
    }

    public List<EstruturaPCS> getEstruturaPCSList() {
        return estruturaPCSList;
    }

    public void setEstruturaPCSList(List<EstruturaPCS> estruturaPCSList) {
        this.estruturaPCSList = estruturaPCSList;
    }

    public List<CategoriaPCS> completaCategoriaPCS(String parte) {
        planoCargosSalarios = planoCargosSalariosFacade.recuperar(planoCargosSalarios.getId());
        return categoriaPCSFacade.listaFiltrandoDescricaoComPCSVigente(planoCargosSalarios, parte.trim(), dataReferencia);
    }

    public void buscarTodosPcs() {
        estruturaPCSList = new LinkedList<>();
        estruturaPCSList = enquadramentoPCSFacade.buscarEstruturaCompletaPCS(selecionadoCategoriaPCS, selecionadoProgressaoPCS);
        for (EstruturaPCS estruturaPCS : estruturaPCSList) {
            Collections.sort(estruturaPCS.getCategoriaPCSList());
            Collections.sort(estruturaPCS.getProgressaoPCSList());
        }

    }

    public void selecionarPcs(EstruturaPCS pcs) {
        dataReferencia = pcs.getInicioVigencia();
        filtrarReajusteEnquadramentos();
    }

    public void gerarPdf() {
        Util.geraPDF("Estrutura_PCCR_da_Prefeitura_Municipal_de_Rio_Branco", gerarConteudo(), FacesContext.getCurrentInstance());
    }

    public void gerarPdfReajuste() {
        Util.geraPDF("Projeção_Reajuste_PCCR", gerarConteudoReajuste(), FacesContext.getCurrentInstance());
    }

    public String getCaminhoBrasao() {
        String imagem = FacesUtil.geraUrlImagemDir();
        imagem += "/img/Brasao_de_Rio_Branco.gif";
        return imagem;
    }

    public String gerarConteudo() {
        if (estruturaPCSList != null && !estruturaPCSList.isEmpty()) {
            String content = "";
            content += "<?xml version='1.0' encoding='iso-8859-1'?>\n";
            content += "<!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">\n";
            content += "<html>\n";
            content += " <style type=\"text/css\">@page{size: A4 landscape;}</style>";

            content += "<style type=\"text/css\">\n";
            content += "    table, th, td {\n";
            content += "        border: 1px solid black;\n";
            content += "        border-collapse: collapse;\n";
            content += "    }body{font-family: Arial, \"Helvetica Neue\", Helvetica, sans-serif;}\n";
            content += "</style>\n";
            content += "<div style='border: 1px solid black;text-align: left'>\n";
            content += " <table style=\"border: none!important;\">  <tr>";
            content += " <td style=\"border: none!important; text-align: center!important;\"><img src=\"" + getCaminhoBrasao() + "\" alt=\"Smiley face\" height=\"90\" width=\"73\" /></td>   ";
            content += " <td style=\"border: none!important;\">   \n";
            content += " <td style=\"border: none!important;\">   <b> PREFEITURA MUNICIPAL DE RIO BRANCO <br/>\n";
            content += "        SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO<br/>\n";
            content += "        DEPARTAMENTO DE RECURSOS HUMANOS</b></td>\n";
            content += " </tr> </table>  \n";
            content += "</div>\n";
            content += "\n";
            content += "<div style='border: 1px solid black; text-align: center'>\n";
            content += "    <b>Estrutura do Plano de Cargos, Salários e Remuneração</b>\n";
            content += "</div>\n";
            content += "\n";

            content += "<div style='border: 1px solid black'>\n";

            EstruturaPCS pccr = estruturaPCSList.get(0);
            content += "  <b>" + pccr.getCategoriaPCS().getPlanoCargosSalarios().getDescricao() + "</b> Grupo: <b>" + pccr.getProgressaoPCS().getDescricao() + "</b> Categoria: <b>" + pccr.getCategoriaPCS().getDescricao() + "</b><br/> ";
            content += "  <p style=\"font-size:11px\">Requisito:<b>" + (pccr.getCategoriaPCS().getRequisito() != null ? pccr.getCategoriaPCS().getRequisito() : "") + "</b></p><br/> ";
            for (EstruturaPCS pcs : estruturaPCSList) {

                content += " De " + Util.dateToString(pcs.getInicioVigencia()) + " até " + (pcs.getFinalVigencia() != null ? Util.dateToString(pcs.getFinalVigencia()) : " Hoje.");
                content += "  Percentual Reajuste: " + getPercentualReajustado(pcs) + "%";
                content += "    <table style=\"width: 100%\" >\n ";
                content += "        <tr>\n ";
                content += "            <th></th>\n";

                for (ProgressaoPCS prog : pcs.getProgressaoPCSList()) {
                    content += "                <th align=\"center\">" + prog.getDescricao() + "\n ";
                    content += "                </th>\n ";
                }
                content += "        </tr>\n ";
                for (CategoriaPCS cat : pcs.getCategoriaPCSList()) {

                    content += "            <tr align=\"center\">\n ";
                    content += "                <td><b>" + cat.getDescricao() + "</b>";
                    content += "                </td>\n";
                    for (ProgressaoPCS progressaoPCS : pcs.getProgressaoPCSList()) {
                        if (pcs.getValores().get(cat).get(progressaoPCS) != null) {
                            content += "                    <td " + (pcs.getValores().get(cat).get(progressaoPCS).getVencimentoBase().compareTo(BigDecimal.ZERO) == 0 ? "bgcolor=\"#dcdcdc\"" : "") + ">" + pcs.getValores().get(cat).get(progressaoPCS).getVencimentoBase() + "</td>\n";
                        }
                    }
                    content += "            </tr>\n";
                }
                content += "    </table>\n";

                content += "<hr>";
            }

            content += "</div>\n";


            content += "</html>";
            content += "";
            return content;
        } else {
            return "Nenhum PCCR selecionado.";
        }
    }

    private BigDecimal getPercentualReajustado(EstruturaPCS pcs) {
        for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> keys : pcs.getValores().entrySet()) {
            for (Map.Entry<ProgressaoPCS, EnquadramentoPCS> values : keys.getValue().entrySet()) {
                return values.getValue().getPercentualReajuste() != null ? values.getValue().getPercentualReajuste() : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }


    public String gerarConteudoReajuste() {
        if (valores != null && !valores.isEmpty() && valoresRejustados != null && !valoresRejustados.isEmpty() && !categoriaPCSs.isEmpty() && !categoriaPCSs.isEmpty()) {
            String content = "";
            content += "<?xml version='1.0' encoding='iso-8859-1'?>\n";
            content += "<!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">\n";
            content += "<html>\n";
            content += " <style type=\"text/css\">@page{size: A4 landscape;}</style>";

            content += "<style type=\"text/css\">\n";
            content += "    table, th, td {\n";
            content += "        border: 1px solid black;\n";
            content += "        border-collapse: collapse;\n";
            content += "    }body{font-family: Arial, \"Helvetica Neue\", Helvetica, sans-serif;}\n";
            content += "</style>\n";

            content += "<div style='border: 1px solid black;text-align: left'>\n";
            content += " <table style=\"border: none!important;\">  <tr>";
            content += " <td style=\"border: none!important; text-align: center!important;\"><img src=\"" + getCaminhoBrasao() + "\" alt=\"Smiley face\" height=\"90\" width=\"73\" /></td>   ";
            content += " <td style=\"border: none!important;\">   \n";
            content += " <td style=\"border: none!important;\">   <b> PREFEITURA MUNICIPAL DE RIO BRANCO <br/>\n";
            content += "        SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO<br/>\n";
            content += "        DEPARTAMENTO DE RECURSOS HUMANOS</b></td>\n";
            content += " </tr> </table>  \n";
            content += "</div>\n";
            content += "\n";
            content += "<div style='border: 1px solid black; text-align: center'>\n";
            content += "    <b>Simulação de Reajuste de Estrutura do Plano de Cargos, Salários e Remuneração</b>\n";
            content += "</div>\n";
            content += "\n";

            content += "<div style='border: 1px solid black'>\n";

            CategoriaPCS categoriaPCS = selecionadoCategoriaPCS;
            ProgressaoPCS progressaoPCS = selecionadoProgressaoPCS;
            content += "  <b>" + categoriaPCS.getPlanoCargosSalarios().getDescricao() + "</b> Grupo: <b>" + progressaoPCS.getDescricao() + "</b> Categoria: <b>" + categoriaPCS.getDescricao() + "</b><br/> ";
            content += "  <p style=\"font-size:11px\">Requisito:<b>" + (categoriaPCS.getRequisito() != null ? categoriaPCS.getRequisito() : "") + "</b></p><br/> ";
            content += " De " + getInicioVigenciaEnquadramentos() + " até " + (!getFinalVigenciaEnquadramentos().equals("") ? getFinalVigenciaEnquadramentos() : " Hoje.");

            content += "    <table style=\"width: 100%\" >\n ";
            content += "        <tr>\n ";
            content += "            <th></th>\n";

            for (ProgressaoPCS prog : progressaoPCSs) {
                content += "                <th align=\"center\">" + prog.getDescricao() + "\n ";
                content += "                </th>\n ";
            }
            content += "        </tr>\n ";
            for (CategoriaPCS cat : categoriaPCSs) {

                content += "            <tr align=\"center\">\n ";
                content += "                <td>" + cat.getDescricao() + "";
                content += "                </td>\n";
                for (ProgressaoPCS prog2 : progressaoPCSs) {
                    content += "                    <td " + (valores.get(cat).get(prog2).getVencimentoBase().compareTo(BigDecimal.ZERO) == 0 ? "bgcolor=\"#dcdcdc\"" : "") + ">" + valores.get(cat).get(prog2).getVencimentoBase() + "</td>\n";
                }
                content += "            </tr>\n";
            }
            content += "    </table>\n";
            content += "<hr>";

            content += " Projeção dos valores sobre " + valorReajuste + "%";
            content += " a partir de " + Util.dateToString(inicioVigenciaReajuste);

            content += "    <table style=\"width: 100%\" >\n ";
            content += "        <tr>\n ";
            content += "            <th></th>\n";

            for (ProgressaoPCS prog : progressaoPCSs) {
                content += "                <th align=\"center\">" + prog.getDescricao() + "\n ";
                content += "                </th>\n ";
            }
            content += "        </tr>\n ";
            for (CategoriaPCS cat : categoriaPCSs) {

                content += "            <tr align=\"center\">\n ";
                content += "                <td>" + cat.getDescricao() + "";
                content += "                </td>\n";
                for (ProgressaoPCS prog2 : progressaoPCSs) {
                    if (valoresRejustados.get(cat).get(prog2) != null) {
                        content += "                     <td " + (valoresRejustados.get(cat).get(prog2).getVencimentoBase().compareTo(BigDecimal.ZERO) == 0 ? "bgcolor=\"#dcdcdc\"" : "") + ">" + valoresRejustados.get(cat).get(prog2).getVencimentoBase() + "</td>\n";
                    }
                }
                content += "            </tr>\n";
            }
            content += "    </table>\n";

            content += "<hr>";


            content += "</div>\n";
            content += "</html>";
            content += "";
            return content;
        } else {
            return "Aplique um reajuste para visualizar.";
        }
    }

    public TipoEdicaoReajuste getTipoEdicaoReajuste() {
        return tipoEdicaoReajuste;
    }

    public void setTipoEdicaoReajuste(TipoEdicaoReajuste tipoEdicaoReajuste) {
        this.tipoEdicaoReajuste = tipoEdicaoReajuste;
    }

    public List<SelectItem> getModosReajuste() {
        return Util.getListSelectItem(TipoEdicaoReajuste.values(), false);
    }

    public enum TipoEdicaoReajuste {
        BASE_VALORES_ATUAIS("Atualizar com base nos valores atuais da tabela"),
        BASE_VALORES_ANTERIORES("Atualizar com base nos valores anteriores à vigência atual");

        String descricao;

        TipoEdicaoReajuste(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
