package br.com.webpublico.entidades;

import br.com.webpublico.enums.administrativo.frotas.TipoMotorista;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 03/09/14
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Motorista")
public class Motorista implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Pesquisavel
    @Etiqueta("Tipo")
    @Tabelavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoMotorista tipo;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Pesquisavel
    @Etiqueta("Motorista")
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private PessoaFisica pessoaFisica;
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;

    @Transient
    private Long criadoEm;

    public Motorista() {
        criadoEm = System.nanoTime();
    }

    public Motorista(Motorista motorista, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.setId(motorista.getId());
        this.setPessoaFisica(motorista.getPessoaFisica());
        this.setTipo(motorista.getTipo());
        this.setUnidadeOrganizacional(motorista.getUnidadeOrganizacional());
        this.setHierarquiaOrganizacional(hierarquiaOrganizacional);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public TipoMotorista getTipo() {
        return tipo;
    }

    public void setTipo(TipoMotorista tipo) {
        this.tipo = tipo;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Motorista)) {
            return false;
        }
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return pessoaFisica.getCpf() + " - " + pessoaFisica.getNome();
    }
}
