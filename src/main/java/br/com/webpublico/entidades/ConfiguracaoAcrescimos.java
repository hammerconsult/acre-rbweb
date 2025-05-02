/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoIncidenciaAcrescimo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CacheableTributario;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.tributario.consultadebitos.dtos.*;
import br.com.webpublico.tributario.consultadebitos.enums.DataBaseCalculoDTO;
import br.com.webpublico.tributario.consultadebitos.enums.EnumConsultaDebitos;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author gustavo
 */
@Entity

@Audited
@Etiqueta("Acréscimos")
public class ConfiguracaoAcrescimos extends CacheableTributario implements Serializable, EntidadeConsultaDebitos {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Boolean calculaCorrecao;
    @ManyToOne
    private IndiceEconomico indiceCorrecao;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoAcrescimos", orphanRemoval = true)
    private List<IncidenciaAcrescimo> incidencias;
    private BigDecimal valorJurosExercicio;
    private BigDecimal valorJurosDividaAtiva;
    private Boolean jurosFracionado;
    private BigDecimal valorDividaAtivaMulta;
    @Deprecated
    private BigDecimal honorariosAdvocaticios;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Enumerated(EnumType.STRING)
    private DataBaseCalculo tipoBaseCorrecao;
    @Enumerated(EnumType.STRING)
    private DataBaseCalculo tipoBaseHonorarios;
    @Enumerated(EnumType.STRING)
    private DataBaseCalculo tipoBaseMulta;
    private Boolean somarCorrecaoOriginal;
    private Boolean somarJurosOriginal;
    @Deprecated
    private Boolean somarMultaOriginal;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configuracaoAcrescimos")
    private List<HonorariosConfiguracaoAcrescimos> honorarios;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configuracaoAcrescimos")
    private List<MultaConfiguracaoAcrescimo> multas;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoAcrescimos", orphanRemoval = true)
    private List<NaturezaJuridicaIsencao> naturezasIsencao;


