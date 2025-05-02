package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Desenvolvimento on 31/03/2016.
 */
@Entity
@Audited
@Etiqueta("Aprovação de Fiscal")
@Table(name = "APROVRESPONSATECNICOFISCAL")
public class AprovacaoResponsavelTecnicoFiscal extends SuperEntidade implements ValidadorEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Solicitação Fiscal")
    private SolicitacaoResponsaveltecnicoFiscal solicitacao;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Motivo")
    private String motivo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoAprovacao situacaoAprovacao;

    @Etiqueta("Ato Legal")
    @Pesquisavel
    @ManyToOne
    private AtoLegal atoLegal;

    public AprovacaoResponsavelTecnicoFiscal() {
        super();
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

    public SolicitacaoResponsaveltecnicoFiscal getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoResponsaveltecnicoFiscal solicitacao) {
        this.solicitacao = solicitacao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public SituacaoAprovacao getSituacaoAprovacao() {
        return situacaoAprovacao;
    }

    public void setSituacaoAprovacao(SituacaoAprovacao situacaoAprovacao) {
        this.situacaoAprovacao = situacaoAprovacao;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Boolean isRejeitada() {
        return SituacaoAprovacao.REJEITADA.equals(situacaoAprovacao);
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (isRejeitada()) {
            if (motivo == null || motivo.isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o motivo da rejeição.");
                ve.validou = false;
            }
        } else {
            if (atoLegal == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Ato Legal é obrigatório!");
                ve.validou = false;
            }
        }
        ve.lancarException();
    }

    public enum SituacaoAprovacao {
        APROVADA("Aprovada"),
        REJEITADA("Rejeitada");

        String descricao;

        SituacaoAprovacao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
