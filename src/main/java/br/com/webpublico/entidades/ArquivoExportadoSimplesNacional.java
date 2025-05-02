package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.EnumComDescricao;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class ArquivoExportadoSimplesNacional extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ArquivoInconsistenciaSimplesNacional arquivoInconsistenciaSimplesNacional;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataExportacao;
    @ManyToOne
    private UsuarioSistema usuarioExportacao;
    @Enumerated(EnumType.STRING)
    private TipoExportacao tipoExportacao;
    @OneToOne(cascade = CascadeType.ALL)
    private Arquivo arquivoExportacao;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ArquivoInconsistenciaSimplesNacional getArquivoInconsistenciaSimplesNacional() {
        return arquivoInconsistenciaSimplesNacional;
    }

    public void setArquivoInconsistenciaSimplesNacional(ArquivoInconsistenciaSimplesNacional arquivoInconsistenciaSimplesNacional) {
        this.arquivoInconsistenciaSimplesNacional = arquivoInconsistenciaSimplesNacional;
    }

    public Date getDataExportacao() {
        return dataExportacao;
    }

    public void setDataExportacao(Date dataExportacao) {
        this.dataExportacao = dataExportacao;
    }

    public UsuarioSistema getUsuarioExportacao() {
        return usuarioExportacao;
    }

    public void setUsuarioExportacao(UsuarioSistema usuarioExportacao) {
        this.usuarioExportacao = usuarioExportacao;
    }

    public Arquivo getArquivoExportacao() {
        return arquivoExportacao;
    }

    public void setArquivoExportacao(Arquivo arquivoExportacao) {
        this.arquivoExportacao = arquivoExportacao;
    }

    public TipoExportacao getTipoExportacao() {
        return tipoExportacao;
    }

    public void setTipoExportacao(TipoExportacao tipoExportacao) {
        this.tipoExportacao = tipoExportacao;
    }

    public enum TipoExportacao implements EnumComDescricao {
        INI("Arquivo Inicial"),
        ADC("Arquivo complementar de pendências"),
        EXC("Arquivo de regularização");

        private final String descricao;

        TipoExportacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
