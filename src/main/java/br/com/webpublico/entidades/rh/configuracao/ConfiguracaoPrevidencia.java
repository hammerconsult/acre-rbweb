package br.com.webpublico.entidades.rh.configuracao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Wellington Abdo on 23/08/2016.
 */
@Entity
@Audited
public class ConfiguracaoPrevidencia extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Início de Registro Individualizado de Contribuição")
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date inicioRegistroIndividualizado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioRegistroIndividualizado() {
        return inicioRegistroIndividualizado;
    }

    public void setInicioRegistroIndividualizado(Date inicioRegistroIndividualizado) {
        this.inicioRegistroIndividualizado = inicioRegistroIndividualizado;
    }
}
