package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by hudson on 03/11/15.
 */

@Entity

@Audited
@Etiqueta("Invalidez Medico")
@GrupoDiagrama(nome = "RecursosHumanos")
public class InvalidezMedico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Médico")
    @ManyToOne
    private Medico medico;
    @Etiqueta("Nome")
    @Tabelavel
    @Pesquisavel
    private String nomeMedico;
    @Etiqueta("Registro CRM")
    @Tabelavel
    @Pesquisavel
    private String registroCRM;
    @ManyToOne
    private InvalidezAposentado invalidezAposentado;
    @ManyToOne
    private InvalidezPensao invalidezPensao;

    public InvalidezMedico() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public String getNomeMedico() {
        return nomeMedico;
    }

    public void setNomeMedico(String nomeMedico) {
        this.nomeMedico = nomeMedico;
    }

    public String getRegistroCRM() {
        return registroCRM;
    }

    public void setRegistroCRM(String registroCRM) {
        this.registroCRM = registroCRM;
    }

    public InvalidezAposentado getInvalidezAposentado() {
        return invalidezAposentado;
    }

    public void setInvalidezAposentado(InvalidezAposentado invalidezAposentado) {
        this.invalidezAposentado = invalidezAposentado;
    }

    public InvalidezPensao getInvalidezPensao() {
        return invalidezPensao;
    }

    public void setInvalidezPensao(InvalidezPensao invalidezPensao) {
        this.invalidezPensao = invalidezPensao;
    }

    public String getMedicos() {
        if (medico == null) {
            return nomeMedico + " - " + registroCRM;
        } else {
            return String.valueOf(medico);
        }
    }

    @Override
    public String toString() {
        return getMedicos().toString();
    }
}
