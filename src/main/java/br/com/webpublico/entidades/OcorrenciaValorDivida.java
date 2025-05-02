/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author daniel
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Tributario")
public class OcorrenciaValorDivida implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    @ManyToOne
    private Construcao construcao;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Ocorrencia ocorrencia;
    @ManyToOne
    private ValorDivida valorDivida;

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public Construcao getConstrucao() {
        return construcao;
    }

    public void setConstrucao(Construcao construcao) {
        this.construcao = construcao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ocorrencia getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(Ocorrencia ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    public ValorDivida getValorDivida() {
        return valorDivida;
    }

    public void setValorDivida(ValorDivida valorDivida) {
        this.valorDivida = valorDivida;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OcorrenciaValorDivida)) {
            return false;
        }
        OcorrenciaValorDivida other = (OcorrenciaValorDivida) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Cadastro Imobiliário: " + cadastroImobiliario + ", Construção : " + construcao + ", Dívida :" + valorDivida.getDivida();
    }


}
