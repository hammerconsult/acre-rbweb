package br.com.webpublico.entidades;

import br.com.webpublico.entidades.comum.Formulario;
import br.com.webpublico.entidades.comum.RespostaFormulario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
public class ExigenciaETRFormulario extends SuperEntidade implements Comparable<ExigenciaETRFormulario> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ExigenciaETR exigenciaETR;
    @Etiqueta("Ordem")
    @Obrigatorio
    private Integer ordem;
    @Etiqueta("Formulário")
    @Obrigatorio
    @ManyToOne
    private Formulario formulario;
    @ManyToOne
    private RespostaFormulario respostaFormulario;
    @OneToMany(mappedBy = "exigenciaETRFormulario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExigenciaETRFormularioCampo> formularioCampoList;

    public ExigenciaETRFormulario() {
        super();
        formularioCampoList = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExigenciaETR getExigenciaETR() {
        return exigenciaETR;
    }

    public void setExigenciaETR(ExigenciaETR exigenciaETR) {
        this.exigenciaETR = exigenciaETR;
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

    public List<ExigenciaETRFormularioCampo> getFormularioCampoList() {
        return formularioCampoList;
    }

    public void setFormularioCampoList(List<ExigenciaETRFormularioCampo> formularioCampoList) {
        this.formularioCampoList = formularioCampoList;
    }

    public RespostaFormulario getRespostaFormulario() {
        return respostaFormulario;
    }

    public void setRespostaFormulario(RespostaFormulario respostaFormulario) {
        this.respostaFormulario = respostaFormulario;
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        if (formularioCampoList == null || formularioCampoList.isEmpty()) {
            throw new ValidacaoException("Nenhum campo informado.");
        }
    }

    @Override
    public int compareTo(ExigenciaETRFormulario o) {
        return ordem.compareTo(o.getOrdem());
    }

    public boolean hasExigenciaETRFormularioCampo(ExigenciaETRFormularioCampo exigenciaETRFormularioCampo) {
        if (formularioCampoList != null) {
            for (ExigenciaETRFormularioCampo etrFormularioCampo : formularioCampoList) {
                if (etrFormularioCampo.getFormularioCampo().getId().equals(exigenciaETRFormularioCampo.getFormularioCampo().getId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
