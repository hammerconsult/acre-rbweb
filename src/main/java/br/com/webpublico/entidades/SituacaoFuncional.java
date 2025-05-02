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
@Etiqueta("Situação Funcional")

public class SituacaoFuncional extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Código")
    @Tabelavel
    @Pesquisavel
    private Long codigo;
    @Obrigatorio
    @Etiqueta("Descrição")
    @Pesquisavel
    @Tabelavel
    private String descricao;
    @Obrigatorio
    @Etiqueta("Código Sig")
    @Tabelavel
    @Pesquisavel
    private Long codigoSig;

    public static final Long A_NOSSA_DISPOSICAO = 5L;
    public static final Long ATIVO_PARA_FOLHA = 1L;
    public static final Long EXONERADO_RESCISO = 6L;
    public static final Long APOSENTADO = 7L;
    public static final Long PENSIONISTA = 8L;
    public static final Long AFASTADO_LICENCIADO = 3L;
    public static final Long INATIVO_PARA_FOLHA = 11L;
    public static final Long A_DISPOSICAO = 4L;
    public static final Long EM_FERIAS = 2L;
    public static final Long INSTITUIDOR = 10L;
    public static final Long EM_DISPONIBILIDADE = 12L;

    public SituacaoFuncional() {
        super();
    }

    public SituacaoFuncional(Long codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getCodigoSig() {
        return codigoSig;
    }

    public void setCodigoSig(Long codigoSig) {
        this.codigoSig = codigoSig;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
