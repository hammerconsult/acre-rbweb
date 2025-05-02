package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.tributario.consultadebitos.enums.EnumConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.enums.SituacaoIsencaoAcrescimoDTO;
import br.com.webpublico.tributario.consultadebitos.enums.TipoDeducaoIsencaoAcrescimoDTO;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Conta Corrente")
@Entity
@Audited
@Etiqueta("Processo de Dedução de Acréscimos")
public class ProcessoIsencaoAcrescimos extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código")
    @Tabelavel
    @Pesquisavel
    private Long codigo;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Exercício")
    private Exercicio exercicio;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data do Lançamento")
    private Date dataLancamento;
    @Temporal(TemporalType.DATE)
    @Invisivel
    private Date dataInicio;
    @Invisivel
    @Temporal(TemporalType.DATE)
    private Date dataFim;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Motivo")
    private String motivo;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Usuário do Sistema")
    private UsuarioSistema usuario;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Protocolo")
    private String protocolo;
    @ManyToOne
    private AtoLegal atoLegal;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "processoIsencaoAcrescimos")
    private List<IsencaoAcrescimoParcela> isencoesParcela;
    private Boolean isentaJuros;
    private Boolean isentaMulta;
    private Boolean isentaCorrecao;
    @ManyToOne
    private Exercicio exercicioCorrecao;
    @ManyToOne
    @Pesquisavel
    private Cadastro cadastro;
    @ManyToOne
    @Pesquisavel
    private Pessoa pessoa;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    private Situacao situacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCancelamento;
    private String motivoCancelamento;
    @ManyToOne
    private UsuarioSistema usuarioCancelamento;
    @Enumerated(EnumType.STRING)
    private TipoDeducao tipoDeducao;
    private BigDecimal percentualDeducao;
    private Boolean lancouDeducoes;

    public ProcessoIsencaoAcrescimos() {
        isencoesParcela = Lists.newArrayList();
        isentaJuros = false;
        isentaMulta = false;
        isentaCorrecao = false;
        situacao = Situacao.ATIVO;
        percentualDeducao = BigDecimal.ZERO;
        lancouDeducoes = Boolean.FALSE;
    }

    public ProcessoIsencaoAcrescimos(Long id, Long codigo, Exercicio exercicio, Date dataLancamento, Date dataInicio, Date dataFim, String protocolo, UsuarioSistema usuario, Boolean isentaJuros, Boolean isentaMulta, Boolean isentaCorrecao, Boolean isentaHonorarios, Pessoa pessoa, Cadastro cadastro, Situacao situacao) {
        this.id = id;
        this.codigo = codigo;
        this.exercicio = exercicio;
        this.dataLancamento = dataLancamento;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.protocolo = protocolo;
        this.usuario = usuario;
        this.isentaJuros = isentaJuros;
        this.isentaMulta = isentaMulta;
        this.isentaCorrecao = isentaCorrecao;
        this.pessoa = pessoa;
        this.cadastro = cadastro;
        this.situacao = situacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public List<IsencaoAcrescimoParcela> getIsencoesParcela() {
        return isencoesParcela;
    }

    public List<IsencaoAcrescimoParcela> getIsencoesParcelaComDeducoes() {
        if (!getLancouDeducoes()) {
            return Lists.newArrayList();
        }
        return isencoesParcela;
    }

    public List<IsencaoAcrescimoParcela> getIsencoesParcelaComDeducoesVer() {
        return isencoesParcela;
    }

    public void setIsencoesParcela(List<IsencaoAcrescimoParcela> isencoesParcela) {
        this.isencoesParcela = isencoesParcela;
    }

    public Boolean getIsentaJuros() {
        return isentaJuros != null ? isentaJuros : false;
    }

    public void setIsentaJuros(Boolean isentaJuros) {
        this.isentaJuros = isentaJuros;
    }

    public Boolean getIsentaMulta() {
        return isentaMulta != null ? isentaMulta : false;
    }

    public void setIsentaMulta(Boolean isentaMulta) {
        this.isentaMulta = isentaMulta;
    }

    public Boolean getIsentaCorrecao() {
        return isentaCorrecao != null ? isentaCorrecao : false;
    }

    public void setIsentaCorrecao(Boolean isentaCorrecao) {
        this.isentaCorrecao = isentaCorrecao;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public UsuarioSistema getUsuarioCancelamento() {
        return usuarioCancelamento;
    }

    public void setUsuarioCancelamento(UsuarioSistema usuarioCancelamento) {
        this.usuarioCancelamento = usuarioCancelamento;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public TipoDeducao getTipoDeducao() {
        return tipoDeducao;
    }

    public void setTipoDeducao(TipoDeducao tipoDeducao) {
        this.tipoDeducao = tipoDeducao;
    }

    public BigDecimal getPercentualDeducao() {
        return percentualDeducao;
    }

    public void setPercentualDeducao(BigDecimal percentualDeducao) {
        this.percentualDeducao = percentualDeducao;
    }

    public boolean isTipoDeducaoFixa() {
        return TipoDeducao.FIXA.equals(tipoDeducao);
    }

    public boolean isTipoDeducaoPercentual() {
        return TipoDeducao.PERCENTUAL.equals(tipoDeducao);
    }

    public Boolean getLancouDeducoes() {
        return lancouDeducoes != null ? lancouDeducoes : Boolean.FALSE;
    }

    public void setLancouDeducoes(Boolean lancouDeducoes) {
        this.lancouDeducoes = lancouDeducoes;
    }

    public Exercicio getExercicioCorrecao() {
        return exercicioCorrecao;
    }

    public void setExercicioCorrecao(Exercicio exercicioCorrecao) {
        this.exercicioCorrecao = exercicioCorrecao;
    }

    public enum Situacao implements EnumConsultaDebitos {
        ATIVO("Ativo", SituacaoIsencaoAcrescimoDTO.ATIVO),
        EFETIVADO("Efetivado", SituacaoIsencaoAcrescimoDTO.EFETIVADO),
        ESTORNADO("Estornado", SituacaoIsencaoAcrescimoDTO.ESTORNADO);

        private String descricao;
        private SituacaoIsencaoAcrescimoDTO dto;

        Situacao(String descricao, SituacaoIsencaoAcrescimoDTO dto) {
            this.descricao = descricao;
            this.dto = dto;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }

        @Override
        public SituacaoIsencaoAcrescimoDTO toDto() {
            return dto;
        }
    }

    public enum TipoDeducao implements EnumConsultaDebitos {
        FIXA("Dedução Fixa", TipoDeducaoIsencaoAcrescimoDTO.FIXA),
        PERCENTUAL("Dedução Percentual", TipoDeducaoIsencaoAcrescimoDTO.PERCENTUAL);

        private String descricao;
        private TipoDeducaoIsencaoAcrescimoDTO dto;

        TipoDeducao(String descricao, TipoDeducaoIsencaoAcrescimoDTO dto) {
            this.descricao = descricao;
            this.dto = dto;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }

        @Override
        public TipoDeducaoIsencaoAcrescimoDTO toDto() {
            return dto;
        }
    }

    public boolean isEstornado() {
        return Situacao.ESTORNADO.equals(situacao);
    }
}
