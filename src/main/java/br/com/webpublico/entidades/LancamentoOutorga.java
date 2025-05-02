/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.StatusLancamentoOutorga;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Lançamento de Outorga")
public class LancamentoOutorga implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel(ordemApresentacao = 1)
    @Etiqueta("Exercício")
    @ManyToOne
    @Pesquisavel
    private Exercicio exercicio;
    @Tabelavel(ordemApresentacao = 2)
    @Etiqueta("Mês")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private Mes mes;
    @Etiqueta("CMC")
    @ManyToOne
    private CadastroEconomico cmc;
    @Tabelavel(ordemApresentacao = 6, ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Passageiro Equi. Transportado")
    @Pesquisavel
    @Numerico
    private BigDecimal passageiroTranspEquiv;
    private String observacao;
    @Tabelavel(ordemApresentacao = 11)
    @Pesquisavel
    @Etiqueta("Data do Lançamento")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataLancamento;
    @Etiqueta("Usuário do Lançamento")
    @Tabelavel(ordemApresentacao = 12)
    @ManyToOne
    private UsuarioSistema usuarioLancamento;
    @Etiqueta("Data do Estorno")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEstorno;
    @Etiqueta("Usuário do Estorno")
    @ManyToOne
    private UsuarioSistema usuarioEstorno;
    @Invisivel
    @OneToMany(mappedBy = "lancamentoOutorga", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProcessoCalculoLancamentoOutorga> listaDeProcessoCalculoLancamentoOutorga;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Etiqueta("Valor da Tarifa (R$)")
    @Tabelavel(ordemApresentacao = 7, ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Monetario
    private BigDecimal valorDaTarifa;
    @Etiqueta("Percentual da Outorga (%)")
    @Tabelavel(ordemApresentacao = 8, ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Porcentagem
    private BigDecimal percentualDaOutorga;
    @Transient
    @Tabelavel(ordemApresentacao = 3)
    @Pesquisavel
    @Etiqueta("C.M.C.")
    private String inscricaoCadastral;
    @Transient
    @Tabelavel(ordemApresentacao = 4)
    @Pesquisavel
    @Etiqueta("Nome/Razão Social")
    private Pessoa pessoaParaLista;
    @Transient
    @Tabelavel(ordemApresentacao = 5)
    @Pesquisavel

    @Etiqueta("CNPJ")
    private String cnpj;
    @Tabelavel(ordemApresentacao = 9, ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor da Outorga (R$)")
    private BigDecimal valorOutorga;
    @Tabelavel(ordemApresentacao = 10, ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor do ISS Correspondente")
    private BigDecimal valorISSCorrespondente;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lancamentoOutorga")
    private List<ArquivoLancamentoOutorga> arquivos;
    @Enumerated(EnumType.STRING)
    @Tabelavel(ordemApresentacao = 13)
    @Etiqueta("Situação")
    private StatusLancamentoOutorga statusLancamentoOutorga;
    private String motivoEstorno;
    @Transient
    private Integer diaVencimentoParcelaSugerido;
    @Transient
    @Temporal(TemporalType.DATE)
    private Date diaVencimentoParaAparecerNoCalender;
    @Transient
    @Temporal(TemporalType.DATE)
    private Date dataVencimentoDam;
    @Transient
    private Mes mesVencimento;
    @Transient
    private Integer anoVencimento;

    /*
     * Os atributos usuarioQueLancou e usuarioQueEstornou devem ser preenchidos com o login do usuario em questão.
     * Os atributos usuarioLancamento e usuarioEstorno não serão atribuídos na migração, pois até o momento os usuários não foram migrados.
     * Quando os usuários forem migrados, os atributos usuarioQueLancou e usuarioQueEstornou poderão ser utilizados para recuperar
     * seus respectivos objetos de Usuario Sistema.
     */
    private String usuarioQueLancou;
    private String usuarioQueEstornou;
    private String migracaoChave;
    @ManyToOne
    @Etiqueta("Tarifa")
    @Obrigatorio
    public TarifaOutorga tarifaOutorga;


    public LancamentoOutorga(Long id, Exercicio exercicio, CadastroEconomico cmc, UsuarioSistema usuarioLancamento, Mes mes, BigDecimal passageiroTranspEquiv, BigDecimal valorDaTarifa, BigDecimal percentualDaOutorga, Date dataLancamento, BigDecimal valorOutorga, BigDecimal valorISSCorrespondente, StatusLancamentoOutorga statusLancamentoOutorga) {
        this.setCmc(cmc);
        this.id = id;
        this.inscricaoCadastral = cmc.getInscricaoCadastral();
        this.pessoaParaLista = cmc.getPessoa();
        this.cnpj = cmc.getPessoa().getCpf_Cnpj();
        this.mes = mes;
        this.passageiroTranspEquiv = passageiroTranspEquiv;
        this.valorDaTarifa = valorDaTarifa;
        this.percentualDaOutorga = percentualDaOutorga;
        this.dataLancamento = dataLancamento;
        this.valorOutorga = valorOutorga;
        this.valorISSCorrespondente = valorISSCorrespondente;
        this.statusLancamentoOutorga = statusLancamentoOutorga;
    }

    public LancamentoOutorga() {
        this.criadoEm = System.nanoTime();
        valorISSCorrespondente = BigDecimal.ZERO;
        this.arquivos = new ArrayList<>();
    }

    public Mes getMesVencimento() {
        return mesVencimento;
    }

    public void setMesVencimento(Mes mesVencimento) {
        this.mesVencimento = mesVencimento;
    }

    public Integer getAnoVencimento() {
        return anoVencimento;
    }

    public void setAnoVencimento(Integer anoVencimento) {
        this.anoVencimento = anoVencimento;
    }

    public Date getDataVencimentoDam() {
        return dataVencimentoDam;
    }

    public void setDataVencimentoDam(Date dataVencimentoDam) {
        this.dataVencimentoDam = dataVencimentoDam;
    }

    public Integer getDiaVencimentoParcelaSugerido() {
        return diaVencimentoParcelaSugerido;
    }

    public Date getDiaVencimentoParaAparecerNoCalender() {
        return diaVencimentoParaAparecerNoCalender;
    }

    public void setDiaVencimentoParaAparecerNoCalender(Date diaVencimentoParaAparecerNoCalender) {
        this.diaVencimentoParaAparecerNoCalender = diaVencimentoParaAparecerNoCalender;
    }

    public void setDiaVencimentoParcelaSugerido(Integer diaVencimentoParcelaSugerido) {
        this.diaVencimentoParcelaSugerido = diaVencimentoParcelaSugerido;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public StatusLancamentoOutorga getStatusLancamentoOutorga() {
        return statusLancamentoOutorga;
    }

    public void setStatusLancamentoOutorga(StatusLancamentoOutorga statusLancamentoOutorga) {
        this.statusLancamentoOutorga = statusLancamentoOutorga;
    }

    public List<ArquivoLancamentoOutorga> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<ArquivoLancamentoOutorga> arquivos) {
        this.arquivos = arquivos;
    }

    public BigDecimal getValorISSCorrespondente() {
        return valorISSCorrespondente;
    }

    public void setValorISSCorrespondente(BigDecimal valorISSCorrespondente) {
        this.valorISSCorrespondente = valorISSCorrespondente;
    }

    public BigDecimal getValorOutorga() {
        return valorOutorga;
    }

    public void setValorOutorga(BigDecimal valorOutorga) {
        this.valorOutorga = valorOutorga;
    }

    public BigDecimal getValorDaTarifa() {
        return valorDaTarifa;
    }

    public void setValorDaTarifa(BigDecimal valorDaTarifa) {
        this.valorDaTarifa = valorDaTarifa;
    }

    public BigDecimal getPercentualDaOutorga() {
        return percentualDaOutorga;
    }

    public void setPercentualDaOutorga(BigDecimal percentualDaOutorga) {
        this.percentualDaOutorga = percentualDaOutorga;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroEconomico getCmc() {
        return cmc;
    }

    public void setCmc(CadastroEconomico cmc) {
        this.cmc = cmc;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }


    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public UsuarioSistema getUsuarioEstorno() {
        return usuarioEstorno;
    }

    public void setUsuarioEstorno(UsuarioSistema usuarioEstorno) {
        this.usuarioEstorno = usuarioEstorno;
    }

    public UsuarioSistema getUsuarioLancamento() {
        return usuarioLancamento;
    }

    public void setUsuarioLancamento(UsuarioSistema usuarioLancamento) {
        this.usuarioLancamento = usuarioLancamento;
    }

    public String getUsuarioQueEstornou() {
        return usuarioQueEstornou;
    }

    public void setUsuarioQueEstornou(String usuarioQueEstornou) {
        this.usuarioQueEstornou = usuarioQueEstornou;
    }

    public String getUsuarioQueLancou() {
        return usuarioQueLancou;
    }

    public void setUsuarioQueLancou(String usuarioQueLancou) {
        this.usuarioQueLancou = usuarioQueLancou;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public BigDecimal getPassageiroTranspEquiv() {
        return passageiroTranspEquiv;
    }

    public void setPassageiroTranspEquiv(BigDecimal passageiroTranspEquiv) {
        this.passageiroTranspEquiv = passageiroTranspEquiv;
    }

    public List<ProcessoCalculoLancamentoOutorga> getListaDeProcessoCalculoLancamentoOutorga() {
        return listaDeProcessoCalculoLancamentoOutorga;
    }

    public void setListaDeProcessoCalculoLancamentoOutorga(List<ProcessoCalculoLancamentoOutorga> listaDeProcessoCalculoLancamentoOutorga) {
        this.listaDeProcessoCalculoLancamentoOutorga = listaDeProcessoCalculoLancamentoOutorga;
    }

    public TarifaOutorga getTarifaOutorga() {
        return tarifaOutorga;
    }

    public void setTarifaOutorga(TarifaOutorga tarifaOutorga) {
        this.tarifaOutorga = tarifaOutorga;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "Lançamento de Outorga " + mes + "/" + exercicio.getAno();
    }


    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public ProcessoCalculoLancamentoOutorga obterUnicoProcessoCalculoDaLista() {
        if (getListaDeProcessoCalculoLancamentoOutorga() != null && !getListaDeProcessoCalculoLancamentoOutorga().isEmpty()) {
            return getListaDeProcessoCalculoLancamentoOutorga().get(0);
        }

        return null;
    }

    public CalculoLancamentoOutorga obterUnicoCalculoDaLista() {
        ProcessoCalculoLancamentoOutorga processo = obterUnicoProcessoCalculoDaLista();

        if (processo != null) {
            if (processo.getListaDeCalculoLancamentoOutorga() != null && !processo.getListaDeCalculoLancamentoOutorga().isEmpty()) {
                return processo.getListaDeCalculoLancamentoOutorga().get(0);
            }
        }

        return null;
    }
}
