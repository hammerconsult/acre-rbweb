package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoClassificacaoFornecedor;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 10/07/14
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "FORNECEDORDISPLIC")
@Etiqueta("Fornecedor Dispensa de Licitação")
public class FornecedorDispensaDeLicitacao extends SuperEntidade implements ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Dispensa de Licitação")
    @ManyToOne
    private DispensaDeLicitacao dispensaDeLicitacao;

    @Etiqueta("Fornecedor")
    @Obrigatorio
    @ManyToOne
    private Pessoa pessoa;

    @Etiqueta("Proposta Fornecedor Dispensa")
    @ManyToOne(cascade = CascadeType.ALL)
    private PropostaFornecedorDispensa propostaFornecedorDispensa;

    @Etiqueta("Código")
    @Tabelavel
    @Pesquisavel
    private Integer codigo;

    @Etiqueta("Tipo Classificação Fornecedor")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoClassificacaoFornecedor tipoClassificacaoFornecedor;

    @Etiqueta("Justificativa da Classificação")
    @Tabelavel
    @Pesquisavel
    private String justificativaDaClassificacao;

    @Etiqueta("Documentos")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fornecedorDispensa", orphanRemoval = true)
    private List<DocumentoFornecedorDispensaDeLicitacao> documentos;

    public FornecedorDispensaDeLicitacao() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DispensaDeLicitacao getDispensaDeLicitacao() {
        return dispensaDeLicitacao;
    }

    public void setDispensaDeLicitacao(DispensaDeLicitacao dispensaDeLicitacao) {
        this.dispensaDeLicitacao = dispensaDeLicitacao;
    }

    public PropostaFornecedorDispensa getPropostaFornecedorDispensa() {
        return propostaFornecedorDispensa;
    }

    public void setPropostaFornecedorDispensa(PropostaFornecedorDispensa propostaFornecedorDispensa) {
        this.propostaFornecedorDispensa = propostaFornecedorDispensa;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public TipoClassificacaoFornecedor getTipoClassificacaoFornecedor() {
        return tipoClassificacaoFornecedor;
    }

    public void setTipoClassificacaoFornecedor(TipoClassificacaoFornecedor tipoClassificacaoFornecedor) {
        this.tipoClassificacaoFornecedor = tipoClassificacaoFornecedor;
    }

    public String getJustificativaDaClassificacao() {
        return justificativaDaClassificacao;
    }

    public void setJustificativaDaClassificacao(String justificativaDaClassificacao) {
        this.justificativaDaClassificacao = justificativaDaClassificacao;
    }

    public List<DocumentoFornecedorDispensaDeLicitacao> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<DocumentoFornecedorDispensaDeLicitacao> documentos) {
        this.documentos = documentos;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    @Override
    public String toString() {
        try {
            return pessoa.getCpf_Cnpj() + " - " + pessoa.getNome();
        } catch (Exception ex) {
            return super.toString();
        }
    }

    public void processarClassificacaoDesteFornecedor(Date dataOperacao){
        if (documentos == null || documentos.isEmpty()){
            setTipoClassificacaoFornecedor(TipoClassificacaoFornecedor.INABILITADO);
            return;
        }
        for (DocumentoFornecedorDispensaDeLicitacao dfdl : documentos) {
            TipoClassificacaoFornecedor tp = dfdl.getSituacaoDeAcordoComEsteDocumento(dataOperacao);
            if (tp.equals(TipoClassificacaoFornecedor.INABILITADO)){
                setTipoClassificacaoFornecedor(tp);
                break;
            }
            if (tp.equals(TipoClassificacaoFornecedor.HABILITADOCOMRESSALVA)){
                setTipoClassificacaoFornecedor(tp);
                break;
            }
            setTipoClassificacaoFornecedor(tp);
        }
    }

    public boolean isFornecedorInabilitado(){
        return TipoClassificacaoFornecedor.INABILITADO.equals(tipoClassificacaoFornecedor);
    }

    public boolean temDocumentos(){
        return documentos != null && !documentos.isEmpty();
    }

    public boolean isFornecedorHabilitadoComRessalva(){
        return TipoClassificacaoFornecedor.HABILITADOCOMRESSALVA.equals(tipoClassificacaoFornecedor);
    }

    public boolean isFornecedorHabilitado(){
        return TipoClassificacaoFornecedor.HABILITADO.equals(tipoClassificacaoFornecedor);
    }

    public boolean jaLancouPrecoDePeloMenosUmItem() {
        if (this.getPropostaFornecedorDispensa() != null) {
            for (LotePropostaFornecedorDispensa loteProposta : this.getPropostaFornecedorDispensa().getLotesDaProposta()) {
                for (ItemPropostaFornecedorDispensa itemProposta : loteProposta.getItensProposta()) {
                    return itemProposta.isItemComPrecoLancado();
                }
            }
        }
        return false;
    }
}
