/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CadastroRural;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.TipoAreaRural;
import br.com.webpublico.negocios.CadastroRuralFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.TipoAreaRuralFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroRuralDAO;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Renato
 */
@ManagedBean
@SessionScoped
public class ComponentePesquisaCadastroRuralControlador implements Serializable {

    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private TipoAreaRuralFacade tipoAreaRuralFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    private ConverterAutoComplete converterCadastroRural;
    private ConverterGenerico converterTipoAreaRual;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String nomePropriedade;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String localizacaoLote;
    @Limpavel(Limpavel.NULO)
    private TipoAreaRural tipoAreaRural;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String numeroIncra;
    @Limpavel(Limpavel.NULO)
    private Long codigo;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String proprietario;
    private List<CadastroRural> lista;
    private CadastroRural cadastroRuralSelecionado;
    private transient final ApplicationContext ap;
    private JdbcCadastroRuralDAO jdbcCadastroRuralDAO;

    public ComponentePesquisaCadastroRuralControlador() {
        this.ap = ContextLoader.getCurrentWebApplicationContext();
        this.jdbcCadastroRuralDAO = (JdbcCadastroRuralDAO) this.ap.getBean("cadastroRuralDAO");
    }

    public CadastroRural getCadastroRuralSelecionado() {
        return cadastroRuralSelecionado;
    }

    public void setCadastroRuralSelecionado(CadastroRural cadastroRuralSelecionado) {
        this.cadastroRuralSelecionado = cadastroRuralSelecionado;
    }

    public List<CadastroRural> getLista() {
        return lista;
    }

    public void setLista(List<CadastroRural> lista) {
        this.lista = lista;
    }

    public String getNomePropriedade() {
        return nomePropriedade;
    }

    public void setNomePropriedade(String nomePropriedade) {
        this.nomePropriedade = nomePropriedade;
    }

    public String getLocalizacaoLote() {
        return localizacaoLote;
    }

    public void setLocalizacaoLote(String localizacaoLote) {
        this.localizacaoLote = localizacaoLote;
    }

    public TipoAreaRural getTipoAreaRural() {
        return tipoAreaRural;
    }

    public void setTipoAreaRural(TipoAreaRural tipoAreaRural) {
        this.tipoAreaRural = tipoAreaRural;
    }

    public String getNumeroIncra() {
        return numeroIncra;
    }

    public void setNumeroIncra(String numeroIncra) {
        this.numeroIncra = numeroIncra;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getProprietario() {
        return proprietario;
    }

    public void setProprietario(String proprietario) {
        this.proprietario = proprietario;
    }

    public ConverterAutoComplete getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, cadastroRuralFacade);
        }
        return converterCadastroRural;
    }

    public Converter getConverterTipoAreaRual() {
        if (converterTipoAreaRual == null) {
            converterTipoAreaRual = new ConverterGenerico(TipoAreaRural.class, tipoAreaRuralFacade);
        }
        return converterTipoAreaRual;
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return cadastroRuralFacade.completaCodigoNomeLocalizacaoIncraNomeCpfCnpjProprietario(parte.trim());
    }

    public List<CadastroRural> completaCadastroRuralJdbc(String parte) {
        return jdbcCadastroRuralDAO.lista(parte);
    }

    public List<SelectItem> completaTipoAreaRural() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoAreaRural object : tipoAreaRuralFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void filtrar() {
        lista = cadastroRuralFacade.filtroCadastroRural(nomePropriedade, localizacaoLote, tipoAreaRural, numeroIncra, codigo, proprietario);
    }

    public void limpaCampos() {
        Util.limparCampos(this);
        lista = new ArrayList<>();
        cadastroRuralSelecionado = null;
    }

    public void teste() {
        //System.out.println("testeeee...: " + cadastroRuralSelecionado.getNomePropriedade());
    }

    public void selecionarCadastro(CadastroRural cadastro) {
        cadastroRuralSelecionado = cadastro;
    }

    public void setaCadastroNull() {
        cadastroRuralSelecionado = null;
    }

    public List<Pessoa> recuperaPessoasCadastro(CadastroRural cadastroRural) {
        return pessoaFacade.recuperaPessoasDoCadastro(cadastroRural);
    }
}
