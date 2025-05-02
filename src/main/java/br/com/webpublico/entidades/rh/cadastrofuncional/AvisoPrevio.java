package br.com.webpublico.entidades.rh.cadastrofuncional;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.rh.TipoAvisoPrevio;
import br.com.webpublico.enums.rh.TipoCumprimentoAvisoPrevio;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by William on 01/10/2018.
 */
@Audited
@Entity
@Etiqueta("Aviso Prévio")
public class AvisoPrevio extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Servidor")
    private ContratoFP contratoFP;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do Aviso Prévio")
    private Date dataAviso;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do Desligamento")
    private Date dataDesligamento;
    @Etiqueta("Tipo do Aviso Prévio")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoAvisoPrevio tipoAvisoPrevio;
    @Etiqueta("Tipo de Cumprimento de Aviso Prévio")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoCumprimentoAvisoPrevio tipoCumprimentoAvisoPrevio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataAviso() {
        return dataAviso;
    }

    public void setDataAviso(Date dataAviso) {
        this.dataAviso = dataAviso;
    }

    public Date getDataDesligamento() {
        return dataDesligamento;
    }

    public void setDataDesligamento(Date dataDesligamento) {
        this.dataDesligamento = dataDesligamento;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public TipoAvisoPrevio getTipoAvisoPrevio() {
        return tipoAvisoPrevio;
    }

    public void setTipoAvisoPrevio(TipoAvisoPrevio tipoAvisoPrevio) {
        this.tipoAvisoPrevio = tipoAvisoPrevio;
    }

    public TipoCumprimentoAvisoPrevio getTipoCumprimentoAvisoPrevio() {
        return tipoCumprimentoAvisoPrevio;
    }

    public void setTipoCumprimentoAvisoPrevio(TipoCumprimentoAvisoPrevio tipoCumprimentoAvisoPrevio) {
        this.tipoCumprimentoAvisoPrevio = tipoCumprimentoAvisoPrevio;
    }

    public String getDescricaoAvisoPrevio() {
        return "Data do Aviso Prévio: " + DataUtil.getDataFormatada(this.getDataAviso()) + "; Data do Desligamento: " +
            DataUtil.getDataFormatada(this.getDataDesligamento()) + " Servidor: " + this.getContratoFP();
    }

    @Override
    public String toString() {
        return getDescricaoAvisoPrevio();
    }
}
