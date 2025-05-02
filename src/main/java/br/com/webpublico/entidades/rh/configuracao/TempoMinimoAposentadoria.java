package br.com.webpublico.entidades.rh.configuracao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.Sexo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @Author peixe on 05/11/2015  17:41.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Configuração RH")
@Etiqueta("Configurações para Aposentadoria")
public class TempoMinimoAposentadoria extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Configuração de Aposentadoria")
    private ConfiguracaoAposentadoria configuracaoAposentadoria;
    @Obrigatorio
    @Etiqueta("Sexo")
    @Enumerated(value = EnumType.STRING)
    private Sexo sexo;
    @Obrigatorio
    @Etiqueta("Quantidade Mínima em Anos")
    private Integer quantidadeMinima;


    public Long getId() {
        return id;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public Integer getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoAposentadoria getConfiguracaoAposentadoria() {
        return configuracaoAposentadoria;
    }

    public void setConfiguracaoAposentadoria(ConfiguracaoAposentadoria configuracaoAposentadoria) {
        this.configuracaoAposentadoria = configuracaoAposentadoria;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public void setQuantidadeMinima(Integer quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    @Override
    public String toString() {
        return sexo.getDescricao() + " - " + quantidadeMinima + " anos";
    }
}
