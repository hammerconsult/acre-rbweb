package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoSubvencao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 27/12/13
 * Time: 16:36
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class SubvencaoEmpresas extends SuperEntidade implements Serializable, Comparable<SubvencaoEmpresas> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal ipo;
    private BigDecimal alunosTransportados;
    private BigDecimal valorSubvencao;
    private BigDecimal valorSubvencionado;
    private BigDecimal saldoSubvencionar;
    private Integer numeroDeAlunos;
    @ManyToOne
    private SubvencaoProcesso subvencaoProcesso;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Enumerated(EnumType.STRING)
    private SituacaoSubvencao situacao;
    @ManyToOne
    private OutorgaIPO outorgaIPO;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "subvencaoEmpresas", orphanRemoval = true)
    private List<SubvencaoParcela> subvencaoParcela;

    @Transient
    private BigDecimal valorQueSobrouDaParcela;
    @Transient
    private Long criadoEm;
    private String migracaoChave;
    @ManyToOne
    private CadastroEconomico empresaDevedora;
    @Transient
    private boolean temDebito;
    @Transient
    private BigDecimal valorParametro;
    @ManyToOne
    private DocumentoOficial documentoOficial;
    @ManyToOne
    private BloqueioOutorga bloqueioOutorga;

    public SubvencaoEmpresas() {
        subvencaoParcela = new ArrayList<>();
        valorQueSobrouDaParcela = BigDecimal.ZERO;
        valorSubvencionado = BigDecimal.ZERO;
        saldoSubvencionar = BigDecimal.ZERO;
        valorSubvencao = BigDecimal.ZERO;
        criadoEm = System.nanoTime();
    }


    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getValorQueSobrouDaParcela() {
        return valorQueSobrouDaParcela;
    }

    public void setValorQueSobrouDaParcela(BigDecimal valorQueSobrouDaParcela) {
        this.valorQueSobrouDaParcela = valorQueSobrouDaParcela;
    }

    public boolean isTemDebito() {
        return temDebito;
    }

    public void setTemDebito(boolean temDebito) {
        this.temDebito = temDebito;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<SubvencaoParcela> getSubvencaoParcela() {
        return subvencaoParcela;
    }

    public void setSubvencaoParcela(List<SubvencaoParcela> subvencaoParcela) {
        this.subvencaoParcela = subvencaoParcela;
    }

    public BigDecimal getIpo() {
        if (ipo != null) {
            return ipo;
        }
        return BigDecimal.ZERO;
    }

    public void setIpo(BigDecimal ipo) {
        this.ipo = ipo;
    }

    public SubvencaoProcesso getSubvencaoProcesso() {
        return subvencaoProcesso;
    }

    public void setSubvencaoProcesso(SubvencaoProcesso subvencaoProcesso) {
        this.subvencaoProcesso = subvencaoProcesso;
    }

    public BigDecimal getAlunosTransportados() {
        if (alunosTransportados != null) {
            return alunosTransportados;
        }
        return BigDecimal.ZERO;
    }

    public void setAlunosTransportados(BigDecimal alunosTransportados) {
        this.alunosTransportados = alunosTransportados;
    }

    public BigDecimal getValorSubvencao() {
        return valorSubvencao != null ? valorSubvencao : BigDecimal.ZERO;
    }

    public void setValorSubvencao(BigDecimal valorSubvencao) {
        this.valorSubvencao = valorSubvencao;
    }

    public BigDecimal getValorSubvencionado() {
        return valorSubvencionado != null ? valorSubvencionado : BigDecimal.ZERO;
    }

    public void setValorSubvencionado(BigDecimal valorSubvencionado) {
        this.valorSubvencionado = valorSubvencionado;
    }

    public BigDecimal getSaldoSubvencionar() {
        return saldoSubvencionar;
    }

    public void setSaldoSubvencionar(BigDecimal saldoSubvencionar) {
        this.saldoSubvencionar = saldoSubvencionar;
    }

    public Integer getNumeroDeAlunos() {
        if (numeroDeAlunos != null) {
            return numeroDeAlunos;
        }
        return 0;
    }

    public void setNumeroDeAlunos(Integer numeroDeAlunos) {
        this.numeroDeAlunos = numeroDeAlunos;
    }

    public SituacaoSubvencao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoSubvencao situacao) {
        this.situacao = situacao;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public CadastroEconomico getEmpresaDevedora() {
        return empresaDevedora;
    }

    public void setEmpresaDevedora(CadastroEconomico empresaDevedora) {
        this.empresaDevedora = empresaDevedora;
    }

    public BigDecimal getValorParametro() {
        if (valorParametro != null) {
            return valorParametro;
        }
        return BigDecimal.ZERO;
    }

    public void setValorParametro(BigDecimal valorParametro) {
        this.valorParametro = valorParametro;
    }

    public OutorgaIPO getOutorgaIPO() {
        return outorgaIPO;
    }

    public void setOutorgaIPO(OutorgaIPO outorgaIPO) {
        this.outorgaIPO = outorgaIPO;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public BloqueioOutorga getBloqueioOutorga() {
        return bloqueioOutorga;
    }

    public void setBloqueioOutorga(BloqueioOutorga bloqueioOutorga) {
        this.bloqueioOutorga = bloqueioOutorga;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(obj, this);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public int compareTo(SubvencaoEmpresas o) {
        try {
            return this.getCadastroEconomico().getInscricaoCadastral().compareTo(o.getCadastroEconomico().getInscricaoCadastral());
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    public BigDecimal totalHonorarios() {
        BigDecimal totalHonorarios = BigDecimal.ZERO;
        for (SubvencaoParcela parcela : this.getSubvencaoParcela()) {
            totalHonorarios = totalHonorarios.add(parcela.getValorHonorarios());
        }
        return totalHonorarios;
    }

    public SubvencaoParcela getUltimaSubvencaoParcela() {
        if (subvencaoParcela != null && !subvencaoParcela.isEmpty()) {
            return subvencaoParcela.get(subvencaoParcela.size() - 1);
        }
        return null;
    }
}
