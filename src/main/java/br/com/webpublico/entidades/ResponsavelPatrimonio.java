package br.com.webpublico.entidades;

import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 23/05/14
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Responsável pela Unidade Organizacional")
public class ResponsavelPatrimonio extends SuperEntidade implements Comparable<ResponsavelPatrimonio> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Unidade Administrativa")
    @ManyToOne
    @Obrigatorio
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta("Responsável pela Unidade")
    @Obrigatorio
    @ManyToOne
    private PessoaFisica responsavel;

    @Etiqueta("Parâmetro Patrimônio")
    @ManyToOne
    private ParametroPatrimonio parametroPatrimonio;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    @Obrigatorio
    private Date inicioVigencia;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;

    public ResponsavelPatrimonio() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        this.responsavel = responsavel;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ParametroPatrimonio getParametroPatrimonio() {
        return parametroPatrimonio;
    }

    public void setParametroPatrimonio(ParametroPatrimonio parametroPatrimonio) {
        this.parametroPatrimonio = parametroPatrimonio;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia != null ? DataUtil.dataSemHorario(fimVigencia) : fimVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia != null ? DataUtil.dataSemHorario(inicioVigencia) : inicioVigencia;
    }

    public boolean temInicioDeVigencia() {
        return inicioVigencia != null;
    }

    @Override
    public int compareTo(ResponsavelPatrimonio o) {
        int x = o.getUnidadeOrganizacional() != null && this.getUnidadeOrganizacional() != null ?
            o.getUnidadeOrganizacional().getDescricao().compareTo(this.getUnidadeOrganizacional().getDescricao()) : 0;
        if (x == 0) {
            x = o.getInicioVigencia() != null && this.getInicioVigencia() != null ?
                o.getInicioVigencia().compareTo(this.getInicioVigencia()) : 0;
        }
        return x;
    }

    @Override
    public String toString() {
        try {
            return responsavel.toString();
        } catch (NullPointerException ex) {
            return "";
        }
    }
}
