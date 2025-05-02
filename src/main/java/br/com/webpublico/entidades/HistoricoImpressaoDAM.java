package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Cacheable
@Audited
public class HistoricoImpressaoDAM implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @ManyToOne
    private DAM dam;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOperacao;
    @Enumerated(EnumType.STRING)
    private TipoImpressao tipoImpressao;
    @ManyToOne
    private ParcelaValorDivida parcela;
    @Invisivel
    @Transient
    private Long criadoEm;

    public HistoricoImpressaoDAM(DAM dam, UsuarioSistema usuarioSistema, Date dataOperacao, TipoImpressao tipoImpressao) {
        this.dam = dam;
        this.usuarioSistema = usuarioSistema;
        this.dataOperacao = dataOperacao;
        this.tipoImpressao = tipoImpressao;
    }

    public HistoricoImpressaoDAM() {
        this.criadoEm = System.currentTimeMillis();
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public DAM getDam() {
        return dam;
    }

    public void setDam(DAM dam) {
        this.dam = dam;
    }

    public TipoImpressao getTipoImpressao() {
        return tipoImpressao;
    }

    public void setTipoImpressao(TipoImpressao tipoImpressao) {
        this.tipoImpressao = tipoImpressao;
    }

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public enum TipoImpressao {
        SISTEMA("Sistema"),
        PORTAL("Portal"),
        WEBSERVICE("WebService"),
        NFSE("Nota Fiscal Eletrônica");
        private String descricao;

        private TipoImpressao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
