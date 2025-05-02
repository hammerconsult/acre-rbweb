package br.com.webpublico.entidades.rh.configuracao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * Created by carlos on 03/03/17.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Configuração RH")
@Etiqueta("Configurações RH Relatório")
public class ConfiguracaoRelatorio extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String textoReciboFerias;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoRelatorio", orphanRemoval = true)
    private List<ConfiguracaoRelatorioRHBaseFP> configuracaoRelatorioRHBaseFPs;

    public ConfiguracaoRelatorio() {
        super();
        this.setConfiguracaoRelatorioRHBaseFPs(Lists.<ConfiguracaoRelatorioRHBaseFP>newArrayList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextoReciboFerias() {
        return textoReciboFerias;
    }

    public void setTextoReciboFerias(String textoReciboFerias) {
        this.textoReciboFerias = textoReciboFerias;
    }

    public List<ConfiguracaoRelatorioRHBaseFP> getConfiguracaoRelatorioRHBaseFPs() {
        return configuracaoRelatorioRHBaseFPs;
    }

    public void setConfiguracaoRelatorioRHBaseFPs(List<ConfiguracaoRelatorioRHBaseFP> configuracaoRelatorioRHBaseFPs) {
        this.configuracaoRelatorioRHBaseFPs = configuracaoRelatorioRHBaseFPs;
    }

    @Override
    public String toString() {
        return getTextoReciboFerias();
    }
}
