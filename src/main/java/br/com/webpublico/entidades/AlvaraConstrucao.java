package br.com.webpublico.entidades;

import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ResponsavelServico;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Tributario")
@Entity
@Audited
@Etiqueta("Alvará de Construção")
public class AlvaraConstrucao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;
    @Etiqueta("Exercício")
    @Pesquisavel
    @ManyToOne
    private Exercicio exercicio;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano do Protocolo")
    private String anoProtocolo;
    @Etiqueta("Usuário Responsável")
    @ManyToOne
    private UsuarioSistema usuarioIncluiu;
    @Tabelavel
    @Etiqueta("Data de Expedição do Alvará")
    @Temporal(TemporalType.DATE)
    private Date dataExpedicao;
    @Temporal(TemporalType.DATE)
    private Date dataVencimentoCartaz;
    @Temporal(TemporalType.DATE)
    private Date dataVencimentoDebito;
    @Etiqueta("Situação")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    @Etiqueta("Processo de Regularização")
    @Obrigatorio
    @ManyToOne
    private ProcRegularizaConstrucao procRegularizaConstrucao;
    @OneToMany(mappedBy = "alvaraConstrucao", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ServicosAlvaraConstrucao> servicos;
    @Etiqueta("Responsável pela Obra")
    @Obrigatorio
    @ManyToOne
    private ResponsavelServico responsavelServico;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ProcessoCalculoAlvaraConstrucaoHabitese processoCalcAlvaConstHabi;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ProcessoCalculoAlvaraConstrucaoHabitese processoCalcAlvaConstVisto;
    @OneToOne(cascade = CascadeType.ALL)
    private DocumentoOficial documentoOficial;
    @OneToOne(cascade = CascadeType.ALL)
    private ConstrucaoAlvara construcaoAlvara;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "alvaraConstrucao")
    private List<Habitese> habiteses;
    private Boolean envioSisObra;

    public AlvaraConstrucao() {
        situacao = Situacao.EM_ABERTO;
        servicos = Lists.newArrayList();
        envioSisObra = false;
    }

    @Override
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

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataExpedicao() {
        return dataExpedicao;
    }

    public void setDataExpedicao(Date dataExpedicao) {
        this.dataExpedicao = dataExpedicao;
    }

    public Date getDataVencimentoCartaz() {
        return dataVencimentoCartaz;
    }

    public void setDataVencimentoCartaz(Date dataVencimentoCartaz) {
        this.dataVencimentoCartaz = dataVencimentoCartaz;
    }

    public Date getDataVencimentoDebito() {
        return dataVencimentoDebito;
    }

    public void setDataVencimentoDebito(Date dataVencimentoDebito) {
        this.dataVencimentoDebito = dataVencimentoDebito;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public ProcRegularizaConstrucao getProcRegularizaConstrucao() {
        return procRegularizaConstrucao;
    }

    public void setProcRegularizaConstrucao(ProcRegularizaConstrucao procRegularizaConstrucao) {
        this.procRegularizaConstrucao = procRegularizaConstrucao;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public UsuarioSistema getUsuarioIncluiu() {
        return usuarioIncluiu;
    }

    public void setUsuarioIncluiu(UsuarioSistema usuarioIncluiu) {
        this.usuarioIncluiu = usuarioIncluiu;
    }

    public List<ServicosAlvaraConstrucao> getServicos() {
        return servicos;
    }

    public void setServicos(List<ServicosAlvaraConstrucao> servicos) {
        this.servicos = servicos;
    }

    public ResponsavelServico getResponsavelServico() {
        return responsavelServico;
    }

    public void setResponsavelServico(ResponsavelServico responsavelServico) {
        this.responsavelServico = responsavelServico;
    }

    public ProcessoCalculoAlvaraConstrucaoHabitese getProcessoCalcAlvaConstHabi() {
        return processoCalcAlvaConstHabi;
    }

    public void setProcessoCalcAlvaConstHabi(ProcessoCalculoAlvaraConstrucaoHabitese processoCalcAlvaConstHabi) {
        this.processoCalcAlvaConstHabi = processoCalcAlvaConstHabi;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public ProcessoCalculoAlvaraConstrucaoHabitese getProcessoCalcAlvaConstVisto() {
        return processoCalcAlvaConstVisto;
    }

    public void setProcessoCalcAlvaConstVisto(ProcessoCalculoAlvaraConstrucaoHabitese processoCalcAlvaConstVisto) {
        this.processoCalcAlvaConstVisto = processoCalcAlvaConstVisto;
    }

    public boolean isVencido() {
        return this.getDataVencimentoCartaz() != null && (DataUtil.dataSemHorario(this.getDataVencimentoCartaz()).compareTo(new Date()) <= 0 && !this.getSituacao().equals(AlvaraConstrucao.Situacao.FINALIZADO));
    }

    public ConstrucaoAlvara getConstrucaoAlvara() {
        return construcaoAlvara;
    }

    public void setConstrucaoAlvara(ConstrucaoAlvara construcaoAlvara) {
        this.construcaoAlvara = construcaoAlvara;
    }

    public List<Habitese> getHabiteses() {
        return habiteses;
    }

    public void setHabiteses(List<Habitese> habiteses) {
        this.habiteses = habiteses;
    }

    public boolean isTodosPavimentosComHabitesePago() {
        return construcaoAlvara.getQuantidadePavimentos() == getQuantidadePavimentosComHabitesePago();
    }

    public int getQuantidadePavimentosComHabitesePago() {
        int quantidadePavimentosPagos = 0;
        for (Habitese habitese1 : habiteses) {
            if (Habitese.Situacao.FINALIZADO.equals(habitese1.getSituacao())) {
                if(habitese1.getCaracteristica() != null) {
                    quantidadePavimentosPagos += habitese1.getCaracteristica().getQuantidadeDePavimentos();
                }
            }
        }
        return quantidadePavimentosPagos;
    }

    public int getQuantidadePavimentosComHabiteseNaoPago() {
        return (construcaoAlvara.getQuantidadePavimentos() != null ? construcaoAlvara.getQuantidadePavimentos() : 0) - getQuantidadePavimentosComHabitesePago();
    }

    public BigDecimal getTotalAreaServicos() {
        BigDecimal areaTotal = BigDecimal.ZERO;
        for (ServicosAlvaraConstrucao servico : servicos) {
            areaTotal = areaTotal.add(servico.getArea());
        }
        return areaTotal;
    }

    public Boolean getEnvioSisObra() {
        return envioSisObra;
    }

    public void setEnvioSisObra(Boolean envioSisObra) {
        this.envioSisObra = envioSisObra;
    }

    public enum Situacao implements EnumComDescricao {
        EM_ABERTO("Em Aberto"),
        EFETIVADO("Efetivado"),
        RECALCULADO("Recalculado"),
        FINALIZADO("Finalizado"),
        CANCELADO("Cancelado");

        private final String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }

    @Override
    public String toString() {
        return (codigo != null ? codigo : "") + "/" + exercicio.getAno() + " (" + procRegularizaConstrucao.getCadastroImobiliario().getInscricaoCadastral() + ")";
    }
}
