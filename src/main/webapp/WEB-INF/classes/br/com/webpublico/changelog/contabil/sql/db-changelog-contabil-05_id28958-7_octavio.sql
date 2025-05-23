update solicitacaoempenho set origemsolicitacaoempenho = 'CONTRATO' where contrato_id is not null;

update solicitacaoempenho set origemsolicitacaoempenho = 'DIARIA'
where id in (select s.id from solicitacaoempenho s inner join diariacivilsolicemp diaria on s.id = diaria.solicitacaoempenho_id);

update solicitacaoempenho set origemsolicitacaoempenho = 'CONVENIO_DESPESA'
where id in (select s.id from solicitacaoempenho s inner join conveniodespsolicempenho convenio on s.id = convenio.solicitacaoempenho_id);

update solicitacaoempenho set origemsolicitacaoempenho = 'CONTRATO' where origemsolicitacaoempenho is null;
