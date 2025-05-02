package br.com.webpublico.entidades;

import br.com.webpublico.enums.ClasseDaConta;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

/**
 * Determina o tipo do plano de contas que será criado. Por exemplo: "Plano
 * Contábil TCU 2011", "Plano Contábil Prefeitura 2011", "Plano de Receitas TCU
 * 2010", "Plano de Receitas Prefeitura 2011".
 *
 * Determina, ainda, como o código das contas será formatado de acordo com os
 * níveis. A máscara "9.99.99.999" determina 4 níveis e o número de dígitos que
 * cada nível possuirá. Só deve aceitar números.
 *
 */
@Etiqueta("Tipo Conta")
public class TipoConta extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Descrição")
    @Pesquisavel
    private String descricao;
    /**
     * Máscara que formatará o código das contas e determinará os níveis
     * possíveis. Só deve aceitar números.
     */
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Máscara")
    private String mascara;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Classe da Conta")
    private ClasseDaConta classeDaConta;
    @Obrigatorio
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Exercício")
    @Tabelavel
    private Exercicio exercicio;

    public TipoConta() {
        super();
    }

    public TipoConta(String descricao, String mascara) {
        super();
        this.descricao = descricao;
        this.mascara = mascara;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMascara() {
        return mascara;
    }

    public void setMascara(String mascara) {
        this.mascara = mascara;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ClasseDaConta getClasseDaConta() {
        return classeDaConta;
    }

    public void setClasseDaConta(ClasseDaConta classeDaConta) {
        this.classeDaConta = classeDaConta;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
