/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPenalidadeContrato;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "Contratos")
@Entity
@Audited
@Table(name = "SUSPIDONEIDADEPENALCONT")
public class SuspensaoIdoneidadePenalidadeContrato extends PenalidadeContrato{


    @Obrigatorio
    @Etiqueta("Veículo de Públicação")
    @ManyToOne
    private VeiculoDePublicacao veiculoDePublicacao;
    @Etiqueta("Data de Públicação")
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date dataDePublicacao;
    @Etiqueta("Número da Públicação")
    private Integer numeroPublicacao;
    @Etiqueta("Inicia Em")
    @Temporal(TemporalType.DATE)
    private Date iniciaEm;
    @Etiqueta("Finaliza Em")
    @Temporal(TemporalType.DATE)
    private Date finalizaEm;


    public VeiculoDePublicacao getVeiculoDePublicacao() {
        return veiculoDePublicacao;
    }

    public void setVeiculoDePublicacao(VeiculoDePublicacao veiculoDePublicacao) {
        this.veiculoDePublicacao = veiculoDePublicacao;
    }

    public Date getDataDePublicacao() {
        return dataDePublicacao;
    }

    public void setDataDePublicacao(Date dataDePublicacao) {
        this.dataDePublicacao = dataDePublicacao;
    }

    public Integer getNumeroPublicacao() {
        return numeroPublicacao;
    }

    public void setNumeroPublicacao(Integer numeroPublicacao) {
        this.numeroPublicacao = numeroPublicacao;
    }

    public Date getIniciaEm() {
        return iniciaEm;
    }

    public void setIniciaEm(Date iniciaEm) {
        this.iniciaEm = iniciaEm;
    }

    public Date getFinalizaEm() {
        return finalizaEm;
    }

    public void setFinalizaEm(Date finalizaEm) {
        this.finalizaEm = finalizaEm;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (getTipo().equals(TipoPenalidadeContrato.SUSPENSAO_TEMPORARIA)) {
            if (getNumeroPublicacao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo 'Número da Públicação' deve ser informado!");
            }
            if (getIniciaEm() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo 'Inicia Em' deve ser informado!");
            }
            if (getFinalizaEm() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo 'Finaliza Em' deve ser informado!");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }
}
