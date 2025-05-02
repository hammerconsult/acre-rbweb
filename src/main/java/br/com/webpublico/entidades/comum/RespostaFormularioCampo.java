package br.com.webpublico.entidades.comum;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.SuperEntidade;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Audited
public class RespostaFormularioCampo extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private RespostaFormulario respostaFormulario;
    @ManyToOne
    private FormularioCampo formularioCampoOrigem;
    @ManyToOne
    private FormularioCampo formularioCampo;
    private String ordem;
    private String resposta;
    @ManyToOne
    private Arquivo arquivo;
    private String justificativa;

    @Transient
    private List<String> respostaList;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RespostaFormulario getRespostaFormulario() {
        return respostaFormulario;
    }

    public void setRespostaFormulario(RespostaFormulario respostaFormulario) {
        this.respostaFormulario = respostaFormulario;
    }

    public FormularioCampo getFormularioCampoOrigem() {
        return formularioCampoOrigem;
    }

    public void setFormularioCampoOrigem(FormularioCampo formularioCampoOrigem) {
        this.formularioCampoOrigem = formularioCampoOrigem;
    }

    public FormularioCampo getFormularioCampo() {
        return formularioCampo;
    }

    public void setFormularioCampo(FormularioCampo formularioCampo) {
        this.formularioCampo = formularioCampo;
    }

    public String getOrdem() {
        return ordem;
    }

    public void setOrdem(String ordem) {
        this.ordem = ordem;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public List<String> getRespostaList() {
        if ((this.respostaList == null || this.respostaList.isEmpty()) && !Strings.isNullOrEmpty(resposta)) {
            this.respostaList = Arrays.asList(resposta.split(";"));
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
