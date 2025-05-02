package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@GrupoDiagrama(nome = "Configuração RH")
public class CargoEmpregadorESocial extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfiguracaoEmpregadorESocial empregador;
    @ManyToOne
    private Cargo cargo;

    public CargoEmpregadorESocial() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoEmpregadorESocial getEmpregador() {
        return empregador;
    }

    public void setEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        this.empregador = empregador;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    @Override
    public String toString() {
        return empregador + "";
    }
}
