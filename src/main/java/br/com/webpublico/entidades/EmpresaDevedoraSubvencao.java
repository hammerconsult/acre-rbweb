package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by William on 01/06/2016.
 */
@Entity
@Audited
public class EmpresaDevedoraSubvencao extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @ManyToOne
    private CadastroEconomicoSubvencao empresaCredora;
    private Integer ordem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public CadastroEconomicoSubvencao getEmpresaCredora() {
        return empresaCredora;
    }

    public void setEmpresaCredora(CadastroEconomicoSubvencao empresaCredora) {
        this.empresaCredora = empresaCredora;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

}
