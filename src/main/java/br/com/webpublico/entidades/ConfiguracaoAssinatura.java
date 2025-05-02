package br.com.webpublico.entidades;

import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class ConfiguracaoAssinatura extends SuperEntidade implements EntidadeWebPublico {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Pessoa pessoa;
    @Enumerated(EnumType.STRING)
    private ModuloTipoDoctoOficial modulo;
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configuracaoAssinatura")
    private List<ConfiguracaoAssinaturaUnidade> unidades;

    public ConfiguracaoAssinatura() {
        unidades = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ModuloTipoDoctoOficial getModulo() {
        return modulo;
    }

    public void setModulo(ModuloTipoDoctoOficial modulo) {
        this.modulo = modulo;
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

    public List<ConfiguracaoAssinaturaUnidade> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<ConfiguracaoAssinaturaUnidade> unidades) {
        this.unidades = unidades;
    }

    @Override
    public String toString() {
        return pessoa.toString() + " - " + modulo.getDescricao();
    }
}
