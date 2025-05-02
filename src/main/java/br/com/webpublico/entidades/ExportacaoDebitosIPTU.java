package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoExportacaoDebitosIPTU;
import br.com.webpublico.enums.TipoArquivoExportacaoDebitosIPTU;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author octavio
 */
@Entity
@Audited
@Etiqueta("Exportação de Débitos IPTU")
public class ExportacaoDebitosIPTU extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Convênio de Lista de Débitos")
    private ConvenioListaDebitos convenioListaDebitos;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Responsável")
    private UsuarioSistema responsavel;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Geração")
    @Pesquisavel
    @Tabelavel
    private Date dataGeracao;
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Arquivo Exportação")
    private TipoArquivoExportacaoDebitosIPTU tipoArqExportacaoDebitosIPTU;
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Etiqueta("Inscrição Inicial")
    private String inscricaoInicial;
    @Etiqueta("Inscrição Final")
    private String inscricaoFinal;
    @Etiqueta("Vencimento Inicial")
    private Date vencimentoIncial;
    @Etiqueta("Vencimento Final")
    private Date vencimentoFinal;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número Remessa")
    private Long numeroRemessa;
    @ManyToOne
    private Arquivo arquivo;
    @Enumerated(value = EnumType.STRING)
    private SituacaoExportacaoDebitosIPTU situacaoExportacaoDebitosIPTU;
    @Transient
    private List<ExportacaoDebitosIPTULinha> linhas;

    public ExportacaoDebitosIPTU() {
        this.linhas = Lists.newArrayList();
    }

    public List<ExportacaoDebitosIPTULinha> getLinhas() {
        return linhas;
    }

    public void setLinhas(List<ExportacaoDebitosIPTULinha> linhas) {
        this.linhas = linhas;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConvenioListaDebitos getConvenioListaDebitos() {
        return convenioListaDebitos;
    }

    public void setConvenioListaDebitos(ConvenioListaDebitos convenioListaDebitos) {
        this.convenioListaDebitos = convenioListaDebitos;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public TipoArquivoExportacaoDebitosIPTU getTipoArqExportacaoDebitosIPTU() {
        return tipoArqExportacaoDebitosIPTU;
    }

    public void setTipoArqExportacaoDebitosIPTU(TipoArquivoExportacaoDebitosIPTU tipoArqExportacaoDebitosIPTU) {
        this.tipoArqExportacaoDebitosIPTU = tipoArqExportacaoDebitosIPTU;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public Date getVencimentoIncial() {
        return vencimentoIncial;
    }

    public void setVencimentoIncial(Date vencimentoIncial) {
        this.vencimentoIncial = vencimentoIncial;
    }

    public Date getVencimentoFinal() {
        return vencimentoFinal;
    }

    public void setVencimentoFinal(Date vencimentoFinal) {
        this.vencimentoFinal = vencimentoFinal;
    }

    public Long getNumeroRemessa() {
        return numeroRemessa;
    }

    public void setNumeroRemessa(Long numeroRemessa) {
        this.numeroRemessa = numeroRemessa;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public SituacaoExportacaoDebitosIPTU getSituacaoExportacaoDebitosIPTU() {
        return situacaoExportacaoDebitosIPTU;
    }

    public void setSituacaoExportacaoDebitosIPTU(SituacaoExportacaoDebitosIPTU situacaoExportacaoDebitosIPTU) {
        this.situacaoExportacaoDebitosIPTU = situacaoExportacaoDebitosIPTU;
    }
}
