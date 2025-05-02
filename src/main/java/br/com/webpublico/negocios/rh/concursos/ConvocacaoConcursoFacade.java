/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.concursos;


import br.com.webpublico.entidades.rh.concursos.*;
import br.com.webpublico.enums.CriterioDesempate;
import br.com.webpublico.enums.TipoEtapa;
import br.com.webpublico.enums.rh.concursos.MetodoAvaliacao;
import br.com.webpublico.enums.rh.concursos.StatusClassificacaoInscricao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class ConvocacaoConcursoFacade extends AbstractFacade<ConvocacaoConcurso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConcursoFacade concursoFacade;

    public ConvocacaoConcursoFacade() {
        super(ConvocacaoConcurso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public void salvar(ConvocacaoConcurso convocacao, ClassificacaoConcurso classificacao) {
        concursoFacade.adicionarEtapaAoConcurso(convocacao.getConcurso(), TipoEtapa.CONVOCACAO);
        classificacao = em.merge(classificacao);
        convocacao = em.merge(convocacao);
    }
}
