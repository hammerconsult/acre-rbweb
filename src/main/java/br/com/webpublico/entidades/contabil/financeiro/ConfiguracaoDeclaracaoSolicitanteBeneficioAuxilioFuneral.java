package br.com.webpublico.entidades.contabil.financeiro;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by carlos on 25/07/17.
 */
@Entity
@Audited
@Etiqueta("Configuração da Declaração do Solicitante do Benefício de Auxílio Funeral")
@Table(name = "DECSOLBENAUXFUNERAL")
public class ConfiguracaoDeclaracaoSolicitanteBeneficioAuxilioFuneral extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Texto Informativo na Declaração")
    private String textoDeclaracao;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Inicio da Vigência")
    private Date inicioVigencia;
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim da Vigência")
    private Date finalVigencia;

    public ConfiguracaoDeclaracaoSolicitanteBeneficioAuxilioFuneral() {
    }

    public ConfiguracaoDeclaracaoSolicitanteBeneficioAuxilioFuneral(String textoDeclaracao, Date inicioVigencia, Date finalVigencia) {
        this.textoDeclaracao = textoDeclaracao;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextoDeclaracao() {
        return textoDeclaracao;
    }

    public void setTextoDeclaracao(String textoDeclaracao) {
        this.textoDeclaracao = textoDeclaracao;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    @Override
    public String toString() {
        return getTextoDeclaracao();
    }
}
