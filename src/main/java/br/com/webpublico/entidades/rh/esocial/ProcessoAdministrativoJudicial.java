package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UF;
import br.com.webpublico.enums.rh.esocial.*;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mateus on 18/06/18.
 */
@Entity
@Audited
@Table(name = "PROCESSOADMJUDICIAL")
@Etiqueta("Processo Administrativo/Judicial")
public class ProcessoAdministrativoJudicial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Autor/requerente")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private PessoaFisica autorRequerente;
    @ManyToOne
    @Etiqueta("Empregador")
    @Obrigatorio
    private ConfiguracaoEmpregadorESocial configuracaoEmpregador;
    @Etiqueta("Tipo de processo")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private TipoProcessoAdministrativoJudicial tipoProcesso;
    @Obrigatorio
    @Etiqueta("Número do processo")
    @Tabelavel
    @Pesquisavel
    private String numeroProcesso;
    @Obrigatorio
    @Etiqueta("Código da Vara")
    @Pesquisavel
    @Tabelavel
    private String codigoVara;
    @Etiqueta("UF da Vara")
    @Obrigatorio
    @ManyToOne
    @Pesquisavel
    private UF ufVara;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data de Abertura")
    @Pesquisavel
    @Tabelavel
    private Date dataAbertura;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data da decisão/sentença/despacho")
    @Pesquisavel
    private Date dataDecisao;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Encerramento")
    @Pesquisavel
    private Date dataEncerramento;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Indicativo de autoria")
    @Pesquisavel
    @Tabelavel
    private IndicativoAutoria indicativoAutoria;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Indicativo da matéria do processo ou alvará judicial")
    private IndicativoMateriaProcesso indicativoMateriaProcesso;
    @Length(maximo = 255)
    @Obrigatorio
    @Etiqueta("Observações")
    private String observacoes;
    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Município")
    private Cidade municipio;
    @Length(maximo = 14)
    @Obrigatorio
    @Etiqueta("Código da Suspensão")
    private String codigoSuspensao;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Indicativo de suspensão da exigibilidade")
    @Obrigatorio
    @Pesquisavel
    private IndicativoSuspensaoExigibilidade indicativoSuspensaoExigib;
    @Etiqueta("Depósito do Montante Integral")
    private Boolean depositoMontanteIntegral;
    @Etiqueta("Tipo de processo")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private TipoIntegracaoEsocial tipoIntegracao;

    public ProcessoAdministrativoJudicial() {
        super();
        depositoMontanteIntegral = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisica getAutorRequerente() {
        return autorRequerente;
    }

    public void setAutorRequerente(PessoaFisica autorRequerente) {
        this.autorRequerente = autorRequerente;
    }

    public TipoProcessoAdministrativoJudicial getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcessoAdministrativoJudicial tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getCodigoVara() {
        return codigoVara;
    }

    public void setCodigoVara(String codigoVara) {
        this.codigoVara = codigoVara;
    }

    public UF getUfVara() {
        return ufVara;
    }

    public void setUfVara(UF ufVara) {
        this.ufVara = ufVara;
    }

    public Date getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(Date dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public Date getDataDecisao() {
        return dataDecisao;
    }

    public void setDataDecisao(Date dataDecisao) {
        this.dataDecisao = dataDecisao;
    }

    public Date getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(Date dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    public IndicativoAutoria getIndicativoAutoria() {
        return indicativoAutoria;
    }

    public void setIndicativoAutoria(IndicativoAutoria indicativoAutoria) {
        this.indicativoAutoria = indicativoAutoria;
    }

    public ConfiguracaoEmpregadorESocial getConfiguracaoEmpregador() {
        return configuracaoEmpregador;
    }

    public void setConfiguracaoEmpregador(ConfiguracaoEmpregadorESocial configuracaoEmpregador) {
        this.configuracaoEmpregador = configuracaoEmpregador;
    }

    public IndicativoMateriaProcesso getIndicativoMateriaProcesso() {
        return indicativoMateriaProcesso;
    }

    public void setIndicativoMateriaProcesso(IndicativoMateriaProcesso indicativoMateriaProcesso) {
        this.indicativoMateriaProcesso = indicativoMateriaProcesso;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Cidade getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Cidade municipio) {
        this.municipio = municipio;
    }

    public String getCodigoSuspensao() {
        return codigoSuspensao;
    }

    public void setCodigoSuspensao(String codigoSuspensao) {
        this.codigoSuspensao = codigoSuspensao;
    }

    public IndicativoSuspensaoExigibilidade getIndicativoSuspensaoExigib() {
        return indicativoSuspensaoExigib;
    }

    public void setIndicativoSuspensaoExigib(IndicativoSuspensaoExigibilidade indicativoSuspensaoExigib) {
        this.indicativoSuspensaoExigib = indicativoSuspensaoExigib;
    }

    public Boolean getDepositoMontanteIntegral() {
        return depositoMontanteIntegral;
    }

    public void setDepositoMontanteIntegral(Boolean depositoMontanteIntegral) {
        this.depositoMontanteIntegral = depositoMontanteIntegral;
    }

    public TipoIntegracaoEsocial getTipoIntegracao() {
        return tipoIntegracao;
    }

    public void setTipoIntegracao(TipoIntegracaoEsocial tipoIntegracao) {
        this.tipoIntegracao = tipoIntegracao;
    }

    @Override
    public String toString() {
        return numeroProcesso + " - " + autorRequerente;
    }
}
