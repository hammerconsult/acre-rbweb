/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Regra Dedução Dias Direito Férias")
public class RegraDeducaoDDF implements Serializable, ValidadorEntidade, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Início da Vigência")
    @Tabelavel
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Final da Vigência")
    @Tabelavel
    private Date finalVigencia;
    @ManyToOne
    private ModalidadeContratoFP modalidadeContratoFP;
    @Etiqueta("Tipo do Afastamento")
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private TipoAfastamento tipoAfastamento;
    @OneToMany(mappedBy = "regraDeducaoDDF", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemRegraDeducaoDDF> itensRegraDeducaoDDF;
    @Transient
    private Date dataRegistro;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Invisivel
    @Transient
    private boolean isEditando;

    public RegraDeducaoDDF() {
        criadoEm = System.nanoTime();
        isEditando = false;
        dataRegistro = new Date();
        itensRegraDeducaoDDF = new ArrayList<ItemRegraDeducaoDDF>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return getFinalVigencia();
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.setFinalVigencia(data);
    }

    public ModalidadeContratoFP getModalidadeContratoFP() {
        return modalidadeContratoFP;
    }

    public void setModalidadeContratoFP(ModalidadeContratoFP modalidadeContratoFP) {
        this.modalidadeContratoFP = modalidadeContratoFP;
    }

    public TipoAfastamento getTipoAfastamento() {
        return tipoAfastamento;
    }

    public void setTipoAfastamento(TipoAfastamento tipoAfastamento) {
        this.tipoAfastamento = tipoAfastamento;
    }

    public List<ItemRegraDeducaoDDF> getItensRegraDeducaoDDF() {
        return itensRegraDeducaoDDF;
    }

    public void setItensRegraDeducaoDDF(List<ItemRegraDeducaoDDF> itensRegraDeducaoDDF) {
        this.itensRegraDeducaoDDF = itensRegraDeducaoDDF;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public boolean isEditando() {
        return isEditando;
    }

    public void setEditando(boolean editando) {
        isEditando = editando;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Início: " + DataUtil.getDataFormatada(inicioVigencia));
        if (temDataFinalVigenciaInformada()) {
            sb.append(" Final: ").append(DataUtil.getDataFormatada(finalVigencia));
        }
        if (temTipoAfastamentoInformado()) {
            sb.append(" - Tipo: ").append(tipoAfastamento.getDescricao());
        }
        return sb.toString();
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    private boolean temTipoAfastamentoInformado() {
        return tipoAfastamento != null;
    }

    public boolean temDataFinalVigenciaInformada() {
        return finalVigencia != null;
    }
}
