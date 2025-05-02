package br.com.webpublico.dte.entidades;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.dte.enums.TipoParametroDte;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
public class ConfiguracaoDte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Cidade cidade;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ConfiguracaoDteRelatorio configuracaoDteRelatorio;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configuracao", fetch = FetchType.EAGER)
    private List<ConfiguracaoDteParametros> parametros;

    public ConfiguracaoDte() {
        super();
        configuracaoDteRelatorio = new ConfiguracaoDteRelatorio();
        parametros = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public ConfiguracaoDteRelatorio getConfiguracaoDteRelatorio() {
        return configuracaoDteRelatorio;
    }

    public void setConfiguracaoDteRelatorio(ConfiguracaoDteRelatorio configuracaoDteRelatorio) {
        this.configuracaoDteRelatorio = configuracaoDteRelatorio;
    }

    public List<ConfiguracaoDteParametros> getParametros() {
        return parametros;
    }

    public void setParametros(List<ConfiguracaoDteParametros> parametros) {
        this.parametros = parametros;
    }

    public String getUrlDte() {
        return getParametroString(TipoParametroDte.URL_APLICACAO);
    }


    public boolean getParametroBoolean(TipoParametroDte tipo) {
        String valor = getParametroString(tipo);
        return Strings.isNullOrEmpty(valor) ? false : Boolean.valueOf(valor);
    }

    public boolean hasParametro(TipoParametroDte tipo) {
        return getParametroString(tipo) != null;
    }

    public String getParametroString(TipoParametroDte tipo) {
        for (ConfiguracaoDteParametros parametro : getParametros()) {
            if (tipo.equals(parametro.getTipoParametro())) {
                return parametro.getValor();
            }
        }
        return null;
    }

    public Integer getParametroInteger(TipoParametroDte tipo) {
        for (ConfiguracaoDteParametros parametro : getParametros()) {
            if (tipo.equals(parametro.getTipoParametro())) {
                return Integer.valueOf(parametro.getValor());
            }
        }
        return null;
    }
}
