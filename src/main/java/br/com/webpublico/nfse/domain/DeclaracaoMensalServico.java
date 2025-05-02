package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.nfse.domain.dtos.DeclaracaoMensalServicoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.domain.dtos.enums.SituacaoDeclaracaoMensalServicoNfseDTO;
import br.com.webpublico.nfse.enums.TipoDeclaracaoMensalServico;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * A DeclaracaoMensalServico.
 */
@Entity
@Table(name = "DECLARACAOMENSALSERVICO")
@Audited
@Etiqueta(value = "Declaração Mensal de Serviço")
public class DeclaracaoMensalServico extends SuperEntidade implements Serializable, NfseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "codigo")
    @Etiqueta(value = "Código")
    @Pesquisavel
    @Tabelavel
    private Integer codigo;

    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Mês")
    @Pesquisavel
    @Tabelavel
    private Mes mes;

    @ManyToOne
    @Etiqueta(value = "Exercício")
    @Pesquisavel
    @Tabelavel
    private Exercicio exercicio;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Etiqueta(value = "Tipo")
    @Pesquisavel
    @Tabelavel
    private TipoDeclaracaoMensalServico tipo;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Etiqueta(value = "Tipo de Movimento")
    @Pesquisavel
    @Tabelavel
    private TipoMovimentoMensal tipoMovimento;

    @ManyToOne
    @Etiqueta(value = "Prestador")
    @Pesquisavel
    @Tabelavel
    private CadastroEconomico prestador;

    @OneToMany(mappedBy = "declaracaoMensalServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotaDeclarada> notas;

    @ManyToOne
    private ProcessoCalculo processoCalculo;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação")
    @Enumerated(value = EnumType.STRING)
    private Situacao situacao;
    private Integer qtdNotas;
    private BigDecimal totalServicos;
    private BigDecimal totalIss;
    @Temporal(TemporalType.TIMESTAMP)
    private Date abertura;
    @Temporal(TemporalType.TIMESTAMP)
    private Date encerramento;
    @Temporal(TemporalType.DATE)
    private Date referencia;
    @Etiqueta("Lançado Por")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private LancadoPor lancadoPor;
    @Etiqueta("Usuário Declaração")
    @Pesquisavel
    private String usuarioDeclaracao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCancelamento;
    @ManyToOne
    private UsuarioSistema usuarioCancelamentoWp;
    @ManyToOne
    private UsuarioWeb usuarioCancelamentoWeb;
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne
    private ArquivoDesif arquivoDesif;

    public DeclaracaoMensalServico() {
        notas = Lists.newArrayList();
    }

    public DeclaracaoMensalServico(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
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

    public TipoDeclaracaoMensalServico getTipo() {
        return tipo;
    }

    public void setTipo(TipoDeclaracaoMensalServico tipo) {
        this.tipo = tipo;
    }

    public CadastroEconomico getPrestador() {
        return prestador;
    }

    public void setPrestador(CadastroEconomico prestador) {
        this.prestador = prestador;
    }

    public List<NotaDeclarada> getNotas() {
        return notas;
    }

    public List<NotaDeclarada> getNotasSemIssRetido() {
        List<NotaDeclarada> notasSemIssRetido = Lists.newArrayList();
        if (notas != null) {
            for (NotaDeclarada notaDeclarada : notas) {
                if (Boolean.FALSE.equals(notaDeclarada.getDeclaracaoPrestacaoServico().getIssRetido())) {
                    notasSemIssRetido.add(notaDeclarada);
                }
            }
        }
        return notasSemIssRetido;
    }

    public void setNotas(List<NotaDeclarada> notas) {
        this.notas = notas;
    }

    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculo;
    }

    public void setProcessoCalculo(ProcessoCalculo processoCalculo) {
        this.processoCalculo = processoCalculo;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public Integer getQtdNotas() {
        return qtdNotas;
    }

    public void setQtdNotas(Integer qtdNotas) {
        this.qtdNotas = qtdNotas;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getTotalIss() {
        return totalIss;
    }

    public void setTotalIss(BigDecimal totalIss) {
        this.totalIss = totalIss;
    }

    public Date getAbertura() {
        return abertura;
    }

    public void setAbertura(Date abertura) {
        this.abertura = abertura;
    }

    public Date getEncerramento() {
        return encerramento;
    }

    public void setEncerramento(Date encerramento) {
        this.encerramento = encerramento;
    }

    public TipoMovimentoMensal getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimentoMensal tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public Date getReferencia() {
        return referencia;
    }

    public void setReferencia(Date referencia) {
        this.referencia = referencia;
    }

    public String getUsuarioDeclaracao() {
        return usuarioDeclaracao;
    }

    public void setUsuarioDeclaracao(String usuarioDeclaracao) {
        this.usuarioDeclaracao = usuarioDeclaracao;
    }

    public LancadoPor getLancadoPor() {
        return lancadoPor;
    }

    public void setLancadoPor(LancadoPor lancadoPor) {
        this.lancadoPor = lancadoPor;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public UsuarioSistema getUsuarioCancelamentoWp() {
        return usuarioCancelamentoWp;
    }

    public void setUsuarioCancelamentoWp(UsuarioSistema usuarioCancelamentoWp) {
        this.usuarioCancelamentoWp = usuarioCancelamentoWp;
    }

    public UsuarioWeb getUsuarioCancelamentoWeb() {
        return usuarioCancelamentoWeb;
    }

    public void setUsuarioCancelamentoWeb(UsuarioWeb usuarioCancelamentoWeb) {
        this.usuarioCancelamentoWeb = usuarioCancelamentoWeb;
    }

    public String getUsuarioCancelamento() {
        if (usuarioCancelamentoWp != null) {
            return usuarioCancelamentoWp.toString();
        } else if (usuarioCancelamentoWeb != null) {
            return usuarioCancelamentoWeb.toString();
        }
        return "";
    }

    public ArquivoDesif getArquivoDesif() {
        return arquivoDesif;
    }

    public void setArquivoDesif(ArquivoDesif arquivoDesif) {
        this.arquivoDesif = arquivoDesif;
    }

    @Override
    public String toString() {
        return getPrestador().toString() +
            " - " + getMes().getNumeroMesString() + "/" +
            getExercicio().getAno() + " (" + getTipoMovimento() + ") ";
    }

    @Override
    public DeclaracaoMensalServicoNfseDTO toNfseDto() {
        DeclaracaoMensalServicoNfseDTO dto = getDeclaracaoMensalServicoNfseDTO();
        dto.setPrestador(this.prestador.toNfseDto());
        return dto;
    }

    public DeclaracaoMensalServicoNfseDTO toSimpleNfseDto() {
        return getDeclaracaoMensalServicoNfseDTO();
    }

    private DeclaracaoMensalServicoNfseDTO getDeclaracaoMensalServicoNfseDTO() {
        DeclaracaoMensalServicoNfseDTO dto = new DeclaracaoMensalServicoNfseDTO();
        dto.setCodigo(this.codigo);
        dto.setMes(this.mes.getNumeroMes());
        dto.setExercicio(this.exercicio.getAno());
        dto.setSituacao(this.situacao.toDto());
        dto.setTipo(this.tipo.toDto());
        dto.setTipoMovimento(this.tipoMovimento.toDto());
        dto.setAbertura(this.abertura);
        dto.setEncerramento(this.encerramento);
        dto.setUsuarioDeclaracao(this.usuarioDeclaracao);
        dto.setId(this.id);
        return dto;
    }

    public enum Situacao implements EnumComDescricao {
        ABERTO("Aberto", SituacaoDeclaracaoMensalServicoNfseDTO.ABERTO),
        ENCERRADO("Encerrado", SituacaoDeclaracaoMensalServicoNfseDTO.ENCERRADO),
        CANCELADO("Cancelado", SituacaoDeclaracaoMensalServicoNfseDTO.CANCELADO);

        private String descricao;
        private SituacaoDeclaracaoMensalServicoNfseDTO dto;

        Situacao(String descricao, SituacaoDeclaracaoMensalServicoNfseDTO dto) {
            this.descricao = descricao;
            this.dto = dto;
        }

        public String getDescricao() {
            return descricao;
        }

        public SituacaoDeclaracaoMensalServicoNfseDTO toDto() {
            return dto;
        }
    }

    public enum LancadoPor {
        CLIENTE("Cliente"),
        WEBPUBLICO("Webpúblico");

        private String descricao;

        LancadoPor(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
