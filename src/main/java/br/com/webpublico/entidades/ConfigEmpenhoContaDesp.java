package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 20/06/13
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */
@Entity

@Audited
public class ConfigEmpenhoContaDesp extends SuperEntidade {

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
    private ConfigEmpenho configEmpenho;

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

    public ConfigEmpenho getConfigEmpenho() {
        return configEmpenho;
    }

    public void setConfigEmpenho(ConfigEmpenho configEmpenho) {
        this.configEmpenho = configEmpenho;
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
            return contaDespesa.getCodigo()+" - "+contaDespesa.getDescricao();
        } else {
            return "";
        }
    }
}
