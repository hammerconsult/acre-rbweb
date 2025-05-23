package br.com.webpublico.entidades.rh.configuracao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.RegraAposentadoria;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author peixe on 05/11/2015  17:41.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Configuração RH")
@Etiqueta("Configurações para Aposentadoria")
public class ConfiguracaoAposentadoria extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Regra de Aposentadoria")
    @Enumerated(value = EnumType.STRING)
    @Pesquisavel
    private RegraAposentadoria regraAposentadoria;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tempo Mínimo")
    @OneToMany(mappedBy = "configuracaoAposentadoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TempoMinimoAposentadoria> tempoMinimoAposentadorias;

    public ConfiguracaoAposentadoria() {
        tempoMinimoAposentadorias = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegraAposentadoria getRegraAposentadoria() {
        return regraAposentadoria;
    }

    public void setRegraAposentadoria(RegraAposentadoria regraAposentadoria) {
        this.regraAposentadoria = regraAposentadoria;
    }

    public List<TempoMinimoAposentadoria> getTempoMinimoAposentadorias() {
        return tempoMinimoAposentadorias;
    }

    public void setTempoMinimoAposentadorias(List<TempoMinimoAposentadoria> tempoMinimoAposentadorias) {
        this.tempoMinimoAposentadorias = tempoMinimoAposentadorias;
    }

    @Override
    public String toString() {
        return regraAposentadoria != null ? regraAposentadoria.getDescricao() : "";
    }
}
