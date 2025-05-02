/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import java.io.Serializable;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

@Entity

@Audited
public class ConfigPagamentoContaDesp implements Serializable {
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
    private ConfigPagamento configPagamento;
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

    public ConfigPagamento getConfigPagamento() {
        return configPagamento;
    }

    public void setConfigPagamento(ConfigPagamento configPagamento) {
        this.configPagamento = configPagamento;
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
