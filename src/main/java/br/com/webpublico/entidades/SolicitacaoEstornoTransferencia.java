package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 23/09/14
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Table(name = "SOLICITESTORNOTRANSFER")
@Etiqueta("Solicitação de Estorno de Transferência")
public class SolicitacaoEstornoTransferencia extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    @CodigoGeradoAoConcluir
    private Long codigo;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de criação")
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date dataDeCriacao;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Solicitante")
    @ManyToOne
    private UsuarioSistema solicitante;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Efetivação de Transferência")
    @ManyToOne
    private LoteEfetivacaoTransferenciaBem loteEfetivacaoTransferencia;

    @Etiqueta("Situação")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private SituacaoEventoBem situacao;

    @Etiqueta("Motivo da Recusa")
    private String motivoRecusa;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Invisivel
    @OneToMany(mappedBy = "solicitacaoEstorno")
    private List<ItemEstornoTransferencia> listaItemSolicitacaoEstornoTransferencia;

    @Etiqueta("Solicitação de Estorno de Transferência Origem")
    @OneToOne
    private SolicitacaoEstornoTransferencia solicitacaoEstTransfOrigem;

    private Boolean removido;

    public SolicitacaoEstornoTransferencia() {
        super();
        listaItemSolicitacaoEstornoTransferencia = Lists.newArrayList();
        situacao = SituacaoEventoBem.EM_ELABORACAO;
        removido = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(UsuarioSistema solicitante) {
        this.solicitante = solicitante;
    }

    public Date getDataDeCriacao() {
        return dataDeCriacao;
    }

    public void setDataDeCriacao(Date dataDeCriacao) {
        this.dataDeCriacao = dataDeCriacao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public LoteEfetivacaoTransferenciaBem getLoteEfetivacaoTransferencia() {
        return loteEfetivacaoTransferencia;
    }

    public void setLoteEfetivacaoTransferencia(LoteEfetivacaoTransferenciaBem loteEfetivacaoTransferencia) {
        this.loteEfetivacaoTransferencia = loteEfetivacaoTransferencia;
    }

    public List<ItemEstornoTransferencia> getListaItemSolicitacaoEstornoTransferencia() {
        return listaItemSolicitacaoEstornoTransferencia;
    }

    public void setListaItemSolicitacaoEstornoTransferencia(List<ItemEstornoTransferencia> listaItemSolicitacaoEstornoTransferencia) {
        this.listaItemSolicitacaoEstornoTransferencia = listaItemSolicitacaoEstornoTransferencia;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public SituacaoEventoBem getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEventoBem situacao) {
        this.situacao = situacao;
    }

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    public void setMotivoRecusa(String motivoRecusa) {
        this.motivoRecusa = motivoRecusa;
    }

    @Override
    public String toString() {
        try {
            return codigo + " - " + solicitante.getPessoaFisica().getNome() + " - " + DataUtil.getDataFormatada(dataDeCriacao);
        } catch (Exception ex) {
            return "";
        }
    }

    public Boolean foiRecusada() {
        return SituacaoEventoBem.RECUSADO.equals(this.situacao);
    }

    public Boolean isEmElaboracao() {
        return SituacaoEventoBem.EM_ELABORACAO.equals(this.situacao);
    }

    public Boolean isPermitidoExcluir() {
        return isAguardandoEfetivacao() || SituacaoEventoBem.EM_ELABORACAO.equals(this.situacao);
    }

    public Boolean isAguardandoEfetivacao() {
        return SituacaoEventoBem.AGUARDANDO_EFETIVACAO.equals(this.situacao);
    }

    public Boolean getRemovido() {
        return removido;
    }

    public void setRemovido(Boolean removido) {
        this.removido = removido;
    }

    public SolicitacaoEstornoTransferencia getSolicitacaoEstTransfOrigem() {
        return solicitacaoEstTransfOrigem;
    }

    public void setSolicitacaoEstTransfOrigem(SolicitacaoEstornoTransferencia solicitacaoEstTransfOrigem) {
        this.solicitacaoEstTransfOrigem = solicitacaoEstTransfOrigem;
    }
}
