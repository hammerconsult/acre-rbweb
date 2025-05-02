package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class ParametroLicenciamentoAmbiental extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Obrigatorio
    @Etiqueta("Secretaria")
    private String secretaria;
    @ManyToOne(cascade = CascadeType.ALL)
    @Obrigatorio
    @Etiqueta("Secretário")
    private SecretarioLicenciamentoAmbiental secretario;
    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Secretário Adjunto")
    private SecretarioLicenciamentoAmbiental secretarioAdjunto;
    @ManyToOne(cascade = CascadeType.ALL)
    @Obrigatorio
    @Etiqueta("Diretor")
    private SecretarioLicenciamentoAmbiental diretor;

    public ParametroLicenciamentoAmbiental() {
        this.secretario = new SecretarioLicenciamentoAmbiental();
        this.secretarioAdjunto = new SecretarioLicenciamentoAmbiental();
        this.diretor = new SecretarioLicenciamentoAmbiental();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(String secretaria) {
        this.secretaria = secretaria;
    }

    public SecretarioLicenciamentoAmbiental getSecretario() {
        return secretario;
    }

    public void setSecretario(SecretarioLicenciamentoAmbiental secretario) {
        this.secretario = secretario;
    }

    public SecretarioLicenciamentoAmbiental getSecretarioAdjunto() {
        return secretarioAdjunto;
    }

    public void setSecretarioAdjunto(SecretarioLicenciamentoAmbiental secretarioAdjunto) {
        this.secretarioAdjunto = secretarioAdjunto;
    }

    public SecretarioLicenciamentoAmbiental getDiretor() {
        return diretor;
    }

    public void setDiretor(SecretarioLicenciamentoAmbiental diretor) {
        this.diretor = diretor;
    }
}
