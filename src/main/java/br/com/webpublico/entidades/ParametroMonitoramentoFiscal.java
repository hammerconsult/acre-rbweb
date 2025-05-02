package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Table(name = "PARAMETROMONITORAFISCAL")
@Entity
@GrupoDiagrama(nome = "Tributário")
@Audited
@Etiqueta("Parâmetros do monitoramento fiscal")
public class ParametroMonitoramentoFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercicio")
    @Obrigatorio
    private Exercicio exercicio;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Secretaria")
    @Obrigatorio
    private UnidadeOrganizacional secretaria;
    @ManyToOne
    @Etiqueta("Tipo documento para ordem geral")
    @Obrigatorio
    private TipoDoctoOficial tipoDoctoOrdemGeral;
    @ManyToOne
    @Etiqueta("Tipo documento para  relatório final")
    @Obrigatorio
    private TipoDoctoOficial tipoDoctoRelatorioFinal;
    @Pesquisavel
    @Etiqueta("Prazo processo de ordem geral")
    @Obrigatorio
    private Integer prazoProcessoOrdem;
    @Pesquisavel
    @Etiqueta("Prazo para resposta comunicado")
    @Obrigatorio
    private Integer prazoRespostaComunicado;
    @Pesquisavel
    @Etiqueta("Prazo para regularizar pendências")
    @Obrigatorio
    private Integer prazoRegularizaPendencia;
    @Pesquisavel
    @Etiqueta("Diretor do Departamento")
    @Obrigatorio
    @ManyToOne
    private PessoaFisica diretorDepartamento;
    @Pesquisavel
    @Etiqueta("Secretário")
    @Obrigatorio
    @ManyToOne
    private PessoaFisica secretario;
    private Boolean nfse;
    private Boolean simplesNacional;
    private Boolean outro;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Parâmetro da Mala Direta")
    private ParametroMalaDireta parametroMalaDireta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public UnidadeOrganizacional getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(UnidadeOrganizacional secretaria) {
        this.secretaria = secretaria;
    }

    public TipoDoctoOficial getTipoDoctoOrdemGeral() {
        return tipoDoctoOrdemGeral;
    }

    public void setTipoDoctoOrdemGeral(TipoDoctoOficial tipoDoctoOrdemGeral) {
        this.tipoDoctoOrdemGeral = tipoDoctoOrdemGeral;
    }

    public TipoDoctoOficial getTipoDoctoRelatorioFinal() {
        return tipoDoctoRelatorioFinal;
    }

    public void setTipoDoctoRelatorioFinal(TipoDoctoOficial tipoDoctoRelatorioFinal) {
        this.tipoDoctoRelatorioFinal = tipoDoctoRelatorioFinal;
    }

    public Integer getPrazoProcessoOrdem() {
        return prazoProcessoOrdem;
    }

    public void setPrazoProcessoOrdem(Integer prazoProcessoOrdem) {
        this.prazoProcessoOrdem = prazoProcessoOrdem;
    }

    public Integer getPrazoRespostaComunicado() {
        return prazoRespostaComunicado;
    }

    public void setPrazoRespostaComunicado(Integer prazoRespostaComunicado) {
        this.prazoRespostaComunicado = prazoRespostaComunicado;
    }

    public Integer getPrazoRegularizaPendencia() {
        return prazoRegularizaPendencia;
    }

    public void setPrazoRegularizaPendencia(Integer prazoRegularizaPendencia) {
        this.prazoRegularizaPendencia = prazoRegularizaPendencia;
    }

    public PessoaFisica getDiretorDepartamento() {
        return diretorDepartamento;
    }

    public void setDiretorDepartamento(PessoaFisica diretorDepartamento) {
        this.diretorDepartamento = diretorDepartamento;
    }

    public PessoaFisica getSecretario() {
        return secretario;
    }

    public void setSecretario(PessoaFisica secretario) {
        this.secretario = secretario;
    }

    public Boolean getNfse() {
        return nfse;
    }

    public void setNfse(Boolean nfse) {
        this.nfse = nfse;
    }

    public Boolean getSimplesNacional() {
        return simplesNacional;
    }

    public void setSimplesNacional(Boolean simplesNacional) {
        this.simplesNacional = simplesNacional;
    }

    public Boolean getOutro() {
        return outro;
    }

    public void setOutro(Boolean outro) {
        this.outro = outro;
    }

    public ParametroMalaDireta getParametroMalaDireta() {
        return parametroMalaDireta;
    }

    public void setParametroMalaDireta(ParametroMalaDireta parametroMalaDireta) {
        this.parametroMalaDireta = parametroMalaDireta;
    }
}
