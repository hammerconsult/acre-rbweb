update processoparcelamento pp set pp.VALORDESCONTOIMPOSTO = coalesce((select sum(ipp.IMPOSTO) from itemprocessoparcelamento ipp where ipp.PROCESSOPARCELAMENTO_ID = pp.id),0) - coalesce(pp.VALORTOTALIMPOSTO,0) where pp.VALORDESCONTOIMPOSTO is null;
update processoparcelamento pp set pp.VALORDESCONTOTAXA = coalesce((select sum(ipp.TAXA) from itemprocessoparcelamento ipp where ipp.PROCESSOPARCELAMENTO_ID = pp.id),0) - coalesce(pp.VALORTOTALTAXA,0) where pp.VALORDESCONTOTAXA is null;
update processoparcelamento pp set pp.VALORDESCONTOJUROS = coalesce((select sum(ipp.JUROS) from itemprocessoparcelamento ipp where ipp.PROCESSOPARCELAMENTO_ID = pp.id),0) - coalesce(pp.VALORTOTALJUROS,0) where pp.VALORDESCONTOJUROS is null;
update processoparcelamento pp set pp.VALORDESCONTOMULTA = coalesce((select sum(ipp.MULTA) from itemprocessoparcelamento ipp where ipp.PROCESSOPARCELAMENTO_ID = pp.id),0) - coalesce(pp.VALORTOTALMULTA,0) where pp.VALORDESCONTOMULTA is null;
update processoparcelamento pp set pp.VALORDESCONTOCORRECAO = coalesce((select sum(ipp.CORRECAO) from itemprocessoparcelamento ipp where ipp.PROCESSOPARCELAMENTO_ID = pp.id),0) - coalesce(pp.VALORTOTALCORRECAO,0) where pp.VALORDESCONTOCORRECAO is null;
update processoparcelamento pp set pp.VALORDESCONTOHONORARIOS = coalesce((select sum(ipp.HONORARIOS) from itemprocessoparcelamento ipp where ipp.PROCESSOPARCELAMENTO_ID = pp.id),0) - coalesce(pp.VALORTOTALHONORARIOS,0) where pp.VALORDESCONTOHONORARIOS is null;

update processoparcelamento pp set pp.VALORTOTALIMPOSTO = coalesce((select sum(ipp.IMPOSTO) from itemprocessoparcelamento ipp where ipp.PROCESSOPARCELAMENTO_ID = pp.id),0);
update processoparcelamento pp set pp.VALORTOTALTAXA = coalesce((select sum(ipp.TAXA) from itemprocessoparcelamento ipp where ipp.PROCESSOPARCELAMENTO_ID = pp.id),0);
update processoparcelamento pp set pp.VALORTOTALJUROS = coalesce((select sum(ipp.JUROS) from itemprocessoparcelamento ipp where ipp.PROCESSOPARCELAMENTO_ID = pp.id),0);
update processoparcelamento pp set pp.VALORTOTALMULTA = coalesce((select sum(ipp.MULTA) from itemprocessoparcelamento ipp where ipp.PROCESSOPARCELAMENTO_ID = pp.id),0);
update processoparcelamento pp set pp.VALORTOTALCORRECAO = coalesce((select sum(ipp.CORRECAO) from itemprocessoparcelamento ipp where ipp.PROCESSOPARCELAMENTO_ID = pp.id),0);
update processoparcelamento pp set pp.VALORTOTALHONORARIOS = coalesce((select sum(ipp.HONORARIOS) from itemprocessoparcelamento ipp where ipp.PROCESSOPARCELAMENTO_ID = pp.id),0);

update PROCESSOPARCELAMENTO pp set pp.VALORTOTALDESCONTO = coalesce(pp.VALORDESCONTOIMPOSTO,0) + coalesce(pp.VALORDESCONTOTAXA,0) + coalesce(pp.VALORDESCONTOJUROS,0) + coalesce(pp.VALORDESCONTOMULTA,0) + coalesce(pp.VALORDESCONTOCORRECAO,0) + coalesce(pp.VALORDESCONTOHONORARIOS,0);
