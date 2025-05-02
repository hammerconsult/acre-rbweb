package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.bi.TipoExportacaoArquivoBI;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ConfiguracaoTributarioBIDivida extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ConfiguracaoTributarioBI configuracaoTributarioBI;
    @Enumerated(EnumType.STRING)
    private TipoExportacaoArquivoBI tipoExportacaoArquivoBI;
    @ManyToOne
    private Divida divida;
    @Transient
    private Boolean excluido;

    public ConfiguracaoTributarioBIDivida() {
        super();
        this.excluido = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoTributarioBI getConfiguracaoTributarioBI() {
        return configuracaoTributarioBI;
    }

    public void setConfiguracaoTributarioBI(ConfiguracaoTributarioBI configuracaoTributarioBI) {
        this.configuracaoTributarioBI = configuracaoTributarioBI;
    }

    public TipoExportacaoArquivoBI getTipoExportacaoArquivoBI() {
        return tipoExportacaoArquivoBI;
    }

    public void setTipoExportacaoArquivoBI(TipoExportacaoArquivoBI tipoExportacaoArquivoBI) {
        this.tipoExportacaoArquivoBI = tipoExportacaoArquivoBI;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Boolean getExcluido() {
        return excluido;
    }

    public void setExcluido(Boolean excluido) {
        this.excluido = excluido;
    }
}
