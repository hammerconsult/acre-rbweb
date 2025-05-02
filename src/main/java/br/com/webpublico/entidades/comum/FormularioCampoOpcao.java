package br.com.webpublico.entidades.comum;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Audited
public class FormularioCampoOpcao extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private FormularioCampo formularioCampo;
    @Etiqueta("Opção")
    @Obrigatorio
    private String opcao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FormularioCampo getFormularioCampo() {
        return formularioCampo;
    }

    public void setFormularioCampo(FormularioCampo formularioCampo) {
        this.formularioCampo = formularioCampo;
    }

    public String getOpcao() {
        return opcao;
    }

    public void setOpcao(String opcao) {
        this.opcao = opcao;
    }
}
