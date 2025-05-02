package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity

@Audited
@Etiqueta("Revisão de Cálculo de IPTU")
public class RevisaoCalculoIPTU {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código da Revisão")
    private Long codigo;
    @ManyToOne
    @Etiqueta("Processo de Cálculo")
    private ProcessoCalculoIPTU processoCalculo;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Cadastro Imobiliário")
    private CadastroImobiliario cadastro;
    @ManyToOne
    private ValorDivida valorDivida;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Transient
    private Long criadoEm;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Usuário Responsável")
    private UsuarioSistema usuario;
    @Temporal(TemporalType.TIMESTAMP)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data")
    private Date dataCriacao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Protocolo/Exercício")
    private String processo;
    @Invisivel
    @Transient
    private String anoProtocolo;
    @Invisivel
    @Transient
    private String numeroProtocolo;
    private String observacao;
    @OneToMany(mappedBy = "revisaoCalculoIPTU")
    private List<ProcessoRevisao> processoRevisao;


    public RevisaoCalculoIPTU() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public ProcessoCalculoIPTU getProcessoCalculo() {
        return processoCalculo;
    }

    public void setProcessoCalculo(ProcessoCalculoIPTU processoCalculo) {
        this.processoCalculo = processoCalculo;
    }

    public CadastroImobiliario getCadastro() {
        return cadastro;
    }

    public void setCadastro(CadastroImobiliario cadastro) {
        this.cadastro = cadastro;
    }

    public ValorDivida getValorDivida() {
        return valorDivida;
    }

    public void setValorDivida(ValorDivida valorDivida) {
        this.valorDivida = valorDivida;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuarioSistema) {
        this.usuario = usuarioSistema;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public List<ProcessoRevisao> getProcessoRevisao() {
        return processoRevisao;
    }

    public void setProcessoRevisao(List<ProcessoRevisao> processoRevisao) {
        this.processoRevisao = processoRevisao;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(o, this);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);

    }
}
