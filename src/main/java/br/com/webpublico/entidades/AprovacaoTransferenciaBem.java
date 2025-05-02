package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoDaSolicitacao;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta(value = "Aprovação de Transferência de Bens", genero = "M")
public class AprovacaoTransferenciaBem extends SuperEntidade {

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
    @Etiqueta("Data da Aprovação")
    @Temporal(TemporalType.DATE)
    private Date dataAprovacao;

    @Obrigatorio
    @Tabelavel
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @Obrigatorio
    @Tabelavel
    @ManyToOne
    @Etiqueta("Solicitação de Transferência")
    private LoteTransferenciaBem  solicitacaoTransferencia;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoDaSolicitacao situacao;

    @Etiqueta("Motivo")
    private String motivo;

    @Invisivel
    @OneToMany(mappedBy = "aprovacao", cascade = CascadeType.REMOVE,  orphanRemoval = true)
    private List<ItemAprovacaoTransferenciaBem> itens;

    @Invisivel
    @OneToMany(mappedBy = "aprovacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AprovacaoTransfBemAnexo> anexos;

    @Etiqueta("Tipo de Bem")
    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    public AprovacaoTransferenciaBem() {
        itens = Lists.newArrayList();
        anexos = Lists.newArrayList();
    }

    public List<AprovacaoTransfBemAnexo> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<AprovacaoTransfBemAnexo> anexos) {
        this.anexos = anexos;
    }

    @Override
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

    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public LoteTransferenciaBem getSolicitacaoTransferencia() {
        return solicitacaoTransferencia;
    }

    public void setSolicitacaoTransferencia(LoteTransferenciaBem solicitacaoTransferencia) {
        this.solicitacaoTransferencia = solicitacaoTransferencia;
    }

    public SituacaoDaSolicitacao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoDaSolicitacao situacao) {
        this.situacao = situacao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public List<ItemAprovacaoTransferenciaBem> getItens() {
        return itens;
    }

    public void setItens(List<ItemAprovacaoTransferenciaBem> itens) {
        this.itens = itens;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    @Override
    public String toString() {
        return solicitacaoTransferencia.toString();
    }

    public Boolean isRecusada() {
        return SituacaoDaSolicitacao.RECUSADA.equals(this.situacao);
    }

    public Boolean isAceita() {
        return SituacaoDaSolicitacao.ACEITA.equals(this.situacao);
    }
}
