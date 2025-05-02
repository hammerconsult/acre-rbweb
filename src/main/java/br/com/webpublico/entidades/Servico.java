/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.AnexoLei1232006;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.domain.dtos.ServicoNfseDTO;
import br.com.webpublico.nfse.enums.NfseEnum;
import br.com.webpublico.pessoa.dto.ServicoDTO;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author Jaime
 */
@GrupoDiagrama(nome = "CadastroEconomico")
@Entity

@Audited
@Etiqueta("Serviço")
public class Servico implements NfseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Etiqueta("Código")
    @Tabelavel
    @Obrigatorio
    private String codigo;
    @Etiqueta("Descrição Detalhada")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String nome;
    @Etiqueta("Valor ISS Serviço")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private BigDecimal valorISSServico;
    @Etiqueta("Aliquota ISS Fixo (%)")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private BigDecimal aliquotaISSFixo;
    @Etiqueta("Aliquota ISS Estimado (%)")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private BigDecimal aliquotaISSEstimado;
    @Etiqueta("Alíquota do ISS (%)")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Porcentagem
    private BigDecimal aliquotaISSHomologado;
    @Transient
    private BigDecimal valorBase;
    @Transient
    private BigDecimal faturamento;
    @Etiqueta("Descrição Curta")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    private String descricaoCurta;
    @Transient
    private BigDecimal valorDiferenca;
    @Transient
    private BigDecimal valorCalculado;
    @Transient
    @Invisivel
    private Long criadoEm;
    @Etiqueta("Permite Dedução")
    @Pesquisavel
    @Tabelavel
    private Boolean permiteDeducao;
    @Etiqueta("Percentual de Dedução (%)")
    @Pesquisavel
    @Tabelavel
    @Porcentagem
    private BigDecimal percentualDeducao;
    @Tabelavel
    @Etiqueta("Construção Civil")
    private Boolean construcaoCivil;
    @Etiqueta("Permite Recolhimento Fora")
    @Pesquisavel
    @Tabelavel
    private Boolean permiteRecolhimentoFora;
    @Etiqueta("Exclusivo Simples Nacional")
    private Boolean exclusivoSimplesNacional;
    @Etiqueta("Vetado LC. 116/2003")
    private Boolean vetadoLC1162003;
    @Etiqueta("Liberado Nfse")
    private Boolean liberadoNfse;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ativo")
    private Boolean ativo;
    private Boolean instituicaoFinanceira;
    @ManyToOne
    private AnexoLei1232006 anexoLei1232006;
    private Boolean permiteAlterarAnexoLei1232006;

    @ManyToOne
    private Servico servico;

    @OneToMany(mappedBy = "servico", cascade = CascadeType.ALL)
    private List<Servico> servicos;


    public Servico() {
        super();
        this.faturamento = BigDecimal.ZERO;
        this.valorBase = BigDecimal.ZERO;
        this.aliquotaISSEstimado = BigDecimal.ZERO;
        this.aliquotaISSFixo = BigDecimal.ZERO;
        this.aliquotaISSHomologado = BigDecimal.ZERO;
        this.criadoEm = System.currentTimeMillis();
        this.liberadoNfse = Boolean.TRUE;
        this.ativo = Boolean.TRUE;
        this.instituicaoFinanceira = Boolean.FALSE;
    }

    public Servico(Long id) {
        super();
        this.id = id;
    }

    public BigDecimal getValorCalculado() {
        return valorCalculado;
    }

    public void setValorCalculado(BigDecimal valorCalculado) {
        this.valorCalculado = valorCalculado;
    }

    public BigDecimal getValorDiferenca() {
        return ((this.valorBase.add(valorBase).setScale(2, RoundingMode.HALF_EVEN)));
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Servico(String nome, CategoriaServico categoriaServico, BigDecimal percentualISS, BigDecimal valorISSServico) {
        this.nome = nome;
        this.valorISSServico = valorISSServico;
    }

    public Servico(String codigo, String descricao, BigDecimal aliquota) {
        this.codigo = codigo;
        this.nome = descricao;
        this.aliquotaISSHomologado = aliquota;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getValorISSServico() {
        return valorISSServico;
    }

    public void setValorISSServico(BigDecimal valorISSServico) {
        this.valorISSServico = valorISSServico;
    }

    public BigDecimal getAliquotaISSEstimado() {
        return aliquotaISSEstimado;
    }

    public void setAliquotaISSEstimado(BigDecimal aliquotaISSEstimado) {
        this.aliquotaISSEstimado = aliquotaISSEstimado;
    }

    public BigDecimal getAliquotaISSFixo() {
        return aliquotaISSFixo;
    }

    public void setAliquotaISSFixo(BigDecimal aliquotaISSFixo) {
        this.aliquotaISSFixo = aliquotaISSFixo;
    }

    public BigDecimal getAliquotaISSHomologado() {
        return aliquotaISSHomologado;
    }

    public void setAliquotaISSHomologado(BigDecimal aliquotaISSHomologado) {
        this.aliquotaISSHomologado = aliquotaISSHomologado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getValorBase() {
        return valorBase;
    }

    public void setValorBase(BigDecimal valorBase) {
        this.valorBase = valorBase;
    }

    public String getDescricaoCurta() {
        return descricaoCurta;
    }

    public void setDescricaoCurta(String descricaoCurta) {
        this.descricaoCurta = descricaoCurta;
    }

    public BigDecimal getFaturamento() {
        return faturamento;
    }

    public void setFaturamento(BigDecimal faturamento) {
        this.faturamento = faturamento;
    }

    public Boolean getPermiteDeducao() {
        return permiteDeducao;
    }

    public void setPermiteDeducao(Boolean permiteDeducao) {
        this.permiteDeducao = permiteDeducao;
    }

    public BigDecimal getPercentualDeducao() {
        return percentualDeducao;
    }

    public void setPercentualDeducao(BigDecimal percentualDeducao) {
        this.percentualDeducao = percentualDeducao;
    }

    public void setValorDiferenca(BigDecimal valorDiferenca) {
        this.valorDiferenca = valorDiferenca;
    }

    public Boolean getConstrucaoCivil() {
        return construcaoCivil;
    }

    public void setConstrucaoCivil(Boolean construcaoCivil) {
        this.construcaoCivil = construcaoCivil;
    }

    public Boolean getPermiteRecolhimentoFora() {
        return permiteRecolhimentoFora != null ? permiteRecolhimentoFora : Boolean.FALSE;
    }

    public void setPermiteRecolhimentoFora(Boolean permiteRecolhimentoFora) {
        this.permiteRecolhimentoFora = permiteRecolhimentoFora;
    }

    public Boolean getExclusivoSimplesNacional() {
        return exclusivoSimplesNacional;
    }

    public void setExclusivoSimplesNacional(Boolean exclusivoSimplesNacional) {
        this.exclusivoSimplesNacional = exclusivoSimplesNacional;
    }

    public Boolean getVetadoLC1162003() {
        return vetadoLC1162003;
    }

    public void setVetadoLC1162003(Boolean vetadoLC1162003) {
        this.vetadoLC1162003 = vetadoLC1162003;
    }

    public Boolean getLiberadoNfse() {
        return liberadoNfse;
    }

    public void setLiberadoNfse(Boolean liberadoNfse) {
        this.liberadoNfse = liberadoNfse;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getInstituicaoFinanceira() {
        return instituicaoFinanceira != null ? instituicaoFinanceira : false;
    }

    public void setInstituicaoFinanceira(Boolean instituicaoFinanceira) {
        this.instituicaoFinanceira = instituicaoFinanceira;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
    }

    public AnexoLei1232006 getAnexoLei1232006() {
        return anexoLei1232006;
    }

    public void setAnexoLei1232006(AnexoLei1232006 anexoLei1232006) {
        this.anexoLei1232006 = anexoLei1232006;
    }

    public Boolean getPermiteAlterarAnexoLei1232006() {
        return permiteAlterarAnexoLei1232006;
    }

    public void setPermiteAlterarAnexoLei1232006(Boolean permiteAlterarAnexoLei1232006) {
        this.permiteAlterarAnexoLei1232006 = permiteAlterarAnexoLei1232006;
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
        return nome;
    }

    public String getToStringAutoComplete() {
        if (codigo == null) {
            return "";
        }
        if (nome == null) {
            return codigo + " - ";
        }
        if (nome.length() > 90) {
            StringBuilder sb = new StringBuilder(codigo).append(" - ").append(nome.substring(0, 90)).append("...");
            return sb.toString();
        }
        return codigo + " - " + nome;
    }

    @Override
    public ServicoNfseDTO toNfseDto() {
        ServicoNfseDTO dto = new ServicoNfseDTO();
        dto.setId(this.id);
        dto.setCodigo(this.codigo);
        dto.setDescricao(this.nome);
        dto.setAliquota(this.aliquotaISSHomologado);
        dto.setConstrucaoCivil(this.construcaoCivil);
        dto.setPermiteRecolhimentoFora(this.permiteRecolhimentoFora);
        dto.setPermiteDeducao(this.permiteDeducao);
        dto.setPercentualDeducao(this.percentualDeducao);
        dto.setExclusivoSimplesNacional(this.exclusivoSimplesNacional);
        dto.setVetadoLC1162003(this.vetadoLC1162003);
//        dto.setPermiteExportacao(this.permiteExportacao);
        return dto;
    }

    public boolean hasServico(final Servico servico) {
        if (servicos != null) {
            for (Servico s : servicos) {
                if (s.getId() == servico.getId())
                    return true;
            }
        }
        return false;
    }

    public enum LocalExecucaoTrabalho implements NfseEnum {
        LOCAL("No Local", br.com.webpublico.nfse.domain.dtos.enums.LocalExecucaoTrabalho.LOCAL),
        DOMICIO_PRESTADOR("No domicilio do prestador", br.com.webpublico.nfse.domain.dtos.enums.LocalExecucaoTrabalho.DOMICIO_PRESTADOR),
        ESTABELECIMENTO_PRESTADOR("No estabelecimento do prestador", br.com.webpublico.nfse.domain.dtos.enums.LocalExecucaoTrabalho.ESTABELECIMENTO_PRESTADOR);

        private String descricao;
        private br.com.webpublico.nfse.domain.dtos.enums.LocalExecucaoTrabalho estabelecimentoPrestador;

        LocalExecucaoTrabalho(String descricao, br.com.webpublico.nfse.domain.dtos.enums.LocalExecucaoTrabalho estabelecimentoPrestador) {
            this.descricao = descricao;
            this.estabelecimentoPrestador = estabelecimentoPrestador;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }

        @Override
        public br.com.webpublico.nfse.domain.dtos.enums.LocalExecucaoTrabalho toDto() {
            return estabelecimentoPrestador;
        }
    }


    public ServicoDTO toServicoDTO() {
        return new ServicoDTO(id, codigo, nome, aliquotaISSHomologado, BigDecimal.ZERO);
    }
}
