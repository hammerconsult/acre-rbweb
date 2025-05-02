/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.rh.esocial.TipoJornadaTrabalho;
import br.com.webpublico.enums.rh.esocial.TipoTempoParcialJornadaTrabalho;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Jornada de Trabalho")
public class JornadaDeTrabalho extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Integer codigo;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Horas Semanais")
    private Integer horasSemanal;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Horas Mensais")
    private Integer horasMensal;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Jornada de Trabalho")
    private TipoJornadaTrabalho tipoJornadaTrabalho;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tempo Parcial")
    private TipoTempoParcialJornadaTrabalho tipoTempoParcial;

    public JornadaDeTrabalho() {
    }

    public JornadaDeTrabalho(Integer horasSemanal, Integer horasMensal) {
        this.horasSemanal = horasSemanal;
        this.horasMensal = horasMensal;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getHorasMensal() {
        return horasMensal;
    }

    public void setHorasMensal(Integer horasMensal) {
        this.horasMensal = horasMensal;
    }

    public Integer getHorasSemanal() {
        return horasSemanal;
    }

    public void setHorasSemanal(Integer horasSemanal) {
        this.horasSemanal = horasSemanal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoJornadaTrabalho getTipoJornadaTrabalho() {
        return tipoJornadaTrabalho;
    }

    public void setTipoJornadaTrabalho(TipoJornadaTrabalho tipoJornadaTrabalho) {
        this.tipoJornadaTrabalho = tipoJornadaTrabalho;
    }

    public TipoTempoParcialJornadaTrabalho getTipoTempoParcial() {
        return tipoTempoParcial;
    }

    public void setTipoTempoParcial(TipoTempoParcialJornadaTrabalho tipoTempoParcial) {
        this.tipoTempoParcial = tipoTempoParcial;
    }

    @Override
    public String toString() {
        return "Horas Semanais:" + horasSemanal + ", Horas Mensais:" + horasMensal;
    }

}
