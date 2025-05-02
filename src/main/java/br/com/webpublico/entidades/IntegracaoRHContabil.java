package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.ContaDespesaTipoContaDespesaIntegracaoRHContabil;
import br.com.webpublico.entidadesauxiliares.FonteIntegracaoRHContabil;
import br.com.webpublico.entidadesauxiliares.FonteItemIntegracaoRHContabil;
import br.com.webpublico.entidadesauxiliares.PessoaClasseIntegracaoRHContabil;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.integracao.TipoContabilizacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Integração RH/Contábil")
public class IntegracaoRHContabil extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Etiqueta("Data de Registro")
    private Date dataRegistro;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Mês")
    private Mes mes;
    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Enumerated(value = EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Folha")
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação")
    private SituacaoIntegracaoRHContabil situacao;
    @OneToOne(cascade = CascadeType.ALL)
    private ParametroRHContabil parametro;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "integracaoRHContabil")
    private List<TipoIntegracaoRHContabil> tipos;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "integracaoRHContabil")
    private List<IntegracaoRHContabilFolha> folhasDePagamento;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "integracaoRHContabil")
    private List<ItemIntegracaoRHContabil> itens;

    @Transient
    private List<FonteItemIntegracaoRHContabil> fontes;
    @Transient
    private List<FonteIntegracaoRHContabil> valoresPorFonte;
    @Transient
    private List<PessoaClasseIntegracaoRHContabil> pessoasClasses;
    @Transient
    private List<ContaDespesaTipoContaDespesaIntegracaoRHContabil> contasTipos;
    @Transient
    private List<RetencaoIntegracaoRHContabil> retencoesSemPagamento;
    @Transient
    private List<ItemIntegracaoRHContabil> empenhos;
    @Transient
    private List<ItemIntegracaoRHContabil> obrigacoes;

    public IntegracaoRHContabil() {
        this.itens = Lists.newArrayList();
        this.folhasDePagamento = Lists.newArrayList();
        this.tipos = Lists.newArrayList();
        this.fontes = Lists.newArrayList();
        this.valoresPorFonte = Lists.newArrayList();
        this.pessoasClasses = Lists.newArrayList();
        this.contasTipos = Lists.newArrayList();
        this.retencoesSemPagamento = Lists.newArrayList();
        this.empenhos = Lists.newArrayList();
        this.obrigacoes = Lists.newArrayList();
        this.situacao = SituacaoIntegracaoRHContabil.ABERTO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public List<ItemIntegracaoRHContabil> getItens() {
        return itens;
    }

    public void setItens(List<ItemIntegracaoRHContabil> itens) {
        this.itens = itens;
    }

    public List<IntegracaoRHContabilFolha> getFolhasDePagamento() {
        return folhasDePagamento;
    }

    public void setFolhasDePagamento(List<IntegracaoRHContabilFolha> folhasDePagamento) {
        this.folhasDePagamento = folhasDePagamento;
    }

    public List<TipoIntegracaoRHContabil> getTipos() {
        return tipos;
    }

    public void setTipos(List<TipoIntegracaoRHContabil> tipos) {
        this.tipos = tipos;
    }

    public ParametroRHContabil getParametro() {
        return parametro;
    }

    public void setParametro(ParametroRHContabil parametro) {
        this.parametro = parametro;
    }

    public List<FonteItemIntegracaoRHContabil> getFontes() {
        return fontes;
    }

    public void setFontes(List<FonteItemIntegracaoRHContabil> fontes) {
        this.fontes = fontes;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public List<PessoaClasseIntegracaoRHContabil> getPessoasClasses() {
        return pessoasClasses;
    }

    public void setPessoasClasses(List<PessoaClasseIntegracaoRHContabil> pessoasClasses) {
        this.pessoasClasses = pessoasClasses;
    }

    public List<ContaDespesaTipoContaDespesaIntegracaoRHContabil> getContasTipos() {
        return contasTipos;
    }

    public void setContasTipos(List<ContaDespesaTipoContaDespesaIntegracaoRHContabil> contasTipos) {
        this.contasTipos = contasTipos;
    }

    public List<RetencaoIntegracaoRHContabil> getRetencoes() {
        List<RetencaoIntegracaoRHContabil> retorno = Lists.newArrayList();
        for (ItemIntegracaoRHContabil item : getEmpenhos()) {
            retorno.addAll(item.getRetencoes());
        }
        return retorno;
    }


    public SituacaoIntegracaoRHContabil getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoIntegracaoRHContabil situacao) {
        this.situacao = situacao;
    }

    public List<RetencaoIntegracaoRHContabil> getRetencoesSemPagamento() {
        return retencoesSemPagamento;
    }

    public void setRetencoesSemPagamento(List<RetencaoIntegracaoRHContabil> retencoesSemPagamento) {
        this.retencoesSemPagamento = retencoesSemPagamento;
    }

    public List<FonteIntegracaoRHContabil> getValoresPorFonte() {
        return valoresPorFonte;
    }

    public void setValoresPorFonte(List<FonteIntegracaoRHContabil> valoresPorFonte) {
        this.valoresPorFonte = valoresPorFonte;
    }

    public List<ItemIntegracaoRHContabil> getEmpenhos() {
        if (empenhos.isEmpty()) {
            for (ItemIntegracaoRHContabil item : this.itens) {
                if (TipoContabilizacao.EMPENHO.equals(item.getTipoContabilizacao())) {
                    empenhos.add(item);
                }
            }
        }
        return empenhos;
    }

    public void setEmpenhos(List<ItemIntegracaoRHContabil> empenhos) {
        this.empenhos = empenhos;
    }

    public List<ItemIntegracaoRHContabil> getObrigacoes() {
        if (obrigacoes.isEmpty()) {
            for (ItemIntegracaoRHContabil item : this.itens) {
                if (TipoContabilizacao.OBRIGACAO_A_APAGAR.equals(item.getTipoContabilizacao())) {
                    obrigacoes.add(item);
                }
            }
        }
        return obrigacoes;
    }

    public void setObrigacoes(List<ItemIntegracaoRHContabil> obrigacoes) {
        this.obrigacoes = obrigacoes;
    }

    public BigDecimal getValorTotal() {
        try {
            BigDecimal valor = BigDecimal.ZERO;
            for (FonteItemIntegracaoRHContabil previsoe : fontes) {
                valor = valor.add(previsoe.getValor());
            }
            return valor;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSaldoDisponivel() {
        try {
            BigDecimal valor = BigDecimal.ZERO;
            for (FonteItemIntegracaoRHContabil previsoe : fontes) {
                valor = valor.add(previsoe.getSaldoDisponivel());
            }
            return valor;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public Boolean isTipoSelecionado(TipoIntegradorRHContabil tipo) {
        for (TipoIntegracaoRHContabil tipoIntegracaoRHContabil : getTipos()) {
            if (tipoIntegracaoRHContabil.getTipo().equals(tipo)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public ServidorIntegracaoRHContabil getServidorIntegracao(RetencaoIntegracaoRHContabil retencao) {
        for (ItemIntegracaoRHContabil itemIntegracaoRHContabil : getEmpenhos()) {
            for (ServidorIntegracaoRHContabil servidor : itemIntegracaoRHContabil.getServidores()) {
                for (ServidorRetencaoRHContabil servidorRetencao : retencao.getServidores()) {
                    if (TipoContaDespesa.PESSOAL_ENCARGOS.equals(((ContaDespesa) itemIntegracaoRHContabil.getDesdobramento()).getTipoContaDespesa())
                        && TipoContabilizacao.RETENCAO_EXTRAORCAMENTARIA_CONSIGNACOES.equals(servidorRetencao.getRetencao().getTipoContabilizacao())) {
                        if (servidor.getVinculoFP().getId().equals(servidorRetencao.getVinculoFP().getId())) {
                            return servidor;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Boolean isProcessado() {
        return SituacaoIntegracaoRHContabil.PROCESSADO.equals(situacao);
    }

    public Boolean isValidado() {
        return SituacaoIntegracaoRHContabil.VALIDADO.equals(situacao);
    }

    public Boolean isAberto() {
        return SituacaoIntegracaoRHContabil.ABERTO.equals(situacao);
    }

    public BigDecimal getValorTotalObrigacoesAPagar() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ItemIntegracaoRHContabil item : getObrigacoes()) {
            retorno = retorno.add(item.getValor());
        }
        return retorno;
    }

    public Integer getQuantidadeEmpenho() {
        Integer retorno = 0;
        for (ItemIntegracaoRHContabil item : getEmpenhos()) {
            if (TipoContabilizacao.EMPENHO.equals(item.getTipoContabilizacao())) {
                retorno++;
            }
        }
        return retorno;
    }

    public Integer getQuantidadeObrigacaoPagar() {
        Integer retorno = 0;
        for (ItemIntegracaoRHContabil item : getObrigacoes()) {
            if (TipoContabilizacao.OBRIGACAO_A_APAGAR.equals(item.getTipoContabilizacao())) {
                retorno++;
            }
        }
        return retorno;
    }

    public BigDecimal getValoTotalEmpenhadoBruto() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ItemIntegracaoRHContabil item : getEmpenhos()) {
            if (TipoContabilizacao.EMPENHO.equals(item.getTipoContabilizacao())) {
                retorno = retorno.add(item.getValor());
            }
        }
        return retorno;
    }


    public BigDecimal getValoTotalEmpenhadoLiquido() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ItemIntegracaoRHContabil item : getEmpenhos()) {
            if (TipoContabilizacao.EMPENHO.equals(item.getTipoContabilizacao())) {
                retorno = retorno.add(item.getValor().subtract(item.getValorTotalRetencoes()));
            }
        }
        return retorno;
    }

    public BigDecimal getValoTotalRetencoes() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ItemIntegracaoRHContabil item : getEmpenhos()) {
            for (RetencaoIntegracaoRHContabil retencao : item.getRetencoes()) {
                retorno = retorno.add(retencao.getValor());
            }
        }
        for (RetencaoIntegracaoRHContabil retencao : getRetencoesSemPagamento()) {
            retorno = retorno.add(retencao.getValor());
        }
        return retorno;
    }

    public BigDecimal getValorTotalBrutoPorFonte() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (FonteIntegracaoRHContabil item : valoresPorFonte) {
            retorno = retorno.add(item.getValorBruto());
        }
        return retorno;
    }

    public BigDecimal getValorTotalRetencaoPorFonte() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (FonteIntegracaoRHContabil item : valoresPorFonte) {
            retorno = retorno.add(item.getValorRetencao());
        }
        return retorno;
    }


    public BigDecimal getValorTotalLiquidoPorFonte() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (FonteIntegracaoRHContabil item : valoresPorFonte) {
            retorno = retorno.add(item.getValorLiquido());
        }
        return retorno;
    }

}
