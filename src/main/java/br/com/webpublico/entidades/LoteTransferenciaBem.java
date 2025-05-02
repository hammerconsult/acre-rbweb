package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoDaSolicitacao;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoTransferenciaUnidadeBem;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 16/12/13
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta(value = "Solicitação de Transferência de Bens", genero = "M")
public class LoteTransferenciaBem extends SuperEntidade implements PossuidorArquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CodigoGeradoAoConcluir
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Criação")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataHoraCriacao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Responsável Unidade Origem")
    @ManyToOne
    private PessoaFisica responsavelOrigem;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Responsável Unidade Destino")
    @ManyToOne
    private PessoaFisica responsavelDestino;

    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Unidade Administrativa Origem")
    private UnidadeOrganizacional unidadeOrigem;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Unidade Administrativa Destino")
    private UnidadeOrganizacional unidadeDestino;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoDaSolicitacao situacaoTransferenciaBem;

    @Invisivel
    @OneToMany(mappedBy = "loteTransferenciaBem", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TransferenciaBem> transferencias;

    @Etiqueta("Motivo da Recusa")
    private String motivoRecusa;

    @Etiqueta("Removido")
    private Boolean removido = Boolean.FALSE;

    @Etiqueta("Tipo de Bem")
    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Tabelavel
    @Transient
    @Etiqueta("Unidade de Origem")
    private String descricaoUnidadeOrigem;

    @Tabelavel
    @Transient
    @Etiqueta("Unidade de Destino")
    private String descricaoUnidadeDestino;

    @Version
    private Long versao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataVersao;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Tipo de Transferência")
    private TipoTransferenciaUnidadeBem tipoTransferencia;

    private Boolean transfHierarquiaEncerrada;

    public LoteTransferenciaBem() {
        this.dataVersao = new Date();
        this.transferencias = new ArrayList<>();
        transfHierarquiaEncerrada = false;
    }

    public TipoTransferenciaUnidadeBem getTipoTransferencia() {
        return tipoTransferencia;
    }

    public void setTipoTransferencia(TipoTransferenciaUnidadeBem tipoTransferencia) {
        this.tipoTransferencia = tipoTransferencia;
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

    public Date getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public void setDataHoraCriacao(Date dataHoraCriacao) {
        this.dataHoraCriacao = dataHoraCriacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public PessoaFisica getResponsavelDestino() {
        return responsavelDestino;
    }

    public void setResponsavelDestino(PessoaFisica responsavelDestino) {
        this.responsavelDestino = responsavelDestino;
    }

    public PessoaFisica getResponsavelOrigem() {
        return responsavelOrigem;
    }

    public void setResponsavelOrigem(PessoaFisica responsavelOrigem) {
        this.responsavelOrigem = responsavelOrigem;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public UnidadeOrganizacional getUnidadeOrigem() {
        return unidadeOrigem;
    }

    public void setUnidadeOrigem(UnidadeOrganizacional unidadeOrigem) {
        this.unidadeOrigem = unidadeOrigem;
    }

    public UnidadeOrganizacional getUnidadeDestino() {
        return unidadeDestino;
    }

    public void setUnidadeDestino(UnidadeOrganizacional unidadeDestino) {
        this.unidadeDestino = unidadeDestino;
    }

    public List<TransferenciaBem> getTransferencias() {
        return transferencias;
    }

    public void setTransferencias(List<TransferenciaBem> transferencias) {
        this.transferencias = transferencias;
    }

    public SituacaoDaSolicitacao getSituacaoTransferenciaBem() {
        return situacaoTransferenciaBem;
    }

    public void setSituacaoTransferenciaBem(SituacaoDaSolicitacao situacaoTransferenciaBem) {
        this.situacaoTransferenciaBem = situacaoTransferenciaBem;
    }

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    public void setMotivoRecusa(String motivoRecusa) {
        this.motivoRecusa = motivoRecusa;
    }

    public Boolean isRecusada() {
        return SituacaoDaSolicitacao.RECUSADA.equals(this.situacaoTransferenciaBem);
    }

    public Boolean isAceita() {
        return SituacaoDaSolicitacao.ACEITA.equals(this.situacaoTransferenciaBem);
    }

    public Boolean isEmElaboracao() {
        return SituacaoDaSolicitacao.EM_ELABORACAO.equals(this.situacaoTransferenciaBem);
    }

    public Boolean isAguardandoAprovacao() {
        return SituacaoDaSolicitacao.AGUARDANDO_APROVACAO.equals(this.situacaoTransferenciaBem);
    }

    public Boolean isAguardandoEfetivacao() {
        return SituacaoDaSolicitacao.AGUARDANDO_EFETIVACAO.equals(this.situacaoTransferenciaBem);
    }

    public Boolean getRemovido() {
        return removido;
    }

    public void setRemovido(Boolean removido) {
        this.removido = removido;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }


    public String getDescricaoUnidadeOrigem() {
        return descricaoUnidadeOrigem;
    }

    public void setDescricaoUnidadeOrigem(String descricaoUnidadeOrigem) {
        this.descricaoUnidadeOrigem = descricaoUnidadeOrigem;
    }

    public String getDescricaoUnidadeDestino() {
        return descricaoUnidadeDestino;
    }

    public void setDescricaoUnidadeDestino(String descricaoUnidadeDestino) {
        this.descricaoUnidadeDestino = descricaoUnidadeDestino;
    }

    public Boolean getTransfHierarquiaEncerrada() {
        return transfHierarquiaEncerrada != null ? transfHierarquiaEncerrada : false;
    }

    public void setTransfHierarquiaEncerrada(Boolean transfHierarquiaEncerrada) {
        this.transfHierarquiaEncerrada = transfHierarquiaEncerrada;
    }

    @Override
    public String toString() {
        try {
            return codigo + " - " + descricao;
        } catch (NullPointerException e) {
            return "";
        }
    }
}
