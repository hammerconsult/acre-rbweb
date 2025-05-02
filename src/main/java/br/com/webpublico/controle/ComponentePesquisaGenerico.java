/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.CabecalhoRelatorio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.AtributoRelatorioGenerico;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.entidadesauxiliares.RelatorioPesquisaGenerico;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoRelatorio;
import br.com.webpublico.negocios.ComponentePesquisaGenericoFacade;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import br.com.webpublico.util.anotacoes.Tabelavel.TIPOCAMPO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.PrettyContext;
import org.hibernate.envers.RevisionType;
import org.primefaces.component.outputpanel.OutputPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author Renato
 */
@ManagedBean
@ViewScoped
public class ComponentePesquisaGenerico extends AbstractReport implements Serializable {

    public static final String SESSION_KEY_PESQUISAGENERICO = "objectConversationPesquisaGenerico";
    protected static final Logger logger = LoggerFactory.getLogger(ComponentePesquisaGenerico.class);
    @EJB
    protected ComponentePesquisaGenericoFacade facade;
    protected Class<?> classe;
    protected EntidadeMetaData metadata;
    protected List lista = new ArrayList<>();
    protected List<DataTablePesquisaGenerico> camposPesquisa;
    protected int inicio = 0;
    protected int maximoRegistrosTabela = 10;
    protected List<ItemPesquisaGenerica> itens;
    protected List<ItemPesquisaGenerica> itensOrdenacao;
    protected List<ItemPesquisaGenerica> camposOrdenacao;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    protected SistemaControlador sistemaControlador;
    protected Integer totalDeRegistrosExistentes;
    private String campoOrdenacaoSelecionado;
    private String ordenacao;
    private String idComponente;
    private HtmlOutputText outPutText;
    private OutputPanel panel;
    private ItemPesquisaGenerica itemOrdenacao;
    private ItemPesquisaGenerica itemOrdenacaoSelecionado;
    private Object objetoSelecionadoHistorico;
    private List<Object[]> listaHistoricoGeral;
    private ConverterItemPesquisaGenerico converterItemPesquisaGenerico;
    private ConverterItemPesquisaGenericoOrdenacao converterItemPesquisaGenericoOrdenacao;
    private Object controlador;
    private ConverterAutoComplete converterCabecalhoRelatorio;
    private ConverterAtributoMetaData converterAtributoMetaData;
    private ConverterAtributoMetaDataRelatorioTabela converterAtributoMetaDataRelatorioTabela;
    @ManagedProperty(name = "relatorioGenericoControlador", value = "#{relatorioGenericoControlador}")
    private RelatorioGenericoControlador relatorioGenericoControlador;
    private RelatorioPesquisaGenerico relatorioPesquisaGenerico;
    private RelatorioPesquisaGenerico relatorioTabela;
    private CabecalhoRelatorio cabecalhoRelatorioVisualizacao;
    private TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional;
    private ConverterAutoComplete converterRelatorioGenerico;
    private List objetosMultiplaSelecao;
    private String hqlConsultaRelatorioTodosRegistros;
    private Boolean mostrarBotaoPesquisa;

    public Boolean getMostrarBotaoPesquisa() {
        return mostrarBotaoPesquisa;
    }

    public void setMostrarBotaoPesquisa(Boolean mostrarBotaoPesquisa) {
        this.mostrarBotaoPesquisa = mostrarBotaoPesquisa;
    }

    public TipoHierarquiaOrganizacional getTipoHierarquiaOrganizacional() {
        return tipoHierarquiaOrganizacional;
    }

