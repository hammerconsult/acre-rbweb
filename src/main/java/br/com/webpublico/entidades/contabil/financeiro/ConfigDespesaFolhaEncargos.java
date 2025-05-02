package br.com.webpublico.entidades.contabil.financeiro;

import br.com.webpublico.entidades.ContaDespesa;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.TipoDespesaFolhaEncargos;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Configuração do Relatório de Despesa de Folha e Encargos")
public class ConfigDespesaFolhaEncargos extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Despesa")
    private TipoDespesaFolhaEncargos tipoDespesaFolhaEncargos;
    @ManyToOne
    @Etiqueta("Conta de Despesa")
    private ContaDespesa contaDespesa;
    @ManyToOne
    @Etiqueta("Exercicio")
    private Exercicio exercicio;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoDespesaFolhaEncargos getTipoDespesaFolhaEncargos() {
        return tipoDespesaFolhaEncargos;
    }

    public void setTipoDespesaFolhaEncargos(TipoDespesaFolhaEncargos tipoDespesaFolhaEncargos) {
        this.tipoDespesaFolhaEncargos = tipoDespesaFolhaEncargos;
    }

    public ContaDespesa getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(ContaDespesa contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    @Override
    public String toString() {
        return "Tipo de Despesa: " + tipoDespesaFolhaEncargos.getDescricao() + " - " + contaDespesa;
    }
}
