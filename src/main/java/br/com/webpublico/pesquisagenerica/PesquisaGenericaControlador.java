package br.com.webpublico.pesquisagenerica;

import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.primefaces.component.column.Column;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.inputtext.InputText;

import javax.ejb.EJB;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.MethodExpressionActionListener;
import javax.faces.model.SelectItem;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 04/09/13
 * Time: 10:23
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PesquisaGenericaControlador {

    private static final String ID_PANEL_PRINCIPAL = "painelPesquisaGenerica";
    private static final String ID_TABELA_PRINCIPAL = "tabelaPesquisaGenerica";
    @EJB
    private PesquisaGenericaFacade pesquisaGenericaFacade;
    private HtmlPanelGroup painelPrincipal;
    private List<List<String>> valores;
    private String[] headers;
    private PesquisaGenerica pesquisaGenerica;
    private List<Field> fieldsTabelaveis;
    private String idComponente;
    private ItemPesquisaGenerica item;


    private void prepararComponentes() {
        if (this.pesquisaGenerica != null) {


            criarTitulo();
            criarItensPesquisa();


            montaHeaders();
            montaConteudo();
            criarDataTable();
        }
    }

    private void montaConteudo() {
        valores = Lists.newArrayList();
        try {
            List lista = pesquisaGenericaFacade.filtar(pesquisaGenerica, 0, 10);
            for (Object o : lista) {
                String[] campos = new String[fieldsTabelaveis.size()];
                int i = 0;
                for (Field field : fieldsTabelaveis) {
                    if (field.getAnnotation(Tabelavel.class) != null) {
                        campos[i] = Persistencia.getAsStringAtributoValue(o, field);
                        i++;
                    }
                }
                valores.add(Arrays.asList(campos));
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar Pesquisar!", ex.getMessage()));
        }
    }

    private void montaHeaders() {
        headers = new String[fieldsTabelaveis.size()];
        int contador = 0;
        for (Field field : fieldsTabelaveis) {
            headers[contador] = Persistencia.getNomeDoCampo(field);
            contador++;
        }
    }


    private void criarDataTable() {
        DataTable dataTable = new DataTable();
        dataTable.setId(ID_TABELA_PRINCIPAL);
        dataTable.setValueExpression("value", createValueExpression("#{pesquisaGenericaControlador.valores}", List.class));
        dataTable.setVar("item");
        for (int i = 0; i < headers.length; i++) {
            Column column = new Column();
            HtmlOutputText header = new HtmlOutputText();
            header.setValue(headers[i]);
            column.setHeader(header);
            HtmlOutputText output = new HtmlOutputText();
            output.setValueExpression("value", createValueExpression("#{item[" + i + "]}", String.class));
            output.setEscape(false);
            column.getChildren().add(output);
            dataTable.getChildren().add(column);
            painelPrincipal.getChildren().add(dataTable);
        }
    }

    private void criarItensPesquisa() {
        fieldsTabelaveis = this.pesquisaGenerica.getAtributosTabelaveis();

        HtmlPanelGrid gridFiltrosPesquisa = new HtmlPanelGrid();
        gridFiltrosPesquisa.setColumns(2);

        item = new ItemPesquisaGenerica();

        HtmlSelectOneMenu itensPesquisa = new HtmlSelectOneMenu();
        itensPesquisa.setValueExpression("value", createValueExpression("#{pesquisaGenericaControlador.item.condicao}", String.class));
        UISelectItems selectItensPesquisa = new UISelectItems();
        selectItensPesquisa.setValue(getItensPesquisaveis());
        itensPesquisa.getChildren().add(selectItensPesquisa);

        InputText inputPesquisa = new InputText();
        inputPesquisa.setValueExpression("value", createValueExpression("#{pesquisaGenericaControlador.item.valor}", String.class));

        gridFiltrosPesquisa.getChildren().add(itensPesquisa);
        gridFiltrosPesquisa.getChildren().add(inputPesquisa);

        painelPrincipal.getChildren().add(gridFiltrosPesquisa);
        painelPrincipal.getChildren().add(criaBotaoPesquisar());

        pesquisaGenerica.getItensDePesquisa().add(item);

    }

    private CommandButton criaBotaoPesquisar() {
        CommandButton pesquisar = new CommandButton();
        pesquisar.setValue("Pesquisar");
        pesquisar.setIcon("ui-icon-search");
        pesquisar.setStyleClass("botaoPadrao prioridario");

        pesquisar.addActionListener(createActionListener("#{pesquisaGenericaControlador.pesquisar()}", void.class, new Class[0]));
        return pesquisar;
    }

    private void criarTitulo() {
        HtmlOutputText titulo = new HtmlOutputText();
        titulo.setValue("Lista de " + pesquisaGenerica.getClasse().getSimpleName() + " <br/>");
        titulo.setEscape(false);
        painelPrincipal.getChildren().add(titulo);
    }

    public static MethodExpression createMethodExpression(String expressao, Class returnType, Class[] paramType) {
        ExpressionFactory elFactory = FacesContext.getCurrentInstance().getApplication().getExpressionFactory();
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        return elFactory.createMethodExpression(elContext, expressao, returnType, paramType);
    }

    public MethodExpressionActionListener createActionListener(String expressao, Class returnType, Class[] paramType) {
        MethodExpression me = createMethodExpression(expressao, returnType, paramType);
        return new MethodExpressionActionListener(me);
    }

    public ValueExpression createValueExpression(String expression, Class<?> expectedType) {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getApplication().getExpressionFactory().createValueExpression(context.getELContext(), expression, expectedType);
    }


    public HtmlPanelGroup getPainelPrincipal() {
        return painelPrincipal;
    }

    public void setPainelPrincipal(HtmlPanelGroup painelPrincipal) {
        this.painelPrincipal = painelPrincipal;
    }

    public List<List<String>> getValores() {
        return valores;
    }

    public void setValores(List<List<String>> valores) {
        this.valores = valores;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public List<SelectItem> getItensPesquisaveis() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (Field field : pesquisaGenerica.getAtributosPesquisaveis()) {
            retorno.add(new SelectItem(field.getName(), Persistencia.getNomeDoCampo(field)));
        }
        return retorno;
    }

    public void pesquisar() {


        montaConteudo();

        FacesUtil.atualizarComponente("FormPesquisaGenerica:componentePesquisaGenerica");
    }

    private void atualizarComponente() {
        FacesUtil.atualizarComponente(idComponente);
    }

    public void criaPagina(PesquisaGenerica pesquisaGenerica, String id) {
        if (painelPrincipal == null) {
            this.pesquisaGenerica = pesquisaGenerica;
            painelPrincipal = new HtmlPanelGroup();
            prepararComponentes();
            idComponente = ":" + id + ":" + ID_PANEL_PRINCIPAL;
            UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent(idComponente);
            component.getChildren().add(painelPrincipal);
        }
    }

    public ItemPesquisaGenerica getItem() {
        return item;
    }

    public void setItem(ItemPesquisaGenerica item) {
        this.item = item;
    }
}
