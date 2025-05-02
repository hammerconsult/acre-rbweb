package br.com.webpublico.entidades;

import br.com.webpublico.entidades.comum.Formulario;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
public class ParametroETRFormulario extends SuperEntidade implements Comparable<ParametroETRFormulario> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParametroETR parametroETR;
    @Etiqueta("Ordem")
    @Obrigatorio
    private Integer ordem;
    @Etiqueta("Formulário")
    @Obrigatorio
    @ManyToOne
    private Formulario formulario;
    @OneToMany(mappedBy = "parametroETRFormulario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametroETRFormularioCampoDispensa> formularioCampoDispensaList;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParametroETR getParametroETR() {
        return parametroETR;
    }

    public void setParametroETR(ParametroETR parametroETR) {
        this.parametroETR = parametroETR;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public List<ParametroETRFormularioCampoDispensa> getFormularioCampoDispensaList() {
        return formularioCampoDispensaList;
    }

    public void setFormularioCampoDispensaList(List<ParametroETRFormularioCampoDispensa> formularioCampoDispensaList) {
        this.formularioCampoDispensaList = formularioCampoDispensaList;
    }

    @Override
    public int compareTo(ParametroETRFormulario o) {
        return ordem.compareTo(o.getOrdem());
    }
}
