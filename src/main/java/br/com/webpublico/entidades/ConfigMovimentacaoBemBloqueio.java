package br.com.webpublico.entidades;


import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "CONFIGMOVBEMBLOQUEIO")
@Etiqueta("Configuração de Movimentação de Bens Móveis - Bloqueio")
public class ConfigMovimentacaoBemBloqueio extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Boolean true, significa que o movimento está bloqueado
     */
    @Etiqueta("Movimento Tipo Um")
    private Boolean movimentoTipoUm;

    @Etiqueta("Movimento Tipo Dois")
    private Boolean movimentoTipoDois;

    @Etiqueta("Movimento Tipo Três")
    private Boolean movimentoTipoTres;

    public ConfigMovimentacaoBemBloqueio() {

    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getMovimentoTipoUm() {
        return movimentoTipoUm;
    }

    public void setMovimentoTipoUm(Boolean movimentoTipoUm) {
        this.movimentoTipoUm = movimentoTipoUm;
    }

    public Boolean getMovimentoTipoDois() {
        return movimentoTipoDois;
    }

    public void setMovimentoTipoDois(Boolean movimentoTipoDois) {
        this.movimentoTipoDois = movimentoTipoDois;
    }

    public Boolean getMovimentoTipoTres() {
        return movimentoTipoTres;
    }

    public void setMovimentoTipoTres(Boolean movimentoTipoTres) {
        this.movimentoTipoTres = movimentoTipoTres;
    }
}
