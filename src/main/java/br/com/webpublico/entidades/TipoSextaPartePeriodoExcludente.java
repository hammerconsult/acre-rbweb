package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class TipoSextaPartePeriodoExcludente extends SuperEntidade implements ValidadorVigencia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private TipoSextaParte tipoSextaParte;
    @Obrigatorio
    @Etiqueta("Início")
    @Temporal(TemporalType.DATE)
    private Date inicio;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Fim")
    private Date fim;
    private String observacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoSextaParte getTipoSextaParte() {
        return tipoSextaParte;
    }

    public void setTipoSextaParte(TipoSextaParte tipoSextaParte) {
        this.tipoSextaParte = tipoSextaParte;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public Date getInicioVigencia() {
        return getInicio();
    }

    @Override
    public Date getFimVigencia() {
        return getFim();
    }

    @Override
    public void setInicioVigencia(Date data) {
        setInicio(data);
    }

    @Override
    public void setFimVigencia(Date data) {
        setFim(data);
    }
}
