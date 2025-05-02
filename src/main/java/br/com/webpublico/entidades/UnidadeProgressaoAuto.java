package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Unidades da Progressao Automática")
public class UnidadeProgressaoAuto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProgressaoAuto progressaoAuto;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    public UnidadeProgressaoAuto() {
    }

    public UnidadeProgressaoAuto(ProgressaoAuto progressaoAuto, UnidadeOrganizacional unidadeOrganizacional) {
        this.progressaoAuto = progressaoAuto;
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProgressaoAuto getProgressaoAuto() {
        return progressaoAuto;
    }

    public void setProgressaoAuto(ProgressaoAuto progressaoAuto) {
        this.progressaoAuto = progressaoAuto;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }
}
