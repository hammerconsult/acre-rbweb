package br.com.webpublico.negocios.manad;

import br.com.webpublico.entidadesauxiliares.manad.Manad;
import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ManadUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 10/06/14
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ManadRHFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private CLPFacade clpFacade;

    public void recuperarInformacoesArquivos(Manad manad) {
        recuperarRegistroManadK050(manad);
        recuperarRegistroManadK100(manad);
        recuperarRegistroManadK150(manad);
        recuperarRegistroManadK250(manad);
        recuperarRegistroManadK300(manad);
    }

    public void criarConteudoArquivo(Manad manad) {
        StringBuilder conteudo = new StringBuilder();
        for (ManadRegistro registroDaVez : manad.getRegistros()) {
            if (registroDaVez.getModulo().equals(ManadRegistro.ManadModulo.RH)) {
                criarConteudo(manad, registroDaVez, conteudo);
                if (!registroDaVez.getTipoRegistro().equals(manad.getRegistros().get(manad.getRegistros().indexOf(manad) + 1).getTipoRegistro())) {
                    ManadUtil.quebraLinha(conteudo);
                }
            }
        }
        manad.setConteudo(manad.getConteudo() + conteudo.toString());
    }

    public void recuperarRegistroManadK300(Manad manad) {
        String sql = " SELECT " +
                "'K300' AS REG," +
                " '04034583000122' as CNPJ_CEI," +
                " CASE" +
                " WHEN FOLHA.TIPOFOLHADEPAGAMENTO = 'NORMAL' THEN '1'" +
                " WHEN FOLHA.TIPOFOLHADEPAGAMENTO = 'SALARIO_13' THEN '2'" +
                " WHEN FOLHA.TIPOFOLHADEPAGAMENTO = 'FERIAS' THEN '3'" +
                " WHEN FOLHA.TIPOFOLHADEPAGAMENTO = 'COMPLEMENTAR' THEN '4'" +
                " ELSE '6' END AS IND_FL," +
                " (SELECT RECURSO.CODIGO FROM RECURSODOVINCULOFP RVFP" +
                " INNER JOIN RECURSOFP RECURSO ON RECURSO.ID = RVFP.RECURSOFP_ID" +
                " WHERE RVFP.VINCULOFP_ID = VINCULO .ID" +
                " AND FOLHA.CALCULADAEM BETWEEN RECURSO.INICIOVIGENCIA AND COALESCE(RECURSO.FINALVIGENCIA, CURRENT_DATE)" +
                " AND FOLHA.CALCULADAEM BETWEEN RVFP.INICIOVIGENCIA AND COALESCE(RVFP.FINALVIGENCIA, CURRENT_DATE) AND ROWNUM = 1) AS COD_LTC," +
                " MATRICULA.MATRICULA || VINCULO.NUMERO AS COD_REG_TRAB," +
                " LPAD((FOLHA.MES + 1), 2, '0') || FOLHA.ANO AS DT_COMP," +
                " EVENTO.CODIGO AS COD_RUBR," +
                " ITEM.VALOR AS VLR_RUBR," +
                " CASE " +
                " WHEN EVENTO.TIPOEVENTOFP = 'VANTAGEM' THEN 'P'" +
                " WHEN EVENTO.TIPOEVENTOFP = 'DESCONTO' THEN 'D'" +
                " ELSE 'O' END IND_RUBR," +
                " '' AS IND_BASE_IRRF," +
                " '' as IND_BASE_PS" +
                " FROM ITEMFICHAFINANCEIRAFP ITEM" +
                " INNER JOIN FICHAFINANCEIRAFP FICHA ON FICHA.ID = ITEM.FICHAFINANCEIRAFP_ID" +
                " INNER JOIN FOLHADEPAGAMENTO FOLHA ON FOLHA.ID = FICHA.FOLHADEPAGAMENTO_ID" +
                " INNER JOIN VINCULOFP VINCULO ON VINCULO.ID = FICHA.VINCULOFP_ID" +
                " INNER JOIN CONTRATOFP CONTRATO ON CONTRATO.ID = VINCULO.ID" +
                " INNER JOIN MATRICULAFP MATRICULA ON MATRICULA.ID = VINCULO.MATRICULAFP_ID" +
                " INNER JOIN UNIDADEORGANIZACIONAL UNIDADE ON UNIDADE.ID = MATRICULA.UNIDADEMATRICULADO_ID" +
                " INNER JOIN ENTIDADE ENTIDADE ON ENTIDADE.ID = UNIDADE.ENTIDADE_ID" +
                " INNER JOIN EVENTOFP EVENTO ON EVENTO.ID = ITEM.EVENTOFP_ID" +
                " WHERE FOLHA.ANO >= :ANOINICIAL AND FOLHA.ANO <= :ANOFINAL" +
                " AND ENTIDADE.ID = :ENTIDADE" +
                " ORDER BY FOLHA.ANO, FOLHA.MES";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("ENTIDADE", manad.getPrefeitura().getId());
        consulta.setParameter("ANOINICIAL", manad.getExercicioInicial().getAno().toString());
        consulta.setParameter("ANOFINAL", manad.getExercicioFinal().getAno().toString());

        List resultList = consulta.getResultList();
        for (Object o : resultList) {
            manad.getRegistros().add(new ManadRegistro(o, ManadRegistro.ManadRegistroTipo.K300, ManadRegistro.ManadModulo.RH, manad));
        }
    }

    public void recuperarRegistroManadK250(Manad manad) {
        String sql = " SELECT " +
                " 'K250' AS REG," +
                " '04034583000122' as CNPJ_CEI," +
                " CASE" +
                " WHEN FOLHA.TIPOFOLHADEPAGAMENTO = 'NORMAL' THEN '1'" +
                " WHEN FOLHA.TIPOFOLHADEPAGAMENTO = 'SALARIO_13' THEN '2'" +
                " WHEN FOLHA.TIPOFOLHADEPAGAMENTO = 'FERIAS' THEN '3'" +
                " WHEN FOLHA.TIPOFOLHADEPAGAMENTO = 'COMPLEMENTAR' THEN '4'" +
                " ELSE '6' END AS IND_FL," +
                " (SELECT RECURSO.CODIGO FROM RECURSODOVINCULOFP RVFP" +
                " INNER JOIN RECURSOFP RECURSO ON RECURSO.ID = RVFP.RECURSOFP_ID" +
                " WHERE RVFP.VINCULOFP_ID = VINCULO.ID" +
                " AND FOLHA.CALCULADAEM BETWEEN RECURSO.INICIOVIGENCIA AND COALESCE(RECURSO.FINALVIGENCIA, CURRENT_DATE)" +
                " AND FOLHA.CALCULADAEM BETWEEN RVFP.INICIOVIGENCIA AND COALESCE(RVFP.FINALVIGENCIA, CURRENT_DATE) AND ROWNUM = 1) AS COD_LTC," +
                " MATRICULA.MATRICULA || VINCULO.NUMERO AS COD_REG_TRAB," +
                " LPAD((FOLHA.MES + 1), 2, '0') || FOLHA.ANO AS DT_COMP," +
                " FOLHA.EFETIVADAEM AS DT_PGTO," +
                " CBO.CODIGO AS COD_CBO," +
                " OCORRENCIA.CODIGO AS COD_OCORR," +
                " CARGO.DESCRICAO AS DESC_CARGO," +
                " (SELECT count(*) FROM DEPENDENTEVINCULOFP DEPENDENTE" +
                " INNER JOIN TIPODEPENDENTE TIPO ON TIPO.ID = DEPENDENTE.TIPODEPENDENTE_ID" +
                " WHERE TIPO.CODIGO IN (2,3,4,10) AND DEPENDENTE.VINCULOFP_ID = VINCULO.ID) AS QTD_DEP_IR," +
                " (SELECT count(*) FROM DEPENDENTEVINCULOFP DEPENDENTE" +
                " INNER JOIN TIPODEPENDENTE TIPO ON TIPO.ID = DEPENDENTE.TIPODEPENDENTE_ID" +
                " WHERE TIPO.CODIGO IN (1,9) AND DEPENDENTE.VINCULOFP_ID = VINCULO.ID) AS QTD_DEP_SF," +
                " (SELECT SUM((SELECT ITEM.valorBaseDeCalculo as valor  FROM ITEMFICHAFINANCEIRAFP ITEM" +
                " INNER JOIN EVENTOFP EVENTO ON EVENTO.ID = ITEM.EVENTOFP_ID" +
                " INNER JOIN ITEMGRUPOEXPORTACAO ITEMGRUPO ON ITEMGRUPO.EVENTOFP_ID = EVENTO.ID" +
                " INNER JOIN GRUPOEXPORTACAO GRUPO ON GRUPO.ID = ITEMGRUPO.GRUPOEXPORTACAO_ID" +
                " INNER JOIN MODULOEXPORTACAO MODULO ON MODULO.ID = GRUPO.MODULOEXPORTACAO_ID" +
                " WHERE MODULO.CODIGO = 10 AND GRUPO.CODIGO = 1 AND ITEMGRUPO.OPERACAOFORMULA = 'ADICAO'" +
                " and item.fichaFinanceiraFP_id = ficha.id)" +
                "  -" +
                " (SELECT ITEM.valorBaseDeCalculo as valor  FROM ITEMFICHAFINANCEIRAFP ITEM" +
                " INNER JOIN EVENTOFP EVENTO ON EVENTO.ID = ITEM.EVENTOFP_ID" +
                " INNER JOIN ITEMGRUPOEXPORTACAO ITEMGRUPO ON ITEMGRUPO.EVENTOFP_ID = EVENTO.ID" +
                " INNER JOIN GRUPOEXPORTACAO GRUPO ON GRUPO.ID = ITEMGRUPO.GRUPOEXPORTACAO_ID" +
                " INNER JOIN MODULOEXPORTACAO MODULO ON MODULO.ID = GRUPO.MODULOEXPORTACAO_ID" +
                " WHERE MODULO.CODIGO = 10 AND GRUPO.CODIGO = 1 AND ITEMGRUPO.OPERACAOFORMULA = 'SUBTRACAO'" +
                " AND ITEM.FICHAFINANCEIRAFP_ID = FICHA.ID)) FROM DUAL) AS VL_BASE_IRRF, " +
                " (SELECT SUM((SELECT ITEM.valorBaseDeCalculo as valor  FROM ITEMFICHAFINANCEIRAFP ITEM" +
                " INNER JOIN EVENTOFP EVENTO ON EVENTO.ID = ITEM.EVENTOFP_ID" +
                " INNER JOIN ITEMGRUPOEXPORTACAO ITEMGRUPO ON ITEMGRUPO.EVENTOFP_ID = EVENTO.ID" +
                " INNER JOIN GRUPOEXPORTACAO GRUPO ON GRUPO.ID = ITEMGRUPO.GRUPOEXPORTACAO_ID" +
                " INNER JOIN MODULOEXPORTACAO MODULO ON MODULO.ID = GRUPO.MODULOEXPORTACAO_ID" +
                " WHERE MODULO.CODIGO = 10 AND GRUPO.CODIGO = 2 AND ITEMGRUPO.OPERACAOFORMULA = 'ADICAO'" +
                " and item.fichaFinanceiraFP_id = ficha.id)" +
                " -" +
                " (SELECT ITEM.valorBaseDeCalculo as valor  FROM ITEMFICHAFINANCEIRAFP ITEM" +
                " INNER JOIN EVENTOFP EVENTO ON EVENTO.ID = ITEM.EVENTOFP_ID" +
                " INNER JOIN ITEMGRUPOEXPORTACAO ITEMGRUPO ON ITEMGRUPO.EVENTOFP_ID = EVENTO.ID" +
                " INNER JOIN GRUPOEXPORTACAO GRUPO ON GRUPO.ID = ITEMGRUPO.GRUPOEXPORTACAO_ID" +
                " INNER JOIN MODULOEXPORTACAO MODULO ON MODULO.ID = GRUPO.MODULOEXPORTACAO_ID" +
                " WHERE MODULO.CODIGO = 10 AND GRUPO.CODIGO = 2 AND ITEMGRUPO.OPERACAOFORMULA = 'SUBTRACAO'" +
                " AND ITEM.FICHAFINANCEIRAFP_ID = FICHA.ID)) from dual) AS VL_BASE_PS" +
                " FROM FICHAFINANCEIRAFP FICHA" +
                " INNER JOIN FOLHADEPAGAMENTO FOLHA ON FOLHA.ID = FICHA.FOLHADEPAGAMENTO_ID" +
                " INNER JOIN VINCULOFP VINCULO ON VINCULO.ID = FICHA.VINCULOFP_ID" +
                " INNER JOIN CONTRATOFP CONTRATO ON CONTRATO.ID = VINCULO.ID" +
                " INNER JOIN MATRICULAFP MATRICULA ON MATRICULA.ID = VINCULO.MATRICULAFP_ID" +
                " INNER JOIN UNIDADEORGANIZACIONAL UNIDADE ON UNIDADE.ID = MATRICULA.UNIDADEMATRICULADO_ID" +
                " INNER JOIN ENTIDADE ENTIDADE ON ENTIDADE.ID = UNIDADE.ENTIDADE_ID" +
                " LEFT JOIN RECURSOFP RECURSO ON RECURSO.ID = FICHA.RECURSOFP_ID" +
                " LEFT JOIN CBO ON CBO.ID = VINCULO.CBO_ID" +
                " LEFT JOIN OCORRENCIASEFIP OCORRENCIA ON OCORRENCIA.ID = CONTRATO.OCORRENCIASEFIP_ID" +
                " left join cargo on cargo.id = contrato.cargo_id" +
                " WHERE FOLHA.ANO >= :ANOINICIAL AND FOLHA.ANO <= :ANOFINAL" +
                " AND ENTIDADE.ID = :ENTIDADE" +
                " ORDER BY FOLHA.ANO, FOLHA.MES";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("ENTIDADE", manad.getPrefeitura().getId());
        consulta.setParameter("ANOINICIAL", manad.getExercicioInicial().getAno().toString());
        consulta.setParameter("ANOFINAL", manad.getExercicioFinal().getAno().toString());

        List resultList = consulta.getResultList();
        for (Object o : resultList) {
            manad.getRegistros().add(new ManadRegistro(o, ManadRegistro.ManadRegistroTipo.K250, ManadRegistro.ManadModulo.RH, manad));
        }
    }

    public void recuperarRegistroManadK150(Manad manad) {
        String sql = " SELECT " +
                " 'K150' AS REG," +
                " '04034583000122' as CNPJ_CEI," +
                " COALESCE(EVENTO.DATAALTERACAO, EVENTO.DATAREGISTRO) AS DT_INC_ALT," +
                " EVENTO.CODIGO AS COD_RUBRICA," +
                " EVENTO.DESCRICAO AS DESC_RUBRICA" +
                " FROM EVENTOFP EVENTO" +
                " WHERE EVENTO.ATIVO = 1";
        Query consulta = em.createNativeQuery(sql);

        List resultList = consulta.getResultList();
        for (Object o : resultList) {
            manad.getRegistros().add(new ManadRegistro(o, ManadRegistro.ManadRegistroTipo.K150, ManadRegistro.ManadModulo.RH, manad));
        }
    }

    public void recuperarRegistroManadK100(Manad manad) {
        String sql = " SELECT " +
                " 'K100' AS REG," +
                " RECURSO.INICIOVIGENCIA AS DT_INC_ALT," +
                " RECURSO.CODIGO AS COD_LTC," +
                " '04034583000122' as CNPJ_CEI," +
                " RECURSO.DESCRICAO AS DESC_LTC," +
                " '' AS CNPJ_CEI_TOM" +
                " FROM RECURSOFP RECURSO " +
                " WHERE (EXTRACT(YEAR FROM RECURSO.INICIOVIGENCIA) <= :ANOINICIAL " +
                " AND EXTRACT(YEAR FROM (COALESCE(RECURSO.FINALVIGENCIA, CURRENT_DATE))) >= :ANOFINAL)";
        Query consulta = em.createNativeQuery(sql);

        consulta.setParameter("ANOINICIAL", manad.getExercicioInicial().getAno().toString());
        consulta.setParameter("ANOFINAL", manad.getExercicioFinal().getAno().toString());

        List resultList = consulta.getResultList();
        for (Object o : resultList) {
            manad.getRegistros().add(new ManadRegistro(o, ManadRegistro.ManadRegistroTipo.K100, ManadRegistro.ManadModulo.RH, manad));
        }
    }

    public void recuperarRegistroManadK050(Manad manad) {
        String sql = " SELECT " +
                " 'K050' AS REG ," +
                " replace(replace(replace(PJ.CNPJ,'.',''),'-',''),'/','') AS CNPJ_CEI," +
                " cast(COALESCE(VINCULO.DATAALTERACAO, VINCULO.DATAREGISTRO) as date) as DT_INC_ALT," +
                " MATRICULA.MATRICULA || VINCULO.NUMERO AS COD_REG_TRAB," +
                " REPLACE(REPLACE(PF.CPF, '.', ''), '-', '') AS CPF," +
                " CARTEIRA.PISPASEP AS NIT," +
                " CATEGORIASEFIP.CODIGO AS COD_CATEG," +
                " PF.NOME AS NOME_TRAB," +
                " PF.DATANASCIMENTO AS DT_NASC," +
                " CONTRATO.DATAADMISSAO AS DT_ADMISSAO," +
                " EXONERACAO.DATARESCISAO AS DT_DEMISSAO," +
                " COALESCE((SELECT 1 FROM CEDENCIACONTRATOFP CEDENCIA WHERE CEDENCIA.CONTRATOFP_ID = CONTRATO.ID AND CEDENCIA.TIPOCONTRATOCEDENCIAFP = 'COM_ONUS' AND CEDENCIA.FINALIDADECEDENCIACONTRATOFP = 'EXTERNA' and rownum = 1),0) as SERVIDOR_CEDIDO," +
                " CASE " +
//                "  -- para o valor 6 -> SERVIDOR_CEDIDO igual a 1" +
                "  WHEN MODALIDADE.CODIGO = 4 THEN 2 " +
                "  WHEN MODALIDADE.CODIGO = 1 AND REGIME.CODIGO = 2 THEN 3" +
                "  WHEN MODALIDADE.CODIGO = 1 AND REGIME.CODIGO = 1 THEN 4" +
                "  WHEN MODALIDADE.CODIGO = 2 THEN 5" +
                "  WHEN MODALIDADE.CODIGO = 3 THEN 7" +
                "  WHEN MODALIDADE.CODIGO = 7 THEN 7" +
                "  WHEN MODALIDADE.CODIGO = 8 THEN 8" +
                "  WHEN MODALIDADE.CODIGO = 6 THEN 9" +
                " ELSE 0 END AS IND_VINC," +
                " CASE " +
                " WHEN ATO.TIPOATOLEGAL = 'LEI' THEN 1" +
                " WHEN ATO.TIPOATOLEGAL = 'DECRETO' THEN 2" +
                " WHEN ATO.TIPOATOLEGAL = 'PORTARIA' THEN 3" +
                " WHEN ATO.TIPOATOLEGAL = 'CONTRATO' THEN 4" +
                " ELSE 9 END AS TIPO_ATO_NOM," +
                " ATO.NOME AS NM_ATO_NOM," +
                " ATO.DATAPUBLICACAO AS DT_ATO_NOM" +
                " FROM CONTRATOFP CONTRATO" +
                " INNER JOIN VINCULOFP VINCULO ON VINCULO.ID = CONTRATO.ID" +
                " INNER JOIN MATRICULAFP MATRICULA ON MATRICULA.ID = VINCULO.MATRICULAFP_ID" +
                " INNER JOIN UNIDADEORGANIZACIONAL UNIDADE ON UNIDADE.ID = MATRICULA.UNIDADEMATRICULADO_ID" +
                " INNER JOIN ENTIDADE ENTIDADE ON ENTIDADE.ID = UNIDADE.ENTIDADE_ID" +
                " INNER JOIN PESSOAJURIDICA PJ ON PJ.ID = ENTIDADE.PESSOAJURIDICA_ID" +
                " INNER JOIN PESSOAFISICA PF ON PF.ID = MATRICULA.PESSOA_ID" +
                " LEFT JOIN DOCUMENTOPESSOAL DOCUMENTO ON DOCUMENTO.PESSOaFISICA_ID = PF.ID" +
                " inner JOIN CARTEIRATRABALHO CARTEIRA ON CARTEIRA.ID = DOCUMENTO.ID" +
                " LEFT JOIN CATEGORIASEFIP ON CATEGORIASEFIP.ID = CONTRATO.CATEGORIASEFIP_ID" +
                " LEFT JOIN EXONERACAORESCISAO EXONERACAO ON EXONERACAO.VINCULOFP_ID = VINCULO.ID" +
                " LEFT JOIN MODALIDADECONTRATOFP MODALIDADE ON MODALIDADE.ID = CONTRATO.MODALIDADECONTRATOFP_ID" +
                " LEFT JOIN TIPOREGIME REGIME ON REGIME.ID = CONTRATO.TIPOREGIME_ID" +
                " LEFT JOIN ATOLEGAL ATO ON ATO.ID = CONTRATO.ATOLEGAL_ID" +
                " WHERE ENTIDADE.ID = :ENTIDADE" +
                " AND (EXTRACT(YEAR FROM VINCULO.INICIOVIGENCIA) >= :ANOINICIAL " +
                " AND EXTRACT(YEAR FROM (COALESCE(VINCULO.FINALVIGENCIA, CURRENT_DATE))) <= :ANOFINAL)";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("ENTIDADE", manad.getPrefeitura().getId());
        consulta.setParameter("ANOINICIAL", manad.getExercicioInicial().getAno().toString());
        consulta.setParameter("ANOFINAL", manad.getExercicioFinal().getAno().toString());

        List resultList = consulta.getResultList();
        for (Object o : resultList) {
            manad.getRegistros().add(new ManadRegistro(o, ManadRegistro.ManadRegistroTipo.K050, ManadRegistro.ManadModulo.RH, manad));
        }
    }

    public void criarConteudo(Manad manad, ManadRegistro manadRegistro, StringBuilder conteudo) {
        Object[] objeto = (Object[]) manadRegistro.getObjeto();

        for (int i = 0; i <= objeto.length - 1; i++) {
            try {
                Object objetoDaVez = objeto[i];
                if (objetoDaVez instanceof Date) {
                    ManadUtil.criaLinha(1, ManadUtil.getDataSemBarra((Date) objetoDaVez), conteudo);
                } else if (objetoDaVez instanceof BigDecimal) {
                    ManadUtil.criaLinha(1, ManadUtil.getValor((BigDecimal) objetoDaVez), conteudo);
                } else {
                    ManadUtil.criaLinha(1, objetoDaVez.toString(), conteudo);
                }

            } catch (NullPointerException e) {
                ManadUtil.criaLinha(1, "", conteudo);
            }
        }
    }
}
