package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 16/05/14
 * Time: 11:34
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Etiqueta("Finalidade do Pagamento")
public class FinalidadePagamento implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código")
    @Tabelavel(TIPOCAMPO = Tabelavel.TIPOCAMPO.NUMERO_ORDENAVEL)
    @Pesquisavel
    private String codigo;
    @Etiqueta("Código da OB")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private String codigoOB;
    @Etiqueta("Descrição")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private String descricao;
    @Etiqueta("Situação")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private SituacaoCadastralContabil situacao;
    @Transient
    private Long criadoEm;

    public FinalidadePagamento() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoOB() {
        return codigoOB;
    }

    public void setCodigoOB(String codigoOB) {
        this.codigoOB = codigoOB;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public SituacaoCadastralContabil getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoCadastralContabil situacao) {
        this.situacao = situacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    public String toString() {
        try {
            String retorno = codigo + " - " + descricao;
            if(codigoOB == null){
                return retorno;
            }
            return retorno + "(O.B. " + codigoOB + ")";
        } catch (Exception e) {
            return codigo + " - " + descricao;
        }
    }
}
