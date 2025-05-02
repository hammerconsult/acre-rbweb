package br.com.webpublico.entidades.rh.cadastrofuncional;

import br.com.webpublico.entidades.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by peixe on 07/02/18.
 */
@Entity

@Etiqueta("Enquadramento Funcional Por Lote")
@GrupoDiagrama(nome = "Recursos Humanos")
@Audited
public class EnquadramentoFuncionalLote implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private AtoLegal atoLegal;
    @ManyToOne
    private PlanoCargosSalarios planoCargosSalariosOrigem;
    @ManyToOne
    private CategoriaPCS categoriaPCSOrigem;
    @ManyToOne
    private ProgressaoPCS progressaoPCSOrigem;
    @ManyToOne
    private PlanoCargosSalarios planoCargosSalariosDestino;
    @ManyToOne
    private CategoriaPCS categoriaPCSDestino;
    @ManyToOne
    private ProgressaoPCS progressaoPCSDestino;
    @ManyToOne
    private Cargo cargo;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Final de Vigência")
    @Temporal(TemporalType.DATE)
    /**final de vigência dos registros anteriores do lote*/
    private Date finalVigencia;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Início da Vigência")
    @Temporal(TemporalType.DATE)
    /**Início de vigência dos novos registros do lote*/
    private Date inicioVigencia;
    @Tabelavel
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Tabelavel
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @OneToMany(mappedBy = "enquadramentoFuncionalLote", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private List<EnquadramentoFuncionalLoteItem> itens;

    private Boolean progressoesCruzadas;


    public EnquadramentoFuncionalLote() {
        itens = Lists.newLinkedList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public PlanoCargosSalarios getPlanoCargosSalariosOrigem() {
        return planoCargosSalariosOrigem;
    }

    public void setPlanoCargosSalariosOrigem(PlanoCargosSalarios planoCargosSalariosOrigem) {
        this.planoCargosSalariosOrigem = planoCargosSalariosOrigem;
    }

    public CategoriaPCS getCategoriaPCSOrigem() {
        return categoriaPCSOrigem;
    }

    public void setCategoriaPCSOrigem(CategoriaPCS categoriaPCSOrigem) {
        this.categoriaPCSOrigem = categoriaPCSOrigem;
    }

    public ProgressaoPCS getProgressaoPCSOrigem() {
        return progressaoPCSOrigem;
    }

    public void setProgressaoPCSOrigem(ProgressaoPCS progressaoPCSOrigem) {
        this.progressaoPCSOrigem = progressaoPCSOrigem;
    }

    public PlanoCargosSalarios getPlanoCargosSalariosDestino() {
        return planoCargosSalariosDestino;
    }

    public void setPlanoCargosSalariosDestino(PlanoCargosSalarios planoCargosSalariosDestino) {
        this.planoCargosSalariosDestino = planoCargosSalariosDestino;
    }

    public Boolean getProgressoesCruzadas() {
        return progressoesCruzadas != null ? progressoesCruzadas : false;
    }

    public void setProgressoesCruzadas(Boolean progressoesCruzadas) {
        this.progressoesCruzadas = progressoesCruzadas;
    }

    public CategoriaPCS getCategoriaPCSDestino() {
        return categoriaPCSDestino;
    }

    public void setCategoriaPCSDestino(CategoriaPCS categoriaPCSDestino) {
        this.categoriaPCSDestino = categoriaPCSDestino;
    }

    public ProgressaoPCS getProgressaoPCSDestino() {
        return progressaoPCSDestino;
    }

    public void setProgressaoPCSDestino(ProgressaoPCS progressaoPCSDestino) {
        this.progressaoPCSDestino = progressaoPCSDestino;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public List<EnquadramentoFuncionalLoteItem> getItens() {
        return itens;
    }

    public void setItens(List<EnquadramentoFuncionalLoteItem> itens) {
        this.itens = itens;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }
}
