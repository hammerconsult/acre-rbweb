package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 16/05/14
 * Time: 15:23
 * To change this template use File | Settings | File Templates.
 */
@Etiqueta(value = "Configuração de Tipo de Conta de Despesa/Classe Credor")
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Table(name = "TIPOCONTACLASSECREDOR")
public class TipoContaDespesaClasseCredor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfiguracaoTipoContaDespesaClasseCredor configuracao;
    @ManyToOne
    private ClasseCredor classeCredor;
    @Transient
    private Long criadoEm;

    public TipoContaDespesaClasseCredor() {
        criadoEm = System.nanoTime();
    }

    public TipoContaDespesaClasseCredor(ConfiguracaoTipoContaDespesaClasseCredor configuracaoTipoContaDespesaClasseCredor, ClasseCredor classeCredor) {
        this.configuracao = configuracaoTipoContaDespesaClasseCredor;
        this.classeCredor = classeCredor;
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoTipoContaDespesaClasseCredor getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(ConfiguracaoTipoContaDespesaClasseCredor configuracao) {
        this.configuracao = configuracao;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return classeCredor.toString();
    }
}
