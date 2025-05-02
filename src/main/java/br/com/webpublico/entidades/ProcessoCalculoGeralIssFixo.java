package br.com.webpublico.entidades;

import br.com.webpublico.enums.NivelOcorrencia;
import br.com.webpublico.enums.SituacaoProcessoCalculoGeralIssFixo;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 05/07/13
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */
@Entity

@Audited
@Etiqueta("Lançamento Geral de ISS Fixo")
@Table(name = "PROCCALCGERALISSFIXO")
public class ProcessoCalculoGeralIssFixo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Exercício")
    @ManyToOne
    private Exercicio exercicio;
    @Pesquisavel
    @Etiqueta("Tipo de Autônomo")
    @ManyToOne
    private TipoAutonomo tipoAutonomo;
    @Etiqueta("Tipo de Autônomo")
    @Tabelavel
    @Transient
    private String tipoAutonomoParaExibirNaLista;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("C.M.C Inicial")
    @Obrigatorio
    private String cmcInicial;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("C.M.C Final")
    @Obrigatorio
    private String cmcFinal;
    @OneToOne
    private ProcessoCalculoISS processoCalculoISS;
    @Invisivel
    private Long tempoDecorrido;
    @Invisivel
    private Integer totalDeInscricoes;
    @OneToMany(mappedBy = "processoCalculoGeral", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OcorrenciaProcessoCalculoGeralIssFixo> ocorrencias;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação do Processo")
    private SituacaoProcessoCalculoGeralIssFixo situacaoProcesso;
    @ManyToOne
    @Etiqueta("Usuário do Lançamento")
    @Tabelavel
    @Pesquisavel
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data/Hora do Lançamento")
    @Tabelavel
    @Pesquisavel
    private Date dataDoLancamento;
    @Transient
    @Invisivel
    private Long criadoEm;

    public ProcessoCalculoGeralIssFixo() {
        this.criadoEm = System.nanoTime();
    }

    public ProcessoCalculoGeralIssFixo(Long id, String descricao, Exercicio exercicio, TipoAutonomo tipoAutonomo, String cmcInicial, String cmcFinal, SituacaoProcessoCalculoGeralIssFixo situacaoProcessoCalculoGeralIssFixo, UsuarioSistema us, Date dataLancamento) {
        this.id = id;
        this.descricao = descricao;
        this.exercicio = exercicio;
        this.tipoAutonomo = tipoAutonomo;
        this.tipoAutonomoParaExibirNaLista = this.tipoAutonomo == null ? "Todos" : this.tipoAutonomo.getDescricao();
        this.cmcInicial = cmcInicial;
        this.cmcFinal = cmcFinal;
        this.situacaoProcesso = situacaoProcessoCalculoGeralIssFixo;
        this.usuarioSistema = us;
        this.dataDoLancamento = dataLancamento;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ProcessoCalculoISS getProcessoCalculoISS() {
        return processoCalculoISS;
    }

    public void setProcessoCalculoISS(ProcessoCalculoISS processoCalculoISS) {
        this.processoCalculoISS = processoCalculoISS;
    }

    public String getCmcFinal() {
        return cmcFinal;
    }

    public void setCmcFinal(String cmcFinal) {
        this.cmcFinal = cmcFinal;
    }

    public String getCmcInicial() {
        return cmcInicial;
    }

    public void setCmcInicial(String cmcInicial) {
        this.cmcInicial = cmcInicial;
    }

    public TipoAutonomo getTipoAutonomo() {
        return tipoAutonomo;
    }

    public void setTipoAutonomo(TipoAutonomo tipoAutonomo) {
        this.tipoAutonomo = tipoAutonomo;
    }

    public String getTipoAutonomoParaExibirNaLista() {
        if (tipoAutonomo != null) {
            tipoAutonomoParaExibirNaLista = tipoAutonomo.getDescricao();
        } else {
            tipoAutonomoParaExibirNaLista = "Todos";
        }

        return tipoAutonomoParaExibirNaLista;
    }

    public void setTipoAutonomoParaExibirNaLista(String tipoAutonomoParaExibirNaLista) {
        this.tipoAutonomoParaExibirNaLista = tipoAutonomoParaExibirNaLista;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<OcorrenciaProcessoCalculoGeralIssFixo> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(List<OcorrenciaProcessoCalculoGeralIssFixo> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public SituacaoProcessoCalculoGeralIssFixo getSituacaoProcessoCalculoGeralIssFixo() {
        return situacaoProcesso;
    }

    public void setSituacaoProcessoCalculoGeralIssFixo(SituacaoProcessoCalculoGeralIssFixo SituacaoProcessoCalculoGeralIssFixo) {
        this.situacaoProcesso = SituacaoProcessoCalculoGeralIssFixo;
    }

    private void testarEInicializarOcorrencias() {
        if (ocorrencias == null) {
            ocorrencias = new ArrayList<OcorrenciaProcessoCalculoGeralIssFixo>();
        }
    }

    private List<OcorrenciaProcessoCalculoGeralIssFixo> obterListaDeOcorrencias(NivelOcorrencia nivelOcorrencia) {
        List<OcorrenciaProcessoCalculoGeralIssFixo> lista = new ArrayList<>();

        if (ocorrencias != null) {
            for (OcorrenciaProcessoCalculoGeralIssFixo ocorrencia : ocorrencias) {
                if (ocorrencia.getOcorrencia().getNivelOcorrencia().equals(nivelOcorrencia)) {
                    lista.add(ocorrencia);
                }
            }
        }

        return lista;
    }

    public Long getTempoDecorrido() {
        return tempoDecorrido;
    }

    public void setTempoDecorrido(Long tempoDecorrido) {
        this.tempoDecorrido = tempoDecorrido;
    }

    public void setTotalDeInscricoes(Integer totalDeInscricoes) {
        this.totalDeInscricoes = totalDeInscricoes;
    }

    public String getDescricaoTipoAutonomo() {
        return tipoAutonomo == null ? "Todos" : tipoAutonomo.getDescricao();
    }

    public Date getDataDoLancamento() {
        return dataDoLancamento;
    }

    public void setDataDoLancamento(Date dataDoLancamento) {
        this.dataDoLancamento = dataDoLancamento;
    }

    public String getDataDoLancamentoTimeStamp() {
        return Util.dateHourToString(dataDoLancamento);
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}
