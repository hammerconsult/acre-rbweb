/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoTermoRBTrans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by AndreGustavo on 28/07/2014.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Parâmetros de Trânsito dos Termos")
public class ParametrosTransitoTermos extends SuperEntidade implements Serializable, Comparable<ParametrosTransitoTermos> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Tipo de Termo RBTrans")
    @Enumerated(EnumType.STRING)
    private TipoTermoRBTrans tipoTermoRBTrans;
    @OneToOne
    @Etiqueta("Tipo de Documento Oficial")
    private TipoDoctoOficial tipoDoctoOficial;
    @JoinColumn(name = "PARAMETROSTRANSITOCONFIG_ID")
    @ManyToOne
    private ParametrosTransitoConfiguracao parametrosTransitoConfiguracao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoTermoRBTrans getTipoTermoRBTrans() {
        return tipoTermoRBTrans;
    }

    public void setTipoTermoRBTrans(TipoTermoRBTrans tipoTermoRBTrans) {
        this.tipoTermoRBTrans = tipoTermoRBTrans;
    }

    public TipoDoctoOficial getTipoDoctoOficial() {
        return tipoDoctoOficial;
    }

    public void setTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.tipoDoctoOficial = tipoDoctoOficial;
    }

    public ParametrosTransitoConfiguracao getParametrosTransitoConfiguracao() {
        return parametrosTransitoConfiguracao;
    }

    public void setParametrosTransitoConfiguracao(ParametrosTransitoConfiguracao parametrosTransitoConfiguracao) {
        this.parametrosTransitoConfiguracao = parametrosTransitoConfiguracao;
    }

    @Override
    public int compareTo(ParametrosTransitoTermos o) {
        try {
            if (o.getTipoTermoRBTrans() != null && this.getTipoTermoRBTrans() != null) {
                return this.getTipoTermoRBTrans().getDescricao().compareTo(o.getTipoTermoRBTrans().getDescricao());
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
