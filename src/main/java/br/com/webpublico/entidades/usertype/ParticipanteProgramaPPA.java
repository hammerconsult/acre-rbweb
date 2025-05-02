package br.com.webpublico.entidades.usertype;

import br.com.webpublico.entidades.ProgramaPPA;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "PPA")
@Entity
@Audited
@Etiqueta("Participante Programa PPA")
public class ParticipanteProgramaPPA extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProgramaPPA programaPPA;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @OneToOne
    private ParticipanteProgramaPPA origem;
    @Etiqueta("Somente Leitura")
    private Boolean somenteLeitura;

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ParticipanteProgramaPPA getOrigem() {
        return origem;
    }

    public void setOrigem(ParticipanteProgramaPPA origem) {
        this.origem = origem;
    }

    public Boolean getSomenteLeitura() {
        return somenteLeitura;
    }

    public void setSomenteLeitura(Boolean somenteLeitura) {
        this.somenteLeitura = somenteLeitura;
    }

    public ParticipanteProgramaPPA() {
        somenteLeitura = false;
        criadoEm = System.nanoTime();
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public String toString() {
        return unidadeOrganizacional.toString();
    }
}
