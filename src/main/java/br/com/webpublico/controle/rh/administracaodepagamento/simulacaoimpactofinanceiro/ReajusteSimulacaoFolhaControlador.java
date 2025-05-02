package br.com.webpublico.controle.rh.administracaodepagamento.simulacaoimpactofinanceiro;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.administracaodepagamento.EnquadramentoPCSSimulacao;
import br.com.webpublico.entidadesauxiliares.EstruturaPCS;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.administracaodepagamento.EnquadramentoPCSSimulacaoFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author peixe on 30/01/2017  12:21.
 */
@ManagedBean(name = "reajusteSimulacaoFolhaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoReajusteSimulacao", pattern = "/reajuste-pccr-simulacao/novo/", viewId = "/faces/rh/administracaodepagamento/simulacaofolha/edita.xhtml"),
    @URLMapping(id = "editarReajusteSimulacao", pattern = "/reajuste-pccr-simulacao/editar/#{reajusteSimulacaoFolhaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/simulacaofolha/edita.xhtml"),
    @URLMapping(id = "listarReajusteSimulacao", pattern = "/reajuste-pccr-simulacao/listar/", viewId = "/faces/rh/administracaodepagamento/simulacaofolha/lista.xhtml"),
    @URLMapping(id = "verReajusteSimulacao", pattern = "/reajuste-pccr-simulacao/ver/#{reajusteSimulacaoFolhaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/simulacaofolha/visualizar.xhtml")
})
public class ReajusteSimulacaoFolhaControlador extends PrettyControlador<EnquadramentoPCSSimulacao> implements Serializable, CRUD {

    protected static final Logger logger = LoggerFactory.getLogger(ReajusteSimulacaoFolhaControlador.class);
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    @EJB
    private EnquadramentoPCSSimulacaoFacade enquadramentoPCSSimulacaoFacade;
    private PlanoCargosSalarios planoCargosSalarios;
    private ConverterGenerico converterPlanoCargosSalarios;
    private List<EnquadramentoPCS> itens;
    private List<ProgressaoPCS> progressaoPCSs;
    private List<CategoriaPCS> categoriaPCSs;

    private MoneyConverter moneyConverter;

    private List<CategoriaPCS> listaCategoriaPCS;
    private CategoriaPCS selecionadoCategoriaPCS;
    private CategoriaPCS selecionadoCategoriaPCSFilha;
    private List<ProgressaoPCS> listaProgressaoPCS;
    private ProgressaoPCS selecionadoProgressaoPCS;
    private Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>> valores;
    private Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>> valoresRejustados;
    private BigDecimal valorReajuste;
    private Date inicioVigenciaReajuste;
    private Date finalVigenciaReajuste;
    private PercentualConverter percentualConverter;

    private List<EstruturaPCS> estruturaPCSList;
    private Date dataReferencia;
    @EJB
    private ReajustePCSFacade reajustePCSFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ReajusteSimulacaoFolhaControlador() {
        metadata = new EntidadeMetaData(FolhaDePagamento.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/reajuste-pccr-simulacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @Override
    public EnquadramentoPCSSimulacaoFacade getFacede() {
        return enquadramentoPCSSimulacaoFacade;
    }


    @Override
    public void salvar() {
        try {

            DateTime dateTime = getDataInicioVigenciaNovoEnquadramentopcs();
            dataReferencia = dateTime.minusDays(1).toDate();

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
                List<EnquadramentoPCSSimulacao> salvos = new ArrayList<>();
                for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>> keys : valoresRejustados.entrySet()) {
                    for (Map.Entry<ProgressaoPCS, EnquadramentoPCSSimulacao> values : keys.getValue().entrySet()) {
                        salvos.add(enquadramentoPCSSimulacaoFacade.salvarComRetorno(values.getValue()));
                    }
                }
            }
            redireciona();
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Erro ao tentar persistir reajuste", e.getMessage());
        }
    }

    public void selecionar(ActionEvent evento) {
        operacao = Operacoes.EDITAR;
        ReajustePCS a = (ReajustePCS) evento.getComponent().getAttributes().get("objeto");

    }

