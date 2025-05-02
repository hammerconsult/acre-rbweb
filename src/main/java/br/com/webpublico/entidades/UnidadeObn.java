package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by israeleriston on 18/05/16.
 */
@Entity
@Audited
@Etiqueta("Unidade Obn")
public class UnidadeObn extends SuperEntidade implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private ContratoObn contratoObn;
    @Transient
    private String descricaoUnidade;

    public UnidadeObn(UnidadeOrganizacional unidadeOrganizacional, ContratoObn contratoObn, String descricaoUnidade) {
        this.unidadeOrganizacional = new UnidadeOrganizacional();
        this.contratoObn = new ContratoObn();
        this.descricaoUnidade = descricaoUnidade;
    }

    public UnidadeObn() {
        this.unidadeOrganizacional = new UnidadeOrganizacional();
        this.contratoObn = new ContratoObn();
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

    public ContratoObn getContratoObn() {
        return contratoObn;
    }

    public void setContratoObn(ContratoObn contratoObn) {
        this.contratoObn = contratoObn;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }
}
