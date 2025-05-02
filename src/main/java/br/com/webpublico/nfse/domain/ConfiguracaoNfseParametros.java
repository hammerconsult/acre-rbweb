package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.enums.TipoParametroNfse;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ConfiguracaoNfseParametros extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoParametroNfse tipoParametro;
    private String valor;
    @ManyToOne
    private ConfiguracaoNfse configuracao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoParametroNfse getTipoParametro() {
        return tipoParametro;
    }

    public void setTipoParametro(TipoParametroNfse tipoParametro) {
        this.tipoParametro = tipoParametro;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public ConfiguracaoNfse getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(ConfiguracaoNfse configuracao) {
        this.configuracao = configuracao;
    }
}
