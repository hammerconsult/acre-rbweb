package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by carlos on 25/04/17.
 */
@Entity
@Audited
@Etiqueta("Representante da Licitação")
public class RepresentanteLicitacao extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Nome")
    @Tabelavel
    @Pesquisavel
    private String nome;

    @Obrigatorio
    @Etiqueta("CPF")
    @Tabelavel
    @Pesquisavel
    private String cpf;

    public RepresentanteLicitacao() {
    }

    public RepresentanteLicitacao(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        String retorno = "";
        if(getCpf() != null && !getCpf().isEmpty()){
            retorno = getCpf() + " ";
        }
        if(getNome() != null && !getNome().isEmpty()){
            retorno += getNome();
        }
        return retorno;
    }
}
