package br.com.webpublico.entidades;

import br.com.webpublico.enums.PatrimonioLiquido;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by mateus on 15/12/17.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Configuração Contábil - Conta Contábil")
@Table(name = "CONFIGCONTABILCONTACONT")
public class ConfiguracaoContabilContaContabil implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Configuração Contábil")
    private ConfiguracaoContabil configuracaoContabil;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta Contábil")
    private ContaContabil contaContabil;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Patrimonio Líquido")
    private br.com.webpublico.enums.PatrimonioLiquido patrimonioLiquido;
    @Enumerated(EnumType.STRING)
    private AjusteResultado ajusteResultado;

    public ConfiguracaoContabilContaContabil() {
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

    public ContaContabil getContaContabil() {
        return contaContabil;
    }

    public void setContaContabil(ContaContabil contaContabil) {
        this.contaContabil = contaContabil;
    }

    public br.com.webpublico.enums.PatrimonioLiquido getPatrimonioLiquido() {
        return patrimonioLiquido;
    }

    public void setPatrimonioLiquido(PatrimonioLiquido patrimonioLiquido) {
        this.patrimonioLiquido = patrimonioLiquido;
    }

    public AjusteResultado getAjusteResultado() {
        return ajusteResultado;
    }

    public void setAjusteResultado(AjusteResultado ajusteResultado) {
        this.ajusteResultado = ajusteResultado;
    }

    public boolean isAjuste(){
        return AjusteResultado.AJUSTE.equals(ajusteResultado);
    }

    public boolean isResultado(){
        return AjusteResultado.RESULTADO.equals(ajusteResultado);
    }

    public enum AjusteResultado {
        AJUSTE,
        RESULTADO;
    }
}