    public void setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional) {
        this.tipoHierarquiaOrganizacional = tipoHierarquiaOrganizacional;
    }

    public CabecalhoRelatorio getCabecalhoRelatorioVisualizacao() {
        return cabecalhoRelatorioVisualizacao;
    }

    public void setCabecalhoRelatorioVisualizacao(CabecalhoRelatorio cabecalhoRelatorioVisualizacao) {
        this.cabecalhoRelatorioVisualizacao = cabecalhoRelatorioVisualizacao;
    }

    public RelatorioPesquisaGenerico getRelatorioTabela() {

        return relatorioTabela;
    }

    public void setRelatorioTabela(RelatorioPesquisaGenerico relatorioTabela) {
        this.relatorioTabela = relatorioTabela;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public RelatorioPesquisaGenerico getRelatorioPesquisaGenerico() {
        return relatorioPesquisaGenerico;
    }

    public void setRelatorioPesquisaGenerico(RelatorioPesquisaGenerico relatorioPesquisaGenerico) {
        this.relatorioPesquisaGenerico = relatorioPesquisaGenerico;
    }

    public RelatorioGenericoControlador getRelatorioGenericoControlador() {
        return relatorioGenericoControlador;
    }

    public void setRelatorioGenericoControlador(RelatorioGenericoControlador RelatorioGenericoControlador) {
        this.relatorioGenericoControlador = RelatorioGenericoControlador;
    }

    public ConverterItemPesquisaGenerico getConverterItemPesquisaGenerico() {
        if (converterItemPesquisaGenerico == null) {
            converterItemPesquisaGenerico = new ConverterItemPesquisaGenerico();
        }
        return converterItemPesquisaGenerico;
    }

    public void setConverterItemPesquisaGenerico(ConverterItemPesquisaGenerico converterItemPesquisaGenerico) {
        this.converterItemPesquisaGenerico = converterItemPesquisaGenerico;
    }

    public ConverterItemPesquisaGenericoOrdenacao getConverterItemPesquisaGenericoOrdenacao() {
        if (converterItemPesquisaGenericoOrdenacao == null) {
            converterItemPesquisaGenericoOrdenacao = new ConverterItemPesquisaGenericoOrdenacao();
        }
        return converterItemPesquisaGenericoOrdenacao;
    }

    public void setConverterItemPesquisaGenericoOrdenacao(ConverterItemPesquisaGenericoOrdenacao converterItemPesquisaGenericoOrdenacao) {
        this.converterItemPesquisaGenericoOrdenacao = converterItemPesquisaGenericoOrdenacao;
    }

    public Object getObjetoSelecionadoHistorico() {
        return objetoSelecionadoHistorico;
    }

    public void setObjetoSelecionadoHistorico(Object objetoSelecionadoHistorico) {
        this.objetoSelecionadoHistorico = objetoSelecionadoHistorico;
    }

    public ItemPesquisaGenerica getItemOrdenacaoSelecionado() {
        return itemOrdenacaoSelecionado;
    }

    public void setItemOrdenacaoSelecionado(ItemPesquisaGenerica itemOrdenacaoSelecionado) {
        this.itemOrdenacaoSelecionado = itemOrdenacaoSelecionado;
    }

    public ItemPesquisaGenerica getItemOrdenacao() {
        return itemOrdenacao;
    }

    public void setItemOrdenacao(ItemPesquisaGenerica itemOrdenacao) {
        this.itemOrdenacao = itemOrdenacao;
    }

    public OutputPanel getPanel() {
        return panel;
    }

    public void setPanel(OutputPanel panel) {
        this.panel = panel;
        if (this.panel != null) {
            recuperaIdComponente();
        }
    }

    public List<ItemPesquisaGenerica> getCamposOrdenacao() {
        if (camposOrdenacao == null) {
            inicializaCamposOrdenacao();
        }
        return camposOrdenacao;
    }

    public void setCamposOrdenacao(List<ItemPesquisaGenerica> camposOrdenacao) {
        this.camposOrdenacao = camposOrdenacao;
    }

    public HtmlOutputText getOutPutText() {

        return outPutText;
    }

    public void setOutPutText(HtmlOutputText outPutText) {
        this.outPutText = outPutText;
    }

    public String getIdComponente() {
        return idComponente;
    }

    public void setIdComponente(String idComponente) {
        this.idComponente = idComponente;
    }

    public String nomeDaClasse() {
        if (this.classe.isAnnotationPresent(br.com.webpublico.util.anotacoes.Etiqueta.class)) {
            br.com.webpublico.util.anotacoes.Etiqueta e = (Etiqueta) this.classe.getAnnotation(br.com.webpublico.util.anotacoes.Etiqueta.class);
            return e.value();
        }
        return this.classe.getSimpleName();
    }

    public List<ItemPesquisaGenerica> getItens() {
        return itens;
    }

    public void setItens(List<ItemPesquisaGenerica> itens) {
        this.itens = itens;
    }

    public List<ItemPesquisaGenerica> getItensOrdenacao() {
        return itensOrdenacao;
    }

    public void setItensOrdenacao(List<ItemPesquisaGenerica> itensOrdenacao) {
        this.itensOrdenacao = itensOrdenacao;
    }

    public List<DataTablePesquisaGenerico> getCamposPesquisa() {
        return camposPesquisa;
    }

    public void setCamposPesquisa(List<DataTablePesquisaGenerico> camposPesquisa) {
        this.camposPesquisa = camposPesquisa;
    }

    public EntidadeMetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(EntidadeMetaData metadata) {
        this.metadata = metadata;
    }

    public List getLista() {
        return lista;
    }

    public void setLista(List lista) {
        this.lista = lista;
    }

    public String getCampoOrdenacaoSelecionado() {
        return campoOrdenacaoSelecionado;
    }

    public void setCampoOrdenacaoSelecionado(String campoOrdenacaoSelecionado) {
        this.campoOrdenacaoSelecionado = campoOrdenacaoSelecionado;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public void setaNullItemOrdenacaoSelecionado() {
        itemOrdenacaoSelecionado = new ItemPesquisaGenerica();
    }

    public boolean ehCrescente(String condicao) {
        if (condicao.contains("asc")) {
            return true;
        }
        return false;
    }

    public void alteraTipoOrdenacao() {

        for (ItemPesquisaGenerica itemPesquisaGenerica : camposOrdenacao) {
            if ((itemOrdenacaoSelecionado.getCondicao().equals(itemPesquisaGenerica.getCondicao())) && (itemOrdenacaoSelecionado.getLabel().equals(itemPesquisaGenerica.getLabel()))) {
                if (itemPesquisaGenerica.getCondicao().contains("asc")) {
                    itemPesquisaGenerica.setCondicao(itemPesquisaGenerica.getCondicao().replace("asc", "desc"));
                } else {
                    itemPesquisaGenerica.setCondicao(itemPesquisaGenerica.getCondicao().replace("desc", "asc"));
                }
            }
        }
    }

    public void removeCampoOrdenacao(ItemPesquisaGenerica objeto) {
        camposOrdenacao.remove(objeto);
    }

    public void adicionarCampoOrdenacao() {
        ItemPesquisaGenerica item = camposOrdenacao.get(camposOrdenacao.size() - 1);
        if (item.getCondicao() != null && !item.getCondicao().isEmpty()) {

            ItemPesquisaGenerica itemPesquisaGenerica = new ItemPesquisaGenerica();
            itemPesquisaGenerica.setTipoOrdenacao("asc");
            itemPesquisaGenerica.setCondicao("");
            itemPesquisaGenerica.setLabel("");
            itemPesquisaGenerica.setPertenceOutraClasse(item.isPertenceOutraClasse());

            camposOrdenacao.add(itemPesquisaGenerica);

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar adicionar!", "Informe o campo."));
        }
    }

    public boolean podeAdicionarCampoOrdenacao(ItemPesquisaGenerica itemOrdenacao) {
        for (ItemPesquisaGenerica itemPesquisaGenerica : camposOrdenacao) {
            if ((itemPesquisaGenerica.getLabel().equals(itemOrdenacao.getLabel()))
                && (itemPesquisaGenerica.getCondicao().equals(itemOrdenacao.getCondicao()))) {
                return false;
            }
        }
        return true;
    }

    public void alteraCampoOrdenacaoPraCima() {
        int indiceAtual = camposOrdenacao.indexOf(itemOrdenacaoSelecionado);
        camposOrdenacao.remove(itemOrdenacaoSelecionado);
        camposOrdenacao.add(indiceAtual - 1, itemOrdenacaoSelecionado);
    }

    public boolean disabeldAlteraTipoOrdenacao() {
        if (itemOrdenacaoSelecionado != null && itemOrdenacaoSelecionado.getLabel() != null) {
            return false;
        }
        return true;
    }

    public boolean disabledBotaoAlteraCampoOrdenacaoParaCima() {
        if (camposOrdenacao.size() == 1) {
            return true;
        }
        int indice = recuperaIndiceItemOrdenacaoSelecionado();
        if (indice == -1) {
            return true;
        }
        if (indice != 0) {
            return false;
        }
        return true;
    }

    public boolean disabledBotaoAlteraCampoOrdenacaoParaBaixo() {
        if (camposOrdenacao.size() == 1) {
            return true;
        }
        int indice = recuperaIndiceItemOrdenacaoSelecionado();
        if (indice == -1) {
            return true;
        }
        if (indice != (camposOrdenacao.size() - 1)) {
            return false;
        }
        return true;
    }

    public void removeCampoOrdenacao() {
        camposOrdenacao.remove(itemOrdenacaoSelecionado);
        setaNullItemOrdenacaoSelecionado();
    }

    public int recuperaIndiceItemOrdenacaoSelecionado() {
        if (itemOrdenacaoSelecionado != null && itemOrdenacaoSelecionado.getLabel() != null) {
            return camposOrdenacao.indexOf(itemOrdenacaoSelecionado);
        }
        return -1;
    }

    public void alteraCampoOrdenacaoPraBaixo() {
        int indiceAtual = camposOrdenacao.indexOf(itemOrdenacaoSelecionado);
        camposOrdenacao.remove(itemOrdenacaoSelecionado);
        camposOrdenacao.add(indiceAtual + 1, itemOrdenacaoSelecionado);
    }

    public void adicionaNovoCampoPesquisa() {
        DataTablePesquisaGenerico item = camposPesquisa.get(camposPesquisa.size() - 1);
//        if (item.getValuePesquisa() != null && !item.getValuePesquisa().isEmpty()) {
        camposPesquisa.add(new DataTablePesquisaGenerico());
//        } else {
//            FacesContext.getCurrentInstance().addMessage(idComponente + "btnAdicionarNovoCampoPesquisa_" + indiceDoObjeto(item), new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar adicionar!", "Informe o campo."));
//        }
    }

    public boolean validaPesquisa() {
        for (DataTablePesquisaGenerico item : camposPesquisa) {
            if ((item.getItemPesquisaGenerica() == null || item.getItemPesquisaGenerica().getLabel().isEmpty()) && (item.getValuePesquisa() != null && !item.getValuePesquisa().isEmpty())) {
                FacesUtil.addError("Impossível continuar!", "Informe um campo para realizar a pesquisa por " + item.getValuePesquisa());
                return false;
            }
        }
        return true;
    }

    public void removeCampoPesquisa(DataTablePesquisaGenerico objeto) {
        camposPesquisa.remove(objeto);
    }

    public boolean mostraBotao(DataTablePesquisaGenerico objeto) {
        int indice = camposPesquisa.indexOf(objeto);
        if ((indice + 1) == camposPesquisa.size()) {
            return true;
        }
        return false;
    }

    public boolean mostraBotaoOrdenacao(ItemPesquisaGenerica objeto) {
        int indice = camposOrdenacao.indexOf(objeto);
        if ((indice + 1) == camposOrdenacao.size()) {
            return true;
        }
        return false;
    }

    public void limpaCamposDaView() {
        Class<?> classeAntiga = classe;
        limpaCampos();
        classe = classeAntiga;
        ItemPesquisaGenerica itemPesquisaGenerica = new ItemPesquisaGenerica();
        itemPesquisaGenerica.setTipoOrdenacao("asc");
        itemPesquisaGenerica.setCondicao("");
        itemPesquisaGenerica.setLabel("");
        camposOrdenacao.add(itemPesquisaGenerica);
    }

    public void inicializaCamposPesquisa() {
        camposPesquisa = new ArrayList<>();
        camposPesquisa.add(new DataTablePesquisaGenerico());
    }

    public void inicializaCamposOrdenacao() {
        itemOrdenacao = new ItemPesquisaGenerica();
        itemOrdenacaoSelecionado = new ItemPesquisaGenerica();
        camposOrdenacao = new ArrayList<>();
    }

    public void limpaCampos() {
        campoOrdenacaoSelecionado = "";
        ordenacao = "asc";
        metadata = null;
        lista = new ArrayList<>();
        inicializaCamposPesquisa();
        inicializaCamposOrdenacao();

        panel = null;
        listaHistoricoGeral = null;
        objetoSelecionadoHistorico = null;
    }

    public Class<?> getClasse() {
        return classe;
    }

    public void setClasse(Class<?> classe) {
        this.classe = classe;
    }

    public void atribuirClasse(String nomeDaClasse, String pacote) {
        nomeDaClasse = pacote + nomeDaClasse;
        try {
            classe = Class.forName(nomeDaClasse);
        } catch (ClassNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro !", "Classe não encontrada : " + nomeDaClasse));
        }
    }

    private Map<String, Object> getSession() {
        Map<String, Object> stringObjectMap = (Map<String, Object>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        if (stringObjectMap == null) {
            stringObjectMap = new HashMap<>();
        }
        return stringObjectMap;
    }

    public String getKeySession() {
        return SESSION_KEY_PESQUISAGENERICO + classe.getSimpleName();
    }

    public void defineControlador(Object controlador) {
        this.controlador = controlador;
    }

    public Object getControlador() {
        if (this.controlador == null) {
            return this;
        }
        return this.controlador;
    }

    public void poeNaSessao(Boolean guardaNaSessao) {
        if (guardaNaSessao) {
            getSession().put(getKeySession(), getControlador());
        }
    }

    public void novo(ComponentSystemEvent evento) {
        Object o = pegaControladorDaSessao();

        if (o != null) {
            mostrarBotaoPesquisa = false;
            Boolean isDialog = (Boolean) evento.getComponent().getAttributes().get("isDialog");
            if (isDialog != null) {
                if (isDialog) {
                    mostrarBotaoPesquisa = true;
                }
            }
            defineControlador(o);
            ComponentePesquisaGenerico controladorDaSessao = (ComponentePesquisaGenerico) this.controlador;

            this.idComponente = controladorDaSessao.getIdComponente();
            if (camposPesquisa == null) {
                camposPesquisa = Lists.newArrayList();
            }
            if (camposOrdenacao == null) {
                camposOrdenacao = Lists.newArrayList();
            }
            filtrar();
        } else {
            mostrarBotaoPesquisa = true;
            idComponente = (String) evento.getComponent().getAttributes().get("clientId");
            recuperaIdComponente();
            prepararAtributos();
            prepararConfiguracaoRelatorio();
        }

    }

    public Object pegaControladorDaSessao() {
        try {
            return getSession().get(getKeySession());
        } catch (Exception e) {
            return null;
        }
    }

    public void prepararAtributos() {
        limpaCampos();
        itens = new ArrayList<>();
        itensOrdenacao = new ArrayList<>();
        getCampos();
        camposPesquisa.get(0).setItemPesquisaGenerica(itens.get(0));

        ItemPesquisaGenerica itemPesquisaGenerica = new ItemPesquisaGenerica();
        itemPesquisaGenerica.setTipoOrdenacao("asc");
        itemPesquisaGenerica.setCondicao("");
        itemPesquisaGenerica.setLabel("");
        camposOrdenacao.add(itemPesquisaGenerica);
    }

    public void prepararConfiguracaoRelatorio() {
        novoRelatorioGenericoTabela();
        novoRelatorioGenericoUnicoRegistro();
    }

    public void novoRelatorioGenericoUnicoRegistro() {

        try {
            RelatorioPesquisaGenerico rpg = facade.getRelatorioPesquisaGenericoFacade().recuperaRelatorioPorClasseTipoPadrao(classe.getName(), TipoRelatorio.UNICO_REGISTRO);
            if (rpg == null) {
                Object o = classe.newInstance();
                EntidadeMetaData emd = new EntidadeMetaData(classe);
                relatorioPesquisaGenerico = new RelatorioPesquisaGenerico(o, emd);
                relatorioPesquisaGenerico.setProfundidade(Integer.valueOf(relatorioPesquisaGenerico.PROFUNDIDADE_BUSCA_RELATORIO));
                relatorioPesquisaGenerico.setTipoRelatorio(TipoRelatorio.UNICO_REGISTRO);

                List<CabecalhoRelatorio> completaCabecalhoRelatorio = completaCabecalhoRelatorio("");
                if (completaCabecalhoRelatorio != null && !completaCabecalhoRelatorio.isEmpty()) {
                    relatorioPesquisaGenerico.setCabecalhoRelatorio(completaCabecalhoRelatorio.get(0));
                }
            } else {
                relatorioPesquisaGenerico = rpg;
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        if (classe != null) {
            for (Field field : Persistencia.getAtributos(classe)) {
                if (field.isAnnotationPresent(Pesquisavel.class)) {
                    criarItemPesquisaGenerico(field);
                }
            }
        }
    }

    public void criarItemPesquisaGenerico(Field field) {
        field.setAccessible(true);
        String nomeDoCampo = recuperaNomeDoCampo(field);
        String condicao = "obj." + field.getName();
        if (facade.getRecuperadorFacade().isEntidade(field.getType()) && !field.isAnnotationPresent(Enumerated.class)) {
            String nomeDaClasseDoCampo = field.getType().getName();
            try {
                Class<?> classeDoCampo = Class.forName(nomeDaClasseDoCampo);
                if (classeDoCampo.equals(Pessoa.class)) {
                    quandoForPessoa(classeDoCampo, nomeDoCampo, condicao);
                } else {
                    for (Field fieldDoCampo : classeDoCampo.getDeclaredFields()) {
                        if (fieldDoCampo.isAnnotationPresent(Pesquisavel.class)) {
                            String nomeFieldDoCampo = nomeDoCampo + "." + recuperaNomeDoCampo(fieldDoCampo);
                            String condicaoFieldDoCampo = condicao + "." + fieldDoCampo.getName();
                            if (fieldDoCampo.isAnnotationPresent(Enumerated.class)) {
                                quandoForEnum(fieldDoCampo, nomeFieldDoCampo, condicaoFieldDoCampo);
                            } else if (fieldDoCampo.getType().equals(Boolean.class)) {
                                quandoForBoolean(fieldDoCampo, nomeFieldDoCampo, condicaoFieldDoCampo);
                            } else {
                                quandoNaoForNada(fieldDoCampo, nomeFieldDoCampo, condicaoFieldDoCampo, false, true);
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException ex) {
                logger.error("Erro: ", ex);
            }
        } else if (field.isAnnotationPresent(Enumerated.class)) {
            quandoForEnum(field, nomeDoCampo, condicao);
        } else if (field.getType().equals(Boolean.class)) {
            quandoForBoolean(field, nomeDoCampo, condicao);
        } else {
            quandoNaoForNada(field, nomeDoCampo, condicao, false, false);
        }
    }

    private void quandoForPessoa(Class<?> classeDoCampo, String nomeDoCampoSuperior, String condicaoSuperior) {
        itens.add(new ItemPesquisaGenerica(condicaoSuperior, nomeDoCampoSuperior + ".Nome", Pessoa.class));
        itens.add(new ItemPesquisaGenerica(condicaoSuperior, nomeDoCampoSuperior + ".Razão Social", Pessoa.class));
        itens.add(new ItemPesquisaGenerica(condicaoSuperior, nomeDoCampoSuperior + ".CPF", Pessoa.class));
        itens.add(new ItemPesquisaGenerica(condicaoSuperior, nomeDoCampoSuperior + ".CNPJ", Pessoa.class));
    }

    private void quandoForEnum(Field field, String nomeDoCampo, String condicao) {
        String nomeDaClasseDoCampo = field.getType().getSimpleName();
        nomeDaClasseDoCampo = "br.com.webpublico.entidades." + nomeDaClasseDoCampo;
        String label = nomeDoCampo;
        Pesquisavel anotacao = (Pesquisavel) field.getAnnotation(Pesquisavel.class);
        ItemPesquisaGenerica e = new ItemPesquisaGenerica(condicao, label, field.getType(), true, false);
        e.seteEnumOrdinal(true);
        if (field.isAnnotationPresent(Enumerated.class) && EnumType.STRING.equals(((Enumerated) field.getAnnotation(Enumerated.class)).value())) {
            e.seteEnumOrdinal(false);
        }
        itens.add(e);
        if (anotacao.mostrarNaOrdenacao()) {
            itensOrdenacao.add(new ItemPesquisaGenerica(condicao, label, field.getType(), true, false, anotacao.labelBooleanFalso(), anotacao.labelBooleanVerdadeiro()));
        }
    }

    private void quandoForBoolean(Field field, String nomeDoCampo, String condicao) throws SecurityException {
        String label = nomeDoCampo;
        Pesquisavel anotacao = (Pesquisavel) field.getAnnotation(Pesquisavel.class);
        ItemPesquisaGenerica item = new ItemPesquisaGenerica(condicao, label, field.getType(), false, false, anotacao.labelBooleanFalso(), anotacao.labelBooleanVerdadeiro());
        itens.add(item);
        if (((Pesquisavel) field.getAnnotation(Pesquisavel.class)).mostrarNaOrdenacao()) {
            itensOrdenacao.add(new ItemPesquisaGenerica(condicao, label, field.getType()));
        }
        field.setAccessible(false);
    }

    private void quandoNaoForNada(Field field, String nomeDoCampo, String condicao, Boolean isEnum, Boolean pertenceOutraClasse) throws SecurityException {
        String label = nomeDoCampo;
        if (field.isAnnotationPresent(Tabelavel.class)) {
            Tabelavel annotation = field.getAnnotation(Tabelavel.class);
            if (annotation.TIPOCAMPO().equals(TIPOCAMPO.NUMERO_ORDENAVEL)) {
                condicao = "to_number(" + condicao + ")";
            }
        }
        itens.add(new ItemPesquisaGenerica(condicao, label, field.getType(), isEnum, pertenceOutraClasse));
        if (((Pesquisavel) field.getAnnotation(Pesquisavel.class)).mostrarNaOrdenacao()) {
            itensOrdenacao.add(new ItemPesquisaGenerica(condicao, label, field.getType(), isEnum, pertenceOutraClasse));
        }
        field.setAccessible(false);
    }

    public UIComponent getComponentById(String id) {
        return FacesContext.getCurrentInstance().getViewRoot().findComponent(id);
    }

    public void filtrar() {
        String hql = getHqlConsulta() + getComplementoQuery();
        String hqlCount = getHqlContador() + getComplementoQuery();
        executarConsulta(hql, hqlCount);
    }

    public String getComplementoQuery() {
        return " where " + montaCondicao() + montaOrdenacao();
    }

    public String getHqlConsulta() {
        return "select obj  from " + classe.getSimpleName() + " obj ";
    }

    public String getHqlContador() {
        return "select count(obj.id)  from " + classe.getSimpleName() + " obj ";
    }

    public void atribuirHqlConsultaRelatorioTodosRegistros() {
        hqlConsultaRelatorioTodosRegistros = getHqlConsulta() + getComplementoQuery();
    }

    public String getHqlConsultaRelatorioTodosRegistros() {
        return hqlConsultaRelatorioTodosRegistros;
    }

    public void executarConsulta(String sql, String sqlCount) {
        Object objeto = null;
        try {
            objeto = (Object) classe.newInstance();
            Object[] retorno = facade.filtarComContadorDeRegistros(sql, sqlCount, objeto, inicio, maximoRegistrosTabela);

            lista = (ArrayList) retorno[0];
            totalDeRegistrosExistentes = ((Long) retorno[1]).intValue();

            if (lista.size() > 0) {
                metadata = new EntidadeMetaData(lista.get(0).getClass());
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar Pesquisar!", ex.getMessage()));
            logger.debug(ex.getMessage());
        }
    }

    protected String primeiraLetraMinuscula(String palavra) {
        return palavra.substring(0, 1).toLowerCase().concat(palavra.substring(1));
    }

    private String primeiraLetraMaiuscula(String palavra) {
        return palavra.substring(0, 1).toUpperCase().concat(palavra.substring(1));
    }

    public String recuperaNomeDoCampo(Field field) {
        return Persistencia.getNomeDoCampo(field);
    }

    protected String ordenacaoPadrao() {
        return " order by obj.id desc";
    }

    public boolean isCampoDeOrdenacaoPertencenteOutraClasse(ItemPesquisaGenerica campoOrdenacao) {
        if (campoOrdenacao.getCondicao() != null && itensOrdenacao != null) {
            for (ItemPesquisaGenerica item : itensOrdenacao) {
                if (item.getCondicao().equals(campoOrdenacao.getCondicao())) {
                    if (item.isPertenceOutraClasse()) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    protected String montaOrdenacao() {
        if (camposOrdenacao != null) {
            if (!camposOrdenacao.isEmpty()) {
                String ordenar = " order by ";
                for (ItemPesquisaGenerica itemPesquisaGenerica : camposOrdenacao) {
                    if (itemPesquisaGenerica.getCondicao() != null) {
                        if (!itemPesquisaGenerica.getCondicao().trim().isEmpty()) {
                            String alias = "";
                            if (!isCampoDeOrdenacaoPertencenteOutraClasse(itemPesquisaGenerica) && !itemPesquisaGenerica.getCondicao().contains("obj.")) {
                                alias = "obj.";
                            }
                            ordenar += alias + primeiraLetraMinuscula(itemPesquisaGenerica.getCondicao()) + " " + itemPesquisaGenerica.getTipoOrdenacao() + ",";
                        }
                    }
                }

                if (ordenar.equals(" order by ")) {
                    return ordenacaoPadrao();
                } else {
                    return ordenar.substring(0, ordenar.length() - 1).replace("obj. ", " ");
                }
            }
        }
        return ordenacaoPadrao();
    }

    private boolean validaCampos() {
        boolean deuCerto = true;
        for (DataTablePesquisaGenerico dataTablePesquisaGenerico : camposPesquisa) {
            deuCerto = validaItem(dataTablePesquisaGenerico);
        }
        return deuCerto;
    }

    public boolean validaItem(DataTablePesquisaGenerico dataTablePesquisaGenerico) {
        boolean deuCerto = true;
        if (dataTablePesquisaGenerico.getItemPesquisaGenerica() == null) {
            return false;
        }
        if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getTipo() == null) {
            return false;
        }
//        if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao() == null) {
//            deuCerto = false;
//        }
//        if ((dataTablePesquisaGenerico.getValuePesquisa() == null || dataTablePesquisaGenerico.getValuePesquisa().isEmpty())
//                && (dataTablePesquisaGenerico.getValeuPesquisaDataFim() == null || dataTablePesquisaGenerico.getValeuPesquisaDataFim().isEmpty())) {
//            deuCerto = false;
//        }
        return deuCerto;
    }

    public int indiceDoObjeto(DataTablePesquisaGenerico objeto) {
        if (camposPesquisa.contains(objeto)) {
            return camposPesquisa.indexOf(objeto);
        } else {
            return 0;
        }
    }

    public int indiceDaTabela(Object objeto) {
        return lista.indexOf(objeto);
    }

    public int recuperaIndice(DataTablePesquisaGenerico objeto) {
        return camposPesquisa.indexOf(objeto) + 1;
    }

    public void retornaPaginacaoInicioPesquisar() {
        if (validaPesquisa()) {
            inicio = 0;
            filtrar();
        }
    }

    public int getInicioBuscaTabela() {
        return inicio;
    }

    public int getMaximoRegistrosTabela() {
        return maximoRegistrosTabela;
    }

    public void setMaximoRegistrosTabela(int maximoRegistrosTabela) {
        this.maximoRegistrosTabela = maximoRegistrosTabela;
    }

    public String montaCondicao() {
        String condicao = "";
        if (camposPesquisa != null) {
            for (DataTablePesquisaGenerico dataTablePesquisaGenerico : camposPesquisa) {
                if (validaItem(dataTablePesquisaGenerico)) {

                    String alias = "";
                    if (dataTablePesquisaGenerico.getValuePesquisa().trim().isEmpty()) {
                        String condicaoDoCampo = primeiraLetraMinuscula(dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao());

                        if (!dataTablePesquisaGenerico.getItemPesquisaGenerica().isPertenceOutraClasse() && !dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().contains("obj.")) {
                            alias = "obj.";
                        }
                        condicao += alias + condicaoDoCampo + " is null and ";

                    } else {
                        String condicaoIsString = "";

                        String condicaoDoCampo = primeiraLetraMinuscula(dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao());
                        String valueDoCampo = dataTablePesquisaGenerico.getValuePesquisa();
                        Class<?> classe;
                        boolean isString = true;
                        boolean isValor = false;
                        boolean isDate = false;
                        try {
                            classe = (Class<?>) dataTablePesquisaGenerico.getItemPesquisaGenerica().getTipo();
                            if (!condicaoDoCampo.contains("to_number")) {
                                condicaoIsString = "lower(to_char(" + alias + condicaoDoCampo + "))";
                            } else {
                                condicaoIsString = alias + condicaoDoCampo;
                            }

                            if (classe.equals(Integer.class) || classe.equals(Long.class)) {
                                isString = false;
                                isValor = true;
                                condicaoIsString = alias + condicaoDoCampo + "";
                                valueDoCampo = "to_number(to_char('" + valueDoCampo + "'))";
                            }
                            if (classe.equals(Date.class)) {
                                isString = false;
                                isDate = true;
                                condicaoIsString = alias + condicaoDoCampo;
                                String valeuFimData = "";
                                if (dataTablePesquisaGenerico.getValeuPesquisaDataFim() != null) {
                                    if (!dataTablePesquisaGenerico.getValeuPesquisaDataFim().isEmpty()) {
                                        valeuFimData = " <= to_date('" + dataTablePesquisaGenerico.getValeuPesquisaDataFim() + "','dd/MM/yyyy')";
                                    }
                                }

                                boolean dateInicioFimPreenchido = false;
                                if (!valueDoCampo.isEmpty() && !valeuFimData.isEmpty()) {
                                    valueDoCampo = " to_date('" + valueDoCampo + "','dd/MM/yyyy')" + "-" + " to_date('" + dataTablePesquisaGenerico.getValeuPesquisaDataFim() + "','dd/MM/yyyy')";
                                    dateInicioFimPreenchido = true;
                                }
                                if (!dateInicioFimPreenchido) {
                                    if (!valueDoCampo.isEmpty()) {
                                        valueDoCampo = " >= to_date('" + valueDoCampo + "','dd/MM/yyyy')";
                                    }
                                    if (!valeuFimData.isEmpty()) {
                                        valueDoCampo = valeuFimData;
                                    }
                                }


                            }
                            if (classe.equals(BigDecimal.class) || classe.equals(Double.class)) {
                                condicaoIsString = alias + condicaoDoCampo + "";
                                isString = false;
                                isValor = true;
                            }
                            if (classe.equals(Boolean.class)) {
                                condicaoIsString = alias + condicaoDoCampo + "";
                                isString = false;
                                isValor = true;
                            }
                            if (classe.equals(Pessoa.class)) {
                                condicao += " (" + alias + condicaoDoCampo + " in (select pf from PessoaFisica pf where lower(pf.nome) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%' or (replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%'))"
                                    + " or " + alias + condicaoDoCampo + " in (select pj from PessoaJuridica pj where lower(pj.razaoSocial) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%'or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%')))";
                            } else if (dataTablePesquisaGenerico.getItemPesquisaGenerica().iseEnum()) {
                                condicao += alias + condicaoDoCampo + " = '" + valueDoCampo + "'";
                            } else if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().contains("cpf") || dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().contains("cnpj")) {
                                condicao += "(" + alias + condicaoDoCampo + " LIKE '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%' or (replace(replace(replace(" + alias + condicaoDoCampo + ",'.',''),'-',''),'/','')) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%')";
                            } else {
                                condicao += processaIntervalo(condicaoIsString, valueDoCampo, isString, isValor, isDate, dataTablePesquisaGenerico.getItemPesquisaGenerica().isStringExata());
                            }
                            if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getSubCondicao() != null) {
                                condicao += " and " + alias + dataTablePesquisaGenerico.getItemPesquisaGenerica().getSubCondicao();
                            }
                            condicao += " and ";
                        } catch (NullPointerException e) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar montar a condição de pesquisa!", ""));
                        }
                    }
                }
            }
        }
        condicao += " 1=1";
        return condicao;
    }

    public String processaIntervalo(String nomeCampo, String intervalo, boolean isString, boolean isValor, boolean isDate, boolean isStringExata) {
        if (isStringExata) {
            return nomeCampo + " = '" + intervalo + "'";
        }

        if ((!intervalo.contains(";") && !intervalo.contains("-")) || isString) {
            if (isString && !isValor) {
                return nomeCampo + " LIKE '%" + intervalo.toLowerCase() + "%'";
            }
            if (isValor) {
                return nomeCampo + " = " + intervalo + "";
            }
            if (isDate) {
                return nomeCampo + intervalo;
            }
            return nomeCampo + " LIKE '%" + intervalo + "%'";
        }
        if (isDate) {
            String[] pedacos = intervalo.split("-");
            return "trunc(" + nomeCampo + ") between " + pedacos[0] + " and " + pedacos[1];
        }

        String aspas = isString ? "'" : "";
        StringBuilder retorno = new StringBuilder("(");
        int lengthInicialRetorno = retorno.length();
        String[] pedacos = intervalo.split(";");
        List<String> pedacosTraco = new ArrayList<>();
        for (String pedaco : pedacos) {

            if (!pedaco.contains("-") && !"".equals(pedaco.trim())) {
                if (retorno.length() == lengthInicialRetorno) {
                    retorno.append(nomeCampo).append(" in (");
                }
                retorno.append(aspas).append(pedaco).append(aspas).append(",");
            } else {
                pedacosTraco.add(pedaco);
            }
        }
        if (retorno.length() != lengthInicialRetorno) {
            retorno.deleteCharAt(retorno.lastIndexOf(","));
            retorno.append(")");
        }
        for (String pedaco : pedacosTraco) {
            String[] pedacosDoTraco = pedaco.split("-");
            if (pedacosDoTraco.length >= 2) {
                if (retorno.length() != lengthInicialRetorno) {
                    retorno.append(" OR ");
                }
                retorno.append(nomeCampo).append(" BETWEEN ").append(aspas).append(pedacosDoTraco[0].toLowerCase()).append(aspas).append(" AND ").append(aspas).append(pedacosDoTraco[pedacosDoTraco.length - 1].toLowerCase()).append(aspas).append("");
            }
        }
        if (retorno.length() != lengthInicialRetorno) {
            retorno.append(")");
        }
        return retorno.toString();
    }

    public List<SelectItem> recuperaValuesBoolean(ItemPesquisaGenerica item) {
        List<SelectItem> retorno = new ArrayList<>();
        String labelVerdadeiro = "Sim";
        String labelFalso = "Não";
        if (item.getLabelBooleanFalse() != null) {
            if (!item.getLabelBooleanFalse().isEmpty()) {
                labelFalso = item.getLabelBooleanFalse();
            }
        }
        if (item.getLabelBooleanTrue() != null) {
            if (!item.getLabelBooleanTrue().isEmpty()) {
                labelVerdadeiro = item.getLabelBooleanTrue();
            }
        }
        retorno.add(new SelectItem("true", labelVerdadeiro));
        retorno.add(new SelectItem("false", labelFalso));
        return retorno;
    }

    public List<SelectItem> recuperaValuesEnum(ItemPesquisaGenerica item) {
        String nomeDaClasse = item.getTipo().toString();
        nomeDaClasse = nomeDaClasse.replace("class ", "");
        Class<?> classe = null;
        try {
            classe = Class.forName(nomeDaClasse);
        } catch (ClassNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro !", "Classe não encontrada : " + nomeDaClasse));
        }
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        int ordem = 0;
        for (Field field : classe.getDeclaredFields()) {
            if (field.isEnumConstant()) {
                String descricao = "";
                String name = field.getName();
                try {
                    Enum<?> valor = Enum.valueOf((Class<? extends Enum>) classe, name);
                    String nomeCampo = "descricao";
                    String methodName = "get" + nomeCampo.substring(0, 1).toUpperCase() + nomeCampo.substring(1);
                    Object valorRecuperado = valor.getClass().getMethod(methodName, new Class[]{}).invoke(valor, new Object[]{});
                    descricao = valorRecuperado.toString();
                } catch (Exception e) {
                    descricao = name;
                }
                if (item.iseEnumOrdinal()) {
                    retorno.add(new SelectItem(ordem, descricao));
                } else {
                    retorno.add(new SelectItem(name, descricao));
                }
                ordem++;
            }
        }
        if (!Mes.class.equals(item.getTipo())) {
            Collections.sort(retorno, new Comparator<SelectItem>() {
                @Override
                public int compare(SelectItem o1, SelectItem o2) {
                    return o1.getLabel().compareTo(o2.getLabel());
                }
            });
        }
        return retorno;
    }

    public boolean renderIsEnum(ItemPesquisaGenerica itemPesquisaGenerica) {
        if (itemPesquisaGenerica != null) {
            if (itemPesquisaGenerica.iseEnum()) {
                return true;
            }
        }
        return false;
    }

    public boolean renderIsDate(DataTablePesquisaGenerico item) {
        if (item != null) {
            try {
                if (item.getItemPesquisaGenerica().getTipo().equals(Date.class)) {

                    return true;
                }
            } catch (Exception e) {
            }

        }
        return false;
    }

    public boolean renderIsBoolean(DataTablePesquisaGenerico item) {
        if (item != null) {
            try {
                if (item.getItemPesquisaGenerica().getTipo().equals(Boolean.class)) {

                    return true;
                }
            } catch (Exception e) {
            }

        }
        return false;
    }

    public boolean renderIsBigDecimal(DataTablePesquisaGenerico item) {
        if (item != null) {
            try {
                if (item.getItemPesquisaGenerica().getTipo().equals(BigDecimal.class)) {

                    return true;
                }
            } catch (Exception e) {
            }

        }
        return false;
    }

    public boolean renderIsNumero(DataTablePesquisaGenerico item) {
        if (item != null) {
            try {
                if (item.getItemPesquisaGenerica().getTipo().equals(Long.class)
                    || item.getItemPesquisaGenerica().getTipo().equals(Integer.class)
                    || item.getItemPesquisaGenerica().getTipo().equals(Double.class)) {
                    return true;
                }
            } catch (Exception e) {
            }

        }
        return false;
    }

    public void setarFoco(DataTablePesquisaGenerico item, String clientId) {
        int indice = camposPesquisa.indexOf(item);
        String idCampo = "";
        if (renderIsBoolean(item)) {
            idCampo = "sorValorPesquisa";
        } else if (renderIsDate(item)) {
            idCampo = "itValorPesquisaDataInicial";
        } else if (renderIsEnum(item.getItemPesquisaGenerica())) {
            idCampo = "somValorPesquisa";
        } else if (renderIsBigDecimal(item)) {
            idCampo = "itValorPesquisa2";
        } else if (renderIsNumero(item)) {
            idCampo = "itValorPesquisa3";
        } else {
            idCampo = "itValorPesquisa";
        }
        FacesUtil.executaJavaScript("setaFoco('" + clientId + ":rpFiltrosPesquisa:" + indice + ":" + idCampo + "')");
    }

    public void alteraValorCampoOrdenacao(ItemPesquisaGenerica item) {
        item.setTipoOrdenacao("asc");
    }

    public void alteraValorCampo(DataTablePesquisaGenerico item) {
        if (renderIsDate(item)) {
            item.setValuePesquisa(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            item.setValeuPesquisaDataFim(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        } else if (renderIsBigDecimal(item)) {
            item.setValuePesquisa(String.valueOf(BigDecimal.ZERO));
        } else if (renderIsBoolean(item)) {
            item.setValuePesquisa("true");
        } else {
            item.setValuePesquisa("");
        }
    }

    public String actionVisualizar() {
        return "visualizar";
    }

    public String actionNovo() {
        return "edita";
    }

    private void recuperaIdComponente() {
        String[] split = idComponente.split(":");
        this.idComponente = idComponente.replace((split[split.length - 1]), "");
    }

    public void setaObjetoHistorico(Object objeto) {
        Web.getSessionMap().put("pagina-anterior-auditoria", PrettyContext.getCurrentInstance().getRequestURL().toString());
        FacesUtil.redirecionamentoInterno("/auditoria/listar-por-entidade/" + Persistencia.getId(objeto) + "/" + objeto.getClass().getSimpleName() + "/");
    }

    public List<Object[]> getListaHistoricoGeral() {
        return listaHistoricoGeral;
    }

    public void setListaHistoricoGeral(List<Object[]> listaHistoricoGeral) {
        this.listaHistoricoGeral = listaHistoricoGeral;
    }

    public String recuperarStringInsercao(Object[] obj) {

        String retorno = "";
        if (obj != null) {

            if (!tipoRevisao(obj).equals(RevisionType.ADD)) {
                return retorno;
            }
            String campo = obj[0].getClass().isAnnotationPresent(Etiqueta.class) ? obj[0].getClass().getAnnotation(Etiqueta.class).value() : obj[0].toString();
            retorno = "Inserção do(a) " + campo + ".<br />";
        }
        return retorno;
    }

    public String recuperarStringRemocao(Object[] obj) {

        String retorno = "";
        if (obj != null) {
            if (!tipoRevisao(obj).equals(RevisionType.ADD)) {
                return retorno;


            }
            String campo = obj[0].getClass().isAnnotationPresent(Etiqueta.class) ? obj[0].getClass().getAnnotation(Etiqueta.class).value() : obj[0].toString();
            retorno = "Removido o(a) " + campo + ".<br />";
        }
        return retorno;
    }

    private RevisionType tipoRevisao(Object[] tipo) {
        try {
            return (RevisionType) tipo[2];
        } catch (Exception e) {
            return null;
        }
    }

    public String recuperarStringAlteracao(Object[] obj) {
        if (obj[0] == null) {
            return "";
        }
        String retorno = "";
        if (!tipoRevisao(obj).equals(RevisionType.MOD)) {
            return retorno;
        }


        try {
            Object objAtual = obj[0];
            Object objAnterior = listaHistoricoGeral.get(listaHistoricoGeral.indexOf(obj) + 1)[0];
            for (Field field : Persistencia.getAtributos(objAtual.getClass())) {
                field.setAccessible(true);


                if (!field.isAnnotationPresent(Transient.class) && !field.getType().equals(List.class)) {
                    String nomeCampo = field.isAnnotationPresent(Etiqueta.class) ? field.getAnnotation(Etiqueta.class).value() : field.getName();
                    String de = recuperarDescricaoCampo(objAnterior, field);
                    String para = recuperarDescricaoCampo(objAtual, field);

                    String style = !de.equals(para) ? " font-weight: bold" : "";
                    retorno += "<tr style=\"" + style + "\">";
                    retorno += "<td></td>"; // Icone
                    retorno += "<td>" + nomeCampo + "</td>";
                    retorno += "<td>" + de + "</td>";
                    retorno += "<td>" + para + "</td>";
                    retorno += "</tr>";
                }
            }
            if (retorno.isEmpty()) {
                retorno += "<tr>";
                retorno += "<td>Nada foi alterado</td>";
                retorno += "</tr>";
            }
        } catch (Exception e) {
        }

        return retorno;
    }

    public String recuperarDescricaoCampo(Object obj, Field campo) {
        AtributoMetadata amd = new AtributoMetadata(campo);
        try {
            return amd.getString(obj);
        } catch (Exception e) {
            return "";
        }
    }

    public ComponentePesquisaGenerico recuperaEsteControloador() {
        return this;
    }

    public void editarConfiguracaoRelatorio() {
        filtrar();
    }

    public String visualizar() {
        return "visualizar";
    }

    public String preencherCampo(Object objeto, AtributoRelatorioGenerico atributo) {
        return facade.getRecuperadorFacade().preencherCampo(objeto, atributo);
    }

    public void novoRelatorioGenericoTabela() {
        tipoHierarquiaOrganizacional = TipoHierarquiaOrganizacional.ADMINISTRATIVA;
        try {
            Object o = classe.newInstance();
            EntidadeMetaData entidadeMetaData = new EntidadeMetaData(classe);
            relatorioTabela = facade.getRelatorioPesquisaGenericoFacade().recuperaRelatorioPorClasseTipoPadrao(classe.getName(), TipoRelatorio.TABELA);
            if (relatorioTabela == null) {
                relatorioTabela = new RelatorioPesquisaGenerico(o, entidadeMetaData);
                relatorioTabela.setEntidadeMetaData(entidadeMetaData);
            } else {
                relatorioTabela.setObjetoSelecionadoRelatorio(o);
                relatorioTabela.setEntidadeMetaData(entidadeMetaData);
            }
            List<CabecalhoRelatorio> completaCabecalhoRelatorio = completaCabecalhoRelatorio("");
            if (completaCabecalhoRelatorio != null && !completaCabecalhoRelatorio.isEmpty()) {
                relatorioTabela.setCabecalhoRelatorio(completaCabecalhoRelatorio.get(0));
            }

        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    public void setaObjetoRelatorioGenerico(Object objeto, String nomeDaTela) {
        relatorioPesquisaGenerico.setObjetoSelecionadoRelatorio(objeto);
        relatorioPesquisaGenerico.setEntidadeMetaData(new EntidadeMetaData(objeto.getClass()));
        setarNomeDaTelaAoRelatorio(relatorioPesquisaGenerico, nomeDaTela);
    }

    public void setaCabecalhoRelatorioParaVisualizacao(CabecalhoRelatorio cabecalhoRelatorio) {
        cabecalhoRelatorioVisualizacao = cabecalhoRelatorio;
    }

    public String mesclarTags() {
        if (cabecalhoRelatorioVisualizacao != null) {
            return facade.getCabecalhoRelatorioFacade().mesclaTags(facade.getCabecalhoRelatorioFacade().alteraURLImagens(cabecalhoRelatorioVisualizacao.getConteudo()));
        }
        return "";
    }

    public String mesclarTagsRelatorioTabela() {
        if (relatorioTabela != null) {
            if (relatorioTabela.getCabecalhoRelatorio() != null) {
                return facade.getCabecalhoRelatorioFacade().mesclaTags(facade.getCabecalhoRelatorioFacade().alteraURLImagens(relatorioTabela.getCabecalhoRelatorio().getConteudo()));
            }
        }
        return "";
    }

    public ConverterAutoComplete getConverterCabecalhoRelatorio() {
        if (converterCabecalhoRelatorio == null) {
            converterCabecalhoRelatorio = new ConverterAutoComplete(CabecalhoRelatorio.class, facade.getCabecalhoRelatorioFacade());
        }
        return converterCabecalhoRelatorio;
    }

    public ConverterAutoComplete getConverterRelatorioGenerico() {
        if (converterRelatorioGenerico == null) {
            converterRelatorioGenerico = new ConverterAutoComplete(RelatorioPesquisaGenerico.class, facade.getRelatorioPesquisaGenericoFacade());
        }
        return converterRelatorioGenerico;
    }

    public List<RelatorioPesquisaGenerico> completaRelatorioPesquisaGenerico(String parte) {
        return facade.getRelatorioPesquisaGenericoFacade().listaFiltrandoPorClasse(parte.trim(), classe.getName(), TipoRelatorio.TABELA);
    }

    public List<RelatorioPesquisaGenerico> listaRelatorioPesquisaGenericoTabela() {
        return facade.getRelatorioPesquisaGenericoFacade().listaFiltrandoPorClasse("", classe.getName(), TipoRelatorio.TABELA);
    }

    public List<RelatorioPesquisaGenerico> listaRelatorioPesquisaGenericoUnicoRegistro() {
        return facade.getRelatorioPesquisaGenericoFacade().listaFiltrandoPorClasse("", classe.getName(), TipoRelatorio.UNICO_REGISTRO);
    }

    public List<RelatorioPesquisaGenerico> completaRelatorioPesquisaGenericoUnicoRegistro(String parte) {
        return facade.getRelatorioPesquisaGenericoFacade().listaFiltrandoPorClasse(parte.trim(), classe.getName(), TipoRelatorio.UNICO_REGISTRO);
    }

    public ConverterAtributoMetaData getConverterAtributoMetaData() {
        if (converterAtributoMetaData == null) {
            converterAtributoMetaData = new ConverterAtributoMetaData();
        }
        return converterAtributoMetaData;
    }

    public ConverterAtributoMetaDataRelatorioTabela getConverterAtributoMetaDataRelatorioTabela() {
        if (converterAtributoMetaDataRelatorioTabela == null) {
            converterAtributoMetaDataRelatorioTabela = new ConverterAtributoMetaDataRelatorioTabela();
        }
        return converterAtributoMetaDataRelatorioTabela;
    }

    public void recuperaCabecalhoDaUnidadeLogadaRelatorioTabela() {
        try {
            relatorioTabela.setCabecalhoRelatorio(completaCabecalhoRelatorio("").get(0));
        } catch (Exception e) {
            relatorioTabela.setCabecalhoRelatorio(null);
        }
    }

    public void recuperaCabecalhoDaUnidadeLogada() {
        try {
            relatorioPesquisaGenerico.setCabecalhoRelatorio(completaCabecalhoRelatorio("").get(0));
        } catch (Exception e) {
            relatorioPesquisaGenerico.setCabecalhoRelatorio(null);
        }
    }

    public List<CabecalhoRelatorio> completaCabecalhoRelatorio(String parte) {
        try {
            if (tipoHierarquiaOrganizacional.equals(TipoHierarquiaOrganizacional.ADMINISTRATIVA)) {
                return facade.getCabecalhoRelatorioFacade().completaCabecalhoPorUnidade(parte.trim(), sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            } else {
                return facade.getCabecalhoRelatorioFacade().completaCabecalhoPorUnidade(parte.trim(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public boolean podeImprimir() {
        if (lista != null) {
            if (!lista.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void imprimirRelatorioAgrupador() {
        relatorioGenericoControlador.imprimirRelatorioAgrupador(lista, relatorioTabela);
    }

    public List<SelectItem> getEntidadesDoProjeto() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Class classe : facade.getRelatorioPesquisaGenericoFacade().getAllClass()) {
            retorno.add(new SelectItem(classe, Persistencia.getNomeDaClasse(classe)));
        }
        Collections.sort(retorno, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                SelectItem s1 = (SelectItem) o1;
                SelectItem s2 = (SelectItem) o2;

                return s1.getLabel().compareTo(s2.getLabel());
            }
        });
        return retorno;
    }

    private void setarNomeDaTelaAoRelatorio(RelatorioPesquisaGenerico relatorio, String nomeDaTela) {
        relatorio.setTitulo("Relatório de " + nomeDaTela);
        relatorio.setNomeRelatorio(nomeDaTela);
    }

    public void imprimirRelatorio(RelatorioPesquisaGenerico relatorio, String nomeDaTela) {
        setarNomeDaTelaAoRelatorio(relatorio, nomeDaTela);
        if (relatorio.getTipoRelatorio().equals(TipoRelatorio.TABELA) && relatorio.getImprimirTodosRegistros()) {
            List listaNova = null;
            try {
                String hql = getHqlConsultaRelatorioTodosRegistros();
                Object objeto = (Object) classe.newInstance();
                listaNova = facade.filtar(hql, objeto, 0, 0);
                relatorioGenericoControlador.imprimirRelatorio(listaNova, relatorio, true);
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar Pesquisar!", ex.getMessage()));
                logger.debug(ex.getMessage());
            }
        } else {
            relatorioGenericoControlador.imprimirRelatorio(lista, relatorio, true);
        }
    }

    public void atribuirRelatorioTabela(RelatorioPesquisaGenerico relatorioPesquisaGenerico) {
        this.relatorioTabela = facade.getRelatorioPesquisaGenericoFacade().recuperar(relatorioPesquisaGenerico.getId());
    }

    public void atribuirRelatorioUnicoRegistro(RelatorioPesquisaGenerico relatorioPesquisaGenerico) {
        this.relatorioPesquisaGenerico = facade.getRelatorioPesquisaGenericoFacade().recuperar(relatorioPesquisaGenerico.getId());
    }

    public boolean desabilitaBotaoSelecionarConsultaPersonalizada(RelatorioPesquisaGenerico relatorioPesquisaGenerico) {
        if (relatorioPesquisaGenerico != null && this.relatorioTabela != null) {
            if (relatorioPesquisaGenerico.equals(this.relatorioTabela)) {
                return true;
            }
        }
        return false;
    }

    public boolean desabilitaBotaoSelecionarConsultaPersonalizadaRelatorioUnicoRegistro(RelatorioPesquisaGenerico relatorioPesquisaGenerico) {
        if (relatorioPesquisaGenerico != null && this.relatorioPesquisaGenerico != null) {
            if (relatorioPesquisaGenerico.equals(this.relatorioPesquisaGenerico)) {
                return true;
            }
        }
        return false;
    }

    public void adicionarTodosObjetoDaLista() {
        for (Object o : lista) {
            adicionarObjetoNaLista(o);
        }
    }

    public void adicionarObjetoNaLista(Object o) {
        objetosMultiplaSelecao = Util.adicionarObjetoEmLista(objetosMultiplaSelecao, o);
    }

    public void removerObjetoDaLista(Object o) {
        objetosMultiplaSelecao.remove(o);
    }

    public boolean objetoEstaNaLista(Object o) {
        if (objetosMultiplaSelecao != null) {
            if (objetosMultiplaSelecao.contains(o)) {
                return true;
            }
        }
        return false;
    }

    public void atribuirObjetosMultiplaSelecao(List objetos) {
        objetosMultiplaSelecao = objetos;
    }

    public List getObjetosMultiplaSelecao() {
        return objetosMultiplaSelecao;
    }

    public void setObjetosMultiplaSelecao(List objetosMultiplaSelecao) {
        this.objetosMultiplaSelecao = objetosMultiplaSelecao;
    }

    public void alterarQuantidadeDeRegistrosPara(Long i) {
        this.maximoRegistrosTabela = i.intValue();
        this.inicio = 0;
        filtrar();
    }

    public void alterarQuantidadeDeRegistrosPara(Integer i) {
        this.maximoRegistrosTabela = i;
        this.inicio = 0;
        filtrar();
    }

    public void proximos() {
        inicio += maximoRegistrosTabela;
        filtrar();
    }

    public void anteriores() {
        inicio -= maximoRegistrosTabela;
        if (inicio < 0) {
            inicio = 0;
        }
        filtrar();
    }

    public boolean isTemMaisResultados() {
        if (lista == null) {
            filtrar();
        }
        return lista.size() >= maximoRegistrosTabela;
    }

    public boolean isTemAnterior() {
        return inicio > 0;
    }

    public void mostrarPrimeirosRegistros() {
        inicio = 0;
        filtrar();
    }

    public void mostrarUltimosRegistros() {
        inicio = getTotalDeRegistrosExistentes();
        inicio = inicio - maximoRegistrosTabela;
        Integer pgAtual = getPaginaAtual();
        if (totalDeRegistrosExistentes - (maximoRegistrosTabela * pgAtual) < maximoRegistrosTabela) {
            inicio = maximoRegistrosTabela * pgAtual;
        }

        filtrar();
    }

    public boolean desabilitarBotaoUtilmo() {
        return getTotalDeRegistrosExistentes() <= (inicio + maximoRegistrosTabela);
    }

    public boolean desabilitarBotaoPrimeiro() {
        return inicio <= 0;
    }

    public Integer getPaginaAtual() {
        Double inicialD = new Double("" + inicio);
        Double maximoD = new Double("" + maximoRegistrosTabela);
        Double totalD = new Double("" + getTotalDeRegistrosExistentes());

        Double pDivisao = totalD / maximoD;
        Double psoma = maximoD + inicialD;
        Double pMultiplicacao = pDivisao * psoma;
        pMultiplicacao = Math.ceil(pMultiplicacao);

        Double pResultado = pMultiplicacao / totalD;

        return pResultado.intValue();
    }

    public Integer getTotalDePaginas() {
        Double maximoD = new Double("" + maximoRegistrosTabela);
        Double totalD = new Double("" + getTotalDeRegistrosExistentes());
        Double totalDePaginas = Math.ceil(totalD / maximoD);

        return totalDePaginas.intValue();
    }

    public String botaoSelecionado(Integer i) {
        if (i == maximoRegistrosTabela) {
            return "botao-cabecalho-selecionado";
        }
        return "";
    }

    public Integer getTotalDeRegistrosExistentes() {
        if (totalDeRegistrosExistentes == null) {
            String hql = getHqlParaTotalDeRegistros();
            atribuirHqlConsultaRelatorioTodosRegistros();
            totalDeRegistrosExistentes = facade.count(hql).intValue();
        }
        return totalDeRegistrosExistentes;
    }

    public void setTotalDeRegistrosExistentes(Integer totalDeRegistrosExistentes) {
        this.totalDeRegistrosExistentes = totalDeRegistrosExistentes;
    }

    public String getHqlParaTotalDeRegistros() {
        return getHqlContador() + getComplementoQuery();
    }

    public int getInicio() {
        return inicio;
    }

    public void setInicio(int inicio) {
        this.inicio = inicio;
    }

    public void atualizarFinal() {
        String id = idComponente + "componentePesquisaGenerico";
        try {

            Object o = pegaControladorDaSessao();
            if (o != null) {
                FacesUtil.atualizarComponente(id);
            }
        } catch (Exception e) {

        }
        mostrarBotaoPesquisa = true;
    }

    public class ConverterItemPesquisaGenerico implements Converter, Serializable {

        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            try {
                for (ItemPesquisaGenerica itemPesquisaGenerica : itens) {
                    if (itemPesquisaGenerica.getCriadoEm().equals(Long.valueOf(value))) {
                        return itemPesquisaGenerica;
                    }
                }
            } catch (Exception ex) {
                logger.debug(ex.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Erro ao converter: " + value));
            }
            return value.toString();
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {
            if (value != null) {
                return String.valueOf(((ItemPesquisaGenerica) value).getCriadoEm());
            } else {
                return value.toString();
            }
        }
    }

    public class ConverterItemPesquisaGenericoOrdenacao implements Converter, Serializable {

        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            try {
                for (ItemPesquisaGenerica itemPesquisaGenerica : itensOrdenacao) {
                    String condicao = itemPesquisaGenerica.getCondicao();
                    if (condicao.equals(value)) {
                        return itemPesquisaGenerica.getCondicao();
                    }
                }
            } catch (Exception ex) {
                return "";
            }
            return "";
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {
            if (value != null) {
                return value.toString();
            } else {
                return value.toString();
            }
        }
    }

    public class ConverterAtributoMetaData implements Converter, Serializable {

        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            try {
                for (AtributoRelatorioGenerico atributo : relatorioPesquisaGenerico.getSource()) {
                    if (atributo.getCondicao().equals(value)) {
                        return atributo;
                    }
                }
                for (AtributoRelatorioGenerico atributo : relatorioPesquisaGenerico.getTarget()) {
                    if (atributo.getCondicao().equals(value)) {
                        return atributo;
                    }
                }
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Erro ao converter: " + value));
            }
            return value.toString();
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {
            if (value != null) {
                return String.valueOf(((AtributoRelatorioGenerico) value).getCondicao());
            } else {
                return value.toString();
            }
        }
    }

    public class ConverterAtributoMetaDataRelatorioTabela implements Converter, Serializable {

        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            try {
                for (AtributoRelatorioGenerico atributo : relatorioTabela.getSource()) {
                    if (atributo.getCondicao().equals(value)) {
                        return atributo;
                    }
                }
                for (AtributoRelatorioGenerico atributo : relatorioTabela.getTarget()) {
                    if (atributo.getCondicao().equals(value)) {
                        return atributo;
                    }
                }
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Erro ao converter: " + value));
            }
            return value.toString();
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {
            if (value != null) {
                return String.valueOf(((AtributoRelatorioGenerico) value).getCondicao());
            } else {
                return value.toString();
            }
        }
    }
}
