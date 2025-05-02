/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCalculoAlvara;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.CalculoAlvara;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Alvara")
@Etiqueta("Cálculo de Alvará de Vigilância Sanitária")
@Table(name = "CALCULOALVARASANITARIO")
public class CalculoAlvaraSanitario extends Calculo implements Serializable, CalculoAlvara {
    private static final long serialVersionUID = 1L;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataVencimento;
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne(cascade = CascadeType.ALL)
    private ProcessoCalculoAlvaraSan processoCalculoAlvaraSan;
    @ManyToOne
    private Alvara alvara;
    @Transient
    private Boolean provisorio;
    @OneToMany(mappedBy = "calculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoImpressaoAlvara> historicosImpressao;
    @Enumerated(EnumType.STRING)
    private SituacaoCalculoAlvara situacaoCalculoAlvara;
    @ManyToOne
    private UsuarioSistema usuarioEstorno;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEstorno;
    private String observacaoEstorno;
    @Transient
    private Boolean emitir;

    public CalculoAlvaraSanitario() {
        cadastroEconomico = new CadastroEconomico();
        super.setTipoCalculo(TipoCalculo.ALVARA_SANITARIO);
        super.setSubDivida(1L);
        historicosImpressao = Lists.newArrayList();
        this.setSituacaoCalculoAlvara(SituacaoCalculoAlvara.CALCULADO);
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
        super.setCadastro(cadastroEconomico);
    }

    public ProcessoCalculoAlvaraSan getProcessoCalculoAlvaraSan() {
        return processoCalculoAlvaraSan;
    }

    public void setProcessoCalculoAlvaraSan(ProcessoCalculoAlvaraSan processoCalculoAlvaraSan) {
        super.setProcessoCalculo(processoCalculoAlvaraSan);
        this.processoCalculoAlvaraSan = processoCalculoAlvaraSan;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Boolean getProvisorio() {
        return provisorio != null ? provisorio : false;
    }

    public void setProvisorio(Boolean provisorio) {
        this.provisorio = provisorio;
    }

    public Alvara getAlvara() {
        return alvara;
    }

    public void setAlvara(Alvara alvara) {
        this.alvara = alvara;
    }

    public Boolean getEmitir() {
        return emitir;
    }

    public void setEmitir(Boolean emitir) {
        this.emitir = emitir;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculoAlvaraSan;
    }

    public List<HistoricoImpressaoAlvara> getHistoricosImpressao() {
        return historicosImpressao;
    }

    public void setHistoricosImpressao(List<HistoricoImpressaoAlvara> historicosImpressao) {
        this.historicosImpressao = historicosImpressao;
    }

    public SituacaoCalculoAlvara getSituacaoCalculoAlvara() {
        return situacaoCalculoAlvara;
    }

    public void setSituacaoCalculoAlvara(SituacaoCalculoAlvara situacaoCalculoAlvara) {
        this.situacaoCalculoAlvara = situacaoCalculoAlvara;
    }

    public UsuarioSistema getUsuarioEstorno() {
        return usuarioEstorno;
    }

    public void setUsuarioEstorno(UsuarioSistema usuarioEstorno) {
        this.usuarioEstorno = usuarioEstorno;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public String getObservacaoEstorno() {
        return observacaoEstorno;
    }

    public void setObservacaoEstorno(String observacaoEstorno) {
        this.observacaoEstorno = observacaoEstorno;
    }

}
