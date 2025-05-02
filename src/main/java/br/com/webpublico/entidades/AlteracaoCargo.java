package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 13/07/15
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Alteração Cargo")
public class AlteracaoCargo extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data da Alteração")
    @Temporal(TemporalType.DATE)
    private Date data;

    @Pesquisavel
    @Etiqueta("Provimento")
    @OneToOne(cascade = CascadeType.ALL)
    private ProvimentoFP provimentoFP;

    @OneToOne(cascade = CascadeType.ALL)
    private ContratoFPCargo contratoFPCargo;

    @OneToOne(cascade = CascadeType.ALL)
    private LotacaoFuncional lotacaoFuncional;

    @OneToOne(cascade = CascadeType.ALL)
    private EnquadramentoFuncional enquadramentoFuncional;

    @Etiqueta("Servidor")
    @Tabelavel
    @Transient
    private ContratoFP contratoFP;
    @Etiqueta("Cargo Atual")
    @Tabelavel
    @Transient
    private String descricaoCargo;
    @Etiqueta("Unidade Organizacional")
    @Tabelavel
    @Transient
    private UnidadeOrganizacional unidadeOrganizacional;

    public AlteracaoCargo() {
        super();
        this.provimentoFP = new ProvimentoFP();
    }

    public AlteracaoCargo(AlteracaoCargo entity, UnidadeOrganizacional unidadeOrganizacional) {
        setId(entity.getId());
        setData(entity.getData());
        setContratoFP(entity.getContratoFPCargo().getContratoFP());
        setDescricaoCargo(entity.getContratoFPCargo().getCargo().getDescricao());
        setUnidadeOrganizacional(unidadeOrganizacional);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public ContratoFPCargo getContratoFPCargo() {
        return contratoFPCargo;
    }

    public void setContratoFPCargo(ContratoFPCargo contratoFPCargo) {
        this.contratoFPCargo = contratoFPCargo;
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public EnquadramentoFuncional getEnquadramentoFuncional() {
        return enquadramentoFuncional;
    }

    public void setEnquadramentoFuncional(EnquadramentoFuncional enquadramentoFuncional) {
        this.enquadramentoFuncional = enquadramentoFuncional;
    }

    public boolean temCargo() {
        return this.getContratoFPCargo() != null;
    }

    public boolean temLotacaoFuncional() {
        return this.getLotacaoFuncional() != null;
    }

    public boolean temEnquadramentoFuncional() {
        return this.getEnquadramentoFuncional() != null;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public String getDescricaoCargo() {
        return descricaoCargo;
    }

    public void setDescricaoCargo(String descricaoCargo) {
        this.descricaoCargo = descricaoCargo;
    }
}
