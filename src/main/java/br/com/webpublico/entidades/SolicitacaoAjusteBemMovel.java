package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoAjusteBemMovel;
import br.com.webpublico.enums.SituacaoMovimentoAdministrativo;
import br.com.webpublico.enums.TipoAjusteBemMovel;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 06/03/2018.
 */
@Entity
@Audited
@Etiqueta("Solicitação de Ajuste de Bens Móveis")
public class SolicitacaoAjusteBemMovel extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    @CodigoGeradoAoConcluir
    private Long codigo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Solicitação")
    @Temporal(TemporalType.DATE)
    private Date dataSolicitacao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    @Length(maximo = 3000)
    private String descricao;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoMovimentoAdministrativo situacao;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Ajuste")
    private TipoAjusteBemMovel tipoAjusteBemMovel;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação")
    private OperacaoAjusteBemMovel operacaoAjusteBemMovel;

    @ManyToOne
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeAdministrativa;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Responsável")
    private UsuarioSistema usuarioSistema;

    @Etiqueta("Motivo da Rejeição")
    private String motivoRejeicao;

    @Invisivel
    @OneToMany(mappedBy = "solicitacaoAjusteBemMovel")
    private List<ItemSolicitacaoAjusteBemMovel> itensSolicitacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Version
    private Long versao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataVersao;

    @Etiqueta("Contabilizar")
    private Boolean contabilizar;

    public SolicitacaoAjusteBemMovel() {
        super();
        this.dataVersao = new Date();
        contabilizar = true;
        itensSolicitacao = Lists.newArrayList();
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

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public SituacaoMovimentoAdministrativo getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMovimentoAdministrativo situacaoEventoBem) {
        this.situacao = situacaoEventoBem;
    }

    public TipoAjusteBemMovel getTipoAjusteBemMovel() {
        return tipoAjusteBemMovel;
    }

    public void setTipoAjusteBemMovel(TipoAjusteBemMovel tipoAjusteBemMovel) {
        this.tipoAjusteBemMovel = tipoAjusteBemMovel;
    }

    public OperacaoAjusteBemMovel getOperacaoAjusteBemMovel() {
        return operacaoAjusteBemMovel;
    }

    public void setOperacaoAjusteBemMovel(OperacaoAjusteBemMovel operacaoAjusteBemMovel) {
        this.operacaoAjusteBemMovel = operacaoAjusteBemMovel;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public List<ItemSolicitacaoAjusteBemMovel> getItensSolicitacao() {
        return itensSolicitacao;
    }

    public void setItensSolicitacao(List<ItemSolicitacaoAjusteBemMovel> itensSolicitacao) {
        this.itensSolicitacao = itensSolicitacao;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public boolean isSolicitacaoAceita() {
        return SituacaoMovimentoAdministrativo.FINALIZADO.equals(this.situacao);
    }

    public boolean isSolicitacaoRejeitada() {
        return SituacaoMovimentoAdministrativo.RECUSADO.equals(this.situacao);
    }

    public boolean isSolicitacaoEmElaboracao() {
        return SituacaoMovimentoAdministrativo.EM_ELABORACAO.equals(this.situacao);
    }

    public Boolean getContabilizar() {
        return contabilizar;
    }

    public void setContabilizar(Boolean integracaoContabil) {
        this.contabilizar = integracaoContabil;
    }

    public String getTituloValorInicial() {
        try {
            return "Valor " + tipoAjusteBemMovel.getDescricao() + " (R$)";
        } catch (NullPointerException e) {
            return "Valor Acumulativo (R$)";
        }
    }

    public String getTituloValorFinal() {
        try {
            return "Valor " + tipoAjusteBemMovel.getDescricao() + " Final (R$)";
        } catch (NullPointerException e) {
            return "Valor Final (R$)";
        }
    }

    @Override
    public String toString() {
        try {
            return codigo + " - " + descricao + " (" + DataUtil.getDataFormatada(dataSolicitacao) + ")";
        } catch (NullPointerException ex) {
            return "";
        }
    }
}
