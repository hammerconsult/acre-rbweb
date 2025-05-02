package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoIntegradorRHContabil;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Integração RH/Contábil - Folha")
public class TipoIntegracaoRHContabil extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoIntegradorRHContabil tipo;
    @ManyToOne
    private IntegracaoRHContabil integracaoRHContabil;

    public TipoIntegracaoRHContabil() {
    }

    public TipoIntegracaoRHContabil(IntegracaoRHContabil integracaoRHContabil, TipoIntegradorRHContabil tipo) {
        this.tipo = tipo;
        this.integracaoRHContabil = integracaoRHContabil;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoIntegradorRHContabil getTipo() {
        return tipo;
    }

    public void setTipo(TipoIntegradorRHContabil tipo) {
        this.tipo = tipo;
    }

    public IntegracaoRHContabil getIntegracaoRHContabil() {
        return integracaoRHContabil;
    }

    public void setIntegracaoRHContabil(IntegracaoRHContabil integracaoRHContabil) {
        this.integracaoRHContabil = integracaoRHContabil;
    }
}

