package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Wellington
 */
@Entity
@GrupoDiagrama(nome = "CadastroEconomico")
@Audited
@Etiqueta("Natureza Jurídica - Isenção")
public class NaturezaJuridicaIsencao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfiguracaoAcrescimos configuracaoAcrescimos;
    @ManyToOne
    private NaturezaJuridica naturezaJuridica;
    @Enumerated(EnumType.STRING)
    private TipoMovimentoMensal tipoMovimentoMensal;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoAcrescimos getConfiguracaoAcrescimos() {
        return configuracaoAcrescimos;
    }

    public void setConfiguracaoAcrescimos(ConfiguracaoAcrescimos configuracaoAcrescimos) {
        this.configuracaoAcrescimos = configuracaoAcrescimos;
    }

    public NaturezaJuridica getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(NaturezaJuridica naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public TipoMovimentoMensal getTipoMovimentoMensal() {
        return tipoMovimentoMensal;
    }

    public void setTipoMovimentoMensal(TipoMovimentoMensal tipoMovimentoMensal) {
        this.tipoMovimentoMensal = tipoMovimentoMensal;
    }
}
