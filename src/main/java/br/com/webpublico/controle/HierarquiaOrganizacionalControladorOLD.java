/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoHierarquia;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacadeOLD;
import br.com.webpublico.negocios.UnidadeOrganizacionalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.MoneyConverter;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author reidocrime
 */
@SessionScoped
@ManagedBean
public class HierarquiaOrganizacionalControladorOLD extends SuperControladorCRUD {

    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    private boolean mostraPaineis;
    private TipoHierarquiaOrganizacional tipoTemp;
    private List<UnidadeOrganizacional> listaUnd;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private TreeNode root;
    private UnidadeOrganizacional unidadeSelecionada;
    private BigDecimal valor;
    private String codigoNo;
    private String codigo;
    private MoneyConverter moneyConverter;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private List<HierarquiaOrganizacional> listaHierarquiaOrganizacionalTipo;
    private HierarquiaOrganizacional hierarquiaPaiSelecionada;
    private HierarquiaOrganizacional hierarquiaSelecionada;
    private boolean enableCampos;
    private List<Mascara> mascaraQuebrada;

    @Override
    public AbstractFacade getFacede() {
        return hierarquiaOrganizacionalFacade;
    }

    public List<UnidadeOrganizacional> getListaUnidadesOrganizacionais() {
        if (tipoTemp != null) {
            return hierarquiaOrganizacionalFacade.unidadesOrganizacionaisDisponiveis(sistemaControlador.getExercicioCorrente(), tipoTemp);
        } else {
            return new ArrayList<UnidadeOrganizacional>();
        }
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public TipoHierarquiaOrganizacional getTipoTemp() {
        return tipoTemp;
    }

    public void setTipoTemp(TipoHierarquiaOrganizacional tipoTemp) {
        this.tipoTemp = tipoTemp;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public UnidadeOrganizacional getUnidadeSelecionada() {
        return unidadeSelecionada;
    }

    public void setUnidadeSelecionada(UnidadeOrganizacional unidadeSelecionada) {
        this.unidadeSelecionada = unidadeSelecionada;
    }

    public String getCodigoNo() {
        return codigoNo;
    }

    public void setCodigoNo(String codigoNo) {
        this.codigoNo = codigoNo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public boolean isSalvandoRaiz() {
        boolean b = hierarquiaOrganizacionalFacade.getRaiz(sistemaControlador.getExercicioCorrente()) == null;
        return b;
    }

    public boolean isCadastraHierarquia() {
        return unidadeSelecionada != null;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        List<HierarquiaOrganizacional> toReturn = new ArrayList<HierarquiaOrganizacional>();
        toReturn = hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipo(parte.trim(), "ORCAMENTARIA", sistemaControlador.getExercicioCorrente());
        return toReturn;
    }

    public ConverterAutoComplete getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public Converter getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(UnidadeOrganizacional.class, unidadeOrganizacionalFacade);
        }
        return converterUnidadeOrganizacional;
    }

    public List<UnidadeOrganizacional> getListaUnd() {
        return listaUnd;
    }

    public void setListaUnd(List<UnidadeOrganizacional> listaUnd) {
        this.listaUnd = listaUnd;
    }

    public List<HierarquiaOrganizacional> getListaHierarquiaOrganizacionalTipo() {
        return listaHierarquiaOrganizacionalTipo;
    }

    public void setListaHierarquiaOrganizacionalTipo(List<HierarquiaOrganizacional> listaHierarquiaOrganizacionalTipo) {
        this.listaHierarquiaOrganizacionalTipo = listaHierarquiaOrganizacionalTipo;
    }

    public void alteraHierarquia(HierarquiaOrganizacional ho) {
        hierarquiaPaiSelecionada = hierarquiaOrganizacionalFacade.getPaiDe(ho, sistemaControlador.getExercicioCorrente());
        hierarquiaSelecionada = ho;
        enableCampos = true;
    }

    public void adicionarHoFilha(HierarquiaOrganizacional ho) {
        operacao = Operacoes.NOVO;
        hierarquiaPaiSelecionada = ho;
        hierarquiaSelecionada.setTipoHierarquiaOrganizacional(tipoTemp);
        hierarquiaSelecionada.setInicioVigencia(new Date());
        hierarquiaSelecionada.setSuperior(hierarquiaPaiSelecionada.getSubordinada());
        hierarquiaSelecionada.setIndiceDoNo(hierarquiaPaiSelecionada.getIndiceDoNo() + 1);
        hierarquiaSelecionada.setExercicio(sistemaControlador.getExercicioCorrente());
        enableCampos = true;

        if (hierarquiaPaiSelecionada.getIndiceDoNo() >= getNiveis()) {
            enableCampos = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Não é permitido desdobrar a hierarquia o nivel " + hierarquiaPaiSelecionada.getIndiceDoNo() + " é o Maior possivel!"));
        }

    }

    public int getNiveis() {
        return hierarquiaOrganizacionalFacade.niveisHierarquia(tipoTemp);
    }

    public void removeHieraquia(HierarquiaOrganizacional ho) {
        ho.setFimVigencia(new Date());
        //System.out.println("Removeu");
    }

    public String codigoCompleto() {
        String toReturn = "";
        //System.out.println(mascaraQuebrada);
        for (Mascara m : getMascaraQuebrada()) {
            toReturn = toReturn + m.getCodigo() + ".";
        }
        //System.out.println("cd comp " + toReturn);
        return toReturn.substring(0, toReturn.length() - 1);
    }

    public void salvarNo() {
        if (operacao.equals(Operacoes.NOVO)) {
            hierarquiaSelecionada.setCodigo(codigoCompleto());
            //System.out.println(hierarquiaSelecionada.getCodigo().split("\\.")[hierarquiaPaiSelecionada.getIndiceDoNo() + 1]);
            hierarquiaSelecionada.setCodigoNo(hierarquiaSelecionada.getCodigo().split("\\.")[hierarquiaPaiSelecionada.getIndiceDoNo()]);
            hierarquiaSelecionada.setIndiceDoNo(hierarquiaPaiSelecionada.getIndiceDoNo() + 1);
            hierarquiaSelecionada.setSuperior(hierarquiaPaiSelecionada.getSubordinada());
            if (validaSalvar(hierarquiaSelecionada)) {
                hierarquiaOrganizacionalFacade.salvarNovo(hierarquiaSelecionada);
            }
        } else {
            hierarquiaOrganizacionalFacade.salvar(hierarquiaSelecionada);
        }
        hierarquiaSelecionada = new HierarquiaOrganizacional();
        hierarquiaPaiSelecionada = new HierarquiaOrganizacional();
        enableCampos = false;
    }

    private boolean validaSalvar(HierarquiaOrganizacional ho) {
        for (HierarquiaOrganizacional h : listaHierarquiaOrganizacionalTipo) {
            //System.out.println("xxxx " + h.getCodigo().equals(ho.getCodigo()) + " sds " + h.getCodigo() + " sds " + ho.getCodigo());
            if (h.getCodigo().equals(ho.getCodigo())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ", "O códico informado já Existe"));
                //System.out.println("Falso");
                return false;
            }
        }
        //System.out.println("verdade");
        return true;
    }

    /**
     * *********
     * Novos metodos
     * <p/>
     * *****
     */
    public TreeNode getArvoreHierarquiaOrganizacionalAdm() {
        //System.out.println("Passou no get");
        TreeNode rootAdm = new DefaultTreeNode(" ", null);
        List<HierarquiaOrganizacional> listaHO = hierarquiaOrganizacionalFacade.listaOrdenadaPorIndiceDoNo(TipoHierarquiaOrganizacional.ADMINISTRATIVA, sistemaControlador.getExercicioCorrente());
        ;
        if (!listaHO.isEmpty()) {
//            arvoreMontada(rootAdm, listaHO);
        }
        rootAdm.setExpanded(true);
        rootAdm.isExpanded();
        rootAdm.setSelected(mostraPaineis);
        return rootAdm;
    }

    public TreeNode getArvoreHierarquiaOrganizacionalOrc() {
        //System.out.println("Passou no get");
        TreeNode rootOrc = new DefaultTreeNode(" ", null);
        List<HierarquiaOrganizacional> listaHO = hierarquiaOrganizacionalFacade.listaOrdenadaPorIndiceDoNo(TipoHierarquiaOrganizacional.ORCAMENTARIA, sistemaControlador.getExercicioCorrente());
        ;
        if (!listaHO.isEmpty()) {
//            arvoreMontada(rootOrc, listaHO);
        }
        rootOrc.setExpanded(true);
        rootOrc.isExpanded();
        rootOrc.setSelected(mostraPaineis);
        return rootOrc;
    }

    public void editaAdm() {
        //System.out.println("Tipo Adm");
        enableCampos = false;
        operacao = Operacoes.EDITAR;
        tipoTemp = TipoHierarquiaOrganizacional.ADMINISTRATIVA;
        listaHierarquiaOrganizacionalTipo = hierarquiaOrganizacionalFacade.getHierarquiasOrganizacionaisPorTipoVigente(new Date(), tipoTemp);
        hierarquiaPaiSelecionada = new HierarquiaOrganizacional();
        hierarquiaSelecionada = new HierarquiaOrganizacional();
        hierarquiaSelecionada.setTipoHierarquiaOrganizacional(tipoTemp);

    }

    public void editaOrc() {
        //System.out.println("Tipo Orc");
        operacao = Operacoes.EDITAR;
        enableCampos = false;
        hierarquiaPaiSelecionada = new HierarquiaOrganizacional();
        tipoTemp = TipoHierarquiaOrganizacional.ORCAMENTARIA;
        listaHierarquiaOrganizacionalTipo = hierarquiaOrganizacionalFacade.getHierarquiasOrganizacionaisPorTipoVigente(new Date(), tipoTemp);
        hierarquiaSelecionada = new HierarquiaOrganizacional();
        hierarquiaSelecionada.setTipoHierarquiaOrganizacional(tipoTemp);
        //System.out.println(listaHierarquiaOrganizacionalTipo);
    }

    public boolean getTipoHierarquiaAdministrativa() {
        if (tipoTemp.equals(TipoHierarquiaOrganizacional.ADMINISTRATIVA)) {
            return true;
        }
        return false;
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

    public List<UnidadeOrganizacional> completaUnidadeOrganizacionalDisponivel(String parte) {
        //System.out.println("Passou pelo completa " + tipoTemp + " " + parte);
        return unidadeOrganizacionalFacade.buscarUnidadesOrganizacionaisNaoVinculadasValidas(tipoTemp, new Date(), parte);
    }

    public List<SelectItem> getTiposAdministrativo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoHierarquia t : TipoHierarquia.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public boolean isEnableCampos() {
        return enableCampos;
    }

    public void setEnableCampos(boolean enableCampos) {
        this.enableCampos = enableCampos;
    }

    public List<Mascara> getMascaraQuebrada() {
        List<Mascara> toReturn = new ArrayList<Mascara>();
        //System.out.println("Entrou no metodo");
        if (hierarquiaPaiSelecionada.getId() != null) {
            String[] parte = hierarquiaPaiSelecionada.getCodigo().split("\\.");
            for (int x = 0; x < parte.length; x++) {
                toReturn.add(new Mascara(parte[x], x));
            }
        } else {
            //System.out.println("Entrou no else");
            toReturn.add(new Mascara("000", 1));
        }
        //System.out.println(toReturn.size());

        return toReturn;
    }

    public void setMascaraQuebrada(List<Mascara> mascaraQuebrada) {
        this.mascaraQuebrada = mascaraQuebrada;
    }

    public class Mascara {

        String codigo;
        int indice;

        public Mascara(String codigo, int indice) {
            this.codigo = codigo;
            this.indice = indice;
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
    }

    public TreeNode getArvore(TreeNode T, List<HierarquiaOrganizacional> listaHo) {
        return null;
    }
}
