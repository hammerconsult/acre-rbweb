/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.ComponentePesquisaCadastroImobiliarioFacade;
import br.com.webpublico.negocios.ConsultaDebitoFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroImobiliarioDAO;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlForm;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Renato
 */
@ManagedBean
@SessionScoped
public class ComponentePesquisaCadastroImobiliarioControlador implements Serializable {

    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroContribuinte;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroCompromissario;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroLote;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroCodigo;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroInscricao;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroQuadra;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroSetor;
    private Construcao construcaoTemp;
    private boolean alterandoConstrucao;
    @Limpavel(Limpavel.ZERO)
    private int alternaLoteQuadra;
    @Limpavel(Limpavel.ZERO)
    private int filtroTipoProprietario;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroCpfCnpj;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroLogradouro;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroBairro;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroNumeroCorreio;
    private boolean filtroTipoCpfCnpj;
    private String mascaraTipoCpfCnpj;
    @Limpavel(Limpavel.VERDADEIRO)
    private boolean filtroSituacaoCadastral;
    private List<CadastroImobiliario> listaCadastroImobiliarios;
    private ConverterAutoComplete converterCadastroImobiliario;
    private CadastroImobiliario cadastroImobiliarioSelecionado;
    @EJB
    private ComponentePesquisaCadastroImobiliarioFacade facade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    private String idFormulario;
    private String idComponente;
    private transient final ApplicationContext ap;
    private JdbcCadastroImobiliarioDAO jdbcCadastroImobiliarioDAO;
    private Testada testadaPrincipal;

    public ComponentePesquisaCadastroImobiliarioControlador() {
        this.ap = ContextLoader.getCurrentWebApplicationContext();
        this.jdbcCadastroImobiliarioDAO = (JdbcCadastroImobiliarioDAO) this.ap.getBean("cadastroImobiliarioJDBCDAO");
    }

    public String getIdComponente() {
        return idComponente;
    }

    public void setIdComponente(String idComponente) {
        this.idComponente = idComponente;
    }

    public String getIdFormulario() {
        return idFormulario;
    }

    public void setIdFormulario(String idFormulario) {
        this.idFormulario = idFormulario;
    }

    public boolean isAlterandoConstrucao() {
        return alterandoConstrucao;
    }

    public void setAlterandoConstrucao(boolean alterandoConstrucao) {
        this.alterandoConstrucao = alterandoConstrucao;
    }

    public int getAlternaLoteQuadra() {
        return alternaLoteQuadra;
    }

    public void setAlternaLoteQuadra(int alternaLoteQuadra) {
        this.alternaLoteQuadra = alternaLoteQuadra;
    }

    public Construcao getConstrucaoTemp() {
        return construcaoTemp;
    }

    public void setConstrucaoTemp(Construcao construcaoTemp) {
        this.construcaoTemp = construcaoTemp;
    }

    public String getFiltroBairro() {
        return filtroBairro;
    }

    public void setFiltroBairro(String filtroBairro) {
        this.filtroBairro = filtroBairro;
    }

    public String getFiltroCodigo() {
        return filtroCodigo;
    }

    public void setFiltroCodigo(String filtroCodigo) {
        this.filtroCodigo = filtroCodigo;
    }

    public String getFiltroCompromissario() {
        return filtroCompromissario;
    }

    public void setFiltroCompromissario(String filtroCompromissario) {
        this.filtroCompromissario = filtroCompromissario;
    }

    public String getFiltroContribuinte() {
        return filtroContribuinte;
    }

    public void setFiltroContribuinte(String filtroContribuinte) {
        this.filtroContribuinte = filtroContribuinte;
    }

    public String getFiltroCpfCnpj() {
        return filtroCpfCnpj;
    }

    public void setFiltroCpfCnpj(String filtroCpfCnpj) {
        this.filtroCpfCnpj = filtroCpfCnpj;
    }

    public String getFiltroInscricao() {
        return filtroInscricao;
    }

    public void setFiltroInscricao(String filtroInscricao) {
        this.filtroInscricao = filtroInscricao;
    }

    public String getFiltroLogradouro() {
        return filtroLogradouro;
    }

    public void setFiltroLogradouro(String filtroLogradouro) {
        this.filtroLogradouro = filtroLogradouro;
    }

