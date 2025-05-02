update solicitabaixapatrimonial sol set sol.unidadeadministrativa_id = (
select ho.subordinada_id from hierarquiaorganizacional ho where id = sol.hierarquiaadministrativa_id);


update solicitabaixapatrimonial sol set sol.unidadeorcamentaria_id = (
select ho.subordinada_id from hierarquiaorganizacional ho where id = sol.hierarquiaorcamentaria_id)