    public ConfiguracaoAcrescimos() {
        incidencias = Lists.newArrayList();
        tipoBaseCorrecao = DataBaseCalculo.VENCIMENTO;
        tipoBaseHonorarios = DataBaseCalculo.VENCIMENTO;
        tipoBaseMulta = DataBaseCalculo.VENCIMENTO;
        honorarios = Lists.newArrayList();
        honorariosAdvocaticios = BigDecimal.ZERO;
        multas = Lists.newArrayList();
        naturezasIsencao = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getCalculaCorrecao() {
        return calculaCorrecao != null ? calculaCorrecao : false;
    }

    public void setCalculaCorrecao(Boolean calculaCorrecao) {
        this.calculaCorrecao = calculaCorrecao;
    }

    public IndiceEconomico getIndiceCorrecao() {
        return indiceCorrecao;
    }

    public void setIndiceCorrecao(IndiceEconomico indiceCorrecao) {
        this.indiceCorrecao = indiceCorrecao;
    }

    public Boolean getJurosFracionado() {
        return jurosFracionado;
    }

    public void setJurosFracionado(Boolean jurosFracionado) {
        this.jurosFracionado = jurosFracionado;
    }

    public BigDecimal getValorJurosExercicio() {
        return valorJurosExercicio;
    }

    public void setValorJurosExercicio(BigDecimal valorJurosExercicio) {
        this.valorJurosExercicio = valorJurosExercicio;
    }

    public BigDecimal getValorJurosDividaAtiva() {
        return valorJurosDividaAtiva;
    }

    public void setValorJurosDividaAtiva(BigDecimal valorJurosDividaAtiva) {
        this.valorJurosDividaAtiva = valorJurosDividaAtiva;
    }

    public BigDecimal getValorDividaAtivaMulta() {
        return valorDividaAtivaMulta;
    }

    public void setValorDividaAtivaMulta(BigDecimal valorDividaAtivaMulta) {
        this.valorDividaAtivaMulta = valorDividaAtivaMulta;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Deprecated
    public BigDecimal getHonorariosAdvocaticios() {
        return honorariosAdvocaticios;
    }

    @Deprecated
    public void setHonorariosAdvocaticios(BigDecimal honorariosAdvocaticios) {
        this.honorariosAdvocaticios = honorariosAdvocaticios;
    }

    public List<IncidenciaAcrescimo> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(List<IncidenciaAcrescimo> incidencias) {
        this.incidencias = incidencias;
    }

    public List<IncidenciaAcrescimo> getIncidenciasJuros() {
        return getIncidencias(IncidenciaAcrescimo.TipoAcrescimo.JUROS);
    }

    public List<IncidenciaAcrescimo> getIncidenciasMulta() {
        return getIncidencias(IncidenciaAcrescimo.TipoAcrescimo.MULTA);
    }

    public List<IncidenciaAcrescimo> getIncidenciasCorrecao() {
        return getIncidencias(IncidenciaAcrescimo.TipoAcrescimo.CORRECAO);
    }

    public List<IncidenciaAcrescimo> getIncidencias(IncidenciaAcrescimo.TipoAcrescimo tipo) {
        List<IncidenciaAcrescimo> retorno = Lists.newArrayList();
        for (IncidenciaAcrescimo inc : incidencias) {
            if (inc.getTipoAcrescimo().equals(tipo)) {
                retorno.add(inc);
            }
        }
        return retorno;
    }

    public List<IncidenciaAcrescimo> getIncidencias(IncidenciaAcrescimo.TipoAcrescimo tipo, TipoIncidenciaAcrescimo tipoIncidencia) {
        List<IncidenciaAcrescimo> retorno = Lists.newArrayList();
        for (IncidenciaAcrescimo inc : incidencias) {
            if (inc.getTipoAcrescimo().equals(tipo) && inc.getIncidencia().equals(tipoIncidencia)) {
                retorno.add(inc);
            }
        }
        return retorno;
    }

    public DataBaseCalculo getTipoBaseCorrecao() {
        return tipoBaseCorrecao;
    }

    public void setTipoBaseCorrecao(DataBaseCalculo tipoBaseCorrecao) {
        this.tipoBaseCorrecao = tipoBaseCorrecao;
    }

    public Boolean getSomarCorrecaoOriginal() {
        return somarCorrecaoOriginal != null ? somarCorrecaoOriginal : false;
    }

    public void setSomarCorrecaoOriginal(Boolean somarCorrecaoOriginal) {
        this.somarCorrecaoOriginal = somarCorrecaoOriginal;
    }

    public Boolean getSomarJurosOriginal() {
        return somarJurosOriginal != null ? somarJurosOriginal : false;
    }

    public void setSomarJurosOriginal(Boolean somarJurosOriginal) {
        this.somarJurosOriginal = somarJurosOriginal;
    }

    @Deprecated
    public Boolean getSomarMultaOriginal() {
        return somarMultaOriginal != null ? somarMultaOriginal : false;
    }

    @Deprecated
    public void setSomarMultaOriginal(Boolean somarMultaOriginal) {
        this.somarMultaOriginal = somarMultaOriginal;
    }

    public List<HonorariosConfiguracaoAcrescimos> getHonorarios() {
        return honorarios;
    }

    public void setHonorarios(List<HonorariosConfiguracaoAcrescimos> honorarios) {
        this.honorarios = honorarios;
    }

    public DataBaseCalculo getTipoBaseHonorarios() {
        return tipoBaseHonorarios;
    }

    public void setTipoBaseHonorarios(DataBaseCalculo tipoBaseHonorarios) {
        this.tipoBaseHonorarios = tipoBaseHonorarios;
    }

    public DataBaseCalculo getTipoBaseMulta() {
        return tipoBaseMulta;
    }

    public void setTipoBaseMulta(DataBaseCalculo tipoBaseMulta) {
        this.tipoBaseMulta = tipoBaseMulta;
    }

    public List<MultaConfiguracaoAcrescimo> getMultas() {
        return multas;
    }

    public void setMultas(List<MultaConfiguracaoAcrescimo> multas) {
        this.multas = multas;
    }

    public BigDecimal getHonorariosAdvocaticiosNaData(Date dataReferencia) {
        if (dataReferencia != null) {
            dataReferencia = DataUtil.dataSemHorario(dataReferencia);
            for (HonorariosConfiguracaoAcrescimos honorario : honorarios) {
                if ((dataReferencia.compareTo(honorario.getInicioVigencia()) >= 0) &&
                    ((dataReferencia.compareTo(honorario.getFimVigenciaNotNull()) <= 0) || honorario.getFimVigencia() == null)) {
                    return honorario.getHonorariosAdvocaticios();
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public MultaConfiguracaoAcrescimo getMultasNaData(Date dataReferencia) {
        if (dataReferencia != null) {
            for (MultaConfiguracaoAcrescimo multa : multas) {
                dataReferencia = DataUtil.dataSemHorario(dataReferencia);
                if ((dataReferencia.compareTo(multa.getInicioVigencia()) >= 0) &&
                    ((dataReferencia.compareTo(multa.getFimVigenciaNotNull()) <= 0) || multa.getFimVigencia() == null)) {
                    return multa;
                }
            }
        }
        return null;
    }

    @Override
    public Object getIdentificacao() {
        return this.id;
    }

    public List<NaturezaJuridicaIsencao> getNaturezasIsencao() {
        if (naturezasIsencao == null) {
            naturezasIsencao = Lists.newArrayList();
        }
        return naturezasIsencao;
    }

    public void setNaturezasIsencao(List<NaturezaJuridicaIsencao> naturezasIsencao) {
        this.naturezasIsencao = naturezasIsencao;
    }

    @Override
    public ConfiguracaoAcrescimosDTO toDto() {
        ConfiguracaoAcrescimosDTO dto = new ConfiguracaoAcrescimosDTO();
        dto.setId(getId());
        dto.setDescricao(getDescricao());
        dto.setCalculaCorrecao(getCalculaCorrecao());
        if (getIndiceCorrecao() != null) {
            IndiceEconomicoDTO indice = new IndiceEconomicoDTO();
            indice.setId(getIndiceCorrecao().getId());
            indice.setDescricao(getIndiceCorrecao().getDescricao());
            dto.setIndiceCorrecao(indice);
        }
        dto.setValorJurosExercicio(getValorJurosExercicio());
        dto.setValorDividaAtivaMulta(getValorDividaAtivaMulta());
        dto.setJurosFracionado(getJurosFracionado());
        dto.setHonorariosAdvocaticios(getHonorariosAdvocaticios());
        dto.setSomarCorrecaoOriginal(getSomarCorrecaoOriginal());
        dto.setSomarJurosOriginal(getSomarJurosOriginal());
        dto.setSomarMultaOriginal(getSomarMultaOriginal());
        if (getTipoBaseHonorarios() != null)
            dto.setTipoBaseHonorarios(getTipoBaseHonorarios().toDto());
        if (getTipoBaseMulta() != null)
            dto.setTipoBaseMulta(getTipoBaseMulta().toDto());
        if (getTipoBaseCorrecao() != null)
            dto.setTipoBaseCorrecao(getTipoBaseCorrecao().toDto());
        dto.setIncidencias(Lists.<IncidenciaAcrescimoDTO>newArrayList());
        for (IncidenciaAcrescimo incidencia : incidencias) {
            dto.getIncidencias().add(incidencia.toDto());
        }
        dto.setHonorarios(Lists.<HonorariosConfiguracaoAcrescimosDTO>newArrayList());
        for (HonorariosConfiguracaoAcrescimos honorario : honorarios) {
            dto.getHonorarios().add(honorario.toDto());
        }
        dto.setMultas(Lists.<MultaConfiguracaoAcrescimoDTO>newArrayList());
        for (MultaConfiguracaoAcrescimo multa : multas) {
            dto.getMultas().add(multa.toDto());

        }
        return dto;
    }

    public enum DataBaseCalculo implements EnumConsultaDebitos {
        VENCIMENTO("Data de Vencimento da Parcela", DataBaseCalculoDTO.VENCIMENTO),
        LANCAMENTO("Data de Lançamento da Parcela", DataBaseCalculoDTO.LANCAMENTO);

        private String descricao;
        private DataBaseCalculoDTO dto;

        DataBaseCalculo(String descricao, DataBaseCalculoDTO dto) {
            this.descricao = descricao;
            this.dto = dto;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public DataBaseCalculoDTO toDto() {
            return dto;
        }
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ConfiguracaoAcrecimos[ id=" + id + " ]";
    }

    public boolean hasIsencaoNfse(NaturezaJuridicaIsencao nova) {
        if (!getNaturezasIsencao().isEmpty()) {
            for (NaturezaJuridicaIsencao naturezaJuridicaIsencao : getNaturezasIsencao()) {
                if (naturezaJuridicaIsencao.getNaturezaJuridica().getId().equals(nova.getNaturezaJuridica().getId()) &&
                    naturezaJuridicaIsencao.getTipoMovimentoMensal().equals(nova.getTipoMovimentoMensal()) &&
                    !naturezaJuridicaIsencao.getId().equals(nova.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void validarNovaIsencaoNfse(NaturezaJuridicaIsencao naturezaJuridicaIsencao) {
        ValidacaoException ve = new ValidacaoException();
        if (naturezaJuridicaIsencao.getNaturezaJuridica() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Natureza Jurídica deve ser informado.");
        }
        if (naturezaJuridicaIsencao.getTipoMovimentoMensal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Movimento Mensal deve ser informado.");
        }
        if (ve.getMensagens() == null || ve.getMensagens().isEmpty()) {
            if (hasIsencaoNfse(naturezaJuridicaIsencao)) {
                ve.adicionarMensagemDeCampoObrigatorio("Configuração de Isenção já registrada.");
            }
        }
        ve.lancarException();
    }
}
