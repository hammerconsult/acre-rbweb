package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoMovimentoAdministrativo;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Table(name = "SOLICITACAOTRANSFGRUPOBEM")
@Etiqueta(value = "Solicitação de Transferência Grupo de Patrimonial")
public class SolicitacaoTransferenciaGrupoBem extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private Long numero;

    @Etiqueta("Data da Solicitação")
    @Temporal(TemporalType.DATE)
    private Date dataSolicitacao;

    @Obrigatorio
    @Etiqueta("Descrição")
    @Length(maximo = 3000)
    private String descricao;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @ManyToOne
    @Etiqueta("Responsável")
    private UsuarioSistema responsavel;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Grupo Bem de Origem")
    private GrupoBem grupoBemOrigem;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Grupo Bem de Destino")
    private GrupoBem grupoBemDestino;

    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoMovimentoAdministrativo situacao;

    @Etiqueta("Motivo da Recusa")
    @Length(maximo = 3000)
    private String motivoRecusa;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Version
    private Long versao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataVersao;

    @Invisivel
    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ItemSolicitacaoTransferenciaGrupoBem> itens;

    public boolean isEmElaboracao() {
        return SituacaoMovimentoAdministrativo.EM_ELABORACAO.equals(situacao);
    }

    public boolean isRecusada() {
        return situacao.isRecusado();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long codigo) {
        this.numero = codigo;
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

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public GrupoBem getGrupoBemOrigem() {
        return grupoBemOrigem;
    }

    public void setGrupoBemOrigem(GrupoBem grupoBemOrigem) {
        this.grupoBemOrigem = grupoBemOrigem;
    }

    public GrupoBem getGrupoBemDestino() {
        return grupoBemDestino;
    }

    public void setGrupoBemDestino(GrupoBem grupoBemDestino) {
        this.grupoBemDestino = grupoBemDestino;
    }

    public SituacaoMovimentoAdministrativo getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMovimentoAdministrativo situacao) {
        this.situacao = situacao;
    }

    public List<ItemSolicitacaoTransferenciaGrupoBem> getItens() {
        return itens;
    }

    public void setItens(List<ItemSolicitacaoTransferenciaGrupoBem> itens) {
        this.itens = itens;
    }

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    public void setMotivoRecusa(String motivoRecusa) {
        this.motivoRecusa = motivoRecusa;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
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

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    public String toString() {
        return numero + " - " + DataUtil.getDataFormatada(dataSolicitacao) + " - " + responsavel;
    }
}
