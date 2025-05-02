/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaReceitaAbrasf;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Camila
 */
@Entity
@Audited
@Etiqueta("Configuracao da Planilha da ABRASF")
public class ConfiguracaoPlanilhaABRASF extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Exercício")
    @Pesquisavel
    @Obrigatorio
    private Exercicio exercicio;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Conta de Receita")
    @Pesquisavel
    @Obrigatorio
    private ContaReceita contaReceita;
    @Enumerated(EnumType.STRING)
    private TipoContaReceitaAbrasf tipoContaReceitaAbrasf;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public TipoContaReceitaAbrasf getTipoContaReceitaAbrasf() {
        return tipoContaReceitaAbrasf;
    }

    public void setTipoContaReceitaAbrasf(TipoContaReceitaAbrasf tipoContaReceitaAbrasf) {
        this.tipoContaReceitaAbrasf = tipoContaReceitaAbrasf;
    }
}

