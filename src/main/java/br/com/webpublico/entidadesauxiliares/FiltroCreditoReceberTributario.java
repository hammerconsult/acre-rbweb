package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.webreportdto.dto.tributario.PrazoCreditoReceberTributario;
import br.com.webpublico.webreportdto.dto.tributario.TipoCreditoReceberTributario;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by rodolfo on 01/09/17.
 */
public class FiltroCreditoReceberTributario implements Serializable {

    private Date referenciaFinal;
    private AgrupadorRelatorioCredito agrupadorRelatorioCredito;
    private Divida divida;
    private List<Divida> dividas;
    private Tributo tributo;
    private List<Tributo> tributos;
    private TipoCreditoReceberTributario[] tiposCredito;
    private ContaReceita contaReceita;
    private List<ContaReceita> contas;
    private Boolean detalhar;
    private Boolean agruparEntidade;

    public FiltroCreditoReceberTributario() {
        referenciaFinal = LocalDate.now().toDate();
        dividas = Lists.newArrayList();
        tributos = Lists.newArrayList();
        contas = Lists.newArrayList();
        referenciaFinal = LocalDate.now().toDate();
        tiposCredito = TipoCreditoReceberTributario.buscarTiposAgrupadorRelatorioCredito().toArray(new TipoCreditoReceberTributario[0]);
        detalhar = true;
        agruparEntidade = false;

    }

    public Date getReferenciaFinal() {
        return referenciaFinal;
    }

    public void setReferenciaFinal(Date referenciaFinal) {
        this.referenciaFinal = referenciaFinal;
    }

    public AgrupadorRelatorioCredito getAgrupadorRelatorioCredito() {
        return agrupadorRelatorioCredito;
    }

