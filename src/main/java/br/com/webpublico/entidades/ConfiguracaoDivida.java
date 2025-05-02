package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCadastroTributario;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Audited
@Entity
public class ConfiguracaoDivida extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Entidade entidade;
    @Enumerated(EnumType.STRING)
    private Calculo.TipoCalculo tipoCalculo;
    @Enumerated(EnumType.STRING)
    private TipoCadastroTributario tipoCadastroTributario;
    @ManyToOne
    private Divida divida;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public Calculo.TipoCalculo getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(Calculo.TipoCalculo tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }
}
