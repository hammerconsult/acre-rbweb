package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Mateus
 * @since 19/02/2016 15:12
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Table(name = "ITEMGRUPOEXPORTACAOCONT")
public class ItemGrupoExportacaoContabil extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código de Receita Dirf PF")
    @Obrigatorio
    private String codigo;
    @Etiqueta("Código de Receita Dirf PJ")
    @Obrigatorio
    private String codigoPj;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Classe de Pessoa")
    @ManyToOne
    private ClasseCredor classeCredor;
    @Tabelavel
    @Etiqueta("Conta Extraorçamentária")
    @ManyToOne
    private ContaExtraorcamentaria contaExtraorcamentaria;
    @Obrigatorio
    private BigDecimal percentual;
    @Tabelavel
    @Etiqueta("Grupo de Exportação")
    @ManyToOne
    private GrupoExportacao grupoExportacao;

    public ItemGrupoExportacaoContabil() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public ContaExtraorcamentaria getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(ContaExtraorcamentaria contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public GrupoExportacao getGrupoExportacao() {
        return grupoExportacao;
    }

    public void setGrupoExportacao(GrupoExportacao grupoExportacao) {
        this.grupoExportacao = grupoExportacao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoPj() {
        return codigoPj;
    }

    public void setCodigoPj(String codigoPj) {
        this.codigoPj = codigoPj;
    }

    @Override
    public String toString() {
        String retorno = "PF " + codigo + ", PJ" + codigoPj;
        if (classeCredor != null) {
            retorno += " - " + classeCredor.toString();
        }
        if (contaExtraorcamentaria != null) {
            retorno += " - " + contaExtraorcamentaria.toString();
        }
        return retorno;
    }
}
