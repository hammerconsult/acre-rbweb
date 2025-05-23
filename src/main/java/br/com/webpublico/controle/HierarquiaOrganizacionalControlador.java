/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquia;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Felipe Marinzeck
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "inicioHierarquiaOrganizacional", pattern = "/hierarquia-organizacional/inicio/", viewId = "/faces/tributario/cadastromunicipal/hierarquiaorganizacional/inicio.xhtml"),

    // ADMINISTRATIVA
    @URLMapping(id = "verAdministrativaFiltro", pattern = "/hierarquia-organizacional/ver/administrativa/#{hierarquiaOrganizacionalControlador.dataDoFiltro}/", viewId = "/faces/tributario/cadastromunicipal/hierarquiaorganizacional/visualizar-administrativa.xhtml"),
    @URLMapping(id = "verAdministrativa", pattern = "/hierarquia-organizacional/ver/administrativa/", viewId = "/faces/tributario/cadastromunicipal/hierarquiaorganizacional/visualizar-administrativa.xhtml"),

    @URLMapping(id = "editarAdministrativaFiltro", pattern = "/hierarquia-organizacional/editar/administrativa/#{hierarquiaOrganizacionalControlador.dataDoFiltro}/", viewId = "/faces/tributario/cadastromunicipal/hierarquiaorganizacional/editar-administrativa.xhtml"),
    @URLMapping(id = "editarAdministrativa", pattern = "/hierarquia-organizacional/editar/administrativa/", viewId = "/faces/tributario/cadastromunicipal/hierarquiaorganizacional/editar-administrativa.xhtml"),


    // ORÇAMENTÁRIA
    @URLMapping(id = "verOrcamentariaFiltro", pattern = "/hierarquia-organizacional/ver/orcamentaria/#{hierarquiaOrganizacionalControlador.dataDoFiltro}/", viewId = "/faces/tributario/cadastromunicipal/hierarquiaorganizacional/visualizar-orcamentaria.xhtml"),
    @URLMapping(id = "verOrcamentaria", pattern = "/hierarquia-organizacional/ver/orcamentaria/", viewId = "/faces/tributario/cadastromunicipal/hierarquiaorganizacional/visualizar-orcamentaria.xhtml"),

    @URLMapping(id = "editarOrcamentariaFiltro", pattern = "/hierarquia-organizacional/editar/orcamentaria/#{hierarquiaOrganizacionalControlador.dataDoFiltro}/", viewId = "/faces/tributario/cadastromunicipal/hierarquiaorganizacional/editar-orcamentaria.xhtml"),
    @URLMapping(id = "editarOrcamentaria", pattern = "/hierarquia-organizacional/editar/orcamentaria/", viewId = "/faces/tributario/cadastromunicipal/hierarquiaorganizacional/editar-orcamentaria.xhtml"),
})
public class HierarquiaOrganizacionalControlador extends SuperControladorCRUD implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(HierarquiaOrganizacionalControlador.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private static final String SIM = "SIM";
    private static final String NAO = "NÃO";
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ProvisaoPPADespesaFacade provisaoPPADespesaFacade;
    @EJB
    private AlteracaoORCFacade alteracaoORCFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private PrestadorServicosFacade prestadorServicosFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private List<HierarquiaOrganizacional> listaHierarquiaOrganizacionalTipo;
    private TipoHierarquiaOrganizacional tipoTemp;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private ConverterAutoComplete converteConta;
    private TreeNode rootAdm;
    private TreeNode rootOrc;
    private MoneyConverter moneyConverter;
    private HierarquiaOrganizacional hierarquiaPaiSelecionada;
    private HierarquiaOrganizacional hierarquiaSelecionada;
    private HierarquiaOrganizacionalCorrespondente hierarquiaOrganizacionalCorrespondente;
    private HierarquiaOrganizacionalResponsavel hierarquiaOrganizacionalResponsavel;
    private List<Mascara> mascaraQuebrada;
    private Integer indiceTabView;
    private String codigo;
    private PrevisaoHOContaDestinacao previsaoHOContaDestinacao;
    private List<PrevisaoHOContaDestinacao> listaPrevisaoHoConta = new ArrayList<>();
    private List<PrevisaoHOContaDestinacao> listaRemovidosPrevisaoHoConta = new ArrayList<>();
    private Date dataFiltro;
    @EJB
    private SingletonHierarquiaOrganizacional singletonHO;
    private String dataDoFiltro;
    private HierarquiaOrganizacionalDataModel lazyHierarquiaOrganizacionalDataModel;
    @EJB
    private RecuperadorFacade recuperadorFacade;

    public HierarquiaOrganizacionalDataModel getLazyHierarquiaOrganizacionalDataModel() {
        if (lazyHierarquiaOrganizacionalDataModel == null) {
            lazyHierarquiaOrganizacionalDataModel = new HierarquiaOrganizacionalDataModel(UtilRH.getDataOperacao(), tipoTemp, recuperadorFacade);
        }
        return lazyHierarquiaOrganizacionalDataModel;
    }

    public void setLazyHierarquiaOrganizacionalDataModel(HierarquiaOrganizacionalDataModel lazyHierarquiaOrganizacionalDataModel) {
        this.lazyHierarquiaOrganizacionalDataModel = lazyHierarquiaOrganizacionalDataModel;
    }

    public String getDataDoFiltro() {
        return dataDoFiltro;
    }

    public void setDataDoFiltro(String dataDoFiltro) {
        this.dataDoFiltro = dataDoFiltro;
    }

    public PrevisaoHOContaDestinacao getPrevisaoHOContaDestinacao() {
        return previsaoHOContaDestinacao;
    }

    public void setPrevisaoHOContaDestinacao(PrevisaoHOContaDestinacao previsaoHOContaDestinacao) {
        this.previsaoHOContaDestinacao = previsaoHOContaDestinacao;
    }

    public HierarquiaOrganizacionalCorrespondente getHierarquiaOrganizacionalCorrespondente() {
        return hierarquiaOrganizacionalCorrespondente;
    }

    public void setHierarquiaOrganizacionalCorrespondente(HierarquiaOrganizacionalCorrespondente hierarquiaOrganizacionalCorrespondente) {
        this.hierarquiaOrganizacionalCorrespondente = hierarquiaOrganizacionalCorrespondente;
    }

    @Override
    public AbstractFacade getFacede() {
        return hierarquiaOrganizacionalFacade;
    }

    public List<PrevisaoHOContaDestinacao> getListaPrevisaoHoConta() {
        return listaPrevisaoHoConta;
    }

    public void setListaPrevisaoHoConta(List<PrevisaoHOContaDestinacao> listaPrevisaoHoConta) {
        this.listaPrevisaoHoConta = listaPrevisaoHoConta;
    }

    public TreeNode getRootOrc() {
        return rootOrc;
    }

    public void setRootOrc(TreeNode rootOrc) {
        this.rootOrc = rootOrc;
    }

    public Integer getIndiceTabView() {
        return indiceTabView;
    }

    public void setIndiceTabView(Integer indiceTabView) {
        this.indiceTabView = indiceTabView;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public void setHierarquiaOrganizacionalFacade(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        this.hierarquiaOrganizacionalFacade = hierarquiaOrganizacionalFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public void setUnidadeOrganizacionalFacade(UnidadeOrganizacionalFacade unidadeOrganizacionalFacade) {
        this.unidadeOrganizacionalFacade = unidadeOrganizacionalFacade;
    }

    public TipoHierarquiaOrganizacional getTipoTemp() {
        return tipoTemp;
    }

    public void setTipoTemp(TipoHierarquiaOrganizacional tipoTemp) {
        this.tipoTemp = tipoTemp;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public TreeNode getRootAdm() {
        return rootAdm;
    }

    public void setRootAdm(TreeNode rootAdm) {
        this.rootAdm = rootAdm;
    }

    public Date getDataFiltro() {
        return dataFiltro;
    }

    public void setDataFiltro(Date dataFiltro) {
        this.dataFiltro = dataFiltro;
    }

    public MoneyConverter getMoneyConverter() {
        return moneyConverter;
    }

    public void setMoneyConverter(MoneyConverter moneyConverter) {
        this.moneyConverter = moneyConverter;
    }

    public List<Mascara> getMascaraQuebrada() {
        return mascaraQuebrada;
    }

    public void setMascaraQuebrada(List<Mascara> mascaraQuebrada) {
        this.mascaraQuebrada = mascaraQuebrada;
    }

    public ConverterAutoComplete getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(UnidadeOrganizacional.class, unidadeOrganizacionalFacade);
        }
        return converterUnidadeOrganizacional;
    }

    public void setConverterUnidadeOrganizacional(ConverterAutoComplete converterUnidadeOrganizacional) {
        this.converterUnidadeOrganizacional = converterUnidadeOrganizacional;
    }

    public ConverterAutoComplete getConverterContaDestinacao() {
        if (converteConta == null) {
            converteConta = new ConverterAutoComplete(Conta.class, hierarquiaOrganizacionalFacade.getContaFacade());
        }
        return converteConta;
    }

    public List<Conta> completaContaDestinacao(String parte) {
        return hierarquiaOrganizacionalFacade.getContaFacade().listaFiltrandoDestinacaoDeRecursos(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<HierarquiaOrganizacional> getListaHierarquiaOrganizacionalTipo() {
        return listaHierarquiaOrganizacionalTipo;
    }

    public void setListaHierarquiaOrganizacionalTipo(List<HierarquiaOrganizacional> listaHierarquiaOrganizacionalTipo) {
        this.listaHierarquiaOrganizacionalTipo = listaHierarquiaOrganizacionalTipo;
    }

    public HierarquiaOrganizacional getHierarquiaPaiSelecionada() {
        return hierarquiaPaiSelecionada;
    }

    public void setHierarquiaPaiSelecionada(HierarquiaOrganizacional hierarquiaPaiSelecionada) {
        this.hierarquiaPaiSelecionada = hierarquiaPaiSelecionada;
    }

    public HierarquiaOrganizacional getHierarquiaSelecionada() {
        return hierarquiaSelecionada;
    }

    public void setHierarquiaSelecionada(HierarquiaOrganizacional hierarquiaSelecionada) {
        this.hierarquiaSelecionada = hierarquiaSelecionada;
    }


    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<UnidadeOrganizacional> completaUnidadeOrganizacionalDisponivel(String parte) {
        return unidadeOrganizacionalFacade.buscarUnidadesOrganizacionaisNaoVinculadasValidas(tipoTemp, sistemaControlador.getDataOperacao(), parte.trim());
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaOrcamentaria(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaControlador.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> completarHierarquiasOrcamentarias(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte.trim(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaControlador.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalAdministrativa(String parte) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), sistemaControlador.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalAdministrativaOndeUsuarioLogadoEhGestorPatrimonio(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaOrganizacionalAdministrativaDoUsuario(parte.trim(), null, sistemaControlador.getDataOperacao(), sistemaControlador.getUsuarioCorrente(), null, Boolean.TRUE);
    }


    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalOrgaoComRais(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaOrganizacionalAdministrativaOndeUsuarioLogadoEhGestorDeLicitacao(String codigoOrDescricao) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaOrganizacionalAdministrativaPorCodigoOrDescricaoAndNivelAndDataAndUsuarioCorrenteAndGestorLicitacao(codigoOrDescricao,
            null, sistemaControlador.getDataOperacao(), sistemaControlador.getUsuarioCorrente());
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalOrcamentariaOndeUsuarioLogadoEhGestorPatrimonio(String parte) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(parte, null, sistemaControlador.getDataOperacao(), sistemaControlador.getUsuarioCorrente(), null);
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalPaiEFilhoOndeUsuarioLogadoEhGestorPatrimonio(String parte) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPaiEFilhoOndeUsuarioEhGestorPatrimonio(parte, null, sistemaControlador.getUsuarioCorrente(), sistemaControlador.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalNivelDoisPaiEFilhoOndeUsuarioLogadoEhGestorPatrimonio(String parte) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPaiEFilhoOndeUsuarioEhGestorPatrimonio(parte, "2", sistemaControlador.getUsuarioCorrente(), sistemaControlador.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalAdministrativaOndeUsuarioLogadoEhGestorMateriais(String parte) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalAdministrativaOndeUsuarioEhGestorMaterial(parte, null, sistemaControlador.getDataOperacao(), sistemaControlador.getUsuarioCorrente());
    }

    public ConverterAutoComplete getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public void setConverterHierarquiaOrganizacional(ConverterAutoComplete converterHierarquiaOrganizacional) {
        this.converterHierarquiaOrganizacional = converterHierarquiaOrganizacional;
    }

    public List<SelectItem> getTiposAdministrativo() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoHierarquia t : TipoHierarquia.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public void validaHierarquiaPossuiFilhos(ValidacaoException ve) {
        List<HierarquiaOrganizacional> listaDeFilhos = hierarquiaOrganizacionalFacade.getHierarquiasFilhasDe(hierarquiaSelecionada.getSubordinada(), sistemaControlador.getDataOperacao(), hierarquiaSelecionada.getTipoHierarquiaOrganizacional());
        if (!listaDeFilhos.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Não foi possível excluir/encerrar vigência da Hierarquia " + hierarquiaSelecionada.getCodigo() + ". Ela possui " + listaDeFilhos.size() + " filhos relacionados e a ação não poderá ser continuada.");
        }
    }

    public void verificarUnidadeVigenteEmLotacaoOrVinculoOrFolha() {
        if(hierarquiaSelecionada != null ? hierarquiaOrganizacionalFacade.isUnidadeVigenteEmLotacaoOrVinculoOrFolha(hierarquiaSelecionada.getSubordinada(), hierarquiaSelecionada.getFimVigencia()) : false) {
            FacesUtil.addWarn("Atenção","Existem registros de lotação funcional vigentes ou fichas financeiras vinculadas a esta hierarquia posteriores à data de final de vigência escolhida. O encerramento pode ocasionar divergências em relatórios e/ou arquivos.");
        }
    }

    private boolean validaSalvar(HierarquiaOrganizacional ho) {
        if (hierarquiaOrganizacionalFacade.existeHierarquiaOrganizacionalVigenteComCodigo(ho, dataFiltro)) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O código informado já existe em uma hierarquia vigente na data de busca utilizada '<b>" + Util.formatterDiaMesAno.format(dataFiltro) + "</b>'");
            return false;
        }
        return true;
    }

    private boolean validaCamposObrigatorios() {
        boolean deuCerto = true;
        if (hierarquiaSelecionada.getSubordinada() == null) {
            addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Unidade Organizacional deve ser informado.");
            deuCerto = false;
        }
//        if (tipoTemp.equals(TipoHierarquiaOrganizacional.ADMINISTRATIVA)) {
//            if (hierarquiaSelecionada.getHierarquiaOrganizacionalOrcamentaria() == null) {
//                addError("Não foi possível Salvar a Hierarquia Organizacional.", "O Campo Obrigatório Hierarquia Orçamentaria Responsavel não foi informado.");
//                deuCerto = false;
//            }
//        }
        return deuCerto;
    }

    public String codigoCompleto() {
        String toReturn = "";
        for (Mascara m : mascaraQuebrada) {
            if (m.indice == hierarquiaSelecionada.getIndiceDoNo() - 1) {
                toReturn = toReturn + codigo + ".";
            } else {
                toReturn = toReturn + m.getCodigo() + ".";
            }
        }
        return toReturn.substring(0, toReturn.length() - 1);
    }

    public void recuperarMascaraQuebrada() {
        mascaraQuebrada = new ArrayList<>();
        ConfiguracaoHierarquiaOrganizacional configuracaoHierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getConfiguracaoHierarquiaOrganizacionalFacade().configuracaoVigente(tipoTemp);
        if (configuracaoHierarquiaOrganizacional == null) {
            FacesUtil.addAtencao("Não foi encontrado configuração de hierarquia organizacional para o tipo " + tipoTemp.getDescricao() + ".");
        }
        String regex = "\\.";
        if (hierarquiaPaiSelecionada == null) {
            String[] parte = configuracaoHierarquiaOrganizacional.getMascara().split(regex);
            for (int x = 0; x < parte.length; x++) {
                String codigo = parte[x];
                codigo = codigo.replace("#", "0");
                mascaraQuebrada.add(new Mascara(codigo, x));

            }
        } else {
            String[] parte = hierarquiaSelecionada.getCodigo().split(regex);
            for (int x = 0; x < parte.length; x++) {
                mascaraQuebrada.add(new Mascara(parte[x], x));
            }
        }

        if (hierarquiaSelecionada != null && hierarquiaSelecionada.getCodigo() != null) {
            codigo = getStringNo(hierarquiaSelecionada.getIndiceDoNo() == null ? 1 : hierarquiaSelecionada.getIndiceDoNo() - 1);
        } else {
            codigo = mascaraQuebrada.get(0).getCodigo();
        }
    }

    public int getNiveis() {
        return hierarquiaOrganizacionalFacade.niveisHierarquia(tipoTemp);
    }

    public void inicializaHierarquiaSelecionada() {
        hierarquiaSelecionada = new HierarquiaOrganizacional();
        hierarquiaPaiSelecionada = new HierarquiaOrganizacional();
    }

    public void cancelarHierarquiaSelecionada() {
        hierarquiaSelecionada = null;
        hierarquiaPaiSelecionada = null;
        hierarquiaOrganizacionalCorrespondente = null;
        hierarquiaOrganizacionalResponsavel = null;
    }

    public void adicionarHoFilha(HierarquiaOrganizacional ho) {
        operacao = Operacoes.NOVO;
        hierarquiaPaiSelecionada = ho;
        hierarquiaSelecionada = new HierarquiaOrganizacional();
        hierarquiaSelecionada.setCodigo(ho.getCodigo());
        hierarquiaSelecionada.setTipoHierarquiaOrganizacional(tipoTemp);
        hierarquiaSelecionada.setInicioVigencia(sistemaControlador.getDataOperacao());
        hierarquiaSelecionada.setSuperior(hierarquiaPaiSelecionada.getSubordinada());
        hierarquiaSelecionada.setIndiceDoNo(hierarquiaPaiSelecionada.getIndiceDoNo() + 1);
        hierarquiaSelecionada.setExercicio(sistemaControlador.getExercicioCorrente());
        hierarquiaSelecionada.setCodigoNo(hierarquiaPaiSelecionada.getIndiceDoNo().toString());
        if (hierarquiaPaiSelecionada.getIndiceDoNo() >= getNiveis()) {
            addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A Hierarquia: " + hierarquiaPaiSelecionada + " não pode ser desdobrada. O nível <b>" + hierarquiaPaiSelecionada.getIndiceDoNo() + "</b> é o maior possivel.");
            return;
        }
        recuperarMascaraQuebrada();
        setarFocoCodigo();
    }

    public void instanciarCorrespondente() {
        hierarquiaOrganizacionalCorrespondente = new HierarquiaOrganizacionalCorrespondente();
        if (hierarquiaSelecionada.getHierarquiaOrganizacionalCorrespondentes() == null) {
            hierarquiaSelecionada.setHierarquiaOrganizacionalCorrespondentes(new ArrayList<HierarquiaOrganizacionalCorrespondente>());
        }
    }

    public void setarNullCorrespondente() {
        hierarquiaOrganizacionalCorrespondente = null;
    }


    public void setarNullResponsavel() {
        hierarquiaOrganizacionalResponsavel = null;
    }

    public void adicionaCorrespondente() {
        if (hierarquiaOrganizacionalCorrespondente.getHierarquiaOrganizacionalAdm() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Hierarquia Orçamentária Correspondente deve ser informado.");
            return;
        }
        hierarquiaOrganizacionalCorrespondente.setHierarquiaOrganizacionalOrc(hierarquiaSelecionada);
        if (!hierarquiaSelecionada.getHierarquiaOrganizacionalCorrespondentes().contains(hierarquiaOrganizacionalCorrespondente)) {
            hierarquiaSelecionada.getHierarquiaOrganizacionalCorrespondentes().add(hierarquiaOrganizacionalCorrespondente);
        }
        instanciarCorrespondente();
        hierarquiaOrganizacionalCorrespondente.setDataInicio(sistemaControlador.getDataOperacao());
    }

    public void adicionaResponsavel() {
        if (hierarquiaOrganizacionalResponsavel.getHierarquiaOrganizacionalOrc() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Hierarquia Orçamentária Responsável deve ser informado.");
            return;
        }
        hierarquiaOrganizacionalResponsavel.setHierarquiaOrganizacionalAdm(hierarquiaSelecionada);
        if (!hierarquiaSelecionada.getHierarquiaOrganizacionalResponsavels().contains(hierarquiaOrganizacionalResponsavel)) {
            hierarquiaSelecionada.getHierarquiaOrganizacionalResponsavels().add(hierarquiaOrganizacionalResponsavel);
        }
        instanciarResponsavel();
        hierarquiaOrganizacionalResponsavel.setDataInicio(sistemaControlador.getDataOperacao());
    }

    public void removeHierarquiaCorre(HierarquiaOrganizacionalCorrespondente hoc) {
        hierarquiaSelecionada.getHierarquiaOrganizacionalCorrespondentes().remove(hoc);
    }

    public void removeHierarquiaResp(HierarquiaOrganizacionalResponsavel hoc) {
        hierarquiaSelecionada.getHierarquiaOrganizacionalResponsavels().remove(hoc);
    }

    public void alterarHierarquia(HierarquiaOrganizacional ho) {
        try {
            hierarquiaPaiSelecionada = hierarquiaOrganizacionalFacade.getPaiDe(ho, dataFiltro);
            hierarquiaSelecionada = hierarquiaOrganizacionalFacade.recuperar(ho);
            recuperarMascaraQuebrada();
            setarFocoCodigo();
        } catch (ExcecaoNegocioGenerica e) {
            addError("Erro ao recupera a Hierarquia Organizacional superior.", e.getMessage());
        }
    }

    private void setarFocoCodigo() {
        FacesUtil.executaJavaScript("setaFoco('Formulario:codigo-digitado-" + (hierarquiaSelecionada.getIndiceDoNo() - 1) + "')");
    }

    public void instanciarPrevisao() {
        if (isOrcamentaria()) {
            previsaoHOContaDestinacao = new PrevisaoHOContaDestinacao();
            previsaoHOContaDestinacao.setUnidadeOrganizacional(hierarquiaSelecionada.getSubordinada());
            previsaoHOContaDestinacao.setExercicio(sistemaControlador.getExercicioCorrente());
            listaPrevisaoHoConta = new ArrayList<>();
            listaRemovidosPrevisaoHoConta = new ArrayList<>();
            listaPrevisaoHoConta = new ArrayList<>();
        }
    }

    private boolean isOrcamentaria() {
        return TipoHierarquiaOrganizacional.ORCAMENTARIA.equals(tipoTemp);
    }

    public void instanciarResponsavel() {
        hierarquiaOrganizacionalResponsavel = new HierarquiaOrganizacionalResponsavel();
        if (hierarquiaSelecionada.getHierarquiaOrganizacionalResponsavels() == null) {
            hierarquiaSelecionada.setHierarquiaOrganizacionalResponsavels(new ArrayList<HierarquiaOrganizacionalResponsavel>());
        }
    }

    public void criarNovaRaiz() {
        hierarquiaPaiSelecionada = null;
        hierarquiaSelecionada = new HierarquiaOrganizacional();
        hierarquiaSelecionada.setTipoHierarquiaOrganizacional(tipoTemp);
        hierarquiaSelecionada.setInicioVigencia(dataFiltro);
        hierarquiaSelecionada.setIndiceDoNo(1);
        hierarquiaSelecionada.setSuperior(null);
        hierarquiaSelecionada.setExercicio(sistemaControlador.getExercicioCorrente());

        recuperarMascaraQuebrada();
        setarFocoCodigo();
    }

    public void alteraHierarquiaCorre(HierarquiaOrganizacionalCorrespondente hoc) {
        hierarquiaOrganizacionalCorrespondente = hoc;
    }

    public void alteraHierarquiaResp(HierarquiaOrganizacionalResponsavel hoc) {
        hierarquiaOrganizacionalResponsavel = hoc;
    }

    public void dialogPrevisaoHOContaDestinacao() {
        try {
            instanciarPrevisao();
            listaPrevisaoHoConta = hierarquiaOrganizacionalFacade.recuperarPrevisaoHOContaDestinacao(hierarquiaSelecionada.getSubordinada(), sistemaControlador.getExercicioCorrente());
        } catch (Exception e) {
            logger.error(e.getMessage());
            addError("Erro ao recuperar Unidade Organizacional ", e.getMessage());
        }
    }

    public void addPrevisaoHOContaDestinacao() {
        if (verificaPrevisaoHOContaDest()) {
            UnidadeOrganizacional uni = hierarquiaSelecionada.getSubordinada();
            previsaoHOContaDestinacao.setUnidadeOrganizacional(uni);
            previsaoHOContaDestinacao.setExercicio(sistemaControlador.getExercicioCorrente());
            listaPrevisaoHoConta = Util.adicionarObjetoEmLista(listaPrevisaoHoConta, previsaoHOContaDestinacao);
            previsaoHOContaDestinacao = new PrevisaoHOContaDestinacao();
            previsaoHOContaDestinacao.setUnidadeOrganizacional(hierarquiaSelecionada.getSubordinada());
        }
    }

    public void salvarPrevisoes() {
        hierarquiaSelecionada.getSubordinada().setPrevisaoHOContaDestinacao(listaPrevisaoHoConta);
        hierarquiaOrganizacionalFacade.persistePrevisoesHOContaDestinacao(hierarquiaSelecionada.getSubordinada());
        FacesUtil.executaJavaScript("dialog.hide()");
        FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Previsão(ões) salva com sucesso.");
    }

    public BigDecimal somaValoresTabela() {
        BigDecimal soma = BigDecimal.ZERO;
        for (PrevisaoHOContaDestinacao prev : listaPrevisaoHoConta) {
            soma = soma.add(prev.getValor());
        }
        hierarquiaSelecionada.setValorEstimado(soma);
        return soma;
    }

    public Boolean verificaPrevisaoHOContaDest() {
        boolean controle = true;
        if (previsaoHOContaDestinacao.getConta() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Fonte de Recursos deve ser informado");
            controle = false;
        }
        if (previsaoHOContaDestinacao.getValor().compareTo(BigDecimal.ZERO) == 0) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Valor deve ser maior que zero (0)");
            controle = false;
        }
        for (PrevisaoHOContaDestinacao p : listaPrevisaoHoConta) {
            if (!p.equals(previsaoHOContaDestinacao) && p.getConta().equals(previsaoHOContaDestinacao.getConta()) && p.getExercicio().getId().equals(previsaoHOContaDestinacao.getExercicio().getId())) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A Conta de Destinação: " + previsaoHOContaDestinacao.getConta() + " já existe na tabela para o exercício de " + p.getExercicio().getAno());
                controle = false;
            }
        }

        if (previsaoHOContaDestinacao.getId() != null) {
            if (controle) {
                BigDecimal valorDasPrevisoesLancadas = provisaoPPADespesaFacade.retornaValorFontesPorExercicioUnidadeConta(previsaoHOContaDestinacao.getExercicio(), previsaoHOContaDestinacao.getConta(), previsaoHOContaDestinacao.getUnidadeOrganizacional());
                if (previsaoHOContaDestinacao.getValor().compareTo(valorDasPrevisoesLancadas) < 0) {
                    controle = false;

                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O valor " + Util.formataValor(previsaoHOContaDestinacao.getValor()) + " deve ser maior que " + Util.formataValor(valorDasPrevisoesLancadas) + ", pois já foram lançados Fixações de Despesa neste valor.");
                }
            }

        }
        return controle;
    }

    public Boolean isGestorOrcamento() {
        if (previsaoHOContaDestinacao.getUnidadeOrganizacional() != null) {
            return hierarquiaOrganizacionalFacade.isGestorOrcamento(sistemaControlador.getUsuarioCorrente(), previsaoHOContaDestinacao.getUnidadeOrganizacional(), sistemaControlador.getDataOperacao());
        }
        return false;
    }

    public void alterarPrevisaoHOContaDest(ActionEvent evento) {
        previsaoHOContaDestinacao = (PrevisaoHOContaDestinacao) evento.getComponent().getAttributes().get("c");
    }

    public void removePrevisaoHOContaDest(ActionEvent evento) {
        PrevisaoHOContaDestinacao p = (PrevisaoHOContaDestinacao) evento.getComponent().getAttributes().get("c");
        try {

            hierarquiaOrganizacionalFacade.validaRemocaoPrevisaoHOContaDestinacao(p);
            listaRemovidosPrevisaoHoConta.add(p);
            listaPrevisaoHoConta.remove(p);

        } catch (ExcecaoNegocioGenerica en) {
            addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), en.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            addError(e.getMessage(), " ");
        }
    }

    @Override
    public void novo() {
        operacao = Operacoes.NOVO;
        previsaoHOContaDestinacao = new PrevisaoHOContaDestinacao();
        listaHierarquiaOrganizacionalTipo = new ArrayList<>();
        hierarquiaPaiSelecionada = new HierarquiaOrganizacional();
        hierarquiaSelecionada = new HierarquiaOrganizacional();
        hierarquiaSelecionada.setTipoHierarquiaOrganizacional(tipoTemp);

        ConfiguracaoHierarquiaOrganizacional conf = hierarquiaOrganizacionalFacade.getConfiguracaoHierarquiaOrganizacionalFacade().configuracaoVigente(tipoTemp);
        hierarquiaSelecionada.setCodigo(conf.getMascara().replace("#", "0"));
        hierarquiaSelecionada.setIndiceDoNo(1);
        hierarquiaSelecionada.setInicioVigencia(sistemaControlador.getDataOperacao());
        hierarquiaSelecionada.setNivelNaEntidade(1);
        hierarquiaSelecionada.setExercicio(sistemaControlador.getExercicioCorrente());
        hierarquiaSelecionada.setCodigoNo(getStringNo(hierarquiaSelecionada.getIndiceDoNo() - 1));
        recuperarMascaraQuebrada();
    }

    //    @URLAction(mappingId = "editarHierarquiaOrganizacionalAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editaAdm() {
        listaHierarquiaOrganizacionalTipo = hierarquiaOrganizacionalFacade.getHierarquiasOrganizacionaisPorTipoVigente(sistemaControlador.getDataOperacao(), tipoTemp);
    }

    //    @URLAction(mappingId = "editarHierarquiaOrganizacionalOrcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editaOrc() {
        listaHierarquiaOrganizacionalTipo = hierarquiaOrganizacionalFacade.getHierarquiasOrganizacionaisPorTipoVigente(sistemaControlador.getDataOperacao(), tipoTemp);
    }

    public void encerrarVigencia() {
        try {
            validaEncerrarVigencia();
            salvarHO(hierarquiaSelecionada);
            addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Vigência encerrada com sucesso na data: " + new SimpleDateFormat("dd/MM/yyyy").format(hierarquiaSelecionada.getFimVigencia()));
            hierarquiaSelecionada = new HierarquiaOrganizacional();
            hierarquiaPaiSelecionada = new HierarquiaOrganizacional();
            FacesUtil.executaJavaScript("encerrar.hide()");
            cancelarHierarquiaSelecionada();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public void validaEncerrarVigencia() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaSelecionada.getFimVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Fim de Vigência deve ser informado.");
        }
        validaRegistrosVigentesRHVinculados(ve);
        validaHierarquiaPossuiFilhos(ve);
        ve.lancarException();
    }

    public void validaRegistrosVigentesRHVinculados(ValidacaoException ve) {
        if (hierarquiaSelecionada.getFimVigencia() != null) {
            boolean existeVinculoFPVigenteNaData = vinculoFPFacade.existeVinculoFPVigenteNaData(hierarquiaSelecionada.getFimVigencia(), hierarquiaSelecionada);
            boolean existePrestadorServicoVigenteNaData = prestadorServicosFacade.existePrestadorServicoVigenteNaData(hierarquiaSelecionada.getFimVigencia(), hierarquiaSelecionada);
            boolean existeLotacaoFuncionalVigenteNaData = lotacaoFuncionalFacade.existeLotacaoFuncionalVigenteNaData(hierarquiaSelecionada.getFimVigencia(), hierarquiaSelecionada);
            if (existeVinculoFPVigenteNaData || existePrestadorServicoVigenteNaData || existeLotacaoFuncionalVigenteNaData) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existem registros no módulo de RH vigentes vinculados à hierarquia organizacional administrativa. Esses registros deverão ser transferidos para permitir o encerramento.");
            }
        }
    }


    public void salvarNo() {
        hierarquiaSelecionada.setCodigo(codigoCompleto());
        if (hierarquiaPaiSelecionada != null) {
            if (hierarquiaPaiSelecionada.getIndiceDoNo() != null) {
                hierarquiaSelecionada.setIndiceDoNo(hierarquiaPaiSelecionada.getIndiceDoNo() + 1);
            }
        } else {
            hierarquiaSelecionada.setIndiceDoNo(1);
        }
//        hierarquiaSelecionada.setIndiceDoNo(hierarquiaPaiSelecionada == null ? 1 : (hierarquiaPaiSelecionada.getIndiceDoNo() + 1));
        hierarquiaSelecionada.setCodigoNo(getStringNo(hierarquiaSelecionada.getIndiceDoNo() - 1));

        if (!validaCamposObrigatorios()) {
            return;
        }

        if (hierarquiaPaiSelecionada != null) {
            hierarquiaSelecionada.setSuperior(hierarquiaPaiSelecionada.getSubordinada());
        }
        if (validaSalvar(hierarquiaSelecionada)) {
            try {
                hierarquiaSelecionada.getSubordinada().setPrevisaoHOContaDestinacao(listaPrevisaoHoConta);
                salvarHO(hierarquiaSelecionada);
                listaHierarquiaOrganizacionalTipo = Util.adicionarObjetoEmLista(listaHierarquiaOrganizacionalTipo, hierarquiaSelecionada);
                hierarquiaSelecionada = new HierarquiaOrganizacional();
                hierarquiaPaiSelecionada = new HierarquiaOrganizacional();
                ordenaListaHierarquia();
                cancelarHierarquiaSelecionada();
            } catch (Exception e) {
                addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
            }
        }
    }

    private void salvarHO(HierarquiaOrganizacional hierarquiaSelecionada) {
        if (hierarquiaSelecionada.getId() == null) {
            hierarquiaSelecionada.setInicioVigencia(dataFiltro);
            hierarquiaSelecionada.setExercicio(sistemaControlador.getExercicioCorrente());
            hierarquiaSelecionada.setNivelNaEntidade(hierarquiaSelecionada.getNivelPorCodigo());
            hierarquiaOrganizacionalFacade.salvarNovo(hierarquiaSelecionada);
        } else {
            hierarquiaOrganizacionalFacade.salvar(hierarquiaSelecionada);
        }
        singletonHO.invalidaCache(hierarquiaSelecionada.getTipoHierarquiaOrganizacional());
        salvaPrevisaoHOContaDestinacao();
        hierarquiaOrganizacionalFacade.atualizaViewHierarquiaOrcamentaria();
    }

    private void salvaPrevisaoHOContaDestinacao() {
        if (hierarquiaSelecionada.getTipoHierarquiaOrganizacional().equals(TipoHierarquiaOrganizacional.ORCAMENTARIA)) {
            if (!listaPrevisaoHoConta.isEmpty()) {
                hierarquiaOrganizacionalFacade.calculaSomatoriaValorEstimado(hierarquiaSelecionada, sistemaControlador.getDataOperacao(), listaPrevisaoHoConta);
            }
            if (!listaRemovidosPrevisaoHoConta.isEmpty()) {
                hierarquiaOrganizacionalFacade.calculaSubtracaoValorEstimado(hierarquiaSelecionada, sistemaControlador.getDataOperacao(), listaRemovidosPrevisaoHoConta);
            }

        }
    }

    @Override
    public void cancelar() {
        rootAdm = null;
        rootOrc = null;
        FacesUtil.redirecionamentoInterno("/hierarquia-organizacional/inicio/");
    }

    public void recuperaArvoreHierarquiaOrganizacionalAdm() {
        rootAdm = singletonHO.getArvoreAdministrativa(dataFiltro);
    }

    public TreeNode getPai(HierarquiaOrganizacional ho, TreeNode root) {
        return singletonHO.getPai(ho, root);
    }

    public void recuperaArvoreHierarquiaOrganizacionalOrc() {
        rootOrc = singletonHO.getArvoreOrcamentaria(sistemaControlador.getDataOperacao());
    }

    public boolean getTipoHierarquiaAdministrativa() {
        return tipoTemp.equals(TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    public String hierarquiaVigente(HierarquiaOrganizacional uo) {
        if (uo.getFimVigencia() == null) {
            return SIM;
        }
        if (Util.getDataHoraMinutoSegundoZerado(sistemaControlador.getDataOperacao()).compareTo(Util.getDataHoraMinutoSegundoZerado(uo.getFimVigencia())) <= 0) {
            return SIM;
        }
        return NAO;
    }

    public String getStringNo(Integer nivel) {
        String[] split = hierarquiaSelecionada.getCodigo().split("\\.");
        return split[nivel];
    }

    private Long retornaNumeroCodigo(String codigo) {
        try {
            return Long.parseLong(codigo.replace(".", ""));
        } catch (Exception e) {
            return 0l;
        }
    }

    private void ordenaListaHierarquia() {

        Collections.sort(listaHierarquiaOrganizacionalTipo, new Comparator<HierarquiaOrganizacional>() {
            @Override
            public int compare(HierarquiaOrganizacional o1, HierarquiaOrganizacional o2) {
                return retornaNumeroCodigo(o1.getCodigo()).compareTo(retornaNumeroCodigo(o2.getCodigo()));
            }
        });
    }

    public void setaDataFiltro() {
        dataFiltro = sistemaControlador.getDataOperacao();
    }

    public void onTabChange(TabChangeEvent event) {
        String tipo = (String) event.getTab().getAttributes().get("tipo");
        tipoTemp = TipoHierarquiaOrganizacional.valueOf(tipo);
    }

    public void definirTipoHierarquia(ActionEvent ev) {
        String tipo = (String) ev.getComponent().getAttributes().get("tipo");
        tipoTemp = TipoHierarquiaOrganizacional.valueOf(tipo);
    }

    private void definirData() {
        if (dataDoFiltro == null) {
            dataFiltro = UtilRH.getDataOperacao();
        } else {
            try {
                dataFiltro = new SimpleDateFormat("dd-MM-yyyy").parse(dataDoFiltro);
            } catch (ParseException e) {
                dataFiltro = UtilRH.getDataOperacao();
            }
        }
    }

    @URLAction(mappingId = "inicioHierarquiaOrganizacional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void inicio() {
        dataFiltro = UtilRH.getDataOperacao();
    }

    public void processarOperacaoHierarquia(ActionEvent ev) {
        if (dataFiltro == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo data de vigência deve ser informado");
            return;
        }

        String url = "/hierarquia-organizacional/";
        String operacao = (String) ev.getComponent().getAttributes().get("operacao");
        String tipo = tipoTemp.toString().toLowerCase();
        String dataOperacao = dataFiltro != null ? new SimpleDateFormat("dd-MM-yyyy").format(dataFiltro) : "";

        url += operacao + "/";
        url += tipo + "/";
        url += dataOperacao + "/";

        FacesUtil.redirecionamentoInterno(url);
    }

    // ADMINISTRATIVA
    @URLAction(mappingId = "verAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verAdministrativa() {
        definirData();
        tipoTemp = TipoHierarquiaOrganizacional.ADMINISTRATIVA;
        rootAdm = singletonHO.getArvoreAdministrativa(dataFiltro);
    }

    @URLAction(mappingId = "verAdministrativaFiltro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verAdministrativaFiltro() {
        definirData();
        tipoTemp = TipoHierarquiaOrganizacional.ADMINISTRATIVA;
        rootAdm = singletonHO.getArvoreAdministrativa(dataFiltro);
    }

    @URLAction(mappingId = "editarAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarAdministrativa() {
        tipoTemp = TipoHierarquiaOrganizacional.ADMINISTRATIVA;
        editaAdm();
        definirData();
    }

    @URLAction(mappingId = "editarAdministrativaFiltro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarAdministrativaFiltro() {
        tipoTemp = TipoHierarquiaOrganizacional.ADMINISTRATIVA;
        editaAdm();
        definirData();
    }

    // ORÇAMENTÁRIA
    @URLAction(mappingId = "verOrcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verOrcamentaria() {
        definirData();
        tipoTemp = TipoHierarquiaOrganizacional.ORCAMENTARIA;
        rootOrc = singletonHO.getArvoreOrcamentaria(dataFiltro);
    }

    @URLAction(mappingId = "verOrcamentariaFiltro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verOrcamentariaFiltro() {
        definirData();
        tipoTemp = TipoHierarquiaOrganizacional.ORCAMENTARIA;
        rootOrc = singletonHO.getArvoreOrcamentaria(dataFiltro);
    }

    @URLAction(mappingId = "editarOrcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarOrcamentaria() {
        tipoTemp = TipoHierarquiaOrganizacional.ORCAMENTARIA;
        editaOrc();
        definirData();
    }

    @URLAction(mappingId = "editarOrcamentariaFiltro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarOrcamentariaFiltro() {
        tipoTemp = TipoHierarquiaOrganizacional.ORCAMENTARIA;
        editaOrc();
        definirData();
    }


    public HierarquiaOrganizacionalResponsavel getHierarquiaOrganizacionalResponsavel() {
        return hierarquiaOrganizacionalResponsavel;
    }

    public void setHierarquiaOrganizacionalResponsavel(HierarquiaOrganizacionalResponsavel hierarquiaOrganizacionalResponsavel) {
        this.hierarquiaOrganizacionalResponsavel = hierarquiaOrganizacionalResponsavel;
    }

    public BigDecimal getValorEstimadoPorExercicio(HierarquiaOrganizacional ho) {
        try {
            List<PrevisaoHOContaDestinacao> previsao = hierarquiaOrganizacionalFacade.recuperarPrevisaoHOContaDestinacaoPorExercicio(ho.getSubordinada(), sistemaControlador.getExercicioCorrente());
            BigDecimal valor = BigDecimal.ZERO;
            for (PrevisaoHOContaDestinacao hoContaDestinacao : previsao) {
                valor = valor.add(hoContaDestinacao.getValor());
            }
            return valor;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalFiltrandoPorNivel2(String parte) {
        return hierarquiaOrganizacionalFacade.listaPorUsuarioEIndiceDoNo(sistemaControlador.getUsuarioCorrente(), parte, "ADIMINISTRATIVA", sistemaControlador.getDataOperacao(), 2);
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalFrotas(String parte) {
        return hierarquiaOrganizacionalFacade.listaOrgaoEntidadeFundoFrotas(sistemaControlador.getUsuarioCorrente(), parte, sistemaControlador.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalFrotasComRegistro(String parte) {
        return hierarquiaOrganizacionalFacade.buscarOrgaoEntidadeFundoFrotasComRegistros(parte, sistemaControlador.getDataOperacao(), true);
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaOrganizacionalAdministrativaPorCodigoOrDescricaoAndDataComLocalDeEstoqueOndeUsuarioEhGestor(String codigoOrDescricao) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaOrganizacionalAdministrativaPorCodigoOrDescricaoAndDataComLocalDeEstoqueOndeUsuarioEhGestor(codigoOrDescricao,
            sistemaControlador.getDataOperacao(), sistemaControlador.getUsuarioCorrente());
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaOrganizacionalOrcamentariaOndeUsuarioLogadoEhGestorDeLicitacao(String codigoOrDescricao) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaOrganizacionalOrcamentariaPorCodigoOrDescricao(codigoOrDescricao);
    }

    public class Mascara implements Serializable {
        String codigo;
        int indice;

        public Mascara(String codigo, int indice) {
            this.codigo = codigo;
            this.indice = indice;
        }

        private Mascara() {
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public int getIndice() {
            return indice;
        }

        public void setIndice(int indice) {
            this.indice = indice;
        }

        @Override
        public String toString() {
            return "indice.: " + indice + " codigo.: " + codigo;
        }
    }

    public String buscarCodigoDescricaoHierarquia(String tipoHierarquia, UnidadeOrganizacional unidade, Date dataMovimento) {
        if (unidade != null && dataMovimento != null) {
            HierarquiaOrganizacional hierarquia = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(tipoHierarquia, unidade, dataMovimento);
            if (hierarquia != null) {
                return hierarquia.getCodigo() + " - " + hierarquia.getDescricao();
            }
            return unidade.getDescricao();
        }
        return "";
    }

    public Date getDataOperacao(){
        return sistemaControlador.getDataOperacao();
    }

    public List<HierarquiaOrganizacional> completarUnidadeAdministrativaPorUsuario(String parte) {
        return hierarquiaOrganizacionalFacade.buscarUnidadeAdministrativaPorUsuario(
            sistemaControlador.getUsuarioCorrente(),
            parte.trim(),
            sistemaControlador.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> completarUnidadeOrcamentariaPorUsuario(String parte) {
        return hierarquiaOrganizacionalFacade.buscarUnidadeOrcamentariaPorUsuario(
            sistemaControlador.getUsuarioCorrente(),
            parte.trim(),
            sistemaControlador.getDataOperacao());
    }
}
