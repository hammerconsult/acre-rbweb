package br.com.webpublico.entidades.rh.configuracao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Author peixe on 29/09/2015  11:25.
 */

@Entity
@Audited
@GrupoDiagrama(nome = "Configuração RH")
@Etiqueta("Configurações RH Holerite")
public class ConfiguracaoHolerite extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String numeroContrato;
    private String codigoProduto;
    private String codigoModalidade;

    public ConfiguracaoHolerite() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getCodigoModalidade() {
        return codigoModalidade;
    }

    public void setCodigoModalidade(String codigoModalidade) {
        this.codigoModalidade = codigoModalidade;
    }

    @Override
    public String toString() {
        return "Número do Contrato: " + numeroContrato + " Código do Produto: " + codigoProduto + " Código da Modalidade: " + codigoModalidade;
    }
}
