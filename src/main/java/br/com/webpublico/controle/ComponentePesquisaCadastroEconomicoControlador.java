/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroEconomicoDAO;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
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
import java.util.HashSet;
import java.util.List;

/**
 * @author Renato
 */
@ManagedBean
@SessionScoped
public class ComponentePesquisaCadastroEconomicoControlador implements Serializable {

    @Limpavel(Limpavel.STRING_VAZIA)
    private String mascaraTipoCpfCnpj;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroCpfCnpj;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroSituacao;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroPessoa;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroProcesso;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroCI;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String filtroCnae;
    @Limpavel(Limpavel.VERDADEIRO)
    private boolean filtroTipoCpfCnpj;
    @Limpavel(Limpavel.NULO)
    private SituacaoCadastralCadastroEconomico situacaoCadastral;
    @Limpavel(Limpavel.NULO)
    private ClassificacaoAtividade filtroClassificacaoAtividade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    private ConverterAutoComplete converterCadastroEconomico;
    private List<CadastroEconomico> lista;
    private String idComponente;
    private CadastroEconomico cadastroEconomicoSelecionado;
    private int inicio = 0;
    private int maximoRegistrosTabela;
    private String filtroEndereco;
    private String filtroNomeFantasia;
    private Pessoa pessoa;
    private transient final ApplicationContext ap;
    private JdbcCadastroEconomicoDAO jdbcCadastroEconomicoDAO;

    public ComponentePesquisaCadastroEconomicoControlador() {
        this.ap = ContextLoader.getCurrentWebApplicationContext();
        this.jdbcCadastroEconomicoDAO = (JdbcCadastroEconomicoDAO) this.ap.getBean("cadastroEconomicoDAO");
    }

    public String getMascaraTipoCpfCnpj() {
        return mascaraTipoCpfCnpj;
    }

    public void setMascaraTipoCpfCnpj(String mascaraTipoCpfCnpj) {
        this.mascaraTipoCpfCnpj = mascaraTipoCpfCnpj;
    }

    public String getFiltroCpfCnpj() {
        return filtroCpfCnpj;
    }

    public void setFiltroCpfCnpj(String filtroCpfCnpj) {
        this.filtroCpfCnpj = filtroCpfCnpj;
    }

    public String getFiltroSituacao() {
        return filtroSituacao;
    }

    public void setFiltroSituacao(String filtroSituacao) {
        this.filtroSituacao = filtroSituacao;
    }

    public String getFiltroPessoa() {
        return filtroPessoa;
    }

    public void setFiltroPessoa(String filtroPessoa) {
        this.filtroPessoa = filtroPessoa;
    }

    public String getFiltroProcesso() {
        return filtroProcesso;
    }

    public void setFiltroProcesso(String filtroProcesso) {
        this.filtroProcesso = filtroProcesso;
    }

    public String getFiltroCI() {
        return filtroCI;
    }

    public void setFiltroCI(String filtroCI) {
        this.filtroCI = filtroCI;
    }

    public String getFiltroCnae() {
        return filtroCnae;
    }

    public void setFiltroCnae(String filtroCnae) {
        this.filtroCnae = filtroCnae;
    }

    public boolean isFiltroTipoCpfCnpj() {
        return filtroTipoCpfCnpj;
    }

    public void setFiltroTipoCpfCnpj(boolean filtroTipoCpfCnpj) {
        this.filtroTipoCpfCnpj = filtroTipoCpfCnpj;
    }

    public SituacaoCadastralCadastroEconomico getSituacaoCadastral() {
        return situacaoCadastral;
    }

