package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class ParamDividaAtivaDivida extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Divida divida;
    @ManyToOne
    private ParametrosDividaAtiva parametrosDividaAtiva;

    public ParamDividaAtivaDivida() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public ParametrosDividaAtiva getParametrosDividaAtiva() {
        return parametrosDividaAtiva;
    }

    public void setParametrosDividaAtiva(ParametrosDividaAtiva parametrosDividaAtiva) {
        this.parametrosDividaAtiva = parametrosDividaAtiva;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ParamParcelamentoDivida[ id=" + id + " ]";
    }
}
