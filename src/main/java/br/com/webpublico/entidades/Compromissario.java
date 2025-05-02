package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Desenvolvimento on 03/05/2016.
 */
@Entity
@Audited
public class Compromissario extends SuperEntidade implements ValidadorVigencia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Pessoa compromissario;

    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;

    @Etiqueta("Proporção")
    private Double proporcao;

    private Boolean atual;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;

    @Temporal(TemporalType.DATE)
    private Date fimVigencia;

    public Compromissario() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getCompromissario() {
        return compromissario;
    }

    public void setCompromissario(Pessoa compromissario) {
        this.compromissario = compromissario;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
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

    public Double getProporcao() {
        return proporcao;
    }

    public void setProporcao(Double proporcao) {
        this.proporcao = proporcao;
    }

    public Boolean getAtual() {
        return atual;
    }

    public void setAtual(Boolean atual) {
        this.atual = atual;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public void encerraVigencia() {
        DateTime dateTime = new DateTime();
        dateTime.minusDays(1);
        setFimVigencia(dateTime.toDate());
    }

    @Override
    public String toString() {
        try {
            return compromissario.getNome();
        } catch (Exception ex) {
            return "";
        }
    }
}
