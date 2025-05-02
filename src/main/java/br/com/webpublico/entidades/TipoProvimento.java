/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Tipo de Provimento")
public class TipoProvimento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Integer codigo;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    public static final int NOMEACAO = 1;
    public static final int TRANSFERENCIA = 2;
    public static final int READAPTACAO = 3;
    public static final int REVERSAO = 4;
    public static final int REINTEGRACAO = 5;
    public static final int PROVIMENTO_PROGRESSAO = 8;
    public static final int PROMOCAO = 10;
    public static final int PROVIMENTO_ACESSO_CARGO_COMISSAO = 11;
    public static final int ALTERACAO_CARGO_COMISSAO = 15;
    public static final int ALTERACAO_ACESSO_SUBSIDIO = 50;
    public static final int EXONERACAORESCISAO_CARREIRA = 19;
    public static final int EXONERACAORESCISAO_COMISSAO = 20;
    public static final int NOMEACAO_FUNCAO_GRATIFICADA = 21;
    public static final int EXONERACAO_DE_FUNCAO_GRATIFICADA = 22;
    public static final int APOSENTADORIA = 23;
    public static final int PENSIONISTAS = 24;
    public static final int ENQUADRAMENTO = 25;
    public static final int NOMEACAO_CONTRATO_TEMPORARIO = 30;
    public static final int RETORNO_CARGO_CARREIRA = 27;
    public static final int PRORROGACAO_CONTRATO = 29;
    public static final int DISPONIBILIDADE = 71;
    public static final int ESTORNO_EXONERAÇÃO_CARREIRA = 69;
    public static final int ESTORNO_EXONERAÇÃO_COMISSÃO = 70;
    public static final int ENQUADRAMENTO_FUNCIONAL = 72;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoProvimento)) {
            return false;
        }
        TipoProvimento other = (TipoProvimento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
