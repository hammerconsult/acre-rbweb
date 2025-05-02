package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 30/07/13
 * Time: 18:52
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Cacheable
@Audited
@Table(name = "EFETPROCISSFIXOGERAL")
@Etiqueta("Efetivação de Lançamento Geral de ISS Fixo")
public class EfetivacaoProcessoIssFixoGeral extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Efetivação")
    private EfetivacaoIssFixoGeral efetivacao;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Processo")
    private ProcessoCalculoGeralIssFixo processo;
    @OneToMany(mappedBy = "efetivacaoProcesso", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OcorrenciaEfetivacaoIssFixoGeral> listaDeOcorrencias;
    /*ATRIBUTOS SOMENTE PARA EXIBIÇÃO*/
    @Transient
    @Tabelavel
    @Etiqueta("Usuário da Efetivação")
    private UsuarioSistema usuarioEfetivacao;
    @Transient
    @Tabelavel
    @Etiqueta("Data da Efetivação")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataDaEfetivacao;
    @Transient
    @Tabelavel
    @Etiqueta("Descrição do Processo")
    private String descricao;
    @Transient
    @Tabelavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Transient
    @Tabelavel
    @Etiqueta("Tipo de Autônomo")
    private String tipoAutonomoParaExibirNaLista;
    @Transient
    @Tabelavel
    @Etiqueta("C.M.C. Inicial")
    private String cmcInicial;
    @Transient
    @Tabelavel
    @Etiqueta("C.M.C. Final")
    private String cmcFinal;
    @Tabelavel
    @Transient
    @Etiqueta("Usuário do Lançamento")
    private UsuarioSistema usuarioLancamento;
    @Tabelavel
    @Transient
    @Etiqueta("Data do Lançamento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataDoLancamento;

    public EfetivacaoProcessoIssFixoGeral() {
        this.listaDeOcorrencias = new ArrayList<>();
    }

    public EfetivacaoProcessoIssFixoGeral(EfetivacaoIssFixoGeral efetivacao, ProcessoCalculoGeralIssFixo processo) {
        this.efetivacao = efetivacao;
        this.processo = processo;
        this.listaDeOcorrencias = new ArrayList<>();
    }

    public EfetivacaoProcessoIssFixoGeral(Long id, ProcessoCalculoGeralIssFixo processo, EfetivacaoIssFixoGeral efetivacao) {
        this.id = id;
        this.dataDoLancamento = processo.getDataDoLancamento();
        this.usuarioLancamento = processo.getUsuarioSistema();
        this.cmcFinal = processo.getCmcFinal();
        this.cmcInicial = processo.getCmcInicial();
        this.tipoAutonomoParaExibirNaLista = processo.getTipoAutonomo() == null ? "Todos" : processo.getTipoAutonomo().getDescricao();
        this.exercicio = processo.getExercicio();
        this.descricao = processo.getDescricao();
        this.dataDaEfetivacao = efetivacao.getDataDaEfetivacao();
        this.usuarioEfetivacao = efetivacao.getUsuarioSistema();
        this.processo = processo;
        this.efetivacao = efetivacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EfetivacaoIssFixoGeral getEfetivacao() {
        return efetivacao;
    }

    public void setEfetivacao(EfetivacaoIssFixoGeral efetivacao) {
        this.efetivacao = efetivacao;
    }

    public ProcessoCalculoGeralIssFixo getProcesso() {
        return processo;
    }

    public void setProcesso(ProcessoCalculoGeralIssFixo processo) {
        this.processo = processo;
    }

    public List<OcorrenciaEfetivacaoIssFixoGeral> getListaDeOcorrencias() {
        return listaDeOcorrencias;
    }

    public void setListaDeOcorrencias(List<OcorrenciaEfetivacaoIssFixoGeral> listaDeOcorrencias) {
        this.listaDeOcorrencias = listaDeOcorrencias;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