    public void setAgrupadorRelatorioCredito(AgrupadorRelatorioCredito agrupadorRelatorioCredito) {
        this.agrupadorRelatorioCredito = agrupadorRelatorioCredito;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public List<Divida> getDividas() {
        return dividas;
    }

    public void setDividas(List<Divida> dividas) {
        this.dividas = dividas;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public List<Tributo> getTributos() {
        return tributos;
    }

    public void setTributos(List<Tributo> tributos) {
        this.tributos = tributos;
    }

    public TipoCreditoReceberTributario[] getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TipoCreditoReceberTributario[] tiposCredito) {
        this.tiposCredito = tiposCredito;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public List<ContaReceita> getContas() {
        return contas;
    }

    public void setContas(List<ContaReceita> contas) {
        this.contas = contas;
    }

    public List<PrazoCreditoReceberTributario> getPrazos() {
        LocalDate referenciaFinal = LocalDate.fromDateFields(this.referenciaFinal);
        LocalDate primeiroDiaDoAno = referenciaFinal.dayOfYear().withMinimumValue();
        LocalDate ultimoDiaDoAno = referenciaFinal.dayOfYear().withMaximumValue();
        List<PrazoCreditoReceberTributario> prazos = Lists.newArrayList();
        prazos.add(new PrazoCreditoReceberTributario(primeiroDiaDoAno, referenciaFinal));
        if (referenciaFinal.equals(ultimoDiaDoAno)) {
            prazos.add(new PrazoCreditoReceberTributario(primeiroDiaDoAno.minusYears(1), ultimoDiaDoAno.minusYears(1)));
        } else {
            if ((referenciaFinal.getYear() % 4 == 0 && referenciaFinal.getYear() % 100 != 0) || (referenciaFinal.getYear() % 400 == 0)) {
                prazos.add(new PrazoCreditoReceberTributario(referenciaFinal.withDayOfMonth(1).plusMonths(1), ultimoDiaDoAno.minusYears(1)));
            } else {
                prazos.add(new PrazoCreditoReceberTributario(referenciaFinal.plusDays(1).minusYears(1), ultimoDiaDoAno.minusYears(1)));
            }
        }
        prazos.add(new PrazoCreditoReceberTributario(primeiroDiaDoAno.minusYears(2), ultimoDiaDoAno.minusYears(2)));
        prazos.add(new PrazoCreditoReceberTributario(primeiroDiaDoAno.minusYears(3), ultimoDiaDoAno.minusYears(3)));
        prazos.add(new PrazoCreditoReceberTributario(primeiroDiaDoAno.minusYears(referenciaFinal.year().get() - 1980), ultimoDiaDoAno.minusYears(4)));
        return prazos;
    }

    public Boolean getDetalhar() {
        return detalhar;
    }

    public void setDetalhar(Boolean detalhar) {
        this.detalhar = detalhar;
    }

    public Boolean getAgruparEntidade() {
        return agruparEntidade;
    }

    public void setAgruparEntidade(Boolean agruparEntidade) {
        this.agruparEntidade = agruparEntidade;
    }

    private List<Divida> getDividasComAgrupador() {
        List<Divida> dividasComAgrupador = getDividas();
        if (dividasComAgrupador == null) {
            dividasComAgrupador = Lists.newArrayList();
        }
        if (agrupadorRelatorioCredito != null) {
            for (AgrupadorRelatorioCreditoDivida ard : agrupadorRelatorioCredito.getDividas()) {
                if (!dividasComAgrupador.contains(ard.getDivida())) {
                    dividasComAgrupador.add(ard.getDivida());
                }
            }
        }
        return dividasComAgrupador;
    }

    public String getFiltrosUtilizados() {
        String filtrosUtilizados = "Referência: ";
        filtrosUtilizados += DataUtil.getDataFormatada(referenciaFinal) + "; ";

        List<Divida> dividasComAgrupador = getDividasComAgrupador();
        if (!dividasComAgrupador.isEmpty()) {
            filtrosUtilizados += "Dividas: ";
            for (Divida divida : dividasComAgrupador) {
                filtrosUtilizados += divida.getDescricao() + "; ";
            }
        }

        if (tributos != null && !tributos.isEmpty()) {
            filtrosUtilizados += "Tributos: ";
            for (Tributo tributo : tributos) {
                filtrosUtilizados += tributo.getDescricao() + "; ";
            }
        }

        if (contas != null && !contas.isEmpty()) {
            filtrosUtilizados += "Contas de Receita: ";
            for (ContaReceita contaReceita : contas) {
                filtrosUtilizados += contaReceita.toString() + "; ";
            }
        }
        return filtrosUtilizados;
    }

    public String getWhere() {
        List<String> itensTiposCreditos = Lists.newArrayList();
        for (TipoCreditoReceberTributario tipo : tiposCredito) {
            itensTiposCreditos.add("'" + tipo.name() + "'");
        }
        String where = " and contaReceita.tiposCredito in (" + StringUtils.join(itensTiposCreditos, ",") + ") ";

        List<PrazoCreditoReceberTributario> prazos = getPrazos();
        String wherePrazos = "";
        String juncaoWherePrazos = " ";
        for (PrazoCreditoReceberTributario prazo : prazos) {
            String dataInicial = DateUtils.getDataFormatada(prazo.getInicio().toDate());
            String dataFinal = DateUtils.getDataFormatada(prazo.getFim().toDate());
            wherePrazos += juncaoWherePrazos +
                " trunc(calc.datacalculo) between to_date('" + dataInicial + "', 'dd/MM/yyyy') and " +
                " to_date('" + dataFinal + "', 'dd/MM/yyyy') ";
            juncaoWherePrazos = " or ";
        }
        where += " and ( " + wherePrazos + " ) ";

        where += " and spvd.situacaoparcela in ('" + SituacaoParcela.EM_ABERTO.name() + "', '" + SituacaoParcela.BAIXADO.name() + "', " +
            " '" + SituacaoParcela.BAIXADO_OUTRA_OPCAO.name() + "', '" + SituacaoParcela.PAGO.name() + "')" +
            " and trib.tipoTributo in ('" + Tributo.TipoTributo.IMPOSTO.name() + "', '" + Tributo.TipoTributo.TAXA.name() + "')";

        if (tributos != null && !tributos.isEmpty()) {
            List<Long> idsTributos = SuperEntidade.getIds(tributos);
            where += " and trib.id in (" + StringUtils.join(idsTributos, ",") + ")";
        }

        if (contas != null && !contas.isEmpty()) {
            List<Long> idsContas = SuperEntidade.getIds(contas);
            where += " and contaReceita.id in (" + StringUtils.join(idsContas, ",") + ")";
        }

        List<Divida> dividasComAgrupador = getDividasComAgrupador();
        if (dividasComAgrupador != null && !dividasComAgrupador.isEmpty()) {
            List<Long> idsDividas = SuperEntidade.getIds(dividasComAgrupador);
            where += " and vd.divida_id in (" + StringUtils.join(idsDividas, ",") + ")";
        }
        return where;
    }
}
