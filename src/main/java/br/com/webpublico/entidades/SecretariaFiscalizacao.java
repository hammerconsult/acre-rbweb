/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Renato
 */
@Entity

@Audited
@Etiqueta("Secretaria de Fiscalização")
@GrupoDiagrama(nome = "fiscalizacaogeral")
public class SecretariaFiscalizacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Código")
    @Pesquisavel
    @Obrigatorio
    private Long codigo;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Secretaria")
    @Tabelavel
    @Obrigatorio
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Departamento")
    private UnidadeOrganizacional departamento;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Responsável")
    @Obrigatorio
    private PessoaFisica pessoaFisica;
    @Obrigatorio
    @Etiqueta("Prazo Termo de Advertência (Dias)")
    private Integer prazoRecurso;
    @Obrigatorio
    @Etiqueta("Prazo Auto Infração (Dias)")
    private Integer prazoAutoInfracao;
    @Obrigatorio
    @Etiqueta("Prazo 1ª Instância")
    private Integer prazoPrimeiraInstancia;
    @Obrigatorio
    @Etiqueta("Prazo 2ª Instância")
    private Integer prazoSegundaInstancia;
    @Obrigatorio
    @Etiqueta("Prazo Vencimento do DAM")
    private Integer vencimentoDam;
    @Obrigatorio
    @Etiqueta("Prazo de Reincidência Genérica (Anos)")
    private Integer prazoReincidenciaGenerica;
    @Obrigatorio
    @Etiqueta("Prazo de Reincidência Específica (Anos)")
    private Integer prazoReincidenciaEspecifica;
    @Obrigatorio
    @Etiqueta("Prazo da Secretaria")
    private Integer prazoPrefeitura;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Dívida do Cadastro Imobiliário")
    private Divida dividaImobiliario;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Dívida do Cadastro Econômico")
    private Divida dividaEconomico;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Dívida do Cadastro Rural")
    private Divida dividaRural;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Dívida da Pessoa")
    private Divida dividaPessoa;
    @Transient
    @Invisivel
    private Long criadoEm;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Termo de Advertência/Notificação do Cadastro Imobiliário")
    private TipoDoctoOficial termoImobiliario;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Termo de Advertência/Notificação do Cadastro Econômico")
    private TipoDoctoOficial termoEconomico;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Termo de Advertência/Notificação do Cadastro Rural")
    private TipoDoctoOficial termoRural;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Termo de Advertência/Notificação da Pessoa Física")
    private TipoDoctoOficial termoPessoaFisica;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Termo de Advertência/Notificação da Pessoa Jurídica")
    private TipoDoctoOficial termoPessoaJuridica;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Auto de Infração do Cadastro Imobiliário")
    private TipoDoctoOficial autoImobiliario;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Auto de Infração do Cadastro Econômico")
    private TipoDoctoOficial autoEconomico;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Auto de Infração do Cadastro Rural")
    private TipoDoctoOficial autoRural;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Auto de Infração da Pessoa Física")
    private TipoDoctoOficial autoPessoaFisica;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Auto de Infração da Pessoa Jurídica")
    private TipoDoctoOficial autoPessoaJuridica;

    public SecretariaFiscalizacao() {
        this.criadoEm = System.nanoTime();
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public UnidadeOrganizacional getDepartamento() {
        return departamento;
    }

    public void setDepartamento(UnidadeOrganizacional departamento) {
        this.departamento = departamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Integer getPrazoAutoInfracao() {
        return prazoAutoInfracao;
    }

    public void setPrazoAutoInfracao(Integer prazoAutoInfracao) {
        this.prazoAutoInfracao = prazoAutoInfracao;
    }

    public Integer getPrazoPrimeiraInstancia() {
        return prazoPrimeiraInstancia;
    }

    public void setPrazoPrimeiraInstancia(Integer prazoPrimeiraInstancia) {
        this.prazoPrimeiraInstancia = prazoPrimeiraInstancia;
    }

    public Integer getPrazoRecurso() {
        return prazoRecurso;
    }

    public void setPrazoRecurso(Integer prazoRecurso) {
        this.prazoRecurso = prazoRecurso;
    }

    public Integer getPrazoReincidenciaEspecifica() {
        return prazoReincidenciaEspecifica;
    }

    public void setPrazoReincidenciaEspecifica(Integer prazoReincidenciaEspecifica) {
        this.prazoReincidenciaEspecifica = prazoReincidenciaEspecifica;
    }

    public Integer getPrazoReincidenciaGenerica() {
        return prazoReincidenciaGenerica;
    }

    public void setPrazoReincidenciaGenerica(Integer prazoReincidenciaGenerica) {
        this.prazoReincidenciaGenerica = prazoReincidenciaGenerica;
    }

    public Integer getPrazoSegundaInstancia() {
        return prazoSegundaInstancia;
    }

    public void setPrazoSegundaInstancia(Integer prazoSegundaInstancia) {
        this.prazoSegundaInstancia = prazoSegundaInstancia;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        if (this.hierarquiaOrganizacional != null) {
            setUnidadeOrganizacional(this.hierarquiaOrganizacional.getSubordinada());
        }
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Integer getVencimentoDam() {
        return vencimentoDam;
    }

    public void setVencimentoDam(Integer vencimentoDam) {
        this.vencimentoDam = vencimentoDam;
    }

    public Integer getPrazoPrefeitura() {
        return prazoPrefeitura;
    }

    public void setPrazoPrefeitura(Integer prazoPrefeitura) {
        this.prazoPrefeitura = prazoPrefeitura;
    }

    public Divida getDividaEconomico() {
        return dividaEconomico;
    }

    public void setDividaEconomico(Divida dividaEconomico) {
        this.dividaEconomico = dividaEconomico;
    }

    public Divida getDividaImobiliario() {
        return dividaImobiliario;
    }

    public void setDividaImobiliario(Divida dividaImobiliario) {
        this.dividaImobiliario = dividaImobiliario;
    }

    public Divida getDividaPessoa() {
        return dividaPessoa;
    }

    public void setDividaPessoa(Divida dividaPessoa) {
        this.dividaPessoa = dividaPessoa;
    }

    public Divida getDividaRural() {
        return dividaRural;
    }

    public void setDividaRural(Divida dividaRural) {
        this.dividaRural = dividaRural;
    }

    public TipoDoctoOficial getTermoImobiliario() {
        return termoImobiliario;
    }

    public void setTermoImobiliario(TipoDoctoOficial termoImobiliario) {
        this.termoImobiliario = termoImobiliario;
    }

    public TipoDoctoOficial getTermoEconomico() {
        return termoEconomico;
    }

    public void setTermoEconomico(TipoDoctoOficial termoEconomico) {
        this.termoEconomico = termoEconomico;
    }

    public TipoDoctoOficial getTermoRural() {
        return termoRural;
    }

    public void setTermoRural(TipoDoctoOficial termoRural) {
        this.termoRural = termoRural;
    }

    public TipoDoctoOficial getTermoPessoaFisica() {
        return termoPessoaFisica;
    }

    public void setTermoPessoaFisica(TipoDoctoOficial termoPessoaFisica) {
        this.termoPessoaFisica = termoPessoaFisica;
    }

    public TipoDoctoOficial getTermoPessoaJuridica() {
        return termoPessoaJuridica;
    }

    public void setTermoPessoaJuridica(TipoDoctoOficial termoPessoaJuridica) {
        this.termoPessoaJuridica = termoPessoaJuridica;
    }

    public TipoDoctoOficial getAutoImobiliario() {
        return autoImobiliario;
    }

    public void setAutoImobiliario(TipoDoctoOficial autoImobiliario) {
        this.autoImobiliario = autoImobiliario;
    }

    public TipoDoctoOficial getAutoEconomico() {
        return autoEconomico;
    }

    public void setAutoEconomico(TipoDoctoOficial autoEconomico) {
        this.autoEconomico = autoEconomico;
    }

    public TipoDoctoOficial getAutoRural() {
        return autoRural;
    }

    public void setAutoRural(TipoDoctoOficial autoRural) {
        this.autoRural = autoRural;
    }

    public TipoDoctoOficial getAutoPessoaFisica() {
        return autoPessoaFisica;
    }

    public void setAutoPessoaFisica(TipoDoctoOficial autoPessoaFisica) {
        this.autoPessoaFisica = autoPessoaFisica;
    }

    public TipoDoctoOficial getAutoPessoaJuridica() {
        return autoPessoaJuridica;
    }

    public void setAutoPessoaJuridica(TipoDoctoOficial autoPessoaJuridica) {
        this.autoPessoaJuridica = autoPessoaJuridica;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return codigo + " - " + unidadeOrganizacional.getDescricao();
    }
}
