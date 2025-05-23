/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.esocial.CategoriaTrabalhador;
import br.com.webpublico.enums.rh.esocial.TipoOperacaoESocial;
import br.com.webpublico.enums.rh.esocial.TipoUnidadePagamento;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import br.com.webpublico.interfaces.FolhaCalculavel;
import br.com.webpublico.interfaces.IHistoricoEsocial;
import br.com.webpublico.util.anotacoes.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Audited
@Etiqueta("Prestador de Serviços")
@GrupoDiagrama(nome = "RecursosHumanos")
public class PrestadorServicos implements Serializable, EntidadePagavelRH, IHistoricoEsocial {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta("Prestador")
    @Tabelavel
    @ManyToOne
    private Pessoa prestador;
    @Obrigatorio
    @Etiqueta("CBO")
    @ManyToOne
    private CBO cbo;
    @ManyToOne
    private Cargo cargo;
    @Etiqueta("Retençao IRRF")
    @ManyToOne
    private RetencaoIRRF retencaoIRRF;
    @Pesquisavel
    @Etiqueta("Ocorrência SEFIP")
    @ManyToOne
    private OcorrenciaSEFIP ocorrenciaSEFIP;
    @Pesquisavel
    @Etiqueta("Categoria SEFIP")
    @ManyToOne
    private CategoriaSEFIP categoriaSEFIP;
    @Etiqueta("Código")
    @Tabelavel
    private Long codigo;
    @ManyToOne
    @Etiqueta("Classificação do Credor")
    @Tabelavel
    private ClassificacaoCredor classificacaoCredor;
    @ManyToOne
    @Etiqueta("Categoria do Trabalhador")
    private CategoriaTrabalhador categoriaTrabalhador;
    @ManyToOne
    private UnidadeOrganizacional lotacao;
    @Temporal(TemporalType.DATE)
    private Date inicioLotacao;
    @Temporal(TemporalType.DATE)
    private Date finalLotacao;
    private String numeroContrato;
    private BigDecimal valorTotal;
    @Enumerated(EnumType.STRING)
    private TipoUnidadePagamento unidadePagamento;
    private String descricaoSalarioVariavel;
    private BigDecimal valorParcelaFixa;
    @Temporal(TemporalType.DATE)
    private Date inicioVigenciaContrato;
    @Temporal(TemporalType.DATE)
    private Date finalVigenciaContrato;
    @Enumerated(EnumType.STRING)
    private VinculoFP.TipoCadastroInicialVinculoFP cadastroInicialEsocial;
    @Invisivel
    @Transient
    private FolhaCalculavel folha;
    @Invisivel
    @Transient
    private Integer ano;
    @Invisivel
    @Transient
    private Integer mes;
    @Transient
    @Invisivel
    private boolean primeiroContrato;
    @Transient
    private boolean calculandoRetroativo;
    @Etiqueta("Data de Cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;
    @Etiqueta("Matrícula e-Social")
    private String matriculaESocial;
    @Transient
    private List<EventoESocialDTO> eventosEsocial;

    @Enumerated
    @Transient
    private TipoOperacaoESocial tipoOperacaoESocial;

    @Override
    public boolean isCalculandoRetroativo() {
        return calculandoRetroativo;
    }

    @Override
    public void setCalculandoRetroativo(boolean calculandoRetroativo) {
        this.calculandoRetroativo = calculandoRetroativo;
    }

    @Override
    public Date getFinalVigencia() {
        return null;
    }


    @Override
    public FolhaCalculavel getFolha() {
        return folha;
    }

    @Override
    public ContratoFP getContratoFP() {
        return null;
    }

    @Override
    public Integer getAno() {
        return ano;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer getMes() {
        return mes;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FichaFinanceiraFP getFicha() {
        return null;
    }

    @Override
    public boolean getPrimeiroContrato() {
        return primeiroContrato;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPrimeiroContrato(boolean primeiroContrato) {
        this.primeiroContrato = primeiroContrato;
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void setFolha(FolhaCalculavel folhaDePagamento) {
        folha = folhaDePagamento;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setFicha(FichaFinanceiraFP ficha) {

    }

    @Override
    public void setAno(Integer ano) {
        this.ano = ano;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setMes(Integer mes) {
        this.mes = mes;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public CategoriaSEFIP getCategoriaSEFIP() {
        return categoriaSEFIP;
    }

    public void setCategoriaSEFIP(CategoriaSEFIP categoriaSEFIP) {
        this.categoriaSEFIP = categoriaSEFIP;
    }

    @Override
    public CBO getCbo() {
        return cbo;
    }

    public void setCbo(CBO cbo) {
        this.cbo = cbo;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public OcorrenciaSEFIP getOcorrenciaSEFIP() {
        return ocorrenciaSEFIP;
    }

    public void setOcorrenciaSEFIP(OcorrenciaSEFIP ocorrenciaSEFIP) {
        this.ocorrenciaSEFIP = ocorrenciaSEFIP;
    }

    public Pessoa getPrestador() {
        return prestador;
    }

    public void setPrestador(Pessoa prestador) {
        this.prestador = prestador;
    }

    public RetencaoIRRF getRetencaoIRRF() {
        return retencaoIRRF;
    }

    public void setRetencaoIRRF(RetencaoIRRF retencaoIRRF) {
        this.retencaoIRRF = retencaoIRRF;
    }

    public VinculoFP.TipoCadastroInicialVinculoFP getCadastroInicialEsocial() {
        return cadastroInicialEsocial;
    }

    public void setCadastroInicialEsocial(VinculoFP.TipoCadastroInicialVinculoFP cadastroInicialEsocial) {
        this.cadastroInicialEsocial = cadastroInicialEsocial;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PrestadorServicos)) {
            return false;
        }
        PrestadorServicos other = (PrestadorServicos) object;
        if ((id == null && other.id != null) || (id != null && !id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public ClassificacaoCredor getClassificacaoCredor() {
        return classificacaoCredor;
    }

    public void setClassificacaoCredor(ClassificacaoCredor classificacaoCredor) {
        this.classificacaoCredor = classificacaoCredor;
    }

    @Override
    public String toString() {
        return prestador != null ? prestador.toString() + " - " + codigo : "";
    }

    @Override
    public MatriculaFP getMatriculaFP() {
        return new MatriculaFP(null, (PessoaFisica) prestador);
    }

    @Override
    public Long getIdCalculo() {
        return id;
    }

    public Date getInicioLotacao() {
        return inicioLotacao;
    }

    public void setInicioLotacao(Date inicioLotacao) {
        this.inicioLotacao = inicioLotacao;
    }

    public Date getFinalLotacao() {
        return finalLotacao;
    }

    public void setFinalLotacao(Date finalLotacao) {
        this.finalLotacao = finalLotacao;
    }

    public CategoriaTrabalhador getCategoriaTrabalhador() {
        return categoriaTrabalhador;
    }

    public void setCategoriaTrabalhador(CategoriaTrabalhador categoriaTrabalhador) {
        this.categoriaTrabalhador = categoriaTrabalhador;
    }

    @Override
    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public UnidadeOrganizacional getLotacao() {
        return lotacao;
    }

    public void setLotacao(UnidadeOrganizacional lotacao) {
        this.lotacao = lotacao;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorParcelaFixa() {
        return valorParcelaFixa;
    }

    public void setValorParcelaFixa(BigDecimal valorParcelaFixa) {
        this.valorParcelaFixa = valorParcelaFixa;
    }

    public Date getInicioVigenciaContrato() {
        return inicioVigenciaContrato;
    }

    public void setInicioVigenciaContrato(Date inicioVigenciaContrato) {
        this.inicioVigenciaContrato = inicioVigenciaContrato;
    }

    public Date getFinalVigenciaContrato() {
        return finalVigenciaContrato;
    }

    public void setFinalVigenciaContrato(Date finalVigenciaContrato) {
        this.finalVigenciaContrato = finalVigenciaContrato;
    }

    public boolean isPrimeiroContrato() {
        return primeiroContrato;
    }

    public TipoUnidadePagamento getUnidadePagamento() {
        return unidadePagamento;
    }

    public void setUnidadePagamento(TipoUnidadePagamento unidadePagamento) {
        this.unidadePagamento = unidadePagamento;
    }

    public String getDescricaoSalarioVariavel() {
        return descricaoSalarioVariavel;
    }

    public void setDescricaoSalarioVariavel(String descricaoSalarioVariavel) {
        this.descricaoSalarioVariavel = descricaoSalarioVariavel;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getMatriculaESocial() {
        return matriculaESocial;
    }

    public void setMatriculaESocial(PessoaFisica pessoaFisica) {
        if (Objects.equals(prestador.getId(), pessoaFisica.getId())) {
            String cpf = pessoaFisica.getCpf().replaceAll("[^0-9]","");
        matriculaESocial = StringUtils.leftPad(cpf, 11, "0") +
            StringUtils.leftPad(codigo.toString(), 6, "0");
        }
    }

    public List<EventoESocialDTO> getEventosEsocial() {
        return eventosEsocial;
    }

    public void setEventosEsocial(List<EventoESocialDTO> eventosEsocial) {
        this.eventosEsocial = eventosEsocial;
    }

    public TipoOperacaoESocial getTipoOperacaoESocial() {
        return tipoOperacaoESocial;
    }

    public void setTipoOperacaoESocial(TipoOperacaoESocial tipoOperacaoESocial) {
        this.tipoOperacaoESocial = tipoOperacaoESocial;
    }

    @Override
    public String getDescricaoCompleta() {
        return toString();
    }

    @Override
    public String getIdentificador() {
        return this.id.toString();
    }
}
