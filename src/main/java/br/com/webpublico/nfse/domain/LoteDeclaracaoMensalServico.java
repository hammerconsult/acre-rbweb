package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.nfse.domain.dtos.enums.SituacaoDeclaracaoMensalServicoNfseDTO;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

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
@Table(name = "LOTEDECMENSALSERVICO")
@Audited
@Etiqueta(value = "Lote de Declaração Mensal de Serviço")
public class LoteDeclaracaoMensalServico extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
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
    @Etiqueta(value = "Tipo de Movimento")
    @Pesquisavel
    @Tabelavel
    private TipoMovimentoMensal tipoMovimento;
    @Enumerated(value = EnumType.STRING)
    private Situacao situacao;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Qtd Declarações")
    private Integer qtdDeclaracoes;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Total dos Serviços")
    @Monetario
    private BigDecimal totalServicos;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Total de ISS")
    @Monetario
    private BigDecimal totalIss;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("CMC Inicial")
    private String cmcInicial;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("CMC Final")
    private String cmcFinal;
    private Boolean selecionarNotas;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Início")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inicio;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Fim")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fim;
    @Temporal(TemporalType.DATE)
    private Date referencia;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @OneToMany(mappedBy = "lote")
    private List<LoteDeclaracaoMensalServicoItem> itens;

    public LoteDeclaracaoMensalServico() {
        super();
        itens = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public TipoMovimentoMensal getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimentoMensal tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public Integer getQtdDeclaracoes() {
        return qtdDeclaracoes;
    }

    public void setQtdDeclaracoes(Integer qtdDeclaracoes) {
        this.qtdDeclaracoes = qtdDeclaracoes;
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

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public Date getReferencia() {
        return referencia;
    }

    public void setReferencia(Date referencia) {
        this.referencia = referencia;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getCmcInicial() {
        return cmcInicial;
    }

    public void setCmcInicial(String cmcInicial) {
        this.cmcInicial = cmcInicial;
    }

    public String getCmcFinal() {
        return cmcFinal;
    }

    public void setCmcFinal(String cmcFinal) {
        this.cmcFinal = cmcFinal;
    }

    public Boolean getSelecionarNotas() {
        return selecionarNotas;
    }

    public void setSelecionarNotas(Boolean selecionarNotas) {
        this.selecionarNotas = selecionarNotas;
    }

    public List<LoteDeclaracaoMensalServicoItem> getItens() {
        return itens;
    }

    public void setItens(List<LoteDeclaracaoMensalServicoItem> itens) {
        this.itens = itens;
    }

    public enum Situacao {
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
}
