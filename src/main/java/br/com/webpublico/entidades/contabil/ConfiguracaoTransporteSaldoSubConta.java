package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Audited
@Entity
@Table(name = "CONFIGTRANSPSALDOSUBC")
@Etiqueta("Configuração de Transporte do Saldo da Conta Financeira")
public class ConfiguracaoTransporteSaldoSubConta extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Exercício Origem")
    private Exercicio exercicioOrigem;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Exercício Destino")
    private Exercicio exercicioDestino;
    @OneToMany(mappedBy = "configuracao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfiguracaoTransporteSaldoSubContaOrigem> origens;

    public ConfiguracaoTransporteSaldoSubConta() {
        origens = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicioOrigem() {
        return exercicioOrigem;
    }

    public void setExercicioOrigem(Exercicio exercicioOrigem) {
        this.exercicioOrigem = exercicioOrigem;
    }

    public Exercicio getExercicioDestino() {
        return exercicioDestino;
    }

    public void setExercicioDestino(Exercicio exercicioDestino) {
        this.exercicioDestino = exercicioDestino;
    }

    public List<ConfiguracaoTransporteSaldoSubContaOrigem> getOrigens() {
        return origens;
    }

    public void setOrigens(List<ConfiguracaoTransporteSaldoSubContaOrigem> origens) {
        this.origens = origens;
    }

    public BigDecimal getValorTotalOrigens() {
        BigDecimal retorno = BigDecimal.ZERO;
        if (this.origens != null && !this.origens.isEmpty()) {
            for (ConfiguracaoTransporteSaldoSubContaOrigem origem : this.origens) {
                retorno = retorno.add(origem.getValor());
            }
        }
        return retorno;
    }

    @Override
    public String toString() {
        return "De " + exercicioOrigem.getAno() + " Para " + exercicioDestino.getAno();
    }
}
