/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.Telefone;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcPessoaDAO;
import br.com.webpublico.util.ConverterAutoComplete;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.primefaces.event.SelectEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Map;


@ManagedBean
@ViewScoped
public class NovoComponentePesquisaPessoaControlador implements Serializable {

    private transient final ApplicationContext ap;
    @EJB
    private PessoaFacade pessoaFacade;
    private ConverterAutoComplete converterPessoa;
    private JdbcPessoaDAO jdbcPessoaDAO;
    private Map<String, ComponentePesquisaPessoa> componente;


    public NovoComponentePesquisaPessoaControlador() {
        this.ap = ContextLoader.getCurrentWebApplicationContext();
        jdbcPessoaDAO = (JdbcPessoaDAO) this.ap.getBean("consultaPessoaDAO");
    }

    public void novo(String id) {
        if (componente == null) {
            componente = Maps.newHashMap();
        }
        ComponentePesquisaPessoa componentePesquisaPessoa = new ComponentePesquisaPessoa(pessoaFacade);
        componente.put(id, componentePesquisaPessoa);
    }

    public Map<String, ComponentePesquisaPessoa> getComponente() {
        return componente;
    }

    public List<Pessoa> completaPessoaJDBC(String filtro) {
        return jdbcPessoaDAO.lista(filtro.trim());
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterPessoa;
    }

    public void verPessoa(String id, Pessoa p) {
        if (p instanceof PessoaFisica) {
            Web.navegacao(componente.get(id).origem, componente.get(id).verFisica);
        } else {
            Web.navegacao(componente.get(id).origem, componente.get(id).verJuridica);
        }
    }

    public void editaPessoa(String id, Pessoa p) {
        if (p instanceof PessoaFisica) {
            Web.navegacao(componente.get(id).origem, componente.get(id).editaFisica);
        } else {
            Web.navegacao(componente.get(id).origem, componente.get(id).editaJuridica);
        }
    }

    public void novoPessoaFisica(String id) {
        Web.navegacao(componente.get(id).origem, componente.get(id).novoFisica);
    }

    public void novoPessoaJuridica(String id) {
        Web.navegacao(componente.get(id).origem, componente.get(id).novoJuridica);
    }

    public void navega() {
        ////System.out.println("navega!!!");
    }

    public void itemSelect() {
        ////System.out.println("itemSelect!!!");
    }

    public void itemBlur() {
        ////System.out.println("itemSelect!!!");
    }

    public List<SituacaoCadastralPessoa> situacaoPorDescricao(String descricao) {
        return Lists.newArrayList(SituacaoCadastralPessoa.valueOf(descricao));
    }

    public static class ComponentePesquisaPessoa {
        private List<SituacaoCadastralPessoa> situacoesSelecionadas;
        private List<SituacaoCadastralPessoa> situacoesDisponiveis;
        private Boolean pesquisaFisica, pesquisaJuridica, pesquisaServidores;
        private PessoaFacade pessoaFacade;
        private String novoFisica, novoJuridica, verFisica, verJuridica, editaFisica, editaJuridica, origem;
        private List<Telefone> telefones;
        private List<EnderecoCorreio> enderecos;

        public ComponentePesquisaPessoa(PessoaFacade pessoaFacade) {
            this.pessoaFacade = pessoaFacade;
        }

        public void setSituacoesDisponiveis(List<SituacaoCadastralPessoa> situacoesDisponiveis) {
            if (situacoesDisponiveis != null) {
                situacoesSelecionadas = Lists.newArrayList(situacoesDisponiveis);
            } else {
                situacoesSelecionadas = Lists.newArrayList(SituacaoCadastralPessoa.values());
                this.situacoesDisponiveis = Lists.newArrayList(SituacaoCadastralPessoa.values());
            }
        }

        public String getOrigem() {
            return origem;
        }

        public void setOrigem(String origem) {
            this.origem = origem;
        }

        public String getNovoFisica() {
            return novoFisica;
        }

        public void setNovoFisica(String novoFisica) {
            this.novoFisica = novoFisica;
        }

        public String getNovoJuridica() {
            return novoJuridica;
        }

        public void setNovoJuridica(String novoJuridica) {
            this.novoJuridica = novoJuridica;
        }

        public String getVerFisica() {
            return verFisica;
        }

