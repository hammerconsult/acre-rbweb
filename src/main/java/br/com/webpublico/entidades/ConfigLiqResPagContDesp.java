/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity

@Audited
public class ConfigLiqResPagContDesp implements Serializable {
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
    private ConfigLiquidacaoResPagar configLiquidacaoResPagar;
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

    public ConfigLiquidacaoResPagar getConfigLiquidacaoResPagar() {
        return configLiquidacaoResPagar;
    }

    public void setConfigLiquidacaoResPagar(ConfigLiquidacaoResPagar configLiquidacaoResPagar) {
        this.configLiquidacaoResPagar = configLiquidacaoResPagar;
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
