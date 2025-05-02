package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Solicitação de Processo de Isenção de Cálculo de IPTU")
public class ProcessoIsencaoIPTU extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta("Inscrição Inicial")
    private String iscricaoInicial;
    @Pesquisavel
    @Etiqueta("Inscrição Final")
    private String iscricaoFinal;
    @Pesquisavel
    @Etiqueta("Setor Inicial")
    private String setorInicial;
    @Pesquisavel
    @Etiqueta("Setor Final")
    private String setorFinal;
    @Pesquisavel
    @Etiqueta("Distrito Inicial")
    private String distritoInicial;
    @Pesquisavel
    @Etiqueta("Distrito Final")
    private String distritoFinal;
    @Pesquisavel
    @Etiqueta("Quadra Inicial")
    private String quadraInicial;
    @Pesquisavel
    @Etiqueta("Quadra Final")
    private String quadraFinal;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Categoria de Isenção")
    @ManyToOne
    private CategoriaIsencaoIPTU categoriaIsencaoIPTU;
    @OneToMany(mappedBy = "processoIsencaoIPTU", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IsencaoCadastroImobiliario> isencoes;
    @Tabelavel
    @Etiqueta("Código do Processo")
    @Obrigatorio
    @Pesquisavel
    private Long numero;
    @Etiqueta("Número do Processo COLUNA NOVA")
    @Transient
    private String numeroProcessoComExercicio;
    @Tabelavel
    @Etiqueta("Exercicio do Processo")
    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    private Exercicio exercicioProcesso;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Exercício de Referencia")
    private Exercicio exercicioReferencia;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Ano do Protocolo")
    private Long anoProtocolo;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Início de Vigência")
    private Date dataLancamento;
    @Tabelavel
    @Etiqueta("Fim de Vigência")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date validade;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Usuário Responsável")
    private UsuarioSistema usuarioResponsavel;
    private String observacao;
    @Etiqueta("Arquivos")
    @OneToMany(mappedBy = "processoIsencaoIPTU", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessoIsencaoIPTUArquivos> arquivos;
    @Tabelavel
    @Etiqueta("Situação")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "processo")
    @Where(clause = "enquadra = 0")
    private List<CadastroIsencaoIPTU> cadastrosNaoEnquadrados;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "processo")
    @Where(clause = "enquadra = 1")
    private List<CadastroIsencaoIPTU> cadastrosEnquadrados;

    public ProcessoIsencaoIPTU() {
        isencoes = new ArrayList<>();
        dataLancamento = new Date();
        numero = 1l;
        iscricaoInicial = "1";
        iscricaoFinal = "9999999999999999";
        cadastrosNaoEnquadrados = new ArrayList<>();
        cadastrosEnquadrados = new ArrayList<>();
        arquivos = Lists.newArrayList();
        situacao = Situacao.ABERTO;
    }

    public List<CadastroIsencaoIPTU> getCadastrosNaoEnquadrados() {
        return cadastrosNaoEnquadrados;
    }

    public void setCadastrosNaoEnquadrados(List<CadastroIsencaoIPTU> cadastrosNaoEnquadrados) {
        this.cadastrosNaoEnquadrados = cadastrosNaoEnquadrados;
    }

    public List<CadastroIsencaoIPTU> getCadastrosEnquadrados() {
        return cadastrosEnquadrados;
    }

    public void setCadastrosEnquadrados(List<CadastroIsencaoIPTU> cadastrosEnquadrados) {
        this.cadastrosEnquadrados = cadastrosEnquadrados;
    }

    public String getIscricaoInicial() {
        return iscricaoInicial;
    }

    public void setIscricaoInicial(String iscricaoInicial) {
        this.iscricaoInicial = iscricaoInicial;
    }

    public String getIscricaoFinal() {
        return iscricaoFinal;
    }

    public void setIscricaoFinal(String iscricaoFinal) {
        this.iscricaoFinal = iscricaoFinal;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        if (numero != null) {
            if (numeroProcessoComExercicio == null || numeroProcessoComExercicio.isEmpty()) {
                numeroProcessoComExercicio = numero.toString();
            } else {
                numeroProcessoComExercicio = numero.toString() + numeroProcessoComExercicio;
            }
        }
        this.numero = numero;
    }

    public Exercicio getExercicioProcesso() {
        return exercicioProcesso;
    }

    public void setExercicioProcesso(Exercicio exercicioProcesso) {
        if (exercicioProcesso != null) {
            if (numeroProcessoComExercicio == null || numeroProcessoComExercicio.isEmpty()) {
                numeroProcessoComExercicio = "/" + exercicioProcesso.getAno();
            } else {
                numeroProcessoComExercicio += "/" + exercicioProcesso.getAno();
            }
        }
        this.exercicioProcesso = exercicioProcesso;
    }

    public Exercicio getExercicioReferencia() {
        return exercicioReferencia;
    }

    public String getNumeroProcessoComExercicio() {
        if (numero != null && exercicioProcesso != null) {
            numeroProcessoComExercicio = numero.toString() + "/" + exercicioProcesso.getAno().toString();
        }
        return numeroProcessoComExercicio;
    }

    public void setNumeroProcessoComExercicio(String numeroProcessoComExercicio) {
        this.numeroProcessoComExercicio = numeroProcessoComExercicio;
    }

    public void setExercicioReferencia(Exercicio exercicioRefencia) {
        this.exercicioReferencia = exercicioRefencia;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public Long getAnoProtocolo() {
        return anoProtocolo;
    }

    public String getAnoProtocoloString() {
        if (anoProtocolo == null) {
            return "";
        }
        return String.valueOf(anoProtocolo);
    }

    public void setAnoProtocoloString(String anoProtocoloString) {
        if (Strings.isNullOrEmpty(anoProtocoloString)) {
            anoProtocolo = null;
        } else {
            anoProtocolo = Long.parseLong(anoProtocoloString);
        }
    }

    public void setAnoProtocolo(Long anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public UsuarioSistema getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(UsuarioSistema usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public String getInscricaoInicial() {
        return iscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoIncial) {
        this.iscricaoInicial = inscricaoIncial;
    }

    public String getInscricaoFinal() {
        return iscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.iscricaoFinal = inscricaoFinal;
    }

    public String getSetorInicial() {
        return setorInicial;
    }

    public void setSetorInicial(String setorInicial) {
        this.setorInicial = setorInicial;
    }

    public String getSetorFinal() {
        return setorFinal;
    }

    public void setSetorFinal(String setorFinal) {
        this.setorFinal = setorFinal;
    }

    public String getDistritoInicial() {
        return distritoInicial;
    }

    public void setDistritoInicial(String distritoInicial) {
        this.distritoInicial = distritoInicial;
    }

    public String getDistritoFinal() {
        return distritoFinal;
    }

    public void setDistritoFinal(String distritoFinal) {
        this.distritoFinal = distritoFinal;
    }

    public String getQuadraInicial() {
        return quadraInicial;
    }

    public void setQuadraInicial(String quadraInicial) {
        this.quadraInicial = quadraInicial;
    }

    public String getQuadraFinal() {
        return quadraFinal;
    }

    public void setQuadraFinal(String quadraFinal) {
        this.quadraFinal = quadraFinal;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public List<IsencaoCadastroImobiliario> getIsencoes() {
        return isencoes;
    }

    public void setIsencoes(List<IsencaoCadastroImobiliario> isencoes) {
        this.isencoes = isencoes;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CategoriaIsencaoIPTU getCategoriaIsencaoIPTU() {
        return categoriaIsencaoIPTU;
    }

    public void setCategoriaIsencaoIPTU(CategoriaIsencaoIPTU categoriaIsencaoIPTU) {
        this.categoriaIsencaoIPTU = categoriaIsencaoIPTU;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ProcessoIsencaoIPTUArquivos> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<ProcessoIsencaoIPTUArquivos> arquivos) {
        this.arquivos = arquivos;
    }

    @Override
    public String toString() {
        try {
            return numero + "/" + exercicioProcesso;
        } catch (Exception ex) {
            return "";
        }
    }

    public String toStringRelatorio() {
        return "Nº " + numero.toString() + " / " + exercicioProcesso.toString() + " Categoria: " + categoriaIsencaoIPTU.toString();
    }

    public boolean temInscricaoInicialEFinal() {
        return (!Util.isStringNulaOuVazia(iscricaoInicial)) && (!Util.isStringNulaOuVazia(iscricaoFinal));
    }

    public boolean temInscricaoInicialENaoTemFinal() {
        return (!Util.isStringNulaOuVazia(iscricaoInicial)) && (Util.isStringNulaOuVazia(iscricaoFinal));
    }

    public boolean temInscricaoFinalENaoTemInicial() {
        return (Util.isStringNulaOuVazia(iscricaoInicial)) && (!Util.isStringNulaOuVazia(iscricaoFinal));
    }

    public boolean temDistritoInicialENaoTemFinal() {
        return (!Util.isStringNulaOuVazia(distritoInicial)) && (Util.isStringNulaOuVazia(distritoFinal));
    }

    public boolean temDistritoFinalENaoTemInicial() {
        return (Util.isStringNulaOuVazia(distritoInicial)) && (!Util.isStringNulaOuVazia(distritoFinal));
    }

    public boolean temDistritoInicialEFinal() {
        return (!Util.isStringNulaOuVazia(distritoInicial)) && (!Util.isStringNulaOuVazia(distritoFinal));
    }

    public boolean temSetorInicialENaoTemFinal() {
        return (!Util.isStringNulaOuVazia(setorInicial)) && (Util.isStringNulaOuVazia(setorFinal));
    }

    public boolean temSetorFinalENaoTemInicial() {
        return (Util.isStringNulaOuVazia(setorInicial)) && (!Util.isStringNulaOuVazia(setorFinal));
    }

    public boolean temQuadraInicialENaoTemFinal() {
        return (!Util.isStringNulaOuVazia(quadraInicial)) && (Util.isStringNulaOuVazia(quadraFinal));
    }

    public boolean temQuadraFinalENaoTemInicial() {
        return (Util.isStringNulaOuVazia(setorInicial)) && (!Util.isStringNulaOuVazia(setorFinal));
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public enum Situacao implements EnumComDescricao {
        ABERTO("Em Aberto"),
        EFETIVADO("Efetivado"),
        DEFERIDO("Deferido"),
        INDEFERIDO("Indeferido"),
        VENCIDO("Vencido");

        private String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        public boolean isAberto() {
            return Situacao.ABERTO.equals(this);
        }

        public boolean isDeferido() {
            return Situacao.DEFERIDO.equals(this);
        }

        public boolean isIndeferido() {
            return Situacao.INDEFERIDO.equals(this);
        }

        public boolean isVencido() {
            return Situacao.VENCIDO.equals(this);
        }
    }
}
