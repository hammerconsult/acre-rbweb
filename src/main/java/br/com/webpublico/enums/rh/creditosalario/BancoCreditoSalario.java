package br.com.webpublico.enums.rh.creditosalario;

import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo.HeaderCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo.TrailerCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.bancodobrasil.BancoDoBrasilHeaderCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.bancodobrasil.BancoDoBrasilHeaderLoteCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.bradesco.BradescoHeaderCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.lote.HeaderLoteCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.lote.TrailerLoteCNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.segmento.SegmentoACNAB240;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.segmento.SegmentoBCNAB240;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum BancoCreditoSalario {

    BANCO_BRASIL("Banco do Brasil", "001", BancoDoBrasilHeaderCNAB240.newInstance(), BancoDoBrasilHeaderLoteCNAB240.newInstance(), SegmentoACNAB240.newInstance(), SegmentoBCNAB240.newInstance(), TrailerLoteCNAB240.newInstance(), TrailerCNAB240.newInstance(), "103"),
    BRADESCO("Bradesco", "237", BradescoHeaderCNAB240.newInstance(), HeaderLoteCNAB240.newInstance(), SegmentoACNAB240.newInstance(), SegmentoBCNAB240.newInstance(), TrailerLoteCNAB240.newInstance(), TrailerCNAB240.newInstance(), "089"),
    CAIXA("Caixa Econômica Federal", "104", HeaderCNAB240.newInstance(), HeaderLoteCNAB240.newInstance(), SegmentoACNAB240.newInstance(), SegmentoBCNAB240.newInstance(), TrailerLoteCNAB240.newInstance(), TrailerCNAB240.newInstance(), "080"),
    SICREDI("Sicredi", "748", HeaderCNAB240.newInstance(), HeaderLoteCNAB240.newInstance(), SegmentoACNAB240.newInstance(), SegmentoBCNAB240.newInstance(), TrailerLoteCNAB240.newInstance(), TrailerCNAB240.newInstance(), "082"),
    ITAU("Itaú", "341", HeaderCNAB240.newInstance(), HeaderLoteCNAB240.newInstance(), SegmentoACNAB240.newInstance(), SegmentoBCNAB240.newInstance(), TrailerLoteCNAB240.newInstance(), TrailerCNAB240.newInstance(), "030");

    private String descricao;
    private String codigo;
    private HeaderCNAB240 newInstanceHeader;
    private HeaderLoteCNAB240 newInstanceHeaderLote;
    private SegmentoACNAB240 newInstanceSegamentoA;
    private SegmentoBCNAB240 newInstanceSegamentoB;
    private TrailerLoteCNAB240 newInstanceTrailerLote;
    private TrailerCNAB240 newInstanceTrailer;
    private String versao;

    private static final Logger logger = LoggerFactory.getLogger(BancoCreditoSalario.class);

    BancoCreditoSalario(String descricao, String codigo, HeaderCNAB240 newInstanceHeader, HeaderLoteCNAB240 newInstanceHeaderLote, SegmentoACNAB240 newInstanceSegamentoA, SegmentoBCNAB240 newInstanceSegamentoB, TrailerLoteCNAB240 newInstanceTrailerLote, TrailerCNAB240 newInstanceTrailer, String versao) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.newInstanceHeader = newInstanceHeader;
        this.newInstanceHeaderLote = newInstanceHeaderLote;
        this.newInstanceSegamentoA = newInstanceSegamentoA;
        this.newInstanceSegamentoB = newInstanceSegamentoB;
        this.newInstanceTrailerLote = newInstanceTrailerLote;
        this.newInstanceTrailer = newInstanceTrailer;
        this.versao = versao;
    }


    public SegmentoACNAB240 getNewInstanceSegamentoA() {
        try {
            return newInstanceSegamentoA.getClass().newInstance();
        } catch (Exception e) {
            logger.error("erro: ", e);
        }
        return null;
    }

    public SegmentoBCNAB240 getNewInstanceSegamentoB() {
        return newInstanceSegamentoB.newInstance();
    }

    public TrailerCNAB240 getNewInstanceTrailer() {
        return newInstanceTrailer;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public HeaderCNAB240 getNewInstanceHeader() {
        try {
            return newInstanceHeader.getClass().newInstance();
        } catch (InstantiationException e) {
            logger.error("Erro: ", e);
        } catch (IllegalAccessException e) {
            logger.error("Erro: ", e);
        }
        return null;
    }

    public HeaderLoteCNAB240 getNewInstanceHeaderLote() {
        try {
            return newInstanceHeaderLote.getClass().newInstance();
        } catch (Exception e) {
            logger.error("Erro: ", e);
        }
        return null;
    }

    public String getVersao() {
        return versao;
    }

    public TrailerLoteCNAB240 getNewInstanceTrailerLote() {
        return TrailerLoteCNAB240.newInstance();
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