    public void setSituacaoCadastral(SituacaoCadastralCadastroEconomico situacaoCadastral) {
        this.situacaoCadastral = situacaoCadastral;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public void setCadastroEconomicoFacade(CadastroEconomicoFacade cadastroEconomicoFacade) {
        this.cadastroEconomicoFacade = cadastroEconomicoFacade;
    }

    public ClassificacaoAtividade getFiltroClassificacaoAtividade() {
        return filtroClassificacaoAtividade;
    }

    public void setFiltroClassificacaoAtividade(ClassificacaoAtividade filtroClassificacaoAtividade) {
        this.filtroClassificacaoAtividade = filtroClassificacaoAtividade;
    }

    public String getFiltroEndereco() {
        return filtroEndereco;
    }

    public void setFiltroEndereco(String filtroEndereco) {
        this.filtroEndereco = filtroEndereco;
    }

    public String getFiltroNomeFantasia() {
        return filtroNomeFantasia;
    }

    public void setFiltroNomeFantasia(String filtroNomeFantasia) {
        this.filtroNomeFantasia = filtroNomeFantasia;
    }

    public List<CadastroEconomico> getLista() {
        return lista;
    }

    public void setLista(List<CadastroEconomico> lista) {
        this.lista = lista;
    }

    public String getIdComponente() {
        return idComponente;
    }

    public void setIdComponente(String idComponente) {
        this.idComponente = idComponente;
    }

    public CadastroEconomico getCadastroEconomicoSelecionado() {
        return cadastroEconomicoSelecionado;
    }

    public void setCadastroEconomicoSelecionado(CadastroEconomico cadastroEconomicoSelecionado) {
        this.cadastroEconomicoSelecionado = cadastroEconomicoSelecionado;
    }

    public List<SelectItem> getSituacaoCadastrais() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoCadastralCadastroEconomico object : SituacaoCadastralCadastroEconomico.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getClassificacaoAtividade() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ClassificacaoAtividade object : ClassificacaoAtividade.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return cadastroEconomicoFacade.buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte.trim());
    }

    public List<CadastroEconomico> completaCadastroEconomicoJdbc(String parte) {
        return jdbcCadastroEconomicoDAO.listaCadastroEconomico(parte.trim());
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, cadastroEconomicoFacade);
        }
        return converterCadastroEconomico;
    }

    public void filtrar() {
        lista = cadastroEconomicoFacade.listaPorFiltro(inicio, maximoRegistrosTabela, filtroCI, filtroCnae, filtroCpfCnpj, filtroPessoa, filtroProcesso, situacaoCadastral, filtroClassificacaoAtividade, filtroEndereco, filtroNomeFantasia);
        lista = new ArrayList(new HashSet(lista));
        atualizaTabela();
    }

    public void atualizaTabela() {
        atualizaComponente("tabela");
    }

    public void montaMascaraCpfCnpj() {
        if (filtroTipoCpfCnpj) {
            mascaraTipoCpfCnpj = "999.999.999-99";
        } else {
            mascaraTipoCpfCnpj = "99.999.999/9999-99";
        }
        atualizaComponente("cpfCnpj");
    }

    public String getMascara() {
        if (!filtroTipoCpfCnpj) {
            return "99.999.999/9999-99";
        }
        return "999.999.999-99";
    }

    public void novo(String idComponente, CadastroEconomico cadastro) {
        this.idComponente = idComponente;
        this.cadastroEconomicoSelecionado = cadastro;
        //System.out.println("- id componente: " + idComponente);
        limparCampos();
    }

    public void setIdForm(ActionEvent evento) {
        UIComponent component = evento.getComponent();
        String idFormulario = getForm(component).getClientId();
        idComponente = ":" + idFormulario + ":" + idComponente + ":";
        //System.out.println("id componente: " + idComponente);
        atualizaPanelGridFiltros();
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

    public void atualizaComponente(String idComponente) {
        String id = getIdComponente() + idComponente;
//        //System.out.println("update 1 " + getIdComponente());
//        //System.out.println("update 2 " + idComponente);
//        //System.out.println("update 3 " + id);
        RequestContext.getCurrentInstance().update(id);
    }

    public void atualizaPanelGridFiltros() {
        atualizaComponente("panelGridFiltros");
    }

    public void limparCampos() {
        mascaraTipoCpfCnpj = "999.999.999-99";
        filtroTipoCpfCnpj = true;
        lista = new ArrayList<>();
        Util.limparCampos(this);
        atualizaPanelGridFiltros();
        atualizaTabela();
    }

    public void selecionarCadastros(CadastroEconomico cadastro) {
        cadastroEconomicoSelecionado = cadastro;
        atualizaTabela();
        atualizaPanelBotoes();
    }

    public void selecionarCadastro(SelectEvent cadastro) {
        //System.out.println("Cadastro - " + cadastro.getObject());
        cadastroEconomicoSelecionado = (CadastroEconomico) cadastro.getObject();
        atualizaTabela();
        atualizaPanelBotoes();
    }

    public void setaCadastroNull() {
        //System.out.println("Cadastro - Null");
        cadastroEconomicoSelecionado = null;
        atualizaPanelBotoes();
    }

    private void atualizaPanelBotoes() {
        atualizaComponente("panelGridBotoes");
    }

    //==========================================================================
    public int getMaxRegistrosNaLista() {
        return AbstractFacade.MAX_RESULTADOS_NA_CONSULTA - 1;
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

    public void acaoBotaoFiltrar() {
        inicio = 0;
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

    public int getMaximoRegistrosTabela() {
        return maximoRegistrosTabela;
    }

    public void setMaximoRegistrosTabela(int maximoRegistrosTabela) {
        this.maximoRegistrosTabela = maximoRegistrosTabela;
    }

    public Pessoa getPessoa() {
        if (pessoa != null) {
            pessoa.getEnderecos().size();
            pessoa.getTelefones().size();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public void selecionaCadastro(SelectEvent e) {
        CadastroEconomico ce = (CadastroEconomico) e.getObject();
        pessoa = pessoaFacade.recarregar(ce.getPessoa());

    }

    public void execute(SelectEvent e) {
        CadastroEconomico ce = (CadastroEconomico) e.getObject();

    }

    public boolean getFisica() {
        if (pessoa != null) {
            return (pessoa instanceof PessoaFisica);
        }
        return false;
    }

    public void mostraDetalhes() {
        RequestContext.getCurrentInstance().execute("dlgdetalhes.show()");
    }

    public void escondeDetalhes() {
        RequestContext.getCurrentInstance().execute("dlgdetalhes.hide()");
    }

    public void setaCadastroEconomico(CadastroEconomico economico) {
        cadastroEconomicoSelecionado = economico;
        if (cadastroEconomicoSelecionado != null && cadastroEconomicoSelecionado.getPessoa() != null) {
            if (cadastroEconomicoSelecionado.getPessoa() instanceof PessoaFisica) {
                pessoa = pessoaFacade.recuperarPF(cadastroEconomicoSelecionado.getPessoa().getId());
            } else if (cadastroEconomicoSelecionado.getPessoa() instanceof PessoaJuridica) {
                pessoa = pessoaFacade.recuperarPJ(cadastroEconomicoSelecionado.getPessoa().getId());
            }
        }
    }
}
