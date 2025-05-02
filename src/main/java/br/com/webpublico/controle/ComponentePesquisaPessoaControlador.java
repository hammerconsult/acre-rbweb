/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.EnderecoFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcPessoaDAO;
import br.com.webpublico.util.ConverterAutoComplete;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wellington
 */
@ManagedBean
@SessionScoped
public class ComponentePesquisaPessoaControlador implements Serializable {

    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    private PessoaFisica pessoaFisica;
    private List<PessoaFisica> listaPessoaFisica;
    private PessoaJuridica pessoaJuridica;
    private List<PessoaJuridica> listaPessoaJuridica;
    private Pessoa pessoaSelecionada;
    private ConverterAutoComplete converterPessoa;
    private List<Telefone> listaTelefone;
    private List<EnderecoCorreio> listaEnderecoCorreio;
    private transient final ApplicationContext ap;
    private JdbcPessoaDAO jdbcPessoaDAO;

    //CONSTRUTORES
    public ComponentePesquisaPessoaControlador() {
        this.ap = ContextLoader.getCurrentWebApplicationContext();
        jdbcPessoaDAO = (JdbcPessoaDAO)this.ap.getBean("consultaPessoaDAO");
        novo();
    }

    //METODOS
    public void novo() {
        pessoaSelecionada = null;
        pessoaFisica = new PessoaFisica();
        pessoaJuridica = new PessoaJuridica();
        listaPessoaFisica = new ArrayList<>();
        listaPessoaJuridica = new ArrayList<>();
    }

    public void novoPessoaFisica() {
        pessoaSelecionada = null;
        pessoaFisica = new PessoaFisica();
        listaPessoaFisica = new ArrayList<>();
    }

    public void novoPessoaJuridica() {
        pessoaSelecionada = null;
        pessoaJuridica = new PessoaJuridica();
        listaPessoaJuridica = new ArrayList<>();
    }

    public String wherePessoaFisica() {
        String where = "";
        String juncao = " where ";
        if (!pessoaFisica.getNome().isEmpty()) {
            where += juncao + " lower(obj.nome) like '%" + pessoaFisica.getNome().toLowerCase() + "%'";
            juncao = " or ";
        }
        if (!pessoaFisica.getCpf().isEmpty()) {
            where += juncao + " (replace(replace(replace(obj.cpf,'.',''),'-',''),'/','') like '%" + pessoaFisica.getCpf() + "%') or "
                    + " (obj.cpf like '%" + pessoaFisica.getCpf() + "%') ";
            juncao = " or ";
        }
        return where;
    }

    public String wherePessoaJuridica() {
        String where = "";
        String juncao = " where ";
        if (!pessoaJuridica.getRazaoSocial().isEmpty()) {
            where += juncao + " lower(obj.razaoSocial) like '%" + pessoaJuridica.getRazaoSocial().toLowerCase() + "%'";
            juncao = " or ";
        }
        if (!pessoaJuridica.getCnpj().isEmpty()) {
            where += juncao + " (replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like '%" + pessoaJuridica.getCnpj() + "%') or "
                    + " (obj.cnpj like '%" + pessoaJuridica.getCnpj() + "%') ";
            juncao = " or ";
        }
        if (!pessoaJuridica.getNomeFantasia().isEmpty()) {
            where += juncao + " lower(obj.nomeFantasia) like '%" + pessoaJuridica.getNomeFantasia().toLowerCase() + "%'";
            juncao = " or ";
        }
        return where;
    }

    public void filtrarPessoasFisicas() {
        listaPessoaFisica = pessoaFacade.retornaPessoasFisicas(wherePessoaFisica());
    }

    public void filtrarPessoasJuridicas() {
        listaPessoaJuridica = pessoaFacade.retornaPessoasJuridicas(wherePessoaJuridica());
    }

    public void selecionaPessoa(Pessoa pessoa) {
        pessoaSelecionada = pessoa;
    }

    public void setaNullPessoaSelecionada() {
        pessoaSelecionada = null;
    }

    public boolean pessoaSelecionada(Pessoa pessoa) {
        boolean retorno = false;
        if (pessoaSelecionada != null) {
            retorno = pessoaSelecionada.equals(pessoa);
        }
        return retorno;
    }

    public List<Pessoa> completaTodasPessoa(String filtro) {
        return pessoaFacade.listaTodasPessoas(filtro.trim());
    }

    public List<Pessoa> completaPessoaJDBC(String filtro){
        return  jdbcPessoaDAO.lista(filtro.trim());
    }


    public List<Pessoa> completaPessoaFisica(String filtro) {
        return pessoaFacade.listaPessoasFisicas(filtro.trim());
    }

    public List<Pessoa> completaPessoaJuridica(String filtro) {
        return pessoaFacade.listaPessoasJuridicas(filtro.trim());
    }

    public List<Telefone> listaTelefones(Pessoa pessoa) {
        List<Telefone> lista = new ArrayList<>();
        lista = pessoaFacade.telefonePorPessoa(pessoa);
        return lista;
    }

    //GETTERS E SETTERS
    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterPessoa;
    }

    public Pessoa getPessoaSelecionada() {
        return pessoaSelecionada;
    }

    public List<Telefone> getListaTelefone() {
        return pessoaFacade.telefonePorPessoa(pessoaSelecionada);
    }

    public void setListaTelefone(List<Telefone> listaTelefone) {
        this.listaTelefone = listaTelefone;
    }

    public List<EnderecoCorreio> getListaEnderecoCorreio() {
        return listaEnderecoCorreio;
    }

    public void setListaEnderecoCorreio(List<EnderecoCorreio> listaEnderecoCorreio) {
        this.listaEnderecoCorreio = listaEnderecoCorreio;
    }

    public void setPessoaSelecionada(Pessoa pessoaSelecionada) {
        this.pessoaSelecionada = pessoaSelecionada;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public List<PessoaFisica> getListaPessoaFisica() {
        return listaPessoaFisica;
    }

    public void setListaPessoaFisica(List<PessoaFisica> listaPessoaFisica) {
        this.listaPessoaFisica = listaPessoaFisica;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public List<PessoaJuridica> getListaPessoaJuridica() {
        return listaPessoaJuridica;
    }

    public void setListaPessoaJuridica(List<PessoaJuridica> listaPessoaJuridica) {
        this.listaPessoaJuridica = listaPessoaJuridica;
    }

    public List<Telefone> recuperaTelefonesDaPessoa(Pessoa p) {
        List<Telefone> retorno = new ArrayList<>();
        if (p != null) {
            return pessoaFacade.telefonePorPessoa(p);
        }
        return retorno;
    }

    public List<EnderecoCorreio> recuperaEnderecosDaPessoa(Pessoa p) {
        List<EnderecoCorreio> retorno = new ArrayList<>();
        if (p != null) {
            retorno = enderecoFacade.retornaEnderecoCorreioDaPessoa(p);
        }
        return retorno;
    }

    public boolean ehPessoaFisica(Pessoa pessoa) {
        return pessoa instanceof PessoaFisica;
    }

    public boolean ehPessoaJuridica(Pessoa pessoa) {
        return pessoa instanceof PessoaJuridica;
    }
}
