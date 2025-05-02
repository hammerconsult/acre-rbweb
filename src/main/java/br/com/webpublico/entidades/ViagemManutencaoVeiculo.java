package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 29/08/14
 * Time: 17:14
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ViagemManutencaoVeiculo implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Viagem viagem;
    @ManyToOne
    private ManutencaoObjetoFrota manutencaoObjetoFrota;
    @Transient
    private Long criadoEm;

    public ViagemManutencaoVeiculo() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Viagem getViagem() {
        return viagem;
    }

    public void setViagem(Viagem viagem) {
        this.viagem = viagem;
    }

    public ManutencaoObjetoFrota getManutencaoObjetoFrota() {
        return manutencaoObjetoFrota;
    }

    public void setManutencaoObjetoFrota(ManutencaoObjetoFrota manutencaoObjetoFrota) {
        this.manutencaoObjetoFrota = manutencaoObjetoFrota;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ViagemManutencaoVeiculo)) {
            return false;
        }
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
