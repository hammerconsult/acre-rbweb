package br.com.webpublico.entidades.rh.arquivos;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoArquivoAtuarial;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Buzatto on 18/05/2016.
 */
@Entity
@Audited
@Etiqueta("Estudo Atuarial")
public class EstudoAtuarial extends SuperEntidade implements PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @Invisivel
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Sequência")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Long sequencia;

    @Etiqueta("Data de Referência")
    @Temporal(TemporalType.DATE)
    @Tabelavel
    private Date dataReferencia;
    @Etiqueta("Competência Inicial")
    @Column(nullable = false)
    private Date inicioCompetencia;
    @Etiqueta("Competência Final")
    @Column(nullable = false)
    private Date finalCompetencia;
    @Etiqueta("Data de Registro")
    @Temporal(TemporalType.DATE)
    @Tabelavel
    private Date dataRegistro;

    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Etiqueta("Usuário")
    @ManyToOne
    @Tabelavel
    private UsuarioSistema usuarioSistema;

    @Etiqueta("Ano de admissão")
    @ManyToOne
    private Exercicio exercicio;

    @Transient
    private List<String> tiposArquivo;
    @Transient
    private List<Long> idsServidoresAtivos = new ArrayList<>();
    @Transient
    private List<Long> idsServidoresAposentados = new ArrayList<>();
    @Transient
    private List<Long> idsServidoresPensionistas = new ArrayList<>();

    @Transient
    private List<Long> idsServidoresAtivosFalecidosExonerados = Lists.newArrayList();
    @Transient
    private List<Long> idsServidoresAposentadosFalecidos = Lists.newArrayList();
    @Transient
    private List<Long> idsServidoresPensionistasFalecidos = Lists.newArrayList();
    @Transient
    private List<Long> idsDependentes = Lists.newArrayList();
    @Transient
    private HashMap<Date, List<ContratoFP>>  servidoresAtivosPorMes;
    @Transient
    private HashMap<Date, List<ContratoFP>>  servidoresAtivosFalecidosOuExonerados;
    @Transient
    private HashMap<Date, List<Aposentadoria>>  aposentadoriaPorMes;
    @Transient
    private HashMap<Date, List<Aposentadoria>>  aposentadosFalecidosPorMes;
    @Transient
    private HashMap<Date, List<Pensionista>>  pensionistasPorMes;
    @Transient
    private HashMap<Date, List<Pensionista>>  pensionistasFalecidosPorMes;
    @Transient
    private HashMap<Date, List<Dependente>>  dependentePorMes;


    public EstudoAtuarial() {
        detentorArquivoComposicao = new DetentorArquivoComposicao();
        servidoresAtivosPorMes = new HashMap<>();
        servidoresAtivosFalecidosOuExonerados = new HashMap<>();
        aposentadoriaPorMes = new HashMap<>();
        aposentadosFalecidosPorMes = new HashMap<>();
        pensionistasPorMes = new HashMap<>();
        pensionistasFalecidosPorMes = new HashMap<>();
        dependentePorMes = new HashMap<>();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public String getDataReferenciaFormatada() {
        return DataUtil.getDataFormatada(dataReferencia);
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
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

    public List<String> getTiposArquivo() {
        return tiposArquivo;
    }

    public void setTiposArquivo(List<String> tiposArquivo) {
        this.tiposArquivo = tiposArquivo;
    }

    public List<Long> getIdsServidoresAtivos() {
        return idsServidoresAtivos;
    }

    public void setIdsServidoresAtivos(List<Long> idsServidoresAtivos) {
        this.idsServidoresAtivos = idsServidoresAtivos;
    }

    public List<Long> getIdsServidoresAtivosFalecidosExonerados() {
        return idsServidoresAtivosFalecidosExonerados;
    }

    public void setIdsServidoresAtivosFalecidosExonerados(List<Long> idsServidoresAtivosFalecidosExonerados) {
        this.idsServidoresAtivosFalecidosExonerados = idsServidoresAtivosFalecidosExonerados;
    }

    public List<Long> getIdsServidoresAposentadosFalecidos() {
        return idsServidoresAposentadosFalecidos;
    }

    public void setIdsServidoresAposentadosFalecidos(List<Long> idsServidoresAposentadosFalecidos) {
        this.idsServidoresAposentadosFalecidos = idsServidoresAposentadosFalecidos;
    }

    public List<Long> getIdsServidoresPensionistasFalecidos() {
        return idsServidoresPensionistasFalecidos;
    }

    public void setIdsServidoresPensionistasFalecidos(List<Long> idsServidoresPensionistasFalecidos) {
        this.idsServidoresPensionistasFalecidos = idsServidoresPensionistasFalecidos;
    }

    public List<Long> getIdsServidoresAposentados() {
        return idsServidoresAposentados;
    }

    public void setIdsServidoresAposentados(List<Long> idsServidoresAposentados) {
        this.idsServidoresAposentados = idsServidoresAposentados;
    }

    public List<Long> getIdsServidoresPensionistas() {
        return idsServidoresPensionistas;
    }

    public void setIdsServidoresPensionistas(List<Long> idsServidoresPensionistas) {
        this.idsServidoresPensionistas = idsServidoresPensionistas;
    }

    public List<Long> getIdsDependentes() { return idsDependentes; }

    public void setIdsDependentes(List<Long> idsDependentes) {
        this.idsDependentes = idsDependentes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getInicioCompetencia() {
        return inicioCompetencia;
    }

    public void setInicioCompetencia(Date inicioCompetencia) {
        this.inicioCompetencia = inicioCompetencia;
    }

    public Date getFinalCompetencia() {
        return finalCompetencia;
    }

    public void setFinalCompetencia(Date finalCompetencia) {
        this.finalCompetencia = finalCompetencia;
    }

    public HashMap<Date, List<ContratoFP>> getServidoresAtivosPorMes() {
        return servidoresAtivosPorMes;
    }

    public HashMap<Date, List<ContratoFP>> getServidoresAtivosFalecidosOuExonerados() {
        return servidoresAtivosFalecidosOuExonerados;
    }

    public HashMap<Date, List<Aposentadoria>> getAposentadoriaPorMes() {
        return aposentadoriaPorMes;
    }

    public HashMap<Date, List<Aposentadoria>> getAposentadosFalecidosPorMes() {
        return aposentadosFalecidosPorMes;
    }

    public HashMap<Date, List<Pensionista>> getPensionistasPorMes() {
        return pensionistasPorMes;
    }

    public HashMap<Date, List<Pensionista>> getPensionistasFalecidosPorMes() {
        return pensionistasFalecidosPorMes;
    }

    public HashMap<Date, List<Dependente>> getDependentePorMes() {
        return dependentePorMes;
    }


    @Override
    public String toString() {
        return "EstudoAtuarial{" +
            "id=" + id +
            ", sequencia=" + sequencia +
            ", dataReferencia=" + dataReferencia +
            ", dataRegistro=" + dataRegistro +
            ", usuarioSistema=" + usuarioSistema +
            '}';
    }

    public boolean temTipoServidoresAtivos() {
        return temTipoArquivo(TipoArquivoAtuarial.SERVIDORES_ATIVOS);
    }

    public boolean temTipoServidoresAtivosFalecidosOuExonerados() {
        return temTipoArquivo(TipoArquivoAtuarial.ATIVOS_FALECIDOS_OU_EXONERADOS);
    }

    public boolean temTipoServidoresAposentados() {
        return temTipoArquivo(TipoArquivoAtuarial.APOSENTADOS);
    }

    public boolean temTipoServidoresPensionistas() {
        return temTipoArquivo(TipoArquivoAtuarial.PENSIONISTAS);
    }

    public boolean temTipoServidoresAposentadosFalecidos() { return temTipoArquivo(TipoArquivoAtuarial.APOSENTADOS_FALECIDOS); }

    public boolean temTipoServidoresPensionistasFalecidos() { return temTipoArquivo(TipoArquivoAtuarial.PENSIONISTAS_FALECIDOS); }

    public boolean temTipoDependentes() { return temTipoArquivo(TipoArquivoAtuarial.DEPENDENTES); }

    public boolean temTipoArquivo(TipoArquivoAtuarial tipoArquivoAtuarial) {
        for (String tipo : tiposArquivo) {
            if (TipoArquivoAtuarial.valueOf(tipo).equals(tipoArquivoAtuarial)) {
                return true;
            }
        }
        return false;
    }

    public boolean temServidorParaSerProcessado() {
        if (CollectionUtils.isEmpty(idsServidoresAtivos)
            && CollectionUtils.isEmpty(idsServidoresAposentados)
            && CollectionUtils.isEmpty(idsServidoresPensionistas)
            && CollectionUtils.isEmpty(idsDependentes)
            && CollectionUtils.isEmpty(idsServidoresAtivosFalecidosExonerados)
            && CollectionUtils.isEmpty(idsServidoresAposentadosFalecidos)
            && CollectionUtils.isEmpty(idsServidoresPensionistasFalecidos)){
            return false;
        }
        return true;
    }

    public List<Long> getTodosServidores() {
        List<Long> todosServidores = new ArrayList<>();
        todosServidores.addAll(idsServidoresAtivos);
        todosServidores.addAll(idsServidoresAposentados);
        todosServidores.addAll(idsServidoresPensionistas);
        todosServidores.addAll(idsServidoresAtivosFalecidosExonerados);
        todosServidores.addAll(idsServidoresAposentadosFalecidos);
        todosServidores.addAll(idsServidoresPensionistasFalecidos);
        todosServidores.addAll(idsDependentes);
        return todosServidores;
    }
}
