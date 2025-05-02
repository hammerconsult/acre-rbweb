package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author octavio
 */
@Audited
@Entity
@Table(name = "PROGCOBRANCAPESSOA")
public class ProgramacaoCobrancaPessoa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Programação de Cobrança")
    private ProgramacaoCobranca programacaoCobranca;
    @ManyToOne
    @Etiqueta("Contribuinte")
    private Pessoa contribuinte;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProgramacaoCobranca getProgramacaoCobranca() {
        return programacaoCobranca;
    }

    public void setProgramacaoCobranca(ProgramacaoCobranca programacaoCobranca) {
        this.programacaoCobranca = programacaoCobranca;
    }

    public Pessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(Pessoa contribuinte) {
        this.contribuinte = contribuinte;
    }
}
