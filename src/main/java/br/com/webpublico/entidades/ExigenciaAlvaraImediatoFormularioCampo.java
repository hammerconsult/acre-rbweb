package br.com.webpublico.entidades;

import br.com.webpublico.entidades.comum.FormularioCampo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ExigenciaAlvaraImediatoFormularioCampo extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ExigenciaAlvaraImediato exigencia;
    @Etiqueta("Campo")
    @Obrigatorio
    @ManyToOne
    private FormularioCampo formularioCampo;
    @Etiqueta("Justificativa")
    @Obrigatorio
    private String justificativa;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExigenciaAlvaraImediato getExigencia() {
        return exigencia;
    }

    public void setExigencia(ExigenciaAlvaraImediato exigencia) {
        this.exigencia = exigencia;
    }

    public FormularioCampo getFormularioCampo() {
        return formularioCampo;
    }

    public void setFormularioCampo(FormularioCampo formularioCampo) {
        this.formularioCampo = formularioCampo;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
}
