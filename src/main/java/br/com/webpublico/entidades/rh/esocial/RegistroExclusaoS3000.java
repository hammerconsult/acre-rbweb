package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.rh.esocial.TipoExclusaoEventoFolha;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.interfaces.IHistoricoEsocial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by William on 30/11/2018.
 */
@Entity
@Audited
@Etiqueta("Registro de Exclusão S-3000")
public class RegistroExclusaoS3000 extends SuperEntidade implements IHistoricoEsocial {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo do Arquivo E-SOCIAL")
    @Tabelavel
    @Pesquisavel
    private TipoArquivoESocial tipoArquivoESocial;
    @Etiqueta("Id do XML")
    @Tabelavel
    @Pesquisavel
    private String idXML;
    @Etiqueta("Contrato")
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private VinculoFP vinculoFP;

    @ManyToOne
    private PrestadorServicos prestadorServicos;

    @Etiqueta("Número do Recibo")
    private String recibo;

    @Enumerated(EnumType.STRING)
    private TipoExclusaoEventoFolha tipoExclusaoEventoFolha;

    @Temporal(TemporalType.DATE)
    private Date competencia;

    @ManyToOne
    private Exercicio exercicio;

    @ManyToOne
    private ExclusaoEventoEsocial exclusaoEventoEsocial;

    @ManyToOne
    private Entidade entidade;
    @ManyToOne
    private PessoaFisica pessoaFisica;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoArquivoESocial getTipoArquivoESocial() {
        return tipoArquivoESocial;
    }

    public void setTipoArquivoESocial(TipoArquivoESocial tipoArquivoESocial) {
        this.tipoArquivoESocial = tipoArquivoESocial;
    }

    public String getIdXML() {
        return idXML;
    }

    public void setIdXML(String idXML) {
        this.idXML = idXML;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public ExclusaoEventoEsocial getExclusaoEventoEsocial() {
        return exclusaoEventoEsocial;
    }

    public void setExclusaoEventoEsocial(ExclusaoEventoEsocial exclusaoEventoEsocial) {
        this.exclusaoEventoEsocial = exclusaoEventoEsocial;
    }

    public TipoExclusaoEventoFolha getTipoExclusaoEventoFolha() {
        return tipoExclusaoEventoFolha;
    }

    public void setTipoExclusaoEventoFolha(TipoExclusaoEventoFolha tipoExclusaoEventoFolha) {
        this.tipoExclusaoEventoFolha = tipoExclusaoEventoFolha;
    }

    public Date getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Date competencia) {
        this.competencia = competencia;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getRecibo() {
        return recibo;
    }

    public void setRecibo(String recibo) {
        this.recibo = recibo;
    }

    public PrestadorServicos getPrestadorServicos() {
        return prestadorServicos;
    }

    public void setPrestadorServicos(PrestadorServicos prestadorServicos) {
        this.prestadorServicos = prestadorServicos;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public RegistroExclusaoS3000() {
    }

    public RegistroExclusaoS3000(TipoArquivoESocial tipoArquivoESocial, String idXML, VinculoFP vinculoFP, TipoExclusaoEventoFolha tipoExclusaoEventoFolha, Date competencia, Exercicio exercicio, ExclusaoEventoEsocial exclusaoEventoEsocial, Entidade entidade, String recibo, PrestadorServicos prestadorServicos, PessoaFisica pessoaFisica) {
        super();
        this.tipoArquivoESocial = tipoArquivoESocial;
        this.idXML = idXML;
        this.vinculoFP = vinculoFP;
        this.tipoExclusaoEventoFolha = tipoExclusaoEventoFolha;
        this.competencia = competencia;
        this.exercicio = exercicio;
        this.exclusaoEventoEsocial = exclusaoEventoEsocial;
        this.entidade = entidade;
        this.recibo = recibo;
        this.prestadorServicos = prestadorServicos;
        this.pessoaFisica = pessoaFisica;
    }

    @Override
    public String getDescricaoCompleta() {
        return this.vinculoFP != null ? this.vinculoFP.toString() : this.prestadorServicos != null ? this.prestadorServicos.toString() :
            this.getRecibo() != null ? this.getRecibo() : "";
    }


    @Override
    public String getIdentificador() {
        return this.vinculoFP != null ? this.vinculoFP.getId().toString() : this.prestadorServicos != null ? this.prestadorServicos.getId().toString() : "";
    }
}
