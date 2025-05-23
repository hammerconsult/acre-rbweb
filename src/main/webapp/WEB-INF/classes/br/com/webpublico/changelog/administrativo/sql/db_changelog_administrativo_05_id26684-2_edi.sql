update execucaocontrato set gerousolicitacaoempenho = 1
where id in
(select ec.id from ExecucaoContrato ec
 where exists (select 1 from ExecucaoContratoEmpenho ece
 where ece.execucaocontrato_id = ec.id));


update execucaocontrato set gerousolicitacaoempenho = 0
where id in
(select ec.id from ExecucaoContrato ec
 where not exists (select 1 from ExecucaoContratoEmpenho ece
 where ece.execucaocontrato_id = ec.id))
