package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by mga on 19/02/2018.
 */
@Etiqueta(value = "Tipo de Objeto de Compra/Conta Despesa")
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Table(name = "TIPOOBJETOCOMPRACONTADESP")
public class TipoObjetoCompraContaDespesa extends SuperEntidade{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Configuração Tipo Objeto de Compra")
    private ConfigTipoObjetoCompra configTipoObjetoCompra;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta de Despesa")
    private Conta contaDespesa;

    public TipoObjetoCompraContaDespesa() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfigTipoObjetoCompra getConfigTipoObjetoCompra() {
        return configTipoObjetoCompra;
    }

    public void setConfigTipoObjetoCompra(ConfigTipoObjetoCompra configTipoObjetoCompra) {
        this.configTipoObjetoCompra = configTipoObjetoCompra;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }
}
