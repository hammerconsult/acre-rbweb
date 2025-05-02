package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPessoaPermitido;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 13/01/15
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Etiqueta("Configuração de Pessoa, Classe e Conta Bancária")
public class ConfigContaBancariaPessoa extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Conta Corrente Bancária")
    private ContaCorrenteBancaria contaCorrenteBancaria;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta(value = "Pessoa")
    private Pessoa pessoa;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta(value = "Classe")
    private ClasseCredor classeCredor;

    public ConfigContaBancariaPessoa() {
    }

    public ConfigContaBancariaPessoa(ContaCorrenteBancaria contaCorrenteBancaria, Pessoa pessoa, ClasseCredor classeCredor) {
        this.contaCorrenteBancaria = contaCorrenteBancaria;
        this.pessoa = pessoa;
        this.classeCredor = classeCredor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContaCorrenteBancaria getContaCorrenteBancaria() {
        return contaCorrenteBancaria;
    }

    public void setContaCorrenteBancaria(ContaCorrenteBancaria contaCorrenteBancaria) {
        this.contaCorrenteBancaria = contaCorrenteBancaria;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public String toString() {
        return pessoa + " - " + classeCredor + " - " + contaCorrenteBancaria.toString();
    }
}
