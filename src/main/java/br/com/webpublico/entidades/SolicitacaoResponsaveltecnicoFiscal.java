package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoSolicitacaoFiscal;
import br.com.webpublico.enums.TipoResponsavelFiscal;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 30/03/2016.
 */

@Entity
@Audited
@Etiqueta("Solicitação de Fiscal")
@Table(name = "SOLICRESPONSATECNICOFISCAL")
public class SolicitacaoResponsaveltecnicoFiscal extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data Solicitação")
    private Date dataSolicitacao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private SituacaoSolicitacaoFiscal situacaoSolicitacao;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Usuário Solicitante")
    private UsuarioSistema usuarioSistema;

    @Invisivel
    @Transient
    @Obrigatorio
    @Etiqueta("Tipo Responsável")
    private TipoResponsavelFiscal tipoResponsavel;
//
//    @Invisivel
//    @Transient
//    @Obrigatorio
//    @Etiqueta("Tipo Fiscal")
//    private TipoFiscalContrato tipoFiscal;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Fiscal")
    private ResponsavelTecnicoFiscal responsavelTecnicoFiscal;

    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Obra")
    private Obra obra;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;

    @Transient
    @OneToMany(mappedBy = "solicitacao", orphanRemoval = true)
    private List<AprovacaoResponsavelTecnicoFiscal> aprovacoes;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private PrincipalSubstituto principalSubstituto;

    public SolicitacaoResponsaveltecnicoFiscal() {
        super();
        this.situacaoSolicitacao = SituacaoSolicitacaoFiscal.AGUARDANDO_APROVACAO;
        this.aprovacoes = Lists.newArrayList();
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

    public SituacaoSolicitacaoFiscal getSituacaoSolicitacao() {
        return situacaoSolicitacao;
    }

    public void setSituacaoSolicitacao(SituacaoSolicitacaoFiscal situacaoSolicitacao) {
        this.situacaoSolicitacao = situacaoSolicitacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoResponsavelFiscal getTipoResponsavel() {
        return tipoResponsavel;
    }

    public void setTipoResponsavel(TipoResponsavelFiscal tipoResponsavel) {
        this.tipoResponsavel = tipoResponsavel;
    }

    public ResponsavelTecnicoFiscal getResponsavelTecnicoFiscal() {
        return responsavelTecnicoFiscal;
    }

    public void setResponsavelTecnicoFiscal(ResponsavelTecnicoFiscal responsavelTecnicoFiscal) {
        this.responsavelTecnicoFiscal = responsavelTecnicoFiscal;
    }

    public Obra getObra() {
        return obra;
    }

    public void setObra(Obra obra) {
        this.obra = obra;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<AprovacaoResponsavelTecnicoFiscal> getAprovacoes() {
        return aprovacoes;
    }

    public void setAprovacoes(List<AprovacaoResponsavelTecnicoFiscal> aprovacoes) {
        this.aprovacoes = aprovacoes;
    }

    public PrincipalSubstituto getPrincipalSubstituto() {
        return principalSubstituto;
    }

    public void setPrincipalSubstituto(PrincipalSubstituto principalSubstituto) {
        this.principalSubstituto = principalSubstituto;
    }

    @Override
    public String toString() {
        return codigo + " - " +
            descricao + " - " +
            DataUtil.getDataFormatada(dataSolicitacao)
            + " - " + responsavelTecnicoFiscal.getTipoResponsavel()
            + "  " + responsavelTecnicoFiscal.getTipoFiscal().getDescricao();
    }

    public Boolean isEditar() {
        return SituacaoSolicitacaoFiscal.REJEITADA.equals(situacaoSolicitacao)
            || SituacaoSolicitacaoFiscal.AGUARDANDO_APROVACAO.equals(situacaoSolicitacao);
    }

    public enum PrincipalSubstituto {
        PRINCIPAL("Principal"),
        SUBSTITUTO("Substituto");

        private String descricao;

        PrincipalSubstituto(String descricao) {
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
