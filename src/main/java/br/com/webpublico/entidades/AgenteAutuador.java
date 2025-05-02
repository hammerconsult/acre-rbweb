package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Fabio on 25/07/2018.
 */
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Agente Autuador")
@Entity
@Audited
public class AgenteAutuador implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Etiqueta("Pessoa")
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private PessoaFisica pessoaFisica;
    @Etiqueta("Ativo")
    @Pesquisavel
    @Tabelavel
    private Boolean ativo;

    public AgenteAutuador() {
        this.ativo = Boolean.TRUE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return this.getPessoaFisica().toString();
    }
}