    public String getFiltroLote() {
        return filtroLote;
    }

    public void setFiltroLote(String filtroLote) {
        this.filtroLote = filtroLote;
    }

    public String getFiltroNumeroCorreio() {
        return filtroNumeroCorreio;
    }

    public void setFiltroNumeroCorreio(String filtroNumeroCorreio) {
        this.filtroNumeroCorreio = filtroNumeroCorreio;
    }

    public String getFiltroQuadra() {
        return filtroQuadra;
    }

    public void setFiltroQuadra(String filtroQuadra) {
        this.filtroQuadra = filtroQuadra;
    }

    public String getFiltroSetor() {
        return filtroSetor;
    }

    public void setFiltroSetor(String filtroSetor) {
        this.filtroSetor = filtroSetor;
    }

    public boolean isFiltroSituacaoCadastral() {
        return filtroSituacaoCadastral;
    }

    public void setFiltroSituacaoCadastral(boolean filtroSituacaoCadastral) {
        this.filtroSituacaoCadastral = filtroSituacaoCadastral;
    }

    public boolean isFiltroTipoCpfCnpj() {
        return filtroTipoCpfCnpj;
    }

    public void setFiltroTipoCpfCnpj(boolean filtroTipoCpfCnpj) {
        this.filtroTipoCpfCnpj = filtroTipoCpfCnpj;
    }

    public int getFiltroTipoProprietario() {
        return filtroTipoProprietario;
    }

    public void setFiltroTipoProprietario(int filtroTipoProprietario) {
        this.filtroTipoProprietario = filtroTipoProprietario;
    }

    public String getMascaraTipoCpfCnpj() {
        return mascaraTipoCpfCnpj;
    }

    public void setMascaraTipoCpfCnpj(String mascaraTipoCpfCnpj) {
        this.mascaraTipoCpfCnpj = mascaraTipoCpfCnpj;
    }

    public List<CadastroImobiliario> getListaCadastroImobiliarios() {
        return listaCadastroImobiliarios;
    }

    public void setListaCadastroImobiliarios(List<CadastroImobiliario> listaCadastroImobiliarios) {
        this.listaCadastroImobiliarios = listaCadastroImobiliarios;
    }

    public CadastroImobiliario getCadastroImobiliarioSelecionado() {
        return cadastroImobiliarioSelecionado;
    }

    public void setCadastroImobiliarioSelecionado(CadastroImobiliario cadastroImobiliarioSelecionado) {
        this.cadastroImobiliarioSelecionado = cadastroImobiliarioSelecionado;
    }

