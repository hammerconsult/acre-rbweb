package br.com.webpublico.entidades.rh.configuracao;

import br.com.webpublico.entidades.BaseFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by zaca.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Configuração Relatorio BaseFP")
@Table(name = "CONFIGRELATORIORHBASEFP")
public class ConfiguracaoRelatorioRHBaseFP extends SuperEntidade {
    private final static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private BaseFP baseFP;

    @ManyToOne
    private ConfiguracaoRelatorio configuracaoRelatorio;

    public ConfiguracaoRelatorioRHBaseFP(BaseFP baseFP) {
        this.baseFP = baseFP;
    }

    public ConfiguracaoRelatorioRHBaseFP() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BaseFP getBaseFP() {
        return baseFP;
    }

    public void setBaseFP(BaseFP baseFP) {
        this.baseFP = baseFP;
    }

    public ConfiguracaoRelatorio getConfiguracaoRelatorio() {
        return configuracaoRelatorio;
    }

    public void setConfiguracaoRelatorio(ConfiguracaoRelatorio configuracaoRelatorio) {
        this.configuracaoRelatorio = configuracaoRelatorio;
    }
}
