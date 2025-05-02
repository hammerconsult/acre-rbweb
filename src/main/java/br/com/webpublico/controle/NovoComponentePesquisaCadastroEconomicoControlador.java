/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.EnderecoCadastroEconomico;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.Telefone;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroEconomicoDAO;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.anotacoes.Limpavel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Renato
 */
@ManagedBean
@ViewScoped
public class NovoComponentePesquisaCadastroEconomicoControlador implements Serializable {

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
    private JdbcCadastroEconomicoDAO jdbcCadastroEconomicoDAO;
    private Map<String, ComponentePesquisaCMC> componente;


    public void novo(String id) {
        if (componente == null) {
            componente = Maps.newHashMap();
        }
        ComponentePesquisaCMC componentePesquisaCMC = new ComponentePesquisaCMC(cadastroEconomicoFacade);
        componente.put(id, componentePesquisaCMC);
    }


    public void novoCMC(String id) {
        Web.navegacao(componente.get(id).origem, componente.get(id).novoCMC);
    }


    public void navega() {
        ////System.out.println("navega!!!");
    }

    public void itemSelect() {
        ////System.out.println("itemSelect!!!");
    }


    public Map<String, ComponentePesquisaCMC> getComponente() {
        return componente;
    }

    public void verCMC(String id, CadastroEconomico c) {
        Web.navegacao(componente.get(id).origem, componente.get(id).verCMC);
    }


    public void editaCMC(String id, CadastroEconomico c) {
        Web.navegacao(componente.get(id).origem, componente.get(id).editaCMC);
    }

    public static class ComponentePesquisaCMC {
        private List<SituacaoCadastralCadastroEconomico> situacoesSelecionadas;
        private List<SituacaoCadastralCadastroEconomico> situacoesDisponiveis;
        private Boolean pesquisaCMC;
        private CadastroEconomicoFacade cadastroEconomicoFacade;
        private String novoCMC, verCMC, editaCMC, origem;
        private List<Telefone> telefones;
        private EnderecoCadastroEconomico endereco;

        public List<Telefone> getTelefones() {
            return telefones;
        }

        public void setTelefones(List<Telefone> telefones) {
            this.telefones = telefones;
        }

        public EnderecoCadastroEconomico getEndereco() {
            return endereco;
        }

        public void setEndereco(EnderecoCadastroEconomico endereco) {
            this.endereco = endereco;
        }

        public String getNovoCMC() {
            return novoCMC;
        }

        public String getVerCMC() {
            return verCMC;
        }

        public String getEditaCMC() {
            return editaCMC;
        }

        public void setEditaCMC(String editaCMC) {
            this.editaCMC = editaCMC;
        }

        public void setVerCMC(String verCMC) {
            this.verCMC = verCMC;
        }

        public void setNovoCMC(String novoCMC) {
            this.novoCMC = novoCMC;
        }

        public ComponentePesquisaCMC(CadastroEconomicoFacade cadastroEconomicoFacade) {
            this.cadastroEconomicoFacade = cadastroEconomicoFacade;
        }

        public void setSituacoesDisponiveis(List<SituacaoCadastralCadastroEconomico> situacoesDisponiveis) {
            if (situacoesDisponiveis != null) {
                situacoesSelecionadas = Lists.newArrayList(situacoesDisponiveis);
            } else {
                situacoesSelecionadas = Lists.newArrayList(SituacaoCadastralCadastroEconomico.values());
                this.situacoesDisponiveis = Lists.newArrayList(SituacaoCadastralCadastroEconomico.values());
            }
        }

        public void setPesquisaCMC(String pesquisaCMC) {
            this.pesquisaCMC = Boolean.parseBoolean(pesquisaCMC);
        }

        public String getOrigem() {
            return origem;
        }

        public void setOrigem(String origem) {
            this.origem = origem;
        }

        public List<SituacaoCadastralCadastroEconomico> getSitucoesCMC() {
            List<SituacaoCadastralCadastroEconomico> situacoes = Lists.newArrayList();
            if (situacoesDisponiveis != null) {
                for (SituacaoCadastralCadastroEconomico situacao : situacoesDisponiveis) {
                    if (!situacoesSelecionadas.contains(situacao)) {
                        situacoes.add(situacao);
                    }
                }
            }
            return situacoes;
        }

        public List<SituacaoCadastralCadastroEconomico> getSituacoesSelecionadas() {
            return situacoesSelecionadas;
        }

        public void setSituacoesSelecionadas(List<SituacaoCadastralCadastroEconomico> situacoesSelecionadas) {
            this.situacoesSelecionadas = situacoesSelecionadas;
        }

        public void addSituacao(SituacaoCadastralCadastroEconomico situacao) {
            if (!situacoesSelecionadas.contains(situacao)) {
                situacoesSelecionadas.add(situacao);
            }
        }

        public void removeSituacao(SituacaoCadastralCadastroEconomico situacao) {
            if (situacoesSelecionadas.size() > 1) {
                situacoesSelecionadas.remove(situacao);
            }
        }

        public void addTodasSituacoes() {
            for (SituacaoCadastralCadastroEconomico situacao : Lists.newArrayList(SituacaoCadastralCadastroEconomico.values())) {
                addSituacao(situacao);
            }
        }

        public List<CadastroEconomico> completaCMC(String parte) {
            if (situacoesSelecionadas != null && !situacoesSelecionadas.isEmpty()) {
                SituacaoCadastralCadastroEconomico[] sits = situacoesSelecionadas.toArray(new SituacaoCadastralCadastroEconomico[situacoesSelecionadas.size()]);
                return getCMC(parte, sits);
            } else {
                return getCMC(parte, null);
            }
        }

        private List<CadastroEconomico> getCMC(String parte, SituacaoCadastralCadastroEconomico[] sits) {
            return cadastroEconomicoFacade.listaCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJPorSituacao(parte, sits);
        }

        public void carregaListasCMC(SelectEvent evento) {
            if (evento != null) {
                carregarListasCMCCadastroEconomico((CadastroEconomico) evento.getObject());
            }
        }

        public void carregarListasCMCCadastroEconomico(CadastroEconomico c) {
            if (c != null && c.getId() != null) {
                telefones = cadastroEconomicoFacade.getPessoaFacade().telefonePorPessoa(c.getPessoa());
                endereco = cadastroEconomicoFacade.getLocalizacao(c);
            }
        }
    }


    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, cadastroEconomicoFacade);
        }
        return converterCadastroEconomico;
    }

    public List<SituacaoCadastralCadastroEconomico> situacaoPorDescricao(List<String> descricoes) {
        List<SituacaoCadastralCadastroEconomico> situacoes = Lists.newArrayList();
        for (String situacao : descricoes) {
            try {
                situacoes.add(SituacaoCadastralCadastroEconomico.valueOf(situacao));
            } catch (Exception e) {
                situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
            }
        }
        return situacoes;
    }
}
