/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoValorPensaoAlimenticia;
import br.com.webpublico.enums.rh.dirf.GrauParentescoDirf;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author boy
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Beneficiário de Pensão Alimentícia")
public class BeneficioPensaoAlimenticia extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Beneficiário")
    @ManyToOne
    private PessoaFisica beneficiario;
    @Etiqueta("Responsável")
    @ManyToOne
    private PessoaFisica responsavel;
    @ManyToOne
    private PensaoAlimenticia pensaoAlimenticia;
    @Transient
    private Date dataRegistro;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Início da Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Etiqueta("Final da Vigência")
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @OneToMany(mappedBy = "beneficioPensaoAlimenticia", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("inicioVigencia")
    private List<ValorPensaoAlimenticia> valoresPensaoAlimenticia;
    @ManyToOne
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Tabelavel
    @Etiqueta("Ato Legal")
    @ManyToOne
    private AtoLegal atoLegal;
    @Etiqueta("Tipo do Valor da Pensão")
    @Enumerated(EnumType.STRING)
    private TipoValorPensaoAlimenticia tipoValorPensaoAlimenticia;
    @ManyToOne
    @Etiqueta("Verbas(evento)")
    private EventoFP eventoFP;
    @ManyToOne
    @Etiqueta("Conta Corrente Bancária")
    @Obrigatorio
    private ContaCorrenteBancaria contaCorrenteBancaria;
    @Etiqueta("Grau de Parentesco")
    @Enumerated(EnumType.STRING)
    private GrauParentescoDirf grauParentesco;

    public BeneficioPensaoAlimenticia(PessoaFisica beneficiario, EventoFP eventoFP, GrauParentescoDirf grauParentesco) {
        this.beneficiario = beneficiario;
        this.eventoFP = eventoFP;
        this.grauParentesco = grauParentesco;
    }

    public BeneficioPensaoAlimenticia(PessoaFisica beneficiario, ContaCorrenteBancaria contaCorrenteBancaria) {
        this.beneficiario = beneficiario;
        this.contaCorrenteBancaria = contaCorrenteBancaria;
    }

    public BeneficioPensaoAlimenticia() {
        valoresPensaoAlimenticia = new ArrayList<ValorPensaoAlimenticia>();
        dataRegistro = new Date();
    }

    public List<ValorPensaoAlimenticia> getValoresPensaoAlimenticia() {
        return valoresPensaoAlimenticia;
    }

    public void setValoresPensaoAlimenticia(List<ValorPensaoAlimenticia> valoresPensaoAlimenticia) {
        this.valoresPensaoAlimenticia = valoresPensaoAlimenticia;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public TipoValorPensaoAlimenticia getTipoValorPensaoAlimenticia() {
        return tipoValorPensaoAlimenticia;
    }

    public void setTipoValorPensaoAlimenticia(TipoValorPensaoAlimenticia tipoValorPensaoAlimenticia) {
        this.tipoValorPensaoAlimenticia = tipoValorPensaoAlimenticia;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public PensaoAlimenticia getPensaoAlimenticia() {
        return pensaoAlimenticia;
    }

    public void setPensaoAlimenticia(PensaoAlimenticia pensaoAlimenticia) {
        this.pensaoAlimenticia = pensaoAlimenticia;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public PessoaFisica getPessoaFisicaBeneficiario() {
        return beneficiario;
    }

    public void setPessoaFisicaBeneficiario(PessoaFisica pessoaFisicaBeneficiario) {
        this.beneficiario = pessoaFisicaBeneficiario;
    }

    public PessoaFisica getPessoaFisicaResponsavel() {
        return responsavel;
    }

    public void setPessoaFisicaResponsavel(PessoaFisica pessoaFisicaResponsavel) {
        this.responsavel = pessoaFisicaResponsavel;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public ContaCorrenteBancaria getContaCorrenteBancaria() {
        return contaCorrenteBancaria;
    }

    public void setContaCorrenteBancaria(ContaCorrenteBancaria contaCorrenteBancaria) {
        this.contaCorrenteBancaria = contaCorrenteBancaria;
    }

    @Override
    public String toString() {
        return getPessoaFisicaBeneficiario().getNome() + " (" + getPessoaFisicaBeneficiario().getNomeTratamento().trim() + ")";
    }

    public boolean isPensaoValorFixo() {
        return this.tipoValorPensaoAlimenticia != null && TipoValorPensaoAlimenticia.VALOR_FIXO.equals(this.tipoValorPensaoAlimenticia);
    }

    public boolean isPensaoPercentualSobreSalarioMinimo() {
        return this.tipoValorPensaoAlimenticia != null && TipoValorPensaoAlimenticia.PERCENTUAL_SOBRE_SALARIO_MINIMO.equals(this.tipoValorPensaoAlimenticia);
    }

    public boolean isPensaoSalarioMinimoIntegral() {
        return this.tipoValorPensaoAlimenticia != null && TipoValorPensaoAlimenticia.SALARIO_MINIMO_INTEGRAL.equals(this.tipoValorPensaoAlimenticia);
    }

    public boolean isPensaoValorBase() {
        return this.tipoValorPensaoAlimenticia != null && (TipoValorPensaoAlimenticia.BASE.equals(this.tipoValorPensaoAlimenticia) || TipoValorPensaoAlimenticia.BASE_BRUTO.equals(this.tipoValorPensaoAlimenticia) || TipoValorPensaoAlimenticia.BASE_SOBRE_LIQUIDO.equals(this.tipoValorPensaoAlimenticia));
    }

    public GrauParentescoDirf getGrauParentesco() {
        return grauParentesco;
    }

    public void setGrauParentesco(GrauParentescoDirf grauParentesco) {
        this.grauParentesco = grauParentesco;
    }
}
