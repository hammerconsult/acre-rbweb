merge into liquidacaodoctofiscal liqDoc using (
    select doc.id as idLiqDoc,
           trunc(valorbasecalculoirrf * (porcentagemretencaomaximairrf/100), 2) as valor
    from liquidacaodoctofiscal doc
    where porcentagemretencaomaximairrf is not null
) resultado on (resultado.idLiqDoc = liqDoc.id)
    when matched then update set liqDoc.valorretidoirrf = resultado.valor;

merge into liquidacaodoctofiscal liqDoc using (
    select doc.id as idLiqDoc,
           trunc(valorbasecalculo * (porcentagemretencaomaxima/100), 2) as valor
    from liquidacaodoctofiscal doc
    where porcentagemretencaomaxima is not null
) resultado on (resultado.idLiqDoc = liqDoc.id)
    when matched then update set liqDoc.valorretido = resultado.valor;
