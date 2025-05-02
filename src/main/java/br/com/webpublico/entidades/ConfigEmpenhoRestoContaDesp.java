package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 26/09/13
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
@Table(name = "ConfigEmpRestoContaDesp")
public class ConfigEmpenhoRestoContaDesp implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta(value = "Conta de Despesa")
    @Tabelavel
    @Pesquisavel
    private Conta contaDespesa;
    @ManyToOne
    private ConfigEmpenhoRestoPagar configEmpenhoResto;
    @Transient
    private Long criadoEm;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public ConfigEmpenhoRestoPagar getConfigEmpenhoResto() {
        return configEmpenhoResto;
    }

    public void setConfigEmpenhoResto(ConfigEmpenhoRestoPagar configEmpenhoResto) {
        this.configEmpenhoResto = configEmpenhoResto;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public String toString() {
        if (contaDespesa != null) {
            return contaDespesa.toString();
        } else {
            return "";
        }
    }
}
