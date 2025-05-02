package br.com.webpublico.entidades;

import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SituacaoConservacaoBem;
import br.com.webpublico.enums.SituacaoMovimentoAdministrativo;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Solicitação de Alteração de Conservação do Bem")
@Table(name = "SOLICITACAOALTERCONSERVBEM")
public class SolicitacaoAlteracaoConservacaoBem extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;

    private Long codigo;

    @Temporal(TemporalType.DATE)
    private Date dataSolicitacao;

    @ManyToOne
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Responsável")
    private UsuarioSistema responsavel;

    @Length(maximo = 3000)
    @Etiqueta("Descrição")
    private String descricao;

    @Length(maximo = 255)
    @Etiqueta("Motivo da Recusa")
    private String motivoRecusa;

    @Obrigatorio
    @Etiqueta("Estado de Conservação")
    @Enumerated(EnumType.STRING)
    private EstadoConservacaoBem estadoConservacao;

    @Obrigatorio
    @Etiqueta("Situação de Conservação")
    @Enumerated(EnumType.STRING)
    private SituacaoConservacaoBem situacaoConservacao;

    @Obrigatorio
    @Etiqueta("Tipo de Bem")
    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoMovimentoAdministrativo situacao;

    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @OneToMany(mappedBy = "solicitacaoAlteracaoConsBem")
    private List<ItemSolicitacaoAlteracaoConservacaoBem> itens;

    @Version
    private Long versao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataVersao;

    public SolicitacaoAlteracaoConservacaoBem() {
        dataVersao = new Date();
        itens = Lists.newArrayList();
        situacao = SituacaoMovimentoAdministrativo.EM_ELABORACAO;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public Date getDataVersao() {
        return dataVersao;
    }

    public void setDataVersao(Date dataVersao) {
        this.dataVersao = dataVersao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ItemSolicitacaoAlteracaoConservacaoBem> getItens() {
        return itens;
    }

    public void setItens(List<ItemSolicitacaoAlteracaoConservacaoBem> itens) {
        this.itens = itens;
    }

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    public void setMotivoRecusa(String motivoRecusa) {
        this.motivoRecusa = motivoRecusa;
    }

    public EstadoConservacaoBem getEstadoConservacao() {
        return estadoConservacao;
    }

    public void setEstadoConservacao(EstadoConservacaoBem estadoConservacao) {
        this.estadoConservacao = estadoConservacao;
    }

    public SituacaoConservacaoBem getSituacaoConservacao() {
        return situacaoConservacao;
    }

    public void setSituacaoConservacao(SituacaoConservacaoBem situacaoConservacao) {
        this.situacaoConservacao = situacaoConservacao;
    }

    public SituacaoMovimentoAdministrativo getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMovimentoAdministrativo situacao) {
        this.situacao = situacao;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public boolean isEmElaboracao() {
        return SituacaoMovimentoAdministrativo.EM_ELABORACAO.equals(this.getSituacao());
    }

    public boolean isRecusada() {
        return situacao.isRecusado();
    }

    @Override
    public String toString() {
        return codigo + " - " + DataUtil.getDataFormatada(dataSolicitacao) + " - " + responsavel;
    }
}
