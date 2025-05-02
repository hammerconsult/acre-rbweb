///*
// * Codigo gerado automaticamente em Wed Jun 29 13:46:26 BRT 2011
// * Gerador de Controlador
// */
//package br.com.webpublico.controle;
//
//import br.com.webpublico.entidades.ContratoServidor;
//import br.com.webpublico.entidades.UnidadeOrganizacional;
//import br.com.webpublico.negocios.UnidadeOrganizacionalFacade;
//import br.com.webpublico.entidades.ModalidadeContratoServidor;
//import br.com.webpublico.entidades.PessoaFisica;
//import br.com.webpublico.negocios.ModalidadeContratoServidorFacade;
//import br.com.webpublico.negocios.AbstractFacade;
//import br.com.webpublico.negocios.ContratoServidorFacade;
//import br.com.webpublico.negocios.PessoaFisicaFacade;
//import br.com.webpublico.util.ConverterAutoComplete;
//import br.com.webpublico.util.ConverterGenerico;
//import br.com.webpublico.util.EntidadeMetaData;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//import javax.ejb.EJB;
//import javax.faces.application.FacesMessage;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.SessionScoped;
//import javax.faces.context.FacesContext;
//import javax.faces.convert.Converter;
//import javax.faces.event.ActionEvent;
//import javax.faces.model.SelectItem;
//import org.primefaces.model.DefaultTreeNode;
//import org.primefaces.model.TreeNode;
//
//@ManagedBean
//@SessionScoped
//public class ContratoServidorControlador extends SuperControladorCRUD implements Serializable {
//
//    @EJB
//    private ContratoServidorFacade contratoServidorFacade;
//    @EJB
//    private PessoaFisicaFacade pessoaFisicaFacade;
//    private Converter converterServidor;
//    @EJB
//    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
//    private UnidadeOrganizacional unidadeSelecionada;
//    private Converter converterUnidadeOrganizacional;
//    private TreeNode root;
//    private TreeNode noSelecionado;
//    @EJB
//    private ModalidadeContratoServidorFacade modalidadeContratoServidorFacade;
//    private ConverterGenerico converterModalidadeContratoServidor;
//
//    public ContratoServidorControlador() {
//        metadata = new EntidadeMetaData(ContratoServidor.class);
//    }
//
//    public ContratoServidorFacade getFacade() {
//        return contratoServidorFacade;
//    }
//
//    @Override
//    public AbstractFacade getFacede() {
//        return contratoServidorFacade;
//    }
//
//    @Override
//    public String salvar() {
//        if (noSelecionado != null) {
//            ((ContratoServidor) selecionado).setUnidadeOrganizacional((UnidadeOrganizacional) noSelecionado.getData());
//        }
//        return super.salvar();
//    }
//
//    @Override
//    public Boolean validaCampos() {
//        boolean retorno = super.validaCampos();
//        if (retorno) {
//            ContratoServidor cs = ((ContratoServidor) selecionado);
//            if (cs.getFinalVigencia() != null) {
//                //System.out.println("passou aqui");
//                if (cs.getFinalVigencia().before(cs.getInicioVigencia())) {
//                    FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Atenção!", "Fim da Vigência não pode ser menor que Início!"));
//                    return false;
//                }
//            }
//        } else {
//            return false;
//        }
//        //System.out.println("passou aqui fora");
//        return retorno;
//    }
//
//    @Override
//    public void novo() {
//        unidadeSelecionada = new UnidadeOrganizacional();
//        super.novo();
//    }
//
//    @Override
//    public void selecionar(ActionEvent evento) {
//        unidadeSelecionada = new UnidadeOrganizacional();
//        super.selecionar(evento);
//    }
//
//
//    public List<SelectItem> getServidor() {
//        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        for (PessoaFisica object : pessoaFisicaFacade.lista()) {
//            toReturn.add(new SelectItem(object, object.getNome()));
//        }
//        return toReturn;
//    }
//
//    public Converter getConverterServidor() {
//        if (converterServidor == null) {
//            converterServidor = new ConverterAutoComplete(PessoaFisica.class, pessoaFisicaFacade);
//        }
//        return converterServidor;
//    }
//
//    public List<PessoaFisica> completaServidor(String parte) {
//        return pessoaFisicaFacade.listaFiltrando(parte.trim(), "nome");
//    }
//
//    public List<SelectItem> getUnidadeOrganizacional() {
//        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        for (UnidadeOrganizacional object : unidadeOrganizacionalFacade.lista()) {
//            toReturn.add(new SelectItem(object, object.getDescricao()));
//        }
//        return toReturn;
//    }
//
//    public Converter getConverterUnidadeOrganizacional() {
//        if (converterUnidadeOrganizacional == null) {
//            converterUnidadeOrganizacional = new ConverterAutoComplete(UnidadeOrganizacional.class, unidadeOrganizacionalFacade);
//        }
//        return converterUnidadeOrganizacional;
//    }
//
//    public void montaArvore(TreeNode raizArvore) {
//        UnidadeOrganizacional raiz = (UnidadeOrganizacional) raizArvore.getData();
//        recuperaNoSelecionado(raizArvore);
//        for (UnidadeOrganizacional filhoUnidadeOrganizacional : unidadeOrganizacionalFacade.getFilhosDe(raiz)) {
//            TreeNode filhoArvore = new DefaultTreeNode(filhoUnidadeOrganizacional, raizArvore);
//            recuperaNoSelecionado(filhoArvore);
//            montaArvore(filhoArvore);
//        }
//    }
//
//    public TreeNode recuperaNoSelecionado(TreeNode no) {
//        if (no.getData().equals(((ContratoServidor) selecionado).getUnidadeOrganizacional())) {
//            no.setExpanded(true);
//            no.setSelected(true);
//        }
//        return no;
//    }
//
//    public TreeNode getArvoreUnidadeOrganizacional() {
//        root = new DefaultTreeNode("root", null);
//        UnidadeOrganizacional raiz = unidadeOrganizacionalFacade.getRaiz();
//        TreeNode raizVisual = new DefaultTreeNode(raiz, root);
//        montaArvore(raizVisual);
//        return root;
//    }
//
//    public List<SelectItem> getModalidadeContratoServidor() {
//        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        toReturn.add(new SelectItem(null, ""));
//        for (ModalidadeContratoServidor object : modalidadeContratoServidorFacade.lista()) {
//            toReturn.add(new SelectItem(object, object.getDescricao()));
//        }
//        return toReturn;
//    }
//
//    public ConverterGenerico getConverterModalidadeContratoServidor() {
//        if (converterModalidadeContratoServidor == null) {
//            converterModalidadeContratoServidor = new ConverterGenerico(ModalidadeContratoServidor.class, modalidadeContratoServidorFacade);
//        }
//        return converterModalidadeContratoServidor;
//    }
//
//    public TreeNode getNoSelecionado() {
//        return noSelecionado;
//    }
//
//    public void setNoSelecionado(TreeNode noSelecionado) {
//        this.noSelecionado = noSelecionado;
//    }
//
//    public TreeNode getRoot() {
//        return root;
//    }
//
//    public void setRoot(TreeNode root) {
//        this.root = root;
//    }
//     public List<UnidadeOrganizacional> completaUnidade(String parte) {
//        return unidadeOrganizacionalFacade.listaFiltrando(parte.trim(), "descricao");
//    }
//
//    public void setaUnidade() {
//        if (unidadeSelecionada!= null) {
//            ((ContratoServidor)selecionado).setUnidadeOrganizacional(unidadeSelecionada);
//        } else {
//            FacesContext.getCurrentInstance().addMessage("Formulario:unidadeOrg", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Selecione para filtrar!"));
//        }
//    }
//
//
//    public UnidadeOrganizacional getUnidadeSelecionada() {
//        return unidadeSelecionada;
//    }
//
//    public void setUnidadeSelecionada(UnidadeOrganizacional unidadeSelecionada) {
//        this.unidadeSelecionada = unidadeSelecionada;
//    }
//}
