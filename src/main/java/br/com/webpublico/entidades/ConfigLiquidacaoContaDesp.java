package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 21/06/13
 * Time: 09:40
 * To change this template use File | Settings | File Templates.
 */
@Entity

@Audited
public class ConfigLiquidacaoContaDesp implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta(value = "Conta de Despesa")
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    private Conta contaDespesa;
    @ManyToOne
    private ConfigLiquidacao configLiquidacao;
    @Transient
    private Long criadoEm;

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

    public ConfigLiquidacao getConfigLiquidacao() {
        return configLiquidacao;
    }

    public void setConfigLiquidacao(ConfigLiquidacao configLiquidacao) {
        this.configLiquidacao = configLiquidacao;
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
