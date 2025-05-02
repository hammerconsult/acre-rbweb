package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoFeirante;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@Etiqueta("Feirante")
public class Feirante extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Código")
    private Long codigo;

    @Etiqueta("Feirante")
    @Obrigatorio
    @ManyToOne
    private PessoaFisica pessoaFisica;

    @OneToMany(mappedBy = "feirante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeiranteFeira> feiras;

    @Etiqueta("Tipo de Feirante")
    @Enumerated(EnumType.STRING)
    private TipoFeirante tipoFeirante;

    public Feirante() {
        feiras = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public List<FeiranteFeira> getFeiras() {
        return feiras;
    }

    public void setFeiras(List<FeiranteFeira> feiras) {
        this.feiras = feiras;
    }

    public TipoFeirante getTipoFeirante() {
        return tipoFeirante;
    }

    public void setTipoFeirante(TipoFeirante tipoFeirante) {
        this.tipoFeirante = tipoFeirante;
    }

    @Override
    public String toString() {
        return codigo + " - " + pessoaFisica;
    }
}
