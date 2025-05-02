package br.com.webpublico.dte.entidades;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.dte.enums.TipoParametroDte;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ConfiguracaoDteParametros extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfiguracaoDte configuracao;
    @Enumerated(EnumType.STRING)
    private TipoParametroDte tipoParametro;
    private String valor;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoParametroDte getTipoParametro() {
        return tipoParametro;
    }

    public void setTipoParametro(TipoParametroDte tipoParametro) {
        this.tipoParametro = tipoParametro;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public ConfiguracaoDte getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(ConfiguracaoDte configuracao) {
        this.configuracao = configuracao;
    }
}
