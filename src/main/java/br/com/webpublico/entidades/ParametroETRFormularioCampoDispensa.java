package br.com.webpublico.entidades;

import br.com.webpublico.entidades.comum.FormularioCampo;
import br.com.webpublico.interfaces.RespostaFormularioCampo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Audited
public class ParametroETRFormularioCampoDispensa extends SuperEntidade implements RespostaFormularioCampo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParametroETRFormulario parametroETRFormulario;
    @Etiqueta("Campo")
    @Obrigatorio
    @ManyToOne
    private FormularioCampo formularioCampo;
    @Etiqueta("Resposta")
    @Obrigatorio
    private String resposta;

    @Transient
    private List<String> respostaList;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParametroETRFormulario getParametroETRFormulario() {
        return parametroETRFormulario;
    }

    public void setParametroETRFormulario(ParametroETRFormulario parametroETRFormulario) {
        this.parametroETRFormulario = parametroETRFormulario;
    }

    public FormularioCampo getFormularioCampo() {
        return formularioCampo;
    }

    public void setFormularioCampo(FormularioCampo formularioCampo) {
        this.formularioCampo = formularioCampo;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public List<String> getRespostaList() {
        if (respostaList == null && !Strings.isNullOrEmpty(resposta)) {
            respostaList = Arrays.asList(resposta.split(";"));
        }
        return respostaList;
    }

    public void setRespostaList(List<String> respostaList) {
        this.respostaList = respostaList;
        if (this.respostaList != null) {
            this.resposta = StringUtils.join(this.respostaList, ";");
        }
    }
}
