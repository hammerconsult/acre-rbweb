/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.SituacaoFuncionalFacade;
import br.com.webpublico.seguranca.SingletonMetricas;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.Util;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author Leonardo
 */
@ManagedBean
@ViewScoped
public class ContratoFPPesquisaGenericaControlador extends PesquisaGenericaRHControlador implements Serializable {

    private final String DESCRICAO_METRICA = "Consulta de Contrato FP";

    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    private SingletonMetricas singletonMetricas;

    @PostConstruct
    public void init() {
        singletonMetricas = (SingletonMetricas) Util.getSpringBeanPeloNome("singletonMetricas");
    }

    public ContratoFPPesquisaGenericaControlador() {
        setNomeVinculo("obj");
    }

    @Override
    public void getCampos() {
        super.getCampos();
        adicionaItemPesquisaGenerica("Nome Tratamento/Social do Servidor", "obj.matriculaFP.pessoa.nomeTratamento", String.class, Boolean.FALSE);
        adicionaItemPesquisaGenerica("Tipo de Previdência", "previdencia.tipoPrevidenciaFP.descricao", TipoPrevidenciaFP.class, Boolean.TRUE);
        adicionaItemPesquisaGenerica("CBO", "cbo.descricao", CBO.class, Boolean.FALSE);
        adicionaItemPesquisaGenerica("Número da Conta Corrente", "concat(conta.numeroConta, conta.digitoVerificador)", ContaCorrenteBancaria.class, Boolean.TRUE);
        adicionaItemPesquisaGenerica("Agência da Conta Corrente", "concat(conta.agencia.numeroAgencia, conta.agencia.digitoVerificador)", Agencia.class, Boolean.TRUE);
        adicionaItemPesquisaGenerica("Banco da Conta Corrente", "conta.agencia.banco.descricao", Banco.class, Boolean.TRUE);
        adicionaItemPesquisaGenerica("Local de Trabalho", "lot.unidadeOrganizacional.descricao", UnidadeOrganizacional.class, Boolean.TRUE);
        adicionaItemPesquisaGenerica("Início de Vigência", "obj.inicioVigencia", Date.class, Boolean.FALSE);
        adicionaItemPesquisaGenerica("Final de Vigência", "obj.finalVigencia", Date.class, Boolean.FALSE);
        adicionaItemPesquisaGenerica("Situação Funcional", "sit.situacaoFuncional.descricao", SituacaoFuncional.class, Boolean.TRUE, Boolean.TRUE);
    }

    @Override
    public List<SelectItem> recuperaValuesEnum(ItemPesquisaGenerica item) {
        List<SelectItem> toReturn = new ArrayList<>();
        if (item.getCondicao().equals("sit.situacaoFuncional.descricao")) {
            for (SituacaoFuncional sf : situacaoFuncionalFacade.lista()) {
                toReturn.add(new SelectItem(sf.getDescricao(), sf.getDescricao()));
            }
        } else {
            toReturn = super.recuperaValuesEnum(item);
        }
        return toReturn;
    }

    @Override
    public String getHqlConsulta() {
        return "select  new ContratoFP(obj.id,obj.matriculaFP.matricula ||'/'||obj.numero||' - '|| obj.matriculaFP.pessoa.nome ||' ('||obj.matriculaFP.pessoa.nomeTratamento||')', " +
            " (select lot.unidadeOrganizacional.descricao from LotacaoFuncional lot where lot.vinculoFP = obj and rownum = 1 and " +
            " lot.inicioVigencia = (select max(lotacao.inicioVigencia) from LotacaoFuncional lotacao where lotacao.vinculoFP.id = lot.vinculoFP.id)) " +
            ", obj.inicioVigencia, cargo, modalidade, tipoRegime , obj.matriculaFP, obj.finalVigencia) "
            + "   from ContratoFP obj ";
    }

    @Override
    public String getHqlContador() {
        return "select count(obj.id) "
            + "   from ContratoFP obj ";
    }


    @Override
    public String getComplementoQuery() {
        return " left join obj.previdenciaVinculoFPs previdencia " +
            " left join obj.contaCorrente conta " +
            " left join obj.lotacaoFuncionals lot " +
            "   left join obj.cargo cargo " +
            "   left join obj.situacaoFuncionals sit " +
            "   left join obj.modalidadeContratoFP modalidade " +
            "   left join obj.tipoRegime tipoRegime " +
            " where (lot.inicioVigencia is null or lot.inicioVigencia = (select max(lf.inicioVigencia) from LotacaoFuncional lf where lf.vinculoFP = obj)) " +
            "  and (previdencia.inicioVigencia is null or previdencia.inicioVigencia = (select max(prev.inicioVigencia) from PrevidenciaVinculoFP prev where prev.contratoFP = obj)) " +
            "  and (sit.inicioVigencia  is null or sit.inicioVigencia = (select max(sf.inicioVigencia) from SituacaoContratoFP sf where sf.contratoFP = obj)) and " +
            montaCondicao() + montaOrdenacao();
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), DESCRICAO_METRICA, System.currentTimeMillis(), MetricaSistema.Tipo.ROTINA_USUARIO);
        super.executarConsulta(sql, sqlCount);
        lista = new ArrayList((new HashSet<ContratoFP>(lista)));
        if (lista.size() > 0) {
            metadata = new EntidadeMetaData(lista.get(0).getClass());
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), DESCRICAO_METRICA, System.currentTimeMillis(), MetricaSistema.Tipo.ROTINA_USUARIO);

    }
}

