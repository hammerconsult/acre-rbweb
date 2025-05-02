package br.com.webpublico.entidades;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoSituacaoProgramacaoCobranca;
import br.com.webpublico.enums.TipoValorProgramacaoCobranca;
import br.com.webpublico.enums.tributario.TipoDamEmitido;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Julio Cesar
 * Date: 31/07/13
 * Time: 10:01
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "ProgaramacaoCobranca")
@Entity
@Cacheable
@Audited
@Etiqueta("Programação de Cobrança")
public class ProgramacaoCobranca implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Etiqueta("Número")
    @Pesquisavel
    @Tabelavel
    private String numero;
    @Etiqueta("Descrição")
    @Pesquisavel
    @Tabelavel
    private String descricao;
    @Etiqueta("Unidade Organizacional")
    @OneToOne
    @Pesquisavel
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Usuário")
    @OneToOne
    @Pesquisavel
    @Tabelavel
    private UsuarioSistema usuarioSistema;
    @Enumerated(EnumType.STRING)
    private TipoValorProgramacaoCobranca tipoValorProgramacaoCobranca;
    @OneToMany(mappedBy = "programacaoCobranca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SituacaoProgramacaoCobranca> listaSituacaoProgramacaoCobrancas;
    @OneToMany(mappedBy = "programacaoCobranca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemProgramacaoCobranca> listaItemProgramacaoCobrancas;
    @Temporal(TemporalType.DATE)
    private Date vencimento;
    @Temporal(TemporalType.DATE)
    private Date dataProgramacaoCobranca;
    @OneToOne
    private NotificacaoCobrancaAdmin notificacaoCobrancaAdmin;
    @Etiqueta(value = "Situação")
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoSituacaoProgramacaoCobranca ultimaSituacao;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Etiqueta("Tipo de Cadastro")
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoCadastroTributario tipoCadastro;
    @Limpavel
    @ManyToOne
    private Lote loteInicial;
    @Limpavel
    @ManyToOne
    private Lote loteFinal;
    @Limpavel
    @ManyToOne
    private Quadra quadraInicial;
    @Limpavel
    @ManyToOne
    private Quadra quadraFinal;
    @Limpavel
    @ManyToOne
    private Setor setorFinal;
    @Limpavel
    @ManyToOne
    private Setor setorInicial;
    @OneToMany(mappedBy = "programacaoCobranca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramacaoCobrancaBairro> bairros;
    @OneToMany(mappedBy = "programacaoCobranca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramacaoCobrancaLogradouro> logradouros;
    @OneToMany(mappedBy = "programacaoCobranca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramacaoCobrancaServico> servicos;
    @OneToMany(mappedBy = "programacaoCobranca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramacaoCobrancaDivida> dividas;
    @OneToMany(mappedBy = "programacaoCobranca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramacaoCobrancaCNAE> cnaes;
    @OneToMany(mappedBy = "programacaoCobranca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramacaoCobrancaClassificacaoAtividade> atividades;
    @OneToMany(mappedBy = "programacaoCobranca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramacaoCobrancaPessoa> contribuintes;
    @Limpavel
    @ManyToOne
    private Exercicio exercicioInicial;
    @Limpavel
    @ManyToOne
    private Exercicio exercicioFinal;
    @Limpavel
    private String cadastroInicial;
    @Limpavel
    private String cadastroFinal;
    @Limpavel
    private String areaInicial;
    @Limpavel
    private String areaFinal;
    @Limpavel
    private BigDecimal valorInicial;
    @Limpavel
    private BigDecimal valorFinal;
    @Limpavel
    @Temporal(TemporalType.DATE)
    private Date vencimentoInicial;
    @Limpavel
    @Temporal(TemporalType.DATE)
    private Date vencimentoFinal;
    @Transient
    @Invisivel
    private List<ResultadoParcela> debitoSelecionados;
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    @Etiqueta("Ano do Protocolo")
    private String anoProtocolo;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Dam Emitido")
    private TipoDamEmitido tipoDamEmitido;
    @Etiqueta("Exercicio")
    @ManyToOne
    private Exercicio exercicio;
    @Etiqueta("Código")
    private Long codigo;

    public TipoDamEmitido getTipoDamEmitido() {
        return tipoDamEmitido;
    }

    public void setTipoDamEmitido(TipoDamEmitido tipoDamEmitido) {
        this.tipoDamEmitido = tipoDamEmitido;
    }

    public ProgramacaoCobranca() {
        this.criadoEm = System.currentTimeMillis();
        this.listaSituacaoProgramacaoCobrancas = Lists.newArrayList();
        this.listaItemProgramacaoCobrancas = Lists.newArrayList();
        this.dataProgramacaoCobranca = new Date();
        this.cnaes = Lists.newArrayList();
        this.dividas = Lists.newArrayList();
        this.servicos = Lists.newArrayList();
        this.bairros = Lists.newArrayList();
        this.logradouros = Lists.newArrayList();
        this.atividades = Lists.newArrayList();
        this.contribuintes = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional == null ? new UnidadeOrganizacional() : unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema == null ? new UsuarioSistema() : usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoValorProgramacaoCobranca getTipoValorProgramacaoCobranca() {
        return tipoValorProgramacaoCobranca;
    }

    public void setTipoValorProgramacaoCobranca(TipoValorProgramacaoCobranca tipoValorProgramacaoCobranca) {
        this.tipoValorProgramacaoCobranca = tipoValorProgramacaoCobranca;
    }

    public List<ItemProgramacaoCobranca> getListaItemProgramacaoCobrancas() {
        return listaItemProgramacaoCobrancas;
    }

    public void setListaDeItemProgramacaoCobrancas(List<ItemProgramacaoCobranca> listaItemProgramacaoCobrancas) {
        this.listaItemProgramacaoCobrancas = listaItemProgramacaoCobrancas;
    }

    public List<SituacaoProgramacaoCobranca> getListaSituacaoProgramacaoCobrancas() {
        return listaSituacaoProgramacaoCobrancas;
    }

    public void setListaSituacaoProgramacaoCobrancas(List<SituacaoProgramacaoCobranca> listaSituacaoProgramacaoCobrancas) {
        this.listaSituacaoProgramacaoCobrancas = listaSituacaoProgramacaoCobrancas;
    }

    public Date getDataProgramacaoCobranca() {
        return dataProgramacaoCobranca;
    }

    public void setDataProgramacaoCobranca(Date dataProgramacaoCobranca) {
        this.dataProgramacaoCobranca = dataProgramacaoCobranca;
    }

    public void setListaItemProgramacaoCobrancas(List<ItemProgramacaoCobranca> listaItemProgramacaoCobrancas) {
        this.listaItemProgramacaoCobrancas = listaItemProgramacaoCobrancas;
    }

    public NotificacaoCobrancaAdmin getNotificacaoCobrancaAdmin() {
        return notificacaoCobrancaAdmin;
    }

    public void setNotificacaoCobrancaAdmin(NotificacaoCobrancaAdmin notificacaoCobrancaAdmin) {
        this.notificacaoCobrancaAdmin = notificacaoCobrancaAdmin;
    }

    public TipoSituacaoProgramacaoCobranca getUltimaSituacao() {
        return ultimaSituacao;
    }

    public void setUltimaSituacao(TipoSituacaoProgramacaoCobranca ultimaSituacao) {
        this.ultimaSituacao = ultimaSituacao;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public Lote getLoteInicial() {
        return loteInicial;
    }

    public void setLoteInicial(Lote loteInicial) {
        this.loteInicial = loteInicial;
    }

    public Lote getLoteFinal() {
        return loteFinal;
    }

    public void setLoteFinal(Lote loteFinal) {
        this.loteFinal = loteFinal;
    }

    public Quadra getQuadraInicial() {
        return quadraInicial;
    }

    public void setQuadraInicial(Quadra quadraInicial) {
        this.quadraInicial = quadraInicial;
    }

    public Quadra getQuadraFinal() {
        return quadraFinal;
    }

    public void setQuadraFinal(Quadra quadraFinal) {
        this.quadraFinal = quadraFinal;
    }

    public Setor getSetorFinal() {
        return setorFinal;
    }

    public void setSetorFinal(Setor setorFinal) {
        this.setorFinal = setorFinal;
    }

    public Setor getSetorInicial() {
        return setorInicial;
    }

    public void setSetorInicial(Setor setorInicial) {
        this.setorInicial = setorInicial;
    }

    public List<ProgramacaoCobrancaBairro> getBairros() {
        return bairros;
    }

    public void setBairros(List<ProgramacaoCobrancaBairro> bairros) {
        this.bairros = bairros;
    }

    public List<ProgramacaoCobrancaLogradouro> getLogradouros() {
        return logradouros;
    }

    public void setLogradouros(List<ProgramacaoCobrancaLogradouro> logradouros) {
        this.logradouros = logradouros;
    }

    public List<ProgramacaoCobrancaServico> getServicos() {
        return servicos;
    }

    public void setServicos(List<ProgramacaoCobrancaServico> servicos) {
        this.servicos = servicos;
    }

    public List<ProgramacaoCobrancaDivida> getDividas() {
        return dividas;
    }

    public void setDividas(List<ProgramacaoCobrancaDivida> dividas) {
        this.dividas = dividas;
    }

    public List<ProgramacaoCobrancaCNAE> getCnaes() {
        return cnaes;
    }

    public void setCnaes(List<ProgramacaoCobrancaCNAE> cnaes) {
        this.cnaes = cnaes;
    }

    public List<ProgramacaoCobrancaClassificacaoAtividade> getAtividades() {
        return atividades;
    }

    public void setAtividades(List<ProgramacaoCobrancaClassificacaoAtividade> atividades) {
        this.atividades = atividades;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial.trim();
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal.trim();
    }

    public String getAreaInicial() {
        return areaInicial;
    }

    public void setAreaInicial(String areaInicial) {
        this.areaInicial = areaInicial.trim();
    }

    public String getAreaFinal() {
        return areaFinal;
    }

    public void setAreaFinal(String areaFinal) {
        this.areaFinal = areaFinal.trim();
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public Date getVencimentoInicial() {
        return vencimentoInicial;
    }

    public void setVencimentoInicial(Date vencimentoInicial) {
        this.vencimentoInicial = vencimentoInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public Date getVencimentoFinal() {
        return vencimentoFinal;
    }

    public void setVencimentoFinal(Date vencimentoFinal) {
        this.vencimentoFinal = vencimentoFinal;
    }

    public List<ProgramacaoCobrancaPessoa> getContribuintes() {
        return contribuintes;
    }

    public void setContribuintes(List<ProgramacaoCobrancaPessoa> contribuintes) {
        this.contribuintes = contribuintes;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public List<Long> retornaIdsDasDividasSelecionadas(){
        List<Long> retorna = new ArrayList<>();
        if (this.getDividas() != null && !this.getDividas().isEmpty()) {
            for (ProgramacaoCobrancaDivida d : this.getDividas()) {
                retorna.add(d.getDivida().getId());
            }
        }
        return retorna;
    }

    public List<ResultadoParcela> getDebitoSelecionados() {
        return debitoSelecionados;
    }

    public void setDebitoSelecionados(List<ResultadoParcela> debitoSelecionados) {
        this.debitoSelecionados = debitoSelecionados;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return numero;
    }

    public String getFiltros() {
        String filtro = "";
        if (tipoCadastro != null) {
            filtro += " Tipo de Cadastro: " + tipoCadastro.getDescricao();
        }
        if (cadastroInicial != null) {
            filtro += " Cadastro Inicial: " + cadastroInicial;
        }
        if (cadastroFinal != null) {
            filtro += " Cadastro Final: " + cadastroFinal;
        }
        if (exercicioInicial != null) {
            filtro += " Exercício Inicial: " + exercicioInicial;
        }
        if (exercicioFinal != null) {
            filtro += " Exercício Final: " + exercicioFinal;
        }
        if (loteInicial != null) {
            filtro += " Lote Inicial: " + loteInicial;
        }
        if (loteFinal != null) {
            filtro += " Lote Final: " + loteFinal;
        }
        if (setorInicial != null) {
            filtro += " Setor Inicial: " + setorInicial;
        }
        if (setorFinal != null) {
            filtro += " Setor Final: " + setorFinal;
        }
        if (quadraInicial != null) {
            filtro += " Quadra Inicial: " + quadraInicial;
        }
        if (quadraFinal != null) {
            filtro += " Quadra Final: " + quadraFinal;
        }
        if (bairros != null && !bairros.isEmpty()) {
            filtro += " Bairros: ";
            for (ProgramacaoCobrancaBairro bairro : bairros) {
                filtro += bairro.getBairro().getDescricao() + ", ";
            }
            filtro.substring(filtro.length() - 2);
        }
        if (logradouros != null && !logradouros.isEmpty()) {
            filtro += " Logradouros: ";
            for (ProgramacaoCobrancaLogradouro logradouro : logradouros) {
                filtro += logradouro.getLogradouro().getNome() + ", ";
            }
            filtro.substring(filtro.length() - 2);
        }
        if (cnaes != null && !cnaes.isEmpty()) {
            filtro += " CNAEs: ";
            for (ProgramacaoCobrancaCNAE cnae : cnaes) {
                filtro += cnae.getCnae().getCodigoCnae() + ", ";
            }
            filtro.substring(filtro.length() - 2);
        }
        if (servicos != null && !servicos.isEmpty()) {
            filtro += " Serviços: ";
            for (ProgramacaoCobrancaServico servico : servicos) {
                filtro += servico.getServico().getNome() + ", ";
            }
            filtro.substring(filtro.length() - 2);
        }
        if (atividades != null && !atividades.isEmpty()) {
            filtro += " Atividades: ";
            for (ProgramacaoCobrancaClassificacaoAtividade atividade : atividades) {
                filtro += atividade.getClassificacaoAtividade().getDescricao() + ", ";
            }
            filtro.substring(filtro.length() - 2);
        }
        if (dividas != null && !dividas.isEmpty()) {
            filtro += " Dívidas: ";
            for (ProgramacaoCobrancaDivida divida : dividas) {
                filtro += divida.getDivida().getDescricao() + ", ";
            }
            filtro.substring(filtro.length() - 2);
        }
        if (contribuintes != null && !contribuintes.isEmpty()) {
            filtro += " Contribuintes: ";
            for (ProgramacaoCobrancaPessoa contribuinte : contribuintes) {
                filtro += contribuinte.getContribuinte().getNomeCpfCnpj() + ", ";
            }
            filtro.substring(filtro.length() - 2);
        }
        return filtro;
    }

    public String getNumeroAnoProtocolo() {
        if (numeroProtocolo != null && anoProtocolo != null) {
            return numeroProtocolo + "/" + anoProtocolo;
        }
        return "";
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }
}