    private boolean temLacunasEntreOsPCSs() {
        if (valores != null && !valores.isEmpty()) {
            for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>> keys : valores.entrySet()) {
                for (Map.Entry<ProgressaoPCS, EnquadramentoPCSSimulacao> values : keys.getValue().entrySet()) {
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
                for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>> keys : valores.entrySet()) {
                    for (Map.Entry<ProgressaoPCS, EnquadramentoPCSSimulacao> values : keys.getValue().entrySet()) {
                        values.getValue().setFinalVigencia(inicioNovo.minusDays(1).toDate());
                    }
                }
            }
        }
    }

    public DateTime getDataInicioVigenciaNovoEnquadramentopcs() {
        if (valoresRejustados != null && !valoresRejustados.isEmpty()) {
            for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>> keys : valoresRejustados.entrySet()) {
                for (Map.Entry<ProgressaoPCS, EnquadramentoPCSSimulacao> values : keys.getValue().entrySet()) {
                    return new DateTime(values.getValue().getInicioVigencia());
                }
            }
        }
        return null;
    }


    public void instanciar() {
        try {
            inicializarAtributos();

            selecionadoCategoriaPCS = null;

            dataReferencia = UtilRH.getDataOperacao();
            selecionadoCategoriaPCS = null;
            selecionadoCategoriaPCSFilha = null;
            selecionadoProgressaoPCS = null;

            listaCategoriaPCS = buscarCategoriaObjeto();
            listaProgressaoPCS = buscarProgressoesObjeto();

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

    public Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>> getValoresRejustados() {
        return valoresRejustados;
    }

    public void setValoresRejustados(Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>> valoresRejustados) {
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

    public EnquadramentoPCSSimulacaoFacade getFacade() {
        return enquadramentoPCSSimulacaoFacade;
    }

    public List<SelectItem> getCategorias() {
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

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public List<ProgressaoPCS> buscarProgressoesObjeto() {
        progressaoPCSs = new ArrayList<ProgressaoPCS>();
        ProgressaoPCS prog = new ProgressaoPCS();

        if (selecionadoProgressaoPCS != null) {
            prog = progressaoPCSFacade.recuperar(selecionadoProgressaoPCS.getId());
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


    public Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>> getValores() {
        return valores;
    }

    public void setValores(Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>> valores) {
        this.valores = valores;
    }

    private Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>> inicializarValores(
        List<CategoriaPCS> pCategorias, List<ProgressaoPCS> pProgressoes) {
        Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>> retorno = new HashMap<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>>();
        boolean encontrouEnquadramento = false;
        Date inivioVigencia = dataReferencia;
        Date finalVigencia = null;
        for (CategoriaPCS c : pCategorias) {
            Map<ProgressaoPCS, EnquadramentoPCSSimulacao> v = new HashMap<ProgressaoPCS, EnquadramentoPCSSimulacao>();
            for (ProgressaoPCS p : pProgressoes) {

                for (EnquadramentoPCS ePCS : itens) {
                    if ((ePCS.getCategoriaPCS().equals(c)) && (ePCS.getProgressaoPCS().equals(p))) {
                        inivioVigencia = ePCS.getInicioVigencia();
                        finalVigencia = ePCS.getFinalVigencia();
                        v.put(p, new EnquadramentoPCSSimulacao(ePCS.getInicioVigencia(), ePCS.getFinalVigencia(), ePCS.getProgressaoPCS(), ePCS.getCategoriaPCS(), ePCS.getVencimentoBase()));
                        encontrouEnquadramento = true;
                    }
                }

                if (!encontrouEnquadramento) {
                    v.put(p, new EnquadramentoPCSSimulacao(inivioVigencia, finalVigencia, p, c, BigDecimal.ZERO));
                }
                encontrouEnquadramento = false;
            }
            retorno.put(c, v);
        }
        return retorno;
    }

    public void limparCategoria() {
        selecionadoCategoriaPCSFilha = null;
    }

    public List<CategoriaPCS> buscarCategoriaObjeto() {
        categoriaPCSs = new ArrayList<CategoriaPCS>();
        CategoriaPCS cat = new CategoriaPCS();
        if (selecionadoCategoriaPCS != null && selecionadoCategoriaPCSFilha != null) {
            cat = categoriaPCSFacade.recuperar(selecionadoCategoriaPCSFilha.getId());
            if (cat.getFilhos().isEmpty()) {
                categoriaPCSs.add(cat);
            } else {
                for (CategoriaPCS o : cat.getFilhos()) {
                    categoriaPCSs.add(o);
                }
            }
        } else if (selecionadoCategoriaPCS != null && selecionadoCategoriaPCSFilha == null) {
            cat = categoriaPCSFacade.recuperar(selecionadoCategoriaPCS.getId());
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
            for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>> keys : valores.entrySet()) {
                for (Map.Entry<ProgressaoPCS, EnquadramentoPCSSimulacao> values : keys.getValue().entrySet()) {
                    if (values.getValue().getFinalVigencia() != null) return true;
                }
            }
        }
        return false;
    }

    public void aplicarReajuste() {
        try {
            validarReajuste();
            if (valorReajuste != null && valorReajuste != BigDecimal.ZERO) {
                valoresRejustados = new LinkedHashMap<>();
                for (Map.Entry<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCSSimulacao>> keys : valores.entrySet()) {
                    valoresRejustados.put(keys.getKey(), reajustarValores(keys.getValue(), keys.getKey()));
                }
            }
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getAllMensagens());
        } catch (Exception e) {
            logger.error("Erro: ", e);
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarReajuste() {
        ValidacaoException val = new ValidacaoException();
        if (valorReajuste == null) {
            val.adicionarMensagemDeCampoObrigatorio("Preenhca o campo Percentual de Reajuste.");
        }
        if (valorReajuste != null && (Util.isMenorOrIgualAZero(valorReajuste))) {
            val.adicionarMensagemDeOperacaoNaoPermitida("O percentual de reajuste deve ser um número positivo e maior que 0 (zero).");
        }

        if (inicioVigenciaReajuste == null) {
            val.adicionarMensagemDeCampoObrigatorio("O início de vigência do reajuste deve ser preenchido.");
        }
        if (valores != null && valores.isEmpty()) {
            val.adicionarMensagemDeOperacaoNaoPermitida("É necessário filtrar uma tabela salarial para simular o reajuste.");
        }
        val.lancarException();
    }

    private Map<ProgressaoPCS, EnquadramentoPCSSimulacao> reajustarValores(Map<ProgressaoPCS, EnquadramentoPCSSimulacao> value, CategoriaPCS key) {
        Map<ProgressaoPCS, EnquadramentoPCSSimulacao> valores = new LinkedHashMap<>();
        for (Map.Entry<ProgressaoPCS, EnquadramentoPCSSimulacao> values : value.entrySet()) {
            EnquadramentoPCSSimulacao pcs = new EnquadramentoPCSSimulacao(inicioVigenciaReajuste, finalVigenciaReajuste, values.getKey(), key, BigDecimal.ZERO);
            BigDecimal valorAntigo = values.getValue().getVencimentoBase();
            pcs.setVencimentoBaseAntigo(valorAntigo);
            if (valorAntigo.compareTo(BigDecimal.ZERO) == 0) {
                pcs.setVencimentoBase(BigDecimal.ZERO);
                pcs.setPercentualReajuste(valorReajuste);
            } else {
                pcs.setVencimentoBase(valorAntigo.add(valorAntigo.multiply(valorReajuste).divide(new BigDecimal("100"))));
                pcs.setPercentualReajuste(valorReajuste);
            }
            valores.put(values.getKey(), pcs);
        }
        return valores;
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

    public PercentualConverter getPercentualConverter() {
        if (percentualConverter == null) {
            percentualConverter = new PercentualConverter();
        }
        return percentualConverter;
    }

    private void inicializarAtributos() {
        valorReajuste = null;
        inicioVigenciaReajuste = null;
        finalVigenciaReajuste = null;
    }

    public void limparCampos() {
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
        inicializarAtributos();
        super.cancelar();
    }

    public String getInicioVigenciaEnquadramentos() {
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        if (valores != null) {
            for (CategoriaPCS c : valores.keySet()) {
                for (ProgressaoPCS p : valores.get(c).keySet()) {
                    EnquadramentoPCSSimulacao ePCS = valores.get(c).get(p);

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
                        EnquadramentoPCSSimulacao ePCS = valores.get(c).get(p);
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
            validarRegrasAoFiltrar();
            inicializarAtributos();

            if (dataReferencia == null) {
                dataReferencia = UtilRH.getDataOperacao();
            }

            listaCategoriaPCS = buscarCategoriaObjeto();
            listaProgressaoPCS = buscarProgressoesObjeto();
            Collections.sort(listaCategoriaPCS);
            Collections.sort(listaProgressaoPCS);
            itens = new ArrayList<EnquadramentoPCS>();
            itens = enquadramentoPCSFacade.listaEnquadramentosVigentes(dataReferencia);

            valores = inicializarValores(listaCategoriaPCS, listaProgressaoPCS);
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao filtrar PCCR", ex);
        }
    }

    private void validarRegrasAoFiltrar() {
        ValidacaoException val = new ValidacaoException();
        if (planoCargosSalarios == null) {
            val.adicionarMensagemDeCampoObrigatorio("Selecione um Plano de Cargos e Salários.");
        }
        if (selecionadoCategoriaPCS == null) {
            val.adicionarMensagemDeCampoObrigatorio("Selecione uma Categoria.");
        }
        if (selecionadoProgressaoPCS == null) {
            val.adicionarMensagemDeCampoObrigatorio("Selecione uma Progressão");
        }
        val.lancarException();
    }

    public List<EstruturaPCS> getEstruturaPCSList() {
        return estruturaPCSList;
    }

    public void setEstruturaPCSList(List<EstruturaPCS> estruturaPCSList) {
        this.estruturaPCSList = estruturaPCSList;
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


    @URLAction(mappingId = "novoReajusteSimulacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        planoCargosSalarios = null;
        dataReferencia = null;
        instanciar();
    }

    @URLAction(mappingId = "verReajusteSimulacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarReajusteSimulacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }


    public List<SelectItem> getPlanos() {
        return Util.getListSelectItem(planoCargosSalariosFacade.listaFiltrandoVigencia(getDataOperacao()), true);
    }

    private Date getDataOperacao() {
        return dataReferencia != null ? dataReferencia : sistemaFacade.getDataOperacao();
    }

}
