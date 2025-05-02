/*
 * Codigo gerado automaticamente em Fri Aug 05 08:57:52 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.HorarioContratoFP;
import br.com.webpublico.entidades.LotacaoFuncional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.HorarioContratoFPFacade;
import br.com.webpublico.negocios.LotacaoFuncionalFacade;
import br.com.webpublico.negocios.UnidadeOrganizacionalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class LotacaoFuncionalControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private UnidadeOrganizacional unidadeSelecionada;
    private DefaultTreeNode root;
    private TreeNode noSelecionado;
    @EJB
    private HorarioContratoFPFacade horarioContratoFPFacade;
    private ConverterGenerico converterHorarioContratoFP;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }


    public LotacaoFuncionalControlador() {
        metadata = new EntidadeMetaData(LotacaoFuncional.class);
    }

    public LotacaoFuncionalFacade getFacade() {
        return lotacaoFuncionalFacade;
    }

    @Override
    public String salvar() {
        if (noSelecionado != null) {
            ((LotacaoFuncional) selecionado).setUnidadeOrganizacional((UnidadeOrganizacional) noSelecionado.getData());
        }
        return super.salvar();
    }

    @Override
    public void novo() {
        unidadeSelecionada = new UnidadeOrganizacional();
        super.novo();
    }

    @Override
    public void selecionar(ActionEvent evento) {
        unidadeSelecionada = new UnidadeOrganizacional();
        super.selecionar(evento);
    }

    @Override
    public AbstractFacade getFacede() {
        return lotacaoFuncionalFacade;
    }

    @Override
    public Boolean validaCampos() {
        Boolean retorno = super.validaCampos();
        LotacaoFuncional lf = ((LotacaoFuncional) selecionado);
        if (lf.getFinalVigencia() != null) {
            if (lf.getFinalVigencia().before(lf.getInicioVigencia())) {
                FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Atenção!", "Fim da Vigência não pode ser menor que Início da Vigência!"));
                retorno = false;
            }
        }
        return retorno;
    }

    public List<SelectItem> getUnidadeOrganizacional() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (UnidadeOrganizacional object : unidadeOrganizacionalFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public Converter getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(UnidadeOrganizacional.class, unidadeOrganizacionalFacade);
        }
        return converterUnidadeOrganizacional;
    }

    public List<SelectItem> getHorarioContratoFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (HorarioContratoFP object : horarioContratoFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterHorarioContratoFP() {
        if (converterHorarioContratoFP == null) {
            converterHorarioContratoFP = new ConverterGenerico(HorarioContratoFP.class, horarioContratoFPFacade);
        }
        return converterHorarioContratoFP;
    }

    public void montaArvore(TreeNode raizArvore) {
        UnidadeOrganizacional raiz = (UnidadeOrganizacional) raizArvore.getData();
        recuperaNoSelecionado(raizArvore);
        for (UnidadeOrganizacional filhoUnidadeOrganizacional : unidadeOrganizacionalFacade.getFilhosDe(raiz)) {
            TreeNode filhoArvore = new DefaultTreeNode(filhoUnidadeOrganizacional, raizArvore);
            recuperaNoSelecionado(filhoArvore);
            montaArvore(filhoArvore);
        }
    }

    public TreeNode recuperaNoSelecionado(TreeNode no) {

        if (no.getData().equals(((LotacaoFuncional) selecionado).getUnidadeOrganizacional())) {
            no.setExpanded(true);
            no.setSelected(true);
            // noSelecionado = no;
        }
        return no;
    }

    public TreeNode getArvoreUnidadeOrganizacional() {
        root = new DefaultTreeNode("root", null);
        UnidadeOrganizacional raiz = unidadeOrganizacionalFacade.getRaiz(sistemaControlador.getExercicioCorrente());
        TreeNode raizVisual = new DefaultTreeNode(raiz, root);
        montaArvore(raizVisual);
        return root;

    }

    public List<UnidadeOrganizacional> completaUnidade(String parte) {
        return unidadeOrganizacionalFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public void setaUnidade() {
        if (unidadeSelecionada != null) {
            ((LotacaoFuncional) selecionado).setUnidadeOrganizacional(unidadeSelecionada);
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:unidadeOrg", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Selecione para filtrar!"));
        }
    }

    public TreeNode getNoSelecionado() {
        return noSelecionado;
    }

    public void setNoSelecionado(TreeNode noSelecionado) {
        this.noSelecionado = noSelecionado;
    }

    public DefaultTreeNode getRoot() {
        return root;
    }

    public void setRoot(DefaultTreeNode root) {
        this.root = root;
    }

    public UnidadeOrganizacional getUnidadeSelecionada() {
        return unidadeSelecionada;
    }

    public void setUnidadeSelecionada(UnidadeOrganizacional unidadeSelecionada) {
        this.unidadeSelecionada = unidadeSelecionada;
    }

}
