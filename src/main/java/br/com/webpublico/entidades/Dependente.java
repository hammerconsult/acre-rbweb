/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoParentescoRPPS;
import br.com.webpublico.enums.rh.estudoatuarial.TipoDependenciaEstudoAtuarial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.pessoa.dto.DependenteDTO;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
public class Dependente extends SuperEntidade implements PossuidorArquivo {

    protected static final Logger logger = LoggerFactory.getLogger(Dependente.class);

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Responsável")
    @Obrigatorio
    @Tabelavel
    private PessoaFisica responsavel;
    @Pesquisavel
    @Etiqueta("Dependente")
    @ManyToOne(cascade = CascadeType.MERGE)
    @Obrigatorio
    @Tabelavel
    private PessoaFisica dependente;
    @ManyToOne
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Grau de Parentesco")
    private GrauDeParentesco grauDeParentesco;
    @Etiqueta("Dependente Vínculo FP")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dependente", orphanRemoval = true)
    @OrderBy("inicioVigencia desc")
    private List<DependenteVinculoFP> dependentesVinculosFPs = new ArrayList<>();
    @Etiqueta("Tipo de Dependência RPPS")
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoParentescoRPPS tipoParentescoRPPS;
    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;
    private Boolean ativo;

    public static List<DependenteDTO> toDependentesDTO(List<Dependente> dependentes) {
        if (dependentes == null) {
            return null;
        }
        List<DependenteDTO> dtos = Lists.newLinkedList();
        for (Dependente depende : dependentes) {
            DependenteDTO dto = toDependenteDTO(depende);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    private static DependenteDTO toDependenteDTO(Dependente dep) {
        if (dep == null) {
            return null;
        }
        DependenteDTO dto = new DependenteDTO();
        dto.setId(dep.getId());
        dto.setNome(dep.getDependente() != null ? dep.getDependente().getNome() : null);
        dto.setNomeMae(dep.getDependente() != null ? dep.getDependente().getMae() : null);
        dto.setNomePai(dep.getDependente() != null ? dep.getDependente().getPai() : null);
        dto.setPessoaId(dep.getDependente() != null ? dep.getDependente().getId() : null);
        dto.setDependentesVinculoPortalDto(TipoDependente.toTipoDependentesDTO(dep.getDependentesVinculosFPs()));
        dto.setGrauDeParentescoDTO(GrauDeParentesco.toGrauDeParentesco(dep.getGrauDeParentesco()));
        dto.setRg(RG.toRGDTO(dep.getDependente().getRg()));
        dto.setCpf(dep.getDependente().getCpf());
        dto.setDataNascimento(dep.getDependente().getDataNascimento());
        dto.setSexo(dep.getDependente().getSexo() != null ? br.com.webpublico.pessoa.enumeration.Sexo.valueOf(dep.getDependente().getSexo().name()) : null);
        dto.setEstadoCivil(dep.getDependente().getEstadoCivil() != null ? br.com.webpublico.pessoa.enumeration.EstadoCivil.valueOf(dep.getDependente().getEstadoCivil().name()) : null);
        dto.setPortadorNecessidadeEspecial(dep.getDependente().getDeficienteFisico());
        dto.setInativarDependente(false);
        dto.setTituloEleitor(TituloEleitor.toTituloEleitorDTO(dep.getDependente().getTituloEleitor()));
        dto.setHabilitacao(dep.getDependente().getHabilitacao() != null ? Habilitacao.toHabilitacoesDTO(Arrays.asList(dep.getDependente().getHabilitacao())) : null);
        dto.setCarteiraTrabalho(CarteiraTrabalho.toCarteiraTrabalhoDTO(dep.getDependente().getCarteiraDeTrabalho()));
        dto.setSituacaoMilitar(dep.getDependente().getSituacaoMilitar() != null ? SituacaoMilitar.toSituacaoMilitarDTO(dep.getDependente().getSituacaoMilitar()) : null);
        dto.setCertidaoNascimento(CertidaoNascimento.toCertidaoNascimentoDTO(dep.getDependente().getCertidaoNascimento()));
        dto.setCertidaoCasamento(CertidaoCasamento.toCertidaoCasamento(dep.getDependente().getCertidaoCasamento()));
        dto.setTelefones(Telefone.toTelefones(dep.getDependente().getTelefones()));
        dto.setEnderecos(EnderecoCorreio.toEnderecoCorreioDTOs(dep.getDependente().getEnderecos()));
        dto.setAlterado(false);
        dto.setNaturalidade(Cidade.toCidadeDTO(dep.getDependente().getNaturalidade()));
        return dto;
    }

    public static Dependente dtoToDependente(DependenteDTO dto) {
        Dependente dependente = new Dependente();
        dependente.setId(dto.getId());
        return dependente;
    }

    public Boolean getAtivo() {
        return ativo == null ? true : ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public PessoaFisica getDependente() {
        return dependente;
    }

    public void setDependente(PessoaFisica dependente) {
        this.dependente = dependente;
    }

    public GrauDeParentesco getGrauDeParentesco() {
        return grauDeParentesco;
    }

    public void setGrauDeParentesco(GrauDeParentesco grauDeParentesco) {
        this.grauDeParentesco = grauDeParentesco;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        this.responsavel = responsavel;
    }

    public List<DependenteVinculoFP> getDependentesVinculosFPs() {
        return dependentesVinculosFPs;
    }

    public void setDependentesVinculosFPs(List<DependenteVinculoFP> dependentesVinculosFPs) {
        this.dependentesVinculosFPs = dependentesVinculosFPs;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public List<DependenteVinculoFP> getDependentesVinculosFPsPorTipoDependente(TipoDependente tipoDependente) {
        List<DependenteVinculoFP> retorno = Lists.newArrayList();
        for (DependenteVinculoFP dependentesVinculosFP : dependentesVinculosFPs) {
            if (tipoDependente.equals(dependentesVinculosFP.getTipoDependente())) {
                retorno.add(dependentesVinculosFP);
            }
        }
        return retorno;
    }

    public DependenteVinculoFP getDependentesVinculosFPsVigente(Date dataOperacao) {
        try {
            if (this.getDependentesVinculosFPs() != null && !this.getDependentesVinculosFPs().isEmpty()) {
                for (DependenteVinculoFP dependenteVinculoFP : this.getDependentesVinculosFPs()) {
                    if (dependenteVinculoFP.getInicioVigencia() == null) {
                        return null;
                    }
                    if (dependenteVinculoFP.getFinalVigencia() == null) {
                        return dependenteVinculoFP;
                    }
                    if (dependenteVinculoFP.getInicioVigencia() != null && dependenteVinculoFP.getFinalVigencia() != null && dependenteVinculoFP.getInicioVigencia().after(dependenteVinculoFP.getFinalVigencia())) {

                        return null;
                    }
                    if (new Interval(new DateTime(dependenteVinculoFP.getInicioVigencia()), new DateTime(dependenteVinculoFP.getFinalVigencia())).contains(new DateTime(dataOperacao))) {
                        return dependenteVinculoFP;
                    }
                    if (new Interval(new DateTime(dependenteVinculoFP.getInicioVigencia()), new DateTime(dependenteVinculoFP.getFinalVigencia())).contains(new DateTime(dataOperacao))) {
                        return dependenteVinculoFP;
                    }
                }
            }
            return null;
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public TipoParentescoRPPS getTipoParentescoRPPS() {
        return tipoParentescoRPPS;
    }

    public void setTipoParentescoRPPS(TipoParentescoRPPS tipoParentescoRPPS) {
        this.tipoParentescoRPPS = tipoParentescoRPPS;
    }

    @Override
    public String toString() {
        return responsavel + " - " + dependente + " - " + grauDeParentesco;
    }
}
