/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.rh.esocial.TipoHorarioFlexivelESocial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Horário de Trabalho")
public class HorarioDeTrabalho extends SuperEntidade implements Serializable {

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
    @Etiqueta("Entrada")
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date entrada;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Início do Intervalo")
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date intervalo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Retorno do Intervalo")
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date retornoIntervalo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Saída")
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date saida;
    @Etiqueta("Duração da Jornada(minutos)")
    @Obrigatorio
    private Integer duracaoJornada;
    @Etiqueta("Permite Horário Flexível")
    @Obrigatorio
    private Boolean permiteHorarioFlexivel;
    @Etiqueta("Tipo de Intervalo da Jornada")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoHorarioFlexivelESocial tipoHorarioIntervalo;
    @Obrigatorio
    @Etiqueta("Início de Vigência")
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Etiqueta("Final de Vigência")
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;

    public HorarioDeTrabalho() {
    }

    public HorarioDeTrabalho(Date entrada, Date intervalo, Date retornoIntervalo, Date saida) {
        this.entrada = entrada;
        this.intervalo = intervalo;
        this.retornoIntervalo = retornoIntervalo;
        this.saida = saida;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Date getEntrada() {
        return entrada;
    }

    public void setEntrada(Date entrada) {
        this.entrada = entrada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Date intervalo) {
        this.intervalo = intervalo;
    }

    public Date getRetornoIntervalo() {
        return retornoIntervalo;
    }

    public void setRetornoIntervalo(Date retornoIntervalo) {
        this.retornoIntervalo = retornoIntervalo;
    }

    public Date getSaida() {
        return saida;
    }

    public void setSaida(Date saida) {
        this.saida = saida;
    }

    public Integer getDuracaoJornada() {
        return duracaoJornada;
    }

    public void setDuracaoJornada(Integer duracaoJornada) {
        this.duracaoJornada = duracaoJornada;
    }

    public Boolean getPermiteHorarioFlexivel() {
        return permiteHorarioFlexivel == null ? false : permiteHorarioFlexivel;
    }

    public void setPermiteHorarioFlexivel(Boolean permiteHorarioFlexivel) {
        this.permiteHorarioFlexivel = permiteHorarioFlexivel;
    }

    public TipoHorarioFlexivelESocial getTipoHorarioIntervalo() {
        return tipoHorarioIntervalo;
    }

    public void setTipoHorarioIntervalo(TipoHorarioFlexivelESocial tipoHorarioIntervalo) {
        this.tipoHorarioIntervalo = tipoHorarioIntervalo;
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
    public String toString() {
        String descricao = "";

        if (entrada != null) {
            descricao += entrada.toString() + " - ";
        }

        if (intervalo != null) {
            descricao += intervalo.toString() + " - ";
        }

        if (retornoIntervalo != null) {
            descricao += retornoIntervalo.toString() + " - ";
        }

        if (saida != null) {
            descricao += saida.toString() + " - ";
        }

        if (descricao.length() != 0) {
            descricao = descricao.substring(0, descricao.length() - 3);
        }

        return descricao;
    }
}
