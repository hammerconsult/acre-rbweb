package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author AndreGustavo
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Autuação Fiscalização RBTrans")
@Table(name = "AUTUACAOFISCALIZACAO")
public class AutuacaoFiscalizacaoRBTrans implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta("Código da Autuação")
    @Tabelavel
    private Long codigo;
    private String localAutuacao;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Pesquisavel
    @Etiqueta("Data da Autuação")
    @Tabelavel
    private Date dataAutuacao;
    private String descricaoDetalhada;
    @OneToMany(mappedBy = "autuacaoFiscalizacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificacaoRBTrans> notificacoes;
    @ManyToOne
    private PessoaFisica motoristaInfrator;
    @ManyToOne
    private PessoaFisica pessoaClandestina;
    @ManyToOne
    @Etiqueta("Agente Autuador")
    private AgenteAutuador agenteAutuador;
    @ManyToOne
    private Cidade cidadeInfracao;
    @ManyToOne
    @Etiqueta("Veículo")
    @Tabelavel
    private VeiculoTransporte veiculoTransporte;
    private String placaVeiculo;
    private String descricaoVeiculo;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Cadastro Econômico")
    @Tabelavel
    private CadastroEconomico cadastroEconomico;
    @OneToMany(mappedBy = "autuacaoFiscalizacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecursoRBTrans> recursosRBTrans;
    @OneToMany(mappedBy = "autuacaoFiscalizacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OcorrenciaAutuacaoRBTrans> ocorrenciasAutuacao;
    @ManyToOne
    private AnaliseAutuacaoRBTrans analiseAutuacaoRBTrans;
    private boolean processoPrecluso;

    public AutuacaoFiscalizacaoRBTrans() {
        recursosRBTrans = new ArrayList<RecursoRBTrans>();
        ocorrenciasAutuacao = new ArrayList<OcorrenciaAutuacaoRBTrans>();
        notificacoes = new ArrayList<NotificacaoRBTrans>();
        processoPrecluso = false;
    }

    public boolean isProcessoPrecluso() {
        return processoPrecluso;
    }

    public void setProcessoPrecluso(boolean processoPrecluso) {
        this.processoPrecluso = processoPrecluso;
    }

    public AnaliseAutuacaoRBTrans getAnaliseAutuacaoRBTrans() {
        return analiseAutuacaoRBTrans;
    }

    public void setAnaliseAutuacaoRBTrans(AnaliseAutuacaoRBTrans analiseAutuacaoRBTrans) {
        this.analiseAutuacaoRBTrans = analiseAutuacaoRBTrans;
    }

    public List<RecursoRBTrans> getRecursosRBTrans() {
        return recursosRBTrans;
    }

    public void setRecursosRBTrans(List<RecursoRBTrans> recursosRBTrans) {
        this.recursosRBTrans = recursosRBTrans;
    }

    public VeiculoTransporte getVeiculoTransporte() {
        return veiculoTransporte;
    }

    public void setVeiculoTransporte(VeiculoTransporte veiculoTransporte) {
        this.veiculoTransporte = veiculoTransporte;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Cidade getCidadeInfracao() {
        return cidadeInfracao;
    }

    public void setCidadeInfracao(Cidade cidadeInfracao) {
        this.cidadeInfracao = cidadeInfracao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getLocalAutuacao() {
        return localAutuacao;
    }

    public void setLocalAutuacao(String localAutuacao) {
        this.localAutuacao = localAutuacao;
    }

    public Date getDataAutuacao() {
        return dataAutuacao;
    }

    public void setDataAutuacao(Date dataAutuacao) {
        this.dataAutuacao = dataAutuacao;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }

    public void setDescricaoDetalhada(String descricaoDetalhada) {
        this.descricaoDetalhada = descricaoDetalhada;
    }

    public List<NotificacaoRBTrans> getNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(List<NotificacaoRBTrans> notificacoes) {
        this.notificacoes = notificacoes;
    }

    public PessoaFisica getMotoristaInfrator() {
        return motoristaInfrator;
    }

    public void setMotoristaInfrator(PessoaFisica motoristaInfrator) {
        this.motoristaInfrator = motoristaInfrator;
    }

    public AgenteAutuador getAgenteAutuador() {
        return agenteAutuador;
    }

    public void setAgenteAutuador(AgenteAutuador agenteAutuador) {
        this.agenteAutuador = agenteAutuador;
    }

    public List<OcorrenciaAutuacaoRBTrans> getOcorrenciasAutuacao() {
        return ocorrenciasAutuacao;
    }

    public void setOcorrenciasAutuacao(List<OcorrenciaAutuacaoRBTrans> ocorrenciasAutuacao) {
        this.ocorrenciasAutuacao = ocorrenciasAutuacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisica getPessoaClandestina() {
        return pessoaClandestina;
    }

    public void setPessoaClandestina(PessoaFisica pessoaClandestina) {
        this.pessoaClandestina = pessoaClandestina;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public String getDescricaoVeiculo() {
        return descricaoVeiculo;
    }

    public void setDescricaoVeiculo(String descricaoVeiculo) {
        this.descricaoVeiculo = descricaoVeiculo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AutuacaoFiscalizacaoRBTrans)) {
            return false;
        }
        AutuacaoFiscalizacaoRBTrans other = (AutuacaoFiscalizacaoRBTrans) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Código: " + codigo + "- Data: " + DataUtil.getDataFormatada(dataAutuacao) + " - CMC: " + cadastroEconomico.toString() ;
    }

    public OcorrenciaAutuacaoRBTrans getUnicaOcorrencia() {
        try {
            return ocorrenciasAutuacao.get(0);
        } catch (NullPointerException e) {
            return null;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private Date adicionarUmAno(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
        return calendar.getTime();
    }

    public Date getDataValidadePontuacao() {
        return adicionarUmAno(dataAutuacao);
    }

    public boolean ehVigente(Date data) {
        if (data.compareTo(adicionarUmAno(dataAutuacao)) < 0) {
            return true;
        }

        return false;
    }
}
