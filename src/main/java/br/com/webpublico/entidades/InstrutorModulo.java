package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by AndreGustavo on 14/10/2014.
 */
@Entity
@Audited
@Etiqueta("Instrutor do Módulo")
public class InstrutorModulo extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PessoaFisica pessoaFisica;
    @ManyToOne
    private ModuloCapacitacao moduloCapacitacao;
    @ManyToOne
    private AreaFormacao areaFormacao;

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

    public ModuloCapacitacao getModuloCapacitacao() {
        return moduloCapacitacao;
    }

    public void setModuloCapacitacao(ModuloCapacitacao moduloCapacitacao) {
        this.moduloCapacitacao = moduloCapacitacao;
    }

    public AreaFormacao getAreaFormacao() {
        return areaFormacao;
    }

    public void setAreaFormacao(AreaFormacao areaFormacao) {
        this.areaFormacao = areaFormacao;
    }
}
