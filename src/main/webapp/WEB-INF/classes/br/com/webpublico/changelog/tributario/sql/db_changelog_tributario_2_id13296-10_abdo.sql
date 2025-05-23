CREATE OR REPLACE FORCE VIEW vwconsultaceasa
AS
    SELECT DISTINCT
        c.cadastro_id                                                                                   id_cadastro,
        ce.inscricaocadastral                                                                           cadastro,
        e.ano                                                                                           exercicio,
        cp.pessoa_id                                                                                    id_pessoa,
        c.id                                                                                            id_calculo,
        c.subdivida                                                                                     sd,
        vd.id                                                                                           id_valordivida,
        c.datacalculo                                                                                   emissao,
        pvd.id                                                                                          id_parcela,
        pvd.opcaopagamento_id                                                                           id_opcaopagamento,
        op.promocional                                                                                  cotaunica,
        pvd.vencimento,
        spvd.situacaoparcela                                                                            situacaoparcela,
        spvd.referencia,
        pacote_consulta_de_debitos.getnumeroparcela(vd.id, op.id, op.promocional, pvd.sequenciaparcela) parcela,
        CASE
        WHEN spvd.situacaoparcela <> 'EM_ABERTO'
            THEN pvd.valor
        ELSE spvd.saldo
        END                                                                                             valororiginal,
        d.descricao                                                                                     divida,
        d.id                                                                                            id_divida,
        coalesce(pvd.dividaativa, 0)                                                                    dividaativa,
        coalesce(pvd.dividaativaajuizada,
                 0)                                                                                     dividaativaajuizada,
        NULL                                                                                            pagamento,
        dva.acrescimo_id                                                                                id_configuracaoacrescimo,
        coalesce(pf.cpf, pj.cnpj)                                                                       cpfcnpj,
        coalesce(pf.nome, pj.razaosocial)                                                               nomerazao,
        contrato.id                                                                                     id_contrato,
        contrato.numerocontrato                                                                         numerocontrato,
        (SELECT loc.codigo || ' ' || loc.descricao
         FROM ptocomercialcontratoceasa rel
             INNER JOIN pontocomercial ponto ON rel.pontocomercial_id = ponto.id
             INNER JOIN localizacao loc ON ponto.localizacao_id = loc.id
         WHERE rel.contratoceasa_id = contrato.id
               AND rownum = 1)                                                                          localizacao,
        (SELECT listagg(ponto.numerobox, '; ')
        WITHIN GROUP (
            ORDER BY ponto.numerobox)
         FROM ptocomercialcontratoceasa rel
             INNER JOIN pontocomercial ponto ON rel.pontocomercial_id = ponto.id
         WHERE rel.contratoceasa_id = contrato.id)                                                      box
    FROM calculoceasa ceasa
        INNER JOIN contratoceasa contrato ON contrato.id = ceasa.contrato_id
        INNER JOIN cadastroeconomico ce ON contrato.locatario_id = ce.id
        INNER JOIN calculo c ON c.id = ceasa.id
        INNER JOIN calculopessoa cp ON cp.calculo_id = ceasa.id
        LEFT JOIN pessoajuridica pj ON pj.id = cp.pessoa_id
        LEFT JOIN pessoafisica pf ON pf.id = cp.pessoa_id
        INNER JOIN valordivida vd ON vd.calculo_id = ceasa.id
        INNER JOIN divida d ON d.id = vd.divida_id
        INNER JOIN dividaacrescimo dva ON dva.divida_id = d.id
        INNER JOIN exercicio e ON e.id = vd.exercicio_id
        INNER JOIN parcelavalordivida pvd ON pvd.valordivida_id = vd.id
        INNER JOIN opcaopagamento op ON op.id = pvd.opcaopagamento_id
        INNER JOIN situacaoparcelavalordivida spvd ON spvd.id = pvd.situacaoatual_id
    WHERE spvd.situacaoparcela <> 'ISOLAMENTO'
