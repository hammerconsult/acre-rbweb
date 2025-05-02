package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCalculoAlvara;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.CalculoAlvara;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Alvara")
@Audited
@Entity
@Etiqueta("Cálculo de Alvará de Funcionamento")
@Table(name = "CALCULOALVARAFUNC")
public class CalculoAlvaraFuncionamento extends Calculo implements Serializable, CalculoAlvara{

    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Transient
    private Long criadoEm;
    @ManyToOne(cascade = CascadeType.ALL)
    private ProcessoCalculoAlvaraFunc processoCalculo;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimento;
    private Boolean inicial;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "calculoAlvaraFunc")
    private List<ItemCalculoAlvaraFunc> itens;
    @ManyToOne
    private CNAE cnaePrimario;
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
    private String motivoDeGeracaoNaMigracao; //Usado apenas para os dados migrados
    @Transient
    private Boolean emitir;

    public CalculoAlvaraFuncionamento() {
        this.cadastroEconomico = new CadastroEconomico();
        this.itens = new ArrayList<ItemCalculoAlvaraFunc>();
        this.criadoEm = System.nanoTime();
        super.setTipoCalculo(TipoCalculo.ALVARA_FUNCIONAMENTO);
        super.setSubDivida(1L);
        historicosImpressao = Lists.newArrayList();
        this.setSituacaoCalculoAlvara(SituacaoCalculoAlvara.CALCULADO);
    }

    public List<ItemCalculoAlvaraFunc> getItens() {
        return itens;
    }

    public void setItens(List<ItemCalculoAlvaraFunc> itens) {
        this.itens = itens;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
        super.setCadastro(cadastroEconomico);
    }

    public ProcessoCalculoAlvaraFunc getProcessoCalculo() {
        return processoCalculo;
    }

    public void setProcessoCalculo(ProcessoCalculoAlvaraFunc processoCalculo) {
        super.setProcessoCalculo(processoCalculo);
        this.processoCalculo = processoCalculo;
    }

    public Boolean getEmitir() {
        return emitir;
    }

    public void setEmitir(Boolean emitir) {
        this.emitir = emitir;
    }

    public Boolean getInicial() {
        if (inicial == null) {
            return Boolean.FALSE;
        } else {
            return inicial;
        }
    }

    public void setInicial(Boolean inicial) {
        this.inicial = inicial;
    }

    public CNAE getCnaePrimario() {
        return cnaePrimario;
    }

    public void setCnaePrimario(CNAE cnaePrimario) {
        this.cnaePrimario = cnaePrimario;
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

    public String getMotivoDeGeracaoNaMigracao() {
        return motivoDeGeracaoNaMigracao;
    }

    public void setMotivoDeGeracaoNaMigracao(String motivoDeGeracaoNaMigracao) {
        this.motivoDeGeracaoNaMigracao = motivoDeGeracaoNaMigracao;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CalculoAlvaraFuncionamento)) {
            return false;
        }
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.CalculoAlvaraFuncionamento[ id=" + super.getId() + " ]";
    }


}
