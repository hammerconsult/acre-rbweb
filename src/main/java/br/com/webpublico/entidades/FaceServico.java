/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author terminal4
 */
@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity

@Audited
public class FaceServico extends SuperEntidade implements Serializable, Comparable<FaceServico> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ServicoUrbano servicoUrbano;
    @ManyToOne
    private Face face;
    private Integer ano;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;


    public FaceServico() {
        this.dataRegistro = new Date();
    }

    public FaceServico(Face face, ServicoUrbano servico, Integer ano) {
        this.face = face;
        this.servicoUrbano = servico;
        this.ano = ano;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Face getFace() {
        return face;
    }

    public void setFace(Face face) {
        this.face = face;
    }

    public ServicoUrbano getServicoUrbano() {
        return servicoUrbano;
    }

    public void setServicoUrbano(ServicoUrbano servicoUrbano) {
        this.servicoUrbano = servicoUrbano;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return servicoUrbano.getNome();
    }

    @Override
    public int compareTo(@NotNull FaceServico o) {
        int i = 0;
        try {
            i = this.getAno().compareTo(o.getAno());
            if (i == 0) {
                i = this.getServicoUrbano().getCodigo().compareTo(o.getServicoUrbano().getCodigo());
            }
            return i;
        } catch (Exception e) {
            return i;
        }
    }
}
