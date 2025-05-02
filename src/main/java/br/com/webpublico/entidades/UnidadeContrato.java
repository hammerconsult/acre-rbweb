package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Unidade do Contrato")
public class UnidadeContrato extends SuperEntidade implements Comparable<UnidadeContrato> {

    private static final Logger logger = LoggerFactory.getLogger(UnidadeContrato.class);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Contrato")
    private Contrato contrato;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeAdministrativa;

    @Transient
    @Etiqueta("Unidade Administrativa")
    private HierarquiaOrganizacional hierarquiaAdm;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;

    public UnidadeContrato() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeAdministrativa = unidadeOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaAdm() {
        return hierarquiaAdm;
    }

    public void setHierarquiaAdm(HierarquiaOrganizacional hierarquiaAdm) {
        this.hierarquiaAdm = hierarquiaAdm;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    @Override
    public int compareTo(UnidadeContrato o) {
        if (o.getInicioVigencia() != null && getInicioVigencia() != null) {
            return o.getInicioVigencia().compareTo(getInicioVigencia());
        }
        return 0;
    }
}
