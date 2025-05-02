/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCertidaoDA;
import br.com.webpublico.enums.SituacaoJudicial;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoCDAException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Renato
 */
@Entity
@Etiqueta("Certidão de Dívida Ativa")
@Audited
@GrupoDiagrama(nome = "Divida Ativa")
public class CertidaoDividaAtiva extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private Long numero;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCertidao;
    @ManyToOne
    private PessoaFisica funcionarioAssinante;
    @ManyToOne
    private PessoaFisica funcionarioEmissao;
    @ManyToOne
    private DocumentoOficial documentoOficial;
    @ManyToOne
    @Etiqueta("Execício")
    @Tabelavel
    @Pesquisavel
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    private SituacaoCertidaoDA situacaoCertidaoDA;
    @Enumerated(EnumType.STRING)
    private SituacaoJudicial situacaoJudicial;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "certidao", orphanRemoval = true)
    private List<ItemCertidaoDividaAtiva> itensCertidaoDividaAtiva;
    private Boolean integrada;
    @Etiqueta("Tipo de Cadastro")
    @Transient
    @Tabelavel
    private TipoCadastroTributario tipoCadastroTributario;
    @Etiqueta("Número do Cadastro")
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Cadastro cadastro;
    @ManyToOne
    private Pessoa pessoa;
    @Transient
    private BigDecimal valorOriginal;
    @Transient
    private BigDecimal valorMulta;
    @Transient
    private BigDecimal valorJuros;
    @Transient
    private ValidacoesCDA validacoesCDA;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "certidaoDividaAtiva")
    private List<OcorrenciaCDA> ocorrencias;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "cda")
    private List<ComunicacaoSoftPlan> comunicacoes;
    private String numeroCDALegado;
    @Enumerated(EnumType.STRING)
    private RetornoComunicacao retornoComunicacao;
    @Transient
    private Boolean semLivro;
    @OneToMany(mappedBy = "certidaoDividaAtiva")
    private List<ProcessoJudicialCDA> processos;
    @OneToMany(mappedBy = "certidaoDividaAtiva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CertidaoDividaAtivaLegada> certidoesLegadas;
    @Transient
    //usado no método de geração do arquivo de CDAs atualizadas
    private String ano;

    @Etiqueta("Motivo Desvinculação CDA")
    private String motivoDesvinculacao;

    @ManyToOne
    @Etiqueta("Usuário Desvinculação CDA")
    private UsuarioSistema usuarioDesvinculacao;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data Desvinculação CDA")
    private Date dataDesvinculacao;

    public CertidaoDividaAtiva() {
        this.integrada = Boolean.FALSE;
        this.itensCertidaoDividaAtiva = Lists.newArrayList();
        this.validacoesCDA = new ValidacoesCDA(this);
        this.ocorrencias = Lists.newArrayList();
        this.comunicacoes = Lists.newArrayList();
        this.certidoesLegadas = Lists.newArrayList();
        valorJuros = BigDecimal.ZERO;
        valorMulta = BigDecimal.ZERO;
        valorOriginal = BigDecimal.ZERO;
        situacaoJudicial = SituacaoJudicial.NAO_AJUIZADA;
    }

    public String getAno() {
        return ano; //usado no método de geração do arquivo de CDAs atualizadas
    }

    public void setAno(String ano) {
        this.ano = ano;//usado no método de geração do arquivo de CDAs atualizadas
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public PessoaFisica getFuncionarioAssinante() {
        return funcionarioAssinante;
    }

    public void setFuncionarioAssinante(PessoaFisica funcionarioAssinante) {
        this.funcionarioAssinante = funcionarioAssinante;
    }

    public PessoaFisica getFuncionarioEmissao() {
        return funcionarioEmissao;
    }

    public void setFuncionarioEmissao(PessoaFisica funcionarioEmissao) {
        this.funcionarioEmissao = funcionarioEmissao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<ComunicacaoSoftPlan> getComunicacoes() {
        return comunicacoes;
    }

    public void setComunicacoes(List<ComunicacaoSoftPlan> comunicacoes) {
        this.comunicacoes = comunicacoes;
    }

    public void setOcorrencias(List<OcorrenciaCDA> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    public SituacaoCertidaoDA getSituacaoCertidaoDA() {
        return situacaoCertidaoDA;
    }

    public void setSituacaoCertidaoDA(SituacaoCertidaoDA situacaoCertidaoDA) {
        this.situacaoCertidaoDA = situacaoCertidaoDA;
    }

    public Boolean getIntegrada() {
        return integrada;
    }

    public void setIntegrada(Boolean integrada) {
        this.integrada = integrada;
    }

    public List<ItemCertidaoDividaAtiva> getItensCertidaoDividaAtiva() {
        return itensCertidaoDividaAtiva;
    }

    public void setItensCertidaoDividaAtiva(List<ItemCertidaoDividaAtiva> itensCertidaoDividaAtiva) {
        this.itensCertidaoDividaAtiva = itensCertidaoDividaAtiva;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDataCertidao() {
        return dataCertidao;
    }

    public void setDataCertidao(Date dataCertidao) {
        this.dataCertidao = dataCertidao;
    }

    public String getNumeroCDALegado() {
        return numeroCDALegado;
    }

    public String getNumeroCDALegadoComExercicio() {
        if (numeroCDALegado != null && !"".equals(numeroCDALegado)) {
            return numeroCDALegado + (exercicio != null ? "/" + exercicio.getAno() : "");
        }
        return "";
    }

    public void setNumeroCDALegado(String numeroCDALegado) {
        this.numeroCDALegado = numeroCDALegado;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        if (cadastro != null) {
            if (cadastro instanceof CadastroImobiliario) {
                tipoCadastroTributario = TipoCadastroTributario.IMOBILIARIO;
            } else if (cadastro instanceof CadastroEconomico) {
                tipoCadastroTributario = TipoCadastroTributario.ECONOMICO;
            } else if (cadastro instanceof CadastroRural) {
                tipoCadastroTributario = TipoCadastroTributario.RURAL;
            } else {
                tipoCadastroTributario = TipoCadastroTributario.PESSOA;
            }
        } else {
            tipoCadastroTributario = TipoCadastroTributario.PESSOA;
        }
        return tipoCadastroTributario;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public List<OcorrenciaCDA> getOcorrencias() {
        return ocorrencias;
    }

    public Boolean getSemLivro() {
        return semLivro != null ? semLivro : false;
    }

    public void setSemLivro(Boolean semLivro) {
        this.semLivro = semLivro;
    }

    public CertidaoDividaAtiva.RetornoComunicacao getRetornoComunicacao() {
        return retornoComunicacao;
    }

    public void setRetornoComunicacao(CertidaoDividaAtiva.RetornoComunicacao retornoComunicacao) {
        this.retornoComunicacao = retornoComunicacao;
    }

    public List<ProcessoJudicialCDA> getProcessos() {
        return processos;
    }

    public void setProcessos(List<ProcessoJudicialCDA> processos) {
        this.processos = processos;
    }

    public String getNumeroCdaSoftplan() {
        return getNumero() + "" + getExercicio().getAno();
    }

    public String getNumeroCdaComExercicio() {
        return getNumero() + "/" + getExercicio().getAno();
    }

    @Override
    public String toString() {
        return "Nº : " + this.numero + "/" + exercicio.getAno();
    }

    public CertidaoDividaAtiva validar() throws ValidacaoCDAException {
        validaCDA();
        if (validacoesCDA.isValida()) {
            return this;
        } else {
            throw new ValidacaoCDAException(validacoesCDA);
        }
    }

    private void validaCDA() {
        if (numero == null) {
            validacoesCDA.addMessage("Número da CDA não é válido");
        }
        if (exercicio == null || exercicio.getAno() == null) {
            validacoesCDA.addMessage("Exercício da CDA não é válido");
        }
        if (itensCertidaoDividaAtiva == null || itensCertidaoDividaAtiva.isEmpty()) {
            validacoesCDA.addMessage("Não há débitos vinculados a essa CDA");
        }
        validaPessoa();
    }

    private void validaPessoa() {
        if (pessoa == null) {
            validacoesCDA.addMessage("Nenhuma pessoa foi vinculada a essa CDA");
        } else {
            if (pessoa.getNome() == null) {
                validacoesCDA.addMessage("Nome da pessoa não é válido");
            }

            if (!Util.valida_CpfCnpj(this.pessoa.getCpf_Cnpj())) {
                validacoesCDA.addMessage("CPF/CNPJ da pessoa não é válido");
            }
            validaEndereco();
        }
    }

    private void validaEndereco() {
        EnderecoCorreio endereco = pessoa.getEnderecoCompletoOuCorrespondencia();
        if (endereco == null) {
            validacoesCDA.addMessage("Nenhum endereço foi vinculado a essa Pessoa");
        } else {
            if (endereco.getLogradouro() == null || endereco.getLogradouro().trim().isEmpty()) {
                validacoesCDA.addMessage("O Logradouro do endereço não é válido");
            }
            if (endereco.getNumero() == null || endereco.getNumero().trim().isEmpty()) {
                validacoesCDA.addMessage("O Número do endereço não é válido");
            }
            if (endereco.getLocalidade() == null || endereco.getLocalidade().trim().isEmpty()) {
                validacoesCDA.addMessage("A Cidade do endereço não é válida");
            }
            if (endereco.getUf() == null || endereco.getUf().trim().isEmpty()) {
                validacoesCDA.addMessage("A UF do endereço não é válida");
            }
            if (endereco.getCep() == null || endereco.getCep().trim().isEmpty()) {
                validacoesCDA.addMessage("O CEP do endereço não é válido");
            }
        }
    }

    public void setId(BigDecimal bigDecimal) {
        setId(bigDecimal.longValue());
    }

    public void setNumero(BigDecimal bigDecimal) {
        setNumero(bigDecimal.longValue());
    }

    public void setAno(BigDecimal bigDecimal) {
        setAno(bigDecimal.toString());//usado no método de geração do arquivo de CDAs atualizadas
    }

    public static enum RetornoComunicacao {
        SUCESSO("Sucesso"), FALHA("Falha"), NAO_ENVIADO("Não Enviado");
        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        private RetornoComunicacao(String descricao) {
            this.descricao = descricao;
        }
    }

    public static class ValidacoesCDA {
        List<String> messages;
        CertidaoDividaAtiva certidao;

        public ValidacoesCDA(CertidaoDividaAtiva certidao) {
            messages = Lists.newArrayList();
            this.certidao = certidao;
        }

        public void addMessage(String message) {
            messages.add(message);
        }

        public void addMessage(List<String> messages) {
            this.messages.addAll(messages);
        }

        public List<String> getMessages() {
            return messages;
        }

        public CertidaoDividaAtiva getCertidao() {
            return certidao;
        }

        public boolean isValida() {
            return messages.isEmpty();
        }

        @Override
        public String toString() {
            String str = "";
            for (String message : messages) {
                str += message + "/n";
            }
            return str;    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    public static class FiltrosPesquisaCertidaoDividaAtiva {
        private TipoCadastroTributario tipoCadastroTributario;
        private Divida divida;
        private SituacaoCertidaoDA situacaoCertidao;
        private SituacaoJudicial situacaoJudicial;
        private Exercicio exercicioInicial;
        private Exercicio exercicioFinal;
        private Exercicio exercicioInicialDivida;
        private Exercicio exercicioFinalDivida;
        private Long numeroCdaInicial;
        private Long numeroCdaFinal;
        private Exercicio exercicioCdaLegadaInicial;
        private Exercicio exercicioCdaLegadaFinal;
        private String numeroLegadoInicial;
        private String numeroLegadoFinal;
        private String cadastroInicial;
        private String cadastroFinal;
        private Date dataInscricaoInicial;
        private Date dataInscricaoFinal;
        private String processoJudicial;
        private Pessoa pessoa;

        public FiltrosPesquisaCertidaoDividaAtiva() {
            novo(true);
        }

        public void novo(boolean novo) {
            if (novo) {
                tipoCadastroTributario = null;
            }
            divida = null;
            situacaoCertidao = null;
            situacaoJudicial = null;
            exercicioInicial = null;
            exercicioFinal = null;
            exercicioInicialDivida = null;
            exercicioFinalDivida = null;
            numeroCdaInicial = null;
            numeroCdaFinal = null;
            exercicioCdaLegadaInicial = null;
            exercicioCdaLegadaFinal = null;
            numeroLegadoInicial = null;
            numeroLegadoFinal = null;
            cadastroInicial = "1";
            cadastroFinal = "999999999999999999";
            dataInscricaoInicial = null;
            dataInscricaoFinal = null;
            processoJudicial = null;
            pessoa = null;
        }

        public TipoCadastroTributario getTipoCadastroTributario() {
            return tipoCadastroTributario;
        }

        public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
            this.tipoCadastroTributario = tipoCadastroTributario;
        }

        public Divida getDivida() {
            return divida;
        }

        public void setDivida(Divida divida) {
            this.divida = divida;
        }

        public SituacaoCertidaoDA getSituacaoCertidao() {
            return situacaoCertidao;
        }

        public void setSituacaoCertidao(SituacaoCertidaoDA situacaoCertidao) {
            this.situacaoCertidao = situacaoCertidao;
        }

        public SituacaoJudicial getSituacaoJudicial() {
            return situacaoJudicial;
        }

        public void setSituacaoJudicial(SituacaoJudicial situacaoJudicial) {
            this.situacaoJudicial = situacaoJudicial;
        }

        public Exercicio getExercicioInicial() {
            return exercicioInicial;
        }

        public void setExercicioInicial(Exercicio exercicioInicial) {
            this.exercicioInicial = exercicioInicial;
        }

        public Exercicio getExercicioFinal() {
            return exercicioFinal;
        }

        public void setExercicioFinal(Exercicio exercicioFinal) {
            this.exercicioFinal = exercicioFinal;
        }

        public Exercicio getExercicioInicialDivida() {
            return exercicioInicialDivida;
        }

        public void setExercicioInicialDivida(Exercicio exercicioInicialDivida) {
            this.exercicioInicialDivida = exercicioInicialDivida;
        }

        public Exercicio getExercicioFinalDivida() {
            return exercicioFinalDivida;
        }

        public void setExercicioFinalDivida(Exercicio exercicioFinalDivida) {
            this.exercicioFinalDivida = exercicioFinalDivida;
        }

        public Long getNumeroCdaInicial() {
            return numeroCdaInicial;
        }

        public void setNumeroCdaInicial(Long numeroCdaInicial) {
            this.numeroCdaInicial = numeroCdaInicial;
        }

        public Long getNumeroCdaFinal() {
            return numeroCdaFinal;
        }

        public void setNumeroCdaFinal(Long numeroCdaFinal) {
            this.numeroCdaFinal = numeroCdaFinal;
        }

        public String getNumeroLegadoInicial() {
            return numeroLegadoInicial;
        }

        public void setNumeroLegadoInicial(String numeroLegadoInicial) {
            this.numeroLegadoInicial = numeroLegadoInicial;
        }

        public String getNumeroLegadoFinal() {
            return numeroLegadoFinal;
        }

        public void setNumeroLegadoFinal(String numeroLegadoFinal) {
            this.numeroLegadoFinal = numeroLegadoFinal;
        }

        public String getCadastroInicial() {
            return cadastroInicial != null && !"".equals(cadastroInicial.trim()) ? cadastroInicial : "1";
        }

        public void setCadastroInicial(String cadastroInicial) {
            this.cadastroInicial = cadastroInicial;
        }

        public String getCadastroFinal() {
            return cadastroFinal != null && !"".equals(cadastroFinal.trim()) ? cadastroFinal : "999999999999999999";
        }

        public void setCadastroFinal(String cadastroFinal) {
            this.cadastroFinal = cadastroFinal;
        }

        public Date getDataInscricaoInicial() {
            return dataInscricaoInicial;
        }

        public void setDataInscricaoInicial(Date dataInscricaoInicial) {
            this.dataInscricaoInicial = dataInscricaoInicial;
        }

        public Date getDataInscricaoFinal() {
            return dataInscricaoFinal;
        }

        public void setDataInscricaoFinal(Date dataInscricaoFinal) {
            this.dataInscricaoFinal = dataInscricaoFinal;
        }

        public Pessoa getPessoa() {
            return pessoa;
        }

        public void setPessoa(Pessoa pessoa) {
            this.pessoa = pessoa;
        }

        public String getProcessoJudicial() {
            return processoJudicial;
        }

        public void setProcessoJudicial(String processoJudicial) {
            this.processoJudicial = processoJudicial;
        }

        public Exercicio getExercicioCdaLegadaInicial() {
            return exercicioCdaLegadaInicial;
        }

        public void setExercicioCdaLegadaInicial(Exercicio exercicioCdaLegadaInicial) {
            this.exercicioCdaLegadaInicial = exercicioCdaLegadaInicial;
        }

        public Exercicio getExercicioCdaLegadaFinal() {
            return exercicioCdaLegadaFinal;
        }

        public void setExercicioCdaLegadaFinal(Exercicio exercicioCdaLegadaFinal) {
            this.exercicioCdaLegadaFinal = exercicioCdaLegadaFinal;
        }
    }

    public List<CertidaoDividaAtivaLegada> getCertidoesLegadas() {
        return certidoesLegadas;
    }

    public void setCertidoesLegadas(List<CertidaoDividaAtivaLegada> certidoesLegadas) {
        this.certidoesLegadas = certidoesLegadas;
    }

    public SituacaoJudicial getSituacaoJudicial() {
        return situacaoJudicial;
    }

    public void setSituacaoJudicial(SituacaoJudicial situacaoJudicial) {
        this.situacaoJudicial = situacaoJudicial;
    }

    public String getMotivoDesvinculacao() {
        return motivoDesvinculacao;
    }

    public void setMotivoDesvinculacao(String motivoDesvinculacao) {
        this.motivoDesvinculacao = motivoDesvinculacao;
    }

    public UsuarioSistema getUsuarioDesvinculacao() {
        return usuarioDesvinculacao;
    }

    public void setUsuarioDesvinculacao(UsuarioSistema usuarioDesvinculacao) {
        this.usuarioDesvinculacao = usuarioDesvinculacao;
    }

    public Date getDataDesvinculacao() {
        return dataDesvinculacao;
    }

    public void setDataDesvinculacao(Date dataDesvinculacao) {
        this.dataDesvinculacao = dataDesvinculacao;
    }
}
