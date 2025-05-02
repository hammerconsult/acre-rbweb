package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@GrupoDiagrama(nome = "Contábil")
@Audited
@Table(name = "CONFIGCONTTIPOCONTADESP")
public class ConfiguracaoContabilTipoContaDespesaReinf extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    private ConfiguracaoContabil configuracaoContabil;
    @Enumerated(EnumType.STRING)
    private TipoContaDespesa tipoContaDespesa;

    public ConfiguracaoContabilTipoContaDespesaReinf() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoContabil getConfiguracaoContabil() {
        return configuracaoContabil;
    }

    public void setConfiguracaoContabil(ConfiguracaoContabil configuracaoContabil) {
        this.configuracaoContabil = configuracaoContabil;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }
}
