package br.com.webpublico.entidades.rh.creditodesalario.caixa;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.CodigoOcorrenciaRetornoArquivoCaixa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Alex
 * @since 10/06/2016 15:36
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RH")
@Etiqueta("Ocorrências de Erros do Arquivo de Retorno de Crédito de Salario")
public class RetornoCaixaOcorrencias extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private ItemRetornoCreditoSalario itemRetornoCreditoSalario;

    @Enumerated(EnumType.STRING)
    private CodigoOcorrenciaRetornoArquivoCaixa ocorrencia;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemRetornoCreditoSalario getItemRetornoCreditoSalario() {
        return itemRetornoCreditoSalario;
    }

    public void setItemRetornoCreditoSalario(ItemRetornoCreditoSalario itemRetornoCreditoSalario) {
        this.itemRetornoCreditoSalario = itemRetornoCreditoSalario;
    }

    public CodigoOcorrenciaRetornoArquivoCaixa getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(CodigoOcorrenciaRetornoArquivoCaixa ocorrencia) {
        this.ocorrencia = ocorrencia;
    }
}
