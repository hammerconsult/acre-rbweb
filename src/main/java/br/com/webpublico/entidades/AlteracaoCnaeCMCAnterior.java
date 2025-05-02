package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.base.Objects;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class AlteracaoCnaeCMCAnterior extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Alteração de Cadastro Econômico")
    private AlteracaoCMC alteracaoCMC;
    @ManyToOne
    @Etiqueta("CNAE")
    private CNAE cnae;

    @Transient
    private String descricaoCnae;

    public AlteracaoCnaeCMCAnterior() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlteracaoCMC getAlteracaoCMC() {
        return alteracaoCMC;
    }

    public void setAlteracaoCMC(AlteracaoCMC alteracaoCMC) {
        this.alteracaoCMC = alteracaoCMC;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AlteracaoCnaeCMCAnterior that = (AlteracaoCnaeCMCAnterior) o;
        return Objects.equal(cnae.getId(), that.cnae.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), cnae.getId());
    }

    public void setDescricaoCnae(String descricaoCnae) {
        this.descricaoCnae = descricaoCnae;
    }

    public String getDescricaoCnae() {
        descricaoCnae = "";
        if(cnae != null) {
            descricaoCnae = (cnae.getCodigoCnae() + " - " + cnae.getDescricaoDetalhada());
        }
        return descricaoCnae;
    }

    public Long getIdCnae() {
        return cnae != null ? cnae.getId() : null;
    }
}
