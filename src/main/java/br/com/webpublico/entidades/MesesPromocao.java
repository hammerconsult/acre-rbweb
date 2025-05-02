package br.com.webpublico.entidades;

import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Buzatto on 22/12/2015.
 */
@Entity
@Audited
@Etiqueta("Meses para Promoção")
public class MesesPromocao extends SuperEntidade implements Serializable, ValidadorVigencia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Meses")
    private Integer meses;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Inicio de Vigência")
    private Date inicioVigencia;

    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Final de Vigência")
    private Date finalVigencia;

    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private PlanoCargosSalarios planoCargosSalarios;

    @Transient
    private Operacoes operacao;

    public MesesPromocao() {
    }

    public MesesPromocao(PlanoCargosSalarios planoCargosSalarios) {
        this.setPlanoCargosSalarios(planoCargosSalarios);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMeses() {
        return meses;
    }

    public void setMeses(Integer meses) {
        this.meses = meses;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return getFinalVigencia();
    }

    @Override
    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        setFinalVigencia(data);
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public PlanoCargosSalarios getPlanoCargosSalarios() {
        return planoCargosSalarios;
    }

    public void setPlanoCargosSalarios(PlanoCargosSalarios planoCargosSalarios) {
        this.planoCargosSalarios = planoCargosSalarios;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    @Override
    public String toString() {
        return getMeses() + " meses";
    }

    public String toStringComAnos() {
        return getMeses() + " meses - (" + getMeses() / 12 + ") anos";
    }

    public boolean isEditando() {
        return Operacoes.EDITAR.equals(operacao);
    }
}