        public void setVerFisica(String verFisica) {
            this.verFisica = verFisica;
        }

        public String getVerJuridica() {
            return verJuridica;
        }

        public void setVerJuridica(String verJuridica) {
            this.verJuridica = verJuridica;
        }

        public String getEditaFisica() {
            return editaFisica;
        }

        public void setEditaFisica(String editaFisica) {
            this.editaFisica = editaFisica;
        }

        public String getEditaJuridica() {
            return editaJuridica;
        }

        public void setEditaJuridica(String editaJuridica) {
            this.editaJuridica = editaJuridica;
        }

        public Boolean getPesquisaFisica() {
            return pesquisaFisica;
        }

        public void setPesquisaFisica(String pesquisaFisica) {
            this.pesquisaFisica = Boolean.parseBoolean(pesquisaFisica);
        }

        public Boolean getPesquisaJuridica() {
            return pesquisaJuridica;
        }

        public void setPesquisaJuridica(String pesquisaJuridica) {
            this.pesquisaJuridica = Boolean.valueOf(pesquisaJuridica);
        }

        public Boolean getPesquisaServidores() {
            return pesquisaServidores;
        }

        public void setPesquisaServidores(Boolean pesquisaServidores) {
            this.pesquisaServidores = pesquisaServidores;
        }

        public PessoaFacade getPessoaFacade() {
            return pessoaFacade;
        }

        public void setPessoaFacade(PessoaFacade pessoaFacade) {
            this.pessoaFacade = pessoaFacade;
        }

        public List<Telefone> getTelefones() {
            return telefones;
        }

        public List<EnderecoCorreio> getEnderecos() {
            return enderecos;
        }

        public List<SituacaoCadastralPessoa> getSitucoesPessoa() {
            List<SituacaoCadastralPessoa> situacoes = Lists.newArrayList();
            if (situacoesDisponiveis != null) {
                for (SituacaoCadastralPessoa situacao : situacoesDisponiveis) {
                    if (!situacoesSelecionadas.contains(situacao)) {
                        situacoes.add(situacao);
                    }
                }
            }
            return situacoes;
        }

        public List<SituacaoCadastralPessoa> getSituacoesSelecionadas() {
            return situacoesSelecionadas;
        }

        public void setSituacoesSelecionadas(List<SituacaoCadastralPessoa> situacoesSelecionadas) {
            this.situacoesSelecionadas = situacoesSelecionadas;
        }

        public void addSituacao(SituacaoCadastralPessoa situacao) {
            if (!situacoesSelecionadas.contains(situacao)) {
                situacoesSelecionadas.add(situacao);
            }
        }

        public void removeSituacao(SituacaoCadastralPessoa situacao) {
            if (situacoesSelecionadas.size() > 1) {
                situacoesSelecionadas.remove(situacao);
            }
        }

        public void addTodasSituacoes() {
            for (SituacaoCadastralPessoa situacao : Lists.newArrayList(SituacaoCadastralPessoa.values())) {
                addSituacao(situacao);
            }
        }

        public List<Pessoa> completaPessoa(String parte) {
            if (situacoesSelecionadas != null && !situacoesSelecionadas.isEmpty()) {
                SituacaoCadastralPessoa[] sits = situacoesSelecionadas.toArray(new SituacaoCadastralPessoa[situacoesSelecionadas.size()]);
                return getPessoas(parte, sits);
            } else {
                return getPessoas(parte, null);
            }
        }

        private List<Pessoa> getPessoas(String parte, SituacaoCadastralPessoa[] sits) {
            if (pesquisaServidores) {
                return pessoaFacade.listaPessoaComVinculoVigente(parte.trim(), sits);
            } else if (pesquisaFisica && pesquisaJuridica) {
                return pessoaFacade.listaTodasPessoas(parte.trim(), sits);
            } else if (pesquisaJuridica) {
                return pessoaFacade.listaPessoasJuridicas(parte.trim(), sits);
            } else {
                return pessoaFacade.listaPessoasFisicas(parte.trim(), sits);
            }
        }


        public void carregaListasPessoa(SelectEvent evento) {
            Pessoa p = (Pessoa) evento.getObject();
            telefones = pessoaFacade.telefonePorPessoa(p);
            enderecos = pessoaFacade.enderecoPorPessoa(p);
        }
    }
}
