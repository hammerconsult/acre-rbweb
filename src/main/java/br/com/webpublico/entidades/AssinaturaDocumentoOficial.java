package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class AssinaturaDocumentoOficial extends SuperEntidade implements EntidadeWebPublico {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @Temporal(TemporalType.DATE)
    private Date dataLimite;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAssinatura;

    private String hashAssinatura;

    @Enumerated(EnumType.STRING)
    private SituacaoAssinatura situacao;

    @ManyToOne
    private DocumentoOficial documentoOficial;

    @ManyToOne
    private ConfiguracaoAssinatura configuracaoAssinatura;

    public AssinaturaDocumentoOficial() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHashAssinatura() {
        return hashAssinatura;
    }

    public void setHashAssinatura(String hashAssinatura) {
        this.hashAssinatura = hashAssinatura;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public SituacaoAssinatura getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoAssinatura situacao) {
        this.situacao = situacao;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(Date dataLimite) {
        this.dataLimite = dataLimite;
    }

    public ConfiguracaoAssinatura getConfiguracaoAssinatura() {
        return configuracaoAssinatura;
    }

    public void setConfiguracaoAssinatura(ConfiguracaoAssinatura configuracaoAssinatura) {
        this.configuracaoAssinatura = configuracaoAssinatura;
    }

    public enum SituacaoAssinatura {

        PENDENTE("Pendente"),
        ASSINADO("Assinado");

        private String descricao;

        SituacaoAssinatura(String descricao) {
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

    public Boolean isAssinado(){
        return SituacaoAssinatura.ASSINADO.equals(this.getSituacao());
    }
}
