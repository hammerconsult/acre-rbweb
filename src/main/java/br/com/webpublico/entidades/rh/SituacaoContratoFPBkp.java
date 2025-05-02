/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades.rh;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.SituacaoFuncional;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Date;

@Audited
@Entity
@GrupoDiagrama(nome = "RecursosHumanos")
public class SituacaoContratoFPBkp extends SuperEntidade {

    private static final Logger logger = LoggerFactory.getLogger(SituacaoContratoFPBkp.class);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ContratoFP contratoFP;
    @ManyToOne
    private SituacaoFuncional situacaoFuncional;
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    private String rotinaResponsavelAlteracao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public SituacaoFuncional getSituacaoFuncional() {
        return situacaoFuncional;
    }

    public void setSituacaoFuncional(SituacaoFuncional situacaoFuncional) {
        this.situacaoFuncional = situacaoFuncional;
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

    public String getRotinaResponsavelAlteracao() {
        return rotinaResponsavelAlteracao;
    }

    public void setRotinaResponsavelAlteracao(String rotinaResponsavelAlteracao) {
        this.rotinaResponsavelAlteracao = rotinaResponsavelAlteracao;
    }
}
