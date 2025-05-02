/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoPromocao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorHistorico;
import br.com.webpublico.interfaces.ValidadorVigenciaFolha;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Enquadramento Funcional")
public class EnquadramentoFuncional extends SuperEntidade implements Serializable, ValidadorVigenciaFolha, Comparable, PossuidorHistorico {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Início da Vigência")
    @ColunaAuditoriaRH(posicao = 2)
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Etiqueta("Final da Vigência")
    @Temporal(TemporalType.DATE)
    @ColunaAuditoriaRH(posicao = 3)
    private Date finalVigencia;
    @Obrigatorio
    @Etiqueta("Progressão de Planos de Cargos e Salários")
    @ColunaAuditoriaRH(posicao = 7)
    @ManyToOne
    private ProgressaoPCS progressaoPCS;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Categoria de Planos de Cargos e Salários")
    @ManyToOne
    private CategoriaPCS categoriaPCS;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Contrato do Servidor")
    @ColunaAuditoriaRH()
    @ManyToOne
    private ContratoFP contratoServidor;
    @Tabelavel
    @Monetario
    @Etiqueta("Enquadramento de Plano de Cargos e Salários")
    @Transient
    private BigDecimal vencimentoBase;
    @Etiqueta("Data de Cadastro")
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    private Date dataCadastro;
    @Pesquisavel
    @Etiqueta("Data Final de Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCadastroFinalVigencia;
    @Etiqueta("Provimento FP")
    @ManyToOne
    private ProvimentoFP provimentoFP;
    @Pesquisavel
    @Etiqueta("Tipo de promoção")
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoPromocao tipoPromocao;
    @Pesquisavel
    @Etiqueta("Data válida para promoção")
    @Temporal(TemporalType.DATE)
    private Date dataValidaPromocao;
    @Transient
    private List<EnquadramentoPCS> enquadramentoPCSList;
    //sempre seta o penultimo resultado do enquadramento funcional
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private EnquadramentoFuncionalHist enquadramentoFuncionalHist;
    @Transient
    private Operacoes operacao;
    @Etiqueta("Observação")
    private String observacao;
    @Etiqueta("Ato Legal")
    @ManyToOne
    private AtoLegal atoLegal;
    @Etiqueta("Considera para Progressão Automática")
    private Boolean consideraParaProgAutomat;

    public EnquadramentoFuncional() {
        super();
        enquadramentoPCSList = new LinkedList<>();
    }


    public EnquadramentoFuncional(Long id, Date inicioVigencia, Date finalVigencia, ProgressaoPCS progressaoPCS, CategoriaPCS categoriaPCS, ContratoFP contratoServidor,
                                  BigDecimal vencimentoBase, Date dataCadastro, Date dataCadastroFinalVigencia) {
        this.id = id;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.progressaoPCS = progressaoPCS;
        this.categoriaPCS = categoriaPCS;
        this.contratoServidor = contratoServidor;
        this.vencimentoBase = vencimentoBase;
        this.dataCadastro = dataCadastro;
        this.dataCadastroFinalVigencia = dataCadastroFinalVigencia;

    }

    public EnquadramentoFuncional(Long id, Date inicioVigencia, Date finalVigencia, ProgressaoPCS progressaoPCS, CategoriaPCS categoriaPCS, ContratoFP contratoServidor,
                                  BigDecimal vencimentoBase, Date dataCadastro, Date dataCadastroFinalVigencia, EnquadramentoFuncionalHist hist) {
        this.id = id;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.progressaoPCS = progressaoPCS;
        this.categoriaPCS = categoriaPCS;
        this.contratoServidor = contratoServidor;
        this.vencimentoBase = vencimentoBase;
        this.dataCadastro = dataCadastro;
        this.dataCadastroFinalVigencia = dataCadastroFinalVigencia;
        this.enquadramentoFuncionalHist = hist;
    }

    public EnquadramentoFuncional(Date inicioVigencia, Date finalVigencia, ProgressaoPCS progressaoPCS, CategoriaPCS categoriaPCS, ContratoFP contratoServidor) {
        super();
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.progressaoPCS = progressaoPCS;
        this.categoriaPCS = categoriaPCS;
        this.contratoServidor = contratoServidor;
    }

    public EnquadramentoFuncional(Date inicioVigencia, Date finalVigencia, BigDecimal valor) {
        super();
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.vencimentoBase = valor;
    }

    public static void copiaPropriedadesEAssociaHistorico(EnquadramentoFuncional original, EnquadramentoFuncionalHist historico) {
        historico.setInicioVigencia(original.getInicioVigencia());
        historico.setFinalVigencia(original.getFinalVigencia());
        historico.setCategoriaPCS(original.getCategoriaPCS());
        historico.setProgressaoPCS(original.getProgressaoPCS());
        historico.setDataCadatro(new Date());
        original.setEnquadramentoFuncionalHist(historico);
    }

    public static void copiaPropriedadesDoHistoricoParaOrginial(EnquadramentoFuncional original, EnquadramentoFuncionalHist historico) {
        original.setInicioVigencia(historico.getInicioVigencia());
        original.setFinalVigencia(historico.getFinalVigencia());
        original.setCategoriaPCS(historico.getCategoriaPCS());
        original.setProgressaoPCS(historico.getProgressaoPCS());
    }


    public List<EnquadramentoPCS> getEnquadramentoPCSList() {
        return enquadramentoPCSList;
    }

    public void setEnquadramentoPCSList(List<EnquadramentoPCS> enquadramentoPCSList) {
        this.enquadramentoPCSList = enquadramentoPCSList;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnquadramentoFuncionalHist getEnquadramentoFuncionalHist() {
        return enquadramentoFuncionalHist;
    }

    public void setEnquadramentoFuncionalHist(EnquadramentoFuncionalHist enquadramentoFuncionalHist) {
        this.enquadramentoFuncionalHist = enquadramentoFuncionalHist;
    }

    public Date getDataCadastroFinalVigencia() {
        return dataCadastroFinalVigencia;
    }

    public void setDataCadastroFinalVigencia(Date dataCadastroFinalVigencia) {
        this.dataCadastroFinalVigencia = dataCadastroFinalVigencia;
    }

    public CategoriaPCS getCategoriaPCS() {
        return categoriaPCS;
    }

    public void setCategoriaPCS(CategoriaPCS categoriaPCS) {
        this.categoriaPCS = categoriaPCS;
    }

    public ContratoFP getContratoServidor() {
        return contratoServidor;
    }

    public void setContratoServidor(ContratoFP contratoServidor) {
        this.contratoServidor = contratoServidor;
    }

    @Override
    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    @Override
    public void criarOrAtualizarAndAssociarHistorico(PossuidorHistorico original) {
        EnquadramentoFuncionalHist historico = temHistorico() ? this.enquadramentoFuncionalHist : new EnquadramentoFuncionalHist();
        historico.setDataCadatro(new Date());
        historico.setInicioVigencia(original.getInicioVigencia());
        if (atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(original, historico)
            || atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(original)) {
            historico.setFinalVigencia(original.getFinalVigencia());
        }
        this.enquadramentoFuncionalHist = historico;
    }

    @Override
    public void voltarHistorico() {
        if (temHistorico()) {
            setFinalVigencia(enquadramentoFuncionalHist.getFinalVigencia());
        } else {
            setFinalVigencia(null);
        }
    }

    public boolean atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(PossuidorHistorico original) {
        return !this.temFinalVigencia() && original.temFinalVigencia();
    }

    public boolean atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(PossuidorHistorico original, EnquadramentoFuncionalHist historico) {
        return this.temFinalVigencia() && finalVigenciaAtualDiferenteFinalVigenciaOriginal(original) || !historico.temId();
    }

    public boolean finalVigenciaAtualDiferenteFinalVigenciaOriginal(PossuidorHistorico original) {
        return !this.getFinalVigencia().equals(original.getFinalVigencia());
    }

    @Override
    public boolean temFinalVigencia() {
        return finalVigencia != null;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return this.finalVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.finalVigencia = data;
    }

    public ProgressaoPCS getProgressaoPCS() {
        return progressaoPCS;
    }

    public void setProgressaoPCS(ProgressaoPCS progressaoPCS) {
        this.progressaoPCS = progressaoPCS;
    }

    public BigDecimal getVencimentoBase() {
        return vencimentoBase;
    }

    public void setVencimentoBase(BigDecimal vencimentoBase) {
        this.vencimentoBase = vencimentoBase;
    }

    @Override
    public BigDecimal getValor() {
        return vencimentoBase;
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public Date getDataValidaPromocao() {

        return dataValidaPromocao == null ? finalVigencia : dataValidaPromocao;
    }

    public void setDataValidaPromocao(Date dataValidaPromocao) {
        this.dataValidaPromocao = dataValidaPromocao;
    }

    public TipoPromocao getTipoPromocao() {
        return tipoPromocao;
    }

    public void setTipoPromocao(TipoPromocao tipoPromocao) {
        this.tipoPromocao = tipoPromocao;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    @Override
    public String toString() {
        return categoriaPCS.getPlanoCargosSalarios() + " - " + categoriaPCS + " - " + progressaoPCS + " - " + DataUtil.getDataFormatada(inicioVigencia) + " - " + DataUtil.getDataFormatada(finalVigencia);
    }

    public String getDescricaoCompleta() {
        return categoriaPCS.getPlanoCargosSalarios() + " - " + categoriaPCS + " - " + progressaoPCS + " - " + DataUtil.getDataFormatada(inicioVigencia) + " - " + DataUtil.getDataFormatada(finalVigencia);
    }

    public String getDescricaoCompletaValor() {
        String servidor = (contratoServidor != null ? contratoServidor + "" : "");
        String planoCargos = (categoriaPCS != null ? categoriaPCS.getPlanoCargosSalarios() + "" : "");

        return planoCargos + " - " + categoriaPCS + " - " + progressaoPCS + " - " + DataUtil.getDataFormatada(inicioVigencia) + " - " + DataUtil.getDataFormatada(finalVigencia) + " - R$ " + vencimentoBase;
    }

    public String getDescricaoCompletaComServidorEValor() {
        String servidor = (contratoServidor != null ? contratoServidor + "" : "");
        String planoCargos = (categoriaPCS != null ? categoriaPCS.getPlanoCargosSalarios() + "" : "");

        return servidor + " - " + planoCargos + " - " + categoriaPCS + " - " + progressaoPCS + " - " + DataUtil.getDataFormatada(inicioVigencia) + " - " + DataUtil.getDataFormatada(finalVigencia) + (vencimentoBase == null ? "" : " - R$ " +vencimentoBase);
    }

    @Override
    public int compareTo(Object o) {
        return this.getInicioVigencia().compareTo(((EnquadramentoFuncional) o).getInicioVigencia());
    }

    public boolean temInicioDeVigencia() {
        return this.getInicioVigencia() != null;
    }

    public boolean temFinalDeVigencia() {
        return this.getFinalVigencia() != null;
    }

    public boolean temDataCadastroFinalDeVigencia() {
        return this.getDataCadastroFinalVigencia() != null;
    }

    public boolean temProgressaoPCS() {
        return this.getProgressaoPCS() != null;
    }

    public boolean isOriginadoDaAlteracaoCargo(AlteracaoCargo alteracaoCargo) {
        try {
            if (this.equals(alteracaoCargo.getEnquadramentoFuncional())) {
                return true;
            }
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean estaEditando() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public boolean isVigente() {
        return getFimVigencia() == null || getFimVigencia().after(new Date());
    }

    public boolean temCategoriaPCS() {
        return categoriaPCS != null;
    }

    public Boolean getConsideraParaProgAutomat() {
        return consideraParaProgAutomat == null ? false : consideraParaProgAutomat;
    }

    public void setConsideraParaProgAutomat(Boolean consideraParaProgAutomat) {
        this.consideraParaProgAutomat = consideraParaProgAutomat;
    }

    @Override
    public boolean temHistorico() {
        return enquadramentoFuncionalHist != null;
    }
}

