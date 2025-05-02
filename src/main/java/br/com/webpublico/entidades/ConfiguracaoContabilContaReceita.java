package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by mateus on 30/08/17.
 */
@Entity
@GrupoDiagrama(nome = "Contábil")
@Audited
@Table(name = "CONFIGCONTCONTARECEITA")
public class ConfiguracaoContabilContaReceita extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta de Receita")
    private Conta contaReceita;
    @Obrigatorio
    @Etiqueta("Configuração Contábil")
    @ManyToOne
    private ConfiguracaoContabil configuracaoContabil;

    public ConfiguracaoContabilContaReceita() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(Conta contaReceita) {
        this.contaReceita = contaReceita;
    }

    public ConfiguracaoContabil getConfiguracaoContabil() {
        return configuracaoContabil;
    }

    public void setConfiguracaoContabil(ConfiguracaoContabil configuracaoContabil) {
        this.configuracaoContabil = configuracaoContabil;
    }
}
