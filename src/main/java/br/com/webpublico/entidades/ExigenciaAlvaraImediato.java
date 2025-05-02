package br.com.webpublico.entidades;

import br.com.webpublico.entidades.comum.Formulario;
import br.com.webpublico.entidades.comum.RespostaFormulario;
import br.com.webpublico.exception.ValidacaoException;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class ExigenciaAlvaraImediato extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private SolicitacaoAlvaraImediato solicitacao;
    @ManyToOne
    private Formulario formulario;
    @OneToMany(mappedBy = "exigencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExigenciaAlvaraImediatoFormularioCampo> campos;
    @ManyToOne
    private RespostaFormulario respostaFormulario;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    private Integer sequencial;

    public ExigenciaAlvaraImediato() {
        super();
        campos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoAlvaraImediato getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoAlvaraImediato solicitacao) {
        this.solicitacao = solicitacao;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public List<ExigenciaAlvaraImediatoFormularioCampo> getCampos() {
        return campos;
    }

    public void setCampos(List<ExigenciaAlvaraImediatoFormularioCampo> campos) {
        this.campos = campos;
    }

    public RespostaFormulario getRespostaFormulario() {
        return respostaFormulario;
    }

    public void setRespostaFormulario(RespostaFormulario respostaFormulario) {
        this.respostaFormulario = respostaFormulario;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        if (campos == null || campos.isEmpty()) {
            throw new ValidacaoException("Nenhum campo foi adicionado na exigência.");
        }
    }

    public boolean hasExigenciaAlvaraImediatoFormularioCampo(ExigenciaAlvaraImediatoFormularioCampo exigenciaAlvaraImediatoFormularioCampo) {
        if (campos != null) {
            for (ExigenciaAlvaraImediatoFormularioCampo campo : campos) {
                if (campo.getFormularioCampo().getId().equals(exigenciaAlvaraImediatoFormularioCampo.getFormularioCampo().getId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