    public List<SelectItem> getAlteraPesquisaPorLoteQuadra() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(1, "Município"));
        toReturn.add(new SelectItem(2, "Loteamento"));
        toReturn.add(new SelectItem(3, "Município/Loteamento"));
        return toReturn;
    }

    public List<SelectItem> getTipoDeProprietario() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(1, "Proprietário Atual"));
        toReturn.add(new SelectItem(2, "Proprietário Anterior"));
        toReturn.add(new SelectItem(3, "Atual/Anteriores"));
        return toReturn;
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, facade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return facade.getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public List<CadastroImobiliario> completaCadastroImobiliarioAtivosJDBC(String filtro){
        return  jdbcCadastroImobiliarioDAO.lista(filtro.trim(), true);
    }

    public List<CadastroImobiliario> completaCadastroImobiliarioJDBC(String filtro){
        return  jdbcCadastroImobiliarioDAO.lista(filtro.trim(), false);
    }


    public void montaMascaraCpfCnpj() {
        if (filtroTipoCpfCnpj) {
            mascaraTipoCpfCnpj = "999.999.999-99";
        } else {
            mascaraTipoCpfCnpj = "99.999.999/9999-99";
        }
    }

    public String getMascara() {
        if (!filtroTipoCpfCnpj) {
            return "99.999.999/9999-99";
        }
        return "999.999.999-99";
    }

    public String nomeProprietario(CadastroImobiliario cadastro) {
        List<Propriedade> proprietarios = facade.getCadastroImobiliarioFacade().recuperarProprietariosVigentes(cadastro);
        if (proprietarios.isEmpty()) {
            return " - ";
        } else {
            Pessoa pessoa = proprietarios.get(0).getPessoa();
            return pessoa.getNomeCpfCnpj();
        }
    }

    public List<Propriedade> recuperaPropriedadesDoCadastro(Cadastro cadastro) {
        List<Propriedade> proprietarios;
        if (cadastro == null || cadastro.getId() == null && cadastroImobiliarioSelecionado.getId() == null) {
            proprietarios = new ArrayList<>();
        } else if (cadastroImobiliarioSelecionado != null && cadastroImobiliarioSelecionado.getId() != null) {
            proprietarios = facade.getCadastroImobiliarioFacade().recuperarProprietariosVigentes(cadastroImobiliarioSelecionado);
        } else {
            proprietarios = facade.getCadastroImobiliarioFacade().recuperarProprietariosVigentes((CadastroImobiliario) cadastro);
        }

        return proprietarios;
    }

    public void novo(String idComponente, CadastroImobiliario cadastro) {
        this.idComponente = idComponente;
        this.cadastroImobiliarioSelecionado = cadastro;
    }

    public void filtrar() {
        listaCadastroImobiliarios = facade.listaPorFiltro(filtroCodigo, filtroContribuinte, filtroInscricao, filtroLote, filtroQuadra, filtroSetor, filtroCompromissario, alternaLoteQuadra, filtroTipoProprietario, filtroCpfCnpj, filtroLogradouro, filtroBairro, filtroNumeroCorreio, filtroSituacaoCadastral);
        atualizaTabela();
    }

    public void limparCampos() {
        Util.limparCampos(this);
        listaCadastroImobiliarios = new ArrayList<>();
        cadastroImobiliarioSelecionado = null;
        filtroSituacaoCadastral = true;
        filtroTipoProprietario = 3;
        filtroTipoCpfCnpj = true;
        mascaraTipoCpfCnpj = "999.999.999-99";
        atualizaPanel();
        atualizaTabela();
    }

    public void teste(SelectEvent event) {
        cadastroImobiliarioSelecionado = ((CadastroImobiliario) event.getObject());
    }

    public String getIdComponenteCompleto() {
        return idFormulario + ":" + idComponente + ":";
    }

    public void atualizaCadastroSelecionado() {
        atualizaComponente("panelGridPrincipal");
    }

    public void atualizaMascaraCpfCnpj() {
        atualizaComponente("cpfCnpj");
    }

    public void atualizaPanelTabela(ActionEvent evento) {
        limparCampos();
        UIComponent component = evento.getComponent();
        idFormulario = getForm(component).getClientId();
        atualizaPanel();
        atualizaTabela();
    }

    public void atualizaPanel() {
        atualizaComponente("panelGridComponente");
    }

    public void atualizaTabela() {
        atualizaComponente("tabela");
    }

    public void atualizaPanelBotoes() {
        atualizaComponente("panelBotoes");
    }

    public void atualizaComponente(String idComponente) {
        String id = getIdComponenteCompleto() + idComponente;
        RequestContext.getCurrentInstance().update(id);
    }

    public UIComponent getForm(UIComponent comp) {
        if (comp == null || comp.getParent() == null) {
            return null;
        } else {
            if (comp instanceof HtmlForm) {
                return comp;
            }
        }
        return getForm(comp.getParent());
    }

    public void selecionarCadastro(CadastroImobiliario cadastro) {
        cadastroImobiliarioSelecionado = cadastro;
        atualizaTabela();
        atualizaPanelBotoes();
    }

    public void selecionaCadastro(CadastroImobiliario cadastroImobiliario) {
        atualizaTabela();
        atualizaPanelBotoes();
        testadaPrincipal = jdbcCadastroImobiliarioDAO.recuperaTestadaPrincipalLote(cadastroImobiliario.getLote());
    }

    public void setaCadastroNull() {
        cadastroImobiliarioSelecionado = null;
    }

    public String montaEnderecoCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        String endereco = "";
        Lote lote = cadastroImobiliario.getLote();

        return endereco;
    }

    public void setTestadaPrincipal(Testada testadaPrincipal) {
        this.testadaPrincipal = testadaPrincipal;
    }

    public Testada getTestadaPrincipal() {
        return testadaPrincipal;
    }
}
