package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.webreportdto.dto.contabil.ConfiguracaoAnexosRREODTO;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Configuração de Totalizadores dos Anexos da RREO")
public class ConfiguracaoAnexosRREO extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Etiqueta("Número da linha DESPESAS (INTRA-ORÇAMENTÁRIAS) (II)")
    @Obrigatorio
    private Integer linhaTotalIntraOrcamentario;
    @Etiqueta("Número da linha TOTAL (III) = (I + II)")
    @Obrigatorio
    private Integer linhaTotalGeral;

    public ConfiguracaoAnexosRREO() {
        super();
    }

    public ConfiguracaoAnexosRREODTO toDto() {
        return new ConfiguracaoAnexosRREODTO(linhaTotalIntraOrcamentario, linhaTotalGeral);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLinhaTotalGeral() {
        return linhaTotalGeral;
    }

    public void setLinhaTotalGeral(Integer linhaTotalGeral) {
        this.linhaTotalGeral = linhaTotalGeral;
    }

    public Integer getLinhaTotalIntraOrcamentario() {
        return linhaTotalIntraOrcamentario;
    }

    public void setLinhaTotalIntraOrcamentario(Integer linhaTotalIntraOrcamentario) {
        this.linhaTotalIntraOrcamentario = linhaTotalIntraOrcamentario;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }
}
