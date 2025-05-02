/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TiposCredito;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

/**
 * @author venon
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta(value = "Conta de Receita")

public class ContaReceita extends Conta {

    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Código Reduzido")
    private String codigoReduzido;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Descrição Reduzida")
    private String descricaoReduzida;
    private String migracao;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Conta Correspondente")
    private ContaReceita correspondente;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Tipos de Crédito")
    private TiposCredito tiposCredito;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Gera Crédito Arrecadação")
    private Boolean geraCreditoArrecadacao;

    public ContaReceita() {
        super();
        super.setdType("ContaReceita");
    }

    public String getCodigoReduzido() {
        return codigoReduzido;
    }

    public void setCodigoReduzido(String codigoReduzido) {
        this.codigoReduzido = codigoReduzido;
    }

    public String getMigracao() {
        return migracao;
    }

    public void setMigracao(String migracao) {
        this.migracao = migracao;
    }

    public ContaReceita getCorrespondente() {
        return correspondente;
    }

    public void setCorrespondente(ContaReceita correspondente) {
        this.correspondente = correspondente;
    }

    public TiposCredito getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TiposCredito tiposCredito) {
        this.tiposCredito = tiposCredito;
    }

    public String getDescricaoReduzida() {
        return descricaoReduzida;
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = descricaoReduzida;
    }

    public Boolean getGeraCreditoArrecadacao() {
        return geraCreditoArrecadacao;
    }

    public void setGeraCreditoArrecadacao(Boolean geraCreditoArrecadacao) {
        this.geraCreditoArrecadacao = geraCreditoArrecadacao;
    }

    public boolean retornaTipoContaDividaAtiva() {
        return TiposCredito.DIVIDA_ATIVA_NAO_TRIBUTARIA_CLIENTES.equals(this.tiposCredito)
            || TiposCredito.DIVIDA_ATIVA_NAO_TRIBUTARIA_DEMAIS.equals(this.tiposCredito)
            || TiposCredito.DIVIDA_ATIVA_TRIBUTARIA.equals(this.tiposCredito);
    }

    public boolean retornaTipoContaCreditoReceber() {
        return TiposCredito.CREDITOS_CLIENTES_A_RECEBER.equals(this.tiposCredito)
            || TiposCredito.CREDITOS_DIVERSOS_A_RECEBER.equals(this.tiposCredito)
            || TiposCredito.CREDITOS_NAO_TRIBUTARIOS_A_RECEBER.equals(this.tiposCredito)
            || TiposCredito.CREDITOS_TRANSFERENCIAS_A_RECEBER.equals(this.tiposCredito)
            || TiposCredito.CREDITOS_TRIBUTARIOS_A_RECEBER.equals(this.tiposCredito);
    }
}

