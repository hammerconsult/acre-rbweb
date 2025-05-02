/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.envers.Audited;

/**
 *
 * @author Wellington
 */
@Entity
@Audited
public class MigracaoChaveParcela implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long ID;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    private String migracaoChave;
    private Tipo tipo;


    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }



    public enum Tipo {

        DAM("Dam"),
        PARCELAMENTOISS("Parcelamento ISS"),
        IPTU_DIVIDAATIVA("Chave IPTU Dívida Ativa"),
        ISS_DIVIDAATIVA("Chave ISS Dívida Ativa"),
        PARCELAMENTOISS_DIVIDAATIVA("Chave PARCELAMENTOISS Dívida Ativa"),
        ALVARA_DIVIDAATIVA("Chave ALVARA Dívida Ativa"),
        FISCALIZACAODIFERENCAISS_DIVIDAATIVA("Chave FISCALIZACAODIFERENCAISS Dívida Ativa"),
        FISCALIZACAOMULTAISS_DIVIDAATIVA("Chave FISCALIZACAOMULTA Dívida Ativa"),
        RENDASPATRIMONIAIS_DIVIDAATIVA("Chave RENDASPATRIMONIAIS Dívida Ativa"),
        PROCESSOFISCALIZACAO_DIVIDAATIVA("Chave PROCESSOFISCALIZACAO Dívida Ativa"),
        DIVIDASDIVERSAS_DIVIDAATIVA("Chave DIVIDASDIVERSAS Dívida Ativa"),
        PARCELAMENTO_DA_N("Parcelamento Divida Ativa Normal"),
        PARCELAMENTO_DA_1("Parcelamento Divida Ativa 1 º Reparcelamento"),
        PARCELAMENTO_DA_2("Parcelamento Divida Ativa 2 º Reparcelamento"),
        PARCELAMENTO_DA_3("Parcelamento Divida Ativa 3 º Reparcelamento"),
        PARCELAMENTO_DA_4("Parcelamento Divida Ativa 4 º Reparcelamento"),
        PARCELAMENTO_DA_5("Parcelamento Divida Ativa 5 º Reparcelamento"),
        PARCELAMENTO_DA_6("Parcelamento Divida Ativa 6 º Reparcelamento"),
        PARCELAMENTO_DA_7("Parcelamento Divida Ativa 7 º Reparcelamento"),
        PARCELAMENTO_DA_8("Parcelamento Divida Ativa 8 º Reparcelamento"),
        PARCELAMENTO_DA_9("Parcelamento Divida Ativa 9 º Reparcelamento"),
        IDENTIFICACAO("Identificação");
        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        private Tipo(String descricao) {
            this.descricao = descricao;
        }
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.ID);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MigracaoChaveParcela other = (MigracaoChaveParcela) obj;
        if (!Objects.equals(this.ID, other.ID)) {
            return false;
        }
        return true;
    }
}
